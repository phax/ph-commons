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
package com.helger.collection.map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Model based property tests for the primitive maps {@link IntIntMap}, {@link IntObjectMap},
 * {@link IntDoubleMap} and {@link IntFloatMap}: a generated sequence of operations is applied to
 * the map and to a {@link HashMap} as the model, and all results must be identical.
 *
 * @author Philip Helger
 */
public final class PrimitiveMapPropertyTest
{
  /** Mostly a small key range (many collisions and re-uses, including the special key 0) */
  private static final IGenerator <Integer> KEYS = Generators.oneOf (Generators.ints (-20, 20), Generators.ints ());

  /**
   * One operation: the operation code (0 = put, 1 = remove, 2 = get, 3 = containsKey, 4 =
   * computeIfAbsent, 5 = clear), the key and a value.
   */
  private static final IGenerator <int []> OPERATIONS = Generators.ints (0, 5)
                                                                  .flatMap (aOp -> KEYS.flatMap (aKey -> Generators.ints (-3,
                                                                                                                          3)
                                                                                                                    .map (aValue -> new int [] { aOp.intValue (),
                                                                                                                                                 aKey.intValue (),
                                                                                                                                                 aValue.intValue () })));

  /** Mostly no "clear", so that the maps grow */
  private static final IGenerator <List <int []>> SEQUENCES = Generators.lists (OPERATIONS.filter (aOp -> aOp[0] != 5 ||
                                                                                                          aOp[2] == 0),
                                                                                0,
                                                                                200);

  private static int [] _getSortedKeys (final Map <Integer, ?> aModel)
  {
    final int [] ret = new int [aModel.size ()];
    int i = 0;
    for (final Integer aKey : aModel.keySet ())
      ret[i++] = aKey.intValue ();
    Arrays.sort (ret);
    return ret;
  }

  private static int [] _sorted (final int [] aArray)
  {
    final int [] ret = aArray.clone ();
    Arrays.sort (ret);
    return ret;
  }

  @Test
  public void testIntIntMap ()
  {
    final int NO = IntIntMap.NO_VALUE;
    new PropertyChecker ().setTries (500).forAll (SEQUENCES, aOps -> {
      final IntIntMap aMap = new IntIntMap (2);
      final Map <Integer, Integer> aModel = new HashMap <> ();
      int nStep = 0;
      for (final int [] aOp : aOps)
      {
        final Integer aKey = Integer.valueOf (aOp[1]);
        final String sMsg = "Step " + nStep++ + ": op " + aOp[0] + " key " + aOp[1] + " value " + aOp[2];
        switch (aOp[0])
        {
          case 0:
          {
            final Integer aPrev = aModel.put (aKey, Integer.valueOf (aOp[2]));
            assertEquals (sMsg, aPrev == null ? NO : aPrev.intValue (), aMap.put (aOp[1], aOp[2]));
            break;
          }
          case 1:
          {
            final Integer aPrev = aModel.remove (aKey);
            assertEquals (sMsg, aPrev == null ? NO : aPrev.intValue (), aMap.remove (aOp[1]));
            break;
          }
          case 2:
            assertEquals (sMsg, aModel.getOrDefault (aKey, Integer.valueOf (NO)).intValue (), aMap.get (aOp[1]));
            break;
          case 3:
            assertEquals (sMsg, aModel.containsKey (aKey), aMap.containsKey (aOp[1]));
            break;
          case 4:
          {
            // A stored NO_VALUE counts as absent, and a computed NO_VALUE is not stored
            final int nCurrent = aModel.getOrDefault (aKey, Integer.valueOf (NO)).intValue ();
            final int nExpected;
            if (nCurrent == NO)
            {
              nExpected = aOp[2];
              if (nExpected != NO)
                aModel.put (aKey, Integer.valueOf (nExpected));
            }
            else
              nExpected = nCurrent;
            assertEquals (sMsg, nExpected, aMap.computeIfAbsent (aOp[1], k -> aOp[2]));
            break;
          }
          default:
            aModel.clear ();
            aMap.clear ();
        }
        assertEquals (sMsg, aModel.size (), aMap.size ());
      }
      assertArrayEquals (_getSortedKeys (aModel), _sorted (aMap.getAllKeys ()));
      final Map <Integer, Integer> aForEach = new HashMap <> ();
      aMap.forEach ( (k, v) -> aForEach.put (Integer.valueOf (k), Integer.valueOf (v)));
      assertEquals (aModel, aForEach);
      final IntIntMap aClone = aMap.getClone ();
      for (final Map.Entry <Integer, Integer> aEntry : aModel.entrySet ())
        assertEquals (aEntry.getValue ().intValue (), aClone.get (aEntry.getKey ().intValue ()));
    });
  }

