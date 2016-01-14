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

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;

/**
 * A non-synchronized copy of the class {@link java.io.PushbackReader}.
 *
 * @author Philip Helger
 * @see java.io.PushbackReader
 */
public class NonBlockingPushbackReader extends FilterReader
{
  /** Pushback buffer */
  private char [] m_aBuf;

  /** Current position in buffer */
  private int m_nBufPos;

  /**
   * Creates a new pushback reader with a pushback buffer of the given size.
   *
   * @param aReader
   *        The reader from which characters will be read
   * @param nSize
   *        The size of the pushback buffer
   * @exception IllegalArgumentException
   *            if size is &le; 0
   */
  public NonBlockingPushbackReader (@Nonnull final Reader aReader, @Nonnegative final int nSize)
  {
    super (aReader);
    ValueEnforcer.isGT0 (nSize, "Size");
    m_aBuf = new char [nSize];
    m_nBufPos = nSize;
  }

  /**
   * Creates a new pushback reader with a one-character pushback buffer.
   *
   * @param aReader
   *        The reader from which characters will be read
   */
  public NonBlockingPushbackReader (@Nonnull final Reader aReader)
  {
    this (aReader, 1);
  }

  /**
   * Checks to make sure that the stream has not been closed.
   */
  private void _ensureOpen () throws IOException
  {
    if (m_aBuf == null)
      throw new IOException ("Reader closed");
  }

  /**
   * @return The number of chars currently in the "unread" buffer.
   */
  @Nonnegative
  public int getUnreadCount ()
  {
    return m_aBuf.length - m_nBufPos;
  }

  /**
   * @return <code>true</code> if at least one "unread" char is present.
   */
  public boolean hasUnreadChars ()
  {
    return m_nBufPos < m_aBuf.length;
  }

  /**
   * Reads a single character.
   *
   * @return The character read, or -1 if the end of the stream has been reached
   * @exception IOException
   *            If an I/O error occurs
   */
  @Override
  public int read () throws IOException
  {
    _ensureOpen ();
    if (m_nBufPos < m_aBuf.length)
      return m_aBuf[m_nBufPos++];

    return super.read ();
  }

  /**
   * Reads characters into a portion of an array.
   *
   * @param aBuf
   *        Destination buffer
   * @param nOfs
   *        Offset at which to start writing characters
   * @param nLen
   *        Maximum number of characters to read
   * @return The number of characters read, or -1 if the end of the stream has
   *         been reached
   * @exception IOException
   *            If an I/O error occurs
   */
  @Override
  public int read (@Nonnull final char [] aBuf,
                   @Nonnegative final int nOfs,
                   @Nonnegative final int nLen) throws IOException
  {
    ValueEnforcer.isArrayOfsLen (aBuf, nOfs, nLen);
    _ensureOpen ();

    if (nLen == 0)
      return 0;

    try
    {
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
          return (nBufAvail == 0) ? -1 : nBufAvail;
        return nBufAvail + nRealLen;
      }
      return nBufAvail;
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      throw new IndexOutOfBoundsException ();
    }
  }

  /**
   * Pushes back a single character by copying it to the front of the pushback
   * buffer. After this method returns, the next character to be read will have
   * the value <code>(char)c</code>.
   *
   * @param c
   *        The int value representing a character to be pushed back
   * @exception IOException
   *            If the pushback buffer is full, or if some other I/O error
   *            occurs
   */
  public void unread (final int c) throws IOException
  {
    _ensureOpen ();
    if (m_nBufPos == 0)
      throw new IOException ("Pushback buffer overflow");
    m_aBuf[--m_nBufPos] = (char) c;
  }

  /**
   * Pushes back a portion of an array of characters by copying it to the front
   * of the pushback buffer. After this method returns, the next character to be
   * read will have the value <code>cbuf[off]</code>, the character after that
   * will have the value <code>cbuf[off+1]</code>, and so forth.
   *
   * @param aBuf
   *        Character array
   * @param nOfs
   *        Offset of first character to push back
   * @param nLen
   *        Number of characters to push back
   * @exception IOException
   *            If there is insufficient room in the pushback buffer, or if some
   *            other I/O error occurs
   */
  public void unread (@Nonnull final char [] aBuf,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen) throws IOException
  {
    ValueEnforcer.isArrayOfsLen (aBuf, nOfs, nLen);
    _ensureOpen ();
    if (nLen > m_nBufPos)
      throw new IOException ("Pushback buffer overflow");
    m_nBufPos -= nLen;
    System.arraycopy (aBuf, nOfs, m_aBuf, m_nBufPos, nLen);
  }

  /**
   * Pushes back an array of characters by copying it to the front of the
   * pushback buffer. After this method returns, the next character to be read
   * will have the value <code>cbuf[0]</code>, the character after that will
   * have the value <code>cbuf[1]</code>, and so forth.
   *
   * @param aBuf
   *        Character array to push back
   * @exception IOException
   *            If there is insufficient room in the pushback buffer, or if some
   *            other I/O error occurs
   */
  public void unread (@Nonnull final char [] aBuf) throws IOException
  {
    unread (aBuf, 0, aBuf.length);
  }

  /**
   * Tells whether this stream is ready to be read.
   *
   * @exception IOException
   *            If an I/O error occurs
   */
  @Override
  public boolean ready () throws IOException
  {
    _ensureOpen ();
    return (m_nBufPos < m_aBuf.length) || super.ready ();
  }

  /**
   * Marks the present position in the stream. The <code>mark</code> for class
   * <code>PushbackReader</code> always throws an exception.
   *
   * @exception IOException
   *            Always, since mark is not supported
   */
  @Override
  public void mark (final int readAheadLimit) throws IOException
  {
    throw new IOException ("mark/reset not supported");
  }

  /**
   * Resets the stream. The <code>reset</code> method of
   * <code>PushbackReader</code> always throws an exception.
   *
   * @exception IOException
   *            Always, since reset is not supported
   */
  @Override
  public void reset () throws IOException
  {
    throw new IOException ("mark/reset not supported");
  }

  /**
   * Tells whether this stream supports the mark() operation, which it does not.
   */
  @Override
  public boolean markSupported ()
  {
    return false;
  }

  /**
   * Closes the stream and releases any system resources associated with it.
   * Once the stream has been closed, further read(), unread(), ready(), or
   * skip() invocations will throw an IOException. Closing a previously closed
   * stream has no effect.
   *
   * @exception IOException
   *            If an I/O error occurs
   */
  @Override
  public void close () throws IOException
  {
    super.close ();
    m_aBuf = null;
  }

  /**
   * Skips characters. This method will block until some characters are
   * available, an I/O error occurs, or the end of the stream is reached.
   *
   * @param nSkip
   *        The number of characters to skip. Must be &ge; 0.
   * @return The number of characters actually skipped
   * @exception IllegalArgumentException
   *            If <code>n</code> is negative.
   * @exception IOException
   *            If an I/O error occurs
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
}
