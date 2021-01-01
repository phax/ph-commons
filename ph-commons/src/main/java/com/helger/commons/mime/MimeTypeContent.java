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
package com.helger.commons.mime;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.ByteArrayWrapper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Represent a single mapping from content bytes to an {@link IMimeType}.
 *
 * @author Philip Helger
 */
@Immutable
@MustImplementEqualsAndHashcode
public class MimeTypeContent
{
  public static final boolean DEFAULT_COPY_BYTES = true;

  private final ByteArrayWrapper m_aContentBytes;
  private final IMimeType m_aMimeType;

  /**
   * Constructor
   *
   * @param aContentBytes
   *        The beginning bytes. May neither be <code>null</code> nor empty.
   * @param aMimeType
   *        The corresponding mime type. May not be <code>null</code>.
   */
  public MimeTypeContent (@Nonnull @Nonempty final byte [] aContentBytes, @Nonnull final IMimeType aMimeType)
  {
    this (aContentBytes, DEFAULT_COPY_BYTES, aMimeType);
  }

  /**
   * Constructor
   *
   * @param aContentBytes
   *        The beginning bytes. May neither be <code>null</code> nor empty.
   * @param bCopyBytes
   *        <code>true</code> to copy the bytes, <code>false</code> to reuse the
   *        provided instance.
   * @param aMimeType
   *        The corresponding mime type. May not be <code>null</code>.
   */
  public MimeTypeContent (@Nonnull @Nonempty final byte [] aContentBytes, final boolean bCopyBytes, @Nonnull final IMimeType aMimeType)
  {
    ValueEnforcer.notEmpty (aContentBytes, "ContentBytes");
    ValueEnforcer.notNull (aMimeType, "MimeType");

    m_aContentBytes = new ByteArrayWrapper (aContentBytes, bCopyBytes);
    m_aMimeType = aMimeType;
  }

  /**
   * @return A copy of the content bytes to use. Neither <code>null</code> nor
   *         empty.
   */
  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public byte [] getAllContentBytes ()
  {
    return m_aContentBytes.getAllBytes ();
  }

  /**
   * @return The number of content bytes available. Always &gt; 0.
   */
  @Nonnegative
  public int getContentByteCount ()
  {
    return m_aContentBytes.size ();
  }

  /**
   * Write the content bytes to the specified output stream.
   *
   * @param aOS
   *        The output stream to write to. The stream is NOT closed. May not be
   *        <code>null</code>.
   * @throws IOException
   *         In case of a write error
   */
  public void writeContentBytes (@Nonnull @WillNotClose final OutputStream aOS) throws IOException
  {
    m_aContentBytes.writeTo (aOS);
  }

  /**
   * @return The matching mime type. Never <code>null</code>.
   */
  @Nonnull
  public IMimeType getMimeType ()
  {
    return m_aMimeType;
  }

  /**
   * Check if the passed byte array starts with the bytes of this object.
   *
   * @param aCmpBytes
   *        The bytes to compare to. May not be <code>null</code>.
   * @return <code>true</code> if the passed bytes start with the bytes in this
   *         object.
   */
  public boolean matchesBeginning (@Nonnull final byte [] aCmpBytes)
  {
    return ArrayHelper.startsWith (aCmpBytes, m_aContentBytes.bytes ());
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MimeTypeContent rhs = (MimeTypeContent) o;
    return m_aContentBytes.equals (rhs.m_aContentBytes) && m_aMimeType.equals (rhs.m_aMimeType);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aContentBytes).append (m_aMimeType).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ContentBytes", m_aContentBytes).append ("MimeType", m_aMimeType).getToString ();
  }
}
