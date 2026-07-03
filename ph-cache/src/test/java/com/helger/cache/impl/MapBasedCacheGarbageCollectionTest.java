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
package com.helger.cache.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDateTime;

import org.junit.Test;

import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.map.SoftHashMap;

/**
 * Test class for the handling of garbage collected values in {@link AbstractMapBasedCache}.
 * Regression test for the NullPointerException in {@link AbstractMapBasedCache#evictExpired()} when
 * a cached value was garbage collected from the internal soft map but the stale entry was not yet
 * cleaned up.
 *
 * @author Philip Helger
 */
public final class MapBasedCacheGarbageCollectionTest
{
  private static final LocalDateTime NOW = LocalDateTime.of (2026, 1, 1, 12, 0);

  /**
   * {@link SoftHashMap} that allows simulating garbage collection of all currently referenced
   * values.
   */
  private static final class GCSimulatingSoftHashMap <K, V> extends SoftHashMap <K, V>
  {
    void clearAllReferences ()
    {
      for (final SoftValue <K, V> aSoftValue : m_aSrcMap.values ())
        aSoftValue.clear ();
    }
  }

  private static final class TestCache extends AbstractMapBasedCache <String, String>
  {
    private final GCSimulatingSoftHashMap <String, CacheEntry <String>> m_aSoftMap = new GCSimulatingSoftHashMap <> ();

    TestCache ()
    {
      super ("gc-test", -1, false, null, () -> NOW);
    }

    @Override
    protected ICommonsMap <String, CacheEntry <String>> createCache ()
    {
      return m_aSoftMap;
    }
  }

  @Test
  public void testEvictExpiredWithGarbageCollectedValues ()
  {
    final TestCache aCache = new TestCache ();
    aCache.putInCache ("k1", "v1");
    aCache.putInCache ("k2", "v2");
    assertEquals (2, aCache.size ());

    // Simulate garbage collection of all cached values
    aCache.m_aSoftMap.clearAllReferences ();

    // A live entry added afterwards must survive
    aCache.putInCache ("k3", "v3");

    // Must not throw an NPE; the stale entries are dropped but not counted as expired
    assertEquals (0, aCache.evictExpired ());
    assertEquals (1, aCache.size ());
    assertEquals ("v3", aCache.getFromCache ("k3"));
    assertNull (aCache.getFromCache ("k1"));
  }

  @Test
  public void testEvictExpiredCountsOnlyLiveExpiredEntries ()
  {
    final TestCache aCache = new TestCache ();
    aCache.putInCache ("gc", "v1");
    aCache.m_aSoftMap.clearAllReferences ();

    aCache.putInCache ("expired", "v2", NOW.minusHours (1));
    aCache.putInCache ("live", "v3");

    // Only the live expired entry is counted; the GC'ed one is dropped silently
    assertEquals (1, aCache.evictExpired ());
    assertEquals (1, aCache.size ());
    assertEquals ("v3", aCache.getFromCache ("live"));
  }
}
