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
package com.helger.commons.collection.impl;

import static org.junit.Assert.assertEquals;

import java.util.Comparator;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.state.EChange;

/**
 * Test class for class {@link CommonsEnumMap}.
 *
 * @author Philip Helger
 */
public final class CommonsEnumMapTest
{
  @Test
  public void testBasic ()
  {
    final ICommonsMap <EChange, String> aTest = new CommonsEnumMap<> (EChange.class);
    aTest.put (EChange.UNCHANGED, "blb");
    aTest.put (EChange.CHANGED, "bla");

    final ICommonsOrderedSet <EChange> aSortedKeys = aTest.getSortedByKey (Comparator.naturalOrder ()).copyOfKeySet ();
    assertEquals (EChange.CHANGED, aSortedKeys.getAtIndex (0));
    assertEquals (EChange.UNCHANGED, aSortedKeys.getAtIndex (1));

    CommonsTestHelper.testDefaultSerialization (aTest);
    CommonsTestHelper.testGetClone (aTest);
  }

  @Test
  public void testCtor ()
  {
    CommonsEnumMap <EChange, Integer> aTest = new CommonsEnumMap<> (EChange.class);
    assertEquals (0, aTest.size ());
    aTest = new CommonsEnumMap<> (new CommonsEnumMap<> (EChange.class));
    assertEquals (0, aTest.size ());
  }
}
