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
package com.helger.collection.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;

import org.junit.Test;

/**
 * Test class for the default methods of {@link ICommonsSortedMap}.
 *
 * @author Philip Helger
 */
public final class ICommonsSortedMapTest
{
  private static ICommonsSortedMap <String, String> _createMap ()
  {
    final ICommonsSortedMap <String, String> ret = new CommonsTreeMap <> ();
    ret.put ("k1", "v1");
    ret.put ("k2", "v2");
    ret.put ("k3", "v3");
    return ret;
  }

  @Test
  public void testCopyOf ()
  {
    final ICommonsSortedMap <String, String> aMap = _createMap ();

    final ICommonsSortedSet <String> aKeys = aMap.copyOfKeySet ();
    assertEquals (3, aKeys.size ());
    assertEquals ("k1", aKeys.getFirstOrNull ());

    assertEquals (1, aMap.copyOfKeySet (x -> x.equals ("k1")).size ());
    assertEquals (3, aMap.copyOfKeySet (null).size ());

    assertNotNull (aMap.createInstance ());
    assertTrue (aMap.<Integer, Integer> createInstance ().isEmpty ());
  }

  @Test
  public void testFirstAndLast ()
  {
    final ICommonsSortedMap <String, String> aMap = _createMap ();
    assertEquals ("k1", aMap.getFirstKey ());
    assertEquals ("k1", aMap.getFirstKey ("x"));
    assertEquals ("v1", aMap.getFirstValue ());
    assertEquals ("v1", aMap.getFirstValue ("x"));
    assertEquals ("k3", aMap.getLastKey ());
    assertEquals ("k3", aMap.getLastKey ("x"));
    assertEquals ("v3", aMap.getLastValue ());
    assertEquals ("v3", aMap.getLastValue ("x"));

    final ICommonsSortedMap <String, String> aEmpty = new CommonsTreeMap <> ();
    assertNull (aEmpty.getFirstKey ());
    assertEquals ("x", aEmpty.getFirstKey ("x"));
    assertNull (aEmpty.getFirstValue ());
    assertEquals ("x", aEmpty.getFirstValue ("x"));
    assertNull (aEmpty.getLastKey ());
    assertEquals ("x", aEmpty.getLastKey ("x"));
    assertNull (aEmpty.getLastValue ());
    assertEquals ("x", aEmpty.getLastValue ("x"));
  }

  @Test
  public void testUnmodifiable ()
  {
    final SortedMap <String, String> aUnmodifiable = _createMap ().getAsUnmodifiable ();
    assertEquals (3, aUnmodifiable.size ());
    try
    {
      aUnmodifiable.put ("k4", "v4");
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {
      // expected
    }
  }

  @Test
  public void testCopyOfEntrySet ()
  {
    // Natural key ordering
    final ICommonsSortedMap <String, String> aMap = _createMap ();
    final ICommonsSortedSet <Map.Entry <String, String>> aEntries = aMap.copyOfEntrySet ();
    assertEquals (3, aEntries.size ());
    assertEquals ("k1", aEntries.getFirstOrNull ().getKey ());
    assertEquals ("k3", aEntries.getLastOrNull ().getKey ());

    // A null value is allowed
    final ICommonsSortedMap <String, String> aWithNull = new CommonsTreeMap <> ();
    aWithNull.put ("k1", null);
    assertNull (aWithNull.copyOfEntrySet ().getFirstOrNull ().getValue ());

    // The copy is independent of the map
    aMap.clear ();
    assertEquals (3, aEntries.size ());
  }

  @Test
  public void testWithComparator ()
  {
    final ICommonsSortedMap <String, String> aMap = new CommonsTreeMap <> (Comparator.reverseOrder ());
    aMap.put ("k1", "v1");
    aMap.put ("k2", "v2");
    aMap.put ("k3", "v3");

    // The copies use the comparator of the map
    assertEquals ("k3", aMap.copyOfKeySet ().getFirstOrNull ());
    assertEquals ("k3", aMap.copyOfKeySet (x -> true).getFirstOrNull ());
    assertEquals ("k3", aMap.copyOfEntrySet ().getFirstOrNull ().getKey ());
    assertEquals (2, aMap.copyOfKeySet (x -> !x.equals ("k1")).size ());
  }
}
