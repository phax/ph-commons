/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.commons.io;

import java.nio.charset.Charset;
import java.util.Arrays;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.string.ToStringGenerator;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A straight forward implementation of {@link IHasByteArray}
 *
 * @author Philip Helger
 * @since 9.1.3
 */
@MustImplementEqualsAndHashcode
public final class ByteArrayWrapper implements IHasByteArray
{
  private final byte [] m_aBytes;
  private final int m_nOffset;
  private final int m_nLength;
  private final boolean m_bIsCopy;

  /**
   * Wrap the whole byte array.
   *
   * @param aBytes
   *        The byte array to be wrapped. May not be <code>null</code>.
   * @param bCopyNeeded
   *        <code>true</code> to copy it, <code>false</code> to reuse the
   *        instance.
   */
  public ByteArrayWrapper (@Nonnull final byte [] aBytes, final boolean bCopyNeeded)
  {
    this (aBytes, 0, aBytes.length, bCopyNeeded);
  }

  /**
   * Wrap the passed byte array or just parts of it.
   *
   * @param aBytes
   *        The byte array to be wrapped. May not be <code>null</code>.
   * @param nOfs
   *        Offset. Must be &ge; 0.
   * @param nLength
   *        Length. Must be &ge; 0.
   * @param bCopyNeeded
   *        <code>true</code> to copy it, <code>false</code> to reuse the
   *        instance.
   */
  public ByteArrayWrapper (@Nonnull final byte [] aBytes,
                           @Nonnegative final int nOfs,
                           @Nonnegative final int nLength,
                           final boolean bCopyNeeded)
  {
    ValueEnforcer.isArrayOfsLen (aBytes, nOfs, nLength);
    m_aBytes = bCopyNeeded ? ArrayHelper.getCopy (aBytes, nOfs, nLength) : aBytes;
    m_nOffset = bCopyNeeded ? 0 : nOfs;
    m_nLength = nLength;
    m_bIsCopy = bCopyNeeded;
  }

  public boolean isCopy ()
  {
    return m_bIsCopy;
  }

  @Nonnull
  @ReturnsMutableObject
  @SuppressFBWarnings ("EI_EXPOSE_REP")
  public byte [] bytes ()
  {
    return m_aBytes;
  }

  @Nonnegative
  public int getOffset ()
  {
    return m_nOffset;
  }

  @Nonnegative
  public int size ()
  {
    return m_nLength;
  }

  @Override
  public boolean isEmpty ()
  {
    return m_nLength == 0;
  }

  @Override
  public boolean isNotEmpty ()
  {
    return m_nLength > 0;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ByteArrayWrapper rhs = (ByteArrayWrapper) o;
    return Arrays.equals (m_aBytes, rhs.m_aBytes) && m_nOffset == rhs.m_nOffset && m_nLength == rhs.m_nLength;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aBytes).append (m_nOffset).append (m_nLength).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("byte[]#", m_aBytes.length)
                                       .append ("Offset", m_nOffset)
                                       .append ("Length", m_nLength)
                                       .append ("IsCopy", m_bIsCopy)
                                       .getToString ();
  }

  /**
   * Wrap the content of a {@link NonBlockingByteArrayOutputStream}.
   *
   * @param aBAOS
   *        The output stream to be wrapped. May not be <code>null</code>.
   * @param bCopyNeeded
   *        <code>true</code> to copy it (needed if the output stream will be
   *        modified afterwards), <code>false</code> to reuse the data (if the
   *        output stream will not be modified afterwards).
   * @return ByteArrayWrapper The created instance. Never <code>null</code>.
   * @since 9.2.1
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ByteArrayWrapper create (@Nonnull final NonBlockingByteArrayOutputStream aBAOS, final boolean bCopyNeeded)
  {
    return new ByteArrayWrapper (aBAOS.directGetBuffer (), 0, aBAOS.size (), bCopyNeeded);
  }

  /**
   * Wrap the content of a String in a certain charset.
   *
   * @param sText
   *        The String to be wrapped. May not be <code>null</code>.
   * @param aCharset
   *        The character set to be used for retrieving the bytes from the
   *        string. May not be <code>null</code>.
   * @return ByteArrayWrapper The created instance. Never <code>null</code>.
   * @since 9.2.1
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ByteArrayWrapper create (@Nonnull final String sText, @Nonnull final Charset aCharset)
  {
    return new ByteArrayWrapper (sText.getBytes (aCharset), false);
  }
}
