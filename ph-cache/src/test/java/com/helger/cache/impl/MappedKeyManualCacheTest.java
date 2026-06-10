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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.base.state.EChange;
import com.helger.cache.ICache;
import com.helger.cache.ICacheWithExpiration;

/**
 * Test class for class {@link MappedKeyManualCache}.
 *
 * @author Philip Helger
 */
public final class MappedKeyManualCacheTest
{
  @NonNull
  private static MappedKeyManualCache <BigDecimal, String, String> _newCache (final String sName)
  {
    return MappedKeyManualCache.of (ManualCache.<String, String> builder ().name (sName).build (),
                                    BigDecimal::toString);
  }

  @Test
  public void testKeyMappingCollapsesAliases ()
  {
    final var c = _newCache ("Mock");
    c.putInCache (BigDecimal.ZERO, "v0");
    assertEquals ("v0", c.getFromCache (BigDecimal.ZERO));
    assertEquals (1, c.size ());

    // Different KEYTYPE values mapping to the same storage key see the same entry
    assertEquals ("v0", c.getFromCache (new BigDecimal ("0")));
    assertEquals (1, c.size ());

    // Putting via an aliased key replaces the original entry
    c.putInCache (new BigDecimal ("0"), "v0-replaced");
    assertEquals ("v0-replaced", c.getFromCache (BigDecimal.ZERO));
    assertEquals (1, c.size ());

    // Genuinely different storage key adds a new entry
    c.putInCache (BigDecimal.TEN, "v10");
    assertEquals (2, c.size ());
  }

  @Test
  public void testGetFromCacheMissReturnsNull ()
  {
    // Manual cache: no provider, so a miss returns null (does not auto-populate).
    final var c = _newCache ("MockMiss");
    assertNull (c.getFromCache (BigDecimal.ONE));
    assertEquals (0, c.size ());
    assertFalse (c.isInCache (BigDecimal.ONE));
  }

  @Test
  public void testIsInCache ()
  {
    final var c = _newCache ("MockIn");
    assertFalse (c.isInCache (BigDecimal.ONE));
    c.putInCache (BigDecimal.ONE, "v1");
    assertTrue (c.isInCache (BigDecimal.ONE));
    // Storage-key alias is also "in cache"
    assertTrue (c.isInCache (new BigDecimal ("1")));
    assertFalse (c.isInCache (BigDecimal.TEN));
  }

  @Test
  public void testRemoveFromCache ()
  {
    final var c = _newCache ("MockRemove");
    c.putInCache (BigDecimal.ONE, "v1");
    c.putInCache (BigDecimal.TEN, "v10");
    assertEquals (2, c.size ());

    assertEquals (EChange.CHANGED, c.removeFromCache (BigDecimal.ONE));
    assertEquals (1, c.size ());
    assertFalse (c.isInCache (BigDecimal.ONE));
    assertTrue (c.isInCache (BigDecimal.TEN));

    // Removing via an aliased key
    assertEquals (EChange.CHANGED, c.removeFromCache (new BigDecimal ("10")));
    assertEquals (0, c.size ());

    // Removing non-existent key
    assertEquals (EChange.UNCHANGED, c.removeFromCache (BigDecimal.ONE));
  }

  @Test
  public void testClearCache ()
  {
    final var c = _newCache ("MockClear");
    assertEquals (EChange.UNCHANGED, c.clearCache ());

    c.putInCache (BigDecimal.ONE, "v1");
    c.putInCache (BigDecimal.TEN, "v10");
    assertEquals (2, c.size ());

    assertEquals (EChange.CHANGED, c.clearCache ());
    assertTrue (c.isEmpty ());
    assertEquals (EChange.UNCHANGED, c.clearCache ());
  }

  @Test
  public void testMaxSize ()
  {
    final int nMaxSize = 3;
    final var aInner = ManualCache.<String, Integer> builder ().name ("MockSize").maxSize (nMaxSize).build ();
    final var c = MappedKeyManualCache.<Integer, String, Integer> of (aInner, Object::toString);
    assertEquals (nMaxSize, c.getMaxSize ());

    for (int i = 0; i < nMaxSize * 2; ++i)
      c.putInCache (Integer.valueOf (i), Integer.valueOf (i));
    assertEquals (nMaxSize, c.size ());
    // Earliest entries evicted, latest kept
    assertFalse (c.isInCache (Integer.valueOf (0)));
    assertFalse (c.isInCache (Integer.valueOf (2)));
    assertTrue (c.isInCache (Integer.valueOf (3)));
    assertTrue (c.isInCache (Integer.valueOf (5)));
  }

