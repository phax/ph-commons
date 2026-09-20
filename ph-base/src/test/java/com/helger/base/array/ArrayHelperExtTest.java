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
package com.helger.base.array;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.function.Predicate;

import org.junit.Test;

import com.helger.base.numeric.mutable.MutableInt;

/**
 * Additional test class for class {@link ArrayHelper}.
 *
 * @author Philip Helger
 */
public final class ArrayHelperExtTest
{
  @Test
  public void testIsNotEmptyPrimitives ()
  {
    assertTrue (ArrayHelper.isNotEmpty (new boolean [] { true }));
    assertFalse (ArrayHelper.isNotEmpty (new boolean [0]));
    assertFalse (ArrayHelper.isNotEmpty ((boolean []) null));

    assertTrue (ArrayHelper.isNotEmpty (new byte [] { 1 }));
    assertFalse (ArrayHelper.isNotEmpty (new byte [0]));
    assertFalse (ArrayHelper.isNotEmpty ((byte []) null));

    assertTrue (ArrayHelper.isNotEmpty (new char [] { 'a' }));
    assertFalse (ArrayHelper.isNotEmpty (new char [0]));
    assertFalse (ArrayHelper.isNotEmpty ((char []) null));

    assertTrue (ArrayHelper.isNotEmpty (new double [] { 1 }));
    assertFalse (ArrayHelper.isNotEmpty (new double [0]));
    assertFalse (ArrayHelper.isNotEmpty ((double []) null));

    assertTrue (ArrayHelper.isNotEmpty (new float [] { 1 }));
    assertFalse (ArrayHelper.isNotEmpty (new float [0]));
    assertFalse (ArrayHelper.isNotEmpty ((float []) null));

    assertTrue (ArrayHelper.isNotEmpty (new int [] { 1 }));
    assertFalse (ArrayHelper.isNotEmpty (new int [0]));
    assertFalse (ArrayHelper.isNotEmpty ((int []) null));

    assertTrue (ArrayHelper.isNotEmpty (new long [] { 1 }));
    assertFalse (ArrayHelper.isNotEmpty (new long [0]));
    assertFalse (ArrayHelper.isNotEmpty ((long []) null));

    assertTrue (ArrayHelper.isNotEmpty (new short [] { 1 }));
    assertFalse (ArrayHelper.isNotEmpty (new short [0]));
    assertFalse (ArrayHelper.isNotEmpty ((short []) null));

    assertTrue (ArrayHelper.isNotEmpty (new String [] { "a" }));
    assertFalse (ArrayHelper.isNotEmpty (new String [0]));
    assertFalse (ArrayHelper.isNotEmpty ((String []) null));
  }

  @Test
  public void testGetAllExceptPrimitives ()
  {
    // boolean
    assertArrayEquals (new boolean [] { true, true },
                       ArrayHelper.getAllExcept (new boolean [] { true, false, true }, false));
    assertArrayEquals (new boolean [0], ArrayHelper.getAllExcept (new boolean [] { true }, true));
    assertNull (ArrayHelper.getAllExcept ((boolean []) null, true));
    final boolean [] aBooleans = new boolean [] { true };
    assertSame (aBooleans, ArrayHelper.getAllExcept (aBooleans, new boolean [0]));

    // byte
    assertArrayEquals (new byte [] { 1, 3 }, ArrayHelper.getAllExcept (new byte [] { 1, 2, 3 }, (byte) 2));
    assertNull (ArrayHelper.getAllExcept ((byte []) null, (byte) 2));
    final byte [] aBytes = new byte [] { 1 };
    assertSame (aBytes, ArrayHelper.getAllExcept (aBytes, new byte [0]));

    // char
    assertArrayEquals (new char [] { 'a', 'c' }, ArrayHelper.getAllExcept (new char [] { 'a', 'b', 'c' }, 'b'));
    assertNull (ArrayHelper.getAllExcept ((char []) null, 'b'));
    final char [] aChars = new char [] { 'a' };
    assertSame (aChars, ArrayHelper.getAllExcept (aChars, new char [0]));

    // double
    assertArrayEquals (new double [] { 1, 3 }, ArrayHelper.getAllExcept (new double [] { 1, 2, 3 }, 2), 0.0001);
    assertNull (ArrayHelper.getAllExcept ((double []) null, 2));
    final double [] aDoubles = new double [] { 1 };
    assertSame (aDoubles, ArrayHelper.getAllExcept (aDoubles, new double [0]));

    // float
    assertArrayEquals (new float [] { 1, 3 }, ArrayHelper.getAllExcept (new float [] { 1, 2, 3 }, 2), 0.0001f);
    assertNull (ArrayHelper.getAllExcept ((float []) null, 2));
    final float [] aFloats = new float [] { 1 };
    assertSame (aFloats, ArrayHelper.getAllExcept (aFloats, new float [0]));

    // int
    assertArrayEquals (new int [] { 1, 3 }, ArrayHelper.getAllExcept (new int [] { 1, 2, 3 }, 2));
    assertNull (ArrayHelper.getAllExcept ((int []) null, 2));
    final int [] aInts = new int [] { 1 };
    assertSame (aInts, ArrayHelper.getAllExcept (aInts, new int [0]));

    // long
    assertArrayEquals (new long [] { 1, 3 }, ArrayHelper.getAllExcept (new long [] { 1, 2, 3 }, 2));
    assertNull (ArrayHelper.getAllExcept ((long []) null, 2));
    final long [] aLongs = new long [] { 1 };
    assertSame (aLongs, ArrayHelper.getAllExcept (aLongs, new long [0]));

    // short
    assertArrayEquals (new short [] { 1, 3 }, ArrayHelper.getAllExcept (new short [] { 1, 2, 3 }, (short) 2));
    assertNull (ArrayHelper.getAllExcept ((short []) null, (short) 2));
    final short [] aShorts = new short [] { 1 };
    assertSame (aShorts, ArrayHelper.getAllExcept (aShorts, new short [0]));
  }

