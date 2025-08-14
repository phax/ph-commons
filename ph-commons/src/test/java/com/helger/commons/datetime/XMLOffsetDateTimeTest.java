/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.commons.datetime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

import org.junit.Test;

import com.helger.base.CGlobal;
import com.helger.base.typeconvert.TypeConverterException;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.typeconvert.TypeConverter;

/**
 * Test class for class {@link XMLOffsetDateTime}
 *
 * @author Philip Helger
 */
public final class XMLOffsetDateTimeTest
{
  @Test
  public void testBasic ()
  {
    final LocalDate aLD = PDTFactory.createLocalDate (2020, Month.JANUARY, 2);
    final LocalTime aLT = PDTFactory.createLocalTime (19, 57, 12);

    // No timezone
    XMLOffsetDateTime aDT = XMLOffsetDateTime.of (aLD, aLT, null);
    assertEquals ("2020-01-02T19:57:12", aDT.toString ());
    assertNull (aDT.getOffset ());
    assertFalse (aDT.hasOffset ());
    assertEquals (aLD, aDT.toLocalDate ());
    assertEquals (aLT, aDT.toLocalTime ());
    assertEquals (aLD.atTime (aLT), aDT.toLocalDateTime ());

    assertEquals (0, aDT.compareTo (aDT));
    assertEquals (-1, aDT.compareTo (aDT.plusSeconds (1)));
    assertEquals (+1, aDT.compareTo (aDT.minusSeconds (1)));
    assertEquals (0, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.UTC)));
    assertEquals (-1, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (2))));
    assertEquals (+1, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (-2))));

    // UTC
    aDT = XMLOffsetDateTime.of (aLD, aLT, ZoneOffset.UTC);
    assertEquals ("2020-01-02T19:57:12Z", aDT.toString ());
    assertSame (ZoneOffset.UTC, aDT.getOffset ());
    assertTrue (aDT.hasOffset ());
    assertEquals (aLD, aDT.toLocalDate ());
    assertEquals (aLT, aDT.toLocalTime ());
    assertEquals (aLD.atTime (aLT), aDT.toLocalDateTime ());

    assertEquals (0, aDT.compareTo (aDT));
    assertEquals (-1, aDT.compareTo (aDT.plusSeconds (1)));
    assertEquals (+1, aDT.compareTo (aDT.minusSeconds (1)));
    assertEquals (0, aDT.compareTo (aDT.withOffsetSameInstant (null)));
    assertEquals (-1, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (2))));
    assertEquals (+1, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (-2))));

    // +2 hours
    final ZoneOffset aZO = ZoneOffset.ofHours (2);
    aDT = XMLOffsetDateTime.of (aLD, aLT, aZO);
    assertEquals ("2020-01-02T19:57:12+02:00", aDT.toString ());
    assertEquals (aZO, aDT.getOffset ());
    assertEquals (aLD, aDT.toLocalDate ());
    assertEquals (aLT, aDT.toLocalTime ());
    assertEquals (aLD.atTime (aLT), aDT.toLocalDateTime ());

    assertEquals (0, aDT.compareTo (aDT));
    assertEquals (-1, aDT.compareTo (aDT.plusSeconds (1)));
    assertEquals (+1, aDT.compareTo (aDT.minusSeconds (1)));
    assertEquals (+1, aDT.compareTo (aDT.withOffsetSameInstant (null)));
    assertEquals (0, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (2))));
    assertEquals (+1, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (-2))));
  }

  @Test
  public void testSerialization ()
  {
    final XMLOffsetDateTime aObj = PDTFactory.getCurrentXMLOffsetDateTime ();
    CommonsTestHelper.testDefaultSerialization (aObj);
  }

  @Test
  public void testConvert ()
  {
    XMLOffsetDateTime aDT = TypeConverter.convert ("2021-05-10T13:09:16.015", XMLOffsetDateTime.class);
    assertNotNull (aDT);
    assertNull (aDT.getOffset ());
    assertFalse (aDT.hasOffset ());
    assertEquals (PDTFactory.createLocalDate (2021, Month.MAY, 10), aDT.toLocalDate ());
    assertEquals (PDTFactory.createLocalTime (13, 9, 16).with (ChronoField.MILLI_OF_SECOND, 15), aDT.toLocalTime ());

    aDT = TypeConverter.convert ("2021-05-10T13:09:16.015Z", XMLOffsetDateTime.class);
    assertNotNull (aDT);
    assertNotNull (aDT.getOffset ());
    assertTrue (aDT.hasOffset ());
    assertEquals (0, aDT.getOffset ().getTotalSeconds ());
    assertEquals (PDTFactory.createLocalDate (2021, Month.MAY, 10), aDT.toLocalDate ());
    assertEquals (PDTFactory.createLocalTime (13, 9, 16).with (ChronoField.MILLI_OF_SECOND, 15), aDT.toLocalTime ());

    aDT = TypeConverter.convert ("2021-05-10T13:09:16.015+01:00", XMLOffsetDateTime.class);
    assertNotNull (aDT);
    assertNotNull (aDT.getOffset ());
    assertTrue (aDT.hasOffset ());
    assertEquals (1 * CGlobal.SECONDS_PER_HOUR, aDT.getOffset ().getTotalSeconds ());
    assertEquals (PDTFactory.createLocalDate (2021, Month.MAY, 10), aDT.toLocalDate ());
    assertEquals (PDTFactory.createLocalTime (13, 9, 16).with (ChronoField.MILLI_OF_SECOND, 15), aDT.toLocalTime ());

    try
    {
      TypeConverter.convert ("2021-05-10T13:09:16.015+01", XMLOffsetDateTime.class);
      fail ();
    }
    catch (final TypeConverterException ex)
    {}
  }
}
