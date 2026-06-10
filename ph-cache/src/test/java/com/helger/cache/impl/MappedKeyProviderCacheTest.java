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

import org.junit.Test;

import com.helger.base.state.EChange;

/**
 * Test class for class {@link MappedKeyProviderCache}.
 *
 * @author Philip Helger
 */
public final class MappedKeyProviderCacheTest
{
  @Test
  public void testKeyMappingAndCacheHit ()
  {
    final var c = MappedKeyProviderCache.<BigDecimal, String, String> builder ()
                                        .name ("Mock")
                                        .keyMapper (BigDecimal::toString)
                                        .valueProvider (k -> "v" + k.toPlainString ())
                                        .build ();
    // Provider invoked, value stored under storage key "0"
    assertEquals ("v0", c.getFromCache (BigDecimal.ZERO));
    assertEquals (1, c.size ());

    // Different KEYTYPE values mapping to the same storage key collapse onto a single entry
    assertEquals ("v0", c.getFromCache (new BigDecimal ("0")));
    assertEquals (1, c.size ());

    // Genuinely different storage key adds a new entry
    assertEquals ("v10", c.getFromCache (BigDecimal.TEN));
    assertEquals (2, c.size ());
  }

  @Test
  public void testIsInCache ()
  {
    final var c = MappedKeyProviderCache.<BigDecimal, String, String> builder ()
                                        .name ("Mock")
                                        .keyMapper (BigDecimal::toString)
                                        .valueProvider (k -> "v" + k.toPlainString ())
                                        .build ();
    assertFalse (c.isInCache (BigDecimal.ONE));
    c.getFromCache (BigDecimal.ONE);
    assertTrue (c.isInCache (BigDecimal.ONE));
    // Storage-key alias is also "in cache"
    assertTrue (c.isInCache (new BigDecimal ("1")));
    assertFalse (c.isInCache (BigDecimal.TEN));
  }

  @Test
  public void testPutInCache ()
  {
    final var c = MappedKeyProviderCache.<BigDecimal, String, String> builder ()
                                        .name ("MockPut")
                                        .keyMapper (BigDecimal::toString)
                                        .valueProvider (k -> "v" + k.toPlainString ())
                                        .build ();
    c.putInCache (BigDecimal.ONE, "prefilled");
    assertEquals (1, c.size ());
    assertTrue (c.isInCache (BigDecimal.ONE));
    // Cache hit -- provider must not be invoked
    assertEquals ("prefilled", c.getFromCache (BigDecimal.ONE));
    assertEquals (1, c.size ());

    // Cache miss for a key not pre-filled -- provider is invoked
    assertEquals ("v2", c.getFromCache (new BigDecimal ("2")));
    assertEquals (2, c.size ());
  }

  @Test
  public void testRemoveFromCache ()
  {
    final var c = MappedKeyProviderCache.<BigDecimal, String, String> builder ()
                                        .name ("MockRemove")
                                        .keyMapper (BigDecimal::toString)
                                        .valueProvider (k -> "v" + k.toPlainString ())
                                        .build ();
    c.getFromCache (BigDecimal.ONE);
    c.getFromCache (BigDecimal.TEN);
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
    final var c = MappedKeyProviderCache.<BigDecimal, String, String> builder ()
                                        .name ("MockClear")
                                        .keyMapper (BigDecimal::toString)
                                        .valueProvider (k -> "v" + k.toPlainString ())
                                        .build ();
    assertEquals (EChange.UNCHANGED, c.clearCache ());

    c.getFromCache (BigDecimal.ONE);
    c.getFromCache (BigDecimal.TEN);
    assertEquals (2, c.size ());

    assertEquals (EChange.CHANGED, c.clearCache ());
    assertTrue (c.isEmpty ());
    assertEquals (EChange.UNCHANGED, c.clearCache ());
  }

  @Test
  public void testMaxSize ()
  {
    final int nMaxSize = 3;
    final var c = MappedKeyProviderCache.<Integer, String, Integer> builder ()
                                        .name ("MockSize")
                                        .maxSize (nMaxSize)
                                        .keyMapper (Object::toString)
                                        .valueProvider (k -> k)
                                        .build ();
    assertTrue (c.hasMaxSize ());
    assertEquals (nMaxSize, c.getMaxSize ());

    for (int i = 0; i < nMaxSize * 2; ++i)
      c.getFromCache (Integer.valueOf (i));
    assertEquals (nMaxSize, c.size ());
    // Earliest entries evicted, latest kept
    assertFalse (c.isInCache (Integer.valueOf (0)));
    assertFalse (c.isInCache (Integer.valueOf (2)));
    assertTrue (c.isInCache (Integer.valueOf (3)));
    assertTrue (c.isInCache (Integer.valueOf (5)));
  }

