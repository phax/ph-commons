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

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link CommonsIdentityHashSet}.
 *
 * @author Philip Helger
 */
public final class CommonsIdentityHashSetTest
{
  @Test
  public void testBasic ()
  {
    final String s1 = "elem";
    // Equal but not identical
    final String s2 = new String (s1);
    assertNotSame (s1, s2);

    final CommonsIdentityHashSet <String> aSet = new CommonsIdentityHashSet <> ();
    assertTrue (aSet.isEmpty ());
    assertTrue (aSet.add (s1));
    assertFalse (aSet.add (s1));
    assertTrue (aSet.add (s2));
    assertEquals (2, aSet.size ());
    assertTrue (aSet.contains (s1));
    assertTrue (aSet.contains (s2));
    assertFalse (aSet.contains (new String (s1)));

    // null is supported
    assertTrue (aSet.add (null));
    assertTrue (aSet.contains (null));
    assertTrue (aSet.remove (null));
    assertFalse (aSet.remove (null));

    // Iterator remove
    final Iterator <String> it = aSet.iterator ();
    it.next ();
    it.remove ();
    assertEquals (1, aSet.size ());

    assertTrue (aSet.remove (aSet.iterator ().next ()));
    assertTrue (aSet.isEmpty ());

    aSet.add (s1);
    aSet.clear ();
    assertTrue (aSet.isEmpty ());
  }

  @Test
  public void testEqualsHashCode ()
  {
    final String s1 = "a";
    final String s2 = "b";
    final CommonsIdentityHashSet <String> aSet = new CommonsIdentityHashSet <> (s1, s2);
    final Set <String> aRef = Collections.newSetFromMap (new IdentityHashMap <> ());
    aRef.add (s1);
    aRef.add (s2);
    assertEquals (aRef, aSet);
    assertEquals (aSet, aRef);
    assertEquals (aRef.hashCode (), aSet.hashCode ());

    TestHelper.testGetClone (aSet);
    TestHelper.testDefaultImplementationWithEqualContentObject (aSet, new CommonsIdentityHashSet <> (s2, s1));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aSet, new CommonsIdentityHashSet <> (s1));

    final CommonsIdentityHashSet <String> aClone = aSet.getClone ();
    aClone.remove (s1);
    assertTrue (aSet.contains (s1));
  }

  @Test
  public void testCtor ()
  {
    CommonsIdentityHashSet <String> aSet = new CommonsIdentityHashSet <> (5);
    assertTrue (aSet.isEmpty ());

    aSet = new CommonsIdentityHashSet <> ((Collection <String>) null);
    assertTrue (aSet.isEmpty ());
    aSet = new CommonsIdentityHashSet <> ((Iterable <String>) null);
    assertTrue (aSet.isEmpty ());
    aSet = new CommonsIdentityHashSet <> ((String []) null);
    assertTrue (aSet.isEmpty ());

    aSet = new CommonsIdentityHashSet <> ("only");
    assertEquals (1, aSet.size ());

    aSet = new CommonsIdentityHashSet <> (new CommonsArrayList <> ("a", "b", "c"));
    assertEquals (3, aSet.size ());

    aSet = new CommonsIdentityHashSet <> ((Iterable <String>) new CommonsArrayList <> ("a", "b"));
    assertEquals (2, aSet.size ());

    final Integer aOne = Integer.valueOf (1);
    final Integer aTwo = Integer.valueOf (2);
    final CommonsIdentityHashSet <Integer> aInts = new CommonsIdentityHashSet <> ((Iterable <String>) new CommonsArrayList <> ("x",
                                                                                                                            "yy"),
                                                                                  x -> x.length () == 1 ? aOne : aTwo);
    assertEquals (2, aInts.size ());
    assertTrue (aInts.contains (aOne));

    final CommonsIdentityHashSet <Integer> aInts2 = new CommonsIdentityHashSet <> (new String [] { "x", "y" },
                                                                                   x -> aOne);
    assertEquals (1, aInts2.size ());

    final CommonsIdentityHashSet <Integer> aNew = aSet.createInstance ();
    assertNotNull (aNew);
    assertTrue (aNew.isEmpty ());

    // Methods from ICommonsSet
    assertEquals (1, new CommonsIdentityHashSet <> ("a", "bb").getAll (x -> x.length () > 1).size ());
  }
}
