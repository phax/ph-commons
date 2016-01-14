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
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Non-synchronized version of {@link java.io.BufferedInputStream}.
 *
 * @author Philip Helger
 */
public class NonBlockingBufferedInputStream extends FilterInputStream
{
  private static final int DEFAULT_BUFFER_SIZE = 8192;

  /**
   * The internal buffer array where the data is stored. When necessary, it may
   * be replaced by another array of a different size.
   */
  @SuppressFBWarnings ("VO_VOLATILE_REFERENCE_TO_ARRAY")
  protected volatile byte [] m_aBuf;

  /**
   * Atomic updater to provide compareAndSet for buf. This is necessary because
   * closes can be asynchronous. We use nullness of buf[] as primary indicator
   * that this stream is closed. (The "in" field is also nulled out on close.)
   */
  private static final AtomicReferenceFieldUpdater <NonBlockingBufferedInputStream, byte []> s_aBufUpdater = AtomicReferenceFieldUpdater.newUpdater (NonBlockingBufferedInputStream.class,
                                                                                                                                                     byte [].class,
                                                                                                                                                     "m_aBuf");

  /**
   * The index one greater than the index of the last valid byte in the buffer.
   * This value is always in the range <code>0</code> through
   * <code>buf.length</code>; elements <code>buf[0]</code> through
   * <code>buf[count-1]
   * </code>contain buffered input data obtained from the underlying input
   * stream.
   */
  protected int m_nCount;

  /**
   * The current position in the buffer. This is the index of the next character
   * to be read from the <code>buf</code> array.
   * <p>
   * This value is always in the range <code>0</code> through <code>count</code>
   * . If it is less than <code>count</code>, then <code>buf[pos]</code> is the
   * next byte to be supplied as input; if it is equal to <code>count</code>,
   * then the next <code>read</code> or <code>skip</code> operation will require
   * more bytes to be read from the contained input stream.
   *
   * @see #m_aBuf
   */
  protected int m_nPos;

  /**
   * The value of the <code>pos</code> field at the time the last
   * <code>mark</code> method was called.
   * <p>
   * This value is always in the range <code>-1</code> through <code>pos</code>.
   * If there is no marked position in the input stream, this field is
   * <code>-1</code>. If there is a marked position in the input stream, then
   * <code>buf[markpos]</code> is the first byte to be supplied as input after a
   * <code>reset</code> operation. If <code>markpos</code> is not
   * <code>-1</code>, then all bytes from positions <code>buf[markpos]</code>
   * through <code>buf[pos-1]</code> must remain in the buffer array (though
   * they may be moved to another place in the buffer array, with suitable
   * adjustments to the values of <code>count</code>, <code>pos</code>, and
   * <code>markpos</code>); they may not be discarded unless and until the
   * difference between <code>pos</code> and <code>markpos</code> exceeds
   * <code>marklimit</code>.
   *
   * @see #mark(int)
   * @see #m_nPos
   */
  protected int m_nMarkPos = -1;

  /**
   * The maximum read ahead allowed after a call to the <code>mark</code> method
   * before subsequent calls to the <code>reset</code> method fail. Whenever the
   * difference between <code>pos</code> and <code>markpos</code> exceeds
   * <code>marklimit</code>, then the mark may be dropped by setting
   * <code>markpos</code> to <code>-1</code>.
   *
   * @see #mark(int)
   * @see #reset()
   */
  protected int m_nMarkLimit;

  /**
   * Check to make sure that underlying input stream has not been nulled out due
   * to close; if not return it;
   */
  @Nonnull
  private InputStream _getInIfOpen () throws IOException
  {
    final InputStream ret = in;
    if (ret == null)
      throw new IOException ("Stream closed");
    return ret;
  }

  /**
   * Check to make sure that buffer has not been nulled out due to close; if not
   * return it;
   */
  @Nonnull
  private byte [] _getBufIfOpen () throws IOException
  {
    final byte [] ret = m_aBuf;
    if (ret == null)
      throw new IOException ("Stream closed");
    return ret;
  }

  /**
   * Creates a <code>BufferedInputStream</code> and saves its argument, the
   * input stream <code>in</code>, for later use. An internal buffer array is
   * created and stored in <code>buf</code>.
   *
   * @param aIS
   *        the underlying input stream.
   */
  public NonBlockingBufferedInputStream (@Nonnull final InputStream aIS)
  {
    this (aIS, DEFAULT_BUFFER_SIZE);
  }

  /**
   * Creates a <code>BufferedInputStream</code> with the specified buffer size,
   * and saves its argument, the input stream <code>in</code>, for later use. An
   * internal buffer array of length <code>size</code> is created and stored in
   * <code>buf</code>.
   *
   * @param aIS
   *        the underlying input stream.
   * @param nSize
   *        the buffer size.
   * @exception IllegalArgumentException
   *            if size &le; 0.
   */
  public NonBlockingBufferedInputStream (@Nonnull final InputStream aIS, @Nonnegative final int nSize)
  {
    super (aIS);
    ValueEnforcer.isGT0 (nSize, "Size");
    m_aBuf = new byte [nSize];
  }