  @Test
  public void testNullStorageKeyOnGet ()
  {
    // Key mapper returns null for value 0 -> getFromCache must throw IllegalStateException
    final var aInner = ManualCache.<String, String> builder ().name ("MockNullKey").build ();
    final Function <Integer, String> aMapper = k -> k.intValue () == 0 ? null : k.toString ();
    final var c = MappedKeyManualCache.of (aInner, aMapper);
    // Normal key works
    c.putInCache (Integer.valueOf (1), "v1");
    assertEquals ("v1", c.getFromCache (Integer.valueOf (1)));

    try
    {
      c.getFromCache (Integer.valueOf (0));
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected -- key mapper returned null
    }
  }

  @Test
  public void testNullStorageKeyOnIsInCache ()
  {
    // Unlike the provider variant, MappedKeyManualCache#isInCache does not swallow the exception
    final var aInner = ManualCache.<String, String> builder ().name ("MockNullKeyIn").build ();
    final Function <Integer, String> aMapper = k -> k.intValue () == 0 ? null : k.toString ();
    final var c = MappedKeyManualCache.of (aInner, aMapper);

    // isInCache deals with invalid keys
    assertFalse (c.isInCache (Integer.valueOf (0)));
  }

  @Test
  public void testNullStorageKeyOnPut ()
  {
    final var aInner = ManualCache.<String, String> builder ().name ("MockNullKeyPut").build ();
    final Function <Integer, String> aMapper = k -> k.intValue () == 0 ? null : k.toString ();
    final var c = MappedKeyManualCache.of (aInner, aMapper);

    try
    {
      c.putInCache (Integer.valueOf (0), "v0");
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected -- key mapper returned null
    }
    assertEquals (0, c.size ());
  }

  @Test
  public void testNullStorageKeyOnRemove ()
  {
    final var aInner = ManualCache.<String, String> builder ().name ("MockNullKeyRem").build ();
    final Function <Integer, String> aMapper = k -> k.intValue () == 0 ? null : k.toString ();
    final var c = MappedKeyManualCache.of (aInner, aMapper);

    try
    {
      c.removeFromCache (Integer.valueOf (0));
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected -- key mapper returned null
    }
  }

  @Test
  public void testNullValueAllowed ()
  {
    final var aInner = ManualCache.<String, String> builder ().name ("MockNullVal").allowNullValues (true).build ();
    final var c = MappedKeyManualCache.<Integer, String, String> of (aInner, Object::toString);
    assertTrue (c.isAllowNullValues ());

    c.putInCache (Integer.valueOf (0), null);
    assertNull (c.getFromCache (Integer.valueOf (0)));
    assertTrue (c.isInCache (Integer.valueOf (0)));
    assertEquals (1, c.size ());
  }

  @Test
  public void testNullValueNotAllowed ()
  {
    final var aInner = ManualCache.<String, String> builder ().name ("MockNoNullVal").build ();
    final var c = MappedKeyManualCache.<Integer, String, String> of (aInner, Object::toString);
    assertFalse (c.isAllowNullValues ());

    try
    {
      c.putInCache (Integer.valueOf (0), null);
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected -- null value rejected by underlying cache
    }
    assertEquals (0, c.size ());
  }

  @Test
  public void testPutInCacheWithDuration ()
  {
    final var c = _newCache ("MockTTL");
    c.putInCache (BigDecimal.ONE, "v1", Duration.ofMinutes (5));
    assertEquals ("v1", c.getFromCache (BigDecimal.ONE));
    assertEquals (1, c.size ());
  }

  @Test
  public void testPutInCacheWithLocalDateTime ()
  {
    final var c = _newCache ("MockExpireAt");
    final LocalDateTime aExpireAt = LocalDateTime.of (2099, 1, 1, 0, 0);
    c.putInCache (BigDecimal.ONE, "v1", aExpireAt);
    assertEquals ("v1", c.getFromCache (BigDecimal.ONE));
    assertEquals (1, c.size ());
  }

