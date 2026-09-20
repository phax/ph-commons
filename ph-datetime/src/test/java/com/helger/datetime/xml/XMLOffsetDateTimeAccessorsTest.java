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
package com.helger.datetime.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalQueries;

import org.junit.Test;

/**
 * Additional test class for class {@link XMLOffsetDateTime}, covering the accessors, the
 * <code>with*</code>, <code>plus*</code> and <code>minus*</code> methods and the conversions.
 *
 * @author Philip Helger
 */
public final class XMLOffsetDateTimeAccessorsTest
{
  private static final ZoneOffset OFS = ZoneOffset.ofHours (2);
  private static final LocalDateTime LDT = LocalDateTime.of (2020, Month.JANUARY, 2, 19, 57, 12, 345000000);
  private static final XMLOffsetDateTime WITH_OFS = XMLOffsetDateTime.of (LDT, OFS);
  private static final XMLOffsetDateTime NO_OFS = XMLOffsetDateTime.of (LDT, null);

  @Test
  public void testFactories ()
  {
    assertNotNull (XMLOffsetDateTime.now ());
    assertNotNull (XMLOffsetDateTime.now (ZoneId.of ("UTC")));
    assertNotNull (XMLOffsetDateTime.now (Clock.systemUTC ()));
    assertNotNull (XMLOffsetDateTime.MIN);
    assertNotNull (XMLOffsetDateTime.MAX);

    assertEquals (LDT, XMLOffsetDateTime.of (LDT).toLocalDateTime ());
    assertEquals (LDT, XMLOffsetDateTime.of (LDT.toLocalDate (), LDT.toLocalTime ()).toLocalDateTime ());
    assertEquals (LDT, XMLOffsetDateTime.of (LDT.toLocalDate (), LDT.toLocalTime (), OFS).toLocalDateTime ());
    assertEquals (WITH_OFS, XMLOffsetDateTime.of (OffsetDateTime.of (LDT, OFS)));
    assertEquals (WITH_OFS, XMLOffsetDateTime.of (2020, 1, 2, 19, 57, 12, 345000000, OFS));

    assertNotNull (XMLOffsetDateTime.ofInstant (Instant.now (), ZoneId.of ("UTC")));
    assertEquals (WITH_OFS, XMLOffsetDateTime.from (OffsetDateTime.of (LDT, OFS)));
  }

