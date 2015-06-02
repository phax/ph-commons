/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.equals;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.collections.ArrayHelper;
import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.collections.impl.NonBlockingStack;
import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.url.SMap;

/**
 * Test class for class {@link CollectionEqualsHelper}.
 *
 * @author Philip Helger
 */
public final class CollectionEqualsHelperTest extends AbstractCommonsTestCase
{
  @Test
  public void testBasic ()
  {
    // Same objects - are tested first
    assertTrue (CollectionEqualsHelper.equals (null, null));
    assertTrue (CollectionEqualsHelper.equals ("abc", "abc"));

    // One argument is null
    assertFalse (CollectionEqualsHelper.equals ("abc", null));
    assertFalse (CollectionEqualsHelper.equals (null, "abc"));

    // Non-containers
    try
    {
      CollectionEqualsHelper.equals ("abc", new String ("abc"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      CollectionEqualsHelper.equals (CollectionHelper.newList ("abc"), "abc");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      CollectionEqualsHelper.equals ("abc", CollectionHelper.newList ("abc"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testList ()
  {
    final List <String> aCont = CollectionHelper.newList ("a", "b", "c");
    assertTrue (CollectionEqualsHelper.equals (aCont, aCont));
    assertTrue (CollectionEqualsHelper.equals (aCont, CollectionHelper.makeUnmodifiable (aCont)));
    assertTrue (CollectionEqualsHelper.equals (aCont, Collections.synchronizedList (aCont)));
    assertTrue (CollectionEqualsHelper.equals (aCont, new ArrayList <String> (aCont)));
    assertTrue (CollectionEqualsHelper.equals (aCont, new LinkedList <String> (aCont)));
    assertTrue (CollectionEqualsHelper.equals (aCont, new Vector <String> (aCont)));
    assertTrue (CollectionEqualsHelper.equals (aCont, new NonBlockingStack <String> (aCont)));
    assertTrue (CollectionEqualsHelper.equals (aCont, CollectionHelper.newQueue (aCont)));
    assertTrue (CollectionEqualsHelper.equals (CollectionHelper.newQueue (aCont), aCont));
    assertTrue (CollectionEqualsHelper.equals (CollectionHelper.newQueue (aCont), CollectionHelper.newQueue (aCont)));
    assertTrue (CollectionEqualsHelper.equals (new ArrayList <String> (), new LinkedList <String> ()));
    assertTrue (CollectionEqualsHelper.equals (new NonBlockingStack <String> (), new Vector <String> ()));
    assertTrue (CollectionEqualsHelper.equals (new NonBlockingStack <String> (), new Stack <String> ()));

    assertFalse (CollectionEqualsHelper.equals (aCont, new LinkedList <String> ()));
    assertFalse (CollectionEqualsHelper.equals (new LinkedList <String> (), aCont));
    assertFalse (CollectionEqualsHelper.equals (aCont, new ArrayList <String> ()));
    assertFalse (CollectionEqualsHelper.equals (aCont, CollectionHelper.newList ("a", "b")));
    assertFalse (CollectionEqualsHelper.equals (aCont, CollectionHelper.newList ("A", "b", "c")));
    assertFalse (CollectionEqualsHelper.equals (aCont, CollectionHelper.newList ("a", "B", "c")));
    assertFalse (CollectionEqualsHelper.equals (aCont, CollectionHelper.newList ("a", "b", "C")));
    assertFalse (CollectionEqualsHelper.equals (aCont, CollectionHelper.newList ("a", "b", "c", "d")));
    assertFalse (CollectionEqualsHelper.equals (aCont, CollectionHelper.newSet ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equals (aCont, ArrayHelper.newArray ("a", "b", "c")));
  }

  @Test
  public void testSet ()
  {
    final Set <String> aCont = CollectionHelper.newSet ("a", "b", "c");
    assertTrue (CollectionEqualsHelper.equals (aCont, aCont));
    assertTrue (CollectionEqualsHelper.equals (aCont, CollectionHelper.makeUnmodifiable (aCont)));
    assertTrue (CollectionEqualsHelper.equals (aCont, Collections.synchronizedSet (aCont)));
    assertTrue (CollectionEqualsHelper.equals (aCont, new HashSet <String> (aCont)));
    assertTrue (CollectionEqualsHelper.equals (aCont, new LinkedHashSet <String> (aCont)));
    assertTrue (CollectionEqualsHelper.equals (aCont, new TreeSet <String> (aCont)));
    assertTrue (CollectionEqualsHelper.equals (new HashSet <String> (), new LinkedHashSet <String> ()));
    assertTrue (CollectionEqualsHelper.equals (new TreeSet <String> (), new HashSet <String> ()));

    assertFalse (CollectionEqualsHelper.equals (aCont, new HashSet <String> ()));
    assertFalse (CollectionEqualsHelper.equals (new HashSet <String> (), aCont));
    assertFalse (CollectionEqualsHelper.equals (aCont, new TreeSet <String> ()));
    assertFalse (CollectionEqualsHelper.equals (aCont, CollectionHelper.newSet ("a", "b")));
    assertFalse (CollectionEqualsHelper.equals (aCont, CollectionHelper.newSet ("A", "b", "c")));
    assertFalse (CollectionEqualsHelper.equals (aCont, CollectionHelper.newSet ("a", "B", "c")));
    assertFalse (CollectionEqualsHelper.equals (aCont, CollectionHelper.newSet ("a", "b", "C")));
    assertFalse (CollectionEqualsHelper.equals (aCont, CollectionHelper.newSet ("a", "b", "c", "d")));
    assertFalse (CollectionEqualsHelper.equals (aCont, CollectionHelper.newList ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equals (aCont, ArrayHelper.newArray ("a", "b", "c")));
  }

  @Test
  public void testMap ()
  {
    final SMap aMap = new SMap ("a", "b").add ("c", "d");
    assertTrue (CollectionEqualsHelper.equals (aMap, aMap));
    assertTrue (CollectionEqualsHelper.equals (aMap, CollectionHelper.makeUnmodifiable (aMap)));
    assertTrue (CollectionEqualsHelper.equals (aMap, Collections.synchronizedMap (aMap)));
    assertTrue (CollectionEqualsHelper.equals (aMap, new SMap ("a", "b").add ("c", "d")));
    assertTrue (CollectionEqualsHelper.equals (new HashMap <Integer, Integer> (), new HashMap <Double, Float> ()));

    assertFalse (CollectionEqualsHelper.equals (aMap, new HashMap <Integer, Integer> ()));
    assertFalse (CollectionEqualsHelper.equals (new HashMap <Integer, Integer> (), aMap));
    assertFalse (CollectionEqualsHelper.equals (aMap, new SMap ("a", "b")));
    assertFalse (CollectionEqualsHelper.equals (aMap, new SMap ("A", "b").add ("c", "d")));
    assertFalse (CollectionEqualsHelper.equals (aMap, new SMap ("a", "B").add ("c", "d")));
    assertFalse (CollectionEqualsHelper.equals (aMap, new SMap ("a", "b").add ("C", "d")));
    assertFalse (CollectionEqualsHelper.equals (aMap, new SMap ("a", "b").add ("c", "D")));
    assertFalse (CollectionEqualsHelper.equals (aMap, new SMap ("a", "b").add ("c", "d").add ("e", "f")));
    assertFalse (CollectionEqualsHelper.equals (aMap, CollectionHelper.newList ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equals (aMap, CollectionHelper.newSet ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equals (aMap, ArrayHelper.newArray ("a", "b", "c")));
  }

  @Test
  public void testArray ()
  {
    final String [] aArray = ArrayHelper.newArray ("a", "b", "c");
    assertTrue (CollectionEqualsHelper.equals (aArray, aArray));
    assertTrue (CollectionEqualsHelper.equals (aArray, ArrayHelper.newArray ("a", "b", "c")));
    assertTrue (CollectionEqualsHelper.equals (new String [0], new String [] {}));

    assertFalse (CollectionEqualsHelper.equals (aArray, new String [0]));
    assertFalse (CollectionEqualsHelper.equals (new String [0], aArray));
    assertFalse (CollectionEqualsHelper.equals (aArray, ArrayHelper.newArray ("a", "b")));
    assertFalse (CollectionEqualsHelper.equals (aArray, ArrayHelper.newArray ("A", "b", "c")));
    assertFalse (CollectionEqualsHelper.equals (aArray, ArrayHelper.newArray ("a", "B", "c")));
    assertFalse (CollectionEqualsHelper.equals (aArray, ArrayHelper.newArray ("a", "b", "C")));
    assertFalse (CollectionEqualsHelper.equals (aArray, ArrayHelper.newArray ("a", "b", "c", "d")));
    assertFalse (CollectionEqualsHelper.equals (aArray, CollectionHelper.newList ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equals (aArray, CollectionHelper.newSet ("a", "b", "c")));
  }

  @SuppressWarnings ("unchecked")
  @Test
  public void testArrayComplex ()
  {
    final List <String> [] aArray = ArrayHelper.newArray (CollectionHelper.newList ("a", "b"),
                                                          CollectionHelper.newList ("c", "d"));
    assertTrue (CollectionEqualsHelper.equals (aArray, aArray));
    assertTrue (CollectionEqualsHelper.equals (aArray, ArrayHelper.newArray (CollectionHelper.newList ("a", "b"),
                                                                            CollectionHelper.newList ("c", "d"))));
    assertTrue (CollectionEqualsHelper.equals (new List <?> [0], new List <?> [] {}));

    assertFalse (CollectionEqualsHelper.equals (aArray, new List <?> [0]));
    assertFalse (CollectionEqualsHelper.equals (new List <?> [0], aArray));
    assertFalse (CollectionEqualsHelper.equals (aArray, ArrayHelper.newArray (CollectionHelper.newList ("a", "b"))));
    assertFalse (CollectionEqualsHelper.equals (aArray, ArrayHelper.newArray (CollectionHelper.newList ("A", "b"),
                                                                             CollectionHelper.newList ("c", "d"))));
    assertFalse (CollectionEqualsHelper.equals (aArray, ArrayHelper.newArray (CollectionHelper.newList ("a", "b"),
                                                                             CollectionHelper.newList ("c", "D"))));
    assertFalse (CollectionEqualsHelper.equals (aArray, ArrayHelper.newArray (CollectionHelper.newList ("a", "b"),
                                                                             CollectionHelper.newList ("c", "d"),
                                                                             CollectionHelper.newList ("e", "f"))));
    assertFalse (CollectionEqualsHelper.equals (aArray, ArrayHelper.newArray (CollectionHelper.newList ("a", "b"),
                                                                             (List <String>) null)));
    assertFalse (CollectionEqualsHelper.equals (aArray, CollectionHelper.newList ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equals (aArray, CollectionHelper.newSet ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equals (aArray, ArrayHelper.newArray ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equals (aArray, ArrayHelper.newArray ("a", null, "c")));
  }

  @Test
  public void testComplex ()
  {
    final Map <List <String>, Set <String>> aMap = new HashMap <List <String>, Set <String>> ();
    aMap.put (CollectionHelper.newList ("a", "b", "c"), CollectionHelper.newSet ("a", "b", "c"));
    aMap.put (CollectionHelper.newList ("a", "b", "d"), CollectionHelper.newSet ("a", "b", "d"));
    assertTrue (CollectionEqualsHelper.equals (aMap, CollectionHelper.newMap (aMap)));

    assertFalse (CollectionEqualsHelper.equals (aMap, ArrayHelper.newArray ("a", "b", "c", "d")));
    assertFalse (CollectionEqualsHelper.equals (aMap, CollectionHelper.newList ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equals (aMap, CollectionHelper.newSet ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equals (aMap, new SMap ().add ("a", "b")));
    final Map <List <String>, String> aMap2 = new HashMap <List <String>, String> ();
    aMap2.put (CollectionHelper.newList ("a", "b", "c"), "d");
    aMap2.put (CollectionHelper.newList ("a", "b", "d"), "e");
    aMap2.put (CollectionHelper.newList ("a", "b", "e"), null);
    aMap2.put (null, "g");
    assertFalse (CollectionEqualsHelper.equals (aMap, aMap2));
    assertFalse (CollectionEqualsHelper.equals (aMap2, aMap));
    final Map <String, List <String>> aMap3 = new HashMap <String, List <String>> ();
    aMap3.put ("d", CollectionHelper.newList ("a", "b", "c"));
    aMap3.put ("e", CollectionHelper.newList ("a", "b", "d"));
    aMap3.put (null, CollectionHelper.newList ("a", "b", "e"));
    aMap3.put ("g", null);
    assertFalse (CollectionEqualsHelper.equals (aMap, aMap3));
    assertFalse (CollectionEqualsHelper.equals (aMap3, aMap));
  }

  @Test
  public void testIterator ()
  {
    final List <String> aCont = CollectionHelper.newList ("a", "b", "c");
    assertTrue (CollectionEqualsHelper.equals (aCont.iterator (), aCont.iterator ()));
    assertTrue (CollectionEqualsHelper.equals (aCont.iterator (), CollectionHelper.makeUnmodifiable (aCont).iterator ()));
    assertTrue (CollectionEqualsHelper.equals (aCont.iterator (), Collections.synchronizedList (aCont).iterator ()));
    assertTrue (CollectionEqualsHelper.equals (aCont.iterator (), new ArrayList <String> (aCont).iterator ()));
    assertTrue (CollectionEqualsHelper.equals (aCont.iterator (), new LinkedList <String> (aCont).iterator ()));
    assertTrue (CollectionEqualsHelper.equals (aCont.iterator (), new Vector <String> (aCont).iterator ()));
    assertTrue (CollectionEqualsHelper.equals (aCont.iterator (), new NonBlockingStack <String> (aCont).iterator ()));
    assertTrue (CollectionEqualsHelper.equals (aCont.iterator (), CollectionHelper.newQueue (aCont).iterator ()));
    assertTrue (CollectionEqualsHelper.equals (CollectionHelper.newQueue (aCont), aCont));
    assertTrue (CollectionEqualsHelper.equals (CollectionHelper.newQueue (aCont), CollectionHelper.newQueue (aCont)));
    assertTrue (CollectionEqualsHelper.equals (new ArrayList <String> ().iterator (),
                                              new LinkedList <String> ().iterator ()));
    assertTrue (CollectionEqualsHelper.equals (new NonBlockingStack <String> ().iterator (),
                                              new Vector <String> ().iterator ()));
    assertTrue (CollectionEqualsHelper.equals (new NonBlockingStack <String> ().iterator (),
                                              new Stack <String> ().iterator ()));

    assertFalse (CollectionEqualsHelper.equals (aCont.iterator (), new LinkedList <String> ().iterator ()));
    assertFalse (CollectionEqualsHelper.equals (new LinkedList <String> ().iterator (), aCont.iterator ()));
    assertFalse (CollectionEqualsHelper.equals (aCont.iterator (), new ArrayList <String> ().iterator ()));
    assertFalse (CollectionEqualsHelper.equals (aCont.iterator (), CollectionHelper.newList ("a", "b").iterator ()));
    assertFalse (CollectionEqualsHelper.equals (aCont.iterator (), CollectionHelper.newList ("A", "b", "c").iterator ()));
    assertFalse (CollectionEqualsHelper.equals (aCont.iterator (), CollectionHelper.newList ("a", "B", "c").iterator ()));
    assertFalse (CollectionEqualsHelper.equals (aCont.iterator (), CollectionHelper.newList ("a", "b", "C").iterator ()));
    assertFalse (CollectionEqualsHelper.equals (aCont.iterator (), CollectionHelper.newList ("a", "b", "c", "d")
                                                                                  .iterator ()));
    assertFalse (CollectionEqualsHelper.equals (aCont.iterator (), CollectionHelper.newSet ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equals (aCont.iterator (), ArrayHelper.newArray ("a", "b", "c")));
  }

  private static final class MockIterable implements Iterable <String>
  {
    private final List <String> m_aList;

    public MockIterable (final String... aValues)
    {
      m_aList = CollectionHelper.newList (aValues);
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
    assertTrue (CollectionEqualsHelper.equals (aCont, aCont));
    assertTrue (CollectionEqualsHelper.equals (aCont, new MockIterable ("a", "b", "c")));
    assertTrue (CollectionEqualsHelper.equals (new MockIterable (), new MockIterable ()));

    assertFalse (CollectionEqualsHelper.equals (aCont, new MockIterable ()));
    assertFalse (CollectionEqualsHelper.equals (new MockIterable (), aCont));
    assertFalse (CollectionEqualsHelper.equals (aCont, new MockIterable ("a", "b")));
    assertFalse (CollectionEqualsHelper.equals (aCont, new MockIterable ("A", "b", "c")));
    assertFalse (CollectionEqualsHelper.equals (aCont, new MockIterable ("a", "B", "c")));
    assertFalse (CollectionEqualsHelper.equals (aCont, new MockIterable ("a", "b", "C")));
    assertFalse (CollectionEqualsHelper.equals (aCont, new MockIterable ("a", "b", "c", "d")));
    assertFalse (CollectionEqualsHelper.equals (aCont, CollectionHelper.newSet ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equals (aCont, ArrayHelper.newArray ("a", "b", "c")));
  }

  @Test
  public void testEnumeration ()
  {
    final List <String> aCont = CollectionHelper.newList ("a", "b", "c");
    assertTrue (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (aCont),
                                              CollectionHelper.getEnumeration (aCont)));
    assertTrue (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (aCont),
                                              CollectionHelper.getEnumeration (CollectionHelper.makeUnmodifiable (aCont))));
    assertTrue (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (aCont),
                                              CollectionHelper.getEnumeration (Collections.synchronizedList (aCont))));
    assertTrue (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (aCont),
                                              CollectionHelper.getEnumeration (new ArrayList <String> (aCont))));
    assertTrue (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (aCont),
                                              CollectionHelper.getEnumeration (new LinkedList <String> (aCont))));
    assertTrue (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (aCont),
                                              CollectionHelper.getEnumeration (new Vector <String> (aCont))));
    assertTrue (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (aCont),
                                              CollectionHelper.getEnumeration (new NonBlockingStack <String> (aCont))));
    assertTrue (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (aCont),
                                              CollectionHelper.getEnumeration (CollectionHelper.newQueue (aCont))));
    assertTrue (CollectionEqualsHelper.equals (CollectionHelper.newQueue (aCont), aCont));
    assertTrue (CollectionEqualsHelper.equals (CollectionHelper.newQueue (aCont), CollectionHelper.newQueue (aCont)));
    assertTrue (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (new ArrayList <String> ()),
                                              CollectionHelper.getEnumeration (new LinkedList <String> ())));
    assertTrue (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (new NonBlockingStack <String> ()),
                                              CollectionHelper.getEnumeration (new Vector <String> ())));
    assertTrue (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (new NonBlockingStack <String> ()),
                                              CollectionHelper.getEnumeration (new Stack <String> ())));

    assertFalse (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (aCont),
                                               CollectionHelper.getEnumeration (new LinkedList <String> ())));
    assertFalse (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (new LinkedList <String> ()),
                                               CollectionHelper.getEnumeration (aCont)));
    assertFalse (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (aCont),
                                               CollectionHelper.getEnumeration (new ArrayList <String> ())));
    assertFalse (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (aCont),
                                               CollectionHelper.getEnumeration (CollectionHelper.newList ("a", "b"))));
    assertFalse (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (aCont),
                                               CollectionHelper.getEnumeration (CollectionHelper.newList ("A", "b", "c"))));
    assertFalse (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (aCont),
                                               CollectionHelper.getEnumeration (CollectionHelper.newList ("a", "B", "c"))));
    assertFalse (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (aCont),
                                               CollectionHelper.getEnumeration (CollectionHelper.newList ("a", "b", "C"))));
    assertFalse (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (aCont),
                                               CollectionHelper.getEnumeration (CollectionHelper.newList ("a",
                                                                                                          "b",
                                                                                                          "c",
                                                                                                          "d"))));
    assertFalse (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (aCont),
                                               CollectionHelper.getEnumeration (new SMap ("a", "b").add ("c", "d"))));
    assertFalse (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (aCont),
                                               CollectionHelper.newSet ("a", "b", "c")));
    assertFalse (CollectionEqualsHelper.equals (CollectionHelper.getEnumeration (aCont),
                                               ArrayHelper.newArray ("a", "b", "c")));
  }

  @Test
  public void testEqualsAsCollection ()
  {
    assertTrue (CollectionEqualsHelper.equalsAsList (null, null));
    assertTrue (CollectionEqualsHelper.equalsAsList (CollectionHelper.newList ("a", "b"),
                                                    ArrayHelper.newArray ("a", "b")));
    assertTrue (CollectionEqualsHelper.equalsAsList (CollectionHelper.newList ("a", "b"),
                                                    CollectionHelper.newList ("a", "b").iterator ()));
    assertTrue (CollectionEqualsHelper.equalsAsList (CollectionHelper.newList ("a", "b"),
                                                    CollectionHelper.getEnumeration ("a", "b")));
    assertTrue (CollectionEqualsHelper.equalsAsList (CollectionHelper.newList ("a", "b"),
                                                    CollectionHelper.newOrderedSet ("a", "b")));
    assertTrue (CollectionEqualsHelper.equalsAsList (CollectionHelper.newList ("a", "b"), new MockIterable ("a", "b")));

    assertFalse (CollectionEqualsHelper.equalsAsList (null, "abc"));
    assertFalse (CollectionEqualsHelper.equalsAsList ("abc", null));
    assertFalse (CollectionEqualsHelper.equalsAsList (CollectionHelper.newList ("a", "b"),
                                                     ArrayHelper.newArray ("a", "B")));
    assertFalse (CollectionEqualsHelper.equalsAsList (CollectionHelper.newList (null, "b"),
                                                     ArrayHelper.newArray ("a", (String) null)));
  }
}
