/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

  public static boolean isNullValue (@Nullable final LocalDate aDate)
  {
    return aDate == null || aDate == CPDT.NULL_LOCAL_DATE;
  }

  public static boolean isNullValue (@Nullable final LocalTime aTime)
  {
    return aTime == null || aTime == CPDT.NULL_LOCAL_TIME;
  }

  public static boolean isNullValue (@Nullable final LocalDateTime aDateTime)
  {
    return aDateTime == null || aDateTime == CPDT.NULL_LOCAL_DATETIME;
  }

  public static boolean isNullValue (@Nullable final ZonedDateTime aDateTime)
  {
    return aDateTime == null || aDateTime.equals (CPDT.NULL_DATETIME);
  }

  public static boolean isWeekendDay (final DayOfWeek nDayOfWeek)
  {
    return nDayOfWeek == DayOfWeek.SATURDAY || nDayOfWeek == DayOfWeek.SUNDAY;
  }

  public static boolean isWeekend (@NonNull final LocalDateTime aDT)
  {
    return isWeekendDay (aDT.getDayOfWeek ());
  }

  public static boolean isWeekend (@NonNull final LocalDate aDT)
  {
    return isWeekendDay (aDT.getDayOfWeek ());
  }

  public static boolean isFirstDayOfWeek (final DayOfWeek nDayOfWeek)
  {
    return nDayOfWeek == CPDT.START_OF_WEEK_DAY;
  }

  public static boolean isFirstDayOfWeek (@NonNull final ZonedDateTime aDT)
  {
    return isFirstDayOfWeek (aDT.getDayOfWeek ());
  }

  public static boolean isFirstDayOfWeek (@NonNull final LocalDateTime aDT)
  {
    return isFirstDayOfWeek (aDT.getDayOfWeek ());
  }

  public static boolean isFirstDayOfWeek (@NonNull final LocalDate aDT)
  {
    return isFirstDayOfWeek (aDT.getDayOfWeek ());
  }

  public static boolean isLastDayOfWeek (final DayOfWeek nDayOfWeek)
  {
    return nDayOfWeek == CPDT.END_OF_WEEK_DAY;
  }

  public static boolean isLastDayOfWeek (@NonNull final ZonedDateTime aDT)
  {
    return isLastDayOfWeek (aDT.getDayOfWeek ());
  }

  public static boolean isLastDayOfWeek (@NonNull final LocalDateTime aDT)
  {
    return isLastDayOfWeek (aDT.getDayOfWeek ());
  }

  public static boolean isLastDayOfWeek (@NonNull final LocalDate aDT)
  {
    return isLastDayOfWeek (aDT.getDayOfWeek ());
  }

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

  public static boolean isSameYearAndDay (@NonNull final LocalDate x, @NonNull final LocalDate y)
  {
    return x.getYear () == y.getYear () && x.getDayOfYear () == y.getDayOfYear ();
  }

  public static boolean isSameYearAndWeek (@NonNull final LocalDate x,
                                           @NonNull final LocalDate y,
                                           @NonNull final Locale aLocale)
  {
    return x.getYear () == y.getYear () && getWeekOfWeekBasedYear (x, aLocale) == getWeekOfWeekBasedYear (y, aLocale);
  }

  public static boolean isSameMonthAndDay (@NonNull final LocalDate x, @NonNull final LocalDate y)
  {
    return x.getMonth () == y.getMonth () && x.getDayOfMonth () == y.getDayOfMonth ();
  }

  public static boolean isBetweenIncl (@Nullable final LocalDate aDate,
                                       @Nullable final LocalDate aLowerBound,
                                       @Nullable final LocalDate aUpperBound)
  {
    if (aDate == null || aLowerBound == null || aUpperBound == null)
      return false;
    return !aLowerBound.isAfter (aDate) && !aDate.isAfter (aUpperBound);
  }

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

  public static int getStartWeekOfMonth (@NonNull final LocalDate aDT, @NonNull final Locale aLocale)
  {
    return getWeekOfWeekBasedYear (aDT.withDayOfMonth (1), aLocale);
  }

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

  public static int getEndWeekOfMonth (@NonNull final LocalDate aDT, @NonNull final Locale aLocale)
  {
    return getWeekOfWeekBasedYear (aDT.plusMonths (1).withDayOfMonth (1).minusDays (1), aLocale);
  }

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

  public static boolean isNewYearsEve (@NonNull final LocalDate aDate)
  {
    ValueEnforcer.notNull (aDate, "Date");
    return aDate.getMonth () == Month.DECEMBER && aDate.getDayOfMonth () == 31;
  }

  @NonNull
  public static LocalDate getMax (@NonNull final LocalDate aDate1, @NonNull final LocalDate aDate2)
  {
    return aDate1.isAfter (aDate2) ? aDate1 : aDate2;
  }

  @NonNull
  public static LocalTime getMax (@NonNull final LocalTime aTime1, @NonNull final LocalTime aTime2)
  {
    return aTime1.isAfter (aTime2) ? aTime1 : aTime2;
  }

  @NonNull
  public static LocalDateTime getMax (@NonNull final LocalDateTime aDateTime1, @NonNull final LocalDateTime aDateTime2)
  {
    return aDateTime1.isAfter (aDateTime2) ? aDateTime1 : aDateTime2;
  }

  @NonNull
  public static ZonedDateTime getMax (@NonNull final ZonedDateTime aDateTime1, @NonNull final ZonedDateTime aDateTime2)
  {
    return aDateTime1.isAfter (aDateTime2) ? aDateTime1 : aDateTime2;
  }

  @NonNull
  public static OffsetDateTime getMax (@NonNull final OffsetDateTime aDateTime1,
                                       @NonNull final OffsetDateTime aDateTime2)
  {
    return aDateTime1.isAfter (aDateTime2) ? aDateTime1 : aDateTime2;
  }

  @NonNull
  public static LocalDate getMin (@NonNull final LocalDate aDate1, @NonNull final LocalDate aDate2)
  {
    return aDate1.isBefore (aDate2) ? aDate1 : aDate2;
  }

  @NonNull
  public static LocalTime getMin (@NonNull final LocalTime aTime1, @NonNull final LocalTime aTime2)
  {
    return aTime1.isBefore (aTime2) ? aTime1 : aTime2;
  }

  @NonNull
  public static LocalDateTime getMin (@NonNull final LocalDateTime aDateTime1, @NonNull final LocalDateTime aDateTime2)
  {
    return aDateTime1.isBefore (aDateTime2) ? aDateTime1 : aDateTime2;
  }

  @NonNull
  public static ZonedDateTime getMin (@NonNull final ZonedDateTime aDateTime1, @NonNull final ZonedDateTime aDateTime2)
  {
    return aDateTime1.isBefore (aDateTime2) ? aDateTime1 : aDateTime2;
  }

  @NonNull
  public static OffsetDateTime getMin (@NonNull final OffsetDateTime aDateTime1,
                                       @NonNull final OffsetDateTime aDateTime2)
  {
    return aDateTime1.isBefore (aDateTime2) ? aDateTime1 : aDateTime2;
  }

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
