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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

import com.helger.base.CGlobal;
import com.helger.datetime.helper.PDTFactory;
import com.helger.datetime.zone.PDTConfig;

/**
 * Test class for class {@link PDTXMLConverter}
 *
 * @author Philip Helger
 */
public final class PDTXMLConverterTest
{
  @Test
  public void testCreateNewCalendar ()
  {
    final XMLGregorianCalendar c1 = PDTXMLConverter.createNewCalendar ();
    assertNotNull (c1);
    final XMLGregorianCalendar c2 = PDTXMLConverter.createNewCalendar ();
    assertNotNull (c2);
    assertNotSame (c1, c2);
    assertEquals (c1, c2);
  }

  @Test
  public void testLocalDate ()
  {
    assertNull (PDTXMLConverter.getXMLCalendarDate ((LocalDate) null));
    final LocalDate aLD = PDTFactory.getCurrentLocalDate ();
    final XMLGregorianCalendar c1 = PDTXMLConverter.getXMLCalendarDate (aLD);
    assertNotNull (c1);
    assertEquals (aLD.getYear (), c1.getYear ());
    assertEquals (aLD.getMonth ().getValue (), c1.getMonth ());
    assertEquals (aLD.getDayOfMonth (), c1.getDay ());
    assertEquals (DatatypeConstants.FIELD_UNDEFINED, c1.getHour ());
    assertEquals (DatatypeConstants.FIELD_UNDEFINED, c1.getMinute ());
    assertEquals (DatatypeConstants.FIELD_UNDEFINED, c1.getSecond ());
    assertEquals (DatatypeConstants.FIELD_UNDEFINED, c1.getMillisecond ());
    assertEquals (DatatypeConstants.FIELD_UNDEFINED, c1.getTimezone ());
    final LocalDate aLD2 = PDTXMLConverter.getLocalDate (c1);
    assertNotNull (aLD2);
    assertEquals (aLD, aLD2);
    assertNull (PDTXMLConverter.getLocalDate (null));
  }

  @Test
  public void testLocalDateWithTimezone ()
  {
    assertNull (PDTXMLConverter.getXMLCalendarDate ((LocalDate) null));
    final LocalDate aLD = PDTFactory.getCurrentLocalDate ();
    final int nOffsetMinutes = 60;
    final XMLGregorianCalendar c1 = PDTXMLConverter.getXMLCalendarDate (aLD, nOffsetMinutes);
    assertNotNull (c1);
    assertEquals (aLD.getYear (), c1.getYear ());
    assertEquals (aLD.getMonth ().getValue (), c1.getMonth ());
    assertEquals (aLD.getDayOfMonth (), c1.getDay ());
    assertEquals (DatatypeConstants.FIELD_UNDEFINED, c1.getHour ());
    assertEquals (DatatypeConstants.FIELD_UNDEFINED, c1.getMinute ());
    assertEquals (DatatypeConstants.FIELD_UNDEFINED, c1.getSecond ());
    assertEquals (DatatypeConstants.FIELD_UNDEFINED, c1.getMillisecond ());
    assertEquals (nOffsetMinutes, c1.getTimezone ());
    final LocalDate aLD2 = PDTXMLConverter.getLocalDate (c1);
    assertNotNull (aLD2);
    assertEquals (aLD, aLD2);
    assertNull (PDTXMLConverter.getLocalDate (null));

  }

  @Test
  public void testDateToCalendar ()
  {
    Date aDate = PDTFactory.createDateForDate (2018, Month.JANUARY, 1);
    // Depends on the system timezone
    if (false)
      assertEquals ("Mon Jan 01 00:00:00 CET 2018", aDate.toString ());

    GregorianCalendar aCal = PDTXMLConverter.getCalendar (aDate);
    assertEquals (2018, aCal.get (Calendar.YEAR));
    assertEquals (Calendar.JANUARY, aCal.get (Calendar.MONTH));
    assertEquals (1, aCal.get (Calendar.DAY_OF_MONTH));

    aDate = PDTFactory.createDateForDate (2018, Month.DECEMBER, 31);
    // Depends on the system timezone
    if (false)
      assertEquals ("Mon Dec 31 00:00:00 CET 2018", aDate.toString ());

    aCal = PDTXMLConverter.getCalendar (aDate);
    assertEquals (2018, aCal.get (Calendar.YEAR));
    assertEquals (Calendar.DECEMBER, aCal.get (Calendar.MONTH));
    assertEquals (31, aCal.get (Calendar.DAY_OF_MONTH));
  }

