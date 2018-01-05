/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.datetime.period;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.Month;

import org.junit.Test;

import com.helger.commons.datetime.PDTFactory;

/**
 * Test class for class {@link LocalDatePeriod}.
 *
 * @author Philip Helger
 */
public final class LocalDatePeriodTest
{
  @Test
  public void testBasic ()
  {
    final LocalDate d1 = PDTFactory.createLocalDate (2017, Month.JANUARY, 1);
    final LocalDate d2 = PDTFactory.createLocalDate (2017, Month.FEBRUARY, 16);
    final LocalDate aNow = PDTFactory.getCurrentLocalDate ();
    assertTrue (aNow.isAfter (d1));
    assertTrue (aNow.isAfter (d2));

    LocalDatePeriod p = new LocalDatePeriod (null, null);
    assertNull (p.getStart ());
    assertFalse (p.hasStart ());
    assertNull (p.getEnd ());
    assertFalse (p.hasEnd ());
    assertTrue (p.isInPeriodIncl (d1));
    assertTrue (p.isInPeriodIncl (d2));
    assertTrue (p.isInPeriodIncl (aNow));
    assertTrue (p.isInPeriodExcl (d1));
    assertTrue (p.isInPeriodExcl (d2));
    assertTrue (p.isInPeriodExcl (aNow));

    p = new LocalDatePeriod (d1, null);
    assertEquals (d1, p.getStart ());
    assertTrue (p.hasStart ());
    assertNull (p.getEnd ());
    assertFalse (p.hasEnd ());
    assertTrue (p.isInPeriodIncl (d1));
    assertTrue (p.isInPeriodIncl (d2));
    assertTrue (p.isInPeriodIncl (aNow));
    assertFalse (p.isInPeriodExcl (d1));
    assertTrue (p.isInPeriodExcl (d2));
    assertTrue (p.isInPeriodExcl (aNow));

    p = new LocalDatePeriod (null, d2);
    assertNull (p.getStart ());
    assertFalse (p.hasStart ());
    assertEquals (d2, p.getEnd ());
    assertTrue (p.hasEnd ());
    assertTrue (p.isInPeriodIncl (d1));
    assertTrue (p.isInPeriodIncl (d2));
    assertFalse (p.isInPeriodIncl (aNow));
    assertTrue (p.isInPeriodExcl (d1));
    assertFalse (p.isInPeriodExcl (d2));
    assertFalse (p.isInPeriodExcl (aNow));

    p = new LocalDatePeriod (d1, d2);
    assertEquals (d1, p.getStart ());
    assertTrue (p.hasStart ());
    assertEquals (d2, p.getEnd ());
    assertTrue (p.hasEnd ());
    assertTrue (p.isInPeriodIncl (d1));
    assertTrue (p.isInPeriodIncl (d2));
    assertFalse (p.isInPeriodIncl (aNow));
    assertFalse (p.isInPeriodExcl (d1));
    assertFalse (p.isInPeriodExcl (d2));
    assertFalse (p.isInPeriodExcl (aNow));
  }
}
