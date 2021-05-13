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
package com.helger.commons.datetime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;

import org.junit.Test;

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
}
