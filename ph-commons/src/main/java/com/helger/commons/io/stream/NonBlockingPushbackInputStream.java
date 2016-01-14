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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;

/**
 * A non-synchronized copy of the class {@link java.io.PushbackInputStream}.
 *
 * @author Philip Helger
 * @see java.io.PushbackInputStream
 */
public class NonBlockingPushbackInputStream extends FilterInputStream
{
  /**
   * The pushback buffer.
   *
   * @since JDK1.1
   */
  protected byte [] m_aBuf;

  /**
   * The position within the pushback buffer from which the next byte will be
   * read. When the buffer is empty, <code>pos</code> is equal to
   * <code>buf.length</code>; when the buffer is full, <code>pos</code> is equal
   * to zero.
   *
   * @since JDK1.1
   */
  protected int m_nBufPos;

  /**
   * Creates a <code>PushbackInputStream</code> with a pushback buffer of the
   * specified <code>size</code>, and saves its argument, the input stream
   * <code>in</code>, for later use. Initially, there is no pushed-back byte
   * (the field <code>pushBack</code> is initialized to <code>-1</code>).
   *
   * @param aIS
   *        the input stream from which bytes will be read.
   * @param nSize
   *        the size of the pushback buffer.
   * @exception IllegalArgumentException
   *            if size is &le; 0
   * @since JDK1.1
   */
  public NonBlockingPushbackInputStream (@Nonnull final InputStream aIS, @Nonnegative final int nSize)
  {
    super (aIS);
    ValueEnforcer.isGT0 (nSize, "Size");
    m_aBuf = new byte [nSize];
    m_nBufPos = nSize;
  }

  /**
   * Creates a <code>PushbackInputStream</code> and saves its argument, the
   * input stream <code>in</code>, for later use. Initially, there is no
   * pushed-back byte (the field <code>pushBack</code> is initialized to
   * <code>-1</code>).
   *
   * @param aIS
   *        the input stream from which bytes will be read.
   */
  public NonBlockingPushbackInputStream (@Nonnull final InputStream aIS)
  {
    this (aIS, 1);
  }

  /**
   * Check to make sure that this stream has not been closed
   */
  private void _ensureOpen () throws IOException
  {
    if (in == null)
      throw new IOException ("Stream closed");
  }

  /**
   * @return The number of bytes currently in the "unread" buffer.
   */
  @Nonnegative
  public int getUnreadCount ()
  {
    return m_aBuf.length - m_nBufPos;
  }

  /**
   * @return <code>true</code> if at least one "unread" byte is present.
   */
  public boolean hasUnreadBytes ()
  {
    return m_nBufPos < m_aBuf.length;
  }

  /**
   * Reads the next byte of data from this input stream. The value byte is
   * returned as an <code>int</code> in the range <code>0</code> to
   * <code>255</code>. If no byte is available because the end of the stream has
   * been reached, the value <code>-1</code> is returned. This method blocks
   * until input data is available, the end of the stream is detected, or an
   * exception is thrown.
   * <p>
   * This method returns the most recently pushed-back byte, if there is one,
   * and otherwise calls the <code>read</code> method of its underlying input
   * stream and returns whatever value that method returns.
   *
   * @return the next byte of data, or <code>-1</code> if the end of the stream
   *         has been reached.
   * @exception IOException
   *            if this input stream has been closed by invoking its
   *            {@link #close()} method, or an I/O error occurs.
   * @see java.io.InputStream#read()
   */
  @Override
  public int read () throws IOException
  {
    _ensureOpen ();
    if (m_nBufPos < m_aBuf.length)
      return m_aBuf[m_nBufPos++] & 0xff;

    return super.read ();
  }

