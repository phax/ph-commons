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
package com.helger.commons.io.streamprovider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.IHasInputStreamAndReader;
import com.helger.commons.io.IHasReader;
import com.helger.commons.io.stream.NonBlockingStringReader;
import com.helger.commons.io.stream.StringInputStream;
import com.helger.commons.serialize.convert.SerializationConverter;
import com.helger.commons.string.ToStringGenerator;

/**
 * An {@link java.io.InputStream} provider based on a {@link String}.
 *
 * @author Philip Helger
 */
public class StringInputStreamProvider implements IHasInputStreamAndReader, IHasReader, Serializable
{
  private String m_sData;
  private Charset m_aCharset;

  public StringInputStreamProvider (@Nonnull final char [] aChars, @Nonnull final Charset aCharset)
  {
    this (new String (aChars), aCharset);
  }

  public StringInputStreamProvider (@Nonnull final char [] aChars,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen,
                                    @Nonnull final Charset aCharset)
  {
    this (new String (aChars, nOfs, nLen), aCharset);
  }

  public StringInputStreamProvider (@Nonnull final CharSequence aData, @Nonnull final Charset aCharset)
  {
    this (aData.toString (), aCharset);
  }

  public StringInputStreamProvider (@Nonnull final String sData, @Nonnull final Charset aCharset)
  {
    m_sData = ValueEnforcer.notNull (sData, "Data");
    m_aCharset = ValueEnforcer.notNull (aCharset, "Charset");
  }

  private void writeObject (@Nonnull final ObjectOutputStream aOOS) throws IOException
  {
    aOOS.writeUTF (m_sData);
    SerializationConverter.writeConvertedObject (m_aCharset, aOOS);
  }

  private void readObject (@Nonnull final ObjectInputStream aOIS) throws IOException
  {
    m_sData = aOIS.readUTF ();
    m_aCharset = SerializationConverter.readConvertedObject (aOIS, Charset.class);
  }

  @Nonnull
  public String getData ()
  {
    return m_sData;
  }

  @Nonnull
  public Charset getCharset ()
  {
    return m_aCharset;
  }

  @Nonnull
  public final StringInputStream getInputStream ()
  {
    return new StringInputStream (m_sData, m_aCharset);
  }

  @Nonnull
  public final NonBlockingStringReader getReader (@Nonnull final Charset aCharset)
  {
    return new NonBlockingStringReader (m_sData);
  }

  @Nonnull
  public final NonBlockingStringReader getReader ()
  {
    return new NonBlockingStringReader (m_sData);
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
    return new ToStringGenerator (null).append ("data", m_sData).append ("charset", m_aCharset).toString ();
  }
}
