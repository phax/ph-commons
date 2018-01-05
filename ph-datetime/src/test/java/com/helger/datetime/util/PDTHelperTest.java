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
package com.helger.datetime.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.WeekFields;
import java.util.Locale;

import org.junit.Test;

import com.helger.commons.datetime.PDTFactory;

/**
 * Test class for class {@link PDTHelper}.
 *
 * @author Philip Helger
 */
public final class PDTHelperTest
{
  private static LocalDateTime _getDT (final int y, final Month m)
  {
    return LocalDateTime.of (y, m, 1, 0, 0);
  }

  @Test
  public void testGetWeeksOfMonth ()
  {
    final Locale aLocale = Locale.GERMANY;

    // Default cases:
    assertEquals (1, PDTHelper.getStartWeekOfMonth (_getDT (2008, Month.JANUARY), aLocale));
    assertEquals (5, PDTHelper.getEndWeekOfMonth (_getDT (2008, Month.JANUARY), aLocale));

    assertEquals (5, PDTHelper.getStartWeekOfMonth (_getDT (2008, Month.FEBRUARY), aLocale));
    assertEquals (9, PDTHelper.getEndWeekOfMonth (_getDT (2008, Month.FEBRUARY), aLocale));

    assertEquals (9, PDTHelper.getStartWeekOfMonth (_getDT (2008, Month.MARCH), aLocale));
    assertEquals (14, PDTHelper.getEndWeekOfMonth (_getDT (2008, Month.MARCH), aLocale));

    // Special case: August ends with a sunday and therefore the last week is
    // different from the beginning of the following week
    assertEquals (31, PDTHelper.getStartWeekOfMonth (_getDT (2008, Month.AUGUST), aLocale));
    assertEquals (35, PDTHelper.getEndWeekOfMonth (_getDT (2008, Month.AUGUST), aLocale));

    assertEquals (36, PDTHelper.getStartWeekOfMonth (_getDT (2008, Month.SEPTEMBER), aLocale));
    assertEquals (40, PDTHelper.getEndWeekOfMonth (_getDT (2008, Month.SEPTEMBER), aLocale));
  }

  @Test
  public void testGetWeekDaysBetweenl ()
  {
    final Locale aLocale = Locale.GERMANY;
    final LocalDate aWeekendDate = PDTFactory.getCurrentLocalDate ().with (WeekFields.of (aLocale).dayOfWeek (),
                                                                           DayOfWeek.SUNDAY.getValue ());
    final LocalDate aStartDate = PDTFactory.getCurrentLocalDate ().with (WeekFields.of (aLocale).dayOfWeek (),
                                                                         DayOfWeek.MONDAY.getValue ());
    final LocalDate aEndDate = PDTFactory.getCurrentLocalDate ().with (WeekFields.of (aLocale).dayOfWeek (),
                                                                       DayOfWeek.TUESDAY.getValue ());
    assertEquals (0, PDTHelper.getWeekDays (aWeekendDate, aWeekendDate));
    assertEquals (1, PDTHelper.getWeekDays (aStartDate, aStartDate));
    assertEquals (2, PDTHelper.getWeekDays (aStartDate, aEndDate));
    assertEquals (-2, PDTHelper.getWeekDays (aEndDate, aStartDate));
    assertEquals (6, PDTHelper.getWeekDays (aStartDate, aStartDate.plusWeeks (1)));
    assertEquals (-6, PDTHelper.getWeekDays (aStartDate.plusWeeks (1), aStartDate));
  }

  @Test
  public void testBirthdayCompare ()
  {
    final LocalDate aDate1 = LocalDate.of (1980, Month.JULY, 6);
    final LocalDate aDate2 = LocalDate.of (1978, Month.JULY, 6);
    final LocalDate aDate3 = LocalDate.of (1978, Month.DECEMBER, 2);
    assertEquals (0, PDTHelper.birthdayCompare (aDate1, aDate2));
    assertTrue (PDTHelper.birthdayCompare (aDate1, aDate3) < 0);
    assertTrue (PDTHelper.birthdayCompare (aDate3, aDate2) > 0);
    assertEquals (0, PDTHelper.birthdayCompare (null, null));
    assertTrue (PDTHelper.birthdayCompare (null, aDate3) < 0);
    assertTrue (PDTHelper.birthdayCompare (aDate3, null) > 0);

    assertTrue (PDTHelper.birthdayEquals (aDate1, aDate2));
    assertFalse (PDTHelper.birthdayEquals (aDate1, aDate3));
    assertFalse (PDTHelper.birthdayEquals (aDate3, aDate2));
    assertTrue (PDTHelper.birthdayEquals (null, null));
    assertFalse (PDTHelper.birthdayEquals (null, aDate3));
    assertFalse (PDTHelper.birthdayEquals (aDate3, null));
  }

  @Test
  public void testComparePeriods ()
  {
    final Duration p1 = Duration.between (LocalTime.of (6, 0, 0), LocalTime.of (15, 0, 0));
    Duration p2 = Duration.ofHours (9);
    // Different field size
    assertEquals (p1, p2);

    // But this leads to equality :)
    assertEquals (0, p1.compareTo (p2));
    assertEquals (p1, p2);

    p2 = p2.plusMinutes (1);
    assertEquals (-1, p1.compareTo (p2));
    assertFalse (p1.equals (p2));
    assertEquals (+1, p2.compareTo (p1));
    assertFalse (p2.equals (p1));
  }

  @Test
  public void testDaysBetween ()
  {
    final LocalDate aStart = PDTFactory.getCurrentLocalDate ();
    assertEquals (0, PDTHelper.getDaysBetween (aStart, aStart));
    assertEquals (60, PDTHelper.getDaysBetween (LocalDate.of (2016, 1, 22), LocalDate.of (2016, 3, 22)));
  }

  @Test
  public void testDayOfWeek ()
  {
    for (final DayOfWeek e : DayOfWeek.values ())
    {
      final int n = PDTHelper.getCalendarDayOfWeek (e);
      assertTrue (n >= 1 && n <= 7);
      final DayOfWeek e2 = PDTHelper.getAsDayOfWeek (n);
      assertNotNull (e2);
      assertEquals (e, e2);
    }
  }
}
