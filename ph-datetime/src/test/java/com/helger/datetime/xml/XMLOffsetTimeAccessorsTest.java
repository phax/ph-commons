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
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalQueries;

import org.junit.Test;

/**
 * Additional test class for class {@link XMLOffsetTime}, covering the accessors, the
 * <code>with*</code>, <code>plus*</code> and <code>minus*</code> methods and the conversions.
 *
 * @author Philip Helger
 */
public final class XMLOffsetTimeAccessorsTest
{
  private static final ZoneOffset OFS = ZoneOffset.ofHours (2);
  private static final LocalTime LT = LocalTime.of (19, 57, 12, 345000000);
  private static final XMLOffsetTime WITH_OFS = XMLOffsetTime.of (LT, OFS);
  private static final XMLOffsetTime NO_OFS = XMLOffsetTime.of (LT, null);

  @Test
  public void testFactories ()
  {
    assertNotNull (XMLOffsetTime.now ());
    assertNotNull (XMLOffsetTime.now (ZoneId.of ("UTC")));
    assertNotNull (XMLOffsetTime.now (Clock.systemUTC ()));
    assertNotNull (XMLOffsetTime.MIN);
    assertNotNull (XMLOffsetTime.MAX);

    assertEquals (LT, XMLOffsetTime.of (LT).toLocalTime ());
    assertEquals (WITH_OFS, XMLOffsetTime.of (OffsetTime.of (LT, OFS)));
    assertEquals (WITH_OFS, XMLOffsetTime.of (19, 57, 12, 345000000, OFS));

    assertNotNull (XMLOffsetTime.ofInstant (Instant.now (), ZoneId.of ("UTC")));
    assertEquals (WITH_OFS, XMLOffsetTime.from (OffsetTime.of (LT, OFS)));
  }

  @Test
  public void testParseAndFormat ()
  {
    assertEquals (WITH_OFS, XMLOffsetTime.parse ("19:57:12.345+02:00"));
    assertEquals (NO_OFS, XMLOffsetTime.parse ("19:57:12.345"));
    assertEquals (WITH_OFS, XMLOffsetTime.parse ("19:57:12.345+02:00", DateTimeFormatter.ISO_OFFSET_TIME));
    assertNotNull (WITH_OFS.format (DateTimeFormatter.ISO_OFFSET_TIME));
  }

  @Test
  public void testAccessors ()
  {
    assertEquals (19, WITH_OFS.getHour ());
    assertEquals (57, WITH_OFS.getMinute ());
    assertEquals (12, WITH_OFS.getSecond ());
    assertEquals (345000000, WITH_OFS.getNano ());
    assertEquals (LT, WITH_OFS.toLocalTime ());

    assertSame (OFS, WITH_OFS.getOffset ());
    assertTrue (WITH_OFS.hasOffset ());
    assertNull (NO_OFS.getOffset ());
    assertFalse (NO_OFS.hasOffset ());
  }

  @Test
  public void testTemporalAccess ()
  {
    assertTrue (WITH_OFS.isSupported (ChronoField.HOUR_OF_DAY));
    assertFalse (WITH_OFS.isSupported ((java.time.temporal.TemporalField) null));
    assertTrue (WITH_OFS.isSupported (ChronoUnit.HOURS));
    assertFalse (WITH_OFS.isSupported ((java.time.temporal.TemporalUnit) null));
    assertNotNull (WITH_OFS.range (ChronoField.HOUR_OF_DAY));
    assertEquals (19, WITH_OFS.get (ChronoField.HOUR_OF_DAY));
    assertEquals (19L, WITH_OFS.getLong (ChronoField.HOUR_OF_DAY));

    assertNotNull (WITH_OFS.query (TemporalQueries.offset ()));
    assertNotNull (WITH_OFS.adjustInto (OffsetTime.of (LT, ZoneOffset.UTC)));
    assertEquals (0L, WITH_OFS.until (WITH_OFS, ChronoUnit.HOURS));
  }