  /**
   * Fills the buffer with more data, taking into account shuffling and other
   * tricks for dealing with marks. Assumes that it is being called by a method.
   * This method also assumes that all data has already been read in, hence pos
   * > count.
   */
  private void _fill () throws IOException
  {
    byte [] buffer = _getBufIfOpen ();
    if (m_nMarkPos < 0)
      m_nPos = 0; /* no mark: throw away the buffer */
    else
      if (m_nPos >= buffer.length) /* no room left in buffer */
        if (m_nMarkPos > 0)
        {
          /* can throw away early part of the buffer */
          final int sz = m_nPos - m_nMarkPos;
          System.arraycopy (buffer, m_nMarkPos, buffer, 0, sz);
          m_nPos = sz;
          m_nMarkPos = 0;
        }
        else
          if (buffer.length >= m_nMarkLimit)
          {
            /* buffer got too big, invalidate mark */
            m_nMarkPos = -1;
            /* drop buffer contents */
            m_nPos = 0;
          }
          else
          {
            /* grow buffer */
            int nsz = m_nPos * 2;
            if (nsz > m_nMarkLimit)
              nsz = m_nMarkLimit;
            final byte nbuf[] = new byte [nsz];
            System.arraycopy (buffer, 0, nbuf, 0, m_nPos);
            if (!s_aBufUpdater.compareAndSet (this, buffer, nbuf))
            {
              // Can't replace buf if there was an async close.
              // Note: This would need to be changed if fill()
              // is ever made accessible to multiple threads.
              // But for now, the only way CAS can fail is via close.
              // assert buf == null;
              throw new IOException ("Stream closed");
            }
            buffer = nbuf;
          }
    m_nCount = m_nPos;
    // Potentially blocking read
    final int n = _getInIfOpen ().read (buffer, m_nPos, buffer.length - m_nPos);
    if (n > 0)
      m_nCount = n + m_nPos;
  }

  /**
   * See the general contract of the <code>read</code> method of
   * <code>InputStream</code>.
   *
   * @return the next byte of data, or <code>-1</code> if the end of the stream
   *         is reached.
   * @exception IOException
   *            if this input stream has been closed by invoking its
   *            {@link #close()} method, or an I/O error occurs.
   */
  @Override
  public int read () throws IOException
  {
    if (m_nPos >= m_nCount)
    {
      _fill ();
      if (m_nPos >= m_nCount)
        return -1;
    }
    return _getBufIfOpen ()[m_nPos++] & 0xff;
  }

  /**
   * Read characters into a portion of an array, reading from the underlying
   * stream at most once if necessary.
   */
  private int _read1 (@Nonnull final byte [] aBuf,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen) throws IOException
  {
    int nAvail = m_nCount - m_nPos;
    if (nAvail <= 0)
    {
      /*
       * If the requested length is at least as large as the buffer, and if
       * there is no mark/reset activity, do not bother to copy the bytes into
       * the local buffer. In this way buffered streams will cascade harmlessly.
       */
      if (nLen >= _getBufIfOpen ().length && m_nMarkPos < 0)
      {
        // Potentially blocking read
        return _getInIfOpen ().read (aBuf, nOfs, nLen);
      }

      _fill ();
      nAvail = m_nCount - m_nPos;
      if (nAvail <= 0)
        return -1;
    }
    final int nCnt = nAvail < nLen ? nAvail : nLen;
    System.arraycopy (_getBufIfOpen (), m_nPos, aBuf, nOfs, nCnt);
    m_nPos += nCnt;
    return nCnt;
  }

  /**
   * Reads bytes from this byte-input stream into the specified byte array,
   * starting at the given offset.
   * <p>
   * This method implements the general contract of the corresponding
   * <code>{@link InputStream#read(byte[], int, int) read}</code> method of the
   * <code>{@link InputStream}</code> class. As an additional convenience, it
   * attempts to read as many bytes as possible by repeatedly invoking the
   * <code>read</code> method of the underlying stream. This iterated
   * <code>read</code> continues until one of the following conditions becomes
   * true:
   * <ul>
   * <li>The specified number of bytes have been read,
   * <li>The <code>read</code> method of the underlying stream returns
   * <code>-1</code>, indicating end-of-file, or
   * <li>The <code>available</code> method of the underlying stream returns
   * zero, indicating that further input requests would block.
   * </ul>
   * If the first <code>read</code> on the underlying stream returns
   * <code>-1</code> to indicate end-of-file then this method returns
   * <code>-1</code>. Otherwise this method returns the number of bytes actually
   * read.
   * <p>
   * Subclasses of this class are encouraged, but not required, to attempt to
   * read as many bytes as possible in the same fashion.
   *
   * @param aBuf
   *        destination buffer.
   * @param nOfs
   *        offset at which to start storing bytes.
   * @param nLen
   *        maximum number of bytes to read.
   * @return the number of bytes read, or <code>-1</code> if the end of the
   *         stream has been reached.
   * @exception IOException
   *            if this input stream has been closed by invoking its
   *            {@link #close()} method, or an I/O error occurs.
   */
  @Override
  public int read (final byte [] aBuf, final int nOfs, final int nLen) throws IOException
  {
    ValueEnforcer.isArrayOfsLen (aBuf, nOfs, nLen);

    // Check for closed stream
    _getBufIfOpen ();
    if (nLen == 0)
      return 0;

    int nTotal = 0;
    for (;;)
    {
      final int nRead = _read1 (aBuf, nOfs + nTotal, nLen - nTotal);
      if (nRead <= 0)
        return nTotal == 0 ? nRead : nTotal;
      nTotal += nRead;
      if (nTotal >= nLen)
        return nTotal;
      // if not closed but no bytes available, return
      final InputStream aIS = in;
      if (aIS != null && aIS.available () <= 0)
        return nTotal;
    }
  }

