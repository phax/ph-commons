/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.datatype.XMLGregorianCalendar;

import com.helger.commons.CGlobal;

/**
 * Philip's Date Time (PDT) factory. Create and convert different date and time
 * stuff into each other. Everything that creates a {@link ZonedDateTime},
 * {@link OffsetDateTime}, {@link OffsetDate} or {@link OffsetTime} uses the
 * default time zone from {@link PDTConfig} if necessary. The addition "UTC" to
 * the name of the method indicates the explicit usage of the UTC time zone.
 *
 * @author Philip Helger
 */
@Immutable
public final class PDTFactory
{
  private PDTFactory ()
  {}

  @Nonnull
  private static ZoneId _getZoneId ()
  {
    return PDTConfig.getDefaultZoneId ();
  }

  /**
   * Get the time zone offset to UTC of the passed Date in minutes to be used in
   * {@link XMLGregorianCalendar}.
   *
   * @param a
   *        The date to use. May not be <code>null</code>.
   * @return 0 for no offset to UTC, the minutes otherwise. Usually in 60minutes
   *         steps :)
   */
  @SuppressWarnings ("deprecation")
  public static int getTimezoneOffsetInMinutes (@Nonnull final Date a)
  {
    return -a.getTimezoneOffset ();
  }

  public static int getTimezoneOffsetInMinutes (@Nonnull final GregorianCalendar aCal)
  {
    final long nOffsetMillis = aCal.getTimeZone ().getRawOffset ();
    return Math.toIntExact (nOffsetMillis / CGlobal.MILLISECONDS_PER_MINUTE);
  }

  public static int getTimezoneOffsetInMinutes (@Nonnull final ZoneId aZID, @Nonnull final Instant aAt)
  {
    final ZoneOffset aZO = aZID.getRules ().getStandardOffset (aAt);
    return getTimezoneOffsetInMinutes (aZO);
  }

  public static int getTimezoneOffsetInMinutes (@Nonnull final ZoneOffset aZO)
  {
    return aZO.getTotalSeconds () / CGlobal.SECONDS_PER_MINUTE;
  }

  @Nonnull
  public static ZoneOffset getZoneOffsetFromOffsetInMinutes (final int nOffsetInMinutes)
  {
    return ZoneOffset.ofHoursMinutes (nOffsetInMinutes / CGlobal.MINUTES_PER_HOUR,
                                      nOffsetInMinutes % CGlobal.MINUTES_PER_HOUR);
  }

  @Nonnull
  public static ZoneOffset getZoneOffset (@Nonnull final Date a)
  {
    final int nOffsetMin = getTimezoneOffsetInMinutes (a);
    return getZoneOffsetFromOffsetInMinutes (nOffsetMin);
  }

  @Nonnull
  public static ZoneId getZoneIdFromOffsetInMinutes (final int nOffsetInMinutes)
  {
    final ZoneOffset aZO = getZoneOffsetFromOffsetInMinutes (nOffsetInMinutes);
    // Empty prefix means "no special"
    return ZoneId.ofOffset ("", aZO);
  }

  @Nonnull
  public static ZoneId getZoneId (@Nonnull final Date a)
  {
    final int nOffsetMin = getTimezoneOffsetInMinutes (a);
    return getZoneIdFromOffsetInMinutes (nOffsetMin);
  }

  @Nonnull
  public static TimeZone getTimeZone (@Nonnull final Date a)
  {
    final ZoneId aZI = getZoneId (a);
    return TimeZone.getTimeZone (aZI);
  }

  // To ZonedDateTime

  @Nonnull
  public static ZonedDateTime getCurrentZonedDateTime ()
  {
    return ZonedDateTime.now (_getZoneId ());
  }

  @Nonnull
  public static ZonedDateTime getCurrentZonedDateTimeUTC ()
  {
    return ZonedDateTime.now (ZoneOffset.UTC);
  }

  /**
   * Get the passed date time but with micro and nanoseconds set to 0, so that
   * only the milliseconds part is present. This is helpful for XSD
   * serialization, where only milliseconds granularity is available.
   *
   * @param a
   *        Source date time. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>, the local
   *         date time with microseconds and nanoseconds set to 0 otherwise.
   * @since 9.2.0
   */
  @Nullable
  public static ZonedDateTime getWithMillisOnly (@Nullable final ZonedDateTime a)
  {
    return a == null ? null : a.truncatedTo (ChronoUnit.MILLIS);
  }

  /**
   * @return The current local date and time but with micro and nanoseconds set
   *         to 0, so that only the milliseconds part is present. This is
   *         helpful for XSD serialization, where only milliseconds granularity
   *         is available.
   * @since 9.2.0
   */
  @Nonnegative
  public static ZonedDateTime getCurrentZonedDateTimeMillisOnly ()
  {
    return getWithMillisOnly (getCurrentZonedDateTime ());
  }

  /**
   * @return The current local date and time but with micro and nanoseconds set
   *         to 0, so that only the milliseconds part is present. This is
   *         helpful for XSD serialization, where only milliseconds granularity
   *         is available.
   */
  @Nonnegative
  public static ZonedDateTime getCurrentZonedDateTimeMillisOnlyUTC ()
  {
    return getWithMillisOnly (getCurrentZonedDateTimeUTC ());
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final OffsetDateTime a)
  {
    return a == null ? null : a.toZonedDateTime ();
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final LocalDateTime a)
  {
    return a == null ? null : ZonedDateTime.of (a, _getZoneId ());
  }

