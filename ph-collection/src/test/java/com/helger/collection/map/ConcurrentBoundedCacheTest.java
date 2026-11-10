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
package com.helger.collection.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link ConcurrentBoundedCache}.
 *
 * @author Philip Helger
 */
public final class ConcurrentBoundedCacheTest
{
  @Test
  public void testBasic ()
  {
    final ConcurrentBoundedCache <String, String> aCache = new ConcurrentBoundedCache <> (10);
    assertEquals (10, aCache.getMaxSize ());
    assertTrue (aCache.isEmpty ());
    assertEquals (0, aCache.size ());
    assertNull (aCache.get ("a"));

    assertNull (aCache.put ("a", "1"));
    assertEquals ("1", aCache.put ("a", "2"));
    assertEquals ("2", aCache.get ("a"));
    assertEquals (1, aCache.size ());
    assertFalse (aCache.isEmpty ());

    // Existing value is used, function not invoked
    assertEquals ("2", aCache.getOrCompute ("a", k -> {
      fail ();
      return null;
    }));

    final AtomicInteger aCount = new AtomicInteger ();
    final String sValue = aCache.getOrCompute ("b", k -> {
      aCount.incrementAndGet ();
      return k + "x";
    });
    assertEquals ("bx", sValue);
    assertSame (sValue, aCache.getOrCompute ("b", k -> {
      aCount.incrementAndGet ();
      return k + "y";
    }));
    assertEquals (1, aCount.get ());
    assertEquals (2, aCache.size ());

    assertEquals ("bx", aCache.remove ("b"));
    assertNull (aCache.remove ("b"));
    assertNull (aCache.get ("b"));

    aCache.clear ();
    assertTrue (aCache.isEmpty ());
    assertNotNull (aCache.toString ());
  }

  @Test
  public void testNullHandling ()
  {
    final ConcurrentBoundedCache <String, String> aCache = new ConcurrentBoundedCache <> (10);
    try
    {
      aCache.getOrCompute ("a", k -> null);
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
    // Nothing was stored
    assertTrue (aCache.isEmpty ());

    try
    {
      aCache.put ("a", null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
    assertTrue (aCache.isEmpty ());

    try
    {
      new ConcurrentBoundedCache <> (0);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testEviction ()
  {
    final ConcurrentBoundedCache <Integer, String> aCache = new ConcurrentBoundedCache <> (100);
    for (int i = 0; i < 100; ++i)
      aCache.put (Integer.valueOf (i), Integer.toString (i));
    // Not yet exceeded
    assertEquals (100, aCache.size ());

    // Exceeding the limit evicts down to 75%
    aCache.put (Integer.valueOf (100), "100");
    assertEquals (75, aCache.size ());

    // Replacing an existing value does not evict
    for (int i = 0; i <= 100; ++i)
    {
      final Integer aKey = Integer.valueOf (i);
      if (aCache.get (aKey) != null)
        aCache.put (aKey, "new");
    }
    assertEquals (75, aCache.size ());

    // Same with getOrCompute
    for (int i = 0; i < 1000; ++i)
    {
      final int nKey = 1000 + i;
      assertEquals ("v" + nKey, aCache.getOrCompute (Integer.valueOf (nKey), k -> "v" + k));
      assertTrue (aCache.size () <= 100);
    }

    // Max size 1: every addition beyond the first one evicts everything
    final ConcurrentBoundedCache <String, String> aSmall = new ConcurrentBoundedCache <> (1);
    aSmall.put ("a", "a");
    assertEquals (1, aSmall.size ());
    assertEquals ("b", aSmall.getOrCompute ("b", k -> k));
    assertTrue (aSmall.size () <= 1);
  }

  @Test
  public void testMultithreaded ()
  {
    final int nMaxSize = 50;
    final ConcurrentBoundedCache <Integer, String> aCache = new ConcurrentBoundedCache <> (nMaxSize);
    TestHelper.testInParallel (200, () -> {
      for (int i = 0; i < 1000; ++i)
      {
        final Integer aKey = Integer.valueOf (ThreadLocalRandom.current ().nextInt (500));
        final String sValue = aCache.getOrCompute (aKey, k -> "v" + k);
        // The value must always belong to the key
        assertEquals ("v" + aKey, sValue);
        final String sGet = aCache.get (aKey);
        if (sGet != null)
          assertEquals ("v" + aKey, sGet);
        if ((i % 100) == 0)
          aCache.remove (aKey);
      }
    });
    // Under contention insertions may happen while another thread is evicting, so the size may
    // temporarily exceed the maximum. A single-threaded insertion evicts again.
    aCache.getOrCompute (Integer.valueOf (-1), k -> "v" + k);
    assertTrue (aCache.size () <= nMaxSize);
  }
}
