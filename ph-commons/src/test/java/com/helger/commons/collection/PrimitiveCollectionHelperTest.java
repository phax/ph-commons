/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.commons.collection;

import static com.helger.collection.helper.PrimitiveCollectionHelper.newPrimitiveList;
import static com.helger.collection.helper.PrimitiveCollectionHelper.newPrimitiveOrderedSet;
import static com.helger.collection.helper.PrimitiveCollectionHelper.newPrimitiveSet;
import static com.helger.collection.helper.PrimitiveCollectionHelper.newPrimitiveSortedSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.collection.helper.PrimitiveCollectionHelper;

/**
 * Test class for class {@link PrimitiveCollectionHelper}
 *
 * @author Philip Helger
 */
public final class PrimitiveCollectionHelperTest
{
  @Test
  public void testNewBooleanContainer ()
  {
    final boolean [] aEmptyArray = new boolean [0];
    assertTrue (newPrimitiveList (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveSet (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveOrderedSet (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveSortedSet (aEmptyArray).isEmpty ());

    final boolean [] aValues = new boolean [] { true, true };
    assertEquals (2, newPrimitiveList (aValues).size ());
    assertEquals (1, newPrimitiveSet (aValues).size ());
    assertEquals (1, newPrimitiveOrderedSet (aValues).size ());
    assertEquals (1, newPrimitiveSortedSet (aValues).size ());
  }

  @Test
  public void testNewByteContainer ()
  {
    final byte [] aEmptyArray = new byte [0];
    assertTrue (newPrimitiveList (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveSet (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveOrderedSet (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveSortedSet (aEmptyArray).isEmpty ());

    final byte [] aValues = new byte [] { 3, 3 };
    assertEquals (2, newPrimitiveList (aValues).size ());
    assertEquals (1, newPrimitiveSet (aValues).size ());
    assertEquals (1, newPrimitiveOrderedSet (aValues).size ());
    assertEquals (1, newPrimitiveSortedSet (aValues).size ());
  }

  @Test
  public void testNewCharContainer ()
  {
    final char [] aEmptyArray = new char [0];
    assertTrue (newPrimitiveList (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveSet (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveOrderedSet (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveSortedSet (aEmptyArray).isEmpty ());

    final char [] aValues = new char [] { 'x', 'x' };
    assertEquals (2, newPrimitiveList (aValues).size ());
    assertEquals (1, newPrimitiveSet (aValues).size ());
    assertEquals (1, newPrimitiveOrderedSet (aValues).size ());
    assertEquals (1, newPrimitiveSortedSet (aValues).size ());
  }

  @Test
  public void testNewDoubleContainer ()
  {
    final double [] aEmptyArray = new double [0];
    assertTrue (newPrimitiveList (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveSet (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveOrderedSet (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveSortedSet (aEmptyArray).isEmpty ());

    final double [] aValues = new double [] { 1.1, 1.1 };
    assertEquals (2, newPrimitiveList (aValues).size ());
    assertEquals (1, newPrimitiveSet (aValues).size ());
    assertEquals (1, newPrimitiveOrderedSet (aValues).size ());
    assertEquals (1, newPrimitiveSortedSet (aValues).size ());
  }

  @Test
  public void testNewFloatContainer ()
  {
    final float [] aEmptyArray = new float [0];
    assertTrue (newPrimitiveList (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveSet (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveOrderedSet (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveSortedSet (aEmptyArray).isEmpty ());

    final float [] aValues = new float [] { 3.2f, 3.2f };
    assertEquals (2, newPrimitiveList (aValues).size ());
    assertEquals (1, newPrimitiveSet (aValues).size ());
    assertEquals (1, newPrimitiveOrderedSet (aValues).size ());
    assertEquals (1, newPrimitiveSortedSet (aValues).size ());
  }

  @Test
  public void testNewIntContainer ()
  {
    final int [] aEmptyArray = new int [0];
    assertTrue (newPrimitiveList (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveSet (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveOrderedSet (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveSortedSet (aEmptyArray).isEmpty ());

    final int [] aValues = new int [] { 5, 5 };
    assertEquals (2, newPrimitiveList (aValues).size ());
    assertEquals (1, newPrimitiveSet (aValues).size ());
    assertEquals (1, newPrimitiveOrderedSet (aValues).size ());
    assertEquals (1, newPrimitiveSortedSet (aValues).size ());
  }

  @Test
  public void testNewLongContainer ()
  {
    final long [] aEmptyArray = new long [0];
    assertTrue (newPrimitiveList (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveSet (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveOrderedSet (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveSortedSet (aEmptyArray).isEmpty ());

    final long [] aValues = new long [] { 17, 17 };
    assertEquals (2, newPrimitiveList (aValues).size ());
    assertEquals (1, newPrimitiveSet (aValues).size ());
    assertEquals (1, newPrimitiveOrderedSet (aValues).size ());
    assertEquals (1, newPrimitiveSortedSet (aValues).size ());
  }

  @Test
  public void testNewShortContainer ()
  {
    final short [] aEmptyArray = new short [0];
    assertTrue (newPrimitiveList (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveSet (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveOrderedSet (aEmptyArray).isEmpty ());
    assertTrue (newPrimitiveSortedSet (aEmptyArray).isEmpty ());

    final short [] aValues = new short [] { 5, 5 };
    assertEquals (2, newPrimitiveList (aValues).size ());
    assertEquals (1, newPrimitiveSet (aValues).size ());
    assertEquals (1, newPrimitiveOrderedSet (aValues).size ());
    assertEquals (1, newPrimitiveSortedSet (aValues).size ());
  }
}
