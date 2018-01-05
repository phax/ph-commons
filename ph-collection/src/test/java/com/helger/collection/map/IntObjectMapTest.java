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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.random.RandomHelper;

public final class IntObjectMapTest
{
  private static final float [] FILL_FACTORS = { 0.25f, 0.5f, 0.75f, 0.9f, 0.99f };

  private static IntObjectMap <String> _makeMap (final int size, final float fillFactor)
  {
    return new IntObjectMap <> (size, fillFactor);
  }

  @Nonnull
  @Nonempty
  private static String _make (final int i)
  {
    return "str" + i;
  }

  private void _testPutHelper (final float fillFactor)
  {
    final IntObjectMap <String> map = _makeMap (100, fillFactor);
    for (int i = 0; i < 100000; ++i)
    {
      assertNull ("Inserting " + i, map.put (i, _make (i)));
      assertEquals (i + 1, map.size ());
      assertEquals (_make (i), map.get (i));
    }
    // now check the final state
    for (int i = 0; i < 100000; ++i)
      assertEquals (_make (i), map.get (i));
  }

  @Test
  public void testPut ()
  {
    for (final float ff : FILL_FACTORS)
      _testPutHelper (ff);
  }

  private void _testPutNegative (final float fillFactor)
  {
    final IntObjectMap <String> map = _makeMap (100, fillFactor);
    for (int i = 0; i < 100000; ++i)
    {
      map.put (-i, _make (-i));
      assertEquals (i + 1, map.size ());
      assertEquals (_make (-i), map.get (-i));
    }
    // now check the final state
    for (int i = 0; i < 100000; ++i)
      assertEquals (_make (-i), map.get (-i));
  }

  @Test
  public void testPutNegative ()
  {
    for (final float ff : FILL_FACTORS)
      _testPutNegative (ff);
  }

  private void _testPutRandom (final float fillFactor)
  {
    final Random aRandom = RandomHelper.getRandom ();
    final int SIZE = 100 * 1000;
    final ICommonsSet <Integer> set = new CommonsHashSet <> (SIZE);
    final int [] vals = new int [SIZE];
    while (set.size () < SIZE)
      set.add (Integer.valueOf (aRandom.nextInt ()));
    int i = 0;
    for (final Integer v : set)
      vals[i++] = v.intValue ();

    final IntObjectMap <String> map = _makeMap (100, fillFactor);
    for (i = 0; i < vals.length; ++i)
    {
      assertNull ("Inserting " + vals[i], map.put (vals[i], _make (vals[i])));
      assertEquals (i + 1, map.size ());
      assertEquals (_make (vals[i]), map.get (vals[i]));
    }
    // now check the final state
    for (i = 0; i < vals.length; ++i)
      assertEquals (_make (vals[i]), map.get (vals[i]));
  }

  @Test
  public void testPutRandom ()
  {
    for (final float ff : FILL_FACTORS)
      _testPutRandom (ff);
  }

  private void _testRemoveHelper (final float fillFactor)
  {
    final IntObjectMap <String> map = _makeMap (100, fillFactor);
    int addCnt = 0;
    int removeCnt = 0;
    for (int i = 0; i < 100000; ++i)
    {
      assertNull (map.put (addCnt, _make (addCnt)));
      addCnt++;
      assertNull ("Failed for addCnt = " + addCnt + ", ff = " + fillFactor, map.put (addCnt, _make (addCnt)));
      addCnt++;
      assertEquals (_make (removeCnt), map.remove (removeCnt));
      removeCnt++;

      // map grows by one element on each iteration
      assertEquals (i + 1, map.size ());
    }
    for (int i = removeCnt; i < addCnt; ++i)
      assertEquals (_make (i), map.get (i));
  }

  @Test
  public void testRemove ()
  {
    for (final float ff : FILL_FACTORS)
      _testRemoveHelper (ff);
  }

  private void _testForEachHelper (final float fillFactor)
  {
    final IntObjectMap <String> map = _makeMap (100, fillFactor);
    for (int i = 0; i <= 10; ++i)
      assertNull (map.put (i, _make (i)));
    assertEquals (11, map.size ());

    final boolean [] aKeysFound = new boolean [map.size ()];
    final boolean [] aValuesFound = new boolean [map.size ()];
    map.forEach ( (k, v) -> {
      aKeysFound[k] = true;
      aValuesFound[Integer.parseInt (v.substring (3))] = true;
    });
    for (final boolean b : aKeysFound)
      assertTrue (b);
    for (final boolean b : aValuesFound)
      assertTrue (b);
  }

  @Test
  public void testForEach ()
  {
    for (final float ff : FILL_FACTORS)
      _testForEachHelper (ff);
  }
}