  @Test
  public void testIntObjectMap ()
  {
    new PropertyChecker ().setTries (500).forAll (SEQUENCES, aOps -> {
      final IntObjectMap <String> aMap = new IntObjectMap <> (2);
      final Map <Integer, String> aModel = new HashMap <> ();
      int nStep = 0;
      for (final int [] aOp : aOps)
      {
        final Integer aKey = Integer.valueOf (aOp[1]);
        // Value -3 stands for null
        final String sValue = aOp[2] == -3 ? null : "v" + aOp[2];
        final String sMsg = "Step " + nStep++ + ": op " + aOp[0] + " key " + aOp[1] + " value " + sValue;
        switch (aOp[0])
        {
          case 0:
            assertEquals (sMsg, aModel.put (aKey, sValue), aMap.put (aOp[1], sValue));
            break;
          case 1:
            assertEquals (sMsg, aModel.remove (aKey), aMap.remove (aOp[1]));
            break;
          case 2:
            assertEquals (sMsg, aModel.get (aKey), aMap.get (aOp[1]));
            break;
          case 3:
            assertEquals (sMsg, aModel.containsKey (aKey), aMap.containsKey (aOp[1]));
            break;
          case 4:
            assertEquals (sMsg, aModel.computeIfAbsent (aKey, k -> sValue), aMap.computeIfAbsent (aOp[1], k -> sValue));
            break;
          default:
            aModel.clear ();
            aMap.clear ();
        }
        assertEquals (sMsg, aModel.size (), aMap.size ());
      }
      assertArrayEquals (_getSortedKeys (aModel), _sorted (aMap.getAllKeys ()));
      final Map <Integer, String> aForEach = new HashMap <> ();
      aMap.forEach ( (k, v) -> aForEach.put (Integer.valueOf (k), v));
      assertEquals (aModel, aForEach);
    });
  }

  @Test
  public void testIntDoubleMap ()
  {
    final double NO = IntDoubleMap.NO_VALUE;
    new PropertyChecker ().setTries (500).forAll (SEQUENCES, aOps -> {
      final IntDoubleMap aMap = new IntDoubleMap (2);
      final Map <Integer, Double> aModel = new HashMap <> ();
      int nStep = 0;
      for (final int [] aOp : aOps)
      {
        final Integer aKey = Integer.valueOf (aOp[1]);
        // Value -3 stands for the special NO_VALUE
        final double dValue = aOp[2] == -3 ? NO : aOp[2] / 2.0;
        final String sMsg = "Step " + nStep++ + ": op " + aOp[0] + " key " + aOp[1] + " value " + dValue;
        switch (aOp[0])
        {
          case 0:
          {
            final Double aPrev = aModel.put (aKey, Double.valueOf (dValue));
            assertEquals (sMsg, aPrev == null ? NO : aPrev.doubleValue (), aMap.put (aOp[1], dValue), 0);
            break;
          }
          case 1:
          {
            final Double aPrev = aModel.remove (aKey);
            assertEquals (sMsg, aPrev == null ? NO : aPrev.doubleValue (), aMap.remove (aOp[1]), 0);
            break;
          }
          case 2:
            assertEquals (sMsg, aModel.getOrDefault (aKey, Double.valueOf (NO)).doubleValue (), aMap.get (aOp[1]), 0);
            break;
          case 3:
            assertEquals (sMsg, aModel.containsKey (aKey), aMap.containsKey (aOp[1]));
            break;
          default:
            aModel.clear ();
            aMap.clear ();
        }
        assertEquals (sMsg, aModel.size (), aMap.size ());
      }
      assertArrayEquals (_getSortedKeys (aModel), _sorted (aMap.getAllKeys ()));
      final Map <Integer, Double> aForEach = new HashMap <> ();
      aMap.forEach ( (k, v) -> aForEach.put (Integer.valueOf (k), Double.valueOf (v)));
      assertEquals (aModel, aForEach);
    });
  }

  @Test
  public void testIntFloatMap ()
  {
    final float NO = IntFloatMap.NO_VALUE;
    new PropertyChecker ().setTries (500).forAll (SEQUENCES, aOps -> {
      final IntFloatMap aMap = new IntFloatMap (2);
      final Map <Integer, Float> aModel = new HashMap <> ();
      int nStep = 0;
      for (final int [] aOp : aOps)
      {
        final Integer aKey = Integer.valueOf (aOp[1]);
        // Value -3 stands for the special NO_VALUE
        final float fValue = aOp[2] == -3 ? NO : aOp[2] / 2.0f;
        final String sMsg = "Step " + nStep++ + ": op " + aOp[0] + " key " + aOp[1] + " value " + fValue;
        switch (aOp[0])
        {
          case 0:
          {
            final Float aPrev = aModel.put (aKey, Float.valueOf (fValue));
            assertEquals (sMsg, aPrev == null ? NO : aPrev.floatValue (), aMap.put (aOp[1], fValue), 0);
            break;
          }
          case 1:
          {
            final Float aPrev = aModel.remove (aKey);
            assertEquals (sMsg, aPrev == null ? NO : aPrev.floatValue (), aMap.remove (aOp[1]), 0);
            break;
          }
          case 2:
            assertEquals (sMsg, aModel.getOrDefault (aKey, Float.valueOf (NO)).floatValue (), aMap.get (aOp[1]), 0);
            break;
          case 3:
            assertEquals (sMsg, aModel.containsKey (aKey), aMap.containsKey (aOp[1]));
            break;
          default:
            aModel.clear ();
            aMap.clear ();
        }
        assertEquals (sMsg, aModel.size (), aMap.size ());
      }
      assertArrayEquals (_getSortedKeys (aModel), _sorted (aMap.getAllKeys ()));
      final Map <Integer, Float> aForEach = new HashMap <> ();
      aMap.forEach ( (k, v) -> aForEach.put (Integer.valueOf (k), Float.valueOf (v)));
      assertEquals (aModel, aForEach);
    });
  }
}
