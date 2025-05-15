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

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.string.StringHelper;

/**
 * A non-synchronized copy of the class {@link java.io.CharArrayWriter}.<br>
 * It does not use the internal lock object.
 *
 * @author Philip Helger
 * @see java.io.CharArrayWriter
 * @since 8.6.4
 */
public class NonBlockingCharArrayWriter extends Writer
{
  /**
   * The buffer where data is stored.
   */
  protected char [] m_aBuf;

  /**
   * The number of chars in the buffer.
   */
  protected int m_nCount;

  /**
   * Creates a new NonBlockingCharArrayWriter.
   */
  public NonBlockingCharArrayWriter ()
  {
    this (32);
  }

  /**
   * Creates a new NonBlockingCharArrayWriter with the specified initial size.
   *
   * @param nInitialSize
   *        an int specifying the initial buffer size.
   * @exception IllegalArgumentException
   *            if initialSize is negative
   */
  public NonBlockingCharArrayWriter (@Nonnegative final int nInitialSize)
  {
    ValueEnforcer.isGE0 (nInitialSize, "InitialSize");
    m_aBuf = new char [nInitialSize];
  }

  /**
   * Writes a character to the buffer.
   */
  @Override
  public void write (final int c)
  {
    final int nNewCount = m_nCount + 1;
    if (nNewCount > m_aBuf.length)
      m_aBuf = Arrays.copyOf (m_aBuf, Math.max (m_aBuf.length << 1, nNewCount));
    m_aBuf[m_nCount] = (char) c;
    m_nCount = nNewCount;
  }

  /**
   * Writes an array of characters.
   *
   * @param aBuf
   *        Array of characters to be written. May not be <code>null</code>.
   * @since 9.4.3
   */
  @Override
  public void write (@Nonnull final char [] aBuf)
  {
    write (aBuf, 0, aBuf.length);
  }

  /**
   * Writes characters to the buffer.
   *
   * @param aBuf
   *        the data to be written
   * @param nOfs
   *        the start offset in the data
   * @param nLen
   *        the number of chars that are written
   */
  @Override
  public void write (@Nonnull final char [] aBuf, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (aBuf, nOfs, nLen);

    if (nLen > 0)
    {
      final int nNewCount = m_nCount + nLen;
      if (nNewCount > m_aBuf.length)
        m_aBuf = Arrays.copyOf (m_aBuf, Math.max (m_aBuf.length << 1, nNewCount));
      System.arraycopy (aBuf, nOfs, m_aBuf, m_nCount, nLen);
      m_nCount = nNewCount;
    }
  }

  /**
   * Writes a string.
   *
   * @param sStr
   *        String to be written. May not be <code>null</code>.
   * @since 9.4.3
   */
  @Override
  public void write (@Nonnull final String sStr)
  {
    write (sStr, 0, sStr.length ());
  }

  /**
   * Write a portion of a string to the buffer.
   *
   * @param sStr
   *        String to be written from
   * @param nOfs
   *        Offset from which to start reading characters
   * @param nLen
   *        Number of characters to be written
   */
  @Override
  public void write (@Nonnull final String sStr, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    if (nLen > 0)
    {
      final int newcount = m_nCount + nLen;
      if (newcount > m_aBuf.length)
      {
        m_aBuf = Arrays.copyOf (m_aBuf, Math.max (m_aBuf.length << 1, newcount));
      }
      sStr.getChars (nOfs, nOfs + nLen, m_aBuf, m_nCount);
      m_nCount = newcount;
    }
  }

