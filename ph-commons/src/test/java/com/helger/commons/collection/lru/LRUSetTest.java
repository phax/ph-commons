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
package com.helger.commons.collection.lru;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.collection.PrimitiveCollectionHelper;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test for class {@link LRUSet}
 *
 * @author Philip Helger
 */
public final class LRUSetTest
{
  private static final int MAX_SIZE = 5;

  @Test
  public void testLRUCache ()
  {
    final LRUSet <Integer> aCache = new LRUSet <> (MAX_SIZE);
    assertTrue (aCache.isEmpty ());
    assertEquals (0, aCache.size ());
    assertEquals (MAX_SIZE, aCache.getMaxSize ());
    assertNotNull (aCache.iterator ());
    assertFalse (aCache.iterator ().hasNext ());

    for (int i = 0; i < MAX_SIZE * 2; ++i)
    {
      assertEquals (i < MAX_SIZE ? i : MAX_SIZE, aCache.size ());
      assertTrue (aCache.add (Integer.valueOf (i)));
    }
    assertEquals (MAX_SIZE, aCache.size ());

    // add the same again
    assertTrue (aCache.add (Integer.valueOf (-3)));
    assertFalse (aCache.add (Integer.valueOf (-3)));
    assertEquals (MAX_SIZE, aCache.size ());

    // addAll
    assertTrue (aCache.addAll (PrimitiveCollectionHelper.newPrimitiveList (-4, -5)));
    assertTrue (aCache.addAll (PrimitiveCollectionHelper.newPrimitiveList (-4, -6)));
    assertEquals (MAX_SIZE, aCache.size ());

    // containsAll
    assertTrue (aCache.containsAll (PrimitiveCollectionHelper.newPrimitiveList (-4, -5)));
    assertFalse (aCache.containsAll (PrimitiveCollectionHelper.newPrimitiveList (-4, -7)));
    assertEquals (MAX_SIZE, aCache.size ());

    // removeAll
    assertTrue (aCache.removeAll (PrimitiveCollectionHelper.newPrimitiveList (-4, -5, -6)));
    assertFalse (aCache.removeAll (PrimitiveCollectionHelper.newPrimitiveList (-4, -5, -6)));
    assertEquals (MAX_SIZE - 3, aCache.size ());

    try
    {
      aCache.retainAll (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    assertEquals (2, aCache.toArray ().length);
    assertEquals (2, aCache.toArray (new Integer [aCache.size ()]).length);
    aCache.clear ();
    assertEquals (0, aCache.size ());

    final LRUSet <String> ret = new LRUSet <String> (4);
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (ret, new LRUSet <> (4));
    ret.add ("a");
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (ret, new LRUSet <> (4));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new LRUSet <> (3), new LRUSet <> (4));
  }
}
