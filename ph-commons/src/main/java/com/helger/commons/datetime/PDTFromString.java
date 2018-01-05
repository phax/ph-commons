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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.string.StringHelper;

/**
 * Handles the conversion to date, time or date time objects from a
 * {@link String}.
 *
 * @author Philip Helger
 */
@Immutable
public final class PDTFromString
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (PDTFromString.class);

  @PresentForCodeCoverage
  private static final PDTFromString s_aInstance = new PDTFromString ();

  private PDTFromString ()
  {}

  private static void _onParseException (@Nonnull final String sDestType,
                                         @Nonnull final String sValue,
                                         @Nullable final DateTimeFormatter aDF,
                                         @Nonnull final DateTimeParseException ex)
  {
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Failed to parse " +
                       sDestType +
                       " '" +
                       sValue +
                       "'" +
                       (aDF == null ? "" : " with " + aDF) +
                       ": " +
                       ex.getMessage ());
  }

  @Nullable
  public static ZonedDateTime getZonedDateTimeFromString (@Nullable final String sValue, @Nonnull final String sPattern)
  {
    return getZonedDateTimeFromString (sValue, PDTFormatter.getForPattern (sPattern, null));
  }

  @Nullable
  public static ZonedDateTime getZonedDateTimeFromString (@Nullable final String sValue,
                                                          @Nonnull final DateTimeFormatter aDF)
  {
    ValueEnforcer.notNull (aDF, "DateTimeFormatter");

    if (StringHelper.hasText (sValue))
      try
      {
        return aDF.parse (sValue, ZonedDateTime::from);
      }
      catch (final DateTimeParseException ex)
      {
        _onParseException ("ZonedDateTime", sValue, aDF, ex);
      }
    return null;
  }

  @Nullable
  public static LocalDate getLocalDateFromString (@Nullable final String sValue, @Nullable final Locale aParseLocale)
  {
    return getLocalDateFromString (sValue,
                                   PDTFormatter.getFormatterDate (PDTFormatter.DEFAULT_STYLE,
                                                                  aParseLocale,
                                                                  EDTFormatterMode.PARSE));
  }

  @Nullable
  public static LocalDate getLocalDateFromString (@Nullable final String sValue, @Nonnull final DateTimeFormatter aDF)
  {
    ValueEnforcer.notNull (aDF, "DateTimeFormatter");

    if (StringHelper.hasText (sValue))
      try
      {
        return aDF.parse (sValue, LocalDate::from);
      }
      catch (final DateTimeParseException ex)
      {
        _onParseException ("LocalDate", sValue, aDF, ex);
      }
    return null;
  }

  @Nullable
  public static LocalDate getLocalDateFromString (@Nullable final String sValue, @Nonnull final String sPattern)
  {
    return getLocalDateFromString (sValue, PDTFormatter.getForPattern (sPattern, null));
  }

  @Nullable
  public static LocalDateTime getLocalDateTimeFromString (@Nullable final String sValue,
                                                          @Nullable final Locale aParseLocale)
  {
    return getLocalDateTimeFromString (sValue,
                                       PDTFormatter.getFormatterDateTime (PDTFormatter.DEFAULT_STYLE,
                                                                          aParseLocale,
                                                                          EDTFormatterMode.PARSE));
  }

  @Nullable
  public static LocalDateTime getLocalDateTimeFromString (@Nullable final String sValue,
                                                          @Nonnull final DateTimeFormatter aDF)
  {
    ValueEnforcer.notNull (aDF, "DateTimeFormatter");

    if (StringHelper.hasText (sValue))
      try
      {
        return aDF.parse (sValue, LocalDateTime::from);
      }
      catch (final DateTimeParseException ex)
      {
        _onParseException ("LocalDateTime", sValue, aDF, ex);
      }
    return null;
  }

  @Nullable
  public static LocalDateTime getLocalDateTimeFromString (@Nullable final String sValue, @Nonnull final String sPattern)
  {
    return getLocalDateTimeFromString (sValue, PDTFormatter.getForPattern (sPattern, null));
  }

  @Nullable
  public static LocalTime getLocalTimeFromString (@Nullable final String sValue, @Nullable final Locale aParseLocale)
  {
    return getLocalTimeFromString (sValue,
                                   PDTFormatter.getFormatterTime (PDTFormatter.DEFAULT_STYLE,
                                                                  aParseLocale,
                                                                  EDTFormatterMode.PARSE));
  }

  @Nullable
  public static LocalTime getLocalTimeFromString (@Nullable final String sValue, @Nonnull final DateTimeFormatter aDF)
  {
    ValueEnforcer.notNull (aDF, "DateTimeFormatter");

    if (StringHelper.hasText (sValue))
      try
      {
        return aDF.parse (sValue, LocalTime::from);
      }
      catch (final DateTimeParseException ex)
      {
        _onParseException ("LocalTime", sValue, aDF, ex);
      }
    return null;
  }

  @Nullable
  public static LocalTime getLocalTimeFromString (@Nullable final String sValue, @Nonnull final String sPattern)
  {
    return getLocalTimeFromString (sValue, PDTFormatter.getForPattern (sPattern, null));
  }

  @Nullable
  public static Duration getDurationFromString (@Nullable final String sValue)
  {
    try
    {
      return Duration.parse (sValue);
    }
    catch (final DateTimeParseException ex)
    {
      _onParseException ("Duration", sValue, null, ex);
    }
    return null;
  }

  @Nullable
  public static Period getPeriodFromString (@Nullable final String sValue)
  {
    try
    {
      return Period.parse (sValue);
    }
    catch (final DateTimeParseException ex)
    {
      _onParseException ("Period", sValue, null, ex);
    }
    return null;
  }
}
