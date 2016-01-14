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
package com.helger.commons.io.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.WillNotClose;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.collection.ArrayHelper;

/**
 * Wrapper for an {@link java.io.OutputStream} around a
 * {@link java.nio.ByteBuffer}.
 *
 * @author Philip Helger
 */
public class ByteBufferOutputStream extends OutputStream
{
  public static final int DEFAULT_BUF_SIZE = 1024;
  public static final boolean DEFAULT_CAN_GROW = true;

  private ByteBuffer m_aBuffer;
  private final boolean m_bCanGrow;

  /**
   * Create a new object with the {@link #DEFAULT_BUF_SIZE} buffer size and it
   * can grow.
   */
  public ByteBufferOutputStream ()
  {
    this (DEFAULT_BUF_SIZE, DEFAULT_CAN_GROW);
  }

  /**
   * Constructor for an output stream than can grow.
   *
   * @param nBytes
   *        The initial number of bytes the buffer has. Must be &ge; 0.
   */
  public ByteBufferOutputStream (@Nonnegative final int nBytes)
  {
    this (nBytes, DEFAULT_CAN_GROW);
  }

  /**
   * Constructor
   *
   * @param nBytes
   *        The number of bytes the buffer has initially. Must be &ge; 0.
   * @param bCanGrow
   *        <code>true</code> if the buffer can grow, <code>false</code>
   *        otherwise.
   */
  public ByteBufferOutputStream (@Nonnegative final int nBytes, final boolean bCanGrow)
  {
    this (ByteBuffer.allocate (nBytes), bCanGrow);
  }

  /**
   * Constructor with an existing byte array to wrap. This output stream cannot
   * grow!
   *
   * @param aArray
   *        The array to be backed by a {@link ByteBuffer}.
   */
  public ByteBufferOutputStream (@Nonnull final byte [] aArray)
  {
    this (ByteBuffer.wrap (aArray), false);
  }

  /**
   * Constructor with an existing byte array to wrap. This output stream cannot
   * grow!
   *
   * @param aArray
   *        The array to be backed by a {@link ByteBuffer}.
   * @param nOfs
   *        Offset into the byte array. Must be &ge; 0.
   * @param nLen
   *        Number of bytes to wrap. Must be &ge; 0.
   */
  public ByteBufferOutputStream (@Nonnull final byte [] aArray,
                                 @Nonnegative final int nOfs,
                                 @Nonnegative final int nLen)
  {
    this (ByteBuffer.wrap (aArray, nOfs, nLen), false);
  }

  /**
   * Constructor
   *
   * @param aBuffer
   *        The byte buffer to use. May not be <code>null</code>.
   * @param bCanGrow
   *        <code>true</code> if the buffer can grow, <code>false</code>
   *        otherwise.
   */
  public ByteBufferOutputStream (@Nonnull final ByteBuffer aBuffer, final boolean bCanGrow)
  {
    m_aBuffer = ValueEnforcer.notNull (aBuffer, "Buffer");
    m_bCanGrow = bCanGrow;
  }

  /**
   * @return The contained buffer. Never <code>null</code>.
   */
  @Nonnull
  public ByteBuffer getBuffer ()
  {
    return m_aBuffer;
  }

  /**
   * @return <code>true</code> if this buffer can grow, <code>false</code> if
   *         not.
   */
  public boolean canGrow ()
  {
    return m_bCanGrow;
  }

  @Override
  public void close ()
  {}

  /**
   * Reset the backing byte buffer
   */
  public void reset ()
  {
    m_aBuffer.clear ();
  }

  /**
   * @return The number of bytes currently in the buffer. Always &ge; 0.
   */
  @Nonnegative
  public int size ()
  {
    return m_aBuffer.position ();
  }