  /**
   * Writes the contents of the buffer to another character stream.
   *
   * @param out
   *        the output stream to write to
   * @throws IOException
   *         If an I/O error occurs.
   */
  public void writeTo (@Nonnull final Writer out) throws IOException
  {
    out.write (m_aBuf, 0, m_nCount);
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
   * @param csq
   *        The character sequence to append. If <code>csq</code> is <code>null</code>, then the
   *        four characters <code>"null"</code> are appended to this writer.
   * @return This writer
   */
  @Override
  public NonBlockingCharArrayWriter append (@Nullable final CharSequence csq)
  {
    final String s = csq == null ? "null" : csq.toString ();
    write (s, 0, s.length ());
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
   * @param csq
   *        The character sequence from which a subsequence will be appended. If <code>csq</code> is
   *        <code>null</code>, then characters will be appended as if <code>csq</code> contained the
   *        four characters <code>"null"</code>.
   * @param start
   *        The index of the first character in the subsequence
   * @param end
   *        The index of the character following the last character in the subsequence
   * @return This writer
   * @throws IndexOutOfBoundsException
   *         If <code>start</code> or <code>end</code> are negative, <code>start</code> is greater
   *         than <code>end</code>, or <code>end</code> is greater than <code>csq.length()</code>
   */
  @Override
  public NonBlockingCharArrayWriter append (@Nullable final CharSequence csq, final int start, final int end)
  {
    final String s = (csq == null ? "null" : csq).subSequence (start, end).toString ();
    write (s, 0, s.length ());
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
  public NonBlockingCharArrayWriter append (final char c)
  {
    write (c);
    return this;
  }

  /**
   * Resets the buffer so that you can use it again without throwing away the already allocated
   * buffer.
   */
  public void reset ()
  {
    m_nCount = 0;
  }

  /**
   * Returns a copy of the input data.
   *
   * @return an array of chars copied from the input data.
   */
  @Nonnull
  @ReturnsMutableCopy
  public char [] toCharArray ()
  {
    return Arrays.copyOf (m_aBuf, m_nCount);
  }

  /**
   * @return The internally used char array. Never <code>null</code>. Handle with care!
   */
  @Nonnull
  @ReturnsMutableObject
  public char [] directGetBuffer ()
  {
    return m_aBuf;
  }

  /**
   * Returns a copy of the input data as bytes in the correct charset.
   *
   * @param aCharset
   *        The charset to be used. May not be <code>null</code>.
   * @return an array of bytes. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public byte [] toByteArray (@Nonnull final Charset aCharset)
  {
    return StringHelper.encodeCharToBytes (m_aBuf, 0, m_nCount, aCharset);
  }

  /**
   * Returns the current size of the buffer.
   *
   * @return an int representing the current size of the buffer.
   */
  @Nonnegative
  public int getSize ()
  {
    return m_nCount;
  }

  /**
   * @return The number of pre-allocated chars. Always &ge; 0.
   */
  @Nonnegative
  public int getBufferSize ()
  {
    return m_aBuf.length;
  }

  public boolean isEmpty ()
  {
    return m_nCount == 0;
  }

  public boolean isNotEmpty ()
  {
    return m_nCount > 0;
  }

  public boolean startsWith (@Nonnull final char [] aChars)
  {
    return startsWith (aChars, 0, aChars.length);
  }

  public boolean startsWith (@Nonnull final char [] aChars, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    if (m_nCount < nLen || nLen < 0)
      return false;
    for (int i = 0; i < nLen; ++i)
      if (m_aBuf[i] != aChars[nOfs + i])
        return false;
    return true;
  }

  /**
   * Converts input data to a string.
   *
   * @return the string.
   */
  @Nonnull
  @ReturnsMutableCopy
  public String getAsString ()
  {
    return new String (m_aBuf, 0, m_nCount);
  }

  /**
   * Converts input data to a string.
   *
   * @param nLength
   *        The number of characters to convert. Must be &le; than {@link #getSize()}.
   * @return the string.
   */
  @Nonnull
  public String getAsString (@Nonnegative final int nLength)
  {
    ValueEnforcer.isBetweenInclusive (nLength, "Length", 0, m_nCount);
    return new String (m_aBuf, 0, nLength);
  }

  /**
   * Converts input data to a string.
   *
   * @param nOfs
   *        The offset to start at. Must be &ge; 0.
   * @param nLength
   *        The number of characters to convert. Must be &le; than {@link #getSize()}.
   * @return the string.
   */
  @Nonnull
  public String getAsString (@Nonnegative final int nOfs, @Nonnegative final int nLength)
  {
    ValueEnforcer.isGE0 (nOfs, "Index");
    ValueEnforcer.isBetweenInclusive (nLength, "Length", 0, m_nCount);
    return new String (m_aBuf, nOfs, nLength);
  }

  /**
   * Flush the stream.
   */
  @Override
  public void flush ()
  {}

  /**
   * Close the stream. This method does not release the buffer, since its contents might still be
   * required. Note: Invoking this method in this class will have no effect.
   */
  @Override
  public void close ()
  {}
}
