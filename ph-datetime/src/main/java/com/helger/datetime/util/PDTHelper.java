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
package com.helger.datetime.util;//NOPMD

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.datetime.CPDT;
import com.helger.datetime.helper.PDTFactory;

/**
 * Some date/time utility methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class PDTHelper
{
  @PresentForCodeCoverage
  private static final PDTHelper INSTANCE = new PDTHelper ();

  private PDTHelper ()
  {}

  /**
   * Check if the provided {@link LocalDate} is <code>null</code> or represents
   * the predefined null date constant.
   *
   * @param aDate
   *        The date to check. May be <code>null</code>.
   * @return <code>true</code> if the date is <code>null</code> or equals the
   *         null date constant.
   */
  public static boolean isNullValue (@Nullable final LocalDate aDate)
  {
    return aDate == null || aDate == CPDT.NULL_LOCAL_DATE;
  }

  /**
   * Check if the provided {@link LocalTime} is <code>null</code> or represents
   * the predefined null time constant.
   *
   * @param aTime
   *        The time to check. May be <code>null</code>.
   * @return <code>true</code> if the time is <code>null</code> or equals the
   *         null time constant.
   */
  public static boolean isNullValue (@Nullable final LocalTime aTime)
  {
    return aTime == null || aTime == CPDT.NULL_LOCAL_TIME;
  }

  /**
   * Check if the provided {@link LocalDateTime} is <code>null</code> or
   * represents the predefined null date time constant.
   *
   * @param aDateTime
   *        The date time to check. May be <code>null</code>.
   * @return <code>true</code> if the date time is <code>null</code> or equals
   *         the null date time constant.
   */
  public static boolean isNullValue (@Nullable final LocalDateTime aDateTime)
  {
    return aDateTime == null || aDateTime == CPDT.NULL_LOCAL_DATETIME;
  }

  /**
   * Check if the provided {@link ZonedDateTime} is <code>null</code> or
   * represents the predefined null date time constant.
   *
   * @param aDateTime
   *        The zoned date time to check. May be <code>null</code>.
   * @return <code>true</code> if the zoned date time is <code>null</code> or
   *         equals the null date time constant.
   */
  public static boolean isNullValue (@Nullable final ZonedDateTime aDateTime)
  {
    return aDateTime == null || aDateTime.equals (CPDT.NULL_DATETIME);
  }

  /**
   * Check if the provided day of week is a weekend day (Saturday or Sunday).
   *
   * @param nDayOfWeek
   *        The day of week to check. May not be <code>null</code>.
   * @return <code>true</code> if the day is Saturday or Sunday.
   */
  public static boolean isWeekendDay (final DayOfWeek nDayOfWeek)
  {
    return nDayOfWeek == DayOfWeek.SATURDAY || nDayOfWeek == DayOfWeek.SUNDAY;
  }

  /**
   * Check if the provided date time falls on a weekend day.
   *
   * @param aDT
   *        The date time to check. May not be <code>null</code>.
   * @return <code>true</code> if the day is Saturday or Sunday.
   */
  public static boolean isWeekend (@NonNull final LocalDateTime aDT)
  {
    return isWeekendDay (aDT.getDayOfWeek ());
  }

  /**
   * Check if the provided date falls on a weekend day.
   *
   * @param aDT
   *        The date to check. May not be <code>null</code>.
   * @return <code>true</code> if the day is Saturday or Sunday.
   */
  public static boolean isWeekend (@NonNull final LocalDate aDT)
  {
    return isWeekendDay (aDT.getDayOfWeek ());
  }

  /**
   * Check if the provided day of week is the first day of the week.
   *
   * @param nDayOfWeek
   *        The day of week to check. May not be <code>null</code>.
   * @return <code>true</code> if the day matches the configured start of week
   *         day.
   */
  public static boolean isFirstDayOfWeek (final DayOfWeek nDayOfWeek)
  {
    return nDayOfWeek == CPDT.START_OF_WEEK_DAY;
  }

  /**
   * Check if the provided zoned date time falls on the first day of the week.
   *
   * @param aDT
   *        The zoned date time to check. May not be <code>null</code>.
   * @return <code>true</code> if the day matches the configured start of week
   *         day.
   */
  public static boolean isFirstDayOfWeek (@NonNull final ZonedDateTime aDT)
  {
    return isFirstDayOfWeek (aDT.getDayOfWeek ());
  }

  /**
   * Check if the provided date time falls on the first day of the week.
   *
   * @param aDT
   *        The date time to check. May not be <code>null</code>.
   * @return <code>true</code> if the day matches the configured start of week
   *         day.
   */
  public static boolean isFirstDayOfWeek (@NonNull final LocalDateTime aDT)
  {
    return isFirstDayOfWeek (aDT.getDayOfWeek ());
  }

  /**
   * Check if the provided date falls on the first day of the week.
   *
   * @param aDT
   *        The date to check. May not be <code>null</code>.
   * @return <code>true</code> if the day matches the configured start of week
   *         day.
   */
  public static boolean isFirstDayOfWeek (@NonNull final LocalDate aDT)
  {
    return isFirstDayOfWeek (aDT.getDayOfWeek ());
  }

  /**
   * Check if the provided day of week is the last day of the week.
   *
   * @param nDayOfWeek
   *        The day of week to check. May not be <code>null</code>.
   * @return <code>true</code> if the day matches the configured end of week
   *         day.
   */
  public static boolean isLastDayOfWeek (final DayOfWeek nDayOfWeek)
  {
    return nDayOfWeek == CPDT.END_OF_WEEK_DAY;
  }

  /**
   * Check if the provided zoned date time falls on the last day of the week.
   *
   * @param aDT
   *        The zoned date time to check. May not be <code>null</code>.
   * @return <code>true</code> if the day matches the configured end of week
   *         day.
   */
  public static boolean isLastDayOfWeek (@NonNull final ZonedDateTime aDT)
  {
    return isLastDayOfWeek (aDT.getDayOfWeek ());
  }

  /**
   * Check if the provided date time falls on the last day of the week.
   *
   * @param aDT
   *        The date time to check. May not be <code>null</code>.
   * @return <code>true</code> if the day matches the configured end of week
   *         day.
   */
  public static boolean isLastDayOfWeek (@NonNull final LocalDateTime aDT)
  {
    return isLastDayOfWeek (aDT.getDayOfWeek ());
  }

  /**
   * Check if the provided date falls on the last day of the week.
   *
   * @param aDT
   *        The date to check. May not be <code>null</code>.
   * @return <code>true</code> if the day matches the configured end of week
   *         day.
   */
  public static boolean isLastDayOfWeek (@NonNull final LocalDate aDT)
  {
    return isLastDayOfWeek (aDT.getDayOfWeek ());
  }

  /**
   * Check if the provided date is a work day (not a weekend day). Does not
   * consider holidays.
   *
   * @param aDate
   *        The date to check. May not be <code>null</code>.
   * @return <code>true</code> if the date is not a Saturday and not a Sunday.
   */
  public static boolean isWorkDay (@NonNull final LocalDate aDate)
  {
    return !isWeekend (aDate);
  }

  /**
   * Count all non-weekend days in the range. Does not consider holidays!
   *
   * @param aStartDate
   *        start date
   * @param aEndDate
   *        end date
   * @return days not counting Saturdays and Sundays. If start date is after end date, the value
   *         will be negative! If start date equals end date the return will be 1 if it is a week
   *         day.
   */
  public static int getWeekDays (@NonNull final LocalDate aStartDate, @NonNull final LocalDate aEndDate)
  {
    ValueEnforcer.notNull (aStartDate, "StartDate");
    ValueEnforcer.notNull (aEndDate, "EndDate");

    final boolean bFlip = aStartDate.isAfter (aEndDate);
    LocalDate aCurDate = bFlip ? aEndDate : aStartDate;
    final LocalDate aRealEndDate = bFlip ? aStartDate : aEndDate;

    int ret = 0;
    while (!aRealEndDate.isBefore (aCurDate))
    {
      if (!isWeekend (aCurDate))
        ret++;
      aCurDate = aCurDate.plusDays (1);
    }
    return bFlip ? -1 * ret : ret;
  }

  /**
   * Check if the two dates have the same year and day of year.
   *
   * @param x
   *        First date. May not be <code>null</code>.
   * @param y
   *        Second date. May not be <code>null</code>.
   * @return <code>true</code> if year and day of year are equal.
   */
  public static boolean isSameYearAndDay (@NonNull final LocalDate x, @NonNull final LocalDate y)
  {
    return x.getYear () == y.getYear () && x.getDayOfYear () == y.getDayOfYear ();
  }

  /**
   * Check if the two dates have the same year and week of week-based year.
   *
   * @param x
   *        First date. May not be <code>null</code>.
   * @param y
   *        Second date. May not be <code>null</code>.
   * @param aLocale
   *        Locale to use for determining the week fields. May not be
   *        <code>null</code>.
   * @return <code>true</code> if year and week of week-based year are equal.
   */
  public static boolean isSameYearAndWeek (@NonNull final LocalDate x,
                                           @NonNull final LocalDate y,
                                           @NonNull final Locale aLocale)
  {
    return x.getYear () == y.getYear () && getWeekOfWeekBasedYear (x, aLocale) == getWeekOfWeekBasedYear (y, aLocale);
  }

  /**
   * Check if the two dates have the same month and day of month.
   *
   * @param x
   *        First date. May not be <code>null</code>.
   * @param y
   *        Second date. May not be <code>null</code>.
   * @return <code>true</code> if month and day of month are equal.
   */
  public static boolean isSameMonthAndDay (@NonNull final LocalDate x, @NonNull final LocalDate y)
  {
    return x.getMonth () == y.getMonth () && x.getDayOfMonth () == y.getDayOfMonth ();
  }

  /**
   * Check if the provided date is between the lower and upper bound
   * (inclusive).
   *
   * @param aDate
   *        The date to check. May be <code>null</code>.
   * @param aLowerBound
   *        The lower bound (inclusive). May be <code>null</code>.
   * @param aUpperBound
   *        The upper bound (inclusive). May be <code>null</code>.
   * @return <code>true</code> if all parameters are non-<code>null</code> and
   *         the date is between the bounds (inclusive).
   */
  public static boolean isBetweenIncl (@Nullable final LocalDate aDate,
                                       @Nullable final LocalDate aLowerBound,
                                       @Nullable final LocalDate aUpperBound)
  {
    if (aDate == null || aLowerBound == null || aUpperBound == null)
      return false;
    return !aLowerBound.isAfter (aDate) && !aDate.isAfter (aUpperBound);
  }

  /**
   * Get the week of week-based year for the provided temporal accessor and
   * locale.
   *
   * @param aDT
   *        The temporal accessor to get the week from. May not be
   *        <code>null</code>.
   * @param aLocale
   *        Locale to use for determining the week fields. May not be
   *        <code>null</code>.
   * @return The week of week-based year.
   */
  public static int getWeekOfWeekBasedYear (@NonNull final TemporalAccessor aDT, @NonNull final Locale aLocale)
  {
    return aDT.get (WeekFields.of (aLocale).weekOfWeekBasedYear ());
  }

  /**
   * Get the start--week number for the passed year and month.
   *
   * @param aDT
   *        The object to use year and month from.
   * @param aLocale
   *        Locale to use. May not be <code>null</code>.
   * @return the start week number.
   */
  public static int getStartWeekOfMonth (@NonNull final LocalDateTime aDT, @NonNull final Locale aLocale)
  {
    return getWeekOfWeekBasedYear (aDT.withDayOfMonth (1), aLocale);
  }

  /**
   * Get the start-week number for the passed year and month.
   *
   * @param aDT
   *        The object to use year and month from.
   * @param aLocale
   *        Locale to use. May not be <code>null</code>.
   * @return the start week number.
   */
  public static int getStartWeekOfMonth (@NonNull final LocalDate aDT, @NonNull final Locale aLocale)
  {
    return getWeekOfWeekBasedYear (aDT.withDayOfMonth (1), aLocale);
  }

  /**
   * Get the start-week number for the passed year and month.
   *
   * @param aDT
   *        The object to use year and month from.
   * @param aLocale
   *        Locale to use. May not be <code>null</code>.
   * @return the start week number.
   */
  public static int getStartWeekOfMonth (@NonNull final ZonedDateTime aDT, @NonNull final Locale aLocale)
  {
    return getWeekOfWeekBasedYear (aDT.withDayOfMonth (1), aLocale);
  }

  /**
   * Get the end-week number for the passed year and month.
   *
   * @param aDT
   *        The object to use year and month from.
   * @param aLocale
   *        Locale to use. May not be <code>null</code>.
   * @return The end week number.
   */
  public static int getEndWeekOfMonth (@NonNull final LocalDateTime aDT, @NonNull final Locale aLocale)
  {
    return getWeekOfWeekBasedYear (aDT.plusMonths (1).withDayOfMonth (1).minusDays (1), aLocale);
  }

  /**
   * Get the end-week number for the passed year and month.
   *
   * @param aDT
   *        The object to use year and month from.
   * @param aLocale
   *        Locale to use. May not be <code>null</code>.
   * @return The end week number.
   */
  public static int getEndWeekOfMonth (@NonNull final LocalDate aDT, @NonNull final Locale aLocale)
  {
    return getWeekOfWeekBasedYear (aDT.plusMonths (1).withDayOfMonth (1).minusDays (1), aLocale);
  }

  /**
   * Get the end-week number for the passed year and month.
   *
   * @param aDT
   *        The object to use year and month from.
   * @param aLocale
   *        Locale to use. May not be <code>null</code>.
   * @return The end week number.
   */
  public static int getEndWeekOfMonth (@NonNull final ZonedDateTime aDT, @NonNull final Locale aLocale)
  {
    return getWeekOfWeekBasedYear (aDT.plusMonths (1).withDayOfMonth (1).minusDays (1), aLocale);
  }

  /**
   * Get the next week day based on the provided date. If the provided date is a week day day, the
   * provided date is returned. A week day is determined by not being a weekend day (usually
   * Saturday or Sunday).
   *
   * @param aStart
   *        The date to start at. May not be <code>null</code>.
   * @return The next matching date. Never <code>null</code>.
   */
  @NonNull
  public static LocalDate getCurrentOrNextWeekday (@NonNull final LocalDate aStart)
  {
    LocalDate aDT = aStart;
    while (isWeekend (aDT))
      aDT = aDT.plusDays (1);
    return aDT;
  }

  /**
   * Get the current or next weekday based on today's date. If today is a week
   * day, today is returned.
   *
   * @return The current or next matching weekday. Never <code>null</code>.
   */
  @NonNull
  public static LocalDate getCurrentOrNextWeekday ()
  {
    return getCurrentOrNextWeekday (PDTFactory.getCurrentLocalDate ());
  }

  /**
   * Get the next weekend day based on the provided date. If the provided date is a weekend day, the
   * provided date is returned. Weekend day are Saturday or Sunday.
   *
   * @param aStart
   *        The date to start at. May not be <code>null</code>.
   * @return The next matching date.
   */
  @NonNull
  public static LocalDate getCurrentOrNextWeekendkDay (@NonNull final LocalDate aStart)
  {
    LocalDate aDT = aStart;
    while (!isWeekend (aDT))
      aDT = aDT.plusDays (1);
    return aDT;
  }

  /**
   * Get the next weekend day based on the provided date. If the provided date is a weekend day, the
   * provided date is returned. Weekend day are Saturday or Sunday.
   *
   * @return The next matching date.
   */
  @NonNull
  public static LocalDate getCurrentOrNextWeekendkDay ()
  {
    return getCurrentOrNextWeekendkDay (PDTFactory.getCurrentLocalDate ());
  }

  /**
   * Compare two dates by birthday. This means, the dates are only compared by day and month, and
   * <b>not</b> by year!
   *
   * @param aDate1
   *        First date. May be <code>null</code>.
   * @param aDate2
   *        Second date. May be <code>null</code>.
   * @return same as {@link Comparator#compare(Object, Object)}
   */
  public static int birthdayCompare (@Nullable final LocalDate aDate1, @Nullable final LocalDate aDate2)
  {
    if (EqualsHelper.identityEqual (aDate1, aDate2))
      return 0;
    if (aDate1 == null)
      return -1;
    if (aDate2 == null)
      return 1;

    // first compare month
    int ret = aDate1.getMonth ().compareTo (aDate2.getMonth ());
    if (ret == 0)
    {
      // on equal month, compare day of month
      ret = aDate1.getDayOfMonth () - aDate2.getDayOfMonth ();
    }
    return ret;
  }

  /**
   * Check if the two birthdays are equal. Equal birthdays are identified by equal months and equal
   * days.
   *
   * @param aDate1
   *        First date. May be <code>null</code>.
   * @param aDate2
   *        Second date. May be <code>null</code>.
   * @return <code>true</code> if month and day are equal
   */
  public static boolean birthdayEquals (@Nullable final LocalDate aDate1, @Nullable final LocalDate aDate2)
  {
    return birthdayCompare (aDate1, aDate2) == 0;
  }

  /**
   * Check if the provided date is New Year's Eve (December 31st).
   *
   * @param aDate
   *        The date to check. May not be <code>null</code>.
   * @return <code>true</code> if the date is December 31st.
   */
  public static boolean isNewYearsEve (@NonNull final LocalDate aDate)
  {
    ValueEnforcer.notNull (aDate, "Date");
    return aDate.getMonth () == Month.DECEMBER && aDate.getDayOfMonth () == 31;
  }

  /**
   * Get the later of two dates.
   *
   * @param aDate1
   *        First date. May not be <code>null</code>.
   * @param aDate2
   *        Second date. May not be <code>null</code>.
   * @return The later of the two dates. Never <code>null</code>.
   */
  @NonNull
  public static LocalDate getMax (@NonNull final LocalDate aDate1, @NonNull final LocalDate aDate2)
  {
    return aDate1.isAfter (aDate2) ? aDate1 : aDate2;
  }

  /**
   * Get the later of two times.
   *
   * @param aTime1
   *        First time. May not be <code>null</code>.
   * @param aTime2
   *        Second time. May not be <code>null</code>.
   * @return The later of the two times. Never <code>null</code>.
   */
  @NonNull
  public static LocalTime getMax (@NonNull final LocalTime aTime1, @NonNull final LocalTime aTime2)
  {
    return aTime1.isAfter (aTime2) ? aTime1 : aTime2;
  }

  /**
   * Get the later of two date times.
   *
   * @param aDateTime1
   *        First date time. May not be <code>null</code>.
   * @param aDateTime2
   *        Second date time. May not be <code>null</code>.
   * @return The later of the two date times. Never <code>null</code>.
   */
  @NonNull
  public static LocalDateTime getMax (@NonNull final LocalDateTime aDateTime1, @NonNull final LocalDateTime aDateTime2)
  {
    return aDateTime1.isAfter (aDateTime2) ? aDateTime1 : aDateTime2;
  }

  /**
   * Get the later of two zoned date times.
   *
   * @param aDateTime1
   *        First zoned date time. May not be <code>null</code>.
   * @param aDateTime2
   *        Second zoned date time. May not be <code>null</code>.
   * @return The later of the two zoned date times. Never <code>null</code>.
   */
  @NonNull
  public static ZonedDateTime getMax (@NonNull final ZonedDateTime aDateTime1, @NonNull final ZonedDateTime aDateTime2)
  {
    return aDateTime1.isAfter (aDateTime2) ? aDateTime1 : aDateTime2;
  }

  /**
   * Get the later of two offset date times.
   *
   * @param aDateTime1
   *        First offset date time. May not be <code>null</code>.
   * @param aDateTime2
   *        Second offset date time. May not be <code>null</code>.
   * @return The later of the two offset date times. Never <code>null</code>.
   */
  @NonNull
  public static OffsetDateTime getMax (@NonNull final OffsetDateTime aDateTime1,
                                       @NonNull final OffsetDateTime aDateTime2)
  {
    return aDateTime1.isAfter (aDateTime2) ? aDateTime1 : aDateTime2;
  }

  /**
   * Get the earlier of two dates.
   *
   * @param aDate1
   *        First date. May not be <code>null</code>.
   * @param aDate2
   *        Second date. May not be <code>null</code>.
   * @return The earlier of the two dates. Never <code>null</code>.
   */
  @NonNull
  public static LocalDate getMin (@NonNull final LocalDate aDate1, @NonNull final LocalDate aDate2)
  {
    return aDate1.isBefore (aDate2) ? aDate1 : aDate2;
  }

  /**
   * Get the earlier of two times.
   *
   * @param aTime1
   *        First time. May not be <code>null</code>.
   * @param aTime2
   *        Second time. May not be <code>null</code>.
   * @return The earlier of the two times. Never <code>null</code>.
   */
  @NonNull
  public static LocalTime getMin (@NonNull final LocalTime aTime1, @NonNull final LocalTime aTime2)
  {
    return aTime1.isBefore (aTime2) ? aTime1 : aTime2;
  }

  /**
   * Get the earlier of two date times.
   *
   * @param aDateTime1
   *        First date time. May not be <code>null</code>.
   * @param aDateTime2
   *        Second date time. May not be <code>null</code>.
   * @return The earlier of the two date times. Never <code>null</code>.
   */
  @NonNull
  public static LocalDateTime getMin (@NonNull final LocalDateTime aDateTime1, @NonNull final LocalDateTime aDateTime2)
  {
    return aDateTime1.isBefore (aDateTime2) ? aDateTime1 : aDateTime2;
  }

  /**
   * Get the earlier of two zoned date times.
   *
   * @param aDateTime1
   *        First zoned date time. May not be <code>null</code>.
   * @param aDateTime2
   *        Second zoned date time. May not be <code>null</code>.
   * @return The earlier of the two zoned date times. Never <code>null</code>.
   */
  @NonNull
  public static ZonedDateTime getMin (@NonNull final ZonedDateTime aDateTime1, @NonNull final ZonedDateTime aDateTime2)
  {
    return aDateTime1.isBefore (aDateTime2) ? aDateTime1 : aDateTime2;
  }

  /**
   * Get the earlier of two offset date times.
   *
   * @param aDateTime1
   *        First offset date time. May not be <code>null</code>.
   * @param aDateTime2
   *        Second offset date time. May not be <code>null</code>.
   * @return The earlier of the two offset date times. Never <code>null</code>.
   */
  @NonNull
  public static OffsetDateTime getMin (@NonNull final OffsetDateTime aDateTime1,
                                       @NonNull final OffsetDateTime aDateTime2)
  {
    return aDateTime1.isBefore (aDateTime2) ? aDateTime1 : aDateTime2;
  }

  /**
   * Get the number of days between two temporal objects.
   *
   * @param aStartIncl
   *        The start (inclusive). May not be <code>null</code>.
   * @param aEndExcl
   *        The end (exclusive). May not be <code>null</code>.
   * @return The number of days between the two temporal objects.
   */
  public static long getDaysBetween (@NonNull final Temporal aStartIncl, @NonNull final Temporal aEndExcl)
  {
    return ChronoUnit.DAYS.between (aStartIncl, aEndExcl);
  }

  /**
   * Convert from Calendar day of week to {@link DayOfWeek} enum.
   *
   * @param nCalendarDayOfWeek
   *        Day of week - must be between 1 and 7.
   * @return {@link DayOfWeek} and never <code>null</code>.
   * @since 8.6.3
   */
  @NonNull
  public static DayOfWeek getAsDayOfWeek (final int nCalendarDayOfWeek)
  {
    ValueEnforcer.isBetweenInclusive (nCalendarDayOfWeek, "DayOfWeek", 1, 7);
    // Convert Calendar DoW to enum DoW
    final int nIndex = (nCalendarDayOfWeek + 6) % 7;
    return DayOfWeek.of (nIndex == 0 ? 7 : nIndex);
  }

  /**
   * Convert from {@link DayOfWeek} to Calendar day of week.
   *
   * @param eDOW
   *        Day of week. May not be <code>null</code>.
   * @return Something between Calendar.SUNDAY and Calendar.SATURDAY
   * @since 8.6.3
   */
  public static int getCalendarDayOfWeek (@NonNull final DayOfWeek eDOW)
  {
    ValueEnforcer.notNull (eDOW, "DayOfWeek");
    return eDOW.getValue () % 7 + 1;
  }

  /**
   * Get {@link Month} from int value in a non-throwing version.
   *
   * @param nMonth
   *        Month to use. 1 == January, 12 == December
   * @return <code>null</code> if invalid int was provided.
   */
  @Nullable
  public static Month getAsMonth (final int nMonth)
  {
    if (nMonth < 1 || nMonth > 12)
      return null;
    return Month.of (nMonth);
  }
}
