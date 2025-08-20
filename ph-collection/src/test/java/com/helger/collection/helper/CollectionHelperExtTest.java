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
package com.helger.collection.helper;

import static com.helger.collection.CollectionHelper.getAtIndex;
import static com.helger.collection.CollectionHelper.getSize;
import static com.helger.collection.CollectionHelper.isEmpty;
import static com.helger.collection.CollectionHelper.makeUnmodifiable;
import static com.helger.collection.CollectionHelper.makeUnmodifiableNotNull;
import static com.helger.collection.enumeration.EnumerationHelper.getEnumeration;
import static com.helger.collection.helper.CollectionHelperExt.*;
import static com.helger.collection.helper.CollectionSort.getReverseInlineList;
import static com.helger.collection.helper.CollectionSort.getReverseList;
import static com.helger.collection.helper.CollectionSort.getSorted;
import static com.helger.collection.helper.PrimitiveCollectionHelper.createPrimitiveList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import org.junit.Test;

import com.helger.base.array.ArrayHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.CommonsIterableIterator;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.CommonsTreeMap;
import com.helger.collection.commons.CommonsTreeSet;
import com.helger.collection.commons.CommonsVector;
import com.helger.collection.commons.ICommonsCollection;
import com.helger.collection.commons.ICommonsIterableIterator;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.commons.ICommonsSet;
import com.helger.collection.commons.ICommonsSortedMap;
import com.helger.collection.commons.ICommonsSortedSet;
import com.helger.collection.stack.NonBlockingStack;

/**
 * Test class for class {@link CollectionHelperExt}
 *
 * @author Philip Helger
 */
public final class CollectionHelperExtTest
{
  private static final Integer I1 = Integer.valueOf (1);
  private static final Integer I2 = Integer.valueOf (2);
  private static final Integer I3 = Integer.valueOf (3);
  private static final Integer I4 = Integer.valueOf (4);
  private static final Integer I5 = Integer.valueOf (5);

  @Test
  public void testGetDifference ()
  {
    final ICommonsList <String> l1 = createList ("Hello", "Welt", "from", "Vienna");
    final ICommonsList <String> l2 = createList ("Welt", "from");

    // Result should be "Hello" and "Vienna"
    final Set <String> ret = getDifference (l1, l2);
    assertNotNull (ret);
    assertEquals (2, ret.size ());
    assertTrue (ret.contains ("Hello"));
    assertFalse (ret.contains ("Welt"));
    assertFalse (ret.contains ("from"));
    assertTrue (ret.contains ("Vienna"));

    assertEquals (4, getDifference (l1, new CommonsVector <> ()).size ());
    assertEquals (4, getDifference (l1, null).size ());
    assertEquals (0, getDifference (new CommonsHashSet <> (), l2).size ());
    assertEquals (0, getDifference (null, l2).size ());
  }

  @Test
  public void testGetIntersected ()
  {
    final ICommonsList <String> l1 = createList ("Hallo", "Welt", "from", "Vienna");
    final ICommonsList <String> l2 = createList ("Welt", "from");

    // Result should be "Hello" and "Vienna"
    final Set <String> ret = getIntersected (l1, l2);
    assertNotNull (ret);
    assertEquals (2, ret.size ());
    assertFalse (ret.contains ("Hello"));
    assertTrue (ret.contains ("Welt"));
    assertTrue (ret.contains ("from"));
    assertFalse (ret.contains ("Vienna"));

    assertEquals (0, getIntersected (l1, null).size ());
    assertEquals (0, getIntersected (null, l2).size ());
  }

  @Test
  public void testMakeUnmodifiable ()
  {
    assertNull (makeUnmodifiable ((Collection <?>) null));
    assertNull (makeUnmodifiable ((ICommonsList <?>) null));
    assertNull (makeUnmodifiable ((Set <?>) null));
    assertNull (makeUnmodifiable ((SortedSet <?>) null));
    assertNull (makeUnmodifiable ((Map <?, ?>) null));
    assertNull (makeUnmodifiable ((SortedMap <?, ?>) null));

    final ICommonsCollection <String> c = createList ("s1", "s2");
    assertNotNull (makeUnmodifiable (c));
    assertNotSame (c, makeUnmodifiable (c));
    final ICommonsList <String> l = createList ("s1", "s2");
    assertNotNull (makeUnmodifiable (l));
    assertNotSame (l, makeUnmodifiable (l));
    final ICommonsSet <String> s = createSet ("s1", "s2");
    assertNotNull (makeUnmodifiable (s));
    assertNotSame (s, makeUnmodifiable (s));
    final ICommonsSortedSet <String> ss = new CommonsTreeSet <> (s);
    assertNotNull (makeUnmodifiable (ss));
    assertNotSame (ss, makeUnmodifiable (ss));
    final ICommonsMap <String, String> m = createMap ("s1", "s2");
    assertNotNull (makeUnmodifiable (m));
    assertNotSame (m, makeUnmodifiable (m));
    final ICommonsSortedMap <String, String> sm = new CommonsTreeMap <> (m);
    assertNotNull (makeUnmodifiable (sm));
    assertNotSame (sm, makeUnmodifiable (sm));
  }

  @Test
  public void testMakeUnmodifiableNotNull ()
  {
    assertNotNull (makeUnmodifiableNotNull ((Collection <?>) null));
    assertNotNull (makeUnmodifiableNotNull ((ICommonsList <?>) null));
    assertNotNull (makeUnmodifiableNotNull ((Set <?>) null));
    assertNotNull (makeUnmodifiableNotNull ((SortedSet <?>) null));
    assertNotNull (makeUnmodifiableNotNull ((Map <?, ?>) null));
    assertNotNull (makeUnmodifiableNotNull ((SortedMap <?, ?>) null));

    final ICommonsCollection <String> c = createList ("s1", "s2");
    assertNotNull (makeUnmodifiableNotNull (c));
    assertNotSame (c, makeUnmodifiableNotNull (c));
    final ICommonsList <String> l = createList ("s1", "s2");
    assertNotNull (makeUnmodifiableNotNull (l));
    assertNotSame (l, makeUnmodifiableNotNull (l));
    final ICommonsSet <String> s = createSet ("s1", "s2");
    assertNotNull (makeUnmodifiableNotNull (s));
    assertNotSame (s, makeUnmodifiableNotNull (s));
    final ICommonsSortedSet <String> ss = new CommonsTreeSet <> (s);
    assertNotNull (makeUnmodifiableNotNull (ss));
    assertNotSame (ss, makeUnmodifiableNotNull (ss));
    final ICommonsMap <String, String> m = createMap ("s1", "s2");
    assertNotNull (makeUnmodifiableNotNull (m));
    assertNotSame (m, makeUnmodifiableNotNull (m));
    final ICommonsSortedMap <String, String> sm = new CommonsTreeMap <> (m);
    assertNotNull (makeUnmodifiableNotNull (sm));
    assertNotSame (sm, makeUnmodifiableNotNull (sm));
  }

