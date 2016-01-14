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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;

/**
 * A non-synchronized copy of the class {@link java.io.BufferedOutputStream}.
 *
 * @author Philip Helger
 * @see java.io.BufferedOutputStream
 */
public class NonBlockingBufferedOutputStream extends FilterOutputStream
{
  /**
   * The internal buffer where data is stored.
   */
  protected byte [] m_aBuf;

  /**
   * The number of valid bytes in the buffer. This value is always in the range
   * <tt>0</tt> through <tt>buf.length</tt>; elements <tt>buf[0]</tt> through
   * <tt>buf[count-1]</tt> contain valid byte data.
   */
  protected int m_nCount;

  /**
   * Creates a new buffered output stream to write data to the specified
   * underlying output stream.
   *
   * @param aOS
   *        the underlying output stream.
   */
  public NonBlockingBufferedOutputStream (@Nonnull final OutputStream aOS)
  {
    this (aOS, 8192);
  }

  /**
   * Creates a new buffered output stream to write data to the specified
   * underlying output stream with the specified buffer size.
   *
   * @param aOS
   *        the underlying output stream.
   * @param nSize
   *        the buffer size.
   * @exception IllegalArgumentException
   *            if size &lt;= 0.
   */
  public NonBlockingBufferedOutputStream (@Nonnull final OutputStream aOS, @Nonnegative final int nSize)
  {
    super (aOS);
    ValueEnforcer.isGT0 (nSize, "Size");
    m_aBuf = new byte [nSize];
  }

  /** Flush the internal buffer */
  private void _flushBuffer () throws IOException
  {
    if (m_nCount > 0)
    {
      out.write (m_aBuf, 0, m_nCount);
      m_nCount = 0;
    }
  }

  /**
   * Writes the specified byte to this buffered output stream.
   *
   * @param b
   *        the byte to be written.
   * @exception IOException
   *            if an I/O error occurs.
   */
  @Override
  public void write (final int b) throws IOException
  {
    if (m_nCount >= m_aBuf.length)
      _flushBuffer ();
    m_aBuf[m_nCount++] = (byte) b;
  }

  /**
   * Writes <code>len</code> bytes from the specified byte array starting at
   * offset <code>off</code> to this buffered output stream.
   * <p>
   * Ordinarily this method stores bytes from the given array into this stream's
   * buffer, flushing the buffer to the underlying output stream as needed. If
   * the requested length is at least as large as this stream's buffer, however,
   * then this method will flush the buffer and write the bytes directly to the
   * underlying output stream. Thus redundant <code>BufferedOutputStream</code>s
   * will not copy data unnecessarily.
   *
   * @param aBuf
   *        the data.
   * @param nOfs
   *        the start offset in the data.
   * @param nLen
   *        the number of bytes to write.
   * @exception IOException
   *            if an I/O error occurs.
   */
  @Override
  public void write (final byte [] aBuf, final int nOfs, final int nLen) throws IOException
  {
    if (nLen >= m_aBuf.length)
    {
      /*
       * If the request length exceeds the size of the output buffer, flush the
       * output buffer and then write the data directly. In this way buffered
       * streams will cascade harmlessly.
       */
      _flushBuffer ();
      out.write (aBuf, nOfs, nLen);
      return;
    }
    if (nLen > m_aBuf.length - m_nCount)
      _flushBuffer ();
    System.arraycopy (aBuf, nOfs, m_aBuf, m_nCount, nLen);
    m_nCount += nLen;
  }

  /**
   * Flushes this buffered output stream. This forces any buffered output bytes
   * to be written out to the underlying output stream.
   *
   * @exception IOException
   *            if an I/O error occurs.
   */
  @Override
  public void flush () throws IOException
  {
    _flushBuffer ();
    out.flush ();
  }
}
