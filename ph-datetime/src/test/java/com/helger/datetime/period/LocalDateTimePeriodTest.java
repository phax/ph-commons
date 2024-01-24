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
package com.helger.datetime.period;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.Test;

import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link LocalDateTimePeriod}.
 *
 * @author Philip Helger
 */
public final class LocalDateTimePeriodTest
{
  @Test
  public void testBasic ()
  {
    final LocalDateTime d1 = PDTFactory.createLocalDateTime (2017, Month.JANUARY, 1);
    final LocalDateTime d2 = PDTFactory.createLocalDateTime (2017, Month.FEBRUARY, 16);
    final LocalDateTime aNow = PDTFactory.getCurrentLocalDateTime ();
    assertTrue (aNow.isAfter (d1));
    assertTrue (aNow.isAfter (d2));

    LocalDateTimePeriod p = new LocalDateTimePeriod (null, null);
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

    p = new LocalDateTimePeriod (d1, null);
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

    p = new LocalDateTimePeriod (null, d2);
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

    p = new LocalDateTimePeriod (d1, d2);
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

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new LocalDateTimePeriod (null, null),
                                                                       new LocalDateTimePeriod (null, null));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new LocalDateTimePeriod (d1, null),
                                                                       new LocalDateTimePeriod (d1, null));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new LocalDateTimePeriod (null, d2),
                                                                       new LocalDateTimePeriod (null, d2));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new LocalDateTimePeriod (d1, d2), new LocalDateTimePeriod (d1, d2));

    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new LocalDateTimePeriod (d1, d2),
                                                                           new LocalDateTimePeriod (d1, aNow));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new LocalDateTimePeriod (d1, d2),
                                                                           new LocalDateTimePeriod (d2, d2));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new LocalDateTimePeriod (d1, d2),
                                                                           new LocalDateTimePeriod (d1, null));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new LocalDateTimePeriod (d1, d2),
                                                                           new LocalDateTimePeriod (null, d2));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new LocalDateTimePeriod (d1, d2),
                                                                           new LocalDateTimePeriod (null, null));
  }

  @Test
  public void testIsInside ()
  {
    final LocalDateTime aNow = PDTFactory.getCurrentLocalDateTime ();
    assertTrue (ILocalDateTimePeriod.isInside (null, true, null, true, aNow));
    assertTrue (ILocalDateTimePeriod.isInside (null, false, null, false, aNow));
  }

  @Test
  public void testDates ()
  {
    final LocalDateTime aNow = PDTFactory.getCurrentLocalDateTime ();
    final LocalDateTime dt0 = aNow.minusDays (100);
    final LocalDateTime dt1 = aNow.minusDays (10);
    final LocalDateTime dt2 = aNow.minusDays (2);
    final LocalDateTime dt3 = aNow.plusDays (2);
    final LocalDateTime dt4 = aNow.plusDays (10);
    final LocalDateTime dt5 = aNow.plusDays (100);

    final ILocalDateTimePeriod r1 = new LocalDateTimePeriod (dt2, dt3);
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

    final ILocalDateTimePeriod r2 = new LocalDateTimePeriod (dt1, dt4);
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

    assertFalse (new LocalDateTimePeriod (dt0, dt1).isOverlappingWithIncl (new LocalDateTimePeriod (dt2, dt3)));
    assertFalse (new LocalDateTimePeriod (dt0, dt1).isOverlappingWithExcl (new LocalDateTimePeriod (dt2, dt3)));

    assertTrue (new LocalDateTimePeriod (dt0, dt1).isOverlappingWithIncl (new LocalDateTimePeriod (dt0, dt3)));
    assertTrue (new LocalDateTimePeriod (dt0, dt1).isOverlappingWithExcl (new LocalDateTimePeriod (dt0, dt3)));

    assertTrue (new LocalDateTimePeriod (dt0, dt1).isOverlappingWithIncl (new LocalDateTimePeriod (dt1, dt3)));
    assertFalse (new LocalDateTimePeriod (dt0, dt1).isOverlappingWithExcl (new LocalDateTimePeriod (dt1, dt3)));

    // Second period is partially open
    assertTrue (new LocalDateTimePeriod (dt2, dt3).isOverlappingWithIncl (new LocalDateTimePeriod (null, dt5)));
    assertTrue (new LocalDateTimePeriod (dt2, dt3).isOverlappingWithExcl (new LocalDateTimePeriod (null, dt5)));

    assertTrue (new LocalDateTimePeriod (dt2, dt3).isOverlappingWithIncl (new LocalDateTimePeriod (null, dt2)));
    assertFalse (new LocalDateTimePeriod (dt2, dt3).isOverlappingWithExcl (new LocalDateTimePeriod (null, dt2)));

    assertTrue (new LocalDateTimePeriod (dt2, dt3).isOverlappingWithIncl (new LocalDateTimePeriod (dt0, null)));
    assertTrue (new LocalDateTimePeriod (dt2, dt3).isOverlappingWithExcl (new LocalDateTimePeriod (dt0, null)));

    assertTrue (new LocalDateTimePeriod (dt2, dt3).isOverlappingWithIncl (new LocalDateTimePeriod (dt3, null)));
    assertFalse (new LocalDateTimePeriod (dt2, dt3).isOverlappingWithExcl (new LocalDateTimePeriod (dt3, null)));

    assertTrue (new LocalDateTimePeriod (dt2, dt3).isOverlappingWithIncl (new LocalDateTimePeriod (null, null)));
    assertTrue (new LocalDateTimePeriod (dt2, dt3).isOverlappingWithExcl (new LocalDateTimePeriod (null, null)));

    // First period is partially open
    assertTrue (new LocalDateTimePeriod (null, dt5).isOverlappingWithIncl (new LocalDateTimePeriod (dt2, dt3)));
    assertTrue (new LocalDateTimePeriod (null, dt5).isOverlappingWithExcl (new LocalDateTimePeriod (dt2, dt3)));

    assertTrue (new LocalDateTimePeriod (null, dt2).isOverlappingWithIncl (new LocalDateTimePeriod (dt2, dt3)));
    assertFalse (new LocalDateTimePeriod (null, dt2).isOverlappingWithExcl (new LocalDateTimePeriod (dt2, dt3)));

    assertTrue (new LocalDateTimePeriod (dt0, null).isOverlappingWithIncl (new LocalDateTimePeriod (dt2, dt3)));
    assertTrue (new LocalDateTimePeriod (dt0, null).isOverlappingWithExcl (new LocalDateTimePeriod (dt2, dt3)));

    assertTrue (new LocalDateTimePeriod (dt3, null).isOverlappingWithIncl (new LocalDateTimePeriod (dt2, dt3)));
    assertFalse (new LocalDateTimePeriod (dt3, null).isOverlappingWithExcl (new LocalDateTimePeriod (dt2, dt3)));

    assertTrue (new LocalDateTimePeriod (null, null).isOverlappingWithIncl (new LocalDateTimePeriod (dt2, dt3)));
    assertTrue (new LocalDateTimePeriod (null, null).isOverlappingWithExcl (new LocalDateTimePeriod (dt2, dt3)));

    // Both sides partially open
    assertTrue (new LocalDateTimePeriod (dt1, null).isOverlappingWithIncl (new LocalDateTimePeriod (null, dt3)));
    assertTrue (new LocalDateTimePeriod (dt1, null).isOverlappingWithExcl (new LocalDateTimePeriod (null, dt3)));

    assertTrue (new LocalDateTimePeriod (dt1, null).isOverlappingWithIncl (new LocalDateTimePeriod (null, dt1)));
    assertFalse (new LocalDateTimePeriod (dt1, null).isOverlappingWithExcl (new LocalDateTimePeriod (null, dt1)));

    assertTrue (new LocalDateTimePeriod (dt1, null).isOverlappingWithIncl (new LocalDateTimePeriod (null, null)));
    assertTrue (new LocalDateTimePeriod (dt1, null).isOverlappingWithExcl (new LocalDateTimePeriod (null, null)));

    assertTrue (new LocalDateTimePeriod (null, null).isOverlappingWithIncl (new LocalDateTimePeriod (null, dt1)));
    assertTrue (new LocalDateTimePeriod (null, null).isOverlappingWithExcl (new LocalDateTimePeriod (null, dt1)));

    // No period, no overlap
    assertFalse (new LocalDateTimePeriod (null, null).isOverlappingWithIncl (new LocalDateTimePeriod (null, null)));
    assertFalse (new LocalDateTimePeriod (null, null).isOverlappingWithExcl (new LocalDateTimePeriod (null, null)));
  }
}