  @Test
  public void testStartsWithChar ()
  {
    final char [] aArray = "abcdef".toCharArray ();
    final char [] aSearch = "abc".toCharArray ();

    assertTrue (ArrayHelper.startsWith (aArray, aSearch));
    assertFalse (ArrayHelper.startsWith (aArray, "bcd".toCharArray ()));
    assertFalse (ArrayHelper.startsWith (aArray, (char []) null));
    // An empty search array is a prefix of everything
    assertTrue (ArrayHelper.startsWith (aArray, new char [0]));
    // The search array is longer than the array
    assertFalse (ArrayHelper.startsWith (aSearch, aArray));
    assertTrue (ArrayHelper.startsWith (aArray, aArray));

    // With an array length
    assertTrue (ArrayHelper.startsWith (aArray, 6, aSearch));
    assertFalse (ArrayHelper.startsWith (aArray, 2, aSearch));

    // With a search offset and length
    assertTrue (ArrayHelper.startsWith (aArray, aSearch, 0, 3));
    assertTrue (ArrayHelper.startsWith (aArray, "xabc".toCharArray (), 1, 3));
    assertFalse (ArrayHelper.startsWith (aArray, "xbcd".toCharArray (), 1, 3));

    // With an array length, a search offset and length
    assertTrue (ArrayHelper.startsWith (aArray, 6, "xabc".toCharArray (), 1, 3));
    assertFalse (ArrayHelper.startsWith (aArray, 2, "xabc".toCharArray (), 1, 3));

    // With an array offset and length
    assertTrue (ArrayHelper.startsWith (aArray, 0, 6, "xabc".toCharArray (), 1, 3));
    assertTrue (ArrayHelper.startsWith (aArray, 1, 5, "xbcd".toCharArray (), 1, 3));
    assertFalse (ArrayHelper.startsWith (aArray, 1, 5, "xabc".toCharArray (), 1, 3));
    assertFalse (ArrayHelper.startsWith (aArray, 0, 2, "xabc".toCharArray (), 1, 3));
  }

  @Test
  public void testStartsWithByte ()
  {
    final byte [] aArray = new byte [] { 1, 2, 3, 4 };

    assertTrue (ArrayHelper.startsWith (aArray, new byte [] { 1, 2 }));
    assertTrue (ArrayHelper.startsWith (aArray, 4, new byte [] { 1, 2 }));
    assertFalse (ArrayHelper.startsWith (aArray, 1, new byte [] { 1, 2 }));

    assertTrue (ArrayHelper.startsWith (aArray, new byte [] { 9, 1, 2 }, 1, 2));
    assertFalse (ArrayHelper.startsWith (aArray, new byte [] { 9, 2, 3 }, 1, 2));

    assertTrue (ArrayHelper.startsWith (aArray, 4, new byte [] { 9, 1, 2 }, 1, 2));
    assertFalse (ArrayHelper.startsWith (aArray, 1, new byte [] { 9, 1, 2 }, 1, 2));
  }

