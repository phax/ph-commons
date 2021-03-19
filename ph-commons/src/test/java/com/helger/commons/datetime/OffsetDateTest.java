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
import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.ERA;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.OFFSET_SECONDS;
import static java.time.temporal.ChronoField.PROLEPTIC_MONTH;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.ChronoField.YEAR_OF_ERA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import java.time.temporal.JulianFields;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.junit.Before;
import org.junit.Test;

/**
 * Test {@link OffsetDate}.
 */
public final class OffsetDateTest
{
  private static final ZoneOffset OFFSET_PONE = ZoneOffset.ofHours (1);
  private static final ZoneOffset OFFSET_PTWO = ZoneOffset.ofHours (2);

  private OffsetDate TEST_2007_07_15_PONE;

  @Before
  public void setUp ()
  {
    TEST_2007_07_15_PONE = OffsetDate.of (LocalDate.of (2007, 7, 15), OFFSET_PONE);
  }

  protected List <TemporalAccessor> samples ()
  {
    final TemporalAccessor [] array = { TEST_2007_07_15_PONE, OffsetDate.MIN, OffsetDate.MAX };
    return Arrays.asList (array);
  }

  protected List <TemporalField> validFields ()
  {
    final TemporalField [] array = { DAY_OF_WEEK,
                                     ALIGNED_DAY_OF_WEEK_IN_MONTH,
                                     ALIGNED_DAY_OF_WEEK_IN_YEAR,
                                     DAY_OF_MONTH,
                                     DAY_OF_YEAR,
                                     EPOCH_DAY,
                                     ALIGNED_WEEK_OF_MONTH,
                                     ALIGNED_WEEK_OF_YEAR,
                                     MONTH_OF_YEAR,
                                     PROLEPTIC_MONTH,
                                     YEAR_OF_ERA,
                                     YEAR,
                                     ERA,
                                     OFFSET_SECONDS,
                                     JulianFields.JULIAN_DAY,
                                     JulianFields.MODIFIED_JULIAN_DAY,
                                     JulianFields.RATA_DIE, };
    return Arrays.asList (array);
  }

  protected List <TemporalField> invalidFields ()
  {
    final List <TemporalField> list = new ArrayList <> (Arrays.<TemporalField> asList (ChronoField.values ()));
    list.removeAll (validFields ());
    return list;
  }

  static interface ISampleWithString
  {
    void accept (final int y, final int m, final int d, final String offsetId, final String parsable);
  }

  private static void data_sampleToString (@Nonnull final ISampleWithString aSample)
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

  // -----------------------------------------------------------------------
  // constants
  // -----------------------------------------------------------------------
  @Test
  public void constant_MIN ()
  {
    check (OffsetDate.MIN, Year.MIN_VALUE, 1, 1, ZoneOffset.MAX);
  }

  @Test
  public void constant_MAX ()
  {
    check (OffsetDate.MAX, Year.MAX_VALUE, 12, 31, ZoneOffset.MIN);
  }

  // -----------------------------------------------------------------------
  // now()
  // -----------------------------------------------------------------------
  @Test
  public void now ()
  {
    OffsetDate expected = OffsetDate.now (Clock.systemDefaultZone ());
    OffsetDate test = OffsetDate.now ();
    for (int i = 0; i < 100; i++)
    {
      if (expected.equals (test))
      {
        return;
      }
      expected = OffsetDate.now (Clock.systemDefaultZone ());
      test = OffsetDate.now ();
    }
    assertEquals (expected, test);
  }

  @Test
  public void now_Clock_allSecsInDay_utc ()
  {
    for (int i = 0; i < (2 * 24 * 60 * 60); i++)
    {
      final Instant instant = Instant.ofEpochSecond (i);
      final Clock clock = Clock.fixed (instant, ZoneOffset.UTC);
      final OffsetDate test = OffsetDate.now (clock);
      check (test, 1970, 1, (i < 24 * 60 * 60 ? 1 : 2), ZoneOffset.UTC);
    }
  }

  @Test
  public void now_Clock_allSecsInDay_beforeEpoch ()
  {
    for (int i = -1; i >= -(2 * 24 * 60 * 60); i--)
    {
      final Instant instant = Instant.ofEpochSecond (i);
      final Clock clock = Clock.fixed (instant, ZoneOffset.UTC);
      final OffsetDate test = OffsetDate.now (clock);
      check (test, 1969, 12, (i >= -24 * 60 * 60 ? 31 : 30), ZoneOffset.UTC);
    }
  }

  @Test
  public void now_Clock_offsets ()
  {
    final Instant base = LocalDateTime.of (1970, 1, 1, 12, 0).toInstant (ZoneOffset.UTC);
    for (int i = -9; i < 15; i++)
    {
      final ZoneOffset offset = ZoneOffset.ofHours (i);
      final Clock clock = Clock.fixed (base, offset);
      final OffsetDate test = OffsetDate.now (clock);
      check (test, 1970, 1, (i >= 12 ? 2 : 1), offset);
    }
  }

  @Test
  public void now_Clock_nullZoneId ()
  {
    assertThrows (NullPointerException.class, () -> OffsetDate.now ((ZoneId) null));
  }

  @Test
  public void now_Clock_nullClock ()
  {
    assertThrows (NullPointerException.class, () -> OffsetDate.now ((Clock) null));
  }

  // -----------------------------------------------------------------------
  // factories
  // -----------------------------------------------------------------------
  private void check (final OffsetDate test, final int y, final int mo, final int d, final ZoneOffset offset)
  {
    assertEquals (LocalDate.of (y, mo, d), test.toLocalDate ());
    assertEquals (offset, test.getOffset ());

    assertEquals (y, test.getYear ());
    assertEquals (mo, test.getMonth ().getValue ());
    assertEquals (d, test.getDayOfMonth ());

    assertEquals (test, test);
    assertEquals (test.hashCode (), test.hashCode ());
    assertEquals (test, OffsetDate.of (LocalDate.of (y, mo, d), offset));
  }

  // -----------------------------------------------------------------------
  @Test
  public void factory_of_intMonthInt ()
  {
    final OffsetDate test = OffsetDate.of (LocalDate.of (2007, Month.JULY, 15), OFFSET_PONE);
    check (test, 2007, 7, 15, OFFSET_PONE);
  }

  // -----------------------------------------------------------------------
  @Test
  public void factory_of_ints ()
  {
    final OffsetDate test = OffsetDate.of (LocalDate.of (2007, 7, 15), OFFSET_PONE);
    check (test, 2007, 7, 15, OFFSET_PONE);
  }

  // -----------------------------------------------------------------------
  @Test
  public void factory_of_intsMonthOffset ()
  {
    assertEquals (OffsetDate.of (LocalDate.of (2007, Month.JULY, 15), OFFSET_PONE), TEST_2007_07_15_PONE);
  }

  @Test
  public void factory_of_intsMonthOffset_dayTooLow ()
  {
    assertThrows (DateTimeException.class, () -> OffsetDate.of (LocalDate.of (2007, Month.JANUARY, 0), OFFSET_PONE));
  }

  @Test
  public void factory_of_intsMonthOffset_dayTooHigh ()
  {
    assertThrows (DateTimeException.class, () -> OffsetDate.of (LocalDate.of (2007, Month.JANUARY, 32), OFFSET_PONE));
  }

  @Test
  public void factory_of_intsMonthOffset_nullMonth ()
  {
    assertThrows (NullPointerException.class, () -> OffsetDate.of (LocalDate.of (2007, null, 30), OFFSET_PONE));
  }

  @Test
  public void factory_of_intsMonthOffset_yearTooLow ()
  {
    assertThrows (DateTimeException.class,
                  () -> OffsetDate.of (LocalDate.of (Integer.MIN_VALUE, Month.JANUARY, 1), OFFSET_PONE));
  }

  @Test
  public void factory_of_intsMonthOffset_nullOffset ()
  {
    assertThrows (NullPointerException.class, () -> OffsetDate.of (LocalDate.of (2007, Month.JANUARY, 30), null));
  }

  // -----------------------------------------------------------------------
  @Test
  public void factory_of_intsOffset ()
  {
    final OffsetDate test = OffsetDate.of (LocalDate.of (2007, 7, 15), OFFSET_PONE);
    check (test, 2007, 7, 15, OFFSET_PONE);
  }

  @Test
  public void factory_of_ints_dayTooLow ()
  {
    assertThrows (DateTimeException.class, () -> OffsetDate.of (LocalDate.of (2007, 1, 0), OFFSET_PONE));
  }

  @Test
  public void factory_of_ints_dayTooHigh ()
  {
    assertThrows (DateTimeException.class, () -> OffsetDate.of (LocalDate.of (2007, 1, 32), OFFSET_PONE));
  }

  @Test
  public void factory_of_ints_monthTooLow ()
  {
    assertThrows (DateTimeException.class, () -> OffsetDate.of (LocalDate.of (2007, 0, 1), OFFSET_PONE));
  }

