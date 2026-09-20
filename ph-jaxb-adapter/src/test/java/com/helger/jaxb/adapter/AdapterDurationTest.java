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
package com.helger.jaxb.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.time.Duration;

import org.junit.Test;

/**
 * Test class for class {@link AdapterDuration}.
 *
 * @author Philip Helger
 */
public final class AdapterDurationTest
{
  @Test
  public void testUnmarshal ()
  {
    final AdapterDuration a = new AdapterDuration ();
    assertNull (a.unmarshal (null));
    assertEquals (Duration.ofHours (1), a.unmarshal ("PT1H"));
    assertEquals (Duration.ofMinutes (90), a.unmarshal ("PT1H30M"));
    // Surrounding whitespace is trimmed
    assertEquals (Duration.ofHours (1), a.unmarshal ("  PT1H  "));
    assertEquals (Duration.ofDays (2), a.unmarshal ("P2D"));
    assertEquals (Duration.ofSeconds (-30), a.unmarshal ("PT-30S"));
  }

  @Test
  public void testUnmarshalInvalid ()
  {
    final AdapterDuration a = new AdapterDuration ();
    for (final String sInvalid : new String [] { "", "bla", "1H", "P" })
      try
      {
        a.unmarshal (sInvalid);
        fail ("Parsing '" + sInvalid + "' should have failed");
      }
      catch (final java.time.format.DateTimeParseException ex)
      {
        // expected
      }
  }

  @Test
  public void testMarshal ()
  {
    final AdapterDuration a = new AdapterDuration ();
    assertNull (a.marshal (null));
    assertEquals ("PT1H", a.marshal (Duration.ofHours (1)));
    assertEquals ("PT1H30M", a.marshal (Duration.ofMinutes (90)));
  }

  @Test
  public void testRoundTrip ()
  {
    final AdapterDuration a = new AdapterDuration ();
    for (final Duration aDuration : new Duration [] { Duration.ZERO,
                                                      Duration.ofHours (1),
                                                      Duration.ofMinutes (90),
                                                      Duration.ofDays (2) })
      assertEquals (aDuration, a.unmarshal (a.marshal (aDuration)));
  }
}
