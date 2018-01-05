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

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.text.IHasText;
import com.helger.commons.text.IMultilingualText;
import com.helger.commons.text.display.IHasDisplayText;

/**
 * A implementation of {@link IHasErrorText} based on {@link IMultilingualText}
 * because this class is Serializable and implements equals/hashCode. Cannot be
 * a lambda expression because equals/hashCode is required!
 *
 * @author Philip Helger
 */
@Immutable
@MustImplementEqualsAndHashcode
public class DynamicHasErrorText implements IHasErrorText
{
  private final IHasText m_aText;

  public DynamicHasErrorText (@Nonnull final IHasDisplayText aText)
  {
    this (aText.getAsHasText ());
  }

  public DynamicHasErrorText (@Nonnull final IHasText aText)
  {
    m_aText = ValueEnforcer.notNull (aText, "Text");
  }

  @Nullable
  public String getDisplayText (@Nonnull final Locale aContentLocale)
  {
    return m_aText.getText (aContentLocale);
  }

  public boolean isMultiLingual ()
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
    final DynamicHasErrorText rhs = (DynamicHasErrorText) o;
    return EqualsHelper.equals (m_aText, rhs.m_aText);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aText).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("MLT", m_aText).getToString ();
  }
}
