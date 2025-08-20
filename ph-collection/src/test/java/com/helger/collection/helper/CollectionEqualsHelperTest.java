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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;

import org.junit.Test;

import com.helger.base.array.ArrayHelper;
import com.helger.collection.CollectionHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.CommonsLinkedList;
import com.helger.collection.commons.CommonsTreeSet;
import com.helger.collection.commons.CommonsVector;
import com.helger.collection.commons.ICommonsIterable;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.commons.ICommonsSet;
import com.helger.collection.enumeration.EnumerationHelper;
import com.helger.collection.stack.NonBlockingStack;

import jakarta.annotation.Nonnull;

/**
 * Test class for class {@link CollectionEqualsHelper}.
 *
 * @author Philip Helger
 */
public final class CollectionEqualsHelperTest
{
  @Test
  public void testBasic ()
  {
    // Same objects - are tested first
    assertTrue (CollectionEqualsHelper.equalsCollection (null, null));
    assertTrue (CollectionEqualsHelper.equalsCollection ("abc", "abc"));

    // One argument is null
    assertFalse (CollectionEqualsHelper.equalsCollection ("abc", null));
    assertFalse (CollectionEqualsHelper.equalsCollection (null, "abc"));

    // Non-containers
    try
    {
      CollectionEqualsHelper.equalsCollection ("abc", "def");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      CollectionEqualsHelper.equalsCollection (new CommonsArrayList <> ("abc"), "abc");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      CollectionEqualsHelper.equalsCollection ("abc", new CommonsArrayList <> ("abc"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testList ()
  {
    final ICommonsList <String> aCont = new CommonsArrayList <> ("a", "b", "c");
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont, aCont));
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont, CollectionHelper.makeUnmodifiable (aCont)));
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont, Collections.synchronizedList (aCont)));
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont, new CommonsArrayList <> (aCont)));
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont, new CommonsLinkedList <> (aCont)));
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont, new CommonsVector <> (aCont)));
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont, new NonBlockingStack <> (aCont)));
    assertTrue (CollectionEqualsHelper.equalsCollection (new CommonsArrayList <> (), new CommonsLinkedList <> ()));
    assertTrue (CollectionEqualsHelper.equalsCollection (new NonBlockingStack <> (), new CommonsVector <> ()));
    assertTrue (CollectionEqualsHelper.equalsCollection (new NonBlockingStack <> (), new Stack <> ()));

    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new CommonsLinkedList <> ()));
    assertFalse (CollectionEqualsHelper.equalsCollection (new CommonsLinkedList <> (), aCont));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new CommonsArrayList <> ()));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new CommonsArrayList <> ("a", "b")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new CommonsArrayList <> ("A", "b", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new CommonsArrayList <> ("a", "B", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new CommonsArrayList <> ("a", "b", "C")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new CommonsArrayList <> ("a", "b", "c", "d")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new CommonsHashSet <> ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, ArrayHelper.createArray ("a", "b", "c")));
  }

  @Test
  public void testSet ()
  {
    final ICommonsSet <String> aCont = new CommonsHashSet <> ("a", "b", "c");
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont, aCont));
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont, CollectionHelper.makeUnmodifiable (aCont)));
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont, Collections.synchronizedSet (aCont)));
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont, new CommonsHashSet <> (aCont)));
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont, new CommonsLinkedHashSet <> (aCont)));
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont, new CommonsTreeSet <> (aCont)));
    assertTrue (CollectionEqualsHelper.equalsCollection (new CommonsHashSet <> (), new CommonsLinkedHashSet <> ()));
    assertTrue (CollectionEqualsHelper.equalsCollection (new CommonsTreeSet <> (), new CommonsHashSet <> ()));

    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new CommonsHashSet <> ()));
    assertFalse (CollectionEqualsHelper.equalsCollection (new CommonsHashSet <> (), aCont));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new CommonsTreeSet <> ()));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new CommonsHashSet <> ("a", "b")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new CommonsHashSet <> ("A", "b", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new CommonsHashSet <> ("a", "B", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new CommonsHashSet <> ("a", "b", "C")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new CommonsHashSet <> ("a", "b", "c", "d")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new CommonsArrayList <> ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, ArrayHelper.createArray ("a", "b", "c")));
  }

  private static final class StringMap extends CommonsHashMap <String, String>
  {
    public StringMap (final String k, final String v)
    {
      put (k, v);
    }

    @Nonnull
    public StringMap add (final String k, final String v)
    {
      put (k, v);
      return this;
    }
  }

  @Test
  public void testMap ()
  {
    final StringMap aMap = new StringMap ("a", "b").add ("c", "d");
    assertTrue (CollectionEqualsHelper.equalsCollection (aMap, aMap));
    assertTrue (CollectionEqualsHelper.equalsCollection (aMap, CollectionHelper.makeUnmodifiable (aMap)));
    assertTrue (CollectionEqualsHelper.equalsCollection (aMap, Collections.synchronizedMap (aMap)));
    assertTrue (CollectionEqualsHelper.equalsCollection (aMap, new StringMap ("a", "b").add ("c", "d")));
    assertTrue (CollectionEqualsHelper.equalsCollection (new CommonsHashMap <> (), new CommonsHashMap <> ()));

    assertFalse (CollectionEqualsHelper.equalsCollection (aMap, new CommonsHashMap <> ()));
    assertFalse (CollectionEqualsHelper.equalsCollection (new CommonsHashMap <> (), aMap));
    assertFalse (CollectionEqualsHelper.equalsCollection (aMap, new StringMap ("a", "b")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aMap, new StringMap ("A", "b").add ("c", "d")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aMap, new StringMap ("a", "B").add ("c", "d")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aMap, new StringMap ("a", "b").add ("C", "d")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aMap, new StringMap ("a", "b").add ("c", "D")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aMap,
                                                          new StringMap ("a", "b").add ("c", "d").add ("e", "f")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aMap, new CommonsArrayList <> ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aMap, new CommonsHashSet <> ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aMap, ArrayHelper.createArray ("a", "b", "c")));
  }

  @Test
  public void testArray ()
  {
    final String [] aArray = ArrayHelper.createArray ("a", "b", "c");
    assertTrue (CollectionEqualsHelper.equalsCollection (aArray, aArray));
    assertTrue (CollectionEqualsHelper.equalsCollection (aArray, ArrayHelper.createArray ("a", "b", "c")));
    assertTrue (CollectionEqualsHelper.equalsCollection (new String [0], new String [] {}));

    assertFalse (CollectionEqualsHelper.equalsCollection (aArray, new String [0]));
    assertFalse (CollectionEqualsHelper.equalsCollection (new String [0], aArray));
    assertFalse (CollectionEqualsHelper.equalsCollection (aArray, ArrayHelper.createArray ("a", "b")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aArray, ArrayHelper.createArray ("A", "b", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aArray, ArrayHelper.createArray ("a", "B", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aArray, ArrayHelper.createArray ("a", "b", "C")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aArray, ArrayHelper.createArray ("a", "b", "c", "d")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aArray, new CommonsArrayList <> ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aArray, new CommonsHashSet <> ("a", "b", "c")));
  }

  @Test
  public void testArrayComplex ()
  {
    final ICommonsList <String> [] aArray = ArrayHelper.createArray (new CommonsArrayList <> ("a", "b"),
                                                                  new CommonsArrayList <> ("c", "d"));
    assertTrue (CollectionEqualsHelper.equalsCollection (aArray, aArray));
    assertTrue (CollectionEqualsHelper.equalsCollection (aArray,
                                                         ArrayHelper.createArray (new CommonsArrayList <> ("a", "b"),
                                                                               new CommonsArrayList <> ("c", "d"))));
    assertTrue (CollectionEqualsHelper.equalsCollection (new ICommonsList <?> [0], new ICommonsList <?> [] {}));

    assertFalse (CollectionEqualsHelper.equalsCollection (aArray, new ICommonsList <?> [0]));
    assertFalse (CollectionEqualsHelper.equalsCollection (new ICommonsList <?> [0], aArray));
    assertFalse (CollectionEqualsHelper.equalsCollection (aArray,
                                                          ArrayHelper.createArray (new CommonsArrayList <> ("a", "b"))));
    assertFalse (CollectionEqualsHelper.equalsCollection (aArray,
                                                          ArrayHelper.createArray (new CommonsArrayList <> ("A", "b"),
                                                                                new CommonsArrayList <> ("c", "d"))));
    assertFalse (CollectionEqualsHelper.equalsCollection (aArray,
                                                          ArrayHelper.createArray (new CommonsArrayList <> ("a", "b"),
                                                                                new CommonsArrayList <> ("c", "D"))));
    assertFalse (CollectionEqualsHelper.equalsCollection (aArray,
                                                          ArrayHelper.createArray (new CommonsArrayList <> ("a", "b"),
                                                                                new CommonsArrayList <> ("c", "d"),
                                                                                new CommonsArrayList <> ("e", "f"))));
    assertFalse (CollectionEqualsHelper.equalsCollection (aArray,
                                                          ArrayHelper.createArray (new CommonsArrayList <> ("a", "b"),
                                                                                (ICommonsList <String>) null)));
    assertFalse (CollectionEqualsHelper.equalsCollection (aArray, new CommonsArrayList <> ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aArray, new CommonsHashSet <> ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aArray, ArrayHelper.createArray ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aArray, ArrayHelper.createArray ("a", null, "c")));
  }

  @Test
  public void testComplex ()
  {
    final ICommonsMap <ICommonsList <String>, ICommonsSet <String>> aMap = new CommonsHashMap <> ();
    aMap.put (new CommonsArrayList <> ("a", "b", "c"), new CommonsHashSet <> ("a", "b", "c"));
    aMap.put (new CommonsArrayList <> ("a", "b", "d"), new CommonsHashSet <> ("a", "b", "d"));
    assertTrue (CollectionEqualsHelper.equalsCollection (aMap, CollectionHelperExt.createMap (aMap)));

    assertFalse (CollectionEqualsHelper.equalsCollection (aMap, ArrayHelper.createArray ("a", "b", "c", "d")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aMap, new CommonsArrayList <> ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aMap, new CommonsHashSet <> ("a", "b", "c")));
    final ICommonsMap <String, String> aMap1a = new CommonsHashMap <> ();
    aMap1a.put ("a", "b");
    assertFalse (CollectionEqualsHelper.equalsCollection (aMap, aMap1a));
    final ICommonsMap <ICommonsList <String>, String> aMap2 = new CommonsHashMap <> ();
    aMap2.put (new CommonsArrayList <> ("a", "b", "c"), "d");
    aMap2.put (new CommonsArrayList <> ("a", "b", "d"), "e");
    aMap2.put (new CommonsArrayList <> ("a", "b", "e"), null);
    aMap2.put (null, "g");
    assertFalse (CollectionEqualsHelper.equalsCollection (aMap, aMap2));
    assertFalse (CollectionEqualsHelper.equalsCollection (aMap2, aMap));
    final ICommonsMap <String, ICommonsList <String>> aMap3 = new CommonsHashMap <> ();
    aMap3.put ("d", new CommonsArrayList <> ("a", "b", "c"));
    aMap3.put ("e", new CommonsArrayList <> ("a", "b", "d"));
    aMap3.put (null, new CommonsArrayList <> ("a", "b", "e"));
    aMap3.put ("g", null);
    assertFalse (CollectionEqualsHelper.equalsCollection (aMap, aMap3));
    assertFalse (CollectionEqualsHelper.equalsCollection (aMap3, aMap));
  }

  @Test
  public void testIterator ()
  {
    final ICommonsList <String> aCont = new CommonsArrayList <> ("a", "b", "c");
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont.iterator (), aCont.iterator ()));
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont.iterator (),
                                                         CollectionHelper.makeUnmodifiable (aCont).iterator ()));
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont.iterator (),
                                                         Collections.synchronizedList (aCont).iterator ()));
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont.iterator (),
                                                         new CommonsArrayList <> (aCont).iterator ()));
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont.iterator (),
                                                         new CommonsLinkedList <> (aCont).iterator ()));
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont.iterator (), new CommonsVector <> (aCont).iterator ()));
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont.iterator (),
                                                         new NonBlockingStack <> (aCont).iterator ()));
    assertTrue (CollectionEqualsHelper.equalsCollection (new CommonsArrayList <String> ().iterator (),
                                                         new CommonsLinkedList <String> ().iterator ()));
    assertTrue (CollectionEqualsHelper.equalsCollection (new NonBlockingStack <String> ().iterator (),
                                                         new CommonsVector <String> ().iterator ()));
    assertTrue (CollectionEqualsHelper.equalsCollection (new NonBlockingStack <String> ().iterator (),
                                                         new Stack <String> ().iterator ()));

    assertFalse (CollectionEqualsHelper.equalsCollection (aCont.iterator (),
                                                          new CommonsLinkedList <String> ().iterator ()));
    assertFalse (CollectionEqualsHelper.equalsCollection (new CommonsLinkedList <String> ().iterator (),
                                                          aCont.iterator ()));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont.iterator (),
                                                          new CommonsArrayList <String> ().iterator ()));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont.iterator (),
                                                          new CommonsArrayList <> ("a", "b").iterator ()));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont.iterator (),
                                                          new CommonsArrayList <> ("A", "b", "c").iterator ()));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont.iterator (),
                                                          new CommonsArrayList <> ("a", "B", "c").iterator ()));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont.iterator (),
                                                          new CommonsArrayList <> ("a", "b", "C").iterator ()));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont.iterator (),
                                                          new CommonsArrayList <> ("a", "b", "c", "d").iterator ()));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont.iterator (), new CommonsHashSet <> ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont.iterator (), ArrayHelper.createArray ("a", "b", "c")));
  }

  private static final class MockIterable implements ICommonsIterable <String>
  {
    private final ICommonsList <String> m_aList;

    public MockIterable (final String... aValues)
    {
      m_aList = new CommonsArrayList <> (aValues);
    }

    @Nonnull
    public Iterator <String> iterator ()
    {
      return m_aList.iterator ();
    }
  }

  @Test
  public void testIterable ()
  {
    final Iterable <String> aCont = new MockIterable ("a", "b", "c");
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont, aCont));
    assertTrue (CollectionEqualsHelper.equalsCollection (aCont, new MockIterable ("a", "b", "c")));
    assertTrue (CollectionEqualsHelper.equalsCollection (new MockIterable (), new MockIterable ()));

    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new MockIterable ()));
    assertFalse (CollectionEqualsHelper.equalsCollection (new MockIterable (), aCont));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new MockIterable ("a", "b")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new MockIterable ("A", "b", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new MockIterable ("a", "B", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new MockIterable ("a", "b", "C")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new MockIterable ("a", "b", "c", "d")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, new CommonsHashSet <> ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (aCont, ArrayHelper.createArray ("a", "b", "c")));
  }

  @Test
  public void testEnumeration ()
  {
    final ICommonsList <String> aCont = new CommonsArrayList <> ("a", "b", "c");
    assertTrue (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (aCont),
                                                         EnumerationHelper.getEnumeration (aCont)));
    assertTrue (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (aCont),
                                                         EnumerationHelper.getEnumeration (CollectionHelper.makeUnmodifiable (aCont))));
    assertTrue (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (aCont),
                                                         EnumerationHelper.getEnumeration (Collections.synchronizedList (aCont))));
    assertTrue (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (aCont),
                                                         EnumerationHelper.getEnumeration (new CommonsArrayList <> (aCont))));
    assertTrue (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (aCont),
                                                         EnumerationHelper.getEnumeration (new CommonsLinkedList <> (aCont))));
    assertTrue (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (aCont),
                                                         EnumerationHelper.getEnumeration (new CommonsVector <> (aCont))));
    assertTrue (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (aCont),
                                                         EnumerationHelper.getEnumeration (new NonBlockingStack <> (aCont))));
    assertTrue (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (new CommonsArrayList <> ()),
                                                         EnumerationHelper.getEnumeration (new CommonsLinkedList <> ())));
    assertTrue (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (new NonBlockingStack <> ()),
                                                         EnumerationHelper.getEnumeration (new CommonsVector <> ())));
    assertTrue (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (new NonBlockingStack <> ()),
                                                         EnumerationHelper.getEnumeration (new Stack <> ())));

    assertFalse (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (aCont),
                                                          EnumerationHelper.getEnumeration (new CommonsLinkedList <> ())));
    assertFalse (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (new CommonsLinkedList <> ()),
                                                          EnumerationHelper.getEnumeration (aCont)));
    assertFalse (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (aCont),
                                                          EnumerationHelper.getEnumeration (new CommonsArrayList <> ())));
    assertFalse (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (aCont),
                                                          EnumerationHelper.getEnumeration (new CommonsArrayList <> ("a",
                                                                                                                     "b"))));
    assertFalse (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (aCont),
                                                          EnumerationHelper.getEnumeration (new CommonsArrayList <> ("A",
                                                                                                                     "b",
                                                                                                                     "c"))));
    assertFalse (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (aCont),
                                                          EnumerationHelper.getEnumeration (new CommonsArrayList <> ("a",
                                                                                                                     "B",
                                                                                                                     "c"))));
    assertFalse (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (aCont),
                                                          EnumerationHelper.getEnumeration (new CommonsArrayList <> ("a",
                                                                                                                     "b",
                                                                                                                     "C"))));
    assertFalse (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (aCont),
                                                          EnumerationHelper.getEnumeration (new CommonsArrayList <> ("a",
                                                                                                                     "b",
                                                                                                                     "c",
                                                                                                                     "d"))));
    assertFalse (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (aCont),
                                                          EnumerationHelper.getEnumeration (new StringMap ("a", "b")
                                                                                                                    .add ("c",
                                                                                                                          "d"))));
    assertFalse (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (aCont),
                                                          new CommonsHashSet <> ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equalsCollection (EnumerationHelper.getEnumeration (aCont),
                                                          ArrayHelper.createArray ("a", "b", "c")));
  }

  @Test
  public void testEqualsAsCollection ()
  {
    assertTrue (CollectionEqualsHelper.equalsAsList (null, null));
    assertTrue (CollectionEqualsHelper.equalsAsList (new CommonsArrayList <> ("a", "b"),
                                                     ArrayHelper.createArray ("a", "b")));
    assertTrue (CollectionEqualsHelper.equalsAsList (new CommonsArrayList <> ("a", "b"),
                                                     new CommonsArrayList <> ("a", "b").iterator ()));
    assertTrue (CollectionEqualsHelper.equalsAsList (new CommonsArrayList <> ("a", "b"),
                                                     EnumerationHelper.getEnumeration ("a", "b")));
    assertTrue (CollectionEqualsHelper.equalsAsList (new CommonsArrayList <> ("a", "b"),
                                                     CollectionHelperExt.createOrderedSet ("a", "b")));
    assertTrue (CollectionEqualsHelper.equalsAsList (new CommonsArrayList <> ("a", "b"), new MockIterable ("a", "b")));

    assertFalse (CollectionEqualsHelper.equalsAsList (null, "abc"));
    assertFalse (CollectionEqualsHelper.equalsAsList ("abc", null));
    assertFalse (CollectionEqualsHelper.equalsAsList (new CommonsArrayList <> ("a", "b"),
                                                      ArrayHelper.createArray ("a", "B")));
    assertFalse (CollectionEqualsHelper.equalsAsList (new CommonsArrayList <> (null, "b"),
                                                      ArrayHelper.createArray ("a", (String) null)));
  }
}
