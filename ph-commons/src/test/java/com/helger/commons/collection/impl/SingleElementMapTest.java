/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.collection.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.mock.AbstractCommonsTestCase;

/**
 * Test class for class {@link SingleElementMap}.
 *
 * @author Philip Helger
 */
public final class SingleElementMapTest extends AbstractCommonsTestCase
{
  @Test
  public void testEmptyCtor ()
  {
    final Map <String, Integer> aMap = new SingleElementMap <String, Integer> ();
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
    final Map <String, Integer> aMap = new SingleElementMap <String, Integer> ("any", I5);
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
    final Map <String, Integer> aMap = new SingleElementMap <String, Integer> ("any", I5);
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
    final Map <String, Integer> aMap = new SingleElementMap <String, Integer> ();
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
    aMap.putAll (new HashMap <String, Integer> ());

    try
    {
      // too many items
      aMap.putAll (CollectionHelper.newMap (new String [] { "x", "y" }, new Integer [] { I1, I2 }));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testEquals ()
  {
    final Map <String, Integer> aMap = new SingleElementMap <String, Integer> ();
    final Map <String, Integer> aMap2 = new SingleElementMap <String, Integer> ();
    assertEquals (aMap, aMap);
    assertEquals (aMap, aMap2);
    assertFalse (aMap.equals (null));
    assertFalse (aMap.equals ("other"));
    assertEquals (aMap.hashCode (), aMap.hashCode ());
    assertEquals (aMap.hashCode (), aMap2.hashCode ());
    aMap.put ("any", I2);
    assertFalse (aMap.equals (aMap2));
    assertFalse (aMap2.equals (aMap));
    assertTrue (aMap.hashCode () == aMap.hashCode ());
    assertFalse (aMap.hashCode () == aMap2.hashCode ());
    aMap2.put ("any", I2);
    assertEquals (aMap, aMap2);
    assertEquals (aMap2, aMap);
    assertEquals (aMap.hashCode (), aMap2.hashCode ());
  }

  @Test
  public void testCollections ()
  {
    final Map <String, Integer> aMap = new SingleElementMap <String, Integer> ();
    assertEquals (0, aMap.keySet ().size ());
    assertEquals (0, aMap.values ().size ());
    assertEquals (0, aMap.entrySet ().size ());
    aMap.put ("any", I4);
    assertEquals (1, aMap.keySet ().size ());
    assertEquals (1, aMap.values ().size ());
    assertEquals (1, aMap.entrySet ().size ());
    aMap.put ("other", I5);
    assertEquals (1, aMap.keySet ().size ());
    assertEquals (1, aMap.values ().size ());
    assertEquals (1, aMap.entrySet ().size ());
    assertTrue (aMap.keySet ().contains ("other"));
    assertTrue (aMap.values ().contains (I5));
    assertTrue (aMap.entrySet ().iterator ().next ().getKey ().equals ("other"));
  }
}