  @Test
  public void testEvictExpiredWithCustomClock ()
  {
    final LocalDateTime aStart = LocalDateTime.of (2026, 1, 1, 12, 0);
    final AtomicReference <LocalDateTime> aNow = new AtomicReference <> (aStart);
    final Supplier <LocalDateTime> aClock = aNow::get;

    final var aInner = ManualCache.<String, String> builder ()
                                  .name ("MockEvict")
                                  .expireAfterWrite (Duration.ofSeconds (10))
                                  .clockSupplier (aClock)
                                  .build ();
    final var c = MappedKeyManualCache.<BigDecimal, String, String> of (aInner, BigDecimal::toString);

    c.putInCache (BigDecimal.ONE, "v1");
    c.putInCache (BigDecimal.TEN, "v10");
    assertEquals (2, c.size ());

    // Within TTL: nothing evicted
    aNow.set (aStart.plusSeconds (5));
    assertEquals (0, c.evictExpired ());
    assertEquals (2, c.size ());

    // Past TTL: both entries evicted
    aNow.set (aStart.plusSeconds (60));
    assertEquals (2, c.evictExpired ());
    assertEquals (0, c.size ());
    assertTrue (c.isEmpty ());
  }

  @Test
  public void testDelegatedProperties ()
  {
    final AtomicReference <LocalDateTime> aNow = new AtomicReference <> (LocalDateTime.of (2026, 1, 1, 12, 0));
    final Supplier <LocalDateTime> aClock = aNow::get;
    final var aInner = ManualCache.<String, String> builder ()
                                  .name ("MyName")
                                  .maxSize (7)
                                  .allowNullValues (true)
                                  .expireAfterWrite (Duration.ofSeconds (30))
                                  .clockSupplier (aClock)
                                  .build ();
    final var c = MappedKeyManualCache.<BigDecimal, String, String> of (aInner, BigDecimal::toString);

    assertEquals ("MyName", c.getName ());
    assertEquals (7, c.getMaxSize ());
    assertTrue (c.isAllowNullValues ());
    assertEquals (Duration.ofSeconds (30), c.getTimeToLive ());
    assertSame (aClock, c.getClockSupplier ());
  }

  @Test
  public void testDefaultsFromUnderlyingCache ()
  {
    final var aInner = ManualCache.<String, String> builder ().name ("Defaults").build ();
    final var c = MappedKeyManualCache.<BigDecimal, String, String> of (aInner, BigDecimal::toString);
    assertEquals (ICache.NO_MAX_SIZE, c.getMaxSize ());
    assertFalse (c.isAllowNullValues ());
    assertNull (c.getTimeToLive ());
    assertSame (ICacheWithExpiration.DEFAULT_CLOCK_SUPPLIER, c.getClockSupplier ());
  }

  @Test
  public void testIsEmptyAndSize ()
  {
    final var c = _newCache ("MockEmpty");
    assertTrue (c.isEmpty ());
    assertEquals (0, c.size ());

    c.putInCache (BigDecimal.ONE, "v1");
    assertFalse (c.isEmpty ());
    assertEquals (1, c.size ());
  }

  @Test
  public void testStaticOfFactoryEquivalentToConstructor ()
  {
    final var aInner1 = ManualCache.<String, String> builder ().name ("OfFactory1").build ();
    final var c1 = MappedKeyManualCache.<BigDecimal, String, String> of (aInner1, BigDecimal::toString);
    c1.putInCache (BigDecimal.ONE, "v1");

    final var aInner2 = ManualCache.<String, String> builder ().name ("OfFactory2").build ();
    final var c2 = new MappedKeyManualCache <> (aInner2, BigDecimal::toString);
    c2.putInCache (BigDecimal.ONE, "v1");

    assertEquals (c1.getFromCache (BigDecimal.ONE), c2.getFromCache (BigDecimal.ONE));
    assertEquals (c1.size (), c2.size ());
  }

  @Test
  public void testConstructorRejectsNullCache ()
  {
    try
    {
      new MappedKeyManualCache <BigDecimal, String, String> (null, BigDecimal::toString);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
  }

  @Test
  public void testConstructorRejectsNullMapper ()
  {
    final var aInner = ManualCache.<String, String> builder ().name ("MockNullMapper").build ();
    try
    {
      new MappedKeyManualCache <BigDecimal, String, String> (aInner, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
  }

  @Test
  public void testMapDirectly ()
  {
    final var c = _newCache ("MockMap");
    assertEquals ("0", c.getStorageKey (BigDecimal.ZERO));
    assertEquals ("1", c.getStorageKey (BigDecimal.ONE));
    // Different KEYTYPE values yielding the same storage key
    assertEquals (c.getStorageKey (BigDecimal.ZERO), c.getStorageKey (new BigDecimal ("0")));
  }
}
