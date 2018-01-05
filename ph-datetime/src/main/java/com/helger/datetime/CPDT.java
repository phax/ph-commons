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
package com.helger.datetime;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * Some date/time related constants.
 *
 * @author Philip Helger
 */
@Immutable
public final class CPDT
{
  /** Default start of week: Monday */
  public static final DayOfWeek START_OF_WEEK_DAY = DayOfWeek.MONDAY;
  /** Default end of week: Sunday */
  public static final DayOfWeek END_OF_WEEK_DAY = DayOfWeek.SUNDAY;

  public static final int MIN_YEAR_INT32 = LocalDate.ofEpochDay (0).getYear ();

  /** Default null local date: 1.1.1970 */
  public static final LocalDate NULL_LOCAL_DATE = LocalDate.ofEpochDay (0);
  /** Default null local time: 00:00:00.000 */
  public static final LocalTime NULL_LOCAL_TIME = LocalTime.MIN;
  /** Default null local date time : 1.1.1970 00:00:00.000 */
  public static final LocalDateTime NULL_LOCAL_DATETIME = LocalDateTime.of (NULL_LOCAL_DATE, NULL_LOCAL_TIME);
  /**
   * Default null date time : 1.1.1970 00:00:00.000 with the default timezone!
   */
  public static final ZonedDateTime NULL_DATETIME = ZonedDateTime.ofInstant (Instant.EPOCH, ZoneId.systemDefault ());
  /** Default null date time : 1.1.1970 00:00:00.000 with the UTC timezone! */
  public static final ZonedDateTime NULL_DATETIME_UTC = ZonedDateTime.ofInstant (Instant.EPOCH,
                                                                                 ZoneOffset.UTC.normalized ());
  /** Default empty period */
  public static final Period NULL_PERIOD = Period.ZERO;
  /** Default empty duration */
  public static final Duration NULL_DURATION = Duration.ZERO;

  /** The last year to which the Julian choreography can be applied. */
  public static final int LAST_JULIAN_YEAR = 1583;

  @PresentForCodeCoverage
  private static final CPDT s_aInstance = new CPDT ();

  private CPDT ()
  {}
}
