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
package com.helger.datetime.period;

import java.time.LocalDateTime;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.datetime.domain.IHasStartAndEnd;
import com.helger.datetime.helper.PDTFactory;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Base interface for a period consisting of 2 local date periods.
 *
 * @author Philip Helger
 * @since 10.0.0
 */
public interface ILocalDateTimePeriod extends IHasStartAndEnd <LocalDateTime>
{
  /**
   * Check if the provided query date time is between start and end. A
   * <code>null</code> start means "since forever". A <code>null</code> end
   * means "until eternity and beyond".
   *
   * @param aStart
   *        Start date time. May be <code>null</code>.
   * @param bInclStart
   *        <code>true</code> if "start date time" = "query date time" should be
   *        a match, <code>false</code> if not.
   * @param aEnd
   *        End date time may be <code>null</code>.
   * @param bInclEnd
   *        <code>true</code> if "end date time" = "query date time" should be a
   *        match, <code>false</code> if not.
   * @param aQuery
   *        Date time to query whether it is inside or not. May not be
   *        <code>null</code>.
   * @return <code>true</code> if query date time &ge; start date time and &le;
   *         end date time
   */
  static boolean isInside (@Nullable final LocalDateTime aStart,
                           final boolean bInclStart,
                           @Nullable final LocalDateTime aEnd,
                           final boolean bInclEnd,
                           @Nonnull final LocalDateTime aQuery)
  {
    ValueEnforcer.notNull (aQuery, "QueryDT");

    if (aStart != null && (bInclStart ? aQuery.compareTo (aStart) < 0 : aQuery.compareTo (aStart) <= 0))
      return false;

    if (aEnd != null && (bInclEnd ? aQuery.compareTo (aEnd) > 0 : aQuery.compareTo (aEnd) >= 0))
      return false;

    return true;
  }

  /**
   * Check if the provided date time is inside this period, assuming that start
   * and end are included in/part of the range.
   *
   * @param bInclBoundaries
   *        <code>true</code> if "start date" = "query date" should be a match
   *        i.e. if "end date" = "query date" should be a match,
   *        <code>false</code> if this should not be a match.
   * @param aDate
   *        time Date time to check. May not be <code>null</code>.
   * @return <code>true</code> if it is contained, <code>false</code> otherwise.
   * @see #isInside(LocalDateTime, boolean, LocalDateTime, boolean,
   *      LocalDateTime)
   */
  default boolean isInPeriod (final boolean bInclBoundaries, @Nonnull final LocalDateTime aDate)
  {
    return isInside (getStart (), bInclBoundaries, getEnd (), bInclBoundaries, aDate);
  }

  /**
   * Check if the provided date time is inside this period, assuming that start
   * and end are included in/part of the range.
   *
   * @param aDateTime
   *        Date time to check. May not be <code>null</code>.
   * @return <code>true</code> if it is contained, <code>false</code> otherwise.
   * @see #isInPeriod(boolean, LocalDateTime)
   * @see #isNowInPeriodIncl()
   */
  default boolean isInPeriodIncl (@Nonnull final LocalDateTime aDateTime)
  {
    return isInPeriod (true, aDateTime);
  }

  /**
   * Check if the current date time is inside this period, assuming that start
   * and end are included in/part of the range.
   *
   * @return <code>true</code> if the current date is contained,
   *         <code>false</code> otherwise.
   * @see #isInPeriod(boolean, LocalDateTime)
   * @see #isInPeriodIncl(LocalDateTime)
   */
  default boolean isNowInPeriodIncl ()
  {
    return isInPeriodIncl (PDTFactory.getCurrentLocalDateTime ());
  }

  /**
   * Check if the provided date time is inside this period, assuming that start
   * and end are excluded from/not part of the range.
   *
   * @param aDateTime
   *        Date time to check. May not be <code>null</code>.
   * @return <code>true</code> if it is contained, <code>false</code> otherwise.
   * @see #isInPeriod(boolean, LocalDateTime)
   * @see #isNowInPeriodExcl()
   */
  default boolean isInPeriodExcl (@Nonnull final LocalDateTime aDateTime)
  {
    return isInPeriod (false, aDateTime);
  }

  /**
   * Check if the current date time is inside this period, assuming that start
   * and end are excluded from/not part of the range.
   *
   * @return <code>true</code> if the current date time is contained,
   *         <code>false</code> otherwise.
   * @see #isInPeriod(boolean, LocalDateTime)
   * @see #isInPeriodExcl(LocalDateTime)
   */
  default boolean isNowInPeriodExcl ()
  {
    return isInPeriodExcl (PDTFactory.getCurrentLocalDateTime ());
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
   *        i.e. if "end date time" = "query date time" should be a match,
   *        <code>false</code> if this should not be a match.
   * @return <code>true</code> if the 2 ranges have at least one point in time
   *         (with duration 0) that they share.
   */
  static boolean hasOverlap (@Nullable final LocalDateTime aStart1,
                             @Nullable final LocalDateTime aEnd1,
                             @Nullable final LocalDateTime aStart2,
                             @Nullable final LocalDateTime aEnd2,
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

  default boolean isOverlappingWith (@Nonnull final ILocalDateTimePeriod aPeriod, final boolean bInclBoundaries)
  {
    return hasOverlap (getStart (), getEnd (), aPeriod.getStart (), aPeriod.getEnd (), bInclBoundaries);
  }

  default boolean isOverlappingWithIncl (@Nonnull final ILocalDateTimePeriod aPeriod)
  {
    return isOverlappingWith (aPeriod, true);
  }

  default boolean isOverlappingWithExcl (@Nonnull final ILocalDateTimePeriod aPeriod)
  {
    return isOverlappingWith (aPeriod, false);
  }
}
