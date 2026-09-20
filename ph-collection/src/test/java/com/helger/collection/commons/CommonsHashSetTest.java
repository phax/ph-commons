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
import java.util.Enumeration;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Test;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link CommonsHashSet}.
 *
 * @author Philip Helger
 */
public final class CommonsHashSetTest
{
  @Test
  public void testBasic ()
  {
    final ICommonsSet <String> aTest = new CommonsHashSet <> ();
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
    CommonsHashSet <String> aTest = new CommonsHashSet <> ();
    assertEquals (0, aTest.size ());

    aTest = new CommonsHashSet <> (5);
    assertEquals (0, aTest.size ());

    aTest = new CommonsHashSet <> ("a", "b", "c");
    assertEquals (3, aTest.size ());

    aTest = new CommonsHashSet <> ("only");
    assertEquals (1, aTest.size ());

    aTest = new CommonsHashSet <> (new CommonsHashSet <> ("a", "b", "c"));
    assertEquals (3, aTest.size ());

    aTest = new CommonsHashSet <> (new CommonsArrayList <> ("a", "b", "c"));
    assertEquals (3, aTest.size ());

    aTest = new CommonsHashSet <> ((Iterable <String>) new CommonsHashSet <> ("a", "b", "c", "d"));
    assertEquals (4, aTest.size ());

    aTest = new CommonsHashSet <> (new CommonsArrayList <> (Integer.valueOf (1), Integer.valueOf (2)),
                                   x -> x.toString ());
    assertEquals (2, aTest.size ());

    aTest = new CommonsHashSet <> ((Iterable <Integer>) new CommonsHashSet <> (Integer.valueOf (1),
                                                                               Integer.valueOf (2),
                                                                               Integer.valueOf (4)),
                                   x -> x.toString ());
    assertEquals (3, aTest.size ());
  }

  @Test
  public void testCtorExt ()
  {
    // Enumeration
    CommonsHashSet <String> aTest = new CommonsHashSet <> (new CommonsVector <> ("a", "b").elements ());
    assertEquals (2, aTest.size ());

    // Mapped from an array
    aTest = new CommonsHashSet <> (new Integer [] { Integer.valueOf (1), Integer.valueOf (2) }, x -> x.toString ());
    assertEquals (2, aTest.size ());

    // null values are allowed everywhere
    assertTrue (new CommonsHashSet <> ((Collection <String>) null).isEmpty ());
    assertTrue (new CommonsHashSet <> ((Iterable <String>) null).isEmpty ());
    assertTrue (new CommonsHashSet <> ((Enumeration <String>) null).isEmpty ());
    assertTrue (new CommonsHashSet <> ((String []) null).isEmpty ());
    assertTrue (new CommonsHashSet <> ((Collection <Integer>) null, x -> x.toString ()).isEmpty ());
    assertTrue (new CommonsHashSet <> ((Iterable <Integer>) null, x -> x.toString ()).isEmpty ());
    assertTrue (new CommonsHashSet <> ((Integer []) null, x -> x.toString ()).isEmpty ());

    assertNotNull (new CommonsHashSet <> ().<Integer> createInstance ());
    assertTrue (new CommonsHashSet <> ("a").getClone ().contains ("a"));
  }

  @Test
  public void testCreateFiltered ()
  {
    final Predicate <String> aSrcFilter = x -> x.equals ("a");
    final Function <Integer, String> aMapper = x -> x.toString ();
    final Predicate <String> aDstFilter = x -> x.equals ("1");
    final ICommonsList <Integer> aSrcInts = new CommonsArrayList <> (Integer.valueOf (1), Integer.valueOf (2));

    assertEquals (1,
                  CommonsHashSet.createFiltered ((Iterable <String>) new CommonsArrayList <> ("a", "b"), aSrcFilter)
                                .size ());
    assertEquals (1, CommonsHashSet.createFiltered (new String [] { "a", "b" }, aSrcFilter).size ());

    assertEquals (1,
                  CommonsHashSet.createFiltered ((Iterable <Integer>) aSrcInts,
                                                 (Predicate <Integer>) x -> x.intValue () == 1,
                                                 aMapper).size ());
    assertEquals (1,
                  CommonsHashSet.createFiltered (new Integer [] { Integer.valueOf (1), Integer.valueOf (2) },
                                                 (Predicate <Integer>) x -> x.intValue () == 1,
                                                 aMapper).size ());

    assertEquals (1, CommonsHashSet.createFiltered (aSrcInts, aMapper, aDstFilter).size ());
    assertEquals (1,
                  CommonsHashSet.createFiltered (new Integer [] { Integer.valueOf (1), Integer.valueOf (2) },
                                                 aMapper,
                                                 aDstFilter).size ());
  }
}