  @Test
  public void testCalendarBackAndForth ()
  {
    final ZoneId aZID = PDTConfig.getDefaultZoneId ();
    final Date aDate = PDTFactory.createDateForDate (2018, Month.OCTOBER, 31);
    final int nOffsetMinutes = PDTFactory.getTimezoneOffsetInMinutes (aZID, aDate.toInstant ());
    // Depends on the system timezone
    if (false)
      assertEquals ("Wed Oct 31 00:00:00 CET 2018", aDate.toString ());
    assertEquals (PDTConfig.getDefaultZoneId ().getRules ().getOffset (PDTFactory.createLocalDateTime (aDate)).getTotalSeconds () /
                  CGlobal.SECONDS_PER_MINUTE,
                  PDTFactory.getTimezoneOffsetInMinutes (aDate));

    GregorianCalendar c0 = PDTXMLConverter.getCalendar (aDate);
    assertEquals (2018, c0.get (Calendar.YEAR));
    // 0-based!
    assertEquals (Calendar.OCTOBER, c0.get (Calendar.MONTH));
    assertEquals (31, c0.get (Calendar.DAY_OF_MONTH));
    assertEquals (0, c0.get (Calendar.HOUR));
    assertEquals (0, c0.get (Calendar.MINUTE));
    assertEquals (0, c0.get (Calendar.SECOND));
    assertEquals (0, c0.get (Calendar.MILLISECOND));
    assertEquals (nOffsetMinutes, PDTFactory.getTimezoneOffsetInMinutes (c0));

    final XMLGregorianCalendar c1 = PDTXMLConverter.getXMLCalendarDate (aDate);
    assertNotNull (c1);
    assertEquals (2018, c1.getYear ());
    // 1-based!
    assertEquals (10, c1.getMonth ());
    assertEquals (31, c1.getDay ());
    assertEquals (DatatypeConstants.FIELD_UNDEFINED, c1.getHour ());
    assertEquals (DatatypeConstants.FIELD_UNDEFINED, c1.getMinute ());
    assertEquals (DatatypeConstants.FIELD_UNDEFINED, c1.getSecond ());
    assertEquals (DatatypeConstants.FIELD_UNDEFINED, c1.getMillisecond ());
    // Depends on the system timezone
    if (false)
      assertEquals (60, c1.getTimezone ());

    c0 = c1.toGregorianCalendar (c1.getTimeZone (c1.getTimezone ()), Locale.getDefault (Locale.Category.FORMAT), null);
    assertEquals (2018, c0.get (Calendar.YEAR));
    assertEquals (Calendar.OCTOBER, c0.get (Calendar.MONTH));
    assertEquals (31, c0.get (Calendar.DAY_OF_MONTH));
    assertEquals (0, c0.get (Calendar.HOUR));
    assertEquals (0, c0.get (Calendar.MINUTE));
    assertEquals (0, c0.get (Calendar.SECOND));
    assertEquals (0, c0.get (Calendar.MILLISECOND));
    assertEquals (nOffsetMinutes, PDTFactory.getTimezoneOffsetInMinutes (c0));

    assertEquals (aDate.getTime (), c1.toGregorianCalendar ().getTime ().getTime ());

    final Date aDate2 = PDTXMLConverter.getDate (c1);
    assertNotNull (aDate2);
    assertEquals (aDate.getTime (), aDate2.getTime ());
    assertEquals (aDate, aDate2);
  }

