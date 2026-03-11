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
package com.helger.datetime.helper;

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

import javax.xml.datatype.XMLGregorianCalendar;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.CGlobal;
import com.helger.datetime.rt.OffsetDate;
import com.helger.datetime.xml.XMLOffsetDate;
import com.helger.datetime.xml.XMLOffsetDateTime;
import com.helger.datetime.xml.XMLOffsetTime;
import com.helger.datetime.zone.PDTConfig;

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

  @NonNull
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
  public static int getTimezoneOffsetInMinutes (@NonNull final Date a)
  {
    return -a.getTimezoneOffset ();
  }

  /**
   * Get the raw timezone offset in minutes of the passed calendar.
   *
   * @param aCal
   *        The calendar to use. May not be <code>null</code>.
   * @return The timezone offset in minutes.
   */
  public static int getTimezoneOffsetInMinutes (@NonNull final GregorianCalendar aCal)
  {
    final long nOffsetMillis = aCal.getTimeZone ().getRawOffset ();
    return Math.toIntExact (nOffsetMillis / CGlobal.MILLISECONDS_PER_MINUTE);
  }

  /**
   * Get the standard timezone offset in minutes of the passed zone at the
   * given instant.
   *
   * @param aZID
   *        The zone ID to use. May not be <code>null</code>.
   * @param aAt
   *        The instant at which to determine the offset. May not be
   *        <code>null</code>.
   * @return The timezone offset in minutes.
   */
  public static int getTimezoneOffsetInMinutes (@NonNull final ZoneId aZID, @NonNull final Instant aAt)
  {
    final ZoneOffset aZO = aZID.getRules ().getStandardOffset (aAt);
    return getTimezoneOffsetInMinutes (aZO);
  }

  /**
   * Get the timezone offset in minutes of the passed zone offset.
   *
   * @param aZO
   *        The zone offset to use. May not be <code>null</code>.
   * @return The timezone offset in minutes.
   */
  public static int getTimezoneOffsetInMinutes (@NonNull final ZoneOffset aZO)
  {
    return aZO.getTotalSeconds () / CGlobal.SECONDS_PER_MINUTE;
  }

  /**
   * Get a {@link ZoneOffset} from a timezone offset in minutes.
   *
   * @param nOffsetInMinutes
   *        The offset in minutes.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static ZoneOffset getZoneOffsetFromOffsetInMinutes (final int nOffsetInMinutes)
  {
    return ZoneOffset.ofHoursMinutes (nOffsetInMinutes / CGlobal.MINUTES_PER_HOUR,
                                      nOffsetInMinutes % CGlobal.MINUTES_PER_HOUR);
  }

  /**
   * Get the {@link ZoneOffset} of the passed {@link Date}.
   *
   * @param a
   *        The date to use. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static ZoneOffset getZoneOffset (@NonNull final Date a)
  {
    final int nOffsetMin = getTimezoneOffsetInMinutes (a);
    return getZoneOffsetFromOffsetInMinutes (nOffsetMin);
  }

  /**
   * Get a {@link ZoneId} from a timezone offset in minutes.
   *
   * @param nOffsetInMinutes
   *        The offset in minutes.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static ZoneId getZoneIdFromOffsetInMinutes (final int nOffsetInMinutes)
  {
    final ZoneOffset aZO = getZoneOffsetFromOffsetInMinutes (nOffsetInMinutes);
    // Empty prefix means "no special"
    return ZoneId.ofOffset ("", aZO);
  }

  /**
   * Get the {@link ZoneId} of the passed {@link Date}.
   *
   * @param a
   *        The date to use. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static ZoneId getZoneId (@NonNull final Date a)
  {
    final int nOffsetMin = getTimezoneOffsetInMinutes (a);
    return getZoneIdFromOffsetInMinutes (nOffsetMin);
  }

  /**
   * Get the {@link TimeZone} of the passed {@link Date}.
   *
   * @param a
   *        The date to use. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static TimeZone getTimeZone (@NonNull final Date a)
  {
    final ZoneId aZI = getZoneId (a);
    return TimeZone.getTimeZone (aZI);
  }

  // To ZonedDateTime

  /**
   * @return The current {@link ZonedDateTime} in the default time zone. Never
   *         <code>null</code>.
   */
  @NonNull
  public static ZonedDateTime getCurrentZonedDateTime ()
  {
    return ZonedDateTime.now (_getZoneId ());
  }

  /**
   * @return The current {@link ZonedDateTime} in UTC. Never <code>null</code>.
   */
  @NonNull
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

  /**
   * Create a {@link ZonedDateTime} from the passed {@link OffsetDateTime}.
   *
   * @param a
   *        The offset date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final OffsetDateTime a)
  {
    return a == null ? null : a.toZonedDateTime ();
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link LocalDateTime} using the default time
   * zone.
   *
   * @param a
   *        The local date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final LocalDateTime a)
  {
    return a == null ? null : ZonedDateTime.of (a, _getZoneId ());
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link LocalDateTime} using UTC.
   *
   * @param a
   *        The local date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final LocalDateTime a)
  {
    return a == null ? null : ZonedDateTime.of (a, ZoneOffset.UTC);
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link LocalDate} using the default time zone.
   * The time is set to midnight.
   *
   * @param a
   *        The local date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final LocalDate a)
  {
    return a == null ? null : ZonedDateTime.of (a.atStartOfDay (), _getZoneId ());
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link LocalDate} using UTC. The time is set to
   * midnight.
   *
   * @param a
   *        The local date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final LocalDate a)
  {
    return a == null ? null : ZonedDateTime.of (a.atStartOfDay (), ZoneOffset.UTC);
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link OffsetDate}. The time is set to midnight.
   *
   * @param aOD
   *        The offset date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final OffsetDate aOD)
  {
    return aOD == null ? null : ZonedDateTime.of (aOD.toLocalDate ().atStartOfDay (), aOD.getOffset ());
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link XMLOffsetDate}. The time is set to
   * midnight.
   *
   * @param aOD
   *        The XML offset date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final XMLOffsetDate aOD)
  {
    return aOD == null ? null : ZonedDateTime.of (aOD.toLocalDate ().atStartOfDay (),
                                                  aOD.hasOffset () ? aOD.getOffset () : _getZoneId ());
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link YearMonth} using the default time zone.
   *
   * @param a
   *        The year-month to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final YearMonth a)
  {
    return createZonedDateTime (createLocalDateTime (a));
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link YearMonth} using UTC.
   *
   * @param a
   *        The year-month to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final YearMonth a)
  {
    return createZonedDateTimeUTC (createLocalDateTime (a));
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link Year} using the default time zone.
   *
   * @param a
   *        The year to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final Year a)
  {
    return createZonedDateTime (createLocalDateTime (a));
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link Year} using UTC.
   *
   * @param a
   *        The year to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final Year a)
  {
    return createZonedDateTimeUTC (createLocalDateTime (a));
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link LocalTime} using the default time zone.
   *
   * @param a
   *        The local time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final LocalTime a)
  {
    return a == null ? null : ZonedDateTime.of (_toLocalDateTime (a), _getZoneId ());
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link LocalTime} using UTC.
   *
   * @param a
   *        The local time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final LocalTime a)
  {
    return a == null ? null : ZonedDateTime.of (_toLocalDateTime (a), ZoneOffset.UTC);
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link OffsetTime}.
   *
   * @param a
   *        The offset time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final OffsetTime a)
  {
    return createZonedDateTime (createOffsetDateTime (a));
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link XMLOffsetTime}.
   *
   * @param a
   *        The XML offset time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final XMLOffsetTime a)
  {
    return createZonedDateTime (createOffsetDateTime (a));
  }

  /**
   * Create a {@link ZonedDateTime} from year, month and day using the default time zone.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month. May not be <code>null</code>.
   * @param nDay
   *        The day of month.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static ZonedDateTime createZonedDateTime (final int nYear, final Month eMonth, final int nDay)
  {
    return createZonedDateTime (createLocalDateTime (nYear, eMonth, nDay));
  }

  /**
   * Create a {@link ZonedDateTime} from year, month and day using UTC.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month. May not be <code>null</code>.
   * @param nDay
   *        The day of month.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static ZonedDateTime createZonedDateTimeUTC (final int nYear, final Month eMonth, final int nDay)
  {
    return createZonedDateTimeUTC (createLocalDateTime (nYear, eMonth, nDay));
  }

  /**
   * Create a {@link ZonedDateTime} from year, month, day, hour, minute and second using the default
   * time zone.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month. May not be <code>null</code>.
   * @param nDay
   *        The day of month.
   * @param nHour
   *        The hour of day.
   * @param nMinute
   *        The minute of hour.
   * @param nSecond
   *        The second of minute.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static ZonedDateTime createZonedDateTime (final int nYear,
                                                   @NonNull final Month eMonth,
                                                   final int nDay,
                                                   final int nHour,
                                                   final int nMinute,
                                                   final int nSecond)
  {
    return createZonedDateTime (createLocalDateTime (nYear, eMonth, nDay, nHour, nMinute, nSecond));
  }

  /**
   * Create a {@link ZonedDateTime} from year, month, day, hour, minute and second using UTC.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month. May not be <code>null</code>.
   * @param nDay
   *        The day of month.
   * @param nHour
   *        The hour of day.
   * @param nMinute
   *        The minute of hour.
   * @param nSecond
   *        The second of minute.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static ZonedDateTime createZonedDateTimeUTC (final int nYear,
                                                      @NonNull final Month eMonth,
                                                      final int nDay,
                                                      final int nHour,
                                                      final int nMinute,
                                                      final int nSecond)
  {
    return createZonedDateTimeUTC (createLocalDateTime (nYear, eMonth, nDay, nHour, nMinute, nSecond));
  }

  @NonNull
  private static ZonedDateTime _toZonedDateTime (@NonNull final Instant a)
  {
    return ZonedDateTime.ofInstant (a, _getZoneId ());
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link Instant} using the default time zone.
   *
   * @param a
   *        The instant to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final Instant a)
  {
    return a == null ? null : _toZonedDateTime (a);
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link Instant} using UTC.
   *
   * @param a
   *        The instant to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final Instant a)
  {
    return a == null ? null : ZonedDateTime.ofInstant (a, ZoneOffset.UTC);
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link GregorianCalendar}.
   *
   * @param aCal
   *        The calendar to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final GregorianCalendar aCal)
  {
    return aCal == null ? null : aCal.toZonedDateTime ();
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link Date}.
   *
   * @param a
   *        The date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final Date a)
  {
    return a == null ? null : _toOffsetDateTime (a).toZonedDateTime ();
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link Timestamp}.
   *
   * @param a
   *        The timestamp to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final Timestamp a)
  {
    return createZonedDateTime (createOffsetDateTime (a));
  }

  /**
   * Create a {@link ZonedDateTime} from the passed milliseconds since epoch using the default time
   * zone.
   *
   * @param nMillis
   *        The milliseconds since epoch.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static ZonedDateTime createZonedDateTime (final long nMillis)
  {
    return _toZonedDateTime (Instant.ofEpochMilli (nMillis));
  }

  /**
   * Create a {@link ZonedDateTime} from the passed milliseconds since epoch using UTC.
   *
   * @param nMillis
   *        The milliseconds since epoch.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static ZonedDateTime createZonedDateTimeUTC (final long nMillis)
  {
    return ZonedDateTime.ofInstant (Instant.ofEpochMilli (nMillis), ZoneOffset.UTC);
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link Number} (interpreted as milliseconds
   * since epoch) using the default time zone.
   *
   * @param a
   *        The number to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @NonNull
  public static ZonedDateTime createZonedDateTime (@Nullable final Number a)
  {
    return a == null ? null : createZonedDateTime (a.longValue ());
  }

  /**
   * Create a {@link ZonedDateTime} from the passed {@link Number} (interpreted as milliseconds
   * since epoch) using UTC.
   *
   * @param a
   *        The number to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @NonNull
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final Number a)
  {
    return a == null ? null : createZonedDateTimeUTC (a.longValue ());
  }

  // To OffsetDateTime

  /**
   * @return The current {@link OffsetDateTime} in the default time zone. Never
   *         <code>null</code>.
   */
  @Nonnegative
  public static OffsetDateTime getCurrentOffsetDateTime ()
  {
    return OffsetDateTime.now (_getZoneId ());
  }

  /**
   * @return The current {@link OffsetDateTime} in UTC. Never <code>null</code>.
   */
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

  /**
   * Create an {@link OffsetDateTime} from the passed {@link ZonedDateTime}.
   *
   * @param a
   *        The zoned date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final ZonedDateTime a)
  {
    return a == null ? null : a.toOffsetDateTime ();
  }

  @Nullable
  private static OffsetDateTime _toOffsetDateTime (@NonNull final LocalDateTime a)
  {
    return ZonedDateTime.of (a, _getZoneId ()).toOffsetDateTime ();
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link LocalDateTime} using the default time
   * zone.
   *
   * @param a
   *        The local date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final LocalDateTime a)
  {
    return a == null ? null : _toOffsetDateTime (a);
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link LocalDateTime} using UTC.
   *
   * @param a
   *        The local date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final LocalDateTime a)
  {
    return a == null ? null : OffsetDateTime.of (a, ZoneOffset.UTC);
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link LocalDate} using the default time zone.
   * The time is set to midnight.
   *
   * @param a
   *        The local date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final LocalDate a)
  {
    return a == null ? null : _toOffsetDateTime (a.atStartOfDay ());
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link LocalDate} using UTC. The time is set
   * to midnight.
   *
   * @param a
   *        The local date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final LocalDate a)
  {
    return a == null ? null : createOffsetDateTimeUTC (a.atStartOfDay ());
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link OffsetDate}. The time is set to
   * midnight.
   *
   * @param aOD
   *        The offset date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final OffsetDate aOD)
  {
    return aOD == null ? null : OffsetDateTime.of (aOD.toLocalDate ().atStartOfDay (), aOD.getOffset ());
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link XMLOffsetDate}.
   *
   * @param aOD
   *        The XML offset date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final XMLOffsetDate aOD)
  {
    return aOD == null ? null : createZonedDateTime (aOD).toOffsetDateTime ();
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link YearMonth} using the default time zone.
   *
   * @param a
   *        The year-month to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final YearMonth a)
  {
    return createOffsetDateTime (createLocalDateTime (a));
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link YearMonth} using UTC.
   *
   * @param a
   *        The year-month to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final YearMonth a)
  {
    return createOffsetDateTimeUTC (createLocalDateTime (a));
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link Year} using the default time zone.
   *
   * @param a
   *        The year to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final Year a)
  {
    return createOffsetDateTime (createLocalDateTime (a));
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link Year} using UTC.
   *
   * @param a
   *        The year to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final Year a)
  {
    return createOffsetDateTimeUTC (createLocalDateTime (a));
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link LocalTime} using the default time zone.
   *
   * @param a
   *        The local time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final LocalTime a)
  {
    return a == null ? null : _toOffsetDateTime (_toLocalDateTime (a));
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link LocalTime} using UTC.
   *
   * @param a
   *        The local time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final LocalTime a)
  {
    return a == null ? null : createOffsetDateTimeUTC (_toLocalDateTime (a));
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link OffsetTime}. The date is set to epoch
   * day 0.
   *
   * @param a
   *        The offset time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final OffsetTime a)
  {
    return a == null ? null : a.atDate (LocalDate.ofEpochDay (0));
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link XMLOffsetTime}. The date is set to
   * epoch day 0.
   *
   * @param a
   *        The XML offset time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final XMLOffsetTime a)
  {
    return a == null ? null : a.atDate (LocalDate.ofEpochDay (0));
  }

  /**
   * Create an {@link OffsetDateTime} from year, month and day using the default time zone.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month.
   * @param nDay
   *        The day of month.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static OffsetDateTime createOffsetDateTime (final int nYear, final Month eMonth, final int nDay)
  {
    return _toOffsetDateTime (createLocalDate (nYear, eMonth, nDay).atStartOfDay ());
  }

  /**
   * Create an {@link OffsetDateTime} from year, month and day using UTC.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month.
   * @param nDay
   *        The day of month.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static OffsetDateTime createOffsetDateTimeUTC (final int nYear, final Month eMonth, final int nDay)
  {
    return createOffsetDateTimeUTC (createLocalDate (nYear, eMonth, nDay).atStartOfDay ());
  }

  /**
   * Create an {@link OffsetDateTime} from date and time components using the default time zone.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month. May not be <code>null</code>.
   * @param nDay
   *        The day of month.
   * @param nHour
   *        The hour of day.
   * @param nMinute
   *        The minute of hour.
   * @param nSecond
   *        The second of minute.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static OffsetDateTime createOffsetDateTime (final int nYear,
                                                     @NonNull final Month eMonth,
                                                     final int nDay,
                                                     final int nHour,
                                                     final int nMinute,
                                                     final int nSecond)
  {
    return _toOffsetDateTime (createLocalDateTime (nYear, eMonth, nDay, nHour, nMinute, nSecond));
  }

  /**
   * Create an {@link OffsetDateTime} from date and time components with a specific zone offset.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month. May not be <code>null</code>.
   * @param nDay
   *        The day of month.
   * @param nHour
   *        The hour of day.
   * @param nMinute
   *        The minute of hour.
   * @param nSecond
   *        The second of minute.
   * @param aZoneOffset
   *        The zone offset to use. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static OffsetDateTime createOffsetDateTime (final int nYear,
                                                     @NonNull final Month eMonth,
                                                     final int nDay,
                                                     final int nHour,
                                                     final int nMinute,
                                                     final int nSecond,
                                                     @NonNull final ZoneOffset aZoneOffset)
  {
    return OffsetDateTime.of (createLocalDateTime (nYear, eMonth, nDay, nHour, nMinute, nSecond), aZoneOffset);
  }

  /**
   * Create an {@link OffsetDateTime} from date and time components using UTC.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month. May not be <code>null</code>.
   * @param nDay
   *        The day of month.
   * @param nHour
   *        The hour of day.
   * @param nMinute
   *        The minute of hour.
   * @param nSecond
   *        The second of minute.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static OffsetDateTime createOffsetDateTimeUTC (final int nYear,
                                                        @NonNull final Month eMonth,
                                                        final int nDay,
                                                        final int nHour,
                                                        final int nMinute,
                                                        final int nSecond)
  {
    return createOffsetDateTimeUTC (createLocalDateTime (nYear, eMonth, nDay, nHour, nMinute, nSecond));
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link Instant} using the default time zone.
   *
   * @param a
   *        The instant to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final Instant a)
  {
    return a == null ? null : OffsetDateTime.ofInstant (a, _getZoneId ());
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link Instant} using UTC.
   *
   * @param a
   *        The instant to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final Instant a)
  {
    return a == null ? null : OffsetDateTime.ofInstant (a, ZoneOffset.UTC);
  }

  @NonNull
  private static OffsetDateTime _toOffsetDateTime (@NonNull final Date a)
  {
    return a.toInstant ().atOffset (getZoneOffset (a));
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link Date}.
   *
   * @param a
   *        The date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final Date a)
  {
    return a == null ? null : _toOffsetDateTime (a);
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link Timestamp}.
   *
   * @param a
   *        The timestamp to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final Timestamp a)
  {
    return a == null ? null : _toOffsetDateTime (a);
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link GregorianCalendar}.
   *
   * @param aCal
   *        The calendar to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final GregorianCalendar aCal)
  {
    return aCal == null ? null : aCal.toZonedDateTime ().toOffsetDateTime ();
  }

  /**
   * Create an {@link OffsetDateTime} from the passed milliseconds since epoch using the default
   * time zone.
   *
   * @param nMillis
   *        The milliseconds since epoch.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static OffsetDateTime createOffsetDateTime (final long nMillis)
  {
    return OffsetDateTime.ofInstant (Instant.ofEpochMilli (nMillis), _getZoneId ());
  }

  /**
   * Create an {@link OffsetDateTime} from the passed milliseconds since epoch using UTC.
   *
   * @param nMillis
   *        The milliseconds since epoch.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static OffsetDateTime createOffsetDateTimeUTC (final long nMillis)
  {
    return OffsetDateTime.ofInstant (Instant.ofEpochMilli (nMillis), ZoneOffset.UTC);
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link Number} (interpreted as milliseconds
   * since epoch) using the default time zone.
   *
   * @param a
   *        The number to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final Number a)
  {
    return a == null ? null : createOffsetDateTime (a.longValue ());
  }

  /**
   * Create an {@link OffsetDateTime} from the passed {@link Number} (interpreted as milliseconds
   * since epoch) using UTC.
   *
   * @param a
   *        The number to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final Number a)
  {
    return a == null ? null : createOffsetDateTimeUTC (a.longValue ());
  }

  // To XMLOffsetDateTime

  /**
   * @return The current {@link XMLOffsetDateTime} in the default time zone.
   *         Never <code>null</code>.
   */
  @Nonnegative
  public static XMLOffsetDateTime getCurrentXMLOffsetDateTime ()
  {
    return XMLOffsetDateTime.now (_getZoneId ());
  }

  /**
   * @return The current {@link XMLOffsetDateTime} in UTC. Never
   *         <code>null</code>.
   */
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

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link ZonedDateTime}.
   *
   * @param a
   *        The zoned date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final ZonedDateTime a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a.toLocalDateTime (), a.getOffset ());
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link OffsetDateTime}.
   *
   * @param a
   *        The offset date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final OffsetDateTime a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a.toLocalDateTime (), a.getOffset ());
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link LocalDateTime} without an offset.
   *
   * @param a
   *        The local date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final LocalDateTime a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a, null);
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link LocalDateTime} using UTC.
   *
   * @param a
   *        The local date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (@Nullable final LocalDateTime a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a, ZoneOffset.UTC);
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link LocalDate} without an offset.
   *
   * @param a
   *        The local date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final LocalDate a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a.atStartOfDay (), null);
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link LocalDate} using UTC.
   *
   * @param a
   *        The local date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (@Nullable final LocalDate a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a.atStartOfDay (), ZoneOffset.UTC);
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link OffsetDate}.
   *
   * @param aOD
   *        The offset date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final OffsetDate aOD)
  {
    return aOD == null ? null : XMLOffsetDateTime.of (aOD.toLocalDate ().atStartOfDay (), aOD.getOffset ());
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link XMLOffsetDate}.
   *
   * @param aOD
   *        The XML offset date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final XMLOffsetDate aOD)
  {
    return aOD == null ? null : XMLOffsetDateTime.of (aOD.toLocalDate ().atStartOfDay (), aOD.getOffset ());
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link YearMonth} without an offset.
   *
   * @param a
   *        The year-month to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final YearMonth a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a.atDay (1).atStartOfDay (), null);
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link YearMonth} using UTC.
   *
   * @param a
   *        The year-month to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (@Nullable final YearMonth a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a.atDay (1).atStartOfDay (), ZoneOffset.UTC);
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link Year} without an offset.
   *
   * @param a
   *        The year to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final Year a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a.atDay (1).atStartOfDay (), null);
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link Year} using UTC.
   *
   * @param a
   *        The year to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (@Nullable final Year a)
  {
    return a == null ? null : XMLOffsetDateTime.of (a.atDay (1).atStartOfDay (), ZoneOffset.UTC);
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link LocalTime} without an offset.
   *
   * @param a
   *        The local time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final LocalTime a)
  {
    return a == null ? null : XMLOffsetDateTime.of (_toLocalDateTime (a), null);
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link LocalTime} using UTC.
   *
   * @param a
   *        The local time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (@Nullable final LocalTime a)
  {
    return a == null ? null : XMLOffsetDateTime.of (_toLocalDateTime (a), ZoneOffset.UTC);
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link OffsetTime}.
   *
   * @param a
   *        The offset time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final OffsetTime a)
  {
    return a == null ? null : XMLOffsetDateTime.of (_toLocalDateTime (a.toLocalTime ()), a.getOffset ());
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link XMLOffsetTime}. The date is set to
   * epoch day 0.
   *
   * @param a
   *        The XML offset time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final XMLOffsetTime a)
  {
    return a == null ? null : a.atXMLDate (LocalDate.ofEpochDay (0));
  }

  /**
   * Create an {@link XMLOffsetDateTime} from year, month and day without an offset.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month.
   * @param nDay
   *        The day of month.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static XMLOffsetDateTime createXMLOffsetDateTime (final int nYear, final Month eMonth, final int nDay)
  {
    return XMLOffsetDateTime.of (createLocalDate (nYear, eMonth, nDay).atStartOfDay (), null);
  }

  /**
   * Create an {@link XMLOffsetDateTime} from year, month and day using UTC.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month.
   * @param nDay
   *        The day of month.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (final int nYear, final Month eMonth, final int nDay)
  {
    return XMLOffsetDateTime.of (createLocalDate (nYear, eMonth, nDay).atStartOfDay (), ZoneOffset.UTC);
  }

  /**
   * Create an {@link XMLOffsetDateTime} from date and time components without an offset.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month. May not be <code>null</code>.
   * @param nDay
   *        The day of month.
   * @param nHour
   *        The hour of day.
   * @param nMinute
   *        The minute of hour.
   * @param nSecond
   *        The second of minute.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static XMLOffsetDateTime createXMLOffsetDateTime (final int nYear,
                                                           @NonNull final Month eMonth,
                                                           final int nDay,
                                                           final int nHour,
                                                           final int nMinute,
                                                           final int nSecond)
  {
    return XMLOffsetDateTime.of (createLocalDateTime (nYear, eMonth, nDay, nHour, nMinute, nSecond), null);
  }

  /**
   * Create an {@link XMLOffsetDateTime} from date and time components with a specific zone offset.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month. May not be <code>null</code>.
   * @param nDay
   *        The day of month.
   * @param nHour
   *        The hour of day.
   * @param nMinute
   *        The minute of hour.
   * @param nSecond
   *        The second of minute.
   * @param aZoneOffset
   *        The zone offset to use. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static XMLOffsetDateTime createXMLOffsetDateTime (final int nYear,
                                                           @NonNull final Month eMonth,
                                                           final int nDay,
                                                           final int nHour,
                                                           final int nMinute,
                                                           final int nSecond,
                                                           @Nullable final ZoneOffset aZoneOffset)
  {
    return XMLOffsetDateTime.of (createLocalDateTime (nYear, eMonth, nDay, nHour, nMinute, nSecond), aZoneOffset);
  }

  /**
   * Create an {@link XMLOffsetDateTime} from date and time components using UTC.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month. May not be <code>null</code>.
   * @param nDay
   *        The day of month.
   * @param nHour
   *        The hour of day.
   * @param nMinute
   *        The minute of hour.
   * @param nSecond
   *        The second of minute.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (final int nYear,
                                                              @NonNull final Month eMonth,
                                                              final int nDay,
                                                              final int nHour,
                                                              final int nMinute,
                                                              final int nSecond)
  {
    return XMLOffsetDateTime.of (createLocalDateTime (nYear, eMonth, nDay, nHour, nMinute, nSecond), ZoneOffset.UTC);
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link Instant} using the default time
   * zone.
   *
   * @param a
   *        The instant to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final Instant a)
  {
    return a == null ? null : XMLOffsetDateTime.ofInstant (a, _getZoneId ());
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link Instant} using UTC.
   *
   * @param a
   *        The instant to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (@Nullable final Instant a)
  {
    return a == null ? null : XMLOffsetDateTime.ofInstant (a, ZoneOffset.UTC);
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link Date}.
   *
   * @param a
   *        The date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final Date a)
  {
    return a == null ? null : XMLOffsetDateTime.ofInstant (a.toInstant (), getZoneOffset (a));
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link Timestamp}.
   *
   * @param a
   *        The timestamp to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final Timestamp a)
  {
    return a == null ? null : XMLOffsetDateTime.ofInstant (a.toInstant (), getZoneOffset (a));
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link GregorianCalendar}.
   *
   * @param aCal
   *        The calendar to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final GregorianCalendar aCal)
  {
    return aCal == null ? null : createXMLOffsetDateTime (aCal.toZonedDateTime ());
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed milliseconds since epoch using the default
   * time zone.
   *
   * @param nMillis
   *        The milliseconds since epoch.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static XMLOffsetDateTime createXMLOffsetDateTime (final long nMillis)
  {
    return XMLOffsetDateTime.ofInstant (Instant.ofEpochMilli (nMillis), _getZoneId ());
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed milliseconds since epoch using UTC.
   *
   * @param nMillis
   *        The milliseconds since epoch.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (final long nMillis)
  {
    return XMLOffsetDateTime.ofInstant (Instant.ofEpochMilli (nMillis), ZoneOffset.UTC);
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link Number} (interpreted as milliseconds
   * since epoch) using the default time zone.
   *
   * @param a
   *        The number to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTime (@Nullable final Number a)
  {
    return a == null ? null : createXMLOffsetDateTime (a.longValue ());
  }

  /**
   * Create an {@link XMLOffsetDateTime} from the passed {@link Number} (interpreted as milliseconds
   * since epoch) using UTC.
   *
   * @param a
   *        The number to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDateTime createXMLOffsetDateTimeUTC (@Nullable final Number a)
  {
    return a == null ? null : createXMLOffsetDateTimeUTC (a.longValue ());
  }

  // To LocalDateTime

  /**
   * @return The current {@link LocalDateTime} in the default time zone. Never
   *         <code>null</code>.
   */
  @Nonnegative
  public static LocalDateTime getCurrentLocalDateTime ()
  {
    return LocalDateTime.now (_getZoneId ());
  }

  /**
   * @return The current {@link LocalDateTime} in UTC. Never <code>null</code>.
   */
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

  /**
   * Create a {@link LocalDateTime} from the passed {@link ZonedDateTime}.
   *
   * @param aDT
   *        The zoned date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final ZonedDateTime aDT)
  {
    return aDT == null ? null : aDT.toLocalDateTime ();
  }

  /**
   * Create a {@link LocalDateTime} from the passed {@link OffsetDateTime}.
   *
   * @param aDT
   *        The offset date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final OffsetDateTime aDT)
  {
    return aDT == null ? null : aDT.toLocalDateTime ();
  }

  /**
   * Create a {@link LocalDateTime} from the passed {@link XMLOffsetDateTime}.
   *
   * @param aDT
   *        The XML offset date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final XMLOffsetDateTime aDT)
  {
    return aDT == null ? null : aDT.toLocalDateTime ();
  }

  /**
   * Create a {@link LocalDateTime} from the passed {@link OffsetDate}. The time is set to midnight.
   *
   * @param aDT
   *        The offset date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final OffsetDate aDT)
  {
    return aDT == null ? null : aDT.toLocalDate ().atStartOfDay ();
  }

  /**
   * Create a {@link LocalDateTime} from the passed {@link XMLOffsetDate}. The time is set to
   * midnight.
   *
   * @param aDT
   *        The XML offset date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final XMLOffsetDate aDT)
  {
    return aDT == null ? null : aDT.toLocalDate ().atStartOfDay ();
  }

  /**
   * Create a {@link LocalDateTime} from the passed {@link LocalDate}. The time is set to midnight.
   *
   * @param a
   *        The local date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final LocalDate a)
  {
    return a == null ? null : a.atStartOfDay ();
  }

  /**
   * Create a {@link LocalDateTime} from the passed {@link YearMonth}. Day is set to 1, time to
   * midnight.
   *
   * @param a
   *        The year-month to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final YearMonth a)
  {
    return a == null ? null : a.atDay (1).atStartOfDay ();
  }

  /**
   * Create a {@link LocalDateTime} from the passed {@link Year}. Day is set to 1, time to
   * midnight.
   *
   * @param a
   *        The year to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final Year a)
  {
    return a == null ? null : a.atDay (1).atStartOfDay ();
  }

  @NonNull
  private static LocalDateTime _toLocalDateTime (@NonNull final LocalTime a)
  {
    return a.atDate (LocalDate.ofEpochDay (0));
  }

  /**
   * Create a {@link LocalDateTime} from the passed {@link LocalTime}. The date is set to epoch
   * day 0.
   *
   * @param a
   *        The local time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final LocalTime a)
  {
    return a == null ? null : _toLocalDateTime (a);
  }

  /**
   * Create a {@link LocalDateTime} from the passed {@link OffsetTime}. The date is set to epoch
   * day 0.
   *
   * @param a
   *        The offset time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final OffsetTime a)
  {
    return a == null ? null : _toLocalDateTime (a.toLocalTime ());
  }

  /**
   * Create a {@link LocalDateTime} from the passed {@link XMLOffsetTime}. The date is set to epoch
   * day 0.
   *
   * @param a
   *        The XML offset time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final XMLOffsetTime a)
  {
    return a == null ? null : _toLocalDateTime (a.toLocalTime ());
  }

  /**
   * Create a {@link LocalDateTime} from year, month and day. Time is set to midnight.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month. May not be <code>null</code>.
   * @param nDay
   *        The day of month.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static LocalDateTime createLocalDateTime (final int nYear, @NonNull final Month eMonth, final int nDay)
  {
    return createLocalDateTime (nYear, eMonth, nDay, 0, 0, 0);
  }

  /**
   * Create a {@link LocalDateTime} from year, month, day, hour and minute. Second is set to 0.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month. May not be <code>null</code>.
   * @param nDay
   *        The day of month.
   * @param nHour
   *        The hour of day.
   * @param nMinute
   *        The minute of hour.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static LocalDateTime createLocalDateTime (final int nYear,
                                                   @NonNull final Month eMonth,
                                                   final int nDay,
                                                   final int nHour,
                                                   final int nMinute)
  {
    return createLocalDateTime (nYear, eMonth, nDay, nHour, nMinute, 0);
  }

  /**
   * Create a {@link LocalDateTime} from year, month, day, hour, minute and second.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month. May not be <code>null</code>.
   * @param nDay
   *        The day of month.
   * @param nHour
   *        The hour of day.
   * @param nMinute
   *        The minute of hour.
   * @param nSecond
   *        The second of minute.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static LocalDateTime createLocalDateTime (final int nYear,
                                                   @NonNull final Month eMonth,
                                                   final int nDay,
                                                   final int nHour,
                                                   final int nMinute,
                                                   final int nSecond)
  {
    return LocalDateTime.of (nYear, eMonth, nDay, nHour, nMinute, nSecond);
  }

  @NonNull
  private static LocalDateTime _toLocalDateTime (@Nullable final Instant a)
  {
    return LocalDateTime.ofInstant (a, _getZoneId ());
  }

  /**
   * Create a {@link LocalDateTime} from the passed {@link Instant} using the default time zone.
   *
   * @param a
   *        The instant to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final Instant a)
  {
    return a == null ? null : _toLocalDateTime (a);
  }

  /**
   * Create a {@link LocalDateTime} from the passed {@link Date} using the default time zone.
   *
   * @param a
   *        The date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final Date a)
  {
    return a == null ? null : _toLocalDateTime (a.toInstant ());
  }

  /**
   * Create a {@link LocalDateTime} from the passed {@link Timestamp}.
   *
   * @param a
   *        The timestamp to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final Timestamp a)
  {
    return a == null ? null : a.toLocalDateTime ();
  }

  /**
   * Create a {@link LocalDateTime} from the passed {@link GregorianCalendar}.
   *
   * @param aCal
   *        The calendar to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final GregorianCalendar aCal)
  {
    return aCal == null ? null : aCal.toZonedDateTime ().toLocalDateTime ();
  }

  /**
   * Create a {@link LocalDateTime} from the passed milliseconds since epoch using the default time
   * zone.
   *
   * @param nMillis
   *        The milliseconds since epoch.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static LocalDateTime createLocalDateTime (final long nMillis)
  {
    return _toLocalDateTime (Instant.ofEpochMilli (nMillis));
  }

  /**
   * Create a {@link LocalDateTime} from the passed {@link Number} (interpreted as milliseconds
   * since epoch) using the default time zone.
   *
   * @param a
   *        The number to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final Number a)
  {
    return a == null ? null : createLocalDateTime (a.longValue ());
  }

  // To LocalDate

  /**
   * @return The current {@link LocalDate} in the default time zone. Never
   *         <code>null</code>.
   */
  @Nonnegative
  public static LocalDate getCurrentLocalDate ()
  {
    return LocalDate.now (_getZoneId ());
  }

  /**
   * @return The current {@link LocalDate} in UTC. Never <code>null</code>.
   */
  @Nonnegative
  public static LocalDate getCurrentLocalDateUTC ()
  {
    return LocalDate.now (ZoneOffset.UTC);
  }

  /**
   * Create a {@link LocalDate} from year, month and day.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month. May not be <code>null</code>.
   * @param nDayOfMonth
   *        The day of month.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static LocalDate createLocalDate (final int nYear, @NonNull final Month eMonth, final int nDayOfMonth)
  {
    return LocalDate.of (nYear, eMonth, nDayOfMonth);
  }

  /**
   * Create a {@link LocalDate} from the passed {@link GregorianCalendar}.
   *
   * @param a
   *        The calendar to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDate createLocalDate (@Nullable final GregorianCalendar a)
  {
    return a == null ? null : a.toZonedDateTime ().toLocalDate ();
  }

  /**
   * Create a {@link LocalDate} from the passed milliseconds since epoch using the default time
   * zone.
   *
   * @param nMillis
   *        The milliseconds since epoch.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static LocalDate createLocalDate (final long nMillis)
  {
    return createLocalDateTime (nMillis).toLocalDate ();
  }

  /**
   * Create a {@link LocalDate} from the passed {@link Number} (interpreted as milliseconds since
   * epoch) using the default time zone.
   *
   * @param a
   *        The number to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @NonNull
  public static LocalDate createLocalDate (@Nullable final Number a)
  {
    return a == null ? null : createLocalDateTime (a.longValue ()).toLocalDate ();
  }

  /**
   * Create a {@link LocalDate} from the passed {@link Instant} using the default time zone.
   *
   * @param a
   *        The instant to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDate createLocalDate (@Nullable final Instant a)
  {
    return a == null ? null : _toLocalDateTime (a).toLocalDate ();
  }

  /**
   * Create a {@link LocalDate} from the passed {@link Date} using the default time zone.
   *
   * @param a
   *        The date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDate createLocalDate (@Nullable final Date a)
  {
    return a == null ? null : _toLocalDateTime (a.toInstant ()).toLocalDate ();
  }

  /**
   * Create a {@link LocalDate} from the passed {@link YearMonth}. Day is set to 1.
   *
   * @param a
   *        The year-month to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDate createLocalDate (@Nullable final YearMonth a)
  {
    return a == null ? null : a.atDay (1);
  }

  /**
   * Create a {@link LocalDate} from the passed {@link Year}. Day is set to 1.
   *
   * @param a
   *        The year to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDate createLocalDate (@Nullable final Year a)
  {
    return a == null ? null : a.atDay (1);
  }

  /**
   * Create a {@link LocalDate} from the passed {@link LocalDateTime}.
   *
   * @param a
   *        The local date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDate createLocalDate (@Nullable final LocalDateTime a)
  {
    return a == null ? null : a.toLocalDate ();
  }

  /**
   * Create a {@link LocalDate} from the passed {@link ZonedDateTime}.
   *
   * @param a
   *        The zoned date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDate createLocalDate (@Nullable final ZonedDateTime a)
  {
    return a == null ? null : a.toLocalDate ();
  }

  /**
   * Create a {@link LocalDate} from the passed {@link OffsetDateTime}.
   *
   * @param a
   *        The offset date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDate createLocalDate (@Nullable final OffsetDateTime a)
  {
    return a == null ? null : a.toLocalDate ();
  }

  /**
   * Create a {@link LocalDate} from the passed {@link XMLOffsetDateTime}.
   *
   * @param a
   *        The XML offset date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDate createLocalDate (@Nullable final XMLOffsetDateTime a)
  {
    return a == null ? null : a.toLocalDate ();
  }

  /**
   * Create a {@link LocalDate} from the passed {@link XMLOffsetDate}.
   *
   * @param a
   *        The XML offset date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDate createLocalDate (@Nullable final XMLOffsetDate a)
  {
    return a == null ? null : a.toLocalDate ();
  }

  // To OffsetDate

  /**
   * @return The current {@link OffsetDate} in the default time zone. Never
   *         <code>null</code>.
   */
  @Nonnegative
  public static OffsetDate getCurrentOffsetDate ()
  {
    return OffsetDate.now (_getZoneId ());
  }

  /**
   * @return The current {@link OffsetDate} in UTC. Never <code>null</code>.
   */
  @Nonnegative
  public static OffsetDate getCurrentOffsetDateUTC ()
  {
    return OffsetDate.now (ZoneOffset.UTC);
  }

  @NonNull
  private static ZoneOffset _getFallbackZoneOffset ()
  {
    return ZoneOffset.UTC;
  }

  @NonNull
  private static ZoneOffset _getFallbackZoneOffset (@Nullable final Instant aInstant)
  {
    if (aInstant == null)
      return _getFallbackZoneOffset ();
    // Works with Instance of LDT
    return _getZoneId ().getRules ().getOffset (aInstant);
  }

  @NonNull
  private static ZoneOffset _getFallbackZoneOffset (@Nullable final LocalDateTime aLDT)
  {
    if (aLDT == null)
      return _getFallbackZoneOffset ();
    // Works with Instance of LDT
    return _getZoneId ().getRules ().getOffset (aLDT);
  }

  /**
   * Create an {@link OffsetDate} from the passed {@link LocalDate} using the default time zone.
   *
   * @param a
   *        The local date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final LocalDate a)
  {
    return a == null ? null : OffsetDate.of (a, _getFallbackZoneOffset (a.atStartOfDay ()));
  }

  /**
   * Create an {@link OffsetDate} from the passed {@link LocalDate} with the specified offset.
   *
   * @param a
   *        The local date to convert. May be <code>null</code>.
   * @param aOffset
   *        The zone offset. May not be <code>null</code>.
   * @return <code>null</code> if the date parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final LocalDate a, @NonNull final ZoneOffset aOffset)
  {
    return a == null ? null : OffsetDate.of (a, aOffset);
  }

  /**
   * Create an {@link OffsetDate} from year, month and day using the default time zone.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month. May not be <code>null</code>.
   * @param nDayOfMonth
   *        The day of month.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static OffsetDate createOffsetDate (final int nYear, @NonNull final Month eMonth, final int nDayOfMonth)
  {
    final LocalDate aLD = createLocalDate (nYear, eMonth, nDayOfMonth);
    return OffsetDate.of (aLD, _getFallbackZoneOffset (aLD.atStartOfDay ()));
  }

  /**
   * Create an {@link OffsetDate} from year, month, day and zone offset.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month. May not be <code>null</code>.
   * @param nDayOfMonth
   *        The day of month.
   * @param aOffset
   *        The zone offset. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static OffsetDate createOffsetDate (final int nYear,
                                             @NonNull final Month eMonth,
                                             final int nDayOfMonth,
                                             @NonNull final ZoneOffset aOffset)
  {
    return OffsetDate.of (nYear, eMonth, nDayOfMonth, aOffset);
  }

  /**
   * Create an {@link OffsetDate} from the passed {@link LocalDateTime} using the default time zone.
   *
   * @param a
   *        The local date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final LocalDateTime a)
  {
    return a == null ? null : OffsetDate.of (a.toLocalDate (), _getFallbackZoneOffset (a));
  }

  /**
   * Create an {@link OffsetDate} from the passed {@link LocalDateTime} with the specified offset.
   *
   * @param a
   *        The local date time to convert. May be <code>null</code>.
   * @param aOffset
   *        The zone offset. May not be <code>null</code>.
   * @return <code>null</code> if the date time parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final LocalDateTime a, @NonNull final ZoneOffset aOffset)
  {
    return a == null ? null : OffsetDate.of (a.toLocalDate (), aOffset);
  }

  /**
   * Create an {@link OffsetDate} from the passed {@link GregorianCalendar}.
   *
   * @param a
   *        The calendar to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final GregorianCalendar a)
  {
    return a == null ? null : createOffsetDate (a.toZonedDateTime ());
  }

  /**
   * Create an {@link OffsetDate} from the passed {@link Instant} using the default time zone.
   *
   * @param a
   *        The instant to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final Instant a)
  {
    return a == null ? null : OffsetDate.ofInstant (a, _getZoneId ());
  }

  /**
   * Create an {@link OffsetDate} from the passed {@link Instant} with the specified offset.
   *
   * @param a
   *        The instant to convert. May be <code>null</code>.
   * @param aOffset
   *        The zone offset. May not be <code>null</code>.
   * @return <code>null</code> if the instant parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final Instant a, @NonNull final ZoneOffset aOffset)
  {
    return a == null ? null : OffsetDate.ofInstant (a, aOffset);
  }

  /**
   * Create an {@link OffsetDate} from the passed milliseconds since epoch using the default time
   * zone.
   *
   * @param nMillis
   *        The milliseconds since epoch.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static OffsetDate createOffsetDate (final long nMillis)
  {
    return createOffsetDate (Instant.ofEpochMilli (nMillis));
  }

  /**
   * Create an {@link OffsetDate} from the passed {@link Number} (interpreted as milliseconds since
   * epoch) using the default time zone.
   *
   * @param a
   *        The number to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final Number a)
  {
    return a == null ? null : createOffsetDate (a.longValue ());
  }

  /**
   * Create an {@link OffsetDate} from the passed {@link Date}.
   *
   * @param a
   *        The date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final Date a)
  {
    return a == null ? null : createOffsetDate (_toOffsetDateTime (a));
  }

  /**
   * Create an {@link OffsetDate} from the passed {@link YearMonth}. Day is set to 1.
   *
   * @param a
   *        The year-month to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final YearMonth a)
  {
    return a == null ? null : createOffsetDate (a.atDay (1));
  }

  /**
   * Create an {@link OffsetDate} from the passed {@link Year}. Day is set to 1.
   *
   * @param a
   *        The year to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final Year a)
  {
    return a == null ? null : createOffsetDate (a.atDay (1));
  }

  /**
   * Create an {@link OffsetDate} from the passed {@link OffsetDateTime}.
   *
   * @param a
   *        The offset date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final OffsetDateTime a)
  {
    return a == null ? null : OffsetDate.of (a.toLocalDate (), a.getOffset ());
  }

  /**
   * Create an {@link OffsetDate} from the passed {@link ZonedDateTime}.
   *
   * @param a
   *        The zoned date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final ZonedDateTime a)
  {
    return a == null ? null : OffsetDate.of (a.toLocalDate (), a.getOffset ());
  }

  // To XMLOffsetDate

  /**
   * @return The current {@link XMLOffsetDate} in the default time zone. Never
   *         <code>null</code>.
   */
  @Nonnegative
  public static XMLOffsetDate getCurrentXMLOffsetDate ()
  {
    return XMLOffsetDate.now (_getZoneId ());
  }

  /**
   * @return The current {@link XMLOffsetDate} in UTC. Never <code>null</code>.
   */
  @Nonnegative
  public static XMLOffsetDate getCurrentXMLOffsetDateUTC ()
  {
    return XMLOffsetDate.now (ZoneOffset.UTC);
  }

  /**
   * Create an {@link XMLOffsetDate} from the passed {@link ZonedDateTime}.
   *
   * @param a
   *        The zoned date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final ZonedDateTime a)
  {
    return a == null ? null : XMLOffsetDate.of (a.toLocalDate (), a.getOffset ());
  }

  /**
   * Create an {@link XMLOffsetDate} from the passed {@link OffsetDateTime}.
   *
   * @param a
   *        The offset date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final OffsetDateTime a)
  {
    return a == null ? null : XMLOffsetDate.of (a.toLocalDate (), a.getOffset ());
  }

  /**
   * Create an {@link XMLOffsetDate} from the passed {@link XMLOffsetDateTime}.
   *
   * @param a
   *        The XML offset date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final XMLOffsetDateTime a)
  {
    return a == null ? null : XMLOffsetDate.of (a.toLocalDate (), a.getOffset ());
  }

  /**
   * Create an {@link XMLOffsetDate} from the passed {@link LocalDate} without an offset.
   *
   * @param a
   *        The local date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final LocalDate a)
  {
    return createXMLOffsetDate (a, null);
  }

  /**
   * Create an {@link XMLOffsetDate} from the passed {@link LocalDate} with the specified offset.
   *
   * @param a
   *        The local date to convert. May be <code>null</code>.
   * @param aOffset
   *        The zone offset. May not be <code>null</code>.
   * @return <code>null</code> if the date parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final LocalDate a, @NonNull final ZoneOffset aOffset)
  {
    return a == null ? null : XMLOffsetDate.of (a, aOffset);
  }

  /**
   * Create an {@link XMLOffsetDate} from year, month and day without an offset.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month. May not be <code>null</code>.
   * @param nDayOfMonth
   *        The day of month.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static XMLOffsetDate createXMLOffsetDate (final int nYear, @NonNull final Month eMonth, final int nDayOfMonth)
  {
    return createXMLOffsetDate (nYear, eMonth, nDayOfMonth, null);
  }

  /**
   * Create an {@link XMLOffsetDate} from year, month, day and zone offset.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month. May not be <code>null</code>.
   * @param nDayOfMonth
   *        The day of month.
   * @param aOffset
   *        The zone offset. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static XMLOffsetDate createXMLOffsetDate (final int nYear,
                                                   @NonNull final Month eMonth,
                                                   final int nDayOfMonth,
                                                   @Nullable final ZoneOffset aOffset)
  {
    return XMLOffsetDate.of (nYear, eMonth, nDayOfMonth, aOffset);
  }

  /**
   * Create an {@link XMLOffsetDate} from the passed {@link LocalDateTime} without an offset.
   *
   * @param a
   *        The local date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final LocalDateTime a)
  {
    return createXMLOffsetDate (a, null);
  }

  /**
   * Create an {@link XMLOffsetDate} from the passed {@link LocalDateTime} with the specified
   * offset.
   *
   * @param a
   *        The local date time to convert. May be <code>null</code>.
   * @param aOffset
   *        The zone offset. May not be <code>null</code>.
   * @return <code>null</code> if the date time parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final LocalDateTime a, @NonNull final ZoneOffset aOffset)
  {
    return a == null ? null : XMLOffsetDate.of (a.toLocalDate (), aOffset);
  }

  /**
   * Create an {@link XMLOffsetDate} from the passed {@link Instant} using the default time zone.
   *
   * @param a
   *        The instant to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final Instant a)
  {
    return a == null ? null : XMLOffsetDate.ofInstant (a, _getZoneId ());
  }

  /**
   * Create an {@link XMLOffsetDate} from the passed {@link Instant} using the specified zone ID.
   *
   * @param a
   *        The instant to convert. May be <code>null</code>.
   * @param aZoneId
   *        The zone ID. May not be <code>null</code>.
   * @return <code>null</code> if the instant parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final Instant a, @NonNull final ZoneId aZoneId)
  {
    return a == null ? null : XMLOffsetDate.ofInstant (a, aZoneId);
  }

  /**
   * Create an {@link XMLOffsetDate} from the passed {@link GregorianCalendar}.
   *
   * @param a
   *        The calendar to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final GregorianCalendar a)
  {
    return a == null ? null : createXMLOffsetDate (a.toZonedDateTime ());
  }

  /**
   * Create an {@link XMLOffsetDate} from the passed milliseconds since epoch using the default time
   * zone.
   *
   * @param nMillis
   *        The milliseconds since epoch.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static XMLOffsetDate createXMLOffsetDate (final long nMillis)
  {
    return createXMLOffsetDate (Instant.ofEpochMilli (nMillis));
  }

  /**
   * Create an {@link XMLOffsetDate} from the passed {@link Date}.
   *
   * @param a
   *        The date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final Date a)
  {
    return a == null ? null : createXMLOffsetDate (_toOffsetDateTime (a));
  }

  /**
   * Create an {@link XMLOffsetDate} from the passed {@link YearMonth}. Day is set to 1.
   *
   * @param a
   *        The year-month to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final YearMonth a)
  {
    return a == null ? null : createXMLOffsetDate (a.atDay (1));
  }

  /**
   * Create an {@link XMLOffsetDate} from the passed {@link Year}. Day is set to 1.
   *
   * @param a
   *        The year to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final Year a)
  {
    return a == null ? null : createXMLOffsetDate (a.atDay (1));
  }

  /**
   * Create an {@link XMLOffsetDate} from the passed {@link Number} (interpreted as milliseconds
   * since epoch) using the default time zone.
   *
   * @param a
   *        The number to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final Number a)
  {
    return a == null ? null : createXMLOffsetDate (a.longValue ());
  }

  // LocalTime

  /**
   * @return The current {@link LocalTime} in the default time zone. Never
   *         <code>null</code>.
   */
  @Nonnegative
  public static LocalTime getCurrentLocalTime ()
  {
    return LocalTime.now (_getZoneId ());
  }

  /**
   * @return The current {@link LocalTime} in UTC. Never <code>null</code>.
   */
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

  /**
   * Create a {@link LocalTime} from the passed {@link GregorianCalendar}.
   *
   * @param a
   *        The calendar to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalTime createLocalTime (@Nullable final GregorianCalendar a)
  {
    return a == null ? null : a.toZonedDateTime ().toLocalTime ();
  }

  /**
   * Create a {@link LocalTime} from the passed milliseconds since epoch using the default time
   * zone.
   *
   * @param nMillis
   *        The milliseconds since epoch.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static LocalTime createLocalTime (final long nMillis)
  {
    return createLocalDateTime (nMillis).toLocalTime ();
  }

  /**
   * Create a {@link LocalTime} from the passed {@link Instant} using the default time zone.
   *
   * @param a
   *        The instant to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalTime createLocalTime (@Nullable final Instant a)
  {
    return a == null ? null : createLocalDateTime (a).toLocalTime ();
  }

  /**
   * Create a {@link LocalTime} from the passed {@link Date} using the default time zone.
   *
   * @param a
   *        The date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalTime createLocalTime (@Nullable final Date a)
  {
    return a == null ? null : createLocalTime (a.toInstant ());
  }

  /**
   * Create a {@link LocalTime} from the passed {@link LocalDateTime}.
   *
   * @param a
   *        The local date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalTime createLocalTime (@Nullable final LocalDateTime a)
  {
    return a == null ? null : a.toLocalTime ();
  }

  /**
   * Create a {@link LocalTime} from the passed {@link OffsetTime}.
   *
   * @param a
   *        The offset time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalTime createLocalTime (@Nullable final OffsetTime a)
  {
    return a == null ? null : a.toLocalTime ();
  }

  /**
   * Create a {@link LocalTime} from the passed {@link XMLOffsetTime}.
   *
   * @param a
   *        The XML offset time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalTime createLocalTime (@Nullable final XMLOffsetTime a)
  {
    return a == null ? null : a.toLocalTime ();
  }

  /**
   * Create a {@link LocalTime} from hour, minute and second.
   *
   * @param nHour
   *        The hour of day.
   * @param nMinute
   *        The minute of hour.
   * @param nSecond
   *        The second of minute.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static LocalTime createLocalTime (final int nHour, final int nMinute, final int nSecond)
  {
    return LocalTime.of (nHour, nMinute, nSecond);
  }

  // to OffsetTime

  /**
   * @return The current {@link OffsetTime} in the default time zone. Never <code>null</code>.
   */
  @Nonnegative
  public static OffsetTime getCurrentOffsetTime ()
  {
    return OffsetTime.now (_getZoneId ());
  }

  /**
   * @return The current {@link OffsetTime} in UTC. Never <code>null</code>.
   */
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

  /**
   * Create an {@link OffsetTime} from the passed {@link GregorianCalendar}.
   *
   * @param a
   *        The calendar to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final GregorianCalendar a)
  {
    return a == null ? null : a.toZonedDateTime ().toOffsetDateTime ().toOffsetTime ();
  }

  /**
   * Create an {@link OffsetTime} from the passed milliseconds since epoch using the default time
   * zone.
   *
   * @param nMillis
   *        The milliseconds since epoch.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static OffsetTime createOffsetTime (final long nMillis)
  {
    return createOffsetDateTime (nMillis).toOffsetTime ();
  }

  /**
   * Create an {@link OffsetTime} from the passed {@link Instant} using the default time zone.
   *
   * @param a
   *        The instant to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final Instant a)
  {
    return a == null ? null : createOffsetDateTime (a).toOffsetTime ();
  }

  /**
   * Create an {@link OffsetTime} from the passed {@link Date}.
   *
   * @param a
   *        The date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final Date a)
  {
    return a == null ? null : createOffsetTime (a.toInstant ());
  }

  /**
   * Create an {@link OffsetTime} from the passed {@link OffsetDateTime}.
   *
   * @param a
   *        The offset date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final OffsetDateTime a)
  {
    return a == null ? null : a.toOffsetTime ();
  }

  /**
   * Create an {@link OffsetTime} from the passed {@link ZonedDateTime}.
   *
   * @param a
   *        The zoned date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final ZonedDateTime a)
  {
    return a == null ? null : a.toOffsetDateTime ().toOffsetTime ();
  }

  /**
   * Create an {@link OffsetTime} from the passed {@link LocalDateTime} using the default time zone.
   *
   * @param a
   *        The local date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final LocalDateTime a)
  {
    return a == null ? null : a.toLocalTime ().atOffset (_getFallbackZoneOffset (a));
  }

  /**
   * Create an {@link OffsetTime} from the passed {@link LocalDateTime} with the specified offset.
   *
   * @param a
   *        The local date time to convert. May be <code>null</code>.
   * @param aOfs
   *        The zone offset. May not be <code>null</code>.
   * @return <code>null</code> if the date time parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final LocalDateTime a, @NonNull final ZoneOffset aOfs)
  {
    return a == null ? null : a.toLocalTime ().atOffset (aOfs);
  }

  /**
   * Create an {@link OffsetTime} from the passed {@link LocalTime} using the default time zone.
   *
   * @param a
   *        The local time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final LocalTime a)
  {
    return a == null ? null : a.atOffset (_getFallbackZoneOffset ());
  }

  /**
   * Create an {@link OffsetTime} from the passed {@link LocalTime} with the specified offset.
   *
   * @param a
   *        The local time to convert. May be <code>null</code>.
   * @param aOfs
   *        The zone offset. May not be <code>null</code>.
   * @return <code>null</code> if the time parameter is <code>null</code>.
   */
  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final LocalTime a, @NonNull final ZoneOffset aOfs)
  {
    return a == null ? null : a.atOffset (aOfs);
  }

  /**
   * Create an {@link OffsetTime} from hour, minute, second and zone offset.
   *
   * @param nHour
   *        The hour of day.
   * @param nMinute
   *        The minute of hour.
   * @param nSecond
   *        The second of minute.
   * @param aOfs
   *        The zone offset. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static OffsetTime createOffsetTime (final int nHour,
                                             final int nMinute,
                                             final int nSecond,
                                             @NonNull final ZoneOffset aOfs)
  {
    return OffsetTime.of (nHour, nMinute, nSecond, 0, aOfs);
  }

  // to XMLOffsetTime

  /**
   * @return The current {@link XMLOffsetTime} in the default time zone. Never <code>null</code>.
   */
  @Nonnegative
  public static XMLOffsetTime getCurrentXMLOffsetTime ()
  {
    return XMLOffsetTime.now (_getZoneId ());
  }

  /**
   * @return The current {@link XMLOffsetTime} in UTC. Never <code>null</code>.
   */
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

  /**
   * Create an {@link XMLOffsetTime} from the passed {@link GregorianCalendar}.
   *
   * @param a
   *        The calendar to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetTime createXMLOffsetTime (@Nullable final GregorianCalendar a)
  {
    return a == null ? null : XMLOffsetTime.of (a.toZonedDateTime ().toOffsetDateTime ().toOffsetTime ());
  }

  /**
   * Create an {@link XMLOffsetTime} from the passed milliseconds since epoch using the default time
   * zone.
   *
   * @param nMillis
   *        The milliseconds since epoch.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static XMLOffsetTime createXMLOffsetTime (final long nMillis)
  {
    return createXMLOffsetDateTime (nMillis).toXMLOffsetTime ();
  }

  /**
   * Create an {@link XMLOffsetTime} from the passed {@link Instant} using the default time zone.
   *
   * @param a
   *        The instant to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetTime createXMLOffsetTime (@Nullable final Instant a)
  {
    return a == null ? null : createXMLOffsetDateTime (a).toXMLOffsetTime ();
  }

  /**
   * Create an {@link XMLOffsetTime} from the passed {@link Date}.
   *
   * @param a
   *        The date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetTime createXMLOffsetTime (@Nullable final Date a)
  {
    return a == null ? null : createXMLOffsetTime (a.toInstant ());
  }

  /**
   * Create an {@link XMLOffsetTime} from the passed {@link OffsetDateTime}.
   *
   * @param a
   *        The offset date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetTime createXMLOffsetTime (@Nullable final OffsetDateTime a)
  {
    return a == null ? null : XMLOffsetTime.of (a.toOffsetTime ());
  }

  /**
   * Create an {@link XMLOffsetTime} from the passed {@link ZonedDateTime}.
   *
   * @param a
   *        The zoned date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetTime createXMLOffsetTime (@Nullable final ZonedDateTime a)
  {
    return a == null ? null : XMLOffsetTime.of (a.toOffsetDateTime ().toOffsetTime ());
  }

  /**
   * Create an {@link XMLOffsetTime} from the passed {@link LocalDateTime} without an offset.
   *
   * @param a
   *        The local date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetTime createXMLOffsetTime (@Nullable final LocalDateTime a)
  {
    return a == null ? null : XMLOffsetTime.of (a.toLocalTime (), null);
  }

  /**
   * Create an {@link XMLOffsetTime} from the passed {@link LocalDateTime} with the specified
   * offset.
   *
   * @param a
   *        The local date time to convert. May be <code>null</code>.
   * @param aOfs
   *        The zone offset. May be <code>null</code>.
   * @return <code>null</code> if the date time parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetTime createXMLOffsetTime (@Nullable final LocalDateTime a, @Nullable final ZoneOffset aOfs)
  {
    return a == null ? null : XMLOffsetTime.of (a.toLocalTime (), aOfs);
  }

  /**
   * Create an {@link XMLOffsetTime} from the passed {@link LocalTime} without an offset.
   *
   * @param a
   *        The local time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetTime createXMLOffsetTime (@Nullable final LocalTime a)
  {
    return a == null ? null : XMLOffsetTime.of (a, null);
  }

  /**
   * Create an {@link XMLOffsetTime} from the passed {@link LocalTime} with the specified offset.
   *
   * @param a
   *        The local time to convert. May be <code>null</code>.
   * @param aOfs
   *        The zone offset. May be <code>null</code>.
   * @return <code>null</code> if the time parameter is <code>null</code>.
   */
  @Nullable
  public static XMLOffsetTime createXMLOffsetTime (@Nullable final LocalTime a, @Nullable final ZoneOffset aOfs)
  {
    return a == null ? null : XMLOffsetTime.of (a, aOfs);
  }

  /**
   * Create an {@link XMLOffsetTime} from hour, minute and second without an offset.
   *
   * @param nHour
   *        The hour of day.
   * @param nMinute
   *        The minute of hour.
   * @param nSecond
   *        The second of minute.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static XMLOffsetTime createXMLOffsetTime (final int nHour, final int nMinute, final int nSecond)
  {
    return createXMLOffsetTime (nHour, nMinute, nSecond, null);
  }

  /**
   * Create an {@link XMLOffsetTime} from hour, minute, second and zone offset.
   *
   * @param nHour
   *        The hour of day.
   * @param nMinute
   *        The minute of hour.
   * @param nSecond
   *        The second of minute.
   * @param aOfs
   *        The zone offset. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static XMLOffsetTime createXMLOffsetTime (final int nHour,
                                                   final int nMinute,
                                                   final int nSecond,
                                                   @Nullable final ZoneOffset aOfs)
  {
    return XMLOffsetTime.of (nHour, nMinute, nSecond, 0, aOfs);
  }

  // to Date

  /**
   * Create a {@link Date} from the passed {@link ZonedDateTime}. Note that timezone details are
   * lost.
   *
   * @param a
   *        The zoned date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static Date createDate (@Nullable final ZonedDateTime a)
  {
    // The timezone details gets lost here
    return a == null ? null : Date.from (a.toInstant ());
  }

  /**
   * Create a {@link Date} from the passed {@link OffsetDateTime}. Note that timezone details are
   * lost.
   *
   * @param a
   *        The offset date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static Date createDate (@Nullable final OffsetDateTime a)
  {
    // The timezone details gets lost here
    return a == null ? null : Date.from (a.toInstant ());
  }

  /**
   * Create a {@link Date} from the passed {@link LocalDateTime} using the default time zone.
   *
   * @param a
   *        The local date time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static Date createDate (@Nullable final LocalDateTime a)
  {
    return createDate (createZonedDateTime (a));
  }

  /**
   * Create a {@link Date} from the passed {@link LocalDate} using the default time zone.
   *
   * @param a
   *        The local date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static Date createDate (@Nullable final LocalDate a)
  {
    return createDate (createZonedDateTime (a));
  }

  /**
   * Create a {@link Date} from the passed {@link OffsetDate}.
   *
   * @param aOD
   *        The offset date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static Date createDate (@Nullable final OffsetDate aOD)
  {
    return createDate (createZonedDateTime (aOD));
  }

  /**
   * Create a {@link Date} from the passed {@link XMLOffsetDate}.
   *
   * @param aOD
   *        The XML offset date to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static Date createDate (@Nullable final XMLOffsetDate aOD)
  {
    return createDate (createZonedDateTime (aOD));
  }

  /**
   * Create a {@link Date} from the passed {@link LocalTime} using the default time zone.
   *
   * @param a
   *        The local time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static Date createDate (@Nullable final LocalTime a)
  {
    return createDate (createZonedDateTime (a));
  }

  /**
   * Create a {@link Date} from the passed {@link OffsetTime}.
   *
   * @param a
   *        The offset time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static Date createDate (@Nullable final OffsetTime a)
  {
    return createDate (createZonedDateTime (a));
  }

  /**
   * Create a {@link Date} from the passed {@link XMLOffsetTime}.
   *
   * @param a
   *        The XML offset time to convert. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static Date createDate (@Nullable final XMLOffsetTime a)
  {
    return createDate (createZonedDateTime (a));
  }

  /**
   * Create a {@link Date} from year, month and day.
   *
   * @param nYear
   *        The year.
   * @param eMonth
   *        The month. May not be <code>null</code>.
   * @param nDayOfMonth
   *        The day of month.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static Date createDateForDate (final int nYear, @NonNull final Month eMonth, final int nDayOfMonth)
  {
    return createDate (createLocalDate (nYear, eMonth, nDayOfMonth));
  }

  /**
   * Create a {@link Date} from hour, minute and second.
   *
   * @param nHour
   *        The hour of day.
   * @param nMin
   *        The minute of hour.
   * @param nSec
   *        The second of minute.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static Date createDateForTime (final int nHour, final int nMin, final int nSec)
  {
    return createDate (createLocalTime (nHour, nMin, nSec));
  }

  /**
   * @return A new {@link Calendar} using the default time zone and the default locale. Never
   *         <code>null</code>.
   */
  @NonNull
  public static Calendar createCalendar ()
  {
    return Calendar.getInstance (PDTConfig.getDefaultTimeZone (), Locale.getDefault (Locale.Category.FORMAT));
  }

  /**
   * @return A new {@link Calendar} using UTC and the default locale. Never <code>null</code>.
   */
  @NonNull
  public static Calendar createCalendarUTC ()
  {
    return Calendar.getInstance (PDTConfig.getUTCTimeZone (), Locale.getDefault (Locale.Category.FORMAT));
  }

  /**
   * @return A new {@link GregorianCalendar} using the default time zone and the default locale.
   *         Never <code>null</code>.
   */
  @NonNull
  public static GregorianCalendar createGregorianCalendar ()
  {
    return new GregorianCalendar (PDTConfig.getDefaultTimeZone (), Locale.getDefault (Locale.Category.FORMAT));
  }

  /**
   * @return A new {@link GregorianCalendar} using UTC and the default locale. Never
   *         <code>null</code>.
   */
  @NonNull
  public static GregorianCalendar createGregorianCalendarUTC ()
  {
    return new GregorianCalendar (PDTConfig.getUTCTimeZone (), Locale.getDefault (Locale.Category.FORMAT));
  }

  // Misc

  /**
   * @return The current year in the default time zone.
   */
  @Nonnegative
  public static int getCurrentYear ()
  {
    return getCurrentLocalDate ().getYear ();
  }

  /**
   * @return The current year in UTC.
   */
  @Nonnegative
  public static int getCurrentYearUTC ()
  {
    return getCurrentLocalDateUTC ().getYear ();
  }

  /**
   * @return The current {@link Year} in the default time zone. Never <code>null</code>.
   */
  @NonNull
  public static Year getCurrentYearObj ()
  {
    return Year.now (_getZoneId ());
  }

  /**
   * @return The current {@link Year} in UTC. Never <code>null</code>.
   */
  @NonNull
  public static Year getCurrentYearObjUTC ()
  {
    return Year.now (ZoneOffset.UTC);
  }

  /**
   * @return The current {@link MonthDay} in the default time zone. Never <code>null</code>.
   */
  @NonNull
  public static MonthDay getCurrentMonthDay ()
  {
    return MonthDay.now (_getZoneId ());
  }

  /**
   * @return The current {@link MonthDay} in UTC. Never <code>null</code>.
   */
  @NonNull
  public static MonthDay getCurrentMonthDayUTC ()
  {
    return MonthDay.now (ZoneOffset.UTC);
  }

  /**
   * @return The current {@link YearMonth} in the default time zone. Never <code>null</code>.
   */
  @Nonnegative
  public static YearMonth getCurrentYearMonth ()
  {
    return YearMonth.now (_getZoneId ());
  }

  /**
   * @return The current {@link YearMonth} in UTC. Never <code>null</code>.
   */
  @Nonnegative
  public static YearMonth getCurrentYearMonthUTC ()
  {
    return YearMonth.now (ZoneOffset.UTC);
  }

  /**
   * @return The current {@link Instant} in the default time zone. Never <code>null</code>.
   */
  @Nonnegative
  public static Instant getCurrentInstant ()
  {
    return Instant.now (Clock.system (_getZoneId ()));
  }

  /**
   * @return The current {@link Instant} in UTC. Never <code>null</code>.
   */
  @Nonnegative
  public static Instant getCurrentInstantUTC ()
  {
    return Instant.now (Clock.system (ZoneOffset.UTC));
  }

  /**
   * @return The current time in milliseconds since epoch in the default time zone.
   */
  public static long getCurrentMillis ()
  {
    return getCurrentInstant ().toEpochMilli ();
  }

  /**
   * @return The current time in milliseconds since epoch in UTC.
   */
  public static long getCurrentMillisUTC ()
  {
    return getCurrentInstantUTC ().toEpochMilli ();
  }

  /**
   * Get the milliseconds since epoch of the passed {@link LocalDate}.
   *
   * @param a
   *        The local date. May not be <code>null</code>.
   * @return The epoch millis.
   */
  public static long getMillis (@NonNull final LocalDate a)
  {
    return getMillis (createZonedDateTime (a));
  }

  /**
   * Get the milliseconds since epoch of the passed {@link OffsetDate}.
   *
   * @param aOD
   *        The offset date. May not be <code>null</code>.
   * @return The epoch millis.
   */
  public static long getMillis (@NonNull final OffsetDate aOD)
  {
    return getMillis (createZonedDateTime (aOD));
  }

  /**
   * Get the milliseconds since epoch of the passed {@link XMLOffsetDate}.
   *
   * @param aOD
   *        The XML offset date. May not be <code>null</code>.
   * @return The epoch millis.
   */
  public static long getMillis (@NonNull final XMLOffsetDate aOD)
  {
    return getMillis (createZonedDateTime (aOD));
  }

  /**
   * Get the milliseconds since epoch of the passed {@link LocalTime}.
   *
   * @param a
   *        The local time. May not be <code>null</code>.
   * @return The epoch millis.
   */
  public static long getMillis (@NonNull final LocalTime a)
  {
    return getMillis (createZonedDateTime (a));
  }

  /**
   * Get the milliseconds since epoch of the passed {@link OffsetTime}.
   *
   * @param a
   *        The offset time. May not be <code>null</code>.
   * @return The epoch millis.
   */
  public static long getMillis (@NonNull final OffsetTime a)
  {
    return getMillis (createZonedDateTime (a));
  }

  /**
   * Get the milliseconds since epoch of the passed {@link XMLOffsetTime}.
   *
   * @param a
   *        The XML offset time. May not be <code>null</code>.
   * @return The epoch millis.
   */
  public static long getMillis (@NonNull final XMLOffsetTime a)
  {
    return getMillis (createZonedDateTime (a));
  }

  /**
   * Get the milliseconds since epoch of the passed {@link LocalDateTime}.
   *
   * @param a
   *        The local date time. May not be <code>null</code>.
   * @return The epoch millis.
   */
  public static long getMillis (@NonNull final LocalDateTime a)
  {
    return getMillis (createZonedDateTime (a));
  }

  /**
   * Get the milliseconds since epoch of the passed {@link OffsetDateTime}.
   *
   * @param a
   *        The offset date time. May not be <code>null</code>.
   * @return The epoch millis.
   */
  public static long getMillis (@NonNull final OffsetDateTime a)
  {
    return a.toInstant ().toEpochMilli ();
  }

  /**
   * Get the milliseconds since epoch of the passed {@link XMLOffsetDateTime}.
   *
   * @param a
   *        The XML offset date time. May not be <code>null</code>.
   * @return The epoch millis.
   */
  public static long getMillis (@NonNull final XMLOffsetDateTime a)
  {
    return a.toInstant ().toEpochMilli ();
  }

  /**
   * Get the milliseconds since epoch of the passed {@link ZonedDateTime}.
   *
   * @param a
   *        The zoned date time. May not be <code>null</code>.
   * @return The epoch millis.
   */
  public static long getMillis (@NonNull final ZonedDateTime a)
  {
    return a.toInstant ().toEpochMilli ();
  }
}
