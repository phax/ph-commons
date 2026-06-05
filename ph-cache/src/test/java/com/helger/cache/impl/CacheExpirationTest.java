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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.helger.base.state.EChange;
import com.helger.base.wrapper.Wrapper;

/**
 * Test class for the time-based expiration support in {@link ProviderCache} / {@link ManualCache}.
 *
 * @author Philip Helger
 */
public final class CacheExpirationTest
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
  public void testTTLBasicExpiry ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    final AtomicInteger aProviderCalls = new AtomicInteger (0);
    final var c = ProviderCache.builder ().valueProvider (k -> {
      aProviderCalls.incrementAndGet ();
      return "v" + k;
    }).name ("TTLCache").expireAfterWrite (Duration.ofSeconds (10)).clockSupplier (aClock::get).build ();

    assertTrue (c.hasTimeToLive ());
    assertEquals (Duration.ofSeconds (10), c.getTimeToLive ());

    assertEquals ("vfoo", c.getFromCache ("foo"));
    assertEquals (1, aProviderCalls.get ());

    // Within the TTL window - should hit cache
    aClock.advance (Duration.ofSeconds (5));
    assertEquals ("vfoo", c.getFromCache ("foo"));
    assertEquals (1, aProviderCalls.get ());

    // Past the TTL window - should re-fetch
    aClock.advance (Duration.ofSeconds (6));
    assertEquals ("vfoo", c.getFromCache ("foo"));
    assertEquals (2, aProviderCalls.get ());
  }

  @Test
  public void testIsInCacheRespectsExpiry ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    final var c = ProviderCache.builder ()
                               .valueProvider (k -> k)
                               .name ("TTLIsInCache")
                               .expireAfterWrite (Duration.ofSeconds (10))
                               .clockSupplier (aClock::get)
                               .build ();

    c.getFromCache ("a");
    assertTrue (c.isInCache ("a"));

    aClock.advance (Duration.ofSeconds (11));
    assertFalse (c.isInCache ("a"));
  }

  @Test
  public void testEvictExpiredRemovesAll ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    final var c = ProviderCache.builder ()
                               .valueProvider (k -> k)
                               .name ("TTLEvict")
                               .expireAfterWrite (Duration.ofSeconds (10))
                               .clockSupplier (aClock::get)
                               .build ();
    c.getFromCache ("a");
    c.getFromCache ("b");
    c.getFromCache ("c");
    assertEquals (3, c.size ());

    // All entries still valid
    assertEquals (0, c.evictExpired ());
    assertEquals (3, c.size ());

    aClock.advance (Duration.ofSeconds (11));
    assertEquals (3, c.evictExpired ());
    assertEquals (0, c.size ());
  }

  @Test
  public void testEvictExpiredOnlySomeExpired ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    final var c = ProviderCache.builder ()
                               .valueProvider (k -> k)
                               .name ("TTLEvictPartial")
                               .expireAfterWrite (Duration.ofSeconds (10))
                               .clockSupplier (aClock::get)
                               .build ();

    c.getFromCache ("a");
    aClock.advance (Duration.ofSeconds (6));
    c.getFromCache ("b");
    // "a" was added at t=0, expires at t=10. "b" was added at t=6, expires at t=16.
    // now at t=11
    aClock.advance (Duration.ofSeconds (5));
    assertEquals (1, c.evictExpired ());
    assertEquals (1, c.size ());
    assertTrue (c.isInCache ("b"));
    assertFalse (c.isInCache ("a"));
  }

  @Test
  public void testEvictExpiredNoTTLIsNoOp ()
  {
    final var c = ProviderCache.builder ().valueProvider (k -> k).name ("NoTTL").build ();
    c.getFromCache ("a");
    c.getFromCache ("b");
    assertEquals (0, c.evictExpired ());
    assertEquals (2, c.size ());
  }

  @Test
  public void testRemoveFromCacheStillWorks ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    final var c = ProviderCache.builder ()
                               .valueProvider (k -> k)
                               .name ("TTLRemove")
                               .expireAfterWrite (Duration.ofSeconds (10))
                               .clockSupplier (aClock::get)
                               .build ();
    c.getFromCache ("a");
    assertEquals (EChange.CHANGED, c.removeFromCache ("a"));
    assertEquals (0, c.size ());
    assertEquals (EChange.UNCHANGED, c.removeFromCache ("a"));
  }

  @Test
  public void testClearCacheStillWorks ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    final var c = ProviderCache.builder ()
                               .valueProvider (k -> k)
                               .name ("TTLClear")
                               .expireAfterWrite (Duration.ofSeconds (10))
                               .clockSupplier (aClock::get)
                               .build ();
    c.getFromCache ("a");
    c.getFromCache ("b");
    assertEquals (EChange.CHANGED, c.clearCache ());
    assertEquals (0, c.size ());
  }

  @Test
  public void testNullValueWithTTL ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    final AtomicInteger aProviderCalls = new AtomicInteger (0);
    final var c = ProviderCache.builder ().valueProvider (k -> {
      aProviderCalls.incrementAndGet ();
      return null;
    })
                               .allowNullValues (true)
                               .name ("TTLNull")
                               .expireAfterWrite (Duration.ofSeconds (10))
                               .clockSupplier (aClock::get)
                               .build ();

    assertNull (c.getFromCache ("k"));
    assertEquals (1, aProviderCalls.get ());
    assertTrue (c.isInCache ("k"));
    assertEquals (1, aProviderCalls.get ());

    // Still in cache and the null is cached
    assertNull (c.getFromCache ("k"));
    assertEquals (1, aProviderCalls.get ());

    // Expire and re-fetch (still null)
    aClock.advance (Duration.ofSeconds (11));
    assertFalse (c.isInCache ("k"));
    assertNull (c.getFromCache ("k"));
    assertEquals (2, aProviderCalls.get ());
  }

  @Test
  public void testTTLInteractionWithMaxSize ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    final var c = ProviderCache.builder ()
                               .valueProvider (k -> k)
                               .maxSize (3)
                               .name ("TTLMaxSize")
                               .expireAfterWrite (Duration.ofSeconds (10))
                               .clockSupplier (aClock::get)
                               .build ();

    c.getFromCache ("a");
    c.getFromCache ("b");
    c.getFromCache ("c");
    c.getFromCache ("d");
    // Size cap drops the oldest
    assertEquals (3, c.size ());
    assertFalse (c.isInCache ("a"));
  }

  @Test
  public void testNoTTLToString ()
  {
    final var c = ManualCache.builder ().name ("ToStr").build ();
    final String s = c.toString ();
    assertNotNull (s);
    assertFalse (s.contains ("TimeToLive"));
  }

  @Test
  public void testWithTTLToString ()
  {
    final var c = ManualCache.builder ().name ("ToStrTTL").expireAfterWrite (Duration.ofSeconds (5)).build ();
    final String s = c.toString ();
    assertNotNull (s);
    assertTrue (s.contains ("TimeToLive"));
  }

  @Test
  public void testZeroTTLIsDisabled ()
  {
    final var c = ManualCache.builder ().name ("ZeroTTL").expireAfterWrite (Duration.ZERO).build ();
    assertFalse (c.hasTimeToLive ());
    assertNull (c.getTimeToLive ());
  }

  @Test
  public void testNegativeTTLIsDisabled ()
  {
    final var c = ManualCache.builder ().name ("NegTTL").expireAfterWrite (Duration.ofSeconds (-5)).build ();
    assertFalse (c.hasTimeToLive ());
  }

  @Test
  public void testPutWithPerEntryTTL ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    // No cache-wide TTL configured
    final var c = ManualCache.builder ().name ("PerEntryTTL").clockSupplier (aClock::get).build ();
    assertFalse (c.hasTimeToLive ());

    c.putInCache ("short", "vS", Duration.ofSeconds (5));
    c.putInCache ("long", "vL", Duration.ofSeconds (60));
    assertTrue (c.isInCache ("short"));
    assertTrue (c.isInCache ("long"));

    aClock.advance (Duration.ofSeconds (10));
    // "short" is expired even though the cache has no cache-wide TTL
    assertFalse (c.isInCache ("short"));
    assertTrue (c.isInCache ("long"));
    assertNull (c.getFromCache ("short"));
    assertEquals ("vL", c.getFromCache ("long"));
  }

  @Test
  public void testPutWithPerEntryExpirationDateTime ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    final var c = ManualCache.builder ().name ("PerEntryDT").clockSupplier (aClock::get).build ();

    final LocalDateTime aExpiry = aClock.get ().plusSeconds (10);
    c.putInCache ("k", "v", aExpiry);
    assertTrue (c.isInCache ("k"));
    assertEquals ("v", c.getFromCache ("k"));

    aClock.advance (Duration.ofSeconds (11));
    assertFalse (c.isInCache ("k"));
    assertNull (c.getFromCache ("k"));
  }

  @Test
  public void testPerEntryTTLOverridesCacheWideTTL ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    // Cache-wide TTL is 5 seconds. The per-entry overload should override it for that key only.
    final var c = ManualCache.builder ()
                             .name ("Override")
                             .expireAfterWrite (Duration.ofSeconds (5))
                             .clockSupplier (aClock::get)
                             .build ();

    c.putInCache ("default", "vD");
    c.putInCache ("custom", "vC", Duration.ofSeconds (60));

    // After 10s the default entry has expired but the custom one hasn't
    aClock.advance (Duration.ofSeconds (10));
    assertFalse (c.isInCache ("default"));
    assertTrue (c.isInCache ("custom"));
  }

  @Test
  public void testEvictExpiredFindsPerEntryExpirations ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    // No cache-wide TTL — evictExpired must still find per-entry expirations
    final var c = ManualCache.builder ().name ("PerEntryEvict").clockSupplier (aClock::get).build ();

    c.putInCache ("a", "vA", Duration.ofSeconds (5));
    c.putInCache ("b", "vB", Duration.ofSeconds (60));
    c.putInCache ("c", "vC");
    assertEquals (3, c.size ());

    aClock.advance (Duration.ofSeconds (10));
    // Only "a" has an expiration that has passed; "b" not yet, "c" never
    assertEquals (1, c.evictExpired ());
    assertEquals (2, c.size ());
    assertTrue (c.isInCache ("b"));
    assertTrue (c.isInCache ("c"));
  }

  @Test
  public void testPutWithPerEntryTTLNullDurationFails ()
  {
    final var c = ManualCache.builder ().name ("NullDur").build ();
    try
    {
      c.putInCache ("k", "v", (Duration) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testPutWithPerEntryExpirationDateTimeNullFails ()
  {
    final var c = ManualCache.builder ().name ("NullDT").build ();
    try
    {
      c.putInCache ("k", "v", (LocalDateTime) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testPutWithPerEntryTTLZeroFails ()
  {
    final var c = ManualCache.builder ().name ("ZeroDur").build ();
    try
    {
      c.putInCache ("k", "v", Duration.ZERO);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testPutWithPerEntryTTLNullValueRespectsPolicy ()
  {
    final var c = ManualCache.builder ().name ("NullPolicy").build ();
    try
    {
      c.putInCache ("k", null, Duration.ofSeconds (10));
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testProviderCachePerEntryTTL ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    final var c = ProviderCache.builder ()
                               .valueProvider (k -> "v" + k)
                               .name ("ProvPerEntry")
                               .clockSupplier (aClock::get)
                               .build ();

    c.putInCache ("k", "explicit", Duration.ofSeconds (10));
    assertEquals ("explicit", c.getFromCache ("k"));

    aClock.advance (Duration.ofSeconds (11));
    // After expiration, ProviderCache re-fetches via the provider
    assertEquals ("vk", c.getFromCache ("k"));
  }

  @Test
  public void testCacheEntryFactories ()
  {
    final CacheEntry <String> aNoExp = CacheEntry.ofNoExpiration ("x");
    assertEquals ("x", aNoExp.getValue ());
    assertNull (aNoExp.getExpirationDateTime ());
    assertFalse (aNoExp.hasExpirationDateTime ());
    assertFalse (aNoExp.isExpiredAt (LocalDateTime.MAX));

    final LocalDateTime aNow = LocalDateTime.of (2026, 1, 1, 12, 0);
    final CacheEntry <String> aTTL = CacheEntry.ofTimeToLive ("y", aNow, Duration.ofSeconds (10));
    assertEquals ("y", aTTL.getValue ());
    assertTrue (aTTL.hasExpirationDateTime ());
    assertFalse (aTTL.isExpiredAt (aNow.plusSeconds (5)));
    assertTrue (aTTL.isExpiredAt (aNow.plusSeconds (11)));

    final CacheEntry <String> aAtDT = CacheEntry.ofExpirationDateTime ("z", aNow);
    assertEquals ("z", aAtDT.getValue ());
    assertEquals (aNow, aAtDT.getExpirationDateTime ());

    assertNotNull (aNoExp.toString ());
    assertNotNull (aTTL.toString ());
  }
}
