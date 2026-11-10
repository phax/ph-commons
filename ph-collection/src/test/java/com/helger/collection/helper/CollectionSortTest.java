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
package com.helger.collection.helper;

import static com.helger.collection.helper.CollectionHelperExt.createList;
import static com.helger.collection.helper.CollectionSort.getSorted;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.helger.collection.commons.CommonsIterableIterator;
import com.helger.collection.commons.ICommonsIterableIterator;
import com.helger.collection.commons.ICommonsList;

/**
 * Test class for class {@link CollectionSort}
 *
 * @author Philip Helger
 */
public final class CollectionSortTest
{
  @Test
  public void testGetSortedIterator ()
  {
    assertNotNull (getSorted ((Iterator <String>) null));

    final ICommonsList <String> aList = createList ("d", "c", "b", "a");
    final ICommonsList <String> aSorted = getSorted (aList.iterator ());
    assertEquals (4, aSorted.size ());
    assertEquals ("a", aSorted.get (0));
    assertEquals ("b", aSorted.get (1));
    assertEquals ("c", aSorted.get (2));
    assertEquals ("d", aSorted.get (3));
  }

  @Test
  public void testGetSortedIterable ()
  {
    assertNotNull (getSorted ((Iterable <String>) null));

    final ICommonsList <String> aList = createList ("d", "c", "b", "a");
    final ICommonsList <String> aSorted = getSorted (aList);
    assertEquals (4, aSorted.size ());
    assertEquals ("a", aSorted.get (0));
    assertEquals ("b", aSorted.get (1));
    assertEquals ("c", aSorted.get (2));
    assertEquals ("d", aSorted.get (3));
  }

  @Test
  public void testGetSortedIIterableIterator ()
  {
    assertNotNull (getSorted ((ICommonsIterableIterator <String>) null));

    final ICommonsList <String> aList = createList ("d", "c", "b", "a");
    final ICommonsList <String> aSorted = getSorted (new CommonsIterableIterator <> (aList));
    assertEquals (4, aSorted.size ());
    assertEquals ("a", aSorted.get (0));
    assertEquals ("b", aSorted.get (1));
    assertEquals ("c", aSorted.get (2));
    assertEquals ("d", aSorted.get (3));
  }

  @Test
  public void testGetSortedArray ()
  {
    assertNotNull (getSorted ((String []) null));

    final ICommonsList <String> aSorted = getSorted ("d", "c", "b", "a");
    assertEquals (4, aSorted.size ());
    assertEquals ("a", aSorted.get (0));
    assertEquals ("b", aSorted.get (1));
    assertEquals ("c", aSorted.get (2));
    assertEquals ("d", aSorted.get (3));
  }

