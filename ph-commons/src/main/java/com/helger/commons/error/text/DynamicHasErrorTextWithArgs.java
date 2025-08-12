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
package com.helger.commons.error.text;

import java.util.Locale;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.MustImplementEqualsAndHashcode;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.text.IHasText;
import com.helger.commons.text.display.IHasDisplayText;
import com.helger.commons.text.util.TextHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A special implementation of {@link IHasErrorText} that encapsulates arguments
 * to be put into the message.
 *
 * @author Philip Helger
 * @since 8.5.2
 */
@Immutable
@MustImplementEqualsAndHashcode
public class DynamicHasErrorTextWithArgs implements IHasErrorText
{
  private final IHasText m_aParentText;
  private final Object [] m_aArgs;

  public DynamicHasErrorTextWithArgs (@Nonnull final IHasDisplayText aParentText, @Nonnull @Nonempty final Object... aArgs)
  {
    this (aParentText.getAsHasText (), aArgs);
  }

  public DynamicHasErrorTextWithArgs (@Nonnull final IHasText aParentText, @Nonnull @Nonempty final Object... aArgs)
  {
    m_aParentText = ValueEnforcer.notNull (aParentText, "ParentText");
    m_aArgs = ValueEnforcer.notEmpty (aArgs, "Arguments");
  }

  @Nonnull
  public IHasText getParentText ()
  {
    return m_aParentText;
  }

  /**
   * Get all arguments from the constructor.
   *
   * @return a copy of all arguments. Neither <code>null</code> nor empty-
   */
  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public Object [] getAllArgs ()
  {
    return ArrayHelper.getCopy (m_aArgs);
  }

  @Nullable
  public String getDisplayText (@Nonnull final Locale aContentLocale)
  {
    final String sText = m_aParentText.getText (aContentLocale);
    return TextHelper.getFormattedText (sText, m_aArgs);
  }

  public boolean isMultiLingual ()
  {
    return true;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("parentText", m_aParentText).append ("args", m_aArgs).getToString ();
  }
}
