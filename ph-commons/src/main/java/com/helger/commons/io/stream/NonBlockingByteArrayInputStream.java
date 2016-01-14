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
import java.io.Serializable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A non-synchronized copy of the class {@link java.io.ByteArrayInputStream}.
 *
 * @author Philip Helger
 * @see java.io.ByteArrayInputStream
 */
public class NonBlockingByteArrayInputStream extends InputStream implements Serializable
{
  /**
   * An array of bytes that was provided by the creator of the stream. Elements
   * <code>buf[0]</code> through <code>buf[count-1]</code> are the only bytes
   * that can ever be read from the stream; element <code>buf[pos]</code> is the
   * next byte to be read.
   */
  protected byte [] m_aBuf;

  /**
   * The index of the next character to read from the input stream buffer. This
   * value should always be nonnegative and not larger than the value of
   * <code>count</code>. The next byte to be read from the input stream buffer
   * will be <code>buf[pos]</code>.
   */
  protected int m_nPos;

  /**
   * The currently marked position in the stream. ByteArrayInputStream objects
   * are marked at position zero by default when constructed. They may be marked
   * at another position within the buffer by the <code>mark()</code> method.
   * The current buffer position is set to this point by the
   * <code>reset()</code> method.
   * <p>
   * If no mark has been set, then the value of mark is the offset passed to the
   * constructor (or 0 if the offset was not supplied).
   */
  protected int m_nMark;

  /**
   * The index one greater than the last valid character in the input stream
   * buffer. This value should always be nonnegative and not larger than the
   * length of <code>buf</code>. It is one greater than the position of the last
   * byte within <code>buf</code> that can ever be read from the input stream
   * buffer.
   */
  protected int m_nCount;

  /**
   * Creates a <code>ByteArrayInputStream</code> so that it uses
   * <code>buf</code> as its buffer array. The buffer array is not copied. The
   * initial value of <code>pos</code> is <code>0</code> and the initial value
   * of <code>count</code> is the length of <code>buf</code>.
   *
   * @param aBuf
   *        the input buffer.
   */
  @SuppressFBWarnings ({ "EI_EXPOSE_REP2" })
  public NonBlockingByteArrayInputStream (@Nonnull final byte [] aBuf)
  {
    this (aBuf, 0, aBuf.length);
  }

  /**
   * Creates <code>ByteArrayInputStream</code> that uses <code>aBuf</code> as
   * its buffer array. The initial value of <code>nOfs</code> is
   * <code>offset</code> and the initial value of <code>m_nCount</code> is the
   * minimum of <code>nOfs+nLen</code> and <code>aBuf.length</code>. The buffer
   * array is not copied. The buffer's mark is set to the specified offset.
   *
   * @param aBuf
   *        the input buffer.
   * @param nOfs
   *        the offset in the buffer of the first byte to read.
   * @param nLen
   *        the maximum number of bytes to read from the buffer.
   */
  @SuppressFBWarnings ({ "EI_EXPOSE_REP2" })
  public NonBlockingByteArrayInputStream (final byte [] aBuf, final int nOfs, final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (aBuf, nOfs, nLen);
    m_aBuf = aBuf;
    m_nPos = nOfs;
    m_nCount = Math.min (nOfs + nLen, aBuf.length);
    m_nMark = nOfs;
  }

  /**
   * Reads the next byte of data from this input stream. The value byte is
   * returned as an <code>int</code> in the range <code>0</code> to
   * <code>255</code>. If no byte is available because the end of the stream has
   * been reached, the value <code>-1</code> is returned.
   * <p>
   * This <code>read</code> method cannot block.
   *
   * @return the next byte of data, or <code>-1</code> if the end of the stream
   *         has been reached.
   */
  @Override
  public int read ()
  {
    return m_nPos < m_nCount ? (m_aBuf[m_nPos++] & 0xff) : -1;
  }

