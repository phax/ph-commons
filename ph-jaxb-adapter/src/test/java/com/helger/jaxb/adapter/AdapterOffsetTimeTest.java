/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

import org.junit.Test;

import com.helger.commons.datetime.PDTFactory;

/**
 * Test class for class {@link AdapterOffsetTime}.
 *
 * @author Philip Helger
 */
public final class AdapterOffsetTimeTest
{
  @Test
  public void testUnmarshal ()
  {
    final AdapterOffsetTime a = new AdapterOffsetTime ();
    assertNull (a.unmarshal (null));
    assertNull (a.unmarshal (""));
    assertNull (a.unmarshal (" "));
    assertNull (a.unmarshal ("a"));
    assertNull (a.unmarshal ("a10:12:45"));
    assertNull (a.unmarshal ("10:12:45a"));
    assertNull (a.unmarshal ("10:12:45.a"));
    assertNull (a.unmarshal ("10:12:45.123a"));
    assertNull (a.unmarshal ("10:12:61"));
    assertNull (a.unmarshal ("10: 12:45"));
    assertNull (a.unmarshal ("10:12: 45"));
    assertNull (a.marshal (null));

    // Offset required
    assertEquals (PDTFactory.createOffsetTime (10, 12, 45, ZoneOffset.UTC), a.unmarshal ("10:12:45Z"));
    assertEquals (PDTFactory.createOffsetTime (10, 12, 45, ZoneOffset.ofHours (1)), a.unmarshal ("10:12:45+01:00"));
    assertEquals (PDTFactory.createOffsetTime (10, 12, 45, ZoneOffset.ofHoursMinutes (-5, -30)), a.unmarshal ("10:12:45-05:30"));
    assertEquals (PDTFactory.createOffsetTime (10, 12, 45, ZoneOffset.UTC), a.unmarshal (" 10:12:45Z"));
    assertEquals (PDTFactory.createOffsetTime (10, 12, 45, ZoneOffset.UTC), a.unmarshal ("10:12:45Z "));
    assertEquals (PDTFactory.createOffsetTime (10, 12, 45, ZoneOffset.UTC), a.unmarshal (" 10:12:45Z "));
    assertEquals (PDTFactory.createOffsetTime (10, 12, 45, ZoneOffset.UTC).with (ChronoField.MILLI_OF_SECOND, 100),
                  a.unmarshal (" 10:12:45.1Z "));
    assertEquals (PDTFactory.createOffsetTime (10, 12, 45, ZoneOffset.UTC).with (ChronoField.MILLI_OF_SECOND, 120),
                  a.unmarshal (" 10:12:45.12Z "));
    assertEquals (PDTFactory.createOffsetTime (10, 12, 45, ZoneOffset.UTC).with (ChronoField.MILLI_OF_SECOND, 123),
                  a.unmarshal (" 10:12:45.123Z "));

    assertEquals (PDTFactory.createOffsetTime (10, 12, 45, ZoneOffset.UTC), a.unmarshal ("10:12:45"));
    assertEquals (PDTFactory.createOffsetTime (10, 12, 45, ZoneOffset.UTC), a.unmarshal (" 10:12:45"));
    assertEquals (PDTFactory.createOffsetTime (10, 12, 45, ZoneOffset.UTC), a.unmarshal ("10:12:45 "));
    assertEquals (PDTFactory.createOffsetTime (10, 12, 45, ZoneOffset.UTC), a.unmarshal (" 10:12:45 "));
    assertEquals (PDTFactory.createOffsetTime (10, 12, 45, ZoneOffset.UTC).with (ChronoField.MILLI_OF_SECOND, 100),
                  a.unmarshal (" 10:12:45.1 "));
    assertEquals (PDTFactory.createOffsetTime (10, 12, 45, ZoneOffset.UTC).with (ChronoField.MILLI_OF_SECOND, 120),
                  a.unmarshal (" 10:12:45.12 "));
    assertEquals (PDTFactory.createOffsetTime (10, 12, 45, ZoneOffset.UTC).with (ChronoField.MILLI_OF_SECOND, 123),
                  a.unmarshal (" 10:12:45.123 "));
  }
}
