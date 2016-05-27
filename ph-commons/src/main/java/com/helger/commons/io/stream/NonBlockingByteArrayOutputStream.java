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
import java.io.Serializable;
import java.nio.charset.Charset;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.WillClose;
import javax.annotation.WillNotClose;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.lang.IHasSize;
import com.helger.commons.string.ToStringGenerator;

/**
 * A non-synchronized copy of the class {@link java.io.ByteArrayOutputStream}.
 *
 * @author Philip Helger
 * @see java.io.ByteArrayOutputStream
 */
public class NonBlockingByteArrayOutputStream extends OutputStream implements IHasSize, Serializable
{
  /**
   * The buffer where data is stored.
   */
  protected byte [] m_aBuf;

  /**
   * The number of valid bytes in the buffer.
   */
  protected int m_nCount;

  /**
   * Creates a new byte array output stream. The buffer capacity is initially 32
   * bytes, though its size increases if necessary.
   */
  public NonBlockingByteArrayOutputStream ()
  {
    this (32);
  }

  /**
   * Creates a new byte array output stream, with a buffer capacity of the
   * specified size, in bytes.
   *
   * @param nSize
   *        the initial size.
   * @exception IllegalArgumentException
   *            if size is negative.
   */
  public NonBlockingByteArrayOutputStream (@Nonnegative final int nSize)
  {
    ValueEnforcer.isGE0 (nSize, "Size");
    m_aBuf = new byte [nSize];
  }

  @Nonnull
  private static byte [] _enlarge (@Nonnull final byte [] aBuf, @Nonnegative final int nNewSize)
  {
    final byte [] ret = new byte [nNewSize];
    System.arraycopy (aBuf, 0, ret, 0, aBuf.length);
    return ret;
  }

  /**
   * Writes the specified byte to this byte array output stream.
   *
   * @param b
   *        the byte to be written.
   */
  @Override
  public void write (final int b)
  {
    final int nNewCount = m_nCount + 1;
    if (nNewCount > m_aBuf.length)
      m_aBuf = _enlarge (m_aBuf, Math.max (m_aBuf.length << 1, nNewCount));
    m_aBuf[m_nCount] = (byte) b;
    m_nCount = nNewCount;
  }

  /*
   * Just overloaded to avoid the IOException in the generic OutputStream.write
   * method.
   */
  @Override
  public void write (@Nonnull final byte [] aBuf)
  {
    write (aBuf, 0, aBuf.length);
  }

  /**
   * Writes <code>nLen</code> bytes from the specified byte array starting at
   * offset <code>nOfs</code> to this byte array output stream.
   *
   * @param aBuf
   *        the data.
   * @param nOfs
   *        the start offset in the data.
   * @param nLen
   *        the number of bytes to write.
   */
  @Override
  public void write (@Nonnull final byte [] aBuf, final int nOfs, final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (aBuf, nOfs, nLen);
    if (nLen > 0)
    {
      final int nNewCount = m_nCount + nLen;
      if (nNewCount > m_aBuf.length)
        m_aBuf = _enlarge (m_aBuf, Math.max (m_aBuf.length << 1, nNewCount));
      System.arraycopy (aBuf, nOfs, m_aBuf, m_nCount, nLen);
      m_nCount = nNewCount;
    }
  }

  /**
   * Writes the complete contents of this byte array output stream to the
   * specified output stream argument, as if by calling the output stream's
   * write method using <code>out.write(buf, 0, count)</code>. The content of
   * this stream is not altered by calling this method.
   *
   * @param aOS
   *        the output stream to which to write the data. May not be
   *        <code>null</code>.
   * @exception IOException
   *            if an I/O error occurs.
   */
  public void writeTo (@Nonnull @WillNotClose final OutputStream aOS) throws IOException
  {
    aOS.write (m_aBuf, 0, m_nCount);
  }

  /**
   * Writes the complete contents of this byte array output stream to the
   * specified output stream argument, as if by calling the output stream's
   * write method using <code>out.write(buf, 0, count)</code> and afterwards
   * closes the passed output stream. The content of this stream is not altered
   * by calling this method.
   *
   * @param aOS
   *        the output stream to which to write the data. May not be
   *        <code>null</code>.
   * @exception IOException
   *            if an I/O error occurs.
   */
  public void writeToAndClose (@Nonnull @WillClose final OutputStream aOS) throws IOException
  {
    try
    {
      writeTo (aOS);
    }
    finally
    {
      StreamHelper.close (aOS);
    }
  }

  /**
   * Resets the <code>count</code> field of this byte array output stream to
   * zero, so that all currently accumulated output in the output stream is
   * discarded. The output stream can be used again, reusing the already
   * allocated buffer space.
   */
  public void reset ()
  {
    m_nCount = 0;
  }

