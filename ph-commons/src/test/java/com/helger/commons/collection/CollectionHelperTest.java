/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.collection;

import static com.helger.commons.collection.CollectionHelper.*;
import static com.helger.commons.collection.IteratorHelper.getEnumeration;
import static com.helger.commons.collection.PrimitiveCollectionHelper.newPrimitiveList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.CommonsTreeMap;
import com.helger.commons.collection.impl.CommonsTreeSet;
import com.helger.commons.collection.impl.CommonsVector;
import com.helger.commons.collection.impl.ICommonsCollection;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.collection.impl.ICommonsSortedMap;
import com.helger.commons.collection.impl.ICommonsSortedSet;
import com.helger.commons.collection.iterate.IIterableIterator;
import com.helger.commons.collection.iterate.IterableIterator;
import com.helger.commons.compare.IComparator;
import com.helger.commons.string.StringHelper;
import com.helger.commons.supplementary.tools.collection.MainCreateCollectionHelperCode2;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link CollectionHelper}
 *
 * @author Philip Helger
 */
public final class CollectionHelperTest
{
  private static final Integer I1 = Integer.valueOf (1);
  private static final Integer I2 = Integer.valueOf (2);
  private static final Integer I3 = Integer.valueOf (3);
  private static final Integer I4 = Integer.valueOf (4);
  private static final Integer I5 = Integer.valueOf (5);

  @Test
  public void testGetDifference ()
  {
    final ICommonsList <String> l1 = newList ("Hello", "Welt", "from", "Vienna");
    final ICommonsList <String> l2 = newList ("Welt", "from");

    // Result should be "Hello" and "Vienna"
    final Set <String> ret = getDifference (l1, l2);
    assertNotNull (ret);
    assertEquals (2, ret.size ());
    assertTrue (ret.contains ("Hello"));
    assertFalse (ret.contains ("Welt"));
    assertFalse (ret.contains ("from"));
    assertTrue (ret.contains ("Vienna"));

    assertEquals (4, getDifference (l1, new CommonsVector <String> ()).size ());
    assertEquals (4, getDifference (l1, null).size ());
    assertEquals (0, getDifference (new CommonsHashSet <String> (), l2).size ());
    assertEquals (0, getDifference (null, l2).size ());
  }

  @Test
  public void testGetIntersected ()
  {
    final ICommonsList <String> l1 = newList ("Hallo", "Welt", "from", "Vienna");
    final ICommonsList <String> l2 = newList ("Welt", "from");

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

    final ICommonsCollection <String> c = newList ("s1", "s2");
    assertNotNull (makeUnmodifiable (c));
    assertNotSame (c, makeUnmodifiable (c));
    final ICommonsList <String> l = newList ("s1", "s2");
    assertNotNull (makeUnmodifiable (l));
    assertNotSame (l, makeUnmodifiable (l));
    final ICommonsSet <String> s = newSet ("s1", "s2");
    assertNotNull (makeUnmodifiable (s));
    assertNotSame (s, makeUnmodifiable (s));
    final ICommonsSortedSet <String> ss = new CommonsTreeSet <> (s);
    assertNotNull (makeUnmodifiable (ss));
    assertNotSame (ss, makeUnmodifiable (ss));
    final ICommonsMap <String, String> m = newMap ("s1", "s2");
    assertNotNull (makeUnmodifiable (m));
    assertNotSame (m, makeUnmodifiable (m));
    final ICommonsSortedMap <String, String> sm = new CommonsTreeMap <> (m);
    assertNotNull (makeUnmodifiable (sm));
    assertNotSame (sm, makeUnmodifiable (sm));
  }

  @Test
  @SuppressFBWarnings ("NP_NONNULL_PARAM_VIOLATION")
  public void testMakeUnmodifiableNotNull ()
  {
    assertNotNull (makeUnmodifiableNotNull ((Collection <?>) null));
    assertNotNull (makeUnmodifiableNotNull ((ICommonsList <?>) null));
    assertNotNull (makeUnmodifiableNotNull ((Set <?>) null));
    assertNotNull (makeUnmodifiableNotNull ((SortedSet <?>) null));
    assertNotNull (makeUnmodifiableNotNull ((Map <?, ?>) null));
    assertNotNull (makeUnmodifiableNotNull ((SortedMap <?, ?>) null));

    final ICommonsCollection <String> c = newList ("s1", "s2");
    assertNotNull (makeUnmodifiableNotNull (c));
    assertNotSame (c, makeUnmodifiableNotNull (c));
    final ICommonsList <String> l = newList ("s1", "s2");
    assertNotNull (makeUnmodifiableNotNull (l));
    assertNotSame (l, makeUnmodifiableNotNull (l));
    final ICommonsSet <String> s = newSet ("s1", "s2");
    assertNotNull (makeUnmodifiableNotNull (s));
    assertNotSame (s, makeUnmodifiableNotNull (s));
    final ICommonsSortedSet <String> ss = new CommonsTreeSet <> (s);
    assertNotNull (makeUnmodifiableNotNull (ss));
    assertNotSame (ss, makeUnmodifiableNotNull (ss));
    final ICommonsMap <String, String> m = newMap ("s1", "s2");
    assertNotNull (makeUnmodifiableNotNull (m));
    assertNotSame (m, makeUnmodifiableNotNull (m));
    final ICommonsSortedMap <String, String> sm = new CommonsTreeMap <> (m);
    assertNotNull (makeUnmodifiableNotNull (sm));
    assertNotSame (sm, makeUnmodifiableNotNull (sm));
  }

  @Test
  public void testNewMap_Empty ()
  {
    assertNotNull (newMap ());
    assertTrue (newMap ().isEmpty ());
  }

  @Test
  public void testNewMap_KeyValue ()
  {
    final Map <String, Integer> aMap = newMap ("Hallo", I5);
    assertNotNull (aMap);
    assertEquals (1, aMap.size ());
    assertNotNull (aMap.get ("Hallo"));
    assertEquals (I5, aMap.get ("Hallo"));
  }

  @Test
  public void testNewMap_Map ()
  {
    final Map <String, Integer> aMap = newMap ("Hallo", I5);
    assertNotNull (aMap);

    final Map <String, Integer> aMap2 = newMap (aMap);
    assertEquals (1, aMap2.size ());
    assertNotNull (aMap2.get ("Hallo"));
    assertEquals (I5, aMap2.get ("Hallo"));
  }

