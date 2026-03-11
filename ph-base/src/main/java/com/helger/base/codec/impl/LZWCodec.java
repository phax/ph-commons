/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.base.codec.impl;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.WillNotClose;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.CGlobal;
import com.helger.base.array.ArrayHelper;
import com.helger.base.codec.DecodeException;
import com.helger.base.codec.EncodeException;
import com.helger.base.codec.IByteArrayCodec;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.nonblocking.NonBlockingBitInputStream;
import com.helger.base.io.nonblocking.NonBlockingBitOutputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Encoder and decoder for the LZW algorithm
 *
 * @author Philip Helger
 */
public class LZWCodec implements IByteArrayCodec
{
  /**
   * A single LZW node
   *
   * @author Philip Helger
   */
  protected static class LZWNode
  {
    private final int m_nTableIndex;
    private LZWNode [] m_aChildren;

    /**
     * Default constructor for the root node (table index is -1).
     */
    public LZWNode ()
    {
      // only for the root node
      m_nTableIndex = -1;
    }

    /**
     * Constructor with a specific table index.
     *
     * @param nTableIndex
     *        The table index for this node. Must be between 0 and
     *        {@link AbstractLZWDictionary#MAX_CODE} (inclusive).
     */
    public LZWNode (@Nonnegative final int nTableIndex)
    {
      ValueEnforcer.isBetweenInclusive (nTableIndex, "TableIndex", 0, AbstractLZWDictionary.MAX_CODE);
      m_nTableIndex = nTableIndex;
    }

    /**
     * @return The table index of this node. Must be &ge; 0.
     * @throws IllegalStateException
     *         if this is the root node (no table index assigned).
     */
    @Nonnegative
    public int getTableIndex ()
    {
      if (m_nTableIndex < 0)
        throw new IllegalStateException ("This node has no table index!");
      return m_nTableIndex;
    }

    /**
     * Set a child node at the specified byte index.
     *
     * @param nIndex
     *        The byte index (0-255) at which the child node is set.
     * @param aNode
     *        The child node to set. May not be <code>null</code>.
     */
    public void setChildNode (@Nonnegative final byte nIndex, @NonNull final LZWNode aNode)
    {
      ValueEnforcer.notNull (aNode, "Node");
      if (m_aChildren == null)
        m_aChildren = new LZWNode [256];
      m_aChildren[nIndex & 0xff] = aNode;
    }

    /**
     * Get the child node at the specified byte index.
     *
     * @param nIndex
     *        The byte index (0-255) of the child node to retrieve.
     * @return The child node at the given index, or <code>null</code> if no
     *         child exists at that index.
     */
    @Nullable
    public LZWNode getChildNode (final byte nIndex)
    {
      return m_aChildren == null ? null : m_aChildren[nIndex & 0xff];
    }

    /**
     * This will traverse the tree until it gets to the sub node. This will return null if the node
     * does not exist.
     *
     * @param aBuffer
     *        The path to the node.
     * @return The node that resides at the data path.
     */
    @Nullable
    public LZWNode getChildNode (final byte @NonNull [] aBuffer)
    {
      LZWNode aCurNode = this;
      for (final byte aByte : aBuffer)
      {
        aCurNode = aCurNode.getChildNode (aByte);
        if (aCurNode == null)
          break;
      }
      return aCurNode;
    }

    @Override
    public String toString ()
    {
      return new ToStringGenerator (null).append ("Index", m_nTableIndex)
                                         .append ("Children#", ArrayHelper.getSize (m_aChildren))
                                         .getToString ();
    }
  }

  protected abstract static class AbstractLZWDictionary
  {
    /** Maximum index */
    public static final int MAX_CODE = 4096;
    /** Special code to clear the table */
    public static final int CODE_CLEARTABLE = 256;
    /** Special code for end of file */
    public static final int CODE_EOF = 257;

    protected byte [] [] m_aTab;
    protected int m_nFreeCode;
    protected int m_nCodeBits;

    protected AbstractLZWDictionary ()
    {}

