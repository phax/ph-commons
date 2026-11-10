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
import static org.junit.Assert.assertTrue;

import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Test;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link CommonsCopyOnWriteArrayList}.
 *
 * @author Philip Helger
 */
public final class CommonsCopyOnWriteArrayListTest
{
  @Test
  public void testBasic ()
  {
    final ICommonsList <String> aTest = new CommonsCopyOnWriteArrayList <> ();
    assertTrue (aTest.isEmpty ());
    aTest.add ("aaa");
    aTest.add ("bbb");
    aTest.add ("ccc");

    TestHelper.testGetClone (aTest);
  }

  @Test
  public void testCtor ()
  {
    CommonsCopyOnWriteArrayList <String> aTest = new CommonsCopyOnWriteArrayList <> ();
    assertEquals (0, aTest.size ());

    aTest = new CommonsCopyOnWriteArrayList <> ("a", "b", "c");
    assertEquals (3, aTest.size ());

    aTest = new CommonsCopyOnWriteArrayList <> ("only");
    assertEquals (1, aTest.size ());

    aTest = new CommonsCopyOnWriteArrayList <> (new CommonsCopyOnWriteArrayList <> ("a", "b", "c"));
    assertEquals (3, aTest.size ());

    aTest = new CommonsCopyOnWriteArrayList <> ((Iterable <String>) new CommonsCopyOnWriteArrayList <> ("a",
                                                                                                        "b",
                                                                                                        "c",
                                                                                                        "d"));
    assertEquals (4, aTest.size ());

    aTest = new CommonsCopyOnWriteArrayList <> (new CommonsCopyOnWriteArrayList <> (Integer.valueOf (1),
                                                                                    Integer.valueOf (2)),
                                                x -> x.toString ());
    assertEquals (2, aTest.size ());

    aTest = new CommonsCopyOnWriteArrayList <> ((Iterable <Integer>) new CommonsCopyOnWriteArrayList <> (Integer.valueOf (1),
                                                                                                         Integer.valueOf (2),
                                                                                                         Integer.valueOf (4)),
                                                x -> x.toString ());
    assertEquals (3, aTest.size ());
  }

  @Test
  public void testBulkCreation ()
  {
    // An Iterable that is not a Collection
    final Iterable <String> aIterable = () -> new CommonsArrayList <> ("a", "bb", "c").iterator ();
    final Predicate <String> aLen1 = x -> x.length () == 1;
    final Predicate <String> aLenGT1 = x -> x.length () > 1;
    final Function <String, String> aLenMapper = x -> Integer.toString (x.length ());
    final Predicate <String> aIs1 = x -> x.equals ("1");
    assertEquals (new CommonsArrayList <> ("a", "bb", "c"), new CommonsCopyOnWriteArrayList <> (aIterable));
    assertEquals (new CommonsArrayList <> ("1", "2", "1"),
                  new CommonsCopyOnWriteArrayList <> (aIterable, aLenMapper));
    assertEquals (new CommonsArrayList <> ("1", "2"),
                  new CommonsCopyOnWriteArrayList <> (new String [] { "a", "bb" }, aLenMapper));
    assertEquals (new CommonsArrayList <> ("a", "a"), new CommonsCopyOnWriteArrayList <> ("a", "a"));
    assertTrue (new CommonsCopyOnWriteArrayList <> ((String []) null).isEmpty ());
    assertTrue (new CommonsCopyOnWriteArrayList <> ((Iterable <String>) null, x -> x).isEmpty ());

    // All createFiltered variants
    assertEquals (new CommonsArrayList <> ("a", "c"),
                  CommonsCopyOnWriteArrayList.createFiltered (aIterable, aLen1));
    assertEquals (new CommonsArrayList <> ("2"),
                  CommonsCopyOnWriteArrayList.createFiltered (aIterable,
                                                              aLenGT1,
                                                              aLenMapper));
    assertEquals (new CommonsArrayList <> ("1", "1"),
                  CommonsCopyOnWriteArrayList.createFiltered (aIterable,
                                                              aLenMapper,
                                                              aIs1));
    final String [] aArray = { "a", "bb", "c" };
    assertEquals (new CommonsArrayList <> ("bb"), CommonsCopyOnWriteArrayList.createFiltered (aArray, x -> x.length () == 2));
    assertEquals (new CommonsArrayList <> ("1", "1"),
                  CommonsCopyOnWriteArrayList.createFiltered (aArray,
                                                              aLen1,
                                                              aLenMapper));
    assertEquals (new CommonsArrayList <> ("2"),
                  CommonsCopyOnWriteArrayList.createFiltered (aArray,
                                                              aLenMapper,
                                                              x -> x.equals ("2")));
    assertTrue (CommonsCopyOnWriteArrayList.createFiltered ((String []) null, x -> true).isEmpty ());
  }
}
