/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.base.io.streamprovider;

import java.nio.charset.Charset;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.io.iface.IHasInputStreamAndReader;
import com.helger.base.io.iface.IHasReader;
import com.helger.base.io.nonblocking.NonBlockingStringReader;
import com.helger.base.io.stream.StringInputStream;
import com.helger.base.tostring.ToStringGenerator;

/**
 * An {@link java.io.InputStream} provider based on a {@link String}.
 *
 * @author Philip Helger
 */
public class StringInputStreamProvider implements IHasInputStreamAndReader, IHasReader
{
  private String m_sData;
  private Charset m_aCharset;

  /**
   * Constructor using a char array.
   *
   * @param aChars
   *        The char data. May not be <code>null</code>.
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   */
  public StringInputStreamProvider (final char @NonNull [] aChars, @NonNull final Charset aCharset)
  {
    this (new String (aChars), aCharset);
  }

  /**
   * Constructor using a portion of a char array.
   *
   * @param aChars
   *        The char data. May not be <code>null</code>.
   * @param nOfs
   *        Offset into the char array. Must be &ge; 0.
   * @param nLen
   *        Number of chars to use. Must be &ge; 0.
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   */
  public StringInputStreamProvider (final char @NonNull [] aChars,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen,
                                    @NonNull final Charset aCharset)
  {
    this (new String (aChars, nOfs, nLen), aCharset);
  }

  /**
   * Constructor using a {@link CharSequence}.
   *
   * @param aData
   *        The char sequence data. May not be <code>null</code>.
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   */
  public StringInputStreamProvider (@NonNull final CharSequence aData, @NonNull final Charset aCharset)
  {
    this (aData.toString (), aCharset);
  }

  /**
   * Constructor using a {@link String}.
   *
   * @param sData
   *        The string data. May not be <code>null</code>.
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   */
  public StringInputStreamProvider (@NonNull final String sData, @NonNull final Charset aCharset)
  {
    m_sData = ValueEnforcer.notNull (sData, "Data");
    m_aCharset = ValueEnforcer.notNull (aCharset, "Charset");
  }

  /**
   * @return The data string. Never <code>null</code>.
   */
  @NonNull
  public final String getData ()
  {
    return m_sData;
  }

  /**
   * @return The charset used for encoding. Never <code>null</code>.
   */
  @NonNull
  public final Charset getCharset ()
  {
    return m_aCharset;
  }

  /** {@inheritDoc} */
  @NonNull
  public final StringInputStream getInputStream ()
  {
    return new StringInputStream (m_sData, m_aCharset);
  }

  /** {@inheritDoc} */
  @Override
  @NonNull
  public final NonBlockingStringReader getReader (@NonNull final Charset aCharset)
  {
    return new NonBlockingStringReader (m_sData);
  }

  /**
   * @return A new reader for the contained string data. Never <code>null</code>.
   */
  @NonNull
  public final NonBlockingStringReader getReader ()
  {
    return new NonBlockingStringReader (m_sData);
  }

  /** {@inheritDoc} */
  public final boolean isReadMultiple ()
  {
    return true;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;

    final StringInputStreamProvider rhs = (StringInputStreamProvider) o;
    return m_sData.equals (rhs.m_sData) && m_aCharset.equals (rhs.m_aCharset);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sData).append (m_aCharset).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("data", m_sData).append ("charset", m_aCharset).getToString ();
  }
}