  @Nullable
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final LocalDateTime a)
  {
    return a == null ? null : ZonedDateTime.of (a, ZoneOffset.UTC);
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final LocalDate a)
  {
    return a == null ? null : ZonedDateTime.of (a.atStartOfDay (), _getZoneId ());
  }

  @Nullable
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final LocalDate a)
  {
    return a == null ? null : ZonedDateTime.of (a.atStartOfDay (), ZoneOffset.UTC);
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final OffsetDate aOD)
  {
    return aOD == null ? null : ZonedDateTime.of (aOD.toLocalDate ().atStartOfDay (), aOD.getOffset ());
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final XMLOffsetDate aOD)
  {
    return aOD == null ? null : ZonedDateTime.of (aOD.toLocalDate ().atStartOfDay (),
                                                  aOD.hasOffset () ? aOD.getOffset () : _getZoneId ());
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final YearMonth a)
  {
    return createZonedDateTime (createLocalDateTime (a));
  }

  @Nullable
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final YearMonth a)
  {
    return createZonedDateTimeUTC (createLocalDateTime (a));
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final Year a)
  {
    return createZonedDateTime (createLocalDateTime (a));
  }

  @Nullable
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final Year a)
  {
    return createZonedDateTimeUTC (createLocalDateTime (a));
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final LocalTime a)
  {
    return a == null ? null : ZonedDateTime.of (_toLocalDateTime (a), _getZoneId ());
  }

  @Nullable
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final LocalTime a)
  {
    return a == null ? null : ZonedDateTime.of (_toLocalDateTime (a), ZoneOffset.UTC);
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final OffsetTime a)
  {
    return createZonedDateTime (createOffsetDateTime (a));
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final XMLOffsetTime a)
  {
    return createZonedDateTime (createOffsetDateTime (a));
  }

  @Nonnull
  public static ZonedDateTime createZonedDateTime (final int nYear, final Month eMonth, final int nDay)
  {
    return createZonedDateTime (createLocalDateTime (nYear, eMonth, nDay));
  }

  @Nonnull
  public static ZonedDateTime createZonedDateTimeUTC (final int nYear, final Month eMonth, final int nDay)
  {
    return createZonedDateTimeUTC (createLocalDateTime (nYear, eMonth, nDay));
  }

  @Nonnull
  public static ZonedDateTime createZonedDateTime (final int nYear,
                                                   @Nonnull final Month eMonth,
                                                   final int nDay,
                                                   final int nHour,
                                                   final int nMinute,
                                                   final int nSecond)
  {
    return createZonedDateTime (createLocalDateTime (nYear, eMonth, nDay, nHour, nMinute, nSecond));
  }

  @Nonnull
  public static ZonedDateTime createZonedDateTimeUTC (final int nYear,
                                                      @Nonnull final Month eMonth,
                                                      final int nDay,
                                                      final int nHour,
                                                      final int nMinute,
                                                      final int nSecond)
  {
    return createZonedDateTimeUTC (createLocalDateTime (nYear, eMonth, nDay, nHour, nMinute, nSecond));
  }

  @Nonnull
  private static ZonedDateTime _toZonedDateTime (@Nonnull final Instant a)
  {
    return ZonedDateTime.ofInstant (a, _getZoneId ());
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final Instant a)
  {
    return a == null ? null : _toZonedDateTime (a);
  }

  @Nullable
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final Instant a)
  {
    return a == null ? null : ZonedDateTime.ofInstant (a, ZoneOffset.UTC);
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final GregorianCalendar aCal)
  {
    return aCal == null ? null : aCal.toZonedDateTime ();
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final Date a)
  {
    return a == null ? null : _toOffsetDateTime (a).toZonedDateTime ();
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final Timestamp a)
  {
    return createZonedDateTime (createOffsetDateTime (a));
  }

  @Nonnull
  public static ZonedDateTime createZonedDateTime (final long nMillis)
  {
    return _toZonedDateTime (Instant.ofEpochMilli (nMillis));
  }

  @Nonnull
  public static ZonedDateTime createZonedDateTimeUTC (final long nMillis)
  {
    return ZonedDateTime.ofInstant (Instant.ofEpochMilli (nMillis), ZoneOffset.UTC);
  }

  @Nonnull
  public static ZonedDateTime createZonedDateTime (@Nullable final Number a)
  {
    return a == null ? null : createZonedDateTime (a.longValue ());
  }

  @Nonnull
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final Number a)
  {
    return a == null ? null : createZonedDateTimeUTC (a.longValue ());
  }

  // To OffsetDateTime

  @Nonnegative
  public static OffsetDateTime getCurrentOffsetDateTime ()
  {
    return OffsetDateTime.now (_getZoneId ());
  }

  @Nonnegative
  public static OffsetDateTime getCurrentOffsetDateTimeUTC ()
  {
    return OffsetDateTime.now (ZoneOffset.UTC);
  }

  /**
   * Get the passed date time but with micro and nanoseconds set to 0, so that
   * only the milliseconds part is present. This is helpful for XSD
   * serialization, where only milliseconds granularity is available.
   *
   * @param a
   *        Source date time. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>, the local
   *         date time with microseconds and nanoseconds set to 0 otherwise.
   * @since 9.2.0
   */
  @Nullable
  public static OffsetDateTime getWithMillisOnly (@Nullable final OffsetDateTime a)
  {
    return a == null ? null : a.truncatedTo (ChronoUnit.MILLIS);
  }

  /**
   * @return The current local date and time but with micro and nanoseconds set
   *         to 0, so that only the milliseconds part is present. This is
   *         helpful for XSD serialization, where only milliseconds granularity
   *         is available.
   * @since 9.2.0
   */
  @Nonnegative
  public static OffsetDateTime getCurrentOffsetDateTimeMillisOnly ()
  {
    return getWithMillisOnly (getCurrentOffsetDateTime ());
  }

  /**
   * @return The current local date and time but with micro and nanoseconds set
   *         to 0, so that only the milliseconds part is present. This is
   *         helpful for XSD serialization, where only milliseconds granularity
   *         is available.
   */
  @Nonnegative
  public static OffsetDateTime getCurrentOffsetDateTimeMillisOnlyUTC ()
  {
    return getWithMillisOnly (getCurrentOffsetDateTimeUTC ());
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final ZonedDateTime a)
  {
    return a == null ? null : a.toOffsetDateTime ();
  }

  @Nullable
  private static OffsetDateTime _toOffsetDateTime (@Nonnull final LocalDateTime a)
  {
    return ZonedDateTime.of (a, _getZoneId ()).toOffsetDateTime ();
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final LocalDateTime a)
  {
    return a == null ? null : _toOffsetDateTime (a);
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final LocalDateTime a)
  {
    return a == null ? null : OffsetDateTime.of (a, ZoneOffset.UTC);
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final LocalDate a)
  {
    return a == null ? null : _toOffsetDateTime (a.atStartOfDay ());
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final LocalDate a)
  {
    return a == null ? null : createOffsetDateTimeUTC (a.atStartOfDay ());
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final OffsetDate aOD)
  {
    return aOD == null ? null : OffsetDateTime.of (aOD.toLocalDate ().atStartOfDay (), aOD.getOffset ());
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final XMLOffsetDate aOD)
  {
    return aOD == null ? null : createZonedDateTime (aOD).toOffsetDateTime ();
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final YearMonth a)
  {
    return createOffsetDateTime (createLocalDateTime (a));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final YearMonth a)
  {
    return createOffsetDateTimeUTC (createLocalDateTime (a));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final Year a)
  {
    return createOffsetDateTime (createLocalDateTime (a));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final Year a)
  {
    return createOffsetDateTimeUTC (createLocalDateTime (a));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final LocalTime a)
  {
    return a == null ? null : _toOffsetDateTime (_toLocalDateTime (a));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final LocalTime a)
  {
    return a == null ? null : createOffsetDateTimeUTC (_toLocalDateTime (a));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final OffsetTime a)
  {
    return a == null ? null : a.atDate (LocalDate.ofEpochDay (0));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final XMLOffsetTime a)
  {
    return a == null ? null : a.atDate (LocalDate.ofEpochDay (0));
  }

  @Nonnull
  public static OffsetDateTime createOffsetDateTime (final int nYear, final Month eMonth, final int nDay)
  {
    return _toOffsetDateTime (createLocalDate (nYear, eMonth, nDay).atStartOfDay ());
  }

  @Nonnull
  public static OffsetDateTime createOffsetDateTimeUTC (final int nYear, final Month eMonth, final int nDay)
  {
    return createOffsetDateTimeUTC (createLocalDate (nYear, eMonth, nDay).atStartOfDay ());
  }

  @Nonnull
  public static OffsetDateTime createOffsetDateTime (final int nYear,
                                                     @Nonnull final Month eMonth,
                                                     final int nDay,
                                                     final int nHour,
                                                     final int nMinute,
                                                     final int nSecond)
  {
    return _toOffsetDateTime (createLocalDateTime (nYear, eMonth, nDay, nHour, nMinute, nSecond));
  }

  @Nonnull
  public static OffsetDateTime createOffsetDateTime (final int nYear,
                                                     @Nonnull final Month eMonth,
                                                     final int nDay,
                                                     final int nHour,
                                                     final int nMinute,
                                                     final int nSecond,
                                                     @Nonnull final ZoneOffset aZoneOffset)
  {
    return OffsetDateTime.of (createLocalDateTime (nYear, eMonth, nDay, nHour, nMinute, nSecond), aZoneOffset);
  }

  @Nonnull
  public static OffsetDateTime createOffsetDateTimeUTC (final int nYear,
                                                        @Nonnull final Month eMonth,
                                                        final int nDay,
                                                        final int nHour,
                                                        final int nMinute,
                                                        final int nSecond)
  {
    return createOffsetDateTimeUTC (createLocalDateTime (nYear, eMonth, nDay, nHour, nMinute, nSecond));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final Instant a)
  {
    return a == null ? null : OffsetDateTime.ofInstant (a, _getZoneId ());
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final Instant a)
  {
    return a == null ? null : OffsetDateTime.ofInstant (a, ZoneOffset.UTC);
  }

  @Nonnull
  private static OffsetDateTime _toOffsetDateTime (@Nonnull final Date a)
  {
    return a.toInstant ().atOffset (getZoneOffset (a));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final Date a)
  {
    return a == null ? null : _toOffsetDateTime (a);
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final Timestamp a)
  {
    return a == null ? null : _toOffsetDateTime (a);
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final GregorianCalendar aCal)
  {
    return aCal == null ? null : aCal.toZonedDateTime ().toOffsetDateTime ();
  }

  @Nonnull
  public static OffsetDateTime createOffsetDateTime (final long nMillis)
  {
    return OffsetDateTime.ofInstant (Instant.ofEpochMilli (nMillis), _getZoneId ());
  }

  @Nonnull
  public static OffsetDateTime createOffsetDateTimeUTC (final long nMillis)
  {
    return OffsetDateTime.ofInstant (Instant.ofEpochMilli (nMillis), ZoneOffset.UTC);
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final Number a)
  {
    return a == null ? null : createOffsetDateTime (a.longValue ());
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final Number a)
  {
    return a == null ? null : createOffsetDateTimeUTC (a.longValue ());
  }

  // To XMLOffsetDateTime

  @Nonnegative
  public static XMLOffsetDateTime getCurrentXMLOffsetDateTime ()
  {
    return XMLOffsetDateTime.now (_getZoneId ());
  }

  @Nonnegative
  public static XMLOffsetDateTime getCurrentXMLOffsetDateTimeUTC ()
  {
    return XMLOffsetDateTime.now (ZoneOffset.UTC);
  }

  /**
   * Get the passed date time but with micro and nanoseconds set to 0, so that
   * only the milliseconds part is present. This is helpful for XSD
   * serialization, where only milliseconds granularity is available.
   *
   * @param a
   *        Source date time. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>, the local
   *         date time with microseconds and nanoseconds set to 0 otherwise.
   */
  @Nullable
  public static XMLOffsetDateTime getWithMillisOnly (@Nullable final XMLOffsetDateTime a)
  {
    return a == null ? null : a.truncatedTo (ChronoUnit.MILLIS);
  }

  /**
   * @return The current local date and time but with micro and nanoseconds set
   *         to 0, so that only the milliseconds part is present. This is
   *         helpful for XSD serialization, where only milliseconds granularity
   *         is available.
   */
  @Nonnegative
  public static XMLOffsetDateTime getCurrentXMLOffsetDateTimeMillisOnly ()
  {
    return getWithMillisOnly (getCurrentXMLOffsetDateTime ());
  }

  /**
   * @return The current local date and time but with micro and nanoseconds set
   *         to 0, so that only the milliseconds part is present. This is
   *         helpful for XSD serialization, where only milliseconds granularity
   *         is available.
   */
  @Nonnegative
  public static XMLOffsetDateTime getCurrentXMLOffsetDateTimeMillisOnlyUTC ()
  {
    return getWithMillisOnly (getCurrentXMLOffsetDateTimeUTC ());
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final ZonedDateTime a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a.toLocalDateTime (), a.getOffset ());
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final OffsetDateTime a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a.toLocalDateTime (), a.getOffset ());
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final LocalDateTime a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a, null);
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (@Nullable final LocalDateTime a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a, ZoneOffset.UTC);
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final LocalDate a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a.atStartOfDay (), null);
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (@Nullable final LocalDate a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a.atStartOfDay (), ZoneOffset.UTC);
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final OffsetDate aOD)
  {
    return aOD == null ? null : XMLOffsetDateTime.of (aOD.toLocalDate ().atStartOfDay (), aOD.getOffset ());
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final XMLOffsetDate aOD)
  {
    return aOD == null ? null : XMLOffsetDateTime.of (aOD.toLocalDate ().atStartOfDay (), aOD.getOffset ());
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final YearMonth a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a.atDay (1).atStartOfDay (), null);
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (@Nullable final YearMonth a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a.atDay (1).atStartOfDay (), ZoneOffset.UTC);
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final Year a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a.atDay (1).atStartOfDay (), null);
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (@Nullable final Year a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a.atDay (1).atStartOfDay (), ZoneOffset.UTC);
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final LocalTime a)
  {
    return a == null ? null : XMLOffsetDateTime.of (_toLocalDateTime (a), null);
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (@Nullable final LocalTime a)
  {
    return a == null ? null : XMLOffsetDateTime.of (_toLocalDateTime (a), ZoneOffset.UTC);
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final OffsetTime a)
  {
    return a == null ? null : XMLOffsetDateTime.of (_toLocalDateTime (a.toLocalTime ()), a.getOffset ());
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final XMLOffsetTime a)
  {
    return a == null ? null : a.atXMLDate (LocalDate.ofEpochDay (0));
  }

  @Nonnull
  public static XMLOffsetDateTime createXMLOffsetDateTime (final int nYear, final Month eMonth, final int nDay)
  {
    return XMLOffsetDateTime.of (createLocalDate (nYear, eMonth, nDay).atStartOfDay (), null);
  }

  @Nonnull
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (final int nYear, final Month eMonth, final int nDay)
  {
    return XMLOffsetDateTime.of (createLocalDate (nYear, eMonth, nDay).atStartOfDay (), ZoneOffset.UTC);
  }

  @Nonnull
  public static XMLOffsetDateTime createXMLOffsetDateTime (final int nYear,
                                                           @Nonnull final Month eMonth,
                                                           final int nDay,
                                                           final int nHour,
                                                           final int nMinute,
                                                           final int nSecond)
  {
    return XMLOffsetDateTime.of (createLocalDateTime (nYear, eMonth, nDay, nHour, nMinute, nSecond), null);
  }

  @Nonnull
  public static XMLOffsetDateTime createXMLOffsetDateTime (final int nYear,
                                                           @Nonnull final Month eMonth,
                                                           final int nDay,
                                                           final int nHour,
                                                           final int nMinute,
                                                           final int nSecond,
                                                           @Nullable final ZoneOffset aZoneOffset)
  {
    return XMLOffsetDateTime.of (createLocalDateTime (nYear, eMonth, nDay, nHour, nMinute, nSecond), aZoneOffset);
  }

  @Nonnull
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (final int nYear,
                                                              @Nonnull final Month eMonth,
                                                              final int nDay,
                                                              final int nHour,
                                                              final int nMinute,
                                                              final int nSecond)
  {
    return XMLOffsetDateTime.of (createLocalDateTime (nYear, eMonth, nDay, nHour, nMinute, nSecond), ZoneOffset.UTC);
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final Instant a)
  {
    return a == null ? null : XMLOffsetDateTime.ofInstant (a, _getZoneId ());
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (@Nullable final Instant a)
  {
    return a == null ? null : XMLOffsetDateTime.ofInstant (a, ZoneOffset.UTC);
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final Date a)
  {
    return a == null ? null : XMLOffsetDateTime.ofInstant (a.toInstant (), getZoneOffset (a));
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final Timestamp a)
  {
    return a == null ? null : XMLOffsetDateTime.ofInstant (a.toInstant (), getZoneOffset (a));
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final GregorianCalendar aCal)
  {
    return aCal == null ? null : createXMLOffsetDateTime (aCal.toZonedDateTime ());
  }

  @Nonnull
  public static XMLOffsetDateTime createXMLOffsetDateTime (final long nMillis)
  {
    return XMLOffsetDateTime.ofInstant (Instant.ofEpochMilli (nMillis), _getZoneId ());
  }

  @Nonnull
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (final long nMillis)
  {
    return XMLOffsetDateTime.ofInstant (Instant.ofEpochMilli (nMillis), ZoneOffset.UTC);
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final Number a)
  {
    return a == null ? null : createXMLOffsetDateTime (a.longValue ());
  }

  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (@Nullable final Number a)
  {
    return a == null ? null : createXMLOffsetDateTimeUTC (a.longValue ());
  }

  // To LocalDateTime

  @Nonnegative
  public static LocalDateTime getCurrentLocalDateTime ()
  {
    return LocalDateTime.now (_getZoneId ());
  }

  @Nonnegative
  public static LocalDateTime getCurrentLocalDateTimeUTC ()
  {
    return LocalDateTime.now (ZoneOffset.UTC);
  }

  /**
   * Get the passed date time but with micro and nanoseconds set to 0, so that
   * only the milliseconds part is present. This is helpful for XSD
   * serialization, where only milliseconds granularity is available.
   *
   * @param a
   *        Source date time. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>, the local
   *         date time with microseconds and nanoseconds set to 0 otherwise.
   * @since 9.2.0
   */
  @Nullable
  public static LocalDateTime getWithMillisOnly (@Nullable final LocalDateTime a)
  {
    return a == null ? null : a.truncatedTo (ChronoUnit.MILLIS);
  }

  /**
   * @return The current local date and time but with micro and nanoseconds set
   *         to 0, so that only the milliseconds part is present. This is
   *         helpful for XSD serialization, where only milliseconds granularity
   *         is available.
   * @since 9.2.0
   */
  @Nonnegative
  public static LocalDateTime getCurrentLocalDateTimeMillisOnly ()
  {
    return getWithMillisOnly (getCurrentLocalDateTime ());
  }

  /**
   * @return The current local date and time but with micro and nanoseconds set
   *         to 0, so that only the milliseconds part is present. This is
   *         helpful for XSD serialization, where only milliseconds granularity
   *         is available.
   */
  @Nonnegative
  public static LocalDateTime getCurrentLocalDateTimeMillisOnlyUTC ()
  {
    return getWithMillisOnly (getCurrentLocalDateTimeUTC ());
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final ZonedDateTime aDT)
  {
    return aDT == null ? null : aDT.toLocalDateTime ();
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final OffsetDateTime aDT)
  {
    return aDT == null ? null : aDT.toLocalDateTime ();
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final XMLOffsetDateTime aDT)
  {
    return aDT == null ? null : aDT.toLocalDateTime ();
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final OffsetDate aDT)
  {
    return aDT == null ? null : aDT.toLocalDate ().atStartOfDay ();
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final XMLOffsetDate aDT)
  {
    return aDT == null ? null : aDT.toLocalDate ().atStartOfDay ();
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final LocalDate a)
  {
    return a == null ? null : a.atStartOfDay ();
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final YearMonth a)
  {
    return a == null ? null : a.atDay (1).atStartOfDay ();
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final Year a)
  {
    return a == null ? null : a.atDay (1).atStartOfDay ();
  }

  @Nonnull
  private static LocalDateTime _toLocalDateTime (@Nonnull final LocalTime a)
  {
    return a.atDate (LocalDate.ofEpochDay (0));
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final LocalTime a)
  {
    return a == null ? null : _toLocalDateTime (a);
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final OffsetTime a)
  {
    return a == null ? null : _toLocalDateTime (a.toLocalTime ());
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final XMLOffsetTime a)
  {
    return a == null ? null : _toLocalDateTime (a.toLocalTime ());
  }

  @Nonnull
  public static LocalDateTime createLocalDateTime (final int nYear, @Nonnull final Month eMonth, final int nDay)
  {
    return createLocalDateTime (nYear, eMonth, nDay, 0, 0, 0);
  }

  @Nonnull
  public static LocalDateTime createLocalDateTime (final int nYear,
                                                   @Nonnull final Month eMonth,
                                                   final int nDay,
                                                   final int nHour,
                                                   final int nMinute)
  {
    return createLocalDateTime (nYear, eMonth, nDay, nHour, nMinute, 0);
  }

  @Nonnull
  public static LocalDateTime createLocalDateTime (final int nYear,
                                                   @Nonnull final Month eMonth,
                                                   final int nDay,
                                                   final int nHour,
                                                   final int nMinute,
                                                   final int nSecond)
  {
    return LocalDateTime.of (nYear, eMonth, nDay, nHour, nMinute, nSecond);
  }

  @Nonnull
  private static LocalDateTime _toLocalDateTime (@Nullable final Instant a)
  {
    return LocalDateTime.ofInstant (a, _getZoneId ());
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final Instant a)
  {
    return a == null ? null : _toLocalDateTime (a);
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final Date a)
  {
    return a == null ? null : _toLocalDateTime (a.toInstant ());
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final Timestamp a)
  {
    return a == null ? null : a.toLocalDateTime ();
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final GregorianCalendar aCal)
  {
    return aCal == null ? null : aCal.toZonedDateTime ().toLocalDateTime ();
  }

  @Nonnull
  public static LocalDateTime createLocalDateTime (final long nMillis)
  {
    return _toLocalDateTime (Instant.ofEpochMilli (nMillis));
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final Number a)
  {
    return a == null ? null : createLocalDateTime (a.longValue ());
  }

  // To LocalDate

  @Nonnegative
  public static LocalDate getCurrentLocalDate ()
  {
    return LocalDate.now (_getZoneId ());
  }

  @Nonnegative
  public static LocalDate getCurrentLocalDateUTC ()
  {
    return LocalDate.now (ZoneOffset.UTC);
  }

  @Nonnull
  public static LocalDate createLocalDate (final int nYear, @Nonnull final Month eMonth, final int nDayOfMonth)
  {
    return LocalDate.of (nYear, eMonth, nDayOfMonth);
  }

  @Nullable
  public static LocalDate createLocalDate (@Nullable final GregorianCalendar a)
  {
    return a == null ? null : a.toZonedDateTime ().toLocalDate ();
  }

  @Nonnull
  public static LocalDate createLocalDate (final long nMillis)
  {
    return createLocalDateTime (nMillis).toLocalDate ();
  }

  @Nonnull
  public static LocalDate createLocalDate (@Nullable final Number a)
  {
    return a == null ? null : createLocalDateTime (a.longValue ()).toLocalDate ();
  }

  @Nullable
  public static LocalDate createLocalDate (@Nullable final Instant a)
  {
    return a == null ? null : _toLocalDateTime (a).toLocalDate ();
  }

  @Nullable
  public static LocalDate createLocalDate (@Nullable final Date a)
  {
    return a == null ? null : _toLocalDateTime (a.toInstant ()).toLocalDate ();
  }

  @Nullable
  public static LocalDate createLocalDate (@Nullable final YearMonth a)
  {
    return a == null ? null : a.atDay (1);
  }

  @Nullable
  public static LocalDate createLocalDate (@Nullable final Year a)
  {
    return a == null ? null : a.atDay (1);
  }

  @Nullable
  public static LocalDate createLocalDate (@Nullable final LocalDateTime a)
  {
    return a == null ? null : a.toLocalDate ();
  }

  @Nullable
  public static LocalDate createLocalDate (@Nullable final ZonedDateTime a)
  {
    return a == null ? null : a.toLocalDate ();
  }

  @Nullable
  public static LocalDate createLocalDate (@Nullable final OffsetDateTime a)
  {
    return a == null ? null : a.toLocalDate ();
  }

  @Nullable
  public static LocalDate createLocalDate (@Nullable final XMLOffsetDateTime a)
  {
    return a == null ? null : a.toLocalDate ();
  }

  @Nullable
  public static LocalDate createLocalDate (@Nullable final XMLOffsetDate a)
  {
    return a == null ? null : a.toLocalDate ();
  }

  // To OffsetDate

  @Nonnegative
  public static OffsetDate getCurrentOffsetDate ()
  {
    return OffsetDate.now (_getZoneId ());
  }

  @Nonnegative
  public static OffsetDate getCurrentOffsetDateUTC ()
  {
    return OffsetDate.now (ZoneOffset.UTC);
  }

  @Nonnull
  private static ZoneOffset _getFallbackZoneOffset ()
  {
    return ZoneOffset.UTC;
  }

  @Nonnull
  private static ZoneOffset _getFallbackZoneOffset (@Nullable final Instant aInstant)
  {
    if (aInstant == null)
      return _getFallbackZoneOffset ();
    // Works with Instance of LDT
    return _getZoneId ().getRules ().getOffset (aInstant);
  }

  @Nonnull
  private static ZoneOffset _getFallbackZoneOffset (@Nullable final LocalDateTime aLDT)
  {
    if (aLDT == null)
      return _getFallbackZoneOffset ();
    // Works with Instance of LDT
    return _getZoneId ().getRules ().getOffset (aLDT);
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final LocalDate a)
  {
    return a == null ? null : OffsetDate.of (a, _getFallbackZoneOffset (a.atStartOfDay ()));
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final LocalDate a, @Nonnull final ZoneOffset aOffset)
  {
    return a == null ? null : OffsetDate.of (a, aOffset);
  }

  @Nonnull
  public static OffsetDate createOffsetDate (final int nYear, @Nonnull final Month eMonth, final int nDayOfMonth)
  {
    final LocalDate aLD = createLocalDate (nYear, eMonth, nDayOfMonth);
    return OffsetDate.of (aLD, _getFallbackZoneOffset (aLD.atStartOfDay ()));
  }

  @Nonnull
  public static OffsetDate createOffsetDate (final int nYear,
                                             @Nonnull final Month eMonth,
                                             final int nDayOfMonth,
                                             @Nonnull final ZoneOffset aOffset)
  {
    return OffsetDate.of (nYear, eMonth, nDayOfMonth, aOffset);
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final LocalDateTime a)
  {
    return a == null ? null : OffsetDate.of (a.toLocalDate (), _getFallbackZoneOffset (a));
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final LocalDateTime a, @Nonnull final ZoneOffset aOffset)
  {
    return a == null ? null : OffsetDate.of (a.toLocalDate (), aOffset);
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final GregorianCalendar a)
  {
    return a == null ? null : createOffsetDate (a.toZonedDateTime ());
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final Instant a)
  {
    return a == null ? null : OffsetDate.ofInstant (a, _getZoneId ());
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final Instant a, @Nonnull final ZoneOffset aOffset)
  {
    return a == null ? null : OffsetDate.ofInstant (a, aOffset);
  }

  @Nonnull
  public static OffsetDate createOffsetDate (final long nMillis)
  {
    return createOffsetDate (Instant.ofEpochMilli (nMillis));
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final Number a)
  {
    return a == null ? null : createOffsetDate (a.longValue ());
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final Date a)
  {
    return a == null ? null : createOffsetDate (_toOffsetDateTime (a));
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final YearMonth a)
  {
    return a == null ? null : createOffsetDate (a.atDay (1));
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final Year a)
  {
    return a == null ? null : createOffsetDate (a.atDay (1));
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final OffsetDateTime a)
  {
    return a == null ? null : OffsetDate.of (a.toLocalDate (), a.getOffset ());
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final ZonedDateTime a)
  {
    return a == null ? null : OffsetDate.of (a.toLocalDate (), a.getOffset ());
  }

  // To XMLOffsetDate

  @Nonnegative
  public static XMLOffsetDate getCurrentXMLOffsetDate ()
  {
    return XMLOffsetDate.now (_getZoneId ());
  }

  @Nonnegative
  public static XMLOffsetDate getCurrentXMLOffsetDateUTC ()
  {
    return XMLOffsetDate.now (ZoneOffset.UTC);
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final ZonedDateTime a)
  {
    return a == null ? null : XMLOffsetDate.of (a.toLocalDate (), a.getOffset ());
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final OffsetDateTime a)
  {
    return a == null ? null : XMLOffsetDate.of (a.toLocalDate (), a.getOffset ());
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final XMLOffsetDateTime a)
  {
    return a == null ? null : XMLOffsetDate.of (a.toLocalDate (), a.getOffset ());
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final LocalDate a)
  {
    return createXMLOffsetDate (a, null);
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final LocalDate a, @Nonnull final ZoneOffset aOffset)
  {
    return a == null ? null : XMLOffsetDate.of (a, aOffset);
  }

  @Nonnull
  public static XMLOffsetDate createXMLOffsetDate (final int nYear, @Nonnull final Month eMonth, final int nDayOfMonth)
  {
    return createXMLOffsetDate (nYear, eMonth, nDayOfMonth, null);
  }

  @Nonnull
  public static XMLOffsetDate createXMLOffsetDate (final int nYear,
                                                   @Nonnull final Month eMonth,
                                                   final int nDayOfMonth,
                                                   @Nullable final ZoneOffset aOffset)
  {
    return XMLOffsetDate.of (nYear, eMonth, nDayOfMonth, aOffset);
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final LocalDateTime a)
  {
    return createXMLOffsetDate (a, null);
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final LocalDateTime a, @Nonnull final ZoneOffset aOffset)
  {
    return a == null ? null : XMLOffsetDate.of (a.toLocalDate (), aOffset);
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final Instant a)
  {
    return a == null ? null : XMLOffsetDate.ofInstant (a, _getZoneId ());
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final Instant a, @Nonnull final ZoneId aZoneId)
  {
    return a == null ? null : XMLOffsetDate.ofInstant (a, aZoneId);
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final GregorianCalendar a)
  {
    return a == null ? null : createXMLOffsetDate (a.toZonedDateTime ());
  }

  @Nonnull
  public static XMLOffsetDate createXMLOffsetDate (final long nMillis)
  {
    return createXMLOffsetDate (Instant.ofEpochMilli (nMillis));
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final Date a)
  {
    return a == null ? null : createXMLOffsetDate (_toOffsetDateTime (a));
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final YearMonth a)
  {
    return a == null ? null : createXMLOffsetDate (a.atDay (1));
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final Year a)
  {
    return a == null ? null : createXMLOffsetDate (a.atDay (1));
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final Number a)
  {
    return a == null ? null : createXMLOffsetDate (a.longValue ());
  }

  // LocalTime

  @Nonnegative
  public static LocalTime getCurrentLocalTime ()
  {
    return LocalTime.now (_getZoneId ());
  }

  @Nonnegative
  public static LocalTime getCurrentLocalTimeUTC ()
  {
    return LocalTime.now (ZoneOffset.UTC);
  }

  /**
   * Get the passed time but with micro and nanoseconds set to 0, so that only
   * the milliseconds part is present. This is helpful for XSD serialization,
   * where only milliseconds granularity is available.
   *
   * @param a
   *        Source time. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>, the local
   *         time with microseconds and nanoseconds set to 0 otherwise.
   * @since 9.4.7
   */
  @Nullable
  public static LocalTime getWithMillisOnly (@Nullable final LocalTime a)
  {
    return a == null ? null : a.truncatedTo (ChronoUnit.MILLIS);
  }

  /**
   * @return The current local time but with micro and nanoseconds set to 0, so
   *         that only the milliseconds part is present. This is helpful for XSD
   *         serialization, where only milliseconds granularity is available.
   * @since 9.4.7
   */
  @Nonnegative
  public static LocalTime getCurrentLocalTimeMillisOnly ()
  {
    return getWithMillisOnly (getCurrentLocalTime ());
  }

  /**
   * @return The current local time but with micro and nanoseconds set to 0, so
   *         that only the milliseconds part is present. This is helpful for XSD
   *         serialization, where only milliseconds granularity is available.
   */
  @Nonnegative
  public static LocalTime getCurrentLocalTimeMillisOnlyUTC ()
  {
    return getWithMillisOnly (getCurrentLocalTimeUTC ());
  }

  @Nullable
  public static LocalTime createLocalTime (@Nullable final GregorianCalendar a)
  {
    return a == null ? null : a.toZonedDateTime ().toLocalTime ();
  }

  @Nonnull
  public static LocalTime createLocalTime (final long nMillis)
  {
    return createLocalDateTime (nMillis).toLocalTime ();
  }

  @Nullable
  public static LocalTime createLocalTime (@Nullable final Instant a)
  {
    return a == null ? null : createLocalDateTime (a).toLocalTime ();
  }

  @Nullable
  public static LocalTime createLocalTime (@Nullable final Date a)
  {
    return a == null ? null : createLocalTime (a.toInstant ());
  }

  @Nullable
  public static LocalTime createLocalTime (@Nullable final LocalDateTime a)
  {
    return a == null ? null : a.toLocalTime ();
  }

  @Nullable
  public static LocalTime createLocalTime (@Nullable final OffsetTime a)
  {
    return a == null ? null : a.toLocalTime ();
  }

  @Nullable
  public static LocalTime createLocalTime (@Nullable final XMLOffsetTime a)
  {
    return a == null ? null : a.toLocalTime ();
  }

  @Nonnull
  public static LocalTime createLocalTime (final int nHour, final int nMinute, final int nSecond)
  {
    return LocalTime.of (nHour, nMinute, nSecond);
  }

  // to OffsetTime

  @Nonnegative
  public static OffsetTime getCurrentOffsetTime ()
  {
    return OffsetTime.now (_getZoneId ());
  }

  @Nonnegative
  public static OffsetTime getCurrentOffsetTimeUTC ()
  {
    return OffsetTime.now (ZoneOffset.UTC);
  }

  /**
   * Get the passed time but with micro and nanoseconds set to 0, so that only
   * the milliseconds part is present. This is helpful for XSD serialization,
   * where only milliseconds granularity is available.
   *
   * @param a
   *        Source time. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>, the local
   *         time with microseconds and nanoseconds set to 0 otherwise.
   * @since 10.0.0
   */
  @Nullable
  public static OffsetTime getWithMillisOnly (@Nullable final OffsetTime a)
  {
    return a == null ? null : a.truncatedTo (ChronoUnit.MILLIS);
  }

  /**
   * @return The current local time but with micro and nanoseconds set to 0, so
   *         that only the milliseconds part is present. This is helpful for XSD
   *         serialization, where only milliseconds granularity is available.
   * @since 9.4.7
   */
  @Nonnegative
  public static OffsetTime getCurrentOffsetTimeMillisOnly ()
  {
    return getWithMillisOnly (getCurrentOffsetTime ());
  }

  /**
   * @return The current local time but with micro and nanoseconds set to 0, so
   *         that only the milliseconds part is present. This is helpful for XSD
   *         serialization, where only milliseconds granularity is available.
   */
  @Nonnegative
  public static OffsetTime getCurrentOffsetTimeMillisOnlyUTC ()
  {
    return getWithMillisOnly (getCurrentOffsetTimeUTC ());
  }

  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final GregorianCalendar a)
  {
    return a == null ? null : a.toZonedDateTime ().toOffsetDateTime ().toOffsetTime ();
  }

  @Nonnull
  public static OffsetTime createOffsetTime (final long nMillis)
  {
    return createOffsetDateTime (nMillis).toOffsetTime ();
  }

  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final Instant a)
  {
    return a == null ? null : createOffsetDateTime (a).toOffsetTime ();
  }

  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final Date a)
  {
    return a == null ? null : createOffsetTime (a.toInstant ());
  }

  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final OffsetDateTime a)
  {
    return a == null ? null : a.toOffsetTime ();
  }

  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final ZonedDateTime a)
  {
    return a == null ? null : a.toOffsetDateTime ().toOffsetTime ();
  }

  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final LocalDateTime a)
  {
    return a == null ? null : a.toLocalTime ().atOffset (_getFallbackZoneOffset (a));
  }

  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final LocalDateTime a, @Nonnull final ZoneOffset aOfs)
  {
    return a == null ? null : a.toLocalTime ().atOffset (aOfs);
  }

  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final LocalTime a)
  {
    return a == null ? null : a.atOffset (_getFallbackZoneOffset ());
  }

  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final LocalTime a, @Nonnull final ZoneOffset aOfs)
  {
    return a == null ? null : a.atOffset (aOfs);
  }

  @Nonnull
  public static OffsetTime createOffsetTime (final int nHour,
                                             final int nMinute,
                                             final int nSecond,
                                             @Nonnull final ZoneOffset aOfs)
  {
    return OffsetTime.of (nHour, nMinute, nSecond, 0, aOfs);
  }

  // to XMLOffsetTime

  @Nonnegative
  public static XMLOffsetTime getCurrentXMLOffsetTime ()
  {
    return XMLOffsetTime.now (_getZoneId ());
  }

  @Nonnegative
  public static XMLOffsetTime getCurrentXMLOffsetTimeUTC ()
  {
    return XMLOffsetTime.now (ZoneOffset.UTC);
  }

  /**
   * Get the passed time but with micro and nanoseconds set to 0, so that only
   * the milliseconds part is present. This is helpful for XSD serialization,
   * where only milliseconds granularity is available.
   *
   * @param a
   *        Source time. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>, the local
   *         time with microseconds and nanoseconds set to 0 otherwise.
   */
  @Nullable
  public static XMLOffsetTime getWithMillisOnly (@Nullable final XMLOffsetTime a)
  {
    return a == null ? null : a.truncatedTo (ChronoUnit.MILLIS);
  }

  /**
   * @return The current local time but with micro and nanoseconds set to 0, so
   *         that only the milliseconds part is present. This is helpful for XSD
   *         serialization, where only milliseconds granularity is available.
   */
  @Nonnegative
  public static XMLOffsetTime getCurrentXMLOffsetTimeMillisOnly ()
  {
    return getWithMillisOnly (getCurrentXMLOffsetTime ());
  }

  /**
   * @return The current local time but with micro and nanoseconds set to 0, so
   *         that only the milliseconds part is present. This is helpful for XSD
   *         serialization, where only milliseconds granularity is available.
   */
  @Nonnegative
  public static XMLOffsetTime getCurrentXMLOffsetTimeMillisOnlyUTC ()
  {
    return getWithMillisOnly (getCurrentXMLOffsetTimeUTC ());
  }

  @Nullable
  public static XMLOffsetTime createXMLOffsetTime (@Nullable final GregorianCalendar a)
  {
    return a == null ? null : XMLOffsetTime.of (a.toZonedDateTime ().toOffsetDateTime ().toOffsetTime ());
  }

  @Nonnull
  public static XMLOffsetTime createXMLOffsetTime (final long nMillis)
  {
    return createXMLOffsetDateTime (nMillis).toXMLOffsetTime ();
  }

  @Nullable
  public static XMLOffsetTime createXMLOffsetTime (@Nullable final Instant a)
  {
    return a == null ? null : createXMLOffsetDateTime (a).toXMLOffsetTime ();
  }

  @Nullable
  public static XMLOffsetTime createXMLOffsetTime (@Nullable final Date a)
  {
    return a == null ? null : createXMLOffsetTime (a.toInstant ());
  }

  @Nullable
  public static XMLOffsetTime createXMLOffsetTime (@Nullable final OffsetDateTime a)
  {
    return a == null ? null : XMLOffsetTime.of (a.toOffsetTime ());
  }

  @Nullable
  public static XMLOffsetTime createXMLOffsetTime (@Nullable final ZonedDateTime a)
  {
    return a == null ? null : XMLOffsetTime.of (a.toOffsetDateTime ().toOffsetTime ());
  }

  @Nullable
  public static XMLOffsetTime createXMLOffsetTime (@Nullable final LocalDateTime a)
  {
    return a == null ? null : XMLOffsetTime.of (a.toLocalTime (), null);
  }

  @Nullable
  public static XMLOffsetTime createXMLOffsetTime (@Nullable final LocalDateTime a, @Nullable final ZoneOffset aOfs)
  {
    return a == null ? null : XMLOffsetTime.of (a.toLocalTime (), aOfs);
  }

  @Nullable
  public static XMLOffsetTime createXMLOffsetTime (@Nullable final LocalTime a)
  {
    return a == null ? null : XMLOffsetTime.of (a, null);
  }

  @Nullable
  public static XMLOffsetTime createXMLOffsetTime (@Nullable final LocalTime a, @Nullable final ZoneOffset aOfs)
  {
    return a == null ? null : XMLOffsetTime.of (a, aOfs);
  }

  @Nonnull
  public static XMLOffsetTime createXMLOffsetTime (final int nHour, final int nMinute, final int nSecond)
  {
    return createXMLOffsetTime (nHour, nMinute, nSecond, null);
  }

  @Nonnull
  public static XMLOffsetTime createXMLOffsetTime (final int nHour,
                                                   final int nMinute,
                                                   final int nSecond,
                                                   @Nullable final ZoneOffset aOfs)
  {
    return XMLOffsetTime.of (nHour, nMinute, nSecond, 0, aOfs);
  }

  // to Date

  @Nullable
  public static Date createDate (@Nullable final ZonedDateTime a)
  {
    // The timezone details gets lost here
    return a == null ? null : Date.from (a.toInstant ());
  }

  @Nullable
  public static Date createDate (@Nullable final OffsetDateTime a)
  {
    // The timezone details gets lost here
    return a == null ? null : Date.from (a.toInstant ());
  }

  @Nullable
  public static Date createDate (@Nullable final LocalDateTime a)
  {
    return createDate (createZonedDateTime (a));
  }

  @Nullable
  public static Date createDate (@Nullable final LocalDate a)
  {
    return createDate (createZonedDateTime (a));
  }

  @Nullable
  public static Date createDate (@Nullable final OffsetDate aOD)
  {
    return createDate (createZonedDateTime (aOD));
  }

  @Nullable
  public static Date createDate (@Nullable final XMLOffsetDate aOD)
  {
    return createDate (createZonedDateTime (aOD));
  }

  @Nullable
  public static Date createDate (@Nullable final LocalTime a)
  {
    return createDate (createZonedDateTime (a));
  }

  @Nullable
  public static Date createDate (@Nullable final OffsetTime a)
  {
    return createDate (createZonedDateTime (a));
  }

  @Nullable
  public static Date createDate (@Nullable final XMLOffsetTime a)
  {
    return createDate (createZonedDateTime (a));
  }

  @Nonnull
  public static Date createDateForDate (final int nYear, @Nonnull final Month eMonth, final int nDayOfMonth)
  {
    return createDate (createLocalDate (nYear, eMonth, nDayOfMonth));
  }

  @Nonnull
  public static Date createDateForTime (final int nHour, final int nMin, final int nSec)
  {
    return createDate (createLocalTime (nHour, nMin, nSec));
  }

  @Nonnull
  public static Calendar createCalendar ()
  {
    return Calendar.getInstance (PDTConfig.getDefaultTimeZone (), Locale.getDefault (Locale.Category.FORMAT));
  }

  @Nonnull
  public static Calendar createCalendarUTC ()
  {
    return Calendar.getInstance (PDTConfig.getUTCTimeZone (), Locale.getDefault (Locale.Category.FORMAT));
  }

  @Nonnull
  public static GregorianCalendar createGregorianCalendar ()
  {
    return new GregorianCalendar (PDTConfig.getDefaultTimeZone (), Locale.getDefault (Locale.Category.FORMAT));
  }

  @Nonnull
  public static GregorianCalendar createGregorianCalendarUTC ()
  {
    return new GregorianCalendar (PDTConfig.getUTCTimeZone (), Locale.getDefault (Locale.Category.FORMAT));
  }

  // Misc

  @Nonnegative
  public static int getCurrentYear ()
  {
    return getCurrentLocalDate ().getYear ();
  }

  @Nonnegative
  public static int getCurrentYearUTC ()
  {
    return getCurrentLocalDateUTC ().getYear ();
  }

  @Nonnull
  public static Year getCurrentYearObj ()
  {
    return Year.now (_getZoneId ());
  }

  @Nonnull
  public static Year getCurrentYearObjUTC ()
  {
    return Year.now (ZoneOffset.UTC);
  }

  @Nonnull
  public static MonthDay getCurrentMonthDay ()
  {
    return MonthDay.now (_getZoneId ());
  }

  @Nonnull
  public static MonthDay getCurrentMonthDayUTC ()
  {
    return MonthDay.now (ZoneOffset.UTC);
  }

  @Nonnegative
  public static YearMonth getCurrentYearMonth ()
  {
    return YearMonth.now (_getZoneId ());
  }

  @Nonnegative
  public static YearMonth getCurrentYearMonthUTC ()
  {
    return YearMonth.now (ZoneOffset.UTC);
  }

  @Nonnegative
  public static Instant getCurrentInstant ()
  {
    return Instant.now (Clock.system (_getZoneId ()));
  }

  @Nonnegative
  public static Instant getCurrentInstantUTC ()
  {
    return Instant.now (Clock.system (ZoneOffset.UTC));
  }

  public static long getCurrentMillis ()
  {
    return getCurrentInstant ().toEpochMilli ();
  }

  public static long getCurrentMillisUTC ()
  {
    return getCurrentInstantUTC ().toEpochMilli ();
  }

  public static long getMillis (@Nonnull final LocalDate a)
  {
    return getMillis (createZonedDateTime (a));
  }

  public static long getMillis (@Nonnull final OffsetDate aOD)
  {
    return getMillis (createZonedDateTime (aOD));
  }

  public static long getMillis (@Nonnull final XMLOffsetDate aOD)
  {
    return getMillis (createZonedDateTime (aOD));
  }

  public static long getMillis (@Nonnull final LocalTime a)
  {
    return getMillis (createZonedDateTime (a));
  }

  public static long getMillis (@Nonnull final OffsetTime a)
  {
    return getMillis (createZonedDateTime (a));
  }

  public static long getMillis (@Nonnull final XMLOffsetTime a)
  {
    return getMillis (createZonedDateTime (a));
  }

  public static long getMillis (@Nonnull final LocalDateTime a)
  {
    return getMillis (createZonedDateTime (a));
  }

  public static long getMillis (@Nonnull final OffsetDateTime a)
  {
    return a.toInstant ().toEpochMilli ();
  }

  public static long getMillis (@Nonnull final XMLOffsetDateTime a)
  {
    return a.toInstant ().toEpochMilli ();
  }

  public static long getMillis (@Nonnull final ZonedDateTime a)
  {
    return a.toInstant ().toEpochMilli ();
  }
}