  @Test
  public void testGetSortedIndicesEmpty ()
  {
    assertEquals (0, CollectionSort.getSortedIndices (0, (a, b) -> {
      fail ();
      return 0;
    }).length);
    assertArrayEquals (new int [] { 0 }, CollectionSort.getSortedIndices (1, (a, b) -> {
      fail ();
      return 0;
    }));

    try
    {
      CollectionSort.getSortedIndices (-1, Integer::compare);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testGetSortedIndicesSmall ()
  {
    final String [] aValues = { "d", "b", "c", "a" };
    final int [] aIndices = CollectionSort.getSortedIndices (aValues.length, (a, b) -> aValues[a].compareTo (aValues[b]));
    assertArrayEquals (new int [] { 3, 1, 2, 0 }, aIndices);

    // Descending
    assertArrayEquals (new int [] { 0, 2, 1, 3 },
                       CollectionSort.getSortedIndices (aValues.length, (a, b) -> aValues[b].compareTo (aValues[a])));
  }

  private static void _testStability (final int nCount, final int nDistinctKeys)
  {
    final Random aRandom = new Random (nCount * 31L + nDistinctKeys);
    final int [] aKeys = new int [nCount];
    for (int i = 0; i < nCount; ++i)
      aKeys[i] = aRandom.nextInt (nDistinctKeys);

    final int [] aIndices = CollectionSort.getSortedIndices (nCount, (a, b) -> Integer.compare (aKeys[a], aKeys[b]));
    assertEquals (nCount, aIndices.length);

    // Every index must be contained exactly once
    final int [] aCopy = aIndices.clone ();
    Arrays.sort (aCopy);
    for (int i = 0; i < nCount; ++i)
      assertEquals (i, aCopy[i]);

    for (int i = 1; i < nCount; ++i)
    {
      final int nPrevKey = aKeys[aIndices[i - 1]];
      final int nCurKey = aKeys[aIndices[i]];
      // Sorted by key
      assertTrue (nPrevKey <= nCurKey);
      // Stable: equal keys keep the original (ascending) index order
      if (nPrevKey == nCurKey)
        assertTrue (aIndices[i - 1] < aIndices[i]);
    }
  }

  @Test
  public void testGetSortedIndicesStable ()
  {
    // Sizes below, at and above the insertion sort threshold, and some odd sizes for the merge
    for (final int nCount : new int [] { 2, 3, 31, 32, 33, 63, 64, 65, 100, 1000, 1025, 10_000 })
      for (final int nDistinctKeys : new int [] { 1, 2, 10, 1000 })
        _testStability (nCount, nDistinctKeys);
  }

  @Test
  public void testGetSortedIndicesPresorted ()
  {
    final int nCount = 1000;
    // Already sorted
    int [] aIndices = CollectionSort.getSortedIndices (nCount, Integer::compare);
    for (int i = 0; i < nCount; ++i)
      assertEquals (i, aIndices[i]);

    // Reverse sorted
    aIndices = CollectionSort.getSortedIndices (nCount, (a, b) -> Integer.compare (b, a));
    for (int i = 0; i < nCount; ++i)
      assertEquals (nCount - 1 - i, aIndices[i]);
  }

  @Test
  public void testGetSortedIndicesComparisonCount ()
  {
    // One range of the insertion sort threshold
    final int nCount = 32;
    final AtomicInteger aCount = new AtomicInteger ();

    // Already sorted: one comparison with the predecessor per element
    CollectionSort.getSortedIndices (nCount, (a, b) -> {
      aCount.incrementAndGet ();
      return Integer.compare (a, b);
    });
    assertEquals (nCount - 1, aCount.get ());

    // Reverse sorted: the binary search needs at most 1 + ceil(log2(i)) comparisons per element,
    // a linear search would need 1 + 2 + ... + 31 = 496
    aCount.set (0);
    final int [] aIndices = CollectionSort.getSortedIndices (nCount, (a, b) -> {
      aCount.incrementAndGet ();
      return Integer.compare (b, a);
    });
    for (int i = 0; i < nCount; ++i)
      assertEquals (nCount - 1 - i, aIndices[i]);
    assertTrue (aCount.get () <= 6 * (nCount - 1));
  }

  @Test
  public void testSortInlineStable ()
  {
    // Sort by "value / 10" - the last digit shows the stability
    final int [] aValues = { 52, 11, 50, 13, 31, 12, 51, 30 };
    CollectionSort.sortInlineStable (aValues, 0, aValues.length, (a, b) -> Integer.compare (a / 10, b / 10));
    assertArrayEquals (new int [] { 11, 13, 12, 31, 30, 52, 50, 51 }, aValues);

    // Only a part of the array
    final int [] aPart = { 9, 52, 11, 50, 13, 0 };
    CollectionSort.sortInlineStable (aPart, 1, 4, (a, b) -> Integer.compare (a / 10, b / 10));
    assertArrayEquals (new int [] { 9, 11, 13, 52, 50, 0 }, aPart);

    // Larger than the insertion sort threshold, compared with the JDK stable object sort
    final Random aRandom = new Random (4711);
    final int nCount = 5000;
    final int [] aBig = new int [nCount + 2];
    final Integer [] aExpected = new Integer [nCount];
    for (int i = 0; i < nCount; ++i)
    {
      aBig[i + 1] = aRandom.nextInt (100_000);
      aExpected[i] = Integer.valueOf (aBig[i + 1]);
    }
    aBig[0] = -1;
    aBig[nCount + 1] = -2;
    Arrays.sort (aExpected, (a, b) -> Integer.compare (a.intValue () % 97, b.intValue () % 97));
    CollectionSort.sortInlineStable (aBig, 1, nCount, (a, b) -> Integer.compare (a % 97, b % 97));
    assertEquals (-1, aBig[0]);
    assertEquals (-2, aBig[nCount + 1]);
    for (int i = 0; i < nCount; ++i)
      assertEquals (aExpected[i].intValue (), aBig[i + 1]);

    // Invalid ranges
    try
    {
      CollectionSort.sortInlineStable (new int [3], 2, 2, Integer::compare);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }
}