  @Test
  public void factory_of_ints_monthTooHigh ()
  {
    assertThrows (DateTimeException.class, () -> OffsetDate.of (LocalDate.of (2007, 13, 1), OFFSET_PONE));
  }

  @Test
  public void factory_of_ints_yearTooLow ()
  {
    assertThrows (DateTimeException.class, () -> OffsetDate.of (LocalDate.of (Integer.MIN_VALUE, 1, 1), OFFSET_PONE));
  }

  @Test
  public void factory_of_ints_nullOffset ()
  {
    assertThrows (NullPointerException.class, () -> OffsetDate.of (LocalDate.of (2007, 1, 1), (ZoneOffset) null));
  }

  // -----------------------------------------------------------------------
  @Test
  public void factory_of_LocalDateZoneOffset ()
  {
    final LocalDate localDate = LocalDate.of (2008, 6, 30);
    final OffsetDate test = OffsetDate.of (localDate, OFFSET_PONE);
    check (test, 2008, 6, 30, OFFSET_PONE);
  }

  @Test
  public void factory_of_LocalDateZoneOffset_nullDate ()
  {
    assertThrows (NullPointerException.class, () -> OffsetDate.of ((LocalDate) null, OFFSET_PONE));
  }

  @Test
  public void factory_of_LocalDateZoneOffset_nullOffset ()
  {
    final LocalDate localDate = LocalDate.of (2008, 6, 30);
    assertThrows (NullPointerException.class, () -> OffsetDate.of (localDate, (ZoneOffset) null));
  }

  // -----------------------------------------------------------------------
  // from(TemporalAccessor)
  // -----------------------------------------------------------------------
  @Test
  public void test_from_TemporalAccessor_OD ()
  {
    assertEquals (TEST_2007_07_15_PONE, OffsetDate.from (TEST_2007_07_15_PONE));
  }

  @Test
  public void test_from_TemporalAccessor_ZDT ()
  {
    final ZonedDateTime base = LocalDateTime.of (2007, 7, 15, 17, 30).atZone (OFFSET_PONE);
    assertEquals (TEST_2007_07_15_PONE, OffsetDate.from (base));
  }

  @Test
  public void test_from_TemporalAccessor_invalid_noDerive ()
  {
    assertThrows (DateTimeException.class, () -> OffsetDate.from (LocalTime.of (12, 30)));
  }

  @Test
  public void test_from_TemporalAccessor_null ()
  {
    assertThrows (NullPointerException.class, () -> OffsetDate.from ((TemporalAccessor) null));
  }

