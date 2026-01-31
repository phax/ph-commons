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

  public StringInputStreamProvider (final char @NonNull [] aChars, @NonNull final Charset aCharset)
  {
    this (new String (aChars), aCharset);
  }

  public StringInputStreamProvider (final char @NonNull [] aChars,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen,
                                    @NonNull final Charset aCharset)
  {
    this (new String (aChars, nOfs, nLen), aCharset);
  }

  public StringInputStreamProvider (@NonNull final CharSequence aData, @NonNull final Charset aCharset)
  {
    this (aData.toString (), aCharset);
  }

  public StringInputStreamProvider (@NonNull final String sData, @NonNull final Charset aCharset)
  {
    m_sData = ValueEnforcer.notNull (sData, "Data");
    m_aCharset = ValueEnforcer.notNull (aCharset, "Charset");
  }

  @NonNull
  public final String getData ()
  {
    return m_sData;
  }

  @NonNull
  public final Charset getCharset ()
  {
    return m_aCharset;
  }

  @NonNull
  public final StringInputStream getInputStream ()
  {
    return new StringInputStream (m_sData, m_aCharset);
  }

  @Override
  @NonNull
  public final NonBlockingStringReader getReader (@NonNull final Charset aCharset)
  {
    return new NonBlockingStringReader (m_sData);
  }

  @NonNull
  public final NonBlockingStringReader getReader ()
  {
    return new NonBlockingStringReader (m_sData);
  }

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
