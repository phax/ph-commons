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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.Test;

import com.helger.base.wrapper.Wrapper;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.commons.ICommonsSet;

/**
 * Test class for the cache iteration support of {@link AbstractMapBasedCache} - see
 * {@link AbstractMapBasedCache#iterateCacheKey(java.util.function.Consumer)},
 * {@link AbstractMapBasedCache#iterateCache(java.util.function.BiConsumer)},
 * {@link AbstractMapBasedCache#getAllCacheKeys()} and
 * {@link AbstractMapBasedCache#removeFromCacheIf(java.util.function.Predicate)}.
 *
 * @author Philip Helger
 */
public final class CacheKeyIterationTest
{
  private static final class FixedClock
  {
    private final Wrapper <LocalDateTime> m_aNow;

    FixedClock (final LocalDateTime aStart)
    {
      m_aNow = Wrapper.of (aStart);
    }

    LocalDateTime get ()
    {
      return m_aNow.get ();
    }

    void advance (final Duration aDuration)
    {
      m_aNow.update (aDT -> aDT.plus (aDuration));
    }
  }

  @Test
  public void testIterateCacheKey ()
  {
    final var c = ManualCache.<String, String> builder ().name ("Iterate").build ();
    final ICommonsSet <String> aCollected = new CommonsHashSet <> ();

    c.iterateCacheKey (aCollected::add);
    assertTrue (aCollected.isEmpty ());

    c.putInCache ("a", "va");
    c.putInCache ("b", "vb");
    c.iterateCacheKey (aCollected::add);
    assertEquals (new CommonsHashSet <> ("a", "b"), aCollected);
  }

  @Test
  public void testIterateCacheKeySkipsExpired ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    final var c = ManualCache.<String, String> builder ()
                             .name ("IterateExpired")
                             .expireAfterWrite (Duration.ofSeconds (10))
                             .clockSupplier (aClock::get)
                             .build ();
    c.putInCache ("a", "va");
    aClock.advance (Duration.ofSeconds (11));
    c.putInCache ("b", "vb");

