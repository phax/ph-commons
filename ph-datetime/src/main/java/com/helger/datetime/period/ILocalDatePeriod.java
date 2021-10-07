/*
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
package com.helger.datetime.period;

import java.time.LocalDate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.datetime.PDTFactory;
import com.helger.datetime.domain.IHasStartAndEnd;

/**
 * Base interface for a period consisting of 2 local date periods.
 *
 * @author Philip Helger
 */
public interface ILocalDatePeriod extends IHasStartAndEnd <LocalDate>
{
  /**
   * Check if the provided query date is between start and end. A
   * <code>null</code> start means "since forever". A <code>null</code> end
   * means "until eternity and beyond".
   *
   * @param aStart
   *        Start date. May be <code>null</code>.
   * @param bInclStart
   *        <code>true</code> if "start date" = "query date" should be a match,
   *        <code>false</code> if not.
   * @param aEnd
   *        End date may be <code>null</code>.
   * @param bInclEnd
   *        <code>true</code> if "end date" = "query date" should be a match,
   *        <code>false</code> if not.
   * @param aQuery
   *        Date to query whether it is inside or not. May not be
   *        <code>null</code>.
   * @return <code>true</code> if query date &ge; start date and &le; end date
   * @since 10.0.0
   */
  static boolean isInside (@Nullable final LocalDate aStart,
                           final boolean bInclStart,
                           @Nullable final LocalDate aEnd,
                           final boolean bInclEnd,
                           @Nonnull final LocalDate aQuery)
  {
    ValueEnforcer.notNull (aQuery, "QueryDT");

    if (aStart != null && (bInclStart ? aQuery.compareTo (aStart) < 0 : aQuery.compareTo (aStart) <= 0))
      return false;

    if (aEnd != null && (bInclEnd ? aQuery.compareTo (aEnd) > 0 : aQuery.compareTo (aEnd) >= 0))
      return false;

    return true;
  }

  /**
   * Check if the provided date is inside this period, assuming that start and
   * end are included in/part of the range.
   *
   * @param bInclBoundaries
   *        <code>true</code> if "start date" = "query date" should be a match
   *        i.e. if "end date" = "query date" should be a match,
   *        <code>false</code> if this should not be a match.
   * @param aDate
   *        Date to check. May not be <code>null</code>.
   * @return <code>true</code> if it is contained, <code>false</code> otherwise.
   * @see #isInside(LocalDate, boolean, LocalDate, boolean, LocalDate)
   * @since 10.0.0
   */
  default boolean isInPeriod (final boolean bInclBoundaries, @Nonnull final LocalDate aDate)
  {
    return isInside (getStart (), bInclBoundaries, getEnd (), bInclBoundaries, aDate);
  }

  /**
   * Check if the provided date is inside this period, assuming that start and
   * end are included in/part of the range.
   *
   * @param aDate
   *        Date to check. May not be <code>null</code>.
   * @return <code>true</code> if it is contained, <code>false</code> otherwise.
   * @see #isInPeriod(boolean, LocalDate)
   * @see #isNowInPeriodIncl()
   * @since 8.6.5
   */
  default boolean isInPeriodIncl (@Nonnull final LocalDate aDate)
  {
    return isInPeriod (true, aDate);
  }

  /**
   * Check if the current date is inside this period, assuming that start and
   * end are included in/part of the range.
   *
   * @return <code>true</code> if the current date is contained,
   *         <code>false</code> otherwise.
   * @see #isInPeriod(boolean, LocalDate)
   * @see #isInPeriodIncl(LocalDate)
   * @since 8.6.5
   */
  default boolean isNowInPeriodIncl ()
  {
    return isInPeriodIncl (PDTFactory.getCurrentLocalDate ());
  }

  /**
   * Check if the provided date is inside this period, assuming that start and
   * end are excluded from/not part of the range.
   *
   * @param aDate
   *        Date to check. May not be <code>null</code>.
   * @return <code>true</code> if it is contained, <code>false</code> otherwise.
   * @see #isInPeriod(boolean, LocalDate)
   * @see #isNowInPeriodExcl()
   * @since 8.6.5
   */
  default boolean isInPeriodExcl (@Nonnull final LocalDate aDate)
  {
    return isInPeriod (false, aDate);
  }

  /**
   * Check if the current date is inside this period, assuming that start and
   * end are excluded from/not part of the range.
   *
   * @return <code>true</code> if the current date is contained,
   *         <code>false</code> otherwise.
   * @see #isInPeriod(boolean, LocalDate)
   * @see #isInPeriodExcl(LocalDate)
   * @since 8.6.5
   */
  default boolean isNowInPeriodExcl ()
  {
    return isInPeriodExcl (PDTFactory.getCurrentLocalDate ());
  }

  /**
   * Check if the provided range 1 has an overlap with the provided range 2.
   *
   * @param aStart1
   *        Start date of the first range. May be <code>null</code>.
   * @param aEnd1
   *        End date of the first range. May be <code>null</code> meaning it's
   *        validity is "until eternity and beyond".
   * @param aStart2
   *        Start date of the second range. May be <code>null</code>.
   * @param aEnd2
   *        End date of the second range. May be <code>null</code> meaning it's
   *        validity is "until eternity and beyond".
   * @param bInclBoundaries
   *        <code>true</code> if "start date" = "query date" should be a match
   *        i.e. if "end date" = "query date" should be a match,
   *        <code>false</code> if this should not be a match.
   * @return <code>true</code> if the 2 ranges have at least one point in time
   *         (with duration 0) that they share.
   * @since 10.0.0
   */
  static boolean hasOverlap (@Nullable final LocalDate aStart1,
                             @Nullable final LocalDate aEnd1,
                             @Nullable final LocalDate aStart2,
                             @Nullable final LocalDate aEnd2,
                             final boolean bInclBoundaries)
  {
    if (aStart2 != null && isInside (aStart1, bInclBoundaries, aEnd1, bInclBoundaries, aStart2))
    {
      // New start date is in valid range
      return true;
    }
    // New start date is out of range
    if (aEnd2 != null && isInside (aStart1, bInclBoundaries, aEnd1, bInclBoundaries, aEnd2))
    {
      // New end date is in valid range
      return true;
    }

    // start1 - end1: 5.1. - 15.1.
    // start2 - end2: 1.1. - 31.1.
    if (aStart1 != null && isInside (aStart2, bInclBoundaries, aEnd2, bInclBoundaries, aStart1))
      return true;

    if (aEnd1 != null && isInside (aStart2, bInclBoundaries, aEnd2, bInclBoundaries, aEnd1))
      return true;

    return false;
  }

  default boolean isOverlappingWith (@Nonnull final ILocalDatePeriod aPeriod, final boolean bInclBoundaries)
  {
    return hasOverlap (getStart (), getEnd (), aPeriod.getStart (), aPeriod.getEnd (), bInclBoundaries);
  }

  default boolean isOverlappingWithIncl (@Nonnull final ILocalDatePeriod aPeriod)
  {
    return isOverlappingWith (aPeriod, true);
  }

  default boolean isOverlappingWithExcl (@Nonnull final ILocalDatePeriod aPeriod)
  {
    return isOverlappingWith (aPeriod, false);
  }
}
