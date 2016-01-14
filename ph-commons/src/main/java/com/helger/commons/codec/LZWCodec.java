/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.codec;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.io.stream.NonBlockingBitInputStream;
import com.helger.commons.io.stream.NonBlockingBitOutputStream;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.string.ToStringGenerator;

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

    public LZWNode ()
    {
      // only for the root node
      m_nTableIndex = -1;
    }

    public LZWNode (@Nonnegative final int nTableIndex)
    {
      ValueEnforcer.isBetweenInclusive (nTableIndex, "TableIndex", 0, AbstractLZWDictionary.MAX_CODE);
      m_nTableIndex = nTableIndex;
    }

    @Nonnegative
    public int getTableIndex ()
    {
      if (m_nTableIndex < 0)
        throw new IllegalStateException ("This node has no table index!");
      return m_nTableIndex;
    }

    public void setChildNode (@Nonnegative final byte nIndex, @Nonnull final LZWNode aNode)
    {
      ValueEnforcer.notNull (aNode, "Node");
      if (m_aChildren == null)
        m_aChildren = new LZWNode [256];
      m_aChildren[nIndex & 0xff] = aNode;
    }

    @Nullable
    public LZWNode getChildNode (final byte nIndex)
    {
      return m_aChildren == null ? null : m_aChildren[nIndex & 0xff];
    }

    /**
     * This will traverse the tree until it gets to the sub node. This will
     * return null if the node does not exist.
     *
     * @param aBuffer
     *        The path to the node.
     * @return The node that resides at the data path.
     */
    @Nullable
    public LZWNode getChildNode (@Nonnull final byte [] aBuffer)
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
      return new ToStringGenerator (null).append ("index", m_nTableIndex)
                                         .append ("children#", ArrayHelper.getSize (m_aChildren))
                                         .toString ();
    }
  }

  protected abstract static class AbstractLZWDictionary
  {
    @SuppressWarnings ("hiding")
    protected static final Logger s_aLogger = LoggerFactory.getLogger (AbstractLZWDictionary.class);
    /** Maximum index */
    public static final int MAX_CODE = 4096;
    /** Special code to clear the table */
    public static final int CODE_CLEARTABLE = 256;
    /** Special code for end of file */
    public static final int CODE_EOF = 257;

    protected byte [] [] m_aTab;
    protected int m_nFreeCode;
    protected int m_nCodeBits;

    public AbstractLZWDictionary ()
    {}

    public void reset ()
    {
      m_aTab = new byte [MAX_CODE] [];
      for (int i = 0; i < 256; ++i)
        m_aTab[i] = new byte [] { (byte) i };
      m_nFreeCode = CODE_EOF + 1;
      m_nCodeBits = 9;
    }

    public final void addEntry (@Nonnull final byte [] aByteSeq, final boolean bForEncode)
    {
      ValueEnforcer.notNull (aByteSeq, "ByteSeq");
      if (m_nFreeCode == m_aTab.length)
        throw bForEncode ? new EncodeException ("LZW encode table overflow")
                         : new DecodeException ("LZW decode table overflow");

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

    @Nonnegative
    public final int getNextFreeCode ()
    {
      return m_nFreeCode;
    }
  }

  protected static class LZWDecodeDictionary extends AbstractLZWDictionary
  {
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
    public int readCode (@Nonnull final NonBlockingBitInputStream aBIS) throws IOException
    {
      return aBIS.readBits (m_nCodeBits);
    }

    @Nullable
    @ReturnsMutableObject ("speed")
    public byte [] getAllBytes (@Nonnegative final int nCode)
    {
      return m_aTab[nCode];
    }
  }

  protected static class LZWEncodeDictionary extends AbstractLZWDictionary
  {
    private final LZWNode m_aRoot = new LZWNode ();
    private final NonBlockingByteArrayOutputStream m_aByteBuf = new NonBlockingByteArrayOutputStream ();

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

    public int getCodeLength ()
    {
      return m_nCodeBits;
    }

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

    @Nullable
    public LZWNode getNode (@Nonnull final byte [] aBytes)
    {
      return m_aRoot.getChildNode (aBytes);
    }
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (LZWCodec.class);

  public LZWCodec ()
  {}

  @Nullable
  @ReturnsMutableCopy
  public static byte [] getDecodedLZW (@Nullable final byte [] aEncodedBuffer)
  {
    if (aEncodedBuffer == null)
      return null;

    try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (aEncodedBuffer);
        final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      getDecodedLZW (aBAIS, aBAOS);
      return aBAOS.toByteArray ();
    }
  }

  public static void getDecodedLZW (@Nonnull @WillNotClose final InputStream aEncodedIS,
                                    @Nonnull @WillNotClose final OutputStream aOS)
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
        byte [] aByteSeq = aDict.getAllBytes (nCode);
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
            aByteSeq = aDict.getAllBytes (nCode);
            aOS.write (aByteSeq);
            aPrevByteSeq = aByteSeq;
          }
          else
          {
            final int nNextFreeCode = aDict.getNextFreeCode ();
            if (nCode < nNextFreeCode)
              aByteSeq = aDict.getAllBytes (nCode);
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
      throw new DecodeException ("Error decoding LZW", ex);
    }
  }

  @Nullable
  @ReturnsMutableCopy
  public byte [] getDecoded (@Nullable final byte [] aEncodedBuffer)
  {
    return getDecodedLZW (aEncodedBuffer);
  }

  @Nullable
  @ReturnsMutableCopy
  public static byte [] getEncodedLZW (@Nullable final byte [] aBuffer)
  {
    if (aBuffer == null)
      return null;

    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    getEncodedLZW (aBuffer, aBAOS);
    return aBAOS.toByteArray ();
  }

  /**
   * LZW-encode the passed byte array to the passed output stream
   *
   * @param aBuffer
   *        The buffer to be encoded. May be <code>null</code> in which case
   *        nothing happens.
   * @param aOS
   *        The output stream to encode the content to. The output stream is not
   *        closed after encoding is done! May not be <code>null</code>.
   */
  public static void getEncodedLZW (@Nullable final byte [] aBuffer, @Nonnull @WillNotClose final OutputStream aOS)
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
      byte [] aByteSeq = ArrayHelper.EMPTY_BYTE_ARRAY;
      for (int nIndex = 0; nIndex < aBuffer.length; ++nIndex)
      {
        // Append current byte
        final byte nByteToEncode = aBuffer[nIndex];
        aByteSeq = ArrayHelper.getConcatenated (aByteSeq, nByteToEncode);
        aDict.visit (nByteToEncode);
        final int nCodeLength = aDict.getCodeLength ();

        final LZWNode aCurNode = aDict.getNode (aByteSeq);
        if (nIndex + 1 == aBuffer.length)
        {
          // last byte
          aBOS.writeBits (aCurNode.getTableIndex (), nCodeLength);
          break;
        }

        // Is there a node for the following byte?
        if (aCurNode.getChildNode (aBuffer[nIndex + 1]) == null)
        {
          // No -> write down
          aBOS.writeBits (aCurNode.getTableIndex (), nCodeLength);
          aByteSeq = ArrayHelper.EMPTY_BYTE_ARRAY;
        }

        if (aDict.getNextFreeCode () == AbstractLZWDictionary.MAX_CODE - 1)
        {
          if (s_aLogger.isTraceEnabled ())
            s_aLogger.trace ("Table overflow in encoding -> resetting (codelength=" +
                             nCodeLength +
                             ";byteseq#=" +
                             aByteSeq.length +
                             ")");
          aBOS.writeBits (AbstractLZWDictionary.CODE_CLEARTABLE, nCodeLength);
          aDict.reset ();
          nIndex -= aByteSeq.length;
          aByteSeq = ArrayHelper.EMPTY_BYTE_ARRAY;
        }
      }

      int nCodeLength = aDict.getCodeLength ();
      switch (aDict.getNextFreeCode ())
      {
        case 511:
        case 1023:
        case 2047:
          nCodeLength++;
          if (s_aLogger.isDebugEnabled ())
            s_aLogger.debug ("EOF char gets a new code length: " + nCodeLength);
          break;
        default:
          break;
      }

      aBOS.writeBits (AbstractLZWDictionary.CODE_EOF, nCodeLength);
    }
    catch (final Throwable t)
    {
      throw new EncodeException ("Error encoding LZW", t);
    }
    finally
    {
      // Flush but do not close
      StreamHelper.flush (aBOS);
    }
  }

  @Nullable
  @ReturnsMutableCopy
  public byte [] getEncoded (@Nullable final byte [] aBuffer)
  {
    return getEncodedLZW (aBuffer);
  }
}
