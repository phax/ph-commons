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
package com.helger.datetime.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.Test;

import com.helger.datetime.CPDT;

/**
 * Additional test class for class {@link PDTHelper}, covering the null value checks, the day of
 * week helpers, the min/max methods and the conversions.
 *
 * @author Philip Helger
 */
public final class PDTHelperNullAndMinMaxTest
{
  private static final LocalDate LD1 = LocalDate.of (2020, Month.JANUARY, 2);
  private static final LocalDate LD2 = LocalDate.of (2021, Month.MARCH, 4);

  @Test
  public void testIsNullValue ()
  {
    assertTrue (PDTHelper.isNullValue ((LocalDate) null));
    assertTrue (PDTHelper.isNullValue (CPDT.NULL_LOCAL_DATE));
    assertFalse (PDTHelper.isNullValue (LD1));

    assertTrue (PDTHelper.isNullValue ((LocalTime) null));
    assertTrue (PDTHelper.isNullValue (CPDT.NULL_LOCAL_TIME));
    assertFalse (PDTHelper.isNullValue (LocalTime.of (12, 0)));

    assertTrue (PDTHelper.isNullValue ((LocalDateTime) null));
    assertTrue (PDTHelper.isNullValue (CPDT.NULL_LOCAL_DATETIME));
    assertFalse (PDTHelper.isNullValue (LocalDateTime.of (LD1, LocalTime.NOON)));

    assertTrue (PDTHelper.isNullValue ((ZonedDateTime) null));
    assertTrue (PDTHelper.isNullValue (CPDT.NULL_DATETIME));
    assertFalse (PDTHelper.isNullValue (CPDT.NULL_DATETIME.plusDays (1)));
  }

  @Test
  public void testWeekendAndWorkDay ()
  {
    assertTrue (PDTHelper.isWeekendDay (DayOfWeek.SATURDAY));
    assertTrue (PDTHelper.isWeekendDay (DayOfWeek.SUNDAY));
    assertFalse (PDTHelper.isWeekendDay (DayOfWeek.MONDAY));

    // 2020-01-04 is a Saturday, 2020-01-02 is a Thursday
    final LocalDate aSat = LocalDate.of (2020, Month.JANUARY, 4);
    assertTrue (PDTHelper.isWeekend (aSat));
    assertTrue (PDTHelper.isWeekend (aSat.atStartOfDay ()));
    assertFalse (PDTHelper.isWeekend (LD1));
    assertFalse (PDTHelper.isWorkDay (aSat));
    assertTrue (PDTHelper.isWorkDay (LD1));
  }

  @Test
  public void testFirstAndLastDayOfWeek ()
  {
    assertTrue (PDTHelper.isFirstDayOfWeek (DayOfWeek.MONDAY));
    assertFalse (PDTHelper.isFirstDayOfWeek (DayOfWeek.SUNDAY));
    assertTrue (PDTHelper.isLastDayOfWeek (DayOfWeek.SUNDAY));
    assertFalse (PDTHelper.isLastDayOfWeek (DayOfWeek.MONDAY));

    // 2020-01-06 is a Monday, 2020-01-05 is a Sunday
    final LocalDate aMon = LocalDate.of (2020, Month.JANUARY, 6);
    final LocalDate aSun = LocalDate.of (2020, Month.JANUARY, 5);

    assertTrue (PDTHelper.isFirstDayOfWeek (aMon));
    assertTrue (PDTHelper.isFirstDayOfWeek (aMon.atStartOfDay ()));
    assertTrue (PDTHelper.isFirstDayOfWeek (aMon.atStartOfDay (ZoneOffset.UTC)));
    assertFalse (PDTHelper.isFirstDayOfWeek (aSun));

    assertTrue (PDTHelper.isLastDayOfWeek (aSun));
    assertTrue (PDTHelper.isLastDayOfWeek (aSun.atStartOfDay ()));
    assertTrue (PDTHelper.isLastDayOfWeek (aSun.atStartOfDay (ZoneOffset.UTC)));
    assertFalse (PDTHelper.isLastDayOfWeek (aMon));
  }

  @Test
  public void testSameChecks ()
  {
    assertTrue (PDTHelper.isSameYearAndDay (LD1, LocalDate.of (2020, Month.JANUARY, 2)));
    assertFalse (PDTHelper.isSameYearAndDay (LD1, LD2));

    assertTrue (PDTHelper.isSameMonthAndDay (LD1, LocalDate.of (2021, Month.JANUARY, 2)));
    assertFalse (PDTHelper.isSameMonthAndDay (LD1, LD2));

    assertTrue (PDTHelper.isSameYearAndWeek (LD1, LocalDate.of (2020, Month.JANUARY, 3), java.util.Locale.GERMANY));
    assertFalse (PDTHelper.isSameYearAndWeek (LD1, LD2, java.util.Locale.GERMANY));
  }

