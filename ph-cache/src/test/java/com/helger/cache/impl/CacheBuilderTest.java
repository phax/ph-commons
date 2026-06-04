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

/**
 * Test class for class {@link CacheBuilder}.
 *
 * @author Philip Helger
 */
public final class CacheBuilderTest
{
  @Test
  public void testBasicBuilder ()
  {
    final var c = new CacheBuilder <String, String> ().valueProvider (x -> x).name ("TestCache").buildManualCache ();
    assertNotNull (c);
    assertEquals ("TestCache", c.getName ());
    assertFalse (c.hasMaxSize ());
    assertFalse (c.isAllowNullValues ());
  }

  @Test
  public void testBuilderWithValueProvider ()
  {
    final var c = new CacheBuilder <String, String> ().valueProvider (x -> "v" + x)
                                                      .name ("TestCache2")
                                                      .buildProviderCache ();
    assertNotNull (c);
    assertEquals ("vfoo", c.getFromCache ("foo"));
    assertEquals (1, c.size ());
  }

  @Test
  public void testBuilderWithMaxSize ()
  {
    final var c = new CacheBuilder <String, String> ().valueProvider (x -> x)
                                                      .maxSize (3)
                                                      .name ("MaxSizeCache")
                                                      .buildProviderCache ();
    assertTrue (c.hasMaxSize ());
    assertEquals (3, c.getMaxSize ());

    for (int i = 0; i < 6; ++i)
      c.getFromCache ("key" + i);
    assertEquals (3, c.size ());
  }

  @Test
  public void testBuilderWithAllowNullValues ()
  {
    final var c = new CacheBuilder <String, String> ().valueProvider (x -> null)
                                                      .name ("NullCache")
                                                      .allowNullValues (true)
                                                      .buildProviderCache ();
    assertTrue (c.isAllowNullValues ());
    assertNull (c.getFromCache ("any"));
    assertEquals (1, c.size ());
  }

  @Test
  public void testBuilderMissingValueProvider ()
  {
    try
    {
      new CacheBuilder <String, String> ().name ("NoProvider").buildProviderCache ();
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
      new CacheBuilder <String, String> ().valueProvider (x -> x).buildManualCache ();
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
      new CacheBuilder <String, String> ().valueProvider (x -> x).name ("").buildManualCache ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }
}