    final ICommonsSet <String> aCollected = new CommonsHashSet <> ();
    c.iterateCacheKey (aCollected::add);
    assertEquals (new CommonsHashSet <> ("b"), aCollected);
  }

  @Test
  public void testIterateCacheKeyNullConsumer ()
  {
    final var c = ManualCache.<String, String> builder ().name ("IterateNull").build ();
    try
    {
      c.iterateCacheKey (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
  }

  @Test
  public void testIterateCacheKeyProviderCache ()
  {
    // The storage key is the cache key, so iteration is supported
    final var c = ProviderCache.<String, String> builder ().name ("IterateProvider").valueProvider (k -> "v" + k).build ();
    assertEquals ("vfoo", c.getFromCache ("foo"));
    assertEquals ("vbar", c.getFromCache ("bar"));

    final ICommonsSet <String> aCollected = new CommonsHashSet <> ();
    c.iterateCacheKey (aCollected::add);
    assertEquals (new CommonsHashSet <> ("foo", "bar"), aCollected);
  }

  @Test
  public void testIterateCacheKeyMappedKeyCacheUnsupported ()
  {
    final var c = MappedKeyManualCache.of (ManualCache.<String, String> builder ().name ("IterateMapped").build (),
                                           (final BigDecimal x) -> x.toString ());
    c.putInCache (BigDecimal.ONE, "v1");

    try
    {
      // The original keys are not retained
      c.iterateCacheKey (x -> fail ());
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {
      // expected
    }

    // The storage keys can be iterated
    final ICommonsSet <String> aCollected = new CommonsHashSet <> ();
    c.iterateStorageCacheKey (aCollected::add);
    assertEquals (new CommonsHashSet <> ("1"), aCollected);
  }

  @Test
  public void testIterateCache ()
  {
    final var c = ManualCache.<String, String> builder ().name ("IterateKV").build ();
    final ICommonsMap <String, String> aCollected = new CommonsHashMap <> ();

    c.iterateCache (aCollected::put);
    assertTrue (aCollected.isEmpty ());

    c.putInCache ("a", "va");
    c.putInCache ("b", "vb");
    c.iterateCache (aCollected::put);
    assertEquals (2, aCollected.size ());
    assertEquals ("va", aCollected.get ("a"));
    assertEquals ("vb", aCollected.get ("b"));
  }

  @Test
  public void testIterateCacheSkipsExpired ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    final var c = ManualCache.<String, String> builder ()
                             .name ("IterateKVExpired")
                             .expireAfterWrite (Duration.ofSeconds (10))
                             .clockSupplier (aClock::get)
                             .build ();
    c.putInCache ("a", "va");
    aClock.advance (Duration.ofSeconds (11));
    c.putInCache ("b", "vb");

    final ICommonsMap <String, String> aCollected = new CommonsHashMap <> ();
    c.iterateCache (aCollected::put);
    assertEquals (1, aCollected.size ());
    assertEquals ("vb", aCollected.get ("b"));
  }

  @Test
  public void testIterateCacheWithNullValues ()
  {
    final var c = ManualCache.<String, String> builder ().name ("IterateKVNull").allowNullValues (true).build ();
    c.putInCache ("a", null);

    final ICommonsMap <String, String> aCollected = new CommonsHashMap <> ();
    c.iterateCache (aCollected::put);
    assertEquals (1, aCollected.size ());
    assertTrue (aCollected.containsKey ("a"));
    assertNull (aCollected.get ("a"));
  }

  @Test
  public void testIterateCacheNullConsumer ()
  {
    final var c = ManualCache.<String, String> builder ().name ("IterateKVNullConsumer").build ();
    try
    {
      c.iterateCache (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
  }

  @Test
  public void testIterateCacheProviderCache ()
  {
    // The storage key is the cache key, so iteration is supported
    final var c = ProviderCache.<String, String> builder ()
                               .name ("IterateKVProvider")
                               .valueProvider (k -> "v" + k)
                               .build ();
    assertEquals ("vfoo", c.getFromCache ("foo"));

    final ICommonsMap <String, String> aCollected = new CommonsHashMap <> ();
    c.iterateCache (aCollected::put);
    assertEquals (1, aCollected.size ());
    assertEquals ("vfoo", aCollected.get ("foo"));
  }

  @Test
  public void testIterateCacheMappedKeyCacheUnsupported ()
  {
    final var c = MappedKeyManualCache.of (ManualCache.<String, String> builder ().name ("IterateKVMapped").build (),
                                           (final BigDecimal x) -> x.toString ());
    c.putInCache (BigDecimal.ONE, "v1");

    try
    {
      // The original keys are not retained
      c.iterateCache ( (k, v) -> fail ());
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {
      // expected
    }

    // The storage keys can be iterated
    final ICommonsMap <String, String> aCollected = new CommonsHashMap <> ();
    c.iterateStorageCache (aCollected::put);
    assertEquals (1, aCollected.size ());
    assertEquals ("v1", aCollected.get ("1"));
  }

  @Test
  public void testGetAllCacheKeysEmpty ()
  {
    final var c = ManualCache.<String, String> builder ().name ("KeysEmpty").build ();
    final ICommonsSet <String> aKeys = c.getAllCacheKeys ();
    assertNotNull (aKeys);
    assertTrue (aKeys.isEmpty ());
  }

  @Test
  public void testGetAllCacheKeys ()
  {
    final var c = ManualCache.<String, String> builder ().name ("Keys").build ();
    c.putInCache ("a", "va");
    c.putInCache ("b", "vb");
    c.putInCache ("c", "vc");

    assertEquals (new CommonsHashSet <> ("a", "b", "c"), c.getAllCacheKeys ());

    c.removeFromCache ("b");
    assertEquals (new CommonsHashSet <> ("a", "c"), c.getAllCacheKeys ());

    c.clearCache ();
    assertTrue (c.getAllCacheKeys ().isEmpty ());
  }

  @Test
  public void testGetAllCacheKeysIsCopy ()
  {
    final var c = ManualCache.<String, String> builder ().name ("KeysCopy").build ();
    c.putInCache ("a", "va");

    final ICommonsSet <String> aKeys = c.getAllCacheKeys ();
    aKeys.add ("b");
    aKeys.remove ("a");

    // The cache is unaffected
    assertEquals (1, c.size ());
    assertEquals (new CommonsHashSet <> ("a"), c.getAllCacheKeys ());
  }

  @Test
  public void testGetAllCacheKeysSkipsExpired ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    final var c = ManualCache.<String, String> builder ()
                             .name ("KeysExpired")
                             .expireAfterWrite (Duration.ofSeconds (10))
                             .clockSupplier (aClock::get)
                             .build ();
    c.putInCache ("a", "va");

    aClock.advance (Duration.ofSeconds (5));
    c.putInCache ("b", "vb");
    assertEquals (new CommonsHashSet <> ("a", "b"), c.getAllCacheKeys ());

    // "a" is expired, "b" is not
    aClock.advance (Duration.ofSeconds (6));
    assertEquals (new CommonsHashSet <> ("b"), c.getAllCacheKeys ());

    // Both are expired - the entries are still in the internal map
    aClock.advance (Duration.ofSeconds (5));
    assertTrue (c.getAllCacheKeys ().isEmpty ());
    assertEquals (2, c.size ());
  }

  @Test
  public void testRemoveFromCacheIf ()
  {
    final var c = ManualCache.<String, String> builder ().name ("RemoveIf").build ();
    c.putInCache ("prefix$a", "va");
    c.putInCache ("prefix$b", "vb");
    c.putInCache ("other$c", "vc");

    assertEquals (2, c.removeFromCacheIf (x -> x.startsWith ("prefix$")));
    assertEquals (new CommonsHashSet <> ("other$c"), c.getAllCacheKeys ());
    assertEquals ("vc", c.getFromCache ("other$c"));

    // Nothing matches anymore
    assertEquals (0, c.removeFromCacheIf (x -> x.startsWith ("prefix$")));
    assertEquals (1, c.size ());

    assertEquals (1, c.removeFromCacheIf (x -> true));
    assertTrue (c.isEmpty ());
  }

  @Test
  public void testRemoveFromCacheIfEmpty ()
  {
    final var c = ManualCache.<String, String> builder ().name ("RemoveIfEmpty").build ();
    assertEquals (0, c.removeFromCacheIf (x -> true));
  }

  @Test
  public void testRemoveFromCacheIfAlsoRemovesExpired ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    final var c = ManualCache.<String, String> builder ()
                             .name ("RemoveIfExpired")
                             .expireAfterWrite (Duration.ofSeconds (10))
                             .clockSupplier (aClock::get)
                             .build ();
    c.putInCache ("a", "va");
    c.putInCache ("b", "vb");

    aClock.advance (Duration.ofSeconds (11));
    assertTrue (c.getAllCacheKeys ().isEmpty ());

    // Expired but not yet evicted entries are removed as well
    assertEquals (2, c.removeFromCacheIf (x -> true));
    assertTrue (c.isEmpty ());
  }

  @Test
  public void testRemoveFromCacheIfNullFilter ()
  {
    final var c = ManualCache.<String, String> builder ().name ("RemoveIfNull").build ();
    try
    {
      c.removeFromCacheIf (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
    assertFalse (c.hasMaxSize ());
  }
}
