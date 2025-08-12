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
package com.helger.commons.text;

import java.util.Locale;

import com.helger.commons.text.display.IHasDisplayTextWithArgs;
import com.helger.commons.text.util.TextHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Basic interface for object providing multilingual texts with and without
 * arguments.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IHasTextWithArgs extends IHasText
{
  /**
   * Get the text specific for the passed locale. The implementation class MAY
   * add locale-generalisation when resolving the text ("de_DE" =&gt; "de" =&gt;
   * <i>default</i>).
   *
   * @param aContentLocale
   *        The locale to use. May not be <code>null</code>.
   * @return <code>null</code> if no text for the given locale was found.
   * @deprecated Don't call this; Use {@link #getText(Locale)} instead!
   */
  @Nullable
  @Deprecated (forRemoval = false)
  default String getTextWithArgs (@Nonnull final Locale aContentLocale)
  {
    return getText (aContentLocale);
  }

  /**
   * Get the text specific for the passed locale. The implementation class MAY
   * add locale-generalisation when resolving the text ("de_DE" =&gt; "de" =&gt;
   * <i>default</i>). The placeholders will be resolved with the
   * {@link java.text.MessageFormat#format(Object)} method.
   *
   * @param aContentLocale
   *        The locale to use. May not be <code>null</code>.
   * @param aArgs
   *        The arguments to be added into the string. May be <code>null</code>
   *        but this makes no sense.
   * @return <code>null</code> if no text for the given locale was found.
   */
  @Nullable
  default String getTextWithArgs (@Nonnull final Locale aContentLocale, @Nullable final Object... aArgs)
  {
    final String sText = getText (aContentLocale);
    return TextHelper.getFormattedText (sText, aArgs);
  }

  /**
   * @return this as an instance of {@link IHasDisplayTextWithArgs}.
   * @since 8.5.2
   */
  @Nonnull
  default IHasDisplayTextWithArgs getAsHasDisplayTextWithArgs ()
  {
    return this::getText;
  }
}
