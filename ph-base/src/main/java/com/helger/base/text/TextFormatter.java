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
package com.helger.base.text;

import java.text.MessageFormat;
import java.util.Locale;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.array.ArrayHelper;
import com.helger.base.equals.ValueEnforcer;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@Immutable
public final class TextFormatter
{
  private TextFormatter ()
  {}

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

  @Nullable
  public static String getFormattedText (@Nonnull final Locale aDisplayLocale,
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
