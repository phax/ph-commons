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
package com.helger.jaxb.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.Month;
import java.time.ZoneOffset;

import org.junit.Test;

import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.datetime.XMLOffsetDate;

/**
 * Test class for class {@link AdapterXMLOffsetDate}.
 *
 * @author Philip Helger
 */
public final class AdapterXMLOffsetDateTest
{
  @Test
  public void testUnmarshal ()
  {
    final AdapterXMLOffsetDate a = new AdapterXMLOffsetDate ();
    assertNull (a.unmarshal (null));
    assertNull (a.unmarshal (""));
    assertNull (a.unmarshal (" "));
    assertNull (a.unmarshal ("a"));
    assertNull (a.unmarshal ("a2020-01-01"));
    assertNull (a.unmarshal ("2020-01-01a"));
    assertNull (a.unmarshal ("2020-02-31"));
    assertNull (a.unmarshal ("2020- 01-01"));
    assertNull (a.unmarshal ("2020- 01 -01"));
    assertNull (a.unmarshal ("2020-01-01 Z"));
    assertNull (a.marshal (null));

    assertNotNull (a.unmarshal ("2020-01-01"));
    assertNotNull (a.unmarshal ("2020-01-01Z"));
    assertNotNull (a.unmarshal ("2020-01-01+01:00"));
    assertNotNull (a.unmarshal ("2020-01-01-01:00"));

    XMLOffsetDate o = PDTFactory.createXMLOffsetDate (2020, Month.JANUARY, 1, ZoneOffset.UTC);
    assertEquals (ZoneOffset.UTC, o.getOffset ());
    assertEquals ("2020-01-01Z", a.marshal (o));

    assertEquals (o, a.unmarshal ("2020-01-01Z"));
    assertEquals (o, a.unmarshal (" 2020-01-01Z"));
    assertEquals (o, a.unmarshal ("2020-01-01Z "));
    assertEquals (o, a.unmarshal (" 2020-01-01Z "));

    assertEquals (o, a.unmarshal ("2020-01-01+00:00"));
    assertEquals (o, a.unmarshal (" 2020-01-01+00:00"));
    assertEquals (o, a.unmarshal ("2020-01-01+00:00 "));
    assertEquals (o, a.unmarshal (" 2020-01-01+00:00 "));

    o = PDTFactory.createXMLOffsetDate (2020, Month.JANUARY, 1, null);
    assertNull (o.getOffset ());
    assertEquals ("2020-01-01", a.marshal (o));

    assertEquals (o, a.unmarshal ("2020-01-01"));
    assertEquals (o, a.unmarshal (" 2020-01-01"));
    assertEquals (o, a.unmarshal ("2020-01-01 "));
    assertEquals (o, a.unmarshal (" 2020-01-01 "));

    o = PDTFactory.createXMLOffsetDate (2020, Month.JANUARY, 1, ZoneOffset.ofHours (1));
    assertEquals (ZoneOffset.ofHours (1), o.getOffset ());
    assertEquals ("2020-01-01+01:00", a.marshal (o));

    assertEquals (o, a.unmarshal ("2020-01-01+01:00"));
    assertEquals (o, a.unmarshal (" 2020-01-01+01:00"));
    assertEquals (o, a.unmarshal ("2020-01-01+01:00 "));
    assertEquals (o, a.unmarshal (" 2020-01-01+01:00 "));
    assertEquals ("2020-01-01+01:00", a.marshal (o));
  }
}