    /**
     * Reset this dictionary to its initial state, clearing all entries and
     * re-initializing with the 256 single-byte entries.
     */
    public void reset ()
    {
      m_aTab = new byte [MAX_CODE] [];
      for (int i = 0; i < 256; ++i)
        m_aTab[i] = new byte [] { (byte) i };
      m_nFreeCode = CODE_EOF + 1;
      m_nCodeBits = 9;
    }

    /**
     * Add a new byte sequence entry to the dictionary.
     *
     * @param aByteSeq
     *        The byte sequence to add. May not be <code>null</code>.
     * @param bForEncode
     *        <code>true</code> if this is used for encoding, <code>false</code>
     *        for decoding. This affects code length thresholds.
     * @throws EncodeException
     *         if the table overflows during encoding.
     * @throws DecodeException
     *         if the table overflows during decoding.
     */
    public final void addEntry (final byte @NonNull [] aByteSeq, final boolean bForEncode)
    {
      ValueEnforcer.notNull (aByteSeq, "ByteSeq");
      if (m_nFreeCode == m_aTab.length)
        throw bForEncode ? new EncodeException ("LZW encode table overflow") : new DecodeException (
                                                                                                    "LZW decode table overflow");

      // Add this new String to the table
      m_aTab[m_nFreeCode] = aByteSeq;
      ++m_nFreeCode;

      if (m_nFreeCode == (bForEncode ? 512 : 511))
        m_nCodeBits = 10;
      else
        if (m_nFreeCode == (bForEncode ? 1024 : 1023))
          m_nCodeBits = 11;
        else
          if (m_nFreeCode == (bForEncode ? 2048 : 2047))
            m_nCodeBits = 12;
    }

    /**
     * @return The next free code in the dictionary. Always &ge; 0.
     */
    @Nonnegative
    public final int getNextFreeCode ()
    {
      return m_nFreeCode;
    }
  }

  protected static class LZWDecodeDictionary extends AbstractLZWDictionary
  {
    /**
     * Constructor.
     */
    public LZWDecodeDictionary ()
    {}

    /**
     * Read the next code
     *
     * @param aBIS
     *        The stream to read from
     * @return The next code
     * @throws IOException
     *         In case EOF is reached
     */
    public int readCode (@NonNull final NonBlockingBitInputStream aBIS) throws IOException
    {
      return aBIS.readBits (m_nCodeBits);
    }

    /**
     * Directly get all bytes for a given code without copying.
     *
     * @param nCode
     *        The code to look up. Must be &ge; 0.
     * @return The byte array for the given code, or <code>null</code> if the
     *         code is not in the dictionary.
     */
    @ReturnsMutableObject ("speed")
    public byte @Nullable [] directGetAllBytes (@Nonnegative final int nCode)
    {
      return m_aTab[nCode];
    }
  }

  protected static class LZWEncodeDictionary extends AbstractLZWDictionary
  {
    private final LZWNode m_aRoot = new LZWNode ();
    private final NonBlockingByteArrayOutputStream m_aByteBuf = new NonBlockingByteArrayOutputStream ();

    /**
     * Constructor.
     */
    public LZWEncodeDictionary ()
    {}

    @Override
    public void reset ()
    {
      super.reset ();
      for (int i = 0; i < 256; ++i)
        m_aRoot.setChildNode ((byte) i, new LZWNode (i));
      m_aByteBuf.reset ();
    }

    /**
     * @return The current code length in bits used for encoding.
     */
    public int getCodeLength ()
    {
      return m_nCodeBits;
    }

    /**
     * Visit a single byte during the encoding process. If a new byte sequence
     * is found that is not yet in the dictionary, it is added.
     *
     * @param nByteToVisit
     *        The byte to process.
     * @return <code>true</code> if a new entry was added to the dictionary,
     *         <code>false</code> otherwise.
     */
    public boolean visit (final byte nByteToVisit)
    {
      m_aByteBuf.write (nByteToVisit);

      LZWNode aCurNode = m_aRoot;
      for (final byte aByte : m_aByteBuf.toByteArray ())
      {
        final LZWNode aPrevNode = aCurNode;
        aCurNode = aCurNode.getChildNode (aByte);
        if (aCurNode == null)
        {
          // We found a new byte-sequence
          aPrevNode.setChildNode (aByte, new LZWNode (m_nFreeCode));
          addEntry (m_aByteBuf.toByteArray (), true);

          m_aByteBuf.reset ();
          m_aByteBuf.write (nByteToVisit);
          // Was added to the dictionary
          return true;
        }
      }

      // Not added to the dictionary
      return false;
    }