  @Test
  public void testNewMap_MapArray ()
  {
    final Map <String, Integer> aMapA = newMap ("Hallo", I5);
    final Map <String, Integer> aMapB = newMap ("Welt", I3);

    Map <String, Integer> aMap2 = newMap (ArrayHelper.newArray (aMapA, aMapB));
    assertEquals (2, aMap2.size ());
    assertEquals (I5, aMap2.get ("Hallo"));
    assertEquals (I3, aMap2.get ("Welt"));

    aMap2 = newMap (ArrayHelper.newArray (aMapA, aMapA));
    assertEquals (1, aMap2.size ());
    assertEquals (I5, aMap2.get ("Hallo"));
  }

  @Test
  public void testNewMap_Array ()
  {
    assertNotNull (newMap ((Object []) null));
    assertTrue (newMap ((Object []) null).isEmpty ());

    try
    {
      // odd number of parameters not allowed
      newMap ("Hallo", "Welt", "from");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    final Map <String, String> aMap = newMap ("Hallo", "Welt", "from", "Vienna");
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
      newMap ((Object []) null, new String [] { "a" });
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // null values not allowed
      newMap (new String [] { "a" }, (Object []) null);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // different length not allowed
      newMap (new String [0], new String [1]);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    Map <Integer, String> aMap = newMap (new Integer [] { I2, I4 }, new String [] { "Hallo", "Welt" });
    assertNotNull (aMap);
    assertEquals (2, aMap.size ());
    assertNotNull (aMap.get (I2));
    assertEquals ("Hallo", aMap.get (I2));
    assertNotNull (aMap.get (I4));
    assertEquals ("Welt", aMap.get (I4));

    aMap = newMap (new Integer [] {}, new String [] {});
    assertNotNull (aMap);
    assertEquals (0, aMap.size ());
  }

  @Test
  public void testNewMap_CollectionCollection ()
  {
    final ICommonsList <String> aKeys = newList ("d", "c", "b", "a");
    final ICommonsList <Integer> aValues = newPrimitiveList (4, 3, 2, 1);
    try
    {
      // null keys not allowed
      newMap (null, aValues);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // null values not allowed
      newMap (aKeys, null);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    Map <String, Integer> aMap = newMap (aKeys, aValues);
    assertNotNull (aMap);
    assertTrue (aMap.keySet ().containsAll (aKeys));
    assertTrue (aMap.values ().containsAll (aValues));

    try
    {
      // There are more values than keys
      aValues.add (Integer.valueOf (42));
      newMap (aKeys, aValues);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // Simple test for newMap (Map)
    assertEquals (aMap, newMap (aMap));
    assertEquals (aMap, newMap (aMap.entrySet ()));

    aKeys.clear ();
    aValues.clear ();
    aMap = newMap (aKeys, aValues);
    assertNotNull (aMap);
    assertTrue (aMap.isEmpty ());

    // Simple test for newMap (Map)
    assertEquals (aMap, newMap (aMap));
    assertEquals (aMap, newMap (aMap.entrySet ()));
  }

  @Test
  public void testNewSortedMap_Empty ()
  {
    assertNotNull (CollectionHelper.<String, String> newSortedMap ());
    assertTrue (CollectionHelper.<String, String> newSortedMap ().isEmpty ());
  }

  @Test
  public void testNewSortedMap_KeyValue ()
  {
    final SortedMap <String, Integer> aSortedMap = newSortedMap ("Hallo", I5);
    assertNotNull (aSortedMap);
    assertEquals (1, aSortedMap.size ());
    assertNotNull (aSortedMap.get ("Hallo"));
    assertEquals (I5, aSortedMap.get ("Hallo"));
  }

  @Test
  public void testNewSortedMap_Array ()
  {
    assertNotNull (newSortedMap ((String []) null));
    assertTrue (newSortedMap ((String []) null).isEmpty ());

    try
    {
      // odd number of parameters not allowed
      newSortedMap ("Hallo", "Welt", "from");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    final SortedMap <String, String> aSortedMap = newSortedMap ("Hallo", "Welt", "from", "Vienna");
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
      newSortedMap ((String []) null, new String [] { "a" });
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // null values not allowed
      newSortedMap (new String [] { "a" }, (Object []) null);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // different length not allowed
      newSortedMap (new String [0], new String [1]);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    SortedMap <Integer, String> aSortedMap = newSortedMap (new Integer [] { I2, I4 },
                                                           new String [] { "Hallo", "Welt" });
    assertNotNull (aSortedMap);
    assertEquals (2, aSortedMap.size ());
    assertNotNull (aSortedMap.get (I2));
    assertEquals ("Hallo", aSortedMap.get (I2));
    assertNotNull (aSortedMap.get (I4));
    assertEquals ("Welt", aSortedMap.get (I4));

    aSortedMap = newSortedMap (new Integer [] {}, new String [] {});
    assertNotNull (aSortedMap);
    assertEquals (0, aSortedMap.size ());
  }

  @Test
  public void testNewSortedMap_CollectionCollection ()
  {
    final ICommonsList <String> aKeys = newList ("d", "c", "b", "a");
    final ICommonsList <Integer> aValues = newPrimitiveList (4, 3, 2, 1);
    try
    {
      // null keys not allowed
      newSortedMap ((ICommonsList <String>) null, aValues);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // null values not allowed
      newSortedMap (aKeys, null);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    SortedMap <String, Integer> aSortedMap = newSortedMap (aKeys, aValues);
    assertNotNull (aSortedMap);
    assertTrue (aSortedMap.keySet ().containsAll (aKeys));
    assertTrue (aSortedMap.values ().containsAll (aValues));

    try
    {
      // There are more values than keys
      aValues.add (Integer.valueOf (42));
      newSortedMap (aKeys, aValues);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // Simple test for newSortedMap (SortedMap)
    assertEquals (aSortedMap, newSortedMap (aSortedMap));
    assertEquals (aSortedMap, newSortedMap (aSortedMap.entrySet ()));

    aKeys.clear ();
    aValues.clear ();
    aSortedMap = newSortedMap (aKeys, aValues);
    assertNotNull (aSortedMap);
    assertTrue (aSortedMap.isEmpty ());

    // Simple test for newSortedMap (SortedMap)
    assertEquals (aSortedMap, newSortedMap (aSortedMap));
    assertEquals (aSortedMap, newSortedMap (aSortedMap.entrySet ()));
  }

  @Test
  public void testNewOrderedMap_Empty ()
  {
    assertNotNull (newOrderedMap ());
    assertTrue (newOrderedMap ().isEmpty ());
  }

  @Test
  public void testNewOrderedMap_KeyValue ()
  {
    final Map <String, Integer> aMap = newOrderedMap ("Hallo", I5);
    assertNotNull (aMap);
    assertEquals (1, aMap.size ());
    assertNotNull (aMap.get ("Hallo"));
    assertEquals (I5, aMap.get ("Hallo"));
  }

  @Test
  public void testNewOrderedMap_Array ()
  {
    assertNotNull (newOrderedMap ((Object []) null));
    assertTrue (newOrderedMap ((Object []) null).isEmpty ());

    try
    {
      // odd number of parameters not allowed
      newOrderedMap ("Hallo", "Welt", "from");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    final Map <String, String> aMap = newOrderedMap ("Hallo", "Welt", "from", "Vienna");
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
    ICommonsMap <String, Integer> aMap = newOrderedMap (new String [] { "Hallo", "Alice" }, new Integer [] { I1, I2 });
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
      newOrderedMap (null, new Integer [] { I1, I2 });
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // value array may not be null
      newOrderedMap (new String [] { "Hallo", "Alice" }, null);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // key and value array need to have the same length
      newOrderedMap (new String [] { "Hallo", "Alice" }, new Integer [] { I1, });
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    aMap = newOrderedMap (new String [] {}, new Integer [] {});
    assertNotNull (aMap);
    assertEquals (0, aMap.size ());
  }

  @Test
  public void testNewOrderedMap_CollectionCollection ()
  {
    final ICommonsList <String> aKeys = newList ("d", "c", "b", "a");
    final ICommonsList <Integer> aValues = newPrimitiveList (4, 3, 2, 1);
    try
    {
      // null keys not allowed
      newOrderedMap (null, aValues);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // null values not allowed
      newOrderedMap (aKeys, null);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    Map <String, Integer> aMap = newOrderedMap (aKeys, aValues);
    assertNotNull (aMap);
    assertTrue (aMap.keySet ().containsAll (aKeys));
    assertTrue (aMap.values ().containsAll (aValues));

    try
    {
      // There are more values than keys
      aValues.add (Integer.valueOf (42));
      newOrderedMap (aKeys, aValues);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // Simple test for newMap (Map)
    assertEquals (aMap, newOrderedMap (aMap));
    assertEquals (aMap, newOrderedMap (aMap.entrySet ()));

    aKeys.clear ();
    aValues.clear ();
    aMap = newOrderedMap (aKeys, aValues);
    assertNotNull (aMap);
    assertTrue (aMap.isEmpty ());

    // Simple test for newMap (Map)
    assertEquals (aMap, newOrderedMap (aMap));
    assertEquals (aMap, newOrderedMap (aMap.entrySet ()));
  }

  @Test
  public void testNewSet_Empty ()
  {
    final Set <String> aSet = newSet ();
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  public void testNewSet_SingleValue ()
  {
    Set <String> aSet = newSet ("Hallo");
    assertNotNull (aSet);
    assertEquals (1, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));

    aSet = newSet ((String) null);
    assertNotNull (aSet);
    assertEquals (1, aSet.size ());
    assertTrue (aSet.contains (null));
  }

  @Test
  public void testNewSet_Array ()
  {
    Set <String> aSet = newSet ("Hallo", "Welt");
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));

    aSet = newSet (new String [0]);
    assertNotNull (aSet);

    aSet = newSet ((String []) null);
    assertNotNull (aSet);
  }

  @Test
  public void testNewSetIterable ()
  {
    Set <String> aSet = newSet ((Iterable <String>) newList ("Hallo", "Welt"));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));

    aSet = newSet ((Iterable <String>) new CommonsArrayList <String> ());
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  public void testNewSetCollection ()
  {
    Set <String> aSet = newSet (newList ("Hallo", "Welt"));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));

    aSet = newSet (new CommonsArrayList <String> ());
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  public void testNewSetIIterableIterator ()
  {
    Set <String> aSet = newSet (new IterableIterator <> (newList ("Hallo", "Welt")));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));

    aSet = newSet (new IterableIterator <> (new CommonsArrayList <String> ()));
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  public void testNewSetEnumeration ()
  {
    Set <String> aSet = newSet (getEnumeration (newList ("Hallo", "Welt")));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));

    aSet = newSet (getEnumeration (new CommonsArrayList <String> ()));
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  public void testNewSetIterator ()
  {
    final Iterator <String> it = newSet ("Hallo", "Welt").iterator ();
    final Set <String> aUnmodifiableSet = newSet (it);
    assertNotNull (aUnmodifiableSet);
    assertEquals (2, aUnmodifiableSet.size ());
    assertTrue (aUnmodifiableSet.contains ("Hallo"));
    assertTrue (aUnmodifiableSet.contains ("Welt"));
  }

  @Test
  public void testNewSortedSet_Empty ()
  {
    final SortedSet <String> aSet = newSortedSet ();
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  @SuppressFBWarnings ("NP_NONNULL_PARAM_VIOLATION")
  public void testNewSortedSet_SingleValue ()
  {
    SortedSet <String> aSet = newSortedSet ("Hallo");
    assertNotNull (aSet);
    assertEquals (1, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));

    aSet = newSortedSet ((String) null);
    assertNotNull (aSet);
    assertEquals (1, aSet.size ());
    assertTrue (aSet.contains (null));
  }

  @Test
  public void testNewSortedSet_Array ()
  {
    SortedSet <String> aSet = newSortedSet ("Hallo", "Welt");
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));

    aSet = newSortedSet (new String [0]);
    assertNotNull (aSet);

    aSet = newSortedSet ((String []) null);
    assertNotNull (aSet);
  }

  @Test
  public void testNewSortedSetIterable ()
  {
    SortedSet <String> aSet = newSortedSet ((Iterable <String>) newList ("Hallo", "Welt"));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));

