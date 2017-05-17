/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
import java.time.Duration;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.equals.EqualsHelper;
import com.helger.datetime.CPDT;

/**
 * Some date/time utility methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class PDTHelper
{
  @PresentForCodeCoverage
  private static final PDTHelper s_aInstance = new PDTHelper ();

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

  public static boolean isWeekend (@Nonnull final LocalDateTime aDT)
  {
    return isWeekendDay (aDT.getDayOfWeek ());
  }

  public static boolean isWeekend (@Nonnull final LocalDate aDT)
  {
    return isWeekendDay (aDT.getDayOfWeek ());
  }

  public static boolean isFirstDayOfWeek (final DayOfWeek nDayOfWeek)
  {
    return nDayOfWeek == CPDT.START_OF_WEEK_DAY;
  }

  public static boolean isFirstDayOfWeek (@Nonnull final ZonedDateTime aDT)
  {
    return isFirstDayOfWeek (aDT.getDayOfWeek ());
  }

  public static boolean isFirstDayOfWeek (@Nonnull final LocalDateTime aDT)
  {
    return isFirstDayOfWeek (aDT.getDayOfWeek ());
  }

  public static boolean isFirstDayOfWeek (@Nonnull final LocalDate aDT)
  {
    return isFirstDayOfWeek (aDT.getDayOfWeek ());
  }

  public static boolean isLastDayOfWeek (final DayOfWeek nDayOfWeek)
  {
    return nDayOfWeek == CPDT.END_OF_WEEK_DAY;
  }

  public static boolean isLastDayOfWeek (@Nonnull final ZonedDateTime aDT)
  {
    return isLastDayOfWeek (aDT.getDayOfWeek ());
  }

  public static boolean isLastDayOfWeek (@Nonnull final LocalDateTime aDT)
  {
    return isLastDayOfWeek (aDT.getDayOfWeek ());
  }

  public static boolean isLastDayOfWeek (@Nonnull final LocalDate aDT)
  {
    return isLastDayOfWeek (aDT.getDayOfWeek ());
  }

  public static boolean isWorkDay (@Nonnull final LocalDate aDate)
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
   * @return days not counting Saturdays and Sundays. If start date is after end
   *         date, the value will be negative! If start date equals end date the
   *         return will be 1 if it is a week day.
   */
  public static int getWeekDays (@Nonnull final LocalDate aStartDate, @Nonnull final LocalDate aEndDate)
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

  public static boolean isSameYearAndDay (@Nonnull final LocalDate x, @Nonnull final LocalDate y)
  {
    return x.getYear () == y.getYear () && x.getDayOfYear () == y.getDayOfYear ();
  }

  public static boolean isSameYearAndWeek (@Nonnull final LocalDate x,
                                           @Nonnull final LocalDate y,
                                           @Nonnull final Locale aLocale)
  {
    return x.getYear () == y.getYear () && getWeekOfWeekBasedYear (x, aLocale) == getWeekOfWeekBasedYear (y, aLocale);
  }

  public static boolean isSameMonthAndDay (@Nonnull final LocalDate x, @Nonnull final LocalDate y)
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

  public static int getWeekOfWeekBasedYear (@Nonnull final TemporalAccessor aDT, @Nonnull final Locale aLocale)
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
  public static int getStartWeekOfMonth (@Nonnull final LocalDateTime aDT, @Nonnull final Locale aLocale)
  {
    return getWeekOfWeekBasedYear (aDT.withDayOfMonth (1), aLocale);
  }

  public static int getStartWeekOfMonth (@Nonnull final LocalDate aDT, @Nonnull final Locale aLocale)
  {
    return getWeekOfWeekBasedYear (aDT.withDayOfMonth (1), aLocale);
  }

  public static int getStartWeekOfMonth (@Nonnull final ZonedDateTime aDT, @Nonnull final Locale aLocale)
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
  public static int getEndWeekOfMonth (@Nonnull final LocalDateTime aDT, @Nonnull final Locale aLocale)
  {
    return getWeekOfWeekBasedYear (aDT.plusMonths (1).withDayOfMonth (1).minusDays (1), aLocale);
  }

  public static int getEndWeekOfMonth (@Nonnull final LocalDate aDT, @Nonnull final Locale aLocale)
  {
    return getWeekOfWeekBasedYear (aDT.plusMonths (1).withDayOfMonth (1).minusDays (1), aLocale);
  }

  public static int getEndWeekOfMonth (@Nonnull final ZonedDateTime aDT, @Nonnull final Locale aLocale)
  {
    return getWeekOfWeekBasedYear (aDT.plusMonths (1).withDayOfMonth (1).minusDays (1), aLocale);
  }

  @Nonnull
  public static LocalDate getCurrentOrNextWeekday ()
  {
    LocalDate aDT = PDTFactory.getCurrentLocalDate ();
    while (isWeekend (aDT))
      aDT = aDT.plusDays (1);
    return aDT;
  }

  /**
   * Get the next working day based on the current day. If the current day is a
   * working day, the current day is returned. A working day is determined by:
   * it's not a weekend day (usually Saturday or Sunday).
   *
   * @return The next matching date.
   */
  @Nonnull
  public static LocalDate getCurrentOrNextWorkDay ()
  {
    LocalDate aDT = PDTFactory.getCurrentLocalDate ();
    while (isWorkDay (aDT))
      aDT = aDT.plusDays (1);
    return aDT;
  }

  /**
   * Compare two dates by birthday. This means, the dates are only compared by
   * day and month, and <b>not</b> by year!
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
   * Check if the two birthdays are equal. Equal birthdays are identified by
   * equal months and equal days.
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

  public static boolean isNewYearsEve (@Nonnull final LocalDate aDate)
  {
    ValueEnforcer.notNull (aDate, "Date");
    return aDate.getMonth () == Month.DECEMBER && aDate.getDayOfMonth () == 31;
  }

  /**
   * @param aDuration1
   *        First value. May not be <code>null</code>.
   * @param aDuration2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &gt; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isGT(Duration,Duration)} instead
   */
  @Deprecated
  public static boolean isGreater (@Nonnull final Duration aDuration1, @Nonnull final Duration aDuration2)
  {
    return isGT (aDuration1, aDuration2);
  }

  /**
   * @param aDuration1
   *        First value. May not be <code>null</code>.
   * @param aDuration2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &gt; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isGT (@Nonnull final Duration aDuration1, @Nonnull final Duration aDuration2)
  {
    return aDuration1.compareTo (aDuration2) > 0;
  }

  /**
   * @param aDuration1
   *        First value. May not be <code>null</code>.
   * @param aDuration2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &ge; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isGE(Duration,Duration)} instead
   */
  @Deprecated
  public static boolean isGreaterOrEqual (@Nonnull final Duration aDuration1, @Nonnull final Duration aDuration2)
  {
    return isGE (aDuration1, aDuration2);
  }

  /**
   * @param aDuration1
   *        First value. May not be <code>null</code>.
   * @param aDuration2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &ge; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isGE (@Nonnull final Duration aDuration1, @Nonnull final Duration aDuration2)
  {
    return aDuration1.compareTo (aDuration2) >= 0;
  }

  /**
   * @param aDuration1
   *        First value. May not be <code>null</code>.
   * @param aDuration2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &lt; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isLT(Duration,Duration)} instead
   */
  @Deprecated
  public static boolean isLess (@Nonnull final Duration aDuration1, @Nonnull final Duration aDuration2)
  {
    return isLT (aDuration1, aDuration2);
  }

  /**
   * @param aDuration1
   *        First value. May not be <code>null</code>.
   * @param aDuration2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &lt; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isLT (@Nonnull final Duration aDuration1, @Nonnull final Duration aDuration2)
  {
    return aDuration1.compareTo (aDuration2) < 0;
  }

  /**
   * @param aDuration1
   *        First value. May not be <code>null</code>.
   * @param aDuration2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &le; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isLE(Duration,Duration)} instead
   */
  @Deprecated
  public static boolean isLessOrEqual (@Nonnull final Duration aDuration1, @Nonnull final Duration aDuration2)
  {
    return isLE (aDuration1, aDuration2);
  }

  /**
   * @param aDuration1
   *        First value. May not be <code>null</code>.
   * @param aDuration2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &le; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isLE (@Nonnull final Duration aDuration1, @Nonnull final Duration aDuration2)
  {
    return aDuration1.compareTo (aDuration2) <= 0;
  }

  /**
   * @param aDT1
   *        First value. May not be <code>null</code>.
   * @param aDT2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &gt; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isGT(ZonedDateTime,ZonedDateTime)} instead
   */
  @Deprecated
  public static boolean isGreater (@Nonnull final ZonedDateTime aDT1, @Nonnull final ZonedDateTime aDT2)
  {
    return isGT (aDT1, aDT2);
  }

  /**
   * @param aDT1
   *        First value. May not be <code>null</code>.
   * @param aDT2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &gt; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isGT (@Nonnull final ZonedDateTime aDT1, @Nonnull final ZonedDateTime aDT2)
  {
    return aDT1.compareTo (aDT2) > 0;
  }

  /**
   * @param aDT1
   *        First value. May not be <code>null</code>.
   * @param aDT2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &ge; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isGE(ZonedDateTime,ZonedDateTime)} instead
   */
  @Deprecated
  public static boolean isGreaterOrEqual (@Nonnull final ZonedDateTime aDT1, @Nonnull final ZonedDateTime aDT2)
  {
    return isGE (aDT1, aDT2);
  }

  /**
   * @param aDT1
   *        First value. May not be <code>null</code>.
   * @param aDT2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &ge; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isGE (@Nonnull final ZonedDateTime aDT1, @Nonnull final ZonedDateTime aDT2)
  {
    return aDT1.compareTo (aDT2) >= 0;
  }

  /**
   * @param aDT1
   *        First value. May not be <code>null</code>.
   * @param aDT2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &lt; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isLT(ZonedDateTime,ZonedDateTime)} instead
   */
  @Deprecated
  public static boolean isLess (@Nonnull final ZonedDateTime aDT1, @Nonnull final ZonedDateTime aDT2)
  {
    return isLT (aDT1, aDT2);
  }

  /**
   * @param aDT1
   *        First value. May not be <code>null</code>.
   * @param aDT2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &lt; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isLT (@Nonnull final ZonedDateTime aDT1, @Nonnull final ZonedDateTime aDT2)
  {
    return aDT1.compareTo (aDT2) < 0;
  }

  /**
   * @param aDT1
   *        First value. May not be <code>null</code>.
   * @param aDT2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &le; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isLE(ZonedDateTime,ZonedDateTime)} instead
   */
  @Deprecated
  public static boolean isLessOrEqual (@Nonnull final ZonedDateTime aDT1, @Nonnull final ZonedDateTime aDT2)
  {
    return isLE (aDT1, aDT2);
  }

  /**
   * @param aDT1
   *        First value. May not be <code>null</code>.
   * @param aDT2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &le; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isLE (@Nonnull final ZonedDateTime aDT1, @Nonnull final ZonedDateTime aDT2)
  {
    return aDT1.compareTo (aDT2) <= 0;
  }

  /**
   * @param aDate1
   *        First value. May not be <code>null</code>.
   * @param aDate2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &gt; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isGT(LocalDate,LocalDate)} instead
   */
  @Deprecated
  public static boolean isGreater (@Nonnull final LocalDate aDate1, @Nonnull final LocalDate aDate2)
  {
    return isGT (aDate1, aDate2);
  }

  /**
   * @param aDate1
   *        First value. May not be <code>null</code>.
   * @param aDate2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &gt; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isGT (@Nonnull final LocalDate aDate1, @Nonnull final LocalDate aDate2)
  {
    return aDate1.compareTo (aDate2) > 0;
  }

  /**
   * @param aDate1
   *        First value. May not be <code>null</code>.
   * @param aDate2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &ge; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isGE(LocalDate,LocalDate)} instead
   */
  @Deprecated
  public static boolean isGreaterOrEqual (@Nonnull final LocalDate aDate1, @Nonnull final LocalDate aDate2)
  {
    return isGE (aDate1, aDate2);
  }

  /**
   * @param aDate1
   *        First value. May not be <code>null</code>.
   * @param aDate2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &ge; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isGE (@Nonnull final LocalDate aDate1, @Nonnull final LocalDate aDate2)
  {
    return aDate1.compareTo (aDate2) >= 0;
  }

  /**
   * @param aDate1
   *        First value. May not be <code>null</code>.
   * @param aDate2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &lt; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isLT(LocalDate,LocalDate)} instead
   */
  @Deprecated
  public static boolean isLess (@Nonnull final LocalDate aDate1, @Nonnull final LocalDate aDate2)
  {
    return isLT (aDate1, aDate2);
  }

  /**
   * @param aDate1
   *        First value. May not be <code>null</code>.
   * @param aDate2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &lt; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isLT (@Nonnull final LocalDate aDate1, @Nonnull final LocalDate aDate2)
  {
    return aDate1.compareTo (aDate2) < 0;
  }

  /**
   * @param aDate1
   *        First value. May not be <code>null</code>.
   * @param aDate2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &le; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isLE(LocalDate,LocalDate)} instead
   */
  @Deprecated
  public static boolean isLessOrEqual (@Nonnull final LocalDate aDate1, @Nonnull final LocalDate aDate2)
  {
    return isLE (aDate1, aDate2);
  }

  /**
   * @param aDate1
   *        First value. May not be <code>null</code>.
   * @param aDate2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &le; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isLE (@Nonnull final LocalDate aDate1, @Nonnull final LocalDate aDate2)
  {
    return aDate1.compareTo (aDate2) <= 0;
  }

  /**
   * @param aTime1
   *        First value. May not be <code>null</code>.
   * @param aTime2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &gt; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isGT(LocalTime,LocalTime)} instead
   */
  @Deprecated
  public static boolean isGreater (@Nonnull final LocalTime aTime1, @Nonnull final LocalTime aTime2)
  {
    return isGT (aTime1, aTime2);
  }

  /**
   * @param aTime1
   *        First value. May not be <code>null</code>.
   * @param aTime2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &gt; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isGT (@Nonnull final LocalTime aTime1, @Nonnull final LocalTime aTime2)
  {
    return aTime1.compareTo (aTime2) > 0;
  }

  /**
   * @param aTime1
   *        First value. May not be <code>null</code>.
   * @param aTime2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &ge; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isGE(LocalTime,LocalTime)} instead
   */
  @Deprecated
  public static boolean isGreaterOrEqual (@Nonnull final LocalTime aTime1, @Nonnull final LocalTime aTime2)
  {
    return isGE (aTime1, aTime2);
  }

  /**
   * @param aTime1
   *        First value. May not be <code>null</code>.
   * @param aTime2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &ge; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isGE (@Nonnull final LocalTime aTime1, @Nonnull final LocalTime aTime2)
  {
    return aTime1.compareTo (aTime2) >= 0;
  }

  /**
   * @param aTime1
   *        First value. May not be <code>null</code>.
   * @param aTime2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &lt; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isLT(LocalTime,LocalTime)} instead
   */
  @Deprecated
  public static boolean isLess (@Nonnull final LocalTime aTime1, @Nonnull final LocalTime aTime2)
  {
    return isLT (aTime1, aTime2);
  }

  /**
   * @param aTime1
   *        First value. May not be <code>null</code>.
   * @param aTime2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &lt; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isLT (@Nonnull final LocalTime aTime1, @Nonnull final LocalTime aTime2)
  {
    return aTime1.compareTo (aTime2) < 0;
  }

  /**
   * @param aTime1
   *        First value. May not be <code>null</code>.
   * @param aTime2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &le; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isLE(LocalTime,LocalTime)} instead
   */
  @Deprecated
  public static boolean isLessOrEqual (@Nonnull final LocalTime aTime1, @Nonnull final LocalTime aTime2)
  {
    return isLE (aTime1, aTime2);
  }

  /**
   * @param aTime1
   *        First value. May not be <code>null</code>.
   * @param aTime2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &le; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isLE (@Nonnull final LocalTime aTime1, @Nonnull final LocalTime aTime2)
  {
    return aTime1.compareTo (aTime2) <= 0;
  }

  /**
   * @param aDateTime1
   *        First value. May not be <code>null</code>.
   * @param aDateTime2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &gt; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isGT(LocalDateTime,LocalDateTime)} instead
   */
  @Deprecated
  public static boolean isGreater (@Nonnull final LocalDateTime aDateTime1, @Nonnull final LocalDateTime aDateTime2)
  {
    return isGT (aDateTime1, aDateTime2);
  }

  /**
   * @param aDateTime1
   *        First value. May not be <code>null</code>.
   * @param aDateTime2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &gt; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isGT (@Nonnull final LocalDateTime aDateTime1, @Nonnull final LocalDateTime aDateTime2)
  {
    return aDateTime1.compareTo (aDateTime2) > 0;
  }

  /**
   * @param aDateTime1
   *        First value. May not be <code>null</code>.
   * @param aDateTime2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &ge; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isGE(LocalDateTime,LocalDateTime)} instead
   */
  @Deprecated
  public static boolean isGreaterOrEqual (@Nonnull final LocalDateTime aDateTime1,
                                          @Nonnull final LocalDateTime aDateTime2)
  {
    return isGE (aDateTime1, aDateTime2);
  }

  /**
   * @param aDateTime1
   *        First value. May not be <code>null</code>.
   * @param aDateTime2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &ge; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isGE (@Nonnull final LocalDateTime aDateTime1, @Nonnull final LocalDateTime aDateTime2)
  {
    return aDateTime1.compareTo (aDateTime2) >= 0;
  }

  /**
   * @param aDateTime1
   *        First value. May not be <code>null</code>.
   * @param aDateTime2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &lt; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isLT(LocalDateTime,LocalDateTime)} instead
   */
  @Deprecated
  public static boolean isLess (@Nonnull final LocalDateTime aDateTime1, @Nonnull final LocalDateTime aDateTime2)
  {
    return isLT (aDateTime1, aDateTime2);
  }

  /**
   * @param aDateTime1
   *        First value. May not be <code>null</code>.
   * @param aDateTime2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &lt; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isLT (@Nonnull final LocalDateTime aDateTime1, @Nonnull final LocalDateTime aDateTime2)
  {
    return aDateTime1.compareTo (aDateTime2) < 0;
  }

  /**
   * @param aDateTime1
   *        First value. May not be <code>null</code>.
   * @param aDateTime2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &le; than second, <code>false</code>
   *         otherwise.
   * @deprecated Use {@link #isLE(LocalDateTime,LocalDateTime)} instead
   */
  @Deprecated
  public static boolean isLessOrEqual (@Nonnull final LocalDateTime aDateTime1, @Nonnull final LocalDateTime aDateTime2)
  {
    return isLE (aDateTime1, aDateTime2);
  }

  /**
   * @param aDateTime1
   *        First value. May not be <code>null</code>.
   * @param aDateTime2
   *        Second value. May not be <code>null</code>.
   * @return <code>true</code> if first is &le; than second, <code>false</code>
   *         otherwise.
   */
  public static boolean isLE (@Nonnull final LocalDateTime aDateTime1, @Nonnull final LocalDateTime aDateTime2)
  {
    return aDateTime1.compareTo (aDateTime2) <= 0;
  }

  @Nonnull
  public static LocalDate getMax (@Nonnull final LocalDate aDate1, @Nonnull final LocalDate aDate2)
  {
    return aDate1.isAfter (aDate2) ? aDate1 : aDate2;
  }

  @Nonnull
  public static LocalTime getMax (@Nonnull final LocalTime aTime1, @Nonnull final LocalTime aTime2)
  {
    return aTime1.isAfter (aTime2) ? aTime1 : aTime2;
  }

  @Nonnull
  public static LocalDateTime getMax (@Nonnull final LocalDateTime aDateTime1, @Nonnull final LocalDateTime aDateTime2)
  {
    return aDateTime1.isAfter (aDateTime2) ? aDateTime1 : aDateTime2;
  }

  @Nonnull
  public static ZonedDateTime getMax (@Nonnull final ZonedDateTime aDateTime1, @Nonnull final ZonedDateTime aDateTime2)
  {
    return aDateTime1.isAfter (aDateTime2) ? aDateTime1 : aDateTime2;
  }

  @Nonnull
  public static OffsetDateTime getMax (@Nonnull final OffsetDateTime aDateTime1,
                                       @Nonnull final OffsetDateTime aDateTime2)
  {
    return aDateTime1.isAfter (aDateTime2) ? aDateTime1 : aDateTime2;
  }

  @Nonnull
  public static LocalDate getMin (@Nonnull final LocalDate aDate1, @Nonnull final LocalDate aDate2)
  {
    return aDate1.isBefore (aDate2) ? aDate1 : aDate2;
  }

  @Nonnull
  public static LocalTime getMin (@Nonnull final LocalTime aTime1, @Nonnull final LocalTime aTime2)
  {
    return aTime1.isBefore (aTime2) ? aTime1 : aTime2;
  }

  @Nonnull
  public static LocalDateTime getMin (@Nonnull final LocalDateTime aDateTime1, @Nonnull final LocalDateTime aDateTime2)
  {
    return aDateTime1.isBefore (aDateTime2) ? aDateTime1 : aDateTime2;
  }

  @Nonnull
  public static ZonedDateTime getMin (@Nonnull final ZonedDateTime aDateTime1, @Nonnull final ZonedDateTime aDateTime2)
  {
    return aDateTime1.isBefore (aDateTime2) ? aDateTime1 : aDateTime2;
  }

  @Nonnull
  public static OffsetDateTime getMin (@Nonnull final OffsetDateTime aDateTime1,
                                       @Nonnull final OffsetDateTime aDateTime2)
  {
    return aDateTime1.isBefore (aDateTime2) ? aDateTime1 : aDateTime2;
  }

  public static long getDaysBetween (@Nonnull final Temporal aStartIncl, @Nonnull final Temporal aEndExcl)
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
  @Nonnull
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
  public static int getCalendarDayOfWeek (@Nonnull final DayOfWeek eDOW)
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
