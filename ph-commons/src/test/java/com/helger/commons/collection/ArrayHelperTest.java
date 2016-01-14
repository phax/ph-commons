/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import static com.helger.commons.collection.ArrayHelper.contains;
import static com.helger.commons.collection.ArrayHelper.containsAnyNullElement;
import static com.helger.commons.collection.ArrayHelper.getAllExcept;
import static com.helger.commons.collection.ArrayHelper.getAllExceptFirst;
import static com.helger.commons.collection.ArrayHelper.getAllExceptLast;
import static com.helger.commons.collection.ArrayHelper.getAsObjectArray;
import static com.helger.commons.collection.ArrayHelper.getComponentType;
import static com.helger.commons.collection.ArrayHelper.getConcatenated;
import static com.helger.commons.collection.ArrayHelper.getCopy;
import static com.helger.commons.collection.ArrayHelper.getFirst;
import static com.helger.commons.collection.ArrayHelper.getFirstIndex;
import static com.helger.commons.collection.ArrayHelper.getLast;
import static com.helger.commons.collection.ArrayHelper.getLastIndex;
import static com.helger.commons.collection.ArrayHelper.getSafeElement;
import static com.helger.commons.collection.ArrayHelper.getSize;
import static com.helger.commons.collection.ArrayHelper.isArray;
import static com.helger.commons.collection.ArrayHelper.isArrayEquals;
import static com.helger.commons.collection.ArrayHelper.isEmpty;
import static com.helger.commons.collection.ArrayHelper.newArray;
import static com.helger.commons.collection.ArrayHelper.newBooleanArray;
import static com.helger.commons.collection.ArrayHelper.newByteArray;
import static com.helger.commons.collection.ArrayHelper.newCharArray;
import static com.helger.commons.collection.ArrayHelper.newDoubleArray;
import static com.helger.commons.collection.ArrayHelper.newFloatArray;
import static com.helger.commons.collection.ArrayHelper.newIntArray;
import static com.helger.commons.collection.ArrayHelper.newLongArray;
import static com.helger.commons.collection.ArrayHelper.newShortArray;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.helger.commons.CGlobal;
import com.helger.commons.mock.AbstractCommonsTestCase;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for {@link ArrayHelper}
 *
 * @author Philip Helger
 */
@SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
public final class ArrayHelperTest extends AbstractCommonsTestCase
{
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
      final String [] x = new String [] { "Hallo", "Welt", "aus", "Kopenhagen" };
      assertTrue (contains (x, "Hallo"));
      assertTrue (contains (x, "Kopenhagen"));
      assertFalse (contains (x, "hallo"));
      assertFalse (contains (null, "hallo"));
      assertFalse (contains (new String [0], "hallo"));
    }

    {
      final boolean [] x = new boolean [] { true, true };
      assertTrue (contains (x, true));
      assertFalse (contains (x, false));
      assertFalse (contains ((boolean []) null, false));
    }

    {
      final byte [] x = new byte [] { 1, 2, 3 };
      assertTrue (contains (x, (byte) 1));
      assertFalse (contains (x, (byte) 4));
      assertFalse (contains ((byte []) null, (byte) 1));
    }

    {
      final char [] x = new char [] { 1, 2, 3 };
      assertTrue (contains (x, (char) 1));
      assertFalse (contains (x, (char) 4));
      assertFalse (contains ((char []) null, 'c'));
    }

    {
      final double [] x = new double [] { 1, 2, 3 };
      assertTrue (contains (x, 1.0));
      assertFalse (contains (x, 1.1));
      assertFalse (contains ((double []) null, 1.0));
    }

    {
      final float [] x = new float [] { 1, 2, 3 };
      assertTrue (contains (x, 1.0F));
      assertFalse (contains (x, 1.1F));
      assertFalse (contains ((float []) null, 1.5f));
    }

    {
      final int [] x = new int [] { 1, 2, 3 };
      assertTrue (contains (x, 1));
      assertFalse (contains (x, 4));
      assertFalse (contains ((int []) null, 7));
    }

    {
      final long [] x = new long [] { 1, 2, 3 };
      assertTrue (contains (x, 1L));
      assertFalse (contains (x, 4L));
      assertFalse (contains ((long []) null, 7));
    }

    {
      final short [] x = new short [] { 1, 2, 3 };
      assertTrue (contains (x, (short) 1));
      assertFalse (contains (x, (short) 4));
      assertFalse (contains ((short []) null, (short) 6));
    }
  }

  @Test
  public void testGetIndex ()
  {
    {
      final String [] x = new String [] { "Hallo", "Welt", "aus", "Kopenhagen", "Welt" };
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
      final boolean [] x = new boolean [] { true, true };
      assertEquals (0, getFirstIndex (x, true));
      assertEquals (1, getLastIndex (x, true));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex (x, false));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex (x, false));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex ((boolean []) null, false));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex ((boolean []) null, false));
    }

    {
      final byte [] x = new byte [] { 1, 2, 3 };
      assertEquals (0, getFirstIndex (x, (byte) 1));
      assertEquals (0, getLastIndex (x, (byte) 1));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex (x, (byte) 4));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex (x, (byte) 4));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex ((byte []) null, (byte) 1));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex ((byte []) null, (byte) 1));
    }

    {
      final char [] x = new char [] { 1, 2, 3 };
      assertEquals (0, getFirstIndex (x, (char) 1));
      assertEquals (0, getLastIndex (x, (char) 1));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex (x, (char) 4));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex (x, (char) 4));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex ((char []) null, 'c'));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex ((char []) null, 'c'));
    }

    {
      final double [] x = new double [] { 1, 2, 3 };
      assertEquals (0, getFirstIndex (x, 1.0));
      assertEquals (0, getLastIndex (x, 1.0));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex (x, 1.1));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex (x, 1.1));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex ((double []) null, 1.0));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex ((double []) null, 1.0));
    }

    {
      final float [] x = new float [] { 1, 2, 3 };
      assertEquals (0, getFirstIndex (x, 1.0F));
      assertEquals (0, getLastIndex (x, 1.0F));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex (x, 1.1F));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex (x, 1.1F));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex ((float []) null, 1.5f));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex ((float []) null, 1.5f));
    }

    {
      final int [] x = new int [] { 1, 2, 3 };
      assertEquals (0, getFirstIndex (x, 1));
      assertEquals (0, getLastIndex (x, 1));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex (x, 4));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex (x, 4));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex ((int []) null, 7));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex ((int []) null, 7));
    }

    {
      final long [] x = new long [] { 1, 2, 3 };
      assertEquals (0, getFirstIndex (x, 1L));
      assertEquals (0, getLastIndex (x, 1L));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex (x, 4L));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex (x, 4L));
      assertEquals (CGlobal.ILLEGAL_UINT, getFirstIndex ((long []) null, 7));
      assertEquals (CGlobal.ILLEGAL_UINT, getLastIndex ((long []) null, 7));
    }

    {
      final short [] x = new short [] { 1, 2, 3 };
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
      final String [] x = new String [] { "Hallo", "Welt", "aus", "Kopenhagen", "Welt" };
      assertArrayEquals (new String [] { "Hallo", "aus", "Kopenhagen" }, getAllExcept (x, "Welt"));
      assertArrayEquals (new String [] { "aus", "Kopenhagen" }, getAllExcept (x, "Hallo", "Welt"));
      assertArrayEquals (x, getAllExcept (x));
      assertArrayEquals (x, getAllExcept (x, (String []) null));
      assertArrayEquals (x, getAllExcept (x, new String [0]));
    }
  }

  @Test
  public void testIsArray ()
  {
    assertTrue (isArray (new String [] { "Hallo" }));
    assertTrue (isArray (new String [0]));
    assertTrue (isArray (new boolean [1]));
    assertTrue (isArray (new boolean [0]));
    assertFalse (isArray ((boolean []) null));
    assertFalse (isArray ((String) null));
    assertFalse (isArray (Boolean.TRUE));
    assertFalse (isArray ("Hi there"));
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
      final Object [] x = new Object [] { "Any", Integer.valueOf (1), " is ", Double.valueOf (1) };
      assertNull (getCopy ((Object []) null));
      assertTrue (isArrayEquals (x, getCopy (x)));
      assertNull (getCopy ((Object []) null, 1));
      assertArrayEquals (new Object [] { "Any" }, getCopy (x, 1));
      assertNull (getCopy ((Object []) null, 1, 1));
      assertEquals (x[1], getCopy (x, 1, 1)[0]);
    }

    {
      assertNull (getCopy ((boolean []) null));
      final boolean [] x = new boolean [] { true, true, true, false };
      assertTrue (isArrayEquals (x, getCopy (x)));
      assertNull (getCopy ((boolean []) null, 1, 1));
      assertEquals (Boolean.valueOf (x[1]), Boolean.valueOf (getCopy (x, 1, 1)[0]));
      assertNotNull (getCopy (x, 1));
      assertNull (getCopy ((boolean []) null, 1));
    }

    {
      assertNull (getCopy ((byte []) null));
      final byte [] x = new byte [] { (byte) 17, (byte) 22, (byte) 255, (byte) 0 };
      assertTrue (isArrayEquals (x, getCopy (x)));
      assertNull (getCopy ((byte []) null, 1, 1));
      assertEquals (x[1], getCopy (x, 1, 1)[0]);
      assertNotNull (getCopy (x, 1));
      assertNull (getCopy ((byte []) null, 1));
    }

    {
      assertNull (getCopy ((char []) null));
      final char [] x = new char [] { 'a', 'Z', '0', '#' };
      assertTrue (isArrayEquals (x, getCopy (x)));
      assertNull (getCopy ((char []) null, 1, 1));
      assertEquals (x[1], getCopy (x, 1, 1)[0]);
      assertNotNull (getCopy (x, 1));
      assertNull (getCopy ((char []) null, 1));
    }

    {
      assertNull (getCopy ((double []) null));
      final double [] x = new double [] { 3.14, 22.45, -34, 255.99 };
      assertTrue (isArrayEquals (x, getCopy (x)));
      assertNull (getCopy ((double []) null, 1, 1));
      assertEquals (x[1], getCopy (x, 1, 1)[0], 0);
      assertNotNull (getCopy (x, 1));
      assertNull (getCopy ((double []) null, 1));
    }

    {
      assertNull (getCopy ((float []) null));
      final float [] x = new float [] { 3.14f, 22.45f, -34f, 255.99f };
      assertTrue (isArrayEquals (x, getCopy (x)));
      assertNull (getCopy ((float []) null, 1, 1));
      assertEquals (x[1], getCopy (x, 1, 1)[0], 0);
      assertNotNull (getCopy (x, 1));
      assertNull (getCopy ((float []) null, 1));
    }

    {
      assertNull (getCopy ((int []) null));
      final int [] x = new int [] { 314, 2245, -34, 25599 };
      assertTrue (isArrayEquals (x, getCopy (x)));
      assertNull (getCopy ((int []) null, 1, 1));
      assertEquals (x[1], getCopy (x, 1, 1)[0]);
      assertNotNull (getCopy (x, 1));
      assertNull (getCopy ((int []) null, 1));
    }

    {
      assertNull (getCopy ((long []) null));
      final long [] x = new long [] { 314, 2245, -34, 25599 };
      assertTrue (isArrayEquals (x, getCopy (x)));
      assertNull (getCopy ((long []) null, 1, 1));
      assertEquals (x[1], getCopy (x, 1, 1)[0]);
      assertNotNull (getCopy (x, 1));
      assertNull (getCopy ((long []) null, 1));
    }

    {
      assertNull (getCopy ((short []) null));
      final short [] x = new short [] { 14, 22, -34, 127 };
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
    assertNull (getAsObjectArray (new ArrayList <String> ()));
    assertArrayEquals (new Object [] { "Hallo" }, getAsObjectArray (CollectionHelper.newList ("Hallo")));
    assertArrayEquals (new Object [] { "Hallo", "Welt" },
                       getAsObjectArray (CollectionHelper.newList ("Hallo", "Welt")));
    assertArrayEquals (new Object [] { I1, "Welt" }, getAsObjectArray (CollectionHelper.<Object> newList (I1, "Welt")));
  }

  /**
   * Test method safeGetElement
   */
  @Test
  public void testSafeGetElement ()
  {
    final String [] x = new String [] { "a", "b", "c" };
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
      final String [] a = new String [] { "a", "b" };
      final String [] b = new String [] { "c", "c2" };
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
      final Integer [] a = new Integer [] { I1, I2 };
      final Integer [] b = new Integer [] { I3, I4 };

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
      assertTrue (Arrays.equals (new boolean [] { false, false, true },
                                 getConcatenated (new boolean [] { false, false }, new boolean [] { true })));
      assertTrue (Arrays.equals (new boolean [] { false, false },
                                 getConcatenated (new boolean [] { false, false }, (boolean []) null)));
      assertTrue (Arrays.equals (new boolean [] { true },
                                 getConcatenated ((boolean []) null, new boolean [] { true })));
      assertTrue (Arrays.equals (new boolean [] { false, false, true },
                                 getConcatenated (new boolean [] { false, false }, true)));
      assertTrue (Arrays.equals (new boolean [] { true }, getConcatenated ((boolean []) null, true)));
      assertTrue (Arrays.equals (new boolean [] { false, false, true },
                                 getConcatenated (false, new boolean [] { false, true })));
      assertTrue (Arrays.equals (new boolean [] { false }, getConcatenated (false, (boolean []) null)));
    }

    {
      assertTrue (Arrays.equals (new byte [] { 1, 2, 3, 4 },
                                 getConcatenated (new byte [] { 1, 2 }, new byte [] { 3, 4 })));
      assertTrue (Arrays.equals (new byte [] { 1, 2 }, getConcatenated (new byte [] { 1, 2 }, (byte []) null)));
      assertTrue (Arrays.equals (new byte [] { 3, 4 }, getConcatenated ((byte []) null, new byte [] { 3, 4 })));
      assertTrue (Arrays.equals (new byte [] { 1, 2, 3 }, getConcatenated (new byte [] { 1, 2 }, (byte) 3)));
      assertTrue (Arrays.equals (new byte [] { 3 }, getConcatenated ((byte []) null, (byte) 3)));
      assertTrue (Arrays.equals (new byte [] { 1, 2, 3 }, getConcatenated ((byte) 1, new byte [] { 2, 3 })));
      assertTrue (Arrays.equals (new byte [] { 1 }, getConcatenated ((byte) 1, (byte []) null)));
    }

    {
      assertTrue (Arrays.equals (new char [] { 1, 2, 3, 4 },
                                 getConcatenated (new char [] { 1, 2 }, new char [] { 3, 4 })));
      assertTrue (Arrays.equals (new char [] { 1, 2 }, getConcatenated (new char [] { 1, 2 }, (char []) null)));
      assertTrue (Arrays.equals (new char [] { 3, 4 }, getConcatenated ((char []) null, new char [] { 3, 4 })));
      assertTrue (Arrays.equals (new char [] { 1, 2, 3 }, getConcatenated (new char [] { 1, 2 }, (char) 3)));
      assertTrue (Arrays.equals (new char [] { 3 }, getConcatenated ((char []) null, (char) 3)));
      assertTrue (Arrays.equals (new char [] { 1, 2, 3 }, getConcatenated ((char) 1, new char [] { 2, 3 })));
      assertTrue (Arrays.equals (new char [] { 1 }, getConcatenated ((char) 1, (char []) null)));
    }

    {
      assertTrue (Arrays.equals (new double [] { 1, 2, 3, 4 },
                                 getConcatenated (new double [] { 1, 2 }, new double [] { 3, 4 })));
      assertTrue (Arrays.equals (new double [] { 1, 2 }, getConcatenated (new double [] { 1, 2 }, (double []) null)));
      assertTrue (Arrays.equals (new double [] { 3, 4 }, getConcatenated ((double []) null, new double [] { 3, 4 })));
      assertTrue (Arrays.equals (new double [] { 1, 2, 3 }, getConcatenated (new double [] { 1, 2 }, 3)));
      assertTrue (Arrays.equals (new double [] { 3 }, getConcatenated ((double []) null, 3)));
      assertTrue (Arrays.equals (new double [] { 1, 2, 3 }, getConcatenated (1, new double [] { 2, 3 })));
      assertTrue (Arrays.equals (new double [] { 1 }, getConcatenated (1, (double []) null)));
    }

    {
      assertTrue (Arrays.equals (new float [] { 1, 2, 3, 4 },
                                 getConcatenated (new float [] { 1, 2 }, new float [] { 3, 4 })));
      assertTrue (Arrays.equals (new float [] { 1, 2 }, getConcatenated (new float [] { 1, 2 }, (float []) null)));
      assertTrue (Arrays.equals (new float [] { 3, 4 }, getConcatenated ((float []) null, new float [] { 3, 4 })));
      assertTrue (Arrays.equals (new float [] { 1, 2, 3 }, getConcatenated (new float [] { 1, 2 }, 3)));
      assertTrue (Arrays.equals (new float [] { 3 }, getConcatenated ((float []) null, 3)));
      assertTrue (Arrays.equals (new float [] { 1, 2, 3 }, getConcatenated (1, new float [] { 2, 3 })));
      assertTrue (Arrays.equals (new float [] { 1 }, getConcatenated (1, (float []) null)));
    }

    {
      assertTrue (Arrays.equals (new int [] { 1, 2, 3, 4 },
                                 getConcatenated (new int [] { 1, 2 }, new int [] { 3, 4 })));
      assertTrue (Arrays.equals (new int [] { 1, 2 }, getConcatenated (new int [] { 1, 2 }, (int []) null)));
      assertTrue (Arrays.equals (new int [] { 3, 4 }, getConcatenated ((int []) null, new int [] { 3, 4 })));
      assertTrue (Arrays.equals (new int [] { 1, 2, 3 }, getConcatenated (new int [] { 1, 2 }, 3)));
      assertTrue (Arrays.equals (new int [] { 3 }, getConcatenated ((int []) null, 3)));
      assertTrue (Arrays.equals (new int [] { 1, 2, 3 }, getConcatenated (1, new int [] { 2, 3 })));
      assertTrue (Arrays.equals (new int [] { 1 }, getConcatenated (1, (int []) null)));
    }

    {
      assertTrue (Arrays.equals (new long [] { 1, 2, 3, 4 },
                                 getConcatenated (new long [] { 1, 2 }, new long [] { 3, 4 })));
      assertTrue (Arrays.equals (new long [] { 1, 2 }, getConcatenated (new long [] { 1, 2 }, (long []) null)));
      assertTrue (Arrays.equals (new long [] { 3, 4 }, getConcatenated ((long []) null, new long [] { 3, 4 })));
      assertTrue (Arrays.equals (new long [] { 1, 2, 3 }, getConcatenated (new long [] { 1, 2 }, 3L)));
      assertTrue (Arrays.equals (new long [] { 3 }, getConcatenated ((long []) null, 3L)));
      assertTrue (Arrays.equals (new long [] { 1, 2, 3 }, getConcatenated (1L, new long [] { 2, 3 })));
      assertTrue (Arrays.equals (new long [] { 1 }, getConcatenated (1L, (long []) null)));
    }

    {
      assertTrue (Arrays.equals (new short [] { 1, 2, 3, 4 },
                                 getConcatenated (new short [] { 1, 2 }, new short [] { 3, 4 })));
      assertTrue (Arrays.equals (new short [] { 1, 2 }, getConcatenated (new short [] { 1, 2 }, (short []) null)));
      assertTrue (Arrays.equals (new short [] { 3, 4 }, getConcatenated ((short []) null, new short [] { 3, 4 })));
      assertTrue (Arrays.equals (new short [] { 1, 2, 3 }, getConcatenated (new short [] { 1, 2 }, (short) 3)));
      assertTrue (Arrays.equals (new short [] { 3 }, getConcatenated ((short []) null, (short) 3)));
      assertTrue (Arrays.equals (new short [] { 1, 2, 3 }, getConcatenated ((short) 1, new short [] { 2, 3 })));
      assertTrue (Arrays.equals (new short [] { 1 }, getConcatenated ((short) 1, (short []) null)));
    }
  }

  @Test
  public void testNewArrayEmpty ()
  {
    final String [] a = newArray (String.class, 3);
    assertNotNull (a);
    assertTrue (isArray (a));
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
    String [] x = newArray (CollectionHelper.newList ("s1", "s2", "s3"), String.class);
    assertNotNull (x);
    assertEquals (3, x.length);

    x = newArray (new ArrayList <String> (), String.class);
    assertNotNull (x);

    x = newArray ((List <String>) null, String.class);
    assertNotNull (x);

    CharSequence [] y = newArray (CollectionHelper.newList ("s1", "s2", "s3"), CharSequence.class);
    assertNotNull (y);
    assertEquals (3, y.length);

    y = newArray (CollectionHelper.newSet ("s1", "s2", "s3"), CharSequence.class);
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
  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
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
      final String [] x = new String [] { "s1", "s2", "s3" };
      final String [] y = new String [] { "s2", "s3" };
      final String [] z = new String [] { "s3" };
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
      final boolean [] x = new boolean [] { true, false, true };
      final boolean [] y = new boolean [] { false, true };
      final boolean [] z = new boolean [] { true };
      assertTrue (Arrays.equals (y, getAllExceptFirst (x)));
      assertTrue (Arrays.equals (x, getAllExceptFirst (x, 0)));
      assertTrue (Arrays.equals (y, getAllExceptFirst (x, 1)));
      assertTrue (Arrays.equals (z, getAllExceptFirst (x, 2)));
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
      final byte [] x = new byte [] { 5, 9, 14 };
      final byte [] y = new byte [] { 9, 14 };
      final byte [] z = new byte [] { 14 };
      assertTrue (Arrays.equals (y, getAllExceptFirst (x)));
      assertTrue (Arrays.equals (x, getAllExceptFirst (x, 0)));
      assertTrue (Arrays.equals (y, getAllExceptFirst (x, 1)));
      assertTrue (Arrays.equals (z, getAllExceptFirst (x, 2)));
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
      final char [] x = new char [] { 'a', 'B', 'c' };
      final char [] y = new char [] { 'B', 'c' };
      final char [] z = new char [] { 'c' };
      assertTrue (Arrays.equals (y, getAllExceptFirst (x)));
      assertTrue (Arrays.equals (x, getAllExceptFirst (x, 0)));
      assertTrue (Arrays.equals (y, getAllExceptFirst (x, 1)));
      assertTrue (Arrays.equals (z, getAllExceptFirst (x, 2)));
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
      final double [] x = new double [] { -1.1, 0, 1.1 };
      final double [] y = new double [] { 0, 1.1 };
      final double [] z = new double [] { 1.1 };
      assertTrue (Arrays.equals (y, getAllExceptFirst (x)));
      assertTrue (Arrays.equals (x, getAllExceptFirst (x, 0)));
      assertTrue (Arrays.equals (y, getAllExceptFirst (x, 1)));
      assertTrue (Arrays.equals (z, getAllExceptFirst (x, 2)));
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
      final float [] x = new float [] { -3.2f, -0.01f, 99.8f };
      final float [] y = new float [] { -0.01f, 99.8f };
      final float [] z = new float [] { 99.8f };
      assertTrue (Arrays.equals (y, getAllExceptFirst (x)));
      assertTrue (Arrays.equals (x, getAllExceptFirst (x, 0)));
      assertTrue (Arrays.equals (y, getAllExceptFirst (x, 1)));
      assertTrue (Arrays.equals (z, getAllExceptFirst (x, 2)));
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
      final int [] x = new int [] { -5, 2, 9 };
      final int [] y = new int [] { 2, 9 };
      final int [] z = new int [] { 9 };
      assertTrue (Arrays.equals (y, getAllExceptFirst (x)));
      assertTrue (Arrays.equals (x, getAllExceptFirst (x, 0)));
      assertTrue (Arrays.equals (y, getAllExceptFirst (x, 1)));
      assertTrue (Arrays.equals (z, getAllExceptFirst (x, 2)));
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
      final long [] x = new long [] { -6, 2, 10 };
      final long [] y = new long [] { 2, 10 };
      final long [] z = new long [] { 10 };
      assertTrue (Arrays.equals (y, getAllExceptFirst (x)));
      assertTrue (Arrays.equals (x, getAllExceptFirst (x, 0)));
      assertTrue (Arrays.equals (y, getAllExceptFirst (x, 1)));
      assertTrue (Arrays.equals (z, getAllExceptFirst (x, 2)));
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
      final short [] x = new short [] { -100, -10, 1 };
      final short [] y = new short [] { -10, 1 };
      final short [] z = new short [] { 1 };
      assertTrue (Arrays.equals (y, getAllExceptFirst (x)));
      assertTrue (Arrays.equals (x, getAllExceptFirst (x, 0)));
      assertTrue (Arrays.equals (y, getAllExceptFirst (x, 1)));
      assertTrue (Arrays.equals (z, getAllExceptFirst (x, 2)));
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
      final String [] x = new String [] { "s1", "s2", "s3" };
      final String [] y = new String [] { "s1", "s2" };
      final String [] z = new String [] { "s1" };
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
      final boolean [] x = new boolean [] { true, false, true };
      final boolean [] y = new boolean [] { true, false };
      final boolean [] z = new boolean [] { true };
      assertTrue (Arrays.equals (y, getAllExceptLast (x)));
      assertTrue (Arrays.equals (x, getAllExceptLast (x, 0)));
      assertTrue (Arrays.equals (y, getAllExceptLast (x, 1)));
      assertTrue (Arrays.equals (z, getAllExceptLast (x, 2)));
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
      final byte [] x = new byte [] { 5, 9, 14 };
      final byte [] y = new byte [] { 5, 9 };
      final byte [] z = new byte [] { 5 };
      assertTrue (Arrays.equals (y, getAllExceptLast (x)));
      assertTrue (Arrays.equals (x, getAllExceptLast (x, 0)));
      assertTrue (Arrays.equals (y, getAllExceptLast (x, 1)));
      assertTrue (Arrays.equals (z, getAllExceptLast (x, 2)));
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
      final char [] x = new char [] { 'a', 'B', 'c' };
      final char [] y = new char [] { 'a', 'B' };
      final char [] z = new char [] { 'a' };
      assertTrue (Arrays.equals (y, getAllExceptLast (x)));
      assertTrue (Arrays.equals (x, getAllExceptLast (x, 0)));
      assertTrue (Arrays.equals (y, getAllExceptLast (x, 1)));
      assertTrue (Arrays.equals (z, getAllExceptLast (x, 2)));
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
      final double [] x = new double [] { -1.1, 0, 1.1 };
      final double [] y = new double [] { -1.1, 0 };
      final double [] z = new double [] { -1.1 };
      assertTrue (Arrays.equals (y, getAllExceptLast (x)));
      assertTrue (Arrays.equals (x, getAllExceptLast (x, 0)));
      assertTrue (Arrays.equals (y, getAllExceptLast (x, 1)));
      assertTrue (Arrays.equals (z, getAllExceptLast (x, 2)));
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
      final float [] x = new float [] { -3.2f, -0.01f, 99.8f };
      final float [] y = new float [] { -3.2f, -0.01f };
      final float [] z = new float [] { -3.2f };
      assertTrue (Arrays.equals (y, getAllExceptLast (x)));
      assertTrue (Arrays.equals (x, getAllExceptLast (x, 0)));
      assertTrue (Arrays.equals (y, getAllExceptLast (x, 1)));
      assertTrue (Arrays.equals (z, getAllExceptLast (x, 2)));
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
      final int [] x = new int [] { -5, 2, 9 };
      final int [] y = new int [] { -5, 2 };
      final int [] z = new int [] { -5 };
      assertTrue (Arrays.equals (y, getAllExceptLast (x)));
      assertTrue (Arrays.equals (x, getAllExceptLast (x, 0)));
      assertTrue (Arrays.equals (y, getAllExceptLast (x, 1)));
      assertTrue (Arrays.equals (z, getAllExceptLast (x, 2)));
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
      final long [] x = new long [] { -6, 2, 10 };
      final long [] y = new long [] { -6, 2 };
      final long [] z = new long [] { -6 };
      assertTrue (Arrays.equals (y, getAllExceptLast (x)));
      assertTrue (Arrays.equals (x, getAllExceptLast (x, 0)));
      assertTrue (Arrays.equals (y, getAllExceptLast (x, 1)));
      assertTrue (Arrays.equals (z, getAllExceptLast (x, 2)));
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
      final short [] x = new short [] { -100, -10, 1 };
      final short [] y = new short [] { -100, -10 };
      final short [] z = new short [] { -100 };
      assertTrue (Arrays.equals (y, getAllExceptLast (x)));
      assertTrue (Arrays.equals (x, getAllExceptLast (x, 0)));
      assertTrue (Arrays.equals (y, getAllExceptLast (x, 1)));
      assertTrue (Arrays.equals (z, getAllExceptLast (x, 2)));
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
}
