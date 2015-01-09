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
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.collections.NonBlockingStack;
import com.helger.commons.mock.AbstractPHTestCase;
import com.helger.commons.url.SMap;

/**
 * Test class for class {@link ContainerEqualsUtils}.
 * 
 * @author Philip Helger
 */
public final class ContainerEqualsUtilsTest extends AbstractPHTestCase
{
  @Test
  public void testBasic ()
  {
    // Same objects - are tested first
    assertTrue (ContainerEqualsUtils.equals (null, null));
    assertTrue (ContainerEqualsUtils.equals ("abc", "abc"));

    // One argument is null
    assertFalse (ContainerEqualsUtils.equals ("abc", null));
    assertFalse (ContainerEqualsUtils.equals (null, "abc"));

    // Non-containers
    try
    {
      ContainerEqualsUtils.equals ("abc", new String ("abc"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      ContainerEqualsUtils.equals (ContainerHelper.newList ("abc"), "abc");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      ContainerEqualsUtils.equals ("abc", ContainerHelper.newList ("abc"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testList ()
  {
    final List <String> aCont = ContainerHelper.newList ("a", "b", "c");
    assertTrue (ContainerEqualsUtils.equals (aCont, aCont));
    assertTrue (ContainerEqualsUtils.equals (aCont, ContainerHelper.makeUnmodifiable (aCont)));
    assertTrue (ContainerEqualsUtils.equals (aCont, Collections.synchronizedList (aCont)));
    assertTrue (ContainerEqualsUtils.equals (aCont, new ArrayList <String> (aCont)));
    assertTrue (ContainerEqualsUtils.equals (aCont, new LinkedList <String> (aCont)));
    assertTrue (ContainerEqualsUtils.equals (aCont, new Vector <String> (aCont)));
    assertTrue (ContainerEqualsUtils.equals (aCont, new NonBlockingStack <String> (aCont)));
    assertTrue (ContainerEqualsUtils.equals (aCont, ContainerHelper.newQueue (aCont)));
    assertTrue (ContainerEqualsUtils.equals (ContainerHelper.newQueue (aCont), aCont));
    assertTrue (ContainerEqualsUtils.equals (ContainerHelper.newQueue (aCont), ContainerHelper.newQueue (aCont)));
    assertTrue (ContainerEqualsUtils.equals (new ArrayList <String> (), new LinkedList <String> ()));
    assertTrue (ContainerEqualsUtils.equals (new NonBlockingStack <String> (), new Vector <String> ()));
    assertTrue (ContainerEqualsUtils.equals (new NonBlockingStack <String> (), new Stack <String> ()));

    assertFalse (ContainerEqualsUtils.equals (aCont, new LinkedList <String> ()));
    assertFalse (ContainerEqualsUtils.equals (new LinkedList <String> (), aCont));
    assertFalse (ContainerEqualsUtils.equals (aCont, new ArrayList <String> ()));
    assertFalse (ContainerEqualsUtils.equals (aCont, ContainerHelper.newList ("a", "b")));
    assertFalse (ContainerEqualsUtils.equals (aCont, ContainerHelper.newList ("A", "b", "c")));
    assertFalse (ContainerEqualsUtils.equals (aCont, ContainerHelper.newList ("a", "B", "c")));
    assertFalse (ContainerEqualsUtils.equals (aCont, ContainerHelper.newList ("a", "b", "C")));
    assertFalse (ContainerEqualsUtils.equals (aCont, ContainerHelper.newList ("a", "b", "c", "d")));
    assertFalse (ContainerEqualsUtils.equals (aCont, ContainerHelper.newSet ("a", "b", "c")));
    assertFalse (ContainerEqualsUtils.equals (aCont, ArrayHelper.newArray ("a", "b", "c")));
  }

  @Test
  public void testSet ()
  {
    final Set <String> aCont = ContainerHelper.newSet ("a", "b", "c");
    assertTrue (ContainerEqualsUtils.equals (aCont, aCont));
    assertTrue (ContainerEqualsUtils.equals (aCont, ContainerHelper.makeUnmodifiable (aCont)));
    assertTrue (ContainerEqualsUtils.equals (aCont, Collections.synchronizedSet (aCont)));
    assertTrue (ContainerEqualsUtils.equals (aCont, new HashSet <String> (aCont)));
    assertTrue (ContainerEqualsUtils.equals (aCont, new LinkedHashSet <String> (aCont)));
    assertTrue (ContainerEqualsUtils.equals (aCont, new TreeSet <String> (aCont)));
    assertTrue (ContainerEqualsUtils.equals (new HashSet <String> (), new LinkedHashSet <String> ()));
    assertTrue (ContainerEqualsUtils.equals (new TreeSet <String> (), new HashSet <String> ()));

    assertFalse (ContainerEqualsUtils.equals (aCont, new HashSet <String> ()));
    assertFalse (ContainerEqualsUtils.equals (new HashSet <String> (), aCont));
    assertFalse (ContainerEqualsUtils.equals (aCont, new TreeSet <String> ()));
    assertFalse (ContainerEqualsUtils.equals (aCont, ContainerHelper.newSet ("a", "b")));
    assertFalse (ContainerEqualsUtils.equals (aCont, ContainerHelper.newSet ("A", "b", "c")));
    assertFalse (ContainerEqualsUtils.equals (aCont, ContainerHelper.newSet ("a", "B", "c")));
    assertFalse (ContainerEqualsUtils.equals (aCont, ContainerHelper.newSet ("a", "b", "C")));
    assertFalse (ContainerEqualsUtils.equals (aCont, ContainerHelper.newSet ("a", "b", "c", "d")));
    assertFalse (ContainerEqualsUtils.equals (aCont, ContainerHelper.newList ("a", "b", "c")));
    assertFalse (ContainerEqualsUtils.equals (aCont, ArrayHelper.newArray ("a", "b", "c")));
  }

  @Test
  public void testMap ()
  {
    final SMap aMap = new SMap ("a", "b").add ("c", "d");
    assertTrue (ContainerEqualsUtils.equals (aMap, aMap));
    assertTrue (ContainerEqualsUtils.equals (aMap, ContainerHelper.makeUnmodifiable (aMap)));
    assertTrue (ContainerEqualsUtils.equals (aMap, Collections.synchronizedMap (aMap)));
    assertTrue (ContainerEqualsUtils.equals (aMap, new SMap ("a", "b").add ("c", "d")));
    assertTrue (ContainerEqualsUtils.equals (new HashMap <Integer, Integer> (), new HashMap <Double, Float> ()));

    assertFalse (ContainerEqualsUtils.equals (aMap, new HashMap <Integer, Integer> ()));
    assertFalse (ContainerEqualsUtils.equals (new HashMap <Integer, Integer> (), aMap));
    assertFalse (ContainerEqualsUtils.equals (aMap, new SMap ("a", "b")));
    assertFalse (ContainerEqualsUtils.equals (aMap, new SMap ("A", "b").add ("c", "d")));
    assertFalse (ContainerEqualsUtils.equals (aMap, new SMap ("a", "B").add ("c", "d")));
    assertFalse (ContainerEqualsUtils.equals (aMap, new SMap ("a", "b").add ("C", "d")));
    assertFalse (ContainerEqualsUtils.equals (aMap, new SMap ("a", "b").add ("c", "D")));
    assertFalse (ContainerEqualsUtils.equals (aMap, new SMap ("a", "b").add ("c", "d").add ("e", "f")));
    assertFalse (ContainerEqualsUtils.equals (aMap, ContainerHelper.newList ("a", "b", "c")));
    assertFalse (ContainerEqualsUtils.equals (aMap, ContainerHelper.newSet ("a", "b", "c")));
    assertFalse (ContainerEqualsUtils.equals (aMap, ArrayHelper.newArray ("a", "b", "c")));
  }

  @Test
  public void testArray ()
  {
    final String [] aArray = ArrayHelper.newArray ("a", "b", "c");
    assertTrue (ContainerEqualsUtils.equals (aArray, aArray));
    assertTrue (ContainerEqualsUtils.equals (aArray, ArrayHelper.newArray ("a", "b", "c")));
    assertTrue (ContainerEqualsUtils.equals (new String [0], new String [] {}));

    assertFalse (ContainerEqualsUtils.equals (aArray, new String [0]));
    assertFalse (ContainerEqualsUtils.equals (new String [0], aArray));
    assertFalse (ContainerEqualsUtils.equals (aArray, ArrayHelper.newArray ("a", "b")));
    assertFalse (ContainerEqualsUtils.equals (aArray, ArrayHelper.newArray ("A", "b", "c")));
    assertFalse (ContainerEqualsUtils.equals (aArray, ArrayHelper.newArray ("a", "B", "c")));
    assertFalse (ContainerEqualsUtils.equals (aArray, ArrayHelper.newArray ("a", "b", "C")));
    assertFalse (ContainerEqualsUtils.equals (aArray, ArrayHelper.newArray ("a", "b", "c", "d")));
    assertFalse (ContainerEqualsUtils.equals (aArray, ContainerHelper.newList ("a", "b", "c")));
    assertFalse (ContainerEqualsUtils.equals (aArray, ContainerHelper.newSet ("a", "b", "c")));
  }

  @SuppressWarnings ("unchecked")
  @Test
  public void testArrayComplex ()
  {
    final List <String> [] aArray = ArrayHelper.newArray (ContainerHelper.newList ("a", "b"),
                                                          ContainerHelper.newList ("c", "d"));
    assertTrue (ContainerEqualsUtils.equals (aArray, aArray));
    assertTrue (ContainerEqualsUtils.equals (aArray,
                                             ArrayHelper.newArray (ContainerHelper.newList ("a", "b"),
                                                                   ContainerHelper.newList ("c", "d"))));
    assertTrue (ContainerEqualsUtils.equals (new List <?> [0], new List <?> [] {}));

    assertFalse (ContainerEqualsUtils.equals (aArray, new List <?> [0]));
    assertFalse (ContainerEqualsUtils.equals (new List <?> [0], aArray));
    assertFalse (ContainerEqualsUtils.equals (aArray, ArrayHelper.newArray (ContainerHelper.newList ("a", "b"))));
    assertFalse (ContainerEqualsUtils.equals (aArray,
                                              ArrayHelper.newArray (ContainerHelper.newList ("A", "b"),
                                                                    ContainerHelper.newList ("c", "d"))));
    assertFalse (ContainerEqualsUtils.equals (aArray,
                                              ArrayHelper.newArray (ContainerHelper.newList ("a", "b"),
                                                                    ContainerHelper.newList ("c", "D"))));
    assertFalse (ContainerEqualsUtils.equals (aArray,
                                              ArrayHelper.newArray (ContainerHelper.newList ("a", "b"),
                                                                    ContainerHelper.newList ("c", "d"),
                                                                    ContainerHelper.newList ("e", "f"))));
    assertFalse (ContainerEqualsUtils.equals (aArray, ArrayHelper.newArray (ContainerHelper.newList ("a", "b"),
                                                                            (List <String>) null)));
    assertFalse (ContainerEqualsUtils.equals (aArray, ContainerHelper.newList ("a", "b", "c")));
    assertFalse (ContainerEqualsUtils.equals (aArray, ContainerHelper.newSet ("a", "b", "c")));
    assertFalse (ContainerEqualsUtils.equals (aArray, ArrayHelper.newArray ("a", "b", "c")));
    assertFalse (ContainerEqualsUtils.equals (aArray, ArrayHelper.newArray ("a", null, "c")));
  }

  @Test
  public void testComplex ()
  {
    final Map <List <String>, Set <String>> aMap = new HashMap <List <String>, Set <String>> ();
    aMap.put (ContainerHelper.newList ("a", "b", "c"), ContainerHelper.newSet ("a", "b", "c"));
    aMap.put (ContainerHelper.newList ("a", "b", "d"), ContainerHelper.newSet ("a", "b", "d"));
    assertTrue (ContainerEqualsUtils.equals (aMap, ContainerHelper.newMap (aMap)));

    assertFalse (ContainerEqualsUtils.equals (aMap, ArrayHelper.newArray ("a", "b", "c", "d")));
    assertFalse (ContainerEqualsUtils.equals (aMap, ContainerHelper.newList ("a", "b", "c")));
    assertFalse (ContainerEqualsUtils.equals (aMap, ContainerHelper.newSet ("a", "b", "c")));
    assertFalse (ContainerEqualsUtils.equals (aMap, new SMap ().add ("a", "b")));
    final Map <List <String>, String> aMap2 = new HashMap <List <String>, String> ();
    aMap2.put (ContainerHelper.newList ("a", "b", "c"), "d");
    aMap2.put (ContainerHelper.newList ("a", "b", "d"), "e");
    aMap2.put (ContainerHelper.newList ("a", "b", "e"), null);
    aMap2.put (null, "g");
    assertFalse (ContainerEqualsUtils.equals (aMap, aMap2));
    assertFalse (ContainerEqualsUtils.equals (aMap2, aMap));
    final Map <String, List <String>> aMap3 = new HashMap <String, List <String>> ();
    aMap3.put ("d", ContainerHelper.newList ("a", "b", "c"));
    aMap3.put ("e", ContainerHelper.newList ("a", "b", "d"));
    aMap3.put (null, ContainerHelper.newList ("a", "b", "e"));
    aMap3.put ("g", null);
    assertFalse (ContainerEqualsUtils.equals (aMap, aMap3));
    assertFalse (ContainerEqualsUtils.equals (aMap3, aMap));
  }

  @Test
  public void testIterator ()
  {
    final List <String> aCont = ContainerHelper.newList ("a", "b", "c");
    assertTrue (ContainerEqualsUtils.equals (aCont.iterator (), aCont.iterator ()));
    assertTrue (ContainerEqualsUtils.equals (aCont.iterator (), ContainerHelper.makeUnmodifiable (aCont).iterator ()));
    assertTrue (ContainerEqualsUtils.equals (aCont.iterator (), Collections.synchronizedList (aCont).iterator ()));
    assertTrue (ContainerEqualsUtils.equals (aCont.iterator (), new ArrayList <String> (aCont).iterator ()));
    assertTrue (ContainerEqualsUtils.equals (aCont.iterator (), new LinkedList <String> (aCont).iterator ()));
    assertTrue (ContainerEqualsUtils.equals (aCont.iterator (), new Vector <String> (aCont).iterator ()));
    assertTrue (ContainerEqualsUtils.equals (aCont.iterator (), new NonBlockingStack <String> (aCont).iterator ()));
    assertTrue (ContainerEqualsUtils.equals (aCont.iterator (), ContainerHelper.newQueue (aCont).iterator ()));
    assertTrue (ContainerEqualsUtils.equals (ContainerHelper.newQueue (aCont), aCont));
    assertTrue (ContainerEqualsUtils.equals (ContainerHelper.newQueue (aCont), ContainerHelper.newQueue (aCont)));
    assertTrue (ContainerEqualsUtils.equals (new ArrayList <String> ().iterator (),
                                             new LinkedList <String> ().iterator ()));
    assertTrue (ContainerEqualsUtils.equals (new NonBlockingStack <String> ().iterator (),
                                             new Vector <String> ().iterator ()));
    assertTrue (ContainerEqualsUtils.equals (new NonBlockingStack <String> ().iterator (),
                                             new Stack <String> ().iterator ()));

    assertFalse (ContainerEqualsUtils.equals (aCont.iterator (), new LinkedList <String> ().iterator ()));
    assertFalse (ContainerEqualsUtils.equals (new LinkedList <String> ().iterator (), aCont.iterator ()));
    assertFalse (ContainerEqualsUtils.equals (aCont.iterator (), new ArrayList <String> ().iterator ()));
    assertFalse (ContainerEqualsUtils.equals (aCont.iterator (), ContainerHelper.newList ("a", "b").iterator ()));
    assertFalse (ContainerEqualsUtils.equals (aCont.iterator (), ContainerHelper.newList ("A", "b", "c").iterator ()));
    assertFalse (ContainerEqualsUtils.equals (aCont.iterator (), ContainerHelper.newList ("a", "B", "c").iterator ()));
    assertFalse (ContainerEqualsUtils.equals (aCont.iterator (), ContainerHelper.newList ("a", "b", "C").iterator ()));
    assertFalse (ContainerEqualsUtils.equals (aCont.iterator (), ContainerHelper.newList ("a", "b", "c", "d")
                                                                                .iterator ()));
    assertFalse (ContainerEqualsUtils.equals (aCont.iterator (), ContainerHelper.newSet ("a", "b", "c")));
    assertFalse (ContainerEqualsUtils.equals (aCont.iterator (), ArrayHelper.newArray ("a", "b", "c")));
  }

  private static final class MockIterable implements Iterable <String>
  {
    private final List <String> m_aList;

    public MockIterable (final String... aValues)
    {
      m_aList = ContainerHelper.newList (aValues);
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
    assertTrue (ContainerEqualsUtils.equals (aCont, aCont));
    assertTrue (ContainerEqualsUtils.equals (aCont, new MockIterable ("a", "b", "c")));
    assertTrue (ContainerEqualsUtils.equals (new MockIterable (), new MockIterable ()));

    assertFalse (ContainerEqualsUtils.equals (aCont, new MockIterable ()));
    assertFalse (ContainerEqualsUtils.equals (new MockIterable (), aCont));
    assertFalse (ContainerEqualsUtils.equals (aCont, new MockIterable ("a", "b")));
    assertFalse (ContainerEqualsUtils.equals (aCont, new MockIterable ("A", "b", "c")));
    assertFalse (ContainerEqualsUtils.equals (aCont, new MockIterable ("a", "B", "c")));
    assertFalse (ContainerEqualsUtils.equals (aCont, new MockIterable ("a", "b", "C")));
    assertFalse (ContainerEqualsUtils.equals (aCont, new MockIterable ("a", "b", "c", "d")));
    assertFalse (ContainerEqualsUtils.equals (aCont, ContainerHelper.newSet ("a", "b", "c")));
    assertFalse (ContainerEqualsUtils.equals (aCont, ArrayHelper.newArray ("a", "b", "c")));
  }

  @Test
  public void testEnumeration ()
  {
    final List <String> aCont = ContainerHelper.newList ("a", "b", "c");
    assertTrue (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (aCont),
                                             ContainerHelper.getEnumeration (aCont)));
    assertTrue (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (aCont),
                                             ContainerHelper.getEnumeration (ContainerHelper.makeUnmodifiable (aCont))));
    assertTrue (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (aCont),
                                             ContainerHelper.getEnumeration (Collections.synchronizedList (aCont))));
    assertTrue (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (aCont),
                                             ContainerHelper.getEnumeration (new ArrayList <String> (aCont))));
    assertTrue (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (aCont),
                                             ContainerHelper.getEnumeration (new LinkedList <String> (aCont))));
    assertTrue (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (aCont),
                                             ContainerHelper.getEnumeration (new Vector <String> (aCont))));
    assertTrue (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (aCont),
                                             ContainerHelper.getEnumeration (new NonBlockingStack <String> (aCont))));
    assertTrue (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (aCont),
                                             ContainerHelper.getEnumeration (ContainerHelper.newQueue (aCont))));
    assertTrue (ContainerEqualsUtils.equals (ContainerHelper.newQueue (aCont), aCont));
    assertTrue (ContainerEqualsUtils.equals (ContainerHelper.newQueue (aCont), ContainerHelper.newQueue (aCont)));
    assertTrue (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (new ArrayList <String> ()),
                                             ContainerHelper.getEnumeration (new LinkedList <String> ())));
    assertTrue (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (new NonBlockingStack <String> ()),
                                             ContainerHelper.getEnumeration (new Vector <String> ())));
    assertTrue (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (new NonBlockingStack <String> ()),
                                             ContainerHelper.getEnumeration (new Stack <String> ())));

    assertFalse (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (aCont),
                                              ContainerHelper.getEnumeration (new LinkedList <String> ())));
    assertFalse (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (new LinkedList <String> ()),
                                              ContainerHelper.getEnumeration (aCont)));
    assertFalse (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (aCont),
                                              ContainerHelper.getEnumeration (new ArrayList <String> ())));
    assertFalse (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (aCont),
                                              ContainerHelper.getEnumeration (ContainerHelper.newList ("a", "b"))));
    assertFalse (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (aCont),
                                              ContainerHelper.getEnumeration (ContainerHelper.newList ("A", "b", "c"))));
    assertFalse (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (aCont),
                                              ContainerHelper.getEnumeration (ContainerHelper.newList ("a", "B", "c"))));
    assertFalse (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (aCont),
                                              ContainerHelper.getEnumeration (ContainerHelper.newList ("a", "b", "C"))));
    assertFalse (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (aCont),
                                              ContainerHelper.getEnumeration (ContainerHelper.newList ("a",
                                                                                                       "b",
                                                                                                       "c",
                                                                                                       "d"))));
    assertFalse (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (aCont),
                                              ContainerHelper.getEnumeration (new SMap ("a", "b").add ("c", "d"))));
    assertFalse (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (aCont),
                                              ContainerHelper.newSet ("a", "b", "c")));
    assertFalse (ContainerEqualsUtils.equals (ContainerHelper.getEnumeration (aCont),
                                              ArrayHelper.newArray ("a", "b", "c")));
  }

  @Test
  public void testEqualsAsCollection ()
  {
    assertTrue (ContainerEqualsUtils.equalsAsList (null, null));
    assertTrue (ContainerEqualsUtils.equalsAsList (ContainerHelper.newList ("a", "b"), ArrayHelper.newArray ("a", "b")));
    assertTrue (ContainerEqualsUtils.equalsAsList (ContainerHelper.newList ("a", "b"),
                                                   ContainerHelper.newList ("a", "b").iterator ()));
    assertTrue (ContainerEqualsUtils.equalsAsList (ContainerHelper.newList ("a", "b"),
                                                   ContainerHelper.getEnumeration ("a", "b")));
    assertTrue (ContainerEqualsUtils.equalsAsList (ContainerHelper.newList ("a", "b"),
                                                   ContainerHelper.newOrderedSet ("a", "b")));
    assertTrue (ContainerEqualsUtils.equalsAsList (ContainerHelper.newList ("a", "b"), new MockIterable ("a", "b")));

    assertFalse (ContainerEqualsUtils.equalsAsList (null, "abc"));
    assertFalse (ContainerEqualsUtils.equalsAsList ("abc", null));
    assertFalse (ContainerEqualsUtils.equalsAsList (ContainerHelper.newList ("a", "b"), ArrayHelper.newArray ("a", "B")));
    assertFalse (ContainerEqualsUtils.equalsAsList (ContainerHelper.newList (null, "b"),
                                                    ArrayHelper.newArray ("a", (String) null)));
  }
}