  @Test
  public void testNewMap_Empty ()
  {
    assertNotNull (createMap ());
    assertTrue (createMap ().isEmpty ());
  }

  @Test
  public void testNewMap_KeyValue ()
  {
    final Map <String, Integer> aMap = createMap ("Hallo", I5);
    assertNotNull (aMap);
    assertEquals (1, aMap.size ());
    assertNotNull (aMap.get ("Hallo"));
    assertEquals (I5, aMap.get ("Hallo"));
  }

  @Test
  public void testNewMap_Map ()
  {
    final Map <String, Integer> aMap = createMap ("Hallo", I5);
    assertNotNull (aMap);

    final Map <String, Integer> aMap2 = createMap (aMap);
    assertEquals (1, aMap2.size ());
    assertNotNull (aMap2.get ("Hallo"));
    assertEquals (I5, aMap2.get ("Hallo"));
  }

  @Test
  public void testNewMap_MapArray ()
  {
    final Map <String, Integer> aMapA = createMap ("Hallo", I5);
    final Map <String, Integer> aMapB = createMap ("Welt", I3);

    Map <String, Integer> aMap2 = createMap (ArrayHelper.createArray (aMapA, aMapB));
    assertEquals (2, aMap2.size ());
    assertEquals (I5, aMap2.get ("Hallo"));
    assertEquals (I3, aMap2.get ("Welt"));

    aMap2 = createMap (ArrayHelper.createArray (aMapA, aMapA));
    assertEquals (1, aMap2.size ());
    assertEquals (I5, aMap2.get ("Hallo"));
  }

