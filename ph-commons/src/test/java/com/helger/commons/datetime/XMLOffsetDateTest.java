/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
/*
 * Copyright (c) 2007-present, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.helger.commons.datetime;

import static java.time.Month.DECEMBER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalUnit;
import java.util.Locale;

import javax.annotation.Nonnull;

import org.junit.Before;
import org.junit.Test;

import com.helger.commons.CGlobal;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.typeconvert.TypeConverter;
import com.helger.commons.typeconvert.TypeConverterException;

/**
 * Test {@link XMLOffsetDate}.
 */
public final class XMLOffsetDateTest
{
  private static final ZoneOffset OFFSET_PONE = ZoneOffset.ofHours (1);
  private static final ZoneOffset OFFSET_PTWO = ZoneOffset.ofHours (2);

  private XMLOffsetDate TEST_2007_07_15_PONE;

  @Before
  public void setUp ()
  {
    TEST_2007_07_15_PONE = XMLOffsetDate.of (LocalDate.of (2007, 7, 15), OFFSET_PONE);
  }

  private interface ISampleWithString
  {
    void accept (final int y, final int m, final int d, final String offsetId, final String parsable);
  }

  private static void _data_sampleToString (@Nonnull final ISampleWithString aSample)
  {
    aSample.accept (2008, 7, 5, "Z", "2008-07-05Z");
    aSample.accept (2008, 7, 5, "+00", "2008-07-05Z");
    aSample.accept (2008, 7, 5, "+0000", "2008-07-05Z");
    aSample.accept (2008, 7, 5, "+00:00", "2008-07-05Z");
    aSample.accept (2008, 7, 5, "+000000", "2008-07-05Z");
    aSample.accept (2008, 7, 5, "+00:00:00", "2008-07-05Z");
    aSample.accept (2008, 7, 5, "-00", "2008-07-05Z");
    aSample.accept (2008, 7, 5, "-0000", "2008-07-05Z");
    aSample.accept (2008, 7, 5, "-00:00", "2008-07-05Z");
    aSample.accept (2008, 7, 5, "-000000", "2008-07-05Z");
    aSample.accept (2008, 7, 5, "-00:00:00", "2008-07-05Z");
    aSample.accept (2008, 7, 5, "+01", "2008-07-05+01:00");
    aSample.accept (2008, 7, 5, "+0100", "2008-07-05+01:00");
    aSample.accept (2008, 7, 5, "+01:00", "2008-07-05+01:00");
    aSample.accept (2008, 7, 5, "+010000", "2008-07-05+01:00");
    aSample.accept (2008, 7, 5, "+01:00:00", "2008-07-05+01:00");
    aSample.accept (2008, 7, 5, "+0130", "2008-07-05+01:30");
    aSample.accept (2008, 7, 5, "+01:30", "2008-07-05+01:30");
    aSample.accept (2008, 7, 5, "+013000", "2008-07-05+01:30");
    aSample.accept (2008, 7, 5, "+01:30:00", "2008-07-05+01:30");
    aSample.accept (2008, 7, 5, "+013040", "2008-07-05+01:30:40");
    aSample.accept (2008, 7, 5, "+01:30:40", "2008-07-05+01:30:40");
  }

  @Test
  public void testConstant_MIN ()
  {
    _check (XMLOffsetDate.MIN, Year.MIN_VALUE, 1, 1, ZoneOffset.MAX);
  }

  @Test
  public void testConstant_MAX ()
  {
    _check (XMLOffsetDate.MAX, Year.MAX_VALUE, 12, 31, ZoneOffset.MIN);
  }

  @Test
  public void testNow ()
  {
    XMLOffsetDate expected = XMLOffsetDate.now (Clock.systemDefaultZone ());
    XMLOffsetDate test = XMLOffsetDate.now ();
    for (int i = 0; i < 100; i++)
    {
      if (expected.equals (test))
      {
        return;
      }
      expected = XMLOffsetDate.now (Clock.systemDefaultZone ());
      test = XMLOffsetDate.now ();
    }
    assertEquals (expected, test);
  }

  @Test
  public void testNow_Clock_allSecsInDay_utc ()
  {
    for (int i = 0; i < (2 * 24 * 60 * 60); i++)
    {
      final Instant instant = Instant.ofEpochSecond (i);
      final Clock clock = Clock.fixed (instant, ZoneOffset.UTC);
      final XMLOffsetDate test = XMLOffsetDate.now (clock);
      _check (test, 1970, 1, (i < 24 * 60 * 60 ? 1 : 2), ZoneOffset.UTC);
    }
  }

  @Test
  public void testNow_Clock_allSecsInDay_beforeEpoch ()
  {
    for (int i = -1; i >= -(2 * 24 * 60 * 60); i--)
    {
      final Instant instant = Instant.ofEpochSecond (i);
      final Clock clock = Clock.fixed (instant, ZoneOffset.UTC);
      final XMLOffsetDate test = XMLOffsetDate.now (clock);
      _check (test, 1969, 12, (i >= -24 * 60 * 60 ? 31 : 30), ZoneOffset.UTC);
    }
  }

  @Test
  public void testNow_Clock_offsets ()
  {
    final Instant base = LocalDateTime.of (1970, 1, 1, 12, 0).toInstant (ZoneOffset.UTC);
    for (int i = -9; i < 15; i++)
    {
      final ZoneOffset offset = ZoneOffset.ofHours (i);
      final Clock clock = Clock.fixed (base, offset);
      final XMLOffsetDate test = XMLOffsetDate.now (clock);
      _check (test, 1970, 1, (i >= 12 ? 2 : 1), offset);
    }
  }

  @Test
  public void testNow_Clock_nullZoneId ()
  {
    assertThrows (NullPointerException.class, () -> XMLOffsetDate.now ((ZoneId) null));
  }

  @Test
  public void testNow_Clock_nullClock ()
  {
    assertThrows (NullPointerException.class, () -> XMLOffsetDate.now ((Clock) null));
  }

  private void _check (final XMLOffsetDate test, final int y, final int mo, final int d, final ZoneOffset offset)
  {
    assertEquals (LocalDate.of (y, mo, d), test.toLocalDate ());
    assertEquals (offset, test.getOffset ());

    assertEquals (y, test.getYear ());
    assertEquals (mo, test.getMonth ().getValue ());
    assertEquals (d, test.getDayOfMonth ());

    assertEquals (test, test);
    assertEquals (test.hashCode (), test.hashCode ());
    assertEquals (test, XMLOffsetDate.of (LocalDate.of (y, mo, d), offset));
  }

  @Test
  public void testFactory_of_intMonthInt ()
  {
    final XMLOffsetDate test = XMLOffsetDate.of (LocalDate.of (2007, Month.JULY, 15), OFFSET_PONE);
    _check (test, 2007, 7, 15, OFFSET_PONE);
  }

  @Test
  public void testFactory_of_ints ()
  {
    final XMLOffsetDate test = XMLOffsetDate.of (LocalDate.of (2007, 7, 15), OFFSET_PONE);
    _check (test, 2007, 7, 15, OFFSET_PONE);
  }

