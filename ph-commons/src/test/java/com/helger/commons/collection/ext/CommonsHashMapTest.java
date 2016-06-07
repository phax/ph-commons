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
package com.helger.commons.collection.ext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Comparator;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link CommonsHashMap}.
 *
 * @author Philip Helger
 */
public final class CommonsHashMapTest
{
  @Test
  public void testBasic ()
  {
    final ICommonsMap <String, String> aTest = new CommonsHashMap<> ();
    aTest.put ("aaa", "bla");
    aTest.put ("bbb", "blb");
    aTest.put ("ccc", "blc");

    final ICommonsSet <String> aSortedKeys = aTest.getSortedByKey (Comparator.naturalOrder ()).copyOfKeySet ();
    assertEquals ("aaa", aSortedKeys.getAtIndex (0));
    assertEquals ("bbb", aSortedKeys.getAtIndex (1));
    assertEquals ("ccc", aSortedKeys.getAtIndex (2));

    CommonsTestHelper.testDefaultSerialization (aTest);
    CommonsTestHelper.testGetClone (aTest);
  }

  @Test
  public void testGetSwappedKeyValues ()
  {
    final ICommonsMap <String, Integer> aMap = CollectionHelper.newMap (new String [] { "a", "b", "c" },
                                                                        new Integer [] { Integer.valueOf (0),
                                                                                         Integer.valueOf (1),
                                                                                         Integer.valueOf (2) });
    final ICommonsMap <Integer, String> aMap2 = aMap.getSwappedKeyValues ();
    assertEquals (aMap.size (), aMap2.size ());
    assertEquals (aMap, aMap2.getSwappedKeyValues ());
    assertNotNull (new CommonsHashMap<> ().getSwappedKeyValues ());
  }
}
