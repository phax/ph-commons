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
package com.helger.commons.array;

import static com.helger.base.array.ArrayHelper.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.junit.Test;

import com.helger.base.CGlobal;
import com.helger.base.array.ArrayHelper;
import com.helger.base.lang.clazz.ClassHelper;
import com.helger.base.mock.CommonsAssert;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.helper.CollectionHelperExt;
import com.helger.commons.string.StringParser;

/**
 * Test class for {@link ArrayHelper}
 *
 * @author Philip Helger
 */
public final class ArrayHelperTest
{
  private static final Integer I1 = Integer.valueOf (1);
  private static final Integer I2 = Integer.valueOf (2);
  private static final Integer I3 = Integer.valueOf (3);
  private static final Integer I4 = Integer.valueOf (4);
  private static final Integer I5 = Integer.valueOf (5);
  private static final Integer I6 = Integer.valueOf (6);

  @Test
  public void testGetComponentClass ()
  {
    assertEquals (String.class, getComponentType (new String [0]));
    try
    {
      getComponentType (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testFirstElement ()
  {
    assertEquals ("d", getFirst ("d", "c", "b", "a"));
    assertNull (getFirst ((String []) null));
    assertNull (getFirst (new String [0]));
  }

  @Test
  public void testLastElement ()
  {
    assertEquals ("a", getLast ("d", "c", "b", "a"));
    assertNull (getLast ((String []) null));
    assertNull (getLast (new String [0]));
  }

  @Test
  public void testIsEmpty ()
  {
    assertTrue (isEmpty ((Object []) null));
    assertTrue (isEmpty (new Boolean [0]));

    assertTrue (isEmpty ((Object []) null));
    assertFalse (isEmpty (new Object [] { "x", Boolean.FALSE }));

    assertTrue (isEmpty ((boolean []) null));
    assertFalse (isEmpty (new boolean [] { true, false }));

    assertTrue (isEmpty ((byte []) null));
    assertFalse (isEmpty (new byte [] { (byte) 17, (byte) 18 }));

    assertTrue (isEmpty ((char []) null));
    assertFalse (isEmpty (new char [] { 'a', 'Z' }));

    assertTrue (isEmpty ((double []) null));
    assertFalse (isEmpty (new double [] { 3.14, 75712.324 }));

    assertTrue (isEmpty ((float []) null));
    assertFalse (isEmpty (new float [] { 3.14f, 75712.324f }));

    assertTrue (isEmpty ((int []) null));
    assertFalse (isEmpty (new int [] { 314, 75712324 }));

    assertTrue (isEmpty ((long []) null));
    assertFalse (isEmpty (new long [] { 314L, 75712324L }));

    assertTrue (isEmpty ((short []) null));
    assertFalse (isEmpty (new short [] { 32, 223 }));
  }

  @Test
  public void testSize ()
  {
    assertEquals (0, getSize ((Object []) null));
    assertEquals (2, getSize (new Object [] { "x", Boolean.FALSE }));

    assertEquals (0, getSize ((boolean []) null));
    assertEquals (2, getSize (new boolean [] { true, false }));

    assertEquals (0, getSize ((byte []) null));
    assertEquals (2, getSize (new byte [] { (byte) 17, (byte) 18 }));

    assertEquals (0, getSize ((char []) null));
    assertEquals (2, getSize (new char [] { 'a', 'Z' }));

    assertEquals (0, getSize ((double []) null));
    assertEquals (2, getSize (new double [] { 3.14, 75712.324 }));

    assertEquals (0, getSize ((float []) null));
    assertEquals (2, getSize (new float [] { 3.14f, 75712.324f }));

    assertEquals (0, getSize ((int []) null));
    assertEquals (2, getSize (new int [] { 314, 75712324 }));

    assertEquals (0, getSize ((long []) null));
    assertEquals (2, getSize (new long [] { 314L, 75712324L }));

    assertEquals (0, getSize ((short []) null));
    assertEquals (2, getSize (new short [] { 32, 223 }));
  }

  @Test
  public void testContains ()
  {
    {
      final String [] x = { "Hallo", "Welt", "aus", "Kopenhagen" };
      assertTrue (contains (x, "Hallo"));
      assertTrue (contains (x, "Kopenhagen"));
      assertFalse (contains (x, "hallo"));
      assertFalse (contains (null, "hallo"));
      assertFalse (contains (new String [0], "hallo"));
    }

    {
      final boolean [] x = { true, true };
      assertTrue (contains (x, true));
      assertFalse (contains (x, false));
      assertFalse (contains ((boolean []) null, false));
    }

    {
      final byte [] x = { 1, 2, 3 };
      assertTrue (contains (x, (byte) 1));
      assertFalse (contains (x, (byte) 4));
      assertFalse (contains ((byte []) null, (byte) 1));
    }

    {
      final char [] x = { 1, 2, 3 };
      assertTrue (contains (x, (char) 1));
      assertFalse (contains (x, (char) 4));
      assertFalse (contains ((char []) null, 'c'));
    }

    {
      final double [] x = { 1, 2, 3 };
      assertTrue (contains (x, 1.0));
      assertFalse (contains (x, 1.1));
      assertFalse (contains ((double []) null, 1.0));
    }

    {
      final float [] x = { 1, 2, 3 };
      assertTrue (contains (x, 1.0F));
      assertFalse (contains (x, 1.1F));
      assertFalse (contains ((float []) null, 1.5f));
    }

    {
      final int [] x = { 1, 2, 3 };
      assertTrue (contains (x, 1));
      assertFalse (contains (x, 4));
      assertFalse (contains ((int []) null, 7));
    }

    {
      final long [] x = { 1, 2, 3 };
      assertTrue (contains (x, 1L));
      assertFalse (contains (x, 4L));
      assertFalse (contains ((long []) null, 7));
    }

    {
      final short [] x = { 1, 2, 3 };
      assertTrue (contains (x, (short) 1));
      assertFalse (contains (x, (short) 4));
      assertFalse (contains ((short []) null, (short) 6));
    }
  }

  @Test
  public void testGetIndex ()
  {
    {
      final String [] x = { "Hallo", "Welt", "aus", "Kopenhagen", "Welt" };
      assertEquals (0, getFirstIndex (x, "Hallo"));
      assertEquals (0, getLastIndex (x, "Hallo"));
      assertEquals (3, getFirstIndex (x, "Kopenhagen"));
      assertEquals (3, getLastIndex (x, "Kopenhagen"));
      assertEquals (1, getFirstIndex (x, "Welt"));
      assertEquals (4, getLastIndex (x, "Welt"));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex (x, "hallo"));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex (x, "hallo"));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex (null, "hallo"));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex (null, "hallo"));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex (new String [0], "hallo"));
    }

    {
      final boolean [] x = { true, true };
      assertEquals (0, getFirstIndex (x, true));
      assertEquals (1, getLastIndex (x, true));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex (x, false));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex (x, false));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex ((boolean []) null, false));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex ((boolean []) null, false));
    }

    {
      final byte [] x = { 1, 2, 3 };
      assertEquals (0, getFirstIndex (x, (byte) 1));
      assertEquals (0, getLastIndex (x, (byte) 1));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex (x, (byte) 4));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex (x, (byte) 4));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex ((byte []) null, (byte) 1));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex ((byte []) null, (byte) 1));
    }

    {
      final char [] x = { 1, 2, 3 };
      assertEquals (0, getFirstIndex (x, (char) 1));
      assertEquals (0, getLastIndex (x, (char) 1));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex (x, (char) 4));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex (x, (char) 4));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex ((char []) null, 'c'));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex ((char []) null, 'c'));
    }

    {
      final double [] x = { 1, 2, 3 };
      assertEquals (0, getFirstIndex (x, 1.0));
      assertEquals (0, getLastIndex (x, 1.0));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex (x, 1.1));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex (x, 1.1));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex ((double []) null, 1.0));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex ((double []) null, 1.0));
    }

    {
      final float [] x = { 1, 2, 3 };
      assertEquals (0, getFirstIndex (x, 1.0F));
      assertEquals (0, getLastIndex (x, 1.0F));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex (x, 1.1F));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex (x, 1.1F));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex ((float []) null, 1.5f));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex ((float []) null, 1.5f));
    }

    {
      final int [] x = { 1, 2, 3 };
      assertEquals (0, getFirstIndex (x, 1));
      assertEquals (0, getLastIndex (x, 1));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex (x, 4));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex (x, 4));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex ((int []) null, 7));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex ((int []) null, 7));
    }

    {
      final long [] x = { 1, 2, 3 };
      assertEquals (0, getFirstIndex (x, 1L));
      assertEquals (0, getLastIndex (x, 1L));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex (x, 4L));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex (x, 4L));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex ((long []) null, 7));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex ((long []) null, 7));
    }

    {
      final short [] x = { 1, 2, 3 };
      assertEquals (0, getFirstIndex (x, (short) 1));
      assertEquals (0, getLastIndex (x, (short) 1));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex (x, (short) 4));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex (x, (short) 4));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex ((short []) null, (short) 6));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex ((short []) null, (short) 6));
    }
  }

  @Test
  public void testGetAllExcept ()
  {
    {
      final String [] x = { "Hallo", "Welt", "aus", "Kopenhagen", "Welt" };
      assertArrayEquals (new String [] { "Hallo", "aus", "Kopenhagen" }, getAllExcept (x, "Welt"));
      assertArrayEquals (new String [] { "aus", "Kopenhagen" }, getAllExcept (x, "Hallo", "Welt"));
      assertArrayEquals (x, getAllExcept (x));
      assertArrayEquals (x, getAllExcept (x, (String []) null));
      assertArrayEquals (x, getAllExcept (x, new String [0]));
    }
  }

  @Test
  public void testArrayEquals ()
  {
    assertTrue (isArrayEquals (null, null));
    assertFalse (isArrayEquals (new boolean [0], null));
    assertFalse (isArrayEquals (null, new boolean [0]));
    assertTrue (isArrayEquals (new boolean [0], new boolean [0]));
    assertFalse (isArrayEquals (new String [0], "Hello"));
    assertFalse (isArrayEquals ("Hello", new String [0]));
    assertFalse (isArrayEquals (new boolean [0], new byte [0]));
    assertFalse (isArrayEquals (new boolean [0], new boolean [1]));
    assertFalse (isArrayEquals (new boolean [1] [0], new boolean [1] [1]));
    assertTrue (isArrayEquals (new boolean [1] [1], new boolean [1] [1]));
    assertFalse (isArrayEquals (new String [] { "Hallo" }, new String [] { "Welt" }));
    assertTrue (isArrayEquals (new String [] { "Hallo" }, new String [] { "Hallo" }));
  }

  @Test
  public void testGetCopy ()
  {
    {
      final Object [] x = { "Any", Integer.valueOf (1), " is ", Double.valueOf (1) };
      assertNull (getCopy ((Object []) null));
      assertTrue (isArrayEquals (x, getCopy (x)));
      assertNull (getCopy ((Object []) null, 1));
      assertArrayEquals (new Object [] { "Any" }, getCopy (x, 1));
      assertNull (getCopy ((Object []) null, 1, 1));
      assertEquals (x[1], getCopy (x, 1, 1)[0]);
    }

    {
      assertNull (getCopy ((boolean []) null));
      final boolean [] x = { true, true, true, false };
      assertTrue (isArrayEquals (x, getCopy (x)));
      assertNull (getCopy ((boolean []) null, 1, 1));
      assertEquals (Boolean.valueOf (x[1]), Boolean.valueOf (getCopy (x, 1, 1)[0]));
      assertNotNull (getCopy (x, 1));
      assertNull (getCopy ((boolean []) null, 1));
    }

    {
      assertNull (getCopy ((byte []) null));
      final byte [] x = { (byte) 17, (byte) 22, (byte) 255, (byte) 0 };
      assertTrue (isArrayEquals (x, getCopy (x)));
      assertNull (getCopy ((byte []) null, 1, 1));
      assertEquals (x[1], getCopy (x, 1, 1)[0]);
      assertNotNull (getCopy (x, 1));
      assertNull (getCopy ((byte []) null, 1));
    }

    {
      assertNull (getCopy ((char []) null));
      final char [] x = { 'a', 'Z', '0', '#' };
      assertTrue (isArrayEquals (x, getCopy (x)));
      assertNull (getCopy ((char []) null, 1, 1));
      assertEquals (x[1], getCopy (x, 1, 1)[0]);
      assertNotNull (getCopy (x, 1));
      assertNull (getCopy ((char []) null, 1));
    }

    {
      assertNull (getCopy ((double []) null));
      final double [] x = { 3.14, 22.45, -34, 255.99 };
      assertTrue (isArrayEquals (x, getCopy (x)));
      assertNull (getCopy ((double []) null, 1, 1));
      assertEquals (x[1], getCopy (x, 1, 1)[0], 0);
      assertNotNull (getCopy (x, 1));
      assertNull (getCopy ((double []) null, 1));
    }

    {
      assertNull (getCopy ((float []) null));
      final float [] x = { 3.14f, 22.45f, -34f, 255.99f };
      assertTrue (isArrayEquals (x, getCopy (x)));
      assertNull (getCopy ((float []) null, 1, 1));
      assertEquals (x[1], getCopy (x, 1, 1)[0], 0);
      assertNotNull (getCopy (x, 1));
      assertNull (getCopy ((float []) null, 1));
    }

    {
      assertNull (getCopy ((int []) null));
      final int [] x = { 314, 2245, -34, 25599 };
      assertTrue (isArrayEquals (x, getCopy (x)));
      assertNull (getCopy ((int []) null, 1, 1));
      assertEquals (x[1], getCopy (x, 1, 1)[0]);
      assertNotNull (getCopy (x, 1));
      assertNull (getCopy ((int []) null, 1));
    }

    {
      assertNull (getCopy ((long []) null));
      final long [] x = { 314, 2245, -34, 25599 };
      assertTrue (isArrayEquals (x, getCopy (x)));
      assertNull (getCopy ((long []) null, 1, 1));
      assertEquals (x[1], getCopy (x, 1, 1)[0]);
      assertNotNull (getCopy (x, 1));
      assertNull (getCopy ((long []) null, 1));
    }

    {
      assertNull (getCopy ((short []) null));
      final short [] x = { 14, 22, -34, 127 };
      assertTrue (isArrayEquals (x, getCopy (x)));
      assertNull (getCopy ((short []) null, 1, 1));
      assertEquals (x[1], getCopy (x, 1, 1)[0]);
      assertNotNull (getCopy (x, 1));
      assertNull (getCopy ((short []) null, 1));
    }
  }

  /**
   * Test for method asObjectArray
   */
  @Test
  public void testAsObjectArray ()
  {
    assertNull (getAsObjectArray (null));
    assertNull (getAsObjectArray (new ArrayList <> ()));
    assertArrayEquals (new Object [] { "Hallo" }, getAsObjectArray (Arrays.asList ("Hallo")));
    assertArrayEquals (new Object [] { "Hallo", "Welt" }, getAsObjectArray (Arrays.asList ("Hallo", "Welt")));
    assertArrayEquals (new Object [] { I1, "Welt" }, getAsObjectArray (Arrays.<Object> asList (I1, "Welt")));
  }

  /**
   * Test method safeGetElement
   */
  @Test
  public void testSafeGetElement ()
  {
    final String [] x = { "a", "b", "c" };
    assertEquals ("a", getSafeElement (x, 0));
    assertEquals ("b", getSafeElement (x, 1));
    assertEquals ("c", getSafeElement (x, 2));
    assertNull (getSafeElement (x, 3));
    assertNull (getSafeElement (x, 12345));
    assertNull (getSafeElement (x, -1));
    assertNull (getSafeElement (new String [0], 0));
    assertNull (getSafeElement ((String []) null, 0));
  }

  /**
   * Test for method concatenate
   */
  @Test
  public void testGetConcatenated ()
  {
    {
      final String [] a = { "a", "b" };
      final String [] b = { "c", "c2" };
      assertArrayEquals (new String [] { "a", "b", "c", "c2" }, getConcatenated (a, b));
      assertArrayEquals (new String [] { "c", "c2", "a", "b" }, getConcatenated (b, a));
      assertArrayEquals (a, getConcatenated (a, (String []) null));
      assertArrayEquals (b, getConcatenated ((String []) null, b));
      assertArrayEquals (new String [] { "a", "b", "c" }, getConcatenated (a, "c"));
      assertArrayEquals (new String [] { "c" }, getConcatenated ((String []) null, "c"));
      assertArrayEquals (new String [] { "c", "a", "b" }, getConcatenated ("c", a));
      assertArrayEquals (new String [] { "c" }, getConcatenated ("c", (String []) null));
    }

    {
      final Integer [] a = { I1, I2 };
      final Integer [] b = { I3, I4 };

      // Generic
      assertArrayEquals (new Integer [] { I1, I2, I3, I4 }, getConcatenated (a, b));
      assertArrayEquals (new Integer [] { I3, I4, I1, I2 }, getConcatenated (b, a));
      assertArrayEquals (a, getConcatenated (a, (Integer []) null));
      assertArrayEquals (b, getConcatenated ((Integer []) null, b));
      assertArrayEquals (new Integer [] { I1, I2, I5 }, getConcatenated (a, I5, Integer.class));
      assertArrayEquals (new Integer [] { I5 }, getConcatenated ((Integer []) null, I5, Integer.class));
      assertArrayEquals (new Integer [] { I6, I1, I2 }, getConcatenated (I6, a, Integer.class));
      assertArrayEquals (new Integer [] { I6 }, getConcatenated (I6, (Integer []) null, Integer.class));
      assertArrayEquals (new Integer [] { I1, I2, null }, getConcatenated (a, (Integer) null, Integer.class));
      assertArrayEquals (new Integer [] { null, I3, I4 }, getConcatenated ((Integer) null, b, Integer.class));
    }

    {
      assertArrayEquals (new boolean [] { false, false, true },
                         getConcatenated (new boolean [] { false, false }, new boolean [] { true }));
      assertArrayEquals (new boolean [] { false, false },
                         getConcatenated (new boolean [] { false, false }, (boolean []) null));
      assertArrayEquals (new boolean [] { true }, getConcatenated ((boolean []) null, new boolean [] { true }));
      assertArrayEquals (new boolean [] { false, false, true },
                         getConcatenated (new boolean [] { false, false }, true));
      assertArrayEquals (new boolean [] { true }, getConcatenated ((boolean []) null, true));
      assertArrayEquals (new boolean [] { false, false, true },
                         getConcatenated (false, new boolean [] { false, true }));
      assertArrayEquals (new boolean [] { false }, getConcatenated (false, (boolean []) null));
    }

    {
      assertArrayEquals (new byte [] { 1, 2, 3, 4 }, getConcatenated (new byte [] { 1, 2 }, new byte [] { 3, 4 }));
      assertArrayEquals (new byte [] { 1, 2 }, getConcatenated (new byte [] { 1, 2 }, (byte []) null));
      assertArrayEquals (new byte [] { 3, 4 }, getConcatenated ((byte []) null, new byte [] { 3, 4 }));
      assertArrayEquals (new byte [] { 1, 2, 3 }, getConcatenated (new byte [] { 1, 2 }, (byte) 3));
      assertArrayEquals (new byte [] { 3 }, getConcatenated ((byte []) null, (byte) 3));
      assertArrayEquals (new byte [] { 1, 2, 3 }, getConcatenated ((byte) 1, new byte [] { 2, 3 }));
      assertArrayEquals (new byte [] { 1 }, getConcatenated ((byte) 1, (byte []) null));
    }

    {
      assertArrayEquals (new char [] { 1, 2, 3, 4 }, getConcatenated (new char [] { 1, 2 }, new char [] { 3, 4 }));
      assertArrayEquals (new char [] { 1, 2 }, getConcatenated (new char [] { 1, 2 }, (char []) null));
      assertArrayEquals (new char [] { 3, 4 }, getConcatenated ((char []) null, new char [] { 3, 4 }));
      assertArrayEquals (new char [] { 1, 2, 3 }, getConcatenated (new char [] { 1, 2 }, (char) 3));
      assertArrayEquals (new char [] { 3 }, getConcatenated ((char []) null, (char) 3));
      assertArrayEquals (new char [] { 1, 2, 3 }, getConcatenated ((char) 1, new char [] { 2, 3 }));
      assertArrayEquals (new char [] { 1 }, getConcatenated ((char) 1, (char []) null));
    }

    {
      assertArrayEquals (new double [] { 1, 2, 3, 4 },
                         getConcatenated (new double [] { 1, 2 }, new double [] { 3, 4 }),
                         CommonsAssert.DOUBLE_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (new double [] { 1, 2 },
                         getConcatenated (new double [] { 1, 2 }, (double []) null),
                         CommonsAssert.DOUBLE_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (new double [] { 3, 4 },
                         getConcatenated ((double []) null, new double [] { 3, 4 }),
                         CommonsAssert.DOUBLE_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (new double [] { 1, 2, 3 },
                         getConcatenated (new double [] { 1, 2 }, 3),
                         CommonsAssert.DOUBLE_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (new double [] { 3 },
                         getConcatenated ((double []) null, 3),
                         CommonsAssert.DOUBLE_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (new double [] { 1, 2, 3 },
                         getConcatenated (1, new double [] { 2, 3 }),
                         CommonsAssert.DOUBLE_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (new double [] { 1 },
                         getConcatenated (1, (double []) null),
                         CommonsAssert.DOUBLE_ALLOWED_ROUNDING_DIFFERENCE);
    }

    {
      assertArrayEquals (new float [] { 1, 2, 3, 4 },
                         getConcatenated (new float [] { 1, 2 }, new float [] { 3, 4 }),
                         CommonsAssert.FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (new float [] { 1, 2 },
                         getConcatenated (new float [] { 1, 2 }, (float []) null),
                         CommonsAssert.FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (new float [] { 3, 4 },
                         getConcatenated ((float []) null, new float [] { 3, 4 }),
                         CommonsAssert.FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (new float [] { 1, 2, 3 },
                         getConcatenated (new float [] { 1, 2 }, 3),
                         CommonsAssert.FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (new float [] { 3 },
                         getConcatenated ((float []) null, 3),
                         CommonsAssert.FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (new float [] { 1, 2, 3 },
                         getConcatenated (1, new float [] { 2, 3 }),
                         CommonsAssert.FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (new float [] { 1 },
                         getConcatenated (1, (float []) null),
                         CommonsAssert.FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    }

    {
      assertArrayEquals (new int [] { 1, 2, 3, 4 }, getConcatenated (new int [] { 1, 2 }, new int [] { 3, 4 }));
      assertArrayEquals (new int [] { 1, 2 }, getConcatenated (new int [] { 1, 2 }, (int []) null));
      assertArrayEquals (new int [] { 3, 4 }, getConcatenated ((int []) null, new int [] { 3, 4 }));
      assertArrayEquals (new int [] { 1, 2, 3 }, getConcatenated (new int [] { 1, 2 }, 3));
      assertArrayEquals (new int [] { 3 }, getConcatenated ((int []) null, 3));
      assertArrayEquals (new int [] { 1, 2, 3 }, getConcatenated (1, new int [] { 2, 3 }));
      assertArrayEquals (new int [] { 1 }, getConcatenated (1, (int []) null));
    }

    {
      assertArrayEquals (new long [] { 1, 2, 3, 4 }, getConcatenated (new long [] { 1, 2 }, new long [] { 3, 4 }));
      assertArrayEquals (new long [] { 1, 2 }, getConcatenated (new long [] { 1, 2 }, (long []) null));
      assertArrayEquals (new long [] { 3, 4 }, getConcatenated ((long []) null, new long [] { 3, 4 }));
      assertArrayEquals (new long [] { 1, 2, 3 }, getConcatenated (new long [] { 1, 2 }, 3L));
      assertArrayEquals (new long [] { 3 }, getConcatenated ((long []) null, 3L));
      assertArrayEquals (new long [] { 1, 2, 3 }, getConcatenated (1L, new long [] { 2, 3 }));
      assertArrayEquals (new long [] { 1 }, getConcatenated (1L, (long []) null));
    }

    {
      assertArrayEquals (new short [] { 1, 2, 3, 4 }, getConcatenated (new short [] { 1, 2 }, new short [] { 3, 4 }));
      assertArrayEquals (new short [] { 1, 2 }, getConcatenated (new short [] { 1, 2 }, (short []) null));
      assertArrayEquals (new short [] { 3, 4 }, getConcatenated ((short []) null, new short [] { 3, 4 }));
      assertArrayEquals (new short [] { 1, 2, 3 }, getConcatenated (new short [] { 1, 2 }, (short) 3));
      assertArrayEquals (new short [] { 3 }, getConcatenated ((short []) null, (short) 3));
      assertArrayEquals (new short [] { 1, 2, 3 }, getConcatenated ((short) 1, new short [] { 2, 3 }));
      assertArrayEquals (new short [] { 1 }, getConcatenated ((short) 1, (short []) null));
    }
  }

  @Test
  public void testNewArrayEmpty ()
  {
    final String [] a = newArray (String.class, 3);
    assertNotNull (a);
    assertTrue (ClassHelper.isArray (a));
    assertEquals (3, a.length);
    for (int i = 0; i < 3; ++i)
      assertNull (a[i]);

    try
    {
      newArray ((Class <?>) null, 5);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      newArray (byte.class, 5);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      newArray (String.class, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testNewArrayFromCollection ()
  {
    String [] x = newArray (CollectionHelperExt.newList ("s1", "s2", "s3"), String.class);
    assertNotNull (x);
    assertEquals (3, x.length);

    x = newArray (new CommonsArrayList <> (), String.class);
    assertNotNull (x);

    x = newArray ((List <String>) null, String.class);
    assertNotNull (x);

    CharSequence [] y = newArray (CollectionHelperExt.newList ("s1", "s2", "s3"), CharSequence.class);
    assertNotNull (y);
    assertEquals (3, y.length);

    y = newArray (CollectionHelperExt.newSet ("s1", "s2", "s3"), CharSequence.class);
    assertNotNull (y);
    assertEquals (3, y.length);
  }

  @Test
  public void testNewArrayFromArray ()
  {
    String [] x = newArray ("s1", "s2", "s3");
    assertNotNull (x);
    assertEquals (3, x.length);

    x = newArray (new String [0]);
    assertNotNull (x);
    assertEquals (0, x.length);
  }

  @Test
  public void testNewArraySizeValue ()
  {
    String [] ret = newArray (1, "6", String.class);
    assertNotNull (ret);
    assertEquals (1, ret.length);
    assertEquals ("6", ret[0]);

    ret = newArray (0, "Hello world", String.class);
    assertNotNull (ret);
    assertEquals (0, ret.length);

    ret = newArray (10, "Hello world", String.class);
    assertNotNull (ret);
    assertEquals (10, ret.length);
    for (final String element : ret)
      assertEquals ("Hello world", element);

    try
    {
      // negative size
      newArray (-1, "6", String.class);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // no class
      newArray (1, "6", null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  /**
   * Test for method getAllExceptFirst
   */
  @Test
  public void testGetAllExceptFirst ()
  {
    // Generic
    {
      final String [] x = { "s1", "s2", "s3" };
      final String [] y = { "s2", "s3" };
      final String [] z = { "s3" };
      assertArrayEquals (y, getAllExceptFirst (x));
      assertArrayEquals (x, getAllExceptFirst (x, 0));
      assertArrayEquals (y, getAllExceptFirst (x, 1));
      assertArrayEquals (z, getAllExceptFirst (x, 2));
      assertNull (getAllExceptFirst (x, 3));
      assertNull (getAllExceptFirst (x, 4));
      assertNull (getAllExceptFirst (x[0]));
      assertNull (getAllExceptFirst (new String [0]));
      assertNull (getAllExceptFirst ((String []) null));
      try
      {
        getAllExceptFirst (x, -1);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }

    // boolean
    {
      final boolean [] x = { true, false, true };
      final boolean [] y = { false, true };
      final boolean [] z = { true };
      assertArrayEquals (y, getAllExceptFirst (x));
      assertArrayEquals (x, getAllExceptFirst (x, 0));
      assertArrayEquals (y, getAllExceptFirst (x, 1));
      assertArrayEquals (z, getAllExceptFirst (x, 2));
      assertNull (getAllExceptFirst (x, 3));
      assertNull (getAllExceptFirst (x, 4));
      assertNull (getAllExceptFirst (new boolean [] { x[0] }));
      assertNull (getAllExceptFirst (new boolean [0]));
      assertNull (getAllExceptFirst ((boolean []) null));
      try
      {
        getAllExceptFirst (x, -1);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }

    // byte
    {
      final byte [] x = { 5, 9, 14 };
      final byte [] y = { 9, 14 };
      final byte [] z = { 14 };
      assertArrayEquals (y, getAllExceptFirst (x));
      assertArrayEquals (x, getAllExceptFirst (x, 0));
      assertArrayEquals (y, getAllExceptFirst (x, 1));
      assertArrayEquals (z, getAllExceptFirst (x, 2));
      assertNull (getAllExceptFirst (x, 3));
      assertNull (getAllExceptFirst (x, 4));
      assertNull (getAllExceptFirst (new byte [] { x[0] }));
      assertNull (getAllExceptFirst (new byte [0]));
      assertNull (getAllExceptFirst ((byte []) null));
      try
      {
        getAllExceptFirst (x, -1);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }

    // char
    {
      final char [] x = { 'a', 'B', 'c' };
      final char [] y = { 'B', 'c' };
      final char [] z = { 'c' };
      assertArrayEquals (y, getAllExceptFirst (x));
      assertArrayEquals (x, getAllExceptFirst (x, 0));
      assertArrayEquals (y, getAllExceptFirst (x, 1));
      assertArrayEquals (z, getAllExceptFirst (x, 2));
      assertNull (getAllExceptFirst (x, 3));
      assertNull (getAllExceptFirst (x, 4));
      assertNull (getAllExceptFirst (new char [] { x[0] }));
      assertNull (getAllExceptFirst (new char [0]));
      assertNull (getAllExceptFirst ((char []) null));
      try
      {
        getAllExceptFirst (x, -1);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }

    // double
    {
      final double [] x = { -1.1, 0, 1.1 };
      final double [] y = { 0, 1.1 };
      final double [] z = { 1.1 };
      assertArrayEquals (y, getAllExceptFirst (x), CommonsAssert.DOUBLE_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (x, getAllExceptFirst (x, 0), CommonsAssert.DOUBLE_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (y, getAllExceptFirst (x, 1), CommonsAssert.DOUBLE_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (z, getAllExceptFirst (x, 2), CommonsAssert.DOUBLE_ALLOWED_ROUNDING_DIFFERENCE);
      assertNull (getAllExceptFirst (x, 3));
      assertNull (getAllExceptFirst (x, 4));
      assertNull (getAllExceptFirst (new double [] { x[0] }));
      assertNull (getAllExceptFirst (new double [0]));
      assertNull (getAllExceptFirst ((double []) null));
      try
      {
        getAllExceptFirst (x, -1);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }

    // float
    {
      final float [] x = { -3.2f, -0.01f, 99.8f };
      final float [] y = { -0.01f, 99.8f };
      final float [] z = { 99.8f };
      assertArrayEquals (y, getAllExceptFirst (x), CommonsAssert.FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (x, getAllExceptFirst (x, 0), CommonsAssert.FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (y, getAllExceptFirst (x, 1), CommonsAssert.FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (z, getAllExceptFirst (x, 2), CommonsAssert.FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
      assertNull (getAllExceptFirst (x, 3));
      assertNull (getAllExceptFirst (x, 4));
      assertNull (getAllExceptFirst (new float [] { x[0] }));
      assertNull (getAllExceptFirst (new float [0]));
      assertNull (getAllExceptFirst ((float []) null));
      try
      {
        getAllExceptFirst (x, -1);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }

    // int
    {
      final int [] x = { -5, 2, 9 };
      final int [] y = { 2, 9 };
      final int [] z = { 9 };
      assertArrayEquals (y, getAllExceptFirst (x));
      assertArrayEquals (x, getAllExceptFirst (x, 0));
      assertArrayEquals (y, getAllExceptFirst (x, 1));
      assertArrayEquals (z, getAllExceptFirst (x, 2));
      assertNull (getAllExceptFirst (x, 3));
      assertNull (getAllExceptFirst (x, 4));
      assertNull (getAllExceptFirst (new int [] { x[0] }));
      assertNull (getAllExceptFirst (new int [0]));
      assertNull (getAllExceptFirst ((int []) null));
      try
      {
        getAllExceptFirst (x, -1);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }

    // long
    {
      final long [] x = { -6, 2, 10 };
      final long [] y = { 2, 10 };
      final long [] z = { 10 };
      assertArrayEquals (y, getAllExceptFirst (x));
      assertArrayEquals (x, getAllExceptFirst (x, 0));
      assertArrayEquals (y, getAllExceptFirst (x, 1));
      assertArrayEquals (z, getAllExceptFirst (x, 2));
      assertNull (getAllExceptFirst (x, 3));
      assertNull (getAllExceptFirst (x, 4));
      assertNull (getAllExceptFirst (new long [] { x[0] }));
      assertNull (getAllExceptFirst (new long [0]));
      assertNull (getAllExceptFirst ((long []) null));
      try
      {
        getAllExceptFirst (x, -1);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }

    // short
    {
      final short [] x = { -100, -10, 1 };
      final short [] y = { -10, 1 };
      final short [] z = { 1 };
      assertArrayEquals (y, getAllExceptFirst (x));
      assertArrayEquals (x, getAllExceptFirst (x, 0));
      assertArrayEquals (y, getAllExceptFirst (x, 1));
      assertArrayEquals (z, getAllExceptFirst (x, 2));
      assertNull (getAllExceptFirst (x, 3));
      assertNull (getAllExceptFirst (x, 4));
      assertNull (getAllExceptFirst (new short [] { x[0] }));
      assertNull (getAllExceptFirst (new short [0]));
      assertNull (getAllExceptFirst ((short []) null));
      try
      {
        getAllExceptFirst (x, -1);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }
  }

  /**
   * Test for method getAllExceptLast
   */
  @Test
  public void testGetAllExceptLast ()
  {
    // Generic
    {
      final String [] x = { "s1", "s2", "s3" };
      final String [] y = { "s1", "s2" };
      final String [] z = { "s1" };
      assertArrayEquals (y, getAllExceptLast (x));
      assertArrayEquals (x, getAllExceptLast (x, 0));
      assertArrayEquals (y, getAllExceptLast (x, 1));
      assertArrayEquals (z, getAllExceptLast (x, 2));
      assertNull (getAllExceptLast (x, 3));
      assertNull (getAllExceptLast (x, 4));
      assertNull (getAllExceptLast (x[0]));
      assertNull (getAllExceptLast (new String [0]));
      assertNull (getAllExceptLast ((String []) null));
      try
      {
        getAllExceptLast (x, -1);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }

    // boolean
    {
      final boolean [] x = { true, false, true };
      final boolean [] y = { true, false };
      final boolean [] z = { true };
      assertArrayEquals (y, getAllExceptLast (x));
      assertArrayEquals (x, getAllExceptLast (x, 0));
      assertArrayEquals (y, getAllExceptLast (x, 1));
      assertArrayEquals (z, getAllExceptLast (x, 2));
      assertNull (getAllExceptLast (x, 3));
      assertNull (getAllExceptLast (x, 4));
      assertNull (getAllExceptLast (new boolean [] { x[0] }));
      assertNull (getAllExceptLast (new boolean [0]));
      assertNull (getAllExceptLast ((boolean []) null));
      try
      {
        getAllExceptLast (x, -1);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }

    // byte
    {
      final byte [] x = { 5, 9, 14 };
      final byte [] y = { 5, 9 };
      final byte [] z = { 5 };
      assertArrayEquals (y, getAllExceptLast (x));
      assertArrayEquals (x, getAllExceptLast (x, 0));
      assertArrayEquals (y, getAllExceptLast (x, 1));
      assertArrayEquals (z, getAllExceptLast (x, 2));
      assertNull (getAllExceptLast (x, 3));
      assertNull (getAllExceptLast (x, 4));
      assertNull (getAllExceptLast (new byte [] { x[0] }));
      assertNull (getAllExceptLast (new byte [0]));
      assertNull (getAllExceptLast ((byte []) null));
      try
      {
        getAllExceptLast (x, -1);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }

    // char
    {
      final char [] x = { 'a', 'B', 'c' };
      final char [] y = { 'a', 'B' };
      final char [] z = { 'a' };
      assertArrayEquals (y, getAllExceptLast (x));
      assertArrayEquals (x, getAllExceptLast (x, 0));
      assertArrayEquals (y, getAllExceptLast (x, 1));
      assertArrayEquals (z, getAllExceptLast (x, 2));
      assertNull (getAllExceptLast (x, 3));
      assertNull (getAllExceptLast (x, 4));
      assertNull (getAllExceptLast (new char [] { x[0] }));
      assertNull (getAllExceptLast (new char [0]));
      assertNull (getAllExceptLast ((char []) null));
      try
      {
        getAllExceptLast (x, -1);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }

    // double
    {
      final double [] x = { -1.1, 0, 1.1 };
      final double [] y = { -1.1, 0 };
      final double [] z = { -1.1 };
      assertArrayEquals (y, getAllExceptLast (x), CommonsAssert.DOUBLE_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (x, getAllExceptLast (x, 0), CommonsAssert.DOUBLE_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (y, getAllExceptLast (x, 1), CommonsAssert.DOUBLE_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (z, getAllExceptLast (x, 2), CommonsAssert.DOUBLE_ALLOWED_ROUNDING_DIFFERENCE);
      assertNull (getAllExceptLast (x, 3));
      assertNull (getAllExceptLast (x, 4));
      assertNull (getAllExceptLast (new double [] { x[0] }));
      assertNull (getAllExceptLast (new double [0]));
      assertNull (getAllExceptLast ((double []) null));
      try
      {
        getAllExceptLast (x, -1);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }

    // float
    {
      final float [] x = { -3.2f, -0.01f, 99.8f };
      final float [] y = { -3.2f, -0.01f };
      final float [] z = { -3.2f };
      assertArrayEquals (y, getAllExceptLast (x), CommonsAssert.FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (x, getAllExceptLast (x, 0), CommonsAssert.FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (y, getAllExceptLast (x, 1), CommonsAssert.FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
      assertArrayEquals (z, getAllExceptLast (x, 2), CommonsAssert.FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
      assertNull (getAllExceptLast (x, 3));
      assertNull (getAllExceptLast (x, 4));
      assertNull (getAllExceptLast (new float [] { x[0] }));
      assertNull (getAllExceptLast (new float [0]));
      assertNull (getAllExceptLast ((float []) null));
      try
      {
        getAllExceptLast (x, -1);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }

    // int
    {
      final int [] x = { -5, 2, 9 };
      final int [] y = { -5, 2 };
      final int [] z = { -5 };
      assertArrayEquals (y, getAllExceptLast (x));
      assertArrayEquals (x, getAllExceptLast (x, 0));
      assertArrayEquals (y, getAllExceptLast (x, 1));
      assertArrayEquals (z, getAllExceptLast (x, 2));
      assertNull (getAllExceptLast (x, 3));
      assertNull (getAllExceptLast (x, 4));
      assertNull (getAllExceptLast (new int [] { x[0] }));
      assertNull (getAllExceptLast (new int [0]));
      assertNull (getAllExceptLast ((int []) null));
      try
      {
        getAllExceptLast (x, -1);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }

    // long
    {
      final long [] x = { -6, 2, 10 };
      final long [] y = { -6, 2 };
      final long [] z = { -6 };
      assertArrayEquals (y, getAllExceptLast (x));
      assertArrayEquals (x, getAllExceptLast (x, 0));
      assertArrayEquals (y, getAllExceptLast (x, 1));
      assertArrayEquals (z, getAllExceptLast (x, 2));
      assertNull (getAllExceptLast (x, 3));
      assertNull (getAllExceptLast (x, 4));
      assertNull (getAllExceptLast (new long [] { x[0] }));
      assertNull (getAllExceptLast (new long [0]));
      assertNull (getAllExceptLast ((long []) null));
      try
      {
        getAllExceptLast (x, -1);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }

    // short
    {
      final short [] x = { -100, -10, 1 };
      final short [] y = { -100, -10 };
      final short [] z = { -100 };
      assertArrayEquals (y, getAllExceptLast (x));
      assertArrayEquals (x, getAllExceptLast (x, 0));
      assertArrayEquals (y, getAllExceptLast (x, 1));
      assertArrayEquals (z, getAllExceptLast (x, 2));
      assertNull (getAllExceptLast (x, 3));
      assertNull (getAllExceptLast (x, 4));
      assertNull (getAllExceptLast (new short [] { x[0] }));
      assertNull (getAllExceptLast (new short [0]));
      assertNull (getAllExceptLast ((short []) null));
      try
      {
        getAllExceptLast (x, -1);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }
  }

  @Test
  public void testContainsNullElement ()
  {
    assertFalse (containsAnyNullElement ((String []) null));
    assertFalse (containsAnyNullElement (new Object [0]));

    assertFalse (containsAnyNullElement (newArray ("a")));
    assertFalse (containsAnyNullElement (newArray ("a", "b", "c")));
    assertTrue (containsAnyNullElement (newArray ((String) null, "a")));
    assertTrue (containsAnyNullElement (newArray ("a", (String) null)));
    assertTrue (containsAnyNullElement (newArray ((String) null)));
  }

  @Test
  public void testNewPrimitiveArray ()
  {
    assertEquals (2, newBooleanArray (true, false).length);
    assertEquals (2, newByteArray ((byte) 5, (byte) 6).length);
    assertEquals (2, newCharArray ((char) 5, (char) 6).length);
    assertEquals (2, newDoubleArray (5.2, 6.1).length);
    assertEquals (2, newFloatArray (5.2f, 6.1f).length);
    assertEquals (2, newIntArray (5, 6).length);
    assertEquals (2, newLongArray (5L, 6L).length);
    assertEquals (2, newShortArray ((short) 5, (short) 6).length);
  }

  @Test
  public void testNewArrayFromCollectionWithMapper ()
  {
    final Function <String, Integer> aMapper = StringParser::parseIntObj;

    Integer [] x = newArrayMapped (CollectionHelperExt.newList ("1", "2", "3"), aMapper, Integer.class);
    assertNotNull (x);
    assertEquals (3, x.length);
    assertEquals (1, x[0].intValue ());
    assertEquals (2, x[1].intValue ());
    assertEquals (3, x[2].intValue ());

    x = newArrayMapped (new CommonsArrayList <> (), aMapper, Integer.class);
    assertNotNull (x);
    assertEquals (0, x.length);

    try
    {
      // List may not be null
      newArrayMapped ((Collection <String>) null, aMapper, Integer.class);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // Converter may not be null
      newArrayMapped (CollectionHelperExt.newList ("1", "2", "3"), null, Integer.class);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // Destination class may not be null
      newArrayMapped (CollectionHelperExt.newList ("1", "2", "3"), aMapper, (Class <Integer>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testNewArrayFromArrayWithMapper ()
  {
    final Function <String, Integer> aMapper = StringParser::parseIntObj;

    Integer [] x = newArrayMapped (new String [] { "1", "2", "3" }, aMapper, Integer.class);
    assertNotNull (x);
    assertEquals (3, x.length);
    assertEquals (1, x[0].intValue ());
    assertEquals (2, x[1].intValue ());
    assertEquals (3, x[2].intValue ());

    x = newArrayMapped (new String [0], aMapper, Integer.class);
    assertNotNull (x);
    assertEquals (0, x.length);

    x = newArrayMapped ((String []) null, aMapper, Integer.class);
    assertNotNull (x);
    assertEquals (0, x.length);

    try
    {
      // Converter may not be null
      newArrayMapped (new String [] { "1", "2", "3" }, null, Integer.class);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // Destination class may not be null
      newArrayMapped (new String [] { "1", "2", "3" }, aMapper, (Class <Integer>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testStartsWith ()
  {
    assertTrue (startsWith (new byte [] { 0, 1, 2, 3 }, new byte [] {}));
    assertTrue (startsWith (new byte [] { 0, 1, 2, 3 }, new byte [] { 0 }));
    assertTrue (startsWith (new byte [] { 0, 1, 2, 3 }, new byte [] { 0, 1 }));
    assertTrue (startsWith (new byte [] { 0, 1, 2, 3 }, new byte [] { 0, 1, 2 }));
    assertTrue (startsWith (new byte [] { 0, 1, 2, 3 }, new byte [] { 0, 1, 2, 3 }));
    assertTrue (startsWith (new byte [] { 0, 1, 2, 3 }, 1, 2, new byte [] { 1, 2 }, 0, 2));
    assertTrue (startsWith (new byte [] { 0, 1, 2, 3 }, 1, 2, new byte [] { 1 }, 0, 1));
    assertFalse (startsWith (new byte [] { 0, 1, 2, 3 }, 1, 2, new byte [] { 2 }, 0, 1));
    assertFalse (startsWith (new byte [] { 0, 1, 2, 3 }, 3, new byte [] { 0, 1, 2, 3 }));
    assertFalse (startsWith (new byte [] { 0, 1, 2, 3 }, new byte [] { 0, 1, 2, 3, 4 }));
    assertFalse (startsWith (new byte [] { 0, 1, 2, 3 }, new byte [] { 1, 2, 3 }));
    assertFalse (startsWith (new byte [] { 0, 1, 2, 3 }, null));

    assertFalse (startsWith (new byte [] {}, new byte [] { 1, 2, 3 }));
    assertFalse (startsWith (new byte [] {}, new byte [] { 1 }));
    assertFalse (startsWith (new byte [] {}, new byte [] {}));
    assertFalse (startsWith (new byte [] {}, null));
  }

  @Test
  public void testForEach ()
  {
    final String [] aArray = { "a", "b", "c" };
    final StringBuilder aSB = new StringBuilder ();
    ArrayHelper.forEach (aArray, x -> aSB.append (x));
    assertEquals ("abc", aSB.toString ());

    aSB.setLength (0);
    ArrayHelper.forEach (aArray, (x, idx) -> aSB.append (x).append (idx));
    assertEquals ("a0b1c2", aSB.toString ());
  }
}