  /**
   * Reads up to <code>len</code> bytes of data from this input stream into an
   * array of bytes. This method first reads any pushed-back bytes; after that,
   * if fewer than <code>len</code> bytes have been read then it reads from the
   * underlying input stream. If <code>len</code> is not zero, the method blocks
   * until at least 1 byte of input is available; otherwise, no bytes are read
   * and <code>0</code> is returned.
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
   * @exception NullPointerException
   *            If <code>b</code> is <code>null</code>.
   * @exception IndexOutOfBoundsException
   *            If <code>off</code> is negative, <code>len</code> is negative,
   *            or <code>len</code> is greater than <code>b.length - off</code>
   * @exception IOException
   *            if this input stream has been closed by invoking its
   *            {@link #close()} method, or an I/O error occurs.
   * @see java.io.InputStream#read(byte[], int, int)
   */
  @Override
  public int read (@Nonnull final byte [] aBuf,
                   @Nonnegative final int nOfs,
                   @Nonnegative final int nLen) throws IOException
  {
    ValueEnforcer.isArrayOfsLen (aBuf, nOfs, nLen);
    _ensureOpen ();

    if (nLen == 0)
      return 0;

    int nRealOfs = nOfs;
    int nRealLen = nLen;
    int nBufAvail = m_aBuf.length - m_nBufPos;
    if (nBufAvail > 0)
    {
      if (nRealLen < nBufAvail)
        nBufAvail = nRealLen;
      System.arraycopy (m_aBuf, m_nBufPos, aBuf, nRealOfs, nBufAvail);
      m_nBufPos += nBufAvail;
      nRealOfs += nBufAvail;
      nRealLen -= nBufAvail;
    }

    if (nRealLen > 0)
    {
      nRealLen = super.read (aBuf, nRealOfs, nRealLen);
      if (nRealLen == -1)
        return nBufAvail == 0 ? -1 : nBufAvail;
      return nBufAvail + nRealLen;
    }
    return nBufAvail;
  }

  /**
   * Pushes back a byte by copying it to the front of the pushback buffer. After
   * this method returns, the next byte to be read will have the value
   * <code>(byte)b</code>.
   *
   * @param b
   *        the <code>int</code> value whose low-order byte is to be pushed
   *        back.
   * @exception IOException
   *            If there is not enough room in the pushback buffer for the byte,
   *            or this input stream has been closed by invoking its
   *            {@link #close()} method.
   */
  public void unread (final int b) throws IOException
  {
    _ensureOpen ();
    if (m_nBufPos == 0)
      throw new IOException ("Push back buffer is full");
    m_aBuf[--m_nBufPos] = (byte) b;
  }

  /**
   * Pushes back a portion of an array of bytes by copying it to the front of
   * the pushback buffer. After this method returns, the next byte to be read
   * will have the value <code>b[off]</code>, the byte after that will have the
   * value <code>b[off+1]</code>, and so forth.
   *
   * @param aBuf
   *        the byte array to push back.
   * @param nOfs
   *        the start offset of the data.
   * @param nLen
   *        the number of bytes to push back.
   * @exception IOException
   *            If there is not enough room in the pushback buffer for the
   *            specified number of bytes, or this input stream has been closed
   *            by invoking its {@link #close()} method.
   * @since JDK1.1
   */
  public void unread (@Nonnull final byte [] aBuf,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen) throws IOException
  {
    ValueEnforcer.isArrayOfsLen (aBuf, nOfs, nLen);
    _ensureOpen ();

    if (nLen > m_nBufPos)
      throw new IOException ("Push back buffer is full");
    m_nBufPos -= nLen;
    System.arraycopy (aBuf, nOfs, m_aBuf, m_nBufPos, nLen);
  }

  /**
   * Pushes back an array of bytes by copying it to the front of the pushback
   * buffer. After this method returns, the next byte to be read will have the
   * value <code>b[0]</code>, the byte after that will have the value
   * <code>b[1]</code>, and so forth.
   *
   * @param aBuf
   *        the byte array to push back
   * @exception IOException
   *            If there is not enough room in the pushback buffer for the
   *            specified number of bytes, or this input stream has been closed
   *            by invoking its {@link #close()} method.
   * @since JDK1.1
   */
  public void unread (@Nonnull final byte [] aBuf) throws IOException
  {
    unread (aBuf, 0, aBuf.length);
  }

