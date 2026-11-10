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
package com.helger.collection.set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link IntHashSet}.
 *
 * @author Philip Helger
 */
public final class IntHashSetTest
{
  private static final float [] FILL_FACTORS = { 0.25f, 0.5f, 0.75f, 0.9f, 0.99f };

  @Test
  public void testBasic ()
  {
    final IntHashSet aSet = new IntHashSet ();
    assertTrue (aSet.isEmpty ());
    assertEquals (0, aSet.size ());
    assertFalse (aSet.contains (0));
    assertFalse (aSet.remove (0));
    assertEquals (0, aSet.toArray ().length);

    // The free key 0 is handled separately
    assertTrue (aSet.add (0));
    assertFalse (aSet.add (0));
    assertTrue (aSet.contains (0));
    assertEquals (1, aSet.size ());

    assertTrue (aSet.add (17));
    assertTrue (aSet.add (-17));
    assertFalse (aSet.add (17));
    assertEquals (3, aSet.size ());
    assertTrue (aSet.contains (17));
    assertTrue (aSet.contains (-17));
    assertFalse (aSet.contains (18));

    final int [] aArray = aSet.toArray ();
    Arrays.sort (aArray);
    assertArrayEquals (new int [] { -17, 0, 17 }, aArray);

    final IntHashSet aCopy = new IntHashSet ();
    aSet.forEach (aCopy::add);
    assertEquals (aSet, aCopy);

    assertTrue (aSet.remove (0));
    assertFalse (aSet.remove (0));
    assertFalse (aSet.contains (0));
    assertTrue (aSet.remove (17));
    assertFalse (aSet.remove (17));
    assertEquals (1, aSet.size ());

    aSet.add (0);
    aSet.clear ();
    assertTrue (aSet.isEmpty ());
    assertFalse (aSet.contains (0));
    assertFalse (aSet.contains (-17));
    assertTrue (aSet.add (-17));
  }

  private static void _testRandom (final float fFillFactor)
  {
    final Random aRandom = new Random (12345);
    final Set <Integer> aRef = new HashSet <> ();
    final IntHashSet aSet = new IntHashSet (4, fFillFactor);
    for (int i = 0; i < 50_000; ++i)
    {
      // Small value range to get many duplicates and removals of existing values
      final int nValue = aRandom.nextInt (5_000) - 2_500;
      if (aRandom.nextInt (3) == 0)
        assertEquals (Boolean.valueOf (aRef.remove (Integer.valueOf (nValue))),
                      Boolean.valueOf (aSet.remove (nValue)));
      else
        assertEquals (Boolean.valueOf (aRef.add (Integer.valueOf (nValue))), Boolean.valueOf (aSet.add (nValue)));
      assertEquals (aRef.size (), aSet.size ());
    }
    for (int i = -2_500; i < 2_500; ++i)
      assertEquals (Boolean.valueOf (aRef.contains (Integer.valueOf (i))), Boolean.valueOf (aSet.contains (i)));
    assertEquals (aRef.size (), aSet.toArray ().length);
  }

  @Test
  public void testRandom ()
  {
    for (final float ff : FILL_FACTORS)
      _testRandom (ff);
  }

  @Test
  public void testLarge ()
  {
    final IntHashSet aSet = new IntHashSet (1);
    for (int i = 0; i < 100_000; ++i)
      assertTrue (aSet.add (i * 7));
    assertEquals (100_000, aSet.size ());
    for (int i = 0; i < 100_000; ++i)
    {
      assertTrue (aSet.contains (i * 7));
      assertFalse (aSet.contains (i * 7 + 1));
    }
    for (int i = 0; i < 100_000; i += 2)
      assertTrue (aSet.remove (i * 7));
    assertEquals (50_000, aSet.size ());
    for (int i = 0; i < 100_000; ++i)
      assertEquals (Boolean.valueOf ((i & 1) == 1), Boolean.valueOf (aSet.contains (i * 7)));
  }

  @Test
  public void testStandardMethods ()
  {
    final IntHashSet aSet = new IntHashSet ();
    assertEquals (aSet, new IntHashSet (1000));
    aSet.add (1);
    aSet.add (2);
    aSet.add (0);

    // Different insertion order and capacity
    final IntHashSet aSet2 = new IntHashSet (1000);
    aSet2.add (0);
    aSet2.add (2);
    aSet2.add (1);
    TestHelper.testDefaultImplementationWithEqualContentObject (aSet, aSet2);

    aSet2.remove (0);
    TestHelper.testDefaultImplementationWithDifferentContentObject (aSet, aSet2);
    aSet2.add (3);
    TestHelper.testDefaultImplementationWithDifferentContentObject (aSet, aSet2);

    TestHelper.testGetClone (aSet);
    final IntHashSet aClone = aSet.getClone ();
    aClone.add (99);
    aClone.remove (0);
    assertFalse (aSet.contains (99));
    assertTrue (aSet.contains (0));
    assertEquals (3, aSet.size ());
  }
}
