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

import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

import org.junit.Test;

import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.datetime.XMLOffsetTime;

/**
 * Test class for class {@link AdapterXMLOffsetTime}.
 *
 * @author Philip Helger
 */
public final class AdapterXMLOffsetTimeTest
{
  @Test
  public void testUnmarshal ()
  {
    final AdapterXMLOffsetTime a = new AdapterXMLOffsetTime ();
    assertNull (a.unmarshal (null));
    assertNull (a.unmarshal (""));
    assertNull (a.unmarshal (" "));
    assertNull (a.unmarshal ("a"));
    assertNull (a.unmarshal ("a10:12:45.654"));
    assertNull (a.unmarshal ("10:12:45.654a"));
    assertNull (a.unmarshal ("99:12:45.654"));
    assertNull (a.unmarshal ("10: 12:45.654"));
    assertNull (a.unmarshal ("10 :12 :45 .654"));
    assertNull (a.unmarshal ("10:12:45.654 Z"));
    assertNull (a.marshal (null));

    assertNotNull (a.unmarshal ("10:12:45.654"));
    assertNotNull (a.unmarshal ("10:12:45.654Z"));
    assertNotNull (a.unmarshal ("10:12:45.654+01:00"));
    assertNotNull (a.unmarshal ("10:12:45.654-01:00"));

    XMLOffsetTime o = PDTFactory.createXMLOffsetTime (10, 12, 45, ZoneOffset.UTC)
                                .with (ChronoField.MILLI_OF_SECOND, 654);
    assertEquals (ZoneOffset.UTC, o.getOffset ());
    assertEquals ("10:12:45.654Z", a.marshal (o));

    assertEquals (o, a.unmarshal ("10:12:45.654Z"));
    assertEquals (o, a.unmarshal (" 10:12:45.654Z"));
    assertEquals (o, a.unmarshal ("10:12:45.654Z "));
    assertEquals (o, a.unmarshal (" 10:12:45.654Z "));

    assertEquals (o, a.unmarshal ("10:12:45.654+00:00"));
    assertEquals (o, a.unmarshal (" 10:12:45.654+00:00"));
    assertEquals (o, a.unmarshal ("10:12:45.654+00:00 "));
    assertEquals (o, a.unmarshal (" 10:12:45.654+00:00 "));

    o = PDTFactory.createXMLOffsetTime (10, 12, 45).with (ChronoField.MILLI_OF_SECOND, 654);
    assertNull (o.getOffset ());
    assertEquals ("10:12:45.654", a.marshal (o));

    assertEquals (o, a.unmarshal ("10:12:45.654"));
    assertEquals (o, a.unmarshal (" 10:12:45.654"));
    assertEquals (o, a.unmarshal ("10:12:45.654 "));
    assertEquals (o, a.unmarshal (" 10:12:45.654 "));

    o = PDTFactory.createXMLOffsetTime (10, 12, 45)
                  .withOffsetSameLocal (ZoneOffset.ofHours (1))
                  .with (ChronoField.MILLI_OF_SECOND, 654);
    assertEquals (ZoneOffset.ofHours (1), o.getOffset ());
    assertEquals ("10:12:45.654+01:00", a.marshal (o));

    assertEquals (o, a.unmarshal ("10:12:45.654+01:00"));
    assertEquals (o, a.unmarshal (" 10:12:45.654+01:00"));
    assertEquals (o, a.unmarshal ("10:12:45.654+01:00 "));
    assertEquals (o, a.unmarshal (" 10:12:45.654+01:00 "));
  }
}
