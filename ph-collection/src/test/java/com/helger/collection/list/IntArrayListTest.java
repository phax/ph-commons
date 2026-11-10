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
package com.helger.collection.list;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

import org.junit.Test;

import com.helger.base.state.EContinue;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link IntArrayList}.
 *
 * @author Philip Helger
 */
public final class IntArrayListTest
{
  @Test
  public void testBasic ()
  {
    final IntArrayList aList = new IntArrayList ();
    assertTrue (aList.isEmpty ());
    assertFalse (aList.isNotEmpty ());
    assertEquals (0, aList.size ());
    assertEquals (0, aList.toArray ().length);
    assertFalse (aList.contains (0));
    assertEquals (-1, aList.indexOf (0));

    aList.add (5);
    aList.add (-3);
    aList.add (5);
    assertFalse (aList.isEmpty ());
    assertTrue (aList.isNotEmpty ());
    assertEquals (3, aList.size ());
    assertEquals (5, aList.get (0));
    assertEquals (-3, aList.get (1));
    assertEquals (5, aList.get (2));
    assertEquals (5, aList.getLast ());
    assertTrue (aList.contains (-3));
    assertFalse (aList.contains (4));
    assertEquals (0, aList.indexOf (5));
    assertEquals (1, aList.indexOf (-3));
    assertArrayEquals (new int [] { 5, -3, 5 }, aList.toArray ());

    assertEquals (-3, aList.set (1, 7));
    assertEquals (7, aList.get (1));

    assertEquals (5, aList.removeLast ());
    assertEquals (2, aList.size ());
    assertEquals (7, aList.getLast ());

    aList.clear ();
    assertTrue (aList.isEmpty ());
    assertEquals (0, aList.size ());
  }

  @Test
  public void testGrowAndAddAll ()
  {
    // Start with capacity 0
    final IntArrayList aList = new IntArrayList (0);
    for (int i = 0; i < 1000; ++i)
      aList.add (i);
    assertEquals (1000, aList.size ());
    for (int i = 0; i < 1000; ++i)
      assertEquals (i, aList.get (i));

    aList.clear ();
    aList.addAll (1, 2, 3);
    aList.addAll ();
    aList.addAll ((int []) null);
    aList.addAll (new int [100]);
    assertEquals (103, aList.size ());
    assertEquals (3, aList.get (2));
    assertEquals (0, aList.get (102));
  }

  @Test
  public void testInvalidAccess ()
  {
    final IntArrayList aList = new IntArrayList (20);
    aList.add (1);
    try
    {
      // Within the capacity but outside of the size
      aList.get (1);
      fail ();
    }
    catch (final IndexOutOfBoundsException ex)
    {
      // expected
    }
    try
    {
      aList.get (-1);
      fail ();
    }
    catch (final IndexOutOfBoundsException ex)
    {
      // expected
    }
    try
    {
      aList.set (1, 5);
      fail ();
    }
    catch (final IndexOutOfBoundsException ex)
    {
      // expected
    }

    aList.clear ();
    try
    {
      aList.getLast ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {
      // expected
    }
    try
    {
      aList.removeLast ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {
      // expected
    }
    try
    {
      new IntArrayList (-1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testForEach ()
  {
    final IntArrayList aList = new IntArrayList ();
    aList.addAll (3, 1, 2);
    final IntArrayList aTarget = new IntArrayList ();
    aList.forEach (aTarget::add);
    assertEquals (aList, aTarget);

    // Filtered
    aTarget.clear ();
    aList.forEach (x -> x != 1, aTarget::add);
    assertArrayEquals (new int [] { 3, 2 }, aTarget.toArray ());

    // No filter
    aTarget.clear ();
    aList.forEach (null, aTarget::add);
    assertEquals (aList, aTarget);
  }

  @Test
  public void testForEachByIndex ()
  {
    final IntArrayList aList = new IntArrayList ();
    aList.addAll (3, 1, 2);
    final IntArrayList aValues = new IntArrayList ();
    final IntArrayList aIndices = new IntArrayList ();
    aList.forEachByIndex ( (nValue, nIndex) -> {
      aValues.add (nValue);
      aIndices.add (nIndex);
    });
    assertEquals (aList, aValues);
    assertArrayEquals (new int [] { 0, 1, 2 }, aIndices.toArray ());
  }

  @Test
  public void testForEachBreakable ()
  {
    final IntArrayList aList = new IntArrayList ();
    aList.addAll (3, 1, 2);
    final IntArrayList aTarget = new IntArrayList ();
    assertEquals (EContinue.BREAK, aList.forEachBreakable (x -> {
      aTarget.add (x);
      return EContinue.valueOf (x != 1);
    }));
    assertArrayEquals (new int [] { 3, 1 }, aTarget.toArray ());

    aTarget.clear ();
    assertEquals (EContinue.CONTINUE, aList.forEachBreakable (x -> {
      aTarget.add (x);
      return EContinue.CONTINUE;
    }));
    assertEquals (aList, aTarget);

    assertEquals (EContinue.CONTINUE, new IntArrayList ().forEachBreakable (x -> EContinue.BREAK));
  }

  @Test
  public void testSort ()
  {
    final IntArrayList aList = new IntArrayList (4);
    aList.addAll (5, -1, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
    aList.sort ();
    assertArrayEquals (new int [] { Integer.MIN_VALUE, -1, 0, 3, 5, Integer.MAX_VALUE }, aList.toArray ());

    // Descending
    aList.sortStable ( (a, b) -> Integer.compare (b, a));
    assertArrayEquals (new int [] { Integer.MAX_VALUE, 5, 3, 0, -1, Integer.MIN_VALUE }, aList.toArray ());

    // Stable: sort by tens only, the units show the original order
    aList.clear ();
    aList.addAll (21, 10, 22, 11, 20, 12);
    aList.sortStable ( (a, b) -> Integer.compare (a / 10, b / 10));
    assertArrayEquals (new int [] { 10, 11, 12, 21, 22, 20 }, aList.toArray ());
  }

  @Test
  public void testStandardMethods ()
  {
    final IntArrayList aList = new IntArrayList ();
    TestHelper.testDefaultImplementationWithEqualContentObject (aList, new IntArrayList (100));
    aList.addAll (1, 2, 3);

    // Same content, different capacity
    final IntArrayList aList2 = new IntArrayList (100);
    aList2.addAll (1, 2, 3);
    TestHelper.testDefaultImplementationWithEqualContentObject (aList, aList2);

    // Unused but filled capacity must not be relevant
    final IntArrayList aList3 = new IntArrayList ();
    aList3.addAll (1, 2, 3, 4);
    aList3.removeLast ();
    TestHelper.testDefaultImplementationWithEqualContentObject (aList, aList3);

    aList2.add (4);
    TestHelper.testDefaultImplementationWithDifferentContentObject (aList, aList2);

    final IntArrayList aList4 = new IntArrayList ();
    aList4.addAll (1, 3, 2);
    TestHelper.testDefaultImplementationWithDifferentContentObject (aList, aList4);

    TestHelper.testGetClone (aList);
    final IntArrayList aClone = aList.getClone ();
    assertNotSame (aList, aClone);
    // Modifying the clone does not modify the original
    aClone.set (0, 99);
    aClone.add (17);
    assertEquals (1, aList.get (0));
    assertEquals (3, aList.size ());
    // The clone of an empty list can grow
    final IntArrayList aEmptyClone = new IntArrayList ().getClone ();
    aEmptyClone.add (1);
    assertEquals (1, aEmptyClone.size ());
  }
}
