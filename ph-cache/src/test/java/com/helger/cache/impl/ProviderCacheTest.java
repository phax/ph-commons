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

import org.junit.Test;

import com.helger.base.state.EChange;
import com.helger.base.string.StringHelper;

/**
 * Test class for class {@link ProviderCache}.
 *
 * @author Philip Helger
 */
public final class ProviderCacheTest
{
  @Test
  public void testMaxSize ()
  {
    final int nMaxSize = 5;
    final var c = ProviderCache.builder ().valueProvider (x -> x).maxSize (nMaxSize).name ("Mock").build ();
    assertTrue (c.hasMaxSize ());
    assertEquals (nMaxSize, c.getMaxSize ());

    for (int i = 0; i < nMaxSize * 2; ++i)
      c.getFromCache ("anything" + i);
    assertEquals (nMaxSize, c.size ());
    assertFalse (c.isInCache ("anything0"));
    assertFalse (c.isInCache ("anything4"));
    assertTrue (c.isInCache ("anything5"));
    assertTrue (c.isInCache ("anything9"));
  }

  @Test
  public void testNoMaxSize ()
  {
    final var c = ProviderCache.builder ().valueProvider (x -> x).name ("Mock").build ();
    assertFalse (c.hasMaxSize ());

    final int nMax = 10;
    for (int i = 0; i < nMax; ++i)
      c.getFromCache ("anything" + i);
    assertEquals (nMax, c.size ());
    assertTrue (c.isInCache ("anything0"));
    assertTrue (c.isInCache ("anything4"));
    assertTrue (c.isInCache ("anything5"));
    assertTrue (c.isInCache ("anything9"));
  }

  @Test
  public void testSameStoreKey ()
  {
    final var c = ProviderCache.<String, String> builder ()
                               .valueProvider (StringHelper::getNotNull)
                               .name ("Mock")
                               .build ();
    assertEquals (0, c.size ());
    assertEquals ("", c.getFromCache (""));
    assertEquals (1, c.size ());
    assertEquals ("", c.getFromCache (""));
    assertEquals (1, c.size ());

    if (false)
    {
      // Creates the same key as ""
      assertEquals ("", c.getFromCache (null));
      assertEquals (1, c.size ());
      assertTrue (c.isInCache (""));
      assertTrue (c.isInCache (null));
    }
  }

  @Test
  public void testNullStoreKey ()
  {
    final var c = ProviderCache.<String, String> builder ()
                               .valueProvider (StringHelper::getNotNull)
                               .name ("Mock")
                               .build ();
    try
    {
      // null key not allowed
      c.getFromCache (null);
      fail ();
    }
    catch (final RuntimeException ex)
    {
      // expected
    }
    assertTrue (c.isEmpty ());
  }

  @Test
  public void testNullValueNotAllowed ()
  {
    final var c = ProviderCache.<Integer, String> builder ()
                               .valueProvider (x -> x == null ? null : "v" + x.intValue ())
                               .name ("Mock")
                               .build ();
    assertEquals ("v1", c.getFromCache (Integer.valueOf (1)));
    assertEquals (1, c.size ());
    try
    {
      // null value not allowed
      c.getFromCache (null);
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
    assertEquals (1, c.size ());
    assertFalse (c.isInCache (null));
  }

  @Test
  public void testNullValueAllowed ()
  {
    final var c = ProviderCache.builder ()
                               .valueProvider (aKey -> "blub".equals (aKey) ? null : aKey)
                               .name ("Mock")
                               .allowNullValues (true)
                               .build ();
    assertEquals ("v1", c.getFromCache ("v1"));
    assertEquals (1, c.size ());
    // null value allowed
    assertNull (c.getFromCache ("blub"));
    assertEquals (2, c.size ());
    assertTrue (c.isInCache ("blub"));
  }

  @Test
  public void testPrefilledCache ()
  {
    final var c = ProviderCache.<String, String> builder ()
                               .valueProvider (StringHelper::getNotNull)
                               .name ("Mock")
                               .build ();
    c.putInCache ("a", "b");
    assertEquals (1, c.size ());
    assertTrue (c.isInCache ("a"));
    assertEquals ("b", c.getFromCache ("a"));
    assertEquals (1, c.size ());
    assertFalse (c.isInCache ("b"));
    assertEquals ("b", c.getFromCache ("b"));
    assertEquals (2, c.size ());
    assertTrue (c.isInCache ("b"));
  }

  @Test
  public void testRemoveFromCache ()
  {
    final var c = ProviderCache.builder ().valueProvider (x -> x).name ("MockRemove").build ();
    c.getFromCache ("a");
    c.getFromCache ("b");
    assertEquals (2, c.size ());

    assertEquals (EChange.CHANGED, c.removeFromCache ("a"));
    assertEquals (1, c.size ());
    assertFalse (c.isInCache ("a"));
    assertTrue (c.isInCache ("b"));

    // Removing non-existent key
    assertEquals (EChange.UNCHANGED, c.removeFromCache ("notThere"));
    assertEquals (1, c.size ());
  }

  @Test
  public void testRemoveFromEmptyCache ()
  {
    final var c = ProviderCache.builder ().valueProvider (x -> x).name ("MockRemoveEmpty").build ();
    assertEquals (EChange.UNCHANGED, c.removeFromCache ("a"));
  }

  @Test
  public void testClearCache ()
  {
    final var c = ProviderCache.builder ().valueProvider (x -> x).name ("MockClear").build ();
    assertEquals (EChange.UNCHANGED, c.clearCache ());

    c.getFromCache ("a");
    c.getFromCache ("b");
    assertEquals (2, c.size ());

    assertEquals (EChange.CHANGED, c.clearCache ());
    assertEquals (0, c.size ());
    assertTrue (c.isEmpty ());

    // Clearing an already empty cache
    assertEquals (EChange.UNCHANGED, c.clearCache ());
  }

  @Test
  public void testIsEmptyAndIsNotEmpty ()
  {
    final var c = ProviderCache.builder ().valueProvider (x -> x).name ("MockEmpty").build ();
    assertTrue (c.isEmpty ());
    assertFalse (c.isNotEmpty ());

    c.getFromCache ("x");
    assertFalse (c.isEmpty ());
    assertTrue (c.isNotEmpty ());
  }

  @Test
  public void testGetName ()
  {
    final var c = ProviderCache.builder ().valueProvider (x -> x).name ("MyName").build ();
    assertEquals ("MyName", c.getName ());
  }

  @Test
  public void testIsAllowNullValues ()
  {
    final var c1 = ProviderCache.builder ().valueProvider (x -> x).name ("Mock1").build ();
    assertFalse (c1.isAllowNullValues ());

    final var c2 = ProviderCache.builder ().valueProvider (x -> x).name ("Mock2").allowNullValues (true).build ();
    assertTrue (c2.isAllowNullValues ());
  }

  @Test
  public void testCacheHit ()
  {
    final var c = ProviderCache.builder ().valueProvider (x -> "v" + x).name ("MockHit").build ();
    assertEquals ("va", c.getFromCache ("a"));
    // Second call should be a cache hit, returning the same value
    assertEquals ("va", c.getFromCache ("a"));
    assertEquals (1, c.size ());
  }

  @Test
  public void testToString ()
  {
    final var c = ProviderCache.builder ().valueProvider (x -> x).name ("MockStr").build ();
    final String s = c.toString ();
    assertNotNull (s);
    assertTrue (s.contains ("MockStr"));
  }
}
