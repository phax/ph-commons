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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link CommonsIdentityHashMap}.
 *
 * @author Philip Helger
 */
public final class CommonsIdentityHashMapTest
{
  @Test
  public void testBasic ()
  {
    final String sKey1 = "key";
    // Equal but not identical
    final String sKey2 = new String (sKey1);
    assertNotSame (sKey1, sKey2);

    final CommonsIdentityHashMap <String, String> aMap = new CommonsIdentityHashMap <> ();
    aMap.put (sKey1, "a");
    aMap.put (sKey2, "b");
    assertEquals (2, aMap.size ());
    assertEquals ("a", aMap.get (sKey1));
    assertEquals ("b", aMap.get (sKey2));
    assertTrue (aMap.containsKey (sKey1));
    assertFalse (aMap.containsKey (new String (sKey1)));

    TestHelper.testGetClone (aMap);
    final CommonsIdentityHashMap <String, String> aClone = aMap.getClone ();
    aClone.remove (sKey1);
    assertEquals (2, aMap.size ());
    assertEquals (1, aClone.size ());

    final CommonsIdentityHashMap <Integer, Integer> aNew = aMap.createInstance ();
    assertNotNull (aNew);
    assertTrue (aNew.isEmpty ());

    // Methods from ICommonsMap - the key set copy keeps the identity semantics
    assertEquals (2, aMap.copyOfKeySet ().size ());
    assertEquals (1, aMap.copyOfKeySet (x -> x == sKey2).size ());
    assertEquals (2, aMap.copyOfKeySet (null).size ());
  }

  @Test
  public void testCtor ()
  {
    CommonsIdentityHashMap <String, String> aMap = new CommonsIdentityHashMap <> (5);
    assertTrue (aMap.isEmpty ());

    aMap = new CommonsIdentityHashMap <> ((Map <String, String>) null);
    assertTrue (aMap.isEmpty ());

    aMap = new CommonsIdentityHashMap <> (new String [] { "a", "b" }, x -> x, x -> x + x);
    assertEquals ("aa", aMap.get ("a"));

    aMap = new CommonsIdentityHashMap <> (new CommonsArrayList <> ("c", "d"), x -> x, x -> x + x);
    assertEquals ("dd", aMap.get ("d"));

    final CommonsHashMap <Integer, String> aSrc = new CommonsHashMap <> ();
    aSrc.put (Integer.valueOf (1), "x");
    final CommonsIdentityHashMap <Integer, String> aMap2 = new CommonsIdentityHashMap <> (aSrc, x -> x, x -> x + "y");
    assertEquals (1, aMap2.size ());
    assertEquals ("xy", aMap2.values ().iterator ().next ());

    final CommonsHashMap <String, String> aSrc2 = new CommonsHashMap <> ();
    aSrc2.put ("e", "f");
    aMap = new CommonsIdentityHashMap <> (aSrc2);
    assertEquals (1, aMap.size ());
  }
}