  @Test
  public void testIsBetweenIncl ()
  {
    assertTrue (PDTHelper.isBetweenIncl (LD1, LD1, LD2));
    assertTrue (PDTHelper.isBetweenIncl (LD2, LD1, LD2));
    assertTrue (PDTHelper.isBetweenIncl (LocalDate.of (2020, Month.JUNE, 1), LD1, LD2));
    assertFalse (PDTHelper.isBetweenIncl (LocalDate.of (2019, Month.JUNE, 1), LD1, LD2));
    assertFalse (PDTHelper.isBetweenIncl (null, LD1, LD2));
    assertFalse (PDTHelper.isBetweenIncl (LD1, null, LD2));
    assertFalse (PDTHelper.isBetweenIncl (LD1, LD1, null));
  }

  @Test
  public void testIsNewYearsEve ()
  {
    assertTrue (PDTHelper.isNewYearsEve (LocalDate.of (2020, Month.DECEMBER, 31)));
    assertFalse (PDTHelper.isNewYearsEve (LocalDate.of (2020, Month.DECEMBER, 30)));
    assertFalse (PDTHelper.isNewYearsEve (LD1));
  }

  @Test
  public void testGetMaxAndMin ()
  {
    assertSame (LD2, PDTHelper.getMax (LD1, LD2));
    assertSame (LD2, PDTHelper.getMax (LD2, LD1));
    assertSame (LD1, PDTHelper.getMin (LD1, LD2));
    assertSame (LD1, PDTHelper.getMin (LD2, LD1));

    final LocalTime aT1 = LocalTime.of (8, 0);
    final LocalTime aT2 = LocalTime.of (18, 0);
    assertSame (aT2, PDTHelper.getMax (aT1, aT2));
    assertSame (aT1, PDTHelper.getMin (aT1, aT2));

    final LocalDateTime aDT1 = LocalDateTime.of (LD1, aT1);
    final LocalDateTime aDT2 = LocalDateTime.of (LD2, aT2);
    assertSame (aDT2, PDTHelper.getMax (aDT1, aDT2));
    assertSame (aDT1, PDTHelper.getMin (aDT1, aDT2));

    final ZonedDateTime aZDT1 = aDT1.atZone (ZoneOffset.UTC);
    final ZonedDateTime aZDT2 = aDT2.atZone (ZoneOffset.UTC);
    assertSame (aZDT2, PDTHelper.getMax (aZDT1, aZDT2));
    assertSame (aZDT1, PDTHelper.getMin (aZDT1, aZDT2));

    final OffsetDateTime aODT1 = aDT1.atOffset (ZoneOffset.UTC);
    final OffsetDateTime aODT2 = aDT2.atOffset (ZoneOffset.UTC);
    assertSame (aODT2, PDTHelper.getMax (aODT1, aODT2));
    assertSame (aODT1, PDTHelper.getMin (aODT1, aODT2));
  }

  @Test
  public void testCurrentOrNextWeekdayAndWeekendDay ()
  {
    // 2020-01-04 is a Saturday, 2020-01-06 is a Monday
    assertEquals (LocalDate.of (2020, Month.JANUARY, 6),
                  PDTHelper.getCurrentOrNextWeekday (LocalDate.of (2020, Month.JANUARY, 4)));
    assertEquals (LD1, PDTHelper.getCurrentOrNextWeekday (LD1));
    assertTrue (PDTHelper.isWorkDay (PDTHelper.getCurrentOrNextWeekday ()));

    assertEquals (LocalDate.of (2020, Month.JANUARY, 4), PDTHelper.getCurrentOrNextWeekendkDay (LD1));
    assertTrue (PDTHelper.isWeekend (PDTHelper.getCurrentOrNextWeekendkDay ()));
  }

  @Test
  public void testDayOfWeekAndMonthConversion ()
  {
    // Calendar.SUNDAY is 1, Calendar.MONDAY is 2, ... Calendar.SATURDAY is 7
    assertSame (DayOfWeek.SUNDAY, PDTHelper.getAsDayOfWeek (1));
    assertSame (DayOfWeek.MONDAY, PDTHelper.getAsDayOfWeek (2));
    assertSame (DayOfWeek.SATURDAY, PDTHelper.getAsDayOfWeek (7));

    assertEquals (1, PDTHelper.getCalendarDayOfWeek (DayOfWeek.SUNDAY));
    assertEquals (2, PDTHelper.getCalendarDayOfWeek (DayOfWeek.MONDAY));
    assertEquals (7, PDTHelper.getCalendarDayOfWeek (DayOfWeek.SATURDAY));

    assertSame (Month.JANUARY, PDTHelper.getAsMonth (1));
    assertSame (Month.DECEMBER, PDTHelper.getAsMonth (12));
    assertNull (PDTHelper.getAsMonth (0));
    assertNull (PDTHelper.getAsMonth (13));

    try
    {
      PDTHelper.getAsDayOfWeek (0);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      PDTHelper.getCalendarDayOfWeek (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
