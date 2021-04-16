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
import java.time.temporal.ChronoField;
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
   * @param aDate
   *        The date to use. May not be <code>null</code>.
   * @return 0 for no offset to UTC, the minutes otherwise. Usually in 60minutes
   *         steps :)
   */
  @SuppressWarnings ("deprecation")
  public static int getTimezoneOffsetInMinutes (@Nonnull final Date aDate)
  {
    return -aDate.getTimezoneOffset ();
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
    return ZoneOffset.ofHoursMinutes (nOffsetInMinutes / CGlobal.MINUTES_PER_HOUR, nOffsetInMinutes % CGlobal.MINUTES_PER_HOUR);
  }

  @Nonnull
  public static ZoneOffset getZoneOffset (@Nonnull final Date aDate)
  {
    final int nOffsetMin = getTimezoneOffsetInMinutes (aDate);
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
  public static ZoneId getZoneId (@Nonnull final Date aDate)
  {
    final int nOffsetMin = getTimezoneOffsetInMinutes (aDate);
    return getZoneIdFromOffsetInMinutes (nOffsetMin);
  }

  @Nonnull
  public static TimeZone getTimeZone (@Nonnull final Date aDate)
  {
    final ZoneId aZI = getZoneId (aDate);
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
   * @param aODT
   *        Source date time. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>, the local
   *         date time with microseconds and nanoseconds set to 0 otherwise.
   * @since 9.2.0
   */
  @Nullable
  public static ZonedDateTime getWithMillisOnly (@Nullable final ZonedDateTime aODT)
  {
    return aODT == null ? null : aODT.withNano (aODT.get (ChronoField.MILLI_OF_SECOND) * (int) CGlobal.NANOSECONDS_PER_MILLISECOND);
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
  public static ZonedDateTime createZonedDateTime (@Nullable final OffsetDateTime aODT)
  {
    return aODT == null ? null : aODT.toZonedDateTime ();
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final LocalDateTime aLDT)
  {
    return aLDT == null ? null : ZonedDateTime.of (aLDT, _getZoneId ());
  }

  @Nullable
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final LocalDateTime aLDT)
  {
    return aLDT == null ? null : ZonedDateTime.of (aLDT, ZoneOffset.UTC);
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final LocalDate aLD)
  {
    return createZonedDateTime (createLocalDateTime (aLD));
  }

  @Nullable
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final LocalDate aLD)
  {
    return createZonedDateTimeUTC (createLocalDateTime (aLD));
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final OffsetDate aOD)
  {
    return aOD == null ? null : ZonedDateTime.of (aOD.toLocalDate ().atStartOfDay (), aOD.getOffset ());
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final XMLOffsetDate aOD)
  {
    return aOD == null ? null : ZonedDateTime.of (aOD.toLocalDate ().atStartOfDay (), aOD.hasOffset () ? aOD.getOffset () : _getZoneId ());
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final YearMonth aYM)
  {
    return createZonedDateTime (createLocalDateTime (aYM));
  }

  @Nullable
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final YearMonth aYM)
  {
    return createZonedDateTimeUTC (createLocalDateTime (aYM));
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final Year aYear)
  {
    return createZonedDateTime (createLocalDateTime (aYear));
  }

  @Nullable
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final Year aYear)
  {
    return createZonedDateTimeUTC (createLocalDateTime (aYear));
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final LocalTime aLT)
  {
    return createZonedDateTime (createLocalDateTime (aLT));
  }

  @Nullable
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final LocalTime aLT)
  {
    return createZonedDateTimeUTC (createLocalDateTime (aLT));
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final OffsetTime aLT)
  {
    return createZonedDateTime (createOffsetDateTime (aLT));
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

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final Instant aInstant)
  {
    return aInstant == null ? null : ZonedDateTime.ofInstant (aInstant, _getZoneId ());
  }

  @Nullable
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final Instant aInstant)
  {
    return aInstant == null ? null : ZonedDateTime.ofInstant (aInstant, ZoneOffset.UTC);
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final GregorianCalendar aCal)
  {
    return aCal == null ? null : aCal.toZonedDateTime ();
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final Date aDate)
  {
    return createZonedDateTime (createOffsetDateTime (aDate));
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final Timestamp aTimestamp)
  {
    return createZonedDateTime (createOffsetDateTime (aTimestamp));
  }

  @Nonnull
  public static ZonedDateTime createZonedDateTime (final long nMillis)
  {
    return createZonedDateTime (createLocalDateTime (nMillis));
  }

  @Nonnull
  public static ZonedDateTime createZonedDateTimeUTC (final long nMillis)
  {
    return createZonedDateTimeUTC (createLocalDateTime (nMillis));
  }

  @Nonnull
  public static ZonedDateTime createZonedDateTime (@Nullable final Number aMillis)
  {
    return createZonedDateTime (createLocalDateTime (aMillis));
  }

  @Nonnull
  public static ZonedDateTime createZonedDateTimeUTC (@Nullable final Number aMillis)
  {
    return createZonedDateTimeUTC (createLocalDateTime (aMillis));
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
   * @param aODT
   *        Source date time. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>, the local
   *         date time with microseconds and nanoseconds set to 0 otherwise.
   * @since 9.2.0
   */
  @Nullable
  public static OffsetDateTime getWithMillisOnly (@Nullable final OffsetDateTime aODT)
  {
    return aODT == null ? null : aODT.withNano (aODT.get (ChronoField.MILLI_OF_SECOND) * (int) CGlobal.NANOSECONDS_PER_MILLISECOND);
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
  public static OffsetDateTime createOffsetDateTime (@Nullable final ZonedDateTime aZDT)
  {
    return aZDT == null ? null : aZDT.toOffsetDateTime ();
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final LocalDateTime aLDT)
  {
    return aLDT == null ? null : ZonedDateTime.of (aLDT, _getZoneId ()).toOffsetDateTime ();
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final LocalDateTime aLDT)
  {
    return aLDT == null ? null : OffsetDateTime.of (aLDT, ZoneOffset.UTC);
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final LocalDate aLD)
  {
    return createOffsetDateTime (createLocalDateTime (aLD));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final LocalDate aLD)
  {
    return createOffsetDateTimeUTC (createLocalDateTime (aLD));
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
  public static OffsetDateTime createOffsetDateTime (@Nullable final YearMonth aYM)
  {
    return createOffsetDateTime (createLocalDateTime (aYM));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final YearMonth aYM)
  {
    return createOffsetDateTimeUTC (createLocalDateTime (aYM));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final Year aYear)
  {
    return createOffsetDateTime (createLocalDateTime (aYear));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final Year aYear)
  {
    return createOffsetDateTimeUTC (createLocalDateTime (aYear));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final LocalTime aLT)
  {
    return createOffsetDateTime (createLocalDateTime (aLT));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final LocalTime aLT)
  {
    return createOffsetDateTimeUTC (createLocalDateTime (aLT));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final OffsetTime aOT)
  {
    return aOT == null ? null : aOT.atDate (LocalDate.ofEpochDay (0));
  }

  @Nonnull
  public static OffsetDateTime createOffsetDateTime (final int nYear, final Month eMonth, final int nDay)
  {
    return createOffsetDateTime (createLocalDate (nYear, eMonth, nDay));
  }

  @Nonnull
  public static OffsetDateTime createOffsetDateTimeUTC (final int nYear, final Month eMonth, final int nDay)
  {
    return createOffsetDateTimeUTC (createLocalDate (nYear, eMonth, nDay));
  }

  @Nonnull
  public static OffsetDateTime createOffsetDateTime (final int nYear,
                                                     @Nonnull final Month eMonth,
                                                     final int nDay,
                                                     final int nHour,
                                                     final int nMinute,
                                                     final int nSecond)
  {
    return createOffsetDateTime (createLocalDateTime (nYear, eMonth, nDay, nHour, nMinute, nSecond));
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
  public static OffsetDateTime createOffsetDateTime (@Nullable final Instant aInstant)
  {
    return aInstant == null ? null : OffsetDateTime.ofInstant (aInstant, _getZoneId ());
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final Instant aInstant)
  {
    return aInstant == null ? null : OffsetDateTime.ofInstant (aInstant, ZoneOffset.UTC);
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final Date aDate)
  {
    return aDate == null ? null : aDate.toInstant ().atOffset (getZoneOffset (aDate));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final Timestamp aTimestamp)
  {
    return aTimestamp == null ? null : aTimestamp.toInstant ().atOffset (getZoneOffset (aTimestamp));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final GregorianCalendar aCal)
  {
    return aCal == null ? null : createOffsetDateTime (aCal.toZonedDateTime ());
  }

  @Nonnull
  public static OffsetDateTime createOffsetDateTime (final long nMillis)
  {
    return createOffsetDateTime (createLocalDateTime (nMillis));
  }

  @Nonnull
  public static OffsetDateTime createOffsetDateTimeUTC (final long nMillis)
  {
    return createOffsetDateTimeUTC (createLocalDateTime (nMillis));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final Number aMillis)
  {
    return createOffsetDateTime (createLocalDateTime (aMillis));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTimeUTC (@Nullable final Number aMillis)
  {
    return createOffsetDateTimeUTC (createLocalDateTime (aMillis));
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
   * @param aLDT
   *        Source date time. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>, the local
   *         date time with microseconds and nanoseconds set to 0 otherwise.
   * @since 9.2.0
   */
  @Nullable
  public static LocalDateTime getWithMillisOnly (@Nullable final LocalDateTime aLDT)
  {
    return aLDT == null ? null : aLDT.withNano (aLDT.get (ChronoField.MILLI_OF_SECOND) * (int) CGlobal.NANOSECONDS_PER_MILLISECOND);
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
  public static LocalDateTime createLocalDateTime (@Nullable final LocalDate aLD)
  {
    return aLD == null ? null : aLD.atStartOfDay ();
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final YearMonth aYM)
  {
    return createLocalDateTime (createLocalDate (aYM));
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final Year aYear)
  {
    return createLocalDateTime (createLocalDate (aYear));
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final LocalTime aLT)
  {
    return aLT == null ? null : aLT.atDate (LocalDate.ofEpochDay (0));
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final OffsetTime aOT)
  {
    return aOT == null ? null : createLocalDateTime (aOT.toLocalTime ());
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

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final Instant aInstant)
  {
    return aInstant == null ? null : LocalDateTime.ofInstant (aInstant, _getZoneId ());
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final Date aDate)
  {
    return aDate == null ? null : createLocalDateTime (aDate.toInstant ());
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final Timestamp aDate)
  {
    return aDate == null ? null : aDate.toLocalDateTime ();
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final GregorianCalendar aCal)
  {
    return aCal == null ? null : createLocalDateTime (aCal.toZonedDateTime ());
  }

  @Nonnull
  public static LocalDateTime createLocalDateTime (final long nMillis)
  {
    return createLocalDateTime (Instant.ofEpochMilli (nMillis));
  }

  @Nullable
  public static LocalDateTime createLocalDateTime (@Nullable final Number aMillis)
  {
    return aMillis == null ? null : createLocalDateTime (aMillis.longValue ());
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
  public static LocalDate createLocalDate (@Nullable final GregorianCalendar aCalendar)
  {
    return aCalendar == null ? null : aCalendar.toZonedDateTime ().toLocalDate ();
  }

  @Nonnull
  public static LocalDate createLocalDate (final long nMillis)
  {
    return createLocalDateTime (nMillis).toLocalDate ();
  }

  @Nullable
  public static LocalDate createLocalDate (@Nullable final Instant aInstant)
  {
    return aInstant == null ? null : createLocalDateTime (aInstant).toLocalDate ();
  }

  @Nullable
  public static LocalDate createLocalDate (@Nullable final Date aDate)
  {
    return aDate == null ? null : createLocalDate (aDate.toInstant ());
  }

  @Nullable
  public static LocalDate createLocalDate (@Nullable final YearMonth aYM)
  {
    return aYM == null ? null : aYM.atDay (1);
  }

  @Nullable
  public static LocalDate createLocalDate (@Nullable final Year aYear)
  {
    return aYear == null ? null : aYear.atDay (1);
  }

  @Nullable
  public static LocalDate createLocalDate (@Nullable final LocalDateTime aLDT)
  {
    return aLDT == null ? null : aLDT.toLocalDate ();
  }

  @Nullable
  public static LocalDate createLocalDate (@Nullable final OffsetDateTime aODT)
  {
    return aODT == null ? null : aODT.toLocalDate ();
  }

  @Nullable
  public static LocalDate createLocalDate (@Nullable final ZonedDateTime aZDT)
  {
    return aZDT == null ? null : aZDT.toLocalDate ();
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

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final LocalDate aLD)
  {
    return createOffsetDate (aLD, ZoneOffset.UTC);
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final LocalDate aLD, @Nonnull final ZoneOffset aOffset)
  {
    return aLD == null ? null : OffsetDate.of (aLD, aOffset);
  }

  @Nonnull
  public static OffsetDate createOffsetDate (final int nYear, @Nonnull final Month eMonth, final int nDayOfMonth)
  {
    return createOffsetDate (nYear, eMonth, nDayOfMonth, ZoneOffset.UTC);
  }

  @Nonnull
  public static OffsetDate createOffsetDate (final int nYear,
                                             @Nonnull final Month eMonth,
                                             final int nDayOfMonth,
                                             @Nullable final ZoneOffset aOffset)
  {
    return OffsetDate.of (nYear, eMonth, nDayOfMonth, aOffset);
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final LocalDateTime aLD)
  {
    return aLD == null ? null : createOffsetDate (aLD.toLocalDate ());
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final LocalDateTime aLD, @Nonnull final ZoneOffset aOffset)
  {
    return aLD == null ? null : createOffsetDate (aLD.toLocalDate (), aOffset);
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final GregorianCalendar aCalendar)
  {
    return aCalendar == null ? null : createOffsetDate (aCalendar.toZonedDateTime ());
  }

  @Nonnull
  public static OffsetDate createOffsetDate (final long nMillis)
  {
    return createOffsetDate (Instant.ofEpochMilli (nMillis));
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final Instant aInstant)
  {
    return createOffsetDate (aInstant, ZoneOffset.UTC);
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final Instant aInstant, @Nonnull final ZoneOffset aOffset)
  {
    return aInstant == null ? null : OffsetDate.ofInstant (aInstant, aOffset);
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final Date aDate)
  {
    return createOffsetDate (createOffsetDateTime (aDate));
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final YearMonth aYM)
  {
    return aYM == null ? null : createOffsetDate (aYM.atDay (1));
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final Year aYear)
  {
    return aYear == null ? null : createOffsetDate (aYear.atDay (1));
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final OffsetDateTime aLDT)
  {
    return aLDT == null ? null : OffsetDate.of (aLDT.toLocalDate (), aLDT.getOffset ());
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final ZonedDateTime aLDT)
  {
    return aLDT == null ? null : OffsetDate.of (aLDT.toLocalDate (), aLDT.getOffset ());
  }

  @Nullable
  public static OffsetDate createOffsetDate (@Nullable final Number aMillis)
  {
    return aMillis == null ? null : createOffsetDate (aMillis.longValue ());
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
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final LocalDate aLD)
  {
    return createXMLOffsetDate (aLD, null);
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final LocalDate aLD, @Nonnull final ZoneOffset aOffset)
  {
    return aLD == null ? null : XMLOffsetDate.of (aLD, aOffset);
  }

  @Nonnull
  public static XMLOffsetDate createXMLOffsetDate (final int nYear, @Nonnull final Month eMonth, final int nDayOfMonth)
  {
    return createXMLOffsetDate (nYear, eMonth, nDayOfMonth, ZoneOffset.UTC);
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
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final LocalDateTime aLD)
  {
    return aLD == null ? null : createXMLOffsetDate (aLD.toLocalDate ());
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final LocalDateTime aLD, @Nonnull final ZoneOffset aOffset)
  {
    return aLD == null ? null : createXMLOffsetDate (aLD.toLocalDate (), aOffset);
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final GregorianCalendar aCalendar)
  {
    return aCalendar == null ? null : createXMLOffsetDate (aCalendar.toZonedDateTime ());
  }

  @Nonnull
  public static XMLOffsetDate createXMLOffsetDate (final long nMillis)
  {
    return createXMLOffsetDate (Instant.ofEpochMilli (nMillis));
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final Instant aInstant)
  {
    return createXMLOffsetDate (aInstant, ZoneOffset.UTC);
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final Instant aInstant, @Nonnull final ZoneOffset aOffset)
  {
    return aInstant == null ? null : XMLOffsetDate.ofInstant (aInstant, aOffset);
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final Date aDate)
  {
    return createXMLOffsetDate (createOffsetDateTime (aDate));
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final YearMonth aYM)
  {
    return aYM == null ? null : createXMLOffsetDate (aYM.atDay (1));
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final Year aYear)
  {
    return aYear == null ? null : createXMLOffsetDate (aYear.atDay (1));
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final OffsetDateTime aLDT)
  {
    return aLDT == null ? null : XMLOffsetDate.of (aLDT.toLocalDate (), aLDT.getOffset ());
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final ZonedDateTime aLDT)
  {
    return aLDT == null ? null : XMLOffsetDate.of (aLDT.toLocalDate (), aLDT.getOffset ());
  }

  @Nullable
  public static XMLOffsetDate createXMLOffsetDate (@Nullable final Number aMillis)
  {
    return aMillis == null ? null : createXMLOffsetDate (aMillis.longValue ());
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
   * @param aLT
   *        Source time. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>, the local
   *         time with microseconds and nanoseconds set to 0 otherwise.
   * @since 9.4.7
   */
  @Nullable
  public static LocalTime getWithMillisOnly (@Nullable final LocalTime aLT)
  {
    return aLT == null ? null : aLT.withNano (aLT.get (ChronoField.MILLI_OF_SECOND) * (int) CGlobal.NANOSECONDS_PER_MILLISECOND);
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
  public static LocalTime createLocalTime (@Nullable final GregorianCalendar aCalendar)
  {
    return aCalendar == null ? null : aCalendar.toZonedDateTime ().toLocalTime ();
  }

  @Nonnull
  public static LocalTime createLocalTime (final long nMillis)
  {
    return createLocalDateTime (nMillis).toLocalTime ();
  }

  @Nullable
  public static LocalTime createLocalTime (@Nullable final Instant aInstant)
  {
    return aInstant == null ? null : createLocalDateTime (aInstant).toLocalTime ();
  }

  @Nullable
  public static LocalTime createLocalTime (@Nullable final Date aDate)
  {
    return aDate == null ? null : createLocalTime (aDate.toInstant ());
  }

  @Nullable
  public static LocalTime createLocalTime (@Nullable final LocalDateTime aLDT)
  {
    return aLDT == null ? null : aLDT.toLocalTime ();
  }

  @Nullable
  public static LocalTime createLocalTime (@Nullable final OffsetTime aOT)
  {
    return aOT == null ? null : aOT.toLocalTime ();
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
   * @param aOT
   *        Source time. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>, the local
   *         time with microseconds and nanoseconds set to 0 otherwise.
   * @since 10.0.0
   */
  @Nullable
  public static OffsetTime getWithMillisOnly (@Nullable final OffsetTime aOT)
  {
    return aOT == null ? null : aOT.withNano (aOT.get (ChronoField.MILLI_OF_SECOND) * (int) CGlobal.NANOSECONDS_PER_MILLISECOND);
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
  public static OffsetTime createOffsetTime (@Nullable final GregorianCalendar aCalendar)
  {
    return aCalendar == null ? null : aCalendar.toZonedDateTime ().toOffsetDateTime ().toOffsetTime ();
  }

  @Nonnull
  public static OffsetTime createOffsetTime (final long nMillis)
  {
    return createOffsetDateTime (nMillis).toOffsetTime ();
  }

  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final Instant aInstant)
  {
    return aInstant == null ? null : createOffsetDateTime (aInstant).toOffsetTime ();
  }

  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final Date aDate)
  {
    return aDate == null ? null : createOffsetTime (aDate.toInstant ());
  }

  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final OffsetDateTime aLDT)
  {
    return aLDT == null ? null : aLDT.toOffsetTime ();
  }

  @Nullable
  public static OffsetTime createOffsetTime (@Nullable final LocalTime aLT, @Nonnull final ZoneOffset aOfs)
  {
    return aLT == null ? null : aLT.atOffset (aOfs);
  }

  @Nonnull
  public static OffsetTime createOffsetTime (final int nHour, final int nMinute, final int nSecond, @Nonnull final ZoneOffset aOfs)
  {
    return OffsetTime.of (nHour, nMinute, nSecond, 0, aOfs);
  }

  // to Date

  @Nullable
  public static Date createDate (@Nullable final ZonedDateTime aZDT)
  {
    // The timezone details gets lost here
    return aZDT == null ? null : Date.from (aZDT.toInstant ());
  }

  @Nullable
  public static Date createDate (@Nullable final OffsetDateTime aODT)
  {
    // The timezone details gets lost here
    return aODT == null ? null : Date.from (aODT.toInstant ());
  }

  @Nullable
  public static Date createDate (@Nullable final LocalDateTime aLDT)
  {
    return createDate (createZonedDateTime (aLDT));
  }

  @Nullable
  public static Date createDate (@Nullable final LocalDate aLD)
  {
    return createDate (createZonedDateTime (aLD));
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
  public static Date createDate (@Nullable final LocalTime aLT)
  {
    return createDate (createZonedDateTime (aLT));
  }

  @Nullable
  public static Date createDate (@Nullable final OffsetTime aOT)
  {
    return createDate (createZonedDateTime (aOT));
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

  public static long getMillis (@Nonnull final LocalDate aLD)
  {
    return getMillis (createZonedDateTime (aLD));
  }

  public static long getMillis (@Nonnull final OffsetDate aOD)
  {
    return getMillis (createZonedDateTime (aOD));
  }

  public static long getMillis (@Nonnull final XMLOffsetDate aOD)
  {
    return getMillis (createZonedDateTime (aOD));
  }

  public static long getMillis (@Nonnull final LocalTime aLT)
  {
    return getMillis (createZonedDateTime (aLT));
  }

  public static long getMillis (@Nonnull final OffsetTime aOT)
  {
    return getMillis (createZonedDateTime (aOT));
  }

  public static long getMillis (@Nonnull final LocalDateTime aLDT)
  {
    return getMillis (createZonedDateTime (aLDT));
  }

  public static long getMillis (@Nonnull final OffsetDateTime aODT)
  {
    return aODT.toInstant ().toEpochMilli ();
  }

  public static long getMillis (@Nonnull final ZonedDateTime aZDT)
  {
    return aZDT.toInstant ().toEpochMilli ();
  }
}
