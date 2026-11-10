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
 * Test class for class {@link CommonsVector}.
 *
 * @author Philip Helger
 */
public final class CommonsVectorTest
{
  @Test
  public void testBasic ()
  {
    final ICommonsList <String> aTest = new CommonsVector <> ();
    assertTrue (aTest.isEmpty ());
    aTest.add ("aaa");
    aTest.add ("bbb");
    aTest.add ("ccc");

    TestHelper.testGetClone (aTest);
  }

  @Test
  public void testCtor ()
  {
    CommonsVector <String> aTest = new CommonsVector <> ();
    assertEquals (0, aTest.size ());

    aTest = new CommonsVector <> (5);
    assertEquals (0, aTest.size ());

    aTest = new CommonsVector <> ("a", "b", "c");
    assertEquals (3, aTest.size ());

    aTest = new CommonsVector <> ("only");
    assertEquals (1, aTest.size ());

    aTest = new CommonsVector <> (new CommonsVector <> ("a", "b", "c"));
    assertEquals (3, aTest.size ());

    aTest = new CommonsVector <> ((Iterable <String>) new CommonsVector <> ("a", "b", "c", "d"));
    assertEquals (4, aTest.size ());

    aTest = new CommonsVector <> (new CommonsVector <> (Integer.valueOf (1), Integer.valueOf (2)), x -> x.toString ());
    assertEquals (2, aTest.size ());

    aTest = new CommonsVector <> ((Iterable <Integer>) new CommonsVector <> (Integer.valueOf (1),
                                                                             Integer.valueOf (2),
                                                                             Integer.valueOf (4)), x -> x.toString ());
    assertEquals (3, aTest.size ());
  }

  @Test
  public void testCreateFilteredPlainIterable ()
  {
    // An Iterable that is not a Collection
    final Iterable <String> aIterable = () -> new CommonsArrayList <> ("a", "bb", "c").iterator ();
    final Predicate <String> aLen1 = x -> x.length () == 1;
    final Predicate <String> aLenGT1 = x -> x.length () > 1;
    final Function <String, String> aLenMapper = x -> Integer.toString (x.length ());
    assertEquals (new CommonsArrayList <> ("a", "c"), CommonsVector.createFiltered (aIterable, aLen1));
    assertEquals (new CommonsArrayList <> ("2"), CommonsVector.createFiltered (aIterable, aLenGT1, aLenMapper));
    assertEquals (0, CommonsVector.createFiltered ((Iterable <String>) null, x -> true).size ());
  }
}
