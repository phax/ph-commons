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
package com.helger.collection.helper;

import static com.helger.collection.helper.PrimitiveCollectionHelper.createPrimitiveList;
import static com.helger.collection.helper.PrimitiveCollectionHelper.createPrimitiveOrderedSet;
import static com.helger.collection.helper.PrimitiveCollectionHelper.createPrimitiveSet;
import static com.helger.collection.helper.PrimitiveCollectionHelper.createPrimitiveSortedSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
    final boolean [] aEmptyArray = {};
    assertTrue (createPrimitiveList (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveSet (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveOrderedSet (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveSortedSet (aEmptyArray).isEmpty ());

    final boolean [] aValues = { true, true };
    assertEquals (2, createPrimitiveList (aValues).size ());
    assertEquals (1, createPrimitiveSet (aValues).size ());
    assertEquals (1, createPrimitiveOrderedSet (aValues).size ());
    assertEquals (1, createPrimitiveSortedSet (aValues).size ());
  }

  @Test
  public void testNewByteContainer ()
  {
    final byte [] aEmptyArray = {};
    assertTrue (createPrimitiveList (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveSet (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveOrderedSet (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveSortedSet (aEmptyArray).isEmpty ());

    final byte [] aValues = { 3, 3 };
    assertEquals (2, createPrimitiveList (aValues).size ());
    assertEquals (1, createPrimitiveSet (aValues).size ());
    assertEquals (1, createPrimitiveOrderedSet (aValues).size ());
    assertEquals (1, createPrimitiveSortedSet (aValues).size ());
  }

  @Test
  public void testNewCharContainer ()
  {
    final char [] aEmptyArray = {};
    assertTrue (createPrimitiveList (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveSet (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveOrderedSet (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveSortedSet (aEmptyArray).isEmpty ());

    final char [] aValues = { 'x', 'x' };
    assertEquals (2, createPrimitiveList (aValues).size ());
    assertEquals (1, createPrimitiveSet (aValues).size ());
    assertEquals (1, createPrimitiveOrderedSet (aValues).size ());
    assertEquals (1, createPrimitiveSortedSet (aValues).size ());
  }

  @Test
  public void testNewDoubleContainer ()
  {
    final double [] aEmptyArray = {};
    assertTrue (createPrimitiveList (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveSet (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveOrderedSet (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveSortedSet (aEmptyArray).isEmpty ());

    final double [] aValues = { 1.1, 1.1 };
    assertEquals (2, createPrimitiveList (aValues).size ());
    assertEquals (1, createPrimitiveSet (aValues).size ());
    assertEquals (1, createPrimitiveOrderedSet (aValues).size ());
    assertEquals (1, createPrimitiveSortedSet (aValues).size ());
  }

  @Test
  public void testNewFloatContainer ()
  {
    final float [] aEmptyArray = {};
    assertTrue (createPrimitiveList (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveSet (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveOrderedSet (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveSortedSet (aEmptyArray).isEmpty ());

    final float [] aValues = { 3.2f, 3.2f };
    assertEquals (2, createPrimitiveList (aValues).size ());
    assertEquals (1, createPrimitiveSet (aValues).size ());
    assertEquals (1, createPrimitiveOrderedSet (aValues).size ());
    assertEquals (1, createPrimitiveSortedSet (aValues).size ());
  }

  @Test
  public void testNewIntContainer ()
  {
    final int [] aEmptyArray = {};
    assertTrue (createPrimitiveList (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveSet (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveOrderedSet (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveSortedSet (aEmptyArray).isEmpty ());

    final int [] aValues = { 5, 5 };
    assertEquals (2, createPrimitiveList (aValues).size ());
    assertEquals (1, createPrimitiveSet (aValues).size ());
    assertEquals (1, createPrimitiveOrderedSet (aValues).size ());
    assertEquals (1, createPrimitiveSortedSet (aValues).size ());
  }

  @Test
  public void testNewLongContainer ()
  {
    final long [] aEmptyArray = {};
    assertTrue (createPrimitiveList (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveSet (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveOrderedSet (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveSortedSet (aEmptyArray).isEmpty ());

    final long [] aValues = { 17, 17 };
    assertEquals (2, createPrimitiveList (aValues).size ());
    assertEquals (1, createPrimitiveSet (aValues).size ());
    assertEquals (1, createPrimitiveOrderedSet (aValues).size ());
    assertEquals (1, createPrimitiveSortedSet (aValues).size ());
  }

  @Test
  public void testNewShortContainer ()
  {
    final short [] aEmptyArray = {};
    assertTrue (createPrimitiveList (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveSet (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveOrderedSet (aEmptyArray).isEmpty ());
    assertTrue (createPrimitiveSortedSet (aEmptyArray).isEmpty ());

    final short [] aValues = { 5, 5 };
    assertEquals (2, createPrimitiveList (aValues).size ());
    assertEquals (1, createPrimitiveSet (aValues).size ());
    assertEquals (1, createPrimitiveOrderedSet (aValues).size ());
    assertEquals (1, createPrimitiveSortedSet (aValues).size ());
  }
}
