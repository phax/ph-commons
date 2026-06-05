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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.junit.Test;

import com.helger.cache.ICache;
import com.helger.cache.ICacheWithExpiration;
import com.helger.cache.eviction.CacheEvictionScheduler;

/**
 * Test class for class {@link ProviderCacheBuilder}.
 *
 * @author Philip Helger
 */
public final class ProviderCacheBuilderTest
{
  @Test
  public void testBasicBuilder ()
  {
    final var c = ProviderCache.builder ().valueProvider (x -> "v" + x).name ("TestCache").build ();
    assertNotNull (c);
    assertEquals ("TestCache", c.getName ());
    assertFalse (c.hasMaxSize ());
    assertFalse (c.isAllowNullValues ());
  }

  @Test
  public void testBuilderWithValueProvider ()
  {
    final var c = ProviderCache.builder ().valueProvider (x -> "v" + x).name ("TestCache2").build ();
    assertNotNull (c);
    assertEquals ("vfoo", c.getFromCache ("foo"));
    assertEquals (1, c.size ());
  }

  @Test
  public void testBuilderWithMaxSize ()
  {
    final var c = ProviderCache.builder ().valueProvider (x -> x).maxSize (3).name ("MaxSizeCache").build ();
    assertTrue (c.hasMaxSize ());
    assertEquals (3, c.getMaxSize ());

    for (int i = 0; i < 6; ++i)
      c.getFromCache ("key" + i);
    assertEquals (3, c.size ());
  }

  @Test
  public void testBuilderWithAllowNullValues ()
  {
    final var c = ProviderCache.builder ().valueProvider (x -> null).name ("NullCache").allowNullValues (true).build ();
    assertTrue (c.isAllowNullValues ());
    assertNull (c.getFromCache ("any"));
    assertEquals (1, c.size ());
  }

  @Test
  public void testBuilderMissingValueProvider ()
  {
    try
    {
      ProviderCache.builder ().name ("NoProvider").build ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testBuilderMissingName ()
  {
    try
    {
      ProviderCache.builder ().valueProvider (x -> x).build ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testBuilderEmptyName ()
  {
    try
    {
      ProviderCache.builder ().valueProvider (x -> x).name ("").build ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testBuilderFluentChain ()
  {
    final ProviderCacheBuilder <String, String> aBuilder = ProviderCache.builder ();
    assertSame (aBuilder, aBuilder.name ("Chain"));
    assertSame (aBuilder, aBuilder.maxSize (5));
    assertSame (aBuilder, aBuilder.allowNullValues (true));
    assertSame (aBuilder, aBuilder.expireAfterWrite (Duration.ofSeconds (10)));
    assertSame (aBuilder, aBuilder.evictionInterval (Duration.ofSeconds (60)));
    assertSame (aBuilder, aBuilder.clockSupplier (ICacheWithExpiration.DEFAULT_CLOCK_SUPPLIER));
    assertSame (aBuilder, aBuilder.valueProvider (x -> x));
    final var c = aBuilder.build ();
    try
    {
      assertNotNull (c);
    }
    finally
    {
      CacheEvictionScheduler.getInstance ().unregister (c);
    }
  }

  @Test
  public void testBuilderWithTTL ()
  {
    final var c = ProviderCache.builder ()
                               .valueProvider (x -> x)
                               .name ("TTL")
                               .expireAfterWrite (Duration.ofSeconds (10))
                               .build ();
    assertTrue (c.hasTimeToLive ());
    assertEquals (Duration.ofSeconds (10), c.getTimeToLive ());
  }

  @Test
  public void testBuilderNullTTL ()
  {
    final var c = ProviderCache.builder ().valueProvider (x -> x).name ("NullTTL").expireAfterWrite (null).build ();
    assertFalse (c.hasTimeToLive ());
    assertNull (c.getTimeToLive ());
  }

  @Test
  public void testBuilderZeroTTLDisabled ()
  {
    final var c = ProviderCache.builder ()
                               .valueProvider (x -> x)
                               .name ("ZeroTTL")
                               .expireAfterWrite (Duration.ZERO)
                               .build ();
    assertFalse (c.hasTimeToLive ());
    assertNull (c.getTimeToLive ());
  }

  @Test
  public void testBuilderNegativeTTLDisabled ()
  {
    final var c = ProviderCache.builder ()
                               .valueProvider (x -> x)
                               .name ("NegTTL")
                               .expireAfterWrite (Duration.ofSeconds (-1))
                               .build ();
    assertFalse (c.hasTimeToLive ());
    assertNull (c.getTimeToLive ());
  }

  @Test
  public void testBuilderClockSupplierPropagated ()
  {
    final AtomicReference <LocalDateTime> aNow = new AtomicReference <> (LocalDateTime.of (2026, 1, 1, 12, 0));
    final Supplier <LocalDateTime> aSupplier = aNow::get;
    final var c = ProviderCache.builder ().valueProvider (x -> x).name ("Clock").clockSupplier (aSupplier).build ();
    assertSame (aSupplier, c.getClockSupplier ());
  }

  @Test
  public void testBuilderNullClockSupplier ()
  {
    try
    {
      ProviderCache.builder ().valueProvider (x -> x).name ("NullClock").clockSupplier (null).build ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testBuilderZeroEvictionIntervalIgnored ()
  {
    final var c = ProviderCache.builder ()
                               .valueProvider (x -> x)
                               .name ("ZeroInterval")
                               .evictionInterval (Duration.ZERO)
                               .build ();
    assertNotNull (c);
    assertEquals (0, CacheEvictionScheduler.getInstance ().getRegistrationCount ());
  }

  @Test
  public void testBuilderNegativeEvictionIntervalIgnored ()
  {
    final var c = ProviderCache.builder ()
                               .valueProvider (x -> x)
                               .name ("NegInterval")
                               .evictionInterval (Duration.ofSeconds (-1))
                               .build ();
    assertNotNull (c);
    assertEquals (0, CacheEvictionScheduler.getInstance ().getRegistrationCount ());
  }

  @Test
  public void testBuilderBuildTwiceCreatesIndependentCaches ()
  {
    final AtomicInteger aCalls = new AtomicInteger (0);
    final ProviderCacheBuilder <String, String> aBuilder = ProviderCache.<String, String> builder ()
                                                                        .name ("Twice")
                                                                        .valueProvider (k -> {
                                                                          aCalls.incrementAndGet ();
                                                                          return "v" + k;
                                                                        });
    final var c1 = aBuilder.build ();
    final var c2 = aBuilder.build ();
    assertNotSame (c1, c2);
    c1.getFromCache ("a");
    assertEquals (1, c1.size ());
    assertEquals (0, c2.size ());
    assertEquals (1, aCalls.get ());
    c2.getFromCache ("a");
    assertEquals (2, aCalls.get ());
  }

  @Test
  public void testBuilderDefaults ()
  {
    final var c = ProviderCache.builder ().valueProvider (x -> x).name ("Defaults").build ();
    assertFalse (c.hasMaxSize ());
    assertEquals (ICache.NO_MAX_SIZE, c.getMaxSize ());
    assertFalse (c.isAllowNullValues ());
    assertFalse (c.hasTimeToLive ());
    assertNull (c.getTimeToLive ());
    assertSame (ICacheWithExpiration.DEFAULT_CLOCK_SUPPLIER, c.getClockSupplier ());
  }
}
