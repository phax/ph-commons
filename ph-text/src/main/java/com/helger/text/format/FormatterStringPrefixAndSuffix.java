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
package com.helger.text.format;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;

/**
 * A formatter that adds a prefix and/or a suffix to a string.
 *
 * @author Philip Helger
 */
public class FormatterStringPrefixAndSuffix extends AbstractFormatterString
{
  private final String m_sPrefix;
  private final String m_sSuffix;

  public FormatterStringPrefixAndSuffix (@NonNull final String sPrefix, @NonNull final String sSuffix)
  {
    m_sPrefix = ValueEnforcer.notNull (sPrefix, "Prefix");
    m_sSuffix = ValueEnforcer.notNull (sSuffix, "Suffix");
  }

  @NonNull
  public String getPrefix ()
  {
    return m_sPrefix;
  }

  @NonNull
  public String getSuffix ()
  {
    return m_sSuffix;
  }

  @Override
  public String apply (@Nullable final Object aValue)
  {
    return m_sPrefix + getValueAsString (aValue) + m_sSuffix;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final FormatterStringPrefixAndSuffix rhs = (FormatterStringPrefixAndSuffix) o;
    return m_sPrefix.equals (rhs.m_sPrefix) && m_sSuffix.equals (rhs.m_sSuffix);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sPrefix).append (m_sSuffix).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("Prefix", m_sPrefix).append ("Suffix", m_sSuffix).getToString ();
  }

  @NonNull
  public static FormatterStringPrefixAndSuffix createPrefixOnly (@NonNull final String sPrefix)
  {
    return new FormatterStringPrefixAndSuffix (sPrefix, "");
  }

  @NonNull
  public static FormatterStringPrefixAndSuffix createSuffixOnly (@NonNull final String sSuffix)
  {
    return new FormatterStringPrefixAndSuffix ("", sSuffix);
  }

  @NonNull
  public static FormatterStringPrefixAndSuffix createWithBrackets ()
  {
    return new FormatterStringPrefixAndSuffix ("[", "]");
  }
}
