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
package com.helger.base.uuid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;

import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based tests for {@link UUID7Helper}.
 *
 * @author Philip Helger
 */
public final class UUID7HelperPropertyTest
{
  private static final IGenerator <Long> TIMESTAMPS = Generators.longs (0, UUID7Helper.MAX_TIMESTAMP_MILLIS);

  /** Arbitrary UUIDs, including ones with the highest bit set, where signed comparison fails */
  private static final IGenerator <UUID> ANY_UUIDS = Generators.lists (Generators.longs (), 2, 2)
                                                               .map (aList -> new UUID (aList.get (0).longValue (),
                                                                                        aList.get (1).longValue ()));

  @Test
  public void testCreateKeepsTimestamp ()
  {
    new PropertyChecker ().setTries (1000).forAll (TIMESTAMPS, aMillis -> {
      final UUID aUUID = UUID7Helper.create (aMillis.longValue ());
      assertTrue (aUUID.toString (), UUID7Helper.isUUID7 (aUUID));
      assertEquals (aMillis.longValue (), UUID7Helper.getTimestampMillis (aUUID));
    });
  }

  @Test
  public void testComparatorFollowsTimestamps ()
  {
    new PropertyChecker ().setTries (1000).forAll (TIMESTAMPS, TIMESTAMPS, (aMillis1, aMillis2) -> {
      final long n1 = aMillis1.longValue ();
      final long n2 = aMillis2.longValue ();
      if (n1 != n2)
        assertEquals (Long.signum (n1 - n2),
                      Integer.signum (UUID7Helper.UNSIGNED_COMPARATOR.compare (UUID7Helper.create (n1), UUID7Helper.create (n2))));
    });
  }

  @Test
  public void testComparatorContract ()
  {
    new PropertyChecker ().setTries (2000).forAll (ANY_UUIDS, ANY_UUIDS, ANY_UUIDS, (aA, aB, aC) -> {
      final int nAB = Integer.signum (UUID7Helper.UNSIGNED_COMPARATOR.compare (aA, aB));
      assertEquals (-nAB, Integer.signum (UUID7Helper.UNSIGNED_COMPARATOR.compare (aB, aA)));
      assertEquals (nAB == 0, aA.equals (aB));
      if (UUID7Helper.UNSIGNED_COMPARATOR.compare (aA, aB) <= 0 && UUID7Helper.UNSIGNED_COMPARATOR.compare (aB, aC) <= 0)
        assertTrue (UUID7Helper.UNSIGNED_COMPARATOR.compare (aA, aC) <= 0);
      // Unsigned order: the same as comparing the hex strings, which all have the same length
      assertEquals (Integer.signum (aA.toString ().compareTo (aB.toString ())), nAB);
    });
  }

  @Test
  public void testMonotonicIsStrictlyIncreasing ()
  {
    new PropertyChecker ().forAll (Generators.ints (2, 500), aCount -> {
      UUID aPrev = UUID7Helper.createMonotonic ();
      for (int i = 1; i < aCount.intValue (); ++i)
      {
        final UUID aNext = UUID7Helper.createMonotonic ();
        assertTrue (aPrev + " < " + aNext, UUID7Helper.UNSIGNED_COMPARATOR.compare (aPrev, aNext) < 0);
        aPrev = aNext;
      }
    });
  }
}