  // -----------------------------------------------------------------------
  // parse()
  // -----------------------------------------------------------------------
  public void factory_parse_validText ()
  {
    data_sampleToString ( (y, m, d, offsetId, parsable) -> {
      final OffsetDate t = OffsetDate.parse (parsable);
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
                                                                       "2008-02-01+19:00",
                                                                       "2008-02-01+01/00",
                                                                       "2008-02-01+1900",
                                                                       "2008-02-01+01:60",
                                                                       "2008-02-01+01:30:123",
                                                                       "2008-02-01",
                                                                       "2008-02-01+01:00[Europe/Paris]" };

  public void factory_parse_invalidText ()
  {
    for (final String unparsable : data_sampleBadParse)
      assertThrows (DateTimeParseException.class, () -> OffsetDate.parse (unparsable));
  }

  @Test
  public void factory_parse_illegalValue ()
  {
    assertThrows (DateTimeParseException.class, () -> OffsetDate.parse ("2008-06-32+01:00"));
  }

  @Test
  public void factory_parse_invalidValue ()
  {
    assertThrows (DateTimeParseException.class, () -> OffsetDate.parse ("2008-06-31+01:00"));
  }

  @Test
  public void factory_parse_nullText ()
  {
    assertThrows (NullPointerException.class, () -> OffsetDate.parse ((String) null));
  }

  // -----------------------------------------------------------------------
  // parse(DateTimeFormatter)
  // -----------------------------------------------------------------------
  @Test
  public void factory_parse_formatter ()
  {
    final DateTimeFormatter f = DateTimeFormatter.ofPattern ("y M d XXX");
    final OffsetDate test = OffsetDate.parse ("2010 12 3 +01:00", f);
    assertEquals (OffsetDate.of (LocalDate.of (2010, 12, 3), ZoneOffset.ofHours (1)), test);
  }

  @Test
  public void factory_parse_formatter_nullText ()
  {
    final DateTimeFormatter f = DateTimeFormatter.ofPattern ("y M d");
    assertThrows (NullPointerException.class, () -> OffsetDate.parse ((String) null, f));
  }

  @Test
  public void factory_parse_formatter_nullFormatter ()
  {
    assertThrows (NullPointerException.class, () -> OffsetDate.parse ("ANY", null));
  }

  // -----------------------------------------------------------------------
  // constructor
  // -----------------------------------------------------------------------
  @Test
  public void constructor_nullDate () throws Throwable
  {
    final Constructor <OffsetDate> con = OffsetDate.class.getDeclaredConstructor (LocalDate.class, ZoneOffset.class);
    con.setAccessible (true);
    final InvocationTargetException thrown = assertThrows (InvocationTargetException.class,
                                                           () -> con.newInstance (null, OFFSET_PONE));
    assertTrue (thrown.getCause () instanceof NullPointerException);
  }

  @Test
  public void constructor_nullOffset () throws Throwable
  {
    final Constructor <OffsetDate> con = OffsetDate.class.getDeclaredConstructor (LocalDate.class, ZoneOffset.class);
    con.setAccessible (true);
    final InvocationTargetException thrown = assertThrows (InvocationTargetException.class,
                                                           () -> con.newInstance (LocalDate.of (2008, 6, 30), null));
    assertTrue (thrown.getCause () instanceof NullPointerException);
  }

  // -----------------------------------------------------------------------
  // basics
  // -----------------------------------------------------------------------
  static interface ISample
  {
    void accept (final int y, final int m, final int d, final ZoneOffset offset);
  }

  private static final void data_sampleDates (@Nonnull final ISample aSample)
  {
    aSample.accept (2008, 7, 5, OFFSET_PTWO);
    aSample.accept (2007, 7, 5, OFFSET_PONE);
    aSample.accept (2006, 7, 5, OFFSET_PTWO);
    aSample.accept (2005, 7, 5, OFFSET_PONE);
    aSample.accept (2004, 1, 1, OFFSET_PTWO);
    aSample.accept (-1, 1, 2, OFFSET_PONE);
    aSample.accept (999999, 11, 20, ZoneOffset.ofHoursMinutesSeconds (6, 9, 12));
  }

  public void test_get_OffsetDate ()
  {
    data_sampleDates ( (y, m, d, offset) -> {
      final LocalDate localDate = LocalDate.of (y, m, d);
      final OffsetDate a = OffsetDate.of (localDate, offset);

      assertEquals (localDate, a.toLocalDate ());
      assertEquals (offset, a.getOffset ());
      assertEquals (localDate.toString () + offset.toString (), a.getAsString ());
      assertEquals (localDate.getYear (), a.getYear ());
      assertEquals (localDate.getMonth (), a.getMonth ());
      assertEquals (localDate.getDayOfMonth (), a.getDayOfMonth ());
      assertEquals (localDate.getDayOfYear (), a.getDayOfYear ());
      assertEquals (localDate.getDayOfWeek (), a.getDayOfWeek ());
    });
  }

  // -----------------------------------------------------------------------
  // isSupported(TemporalUnit)
  // -----------------------------------------------------------------------
  @Test
  public void test_isSupported_TemporalUnit ()
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

  // -----------------------------------------------------------------------
  // get(TemporalField)
  // -----------------------------------------------------------------------
  @Test
  public void test_get_TemporalField ()
  {
    final OffsetDate test = OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    assertEquals (2008, test.get (ChronoField.YEAR));
    assertEquals (6, test.get (ChronoField.MONTH_OF_YEAR));
    assertEquals (30, test.get (ChronoField.DAY_OF_MONTH));
    assertEquals (1, test.get (ChronoField.DAY_OF_WEEK));
    assertEquals (182, test.get (ChronoField.DAY_OF_YEAR));

    assertEquals (3600, test.get (ChronoField.OFFSET_SECONDS));
  }

  @Test
  public void test_getLong_TemporalField ()
  {
    final OffsetDate test = OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    assertEquals (2008, test.getLong (ChronoField.YEAR));
    assertEquals (6, test.getLong (ChronoField.MONTH_OF_YEAR));
    assertEquals (30, test.getLong (ChronoField.DAY_OF_MONTH));
    assertEquals (1, test.getLong (ChronoField.DAY_OF_WEEK));
    assertEquals (182, test.getLong (ChronoField.DAY_OF_YEAR));

    assertEquals (3600, test.getLong (ChronoField.OFFSET_SECONDS));
  }

  // -----------------------------------------------------------------------
  // query(TemporalQuery)
  // -----------------------------------------------------------------------
  @Test
  public void test_query_chrono ()
  {
    assertEquals (IsoChronology.INSTANCE, TEST_2007_07_15_PONE.query (TemporalQueries.chronology ()));
    assertEquals (IsoChronology.INSTANCE, TemporalQueries.chronology ().queryFrom (TEST_2007_07_15_PONE));
  }

  @Test
  public void test_query_zoneId ()
  {
    assertNull (TEST_2007_07_15_PONE.query (TemporalQueries.zoneId ()));
    assertNull (TemporalQueries.zoneId ().queryFrom (TEST_2007_07_15_PONE));
  }

  @Test
  public void test_query_precision ()
  {
    assertEquals (ChronoUnit.DAYS, TEST_2007_07_15_PONE.query (TemporalQueries.precision ()));
    assertEquals (ChronoUnit.DAYS, TemporalQueries.precision ().queryFrom (TEST_2007_07_15_PONE));
  }

  @Test
  public void test_query_offset ()
  {
    assertEquals (OFFSET_PONE, TEST_2007_07_15_PONE.query (TemporalQueries.offset ()));
    assertEquals (OFFSET_PONE, TemporalQueries.offset ().queryFrom (TEST_2007_07_15_PONE));
  }

  @Test
  public void test_query_zone ()
  {
    assertEquals (OFFSET_PONE, TEST_2007_07_15_PONE.query (TemporalQueries.zone ()));
    assertEquals (OFFSET_PONE, TemporalQueries.zone ().queryFrom (TEST_2007_07_15_PONE));
  }

  @Test
  public void test_query_null ()
  {
    assertThrows (NullPointerException.class, () -> TEST_2007_07_15_PONE.query (null));
  }

  // -----------------------------------------------------------------------
  // adjustInto(Temporal)
  // -----------------------------------------------------------------------
  @Test
  public void test_adjustInto ()
  {
    final OffsetDateTime odt = OffsetDateTime.of (2007, 12, 3, 10, 15, 30, 0, ZoneOffset.UTC);
    final OffsetDate od = OffsetDate.of (2008, 1, 4, OFFSET_PONE);
    final OffsetDateTime expected = OffsetDateTime.of (2008, 1, 4, 10, 15, 30, 0, OFFSET_PONE);
    assertEquals (expected, od.adjustInto (odt));
  }

  // -----------------------------------------------------------------------
  // until(Temporal, TemporalUnit)
  // -----------------------------------------------------------------------
  private static interface IUntil
  {
    void accept (final long expected, final OffsetDate od1, final OffsetDate od2, final TemporalUnit unit);
  }

  private static void data_until (@Nonnull final IUntil aSample)
  {
    aSample.accept (1,
                    OffsetDate.of (2007, 6, 30, OFFSET_PONE),
                    OffsetDate.of (2007, 7, 1, OFFSET_PONE),
                    ChronoUnit.DAYS);
    aSample.accept (1,
                    OffsetDate.of (2007, 6, 30, OFFSET_PONE),
                    OffsetDate.of (2007, 8, 29, OFFSET_PONE),
                    ChronoUnit.MONTHS);
    aSample.accept (2,
                    OffsetDate.of (2007, 6, 30, OFFSET_PONE),
                    OffsetDate.of (2007, 8, 30, OFFSET_PONE),
                    ChronoUnit.MONTHS);
    aSample.accept (2,
                    OffsetDate.of (2007, 6, 30, OFFSET_PONE),
                    OffsetDate.of (2007, 8, 31, OFFSET_PONE),
                    ChronoUnit.MONTHS);
  }

  public void test_until ()
  {
    data_until ( (expected, od1, od2, unit) -> {
      assertEquals (expected, od1.until (od2, unit));
      assertEquals (-expected, od2.until (od1, unit));
    });
  }

  @Test
  public void test_until_otherType ()
  {
    final OffsetDate start = OffsetDate.of (2007, 6, 30, OFFSET_PONE);
    final Temporal end = OffsetDateTime.of (2007, 8, 31, 12, 0, 0, 0, OFFSET_PONE);
    assertEquals (2, start.until (end, ChronoUnit.MONTHS));
  }

  @Test
  public void test_until_invalidType ()
  {
    final OffsetDate od1 = OffsetDate.of (2012, 6, 30, OFFSET_PONE);
    assertThrows (DateTimeException.class, () -> od1.until (Instant.ofEpochSecond (7), ChronoUnit.SECONDS));
  }

  // -----------------------------------------------------------------------
  // withOffsetSameLocal()
  // -----------------------------------------------------------------------
  @Test
  public void test_withOffsetSameLocal ()
  {
    final OffsetDate base = OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    final OffsetDate test = base.withOffsetSameLocal (OFFSET_PTWO);
    assertEquals (base.toLocalDate (), test.toLocalDate ());
    assertEquals (OFFSET_PTWO, test.getOffset ());
  }

  @Test
  public void test_withOffsetSameLocal_noChange ()
  {
    final OffsetDate base = OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    final OffsetDate test = base.withOffsetSameLocal (OFFSET_PONE);
    assertEquals (base, test);
  }

  @Test
  public void test_withOffsetSameLocal_null ()
  {
    assertThrows (NullPointerException.class, () -> TEST_2007_07_15_PONE.withOffsetSameLocal (null));
  }

  // -----------------------------------------------------------------------
  // with(WithAdjuster)
  // -----------------------------------------------------------------------
  @Test
  public void test_with_adjustment ()
  {
    final OffsetDate sample = OffsetDate.of (LocalDate.of (2012, 3, 4), OFFSET_PONE);
    final TemporalAdjuster adjuster = dateTime -> sample;
    assertEquals (sample, TEST_2007_07_15_PONE.with (adjuster));
  }

  @Test
  public void test_with_adjustment_LocalDate ()
  {
    final OffsetDate test = TEST_2007_07_15_PONE.with (LocalDate.of (2008, 6, 30));
    assertEquals (OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE), test);
  }

  @Test
  public void test_with_adjustment_OffsetDate ()
  {
    final OffsetDate test = TEST_2007_07_15_PONE.with (OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PTWO));
    assertEquals (OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PTWO), test);
  }

  @Test
  public void test_with_adjustment_ZoneOffset ()
  {
    final OffsetDate test = TEST_2007_07_15_PONE.with (OFFSET_PTWO);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 7, 15), OFFSET_PTWO), test);
  }

  @Test
  public void test_with_adjustment_Month ()
  {
    final OffsetDate test = TEST_2007_07_15_PONE.with (DECEMBER);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 12, 15), OFFSET_PONE), test);
  }

  @Test
  public void test_with_adjustment_offsetUnchanged ()
  {
    final OffsetDate base = OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    final OffsetDate test = base.with (Year.of (2008));
    assertEquals (base, test);
  }

  @Test
  public void test_with_adjustment_noChange ()
  {
    final LocalDate date = LocalDate.of (2008, 6, 30);
    final OffsetDate base = OffsetDate.of (date, OFFSET_PONE);
    final OffsetDate test = base.with (date);
    assertEquals (base, test);
  }

  @Test
  public void test_with_adjustment_null ()
  {
    assertThrows (NullPointerException.class, () -> TEST_2007_07_15_PONE.with ((TemporalAdjuster) null));
  }

  // -----------------------------------------------------------------------
  // with(TemporalField, long)
  // -----------------------------------------------------------------------
  @Test
  public void test_with_TemporalField ()
  {
    final OffsetDate test = OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    assertEquals (OffsetDate.of (LocalDate.of (2009, 6, 30), OFFSET_PONE), test.with (ChronoField.YEAR, 2009));
    assertEquals (OffsetDate.of (LocalDate.of (2008, 7, 30), OFFSET_PONE), test.with (ChronoField.MONTH_OF_YEAR, 7));
    assertEquals (OffsetDate.of (LocalDate.of (2008, 6, 1), OFFSET_PONE), test.with (ChronoField.DAY_OF_MONTH, 1));
    assertEquals (OffsetDate.of (LocalDate.of (2008, 7, 1), OFFSET_PONE), test.with (ChronoField.DAY_OF_WEEK, 2));
    assertEquals (OffsetDate.of (LocalDate.of (2008, 7, 1), OFFSET_PONE), test.with (ChronoField.DAY_OF_YEAR, 183));

    assertEquals (OffsetDate.of (LocalDate.of (2008, 6, 30), ZoneOffset.ofHoursMinutesSeconds (2, 0, 5)),
                  test.with (ChronoField.OFFSET_SECONDS, 7205));
  }

  @Test
  public void test_with_TemporalField_null ()
  {
    assertThrows (NullPointerException.class, () -> TEST_2007_07_15_PONE.with ((TemporalField) null, 0));
  }

  @Test
  public void test_with_TemporalField_invalidField ()
  {
    assertThrows (DateTimeException.class, () -> TEST_2007_07_15_PONE.with (ChronoField.AMPM_OF_DAY, 0));
  }

  // -----------------------------------------------------------------------
  // withYear()
  // -----------------------------------------------------------------------
  @Test
  public void test_withYear_int_normal ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.withYear (2008);
    assertEquals (OffsetDate.of (LocalDate.of (2008, 7, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_withYear_int_noChange ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.withYear (2007);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_withYear_int_invalid ()
  {
    assertThrows (DateTimeException.class, () -> TEST_2007_07_15_PONE.withYear (Year.MIN_VALUE - 1));
  }

  @Test
  public void test_withYear_int_adjustDay ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (2008, 2, 29), OFFSET_PONE).withYear (2007);
    final OffsetDate expected = OffsetDate.of (LocalDate.of (2007, 2, 28), OFFSET_PONE);
    assertEquals (expected, t);
  }

  // -----------------------------------------------------------------------
  // withMonth()
  // -----------------------------------------------------------------------
  @Test
  public void test_withMonth_int_normal ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.withMonth (1);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 1, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_withMonth_int_noChange ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.withMonth (7);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_withMonth_int_invalid ()
  {
    assertThrows (DateTimeException.class, () -> TEST_2007_07_15_PONE.withMonth (13));
  }

  @Test
  public void test_withMonth_int_adjustDay ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (2007, 12, 31), OFFSET_PONE).withMonth (11);
    final OffsetDate expected = OffsetDate.of (LocalDate.of (2007, 11, 30), OFFSET_PONE);
    assertEquals (expected, t);
  }

  // -----------------------------------------------------------------------
  // withDayOfMonth()
  // -----------------------------------------------------------------------
  @Test
  public void test_withDayOfMonth_normal ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.withDayOfMonth (1);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 7, 1), OFFSET_PONE), t);
  }

  @Test
  public void test_withDayOfMonth_noChange ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.withDayOfMonth (15);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 7, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_withDayOfMonth_invalidForMonth ()
  {
    assertThrows (DateTimeException.class,
                  () -> OffsetDate.of (LocalDate.of (2007, 11, 30), OFFSET_PONE).withDayOfMonth (31));
  }

  @Test
  public void test_withDayOfMonth_invalidAlways ()
  {
    assertThrows (DateTimeException.class,
                  () -> OffsetDate.of (LocalDate.of (2007, 11, 30), OFFSET_PONE).withDayOfMonth (32));
  }

  // -----------------------------------------------------------------------
  // withDayOfYear(int)
  // -----------------------------------------------------------------------
  @Test
  public void test_withDayOfYear_normal ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.withDayOfYear (33);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 2, 2), OFFSET_PONE), t);
  }

  @Test
  public void test_withDayOfYear_noChange ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.withDayOfYear (31 + 28 + 31 + 30 + 31 + 30 + 15);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_withDayOfYear_illegal ()
  {
    assertThrows (DateTimeException.class, () -> TEST_2007_07_15_PONE.withDayOfYear (367));
  }

  @Test
  public void test_withDayOfYear_invalid ()
  {
    assertThrows (DateTimeException.class, () -> TEST_2007_07_15_PONE.withDayOfYear (366));
  }

  // -----------------------------------------------------------------------
  // plus(PlusAdjuster)
  // -----------------------------------------------------------------------
  @Test
  public void test_plus_PlusAdjuster ()
  {
    final Period period = Period.ofMonths (7);
    final OffsetDate t = TEST_2007_07_15_PONE.plus (period);
    assertEquals (OffsetDate.of (LocalDate.of (2008, 2, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_plus_PlusAdjuster_noChange ()
  {
    final Period period = Period.ofMonths (0);
    final OffsetDate t = TEST_2007_07_15_PONE.plus (period);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_plus_PlusAdjuster_zero ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plus (Period.ZERO);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_plus_PlusAdjuster_null ()
  {
    assertThrows (NullPointerException.class, () -> TEST_2007_07_15_PONE.plus ((TemporalAmount) null));
  }

  // -----------------------------------------------------------------------
  // plusYears()
  // -----------------------------------------------------------------------
  @Test
  public void test_plusYears_long_normal ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusYears (1);
    assertEquals (OffsetDate.of (LocalDate.of (2008, 7, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_plusYears_long_negative ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusYears (-1);
    assertEquals (OffsetDate.of (LocalDate.of (2006, 7, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_plusYears_long_noChange ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusYears (0);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_plusYears_long_adjustDay ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (2008, 2, 29), OFFSET_PONE).plusYears (1);
    final OffsetDate expected = OffsetDate.of (LocalDate.of (2009, 2, 28), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void test_plusYears_long_big ()
  {
    final long years = 20L + Year.MAX_VALUE;
    final OffsetDate test = OffsetDate.of (LocalDate.of (-40, 6, 1), OFFSET_PONE).plusYears (years);
    assertEquals (OffsetDate.of (LocalDate.of ((int) (-40L + years), 6, 1), OFFSET_PONE), test);
  }

  @Test
  public void test_plusYears_long_invalidTooLarge ()
  {
    assertThrows (DateTimeException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 1, 1), OFFSET_PONE).plusYears (1));
  }

  @Test
  public void test_plusYears_long_invalidTooLargeMaxAddMax ()
  {
    final OffsetDate test = OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE);
    assertThrows (DateTimeException.class, () -> test.plusYears (Long.MAX_VALUE));
  }

  @Test
  public void test_plusYears_long_invalidTooLargeMaxAddMin ()
  {
    final OffsetDate test = OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE);
    assertThrows (DateTimeException.class, () -> test.plusYears (Long.MIN_VALUE));
  }

  @Test
  public void test_plusYears_long_invalidTooSmall ()
  {
    assertThrows (DateTimeException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE).plusYears (-1));
  }

  // -----------------------------------------------------------------------
  // plusMonths()
  // -----------------------------------------------------------------------
  @Test
  public void test_plusMonths_long_normal ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusMonths (1);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 8, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_plusMonths_long_overYears ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusMonths (25);
    assertEquals (OffsetDate.of (LocalDate.of (2009, 8, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_plusMonths_long_negative ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusMonths (-1);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 6, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_plusMonths_long_negativeAcrossYear ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusMonths (-7);
    assertEquals (OffsetDate.of (LocalDate.of (2006, 12, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_plusMonths_long_negativeOverYears ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusMonths (-31);
    assertEquals (OffsetDate.of (LocalDate.of (2004, 12, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_plusMonths_long_noChange ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusMonths (0);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_plusMonths_long_adjustDayFromLeapYear ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (2008, 2, 29), OFFSET_PONE).plusMonths (12);
    final OffsetDate expected = OffsetDate.of (LocalDate.of (2009, 2, 28), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void test_plusMonths_long_adjustDayFromMonthLength ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (2007, 3, 31), OFFSET_PONE).plusMonths (1);
    final OffsetDate expected = OffsetDate.of (LocalDate.of (2007, 4, 30), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void test_plusMonths_long_big ()
  {
    final long months = 20L + Integer.MAX_VALUE;
    final OffsetDate test = OffsetDate.of (LocalDate.of (-40, 6, 1), OFFSET_PONE).plusMonths (months);
    assertEquals (OffsetDate.of (LocalDate.of ((int) (-40L + months / 12), 6 + (int) (months % 12), 1), OFFSET_PONE),
                  test);
  }

  @Test
  public void test_plusMonths_long_invalidTooLarge ()
  {
    assertThrows (DateTimeException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE).plusMonths (1));
  }

  @Test
  public void test_plusMonths_long_invalidTooLargeMaxAddMax ()
  {
    final OffsetDate test = OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE);
    assertThrows (DateTimeException.class, () -> test.plusMonths (Long.MAX_VALUE));
  }

  @Test
  public void test_plusMonths_long_invalidTooLargeMaxAddMin ()
  {
    final OffsetDate test = OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE);
    assertThrows (DateTimeException.class, () -> test.plusMonths (Long.MIN_VALUE));
  }

  @Test
  public void test_plusMonths_long_invalidTooSmall ()
  {
    assertThrows (DateTimeException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE).plusMonths (-1));
  }

  // -----------------------------------------------------------------------
  // plusWeeks()
  // -----------------------------------------------------------------------

  public void test_plusWeeks_symmetry ()
  {
    for (final OffsetDate reference : new OffsetDate [] { OffsetDate.of (LocalDate.of (-1, 1, 1), OFFSET_PONE),
                                                          OffsetDate.of (LocalDate.of (-1, 2, 28), OFFSET_PTWO),
                                                          OffsetDate.of (LocalDate.of (-1, 3, 1), OFFSET_PONE),
                                                          OffsetDate.of (LocalDate.of (-1, 12, 31), OFFSET_PTWO),
                                                          OffsetDate.of (LocalDate.of (0, 1, 1), OFFSET_PONE),
                                                          OffsetDate.of (LocalDate.of (0, 2, 28), OFFSET_PTWO),
                                                          OffsetDate.of (LocalDate.of (0, 2, 29), OFFSET_PTWO),
                                                          OffsetDate.of (LocalDate.of (0, 3, 1), OFFSET_PONE),
                                                          OffsetDate.of (LocalDate.of (0, 12, 31), OFFSET_PTWO),
                                                          OffsetDate.of (LocalDate.of (2007, 1, 1), OFFSET_PONE),
                                                          OffsetDate.of (LocalDate.of (2007, 2, 28), OFFSET_PTWO),
                                                          OffsetDate.of (LocalDate.of (2007, 3, 1), OFFSET_PONE),
                                                          OffsetDate.of (LocalDate.of (2007, 12, 31), OFFSET_PTWO),
                                                          OffsetDate.of (LocalDate.of (2008, 1, 1), OFFSET_PONE),
                                                          OffsetDate.of (LocalDate.of (2008, 2, 28), OFFSET_PTWO),
                                                          OffsetDate.of (LocalDate.of (2008, 2, 29), OFFSET_PTWO),
                                                          OffsetDate.of (LocalDate.of (2008, 3, 1), OFFSET_PONE),
                                                          OffsetDate.of (LocalDate.of (2008, 12, 31), OFFSET_PTWO),
                                                          OffsetDate.of (LocalDate.of (2099, 1, 1), OFFSET_PONE),
                                                          OffsetDate.of (LocalDate.of (2099, 2, 28), OFFSET_PTWO),
                                                          OffsetDate.of (LocalDate.of (2099, 3, 1), OFFSET_PONE),
                                                          OffsetDate.of (LocalDate.of (2099, 12, 31), OFFSET_PTWO),
                                                          OffsetDate.of (LocalDate.of (2100, 1, 1), OFFSET_PONE),
                                                          OffsetDate.of (LocalDate.of (2100, 2, 28), OFFSET_PTWO),
                                                          OffsetDate.of (LocalDate.of (2100, 3, 1), OFFSET_PONE),
                                                          OffsetDate.of (LocalDate.of (2100, 12, 31), OFFSET_PTWO) })
    {
      for (int weeks = 0; weeks < 365 * 8; weeks++)
      {
        OffsetDate t = reference.plusWeeks (weeks).plusWeeks (-weeks);
        assertEquals (reference, t);

        t = reference.plusWeeks (-weeks).plusWeeks (weeks);
        assertEquals (reference, t);
      }
    }
  }

  @Test
  public void test_plusWeeks_normal ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusWeeks (1);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 7, 22), OFFSET_PONE), t);
  }

  @Test
  public void test_plusWeeks_overMonths ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusWeeks (9);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 9, 16), OFFSET_PONE), t);
  }

  @Test
  public void test_plusWeeks_overYears ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (2006, 7, 16), OFFSET_PONE).plusWeeks (52);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_plusWeeks_overLeapYears ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusYears (-1).plusWeeks (104);
    assertEquals (OffsetDate.of (LocalDate.of (2008, 7, 12), OFFSET_PONE), t);
  }

  @Test
  public void test_plusWeeks_negative ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusWeeks (-1);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 7, 8), OFFSET_PONE), t);
  }

  @Test
  public void test_plusWeeks_negativeAcrossYear ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusWeeks (-28);
    assertEquals (OffsetDate.of (LocalDate.of (2006, 12, 31), OFFSET_PONE), t);
  }

  @Test
  public void test_plusWeeks_negativeOverYears ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusWeeks (-104);
    assertEquals (OffsetDate.of (LocalDate.of (2005, 7, 17), OFFSET_PONE), t);
  }

  @Test
  public void test_plusWeeks_noChange ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusWeeks (0);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_plusWeeks_maximum ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 24), OFFSET_PONE).plusWeeks (1);
    final OffsetDate expected = OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 31), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void test_plusWeeks_minimum ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 8), OFFSET_PONE).plusWeeks (-1);
    final OffsetDate expected = OffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void test_plusWeeks_invalidTooLarge ()
  {
    assertThrows (DateTimeException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 25), OFFSET_PONE).plusWeeks (1));
  }

  @Test
  public void test_plusWeeks_invalidTooSmall ()
  {
    assertThrows (DateTimeException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 7), OFFSET_PONE).plusWeeks (-1));
  }

  @Test
  public void test_plusWeeks_invalidMaxMinusMax ()
  {
    assertThrows (ArithmeticException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 25), OFFSET_PONE).plusWeeks (Long.MAX_VALUE));
  }

  @Test
  public void test_plusWeeks_invalidMaxMinusMin ()
  {
    assertThrows (ArithmeticException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 25), OFFSET_PONE).plusWeeks (Long.MIN_VALUE));
  }

  // -----------------------------------------------------------------------
  // plusDays()
  // -----------------------------------------------------------------------
  private static final OffsetDate [] data_samplePlusDaysSymmetry;
  static
  {
    data_samplePlusDaysSymmetry = new OffsetDate [] { OffsetDate.of (LocalDate.of (-1, 1, 1), OFFSET_PONE),
                                                      OffsetDate.of (LocalDate.of (-1, 2, 28), OFFSET_PTWO),
                                                      OffsetDate.of (LocalDate.of (-1, 3, 1), OFFSET_PONE),
                                                      OffsetDate.of (LocalDate.of (-1, 12, 31), OFFSET_PTWO),
                                                      OffsetDate.of (LocalDate.of (0, 1, 1), OFFSET_PONE),
                                                      OffsetDate.of (LocalDate.of (0, 2, 28), OFFSET_PTWO),
                                                      OffsetDate.of (LocalDate.of (0, 2, 29), OFFSET_PTWO),
                                                      OffsetDate.of (LocalDate.of (0, 3, 1), OFFSET_PONE),
                                                      OffsetDate.of (LocalDate.of (0, 12, 31), OFFSET_PTWO),
                                                      OffsetDate.of (LocalDate.of (2007, 1, 1), OFFSET_PONE),
                                                      OffsetDate.of (LocalDate.of (2007, 2, 28), OFFSET_PTWO),
                                                      OffsetDate.of (LocalDate.of (2007, 3, 1), OFFSET_PONE),
                                                      OffsetDate.of (LocalDate.of (2007, 12, 31), OFFSET_PTWO),
                                                      OffsetDate.of (LocalDate.of (2008, 1, 1), OFFSET_PONE),
                                                      OffsetDate.of (LocalDate.of (2008, 2, 28), OFFSET_PTWO),
                                                      OffsetDate.of (LocalDate.of (2008, 2, 29), OFFSET_PTWO),
                                                      OffsetDate.of (LocalDate.of (2008, 3, 1), OFFSET_PONE),
                                                      OffsetDate.of (LocalDate.of (2008, 12, 31), OFFSET_PTWO),
                                                      OffsetDate.of (LocalDate.of (2099, 1, 1), OFFSET_PONE),
                                                      OffsetDate.of (LocalDate.of (2099, 2, 28), OFFSET_PTWO),
                                                      OffsetDate.of (LocalDate.of (2099, 3, 1), OFFSET_PONE),
                                                      OffsetDate.of (LocalDate.of (2099, 12, 31), OFFSET_PTWO),
                                                      OffsetDate.of (LocalDate.of (2100, 1, 1), OFFSET_PONE),
                                                      OffsetDate.of (LocalDate.of (2100, 2, 28), OFFSET_PTWO),
                                                      OffsetDate.of (LocalDate.of (2100, 3, 1), OFFSET_PONE),
                                                      OffsetDate.of (LocalDate.of (2100, 12, 31), OFFSET_PTWO) };
  }

  public void test_plusDays_symmetry ()
  {
    for (final OffsetDate reference : data_samplePlusDaysSymmetry)
      for (int days = 0; days < 365 * 8; days++)
      {
        OffsetDate t = reference.plusDays (days).plusDays (-days);
        assertEquals (reference, t);

        t = reference.plusDays (-days).plusDays (days);
        assertEquals (reference, t);
      }
  }

  @Test
  public void test_plusDays_normal ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusDays (1);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 7, 16), OFFSET_PONE), t);
  }

  @Test
  public void test_plusDays_overMonths ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusDays (62);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 9, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_plusDays_overYears ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (2006, 7, 14), OFFSET_PONE).plusDays (366);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_plusDays_overLeapYears ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusYears (-1).plusDays (365 + 366);
    assertEquals (OffsetDate.of (LocalDate.of (2008, 7, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_plusDays_negative ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusDays (-1);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 7, 14), OFFSET_PONE), t);
  }

  @Test
  public void test_plusDays_negativeAcrossYear ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusDays (-196);
    assertEquals (OffsetDate.of (LocalDate.of (2006, 12, 31), OFFSET_PONE), t);
  }

  @Test
  public void test_plusDays_negativeOverYears ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusDays (-730);
    assertEquals (OffsetDate.of (LocalDate.of (2005, 7, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_plusDays_noChange ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusDays (0);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_plusDays_maximum ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 30), OFFSET_PONE).plusDays (1);
    final OffsetDate expected = OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 31), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void test_plusDays_minimum ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 2), OFFSET_PONE).plusDays (-1);
    final OffsetDate expected = OffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void test_plusDays_invalidTooLarge ()
  {
    assertThrows (DateTimeException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 31), OFFSET_PONE).plusDays (1));
  }

  @Test
  public void test_plusDays_invalidTooSmall ()
  {
    assertThrows (DateTimeException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE).plusDays (-1));
  }

  @Test
  public void test_plusDays_overflowTooLarge ()
  {
    assertThrows (ArithmeticException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 31), OFFSET_PONE).plusDays (Long.MAX_VALUE));
  }

  @Test
  public void test_plusDays_overflowTooSmall ()
  {
    assertThrows (ArithmeticException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE).plusDays (Long.MIN_VALUE));
  }

  // -----------------------------------------------------------------------
  // minus(MinusAdjuster)
  // -----------------------------------------------------------------------
  @Test
  public void test_minus_MinusAdjuster ()
  {
    final Period period = Period.ofMonths (7);
    final OffsetDate t = TEST_2007_07_15_PONE.minus (period);
    assertEquals (OffsetDate.of (LocalDate.of (2006, 12, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_minus_MinusAdjuster_noChange ()
  {
    final Period period = Period.ofMonths (0);
    final OffsetDate t = TEST_2007_07_15_PONE.minus (period);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_minus_MinusAdjuster_zero ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minus (Period.ZERO);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_plus_MinusAdjuster_null ()
  {
    assertThrows (NullPointerException.class, () -> TEST_2007_07_15_PONE.minus ((TemporalAmount) null));
  }

  // -----------------------------------------------------------------------
  // minusYears()
  // -----------------------------------------------------------------------
  @Test
  public void test_minusYears_long_normal ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusYears (1);
    assertEquals (OffsetDate.of (LocalDate.of (2006, 7, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_minusYears_long_negative ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusYears (-1);
    assertEquals (OffsetDate.of (LocalDate.of (2008, 7, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_minusYears_long_noChange ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusYears (0);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_minusYears_long_adjustDay ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (2008, 2, 29), OFFSET_PONE).minusYears (1);
    final OffsetDate expected = OffsetDate.of (LocalDate.of (2007, 2, 28), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void test_minusYears_long_big ()
  {
    final long years = 20L + Year.MAX_VALUE;
    final OffsetDate test = OffsetDate.of (LocalDate.of (40, 6, 1), OFFSET_PONE).minusYears (years);
    assertEquals (OffsetDate.of (LocalDate.of ((int) (40L - years), 6, 1), OFFSET_PONE), test);
  }

  @Test
  public void test_minusYears_long_invalidTooLarge ()
  {
    assertThrows (DateTimeException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 1, 1), OFFSET_PONE).minusYears (-1));
  }

  @Test
  public void test_minusYears_long_invalidTooLargeMaxAddMax ()
  {
    final OffsetDate test = OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE);
    assertThrows (DateTimeException.class, () -> test.minusYears (Long.MAX_VALUE));
  }

  @Test
  public void test_minusYears_long_invalidTooLargeMaxAddMin ()
  {
    final OffsetDate test = OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE);
    assertThrows (DateTimeException.class, () -> test.minusYears (Long.MIN_VALUE));
  }

  @Test
  public void test_minusYears_long_invalidTooSmall ()
  {
    assertThrows (DateTimeException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE).minusYears (1));
  }

  // -----------------------------------------------------------------------
  // minusMonths()
  // -----------------------------------------------------------------------
  @Test
  public void test_minusMonths_long_normal ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusMonths (1);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 6, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_minusMonths_long_overYears ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusMonths (25);
    assertEquals (OffsetDate.of (LocalDate.of (2005, 6, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_minusMonths_long_negative ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusMonths (-1);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 8, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_minusMonths_long_negativeAcrossYear ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusMonths (-7);
    assertEquals (OffsetDate.of (LocalDate.of (2008, 2, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_minusMonths_long_negativeOverYears ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusMonths (-31);
    assertEquals (OffsetDate.of (LocalDate.of (2010, 2, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_minusMonths_long_noChange ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusMonths (0);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_minusMonths_long_adjustDayFromLeapYear ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (2008, 2, 29), OFFSET_PONE).minusMonths (12);
    final OffsetDate expected = OffsetDate.of (LocalDate.of (2007, 2, 28), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void test_minusMonths_long_adjustDayFromMonthLength ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (2007, 3, 31), OFFSET_PONE).minusMonths (1);
    final OffsetDate expected = OffsetDate.of (LocalDate.of (2007, 2, 28), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void test_minusMonths_long_big ()
  {
    final long months = 20L + Integer.MAX_VALUE;
    final OffsetDate test = OffsetDate.of (LocalDate.of (40, 6, 1), OFFSET_PONE).minusMonths (months);
    assertEquals (OffsetDate.of (LocalDate.of ((int) (40L - months / 12), 6 - (int) (months % 12), 1), OFFSET_PONE),
                  test);
  }

  @Test
  public void test_minusMonths_long_invalidTooLarge ()
  {
    assertThrows (DateTimeException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE).minusMonths (-1));
  }

  @Test
  public void test_minusMonths_long_invalidTooLargeMaxAddMax ()
  {
    final OffsetDate test = OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE);
    assertThrows (DateTimeException.class, () -> test.minusMonths (Long.MAX_VALUE));
  }

  @Test
  public void test_minusMonths_long_invalidTooLargeMaxAddMin ()
  {
    final OffsetDate test = OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 1), OFFSET_PONE);
    assertThrows (DateTimeException.class, () -> test.minusMonths (Long.MIN_VALUE));
  }

  @Test
  public void test_minusMonths_long_invalidTooSmall ()
  {
    assertThrows (DateTimeException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE).minusMonths (1));
  }

  // -----------------------------------------------------------------------
  // minusWeeks()
  // -----------------------------------------------------------------------
  private static final OffsetDate [] data_sampleMinusWeeksSymmetry;
  static
  {
    data_sampleMinusWeeksSymmetry = new OffsetDate [] { OffsetDate.of (LocalDate.of (-1, 1, 1), OFFSET_PONE),
                                                        OffsetDate.of (LocalDate.of (-1, 2, 28), OFFSET_PTWO),
                                                        OffsetDate.of (LocalDate.of (-1, 3, 1), OFFSET_PONE),
                                                        OffsetDate.of (LocalDate.of (-1, 12, 31), OFFSET_PTWO),
                                                        OffsetDate.of (LocalDate.of (0, 1, 1), OFFSET_PONE),
                                                        OffsetDate.of (LocalDate.of (0, 2, 28), OFFSET_PTWO),
                                                        OffsetDate.of (LocalDate.of (0, 2, 29), OFFSET_PTWO),
                                                        OffsetDate.of (LocalDate.of (0, 3, 1), OFFSET_PONE),
                                                        OffsetDate.of (LocalDate.of (0, 12, 31), OFFSET_PTWO),
                                                        OffsetDate.of (LocalDate.of (2007, 1, 1), OFFSET_PONE),
                                                        OffsetDate.of (LocalDate.of (2007, 2, 28), OFFSET_PTWO),
                                                        OffsetDate.of (LocalDate.of (2007, 3, 1), OFFSET_PONE),
                                                        OffsetDate.of (LocalDate.of (2007, 12, 31), OFFSET_PTWO),
                                                        OffsetDate.of (LocalDate.of (2008, 1, 1), OFFSET_PONE),
                                                        OffsetDate.of (LocalDate.of (2008, 2, 28), OFFSET_PTWO),
                                                        OffsetDate.of (LocalDate.of (2008, 2, 29), OFFSET_PTWO),
                                                        OffsetDate.of (LocalDate.of (2008, 3, 1), OFFSET_PONE),
                                                        OffsetDate.of (LocalDate.of (2008, 12, 31), OFFSET_PTWO),
                                                        OffsetDate.of (LocalDate.of (2099, 1, 1), OFFSET_PONE),
                                                        OffsetDate.of (LocalDate.of (2099, 2, 28), OFFSET_PTWO),
                                                        OffsetDate.of (LocalDate.of (2099, 3, 1), OFFSET_PONE),
                                                        OffsetDate.of (LocalDate.of (2099, 12, 31), OFFSET_PTWO),
                                                        OffsetDate.of (LocalDate.of (2100, 1, 1), OFFSET_PONE),
                                                        OffsetDate.of (LocalDate.of (2100, 2, 28), OFFSET_PTWO),
                                                        OffsetDate.of (LocalDate.of (2100, 3, 1), OFFSET_PONE),
                                                        OffsetDate.of (LocalDate.of (2100, 12, 31), OFFSET_PTWO) };
  }

  public void test_minusWeeks_symmetry ()
  {
    for (final OffsetDate reference : data_sampleMinusWeeksSymmetry)
      for (int weeks = 0; weeks < 365 * 8; weeks++)
      {
        OffsetDate t = reference.minusWeeks (weeks).minusWeeks (-weeks);
        assertEquals (reference, t);

        t = reference.minusWeeks (-weeks).minusWeeks (weeks);
        assertEquals (reference, t);
      }
  }

  @Test
  public void test_minusWeeks_normal ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusWeeks (1);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 7, 8), OFFSET_PONE), t);
  }

  @Test
  public void test_minusWeeks_overMonths ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusWeeks (9);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 5, 13), OFFSET_PONE), t);
  }

  @Test
  public void test_minusWeeks_overYears ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (2008, 7, 13), OFFSET_PONE).minusWeeks (52);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_minusWeeks_overLeapYears ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusYears (-1).minusWeeks (104);
    assertEquals (OffsetDate.of (LocalDate.of (2006, 7, 18), OFFSET_PONE), t);
  }

  @Test
  public void test_minusWeeks_negative ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusWeeks (-1);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 7, 22), OFFSET_PONE), t);
  }

  @Test
  public void test_minusWeeks_negativeAcrossYear ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusWeeks (-28);
    assertEquals (OffsetDate.of (LocalDate.of (2008, 1, 27), OFFSET_PONE), t);
  }

  @Test
  public void test_minusWeeks_negativeOverYears ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusWeeks (-104);
    assertEquals (OffsetDate.of (LocalDate.of (2009, 7, 12), OFFSET_PONE), t);
  }

  @Test
  public void test_minusWeeks_noChange ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusWeeks (0);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_minusWeeks_maximum ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 24), OFFSET_PONE).minusWeeks (-1);
    final OffsetDate expected = OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 31), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void test_minusWeeks_minimum ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 8), OFFSET_PONE).minusWeeks (1);
    final OffsetDate expected = OffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void test_minusWeeks_invalidTooLarge ()
  {
    assertThrows (DateTimeException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 25), OFFSET_PONE).minusWeeks (-1));
  }

  @Test
  public void test_minusWeeks_invalidTooSmall ()
  {
    assertThrows (DateTimeException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 7), OFFSET_PONE).minusWeeks (1));
  }

  @Test
  public void test_minusWeeks_invalidMaxMinusMax ()
  {
    assertThrows (ArithmeticException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 25), OFFSET_PONE).minusWeeks (Long.MAX_VALUE));
  }

  @Test
  public void test_minusWeeks_invalidMaxMinusMin ()
  {
    assertThrows (ArithmeticException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 25), OFFSET_PONE).minusWeeks (Long.MIN_VALUE));
  }

  // -----------------------------------------------------------------------
  // minusDays()
  // -----------------------------------------------------------------------
  private static final OffsetDate [] data_sampleMinusDaysSymmetry;
  static
  {
    data_sampleMinusDaysSymmetry = new OffsetDate [] { OffsetDate.of (LocalDate.of (-1, 1, 1), OFFSET_PONE),
                                                       OffsetDate.of (LocalDate.of (-1, 2, 28), OFFSET_PTWO),
                                                       OffsetDate.of (LocalDate.of (-1, 3, 1), OFFSET_PONE),
                                                       OffsetDate.of (LocalDate.of (-1, 12, 31), OFFSET_PTWO),
                                                       OffsetDate.of (LocalDate.of (0, 1, 1), OFFSET_PONE),
                                                       OffsetDate.of (LocalDate.of (0, 2, 28), OFFSET_PTWO),
                                                       OffsetDate.of (LocalDate.of (0, 2, 29), OFFSET_PTWO),
                                                       OffsetDate.of (LocalDate.of (0, 3, 1), OFFSET_PONE),
                                                       OffsetDate.of (LocalDate.of (0, 12, 31), OFFSET_PTWO),
                                                       OffsetDate.of (LocalDate.of (2007, 1, 1), OFFSET_PONE),
                                                       OffsetDate.of (LocalDate.of (2007, 2, 28), OFFSET_PTWO),
                                                       OffsetDate.of (LocalDate.of (2007, 3, 1), OFFSET_PONE),
                                                       OffsetDate.of (LocalDate.of (2007, 12, 31), OFFSET_PTWO),
                                                       OffsetDate.of (LocalDate.of (2008, 1, 1), OFFSET_PONE),
                                                       OffsetDate.of (LocalDate.of (2008, 2, 28), OFFSET_PTWO),
                                                       OffsetDate.of (LocalDate.of (2008, 2, 29), OFFSET_PTWO),
                                                       OffsetDate.of (LocalDate.of (2008, 3, 1), OFFSET_PONE),
                                                       OffsetDate.of (LocalDate.of (2008, 12, 31), OFFSET_PTWO),
                                                       OffsetDate.of (LocalDate.of (2099, 1, 1), OFFSET_PONE),
                                                       OffsetDate.of (LocalDate.of (2099, 2, 28), OFFSET_PTWO),
                                                       OffsetDate.of (LocalDate.of (2099, 3, 1), OFFSET_PONE),
                                                       OffsetDate.of (LocalDate.of (2099, 12, 31), OFFSET_PTWO),
                                                       OffsetDate.of (LocalDate.of (2100, 1, 1), OFFSET_PONE),
                                                       OffsetDate.of (LocalDate.of (2100, 2, 28), OFFSET_PTWO),
                                                       OffsetDate.of (LocalDate.of (2100, 3, 1), OFFSET_PONE),
                                                       OffsetDate.of (LocalDate.of (2100, 12, 31), OFFSET_PTWO) };
  }

  public void test_minusDays_symmetry ()
  {
    for (final OffsetDate reference : data_sampleMinusDaysSymmetry)
      for (int days = 0; days < 365 * 8; days++)
      {
        OffsetDate t = reference.minusDays (days).minusDays (-days);
        assertEquals (reference, t);

        t = reference.minusDays (-days).minusDays (days);
        assertEquals (reference, t);
      }
  }

  @Test
  public void test_minusDays_normal ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusDays (1);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 7, 14), OFFSET_PONE), t);
  }

  @Test
  public void test_minusDays_overMonths ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusDays (62);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 5, 14), OFFSET_PONE), t);
  }

  @Test
  public void test_minusDays_overYears ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (2008, 7, 16), OFFSET_PONE).minusDays (367);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_minusDays_overLeapYears ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.plusYears (2).minusDays (365 + 366);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_minusDays_negative ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusDays (-1);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 7, 16), OFFSET_PONE), t);
  }

  @Test
  public void test_minusDays_negativeAcrossYear ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusDays (-169);
    assertEquals (OffsetDate.of (LocalDate.of (2007, 12, 31), OFFSET_PONE), t);
  }

  @Test
  public void test_minusDays_negativeOverYears ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusDays (-731);
    assertEquals (OffsetDate.of (LocalDate.of (2009, 7, 15), OFFSET_PONE), t);
  }

  @Test
  public void test_minusDays_noChange ()
  {
    final OffsetDate t = TEST_2007_07_15_PONE.minusDays (0);
    assertEquals (TEST_2007_07_15_PONE, t);
  }

  @Test
  public void test_minusDays_maximum ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 30), OFFSET_PONE).minusDays (-1);
    final OffsetDate expected = OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 31), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void test_minusDays_minimum ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 2), OFFSET_PONE).minusDays (1);
    final OffsetDate expected = OffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE);
    assertEquals (expected, t);
  }

  @Test
  public void test_minusDays_invalidTooLarge ()
  {
    assertThrows (DateTimeException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 31), OFFSET_PONE).minusDays (-1));
  }

  @Test
  public void test_minusDays_invalidTooSmall ()
  {
    assertThrows (DateTimeException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE).minusDays (1));
  }

  @Test
  public void test_minusDays_overflowTooLarge ()
  {
    assertThrows (ArithmeticException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MAX_VALUE, 12, 31), OFFSET_PONE).minusDays (Long.MIN_VALUE));
  }

  @Test
  public void test_minusDays_overflowTooSmall ()
  {
    assertThrows (ArithmeticException.class,
                  () -> OffsetDate.of (LocalDate.of (Year.MIN_VALUE, 1, 1), OFFSET_PONE).minusDays (Long.MAX_VALUE));
  }

  // -----------------------------------------------------------------------
  // format(DateTimeFormatter)
  // -----------------------------------------------------------------------
  @Test
  public void test_format_formatter ()
  {
    final DateTimeFormatter f = DateTimeFormatter.ofPattern ("y M d");
    final String t = OffsetDate.of (LocalDate.of (2010, 12, 3), OFFSET_PONE).format (f);
    assertEquals ("2010 12 3", t);
  }

  @Test
  public void test_format_formatter_null ()
  {
    assertThrows (NullPointerException.class,
                  () -> OffsetDate.of (LocalDate.of (2010, 12, 3), OFFSET_PONE).format (null));
  }

  // -----------------------------------------------------------------------
  // atTime()
  // -----------------------------------------------------------------------
  @Test
  public void test_atTime_Local ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PTWO);
    assertEquals (OffsetDateTime.of (LocalDate.of (2008, 6, 30), LocalTime.of (11, 30), OFFSET_PTWO),
                  t.atTime (LocalTime.of (11, 30)));
  }

  @Test
  public void test_atTime_Local_nullLocalTime ()
  {
    final OffsetDate t = OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PTWO);
    assertThrows (NullPointerException.class, () -> t.atTime ((LocalTime) null));
  }

  // -----------------------------------------------------------------------
  // toLocalDate()
  // -----------------------------------------------------------------------
  public void test_toLocalDate ()
  {
    data_sampleDates ( (y, m, d, offset) -> {
      final LocalDate t = LocalDate.of (y, m, d);
      assertEquals (t, OffsetDate.of (LocalDate.of (y, m, d), offset).toLocalDate ());
    });
  }

  // -----------------------------------------------------------------------
  // toEpochSecond(LocalTime)
  // -----------------------------------------------------------------------
  @Test
  public void test_toEpochSecond ()
  {
    final OffsetDate od = OffsetDate.of (1970, 1, 1, ZoneOffset.UTC);
    assertEquals (0, od.toEpochSecond (LocalTime.MIDNIGHT));
    assertEquals (12 * 60 * 60, od.toEpochSecond (LocalTime.MIDNIGHT.plusSeconds (12 * 60 * 60)));
  }

  // -----------------------------------------------------------------------
  // compareTo()
  // -----------------------------------------------------------------------
  @Test
  public void test_compareTo_date ()
  {
    // a is before b due to date
    final OffsetDate a = OffsetDate.of (LocalDate.of (2008, 6, 29), OFFSET_PONE);
    final OffsetDate b = OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);

    assertTrue (a.compareTo (b) < 0);
    assertTrue (b.compareTo (a) > 0);
    assertTrue (a.compareTo (a) == 0);
    assertTrue (b.compareTo (b) == 0);
    assertTrue (a.atTime (LocalTime.MIDNIGHT).toInstant ().compareTo (b.atTime (LocalTime.MIDNIGHT).toInstant ()) < 0);
  }

  @Test
  public void test_compareTo_offset ()
  {
    // a is before b due to offset
    final OffsetDate a = OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PTWO);
    final OffsetDate b = OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);

    assertTrue (a.compareTo (b) < 0);
    assertTrue (b.compareTo (a) > 0);
    assertTrue (a.compareTo (a) == 0);
    assertTrue (b.compareTo (b) == 0);
    assertTrue (a.atTime (LocalTime.MIDNIGHT).toInstant ().compareTo (b.atTime (LocalTime.MIDNIGHT).toInstant ()) < 0);
  }

  @Test
  public void test_compareTo_both ()
  {
    // a is before b due in instant scale
    final OffsetDate a = OffsetDate.of (LocalDate.of (2008, 6, 29), OFFSET_PTWO);
    final OffsetDate b = OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);

    assertTrue (a.compareTo (b) < 0);
    assertTrue (b.compareTo (a) > 0);
    assertTrue (a.compareTo (a) == 0);
    assertTrue (b.compareTo (b) == 0);
    assertTrue (a.atTime (LocalTime.MIDNIGHT).toInstant ().compareTo (b.atTime (LocalTime.MIDNIGHT).toInstant ()) < 0);
  }

  @Test
  public void test_compareTo_24hourDifference ()
  {
    // a is before b despite being same time-line time
    final OffsetDate a = OffsetDate.of (LocalDate.of (2008, 6, 29), ZoneOffset.ofHours (-12));
    final OffsetDate b = OffsetDate.of (LocalDate.of (2008, 6, 30), ZoneOffset.ofHours (12));

    assertTrue (a.compareTo (b) < 0);
    assertTrue (b.compareTo (a) > 0);
    assertTrue (a.compareTo (a) == 0);
    assertTrue (b.compareTo (b) == 0);
    assertTrue (a.atTime (LocalTime.MIDNIGHT).toInstant ().compareTo (b.atTime (LocalTime.MIDNIGHT).toInstant ()) == 0);
  }

  @Test
  public void test_compareTo_null ()
  {
    final OffsetDate a = OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    assertThrows (NullPointerException.class, () -> a.compareTo (null));
  }

  @Test
  @SuppressWarnings ({ "unchecked", "rawtypes" })
  public void compareToNonOffsetDate ()
  {
    final Comparable c = TEST_2007_07_15_PONE;
    assertThrows (ClassCastException.class, () -> c.compareTo (new Object ()));
  }

  // -----------------------------------------------------------------------
  // isAfter() / isBefore() / isEqual()
  // -----------------------------------------------------------------------
  @Test
  public void test_isBeforeIsAfterIsEqual1 ()
  {
    // a is before b due to time
    final OffsetDate a = OffsetDate.of (LocalDate.of (2008, 6, 29), OFFSET_PONE);
    final OffsetDate b = OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);

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
  public void test_isBeforeIsAfterIsEqual2 ()
  {
    // a is before b due to offset
    final OffsetDate a = OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PTWO);
    final OffsetDate b = OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);

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
  public void test_isBeforeIsAfterIsEqual_instantComparison ()
  {
    // a is same instant as b
    final OffsetDate a = OffsetDate.of (LocalDate.of (2008, 6, 30), ZoneOffset.ofHours (12));
    final OffsetDate b = OffsetDate.of (LocalDate.of (2008, 6, 29), ZoneOffset.ofHours (-12));

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
  public void test_isBefore_null ()
  {
    final OffsetDate a = OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    assertThrows (NullPointerException.class, () -> a.isBefore (null));
  }

  @Test
  public void test_isAfter_null ()
  {
    final OffsetDate a = OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    assertThrows (NullPointerException.class, () -> a.isAfter (null));
  }

  @Test
  public void test_isEqual_null ()
  {
    final OffsetDate a = OffsetDate.of (LocalDate.of (2008, 6, 30), OFFSET_PONE);
    assertThrows (NullPointerException.class, () -> a.isEqual (null));
  }

  // -----------------------------------------------------------------------
  // equals() / hashCode()
  // -----------------------------------------------------------------------
  public void test_equals_true ()
  {
    data_sampleDates ( (y, m, d, offset) -> {
      final OffsetDate a = OffsetDate.of (LocalDate.of (y, m, d), offset);
      final OffsetDate b = OffsetDate.of (LocalDate.of (y, m, d), offset);
      assertTrue (a.equals (b));
      assertTrue (a.hashCode () == b.hashCode ());
    });
  }

  public void test_equals_false_year_differs ()
  {
    data_sampleDates ( (y, m, d, offset) -> {
      final OffsetDate a = OffsetDate.of (LocalDate.of (y, m, d), offset);
      final OffsetDate b = OffsetDate.of (LocalDate.of (y + 1, m, d), offset);
      assertFalse (a.equals (b));
    });
  }

  public void test_equals_false_month_differs ()
  {
    data_sampleDates ( (y, m, d, offset) -> {
      final OffsetDate a = OffsetDate.of (LocalDate.of (y, m, d), offset);
      final OffsetDate b = OffsetDate.of (LocalDate.of (y, m + 1, d), offset);
      assertFalse (a.equals (b));
    });
  }

  public void test_equals_false_day_differs ()
  {
    data_sampleDates ( (y, m, d, offset) -> {
      final OffsetDate a = OffsetDate.of (LocalDate.of (y, m, d), offset);
      final OffsetDate b = OffsetDate.of (LocalDate.of (y, m, d + 1), offset);
      assertFalse (a.equals (b));
    });
  }

  public void test_equals_false_offset_differs ()
  {
    data_sampleDates ( (y, m, d, offset) -> {
      final OffsetDate a = OffsetDate.of (LocalDate.of (y, m, d), OFFSET_PONE);
      final OffsetDate b = OffsetDate.of (LocalDate.of (y, m, d), OFFSET_PTWO);
      assertFalse (a.equals (b));
    });
  }

  @Test
  public void test_equals_itself_true ()
  {
    assertTrue (TEST_2007_07_15_PONE.equals (TEST_2007_07_15_PONE));
  }

  @Test
  public void test_equals_string_false ()
  {
    assertFalse (TEST_2007_07_15_PONE.equals ("2007-07-15"));
  }

  // -----------------------------------------------------------------------
  // toString()
  // -----------------------------------------------------------------------

  public void test_toString ()
  {
    data_sampleToString ( (y, m, d, offsetId, expected) -> {
      final OffsetDate t = OffsetDate.of (LocalDate.of (y, m, d), ZoneOffset.of (offsetId));
      final String str = t.getAsString ();
      assertEquals (expected, str);
    });
  }

}
