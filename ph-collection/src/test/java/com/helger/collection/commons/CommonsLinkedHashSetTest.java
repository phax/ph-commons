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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Test;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link CommonsLinkedHashSet}.
 *
 * @author Philip Helger
 */
public final class CommonsLinkedHashSetTest
{
  @Test
  public void testBasic ()
  {
    final ICommonsSet <String> aTest = new CommonsLinkedHashSet <> ();
    aTest.add ("aaa");
    aTest.add ("bbb");
    aTest.add ("ccc");

    final ICommonsList <String> aSortedKeys = aTest.getSorted (Comparator.naturalOrder ());
    assertEquals ("aaa", aSortedKeys.getAtIndex (0));
    assertEquals ("bbb", aSortedKeys.getAtIndex (1));
    assertEquals ("ccc", aSortedKeys.getAtIndex (2));

    TestHelper.testGetClone (aTest);
  }

  @Test
  public void testCtor ()
  {
    CommonsLinkedHashSet <String> aTest = new CommonsLinkedHashSet <> ();
    assertEquals (0, aTest.size ());

    aTest = new CommonsLinkedHashSet <> (5);
    assertEquals (0, aTest.size ());

    aTest = new CommonsLinkedHashSet <> ("a", "b", "c");
    assertEquals (3, aTest.size ());

    aTest = new CommonsLinkedHashSet <> ("only");
    assertEquals (1, aTest.size ());

    aTest = new CommonsLinkedHashSet <> (new CommonsLinkedHashSet <> ("a", "b", "c"));
    assertEquals (3, aTest.size ());

    aTest = new CommonsLinkedHashSet <> (new CommonsArrayList <> ("a", "b", "c"));
    assertEquals (3, aTest.size ());

    aTest = new CommonsLinkedHashSet <> ((Iterable <String>) new CommonsLinkedHashSet <> ("a", "b", "c", "d"));
    assertEquals (4, aTest.size ());

    aTest = new CommonsLinkedHashSet <> (new CommonsArrayList <> (Integer.valueOf (1), Integer.valueOf (2)),
                                         x -> x.toString ());
    assertEquals (2, aTest.size ());

    aTest = new CommonsLinkedHashSet <> ((Iterable <Integer>) new CommonsLinkedHashSet <> (Integer.valueOf (1),
                                                                                           Integer.valueOf (2),
                                                                                           Integer.valueOf (4)),
                                         x -> x.toString ());
    assertEquals (3, aTest.size ());
  }

  @Test
  public void testCtorExt ()
  {
    // null values are allowed everywhere
    assertTrue (new CommonsLinkedHashSet <> ((Collection <String>) null).isEmpty ());
    assertTrue (new CommonsLinkedHashSet <> ((Iterable <String>) null).isEmpty ());
    assertTrue (new CommonsLinkedHashSet <> ((String []) null).isEmpty ());
    assertTrue (new CommonsLinkedHashSet <> (5, 0.75f).isEmpty ());

    assertNotNull (new CommonsLinkedHashSet <> ().<Integer> createInstance ());
    assertTrue (new CommonsLinkedHashSet <> ("a").getClone ().contains ("a"));
  }

  @Test
  public void testCreateFiltered ()
  {
    final Predicate <String> aSrcFilter = x -> x.equals ("a");
    final Function <Integer, String> aMapper = x -> x.toString ();
    final Predicate <String> aDstFilter = x -> x.equals ("1");
    final ICommonsList <Integer> aSrcInts = new CommonsArrayList <> (Integer.valueOf (1), Integer.valueOf (2));

    assertEquals (1,
                  CommonsLinkedHashSet.createFiltered ((Iterable <String>) new CommonsArrayList <> ("a", "b"),
                                                       aSrcFilter).size ());
    assertEquals (1, CommonsLinkedHashSet.createFiltered (new String [] { "a", "b" }, aSrcFilter).size ());

    assertEquals (1,
                  CommonsLinkedHashSet.createFiltered ((Iterable <Integer>) aSrcInts,
                                                       (Predicate <Integer>) x -> x.intValue () == 1,
                                                       aMapper).size ());
    assertEquals (1,
                  CommonsLinkedHashSet.createFiltered (new Integer [] { Integer.valueOf (1), Integer.valueOf (2) },
                                                       (Predicate <Integer>) x -> x.intValue () == 1,
                                                       aMapper).size ());

    assertEquals (1, CommonsLinkedHashSet.createFiltered (aSrcInts, aMapper, aDstFilter).size ());
    assertEquals (1,
                  CommonsLinkedHashSet.createFiltered (new Integer [] { Integer.valueOf (1), Integer.valueOf (2) },
                                                       aMapper,
                                                       aDstFilter).size ());
  }
}
