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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;
import java.util.SortedSet;

import org.junit.Test;

/**
 * Test class for class {@link CommonsTreeSet}.
 *
 * @author Philip Helger
 */
public final class CommonsTreeSetTest
{
  @Test
  public void testGetAllKeepsComparator ()
  {
    final Comparator <String> aComp = Comparator.reverseOrder ();
    final CommonsTreeSet <String> aSet = new CommonsTreeSet <> (aComp);
    aSet.add ("a");
    aSet.add ("b");
    aSet.add ("c");
    assertEquals (new CommonsArrayList <> ("c", "b", "a"), new CommonsArrayList <> (aSet));

    // With filter
    final ICommonsSet <String> aFiltered = aSet.getAll (x -> !"b".equals (x));
    assertTrue (aFiltered instanceof SortedSet <?>);
    assertSame (aComp, ((SortedSet <String>) aFiltered).comparator ());
    assertEquals (new CommonsArrayList <> ("c", "a"), new CommonsArrayList <> (aFiltered));

    // Without filter
    final ICommonsSet <String> aAll = aSet.getAll (null);
    assertSame (aComp, ((SortedSet <String>) aAll).comparator ());
    assertEquals (new CommonsArrayList <> ("c", "b", "a"), new CommonsArrayList <> (aAll));

    // Instance of
    final ICommonsSet <String> aInstanceOf = aSet.getAllInstanceOf (String.class);
    assertSame (aComp, ((SortedSet <String>) aInstanceOf).comparator ());
    assertEquals (new CommonsArrayList <> ("c", "b", "a"), new CommonsArrayList <> (aInstanceOf));

    // Mapped - natural order of the destination type
    final ICommonsSet <Integer> aMapped = aSet.getAllMapped (x -> Integer.valueOf (x.charAt (0)));
    assertEquals (new CommonsArrayList <> (Integer.valueOf ('a'), Integer.valueOf ('b'), Integer.valueOf ('c')),
                  new CommonsArrayList <> (aMapped));
  }
}
