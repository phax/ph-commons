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
}
