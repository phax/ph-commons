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
package com.helger.base.io.streamprovider;

import com.helger.annotation.Nonnegative;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.io.iface.IHasReader;
import com.helger.base.io.nonblocking.NonBlockingStringReader;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;

/**
 * An {@link java.io.Reader} provider based on a {@link String}.
 *
 * @author Philip Helger
 */
public class StringReaderProvider implements IHasReader
{
  private final String m_sData;

  public StringReaderProvider (@Nonnull final char [] aChars)
  {
    this (new String (aChars));
  }

  public StringReaderProvider (@Nonnull final char [] aChars, @Nonnegative final int nOfs, @Nonnegative final int nLength)
  {
    this (new String (aChars, nOfs, nLength));
  }

  public StringReaderProvider (@Nonnull final CharSequence aData)
  {
    this (aData.toString ());
  }

  public StringReaderProvider (@Nonnull final String sData)
  {
    m_sData = ValueEnforcer.notNull (sData, "Data");
  }

  @Nonnull
  public String getData ()
  {
    return m_sData;
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

    final StringReaderProvider rhs = (StringReaderProvider) o;
    return m_sData.equals (rhs.m_sData);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sData).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("data", m_sData).getToString ();
  }
}