    /**
     * Get the node in the encoding tree corresponding to the provided byte
     * sequence.
     *
     * @param aBytes
     *        The byte sequence to look up. May not be <code>null</code>.
     * @return The corresponding node, or <code>null</code> if no such node
     *         exists.
     */
    @Nullable
    public LZWNode getNode (final byte @NonNull [] aBytes)
    {
      return m_aRoot.getChildNode (aBytes);
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger (LZWCodec.class);

  /**
   * Constructor.
   */
  public LZWCodec ()
  {}

  /**
   * Encode the passed buffer using LZW compression and write it to the output stream.
   *
   * @param aBuffer
   *        The buffer to be encoded. May be <code>null</code>.
   * @param nOfs
   *        The offset in the buffer to start encoding from.
   * @param nLen
   *        The number of bytes to encode.
   * @param aOS
   *        The output stream to write the encoded data to. May not be <code>null</code>.
   */
  public void encode (final byte @Nullable [] aBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @NonNull @WillNotClose final OutputStream aOS)
  {
    ValueEnforcer.notNull (aOS, "OutputStream");

    if (aBuffer == null)
      return;

    final NonBlockingBitOutputStream aBOS = new NonBlockingBitOutputStream (aOS, ByteOrder.LITTLE_ENDIAN);

    final LZWEncodeDictionary aDict = new LZWEncodeDictionary ();
    aDict.reset ();

    try
    {
      // Always the same
      aBOS.writeBits (AbstractLZWDictionary.CODE_CLEARTABLE, aDict.getCodeLength ());
      byte [] aByteSeq = CGlobal.EMPTY_BYTE_ARRAY;
      for (int nIndex = 0; nIndex < nLen; ++nIndex)
      {
        // Append current byte
        final byte nByteToEncode = aBuffer[nOfs + nIndex];
        aByteSeq = ArrayHelper.getConcatenated (aByteSeq, nByteToEncode);
        aDict.visit (nByteToEncode);
        final int nCodeLength = aDict.getCodeLength ();

        final LZWNode aCurNode = aDict.getNode (aByteSeq);
        if (nIndex + 1 == nLen)
        {
          // last byte
          aBOS.writeBits (aCurNode.getTableIndex (), nCodeLength);
          break;
        }

        // Is there a node for the following byte?
        if (aCurNode.getChildNode (aBuffer[nOfs + nIndex + 1]) == null)
        {
          // No -> write down
          aBOS.writeBits (aCurNode.getTableIndex (), nCodeLength);
          aByteSeq = CGlobal.EMPTY_BYTE_ARRAY;
        }

        if (aDict.getNextFreeCode () == AbstractLZWDictionary.MAX_CODE - 1)
        {
          if (LOGGER.isTraceEnabled ())
            LOGGER.trace ("Table overflow in encoding -> resetting (codelength=" +
                          nCodeLength +
                          ";byteseq#=" +
                          aByteSeq.length +
                          ")");
          aBOS.writeBits (AbstractLZWDictionary.CODE_CLEARTABLE, nCodeLength);
          aDict.reset ();
          nIndex -= aByteSeq.length;
          aByteSeq = CGlobal.EMPTY_BYTE_ARRAY;
        }
      }

      int nCodeLength = aDict.getCodeLength ();
      switch (aDict.getNextFreeCode ())
      {
        case 511:
        case 1023:
        case 2047:
          nCodeLength++;
          if (LOGGER.isDebugEnabled ())
            LOGGER.debug ("EOF char gets a new code length: " + nCodeLength);
          break;
        default:
          break;
      }

      aBOS.writeBits (AbstractLZWDictionary.CODE_EOF, nCodeLength);
    }
    catch (final Exception ex)
    {
      throw new EncodeException ("Failed to encode LZW", ex);
    }
    finally
    {
      // Flush but do not close
      StreamHelper.flush (aBOS);
    }
  }

  /**
   * Decode LZW compressed data from the input stream and write the decoded bytes to the output
   * stream.
   *
   * @param aEncodedIS
   *        The LZW encoded input stream to read from. May not be <code>null</code>.
   * @param aOS
   *        The output stream to write the decoded data to. May not be <code>null</code>.
   */
  public void decode (@NonNull @WillNotClose final InputStream aEncodedIS,
                      @NonNull @WillNotClose final OutputStream aOS)
  {
    ValueEnforcer.notNull (aEncodedIS, "EncodedInputStream");
    ValueEnforcer.notNull (aOS, "OutputStream");

    // Don't close!
    final NonBlockingBitInputStream aBIS = new NonBlockingBitInputStream (aEncodedIS, ByteOrder.LITTLE_ENDIAN);
    try
    {
      final LZWDecodeDictionary aDict = new LZWDecodeDictionary ();
      aDict.reset ();

      int nCode = aDict.readCode (aBIS);
      while (nCode == AbstractLZWDictionary.CODE_CLEARTABLE)
        nCode = aDict.readCode (aBIS);

      // May be EOF if encoded byte array was empty!
      if (nCode != AbstractLZWDictionary.CODE_EOF)
      {
        byte [] aByteSeq = aDict.directGetAllBytes (nCode);
        if (aByteSeq == null)
          throw new DecodeException ("Failed to resolve initial code " + nCode);
        aOS.write (aByteSeq);
        byte [] aPrevByteSeq = aByteSeq;
        while (true)
        {
          nCode = aDict.readCode (aBIS);
          if (nCode == AbstractLZWDictionary.CODE_EOF)
            break;
          if (nCode == AbstractLZWDictionary.CODE_CLEARTABLE)
          {
            aDict.reset ();

            nCode = aDict.readCode (aBIS);
            if (nCode == AbstractLZWDictionary.CODE_EOF)
              break;

            // upon clear table, don't add something to the table
            aByteSeq = aDict.directGetAllBytes (nCode);
            aOS.write (aByteSeq);
            aPrevByteSeq = aByteSeq;
          }
          else
          {
            final int nNextFreeCode = aDict.getNextFreeCode ();
            if (nCode < nNextFreeCode)
              aByteSeq = aDict.directGetAllBytes (nCode);
            else
              if (nCode == nNextFreeCode)
                aByteSeq = ArrayHelper.getConcatenated (aPrevByteSeq, aPrevByteSeq[0]);
              else
                throw new DecodeException ("Error decoding LZW: unexpected code " +
                                           nCode +
                                           " while next free code is " +
                                           nNextFreeCode);
            aOS.write (aByteSeq);
            aDict.addEntry (ArrayHelper.getConcatenated (aPrevByteSeq, aByteSeq[0]), false);
            aPrevByteSeq = aByteSeq;
          }
        }
      }
    }
    catch (final EOFException ex)
    {
      throw new DecodeException ("Unexpected EOF decoding LZW", ex);
    }
    catch (final IOException ex)
    {
      throw new DecodeException ("Failed to decode LZW", ex);
    }
  }

  /**
   * Decode the passed LZW compressed buffer and write the decoded bytes to the output stream.
   *
   * @param aEncodedBuffer
   *        The LZW compressed buffer to be decoded. May be <code>null</code>.
   * @param nOfs
   *        The offset in the buffer to start decoding from.
   * @param nLen
   *        The number of bytes to decode.
   * @param aOS
   *        The output stream to write the decoded data to. May not be <code>null</code>.
   */
  public void decode (final byte @Nullable [] aEncodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @NonNull @WillNotClose final OutputStream aOS)
  {
    if (aEncodedBuffer == null)
      return;

    try (final NonBlockingByteArrayInputStream aIS = new NonBlockingByteArrayInputStream (aEncodedBuffer, nOfs, nLen))
    {
      decode (aIS, aOS);
    }
  }
}