  @Test
  public void testParseAndFormat ()
  {
    assertEquals (WITH_OFS, XMLOffsetDateTime.parse ("2020-01-02T19:57:12.345+02:00"));
    assertEquals (NO_OFS, XMLOffsetDateTime.parse ("2020-01-02T19:57:12.345"));
    assertEquals (WITH_OFS,
                  XMLOffsetDateTime.parse ("2020-01-02T19:57:12.345+02:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    assertNotNull (WITH_OFS.format (DateTimeFormatter.ISO_OFFSET_DATE_TIME));
  }

  @Test
  public void testDateAndTimeAccessors ()
  {
    assertEquals (2020, WITH_OFS.getYear ());
    assertEquals (1, WITH_OFS.getMonthValue ());
    assertSame (Month.JANUARY, WITH_OFS.getMonth ());
    assertEquals (2, WITH_OFS.getDayOfMonth ());
    assertEquals (2, WITH_OFS.getDayOfYear ());
    assertSame (DayOfWeek.THURSDAY, WITH_OFS.getDayOfWeek ());
    assertEquals (19, WITH_OFS.getHour ());
    assertEquals (57, WITH_OFS.getMinute ());
    assertEquals (12, WITH_OFS.getSecond ());
    assertEquals (345000000, WITH_OFS.getNano ());

    assertEquals (LDT.toLocalDate (), WITH_OFS.toLocalDate ());
    assertEquals (LDT.toLocalTime (), WITH_OFS.toLocalTime ());
    assertEquals (LDT, WITH_OFS.toLocalDateTime ());

    assertSame (OFS, WITH_OFS.getOffset ());
    assertTrue (WITH_OFS.hasOffset ());
    assertNull (NO_OFS.getOffset ());
    assertFalse (NO_OFS.hasOffset ());
  }

  @Test
  public void testTemporalAccess ()
  {
    assertTrue (WITH_OFS.isSupported (ChronoField.YEAR));
    assertFalse (WITH_OFS.isSupported ((java.time.temporal.TemporalField) null));
    assertTrue (WITH_OFS.isSupported (ChronoUnit.DAYS));
    assertFalse (WITH_OFS.isSupported ((java.time.temporal.TemporalUnit) null));
    assertNotNull (WITH_OFS.range (ChronoField.YEAR));
    assertEquals (2020, WITH_OFS.get (ChronoField.YEAR));
    assertEquals (2020L, WITH_OFS.getLong (ChronoField.YEAR));

    assertNotNull (WITH_OFS.query (TemporalQueries.offset ()));
    assertNotNull (WITH_OFS.adjustInto (OffsetDateTime.of (LDT, ZoneOffset.UTC)));
    assertEquals (0L, WITH_OFS.until (WITH_OFS, ChronoUnit.DAYS));
  }

  @Test
  public void testWithOffset ()
  {
    final ZoneOffset aOther = ZoneOffset.ofHours (5);

    final XMLOffsetDateTime aSameLocal = WITH_OFS.withOffsetSameLocal (aOther);
    assertEquals (LDT, aSameLocal.toLocalDateTime ());
    assertSame (aOther, aSameLocal.getOffset ());
    assertSame (WITH_OFS, WITH_OFS.withOffsetSameLocal (OFS));

    final XMLOffsetDateTime aSameInstant = WITH_OFS.withOffsetSameInstant (aOther);
    assertSame (aOther, aSameInstant.getOffset ());
    assertEquals (LDT.plusHours (3), aSameInstant.toLocalDateTime ());
    assertSame (WITH_OFS, WITH_OFS.withOffsetSameInstant (OFS));
  }

  @Test
  public void testWithFields ()
  {
    assertEquals (2021, WITH_OFS.withYear (2021).getYear ());
    assertEquals (2, WITH_OFS.withMonth (2).getMonthValue ());
    assertEquals (3, WITH_OFS.withDayOfMonth (3).getDayOfMonth ());
    assertEquals (5, WITH_OFS.withDayOfYear (5).getDayOfYear ());
    assertEquals (20, WITH_OFS.withHour (20).getHour ());
    assertEquals (58, WITH_OFS.withMinute (58).getMinute ());
    assertEquals (13, WITH_OFS.withSecond (13).getSecond ());
    assertEquals (1000, WITH_OFS.withNano (1000).getNano ());

    assertEquals (2021, WITH_OFS.with (ChronoField.YEAR, 2021).getYear ());
    assertEquals (31, WITH_OFS.with (TemporalAdjusters.lastDayOfMonth ()).getDayOfMonth ());
    assertEquals (0, WITH_OFS.truncatedTo (ChronoUnit.DAYS).getHour ());
  }

  @Test
  public void testPlus ()
  {
    assertEquals (2021, WITH_OFS.plusYears (1).getYear ());
    assertEquals (2, WITH_OFS.plusMonths (1).getMonthValue ());
    assertEquals (9, WITH_OFS.plusWeeks (1).getDayOfMonth ());
    assertEquals (3, WITH_OFS.plusDays (1).getDayOfMonth ());
    assertEquals (20, WITH_OFS.plusHours (1).getHour ());
    assertEquals (58, WITH_OFS.plusMinutes (1).getMinute ());
    assertEquals (13, WITH_OFS.plusSeconds (1).getSecond ());
    assertEquals (345000001, WITH_OFS.plusNanos (1).getNano ());

    assertEquals (2021, WITH_OFS.plus (Period.ofYears (1)).getYear ());
    assertEquals (2021, WITH_OFS.plus (1, ChronoUnit.YEARS).getYear ());
    assertEquals (20, WITH_OFS.plus (Duration.ofHours (1)).getHour ());
  }

  @Test
  public void testMinus ()
  {
    assertEquals (2019, WITH_OFS.minusYears (1).getYear ());
    assertEquals (12, WITH_OFS.minusMonths (1).getMonthValue ());
    assertEquals (26, WITH_OFS.minusWeeks (1).getDayOfMonth ());
    assertEquals (1, WITH_OFS.minusDays (1).getDayOfMonth ());
    assertEquals (18, WITH_OFS.minusHours (1).getHour ());
    assertEquals (56, WITH_OFS.minusMinutes (1).getMinute ());
    assertEquals (11, WITH_OFS.minusSeconds (1).getSecond ());
    assertEquals (344999999, WITH_OFS.minusNanos (1).getNano ());

    assertEquals (2019, WITH_OFS.minus (Period.ofYears (1)).getYear ());
    assertEquals (2019, WITH_OFS.minus (1, ChronoUnit.YEARS).getYear ());
    assertEquals (18, WITH_OFS.minus (Duration.ofHours (1)).getHour ());
  }

  @Test
  public void testConversions ()
  {
    assertEquals (LDT.toLocalTime (), WITH_OFS.toOffsetTime ().toLocalTime ());
    assertEquals (LDT.toLocalTime (), WITH_OFS.toXMLOffsetTime ().toLocalTime ());
    assertEquals (LDT.toLocalDate (), WITH_OFS.toOffsetDate ().toLocalDate ());
    assertEquals (LDT.toLocalDate (), WITH_OFS.toXMLOffsetDate ().toLocalDate ());
    assertEquals (OffsetDateTime.of (LDT, OFS), WITH_OFS.toOffsetDateTime ());
    assertNotNull (WITH_OFS.toZonedDateTime ());
    assertNotNull (WITH_OFS.toInstant ());
    assertNotNull (WITH_OFS.atZoneSameInstant (ZoneId.of ("UTC")));
    assertNotNull (WITH_OFS.atZoneSimilarLocal (ZoneId.of ("UTC")));

    // Without an offset the default offset is used
    assertNotNull (NO_OFS.toOffsetDateTime ());
    assertNotNull (NO_OFS.toInstant ());
    assertEquals (LDT.toEpochSecond (ZoneOffset.UTC), NO_OFS.toEpochSecond ());
    assertEquals (LDT.toEpochSecond (OFS), WITH_OFS.toEpochSecond ());
  }

  @Test
  public void testComparisons ()
  {
    final XMLOffsetDateTime aLater = WITH_OFS.plusDays (1);

    assertTrue (WITH_OFS.compareTo (aLater) < 0);
    assertTrue (aLater.compareTo (WITH_OFS) > 0);
    assertEquals (0, WITH_OFS.compareTo (XMLOffsetDateTime.of (LDT, OFS)));

    assertTrue (WITH_OFS.isBefore (aLater));
    assertFalse (aLater.isBefore (WITH_OFS));
    assertTrue (aLater.isAfter (WITH_OFS));
    assertFalse (WITH_OFS.isAfter (aLater));
    assertTrue (WITH_OFS.isEqual (XMLOffsetDateTime.of (LDT, OFS)));
    assertFalse (WITH_OFS.isEqual (aLater));

    assertNotNull (XMLOffsetDateTime.timeLineOrder ());
    assertTrue (XMLOffsetDateTime.timeLineOrder ().compare (WITH_OFS, aLater) < 0);
  }

  @SuppressWarnings ({ "deprecation", "unlikely-arg-type" })
  @Test
  public void testToString ()
  {
    assertEquals ("2020-01-02T19:57:12.345+02:00", WITH_OFS.toString ());
    assertEquals ("2020-01-02T19:57:12.345", NO_OFS.toString ());
    assertEquals (WITH_OFS.toString (), WITH_OFS.getAsString ());

    assertEquals (WITH_OFS.hashCode (), XMLOffsetDateTime.of (LDT, OFS).hashCode ());
    assertFalse (WITH_OFS.equals (null));
    assertFalse (WITH_OFS.equals ("any other type"));
    assertFalse (WITH_OFS.equals (NO_OFS));
  }
}
