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

import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.lang.ByteBufferHelper;

/**
 * {@link java.io.InputStream} wrapped around one or more
 * {@link java.nio.ByteBuffer} objects.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class ByteBuffersInputStream extends InputStream
{
  private ByteBuffer [] m_aBuffers;
  private int m_nBufferIndex = 0;
  private int m_nMarkedBufferIndex = -1;

  /**
   * Constructor
   *
   * @param aBuffers
   *        Array of {@link ByteBuffer}. May neither be <code>null</code> nor
   *        empty and may not contain <code>null</code> elements.
   */
  public ByteBuffersInputStream (@Nonnull @Nonempty final ByteBuffer... aBuffers)
  {
    ValueEnforcer.notEmpty (aBuffers, "Buffers");

    m_aBuffers = ArrayHelper.getCopy (aBuffers);
  }

  public boolean isClosed ()
  {
    return m_aBuffers == null;
  }

  private void _checkClosed ()
  {
    if (isClosed ())
      throw new IllegalStateException ("ByteBuffers are already closed");
  }

  /**
   * @return A copy of the array with the byte buffers. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ByteBuffer [] getAllBuffers ()
  {
    _checkClosed ();
    return ArrayHelper.getCopy (m_aBuffers);
  }

  /**
   * @return <code>true</code> if any byte buffer has at least one byte left.
   */
  public boolean isAnythingAvailable ()
  {
    _checkClosed ();
    for (int i = m_aBuffers.length - 1; i >= m_nBufferIndex; --i)
      if (m_aBuffers[i].hasRemaining ())
        return true;
    return false;
  }

  /**
   * @return The number of available bytes as a long. Always &ge; 0.
   */
  @Nonnegative
  public long getAvailable ()
  {
    _checkClosed ();
    long nRet = 0;
    for (int i = m_aBuffers.length - 1; i >= m_nBufferIndex; --i)
      nRet += m_aBuffers[i].remaining ();
    return nRet;
  }

  @Override
  @Nonnegative
  public int available ()
  {
    final long nAvailable = getAvailable ();
    if (nAvailable > Integer.MAX_VALUE)
      throw new IllegalStateException ("Value does not fit in an int: " + nAvailable);
    return (int) nAvailable;
  }

  @Override
  public void close ()
  {
    m_nBufferIndex = 0;
    m_nMarkedBufferIndex = -1;
    m_aBuffers = null;
  }

  @SuppressWarnings ("sync-override")
  @Override
  public void mark (final int nReadlimit)
  {
    _checkClosed ();
    if (m_nBufferIndex < m_aBuffers.length)
    {
      m_nMarkedBufferIndex = m_nBufferIndex;
      for (int i = m_aBuffers.length - 1; i >= m_nMarkedBufferIndex; --i)
        m_aBuffers[i].mark ();
    }
  }

  @SuppressWarnings ("sync-override")
  @Override
  public void reset ()
  {
    _checkClosed ();
    if (m_nMarkedBufferIndex != -1)
    {
      m_nBufferIndex = m_nMarkedBufferIndex;
      for (int i = m_aBuffers.length - 1; i >= m_nBufferIndex; --i)
        m_aBuffers[i].reset ();
      m_nMarkedBufferIndex = -1;
    }
  }

  @Override
  public boolean markSupported ()
  {
    return true;
  }

  @Override
  public int read ()
  {
    _checkClosed ();
    while (m_nBufferIndex < m_aBuffers.length)
    {
      final ByteBuffer aByteBuffer = m_aBuffers[m_nBufferIndex];
      if (aByteBuffer.hasRemaining ())
        return aByteBuffer.get () & 0xff;

      ++m_nBufferIndex;
    }

    // EOF
    return -1;
  }

  @Override
  public int read (@Nonnull final byte [] aBuf)
  {
    return read (aBuf, 0, aBuf.length);
  }

  @Override
  public int read (@Nonnull final byte [] aBuf, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (aBuf, nOfs, nLen);

    _checkClosed ();
    if (m_nBufferIndex >= m_aBuffers.length)
      return -1;
    if (m_nBufferIndex == m_aBuffers.length - 1 && !m_aBuffers[m_nBufferIndex].hasRemaining ())
      return -1;
    if (nLen == 0 || aBuf.length == 0)
      return isAnythingAvailable () ? 0 : -1;

    int nReadSoFar = 0;
    while (m_nBufferIndex < m_aBuffers.length)
    {
      final ByteBuffer aByteBuffer = m_aBuffers[m_nBufferIndex];

      // What we can read from the current buffer?
      final int nMaxRead = Math.min (aByteBuffer.remaining (), nLen - nReadSoFar);

      // Main read
      aByteBuffer.get (aBuf, nOfs + nReadSoFar, nMaxRead);
      nReadSoFar += nMaxRead;

      // We're done
      if (nReadSoFar == nLen)
        break;

      ++m_nBufferIndex;
    }

    // Return EOF if no bytes available
    return nReadSoFar > 0 ? nReadSoFar : -1;
  }

  @Override
  @Nonnegative
  public long skip (final long nBytesToSkip)
  {
    _checkClosed ();
    long nSkippedSoFar = 0;
    while (m_nBufferIndex < m_aBuffers.length)
    {
      final ByteBuffer aByteBuffer = m_aBuffers[m_nBufferIndex];

      // Skip whatever possible from the current buffer
      final long nSkip = Math.min (aByteBuffer.remaining (), nBytesToSkip - nSkippedSoFar);
      aByteBuffer.position (aByteBuffer.position () + (int) nSkip);
      nSkippedSoFar += nSkip;

      // We're done
      if (nSkippedSoFar == nBytesToSkip)
        break;

      ++m_nBufferIndex;
    }

    return nSkippedSoFar;
  }

  /**
   * Reads as much as possible into the destination buffer.
   *
   * @param aDestByteBuffer
   *        The destination byte buffer to use. May not be <code>null</code>.
   * @return The number of bytes read. Always &ge; 0.
   */
  @Nonnegative
  public long read (@Nonnull final ByteBuffer aDestByteBuffer)
  {
    ValueEnforcer.notNull (aDestByteBuffer, "DestByteBuffer");

    _checkClosed ();
    long nBytesRead = 0;
    while (m_nBufferIndex < m_aBuffers.length)
    {
      final ByteBuffer aByteBuffer = m_aBuffers[m_nBufferIndex];
      if (aByteBuffer.hasRemaining ())
        nBytesRead += ByteBufferHelper.transfer (aByteBuffer, aDestByteBuffer, false);
      if (!aDestByteBuffer.hasRemaining ())
        break;
      // Try next ByteBuffer
      ++m_nBufferIndex;
    }
    return nBytesRead;
  }
}
