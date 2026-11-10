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
package com.helger.collection.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Test for class {@link LRUMap}
 *
 * @author Philip Helger
 */
public final class LRUMapTest
{
  private static final int MAX_SIZE = 5;

  @Test
  public void testLRUCache ()
  {
    final LRUMap <Integer, String> aCache = new LRUMap <> (MAX_SIZE);
    for (int i = 0; i < MAX_SIZE * 2; ++i)
    {
      if (i < MAX_SIZE)
        assertEquals (i, aCache.size ());
      else
        assertEquals (MAX_SIZE, aCache.size ());
      assertNull (aCache.put (Integer.valueOf (i), "Hallo Welt"));
    }
    assertEquals (MAX_SIZE, aCache.size ());
  }

  @Test
  public void testHashCodeIndependentOfAccessOrder ()
  {
    final LRUMap <String, String> aMap1 = new LRUMap <> (MAX_SIZE);
    aMap1.put ("a", "1");
    aMap1.put ("b", "2");
    final LRUMap <String, String> aMap2 = new LRUMap <> (MAX_SIZE);
    aMap2.put ("b", "2");
    aMap2.put ("a", "1");
    assertEquals (aMap1, aMap2);
    assertEquals (aMap1.hashCode (), aMap2.hashCode ());

    // Access changes the order, but neither equals nor the hash code
    aMap1.get ("a");
    assertEquals (aMap1, aMap2);
    assertEquals (aMap1.hashCode (), aMap2.hashCode ());
  }
}