  @Test
  public void testNullStorageKey ()
  {
    // Key mapper returns null for value 0 -> getFromCache must throw IllegalStateException
    final var c = MappedKeyProviderCache.<Integer, String, String> builder ()
                                        .name ("MockNullKey")
                                        .keyMapper (k -> k.intValue () == 0 ? null : k.toString ())
                                        .valueProvider (k -> "v" + k.intValue ())
                                        .build ();
    // Normal key works
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

    // isInCache must swallow the null-storage-key exception and report false
    assertFalse (c.isInCache (Integer.valueOf (0)));
  }

  @Test
  public void testNullValueAllowed ()
  {
    final var c = MappedKeyProviderCache.<Integer, String, String> builder ()
                                        .name ("MockNullVal")
                                        .allowNullValues (true)
                                        .keyMapper (Object::toString)
                                        .valueProvider (k -> k.intValue () == 0 ? null : "v" + k.intValue ())
                                        .build ();
    assertEquals ("v1", c.getFromCache (Integer.valueOf (1)));
    assertNull (c.getFromCache (Integer.valueOf (0)));
    assertEquals (2, c.size ());
    assertTrue (c.isInCache (Integer.valueOf (0)));
  }

  @Test
  public void testNullValueNotAllowed ()
  {
    final var c = MappedKeyProviderCache.<Integer, String, String> builder ()
                                        .name ("MockNoNullVal")
                                        .keyMapper (Object::toString)
                                        .valueProvider (k -> k.intValue () == 0 ? null : "v" + k.intValue ())
                                        .build ();
    assertEquals ("v1", c.getFromCache (Integer.valueOf (1)));
    try
    {
      c.getFromCache (Integer.valueOf (0));
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected -- null value not allowed
    }
    assertEquals (1, c.size ());
  }

  @Test
  public void testIsEmptyAndIsNotEmpty ()
  {
    final var c = MappedKeyProviderCache.<BigDecimal, String, String> builder ()
                                        .name ("MockEmpty")
                                        .keyMapper (BigDecimal::toString)
                                        .valueProvider (k -> "v")
                                        .build ();
    assertTrue (c.isEmpty ());
    assertFalse (c.isNotEmpty ());

    c.getFromCache (BigDecimal.ONE);
    assertFalse (c.isEmpty ());
    assertTrue (c.isNotEmpty ());
  }

  @Test
  public void testGetNameAndDefaults ()
  {
    final var c = MappedKeyProviderCache.<BigDecimal, String, String> builder ()
                                        .name ("MyName")
                                        .keyMapper (BigDecimal::toString)
                                        .valueProvider (k -> "v")
                                        .build ();
    assertEquals ("MyName", c.getName ());
    assertFalse (c.isAllowNullValues ());
    assertFalse (c.hasMaxSize ());
    assertNull (c.getTimeToLive ());
    assertNotNull (c.getClockSupplier ());
  }

  @Test
  public void testBuilderMissingKeyMapper ()
  {
    try
    {
      MappedKeyProviderCache.<BigDecimal, String, String> builder ().name ("Mock").valueProvider (k -> "v").build ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testBuilderMissingValueProvider ()
  {
    try
    {
      MappedKeyProviderCache.<BigDecimal, String, String> builder ()
                            .name ("Mock")
                            .keyMapper (BigDecimal::toString)
                            .build ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testToString ()
  {
    final var c = MappedKeyProviderCache.<BigDecimal, String, String> builder ()
                                        .name ("MockStr")
                                        .keyMapper (BigDecimal::toString)
                                        .valueProvider (k -> "v")
                                        .build ();
    final String s = c.toString ();
    assertNotNull (s);
    assertTrue (s.contains ("MockStr"));
    assertTrue (s.contains ("KeyMapper"));
    assertTrue (s.contains ("ValueProvider"));
  }
}
