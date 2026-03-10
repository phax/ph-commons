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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.datetime.rt.OffsetDate;
import com.helger.datetime.xml.XMLOffsetDate;
import com.helger.datetime.xml.XMLOffsetDateTime;
import com.helger.datetime.xml.XMLOffsetTime;

/**
 * Handles the conversion to date, time or date time objects from a {@link String}.
 *
 * @author Philip Helger
 */
@Immutable
public final class PDTFromString
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PDTFromString.class);

  @PresentForCodeCoverage
  private static final PDTFromString INSTANCE = new PDTFromString ();

  private PDTFromString ()
  {}

  private static void _onParseException (@NonNull final String sDestType,
                                         @NonNull final String sValue,
                                         @Nullable final DateTimeFormatter aDF,
                                         @NonNull final DateTimeParseException ex)
  {
    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Failed to parse " +
                    sDestType +
                    " '" +
                    sValue +
                    "'" +
                    (aDF == null ? "" : " with " + aDF) +
                    ": " +
                    ex.getMessage ());
  }

  /**
   * Parse a {@link ZonedDateTime} from the given string using the specified
   * pattern.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param sPattern
   *        The pattern to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static ZonedDateTime getZonedDateTimeFromString (@Nullable final String sValue, @NonNull final String sPattern)
  {
    return getZonedDateTimeFromString (sValue, PDTFormatter.getForPattern (sPattern, null));
  }

  /**
   * Parse a {@link ZonedDateTime} from the given string using the specified
   * formatter.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param aDF
   *        The formatter to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static ZonedDateTime getZonedDateTimeFromString (@Nullable final String sValue,
                                                          @NonNull final DateTimeFormatter aDF)
  {
    ValueEnforcer.notNull (aDF, "DateTimeFormatter");

    if (StringHelper.isNotEmpty (sValue))
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

  /**
   * Parse an {@link OffsetDateTime} from the given string using the specified
   * pattern.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param sPattern
   *        The pattern to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static OffsetDateTime getOffsetDateTimeFromString (@Nullable final String sValue,
                                                            @NonNull final String sPattern)
  {
    return getOffsetDateTimeFromString (sValue, PDTFormatter.getForPattern (sPattern, null));
  }

  /**
   * Parse an {@link OffsetDateTime} from the given string using the specified
   * formatter.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param aDF
   *        The formatter to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static OffsetDateTime getOffsetDateTimeFromString (@Nullable final String sValue,
                                                            @NonNull final DateTimeFormatter aDF)
  {
    ValueEnforcer.notNull (aDF, "DateTimeFormatter");

    if (StringHelper.isNotEmpty (sValue))
      try
      {
        return aDF.parse (sValue, OffsetDateTime::from);
      }
      catch (final DateTimeParseException ex)
      {
        _onParseException ("OffsetDateTime", sValue, aDF, ex);
      }
    return null;
  }

  /**
   * Parse an {@link XMLOffsetDateTime} from the given string using the
   * specified pattern.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param sPattern
   *        The pattern to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static XMLOffsetDateTime getXMLOffsetDateTimeFromString (@Nullable final String sValue,
                                                                  @NonNull final String sPattern)
  {
    return getXMLOffsetDateTimeFromString (sValue, PDTFormatter.getForPattern (sPattern, null));
  }

  /**
   * Parse an {@link XMLOffsetDateTime} from the given string using the
   * specified formatter.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param aDF
   *        The formatter to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static XMLOffsetDateTime getXMLOffsetDateTimeFromString (@Nullable final String sValue,
                                                                  @NonNull final DateTimeFormatter aDF)
  {
    ValueEnforcer.notNull (aDF, "DateTimeFormatter");

    if (StringHelper.isNotEmpty (sValue))
      try
      {
        return aDF.parse (sValue, XMLOffsetDateTime::from);
      }
      catch (final DateTimeParseException ex)
      {
        _onParseException ("XMLOffsetDateTime", sValue, aDF, ex);
      }
    return null;
  }

  /**
   * Parse a {@link LocalDate} from the given string using a locale-specific
   * default formatter.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param aParseLocale
   *        The locale to use for determining the date format. May be
   *        <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static LocalDate getLocalDateFromString (@Nullable final String sValue, @Nullable final Locale aParseLocale)
  {
    return getLocalDateFromString (sValue,
                                   PDTFormatter.getFormatterDate (PDTFormatter.DEFAULT_STYLE,
                                                                  aParseLocale,
                                                                  EDTFormatterMode.PARSE));
  }

  /**
   * Parse a {@link LocalDate} from the given string using the specified
   * formatter.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param aDF
   *        The formatter to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static LocalDate getLocalDateFromString (@Nullable final String sValue, @NonNull final DateTimeFormatter aDF)
  {
    ValueEnforcer.notNull (aDF, "DateTimeFormatter");

    if (StringHelper.isNotEmpty (sValue))
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

  /**
   * Parse a {@link LocalDate} from the given string using the specified
   * pattern.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param sPattern
   *        The pattern to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static LocalDate getLocalDateFromString (@Nullable final String sValue, @NonNull final String sPattern)
  {
    return getLocalDateFromString (sValue, PDTFormatter.getForPattern (sPattern, null));
  }

  /**
   * Parse an {@link OffsetDate} from the given string using the specified
   * formatter.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param aDF
   *        The formatter to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static OffsetDate getOffsetDateFromString (@Nullable final String sValue, @NonNull final DateTimeFormatter aDF)
  {
    ValueEnforcer.notNull (aDF, "DateTimeFormatter");

    if (StringHelper.isNotEmpty (sValue))
      try
      {
        return aDF.parse (sValue, OffsetDate::from);
      }
      catch (final DateTimeParseException ex)
      {
        _onParseException ("OffsetDate", sValue, aDF, ex);
      }
    return null;
  }

  /**
   * Parse an {@link OffsetDate} from the given string using the specified
   * pattern.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param sPattern
   *        The pattern to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static OffsetDate getOffsetDateFromString (@Nullable final String sValue, @NonNull final String sPattern)
  {
    return getOffsetDateFromString (sValue, PDTFormatter.getForPattern (sPattern, null));
  }

  /**
   * Parse an {@link XMLOffsetDate} from the given string using the specified
   * formatter.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param aDF
   *        The formatter to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static XMLOffsetDate getXMLOffsetDateFromString (@Nullable final String sValue,
                                                          @NonNull final DateTimeFormatter aDF)
  {
    ValueEnforcer.notNull (aDF, "DateTimeFormatter");

    if (StringHelper.isNotEmpty (sValue))
      try
      {
        return aDF.parse (sValue, XMLOffsetDate::from);
      }
      catch (final DateTimeParseException ex)
      {
        _onParseException ("XMLOffsetDate", sValue, aDF, ex);
      }
    return null;
  }

  /**
   * Parse an {@link XMLOffsetDate} from the given string using the specified
   * pattern.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param sPattern
   *        The pattern to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static XMLOffsetDate getXMLOffsetDateFromString (@Nullable final String sValue, @NonNull final String sPattern)
  {
    return getXMLOffsetDateFromString (sValue, PDTFormatter.getForPattern (sPattern, null));
  }

  /**
   * Parse a {@link LocalDateTime} from the given string using a locale-specific
   * default formatter.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param aParseLocale
   *        The locale to use for determining the date time format. May be
   *        <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static LocalDateTime getLocalDateTimeFromString (@Nullable final String sValue,
                                                          @Nullable final Locale aParseLocale)
  {
    return getLocalDateTimeFromString (sValue,
                                       PDTFormatter.getFormatterDateTime (PDTFormatter.DEFAULT_STYLE,
                                                                          aParseLocale,
                                                                          EDTFormatterMode.PARSE));
  }

  /**
   * Parse a {@link LocalDateTime} from the given string using the specified
   * formatter.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param aDF
   *        The formatter to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static LocalDateTime getLocalDateTimeFromString (@Nullable final String sValue,
                                                          @NonNull final DateTimeFormatter aDF)
  {
    ValueEnforcer.notNull (aDF, "DateTimeFormatter");

    if (StringHelper.isNotEmpty (sValue))
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

  /**
   * Parse a {@link LocalDateTime} from the given string using the specified
   * pattern.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param sPattern
   *        The pattern to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static LocalDateTime getLocalDateTimeFromString (@Nullable final String sValue, @NonNull final String sPattern)
  {
    return getLocalDateTimeFromString (sValue, PDTFormatter.getForPattern (sPattern, null));
  }

  /**
   * Parse a {@link LocalTime} from the given string using a locale-specific
   * default formatter.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param aParseLocale
   *        The locale to use for determining the time format. May be
   *        <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static LocalTime getLocalTimeFromString (@Nullable final String sValue, @Nullable final Locale aParseLocale)
  {
    return getLocalTimeFromString (sValue,
                                   PDTFormatter.getFormatterTime (PDTFormatter.DEFAULT_STYLE,
                                                                  aParseLocale,
                                                                  EDTFormatterMode.PARSE));
  }

  /**
   * Parse a {@link LocalTime} from the given string using the specified
   * pattern.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param sPattern
   *        The pattern to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static LocalTime getLocalTimeFromString (@Nullable final String sValue, @NonNull final String sPattern)
  {
    return getLocalTimeFromString (sValue, PDTFormatter.getForPattern (sPattern, null));
  }

  /**
   * Parse a {@link LocalTime} from the given string using the specified
   * formatter.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param aDF
   *        The formatter to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static LocalTime getLocalTimeFromString (@Nullable final String sValue, @NonNull final DateTimeFormatter aDF)
  {
    ValueEnforcer.notNull (aDF, "DateTimeFormatter");

    if (StringHelper.isNotEmpty (sValue))
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

  /**
   * Parse an {@link OffsetTime} from the given string using a locale-specific
   * default formatter.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param aParseLocale
   *        The locale to use for determining the time format. May be
   *        <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static OffsetTime getOffsetTimeFromString (@Nullable final String sValue, @Nullable final Locale aParseLocale)
  {
    return getOffsetTimeFromString (sValue,
                                    PDTFormatter.getFormatterTime (PDTFormatter.DEFAULT_STYLE,
                                                                   aParseLocale,
                                                                   EDTFormatterMode.PARSE));
  }

  /**
   * Parse an {@link OffsetTime} from the given string using the specified
   * formatter.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param aDF
   *        The formatter to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static OffsetTime getOffsetTimeFromString (@Nullable final String sValue, @NonNull final DateTimeFormatter aDF)
  {
    ValueEnforcer.notNull (aDF, "DateTimeFormatter");

    if (StringHelper.isNotEmpty (sValue))
      try
      {
        return aDF.parse (sValue, OffsetTime::from);
      }
      catch (final DateTimeParseException ex)
      {
        _onParseException ("OffsetTime", sValue, aDF, ex);
      }
    return null;
  }

  /**
   * Parse an {@link OffsetTime} from the given string using the specified
   * pattern.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param sPattern
   *        The pattern to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static OffsetTime getOffsetTimeFromString (@Nullable final String sValue, @NonNull final String sPattern)
  {
    return getOffsetTimeFromString (sValue, PDTFormatter.getForPattern (sPattern, null));
  }

  /**
   * Parse an {@link XMLOffsetTime} from the given string using the specified
   * formatter.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param aDF
   *        The formatter to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static XMLOffsetTime getXMLOffsetTimeFromString (@Nullable final String sValue,
                                                          @NonNull final DateTimeFormatter aDF)
  {
    ValueEnforcer.notNull (aDF, "DateTimeFormatter");

    if (StringHelper.isNotEmpty (sValue))
      try
      {
        return aDF.parse (sValue, XMLOffsetTime::from);
      }
      catch (final DateTimeParseException ex)
      {
        _onParseException ("XMLOffsetTime", sValue, aDF, ex);
      }
    return null;
  }

  /**
   * Parse an {@link XMLOffsetTime} from the given string using the specified
   * pattern.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param sPattern
   *        The pattern to use for parsing. May not be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code>, empty or
   *         cannot be parsed.
   */
  @Nullable
  public static XMLOffsetTime getXMLOffsetTimeFromString (@Nullable final String sValue, @NonNull final String sPattern)
  {
    return getXMLOffsetTimeFromString (sValue, PDTFormatter.getForPattern (sPattern, null));
  }

  /**
   * Parse a {@link Duration} from the given ISO-8601 duration string.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code> or cannot be
   *         parsed.
   */
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

  /**
   * Parse a {@link Period} from the given ISO-8601 period string.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @return <code>null</code> if the value is <code>null</code> or cannot be
   *         parsed.
   */
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