  /**
   * See the general contract of the <code>skip</code> method of
   * <code>InputStream</code>.
   *
   * @exception IOException
   *            if the stream does not support seek, or if this input stream has
   *            been closed by invoking its {@link #close()} method, or an I/O
   *            error occurs.
   */
  @Override
  public long skip (final long nBytesToSkip) throws IOException
  {
    _getBufIfOpen (); // Check for closed stream
    if (nBytesToSkip <= 0)
      return 0;

    long nAvail = m_nCount - m_nPos;
    if (nAvail <= 0)
    {
      // If no mark position set then don't keep in buffer
      if (m_nMarkPos < 0)
        return _getInIfOpen ().skip (nBytesToSkip);

      // Fill in buffer to save bytes for reset
      _fill ();
      nAvail = m_nCount - m_nPos;
      if (nAvail <= 0)
        return 0;
    }

    final long nSkipped = nAvail < nBytesToSkip ? nAvail : nBytesToSkip;
    m_nPos += nSkipped;
    return nSkipped;
  }

  /**
   * Returns an estimate of the number of bytes that can be read (or skipped
   * over) from this input stream without blocking by the next invocation of a
   * method for this input stream. The next invocation might be the same thread
   * or another thread. A single read or skip of this many bytes will not block,
   * but may read or skip fewer bytes.
   * <p>
   * This method returns the sum of the number of bytes remaining to be read in
   * the buffer (<code>count&nbsp;- pos</code>) and the result of calling the
   * in.available().
   *
   * @return an estimate of the number of bytes that can be read (or skipped
   *         over) from this input stream without blocking.
   * @exception IOException
   *            if this input stream has been closed by invoking its
   *            {@link #close()} method, or an I/O error occurs.
   */
  @Override
  public int available () throws IOException
  {
    return _getInIfOpen ().available () + (m_nCount - m_nPos);
  }

  /**
   * See the general contract of the <code>mark</code> method of
   * <code>InputStream</code>.
   *
   * @param nReadlimit
   *        the maximum limit of bytes that can be read before the mark position
   *        becomes invalid.
   * @see #reset()
   */
  @SuppressWarnings ("sync-override")
  @Override
  public void mark (final int nReadlimit)
  {
    m_nMarkLimit = nReadlimit;
    m_nMarkPos = m_nPos;
  }

  /**
   * See the general contract of the <code>reset</code> method of
   * <code>InputStream</code>.
   * <p>
   * If <code>markpos</code> is <code>-1</code> (no mark has been set or the
   * mark has been invalidated), an <code>IOException</code> is thrown.
   * Otherwise, <code>pos</code> is set equal to <code>markpos</code>.
   *
   * @exception IOException
   *            if this stream has not been marked or, if the mark has been
   *            invalidated, or the stream has been closed by invoking its
   *            {@link #close()} method, or an I/O error occurs.
   * @see #mark(int)
   */
  @SuppressWarnings ("sync-override")
  @Override
  public void reset () throws IOException
  {
    _getBufIfOpen (); // Cause exception if closed
    if (m_nMarkPos < 0)
      throw new IOException ("Resetting to invalid mark");
    m_nPos = m_nMarkPos;
  }

  /**
   * Tests if this input stream supports the <code>mark</code> and
   * <code>reset</code> methods. The <code>markSupported</code> method of
   * <code>BufferedInputStream</code> returns <code>true</code>.
   *
   * @return a <code>boolean</code> indicating if this stream type supports the
   *         <code>mark</code> and <code>reset</code> methods.
   * @see java.io.InputStream#mark(int)
   * @see java.io.InputStream#reset()
   */
  @Override
  public boolean markSupported ()
  {
    return true;
  }

  /**
   * Closes this input stream and releases any system resources associated with
   * the stream. Once the stream has been closed, further read(), available(),
   * reset(), or skip() invocations will throw an IOException. Closing a
   * previously closed stream has no effect.
   *
   * @exception IOException
   *            if an I/O error occurs.
   */
  @Override
  public void close () throws IOException
  {
    byte [] aBuffer;
    while ((aBuffer = m_aBuf) != null)
    {
      if (s_aBufUpdater.compareAndSet (this, aBuffer, null))
      {
        final InputStream aIS = in;
        in = null;
        if (aIS != null)
          aIS.close ();
        return;
      }
      // Else retry in case a new buf was CASed in fill()
    }
  }
}
