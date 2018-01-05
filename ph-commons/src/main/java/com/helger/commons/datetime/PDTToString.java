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
package com.helger.commons.datetime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * Standard API to convert a date, time or date time to a {@link String}.
 *
 * @author Philip Helger
 */
@Immutable
public final class PDTToString
{
  @PresentForCodeCoverage
  private static final PDTToString s_aInstance = new PDTToString ();

  private PDTToString ()
  {}

  @Nullable
  public static String getAsString (@Nullable final LocalDate aDate, @Nonnull final Locale aDisplayLocale)
  {
    return aDate == null ? null
                         : PDTFormatter.getFormatterDate (PDTFormatter.DEFAULT_STYLE,
                                                          aDisplayLocale,
                                                          EDTFormatterMode.PRINT)
                                       .format (aDate);
  }

  @Nullable
  public static String getAsString (@Nullable final LocalTime aTime, @Nonnull final Locale aDisplayLocale)
  {
    return aTime == null ? null
                         : PDTFormatter.getFormatterTime (PDTFormatter.DEFAULT_STYLE,
                                                          aDisplayLocale,
                                                          EDTFormatterMode.PRINT)
                                       .format (aTime);
  }

  @Nullable
  public static String getAsString (@Nullable final LocalDateTime aDateTime, @Nonnull final Locale aDisplayLocale)
  {
    return aDateTime == null ? null
                             : PDTFormatter.getFormatterDateTime (PDTFormatter.DEFAULT_STYLE,
                                                                  aDisplayLocale,
                                                                  EDTFormatterMode.PRINT)
                                           .format (aDateTime);
  }

  @Nullable
  public static String getAsString (@Nullable final ZonedDateTime aDateTime, @Nonnull final Locale aDisplayLocale)
  {
    return aDateTime == null ? null
                             : PDTFormatter.getFormatterDateTime (PDTFormatter.DEFAULT_STYLE,
                                                                  aDisplayLocale,
                                                                  EDTFormatterMode.PRINT)
                                           .format (aDateTime);
  }

  @Nullable
  public static String getAsString (@Nullable final OffsetDateTime aDateTime, @Nonnull final Locale aDisplayLocale)
  {
    return aDateTime == null ? null
                             : PDTFormatter.getFormatterDateTime (PDTFormatter.DEFAULT_STYLE,
                                                                  aDisplayLocale,
                                                                  EDTFormatterMode.PRINT)
                                           .format (aDateTime);
  }

  @Nullable
  public static String getAsString (@Nonnull final String sFormatPattern, @Nullable final TemporalAccessor aPartial)
  {
    return getAsString (sFormatPattern, aPartial, (Locale) null);
  }

  @Nullable
  public static String getAsString (@Nonnull final String sFormatPattern,
                                    @Nullable final TemporalAccessor aPartial,
                                    @Nullable final Locale aDisplayLocale)
  {
    return aPartial == null ? null : PDTFormatter.getForPattern (sFormatPattern, aDisplayLocale).format (aPartial);
  }
}
