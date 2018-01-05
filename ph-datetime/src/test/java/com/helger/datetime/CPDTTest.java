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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Month;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.HijrahChronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DecimalStyle;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.junit.Test;

/**
 * Test class for class {@link CPDT}.
 *
 * @author Philip Helger
 */
public final class CPDTTest
{
  @Test
  public void testNullLocalDate ()
  {
    assertEquals (1, CPDT.NULL_LOCAL_DATE.getDayOfMonth ());
    assertEquals (Month.JANUARY, CPDT.NULL_LOCAL_DATE.getMonth ());
    assertEquals (CPDT.MIN_YEAR_INT32, CPDT.NULL_LOCAL_DATE.getYear ());
  }

  @Test
  public void testNullLocalTime ()
  {
    assertEquals (0, CPDT.NULL_LOCAL_TIME.getHour ());
    assertEquals (0, CPDT.NULL_LOCAL_TIME.getMinute ());
    assertEquals (0, CPDT.NULL_LOCAL_TIME.getSecond ());
    assertEquals (0, CPDT.NULL_LOCAL_TIME.getNano ());
  }

  @Test
  public void testNullLocalDateTime ()
  {
    assertEquals (1, CPDT.NULL_LOCAL_DATETIME.getDayOfMonth ());
    assertEquals (Month.JANUARY, CPDT.NULL_LOCAL_DATETIME.getMonth ());
    assertEquals (CPDT.MIN_YEAR_INT32, CPDT.NULL_LOCAL_DATETIME.getYear ());
    assertEquals (0, CPDT.NULL_LOCAL_DATETIME.getHour ());
    assertEquals (0, CPDT.NULL_LOCAL_DATETIME.getMinute ());
    assertEquals (0, CPDT.NULL_LOCAL_DATETIME.getSecond ());
    assertEquals (0, CPDT.NULL_LOCAL_DATETIME.getNano ());
  }

  @Test
  public void testNullDateTime ()
  {
    // By default the local chronology is used - must be converted to UTC to
    // deliver a reall NULL date
    final ZonedDateTime aNullDTUTC = CPDT.NULL_DATETIME_UTC;
    assertEquals (1, aNullDTUTC.getDayOfMonth ());
    assertEquals (Month.JANUARY, aNullDTUTC.getMonth ());
    assertEquals (CPDT.MIN_YEAR_INT32, aNullDTUTC.getYear ());
    assertEquals (0, aNullDTUTC.getHour ());
    assertEquals (0, aNullDTUTC.getMinute ());
    assertEquals (0, aNullDTUTC.getSecond ());
    assertEquals (0, aNullDTUTC.getNano ());
  }

  @Test
  public void testNullDateTimeUTC ()
  {
    assertEquals (1, CPDT.NULL_DATETIME_UTC.getDayOfMonth ());
    assertEquals (Month.JANUARY, CPDT.NULL_DATETIME_UTC.getMonth ());
    assertEquals (CPDT.MIN_YEAR_INT32, CPDT.NULL_DATETIME_UTC.getYear ());
    assertEquals (0, CPDT.NULL_DATETIME_UTC.getHour ());
    assertEquals (0, CPDT.NULL_DATETIME_UTC.getMinute ());
    assertEquals (0, CPDT.NULL_DATETIME_UTC.getSecond ());
    assertEquals (0, CPDT.NULL_DATETIME_UTC.getNano ());
  }

  @Test
  public void testNullPeriod ()
  {
    assertEquals (0, CPDT.NULL_PERIOD.getDays ());
    assertEquals (0, CPDT.NULL_PERIOD.getMonths ());
    assertEquals (0, CPDT.NULL_PERIOD.toTotalMonths ());
    assertEquals (0, CPDT.NULL_PERIOD.getYears ());
  }

  @Test
  public void testNullDuration ()
  {
    assertEquals (0, CPDT.NULL_DURATION.getNano ());
    assertEquals (0, CPDT.NULL_DURATION.getSeconds ());
  }

  @Test
  public void testChrono ()
  {
    final ChronoLocalDate aDateISO = IsoChronology.INSTANCE.date (2010, 7, 6);
    assertNotNull (aDateISO);
    final ChronoLocalDate aDateHijrah = HijrahChronology.INSTANCE.date (1400, 7, 6);
    assertNotNull (aDateHijrah);

    final DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder ().appendLocalized (FormatStyle.SHORT, null)
                                                                           .toFormatter (Locale.GERMANY)
                                                                           .withChronology (HijrahChronology.INSTANCE)
                                                                           .withDecimalStyle (DecimalStyle.of (Locale.GERMANY));
    assertNotNull (dateFormatter.format (aDateISO));
    assertNotNull (dateFormatter.format (aDateHijrah));
  }
}
