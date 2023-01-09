/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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
import com.helger.commons.mock.CommonsTestHelper;

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

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new LocalDatePeriod (null, null), new LocalDatePeriod (null, null));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new LocalDatePeriod (d1, null), new LocalDatePeriod (d1, null));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new LocalDatePeriod (null, d2), new LocalDatePeriod (null, d2));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new LocalDatePeriod (d1, d2), new LocalDatePeriod (d1, d2));

    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new LocalDatePeriod (d1, d2), new LocalDatePeriod (d1, aNow));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new LocalDatePeriod (d1, d2), new LocalDatePeriod (d2, d2));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new LocalDatePeriod (d1, d2), new LocalDatePeriod (d1, null));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new LocalDatePeriod (d1, d2), new LocalDatePeriod (null, d2));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new LocalDatePeriod (d1, d2), new LocalDatePeriod (null, null));
  }

  @Test
  public void testIsInside ()
  {
    final LocalDate aNow = PDTFactory.getCurrentLocalDate ();
    assertTrue (ILocalDatePeriod.isInside (null, true, null, true, aNow));
    assertTrue (ILocalDatePeriod.isInside (null, false, null, false, aNow));
  }

  @Test
  public void testDates ()
  {
    final LocalDate aNow = PDTFactory.getCurrentLocalDate ();
    final LocalDate dt0 = aNow.minusDays (100);
    final LocalDate dt1 = aNow.minusDays (10);
    final LocalDate dt2 = aNow.minusDays (2);
    final LocalDate dt3 = aNow.plusDays (2);
    final LocalDate dt4 = aNow.plusDays (10);
    final LocalDate dt5 = aNow.plusDays (100);

    final ILocalDatePeriod r1 = new LocalDatePeriod (dt2, dt3);
    assertFalse (r1.isInPeriodIncl (dt0));
    assertFalse (r1.isInPeriodIncl (dt1));
    assertTrue (r1.isInPeriodIncl (dt2));
    assertTrue (r1.isInPeriodIncl (aNow));
    assertTrue (r1.isInPeriodIncl (dt3));
    assertFalse (r1.isInPeriodIncl (dt4));
    assertFalse (r1.isInPeriodIncl (dt5));

    assertFalse (r1.isInPeriodExcl (dt0));
    assertFalse (r1.isInPeriodExcl (dt1));
    assertFalse (r1.isInPeriodExcl (dt2));
    assertTrue (r1.isInPeriodExcl (aNow));
    assertFalse (r1.isInPeriodExcl (dt3));
    assertFalse (r1.isInPeriodExcl (dt4));
    assertFalse (r1.isInPeriodExcl (dt5));

    final ILocalDatePeriod r2 = new LocalDatePeriod (dt1, dt4);
    assertFalse (r2.isInPeriodIncl (dt0));
    assertTrue (r2.isInPeriodIncl (dt1));
    assertTrue (r2.isInPeriodIncl (dt2));
    assertTrue (r2.isInPeriodIncl (aNow));
    assertTrue (r2.isInPeriodIncl (dt3));
    assertTrue (r2.isInPeriodIncl (dt4));
    assertFalse (r2.isInPeriodIncl (dt5));

    assertFalse (r2.isInPeriodExcl (dt0));
    assertFalse (r2.isInPeriodExcl (dt1));
    assertTrue (r2.isInPeriodExcl (dt2));
    assertTrue (r2.isInPeriodExcl (aNow));
    assertTrue (r2.isInPeriodExcl (dt3));
    assertFalse (r2.isInPeriodExcl (dt4));
    assertFalse (r2.isInPeriodExcl (dt5));

    assertTrue (r1.isOverlappingWithIncl (r1));
    assertTrue (r1.isOverlappingWithIncl (r2));
    assertTrue (r2.isOverlappingWithIncl (r1));
    assertTrue (r2.isOverlappingWithIncl (r2));

    assertFalse (r1.isOverlappingWithExcl (r1));
    assertTrue (r1.isOverlappingWithExcl (r2));
    assertTrue (r2.isOverlappingWithExcl (r1));
    assertFalse (r2.isOverlappingWithExcl (r2));

    assertFalse (new LocalDatePeriod (dt0, dt1).isOverlappingWithIncl (new LocalDatePeriod (dt2, dt3)));
    assertFalse (new LocalDatePeriod (dt0, dt1).isOverlappingWithExcl (new LocalDatePeriod (dt2, dt3)));

    assertTrue (new LocalDatePeriod (dt0, dt1).isOverlappingWithIncl (new LocalDatePeriod (dt0, dt3)));
    assertTrue (new LocalDatePeriod (dt0, dt1).isOverlappingWithExcl (new LocalDatePeriod (dt0, dt3)));

    assertTrue (new LocalDatePeriod (dt0, dt1).isOverlappingWithIncl (new LocalDatePeriod (dt1, dt3)));
    assertFalse (new LocalDatePeriod (dt0, dt1).isOverlappingWithExcl (new LocalDatePeriod (dt1, dt3)));

    // Second period is partially open
    assertTrue (new LocalDatePeriod (dt2, dt3).isOverlappingWithIncl (new LocalDatePeriod (null, dt5)));
    assertTrue (new LocalDatePeriod (dt2, dt3).isOverlappingWithExcl (new LocalDatePeriod (null, dt5)));

    assertTrue (new LocalDatePeriod (dt2, dt3).isOverlappingWithIncl (new LocalDatePeriod (null, dt2)));
    assertFalse (new LocalDatePeriod (dt2, dt3).isOverlappingWithExcl (new LocalDatePeriod (null, dt2)));

    assertTrue (new LocalDatePeriod (dt2, dt3).isOverlappingWithIncl (new LocalDatePeriod (dt0, null)));
    assertTrue (new LocalDatePeriod (dt2, dt3).isOverlappingWithExcl (new LocalDatePeriod (dt0, null)));

    assertTrue (new LocalDatePeriod (dt2, dt3).isOverlappingWithIncl (new LocalDatePeriod (dt3, null)));
    assertFalse (new LocalDatePeriod (dt2, dt3).isOverlappingWithExcl (new LocalDatePeriod (dt3, null)));

    assertTrue (new LocalDatePeriod (dt2, dt3).isOverlappingWithIncl (new LocalDatePeriod (null, null)));
    assertTrue (new LocalDatePeriod (dt2, dt3).isOverlappingWithExcl (new LocalDatePeriod (null, null)));

    // First period is partially open
    assertTrue (new LocalDatePeriod (null, dt5).isOverlappingWithIncl (new LocalDatePeriod (dt2, dt3)));
    assertTrue (new LocalDatePeriod (null, dt5).isOverlappingWithExcl (new LocalDatePeriod (dt2, dt3)));

    assertTrue (new LocalDatePeriod (null, dt2).isOverlappingWithIncl (new LocalDatePeriod (dt2, dt3)));
    assertFalse (new LocalDatePeriod (null, dt2).isOverlappingWithExcl (new LocalDatePeriod (dt2, dt3)));

    assertTrue (new LocalDatePeriod (dt0, null).isOverlappingWithIncl (new LocalDatePeriod (dt2, dt3)));
    assertTrue (new LocalDatePeriod (dt0, null).isOverlappingWithExcl (new LocalDatePeriod (dt2, dt3)));

    assertTrue (new LocalDatePeriod (dt3, null).isOverlappingWithIncl (new LocalDatePeriod (dt2, dt3)));
    assertFalse (new LocalDatePeriod (dt3, null).isOverlappingWithExcl (new LocalDatePeriod (dt2, dt3)));

    assertTrue (new LocalDatePeriod (null, null).isOverlappingWithIncl (new LocalDatePeriod (dt2, dt3)));
    assertTrue (new LocalDatePeriod (null, null).isOverlappingWithExcl (new LocalDatePeriod (dt2, dt3)));

    // Both sides partially open
    assertTrue (new LocalDatePeriod (dt1, null).isOverlappingWithIncl (new LocalDatePeriod (null, dt3)));
    assertTrue (new LocalDatePeriod (dt1, null).isOverlappingWithExcl (new LocalDatePeriod (null, dt3)));

    assertTrue (new LocalDatePeriod (dt1, null).isOverlappingWithIncl (new LocalDatePeriod (null, dt1)));
    assertFalse (new LocalDatePeriod (dt1, null).isOverlappingWithExcl (new LocalDatePeriod (null, dt1)));

    assertTrue (new LocalDatePeriod (dt1, null).isOverlappingWithIncl (new LocalDatePeriod (null, null)));
    assertTrue (new LocalDatePeriod (dt1, null).isOverlappingWithExcl (new LocalDatePeriod (null, null)));

    assertTrue (new LocalDatePeriod (null, null).isOverlappingWithIncl (new LocalDatePeriod (null, dt1)));
    assertTrue (new LocalDatePeriod (null, null).isOverlappingWithExcl (new LocalDatePeriod (null, dt1)));

    // No period, no overlap
    assertFalse (new LocalDatePeriod (null, null).isOverlappingWithIncl (new LocalDatePeriod (null, null)));
    assertFalse (new LocalDatePeriod (null, null).isOverlappingWithExcl (new LocalDatePeriod (null, null)));
  }
}