  /**
   * Get everything as a big byte array, without altering the ByteBuffer.
   *
   * @return The content of the buffer as a byte array. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public byte [] getAsByteArray ()
  {
    final byte [] aArray = m_aBuffer.array ();
    final int nOfs = m_aBuffer.arrayOffset ();
    final int nLength = m_aBuffer.position ();

    return ArrayHelper.getCopy (aArray, nOfs, nLength);
  }

  /**
   * Write everything currently contained to the specified buffer. If the passed
   * buffer is too small, a {@link java.nio.BufferOverflowException} is thrown.
   * The copied elements are removed from this streams buffer.
   *
   * @param aDestBuffer
   *        The destination buffer to write to. May not be <code>null</code>.
   */
  public void writeTo (@Nonnull final ByteBuffer aDestBuffer)
  {
    ValueEnforcer.notNull (aDestBuffer, "DestBuffer");

    m_aBuffer.flip ();
    aDestBuffer.put (m_aBuffer);
    m_aBuffer.compact ();
  }

  /**
   * Writes the current content to the passed buffer. The copied elements are
   * removed from this streams buffer.
   *
   * @param aBuf
   *        The buffer to be filled. May not be <code>null</code>.
   */
  public void writeTo (@Nonnull final byte [] aBuf)
  {
    ValueEnforcer.notNull (aBuf, "Buffer");

    writeTo (aBuf, 0, aBuf.length);
  }

  /**
   * Write current content to the passed byte array. The copied elements are
   * removed from this streams buffer.
   *
   * @param aBuf
   *        Byte array to write to. May not be <code>null</code>.
   * @param nOfs
   *        Offset to start writing. Must be &ge; 0.
   * @param nLen
   *        Number of bytes to copy. Must be &ge; 0.
   */
  public void writeTo (@Nonnull final byte [] aBuf, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (aBuf, nOfs, nLen);

    m_aBuffer.flip ();
    m_aBuffer.get (aBuf, nOfs, nLen);
    m_aBuffer.compact ();
  }

  /**
   * Write everything to the passed output stream and clear the contained
   * buffer.
   *
   * @param aOS
   *        The output stream to write to. May not be <code>null</code>.
   * @throws IOException
   *         In case of IO error
   */
  public void writeTo (@Nonnull @WillNotClose final OutputStream aOS) throws IOException
  {
    ValueEnforcer.notNull (aOS, "OutputStream");

    aOS.write (m_aBuffer.array (), m_aBuffer.arrayOffset (), m_aBuffer.position ());
    m_aBuffer.clear ();
  }

  /**
   * Get the content as a string without modifying the buffer
   *
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @return The String representation.
   */
  @Nonnull
  public String getAsString (@Nonnull final Charset aCharset)
  {
    return CharsetManager.getAsString (m_aBuffer.array (), m_aBuffer.arrayOffset (), m_aBuffer.position (), aCharset);
  }

  private void _growBy (@Nonnegative final int nBytesToGrow)
  {
    final int nCurSize = m_aBuffer.capacity ();
    final int nNewSize = Math.max (nCurSize << 1, nCurSize + nBytesToGrow);
    final ByteBuffer aNewBuffer = ByteBuffer.allocate (nNewSize);
    m_aBuffer.flip ();
    aNewBuffer.put (m_aBuffer);
    m_aBuffer = aNewBuffer;
  }

  @Override
  public void write (final int b)
  {
    if (m_bCanGrow && !m_aBuffer.hasRemaining ())
      _growBy (1);

    m_aBuffer.put ((byte) b);
  }

  @Override
  public void write (@Nonnull final byte [] aBuf, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    if (m_bCanGrow && nLen > m_aBuffer.remaining ())
      _growBy (nLen);

    m_aBuffer.put (aBuf, nOfs, nLen);
  }

  /**
   * Write the content from the passed byte buffer to this output stream.
   *
   * @param aSrcBuffer
   *        The buffer to use. May not be <code>null</code>.
   */
  public void write (@Nonnull final ByteBuffer aSrcBuffer)
  {
    ValueEnforcer.notNull (aSrcBuffer, "SourceBuffer");

    if (m_bCanGrow && aSrcBuffer.remaining () > m_aBuffer.remaining ())
      _growBy (aSrcBuffer.remaining ());

    m_aBuffer.put (aSrcBuffer);
  }
}