  @Test
  public void testFindAndCount ()
  {
    final String [] aArray = new String [] { "a", "bb", "ccc" };
    final Predicate <String> aFilter = x -> x.length () > 1;

    assertEquals ("bb", ArrayHelper.findFirst (aArray, aFilter));
    assertNull (ArrayHelper.findFirst (aArray, x -> false));
    assertEquals ("a", ArrayHelper.findFirst (aArray, (Predicate <String>) null));
    assertNull (ArrayHelper.findFirst ((String []) null, aFilter));

    assertEquals ("2", ArrayHelper.findFirstMapped (aArray, aFilter, x -> Integer.toString (x.length ())));
    assertNull (ArrayHelper.findFirstMapped (aArray, x -> false, x -> Integer.toString (x.length ())));
    assertEquals ("x", ArrayHelper.findFirstMapped (aArray, x -> false, x -> Integer.toString (x.length ()), "x"));
    assertEquals ("2", ArrayHelper.findFirstMapped (aArray, aFilter, x -> Integer.toString (x.length ()), "x"));
    assertEquals ("1",
                  ArrayHelper.findFirstMapped (aArray,
                                               (Predicate <String>) null,
                                               x -> Integer.toString (x.length ()),
                                               "x"));
    assertEquals ("x",
                  ArrayHelper.findFirstMapped ((String []) null,
                                               aFilter,
                                               x -> Integer.toString (x.length ()),
                                               "x"));

    assertEquals (2, ArrayHelper.getCount (aArray, aFilter));
    assertEquals (3, ArrayHelper.getCount (aArray, (Predicate <String>) null));
    assertEquals (0, ArrayHelper.getCount ((String []) null, aFilter));

    assertTrue (ArrayHelper.containsAny (aArray, aFilter));
    assertFalse (ArrayHelper.containsAny (aArray, x -> false));
    assertTrue (ArrayHelper.containsAny (aArray, (Predicate <String>) null));
    assertFalse (ArrayHelper.containsAny ((String []) null, aFilter));
    assertFalse (ArrayHelper.containsAny (new String [0], (Predicate <String>) null));
  }

  @Test
  public void testForEachWithFilter ()
  {
    final String [] aArray = new String [] { "a", "bb", "ccc" };

    final MutableInt aCount = new MutableInt (0);
    ArrayHelper.forEach (aArray, x -> x.length () > 1, x -> aCount.inc ());
    assertEquals (2, aCount.intValue ());

    aCount.set (0);
    ArrayHelper.forEach (aArray, (Predicate <String>) null, x -> aCount.inc ());
    assertEquals (3, aCount.intValue ());

    aCount.set (0);
    ArrayHelper.forEach ((String []) null, x -> true, x -> aCount.inc ());
    assertEquals (0, aCount.intValue ());
  }

  @Test
  public void testContainsOnlyNullElements ()
  {
    assertTrue (ArrayHelper.containsOnlyNullElements (new String [] { null, null }));
    assertFalse (ArrayHelper.containsOnlyNullElements (new String [] { null, "a" }));
    assertFalse (ArrayHelper.containsOnlyNullElements (new String [] { "a" }));
    assertFalse (ArrayHelper.containsOnlyNullElements (new String [0]));
    assertFalse (ArrayHelper.containsOnlyNullElements ((String []) null));
  }

  @Test
  public void testGetConcatenatedCharArrayArray ()
  {
    final char [] [] aHead = new char [] [] { "ab".toCharArray () };
    final char [] [] aTail = new char [] [] { "cd".toCharArray (), "ef".toCharArray () };

    final char [] [] aConcatenated = ArrayHelper.getConcatenated (aHead, aTail);
    assertEquals (3, aConcatenated.length);
    assertArrayEquals ("ab".toCharArray (), aConcatenated[0]);
    assertArrayEquals ("ef".toCharArray (), aConcatenated[2]);

    // An empty head or tail returns a copy of the other one
    assertArrayEquals (aTail, ArrayHelper.getConcatenated ((char [] []) null, aTail));
    assertArrayEquals (aHead, ArrayHelper.getConcatenated (aHead, (char [] []) null));
  }
}