  /**
   * Reads up to <code>len</code> bytes of data into an array of bytes from this
   * input stream. If <code>pos</code> equals <code>count</code>, then
   * <code>-1</code> is returned to indicate end of file. Otherwise, the number
   * <code>k</code> of bytes read is equal to the smaller of <code>len</code>
   * and <code>count-pos</code>. If <code>k</code> is positive, then bytes
   * <code>buf[pos]</code> through <code>buf[pos+k-1]</code> are copied into
   * <code>b[off]</code> through <code>b[off+k-1]</code> in the manner performed
   * by <code>System.arraycopy</code>. The value <code>k</code> is added into
   * <code>pos</code> and <code>k</code> is returned.
   * <p>
   * This <code>read</code> method cannot block.
   *
   * @param aBuf
   *        the buffer into which the data is read.
   * @param nOfs
   *        the start offset in the destination array <code>b</code>
   * @param nLen
   *        the maximum number of bytes read.
   * @return the total number of bytes read into the buffer, or <code>-1</code>
   *         if there is no more data because the end of the stream has been
   *         reached.
   */
  @Override
  public int read (final byte [] aBuf, final int nOfs, final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (aBuf, nOfs, nLen);

    if (m_nPos >= m_nCount)
      return -1;
    final int nRealLen = m_nPos + nLen > m_nCount ? m_nCount - m_nPos : nLen;
    if (nRealLen <= 0)
      return 0;
    System.arraycopy (m_aBuf, m_nPos, aBuf, nOfs, nRealLen);
    m_nPos += nRealLen;
    return nRealLen;
  }

  /**
   * Skips <code>n</code> bytes of input from this input stream. Fewer bytes
   * might be skipped if the end of the input stream is reached. The actual
   * number <code>k</code> of bytes to be skipped is equal to the smaller of
   * <code>n</code> and <code>count-pos</code>. The value <code>k</code> is
   * added into <code>pos</code> and <code>k</code> is returned.
   *
   * @param n
   *        the number of bytes to be skipped.
   * @return the actual number of bytes skipped.
   */
  @Override
  public long skip (final long n)
  {
    final long nSkip = m_nPos + n > m_nCount ? m_nCount - m_nPos : n;
    if (nSkip <= 0)
      return 0;
    m_nPos += nSkip;
    return nSkip;
  }

  /**
   * Returns the number of remaining bytes that can be read (or skipped over)
   * from this input stream.
   * <p>
   * The value returned is <code>count&nbsp;- pos</code>, which is the number of
   * bytes remaining to be read from the input buffer.
   *
   * @return the number of remaining bytes that can be read (or skipped over)
   *         from this input stream without blocking.
   */
  @Override
  public int available ()
  {
    return m_nCount - m_nPos;
  }

  /**
   * Tests if this <code>InputStream</code> supports mark/reset. The
   * <code>markSupported</code> method of <code>ByteArrayInputStream</code>
   * always returns <code>true</code>.
   *
   * @return Always true
   */
  @Override
  public boolean markSupported ()
  {
    return true;
  }

  /**
   * Set the current marked position in the stream. ByteArrayInputStream objects
   * are marked at position zero by default when constructed. They may be marked
   * at another position within the buffer by this method.
   * <p>
   * If no mark has been set, then the value of the mark is the offset passed to
   * the constructor (or 0 if the offset was not supplied).
   * <p>
   * Note: The <code>readAheadLimit</code> for this class has no meaning.
   */
  @SuppressWarnings ("sync-override")
  @Override
  public void mark (final int readAheadLimit)
  {
    m_nMark = m_nPos;
  }

  /**
   * Resets the buffer to the marked position. The marked position is 0 unless
   * another position was marked or an offset was specified in the constructor.
   */
  @SuppressWarnings ("sync-override")
  @Override
  public void reset ()
  {
    m_nPos = m_nMark;
  }

  /**
   * Closing a <tt>ByteArrayInputStream</tt> has no effect. The methods in this
   * class can be called after the stream has been closed without generating an
   * <tt>IOException</tt>.
   */
  @Override
  public void close ()
  {}

  /**
   * @return The position where we are in the stream.
   */
  @Nonnegative
  public int getPosition ()
  {
    return m_nPos;
  }
}