  @Test
  public void testFactory_of_intsMonthOffset ()
  {
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, Month.JULY, 15), OFFSET_PONE), TEST_2007_07_15_PONE);
  }

  @Test
  public void testFactory_of_intsMonthOffset_dayTooLow ()
  {
    assertThrows (DateTimeException.class, () -> XMLOffsetDate.of (LocalDate.of (2007, Month.JANUARY, 0), OFFSET_PONE));
  }

  @Test
  public void testFactory_of_intsMonthOffset_dayTooHigh ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (2007, Month.JANUARY, 32), OFFSET_PONE));
  }

  @Test
  public void testFactory_of_intsMonthOffset_nullMonth ()
  {
    assertThrows (NullPointerException.class, () -> XMLOffsetDate.of (LocalDate.of (2007, null, 30), OFFSET_PONE));
  }

  @Test
  public void testFactory_of_intsMonthOffset_yearTooLow ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Integer.MIN_VALUE, Month.JANUARY, 1), OFFSET_PONE));
  }

  @Test
  public void testFactory_of_intsMonthOffset_nullOffset ()
  {
    assertNotNull (XMLOffsetDate.of (LocalDate.of (2007, Month.JANUARY, 30), null));
  }

  @Test
  public void testFactory_of_intsOffset ()
  {
    final XMLOffsetDate test = XMLOffsetDate.of (LocalDate.of (2007, 7, 15), OFFSET_PONE);
    _check (test, 2007, 7, 15, OFFSET_PONE);
  }

  @Test
  public void testFactory_of_ints_dayTooLow ()
  {
    assertThrows (DateTimeException.class, () -> XMLOffsetDate.of (LocalDate.of (2007, 1, 0), OFFSET_PONE));
  }

  @Test
  public void testFactory_of_ints_dayTooHigh ()
  {
    assertThrows (DateTimeException.class, () -> XMLOffsetDate.of (LocalDate.of (2007, 1, 32), OFFSET_PONE));
  }

  @Test
  public void testFactory_of_ints_monthTooLow ()
  {
    assertThrows (DateTimeException.class, () -> XMLOffsetDate.of (LocalDate.of (2007, 0, 1), OFFSET_PONE));
  }

  @Test
  public void testFactory_of_ints_monthTooHigh ()
  {
    assertThrows (DateTimeException.class, () -> XMLOffsetDate.of (LocalDate.of (2007, 13, 1), OFFSET_PONE));
  }

  @Test
  public void testFactory_of_ints_yearTooLow ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Integer.MIN_VALUE, 1, 1), OFFSET_PONE));
  }

  @Test
  public void testFactory_of_ints_nullOffset ()
  {
    assertNotNull (XMLOffsetDate.of (LocalDate.of (2007, 1, 1), (ZoneOffset) null));
  }

  @Test
  public void testFactory_of_LocalDateZoneOffset ()
  {
    final LocalDate localDate = LocalDate.of (2008, 6, 30);
    final XMLOffsetDate test = XMLOffsetDate.of (localDate, OFFSET_PONE);
    _check (test, 2008, 6, 30, OFFSET_PONE);
  }

  @Test
  public void testFactory_of_LocalDateZoneOffset_nullDate ()
  {
    assertThrows (NullPointerException.class, () -> XMLOffsetDate.of ((LocalDate) null, OFFSET_PONE));
  }

  @Test
  public void testFactory_of_LocalDateZoneOffset_nullOffset ()
  {
    final LocalDate localDate = LocalDate.of (2008, 6, 30);
    assertNotNull (XMLOffsetDate.of (localDate, (ZoneOffset) null));
  }

  @Test
  public void testFrom_TemporalAccessor_OD ()
  {
    assertEquals (TEST_2007_07_15_PONE, XMLOffsetDate.from (TEST_2007_07_15_PONE));
  }

  @Test
  public void testFrom_TemporalAccessor_ZDT ()
  {
    final ZonedDateTime base = LocalDateTime.of (2007, 7, 15, 17, 30).atZone (OFFSET_PONE);
    assertEquals (TEST_2007_07_15_PONE, XMLOffsetDate.from (base));
  }

  @Test
  public void testFrom_TemporalAccessor_invalid_noDerive ()
  {
    assertThrows (DateTimeException.class, () -> XMLOffsetDate.from (LocalTime.of (12, 30)));
  }

  @Test
  public void testFrom_TemporalAccessor_null ()
  {
    assertThrows (NullPointerException.class, () -> XMLOffsetDate.from ((TemporalAccessor) null));
  }

  @Test
  public void testFactory_parse_validText ()
  {
    _data_sampleToString ( (y, m, d, offsetId, parsable) -> {
      final XMLOffsetDate t = XMLOffsetDate.parse (parsable);
      assertNotNull (parsable, t);
      assertEquals (parsable, y, t.getYear ());
      assertEquals (parsable, m, t.getMonth ().getValue ());
      assertEquals (parsable, d, t.getDayOfMonth ());
      assertEquals (parsable, ZoneOffset.of (offsetId), t.getOffset ());
    });
  }

  private static final String [] data_sampleBadParse = new String [] { "2008/07/05",
                                                                       "10000-01-01",
                                                                       "2008-1-1",
                                                                       "2008--01",
                                                                       "ABCD-02-01",
                                                                       "2008-AB-01",
                                                                       "2008-02-AB",
                                                                       "-0000-02-01",
                                                                       "2008-02-01Y",
                                                                       // "2008-02-01+19:00",
                                                                       "2008-02-01+01/00",
                                                                       "2008-02-01+1900",
                                                                       "2008-02-01+01:60",
                                                                       "2008-02-01+01:30:123",
                                                                       // "2008-02-01",
                                                                       "2008-02-01+01:00[Europe/Paris]" };

  @Test
  public void testFactory_parse_invalidText ()
  {
    for (final String unparsable : data_sampleBadParse)
    {
      assertThrows (DateTimeParseException.class, () -> XMLOffsetDate.parse (unparsable));
    }
  }

  @Test
  public void testFactory_parse_illegalValue ()
  {
    assertThrows (DateTimeParseException.class, () -> XMLOffsetDate.parse ("2008-06-32+01:00"));
  }

  @Test
  public void testFactory_parse_invalidValue ()
  {
    assertThrows (DateTimeParseException.class, () -> XMLOffsetDate.parse ("2008-06-31+01:00"));
  }

  @Test
  public void testFactory_parse_nullText ()
  {
    assertThrows (NullPointerException.class, () -> XMLOffsetDate.parse ((String) null));
  }

  @Test
  public void testFactory_parse_formatter ()
  {
    final DateTimeFormatter f = DateTimeFormatter.ofPattern ("y M d XXX", Locale.US);
    final XMLOffsetDate test = XMLOffsetDate.parse ("2010 12 3 +01:00", f);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2010, 12, 3), ZoneOffset.ofHours (1)), test);
  }

  @Test
  public void testFactory_parse_formatter_nullText ()
  {
    final DateTimeFormatter f = DateTimeFormatter.ofPattern ("y M d", Locale.US);
    assertThrows (NullPointerException.class, () -> XMLOffsetDate.parse ((String) null, f));
  }

  @Test
  public void testFactory_parse_formatter_nullFormatter ()
  {
    assertThrows (NullPointerException.class, () -> XMLOffsetDate.parse ("ANY", null));
  }

  @Test
  public void testConstructor_nullDate () throws Throwable
  {
    assertThrows (NullPointerException.class, () -> new XMLOffsetDate (null, OFFSET_PONE));
  }

  @Test
  public void testConstructor_nullOffset () throws Throwable
  {
    assertNotNull (new XMLOffsetDate (LocalDate.of (2008, 6, 30), null));
  }

  private interface ISample
  {
    void accept (final int y, final int m, final int d, final ZoneOffset offset);
  }

  private static void _data_sampleDates (@Nonnull final ISample aSample)
  {
    aSample.accept (2008, 7, 5, OFFSET_PTWO);
    aSample.accept (2007, 7, 5, OFFSET_PONE);
    aSample.accept (2006, 7, 5, OFFSET_PTWO);
    aSample.accept (2005, 7, 5, OFFSET_PONE);
    aSample.accept (2004, 1, 1, OFFSET_PTWO);
    aSample.accept (-1, 1, 2, OFFSET_PONE);
    aSample.accept (999999, 11, 20, ZoneOffset.ofHoursMinutesSeconds (6, 9, 12));
  }

  @Test
  public void testGet_OffsetDate ()
  {
    _data_sampleDates ( (y, m, d, offset) -> {
      final LocalDate localDate = LocalDate.of (y, m, d);
      final XMLOffsetDate a = XMLOffsetDate.of (localDate, offset);

      assertEquals (localDate, a.toLocalDate ());
      assertEquals (offset, a.getOffset ());
      assertEquals (localDate.toString () + offset.toString (), a.toString ());
      assertEquals (localDate.getYear (), a.getYear ());
      assertEquals (localDate.getMonth (), a.getMonth ());
      assertEquals (localDate.getDayOfMonth (), a.getDayOfMonth ());
      assertEquals (localDate.getDayOfYear (), a.getDayOfYear ());
      assertEquals (localDate.getDayOfWeek (), a.getDayOfWeek ());
    });
  }

  @Test
  public void testIsSupported_TemporalUnit ()
  {
    assertFalse (TEST_2007_07_15_PONE.isSupported ((TemporalUnit) null));
    assertFalse (TEST_2007_07_15_PONE.isSupported (ChronoUnit.NANOS));
    assertFalse (TEST_2007_07_15_PONE.isSupported (ChronoUnit.MICROS));
    assertFalse (TEST_2007_07_15_PONE.isSupported (ChronoUnit.MILLIS));
    assertFalse (TEST_2007_07_15_PONE.isSupported (ChronoUnit.SECONDS));
    assertFalse (TEST_2007_07_15_PONE.isSupported (ChronoUnit.MINUTES));
    assertFalse (TEST_2007_07_15_PONE.isSupported (ChronoUnit.HOURS));
    assertFalse (TEST_2007_07_15_PONE.isSupported (ChronoUnit.HALF_DAYS));
    assertTrue (TEST_2007_07_15_PONE.isSupported (ChronoUnit.DAYS));
    assertTrue (TEST_2007_07_15_PONE.isSupported (ChronoUnit.WEEKS));
    assertTrue (TEST_2007_07_15_PONE.isSupported (ChronoUnit.MONTHS));
    assertTrue (TEST_2007_07_15_PONE.isSupported (ChronoUnit.YEARS));
    assertTrue (TEST_2007_07_15_PONE.isSupported (ChronoUnit.DECADES));
    assertTrue (TEST_2007_07_15_PONE.isSupported (ChronoUnit.CENTURIES));
    assertTrue (TEST_2007_07_15_PONE.isSupported (ChronoUnit.MILLENNIA));
    assertTrue (TEST_2007_07_15_PONE.isSupported (ChronoUnit.ERAS));
    assertFalse (TEST_2007_07_15_PONE.isSupported (ChronoUnit.FOREVER));
  }

  @Test
  public void testGet_TemporalField ()
  {
    final XMLOffsetDate test = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    assertEquals (2008, test.get (ChronoField.YEAR));
    assertEquals (6, test.get (ChronoField.MONTH_OF_YEAR));
    assertEquals (30, test.get (ChronoField.DAY_OF_MONTH));
    assertEquals (1, test.get (ChronoField.DAY_OF_WEEK));
    assertEquals (182, test.get (ChronoField.DAY_OF_YEAR));

    assertEquals (3600, test.get (ChronoField.OFFSET_SECONDS));
  }

  @Test
  public void testGetLong_TemporalField ()
  {
    final XMLOffsetDate test = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    assertEquals (2008, test.getLong (ChronoField.YEAR));
    assertEquals (6, test.getLong (ChronoField.MONTH_OF_YEAR));
    assertEquals (30, test.getLong (ChronoField.DAY_OF_MONTH));
    assertEquals (1, test.getLong (ChronoField.DAY_OF_WEEK));
    assertEquals (182, test.getLong (ChronoField.DAY_OF_YEAR));

    assertEquals (3600, test.getLong (ChronoField.OFFSET_SECONDS));
  }

  @Test
  public void testQuery_chrono ()
  {
    assertEquals (IsoChronology.INSTANCE, TEST_2007_07_15_PONE.query (TemporalQueries.chronology ()));
    assertEquals (IsoChronology.INSTANCE, TemporalQueries.chronology ().queryFrom (TEST_2007_07_15_PONE));
  }

  @Test
  public void testQuery_zoneId ()
  {
    assertNull (TEST_2007_07_15_PONE.query (TemporalQueries.zoneId ()));
    assertNull (TemporalQueries.zoneId ().queryFrom (TEST_2007_07_15_PONE));
  }

  @Test
  public void testQuery_precision ()
  {
    assertEquals (ChronoUnit.DAYS, TEST_2007_07_15_PONE.query (TemporalQueries.precision ()));
    assertEquals (ChronoUnit.DAYS, TemporalQueries.precision ().queryFrom (TEST_2007_07_15_PONE));
  }

  @Test
  public void testQuery_offset ()
  {
    assertEquals (OFFSET_PONE, TEST_2007_07_15_PONE.query (TemporalQueries.offset ()));
    assertEquals (OFFSET_PONE, TemporalQueries.offset ().queryFrom (TEST_2007_07_15_PONE));
  }

  @Test
  public void testQuery_zone ()
  {
    assertEquals (OFFSET_PONE, TEST_2007_07_15_PONE.query (TemporalQueries.zone ()));
    assertEquals (OFFSET_PONE, TemporalQueries.zone ().queryFrom (TEST_2007_07_15_PONE));
  }

  @Test
  public void testQuery_null ()
  {
    assertThrows (NullPointerException.class, () -> TEST_2007_07_15_PONE.query (null));
  }

  @Test
  public void testAdjustInto ()
  {
    final OffsetDateTime odt = OffsetDateTime.of (2007, 12, 3, 10, 15, 30, 0, ZoneOffset.UTC);
    final XMLOffsetDate od = XMLOffsetDate.of (2008, 1, 4, OFFSET_PONE);
    final OffsetDateTime expected = OffsetDateTime.of (2008, 1, 4, 10, 15, 30, 0, OFFSET_PONE);
    assertEquals (expected, od.adjustInto (odt));
  }

  private interface IUntil
  {
    void accept (final long expected, final XMLOffsetDate od1, final XMLOffsetDate od2, final TemporalUnit unit);
  }

  private static void _data_until (@Nonnull final IUntil aSample)
  {
    aSample.accept (1,
                    XMLOffsetDate.of (2007, 6, 30, OFFSET_PONE),
                    XMLOffsetDate.of (2007, 7, 1, OFFSET_PONE),
                    ChronoUnit.DAYS);
    aSample.accept (1,
                    XMLOffsetDate.of (2007, 6, 30, OFFSET_PONE),
                    XMLOffsetDate.of (2007, 8, 29, OFFSET_PONE),
                    ChronoUnit.MONTHS);
    aSample.accept (2,
                    XMLOffsetDate.of (2007, 6, 30, OFFSET_PONE),
                    XMLOffsetDate.of (2007, 8, 30, OFFSET_PONE),
                    ChronoUnit.MONTHS);
    aSample.accept (2,
                    XMLOffsetDate.of (2007, 6, 30, OFFSET_PONE),
                    XMLOffsetDate.of (2007, 8, 31, OFFSET_PONE),
                    ChronoUnit.MONTHS);
  }

  @Test
  public void testUntil ()
  {
    _data_until ( (expected, od1, od2, unit) -> {
      assertEquals (expected, od1.until (od2, unit));
      assertEquals (-expected, od2.until (od1, unit));
    });
  }

  @Test
  public void testUntil_otherType ()
  {
    final XMLOffsetDate start = XMLOffsetDate.of (2007, 6, 30, OFFSET_PONE);
    final Temporal end = OffsetDateTime.of (2007, 8, 31, 12, 0, 0, 0, OFFSET_PONE);
    assertEquals (2, start.until (end, ChronoUnit.MONTHS));
  }

  @Test
  public void testUntil_invalidType ()
  {
    final XMLOffsetDate od1 = XMLOffsetDate.of (2012, 6, 30, OFFSET_PONE);
    assertThrows (DateTimeException.class, () -> od1.until (Instant.ofEpochSecond (7), ChronoUnit.SECONDS));
  }

  @Test
  public void testWithOffsetSameLocal ()
  {
    final XMLOffsetDate base = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    final XMLOffsetDate test = base.withOffsetSameLocal (OFFSET_PTWO);
    assertEquals (base.toLocalDate (), test.toLocalDate ());
    assertEquals (OFFSET_PTWO, test.getOffset ());
  }

  @Test
  public void testWithOffsetSameLocal_noChange ()
  {
    final XMLOffsetDate base = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    final XMLOffsetDate test = base.withOffsetSameLocal (OFFSET_PONE);
    assertEquals (base, test);
  }

  @Test
  public void testWithOffsetSameLocal_null ()
  {
    assertNotNull (TEST_2007_07_15_PONE.withOffsetSameLocal (null));
  }

  @Test
  public void testWith_adjustment ()
  {
    final XMLOffsetDate sample = XMLOffsetDate.of (LocalDate.of (2012, 3, 4), OFFSET_PONE);
    final TemporalAdjuster adjuster = dateTime -> sample;
    assertEquals (sample, TEST_2007_07_15_PONE.with (adjuster));
  }

  @Test
  public void testWith_adjustment_LocalDate ()
  {
    final XMLOffsetDate test = TEST_2007_07_15_PONE.with (LocalDate.of (2008, 6, 30));
    assertEquals (XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE), test);
  }

  @Test
  public void testWith_adjustment_OffsetDate ()
  {
    final XMLOffsetDate test = TEST_2007_07_15_PONE.with (XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PTWO));
    assertEquals (XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PTWO), test);
  }

  @Test
  public void testWith_adjustment_ZoneOffset ()
  {
    final XMLOffsetDate test = TEST_2007_07_15_PONE.with (OFFSET_PTWO);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 7, 15), OFFSET_PTWO), test);
  }

  @Test
  public void testWith_adjustment_Month ()
  {
    final XMLOffsetDate test = TEST_2007_07_15_PONE.with (DECEMBER);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 12, 15), OFFSET_PONE), test);
  }

  @Test
  public void testWith_adjustment_offsetUnchanged ()
  {
    final XMLOffsetDate base = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    final XMLOffsetDate test = base.with (Year.of (2008));
    assertEquals (base, test);
  }

  @Test
  public void testWith_adjustment_noChange ()
  {
    final LocalDate date = LocalDate.of (2008, 6, 30);
    final XMLOffsetDate base = XMLOffsetDate.of (date, OFFSET_PONE);
    final XMLOffsetDate test = base.with (date);
    assertEquals (base, test);
  }

  @Test
  public void testWith_adjustment_null ()
  {
    assertThrows (NullPointerException.class, () -> TEST_2007_07_15_PONE.with ((TemporalAdjuster) null));
  }

  @Test
  public void testWith_TemporalField ()
  {
    final XMLOffsetDate test = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2009, 6, 30), OFFSET_PONE), test.with (ChronoField.YEAR, 2009));
    assertEquals (XMLOffsetDate.of (LocalDate.of (2008, 7, 30), OFFSET_PONE), test.with (ChronoField.MONTH_OF_YEAR, 7));
    assertEquals (XMLOffsetDate.of (LocalDate.of (2008, 6, 1), OFFSET_PONE), test.with (ChronoField.DAY_OF_MONTH, 1));
    assertEquals (XMLOffsetDate.of (LocalDate.of (2008, 7, 1), OFFSET_PONE), test.with (ChronoField.DAY_OF_WEEK, 2));
    assertEquals (XMLOffsetDate.of (LocalDate.of (2008, 7, 1), OFFSET_PONE), test.with (ChronoField.DAY_OF_YEAR, 183));

    assertEquals (XMLOffsetDate.of (LocalDate.of (2008, 6, 30), ZoneOffset.ofHoursMinutesSeconds (2, 0, 5)),
                  test.with (ChronoField.OFFSET_SECONDS, 7205));
  }

  @Test
  public void testWith_TemporalField_null ()
  {
    assertThrows (NullPointerException.class, () -> TEST_2007_07_15_PONE.with ((TemporalField) null, 0));
  }

  @Test
  public void testWith_TemporalField_invalidField ()
  {
    assertThrows (DateTimeException.class, () -> TEST_2007_07_15_PONE.with (ChronoField.AMPM_OF_DAY, 0));
  }

  @Test
  public void testWithYear_int_normal ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.withYear (2008);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2008, 7, 15), OFFSET_PONE), t);
  }

  @Test
  public void testWithYear_int_noChange ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.withYear (2007);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testWithYear_int_invalid ()
  {
    assertThrows (DateTimeException.class, () -> TEST_2007_07_15_PONE.withYear (Year.MIN_VALUE - 1));
  }

  @Test
  public void testWithYear_int_adjustDay ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (2008, 2, 29), OFFSET_PONE).withYear (2007);
    final XMLOffsetDate expected = XMLOffsetDate.of (LocalDate.of (2007, 2, 28), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void testWithMonth_int_normal ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.withMonth (1);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 1, 15), OFFSET_PONE), t);
  }

  @Test
  public void testWithMonth_int_noChange ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.withMonth (7);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testWithMonth_int_invalid ()
  {
    assertThrows (DateTimeException.class, () -> TEST_2007_07_15_PONE.withMonth (13));
  }

  @Test
  public void testWithMonth_int_adjustDay ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (2007, 12, 31), OFFSET_PONE).withMonth (11);
    final XMLOffsetDate expected = XMLOffsetDate.of (LocalDate.of (2007, 11, 30), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void testWithDayOfMonth_normal ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.withDayOfMonth (1);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 7, 1), OFFSET_PONE), t);
  }

  @Test
  public void testWithDayOfMonth_noChange ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.withDayOfMonth (15);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 7, 15), OFFSET_PONE), t);
  }

  @Test
  public void testWithDayOfMonth_invalidForMonth ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (2007, 11, 30), OFFSET_PONE).withDayOfMonth (31));
  }

  @Test
  public void testWithDayOfMonth_invalidAlways ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (2007, 11, 30), OFFSET_PONE).withDayOfMonth (32));
  }

  @Test
  public void testWithDayOfYear_normal ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.withDayOfYear (33);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 2, 2), OFFSET_PONE), t);
  }

  @Test
  public void testWithDayOfYear_noChange ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.withDayOfYear (31 + 28 + 31 + 30 + 31 + 30 + 15);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testWithDayOfYear_illegal ()
  {
    assertThrows (DateTimeException.class, () -> TEST_2007_07_15_PONE.withDayOfYear (367));
  }

  @Test
  public void testWithDayOfYear_invalid ()
  {
    assertThrows (DateTimeException.class, () -> TEST_2007_07_15_PONE.withDayOfYear (366));
  }

  @Test
  public void testPlus_PlusAdjuster ()
  {
    final Period period = Period.ofMonths (7);
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plus (period);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2008, 2, 15), OFFSET_PONE), t);
  }

  @Test
  public void testPlus_PlusAdjuster_noChange ()
  {
    final Period period = Period.ofMonths (0);
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plus (period);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testPlus_PlusAdjuster_zero ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plus (Period.ZERO);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testPlus_PlusAdjuster_null ()
  {
    assertThrows (NullPointerException.class, () -> TEST_2007_07_15_PONE.plus ((TemporalAmount) null));
  }

  @Test
  public void testPlusYears_long_normal ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusYears (1);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2008, 7, 15), OFFSET_PONE), t);
  }

  @Test
  public void testPlusYears_long_negative ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusYears (-1);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2006, 7, 15), OFFSET_PONE), t);
  }

  @Test
  public void testPlusYears_long_noChange ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusYears (0);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testPlusYears_long_adjustDay ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (2008, 2, 29), OFFSET_PONE).plusYears (1);
    final XMLOffsetDate expected = XMLOffsetDate.of (LocalDate.of (2009, 2, 28), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void testPlusYears_long_big ()
  {
    final long years = 20L + Year.MAX_VALUE;
    final XMLOffsetDate test = XMLOffsetDate.of (LocalDate.of (-40, 6, 1), OFFSET_PONE).plusYears (years);
    assertEquals (XMLOffsetDate.of (LocalDate.of ((int) (-40L + years), 6, 1), OFFSET_PONE), test);
  }

  @Test
  public void testPlusYears_long_invalidTooLarge ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 1, 1), OFFSET_PONE).plusYears (1));
  }

  @Test
  public void testPlusYears_long_invalidTooLargeMaxAddMax ()
  {
    final XMLOffsetDate test = XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE);
    assertThrows (DateTimeException.class, () -> test.plusYears (Long.MAX_VALUE));
  }

  @Test
  public void testPlusYears_long_invalidTooLargeMaxAddMin ()
  {
    final XMLOffsetDate test = XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE);
    assertThrows (DateTimeException.class, () -> test.plusYears (Long.MIN_VALUE));
  }

  @Test
  public void testPlusYears_long_invalidTooSmall ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE).plusYears (-1));
  }

  @Test
  public void testPlusMonths_long_normal ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusMonths (1);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 8, 15), OFFSET_PONE), t);
  }

  @Test
  public void testPlusMonths_long_overYears ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusMonths (25);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2009, 8, 15), OFFSET_PONE), t);
  }

  @Test
  public void testPlusMonths_long_negative ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusMonths (-1);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 6, 15), OFFSET_PONE), t);
  }

  @Test
  public void testPlusMonths_long_negativeAcrossYear ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusMonths (-7);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2006, 12, 15), OFFSET_PONE), t);
  }

  @Test
  public void testPlusMonths_long_negativeOverYears ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusMonths (-31);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2004, 12, 15), OFFSET_PONE), t);
  }

  @Test
  public void testPlusMonths_long_noChange ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusMonths (0);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testPlusMonths_long_adjustDayFromLeapYear ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (2008, 2, 29), OFFSET_PONE).plusMonths (12);
    final XMLOffsetDate expected = XMLOffsetDate.of (LocalDate.of (2009, 2, 28), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void testPlusMonths_long_adjustDayFromMonthLength ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (2007, 3, 31), OFFSET_PONE).plusMonths (1);
    final XMLOffsetDate expected = XMLOffsetDate.of (LocalDate.of (2007, 4, 30), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void testPlusMonths_long_big ()
  {
    final long months = 20L + Integer.MAX_VALUE;
    final XMLOffsetDate test = XMLOffsetDate.of (LocalDate.of (-40, 6, 1), OFFSET_PONE).plusMonths (months);
    assertEquals (XMLOffsetDate.of (LocalDate.of ((int) (-40L + months / 12), 6 + (int) (months % 12), 1), OFFSET_PONE),
                  test);
  }

  @Test
  public void testPlusMonths_long_invalidTooLarge ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE).plusMonths (1));
  }

  @Test
  public void testPlusMonths_long_invalidTooLargeMaxAddMax ()
  {
    final XMLOffsetDate test = XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE);
    assertThrows (DateTimeException.class, () -> test.plusMonths (Long.MAX_VALUE));
  }

  @Test
  public void testPlusMonths_long_invalidTooLargeMaxAddMin ()
  {
    final XMLOffsetDate test = XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE);
    assertThrows (DateTimeException.class, () -> test.plusMonths (Long.MIN_VALUE));
  }

  @Test
  public void testPlusMonths_long_invalidTooSmall ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE).plusMonths (-1));
  }

  @Test
  public void testPlusWeeks_symmetry ()
  {
    for (final XMLOffsetDate reference : new XMLOffsetDate [] { XMLOffsetDate.of (LocalDate.of (-1, 1, 1), OFFSET_PONE),
                                                                XMLOffsetDate.of (LocalDate.of (-1, 2, 28),
                                                                                  OFFSET_PTWO),
                                                                XMLOffsetDate.of (LocalDate.of (-1, 3, 1), OFFSET_PONE),
                                                                XMLOffsetDate.of (LocalDate.of (-1, 12, 31),
                                                                                  OFFSET_PTWO),
                                                                XMLOffsetDate.of (LocalDate.of (0, 1, 1), OFFSET_PONE),
                                                                XMLOffsetDate.of (LocalDate.of (0, 2, 28), OFFSET_PTWO),
                                                                XMLOffsetDate.of (LocalDate.of (0, 2, 29), OFFSET_PTWO),
                                                                XMLOffsetDate.of (LocalDate.of (0, 3, 1), OFFSET_PONE),
                                                                XMLOffsetDate.of (LocalDate.of (0, 12, 31),
                                                                                  OFFSET_PTWO),
                                                                XMLOffsetDate.of (LocalDate.of (2007, 1, 1),
                                                                                  OFFSET_PONE),
                                                                XMLOffsetDate.of (LocalDate.of (2007, 2, 28),
                                                                                  OFFSET_PTWO),
                                                                XMLOffsetDate.of (LocalDate.of (2007, 3, 1),
                                                                                  OFFSET_PONE),
                                                                XMLOffsetDate.of (LocalDate.of (2007, 12, 31),
                                                                                  OFFSET_PTWO),
                                                                XMLOffsetDate.of (LocalDate.of (2008, 1, 1),
                                                                                  OFFSET_PONE),
                                                                XMLOffsetDate.of (LocalDate.of (2008, 2, 28),
                                                                                  OFFSET_PTWO),
                                                                XMLOffsetDate.of (LocalDate.of (2008, 2, 29),
                                                                                  OFFSET_PTWO),
                                                                XMLOffsetDate.of (LocalDate.of (2008, 3, 1),
                                                                                  OFFSET_PONE),
                                                                XMLOffsetDate.of (LocalDate.of (2008, 12, 31),
                                                                                  OFFSET_PTWO),
                                                                XMLOffsetDate.of (LocalDate.of (2099, 1, 1),
                                                                                  OFFSET_PONE),
                                                                XMLOffsetDate.of (LocalDate.of (2099, 2, 28),
                                                                                  OFFSET_PTWO),
                                                                XMLOffsetDate.of (LocalDate.of (2099, 3, 1),
                                                                                  OFFSET_PONE),
                                                                XMLOffsetDate.of (LocalDate.of (2099, 12, 31),
                                                                                  OFFSET_PTWO),
                                                                XMLOffsetDate.of (LocalDate.of (2100, 1, 1),
                                                                                  OFFSET_PONE),
                                                                XMLOffsetDate.of (LocalDate.of (2100, 2, 28),
                                                                                  OFFSET_PTWO),
                                                                XMLOffsetDate.of (LocalDate.of (2100, 3, 1),
                                                                                  OFFSET_PONE),
                                                                XMLOffsetDate.of (LocalDate.of (2100, 12, 31),
                                                                                  OFFSET_PTWO) })
    {
      for (int weeks = 0; weeks < 365 * 8; weeks++)
      {
        XMLOffsetDate t = reference.plusWeeks (weeks).plusWeeks (-weeks);
        assertEquals (reference, t);

        t = reference.plusWeeks (-weeks).plusWeeks (weeks);
        assertEquals (reference, t);
      }
    }
  }

  @Test
  public void testPlusWeeks_normal ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusWeeks (1);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 7, 22), OFFSET_PONE), t);
  }

  @Test
  public void testPlusWeeks_overMonths ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusWeeks (9);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 9, 16), OFFSET_PONE), t);
  }

  @Test
  public void testPlusWeeks_overYears ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (2006, 7, 16), OFFSET_PONE).plusWeeks (52);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testPlusWeeks_overLeapYears ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusYears (-1).plusWeeks (104);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2008, 7, 12), OFFSET_PONE), t);
  }

  @Test
  public void testPlusWeeks_negative ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusWeeks (-1);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 7, 8), OFFSET_PONE), t);
  }

  @Test
  public void testPlusWeeks_negativeAcrossYear ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusWeeks (-28);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2006, 12, 31), OFFSET_PONE), t);
  }

  @Test
  public void testPlusWeeks_negativeOverYears ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusWeeks (-104);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2005, 7, 17), OFFSET_PONE), t);
  }

  @Test
  public void testPlusWeeks_noChange ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusWeeks (0);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testPlusWeeks_maximum ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 24), OFFSET_PONE).plusWeeks (1);
    final XMLOffsetDate expected = XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 31), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void testPlusWeeks_minimum ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 8), OFFSET_PONE).plusWeeks (-1);
    final XMLOffsetDate expected = XMLOffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void testPlusWeeks_invalidTooLarge ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 25), OFFSET_PONE).plusWeeks (1));
  }

  @Test
  public void testPlusWeeks_invalidTooSmall ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 7), OFFSET_PONE).plusWeeks (-1));
  }

  @Test
  public void testPlusWeeks_invalidMaxMinusMax ()
  {
    assertThrows (ArithmeticException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 25), OFFSET_PONE)
                                     .plusWeeks (Long.MAX_VALUE));
  }

  @Test
  public void testPlusWeeks_invalidMaxMinusMin ()
  {
    assertThrows (ArithmeticException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 25), OFFSET_PONE)
                                     .plusWeeks (Long.MIN_VALUE));
  }

  private static final XMLOffsetDate [] data_samplePlusDaysSymmetry;
  static
  {
    data_samplePlusDaysSymmetry = new XMLOffsetDate [] { XMLOffsetDate.of (LocalDate.of (-1, 1, 1), OFFSET_PONE),
                                                         XMLOffsetDate.of (LocalDate.of (-1, 2, 28), OFFSET_PTWO),
                                                         XMLOffsetDate.of (LocalDate.of (-1, 3, 1), OFFSET_PONE),
                                                         XMLOffsetDate.of (LocalDate.of (-1, 12, 31), OFFSET_PTWO),
                                                         XMLOffsetDate.of (LocalDate.of (0, 1, 1), OFFSET_PONE),
                                                         XMLOffsetDate.of (LocalDate.of (0, 2, 28), OFFSET_PTWO),
                                                         XMLOffsetDate.of (LocalDate.of (0, 2, 29), OFFSET_PTWO),
                                                         XMLOffsetDate.of (LocalDate.of (0, 3, 1), OFFSET_PONE),
                                                         XMLOffsetDate.of (LocalDate.of (0, 12, 31), OFFSET_PTWO),
                                                         XMLOffsetDate.of (LocalDate.of (2007, 1, 1), OFFSET_PONE),
                                                         XMLOffsetDate.of (LocalDate.of (2007, 2, 28), OFFSET_PTWO),
                                                         XMLOffsetDate.of (LocalDate.of (2007, 3, 1), OFFSET_PONE),
                                                         XMLOffsetDate.of (LocalDate.of (2007, 12, 31), OFFSET_PTWO),
                                                         XMLOffsetDate.of (LocalDate.of (2008, 1, 1), OFFSET_PONE),
                                                         XMLOffsetDate.of (LocalDate.of (2008, 2, 28), OFFSET_PTWO),
                                                         XMLOffsetDate.of (LocalDate.of (2008, 2, 29), OFFSET_PTWO),
                                                         XMLOffsetDate.of (LocalDate.of (2008, 3, 1), OFFSET_PONE),
                                                         XMLOffsetDate.of (LocalDate.of (2008, 12, 31), OFFSET_PTWO),
                                                         XMLOffsetDate.of (LocalDate.of (2099, 1, 1), OFFSET_PONE),
                                                         XMLOffsetDate.of (LocalDate.of (2099, 2, 28), OFFSET_PTWO),
                                                         XMLOffsetDate.of (LocalDate.of (2099, 3, 1), OFFSET_PONE),
                                                         XMLOffsetDate.of (LocalDate.of (2099, 12, 31), OFFSET_PTWO),
                                                         XMLOffsetDate.of (LocalDate.of (2100, 1, 1), OFFSET_PONE),
                                                         XMLOffsetDate.of (LocalDate.of (2100, 2, 28), OFFSET_PTWO),
                                                         XMLOffsetDate.of (LocalDate.of (2100, 3, 1), OFFSET_PONE),
                                                         XMLOffsetDate.of (LocalDate.of (2100, 12, 31), OFFSET_PTWO) };
  }

  @Test
  public void testPlusDays_symmetry ()
  {
    for (final XMLOffsetDate reference : data_samplePlusDaysSymmetry)
      for (int days = 0; days < 365 * 8; days++)
      {
        XMLOffsetDate t = reference.plusDays (days).plusDays (-days);
        assertEquals (reference, t);

        t = reference.plusDays (-days).plusDays (days);
        assertEquals (reference, t);
      }
  }

  @Test
  public void testPlusDays_normal ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusDays (1);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 7, 16), OFFSET_PONE), t);
  }

  @Test
  public void testPlusDays_overMonths ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusDays (62);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 9, 15), OFFSET_PONE), t);
  }

  @Test
  public void testPlusDays_overYears ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (2006, 7, 14), OFFSET_PONE).plusDays (366);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testPlusDays_overLeapYears ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusYears (-1).plusDays (365 + 366);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2008, 7, 15), OFFSET_PONE), t);
  }

  @Test
  public void testPlusDays_negative ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusDays (-1);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 7, 14), OFFSET_PONE), t);
  }

  @Test
  public void testPlusDays_negativeAcrossYear ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusDays (-196);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2006, 12, 31), OFFSET_PONE), t);
  }

  @Test
  public void testPlusDays_negativeOverYears ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusDays (-730);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2005, 7, 15), OFFSET_PONE), t);
  }

  @Test
  public void testPlusDays_noChange ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusDays (0);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testPlusDays_maximum ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 30), OFFSET_PONE).plusDays (1);
    final XMLOffsetDate expected = XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 31), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void testPlusDays_minimum ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 2), OFFSET_PONE).plusDays (-1);
    final XMLOffsetDate expected = XMLOffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void testPlusDays_invalidTooLarge ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 31), OFFSET_PONE).plusDays (1));
  }

  @Test
  public void testPlusDays_invalidTooSmall ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE).plusDays (-1));
  }

  @Test
  public void testPlusDays_overflowTooLarge ()
  {
    assertThrows (ArithmeticException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 31), OFFSET_PONE)
                                     .plusDays (Long.MAX_VALUE));
  }

  @Test
  public void testPlusDays_overflowTooSmall ()
  {
    assertThrows (ArithmeticException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE).plusDays (Long.MIN_VALUE));
  }

  @Test
  public void testMinus_MinusAdjuster ()
  {
    final Period period = Period.ofMonths (7);
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minus (period);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2006, 12, 15), OFFSET_PONE), t);
  }

  @Test
  public void testMinus_MinusAdjuster_noChange ()
  {
    final Period period = Period.ofMonths (0);
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minus (period);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testMinus_MinusAdjuster_zero ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minus (Period.ZERO);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testPlus_MinusAdjuster_null ()
  {
    assertThrows (NullPointerException.class, () -> TEST_2007_07_15_PONE.minus ((TemporalAmount) null));
  }

  @Test
  public void testMinusYears_long_normal ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusYears (1);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2006, 7, 15), OFFSET_PONE), t);
  }

  @Test
  public void testMinusYears_long_negative ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusYears (-1);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2008, 7, 15), OFFSET_PONE), t);
  }

  @Test
  public void testMinusYears_long_noChange ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusYears (0);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testMinusYears_long_adjustDay ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (2008, 2, 29), OFFSET_PONE).minusYears (1);
    final XMLOffsetDate expected = XMLOffsetDate.of (LocalDate.of (2007, 2, 28), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void testMinusYears_long_big ()
  {
    final long years = 20L + Year.MAX_VALUE;
    final XMLOffsetDate test = XMLOffsetDate.of (LocalDate.of (40, 6, 1), OFFSET_PONE).minusYears (years);
    assertEquals (XMLOffsetDate.of (LocalDate.of ((int) (40L - years), 6, 1), OFFSET_PONE), test);
  }

  @Test
  public void testMinusYears_long_invalidTooLarge ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 1, 1), OFFSET_PONE).minusYears (-1));
  }

  @Test
  public void testMinusYears_long_invalidTooLargeMaxAddMax ()
  {
    final XMLOffsetDate test = XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE);
    assertThrows (DateTimeException.class, () -> test.minusYears (Long.MAX_VALUE));
  }

  @Test
  public void testMinusYears_long_invalidTooLargeMaxAddMin ()
  {
    final XMLOffsetDate test = XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE);
    assertThrows (DateTimeException.class, () -> test.minusYears (Long.MIN_VALUE));
  }

  @Test
  public void testMinusYears_long_invalidTooSmall ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE).minusYears (1));
  }

  @Test
  public void testMinusMonths_long_normal ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusMonths (1);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 6, 15), OFFSET_PONE), t);
  }

  @Test
  public void testMinusMonths_long_overYears ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusMonths (25);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2005, 6, 15), OFFSET_PONE), t);
  }

  @Test
  public void testMinusMonths_long_negative ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusMonths (-1);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 8, 15), OFFSET_PONE), t);
  }

  @Test
  public void testMinusMonths_long_negativeAcrossYear ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusMonths (-7);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2008, 2, 15), OFFSET_PONE), t);
  }

  @Test
  public void testMinusMonths_long_negativeOverYears ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusMonths (-31);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2010, 2, 15), OFFSET_PONE), t);
  }

  @Test
  public void testMinusMonths_long_noChange ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusMonths (0);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testMinusMonths_long_adjustDayFromLeapYear ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (2008, 2, 29), OFFSET_PONE).minusMonths (12);
    final XMLOffsetDate expected = XMLOffsetDate.of (LocalDate.of (2007, 2, 28), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void testMinusMonths_long_adjustDayFromMonthLength ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (2007, 3, 31), OFFSET_PONE).minusMonths (1);
    final XMLOffsetDate expected = XMLOffsetDate.of (LocalDate.of (2007, 2, 28), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void testMinusMonths_long_big ()
  {
    final long months = 20L + Integer.MAX_VALUE;
    final XMLOffsetDate test = XMLOffsetDate.of (LocalDate.of (40, 6, 1), OFFSET_PONE).minusMonths (months);
    assertEquals (XMLOffsetDate.of (LocalDate.of ((int) (40L - months / 12), 6 - (int) (months % 12), 1), OFFSET_PONE),
                  test);
  }

  @Test
  public void testMinusMonths_long_invalidTooLarge ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE).minusMonths (-1));
  }

  @Test
  public void testMinusMonths_long_invalidTooLargeMaxAddMax ()
  {
    final XMLOffsetDate test = XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE);
    assertThrows (DateTimeException.class, () -> test.minusMonths (Long.MAX_VALUE));
  }

  @Test
  public void testMinusMonths_long_invalidTooLargeMaxAddMin ()
  {
    final XMLOffsetDate test = XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE);
    assertThrows (DateTimeException.class, () -> test.minusMonths (Long.MIN_VALUE));
  }

  @Test
  public void testMinusMonths_long_invalidTooSmall ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE).minusMonths (1));
  }

  private static final XMLOffsetDate [] data_sampleMinusWeeksSymmetry;
  static
  {
    data_sampleMinusWeeksSymmetry = new XMLOffsetDate [] { XMLOffsetDate.of (LocalDate.of (-1, 1, 1), OFFSET_PONE),
                                                           XMLOffsetDate.of (LocalDate.of (-1, 2, 28), OFFSET_PTWO),
                                                           XMLOffsetDate.of (LocalDate.of (-1, 3, 1), OFFSET_PONE),
                                                           XMLOffsetDate.of (LocalDate.of (-1, 12, 31), OFFSET_PTWO),
                                                           XMLOffsetDate.of (LocalDate.of (0, 1, 1), OFFSET_PONE),
                                                           XMLOffsetDate.of (LocalDate.of (0, 2, 28), OFFSET_PTWO),
                                                           XMLOffsetDate.of (LocalDate.of (0, 2, 29), OFFSET_PTWO),
                                                           XMLOffsetDate.of (LocalDate.of (0, 3, 1), OFFSET_PONE),
                                                           XMLOffsetDate.of (LocalDate.of (0, 12, 31), OFFSET_PTWO),
                                                           XMLOffsetDate.of (LocalDate.of (2007, 1, 1), OFFSET_PONE),
                                                           XMLOffsetDate.of (LocalDate.of (2007, 2, 28), OFFSET_PTWO),
                                                           XMLOffsetDate.of (LocalDate.of (2007, 3, 1), OFFSET_PONE),
                                                           XMLOffsetDate.of (LocalDate.of (2007, 12, 31), OFFSET_PTWO),
                                                           XMLOffsetDate.of (LocalDate.of (2008, 1, 1), OFFSET_PONE),
                                                           XMLOffsetDate.of (LocalDate.of (2008, 2, 28), OFFSET_PTWO),
                                                           XMLOffsetDate.of (LocalDate.of (2008, 2, 29), OFFSET_PTWO),
                                                           XMLOffsetDate.of (LocalDate.of (2008, 3, 1), OFFSET_PONE),
                                                           XMLOffsetDate.of (LocalDate.of (2008, 12, 31), OFFSET_PTWO),
                                                           XMLOffsetDate.of (LocalDate.of (2099, 1, 1), OFFSET_PONE),
                                                           XMLOffsetDate.of (LocalDate.of (2099, 2, 28), OFFSET_PTWO),
                                                           XMLOffsetDate.of (LocalDate.of (2099, 3, 1), OFFSET_PONE),
                                                           XMLOffsetDate.of (LocalDate.of (2099, 12, 31), OFFSET_PTWO),
                                                           XMLOffsetDate.of (LocalDate.of (2100, 1, 1), OFFSET_PONE),
                                                           XMLOffsetDate.of (LocalDate.of (2100, 2, 28), OFFSET_PTWO),
                                                           XMLOffsetDate.of (LocalDate.of (2100, 3, 1), OFFSET_PONE),
                                                           XMLOffsetDate.of (LocalDate.of (2100, 12, 31),
                                                                             OFFSET_PTWO) };
  }

  @Test
  public void testMinusWeeks_symmetry ()
  {
    for (final XMLOffsetDate reference : data_sampleMinusWeeksSymmetry)
      for (int weeks = 0; weeks < 365 * 8; weeks++)
      {
        XMLOffsetDate t = reference.minusWeeks (weeks).minusWeeks (-weeks);
        assertEquals (reference, t);

        t = reference.minusWeeks (-weeks).minusWeeks (weeks);
        assertEquals (reference, t);
      }
  }

  @Test
  public void testMinusWeeks_normal ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusWeeks (1);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 7, 8), OFFSET_PONE), t);
  }

  @Test
  public void testMinusWeeks_overMonths ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusWeeks (9);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 5, 13), OFFSET_PONE), t);
  }

  @Test
  public void testMinusWeeks_overYears ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (2008, 7, 13), OFFSET_PONE).minusWeeks (52);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testMinusWeeks_overLeapYears ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusYears (-1).minusWeeks (104);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2006, 7, 18), OFFSET_PONE), t);
  }

  @Test
  public void testMinusWeeks_negative ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusWeeks (-1);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 7, 22), OFFSET_PONE), t);
  }

  @Test
  public void testMinusWeeks_negativeAcrossYear ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusWeeks (-28);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2008, 1, 27), OFFSET_PONE), t);
  }

  @Test
  public void testMinusWeeks_negativeOverYears ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusWeeks (-104);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2009, 7, 12), OFFSET_PONE), t);
  }

  @Test
  public void testMinusWeeks_noChange ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusWeeks (0);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testMinusWeeks_maximum ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 24), OFFSET_PONE).minusWeeks (-1);
    final XMLOffsetDate expected = XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 31), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void testMinusWeeks_minimum ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 8), OFFSET_PONE).minusWeeks (1);
    final XMLOffsetDate expected = XMLOffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void testMinusWeeks_invalidTooLarge ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 25), OFFSET_PONE).minusWeeks (-1));
  }

  @Test
  public void testMinusWeeks_invalidTooSmall ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 7), OFFSET_PONE).minusWeeks (1));
  }

  @Test
  public void testMinusWeeks_invalidMaxMinusMax ()
  {
    assertThrows (ArithmeticException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 25), OFFSET_PONE)
                                     .minusWeeks (Long.MAX_VALUE));
  }

  @Test
  public void testMinusWeeks_invalidMaxMinusMin ()
  {
    assertThrows (ArithmeticException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 25), OFFSET_PONE)
                                     .minusWeeks (Long.MIN_VALUE));
  }

  private static final XMLOffsetDate [] data_sampleMinusDaysSymmetry;
  static
  {
    data_sampleMinusDaysSymmetry = new XMLOffsetDate [] { XMLOffsetDate.of (LocalDate.of (-1, 1, 1), OFFSET_PONE),
                                                          XMLOffsetDate.of (LocalDate.of (-1, 2, 28), OFFSET_PTWO),
                                                          XMLOffsetDate.of (LocalDate.of (-1, 3, 1), OFFSET_PONE),
                                                          XMLOffsetDate.of (LocalDate.of (-1, 12, 31), OFFSET_PTWO),
                                                          XMLOffsetDate.of (LocalDate.of (0, 1, 1), OFFSET_PONE),
                                                          XMLOffsetDate.of (LocalDate.of (0, 2, 28), OFFSET_PTWO),
                                                          XMLOffsetDate.of (LocalDate.of (0, 2, 29), OFFSET_PTWO),
                                                          XMLOffsetDate.of (LocalDate.of (0, 3, 1), OFFSET_PONE),
                                                          XMLOffsetDate.of (LocalDate.of (0, 12, 31), OFFSET_PTWO),
                                                          XMLOffsetDate.of (LocalDate.of (2007, 1, 1), OFFSET_PONE),
                                                          XMLOffsetDate.of (LocalDate.of (2007, 2, 28), OFFSET_PTWO),
                                                          XMLOffsetDate.of (LocalDate.of (2007, 3, 1), OFFSET_PONE),
                                                          XMLOffsetDate.of (LocalDate.of (2007, 12, 31), OFFSET_PTWO),
                                                          XMLOffsetDate.of (LocalDate.of (2008, 1, 1), OFFSET_PONE),
                                                          XMLOffsetDate.of (LocalDate.of (2008, 2, 28), OFFSET_PTWO),
                                                          XMLOffsetDate.of (LocalDate.of (2008, 2, 29), OFFSET_PTWO),
                                                          XMLOffsetDate.of (LocalDate.of (2008, 3, 1), OFFSET_PONE),
                                                          XMLOffsetDate.of (LocalDate.of (2008, 12, 31), OFFSET_PTWO),
                                                          XMLOffsetDate.of (LocalDate.of (2099, 1, 1), OFFSET_PONE),
                                                          XMLOffsetDate.of (LocalDate.of (2099, 2, 28), OFFSET_PTWO),
                                                          XMLOffsetDate.of (LocalDate.of (2099, 3, 1), OFFSET_PONE),
                                                          XMLOffsetDate.of (LocalDate.of (2099, 12, 31), OFFSET_PTWO),
                                                          XMLOffsetDate.of (LocalDate.of (2100, 1, 1), OFFSET_PONE),
                                                          XMLOffsetDate.of (LocalDate.of (2100, 2, 28), OFFSET_PTWO),
                                                          XMLOffsetDate.of (LocalDate.of (2100, 3, 1), OFFSET_PONE),
                                                          XMLOffsetDate.of (LocalDate.of (2100, 12, 31), OFFSET_PTWO) };
  }

  @Test
  public void testMinusDays_symmetry ()
  {
    for (final XMLOffsetDate reference : data_sampleMinusDaysSymmetry)
      for (int days = 0; days < 365 * 8; days++)
      {
        XMLOffsetDate t = reference.minusDays (days).minusDays (-days);
        assertEquals (reference, t);

        t = reference.minusDays (-days).minusDays (days);
        assertEquals (reference, t);
      }
  }

  @Test
  public void testMinusDays_normal ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusDays (1);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 7, 14), OFFSET_PONE), t);
  }

  @Test
  public void testMinusDays_overMonths ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusDays (62);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 5, 14), OFFSET_PONE), t);
  }

  @Test
  public void testMinusDays_overYears ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (2008, 7, 16), OFFSET_PONE).minusDays (367);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testMinusDays_overLeapYears ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.plusYears (2).minusDays (365 + 366);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testMinusDays_negative ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusDays (-1);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 7, 16), OFFSET_PONE), t);
  }

  @Test
  public void testMinusDays_negativeAcrossYear ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusDays (-169);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2007, 12, 31), OFFSET_PONE), t);
  }

  @Test
  public void testMinusDays_negativeOverYears ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusDays (-731);
    assertEquals (XMLOffsetDate.of (LocalDate.of (2009, 7, 15), OFFSET_PONE), t);
  }

  @Test
  public void testMinusDays_noChange ()
  {
    final XMLOffsetDate t = TEST_2007_07_15_PONE.minusDays (0);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void testMinusDays_maximum ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 30), OFFSET_PONE).minusDays (-1);
    final XMLOffsetDate expected = XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 31), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void testMinusDays_minimum ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 2), OFFSET_PONE).minusDays (1);
    final XMLOffsetDate expected = XMLOffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void testMinusDays_invalidTooLarge ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 31), OFFSET_PONE).minusDays (-1));
  }

  @Test
  public void testMinusDays_invalidTooSmall ()
  {
    assertThrows (DateTimeException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE).minusDays (1));
  }

  @Test
  public void testMinusDays_overflowTooLarge ()
  {
    assertThrows (ArithmeticException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 31), OFFSET_PONE)
                                     .minusDays (Long.MIN_VALUE));
  }

  @Test
  public void testMinusDays_overflowTooSmall ()
  {
    assertThrows (ArithmeticException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE).minusDays (Long.MAX_VALUE));
  }

  @Test
  public void testFormat_formatter ()
  {
    final DateTimeFormatter f = DateTimeFormatter.ofPattern ("y M d", Locale.US);
    final String t = XMLOffsetDate.of (LocalDate.of (2010, 12, 3), OFFSET_PONE).format (f);
    assertEquals ("2010 12 3", t);
  }

  @Test
  public void testFormat_formatter_null ()
  {
    assertThrows (NullPointerException.class,
                  () -> XMLOffsetDate.of (LocalDate.of (2010, 12, 3), OFFSET_PONE).format (null));
  }

  @Test
  public void testAtTime_Local ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PTWO);
    assertEquals (OffsetDateTime.of (LocalDate.of (2008, 6, 30), LocalTime.of (11, 30), OFFSET_PTWO),
                  t.atTime (LocalTime.of (11, 30)));
  }

  @Test
  public void testAtTime_Local_nullLocalTime ()
  {
    final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PTWO);
    assertThrows (NullPointerException.class, () -> t.atTime ((LocalTime) null));
  }

  @Test
  public void testToLocalDate ()
  {
    _data_sampleDates ( (y, m, d, offset) -> {
      final LocalDate t = LocalDate.of (y, m, d);
      assertEquals (t, XMLOffsetDate.of (LocalDate.of (y, m, d), offset).toLocalDate ());
    });
  }

  @Test
  public void testToEpochSecond ()
  {
    final XMLOffsetDate od = XMLOffsetDate.of (1970, 1, 1, ZoneOffset.UTC);
    assertEquals (0, od.toEpochSecond (LocalTime.MIDNIGHT));
    assertEquals (12 * 60 * 60, od.toEpochSecond (LocalTime.MIDNIGHT.plusSeconds (12 * 60 * 60)));
  }

  @Test
  public void testCompareTo_date ()
  {
    // a is before b due to date
    final XMLOffsetDate a = XMLOffsetDate.of (LocalDate.of (2008, 6, 29), OFFSET_PONE);
    final XMLOffsetDate b = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);

    assertTrue (a.compareTo (b) < 0);
    assertTrue (b.compareTo (a) > 0);
    assertTrue (a.compareTo (a) == 0);
    assertTrue (b.compareTo (b) == 0);
    assertTrue (a.atTime (LocalTime.MIDNIGHT).toInstant ().compareTo (b.atTime (LocalTime.MIDNIGHT).toInstant ()) < 0);
  }

  @Test
  public void testCompareTo_offset ()
  {
    // a is before b due to offset
    final XMLOffsetDate a = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PTWO);
    final XMLOffsetDate b = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);

    assertTrue (a.compareTo (b) < 0);
    assertTrue (b.compareTo (a) > 0);
    assertTrue (a.compareTo (a) == 0);
    assertTrue (b.compareTo (b) == 0);
    assertTrue (a.atTime (LocalTime.MIDNIGHT).toInstant ().compareTo (b.atTime (LocalTime.MIDNIGHT).toInstant ()) < 0);
  }

  @Test
  public void testCompareTo_both ()
  {
    // a is before b due in instant scale
    final XMLOffsetDate a = XMLOffsetDate.of (LocalDate.of (2008, 6, 29), OFFSET_PTWO);
    final XMLOffsetDate b = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);

    assertTrue (a.compareTo (b) < 0);
    assertTrue (b.compareTo (a) > 0);
    assertEquals (a.compareTo (a), 0);
    assertEquals (b.compareTo (b), 0);
    assertTrue (a.atTime (LocalTime.MIDNIGHT).toInstant ().compareTo (b.atTime (LocalTime.MIDNIGHT).toInstant ()) < 0);
  }

  @Test
  public void testCompareTo_24hourDifference ()
  {
    // a is before b despite being same time-line time
    final XMLOffsetDate a = XMLOffsetDate.of (LocalDate.of (2008, 6, 29), ZoneOffset.ofHours (-12));
    final XMLOffsetDate b = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), ZoneOffset.ofHours (12));

    assertTrue (a.compareTo (b) < 0);
    assertTrue (b.compareTo (a) > 0);
    assertEquals (0, a.compareTo (a));
    assertEquals (0, b.compareTo (b));
    assertEquals (0, a.atTime (LocalTime.MIDNIGHT).toInstant ().compareTo (b.atTime (LocalTime.MIDNIGHT).toInstant ()));
  }

  @Test
  public void testCompareTo_null ()
  {
    final XMLOffsetDate a = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    assertThrows (NullPointerException.class, () -> a.compareTo (null));
  }

  @Test
  @SuppressWarnings ({ "unchecked", "rawtypes" })
  public void testCompareToNonOffsetDate ()
  {
    final Comparable c = TEST_2007_07_15_PONE;
    assertThrows (ClassCastException.class, () -> c.compareTo (new Object ()));
  }

  @Test
  public void testIsBeforeIsAfterIsEqual1 ()
  {
    // a is before b due to time
    final XMLOffsetDate a = XMLOffsetDate.of (LocalDate.of (2008, 6, 29), OFFSET_PONE);
    final XMLOffsetDate b = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);

    assertTrue (a.isBefore (b));
    assertFalse (a.isEqual (b));
    assertFalse (a.isAfter (b));

    assertFalse (b.isBefore (a));
    assertFalse (b.isEqual (a));
    assertTrue (b.isAfter (a));

    assertFalse (a.isBefore (a));
    assertFalse (b.isBefore (b));

    assertTrue (a.isEqual (a));
    assertTrue (b.isEqual (b));

    assertFalse (a.isAfter (a));
    assertFalse (b.isAfter (b));
  }

  @Test
  public void testIsBeforeIsAfterIsEqual2 ()
  {
    // a is before b due to offset
    final XMLOffsetDate a = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PTWO);
    final XMLOffsetDate b = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);

    assertTrue (a.isBefore (b));
    assertFalse (a.isEqual (b));
    assertFalse (a.isAfter (b));

    assertFalse (b.isBefore (a));
    assertFalse (b.isEqual (a));
    assertTrue (b.isAfter (a));

    assertFalse (a.isBefore (a));
    assertFalse (b.isBefore (b));

    assertTrue (a.isEqual (a));
    assertTrue (b.isEqual (b));

    assertFalse (a.isAfter (a));
    assertFalse (b.isAfter (b));
  }

  @Test
  public void testIsBeforeIsAfterIsEqual_instantComparison ()
  {
    // a is same instant as b
    final XMLOffsetDate a = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), ZoneOffset.ofHours (12));
    final XMLOffsetDate b = XMLOffsetDate.of (LocalDate.of (2008, 6, 29), ZoneOffset.ofHours (-12));

    assertFalse (a.isBefore (b));
    assertTrue (a.isEqual (b));
    assertFalse (a.isAfter (b));

    assertFalse (b.isBefore (a));
    assertTrue (b.isEqual (a));
    assertFalse (b.isAfter (a));

    assertFalse (a.isBefore (a));
    assertFalse (b.isBefore (b));

    assertTrue (a.isEqual (a));
    assertTrue (b.isEqual (b));

    assertFalse (a.isAfter (a));
    assertFalse (b.isAfter (b));
  }

  @Test
  public void testIsBefore_null ()
  {
    final XMLOffsetDate a = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    assertThrows (NullPointerException.class, () -> a.isBefore (null));
  }

  @Test
  public void testIsAfter_null ()
  {
    final XMLOffsetDate a = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    assertThrows (NullPointerException.class, () -> a.isAfter (null));
  }

  @Test
  public void testIsEqual_null ()
  {
    final XMLOffsetDate a = XMLOffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    assertThrows (NullPointerException.class, () -> a.isEqual (null));
  }

  @Test
  public void testEquals_true ()
  {
    _data_sampleDates ( (y, m, d, offset) -> {
      final XMLOffsetDate a = XMLOffsetDate.of (LocalDate.of (y, m, d), offset);
      final XMLOffsetDate b = XMLOffsetDate.of (LocalDate.of (y, m, d), offset);
      assertEquals (a, b);
      assertEquals (a.hashCode (), b.hashCode ());
    });
  }

  @Test
  public void testEquals_false_year_differs ()
  {
    _data_sampleDates ( (y, m, d, offset) -> {
      final XMLOffsetDate a = XMLOffsetDate.of (LocalDate.of (y, m, d), offset);
      final XMLOffsetDate b = XMLOffsetDate.of (LocalDate.of (y + 1, m, d), offset);
      assertNotEquals (a, b);
    });
  }

  @Test
  public void testEquals_false_month_differs ()
  {
    _data_sampleDates ( (y, m, d, offset) -> {
      final XMLOffsetDate a = XMLOffsetDate.of (LocalDate.of (y, m, d), offset);
      final XMLOffsetDate b = XMLOffsetDate.of (LocalDate.of (y, m + 1, d), offset);
      assertNotEquals (a, b);
    });
  }

  @Test
  public void testEquals_false_day_differs ()
  {
    _data_sampleDates ( (y, m, d, offset) -> {
      final XMLOffsetDate a = XMLOffsetDate.of (LocalDate.of (y, m, d), offset);
      final XMLOffsetDate b = XMLOffsetDate.of (LocalDate.of (y, m, d + 1), offset);
      assertNotEquals (a, b);
    });
  }

  @Test
  public void testEquals_false_offset_differs ()
  {
    _data_sampleDates ( (y, m, d, offset) -> {
      final XMLOffsetDate a = XMLOffsetDate.of (LocalDate.of (y, m, d), OFFSET_PONE);
      final XMLOffsetDate b = XMLOffsetDate.of (LocalDate.of (y, m, d), OFFSET_PTWO);
      assertNotEquals (a, b);
    });
  }

  @Test
  public void testEquals_itself_true ()
  {
    assertTrue (TEST_2007_07_15_PONE.equals (TEST_2007_07_15_PONE));
  }

  @SuppressWarnings ("unlikely-arg-type")
  @Test
  public void testEquals_string_false ()
  {
    assertFalse (TEST_2007_07_15_PONE.equals ("2007-07-15"));
  }

  @Test
  public void testToString ()
  {
    _data_sampleToString ( (y, m, d, offsetId, expected) -> {
      final XMLOffsetDate t = XMLOffsetDate.of (LocalDate.of (y, m, d), ZoneOffset.of (offsetId));
      final String str = t.toString ();
      assertEquals (expected, str);
    });
  }

  @Test
  public void testSerialization ()
  {
    final XMLOffsetDate aObj = PDTFactory.getCurrentXMLOffsetDate ();
    CommonsTestHelper.testDefaultSerialization (aObj);
  }

  @Test
  public void testConvert ()
  {
    XMLOffsetDate aDT = TypeConverter.convert ("2021-05-10", XMLOffsetDate.class);
    assertNotNull (aDT);
    assertNull (aDT.getOffset ());
    assertFalse (aDT.hasOffset ());
    assertEquals (PDTFactory.createLocalDate (2021, Month.MAY, 10), aDT.toLocalDate ());

    aDT = TypeConverter.convert ("2021-05-10Z", XMLOffsetDate.class);
    assertNotNull (aDT);
    assertNotNull (aDT.getOffset ());
    assertTrue (aDT.hasOffset ());
    assertEquals (0, aDT.getOffset ().getTotalSeconds ());
    assertEquals (PDTFactory.createLocalDate (2021, Month.MAY, 10), aDT.toLocalDate ());

    aDT = TypeConverter.convert ("2021-05-10+01:00", XMLOffsetDate.class);
    assertNotNull (aDT);
    assertNotNull (aDT.getOffset ());
    assertTrue (aDT.hasOffset ());
    assertEquals (1 * CGlobal.SECONDS_PER_HOUR, aDT.getOffset ().getTotalSeconds ());
    assertEquals (PDTFactory.createLocalDate (2021, Month.MAY, 10), aDT.toLocalDate ());

    try
    {
      TypeConverter.convert ("2021-05-10+01", XMLOffsetDate.class);
      fail ();
    }
    catch (final TypeConverterException ex)
    {}
  }
}
