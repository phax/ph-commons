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
import static org.junit.Assert.fail;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

import org.junit.Test;

import com.helger.base.CGlobal;
import com.helger.datetime.helper.PDTFactory;
import com.helger.typeconvert.TypeConverterException;
import com.helger.typeconvert.impl.TypeConverter;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link XMLOffsetTime}
 *
 * @author Philip Helger
 */
public final class XMLOffsetTimeTest
{
  @Test
  public void testBasic ()
  {
    final LocalTime aLT = PDTFactory.createLocalTime (19, 57, 12);

    // No timezone
    XMLOffsetTime aDT = XMLOffsetTime.of (aLT, null);
    assertEquals ("19:57:12", aDT.toString ());
    assertNull (aDT.getOffset ());
    assertFalse (aDT.hasOffset ());
    assertEquals (aLT, aDT.toLocalTime ());

    assertEquals (0, aDT.compareTo (aDT));
    assertEquals (-1, aDT.compareTo (aDT.plusSeconds (1)));
    assertEquals (+1, aDT.compareTo (aDT.minusSeconds (1)));
    assertEquals (0, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.UTC)));
    assertEquals (-1, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (2))));
    assertEquals (+1, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (-2))));

    // UTC
    aDT = XMLOffsetTime.of (aLT, ZoneOffset.UTC);
    assertEquals ("19:57:12Z", aDT.toString ());
    assertSame (ZoneOffset.UTC, aDT.getOffset ());
    assertTrue (aDT.hasOffset ());
    assertEquals (aLT, aDT.toLocalTime ());

    assertEquals (0, aDT.compareTo (aDT));
    assertEquals (-1, aDT.compareTo (aDT.plusSeconds (1)));
    assertEquals (+1, aDT.compareTo (aDT.minusSeconds (1)));
    assertEquals (0, aDT.compareTo (aDT.withOffsetSameInstant (null)));
    assertEquals (-1, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (2))));
    assertEquals (+1, aDT.compareTo (aDT.withOffsetSameInstant (ZoneOffset.ofHours (-2))));

    // +2 hours
    final ZoneOffset aZO = ZoneOffset.ofHours (2);
    aDT = XMLOffsetTime.of (aLT, aZO);
    assertEquals ("19:57:12+02:00", aDT.toString ());
    assertEquals (aZO, aDT.getOffset ());
    assertEquals (aLT, aDT.toLocalTime ());

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
    final XMLOffsetTime aObj = PDTFactory.getCurrentXMLOffsetTime ();
    TestHelper.testDefaultSerialization (aObj);
  }

  @Test
  public void testConvert ()
  {
    XMLOffsetTime aDT = TypeConverter.convert ("13:09:16.015", XMLOffsetTime.class);
    assertNotNull (aDT);
    assertNull (aDT.getOffset ());
    assertFalse (aDT.hasOffset ());
    assertEquals (PDTFactory.createLocalTime (13, 9, 16).with (ChronoField.MILLI_OF_SECOND, 15), aDT.toLocalTime ());

    aDT = TypeConverter.convert ("13:09:16.015Z", XMLOffsetTime.class);
    assertNotNull (aDT);
    assertNotNull (aDT.getOffset ());
    assertTrue (aDT.hasOffset ());
    assertEquals (0, aDT.getOffset ().getTotalSeconds ());
    assertEquals (PDTFactory.createLocalTime (13, 9, 16).with (ChronoField.MILLI_OF_SECOND, 15), aDT.toLocalTime ());

    aDT = TypeConverter.convert ("13:09:16.015+01:00", XMLOffsetTime.class);
    assertNotNull (aDT);
    assertNotNull (aDT.getOffset ());
    assertTrue (aDT.hasOffset ());
    assertEquals (1 * CGlobal.SECONDS_PER_HOUR, aDT.getOffset ().getTotalSeconds ());
    assertEquals (PDTFactory.createLocalTime (13, 9, 16).with (ChronoField.MILLI_OF_SECOND, 15), aDT.toLocalTime ());

    try
    {
      TypeConverter.convert ("13:09:16.015+01", XMLOffsetTime.class);
      fail ();
    }
    catch (final TypeConverterException ex)
    {}
  }
}
