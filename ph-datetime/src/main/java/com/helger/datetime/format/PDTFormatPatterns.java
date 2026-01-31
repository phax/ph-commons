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
package com.helger.datetime.format;

import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;

/**
 * Create common {@link FormatStyle} patterns to format date, time and datetime
 * objects.
 *
 * @author Philip Helger
 */
@Immutable
public final class PDTFormatPatterns
{
  @PresentForCodeCoverage
  private static final PDTFormatPatterns INSTANCE = new PDTFormatPatterns ();

  private PDTFormatPatterns ()
  {}

  @NonNull
  public static String getPatternDate (@NonNull final FormatStyle eStyle, @NonNull final Locale aDisplayLocale)
  {
    return DateTimeFormatterBuilder.getLocalizedDateTimePattern (eStyle, null, IsoChronology.INSTANCE, aDisplayLocale);
  }

  @NonNull
  public static String getDefaultPatternDate (@NonNull final Locale aDisplayLocale)
  {
    return getMediumPatternDate (aDisplayLocale);
  }

  @NonNull
  public static String getShortPatternDate (@NonNull final Locale aDisplayLocale)
  {
    return getPatternDate (FormatStyle.SHORT, aDisplayLocale);
  }

  @NonNull
  public static String getMediumPatternDate (@NonNull final Locale aDisplayLocale)
  {
    return getPatternDate (FormatStyle.MEDIUM, aDisplayLocale);
  }

  @NonNull
  public static String getLongPatternDate (@NonNull final Locale aDisplayLocale)
  {
    return getPatternDate (FormatStyle.LONG, aDisplayLocale);
  }

  @NonNull
  public static String getFullPatternDate (@NonNull final Locale aDisplayLocale)
  {
    return getPatternDate (FormatStyle.FULL, aDisplayLocale);
  }

  @NonNull
  public static String getPatternTime (@NonNull final FormatStyle eStyle, @NonNull final Locale aDisplayLocale)
  {
    return DateTimeFormatterBuilder.getLocalizedDateTimePattern (null, eStyle, IsoChronology.INSTANCE, aDisplayLocale);
  }

  @NonNull
  public static String getDefaultPatternTime (@NonNull final Locale aDisplayLocale)
  {
    return getMediumPatternTime (aDisplayLocale);
  }

  @NonNull
  public static String getShortPatternTime (@NonNull final Locale aDisplayLocale)
  {
    return getPatternTime (FormatStyle.SHORT, aDisplayLocale);
  }

  @NonNull
  public static String getMediumPatternTime (@NonNull final Locale aDisplayLocale)
  {
    return getPatternTime (FormatStyle.MEDIUM, aDisplayLocale);
  }

  @NonNull
  public static String getLongPatternTime (@NonNull final Locale aDisplayLocale)
  {
    return getPatternTime (FormatStyle.LONG, aDisplayLocale);
  }

  @NonNull
  public static String getFullPatternTime (@NonNull final Locale aDisplayLocale)
  {
    return getPatternTime (FormatStyle.FULL, aDisplayLocale);
  }

  @NonNull
  public static String getPatternDateTime (@NonNull final FormatStyle eStyle, @NonNull final Locale aDisplayLocale)
  {
    return DateTimeFormatterBuilder.getLocalizedDateTimePattern (eStyle, eStyle, IsoChronology.INSTANCE, aDisplayLocale);
  }

  @NonNull
  public static String getDefaultPatternDateTime (@NonNull final Locale aDisplayLocale)
  {
    return getMediumPatternDateTime (aDisplayLocale);
  }

  @NonNull
  public static String getShortPatternDateTime (@NonNull final Locale aDisplayLocale)
  {
    return getPatternDateTime (FormatStyle.SHORT, aDisplayLocale);
  }

  @NonNull
  public static String getMediumPatternDateTime (@NonNull final Locale aDisplayLocale)
  {
    return getPatternDateTime (FormatStyle.MEDIUM, aDisplayLocale);
  }

  @NonNull
  public static String getLongPatternDateTime (@NonNull final Locale aDisplayLocale)
  {
    return getPatternDateTime (FormatStyle.LONG, aDisplayLocale);
  }

  @NonNull
  public static String getFullPatternDateTime (@NonNull final Locale aDisplayLocale)
  {
    return getPatternDateTime (FormatStyle.FULL, aDisplayLocale);
  }
}
