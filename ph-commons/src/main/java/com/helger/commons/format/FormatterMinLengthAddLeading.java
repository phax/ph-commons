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
package com.helger.commons.format;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * A string formatter that ensures that a string has a minimum length by filling
 * the remaining chars with a custom character at front (leading).
 *
 * @author Philip Helger
 */
public final class FormatterMinLengthAddLeading extends AbstractFormatterString
{
  private final int m_nMinLength;
  private final char m_cFill;

  public FormatterMinLengthAddLeading (@Nonnegative final int nMinLength, final char cFill)
  {
    ValueEnforcer.isGT0 (nMinLength, "MinLength");
    m_nMinLength = nMinLength;
    m_cFill = cFill;
  }

  @Nonnegative
  public int getMinLength ()
  {
    return m_nMinLength;
  }

  public char getFillChar ()
  {
    return m_cFill;
  }

  @Override
  public String apply (@Nullable final Object aValue)
  {
    final String s = getValueAsString (aValue);
    if (s.length () >= m_nMinLength)
      return s;
    return StringHelper.getRepeated (m_cFill, m_nMinLength - s.length ()) + s;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final FormatterMinLengthAddLeading rhs = (FormatterMinLengthAddLeading) o;
    return m_nMinLength == rhs.m_nMinLength && m_cFill == rhs.m_cFill;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_nMinLength).append (m_cFill).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("minLength", m_nMinLength)
                            .append ("fill", m_cFill)
                            .toString ();
  }
}
