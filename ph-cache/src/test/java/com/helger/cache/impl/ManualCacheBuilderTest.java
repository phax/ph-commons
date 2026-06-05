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
import java.util.function.Supplier;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import com.helger.cache.eviction.CacheEvictionScheduler;

/**
 * Test class for class {@link ManualCacheBuilder}.
 *
 * @author Philip Helger
 */
public final class ManualCacheBuilderTest
{
  @Test
  public void testBasicBuilder ()
  {
    final var c = ManualCache.builder ().name ("TestCache").build ();
    assertNotNull (c);
    assertEquals ("TestCache", c.getName ());
    assertFalse (c.hasMaxSize ());
    assertFalse (c.isAllowNullValues ());
  }

  @Test
  public void testBuilderWithMaxSize ()
  {
    final var c = ManualCache.builder ().maxSize (3).name ("MaxSizeCache").build ();
    assertTrue (c.hasMaxSize ());
    assertEquals (3, c.getMaxSize ());

    for (int i = 0; i < 6; ++i)
      c.putInCache ("key" + i, "value" + i);
    assertEquals (3, c.size ());
  }

  @Test
  public void testBuilderWithAllowNullValues ()
  {
    final var c = ManualCache.builder ().name ("NullCache").allowNullValues (true).build ();
    assertTrue (c.isAllowNullValues ());
    c.putInCache ("any", null);
    assertNull (c.getFromCache ("any"));
    assertEquals (1, c.size ());
  }

  @Test
  public void testBuilderMissingName ()
  {
    try
    {
      ManualCache.builder ().build ();
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
      ManualCache.builder ().name ("").build ();
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
    final ManualCacheBuilder <String, String> aBuilder = ManualCache.builder ();
    assertSame (aBuilder, aBuilder.name ("Chain"));
    assertSame (aBuilder, aBuilder.maxSize (5));
    assertSame (aBuilder, aBuilder.allowNullValues (true));
    assertSame (aBuilder, aBuilder.expireAfterWrite (Duration.ofSeconds (10)));
    assertSame (aBuilder, aBuilder.evictionInterval (Duration.ofSeconds (60)));
    assertSame (aBuilder, aBuilder.clockSupplier (AbstractMapBasedCache.DEFAULT_CLOCK_SUPPLIER));
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
    final var c = ManualCache.builder ().name ("TTL").expireAfterWrite (Duration.ofSeconds (10)).build ();
    assertTrue (c.hasTimeToLive ());
    assertEquals (Duration.ofSeconds (10), c.getTimeToLive ());
  }

  @Test
  public void testBuilderNullTTL ()
  {
    final var c = ManualCache.builder ().name ("NullTTL").expireAfterWrite (null).build ();
    assertFalse (c.hasTimeToLive ());
    assertNull (c.getTimeToLive ());
  }

  @Test
  public void testBuilderZeroTTLDisabled ()
  {
    final var c = ManualCache.builder ().name ("ZeroTTL").expireAfterWrite (Duration.ZERO).build ();
    assertFalse (c.hasTimeToLive ());
    assertNull (c.getTimeToLive ());
  }

  @Test
  public void testBuilderNegativeTTLDisabled ()
  {
    final var c = ManualCache.builder ().name ("NegTTL").expireAfterWrite (Duration.ofSeconds (-1)).build ();
    assertFalse (c.hasTimeToLive ());
    assertNull (c.getTimeToLive ());
  }

  @Test
  public void testBuilderClockSupplierPropagated ()
  {
    final AtomicReference <LocalDateTime> aNow = new AtomicReference <> (LocalDateTime.of (2026, 1, 1, 12, 0));
    final Supplier <LocalDateTime> aSupplier = aNow::get;
    final var c = ManualCache.builder ().name ("Clock").clockSupplier (aSupplier).build ();
    assertSame (aSupplier, c.getClockSupplier ());
  }

  @Test
  public void testBuilderNullClockSupplier ()
  {
    try
    {
      ManualCache.builder ().name ("NullClock").clockSupplier (null).build ();
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
    // Zero is treated as "disabled": no auto-register and no validation error even without a TTL.
    final var c = ManualCache.builder ().name ("ZeroInterval").evictionInterval (Duration.ZERO).build ();
    assertNotNull (c);
    assertEquals (0, CacheEvictionScheduler.getInstance ().getRegistrationCount ());
  }

  @Test
  public void testBuilderNegativeEvictionIntervalIgnored ()
  {
    final var c = ManualCache.builder ()
                             .name ("NegInterval")
                             .evictionInterval (Duration.ofSeconds (-1))
                             .build ();
    assertNotNull (c);
    assertEquals (0, CacheEvictionScheduler.getInstance ().getRegistrationCount ());
  }

  @Test
  public void testBuilderBuildTwiceCreatesIndependentCaches ()
  {
    final ManualCacheBuilder <String, String> aBuilder = ManualCache.<String, String> builder ().name ("Twice");
    final var c1 = aBuilder.build ();
    final var c2 = aBuilder.build ();
    assertNotSame (c1, c2);
    c1.putInCache ("k", "v");
    assertEquals (1, c1.size ());
    assertEquals (0, c2.size ());
  }

  @Test
  public void testBuilderDefaults ()
  {
    final var c = ManualCache.builder ().name ("Defaults").build ();
    assertFalse (c.hasMaxSize ());
    assertEquals (AbstractMapBasedCache.NO_MAX_SIZE, c.getMaxSize ());
    assertEquals (AbstractMapBasedCache.DEFAULT_ALLOW_NULL_VALUES, c.isAllowNullValues ());
    assertFalse (c.hasTimeToLive ());
    assertNull (c.getTimeToLive ());
    assertSame (AbstractMapBasedCache.DEFAULT_CLOCK_SUPPLIER, c.getClockSupplier ());
  }
}
