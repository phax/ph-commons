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
package com.helger.datetime.format;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * Create common {@link DateFormat} patterns to format date, time and datetime
 * objects.
 *
 * @author Philip Helger
 */
@Immutable
public final class PDTFormatPatterns
{
  @PresentForCodeCoverage
  private static final PDTFormatPatterns s_aInstance = new PDTFormatPatterns ();

  private PDTFormatPatterns ()
  {}

  @Nonnull
  private static String _getPatternDate (final int nStyle, @Nonnull final Locale aDisplayLocale)
  {
    // Not nice but it works
    return ((SimpleDateFormat) DateFormat.getDateInstance (nStyle, aDisplayLocale)).toPattern ();
  }

  @Nonnull
  public static String getDefaultPatternDate (@Nonnull final Locale aDisplayLocale)
  {
    return getMediumPatternDate (aDisplayLocale);
  }

  @Nonnull
  public static String getShortPatternDate (@Nonnull final Locale aDisplayLocale)
  {
    return _getPatternDate (DateFormat.SHORT, aDisplayLocale);
  }

  @Nonnull
  public static String getMediumPatternDate (@Nonnull final Locale aDisplayLocale)
  {
    return _getPatternDate (DateFormat.MEDIUM, aDisplayLocale);
  }

  @Nonnull
  public static String getLongPatternDate (@Nonnull final Locale aDisplayLocale)
  {
    return _getPatternDate (DateFormat.LONG, aDisplayLocale);
  }

  @Nonnull
  public static String getFullPatternDate (@Nonnull final Locale aDisplayLocale)
  {
    return _getPatternDate (DateFormat.FULL, aDisplayLocale);
  }

  @Nonnull
  private static String _getPatternTime (final int nStyle, @Nonnull final Locale aDisplayLocale)
  {
    // Not nice but it works
    return ((SimpleDateFormat) DateFormat.getTimeInstance (nStyle, aDisplayLocale)).toPattern ();
  }

  @Nonnull
  public static String getDefaultPatternTime (@Nonnull final Locale aDisplayLocale)
  {
    return getMediumPatternTime (aDisplayLocale);
  }

  @Nonnull
  public static String getShortPatternTime (@Nonnull final Locale aDisplayLocale)
  {
    return _getPatternTime (DateFormat.SHORT, aDisplayLocale);
  }

  @Nonnull
  public static String getMediumPatternTime (@Nonnull final Locale aDisplayLocale)
  {
    return _getPatternTime (DateFormat.MEDIUM, aDisplayLocale);
  }

  @Nonnull
  public static String getLongPatternTime (@Nonnull final Locale aDisplayLocale)
  {
    return _getPatternTime (DateFormat.LONG, aDisplayLocale);
  }

  @Nonnull
  public static String getFullPatternTime (@Nonnull final Locale aDisplayLocale)
  {
    return _getPatternTime (DateFormat.FULL, aDisplayLocale);
  }

  @Nonnull
  private static String _getPatternDateTime (final int nStyle, @Nonnull final Locale aDisplayLocale)
  {
    // Not nice but it works
    return ((SimpleDateFormat) DateFormat.getDateTimeInstance (nStyle, nStyle, aDisplayLocale)).toPattern ();
  }

  @Nonnull
  public static String getDefaultPatternDateTime (@Nonnull final Locale aDisplayLocale)
  {
    return getMediumPatternDateTime (aDisplayLocale);
  }

  @Nonnull
  public static String getShortPatternDateTime (@Nonnull final Locale aDisplayLocale)
  {
    return _getPatternDateTime (DateFormat.SHORT, aDisplayLocale);
  }

  @Nonnull
  public static String getMediumPatternDateTime (@Nonnull final Locale aDisplayLocale)
  {
    return _getPatternDateTime (DateFormat.MEDIUM, aDisplayLocale);
  }

  @Nonnull
  public static String getLongPatternDateTime (@Nonnull final Locale aDisplayLocale)
  {
    return _getPatternDateTime (DateFormat.LONG, aDisplayLocale);
  }

  @Nonnull
  public static String getFullPatternDateTime (@Nonnull final Locale aDisplayLocale)
  {
    return _getPatternDateTime (DateFormat.FULL, aDisplayLocale);
  }
}
