/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.text.util;

import java.text.MessageFormat;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.text.MultilingualText;

/**
 * Utility methods for formatting text using {@link MessageFormat}.
 *
 * @author Philip Helger
 */
@Immutable
public final class TextHelper
{
  /** German locale used */
  public static final Locale DE = LocaleCache.getLocale ("de");
  /** English locale used */
  public static final Locale EN = LocaleCache.getLocale ("en");

  @PresentForCodeCoverage
  private static final TextHelper s_aInstance = new TextHelper ();

  private TextHelper ()
  {}

  @Nullable
  public static String getFormattedText (@Nullable final String sText, @Nullable final Object... aArgs)
  {
    if (sText == null)
    {
      // Avoid NPE in MessageFormat
      return null;
    }

    if (aArgs == null || aArgs.length == 0)
    {
      // Return text unchanged
      return sText;
    }

    return MessageFormat.format (sText, aArgs);
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

    if (aArgs == null || aArgs.length == 0)
    {
      // Return text unchanged
      return sText;
    }

    final MessageFormat aMF = new MessageFormat (sText, aDisplayLocale);
    return aMF.format (aArgs);
  }

  @Nonnull
  public static MultilingualText create_DE (@Nonnull final String sDE)
  {
    final MultilingualText ret = new MultilingualText ();
    ret.addText (DE, sDE);
    return ret;
  }

  @Nonnull
  public static MultilingualText create_EN (@Nonnull final String sEN)
  {
    final MultilingualText ret = new MultilingualText ();
    ret.addText (EN, sEN);
    return ret;
  }

  @Nonnull
  public static MultilingualText create_DE_EN (@Nonnull final String sDE, @Nonnull final String sEN)
  {
    final MultilingualText ret = new MultilingualText ();
    ret.addText (DE, sDE);
    ret.addText (EN, sEN);
    return ret;
  }
}
