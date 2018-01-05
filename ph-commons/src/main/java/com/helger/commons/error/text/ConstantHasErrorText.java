/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.error.text;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * A constant implementation of {@link IHasErrorText}. Cannot be a lambda
 * expression because equals/hashCode is required!
 *
 * @author Philip Helger
 */
@Immutable
@MustImplementEqualsAndHashcode
public class ConstantHasErrorText implements IHasErrorText
{
  private final String m_sText;

  public ConstantHasErrorText (@Nullable final String sText)
  {
    m_sText = sText;
  }

  @Nullable
  public String getDisplayText (@Nonnull final Locale aContentLocale)
  {
    return m_sText;
  }

  public boolean isMultiLingual ()
  {
    return false;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ConstantHasErrorText rhs = (ConstantHasErrorText) o;
    return EqualsHelper.equals (m_sText, rhs.m_sText);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sText).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Text", m_sText).getToString ();
  }

  @Nullable
  public static ConstantHasErrorText createOnDemand (@Nullable final String sErrorText)
  {
    // Empty error text is remembered
    return sErrorText == null ? null : new ConstantHasErrorText (sErrorText);
  }
}
