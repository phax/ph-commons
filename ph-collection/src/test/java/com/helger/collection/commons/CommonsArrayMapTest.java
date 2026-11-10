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

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Test;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link CommonsArrayMap}.
 *
 * @author Philip Helger
 */
public final class CommonsArrayMapTest
{
  @Test
  public void testBasic ()
  {
    final CommonsArrayMap <String, Integer> aMap = new CommonsArrayMap <> ();
    assertTrue (aMap.isEmpty ());
    assertEquals (0, aMap.size ());
    assertNull (aMap.get ("a"));
    assertNull (aMap.remove ("a"));
    assertFalse (aMap.containsKey ("a"));

    // Grow beyond the initial capacity
    for (int i = 0; i < 10; ++i)
      assertNull (aMap.put ("k" + i, Integer.valueOf (i)));
    assertEquals (10, aMap.size ());
    for (int i = 0; i < 10; ++i)
      assertEquals (Integer.valueOf (i), aMap.get ("k" + i));

    // Replace keeps the order
    assertEquals (Integer.valueOf (0), aMap.put ("k0", Integer.valueOf (100)));
    assertEquals ("k0", aMap.getFirstKey ());
    assertEquals ("k9", aMap.getLastKey ());
    assertEquals (Integer.valueOf (100), aMap.getFirstValue ());

    assertTrue (aMap.containsValue (Integer.valueOf (100)));
    assertFalse (aMap.containsValue (Integer.valueOf (0)));

    // Remove from the middle keeps the order
    assertEquals (Integer.valueOf (5), aMap.remove ("k5"));
    assertEquals (new CommonsArrayList <> ("k0", "k1", "k2", "k3", "k4", "k6", "k7", "k8", "k9"),
                  new CommonsArrayList <> (aMap.keySet ()));

    aMap.clear ();
    assertTrue (aMap.isEmpty ());
    assertNull (aMap.get ("k1"));
    aMap.put ("x", Integer.valueOf (1));
    assertEquals (1, aMap.size ());
  }

  @Test
  public void testNullKeysAndValues ()
  {
    final CommonsArrayMap <String, String> aMap = new CommonsArrayMap <> (0);
    assertNull (aMap.put (null, "a"));
    assertNull (aMap.put ("b", null));
    assertTrue (aMap.containsKey (null));
    assertTrue (aMap.containsKey ("b"));
    assertTrue (aMap.containsValue (null));
    assertTrue (aMap.containsValue ("a"));
    assertEquals ("a", aMap.get (null));
    assertNull (aMap.get ("b"));
    assertEquals ("a", aMap.put (null, "c"));
    assertEquals (2, aMap.size ());
    assertEquals ("c", aMap.remove (null));
    assertFalse (aMap.containsKey (null));
    assertEquals (1, aMap.size ());
  }

  @Test
  public void testMapContract ()
  {
    final CommonsArrayMap <String, String> aMap = new CommonsArrayMap <> ();
    final Map <String, String> aRef = new LinkedHashMap <> ();
    for (final String s : new String [] { "x", "a", "m", "b" })
    {
      aMap.put (s, s + s);
      aRef.put (s, s + s);
    }
    aMap.put (null, null);
    aRef.put (null, null);

    // equals and hashCode in both directions
    assertEquals (aRef, aMap);
    assertEquals (aMap, aRef);
    assertEquals (aRef.hashCode (), aMap.hashCode ());
    assertEquals (aRef.toString (), aMap.toString ());
    assertEquals (aRef.entrySet (), aMap.entrySet ());
    assertEquals (aRef.keySet (), aMap.keySet ());
    assertEquals (new CommonsArrayList <> (aRef.values ()), new CommonsArrayList <> (aMap.values ()));
    assertEquals (new CommonsLinkedHashMap <> (aRef), aMap);

    aMap.put ("x", "other");
    assertFalse (aMap.equals (aRef));
    assertFalse (aRef.equals (aMap));

    TestHelper.testGetClone (aMap);
    TestHelper.testDefaultImplementationWithEqualContentObject (aMap, new CommonsArrayMap <> (aMap));
    final CommonsArrayMap <String, String> aOther = aMap.getClone ();
    aOther.put ("new", "value");
    TestHelper.testDefaultImplementationWithDifferentContentObject (aMap, aOther);
    assertFalse (aMap.containsKey ("new"));
    TestHelper.testDefaultSerialization (aMap);
  }

