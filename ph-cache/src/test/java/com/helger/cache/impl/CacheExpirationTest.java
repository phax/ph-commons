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
    final var c = new CacheBuilder <String, String> ().valueProvider (k -> {
      aProviderCalls.incrementAndGet ();
      return "v" + k;
    }).name ("TTLCache").expireAfterWrite (Duration.ofSeconds (10)).clockSupplier (aClock::get).buildProviderCache ();

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
    final var c = new CacheBuilder <String, String> ().valueProvider (k -> k)
                                                      .name ("TTLIsInCache")
                                                      .expireAfterWrite (Duration.ofSeconds (10))
                                                      .clockSupplier (aClock::get)
                                                      .buildProviderCache ();

    c.getFromCache ("a");
    assertTrue (c.isInCache ("a"));

    aClock.advance (Duration.ofSeconds (11));
    assertFalse (c.isInCache ("a"));
  }

  @Test
  public void testEvictExpiredRemovesAll ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    final var c = new CacheBuilder <String, String> ().valueProvider (k -> k)
                                                      .name ("TTLEvict")
                                                      .expireAfterWrite (Duration.ofSeconds (10))
                                                      .clockSupplier (aClock::get)
                                                      .buildProviderCache ();
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
    final var c = new CacheBuilder <String, String> ().valueProvider (k -> k)
                                                      .name ("TTLEvictPartial")
                                                      .expireAfterWrite (Duration.ofSeconds (10))
                                                      .clockSupplier (aClock::get)
                                                      .buildProviderCache ();

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
    final var c = new CacheBuilder <String, String> ().valueProvider (k -> k).name ("NoTTL").buildProviderCache ();
    c.getFromCache ("a");
    c.getFromCache ("b");
    assertEquals (0, c.evictExpired ());
    assertEquals (2, c.size ());
  }

  @Test
  public void testRemoveFromCacheStillWorks ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    final var c = new CacheBuilder <String, String> ().valueProvider (k -> k)
                                                      .name ("TTLRemove")
                                                      .expireAfterWrite (Duration.ofSeconds (10))
                                                      .clockSupplier (aClock::get)
                                                      .buildProviderCache ();
    c.getFromCache ("a");
    assertEquals (EChange.CHANGED, c.removeFromCache ("a"));
    assertEquals (0, c.size ());
    assertEquals (EChange.UNCHANGED, c.removeFromCache ("a"));
  }

  @Test
  public void testClearCacheStillWorks ()
  {
    final FixedClock aClock = new FixedClock (LocalDateTime.of (2026, 1, 1, 12, 0));
    final var c = new CacheBuilder <String, String> ().valueProvider (k -> k)
                                                      .name ("TTLClear")
                                                      .expireAfterWrite (Duration.ofSeconds (10))
                                                      .clockSupplier (aClock::get)
                                                      .buildProviderCache ();
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
    final var c = new CacheBuilder <String, String> ().valueProvider (k -> {
      aProviderCalls.incrementAndGet ();
      return null;
    })
                                                      .allowNullValues (true)
                                                      .name ("TTLNull")
                                                      .expireAfterWrite (Duration.ofSeconds (10))
                                                      .clockSupplier (aClock::get)
                                                      .buildProviderCache ();

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
    final var c = new CacheBuilder <String, String> ().valueProvider (k -> k)
                                                      .maxSize (3)
                                                      .name ("TTLMaxSize")
                                                      .expireAfterWrite (Duration.ofSeconds (10))
                                                      .clockSupplier (aClock::get)
                                                      .buildProviderCache ();

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
    final var c = new CacheBuilder <String, String> ().valueProvider (k -> k).name ("ToStr").buildManualCache ();
    final String s = c.toString ();
    assertNotNull (s);
    assertFalse (s.contains ("TimeToLive"));
  }

  @Test
  public void testWithTTLToString ()
  {
    final var c = new CacheBuilder <String, String> ().valueProvider (k -> k)
                                                      .name ("ToStrTTL")
                                                      .expireAfterWrite (Duration.ofSeconds (5))
                                                      .buildManualCache ();
    final String s = c.toString ();
    assertNotNull (s);
    assertTrue (s.contains ("TimeToLive"));
  }

  @Test
  public void testZeroTTLIsDisabled ()
  {
    final var c = new CacheBuilder <String, String> ().valueProvider (k -> k)
                                                      .name ("ZeroTTL")
                                                      .expireAfterWrite (Duration.ZERO)
                                                      .buildManualCache ();
    assertFalse (c.hasTimeToLive ());
    assertNull (c.getTimeToLive ());
  }

  @Test
  public void testNegativeTTLIsDisabled ()
  {
    final var c = new CacheBuilder <String, String> ().valueProvider (k -> k)
                                                      .name ("NegTTL")
                                                      .expireAfterWrite (Duration.ofSeconds (-5))
                                                      .buildManualCache ();
    assertFalse (c.hasTimeToLive ());
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
