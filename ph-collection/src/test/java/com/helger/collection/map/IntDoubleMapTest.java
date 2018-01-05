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
package com.helger.collection.map;

import static org.junit.Assert.assertEquals;

import java.util.Random;
import java.util.Set;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.random.RandomHelper;

public final class IntDoubleMapTest
{
  private static final double DELTA = 0.0001;
  private static final float [] FILL_FACTORS = { 0.25f, 0.5f, 0.75f, 0.9f, 0.99f };

  private static IntDoubleMap _makeMap (final int size, final float fillFactor)
  {
    return new IntDoubleMap (size, fillFactor);
  }

  @Test
  public void testPut ()
  {
    for (final float ff : FILL_FACTORS)
      _testPutHelper (ff);
  }

  private void _testPutHelper (final float fillFactor)
  {
    final IntDoubleMap map = _makeMap (100, fillFactor);
    for (int i = 0; i < 100000; ++i)
    {
      assertEquals ("Inserting " + i, IntDoubleMap.NO_VALUE, map.put (i, i), DELTA);
      assertEquals (i + 1, map.size ());
      assertEquals (i, map.get (i), DELTA);
    }
    // now check the final state
    for (int i = 0; i < 100000; ++i)
      assertEquals (i, map.get (i), DELTA);
  }

  @Test
  public void testPutNegative ()
  {
    for (final float ff : FILL_FACTORS)
      _testPutNegative (ff);
  }

  private void _testPutNegative (final float fillFactor)
  {
    final IntDoubleMap map = _makeMap (100, fillFactor);
    for (int i = 0; i < 100000; ++i)
    {
      map.put (-i, -i);
      assertEquals (i + 1, map.size ());
      assertEquals (-i, map.get (-i), DELTA);
    }
    // now check the final state
    for (int i = 0; i < 100000; ++i)
      assertEquals (-i, map.get (-i), DELTA);
  }

  @Test
  public void testPutRandom ()
  {
    for (final float ff : FILL_FACTORS)
      _testPutRandom (ff);
  }

  private void _testPutRandom (final float fillFactor)
  {
    final Random aRandom = RandomHelper.getRandom ();
    final int SIZE = 100 * 1000;
    final Set <Integer> set = new CommonsHashSet <> (SIZE);
    final int [] vals = new int [SIZE];
    while (set.size () < SIZE)
      set.add (Integer.valueOf (aRandom.nextInt ()));
    int i = 0;
    for (final Integer v : set)
      vals[i++] = v.intValue ();

    final IntDoubleMap map = _makeMap (100, fillFactor);
    for (i = 0; i < vals.length; ++i)
    {
      assertEquals ("Inserting " + vals[i], IntDoubleMap.NO_VALUE, map.put (vals[i], vals[i]), DELTA);
      assertEquals (i + 1, map.size ());
      assertEquals (vals[i], map.get (vals[i]), DELTA);
    }
    // now check the final state
    for (i = 0; i < vals.length; ++i)
      assertEquals (vals[i], map.get (vals[i]), DELTA);
  }

  @Test
  public void testRemove ()
  {
    for (final float ff : FILL_FACTORS)
      _testRemoveHelper (ff);
  }

  private void _testRemoveHelper (final float fillFactor)
  {
    final IntDoubleMap map = _makeMap (100, fillFactor);
    int addCnt = 0;
    int removeCnt = 0;
    for (int i = 0; i < 100000; ++i)
    {
      assertEquals (IntDoubleMap.NO_VALUE, map.put (addCnt, addCnt), DELTA);
      addCnt++;
      assertEquals ("Failed for addCnt = " + addCnt + ", ff = " + fillFactor,
                    IntDoubleMap.NO_VALUE,
                    map.put (addCnt, addCnt),
                    DELTA);
      addCnt++;
      assertEquals (removeCnt, map.remove (removeCnt), DELTA);
      removeCnt++;

      // map grows by one element on each iteration
      assertEquals (i + 1, map.size ());
    }
    for (int i = removeCnt; i < addCnt; ++i)
      assertEquals (i, map.get (i), DELTA);
  }
}
