/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import java.io.Writer;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.lang.IHasSize;

/**
 * A non-synchronized copy of the class {@link java.io.StringWriter}.<br>
 * It uses {@link StringBuilder} instead of {@link StringBuffer} and therefore does not need
 * synchronized access!
 *
 * @author Philip Helger
 * @see java.io.StringWriter
 */
@NotThreadSafe
public class NonBlockingStringWriter extends Writer implements IHasSize
{
  private final StringBuilder m_aSB;

  /**
   * Create a new string writer using the default initial string-buffer size.
   */
  public NonBlockingStringWriter ()
  {
    m_aSB = new StringBuilder ();
    lock = m_aSB;
  }

  /**
   * Create a new string writer using the specified initial string-buffer size.
   *
   * @param nInitialSize
   *        The number of <code>char</code> values that will fit into this buffer before it is
   *        automatically expanded
   * @throws IllegalArgumentException
   *         If <code>initialSize</code> is negative
   */
  public NonBlockingStringWriter (@Nonnegative final int nInitialSize)
  {
    ValueEnforcer.isGE0 (nInitialSize, "InitialSize");
    m_aSB = new StringBuilder (nInitialSize);
    lock = m_aSB;
  }

  /**
   * Write a single character.
   */
  @Override
  public void write (final int c)
  {
    m_aSB.append ((char) c);
  }

  /**
   * Write a portion of an array of characters.
   *
   * @param aBuf
   *        Array of characters
   * @param nOfs
   *        Offset from which to start writing characters
   * @param nLen
   *        Number of characters to write
   */
  @Override
  public void write (@Nonnull final char [] aBuf, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (aBuf, nOfs, nLen);
    if (nLen > 0)
      m_aSB.append (aBuf, nOfs, nLen);
  }

  /**
   * Write a string.
   */
  @Override
  public void write (@Nullable final String sStr)
  {
    m_aSB.append (sStr);
  }

  /**
   * Write a portion of a string.
   *
   * @param sStr
   *        String to be written
   * @param nOfs
   *        Offset from which to start writing characters
   * @param nLen
   *        Number of characters to write
   */
  @Override
  public void write (@Nonnull final String sStr, final int nOfs, final int nLen)
  {
    m_aSB.append (sStr.substring (nOfs, nOfs + nLen));
  }

  /**
   * Appends the specified character sequence to this writer.
   * <p>
   * An invocation of this method of the form <code>out.append(csq)</code> behaves in exactly the
   * same way as the invocation
   *
   * <pre>
   * out.write (csq.toString ())
   * </pre>
   * <p>
   * Depending on the specification of <code>toString</code> for the character sequence
   * <code>csq</code>, the entire sequence may not be appended. For instance, invoking the
   * <code>toString</code> method of a character buffer will return a subsequence whose content
   * depends upon the buffer's position and limit.
   *
   * @param aCS
   *        The character sequence to append. If <code>csq</code> is <code>null</code>, then the
   *        four characters <code>"null"</code> are appended to this writer.
   * @return This writer
   */
  @Override
  public NonBlockingStringWriter append (final CharSequence aCS)
  {
    if (aCS == null)
      write ("null");
    else
      write (aCS.toString ());
    return this;
  }

  /**
   * Appends a subsequence of the specified character sequence to this writer.
   * <p>
   * An invocation of this method of the form <code>out.append(csq, start,
   * end)</code> when <code>csq</code> is not <code>null</code>, behaves in exactly the same way as
   * the invocation
   *
   * <pre>
   * out.write (csq.subSequence (start, end).toString ())
   * </pre>
   *
   * @param aCS
   *        The character sequence from which a subsequence will be appended. If <code>csq</code> is
   *        <code>null</code>, then characters will be appended as if <code>csq</code> contained the
   *        four characters <code>"null"</code>.
   * @param nStart
   *        The index of the first character in the subsequence
   * @param nEnd
   *        The index of the character following the last character in the subsequence
   * @return This writer
   * @throws IndexOutOfBoundsException
   *         If <code>start</code> or <code>end</code> are negative, <code>start</code> is greater
   *         than <code>end</code>, or <code>end</code> is greater than <code>csq.length()</code>
   */
  @Override
  public NonBlockingStringWriter append (final CharSequence aCS, final int nStart, final int nEnd)
  {
    final CharSequence cs = (aCS == null ? "null" : aCS);
    write (cs.subSequence (nStart, nEnd).toString ());
    return this;
  }

  /**
   * Appends the specified character to this writer.
   * <p>
   * An invocation of this method of the form <code>out.append(c)</code> behaves in exactly the same
   * way as the invocation
   *
   * <pre>
   * out.write (c)
   * </pre>
   *
   * @param c
   *        The 16-bit character to append
   * @return This writer
   */
  @Override
  public NonBlockingStringWriter append (final char c)
  {
    write (c);
    return this;
  }

  /**
   * Flush the stream.
   */
  @Override
  public void flush ()
  {}

  /**
   * Closing a <code>StringWriter</code> has no effect. The methods in this class can be called
   * after the stream has been closed without generating an <code>IOException</code>.
   */
  @Override
  public void close ()
  {}

  /**
   * @return The contained StringBuilder. Never <code>null</code>. Handle with care!
   */
  @Nonnull
  @ReturnsMutableObject ("design")
  public StringBuilder directGetStringBuilder ()
  {
    return m_aSB;
  }

  /**
   * @return Return the buffer's current value as a string.
   */
  @Nonnull
  @ReturnsMutableCopy
  public char [] getAsCharArray ()
  {
    final int nChars = m_aSB.length ();
    final char [] ret = new char [nChars];
    m_aSB.getChars (0, nChars, ret, 0);
    return ret;
  }

  /**
   * @return the buffer's current value as a string.
   */
  @Nonnull
  public String getAsString ()
  {
    return m_aSB.toString ();
  }

  /**
   * Remove all content of the buffer.
   */
  public void reset ()
  {
    m_aSB.setLength (0);
  }

  @Nonnegative
  public int size ()
  {
    return m_aSB.length ();
  }

  public boolean isEmpty ()
  {
    return m_aSB.length () == 0;
  }

  /**
   * @return the buffer's current value as a string.
   * @deprecated Don't call this; use getAsString instead
   */
  @Override
  @Deprecated (forRemoval = false)
  public String toString ()
  {
    return getAsString ();
  }
}
