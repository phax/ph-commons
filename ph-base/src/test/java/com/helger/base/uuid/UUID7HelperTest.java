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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

/**
 * Test class for class {@link UUID7Helper}
 *
 * @author Philip Helger
 */
public final class UUID7HelperTest
{
  @Test
  public void testVersionAndVariant ()
  {
    for (int i = 0; i < 1000; ++i)
    {
      final UUID aUUID = UUID7Helper.create ();
      assertNotNull (aUUID);
      assertEquals (7, aUUID.version ());
      assertEquals (2, aUUID.variant ());
      assertTrue (UUID7Helper.isUUID7 (aUUID));

      final UUID aUUIDMono = UUID7Helper.createMonotonic ();
      assertEquals (7, aUUIDMono.version ());
      assertEquals (2, aUUIDMono.variant ());
    }
    assertFalse (UUID7Helper.isUUID7 (UUID.randomUUID ()));
    assertFalse (UUID7Helper.isUUID7 (UUID5Helper.fromUTF8 ("abc")));
  }

  @Test
  public void testTimestampRoundTrip ()
  {
    for (final long nMillis : new long [] { 0, 1, 1_763_600_000_000L, UUID7Helper.MAX_TIMESTAMP_MILLIS })
    {
      final UUID aUUID = UUID7Helper.create (nMillis);
      assertTrue (UUID7Helper.isUUID7 (aUUID));
      assertEquals (nMillis, UUID7Helper.getTimestampMillis (aUUID));
    }

    final long nBefore = System.currentTimeMillis ();
    final long nMillis = UUID7Helper.getTimestampMillis (UUID7Helper.create ());
    assertTrue (nMillis >= nBefore);
    assertTrue (nMillis <= System.currentTimeMillis ());
  }

  @Test
  public void testInvalidTimestamp ()
  {
    for (final long nMillis : new long [] { -1, Long.MIN_VALUE, UUID7Helper.MAX_TIMESTAMP_MILLIS + 1, Long.MAX_VALUE })
      try
      {
        UUID7Helper.create (nMillis);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {
        // expected
      }

    try
    {
      UUID7Helper.getTimestampMillis (UUID.randomUUID ());
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testStringPrefix ()
  {
    assertTrue (UUID7Helper.create (0).toString ().startsWith ("00000000-0000-7"));
    assertTrue (UUID7Helper.create (0x0123_4567_89abL).toString ().startsWith ("01234567-89ab-7"));
    assertTrue (UUID7Helper.create (UUID7Helper.MAX_TIMESTAMP_MILLIS).toString ().startsWith ("ffffffff-ffff-7"));
  }

  @Test
  public void testUnsignedOrder ()
  {
    // Includes values >= 2^47, which UUID.compareTo sorts incorrectly
    final long [] aMillis = { 0, 1, 1_763_600_000_000L, 0x7fff_ffff_ffffL, 0x8000_0000_0000L, UUID7Helper.MAX_TIMESTAMP_MILLIS };
    UUID aPrev = null;
    for (final long nMillis : aMillis)
    {
      final UUID aUUID = UUID7Helper.create (nMillis);
      if (aPrev != null)
        assertTrue (UUID7Helper.UNSIGNED_COMPARATOR.compare (aPrev, aUUID) < 0);
      aPrev = aUUID;
    }

    final UUID aLow = UUID7Helper.create (0x7fff_ffff_ffffL);
    final UUID aHigh = UUID7Helper.create (0x8000_0000_0000L);
    assertTrue (UUID7Helper.UNSIGNED_COMPARATOR.compare (aLow, aHigh) < 0);
    // Shows why the unsigned comparator is needed
    assertTrue (aLow.compareTo (aHigh) > 0);
  }

  @Test
  public void testUniqueness ()
  {
    final int nCount = 100_000;
    final Set <UUID> aSet = new HashSet <> (nCount * 2);
    for (int i = 0; i < nCount; ++i)
      aSet.add (UUID7Helper.create (1_763_600_000_000L));
    assertEquals (nCount, aSet.size ());
  }

  @Test
  public void testMonotonic ()
  {
    UUID aPrev = UUID7Helper.createMonotonic ();
    for (int i = 0; i < 100_000; ++i)
    {
      final UUID aUUID = UUID7Helper.createMonotonic ();
      assertTrue (UUID7Helper.UNSIGNED_COMPARATOR.compare (aPrev, aUUID) < 0);
      aPrev = aUUID;
    }
  }

  @Test
  public void testMonotonicMultiThreaded () throws Exception
  {
    final int nThreads = 8;
    final int nPerThread = 20_000;
    final ExecutorService aES = Executors.newFixedThreadPool (nThreads);
    try
    {
      final List <Future <List <UUID>>> aFutures = new ArrayList <> ();
      for (int t = 0; t < nThreads; ++t)
        aFutures.add (aES.submit ( () -> {
          final List <UUID> ret = new ArrayList <> (nPerThread);
          for (int i = 0; i < nPerThread; ++i)
            ret.add (UUID7Helper.createMonotonic ());
          return ret;
        }));

      final Set <UUID> aAll = new HashSet <> ();
      for (final Future <List <UUID>> aFuture : aFutures)
      {
        final List <UUID> aList = aFuture.get ();
        // Strictly increasing within each thread
        for (int i = 1; i < aList.size (); ++i)
          assertTrue (UUID7Helper.UNSIGNED_COMPARATOR.compare (aList.get (i - 1), aList.get (i)) < 0);
        aAll.addAll (aList);
      }
      // No duplicates across threads
      assertEquals (nThreads * nPerThread, aAll.size ());
    }
    finally
    {
      aES.shutdown ();
    }
  }
}
