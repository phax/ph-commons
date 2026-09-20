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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Comparator;
import java.util.Map;

import org.junit.Test;

import com.helger.base.numeric.mutable.MutableInt;

/**
 * Test class for the default methods of {@link ICommonsMap}.
 *
 * @author Philip Helger
 */
public final class ICommonsMapTest
{
  private static ICommonsOrderedMap <String, String> _createMap ()
  {
    final ICommonsOrderedMap <String, String> ret = new CommonsLinkedHashMap <> ();
    ret.put ("k1", "v1");
    ret.put ("k2", "v2");
    ret.put ("k3", "v3");
    return ret;
  }

  @Test
  public void testCopyOf ()
  {
    final ICommonsMap <String, String> aMap = _createMap ();

    assertEquals (3, aMap.copyOfKeySet ().size ());
    assertEquals (1, aMap.copyOfKeySet (x -> x.equals ("k1")).size ());
    assertEquals (3, aMap.copyOfKeySet (null).size ());

    assertEquals (3, aMap.copyOfValues ().size ());
    assertEquals (1, aMap.copyOfValues (x -> x.equals ("v1")).size ());
    assertEquals (3, aMap.copyOfValues (null).size ());

    assertEquals (new CommonsArrayList <> ("#v1", "#v2", "#v3"), aMap.copyOfValuesMapped (x -> "#" + x));
    assertEquals (new CommonsArrayList <> ("#v1"),
                  aMap.copyOfValuesMapped (x -> x.equals ("v1"), x -> "#" + x));
    assertEquals (3, aMap.copyOfValuesMapped (null, x -> "#" + x).size ());

    assertEquals (3, aMap.copyOfEntrySet ().size ());

    // The copy is independent of the original
    final ICommonsSet <String> aKeys = aMap.copyOfKeySet ();
    aKeys.clear ();
    assertEquals (3, aMap.size ());
  }

  @Test
  public void testFirstAndFind ()
  {
    final ICommonsMap <String, String> aMap = _createMap ();
    assertTrue (aMap.isNotEmpty ());

    assertNotNull (aMap.getFirstEntry ());
    assertEquals ("k1", aMap.getFirstKey ());
    assertEquals ("v1", aMap.getFirstValue ());

    assertNotNull (aMap.findFirstEntry (x -> x.getKey ().equals ("k2")));
    assertEquals ("k2", aMap.findFirstKey (x -> x.getKey ().equals ("k2")));
    assertEquals ("v2", aMap.findFirstValue (x -> x.getKey ().equals ("k2")));
    assertNull (aMap.findFirstEntry (x -> false));
    assertNull (aMap.findFirstKey (x -> false));
    assertNull (aMap.findFirstValue (x -> false));
    assertNotNull (aMap.findFirstEntry (null));

    assertTrue (aMap.containsAnyEntry (x -> x.getKey ().equals ("k1")));
    assertFalse (aMap.containsAnyEntry (x -> false));
    assertTrue (aMap.containsAnyKey (x -> x.equals ("k1")));
    assertFalse (aMap.containsAnyKey (x -> false));
    assertTrue (aMap.containsAnyValue (x -> x.equals ("v1")));
    assertFalse (aMap.containsAnyValue (x -> false));

    // Empty map
    final ICommonsMap <String, String> aEmpty = new CommonsLinkedHashMap <> ();
    assertFalse (aEmpty.isNotEmpty ());
    assertNull (aEmpty.getFirstEntry ());
    assertNull (aEmpty.getFirstKey ());
    assertNull (aEmpty.getFirstValue ());
    assertEquals ("x", aEmpty.getFirstKey ("x"));
    assertEquals ("x", aEmpty.getFirstValue ("x"));
    assertNull (aEmpty.getFirstEntry (null));
    assertFalse (aEmpty.containsAnyEntry (x -> true));
    assertFalse (aEmpty.containsAnyKey (x -> true));
    assertFalse (aEmpty.containsAnyValue (x -> true));
  }

  @Test
  public void testForEach ()
  {
    final ICommonsMap <String, String> aMap = _createMap ();

    final MutableInt aCount = new MutableInt (0);
    aMap.forEachKey (x -> aCount.inc ());
    assertEquals (3, aCount.intValue ());

    aCount.set (0);
    aMap.forEachValue (x -> aCount.inc ());
    assertEquals (3, aCount.intValue ());

    aCount.set (0);
    aMap.forEach ( (k, v) -> k.equals ("k1"), (k, v) -> aCount.inc ());
    assertEquals (1, aCount.intValue ());

    aCount.set (0);
    aMap.forEach (null, (k, v) -> aCount.inc ());
    assertEquals (3, aCount.intValue ());

    aCount.set (0);
    aMap.forEachKey (x -> x.equals ("k1"), x -> aCount.inc ());
    assertEquals (1, aCount.intValue ());

    aCount.set (0);
    aMap.forEachKey (null, x -> aCount.inc ());
    assertEquals (3, aCount.intValue ());

    aCount.set (0);
    aMap.forEachValue (x -> x.equals ("v1"), x -> aCount.inc ());
    assertEquals (1, aCount.intValue ());

    aCount.set (0);
    aMap.forEachValue (null, x -> aCount.inc ());
    assertEquals (3, aCount.intValue ());
  }