    aSet = newSortedSet ((Iterable <String>) new CommonsArrayList <String> ());
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  public void testNewSortedSetCollection ()
  {
    SortedSet <String> aSet = newSortedSet (newList ("Hallo", "Welt"));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));

    aSet = newSortedSet (new CommonsArrayList <String> ());
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  @SuppressFBWarnings ("NP_NONNULL_PARAM_VIOLATION")
  public void testNewSortedSetIIterableIterator ()
  {
    SortedSet <String> aSet = newSortedSet (new IterableIterator <> (newList ("Hallo", "Welt", null)));
    assertNotNull (aSet);
    assertEquals (3, aSet.size ());
    assertNull (aSet.first ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));
    assertTrue (aSet.contains (null));

    aSet = newSortedSet (new IterableIterator <> (new CommonsArrayList <String> ()));
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  public void testNewSortedSetEnumeration ()
  {
    SortedSet <String> aSet = newSortedSet (getEnumeration (newList ("Hallo", "Welt")));
    assertNotNull (aSet);
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains ("Hallo"));
    assertTrue (aSet.contains ("Welt"));

    aSet = newSortedSet (getEnumeration (new CommonsArrayList <String> ()));
    assertNotNull (aSet);
    assertEquals (0, aSet.size ());
  }

  @Test
  public void testNewSortedSetIterator ()
  {
    final Iterator <String> it = newSortedSet ("Hallo", "Welt").iterator ();
    final SortedSet <String> aUnmodifiableSet = newSortedSet (it);
    assertNotNull (aUnmodifiableSet);
    assertEquals (2, aUnmodifiableSet.size ());
    assertTrue (aUnmodifiableSet.contains ("Hallo"));
    assertTrue (aUnmodifiableSet.contains ("Welt"));
  }

  @Test
  public void testNewOrderedSetEmpty ()
  {
    final Set <String> aOrderedSet = newOrderedSet ();
    assertNotNull (aOrderedSet);
    assertEquals (0, aOrderedSet.size ());
  }

  @Test
  public void testNewOrderedSetSingleValue ()
  {
    Set <String> aOrderedSet = newOrderedSet ("Hallo");
    assertNotNull (aOrderedSet);
    assertEquals (1, aOrderedSet.size ());
    assertTrue (aOrderedSet.contains ("Hallo"));

    aOrderedSet = newOrderedSet ((String) null);
    assertNotNull (aOrderedSet);
    assertEquals (1, aOrderedSet.size ());
    assertTrue (aOrderedSet.contains (null));
  }

  @Test
  public void testNewOrderedSetArray ()
  {
    Set <String> aOrderedSet = newOrderedSet ("Hallo", "Welt");
    assertNotNull (aOrderedSet);
    assertEquals (2, aOrderedSet.size ());
    assertTrue (aOrderedSet.contains ("Hallo"));
    assertTrue (aOrderedSet.contains ("Welt"));

    aOrderedSet = newOrderedSet (new String [0]);
    assertNotNull (aOrderedSet);

    aOrderedSet = newOrderedSet ((String []) null);
    assertNotNull (aOrderedSet);
  }

  @Test
  public void testNewOrderedSetIterable ()
  {
    Set <String> aOrderedSet = newOrderedSet ((Iterable <String>) newList ("Hallo", "Welt"));
    assertNotNull (aOrderedSet);
    assertEquals (2, aOrderedSet.size ());
    assertTrue (aOrderedSet.contains ("Hallo"));
    assertTrue (aOrderedSet.contains ("Welt"));

    aOrderedSet = newOrderedSet ((Iterable <String>) new CommonsArrayList <String> ());
    assertNotNull (aOrderedSet);
    assertEquals (0, aOrderedSet.size ());
  }

  @Test
  public void testNewOrderedSetCollection ()
  {
    Set <String> aOrderedSet = newOrderedSet (newList ("Hallo", "Welt"));
    assertNotNull (aOrderedSet);
    assertEquals (2, aOrderedSet.size ());
    assertTrue (aOrderedSet.contains ("Hallo"));
    assertTrue (aOrderedSet.contains ("Welt"));

    aOrderedSet = newOrderedSet (new CommonsArrayList <String> ());
    assertNotNull (aOrderedSet);
    assertEquals (0, aOrderedSet.size ());
  }

  @Test
  public void testNewOrderedSetIIterableIterator ()
  {
    Set <String> aOrderedSet = newOrderedSet (new IterableIterator <> (newList ("Hallo", "Welt")));
    assertNotNull (aOrderedSet);
    assertEquals (2, aOrderedSet.size ());
    assertTrue (aOrderedSet.contains ("Hallo"));
    assertTrue (aOrderedSet.contains ("Welt"));

    aOrderedSet = newOrderedSet (new IterableIterator <> (new CommonsArrayList <String> ()));
    assertNotNull (aOrderedSet);
    assertEquals (0, aOrderedSet.size ());
  }

  @Test
  public void testNewOrderedSetEnumeration ()
  {
    Set <String> aOrderedSet = newOrderedSet (getEnumeration (newList ("Hallo", "Welt")));
    assertNotNull (aOrderedSet);
    assertEquals (2, aOrderedSet.size ());
    assertTrue (aOrderedSet.contains ("Hallo"));
    assertTrue (aOrderedSet.contains ("Welt"));

    aOrderedSet = newOrderedSet (getEnumeration (new CommonsArrayList <String> ()));
    assertNotNull (aOrderedSet);
    assertEquals (0, aOrderedSet.size ());
  }

  @Test
  public void testNewOrderedSetIterator ()
  {
    final Iterator <String> it = newOrderedSet ("Hallo", "Welt").iterator ();
    final Set <String> aOrderedSet = newOrderedSet (it);
    assertNotNull (aOrderedSet);
    assertEquals (2, aOrderedSet.size ());
    assertTrue (aOrderedSet.contains ("Hallo"));
    assertTrue (aOrderedSet.contains ("Welt"));
  }

  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  @Test
  public void testNewListPrefilled ()
  {
    ICommonsList <String> aList = newListPrefilled ("s", 5);
    assertNotNull (aList);
    assertEquals (5, aList.size ());
    for (int i = 0; i < 5; ++i)
      assertEquals ("s", aList.get (i));

    aList = newListPrefilled ("s", 0);
    assertNotNull (aList);
    assertEquals (0, aList.size ());

    try
    {
      newListPrefilled ("s", -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testNewListEmpty ()
  {
    final ICommonsList <String> aList = newList ();
    assertNotNull (aList);
    assertEquals (0, aList.size ());
  }

  @Test
  public void testNewListSingleValue ()
  {
    ICommonsList <String> aList = newList ("Hallo");
    assertNotNull (aList);
    assertEquals (1, aList.size ());
    assertTrue (aList.contains ("Hallo"));

    aList = newList ((String) null);
    assertNotNull (aList);
    assertEquals (1, aList.size ());
    assertTrue (aList.contains (null));
  }

  @Test
  public void testNewListArray ()
  {
    ICommonsList <String> aList = newList ("Hallo", "Welt", "from", "Vienna");
    assertNotNull (aList);
    assertEquals (4, aList.size ());
    assertTrue (aList.contains ("Hallo"));
    assertTrue (aList.contains ("Welt"));
    assertTrue (aList.contains ("from"));
    assertTrue (aList.contains ("Vienna"));

    aList = newList ((String []) null);
    assertNotNull (aList);

    aList = newList (new String [0]);
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

    ICommonsList <String> aList = newList (aSource.iterator ());
    assertNotNull (aList);
    assertEquals (4, aList.size ());
    assertTrue (aList.contains ("Hallo"));
    assertTrue (aList.contains ("Welt"));
    assertTrue (aList.contains ("from"));
    assertTrue (aList.contains ("Vienna"));

    aList = newList (new CommonsArrayList <String> ().iterator ());
    assertNotNull (aList);

    aList = newList ((Iterator <String>) null);
    assertNotNull (aList);
  }

  @Test
  public void testNewListCollection ()
  {
    final ICommonsList <String> aSource = newList ("Hallo", "Welt", "from", "Vienna");

    ICommonsList <String> aList = newList (aSource);
    assertNotNull (aList);
    assertEquals (4, aList.size ());
    assertTrue (aList.contains ("Hallo"));
    assertTrue (aList.contains ("Welt"));
    assertTrue (aList.contains ("from"));
    assertTrue (aList.contains ("Vienna"));

    aList = newList (new CommonsArrayList <String> ());
    assertNotNull (aList);

    aList = newList ((ICommonsList <String>) null);
    assertNotNull (aList);
  }

  @Test
  public void testNewListIterable ()
  {
    final ICommonsList <String> aSource = newList ("Hallo", "Welt", "from", "Vienna");

    ICommonsList <String> aList = newList ((Iterable <String>) aSource);
    assertNotNull (aList);
    assertEquals (4, aList.size ());
    assertTrue (aList.contains ("Hallo"));
    assertTrue (aList.contains ("Welt"));
    assertTrue (aList.contains ("from"));
    assertTrue (aList.contains ("Vienna"));

    aList = newList ((Iterable <String>) new CommonsArrayList <String> ());
    assertNotNull (aList);

    aList = newList ((Iterable <String>) null);
    assertNotNull (aList);
  }

  @Test
  public void testNewListIIterableIterator ()
  {
    final ICommonsList <String> aSource = newList ("Hallo", "Welt", "from", "Vienna");

    ICommonsList <String> aList = newList (new IterableIterator <> (aSource));
    assertNotNull (aList);
    assertEquals (4, aList.size ());
    assertTrue (aList.contains ("Hallo"));
    assertTrue (aList.contains ("Welt"));
    assertTrue (aList.contains ("from"));
    assertTrue (aList.contains ("Vienna"));

    aList = newList (new IterableIterator <> (new CommonsArrayList <String> ()));
    assertNotNull (aList);

    aList = newList ((IIterableIterator <String>) null);
    assertNotNull (aList);
  }

  @Test
  public void testGetSortedIterator ()
  {
    assertNotNull (getSorted ((Iterator <String>) null));

    final ICommonsList <String> aList = newList ("d", "c", "b", "a");
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

    final ICommonsList <String> aList = newList ("d", "c", "b", "a");
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
    assertNotNull (getSorted ((IIterableIterator <String>) null));

    final ICommonsList <String> aList = newList ("d", "c", "b", "a");
    ICommonsList <String> aSorted = getSorted (new IterableIterator <> (aList));
    assertEquals (4, aSorted.size ());
    assertEquals ("a", aSorted.get (0));
    assertEquals ("b", aSorted.get (1));
    assertEquals ("c", aSorted.get (2));
    assertEquals ("d", aSorted.get (3));

    aSorted = getSorted (new IterableIterator <> (aList), IComparator.getComparatorCollating (Locale.US));
    assertEquals (4, aSorted.size ());
    assertEquals ("a", aSorted.get (0));
    assertEquals ("b", aSorted.get (1));
    assertEquals ("c", aSorted.get (2));
    assertEquals ("d", aSorted.get (3));
  }

  private static final class MyStringCompi implements IComparator <String>
  {
    public int compare (@Nonnull final String sStr1, @Nonnull final String sStr2)
    {
      if (sStr1.equals ("b"))
        return -1;
      if (sStr2.equals ("b"))
        return +1;
      return sStr1.compareTo (sStr2);
    }
  }

  @Test
  public void testGetSortedFromIteratorWithCompi ()
  {
    assertNotNull (getSorted ((Iterator <String>) null, new MyStringCompi ()));

    final ICommonsList <String> aList = newList ("d", "c", "b", "a");

    try
    {
      // null comparator not allowed
      getSorted (aList.iterator (), (Comparator <String>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final ICommonsList <String> aSorted = getSorted (aList.iterator (), new MyStringCompi ());
    assertEquals (4, aSorted.size ());
    assertEquals ("b", aSorted.get (0));
    assertEquals ("a", aSorted.get (1));
    assertEquals ("c", aSorted.get (2));
    assertEquals ("d", aSorted.get (3));
  }

  @Test
  public void testGetSortedIterableWithCompi ()
  {
    assertNotNull (getSorted ((Iterable <String>) null, new MyStringCompi ()));

    final ICommonsList <String> aList = newList ("d", "c", "b", "a");

    try
    {
      // null comparator not allowed
      getSorted (aList, (Comparator <String>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final ICommonsList <String> aSorted = getSorted (aList, new MyStringCompi ());
    assertEquals (4, aSorted.size ());
    assertEquals ("b", aSorted.get (0));
    assertEquals ("a", aSorted.get (1));
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
  public void testGetSortedArrayWithCompi ()
  {
    assertNotNull (getSorted ((String []) null, new MyStringCompi ()));

    try
    {
      // null comparator not allowed
      getSorted (new String [0], null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final ICommonsList <String> aSorted = getSorted (new String [] { "d", "c", "b", "a" }, new MyStringCompi ());
    assertEquals (4, aSorted.size ());
    assertEquals ("b", aSorted.get (0));
    assertEquals ("a", aSorted.get (1));
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
    assertFalse (isEmpty (newList ("d", "c", "b", "a")));
    assertTrue (isEmpty ((Iterable <?>) new NonBlockingStack <> ()));
  }

  @Test
  public void testIsEmptyCollection ()
  {
    assertTrue (isEmpty ((Collection <?>) null));
    assertTrue (isEmpty ((Map <?, ?>) null));
    assertTrue (isEmpty (new CommonsArrayList <String> ()));
    assertTrue (isEmpty (new CommonsVector <String> ()));
    assertTrue (isEmpty (new CommonsHashSet <String> ()));
    assertTrue (isEmpty (new CommonsTreeSet <String> ()));
    assertTrue (isEmpty (new CommonsHashMap <String, String> ()));
    assertTrue (isEmpty (new CommonsLinkedHashMap <String, String> ()));

    assertFalse (isEmpty (newList ("Hallo")));
    assertFalse (isEmpty (newMap ("Hallo", "Welt")));
  }

  @Test
  public void testSize ()
  {
    assertEquals (2, getSize (newList ("Ha", "We")));
    assertEquals (1, getSize (newMap ("Ha", "We")));
    assertEquals (0, getSize ((Collection <String>) null));
    assertEquals (0, getSize ((Map <String, Double>) null));
  }

  @Test
  public void testGetFilteredMap ()
  {
    assertNull (getFilteredMap (null, newList ("a")));
    assertNull (getFilteredMap (newMap ("a", "value-of-a"), null));

    final Map <String, String> aFilteredMap = getFilteredMap (newMap ("a", "value-of-a", "b", "value-of-b"),
                                                              newList ("a"));
    assertNotNull (aFilteredMap);
    assertEquals (1, aFilteredMap.size ());
    assertTrue (aFilteredMap.containsKey ("a"));
    assertEquals ("value-of-a", aFilteredMap.get ("a"));
  }

  /**
   * Test for method getSortedByKey
   */
  @Test
  public void testGetSortedByKey ()
  {
    assertNotNull (getSortedByKey ((Map <String, ?>) null));
    assertNotNull (getSortedByKey (null, IComparator.getComparatorCollating (Locale.US).reversed ()));

    try
    {
      // null Comparator
      getSortedByKey (newMap (), (Comparator <String>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final Map <String, String> aMap = newMap ("K2", "ValueA", "K3", "ValueB", "K1", "ValueC");
    assertNotNull (aMap);
    assertEquals (3, aMap.size ());

    Iterator <Map.Entry <String, String>> it = getSortedByKey (aMap).entrySet ().iterator ();
    assertEquals ("K1", it.next ().getKey ());
    assertEquals ("K2", it.next ().getKey ());
    assertEquals ("K3", it.next ().getKey ());

    // reverse sort
    it = getSortedByKey (aMap, IComparator.getComparatorCollating (Locale.US).reversed ()).entrySet ().iterator ();
    assertEquals ("K3", it.next ().getKey ());
    assertEquals ("K2", it.next ().getKey ());
    assertEquals ("K1", it.next ().getKey ());
  }

  /**
   * Test for method getSortedByValue
   */
  @Test
  public void testGetSortedByValue ()
  {
    assertNotNull (getSortedByValue ((Map <?, String>) null));
    assertNotNull (getSortedByValue (null, IComparator.getComparatorCollating (Locale.US).reversed ()));

    try
    {
      // null Comparator
      getSortedByValue (newMap (), (Comparator <String>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final Map <String, String> aMap = newMap ("K1", "ValueB", "K2", "ValueC", "K3", "ValueA");
    assertNotNull (aMap);
    assertEquals (3, aMap.size ());

    Iterator <Map.Entry <String, String>> it = getSortedByValue (aMap).entrySet ().iterator ();
    assertEquals ("ValueA", it.next ().getValue ());
    assertEquals ("ValueB", it.next ().getValue ());
    assertEquals ("ValueC", it.next ().getValue ());

    // reverse sort
    it = getSortedByValue (aMap, IComparator.getComparatorCollating (Locale.US).reversed ()).entrySet ().iterator ();
    assertEquals ("ValueC", it.next ().getValue ());
    assertEquals ("ValueB", it.next ().getValue ());
    assertEquals ("ValueA", it.next ().getValue ());
  }

  @Test
  public void testGetReverseList ()
  {
    assertNotNull (getReverseList (null));
    assertTrue (getReverseList (null).isEmpty ());

    // Make it not sorted :)
    final ICommonsList <String> aList = newList ("1", "3", "2");
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
    ICommonsList <String> aList = newList ("1", "3", "2");

    // Sort inline
    assertSame (aList, getReverseInlineList (aList));
    assertEquals (3, aList.size ());
    assertEquals ("2", aList.get (0));
    assertEquals ("3", aList.get (1));
    assertEquals ("1", aList.get (2));

    aList = newList ();
    assertSame (aList, getReverseInlineList (aList));
    assertEquals (0, aList.size ());

    assertNull (getReverseInlineList (null));
  }

  @Test
  public void testFirstAndLast ()
  {
    final ICommonsList <String> aList = newList ("s1", "s2", "s3");
    final Set <String> aSet = new CommonsLinkedHashSet <> (aList);

    assertNull (removeFirstElement (new CommonsArrayList <String> ()));
    assertNull (removeFirstElement ((ICommonsList <String>) null));

    assertEquals ("s1", getFirstElement (aList));
    assertEquals ("s1", getFirstElement (aSet));
    assertEquals ("s1", getFirstElement ((Iterable <String>) aSet));
    assertEquals ("s1", removeFirstElement (aList));
    assertNull (getFirstElement (new CommonsArrayList <String> ()));
    assertNull (getFirstElement (new CommonsHashSet <String> ()));
    assertNull (getFirstElement ((Iterable <String>) new CommonsHashSet <String> ()));
    assertNull (getFirstElement ((ICommonsList <String>) null));
    assertNull (getFirstElement ((Set <String>) null));
    assertNull (getFirstElement ((Iterable <String>) null));

    assertNull (removeLastElement (new CommonsArrayList <> ()));
    assertNull (removeLastElement ((ICommonsList <String>) null));

    assertEquals ("s3", getLastElement (aList));
    assertEquals ("s3", getLastElement (aSet));
    assertEquals ("s3", getLastElement ((Iterable <String>) aSet));
    assertEquals ("s3", removeLastElement (aList));
    assertNull (getLastElement (new CommonsArrayList <String> ()));
    assertNull (getLastElement (new CommonsHashSet <String> ()));
    assertNull (getLastElement ((Iterable <String>) new CommonsHashSet <String> ()));
    assertNull (getLastElement ((ICommonsList <String>) null));
    assertNull (getLastElement ((Set <String>) null));
    assertNull (getLastElement ((Iterable <String>) null));
  }

  @Test
  public void testIsEmpty_Iterable ()
  {
    assertTrue (isEmpty ((Iterable <?>) null));
    assertTrue (isEmpty ((Iterable <String>) new CommonsArrayList <String> ()));
    assertFalse (isEmpty ((Iterable <String>) newList ("any")));
  }

  @Test
  public void testIsEmpty_Collection ()
  {
    assertTrue (isEmpty ((Collection <?>) null));
    assertTrue (isEmpty (new CommonsArrayList <String> ()));
    assertFalse (isEmpty (newList ("any")));
  }

  @Test
  public void testIsEmpty_Map ()
  {
    assertTrue (isEmpty ((Map <?, ?>) null));
    assertTrue (isEmpty (new CommonsHashMap <String, Double> ()));
    assertFalse (isEmpty (newMap ("any", "value")));
  }

  @Test
  public void testGetSize_Collection ()
  {
    assertEquals (0, getSize ((Collection <?>) null));
    assertEquals (0, getSize (new CommonsArrayList <String> ()));
    assertEquals (1, getSize (newList ("any")));
  }

  @Test
  public void testGetSize_Map ()
  {
    assertEquals (0, getSize ((Map <?, ?>) null));
    assertEquals (0, getSize (new CommonsHashMap <BigDecimal, String> ()));
    assertEquals (1, getSize (newMap ("key", "value")));
  }

  @Test
  public void testGetSize_Iterable ()
  {
    assertEquals (0, getSize ((Iterable <?>) null));
    assertEquals (0, getSize ((Iterable <String>) new CommonsArrayList <String> ()));
    assertEquals (1, getSize ((Iterable <String>) newList ("any")));
  }

  @Test
  public void testGetConcatenatedList_CollectionCollection ()
  {
    final ICommonsList <String> a = newList ("a", "b");
    final ICommonsList <String> b = newList ("c", "d");
    assertTrue (getConcatenatedList ((Collection <String>) null, (Collection <String>) null).isEmpty ());
    assertEquals (a, getConcatenatedList (a, (Collection <String>) null));
    assertEquals (b, getConcatenatedList ((Collection <String>) null, b));
    assertEquals (newList ("a", "b", "c", "d"), getConcatenatedList (a, b));
  }

  @Test
  public void testGetConcatenatedList_CollectionArray ()
  {
    final ICommonsList <String> a = newList ("a", "b");
    final String [] b = ArrayHelper.newArray ("c", "d");
    assertTrue (getConcatenatedList ((Collection <String>) null, (String []) null).isEmpty ());
    assertEquals (a, getConcatenatedList (a, (String []) null));
    assertEquals (newList (b), getConcatenatedList ((Collection <String>) null, b));
    assertEquals (newList ("a", "b", "c", "d"), getConcatenatedList (a, b));
  }

  @Test
  public void testGetConcatenatedList_ArrayCollection ()
  {
    final String [] a = ArrayHelper.newArray ("a", "b");
    final ICommonsList <String> b = newList ("c", "d");
    assertTrue (getConcatenatedList ((String []) null, (Collection <String>) null).isEmpty ());
    assertEquals (newList (a), getConcatenatedList (a, (Collection <String>) null));
    assertEquals (b, getConcatenatedList ((String []) null, b));
    assertEquals (newList ("a", "b", "c", "d"), getConcatenatedList (a, b));
  }

  @Test
  public void testGetConcatenatedSet_CollectionCollection ()
  {
    final Set <String> a = newSet ("a", "b");
    final Set <String> b = newSet ("c", "d");
    assertTrue (getConcatenatedSet ((Collection <String>) null, (Collection <String>) null).isEmpty ());
    assertEquals (a, getConcatenatedSet (a, (Collection <String>) null));
    assertEquals (b, getConcatenatedSet ((Collection <String>) null, b));
    assertEquals (newSet ("a", "b", "c", "d"), getConcatenatedSet (a, b));
  }

  @Test
  public void testGetConcatenatedSet_CollectionArray ()
  {
    final Set <String> a = newSet ("a", "b");
    final String [] b = ArrayHelper.newArray ("c", "d");
    assertTrue (getConcatenatedSet ((Collection <String>) null, (String []) null).isEmpty ());
    assertEquals (a, getConcatenatedSet (a, (String []) null));
    assertEquals (newSet (b), getConcatenatedSet ((Collection <String>) null, b));
    assertEquals (newSet ("a", "b", "c", "d"), getConcatenatedSet (a, b));
  }

  @Test
  public void testGetConcatenatedSet_ArrayCollection ()
  {
    final String [] a = ArrayHelper.newArray ("a", "b");
    final Set <String> b = newSet ("c", "d");
    assertTrue (getConcatenatedSet ((String []) null, (Collection <String>) null).isEmpty ());
    assertEquals (newSet (a), getConcatenatedSet (a, (Collection <String>) null));
    assertEquals (b, getConcatenatedSet ((String []) null, b));
    assertEquals (newSet ("a", "b", "c", "d"), getConcatenatedSet (a, b));
  }

  @Test
  public void testGetConcatenatedInline ()
  {
    // Array version
    ICommonsList <String> aBaseList = newList ("1");
    assertSame (aBaseList, getConcatenatedInline (aBaseList, "2", "3"));
    assertEquals (3, aBaseList.size ());
    assertEquals ("1", aBaseList.get (0));
    assertEquals ("3", aBaseList.get (2));
    assertSame (aBaseList, getConcatenatedInline (aBaseList, (String []) null));
    assertEquals (3, aBaseList.size ());

    // Collection version
    aBaseList = newList ("1");
    assertSame (aBaseList, getConcatenatedInline (aBaseList, newList ("2", "3")));
    assertEquals (3, aBaseList.size ());
    assertEquals ("1", aBaseList.get (0));
    assertEquals ("3", aBaseList.get (2));
    assertSame (aBaseList, getConcatenatedInline (aBaseList, (Collection <String>) null));
    assertEquals (3, aBaseList.size ());

    // Set test
    final Set <String> aBaseSet = newSet ("1");
    assertSame (aBaseSet, getConcatenatedInline (aBaseSet, "2", "3"));
    assertEquals (3, aBaseSet.size ());
    assertTrue (aBaseSet.contains ("1"));
    assertTrue (aBaseSet.contains ("3"));
    assertSame (aBaseSet, getConcatenatedInline (aBaseSet, (String []) null));
    assertEquals (3, aBaseSet.size ());

    try
    {
      getConcatenatedInline ((ICommonsList <String>) null, new String [0]);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      getConcatenatedInline ((ICommonsList <String>) null, newList ("a"));
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetCombinedMap ()
  {
    final Map <String, Integer> m1 = newMap ("Hallo", I1);
    final Map <String, Integer> m2 = newMap ("Welt", I2);
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
    assertNotNull (newObjectListFromArray (new Integer [] { Integer.valueOf (2), Integer.valueOf (0x7f) },
                                           Integer.class));
    assertNotNull (newObjectListFromArray (new boolean [] { true, false }, boolean.class));
    assertNotNull (newObjectListFromArray (new byte [] { (byte) 2, (byte) 0x7f }, byte.class));
    assertNotNull (newObjectListFromArray (new char [] { 'a', 'Z' }, char.class));
    assertNotNull (newObjectListFromArray (new double [] { 3.14, 47.11 }, double.class));
    assertNotNull (newObjectListFromArray (new float [] { 3.14f, 47.11f }, float.class));
    assertNotNull (newObjectListFromArray (new int [] { 314, 4711 }, int.class));
    assertNotNull (newObjectListFromArray (new long [] { 314, 4711 }, long.class));
    assertNotNull (newObjectListFromArray (new short [] { 123, 255 }, short.class));

    assertNull (newObjectListFromArray (null, Integer.class));
    assertNull (newObjectListFromArray (null, boolean.class));
    assertNull (newObjectListFromArray (null, byte.class));
    assertNull (newObjectListFromArray (null, char.class));
    assertNull (newObjectListFromArray (null, double.class));
    assertNull (newObjectListFromArray (null, float.class));
    assertNull (newObjectListFromArray (null, int.class));
    assertNull (newObjectListFromArray (null, long.class));
    assertNull (newObjectListFromArray (null, short.class));
  }

  @Test
  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  public void testGetSubList ()
  {
    assertNotNull (getSubList (null, 0, 5));

    try
    {
      // start index may not be < 0
      getSubList (new CommonsArrayList <String> (), -1, 2);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // length not be < 0
      getSubList (new CommonsArrayList <String> (), 0, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    final ICommonsList <String> aSource = newList ("a", "b", "c", "d");

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

    final ICommonsList <String> aList = newList ("a", "b");
    assertNull (getAtIndex (aList, -1));
    assertEquals ("a", getAtIndex (aList, 0));
    assertNull (getAtIndex (aList, 2));

    assertEquals ("x", getAtIndex (aList, -1, "x"));
    assertEquals ("a", getAtIndex (aList, 0, "x"));
    assertEquals ("x", getAtIndex (aList, 2, "x"));
  }

  @Test
  public void testContainsNullElement ()
  {
    assertFalse (containsAnyNullElement ((ICommonsList <String>) null));
    assertFalse (containsAnyNullElement (new CommonsArrayList <> ()));
    assertFalse (containsAnyNullElement (newList ("a")));
    assertFalse (containsAnyNullElement (newList ("a", "b", "c")));
    assertTrue (containsAnyNullElement (newList (null, "a")));
    assertTrue (containsAnyNullElement (newList ("a", null)));
    assertTrue (containsAnyNullElement (newList ((String) null)));
    assertTrue (containsAnyNullElement (newList (null, Integer.valueOf (5))));
  }

  @Test
  public void testContainsOnly ()
  {
    assertTrue (containsOnly (new CommonsArrayList <> ("a"), StringHelper::hasText));
    assertTrue (containsOnly (new CommonsArrayList <> ("a", "b"), StringHelper::hasText));
    assertTrue (containsOnly (new CommonsArrayList <> ("a"), null));
    assertTrue (containsOnly (new CommonsArrayList <> ("a", "b"), null));
    assertTrue (containsOnly (new CommonsArrayList <> ("a", ""), null));

    assertFalse (containsOnly (new CommonsArrayList <> ("a", ""), StringHelper::hasText));
    assertFalse (containsOnly (new CommonsArrayList <> ("", ""), StringHelper::hasText));
    assertFalse (containsOnly (new CommonsArrayList <String> (), StringHelper::hasText));
    assertFalse (containsOnly (new CommonsArrayList <> (), null));
  }

  /**
   * Created by {@link MainCreateCollectionHelperCode2} to check if all APIs are
   * present
   */
  @Test
  public void testNew ()
  {
    newList ();
    newList ("a");
    newList (new String [] { "a" });
    newList (new CommonsArrayList <> ("a"));
    newList (new IterableIterator <> (new CommonsArrayList <> ("a")));
    newList ((Iterable <String>) new CommonsArrayList <> ("a"));
    newList (new CommonsArrayList <> ("a").iterator ());
    newList (new CommonsArrayList <> ("a"), Objects::nonNull);
    newListMapped (new CommonsArrayList <Object> ("a"), Object::toString);
    newListMapped (new Object [] { "a" }, Object::toString);
    newSet ();
    newSet ("a");
    newSet (new String [] { "a" });
    newSet (new CommonsArrayList <> ("a"));
    newSet (new IterableIterator <> (new CommonsArrayList <> ("a")));
    newSet ((Iterable <String>) new CommonsArrayList <> ("a"));
    newSet (new CommonsArrayList <> ("a").iterator ());
    newSet (new CommonsArrayList <> ("a"), Objects::nonNull);
    newSetMapped (new CommonsArrayList <Object> ("a"), Object::toString);
    newSetMapped (new Object [] { "a" }, Object::toString);
    newOrderedSet ();
    newOrderedSet ("a");
    newOrderedSet (new String [] { "a" });
    newOrderedSet (new CommonsArrayList <> ("a"));
    newOrderedSet (new IterableIterator <> (new CommonsArrayList <> ("a")));
    newOrderedSet ((Iterable <String>) new CommonsArrayList <> ("a"));
    newOrderedSet (new CommonsArrayList <> ("a").iterator ());
    newOrderedSet (new CommonsArrayList <> ("a"), Objects::nonNull);
    newOrderedSetMapped (new CommonsArrayList <Object> ("a"), Object::toString);
    newOrderedSetMapped (new Object [] { "a" }, Object::toString);
    newSortedSet ();
    newSortedSet ("a");
    newSortedSet (new String [] { "a" });
    newSortedSet (new CommonsArrayList <> ("a"));
    newSortedSet (new IterableIterator <> (new CommonsArrayList <> ("a")));
    newSortedSet ((Iterable <String>) new CommonsArrayList <> ("a"));
    newSortedSet (new CommonsArrayList <> ("a").iterator ());
    newSortedSet (new CommonsArrayList <> ("a"), Objects::nonNull);
    newSortedSetMapped (new CommonsArrayList <Object> ("a"), Object::toString);
    newSortedSetMapped (new Object [] { "a" }, Object::toString);
  }
}