  @Test
  public void testWithOffset ()
  {
    final ZoneOffset aOther = ZoneOffset.ofHours (5);

    final XMLOffsetTime aSameLocal = WITH_OFS.withOffsetSameLocal (aOther);
    assertEquals (LT, aSameLocal.toLocalTime ());
    assertSame (aOther, aSameLocal.getOffset ());
    assertSame (WITH_OFS, WITH_OFS.withOffsetSameLocal (OFS));

    final XMLOffsetTime aSameInstant = WITH_OFS.withOffsetSameInstant (aOther);
    assertSame (aOther, aSameInstant.getOffset ());
    assertEquals (LT.plusHours (3), aSameInstant.toLocalTime ());
    assertSame (WITH_OFS, WITH_OFS.withOffsetSameInstant (OFS));
  }

  @Test
  public void testWithFields ()
  {
    assertEquals (20, WITH_OFS.withHour (20).getHour ());
    assertEquals (58, WITH_OFS.withMinute (58).getMinute ());
    assertEquals (13, WITH_OFS.withSecond (13).getSecond ());
    assertEquals (1000, WITH_OFS.withNano (1000).getNano ());

    assertEquals (20, WITH_OFS.with (ChronoField.HOUR_OF_DAY, 20).getHour ());
    assertEquals (0, WITH_OFS.truncatedTo (ChronoUnit.HOURS).getMinute ());
  }

  @Test
  public void testPlusAndMinus ()
  {
    assertEquals (20, WITH_OFS.plusHours (1).getHour ());
    assertEquals (58, WITH_OFS.plusMinutes (1).getMinute ());
    assertEquals (13, WITH_OFS.plusSeconds (1).getSecond ());
    assertEquals (345000001, WITH_OFS.plusNanos (1).getNano ());
    assertEquals (20, WITH_OFS.plus (Duration.ofHours (1)).getHour ());
    assertEquals (20, WITH_OFS.plus (1, ChronoUnit.HOURS).getHour ());

    assertEquals (18, WITH_OFS.minusHours (1).getHour ());
    assertEquals (56, WITH_OFS.minusMinutes (1).getMinute ());
    assertEquals (11, WITH_OFS.minusSeconds (1).getSecond ());
    assertEquals (344999999, WITH_OFS.minusNanos (1).getNano ());
    assertEquals (18, WITH_OFS.minus (Duration.ofHours (1)).getHour ());
    assertEquals (18, WITH_OFS.minus (1, ChronoUnit.HOURS).getHour ());
  }

  @Test
  public void testConversions ()
  {
    final LocalDate aLD = LocalDate.of (2020, 1, 2);
    assertEquals (LT, WITH_OFS.atDate (aLD).toLocalTime ());
    assertEquals (aLD, WITH_OFS.atXMLDate (aLD).toLocalDate ());
    assertEquals (OffsetTime.of (LT, OFS), WITH_OFS.toOffsetTime ());
    // Without an offset the default offset is used
    assertNotNull (NO_OFS.toOffsetTime ());
  }

  @Test
  public void testComparisons ()
  {
    final XMLOffsetTime aLater = WITH_OFS.plusHours (1);

    assertTrue (WITH_OFS.compareTo (aLater) < 0);
    assertTrue (aLater.compareTo (WITH_OFS) > 0);
    assertEquals (0, WITH_OFS.compareTo (XMLOffsetTime.of (LT, OFS)));

    assertTrue (WITH_OFS.isBefore (aLater));
    assertFalse (aLater.isBefore (WITH_OFS));
    assertTrue (aLater.isAfter (WITH_OFS));
    assertFalse (WITH_OFS.isAfter (aLater));
    assertTrue (WITH_OFS.isEqual (XMLOffsetTime.of (LT, OFS)));
    assertFalse (WITH_OFS.isEqual (aLater));
  }

  @SuppressWarnings ({ "deprecation", "unlikely-arg-type" })
  @Test
  public void testToString ()
  {
    assertEquals ("19:57:12.345+02:00", WITH_OFS.toString ());
    assertEquals ("19:57:12.345", NO_OFS.toString ());
    assertEquals (WITH_OFS.toString (), WITH_OFS.getAsString ());

    assertEquals (WITH_OFS.hashCode (), XMLOffsetTime.of (LT, OFS).hashCode ());
    assertFalse (WITH_OFS.equals (null));
    assertFalse (WITH_OFS.equals ("any other type"));
    assertFalse (WITH_OFS.equals (NO_OFS));
  }
}
