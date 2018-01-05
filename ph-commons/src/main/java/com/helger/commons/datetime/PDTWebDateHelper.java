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

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MILLI_OF_SECOND;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.Temporal;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.string.StringHelper;
import com.helger.commons.typeconvert.TypeConverter;

/**
 * A helper class that parses Dates out of Strings with date time in RFC822 and
 * W3CDateTime formats plus the variants Atom (0.3) and RSS (0.9, 0.91, 0.92,
 * 0.93, 0.94, 1.0 and 2.0) specificators added to those formats.<br>
 * It uses the JDK java.text.SimpleDateFormat class attempting the parse using a
 * mask for each one of the possible formats.<br>
 * Original work Copyright 2004 Sun Microsystems, Inc.
 *
 * @author Alejandro Abdelnur (original; mainly the formatting masks)
 * @author Philip Helger (major modification)
 */
@Immutable
public final class PDTWebDateHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (PDTWebDateHelper.class);
  // "XXX" means "+HH:mm"
  // "XX" means "+HHmm"
  private static final String ZONE_PATTERN1 = "XXX";
  private static final String ZONE_PATTERN2 = "XX";
  private static final String FORMAT_RFC822 = "EEE, dd MMM uuuu HH:mm:ss 'GMT'";
  private static final String FORMAT_W3C = "uuuu-MM-dd'T'HH:mm:ss" + ZONE_PATTERN1;

  /**
   * order is like this because the SimpleDateFormat.parse does not fail with
   * exception if it can parse a valid date out of a substring of the full
   * string given the mask so we have to check the most complete format first,
   * then it fails with exception. <br>
   * RFC 1123 superseding 822 recommends to use yyyy instead of yy<br>
   * Because of strict formatting "uuuu" (year) must be used instead of "yyyy"
   * (year of era)
   */
  private static final PDTMask <?> [] RFC822_MASKS = { PDTMask.zonedDateTime (FORMAT_RFC822),
                                                       PDTMask.zonedDateTime ("EEE, dd MMM uuuu HH:mm:ss " +
                                                                              ZONE_PATTERN2),
                                                       PDTMask.localDateTime ("EEE, dd MMM uuuu HH:mm:ss"),
                                                       PDTMask.localDateTime ("EEE, dd MMM uu HH:mm:ss"),
                                                       PDTMask.localDateTime ("EEE, dd MMM uuuu HH:mm"),
                                                       PDTMask.localDateTime ("EEE, dd MMM uu HH:mm"),
                                                       PDTMask.localDateTime ("dd MMM uuuu HH:mm:ss"),
                                                       PDTMask.localDateTime ("dd MMM uu HH:mm:ss"),
                                                       PDTMask.localDateTime ("dd MMM uuuu HH:mm"),
                                                       PDTMask.localDateTime ("dd MMM uu HH:mm") };

  /*
   * order is like this because the SimpleDateFormat.parse does not fail with
   * exception if it can parse a valid date out of a substring of the full
   * string given the mask so we have to check the most complete format first,
   * then it fails with exception
   */
  private static final PDTMask <?> [] W3CDATETIME_MASKS = { PDTMask.offsetDateTime ("uuuu-MM-dd'T'HH:mm:ss.SSS" +
                                                                                    ZONE_PATTERN1),
                                                            PDTMask.offsetDateTime ("uuuu-MM-dd'T'HH:mm:ss.SSS" +
                                                                                    ZONE_PATTERN2),
                                                            PDTMask.offsetDateTime ("uuuu-MM-dd't'HH:mm:ss.SSS" +
                                                                                    ZONE_PATTERN1),
                                                            PDTMask.offsetDateTime ("uuuu-MM-dd't'HH:mm:ss.SSS" +
                                                                                    ZONE_PATTERN2),
                                                            PDTMask.localDateTime ("uuuu-MM-dd'T'HH:mm:ss.SSS"),
                                                            PDTMask.localDateTime ("uuuu-MM-dd't'HH:mm:ss.SSS"),
                                                            PDTMask.offsetDateTime (FORMAT_W3C),
                                                            PDTMask.offsetDateTime ("uuuu-MM-dd'T'HH:mm:ss" +
                                                                                    ZONE_PATTERN2),
                                                            PDTMask.offsetDateTime ("uuuu-MM-dd't'HH:mm:ss" +
                                                                                    ZONE_PATTERN1),
                                                            PDTMask.offsetDateTime ("uuuu-MM-dd't'HH:mm:ss" +
                                                                                    ZONE_PATTERN2),
                                                            PDTMask.localDateTime ("uuuu-MM-dd'T'HH:mm:ss"),
                                                            PDTMask.localDateTime ("uuuu-MM-dd't'HH:mm:ss"),
                                                            PDTMask.offsetDateTime ("uuuu-MM-dd'T'HH:mm" +
                                                                                    ZONE_PATTERN1),
                                                            PDTMask.offsetDateTime ("uuuu-MM-dd'T'HH:mm" +
                                                                                    ZONE_PATTERN2),
                                                            PDTMask.offsetDateTime ("uuuu-MM-dd't'HH:mm" +
                                                                                    ZONE_PATTERN1),
                                                            PDTMask.offsetDateTime ("uuuu-MM-dd't'HH:mm" +
                                                                                    ZONE_PATTERN2),
                                                            PDTMask.localDateTime ("uuuu-MM-dd'T'HH:mm"),
                                                            PDTMask.localDateTime ("uuuu-MM-dd't'HH:mm"),
                                                            /*
                                                             * Applies to the
                                                             * following 2:
                                                             * together with
                                                             * logic in the
                                                             * parseW3CDateTime
                                                             * they handle W3C
                                                             * dates without
                                                             * time forcing them
                                                             * to be GMT
                                                             */
                                                            PDTMask.localDateTime ("uuuu-MM'T'HH:mm"),
                                                            PDTMask.localDateTime ("uuuu'T'HH:mm"),
                                                            PDTMask.localDate ("uuuu-MM-dd"),
                                                            PDTMask.yearMonth ("uuuu-MM"),
                                                            PDTMask.year ("uuuu") };

  private static final Locale LOCALE_TO_USE = Locale.US;

  @PresentForCodeCoverage
  private static final PDTWebDateHelper s_aInstance = new PDTWebDateHelper ();

  private PDTWebDateHelper ()
  {}

  /**
   * Parses a Date out of a string using an array of masks. It uses the masks in
   * order until one of them succeeds or all fail.
   *
   * @param aMasks
   *        array of masks to use for parsing the string
   * @param sDate
   *        string to parse for a date.
   * @return the Date represented by the given string using one of the given
   *         masks. It returns <b>null</b> if it was not possible to parse the
   *         the string with any of the masks.
   */
  @Nullable
  public static OffsetDateTime parseOffsetDateTimeUsingMask (@Nonnull final PDTMask <?> [] aMasks,
                                                             @Nonnull @Nonempty final String sDate)
  {
    for (final PDTMask <?> aMask : aMasks)
    {
      final DateTimeFormatter aDTF = PDTFormatter.getForPattern (aMask.getPattern (), LOCALE_TO_USE);
      try
      {
        final Temporal ret = aDTF.parse (sDate, aMask.getQuery ());
        if (s_aLogger.isDebugEnabled ())
          s_aLogger.debug ("Parsed '" +
                           sDate +
                           "' with '" +
                           aMask.getPattern () +
                           "' to " +
                           ret.getClass ().getName ());
        return TypeConverter.convert (ret, OffsetDateTime.class);
      }
      catch (final DateTimeParseException ex)
      {
        if (s_aLogger.isDebugEnabled ())
          s_aLogger.debug ("Failed to parse '" + sDate + "' with '" + aMask.getPattern () + "': " + ex.getMessage ());
      }
    }
    return null;
  }

  /**
   * Parses a Date out of a string using an array of masks. It uses the masks in
   * order until one of them succeeds or all fail.
   *
   * @param aMasks
   *        array of masks to use for parsing the string
   * @param sDate
   *        string to parse for a date.
   * @param aDTZ
   *        The date/time zone to use. Optional.
   * @return the Date represented by the given string using one of the given
   *         masks. It returns <b>null</b> if it was not possible to parse the
   *         the string with any of the masks.
   */
  @Nullable
  public static ZonedDateTime parseZonedDateTimeUsingMask (@Nonnull final PDTMask <?> [] aMasks,
                                                           @Nonnull @Nonempty final String sDate,
                                                           @Nullable final ZoneId aDTZ)
  {
    for (final PDTMask <?> aMask : aMasks)
    {
      DateTimeFormatter aDTF = PDTFormatter.getForPattern (aMask.getPattern (), LOCALE_TO_USE);
      if (aDTZ != null)
        aDTF = aDTF.withZone (aDTZ);
      try
      {
        final Temporal ret = aDTF.parse (sDate, aMask.getQuery ());
        if (s_aLogger.isDebugEnabled ())
          s_aLogger.debug ("Parsed '" +
                           sDate +
                           "' with '" +
                           aMask.getPattern () +
                           "' to " +
                           ret.getClass ().getName ());
        return TypeConverter.convert (ret, ZonedDateTime.class);
      }
      catch (final DateTimeParseException ex)
      {
        if (s_aLogger.isDebugEnabled ())
          s_aLogger.debug ("Failed to parse '" + sDate + "' with '" + aMask.getPattern () + "': " + ex.getMessage ());
      }
    }
    return null;
  }

  /**
   * Extract the time zone from the passed string. UTC and GMT are supported.
   *
   * @param sDate
   *        The date string.
   * @return A non-<code>null</code> {@link WithZoneId}, where the remaining
   *         string to be parsed (never <code>null</code>) and and the extracted
   *         time zone (may be <code>null</code>) are contained.
   */
  @Nonnull
  public static WithZoneId extractDateTimeZone (@Nonnull final String sDate)
  {
    ValueEnforcer.notNull (sDate, "Date");

    final int nDateLen = sDate.length ();
    for (final PDTZoneID aSupp : PDTZoneID.getDefaultZoneIDs ())
    {
      final String sDTZ = aSupp.getZoneIDString ();
      if (sDate.endsWith (" " + sDTZ))
        return new WithZoneId (sDate.substring (0, nDateLen - (1 + sDTZ.length ())), aSupp.getZoneID ());
      if (sDate.endsWith (sDTZ))
        return new WithZoneId (sDate.substring (0, nDateLen - sDTZ.length ()), aSupp.getZoneID ());
    }
    return new WithZoneId (sDate, null);
  }

  /**
   * Parses a Date out of a String with a date in RFC822 format. <br>
   * It parsers the following formats:
   * <ul>
   * <li>"EEE, dd MMM uuuu HH:mm:ss z"</li>
   * <li>"EEE, dd MMM uuuu HH:mm z"</li>
   * <li>"EEE, dd MMM uu HH:mm:ss z"</li>
   * <li>"EEE, dd MMM uu HH:mm z"</li>
   * <li>"dd MMM uuuu HH:mm:ss z"</li>
   * <li>"dd MMM uuuu HH:mm z"</li>
   * <li>"dd MMM uu HH:mm:ss z"</li>
   * <li>"dd MMM uu HH:mm z"</li>
   * </ul>
   * <p>
   * Refer to the java.text.SimpleDateFormat javadocs for details on the format
   * of each element.
   * </p>
   *
   * @param sDate
   *        string to parse for a date. May be <code>null</code>.
   * @return the Date represented by the given RFC822 string. It returns
   *         <b>null</b> if it was not possible to parse the given string into a
   *         {@link ZonedDateTime} or if the passed {@link String} was
   *         <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime getDateTimeFromRFC822 (@Nullable final String sDate)
  {
    if (StringHelper.hasNoText (sDate))
      return null;

    final WithZoneId aPair = extractDateTimeZone (sDate.trim ());
    return parseZonedDateTimeUsingMask (RFC822_MASKS, aPair.getString (), aPair.getZoneId ());
  }

  /**
   * Parses a Date out of a String with a date in W3C date-time format. <br>
   * It parsers the following formats:
   * <ul>
   * <li>"uuuu-MM-dd'T'HH:mm:ssz"</li>
   * <li>"uuuu-MM-dd'T'HH:mmz"</li>
   * <li>"uuuu-MM-dd"</li>
   * <li>"uuuu-MM"</li>
   * <li>"uuuu"</li>
   * </ul>
   * <p>
   * Refer to the java.text.SimpleDateFormat javadocs for details on the format
   * of each element.
   * </p>
   *
   * @param sDate
   *        string to parse for a date. May be <code>null</code>.
   * @return the Date represented by the given W3C date-time string. It returns
   *         <b>null</b> if it was not possible to parse the given string into a
   *         {@link ZonedDateTime} or if the input string was <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime getDateTimeFromW3C (@Nullable final String sDate)
  {
    if (StringHelper.hasNoText (sDate))
      return null;

    return parseOffsetDateTimeUsingMask (W3CDATETIME_MASKS, sDate.trim ());
  }

  /**
   * Parses a Date out of a String with a date in W3C date-time format or in a
   * RFC822 format.
   *
   * @param sDate
   *        string to parse for a date.
   * @return the Date represented by the given W3C date-time string. It returns
   *         <b>null</b> if it was not possible to parse the given string into a
   *         Date.
   */
  @Nullable
  public static ZonedDateTime getDateTimeFromW3COrRFC822 (@Nullable final String sDate)
  {
    final OffsetDateTime aDateTime = getDateTimeFromW3C (sDate);
    if (aDateTime != null)
      return aDateTime.toZonedDateTime ();

    return getDateTimeFromRFC822 (sDate);
  }

  /**
   * Parses a Date out of a String with a date in W3C date-time format or in a
   * RFC822 format.
   *
   * @param sDate
   *        string to parse for a date.
   * @return the Date represented by the given W3C date-time string. It returns
   *         <b>null</b> if it was not possible to parse the given string into a
   *         Date.
   */
  @Nullable
  public static LocalDateTime getLocalDateTimeFromW3COrRFC822 (@Nullable final String sDate)
  {
    final ZonedDateTime aDateTime = getDateTimeFromW3COrRFC822 (sDate);
    return aDateTime == null ? null : aDateTime.toLocalDateTime ();
  }

  /**
   * create a RFC822 representation of a date.
   *
   * @param aDateTime
   *        Date to print. May be <code>null</code>.
   * @return the RFC822 represented by the given Date. <code>null</code> if the
   *         parameter is <code>null</code>.
   */
  @Nullable
  public static String getAsStringRFC822 (@Nullable final ZonedDateTime aDateTime)
  {
    if (aDateTime == null)
      return null;
    return PDTFormatter.getForPattern (FORMAT_RFC822, LOCALE_TO_USE).format (aDateTime);
  }

  /**
   * create a RFC822 representation of a date.
   *
   * @param aDateTime
   *        Date to print. May be <code>null</code>.
   * @return the RFC822 represented by the given Date. <code>null</code> if the
   *         parameter is <code>null</code>.
   */
  @Nullable
  public static String getAsStringRFC822 (@Nullable final OffsetDateTime aDateTime)
  {
    if (aDateTime == null)
      return null;
    return getAsStringRFC822 (aDateTime.toZonedDateTime ());
  }

  /**
   * create a RFC822 representation of a date time using UTC date time zone.
   *
   * @param aDateTime
   *        Date to print. May be <code>null</code>.
   * @return the RFC822 represented by the given Date. <code>null</code> if the
   *         parameter is <code>null</code>.
   */
  @Nullable
  public static String getAsStringRFC822 (@Nullable final LocalDateTime aDateTime)
  {
    if (aDateTime == null)
      return null;
    return getAsStringRFC822 (aDateTime.atOffset (ZoneOffset.UTC));
  }

  /**
   * create a W3C Date Time representation of a date time using UTC date time
   * zone.
   *
   * @param aDateTime
   *        Date to print. May not be <code>null</code>.
   * @return the W3C Date Time represented by the given Date.
   */
  @Nullable
  public static String getAsStringW3C (@Nullable final ZonedDateTime aDateTime)
  {
    if (aDateTime == null)
      return null;
    final DateTimeFormatter aFormatter = PDTFormatter.getForPattern (FORMAT_W3C, LOCALE_TO_USE);
    return aFormatter.format (aDateTime);
  }

  /**
   * create a W3C Date Time representation of a date time using UTC date time
   * zone.
   *
   * @param aDateTime
   *        Date to print. May not be <code>null</code>.
   * @return the W3C Date Time represented by the given Date.
   */
  @Nullable
  public static String getAsStringW3C (@Nullable final OffsetDateTime aDateTime)
  {
    if (aDateTime == null)
      return null;
    return getAsStringW3C (aDateTime.toZonedDateTime ());
  }

  /**
   * create a W3C Date Time representation of a date.
   *
   * @param aDateTime
   *        Date to print. May not be <code>null</code>.
   * @return the W3C Date Time represented by the given Date.
   */
  @Nullable
  public static String getAsStringW3C (@Nullable final LocalDateTime aDateTime)
  {
    if (aDateTime == null)
      return null;
    return getAsStringW3C (aDateTime.atOffset (ZoneOffset.UTC));
  }

  /**
   * @return The current date time formatted using RFC 822
   */
  @Nonnull
  public static String getCurrentDateTimeAsStringRFC822 ()
  {
    // Important to use date time zone GMT as this is what the standard
    // printer emits!
    // Use no milliseconds as the standard printer does not print them!
    final ZonedDateTime aNow = ZonedDateTime.now (Clock.systemUTC ()).withNano (0);
    return getAsStringRFC822 (aNow);
  }

  /**
   * @return The current date time formatted using W3C format
   */
  @Nonnull
  public static String getCurrentDateTimeAsStringW3C ()
  {
    // Use no milli seconds as the standard printer does not print them!
    final ZonedDateTime aNow = PDTFactory.getCurrentZonedDateTime ().withNano (0);
    return getAsStringW3C (aNow);
  }

  public static final DateTimeFormatter XSD_DATE_TIME;

  static
  {
    XSD_DATE_TIME = new DateTimeFormatterBuilder ().parseCaseInsensitive ()
                                                   .append (DateTimeFormatter.ISO_LOCAL_DATE)
                                                   .appendLiteral ('T')
                                                   .appendValue (HOUR_OF_DAY, 2)
                                                   .appendLiteral (':')
                                                   .appendValue (MINUTE_OF_HOUR, 2)
                                                   .optionalStart ()
                                                   .appendLiteral (':')
                                                   .appendValue (SECOND_OF_MINUTE, 2)
                                                   .optionalStart ()
                                                   /*
                                                    * This is different compared
                                                    * to ISO_LOCAL_TIME
                                                    */
                                                   .appendFraction (MILLI_OF_SECOND, 3, 3, true)
                                                   .optionalStart ()
                                                   .appendOffsetId ()
                                                   .optionalStart ()
                                                   .appendLiteral ('[')
                                                   .parseCaseSensitive ()
                                                   .appendZoneRegionId ()
                                                   .appendLiteral (']')
                                                   .toFormatter ()
                                                   .withResolverStyle (ResolverStyle.STRICT)
                                                   .withChronology (IsoChronology.INSTANCE);
  }

  @Nonnull
  public static DateTimeFormatter getXSDFormatterDateTime (@Nonnull final ZoneId aZoneID)
  {
    return XSD_DATE_TIME.withZone (aZoneID);
  }

  @Nullable
  public static ZonedDateTime getDateTimeFromXSD (@Nullable final String sValue)
  {
    return getDateTimeFromXSD (sValue, ZoneOffset.UTC);
  }

  @Nullable
  public static ZonedDateTime getDateTimeFromXSD (@Nullable final String sValue, @Nonnull final ZoneId aZoneID)
  {
    return PDTFromString.getZonedDateTimeFromString (sValue, getXSDFormatterDateTime (aZoneID));
  }

  @Nullable
  public static LocalDateTime getLocalDateTimeFromXSD (@Nullable final String sValue)
  {
    // For LocalDateTime always use the default chronology
    return PDTFromString.getLocalDateTimeFromString (sValue, getXSDFormatterDateTime (ZoneOffset.UTC));
  }

  @Nullable
  public static String getAsStringXSD (@Nullable final ZonedDateTime aZDT)
  {
    return aZDT == null ? null : getAsStringXSD (aZDT.getZone (), aZDT);
  }

  @Nullable
  public static String getAsStringXSD (@Nonnull final ZoneId aZoneID, @Nullable final ZonedDateTime aZDT)
  {
    return aZDT == null ? null : getXSDFormatterDateTime (aZoneID).format (aZDT);
  }

  @Nullable
  public static String getAsStringXSD (@Nullable final LocalDateTime aLDT)
  {
    // For LocalDateTime always use the default zone ID
    return aLDT == null ? null : getXSDFormatterDateTime (PDTConfig.getDefaultZoneId ()).format (aLDT);
  }

  @Nonnull
  public static DateTimeFormatter getXSDFormatterDate ()
  {
    return DateTimeFormatter.ISO_DATE.withZone (ZoneOffset.UTC);
  }

  @Nullable
  public static LocalDate getLocalDateFromXSD (@Nullable final String sValue)
  {
    return PDTFromString.getLocalDateFromString (sValue, getXSDFormatterDate ());
  }

  @Nullable
  public static String getAsStringXSD (@Nullable final LocalDate aLD)
  {
    return aLD == null ? null : getXSDFormatterDate ().format (aLD);
  }
}
