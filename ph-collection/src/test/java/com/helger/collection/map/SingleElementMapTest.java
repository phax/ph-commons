/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.collection.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.helger.collection.helper.CollectionHelperExt;

/**
 * Test class for class {@link SingleElementMap}.
 *
 * @author Philip Helger
 */
public final class SingleElementMapTest
{
  private static final Integer I1 = Integer.valueOf (1);
  private static final Integer I2 = Integer.valueOf (2);
  private static final Integer I3 = Integer.valueOf (3);
  private static final Integer I4 = Integer.valueOf (4);
  private static final Integer I5 = Integer.valueOf (5);
  private static final Integer I6 = Integer.valueOf (6);

  @Test
  public void testEmptyCtor ()
  {
    final Map <String, Integer> aMap = new SingleElementMap <> ();
    assertEquals (0, aMap.size ());
    assertTrue (aMap.isEmpty ());
    assertFalse (aMap.containsKey ("any"));
    aMap.clear ();
    assertEquals (0, aMap.size ());
    assertTrue (aMap.isEmpty ());
    assertFalse (aMap.containsKey ("any"));
    assertFalse (aMap.containsKey (null));
  }

  @Test
  public void testNonEmptyCtor ()
  {
    final Map <String, Integer> aMap = new SingleElementMap <> ("any", I5);
    assertEquals (1, aMap.size ());
    assertFalse (aMap.isEmpty ());
    assertTrue (aMap.containsKey ("any"));
    assertFalse (aMap.containsKey ("dummy"));
    assertFalse (aMap.containsKey (null));
    assertTrue (aMap.containsValue (I5));
    assertFalse (aMap.containsValue (I6));
    assertFalse (aMap.containsValue (null));
    assertEquals (I5, aMap.get ("any"));
    assertNull (aMap.get ("dummy"));
    aMap.clear ();
    assertEquals (0, aMap.size ());
    assertTrue (aMap.isEmpty ());
    assertFalse (aMap.containsKey ("any"));

    assertNotNull (aMap.toString ());
  }

  @Test
  public void testRemove ()
  {
    final Map <String, Integer> aMap = new SingleElementMap <> ("any", I5);
    assertNull (aMap.remove ("dummy"));
    assertEquals (I5, aMap.remove ("any"));
    assertEquals (0, aMap.size ());
    assertTrue (aMap.isEmpty ());
    assertFalse (aMap.containsKey ("any"));
    assertEquals (null, aMap.remove ("any"));
    assertEquals (null, aMap.remove (null));
  }

  @Test
  public void testPut ()
  {
    final Map <String, Integer> aMap = new SingleElementMap <> ();
    assertEquals (0, aMap.size ());
    assertNull (aMap.put ("any", I1));
    assertEquals (1, aMap.size ());
    assertEquals (I1, aMap.put ("any", I1));
    assertEquals (1, aMap.size ());
    assertEquals (I1, aMap.put ("any", I2));
    assertEquals (1, aMap.size ());
    assertEquals (I2, aMap.put ("any", I3));
    assertEquals (1, aMap.size ());
    assertEquals (I3, aMap.put ("any", null));
    assertEquals (1, aMap.size ());
    assertEquals (null, aMap.put ("any", null));
    assertEquals (1, aMap.size ());
    assertEquals (null, aMap.put ("any", I2));

    // Works, because exactly one element
    aMap.putAll (aMap);

    // Empty map also ok
    aMap.putAll (new HashMap <> ());

    try
    {
      // too many items
      aMap.putAll (CollectionHelperExt.createMap (new String [] { "x", "y" }, new Integer [] { I1, I2 }));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testEquals ()
  {
    final Map <String, Integer> aMap = new SingleElementMap <> ();
    final Map <String, Integer> aMap2 = new SingleElementMap <> ();
    assertEquals (aMap, aMap);
    assertEquals (aMap, aMap2);
    assertEquals (aMap2, aMap);
    assertNotEquals (aMap, null);
    assertNotEquals (aMap, "other");
    assertEquals (aMap.hashCode (), aMap.hashCode ());
    assertEquals (aMap.hashCode (), aMap2.hashCode ());
    aMap.put ("any", I2);
    assertNotEquals (aMap, aMap2);
    assertNotEquals (aMap2, aMap);
    assertEquals (aMap.hashCode (), aMap.hashCode ());
    assertNotEquals (aMap.hashCode (), aMap2.hashCode ());
    aMap2.put ("any", I2);
    assertEquals (aMap, aMap2);
    assertEquals (aMap2, aMap);
    assertEquals (aMap.hashCode (), aMap2.hashCode ());
  }

  @Test
  public void testCollections ()
  {
    final Map <String, Integer> aMap = new SingleElementMap <> ();
    assertEquals (0, aMap.size ());
    assertEquals (0, aMap.size ());
    assertEquals (0, aMap.entrySet ().size ());
    aMap.put ("any", I4);
    assertEquals (1, aMap.size ());
    assertEquals (1, aMap.size ());
    assertEquals (1, aMap.entrySet ().size ());
    aMap.put ("other", I5);
    assertEquals (1, aMap.size ());
    assertEquals (1, aMap.size ());
    assertEquals (1, aMap.entrySet ().size ());
    assertTrue (aMap.containsKey ("other"));
    assertTrue (aMap.containsValue (I5));
    assertTrue (aMap.entrySet ().iterator ().next ().getKey ().equals ("other"));
  }
}