  /**
   * Returns an estimate of the number of bytes that can be read (or skipped
   * over) from this input stream without blocking by the next invocation of a
   * method for this input stream. The next invocation might be the same thread
   * or another thread. A single read or skip of this many bytes will not block,
   * but may read or skip fewer bytes.
   * <p>
   * The method returns the sum of the number of bytes that have been pushed
   * back and the value returned by {@link java.io.FilterInputStream#available
   * available}.
   *
   * @return the number of bytes that can be read (or skipped over) from the
   *         input stream without blocking.
   * @exception IOException
   *            if this input stream has been closed by invoking its
   *            {@link #close()} method, or an I/O error occurs.
   * @see java.io.InputStream#available()
   */
  @Override
  public int available () throws IOException
  {
    _ensureOpen ();
    return (m_aBuf.length - m_nBufPos) + super.available ();
  }

  /**
   * Skips over and discards <code>n</code> bytes of data from this input
   * stream. The <code>skip</code> method may, for a variety of reasons, end up
   * skipping over some smaller number of bytes, possibly zero. If
   * <code>n</code> is negative, no bytes are skipped.
   * <p>
   * The <code>skip</code> method of <code>PushbackInputStream</code> first
   * skips over the bytes in the pushback buffer, if any. It then calls the
   * <code>skip</code> method of the underlying input stream if more bytes need
   * to be skipped. The actual number of bytes skipped is returned.
   *
   * @param nSkip
   *        The number of bytes to skip. Must be &ge; 0.
   * @return The number of bytes actually skipped
   * @exception IOException
   *            if the stream does not support seek, or the stream has been
   *            closed by invoking its {@link #close()} method, or an I/O error
   *            occurs.
   * @see java.io.InputStream#skip(long n)
   * @since 1.2
   */
  @Override
  public long skip (final long nSkip) throws IOException
  {
    ValueEnforcer.isGE0 (nSkip, "SkipValue");

    _ensureOpen ();

    if (nSkip == 0)
      return 0L;

    long nRealSkip = nSkip;
    final int nBufAvail = m_aBuf.length - m_nBufPos;
    if (nBufAvail > 0)
    {
      if (nRealSkip <= nBufAvail)
      {
        m_nBufPos += nRealSkip;
        return nRealSkip;
      }
      m_nBufPos = m_aBuf.length;
      nRealSkip -= nBufAvail;
    }
    return nBufAvail + super.skip (nRealSkip);
  }

  /**
   * Tests if this input stream supports the <code>mark</code> and
   * <code>reset</code> methods, which it does not.
   *
   * @return <code>false</code>, since this class does not support the
   *         <code>mark</code> and <code>reset</code> methods.
   * @see java.io.InputStream#mark(int)
   * @see java.io.InputStream#reset()
   */
  @Override
  public boolean markSupported ()
  {
    return false;
  }

  /**
   * Marks the current position in this input stream.
   * <p>
   * The <code>mark</code> method of <code>PushbackInputStream</code> does
   * nothing.
   *
   * @param readlimit
   *        the maximum limit of bytes that can be read before the mark position
   *        becomes invalid.
   * @see java.io.InputStream#reset()
   */
  @Override
  @SuppressWarnings ("sync-override")
  public void mark (final int readlimit)
  {}

  /**
   * Repositions this stream to the position at the time the <code>mark</code>
   * method was last called on this input stream.
   * <p>
   * The method <code>reset</code> for class <code>PushbackInputStream</code>
   * does nothing except throw an <code>IOException</code>.
   *
   * @exception IOException
   *            if this method is invoked.
   * @see java.io.InputStream#mark(int)
   * @see java.io.IOException
   */
  @SuppressWarnings ("sync-override")
  @Override
  public void reset () throws IOException
  {
    throw new IOException ("mark/reset not supported");
  }

  /**
   * Closes this input stream and releases any system resources associated with
   * the stream. Once the stream has been closed, further read(), unread(),
   * available(), reset(), or skip() invocations will throw an IOException.
   * Closing a previously closed stream has no effect.
   *
   * @exception IOException
   *            if an I/O error occurs.
   */
  @Override
  public void close () throws IOException
  {
    if (in != null)
    {
      in.close ();
      in = null;
      m_aBuf = null;
    }
  }
}