  @SuppressWarnings ("unlikely-arg-type")
  @Test
  public void testViews ()
  {
    final CommonsArrayMap <String, Integer> aMap = new CommonsArrayMap <> ();
    for (int i = 0; i < 6; ++i)
      aMap.put ("k" + i, Integer.valueOf (i));

    // Remove via entry set iterator
    final Iterator <Map.Entry <String, Integer>> itE = aMap.entrySet ().iterator ();
    while (itE.hasNext ())
    {
      final Map.Entry <String, Integer> e = itE.next ();
      if ((e.getValue ().intValue () % 2) == 0)
        itE.remove ();
    }
    assertEquals (new CommonsArrayList <> ("k1", "k3", "k5"), new CommonsArrayList <> (aMap.keySet ()));
    try
    {
      itE.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {
      // expected
    }

    // Remove via key set iterator
    final Iterator <String> itK = aMap.keySet ().iterator ();
    assertEquals ("k1", itK.next ());
    itK.remove ();
    try
    {
      itK.remove ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
    assertEquals (2, aMap.size ());

    // Remove via values view
    final Collection <Integer> aValues = aMap.values ();
    assertTrue (aValues.remove (Integer.valueOf (3)));
    assertEquals (1, aMap.size ());
    assertEquals (Integer.valueOf (5), aMap.get ("k5"));

    // Write through via Entry.setValue
    for (final Map.Entry <String, Integer> e : aMap.entrySet ())
      e.setValue (Integer.valueOf (55));
    assertEquals (Integer.valueOf (55), aMap.get ("k5"));

    // Entry set contains/remove
    assertTrue (aMap.entrySet ().contains (new MapEntry <> ("k5", Integer.valueOf (55))));
    assertFalse (aMap.entrySet ().contains (new MapEntry <> ("k5", Integer.valueOf (5))));
    assertFalse (aMap.entrySet ().contains ("k5"));
    assertFalse (aMap.entrySet ().remove (new MapEntry <> ("k5", Integer.valueOf (5))));
    assertTrue (aMap.entrySet ().remove (new MapEntry <> ("k5", Integer.valueOf (55))));
    assertTrue (aMap.isEmpty ());

    // Key set and values clear the map
    aMap.put ("a", Integer.valueOf (1));
    aMap.clear ();
    assertTrue (aMap.isEmpty ());
    aMap.put ("a", Integer.valueOf (1));
    aMap.entrySet ().clear ();
    assertTrue (aMap.isEmpty ());

    // Fail fast
    aMap.put ("a", Integer.valueOf (1));
    aMap.put ("b", Integer.valueOf (2));
    final Iterator <String> it = aMap.keySet ().iterator ();
    it.next ();
    aMap.put ("c", Integer.valueOf (3));
    try
    {
      it.next ();
      fail ();
    }
    catch (final ConcurrentModificationException ex)
    {
      // expected
    }
  }

  @Test
  public void testCtor ()
  {
    CommonsArrayMap <String, String> aMap = new CommonsArrayMap <> ((Map <String, String>) null);
    assertTrue (aMap.isEmpty ());

    aMap = new CommonsArrayMap <> (new String [] { "a", "b" }, x -> x, x -> x + x);
    assertEquals ("bb", aMap.get ("b"));
    assertEquals ("a", aMap.getFirstKey ());

    aMap = new CommonsArrayMap <> (new CommonsArrayList <> ("c", "d"), x -> x, x -> x + x);
    assertEquals ("cc", aMap.get ("c"));

    aMap = new CommonsArrayMap <> (new CommonsArrayMap <> (aMap), x -> x + "k", x -> x + "v");
    assertEquals ("ccv", aMap.get ("ck"));
    assertEquals ("ck", aMap.getFirstKey ());

    try
    {
      new CommonsArrayMap <> (-1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }

    final CommonsArrayMap <Integer, String> aNew = aMap.createInstance ();
    assertNotNull (aNew);
    assertTrue (aNew.isEmpty ());
  }
}