  @Test
  public void testLocalTime ()
  {
    assertNull (PDTXMLConverter.getXMLCalendarTime ((LocalTime) null));
    final LocalTime aLT = PDTFactory.getCurrentLocalTime ();
    final XMLGregorianCalendar c1 = PDTXMLConverter.getXMLCalendarTime (aLT);
    assertNotNull (c1);
    assertEquals (DatatypeConstants.FIELD_UNDEFINED, c1.getYear ());
    assertEquals (DatatypeConstants.FIELD_UNDEFINED, c1.getMonth ());
    assertEquals (DatatypeConstants.FIELD_UNDEFINED, c1.getDay ());
    assertEquals (aLT.getHour (), c1.getHour ());
    assertEquals (aLT.getMinute (), c1.getMinute ());
    assertEquals (aLT.getSecond (), c1.getSecond ());
    assertEquals (aLT.get (ChronoField.MILLI_OF_SECOND), c1.getMillisecond ());
    assertEquals (DatatypeConstants.FIELD_UNDEFINED, c1.getTimezone ());
    final LocalTime aLT2 = PDTXMLConverter.getLocalTime (c1);
    assertNotNull (aLT2);
    // In Java9 aLDT2 only has millis
    assertEquals (aLT.withNano (c1.getMillisecond () * (int) CGlobal.NANOSECONDS_PER_MILLISECOND), aLT2);
    assertNull (PDTXMLConverter.getLocalTime (null));
  }

  @Test
  public void testLocalDateTime ()
  {
    assertNull (PDTXMLConverter.getXMLCalendar ((LocalDateTime) null));
    final LocalDateTime aLDT = PDTFactory.getCurrentLocalDateTime ();
    final XMLGregorianCalendar c1 = PDTXMLConverter.getXMLCalendar (aLDT);
    assertNotNull (c1);
    assertEquals (aLDT.getYear (), c1.getYear ());
    assertEquals (aLDT.getMonth ().getValue (), c1.getMonth ());
    assertEquals (aLDT.getDayOfMonth (), c1.getDay ());
    assertEquals (aLDT.getHour (), c1.getHour ());
    assertEquals (aLDT.getMinute (), c1.getMinute ());
    assertEquals (aLDT.getSecond (), c1.getSecond ());
    assertEquals (aLDT.get (ChronoField.MILLI_OF_SECOND), c1.getMillisecond ());
    assertEquals (DatatypeConstants.FIELD_UNDEFINED, c1.getTimezone ());
    final LocalDateTime aLDT2 = PDTXMLConverter.getLocalDateTime (c1);
    assertNotNull (aLDT2);
    // In Java9 aLDT2 only has millis
    assertEquals (PDTFactory.getWithMillisOnly (aLDT), aLDT2);
    assertNull (PDTXMLConverter.getLocalDateTime (null));
  }

  @Test
  public void testDateTime ()
  {
    assertNull (PDTXMLConverter.getXMLCalendar ((ZonedDateTime) null));
    final ZonedDateTime aLDT = PDTFactory.getCurrentZonedDateTime ();
    final XMLGregorianCalendar c1 = PDTXMLConverter.getXMLCalendar (aLDT);
    assertNotNull (c1);
    assertEquals (aLDT.getYear (), c1.getYear ());
    assertEquals (aLDT.getMonth ().getValue (), c1.getMonth ());
    assertEquals (aLDT.getDayOfMonth (), c1.getDay ());
    assertEquals (aLDT.getHour (), c1.getHour ());
    assertEquals (aLDT.getMinute (), c1.getMinute ());
    assertEquals (aLDT.getSecond (), c1.getSecond ());
    assertEquals (aLDT.get (ChronoField.MILLI_OF_SECOND), c1.getMillisecond ());
    // assertTrue (c1.getFractionalSecond () + " is supposed to be 0",
    // MathHelper.isEQ0 (c1.getFractionalSecond ()));
    assertEquals (aLDT.getOffset ().get (ChronoField.OFFSET_SECONDS) / CGlobal.SECONDS_PER_MINUTE, c1.getTimezone ());
    final ZonedDateTime aLDT2 = PDTXMLConverter.getZonedDateTime (c1);
    assertNotNull (aLDT2);
    // In Java9 aLDT2 only has millis
    assertEquals (PDTFactory.getWithMillisOnly (aLDT.toLocalDateTime ()), aLDT2.toLocalDateTime ());
    assertEquals (aLDT.getOffset (), aLDT2.getOffset ());
  }

  @Test
  public void testNow ()
  {
    assertNotNull (PDTXMLConverter.getXMLCalendarNow ());
    assertNotNull (PDTXMLConverter.getXMLCalendarDateNow ());
    assertNotNull (PDTXMLConverter.getXMLCalendarTimeNow ());
  }
}
