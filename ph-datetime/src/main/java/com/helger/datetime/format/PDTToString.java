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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.datetime.rt.OffsetDate;
import com.helger.datetime.xml.XMLOffsetDate;
import com.helger.datetime.xml.XMLOffsetDateTime;
import com.helger.datetime.xml.XMLOffsetTime;

/**
 * Standard API to convert a date, time or date time to a {@link String}.
 *
 * @author Philip Helger
 */
@Immutable
public final class PDTToString
{
  @PresentForCodeCoverage
  private static final PDTToString INSTANCE = new PDTToString ();

  private PDTToString ()
  {}

  @Nullable
  public static String getAsString (@Nullable final LocalDate aDate, @NonNull final Locale aDisplayLocale)
  {
    return aDate == null ? null
                         : PDTFormatter.getFormatterDate (PDTFormatter.DEFAULT_STYLE,
                                                          aDisplayLocale,
                                                          EDTFormatterMode.PRINT)
                                       .format (aDate);
  }

  @Nullable
  public static String getAsString (@Nullable final OffsetDate aDate, @NonNull final Locale aDisplayLocale)
  {
    return aDate == null ? null
                         : PDTFormatter.getFormatterOffsetDate (PDTFormatter.DEFAULT_STYLE,
                                                                aDisplayLocale,
                                                                EDTFormatterMode.PRINT)
                                       .format (aDate);
  }

  @Nullable
  public static String getAsString (@Nullable final XMLOffsetDate aDate, @NonNull final Locale aDisplayLocale)
  {
    if (aDate == null)
      return null;

    if (aDate.hasOffset ())
      return PDTFormatter.getFormatterOffsetDate (PDTFormatter.DEFAULT_STYLE, aDisplayLocale, EDTFormatterMode.PRINT)
                         .format (aDate);

    return PDTFormatter.getFormatterDate (PDTFormatter.DEFAULT_STYLE, aDisplayLocale, EDTFormatterMode.PRINT)
                       .format (aDate.toLocalDate ());
  }

  @Nullable
  public static String getAsString (@Nullable final LocalTime aTime, @NonNull final Locale aDisplayLocale)
  {
    return aTime == null ? null
                         : PDTFormatter.getFormatterTime (PDTFormatter.DEFAULT_STYLE,
                                                          aDisplayLocale,
                                                          EDTFormatterMode.PRINT)
                                       .format (aTime);
  }

  @Nullable
  public static String getAsString (@Nullable final OffsetTime aTime, @NonNull final Locale aDisplayLocale)
  {
    return aTime == null ? null
                         : PDTFormatter.getFormatterOffsetTime (PDTFormatter.DEFAULT_STYLE,
                                                                aDisplayLocale,
                                                                EDTFormatterMode.PRINT)
                                       .format (aTime);
  }

  @Nullable
  public static String getAsString (@Nullable final XMLOffsetTime aTime, @NonNull final Locale aDisplayLocale)
  {
    if (aTime == null)
      return null;

    if (aTime.hasOffset ())
      return PDTFormatter.getFormatterOffsetTime (PDTFormatter.DEFAULT_STYLE, aDisplayLocale, EDTFormatterMode.PRINT)
                         .format (aTime);

    return PDTFormatter.getFormatterTime (PDTFormatter.DEFAULT_STYLE, aDisplayLocale, EDTFormatterMode.PRINT)
                       .format (aTime.toLocalTime ());
  }

  @Nullable
  public static String getAsString (@Nullable final LocalDateTime aDateTime, @NonNull final Locale aDisplayLocale)
  {
    return aDateTime == null ? null
                             : PDTFormatter.getFormatterDateTime (PDTFormatter.DEFAULT_STYLE,
                                                                  aDisplayLocale,
                                                                  EDTFormatterMode.PRINT)
                                           .format (aDateTime);
  }

  @Nullable
  public static String getAsString (@Nullable final ZonedDateTime aDateTime, @NonNull final Locale aDisplayLocale)
  {
    return aDateTime == null ? null
                             : PDTFormatter.getFormatterZonedDateTime (PDTFormatter.DEFAULT_STYLE,
                                                                       aDisplayLocale,
                                                                       EDTFormatterMode.PRINT)
                                           .format (aDateTime);
  }

  @Nullable
  public static String getAsString (@Nullable final OffsetDateTime aDateTime, @NonNull final Locale aDisplayLocale)
  {
    return aDateTime == null ? null
                             : PDTFormatter.getFormatterOffsetDateTime (PDTFormatter.DEFAULT_STYLE,
                                                                        aDisplayLocale,
                                                                        EDTFormatterMode.PRINT)
                                           .format (aDateTime);
  }

  @Nullable
  public static String getAsString (@Nullable final XMLOffsetDateTime aDateTime, @NonNull final Locale aDisplayLocale)
  {
    if (aDateTime == null)
      return null;

    if (aDateTime.hasOffset ())
      return PDTFormatter.getFormatterOffsetDateTime (PDTFormatter.DEFAULT_STYLE,
                                                      aDisplayLocale,
                                                      EDTFormatterMode.PRINT)
                         .format (aDateTime);
    return PDTFormatter.getFormatterDateTime (PDTFormatter.DEFAULT_STYLE, aDisplayLocale, EDTFormatterMode.PRINT)
                       .format (aDateTime.toLocalDateTime ());
  }

  @Nullable
  public static String getAsString (@NonNull final String sFormatPattern, @Nullable final TemporalAccessor aPartial)
  {
    return getAsString (sFormatPattern, aPartial, (Locale) null);
  }

  @Nullable
  public static String getAsString (@NonNull final String sFormatPattern,
                                    @Nullable final TemporalAccessor aPartial,
                                    @Nullable final Locale aDisplayLocale)
  {
    return aPartial == null ? null : PDTFormatter.getForPattern (sFormatPattern, aDisplayLocale).format (aPartial);
  }
}
