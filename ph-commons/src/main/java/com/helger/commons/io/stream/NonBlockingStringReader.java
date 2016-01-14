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
import java.io.Reader;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;

/**
 * A non-synchronized copy of the class {@link java.io.StringReader}.<br>
 * Note: super class {@link Reader} uses the lock object internally only for
 * <code>long skip(long n)</code> and as this method is overwritten in here, the
 * lock is never used.
 *
 * @author Philip Helger
 * @see java.io.StringReader
 */
@NotThreadSafe
public class NonBlockingStringReader extends Reader
{
  private String m_sStr;
  private final int m_nLength;
  private int m_nNext = 0;
  private int m_nMark = 0;

  public NonBlockingStringReader (@Nonnull final char [] aChars)
  {
    this (new String (aChars));
  }

  public NonBlockingStringReader (@Nonnull final char [] aChars,
                                  @Nonnegative final int nOfs,
                                  @Nonnegative final int nLen)
  {
    this (new String (aChars, nOfs, nLen));
  }

  /**
   * Creates a new string reader.
   *
   * @param sStr
   *        String providing the character stream. May not be <code>null</code>.
   */
  public NonBlockingStringReader (@Nonnull final String sStr)
  {
    m_sStr = ValueEnforcer.notNull (sStr, "String");
    m_nLength = sStr.length ();
  }

  /**
   * Check to make sure that the stream has not been closed
   *
   * @throws IOException
   *         When the string is closed
   */
  private void _ensureOpen () throws IOException
  {
    if (m_sStr == null)
      throw new IOException ("Stream closed");
  }

  /**
   * Reads a single character.
   *
   * @return The character read, or -1 if the end of the stream has been reached
   * @exception IOException
   *            If an I/O error occurs
   */
  @Override
  @CheckForSigned
  public int read () throws IOException
  {
    _ensureOpen ();
    if (m_nNext >= m_nLength)
      return -1;
    return m_sStr.charAt (m_nNext++);
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
  @CheckForSigned
  public int read (@Nonnull final char [] aBuf,
                   @Nonnegative final int nOfs,
                   @Nonnegative final int nLen) throws IOException
  {
    _ensureOpen ();
    ValueEnforcer.isArrayOfsLen (aBuf, nOfs, nLen);

    if (nLen == 0)
      return 0;
    if (m_nNext >= m_nLength)
      return -1;
    final int nChars = Math.min (m_nLength - m_nNext, nLen);
    m_sStr.getChars (m_nNext, m_nNext + nChars, aBuf, nOfs);
    m_nNext += nChars;
    return nChars;
  }

  /**
   * Skips the specified number of characters in the stream. Returns the number
   * of characters that were skipped.
   * <p>
   *
   * @param nCharsToSkip
   *        The parameter may be negative, even though the <code>skip</code>
   *        method of the {@link Reader} superclass throws an exception in this
   *        case. Negative values of the parameter cause the stream to skip
   *        backwards. Negative return values indicate a skip backwards. It is
   *        not possible to skip backwards past the beginning of the string.
   * @return If the entire string has been read or skipped, then this method has
   *         no effect and always returns 0.
   * @exception IOException
   *            If an I/O error occurs
   */
  @Override
  public long skip (final long nCharsToSkip) throws IOException
  {
    _ensureOpen ();
    if (m_nNext >= m_nLength)
      return 0;
    // Bound skip by beginning and end of the source
    long n = Math.min (m_nLength - m_nNext, nCharsToSkip);
    n = Math.max (-m_nNext, n);
    m_nNext += n;
    return n;
  }

  /**
   * Tells whether this stream is ready to be read.
   *
   * @return <code>true</code> if the next read() is guaranteed not to block for
   *         input
   * @exception IOException
   *            If the stream is closed
   */
  @Override
  public boolean ready () throws IOException
  {
    _ensureOpen ();
    return true;
  }

  /**
   * Tells whether this stream supports the mark() operation, which it does.
   *
   * @return always <code>true</code>
   */
  @Override
  public boolean markSupported ()
  {
    return true;
  }

  /**
   * Marks the present position in the stream. Subsequent calls to reset() will
   * reposition the stream to this point.
   *
   * @param nReadAheadLimit
   *        Limit on the number of characters that may be read while still
   *        preserving the mark. Because the stream's input comes from a string,
   *        there is no actual limit, so this argument must not be negative, but
   *        is otherwise ignored.
   * @exception IllegalArgumentException
   *            If readAheadLimit is &lt; 0
   * @exception IOException
   *            If an I/O error occurs
   */
  @Override
  public void mark (final int nReadAheadLimit) throws IOException
  {
    ValueEnforcer.isGE0 (nReadAheadLimit, "ReadAheadLimit");

    _ensureOpen ();
    m_nMark = m_nNext;
  }

  /**
   * Resets the stream to the most recent mark, or to the beginning of the
   * string if it has never been marked.
   *
   * @exception IOException
   *            If an I/O error occurs
   */
  @Override
  public void reset () throws IOException
  {
    _ensureOpen ();
    m_nNext = m_nMark;
  }

  /**
   * Closes the stream and releases any system resources associated with it.
   * Once the stream has been closed, further read(), ready(), mark(), or
   * reset() invocations will throw an IOException. Closing a previously closed
   * stream has no effect.
   */
  @Override
  public void close ()
  {
    m_sStr = null;
  }
}
