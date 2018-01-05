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

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

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
  public static ZonedDateTime createZonedDateTime (@Nullable final LocalDate aLD)
  {
    return createZonedDateTime (createLocalDateTime (aLD));
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final YearMonth aYM)
  {
    return createZonedDateTime (createLocalDateTime (aYM));
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final Year aYear)
  {
    return createZonedDateTime (createLocalDateTime (aYear));
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final LocalTime aLT)
  {
    return createZonedDateTime (createLocalDateTime (aLT));
  }

  @Nonnull
  public static ZonedDateTime createZonedDateTime (final int nYear, final Month eMonth, final int nDay)
  {
    return createZonedDateTime (createLocalDateTime (nYear, eMonth, nDay));
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

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final Instant aInstant)
  {
    return aInstant == null ? null : ZonedDateTime.ofInstant (aInstant, _getZoneId ());
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final GregorianCalendar aCal)
  {
    return aCal == null ? null : aCal.toZonedDateTime ();
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final Date aDate)
  {
    return createZonedDateTime (createLocalDateTime (aDate));
  }

  @Nullable
  public static ZonedDateTime createZonedDateTime (@Nullable final Timestamp aTimestamp)
  {
    return createZonedDateTime (createLocalDateTime (aTimestamp));
  }

  @Nonnull
  public static ZonedDateTime createZonedDateTime (final long nMillis)
  {
    return createZonedDateTime (createLocalDateTime (nMillis));
  }

  @Nonnull
  public static ZonedDateTime createZonedDateTime (@Nullable final Number aMillis)
  {
    return createZonedDateTime (createLocalDateTime (aMillis));
  }

  // To OffsetDateTime

  @Nonnegative
  public static OffsetDateTime getCurrentOffsetDateTime ()
  {
    return OffsetDateTime.now (_getZoneId ());
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final ZonedDateTime aZDT)
  {
    return aZDT == null ? null : aZDT.toOffsetDateTime ();
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final LocalDateTime aLDT)
  {
    return createOffsetDateTime (createZonedDateTime (aLDT));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final LocalDate aLD)
  {
    return createOffsetDateTime (createZonedDateTime (aLD));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final YearMonth aYM)
  {
    return createOffsetDateTime (createZonedDateTime (aYM));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final Year aYear)
  {
    return createOffsetDateTime (createZonedDateTime (aYear));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final LocalTime aLT)
  {
    return createOffsetDateTime (createZonedDateTime (aLT));
  }

  @Nonnull
  public static OffsetDateTime createOffsetDateTime (final int nYear, final Month eMonth, final int nDay)
  {
    return createOffsetDateTime (createZonedDateTime (nYear, eMonth, nDay));
  }

  @Nonnull
  public static OffsetDateTime createOffsetDateTime (final int nYear,
                                                     @Nonnull final Month eMonth,
                                                     final int nDay,
                                                     final int nHour,
                                                     final int nMinute,
                                                     final int nSecond)
  {
    return createOffsetDateTime (createZonedDateTime (nYear, eMonth, nDay, nHour, nMinute, nSecond));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final Instant aInstant)
  {
    return aInstant == null ? null : OffsetDateTime.ofInstant (aInstant, _getZoneId ());
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final Date aDate)
  {
    return createOffsetDateTime (createLocalDateTime (aDate));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final Timestamp aTimestamp)
  {
    return createOffsetDateTime (createLocalDateTime (aTimestamp));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final GregorianCalendar aCal)
  {
    return createOffsetDateTime (aCal == null ? null : aCal.toZonedDateTime ());
  }

  @Nonnull
  public static OffsetDateTime createOffsetDateTime (final long nMillis)
  {
    return createOffsetDateTime (createLocalDateTime (nMillis));
  }

  @Nullable
  public static OffsetDateTime createOffsetDateTime (@Nullable final Number aMillis)
  {
    return createOffsetDateTime (createLocalDateTime (aMillis));
  }

  // To LocalDateTime

  @Nonnegative
  public static LocalDateTime getCurrentLocalDateTime ()
  {
    return LocalDateTime.now (_getZoneId ());
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

  @Nullable
  public static LocalDate createLocalDate (final int nYear, @Nonnull final Month eMonth, final int nDay)
  {
    return LocalDate.of (nYear, eMonth, nDay);
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

  // To LocalTime

  @Nonnegative
  public static LocalTime getCurrentLocalTime ()
  {
    return LocalTime.now (_getZoneId ());
  }

  @Nullable
  public static LocalTime createLocalTime (@Nullable final GregorianCalendar aCalendar)
  {
    return aCalendar == null ? null : aCalendar.toZonedDateTime ().toLocalTime ();
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
  public static LocalTime createLocalTime (final int nHour, final int nMinute, final int nSecond)
  {
    return LocalTime.of (nHour, nMinute, nSecond);
  }

  // To Date

  @Nullable
  public static Date createDate (@Nullable final ZonedDateTime aZDT)
  {
    return aZDT == null ? null : Date.from (aZDT.toInstant ());
  }

  @Nullable
  public static Date createDate (@Nullable final OffsetDateTime aODT)
  {
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
  public static Date createDate (@Nullable final LocalTime aLT)
  {
    return createDate (createZonedDateTime (aLT));
  }

  @Nonnull
  public static Calendar createCalendar ()
  {
    return Calendar.getInstance (TimeZone.getDefault (), Locale.getDefault (Locale.Category.FORMAT));
  }

  @Nonnull
  public static GregorianCalendar createGregorianCalendar ()
  {
    return new GregorianCalendar (TimeZone.getDefault (), Locale.getDefault (Locale.Category.FORMAT));
  }

  // Misc

  @Nonnegative
  public static int getCurrentYear ()
  {
    return getCurrentLocalDate ().getYear ();
  }

  @Nonnegative
  public static Year getCurrentYearObj ()
  {
    return Year.now (_getZoneId ());
  }

  @Nonnegative
  public static YearMonth getCurrentYearMonth ()
  {
    return YearMonth.now (_getZoneId ());
  }

  @Nonnegative
  public static Instant getCurrentInstant ()
  {
    return Instant.now (Clock.system (_getZoneId ()));
  }

  public static long getCurrentMillis ()
  {
    return getCurrentInstant ().toEpochMilli ();
  }

  public static long getMillis (@Nonnull final LocalDate aLD)
  {
    return createZonedDateTime (aLD).toInstant ().toEpochMilli ();
  }

  public static long getMillis (@Nonnull final LocalTime aLT)
  {
    return createZonedDateTime (aLT).toInstant ().toEpochMilli ();
  }

  public static long getMillis (@Nonnull final LocalDateTime aLDT)
  {
    return createZonedDateTime (aLDT).toInstant ().toEpochMilli ();
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
