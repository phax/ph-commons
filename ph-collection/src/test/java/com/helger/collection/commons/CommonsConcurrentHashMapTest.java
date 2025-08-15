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
package com.helger.collection.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Comparator;
import java.util.function.Function;

import org.junit.Test;

import com.helger.collection.helper.CollectionHelperExt;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link CommonsConcurrentHashMap}.
 *
 * @author Philip Helger
 */
public final class CommonsConcurrentHashMapTest
{
  @Test
  public void testBasic ()
  {
    final ICommonsMap <String, String> aTest = new CommonsConcurrentHashMap <> ();
    aTest.put ("aaa", "bla");
    aTest.put ("bbb", "blb");
    aTest.put ("ccc", "blc");

    final ICommonsSet <String> aSortedKeys = aTest.getSortedByKey (Comparator.naturalOrder ()).copyOfKeySet ();
    assertEquals ("aaa", aSortedKeys.getAtIndex (0));
    assertEquals ("bbb", aSortedKeys.getAtIndex (1));
    assertEquals ("ccc", aSortedKeys.getAtIndex (2));

    TestHelper.testGetClone (aTest);
  }

  @Test
  public void testCtor ()
  {
    CommonsConcurrentHashMap <String, Integer> aTest = new CommonsConcurrentHashMap <> ();
    assertEquals (0, aTest.size ());
    aTest = new CommonsConcurrentHashMap <> (7_000_123);
    assertEquals (0, aTest.size ());
    aTest = new CommonsConcurrentHashMap <> (7_000_123, 0.1f);
    assertEquals (0, aTest.size ());
    aTest = new CommonsConcurrentHashMap <> (new CommonsArrayList <> ("test", "any", "foo"),
                                             Function.identity (),
                                             x -> Integer.valueOf (x.length ()));
    assertEquals (3, aTest.size ());
    aTest = new CommonsConcurrentHashMap <> (new CommonsConcurrentHashMap <String, Integer> (new CommonsArrayList <> ("test",
                                                                                                                      "any",
                                                                                                                      "foo"),
                                                                                             Function.identity (),
                                                                                             x -> Integer.valueOf (x.length ())));
    assertEquals (3, aTest.size ());
  }

  @Test
  public void testGetSwappedKeyValues ()
  {
    final ICommonsMap <String, Integer> aMap = CollectionHelperExt.newMap (new String [] { "a", "b", "c" },
                                                                           new Integer [] { Integer.valueOf (0),
                                                                                            Integer.valueOf (1),
                                                                                            Integer.valueOf (2) });
    final ICommonsMap <Integer, String> aMapSwapped = aMap.getSwappedKeyValues ();
    assertEquals (aMap.size (), aMapSwapped.size ());
    assertEquals (aMap, aMapSwapped.getSwappedKeyValues ());
    assertNotNull (new CommonsConcurrentHashMap <> ().getSwappedKeyValues ());
  }
}