  /**
   * Creates a newly allocated byte array. Its size is the current size of this
   * output stream and the valid contents of the buffer have been copied into
   * it.
   *
   * @return the current contents of this output stream, as a byte array.
   */
  @Nonnull
  @ReturnsMutableCopy
  public byte [] toByteArray ()
  {
    return ArrayHelper.getCopy (m_aBuf, m_nCount);
  }

  /**
   * Get the byte at the specified index
   *
   * @param nIndex
   *        The index to use. Must be &ge; 0 and &lt; count
   * @return The byte at the specified position
   */
  public byte getByteAt (@Nonnegative final int nIndex)
  {
    ValueEnforcer.isBetweenInclusive (nIndex, "Index", 0, m_nCount - 1);
    return m_aBuf[nIndex];
  }

  /**
   * Returns the current size of the buffer.
   *
   * @return the value of the <code>count</code> field, which is the number of
   *         valid bytes in this output stream.
   */
  @Nonnegative
  public int getSize ()
  {
    return m_nCount;
  }

  /**
   * @return The number of pre-allocated bytes. Always &ge; 0.
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

  public boolean startsWith (@Nonnull final byte [] aBytes)
  {
    return startsWith (aBytes, 0, aBytes.length);
  }

  public boolean startsWith (@Nonnull final byte [] aBytes, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    if (m_nCount < nLen)
      return false;
    for (int i = 0; i < nLen; ++i)
      if (m_aBuf[i] != aBytes[nOfs + i])
        return false;
    return true;
  }

  /**
   * Converts the buffer's contents into a string by decoding the bytes using
   * the specified {@link java.nio.charset.Charset charsetName}. The length of
   * the new <tt>String</tt> is a function of the charset, and hence may not be
   * equal to the length of the byte array.
   * <p>
   * This method always replaces malformed-input and unmappable-character
   * sequences with this charset's default replacement string. The
   * {@link java.nio.charset.CharsetDecoder} class should be used when more
   * control over the decoding process is required.
   *
   * @param aCharset
   *        the charset to be used. May not be <code>null</code>.
   * @return String decoded from the buffer's contents.
   */
  @Nonnull
  public String getAsString (@Nonnull final Charset aCharset)
  {
    return CharsetManager.getAsString (m_aBuf, 0, m_nCount, aCharset);
  }

  /**
   * Converts the buffer's contents into a string by decoding the bytes using
   * the specified {@link java.nio.charset.Charset charsetName}. The length of
   * the new <tt>String</tt> is a function of the charset, and hence may not be
   * equal to the length of the byte array.
   * <p>
   * This method always replaces malformed-input and unmappable-character
   * sequences with this charset's default replacement string. The
   * {@link java.nio.charset.CharsetDecoder} class should be used when more
   * control over the decoding process is required.
   *
   * @param nLength
   *        The number of bytes to be converted to a String. Must be &ge; 0.
   * @param aCharset
   *        the charset to be used. May not be <code>null</code>.
   * @return String decoded from the buffer's contents.
   */
  @Nonnull
  public String getAsString (@Nonnegative final int nLength, @Nonnull final Charset aCharset)
  {
    ValueEnforcer.isBetweenInclusive (nLength, "Length", 0, m_nCount);
    return CharsetManager.getAsString (m_aBuf, 0, nLength, aCharset);
  }

  /**
   * Converts the buffer's contents into a string by decoding the bytes using
   * the specified {@link java.nio.charset.Charset charsetName}. The length of
   * the new <tt>String</tt> is a function of the charset, and hence may not be
   * equal to the length of the byte array.
   * <p>
   * This method always replaces malformed-input and unmappable-character
   * sequences with this charset's default replacement string. The
   * {@link java.nio.charset.CharsetDecoder} class should be used when more
   * control over the decoding process is required.
   *
   * @param nIndex
   *        The start index to use
   * @param nLength
   *        The number of bytes to be converted to a String. Must be &ge; 0.
   * @param aCharset
   *        the charset to be used. May not be <code>null</code>.
   * @return String decoded from the buffer's contents.
   */
  @Nonnull
  public String getAsString (@Nonnegative final int nIndex,
                             @Nonnegative final int nLength,
                             @Nonnull final Charset aCharset)
  {
    ValueEnforcer.isGE0 (nIndex, "Index");
    ValueEnforcer.isBetweenInclusive (nLength, "Length", 0, m_nCount);
    return CharsetManager.getAsString (m_aBuf, nIndex, nLength, aCharset);
  }

  /**
   * Closing a <tt>ByteArrayOutputStream</tt> has no effect. The methods in this
   * class can be called after the stream has been closed without generating an
   * <tt>IOException</tt>.
   */
  @Override
  public void close ()
  {}

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("buf#", ArrayHelper.getSize (m_aBuf))
                                       .append ("size", m_nCount)
                                       .toString ();
  }
}