  @Test
  public void testSortedAndSwapped ()
  {
    final ICommonsMap <String, String> aMap = _createMap ();

    final ICommonsOrderedMap <String, String> aSortedByKey = aMap.getSortedByKey (Comparator.reverseOrder ());
    assertEquals ("k3", aSortedByKey.getFirstKey ());

    final ICommonsOrderedMap <String, String> aSortedByValue = aMap.getSortedByValue (Comparator.reverseOrder ());
    assertEquals ("v3", aSortedByValue.getFirstValue ());

    final ICommonsMap <String, String> aSwapped = aMap.getSwappedKeyValues ();
    assertEquals (3, aSwapped.size ());
    assertEquals ("k1", aSwapped.get ("v1"));

    assertTrue (new CommonsLinkedHashMap <String, String> ().getSwappedKeyValues ().isEmpty ());
  }

  @Test
  public void testPut ()
  {
    final ICommonsMap <String, String> aMap = new CommonsLinkedHashMap <> ();

    aMap.put (_createMap ().getFirstEntry ());
    assertEquals ("v1", aMap.get ("k1"));

    aMap.putIf ("k2", "v2", x -> true);
    assertEquals ("v2", aMap.get ("k2"));
    aMap.putIf ("k3", "v3", x -> false);
    assertFalse (aMap.containsKey ("k3"));

    aMap.putIfNotNull ("k4", "v4");
    assertEquals ("v4", aMap.get ("k4"));
    aMap.putIfNotNull ("k5", null);
    assertFalse (aMap.containsKey ("k5"));

    assertEquals (3, aMap.size ());
  }

  @Test
  public void testPutAll ()
  {
    final ICommonsMap <String, String> aMap = new CommonsLinkedHashMap <> ();

    aMap.putAll (_createMap ().copyOfEntrySet ());
    assertEquals (3, aMap.size ());
    aMap.putAll ((Iterable <Map.Entry <String, String>>) null);
    assertEquals (3, aMap.size ());

    aMap.clear ();
    aMap.putAll (_createMap (), x -> x.getKey ().equals ("k1"));
    assertEquals (1, aMap.size ());
    aMap.putAll (_createMap (), null);
    assertEquals (3, aMap.size ());
    aMap.putAll (null, x -> true);
    assertEquals (3, aMap.size ());

    aMap.clear ();
    aMap.putAllMapped (new String [] { "a", "b" }, x -> x, x -> "#" + x);
    assertEquals (2, aMap.size ());
    assertEquals ("#a", aMap.get ("a"));
    aMap.putAllMapped ((String []) null, x -> x, x -> "#" + x);
    assertEquals (2, aMap.size ());

    aMap.clear ();
    aMap.putAllMapped (new CommonsArrayList <> ("a", "b"), x -> x, x -> "#" + x);
    assertEquals (2, aMap.size ());
    aMap.putAllMapped ((Iterable <String>) null, x -> x, x -> "#" + x);
    assertEquals (2, aMap.size ());

    aMap.clear ();
    aMap.putAllMapped (_createMap (), x -> "#" + x, x -> "#" + x);
    assertEquals (3, aMap.size ());
    assertEquals ("#v1", aMap.get ("#k1"));
    aMap.putAllMapped ((Map <String, String>) null, x -> "#" + x, x -> "#" + x);
    assertEquals (3, aMap.size ());

    aMap.clear ();
    // "IfNotNull" refers to the map, not to the contained values
    aMap.putAllIfNotNull (_createMap ());
    assertEquals (3, aMap.size ());
    aMap.putAllIfNotNull (null);
    assertEquals (3, aMap.size ());
  }

  @Test
  public void testSetAllAndRemove ()
  {
    final ICommonsMap <String, String> aMap = new CommonsLinkedHashMap <> ();
    assertFalse (aMap.setAll (null).isChanged ());
    assertTrue (aMap.setAll (_createMap ()).isChanged ());
    assertEquals (3, aMap.size ());

    assertTrue (aMap.removeObject ("k1").isChanged ());
    assertFalse (aMap.removeObject ("k1").isChanged ());
    assertFalse (aMap.removeObject (null).isChanged ());

    assertTrue (aMap.removeIf (x -> x.getKey ().equals ("k2")).isChanged ());
    assertFalse (aMap.removeIf (x -> x.getKey ().equals ("k2")).isChanged ());

    aMap.setAll (_createMap ());
    assertTrue (aMap.removeIfKey (x -> x.equals ("k1")).isChanged ());
    assertFalse (aMap.removeIfKey (x -> x.equals ("k1")).isChanged ());
    assertTrue (aMap.removeIfValue (x -> x.equals ("v2")).isChanged ());
    assertFalse (aMap.removeIfValue (x -> x.equals ("v2")).isChanged ());

    assertTrue (aMap.removeAll ().isChanged ());
    assertFalse (aMap.removeAll ().isChanged ());
  }

  @Test
  public void testUnmodifiable ()
  {
    final ICommonsMap <String, String> aMap = _createMap ();
    final Map <String, String> aUnmodifiable = aMap.getAsUnmodifiable ();
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
  public void testCreateInstance ()
  {
    final ICommonsMap <String, String> aMap = _createMap ();
    final ICommonsMap <Integer, Integer> aNew = aMap.createInstance ();
    assertNotNull (aNew);
    assertTrue (aNew.isEmpty ());
  }
}
