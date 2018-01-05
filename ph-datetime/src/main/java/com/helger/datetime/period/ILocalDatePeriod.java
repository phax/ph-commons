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
package com.helger.datetime.period;

import java.io.Serializable;
import java.time.LocalDate;

import javax.annotation.Nonnull;

import com.helger.commons.datetime.PDTFactory;
import com.helger.datetime.domain.IHasStartAndEnd;

/**
 * Base interface for a period consisting of 2 local date periods.
 *
 * @author Philip Helger
 */
public interface ILocalDatePeriod extends Serializable, IHasStartAndEnd <LocalDate>
{
  /**
   * Check if the provided date is inside this period, assuming that start and
   * end are included in/part of the range.
   *
   * @param aDate
   *        Date to check. May not be <code>null</code>.
   * @return <code>true</code> if it is contained, <code>false</code> otherwise.
   * @see #isNowInPeriodIncl()
   * @since 8.6.5
   */
  default boolean isInPeriodIncl (@Nonnull final LocalDate aDate)
  {
    final LocalDate aStart = getStart ();
    if (aStart != null && aDate.compareTo (aStart) < 0)
      return false;

    final LocalDate aEnd = getEnd ();
    if (aEnd != null && aDate.compareTo (aEnd) > 0)
      return false;

    return true;
  }

  /**
   * Check if the current date is inside this period, assuming that start and
   * end are included in/part of the range.
   *
   * @return <code>true</code> if the current date is contained,
   *         <code>false</code> otherwise.
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
   * @see #isNowInPeriodExcl()
   * @since 8.6.5
   */
  default boolean isInPeriodExcl (@Nonnull final LocalDate aDate)
  {
    final LocalDate aStart = getStart ();
    if (aStart != null && aDate.compareTo (aStart) <= 0)
      return false;

    final LocalDate aEnd = getEnd ();
    if (aEnd != null && aDate.compareTo (aEnd) >= 0)
      return false;

    return true;
  }

  /**
   * Check if the current date is inside this period, assuming that start and
   * end are excluded from/not part of the range.
   *
   * @return <code>true</code> if the current date is contained,
   *         <code>false</code> otherwise.
   * @see #isInPeriodExcl(LocalDate)
   * @since 8.6.5
   */
  default boolean isNowInPeriodExcl ()
  {
    return isInPeriodExcl (PDTFactory.getCurrentLocalDate ());
  }
}
