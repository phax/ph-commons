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
package com.helger.base.text;

import java.text.MessageFormat;
import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.array.ArrayHelper;
import com.helger.base.enforce.ValueEnforcer;

/**
 * Helper class for formatting text using {@link java.text.MessageFormat}
 * patterns with locale-aware formatting.
 *
 * @author Philip Helger
 */
@Immutable
public final class TextFormatter
{
  private TextFormatter ()
  {}

  /**
   * Format the given text using {@link MessageFormat} with the default format
   * locale. If no arguments are provided, the text is returned unchanged.
   *
   * @param sText
   *        The message pattern to format. May be <code>null</code>.
   * @param aArgs
   *        The arguments to be substituted into the pattern. May be
   *        <code>null</code>.
   * @return The formatted text, or <code>null</code> if the input text is
   *         <code>null</code>.
   */
  @Nullable
  public static String getFormattedText (@Nullable final String sText, @Nullable final Object... aArgs)
  {
    if (sText == null)
    {
      // Avoid NPE in MessageFormat
      return null;
    }

    if (ArrayHelper.isEmpty (aArgs))
    {
      // Return text unchanged
      return sText;
    }

    final MessageFormat aMF = new MessageFormat (sText, Locale.getDefault (Locale.Category.FORMAT));
    return aMF.format (aArgs);
  }

  /**
   * Format the given text using {@link MessageFormat} with the specified locale.
   * If no arguments are provided, the text is returned unchanged.
   *
   * @param aDisplayLocale
   *        The locale to use for formatting. May not be <code>null</code>.
   * @param sText
   *        The message pattern to format. May be <code>null</code>.
   * @param aArgs
   *        The arguments to be substituted into the pattern. May be
   *        <code>null</code>.
   * @return The formatted text, or <code>null</code> if the input text is
   *         <code>null</code>.
   */
  @Nullable
  public static String getFormattedText (@NonNull final Locale aDisplayLocale,
                                         @Nullable final String sText,
                                         @Nullable final Object... aArgs)
  {
    ValueEnforcer.notNull (aDisplayLocale, "DisplayLocale");

    if (sText == null)
    {
      // Avoid NPE in MessageFormat
      return null;
    }

    if (ArrayHelper.isEmpty (aArgs))
    {
      // Return text unchanged
      return sText;
    }

    final MessageFormat aMF = new MessageFormat (sText, aDisplayLocale);
    return aMF.format (aArgs);
  }

}
