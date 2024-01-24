/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.jaxb.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;

import org.junit.Test;

import com.helger.commons.datetime.PDTFactory;

/**
 * Test class for class {@link AdapterZonedDateTime}.
 *
 * @author Philip Helger
 */
public final class AdapterZonedDateTimeTest
{
  @Test
  public void testUnmarshal ()
  {
    final AdapterZonedDateTime a = new AdapterZonedDateTime ();
    assertNull (a.unmarshal (null));
    assertNull (a.unmarshal (""));
    assertNull (a.unmarshal (" "));
    assertNull (a.unmarshal ("a"));
    assertNull (a.unmarshal ("a2020-01-01"));
    assertNull (a.unmarshal ("2020-01-01a"));
    assertNull (a.unmarshal ("2020-02-31"));
    assertNull (a.unmarshal ("2020- 01-01"));
    assertNull (a.unmarshal ("2020- 01 -01"));
    assertNull (a.unmarshal ("2020-01-01"));
    assertNull (a.unmarshal ("2020-01-01 T10:12:45.654Z"));
    assertNull (a.unmarshal ("2020-01-01T 10:12:45.654Z"));
    assertNull (a.marshal (null));

    ZonedDateTime o = PDTFactory.createZonedDateTimeUTC (2020, Month.JANUARY, 1, 10, 12, 45).with (ChronoField.MILLI_OF_SECOND, 654);
    assertEquals (o, a.unmarshal ("2020-01-01T10:12:45.654"));
    assertEquals (o, a.unmarshal (" 2020-01-01T10:12:45.654"));
    assertEquals (o, a.unmarshal ("2020-01-01T10:12:45.654 "));
    assertEquals (o, a.unmarshal (" 2020-01-01T10:12:45.654 "));

    assertEquals (o, a.unmarshal ("2020-01-01T10:12:45.654Z"));
    assertEquals (o, a.unmarshal (" 2020-01-01T10:12:45.654Z"));
    assertEquals (o, a.unmarshal ("2020-01-01T10:12:45.654Z "));
    assertEquals (o, a.unmarshal (" 2020-01-01T10:12:45.654Z "));

    assertEquals (o, a.unmarshal ("2020-01-01T10:12:45.654+00:00"));
    assertEquals (o, a.unmarshal (" 2020-01-01T10:12:45.654+00:00"));
    assertEquals (o, a.unmarshal ("2020-01-01T10:12:45.654+00:00 "));
    assertEquals (o, a.unmarshal (" 2020-01-01T10:12:45.654+00:00 "));

    o = PDTFactory.createZonedDateTime (2020, Month.JANUARY, 1, 10, 12, 45)
                  .withZoneSameLocal (ZoneOffset.ofHours (1))
                  .with (ChronoField.MILLI_OF_SECOND, 654);
    assertEquals (o, a.unmarshal ("2020-01-01T10:12:45.654+01:00"));
    assertEquals (o, a.unmarshal (" 2020-01-01T10:12:45.654+01:00"));
    assertEquals (o, a.unmarshal ("2020-01-01T10:12:45.654+01:00 "));
    assertEquals (o, a.unmarshal (" 2020-01-01T10:12:45.654+01:00 "));
  }
}