  @Test
  public void testNewMap_Array ()
  {
    assertNotNull (createMap ((Object []) null));
    assertTrue (createMap ((Object []) null).isEmpty ());

    try
    {
      // odd number of parameters not allowed
      createMap ("Hallo", "Welt", "from");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    final Map <String, String> aMap = createMap ("Hallo", "Welt", "from", "Vienna");
    assertNotNull (aMap);
    assertEquals (2, aMap.size ());
    assertNotNull (aMap.get ("Hallo"));
    assertEquals ("Welt", aMap.get ("Hallo"));
    assertNotNull (aMap.get ("from"));
    assertEquals ("Vienna", aMap.get ("from"));
  }

  @Test
  public void testNewMap_ArrayArray ()
  {
    try
    {
      // null keys not allowed
      createMap ((Object []) null, new String [] { "a" });
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // null values not allowed
      createMap (new String [] { "a" }, (Object []) null);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // different length not allowed
      createMap (new String [0], new String [1]);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    Map <Integer, String> aMap = createMap (new Integer [] { I2, I4 }, new String [] { "Hallo", "Welt" });
    assertNotNull (aMap);
    assertEquals (2, aMap.size ());
    assertNotNull (aMap.get (I2));
    assertEquals ("Hallo", aMap.get (I2));
    assertNotNull (aMap.get (I4));
    assertEquals ("Welt", aMap.get (I4));

    aMap = createMap (new Integer [] {}, new String [] {});
    assertNotNull (aMap);
    assertEquals (0, aMap.size ());
  }

  @Test
  public void testNewMap_CollectionCollection ()
  {
    final ICommonsList <String> aKeys = createList ("d", "c", "b", "a");
    final ICommonsList <Integer> aValues = createPrimitiveList (4, 3, 2, 1);
    try
    {
      // null keys not allowed
      createMap (null, aValues);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // null values not allowed
      createMap (aKeys, null);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    Map <String, Integer> aMap = createMap (aKeys, aValues);
    assertNotNull (aMap);
    assertTrue (aMap.keySet ().containsAll (aKeys));
    assertTrue (aMap.values ().containsAll (aValues));

    try
    {
      // There are more values than keys
      aValues.add (Integer.valueOf (42));
      createMap (aKeys, aValues);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // Simple test for createMap (Map)
    assertEquals (aMap, createMap (aMap));
    assertEquals (aMap, createMap (aMap.entrySet ()));

    aKeys.clear ();
    aValues.clear ();
    aMap = createMap (aKeys, aValues);
    assertNotNull (aMap);
    assertTrue (aMap.isEmpty ());

    // Simple test for createMap (Map)
    assertEquals (aMap, createMap (aMap));
    assertEquals (aMap, createMap (aMap.entrySet ()));
  }

  @Test
  public void testNewSortedMap_Empty ()
  {
    assertNotNull (CollectionHelperExt.<String, String> createSortedMap ());
    assertTrue (CollectionHelperExt.<String, String> createSortedMap ().isEmpty ());
  }

  @Test
  public void testNewSortedMap_KeyValue ()
  {
    final SortedMap <String, Integer> aSortedMap = createSortedMap ("Hallo", I5);
    assertNotNull (aSortedMap);
    assertEquals (1, aSortedMap.size ());
    assertNotNull (aSortedMap.get ("Hallo"));
    assertEquals (I5, aSortedMap.get ("Hallo"));
  }

  @Test
  public void testNewSortedMap_Array ()
  {
    assertNotNull (createSortedMap ((String []) null));
    assertTrue (createSortedMap ((String []) null).isEmpty ());

    try
    {
      // odd number of parameters not allowed
      createSortedMap ("Hallo", "Welt", "from");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    final SortedMap <String, String> aSortedMap = createSortedMap ("Hallo", "Welt", "from", "Vienna");
    assertNotNull (aSortedMap);
    assertEquals (2, aSortedMap.size ());
    assertNotNull (aSortedMap.get ("Hallo"));
    assertEquals ("Welt", aSortedMap.get ("Hallo"));
    assertNotNull (aSortedMap.get ("from"));
    assertEquals ("Vienna", aSortedMap.get ("from"));
  }

  @Test
  public void testNewSortedMap_ArrayArray ()
  {
    try
    {
      // null keys not allowed
      createSortedMap ((String []) null, new String [] { "a" });
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // null values not allowed
      createSortedMap (new String [] { "a" }, (Object []) null);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // different length not allowed
      createSortedMap (new String [0], new String [1]);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    SortedMap <Integer, String> aSortedMap = createSortedMap (new Integer [] { I2, I4 },
                                                              new String [] { "Hallo", "Welt" });
    assertNotNull (aSortedMap);
    assertEquals (2, aSortedMap.size ());
    assertNotNull (aSortedMap.get (I2));
    assertEquals ("Hallo", aSortedMap.get (I2));
    assertNotNull (aSortedMap.get (I4));
    assertEquals ("Welt", aSortedMap.get (I4));

    aSortedMap = createSortedMap (new Integer [] {}, new String [] {});
    assertNotNull (aSortedMap);
    assertEquals (0, aSortedMap.size ());
  }

  @Test
  public void testNewSortedMap_CollectionCollection ()
  {
    final ICommonsList <String> aKeys = createList ("d", "c", "b", "a");
    final ICommonsList <Integer> aValues = createPrimitiveList (4, 3, 2, 1);
    try
    {
      // null keys not allowed
      createSortedMap ((ICommonsList <String>) null, aValues);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // null values not allowed
      createSortedMap (aKeys, null);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    SortedMap <String, Integer> aSortedMap = createSortedMap (aKeys, aValues);
    assertNotNull (aSortedMap);
    assertTrue (aSortedMap.keySet ().containsAll (aKeys));
    assertTrue (aSortedMap.values ().containsAll (aValues));

    try
    {
      // There are more values than keys
      aValues.add (Integer.valueOf (42));
      createSortedMap (aKeys, aValues);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // Simple test for createSortedMap (SortedMap)
    assertEquals (aSortedMap, createSortedMap (aSortedMap));
    assertEquals (aSortedMap, createSortedMap (aSortedMap.entrySet ()));

    aKeys.clear ();
    aValues.clear ();
    aSortedMap = createSortedMap (aKeys, aValues);
    assertNotNull (aSortedMap);
    assertTrue (aSortedMap.isEmpty ());

    // Simple test for createSortedMap (SortedMap)
    assertEquals (aSortedMap, createSortedMap (aSortedMap));
    assertEquals (aSortedMap, createSortedMap (aSortedMap.entrySet ()));
  }

  @Test
  public void testNewOrderedMap_Empty ()
  {
    assertNotNull (createOrderedMap ());
    assertTrue (createOrderedMap ().isEmpty ());
  }

  @Test
  public void testNewOrderedMap_KeyValue ()
  {
    final Map <String, Integer> aMap = createOrderedMap ("Hallo", I5);
    assertNotNull (aMap);
    assertEquals (1, aMap.size ());
    assertNotNull (aMap.get ("Hallo"));
    assertEquals (I5, aMap.get ("Hallo"));
  }

  @Test
  public void testNewOrderedMap_Array ()
  {
    assertNotNull (createOrderedMap ((Object []) null));
    assertTrue (createOrderedMap ((Object []) null).isEmpty ());

    try
    {
      // odd number of parameters not allowed
      createOrderedMap ("Hallo", "Welt", "from");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    final Map <String, String> aMap = createOrderedMap ("Hallo", "Welt", "from", "Vienna");
    assertNotNull (aMap);
    assertEquals (2, aMap.size ());
    assertNotNull (aMap.get ("Hallo"));
    assertEquals ("Welt", aMap.get ("Hallo"));
    assertNotNull (aMap.get ("from"));
    assertEquals ("Vienna", aMap.get ("from"));
  }

  @Test
  public void testNewOrderedMap_ArrayArray ()
  {
    ICommonsMap <String, Integer> aMap = createOrderedMap (new String [] { "Hallo", "Alice" },
                                                           new Integer [] { I1, I2 });
    assertNotNull (aMap);
    assertEquals (2, aMap.size ());
    assertNotNull (aMap.get ("Hallo"));
    assertNotNull (aMap.get ("Alice"));
    assertEquals (I1, aMap.get ("Hallo"));
    assertEquals (I2, aMap.get ("Alice"));

    // check order
    final Iterator <String> it = aMap.keySet ().iterator ();
    assertNotNull (it);
    assertTrue (it.hasNext ());
    assertEquals ("Hallo", it.next ());
    assertTrue (it.hasNext ());
    assertEquals ("Alice", it.next ());
    assertFalse (it.hasNext ());

    try
    {
      // key array may not be null
      createOrderedMap (null, new Integer [] { I1, I2 });
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // value array may not be null
      createOrderedMap (new String [] { "Hallo", "Alice" }, null);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // key and value array need to have the same length
      createOrderedMap (new String [] { "Hallo", "Alice" }, new Integer [] { I1, });
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    aMap = createOrderedMap (new String [] {}, new Integer [] {});
    assertNotNull (aMap);
    assertEquals (0, aMap.size ());
  }

  @Test
  public void testNewOrderedMap_CollectionCollection ()
  {
    final ICommonsList <String> aKeys = createList ("d", "c", "b", "a");
    final ICommonsList <Integer> aValues = createPrimitiveList (4, 3, 2, 1);
    try
    {
      // null keys not allowed
      createOrderedMap (null, aValues);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // null values not allowed
      createOrderedMap (aKeys, null);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    Map <String, Integer> aMap = createOrderedMap (aKeys, aValues);
    assertNotNull (aMap);
    assertTrue (aMap.keySet ().containsAll (aKeys));
    assertTrue (aMap.values ().containsAll (aValues));

    try
    {
      // There are more values than keys
      aValues.add (Integer.valueOf (42));
      createOrderedMap (aKeys, aValues);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // Simple test for createMap (Map)
    assertEquals (aMap, createOrderedMap (aMap));
    assertEquals (aMap, createOrderedMap (aMap.entrySet ()));

    aKeys.clear ();
    aValues.clear ();
    aMap = createOrderedMap (aKeys, aValues);
    assertNotNull (aMap);
    assertTrue (aMap.isEmpty ());

    // Simple test for createMap (Map)
    assertEquals (aMap, createOrderedMap (aMap));
    assertEquals (aMap, createOrderedMap (aMap.entrySet ()));
  }

  @Test
  public void testNewSet_Empty ()
  {
    final Set <String> aSet = createSet ();
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  public void testNewSet_SingleValue ()
  {
    Set <String> aSet = createSet ("Hallo");
    assertNotNull (aSet);
    assertEquals (1, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));

    aSet = createSet ((String) null);
    assertNotNull (aSet);
    assertEquals (1, aSet.size ());
    assertTrue (aSet.contains (null));
  }

  @Test
  public void testNewSet_Array ()
  {
    Set <String> aSet = createSet ("Hallo", "Welt");
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));

    aSet = createSet (new String [0]);
    assertNotNull (aSet);

    aSet = createSet ((String []) null);
    assertNotNull (aSet);
  }

  @Test
  public void testNewSetIterable ()
  {
    Set <String> aSet = createSet ((Iterable <String>) createList ("Hallo", "Welt"));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));

    aSet = createSet ((Iterable <String>) new CommonsArrayList <String> ());
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  public void testNewSetCollection ()
  {
    Set <String> aSet = createSet (createList ("Hallo", "Welt"));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));

    aSet = createSet (new CommonsArrayList <> ());
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  public void testNewSetIIterableIterator ()
  {
    Set <String> aSet = createSet (new CommonsIterableIterator <> (createList ("Hallo", "Welt")));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));

    aSet = createSet (new CommonsIterableIterator <> (new CommonsArrayList <> ()));
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  public void testNewSetEnumeration ()
  {
    Set <String> aSet = createSet (getEnumeration (createList ("Hallo", "Welt")));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));

    aSet = createSet (getEnumeration (new CommonsArrayList <> ()));
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  public void testNewSetIterator ()
  {
    final Iterator <String> it = createSet ("Hallo", "Welt").iterator ();
    final Set <String> aUnmodifiableSet = createSet (it);
    assertNotNull (aUnmodifiableSet);
    assertEquals (2, aUnmodifiableSet.size ());
    assertTrue (aUnmodifiableSet.contains ("Hallo"));
    assertTrue (aUnmodifiableSet.contains ("Welt"));
  }

  @Test
  public void testNewSortedSet_Empty ()
  {
    final SortedSet <String> aSet = createSortedSet ();
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  public void testNewSortedSet_SingleValue ()
  {
    SortedSet <String> aSet = createSortedSet ("Hallo");
    assertNotNull (aSet);
    assertEquals (1, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));

    aSet = createSortedSet ((String) null);
    assertNotNull (aSet);
    assertEquals (1, aSet.size ());
    assertTrue (aSet.contains (null));
  }

  @Test
  public void testNewSortedSet_Array ()
  {
    SortedSet <String> aSet = createSortedSet ("Hallo", "Welt");
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));

    aSet = createSortedSet (new String [0]);
    assertNotNull (aSet);

    aSet = createSortedSet ((String []) null);
    assertNotNull (aSet);
  }

  @Test
  public void testNewSortedSetIterable ()
  {
    SortedSet <String> aSet = createSortedSet ((Iterable <String>) createList ("Hallo", "Welt"));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));

    aSet = createSortedSet ((Iterable <String>) new CommonsArrayList <String> ());
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  public void testNewSortedSetCollection ()
  {
    SortedSet <String> aSet = createSortedSet (createList ("Hallo", "Welt"));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));

    aSet = createSortedSet (new CommonsArrayList <> ());
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  public void testNewSortedSetIIterableIterator ()
  {
    SortedSet <String> aSet = createSortedSet (new CommonsIterableIterator <> (createList ("Hallo", "Welt", null)));
    assertNotNull (aSet);
    assertEquals (3, aSet.size ());
    assertNull (aSet.first ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));
    assertTrue (aSet.contains (null));

    aSet = createSortedSet (new CommonsIterableIterator <> (new CommonsArrayList <> ()));
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  public void testNewSortedSetEnumeration ()
  {
    SortedSet <String> aSet = createSortedSet (getEnumeration (createList ("Hallo", "Welt")));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));

    aSet = createSortedSet (getEnumeration (new CommonsArrayList <> ()));
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  public void testNewSortedSetIterator ()
  {
    final Iterator <String> it = createSortedSet ("Hallo", "Welt").iterator ();
    final SortedSet <String> aUnmodifiableSet = createSortedSet (it);
    assertNotNull (aUnmodifiableSet);
    assertEquals (2, aUnmodifiableSet.size ());
    assertTrue (aUnmodifiableSet.contains ("Hallo"));
    assertTrue (aUnmodifiableSet.contains ("Welt"));
  }

  @Test
  public void testNewOrderedSetEmpty ()
  {
    final Set <String> aOrderedSet = createOrderedSet ();
    assertNotNull (aOrderedSet);
    assertEquals (0, aOrderedSet.size ());
  }

  @Test
  public void testNewOrderedSetSingleValue ()
  {
    Set <String> aOrderedSet = createOrderedSet ("Hallo");
    assertNotNull (aOrderedSet);
    assertEquals (1, aOrderedSet.size ());
    assertTrue (aOrderedSet.contains ("Hallo"));

    aOrderedSet = createOrderedSet ((String) null);
    assertNotNull (aOrderedSet);
    assertEquals (1, aOrderedSet.size ());
    assertTrue (aOrderedSet.contains (null));
  }

  @Test
  public void testNewOrderedSetArray ()
  {
    Set <String> aOrderedSet = createOrderedSet ("Hallo", "Welt");
    assertNotNull (aOrderedSet);
    assertEquals (2, aOrderedSet.size ());
    assertTrue (aOrderedSet.contains ("Hallo"));
    assertTrue (aOrderedSet.contains ("Welt"));

    aOrderedSet = createOrderedSet (new String [0]);
    assertNotNull (aOrderedSet);

    aOrderedSet = createOrderedSet ((String []) null);
    assertNotNull (aOrderedSet);
  }

  @Test
  public void testNewOrderedSetIterable ()
  {
    Set <String> aOrderedSet = createOrderedSet ((Iterable <String>) createList ("Hallo", "Welt"));
    assertNotNull (aOrderedSet);
    assertEquals (2, aOrderedSet.size ());
    assertTrue (aOrderedSet.contains ("Hallo"));
    assertTrue (aOrderedSet.contains ("Welt"));

    aOrderedSet = createOrderedSet ((Iterable <String>) new CommonsArrayList <String> ());
    assertNotNull (aOrderedSet);
    assertEquals (0, aOrderedSet.size ());
  }

  @Test
  public void testNewOrderedSetCollection ()
  {
    Set <String> aOrderedSet = createOrderedSet (createList ("Hallo", "Welt"));
    assertNotNull (aOrderedSet);
    assertEquals (2, aOrderedSet.size ());
    assertTrue (aOrderedSet.contains ("Hallo"));
    assertTrue (aOrderedSet.contains ("Welt"));

    aOrderedSet = createOrderedSet (new CommonsArrayList <> ());
    assertNotNull (aOrderedSet);
    assertEquals (0, aOrderedSet.size ());
  }

  @Test
  public void testNewOrderedSetIIterableIterator ()
  {
    Set <String> aOrderedSet = createOrderedSet (new CommonsIterableIterator <> (createList ("Hallo", "Welt")));
    assertNotNull (aOrderedSet);
    assertEquals (2, aOrderedSet.size ());
    assertTrue (aOrderedSet.contains ("Hallo"));
    assertTrue (aOrderedSet.contains ("Welt"));

    aOrderedSet = createOrderedSet (new CommonsIterableIterator <> (new CommonsArrayList <> ()));
    assertNotNull (aOrderedSet);
    assertEquals (0, aOrderedSet.size ());
  }

  @Test
  public void testNewOrderedSetEnumeration ()
  {
    Set <String> aOrderedSet = createOrderedSet (getEnumeration (createList ("Hallo", "Welt")));
    assertNotNull (aOrderedSet);
    assertEquals (2, aOrderedSet.size ());
    assertTrue (aOrderedSet.contains ("Hallo"));
    assertTrue (aOrderedSet.contains ("Welt"));

    aOrderedSet = createOrderedSet (getEnumeration (new CommonsArrayList <> ()));
    assertNotNull (aOrderedSet);
    assertEquals (0, aOrderedSet.size ());
  }

  @Test
  public void testNewOrderedSetIterator ()
  {
    final Iterator <String> it = createOrderedSet ("Hallo", "Welt").iterator ();
    final Set <String> aOrderedSet = createOrderedSet (it);
    assertNotNull (aOrderedSet);
    assertEquals (2, aOrderedSet.size ());
    assertTrue (aOrderedSet.contains ("Hallo"));
    assertTrue (aOrderedSet.contains ("Welt"));
  }

  @Test
  public void testNewListPrefilled ()
  {
    ICommonsList <String> aList = createListPrefilled ("s", 5);
    assertNotNull (aList);
    assertEquals (5, aList.size ());
    for (int i = 0; i < 5; ++i)
      assertEquals ("s", aList.get (i));

    aList = createListPrefilled ("s", 0);
    assertNotNull (aList);
    assertEquals (0, aList.size ());

    try
    {
      createListPrefilled ("s", -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testNewListEmpty ()
  {
    final ICommonsList <String> aList = createList ();
    assertNotNull (aList);
    assertEquals (0, aList.size ());
  }

  @Test
  public void testNewListSingleValue ()
  {
    ICommonsList <String> aList = createList ("Hallo");
    assertNotNull (aList);
    assertEquals (1, aList.size ());
    assertTrue (aList.contains ("Hallo"));

    aList = createList ((String) null);
    assertNotNull (aList);
    assertEquals (1, aList.size ());
    assertTrue (aList.contains (null));
  }

  @Test
  public void testNewListArray ()
  {
    ICommonsList <String> aList = createList ("Hallo", "Welt", "from", "Vienna");
    assertNotNull (aList);
    assertEquals (4, aList.size ());
    assertTrue (aList.contains ("Hallo"));
    assertTrue (aList.contains ("Welt"));
    assertTrue (aList.contains ("from"));
    assertTrue (aList.contains ("Vienna"));

    aList = createList ((String []) null);
    assertNotNull (aList);

    aList = createList (new String [0]);
    assertNotNull (aList);
  }

  @Test
  public void testNewListIterator ()
  {
    final ICommonsList <String> aSource = new CommonsArrayList <> ();
    assertTrue (aSource.add ("Hallo"));
    assertTrue (aSource.add ("Welt"));
    assertTrue (aSource.add ("from"));
    assertTrue (aSource.add ("Vienna"));

    ICommonsList <String> aList = createList (aSource.iterator ());
    assertNotNull (aList);
    assertEquals (4, aList.size ());
    assertTrue (aList.contains ("Hallo"));
    assertTrue (aList.contains ("Welt"));
    assertTrue (aList.contains ("from"));
    assertTrue (aList.contains ("Vienna"));

    aList = createList (new CommonsArrayList <String> ().iterator ());
    assertNotNull (aList);

    aList = createList ((Iterator <String>) null);
    assertNotNull (aList);
  }

  @Test
  public void testNewListCollection ()
  {
    final ICommonsList <String> aSource = createList ("Hallo", "Welt", "from", "Vienna");

    ICommonsList <String> aList = createList (aSource);
    assertNotNull (aList);
    assertEquals (4, aList.size ());
    assertTrue (aList.contains ("Hallo"));
    assertTrue (aList.contains ("Welt"));
    assertTrue (aList.contains ("from"));
    assertTrue (aList.contains ("Vienna"));

    aList = createList (new CommonsArrayList <> ());
    assertNotNull (aList);

    aList = createList ((ICommonsList <String>) null);
    assertNotNull (aList);
  }

  @Test
  public void testNewListIterable ()
  {
    final ICommonsList <String> aSource = createList ("Hallo", "Welt", "from", "Vienna");

    ICommonsList <String> aList = createList ((Iterable <String>) aSource);
    assertNotNull (aList);
    assertEquals (4, aList.size ());
    assertTrue (aList.contains ("Hallo"));
    assertTrue (aList.contains ("Welt"));
    assertTrue (aList.contains ("from"));
    assertTrue (aList.contains ("Vienna"));

    aList = createList ((Iterable <String>) new CommonsArrayList <String> ());
    assertNotNull (aList);

    aList = createList ((Iterable <String>) null);
    assertNotNull (aList);
  }

  @Test
  public void testNewListIIterableIterator ()
  {
    final ICommonsList <String> aSource = createList ("Hallo", "Welt", "from", "Vienna");

    ICommonsList <String> aList = createList (new CommonsIterableIterator <> (aSource));
    assertNotNull (aList);
    assertEquals (4, aList.size ());
    assertTrue (aList.contains ("Hallo"));
    assertTrue (aList.contains ("Welt"));
    assertTrue (aList.contains ("from"));
    assertTrue (aList.contains ("Vienna"));

    aList = createList (new CommonsIterableIterator <> (new CommonsArrayList <> ()));
    assertNotNull (aList);

    aList = createList ((ICommonsIterableIterator <String>) null);
    assertNotNull (aList);
  }

  @Test
  public void testGetSortedIterator ()
  {
    assertNotNull (getSorted ((Iterator <String>) null));

    final ICommonsList <String> aList = createList ("d", "c", "b", "a");
    final ICommonsList <String> aSorted = getSorted (aList.iterator ());
    assertEquals (4, aSorted.size ());
    assertEquals ("a", aSorted.get (0));
    assertEquals ("b", aSorted.get (1));
    assertEquals ("c", aSorted.get (2));
    assertEquals ("d", aSorted.get (3));
  }

  @Test
  public void testGetSortedIterable ()
  {
    assertNotNull (getSorted ((Iterable <String>) null));

    final ICommonsList <String> aList = createList ("d", "c", "b", "a");
    final ICommonsList <String> aSorted = getSorted (aList);
    assertEquals (4, aSorted.size ());
    assertEquals ("a", aSorted.get (0));
    assertEquals ("b", aSorted.get (1));
    assertEquals ("c", aSorted.get (2));
    assertEquals ("d", aSorted.get (3));
  }

  @Test
  public void testGetSortedIIterableIterator ()
  {
    assertNotNull (getSorted ((ICommonsIterableIterator <String>) null));

    final ICommonsList <String> aList = createList ("d", "c", "b", "a");
    final ICommonsList <String> aSorted = getSorted (new CommonsIterableIterator <> (aList));
    assertEquals (4, aSorted.size ());
    assertEquals ("a", aSorted.get (0));
    assertEquals ("b", aSorted.get (1));
    assertEquals ("c", aSorted.get (2));
    assertEquals ("d", aSorted.get (3));
  }

  @Test
  public void testGetSortedArray ()
  {
    assertNotNull (getSorted ((String []) null));

    final ICommonsList <String> aSorted = getSorted ("d", "c", "b", "a");
    assertEquals (4, aSorted.size ());
    assertEquals ("a", aSorted.get (0));
    assertEquals ("b", aSorted.get (1));
    assertEquals ("c", aSorted.get (2));
    assertEquals ("d", aSorted.get (3));
  }

  @Test
  public void testIsEmpty ()
  {
    assertTrue (isEmpty ((ICommonsList <?>) null));
    assertTrue (isEmpty ((Map <?, ?>) null));
    assertTrue (isEmpty (new CommonsVector <> ()));
    assertTrue (isEmpty (new CommonsHashMap <> ()));
    assertFalse (isEmpty (createList ("d", "c", "b", "a")));
    assertTrue (isEmpty ((Iterable <?>) new NonBlockingStack <> ()));
  }

  @Test
  public void testIsEmptyCollection ()
  {
    assertTrue (isEmpty ((Collection <?>) null));
    assertTrue (isEmpty ((Map <?, ?>) null));
    assertTrue (isEmpty (new CommonsArrayList <> ()));
    assertTrue (isEmpty (new CommonsVector <> ()));
    assertTrue (isEmpty (new CommonsHashSet <> ()));
    assertTrue (isEmpty (new CommonsTreeSet <> ()));
    assertTrue (isEmpty (new CommonsHashMap <> ()));
    assertTrue (isEmpty (new CommonsLinkedHashMap <> ()));

    assertFalse (isEmpty (createList ("Hallo")));
    assertFalse (isEmpty (createMap ("Hallo", "Welt")));
  }

  @Test
  public void testSize ()
  {
    assertEquals (2, getSize (createList ("Ha", "We")));
    assertEquals (1, getSize (createMap ("Ha", "We")));
    assertEquals (0, getSize ((Collection <String>) null));
    assertEquals (0, getSize ((Map <String, Double>) null));
  }

  @Test
  public void testGetFilteredMap ()
  {
    assertNull (getFilteredMap (null, createList ("a")));
    assertNull (getFilteredMap (createMap ("a", "value-of-a"), null));

    final Map <String, String> aFilteredMap = getFilteredMap (createMap ("a", "value-of-a", "b", "value-of-b"),
                                                              createList ("a"));
    assertNotNull (aFilteredMap);
    assertEquals (1, aFilteredMap.size ());
    assertTrue (aFilteredMap.containsKey ("a"));
    assertEquals ("value-of-a", aFilteredMap.get ("a"));
  }

  @Test
  public void testGetReverseList ()
  {
    assertNotNull (getReverseList (null));
    assertTrue (getReverseList (null).isEmpty ());

    // Make it not sorted :)
    final ICommonsList <String> aList = createList ("1", "3", "2");
    final ICommonsList <String> aReverse = getReverseList (aList);
    assertNotNull (aReverse);
    assertEquals (3, aReverse.size ());
    assertEquals ("2", aReverse.get (0));
    assertEquals ("3", aReverse.get (1));
    assertEquals ("1", aReverse.get (2));

    // Check original
    assertEquals (3, aList.size ());
    assertEquals ("1", aList.get (0));
    assertEquals ("3", aList.get (1));
    assertEquals ("2", aList.get (2));
  }

  @Test
  public void testGetReverseInlineList ()
  {
    ICommonsList <String> aList = createList ("1", "3", "2");

    // Sort inline
    assertSame (aList, getReverseInlineList (aList));
    assertEquals (3, aList.size ());
    assertEquals ("2", aList.get (0));
    assertEquals ("3", aList.get (1));
    assertEquals ("1", aList.get (2));

    aList = createList ();
    assertSame (aList, getReverseInlineList (aList));
    assertEquals (0, aList.size ());

    assertNull (getReverseInlineList (null));
  }

  @Test
  public void testIsEmpty_Iterable ()
  {
    assertTrue (isEmpty ((Iterable <?>) null));
    assertTrue (isEmpty ((Iterable <String>) new CommonsArrayList <String> ()));
    assertFalse (isEmpty ((Iterable <String>) createList ("any")));
  }

  @Test
  public void testIsEmpty_Collection ()
  {
    assertTrue (isEmpty ((Collection <?>) null));
    assertTrue (isEmpty (new CommonsArrayList <> ()));
    assertFalse (isEmpty (createList ("any")));
  }

  @Test
  public void testIsEmpty_Map ()
  {
    assertTrue (isEmpty ((Map <?, ?>) null));
    assertTrue (isEmpty (new CommonsHashMap <> ()));
    assertFalse (isEmpty (createMap ("any", "value")));
  }

  @Test
  public void testGetSize_Collection ()
  {
    assertEquals (0, getSize ((Collection <?>) null));
    assertEquals (0, getSize (new CommonsArrayList <> ()));
    assertEquals (1, getSize (createList ("any")));
  }

  @Test
  public void testGetSize_Map ()
  {
    assertEquals (0, getSize ((Map <?, ?>) null));
    assertEquals (0, getSize (new CommonsHashMap <> ()));
    assertEquals (1, getSize (createMap ("key", "value")));
  }

  @Test
  public void testGetSize_Iterable ()
  {
    assertEquals (0, getSize ((Iterable <?>) null));
    assertEquals (0, getSize ((Iterable <String>) new CommonsArrayList <String> ()));
    assertEquals (1, getSize ((Iterable <String>) createList ("any")));
  }

  @Test
  public void testGetConcatenatedList_CollectionCollection ()
  {
    final ICommonsList <String> a = createList ("a", "b");
    final ICommonsList <String> b = createList ("c", "d");
    assertTrue (getConcatenatedList ((Collection <String>) null, (Collection <String>) null).isEmpty ());
    assertEquals (a, getConcatenatedList (a, (Collection <String>) null));
    assertEquals (b, getConcatenatedList ((Collection <String>) null, b));
    assertEquals (createList ("a", "b", "c", "d"), getConcatenatedList (a, b));
  }

  @Test
  public void testGetConcatenatedList_CollectionArray ()
  {
    final ICommonsList <String> a = createList ("a", "b");
    final String [] b = ArrayHelper.createArray ("c", "d");
    assertTrue (getConcatenatedList ((Collection <String>) null, (String []) null).isEmpty ());
    assertEquals (a, getConcatenatedList (a, (String []) null));
    assertEquals (createList (b), getConcatenatedList ((Collection <String>) null, b));
    assertEquals (createList ("a", "b", "c", "d"), getConcatenatedList (a, b));
  }

  @Test
  public void testGetConcatenatedList_ArrayCollection ()
  {
    final String [] a = ArrayHelper.createArray ("a", "b");
    final ICommonsList <String> b = createList ("c", "d");
    assertTrue (getConcatenatedList ((String []) null, (Collection <String>) null).isEmpty ());
    assertEquals (createList (a), getConcatenatedList (a, (Collection <String>) null));
    assertEquals (b, getConcatenatedList ((String []) null, b));
    assertEquals (createList ("a", "b", "c", "d"), getConcatenatedList (a, b));
  }

  @Test
  public void testGetConcatenatedSet_CollectionCollection ()
  {
    final Set <String> a = createSet ("a", "b");
    final Set <String> b = createSet ("c", "d");
    assertTrue (getConcatenatedSet ((Collection <String>) null, (Collection <String>) null).isEmpty ());
    assertEquals (a, getConcatenatedSet (a, (Collection <String>) null));
    assertEquals (b, getConcatenatedSet ((Collection <String>) null, b));
    assertEquals (createSet ("a", "b", "c", "d"), getConcatenatedSet (a, b));
  }

  @Test
  public void testGetConcatenatedSet_CollectionArray ()
  {
    final Set <String> a = createSet ("a", "b");
    final String [] b = ArrayHelper.createArray ("c", "d");
    assertTrue (getConcatenatedSet ((Collection <String>) null, (String []) null).isEmpty ());
    assertEquals (a, getConcatenatedSet (a, (String []) null));
    assertEquals (createSet (b), getConcatenatedSet ((Collection <String>) null, b));
    assertEquals (createSet ("a", "b", "c", "d"), getConcatenatedSet (a, b));
  }

  @Test
  public void testGetConcatenatedSet_ArrayCollection ()
  {
    final String [] a = ArrayHelper.createArray ("a", "b");
    final Set <String> b = createSet ("c", "d");
    assertTrue (getConcatenatedSet ((String []) null, (Collection <String>) null).isEmpty ());
    assertEquals (createSet (a), getConcatenatedSet (a, (Collection <String>) null));
    assertEquals (b, getConcatenatedSet ((String []) null, b));
    assertEquals (createSet ("a", "b", "c", "d"), getConcatenatedSet (a, b));
  }

  @Test
  public void testGetConcatenatedInline ()
  {
    // Array version
    ICommonsList <String> aBaseList = createList ("1");
    assertSame (aBaseList, getConcatenatedInline (aBaseList, "2", "3"));
    assertEquals (3, aBaseList.size ());
    assertEquals ("1", aBaseList.get (0));
    assertEquals ("3", aBaseList.get (2));
    assertSame (aBaseList, getConcatenatedInline (aBaseList, (String []) null));
    assertEquals (3, aBaseList.size ());

    // Collection version
    aBaseList = createList ("1");
    assertSame (aBaseList, getConcatenatedInline (aBaseList, createList ("2", "3")));
    assertEquals (3, aBaseList.size ());
    assertEquals ("1", aBaseList.get (0));
    assertEquals ("3", aBaseList.get (2));
    assertSame (aBaseList, getConcatenatedInline (aBaseList, (Collection <String>) null));
    assertEquals (3, aBaseList.size ());

    // Set test
    final Set <String> aBaseSet = createSet ("1");
    assertSame (aBaseSet, getConcatenatedInline (aBaseSet, "2", "3"));
    assertEquals (3, aBaseSet.size ());
    assertTrue (aBaseSet.contains ("1"));
    assertTrue (aBaseSet.contains ("3"));
    assertSame (aBaseSet, getConcatenatedInline (aBaseSet, (String []) null));
    assertEquals (3, aBaseSet.size ());

    try
    {
      getConcatenatedInline ((ICommonsList <String>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      getConcatenatedInline ((ICommonsList <String>) null, createList ("a"));
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetCombinedMap ()
  {
    final Map <String, Integer> m1 = createMap ("Hallo", I1);
    final Map <String, Integer> m2 = createMap ("Welt", I2);
    assertTrue (getCombinedMap (null, null).isEmpty ());
    assertEquals (m1, getCombinedMap (m1, null));
    assertEquals (m2, getCombinedMap (null, m2));

    final Map <String, Integer> m12 = getCombinedMap (m1, m2);
    assertNotNull (m12);
    assertEquals (2, m12.size ());
    assertTrue (m12.containsKey ("Hallo"));
    assertTrue (m12.containsKey ("Welt"));

    final Map <String, Integer> m1212 = getCombinedMap (m12, m12);
    assertNotNull (m1212);
    assertEquals (2, m1212.size ());
    assertTrue (m1212.containsKey ("Hallo"));
    assertTrue (m1212.containsKey ("Welt"));
  }

  @Test
  public void testNewObjectListFromArray ()
  {
    assertNotNull (createObjectListFromArray (new Integer [] { Integer.valueOf (2), Integer.valueOf (0x7f) },
                                              Integer.class));
    assertNotNull (createObjectListFromArray (new boolean [] { true, false }, boolean.class));
    assertNotNull (createObjectListFromArray (new byte [] { (byte) 2, (byte) 0x7f }, byte.class));
    assertNotNull (createObjectListFromArray (new char [] { 'a', 'Z' }, char.class));
    assertNotNull (createObjectListFromArray (new double [] { 3.14, 47.11 }, double.class));
    assertNotNull (createObjectListFromArray (new float [] { 3.14f, 47.11f }, float.class));
    assertNotNull (createObjectListFromArray (new int [] { 314, 4711 }, int.class));
    assertNotNull (createObjectListFromArray (new long [] { 314, 4711 }, long.class));
    assertNotNull (createObjectListFromArray (new short [] { 123, 255 }, short.class));

    assertNull (createObjectListFromArray (null, Integer.class));
    assertNull (createObjectListFromArray (null, boolean.class));
    assertNull (createObjectListFromArray (null, byte.class));
    assertNull (createObjectListFromArray (null, char.class));
    assertNull (createObjectListFromArray (null, double.class));
    assertNull (createObjectListFromArray (null, float.class));
    assertNull (createObjectListFromArray (null, int.class));
    assertNull (createObjectListFromArray (null, long.class));
    assertNull (createObjectListFromArray (null, short.class));
  }

  @Test
  public void testGetSubList ()
  {
    assertNotNull (getSubList (null, 0, 5));

    try
    {
      // start index may not be < 0
      getSubList (new CommonsArrayList <> (), -1, 2);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // length not be < 0
      getSubList (new CommonsArrayList <> (), 0, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    final ICommonsList <String> aSource = createList ("a", "b", "c", "d");

    ICommonsList <String> aSubList = getSubList (aSource, 0, 2);
    assertNotNull (aSubList);
    assertEquals (2, aSubList.size ());
    assertTrue (aSubList.contains ("a"));
    assertTrue (aSubList.contains ("b"));
    assertFalse (aSubList.contains ("c"));
    assertFalse (aSubList.contains ("d"));

    aSubList = getSubList (aSource, 1, 2);
    assertNotNull (aSubList);
    assertEquals (2, aSubList.size ());
    assertFalse (aSubList.contains ("a"));
    assertTrue (aSubList.contains ("b"));
    assertTrue (aSubList.contains ("c"));
    assertFalse (aSubList.contains ("d"));

    aSubList = getSubList (aSource, 2, 2);
    assertNotNull (aSubList);
    assertEquals (2, aSubList.size ());
    assertFalse (aSubList.contains ("a"));
    assertFalse (aSubList.contains ("b"));
    assertTrue (aSubList.contains ("c"));
    assertTrue (aSubList.contains ("d"));

    aSubList = getSubList (aSource, 3, 2);
    assertNotNull (aSubList);
    assertEquals (1, aSubList.size ());
    assertFalse (aSubList.contains ("a"));
    assertFalse (aSubList.contains ("b"));
    assertFalse (aSubList.contains ("c"));
    assertTrue (aSubList.contains ("d"));

    aSubList = getSubList (aSource, 4, 2);
    assertNotNull (aSubList);
  }

  @Test
  public void testGetSafe ()
  {
    assertNull (getAtIndex (null, 0));
    assertNull (getAtIndex (null, -1));
    assertNull (getAtIndex (null, 1));

    final ICommonsList <String> aList = createList ("a", "b");
    assertNull (getAtIndex (aList, -1));
    assertEquals ("a", getAtIndex (aList, 0));
    assertNull (getAtIndex (aList, 2));

    assertEquals ("x", getAtIndex (aList, -1, "x"));
    assertEquals ("a", getAtIndex (aList, 0, "x"));
    assertEquals ("x", getAtIndex (aList, 2, "x"));
  }

  /**
   * Check if all APIs are present
   */
  @Test
  public void testNew ()
  {
    createList ();
    createList ("a");
    createList ("a");
    createList (new CommonsArrayList <> ("a"));
    createList (new CommonsIterableIterator <> (new CommonsArrayList <> ("a")));
    createList ((Iterable <String>) new CommonsArrayList <> ("a"));
    createList (new CommonsArrayList <> ("a").iterator ());
    createList (new CommonsArrayList <> ("a"), Objects::nonNull);
    createListMapped (new CommonsArrayList <> ("a"), Object::toString);
    createListMapped (new Object [] { "a" }, Object::toString);
    createSet ();
    createSet ("a");
    createSet ("a");
    createSet (new CommonsArrayList <> ("a"));
    createSet (new CommonsIterableIterator <> (new CommonsArrayList <> ("a")));
    createSet ((Iterable <String>) new CommonsArrayList <> ("a"));
    createSet (new CommonsArrayList <> ("a").iterator ());
    createSet (new CommonsArrayList <> ("a"), Objects::nonNull);
    createSetMapped (new CommonsArrayList <> ("a"), Object::toString);
    createSetMapped (new Object [] { "a" }, Object::toString);
    createOrderedSet ();
    createOrderedSet ("a");
    createOrderedSet ("a");
    createOrderedSet (new CommonsArrayList <> ("a"));
    createOrderedSet (new CommonsIterableIterator <> (new CommonsArrayList <> ("a")));
    createOrderedSet ((Iterable <String>) new CommonsArrayList <> ("a"));
    createOrderedSet (new CommonsArrayList <> ("a").iterator ());
    createOrderedSet (new CommonsArrayList <> ("a"), Objects::nonNull);
    createOrderedSetMapped (new CommonsArrayList <> ("a"), Object::toString);
    createOrderedSetMapped (new Object [] { "a" }, Object::toString);
    createSortedSet ();
    createSortedSet ("a");
    createSortedSet ("a");
    createSortedSet (new CommonsArrayList <> ("a"));
    createSortedSet (new CommonsIterableIterator <> (new CommonsArrayList <> ("a")));
    createSortedSet ((Iterable <String>) new CommonsArrayList <> ("a"));
    createSortedSet (new CommonsArrayList <> ("a").iterator ());
    createSortedSet (new CommonsArrayList <> ("a"), Objects::nonNull);
    createSortedSetMapped (new CommonsArrayList <> ("a"), Object::toString);
    createSortedSetMapped (new Object [] { "a" }, Object::toString);
  }
}
