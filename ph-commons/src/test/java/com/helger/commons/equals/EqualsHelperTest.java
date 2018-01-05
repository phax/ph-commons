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
package com.helger.commons.equals;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.IteratorHelper;
import com.helger.commons.collection.NonBlockingStack;
import com.helger.commons.collection.StackHelper;
import com.helger.commons.collection.attr.StringMap;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.CommonsLinkedList;
import com.helger.commons.collection.impl.CommonsTreeSet;
import com.helger.commons.collection.impl.CommonsVector;
import com.helger.commons.collection.impl.ICommonsIterable;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.mock.CommonsAssert;
import com.helger.commons.string.StringParser;

/**
 * Test class for class {@link EqualsHelper}.
 *
 * @author Philip Helger
 */
public final class EqualsHelperTest
{
  @Test
  public void testEquals_Float ()
  {
    CommonsAssert.assertEquals (0f, -0f);
    CommonsAssert.assertEquals (1.1f, 1.1f);
    CommonsAssert.assertEquals (Float.NaN, Float.NaN);
    CommonsAssert.assertEquals (1f / 0f, Float.POSITIVE_INFINITY);
    CommonsAssert.assertEquals (-1f / 0f, Float.NEGATIVE_INFINITY);
    CommonsAssert.assertEquals (Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
    CommonsAssert.assertEquals (Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
    CommonsAssert.assertEquals (Float.MIN_VALUE, Float.MIN_VALUE);
    CommonsAssert.assertEquals (Float.MAX_VALUE, Float.MAX_VALUE);
  }

  @Test
  public void testEquals_Double ()
  {
    CommonsAssert.assertEquals (0d, -0d);
    CommonsAssert.assertEquals (1.1d, 1.1d);
    CommonsAssert.assertEquals (Double.NaN, Double.NaN);
    CommonsAssert.assertEquals (1d / 0d, Double.POSITIVE_INFINITY);
    CommonsAssert.assertEquals (-1d / 0d, Double.NEGATIVE_INFINITY);
    CommonsAssert.assertEquals (Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    CommonsAssert.assertEquals (Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
    CommonsAssert.assertEquals (Double.MIN_VALUE, Double.MIN_VALUE);
    CommonsAssert.assertEquals (Double.MAX_VALUE, Double.MAX_VALUE);
  }

  @Test
  public void testEquals_URL () throws MalformedURLException
  {
    final URL u1 = new URL ("http://www.helger.com");
    final URL u2 = new URL ("http://www.mydomain.at");
    CommonsAssert.assertEquals (u1, u1);
    CommonsAssert.assertEquals (u1, new URL ("http://www.helger.com"));
    CommonsAssert.assertNotEquals (u1, u2);
  }

  @Test
  public void testEquals_BigDecimal ()
  {
    final BigDecimal bd1 = StringParser.parseBigDecimal ("5.5");
    final BigDecimal bd2 = StringParser.parseBigDecimal ("5.49999");
    CommonsAssert.assertEquals (bd1, bd1);
    CommonsAssert.assertEquals (bd1, StringParser.parseBigDecimal ("5.5000"));
    CommonsAssert.assertEquals (bd1, StringParser.parseBigDecimal ("5.50000000000000000"));
    CommonsAssert.assertNotEquals (bd1, bd2);
  }

  public void _test (final String s1, final String s2)
  {
    CommonsAssert.assertEquals (s1, s1);
    CommonsAssert.assertNotEquals (s1, s2);
    CommonsAssert.assertNotEquals (s2, s1);
    CommonsAssert.assertNotEquals (s1, null);
    CommonsAssert.assertNotEquals (null, s2);
    CommonsAssert.assertEquals ((String) null, (String) null);
  }

  public void _test (final BigDecimal s1, final BigDecimal s2)
  {
    CommonsAssert.assertEquals (s1, s1);
    CommonsAssert.assertNotEquals (s1, s2);
    CommonsAssert.assertNotEquals (s2, s1);
    CommonsAssert.assertNotEquals (s1, null);
    CommonsAssert.assertNotEquals (null, s2);
    CommonsAssert.assertEquals ((Float) null, (Float) null);
  }

  public void _test (final Double s1, final Double s2)
  {
    CommonsAssert.assertEquals (s1, s1);
    CommonsAssert.assertNotEquals (s1, s2);
    CommonsAssert.assertNotEquals (s2, s1);
    CommonsAssert.assertNotEquals (s1, null);
    CommonsAssert.assertNotEquals (null, s2);
    CommonsAssert.assertEquals ((Double) null, (Double) null);
  }

  public void _test (final Float s1, final Float s2)
  {
    CommonsAssert.assertEquals (s1, s1);
    CommonsAssert.assertNotEquals (s1, s2);
    CommonsAssert.assertNotEquals (s2, s1);
    CommonsAssert.assertNotEquals (s1, null);
    CommonsAssert.assertNotEquals (null, s2);
    CommonsAssert.assertEquals ((Float) null, (Float) null);
  }

  public void _test (final URL s1, final URL s2)
  {
    CommonsAssert.assertEquals (s1, s1);
    CommonsAssert.assertNotEquals (s1, s2);
    CommonsAssert.assertNotEquals (s2, s1);
    CommonsAssert.assertNotEquals (s1, null);
    CommonsAssert.assertNotEquals (null, s2);
    CommonsAssert.assertEquals ((Float) null, (Float) null);
  }

  public void _test (final boolean [] s1, final boolean [] s2)
  {
    assertArrayEquals (s1, s1);
    CommonsAssert.assertNotEquals (s1, s2);
    CommonsAssert.assertNotEquals (s2, s1);
    CommonsAssert.assertNotEquals (s1, null);
    CommonsAssert.assertNotEquals (null, s2);
    CommonsAssert.assertEquals ((boolean []) null, (boolean []) null);
  }

  public void _test (final byte [] s1, final byte [] s2)
  {
    assertArrayEquals (s1, s1);
    CommonsAssert.assertNotEquals (s1, s2);
    CommonsAssert.assertNotEquals (s2, s1);
    CommonsAssert.assertNotEquals (s1, null);
    CommonsAssert.assertNotEquals (null, s2);
    CommonsAssert.assertEquals ((byte []) null, (byte []) null);
  }

  public void _test (final char [] s1, final char [] s2)
  {
    assertArrayEquals (s1, s1);
    CommonsAssert.assertNotEquals (s1, s2);
    CommonsAssert.assertNotEquals (s2, s1);
    CommonsAssert.assertNotEquals (s1, null);
    CommonsAssert.assertNotEquals (null, s2);
    CommonsAssert.assertEquals ((char []) null, (char []) null);
  }

  public void _test (final double [] s1, final double [] s2)
  {
    assertArrayEquals (s1, s1, CommonsAssert.DOUBLE_ALLOWED_ROUNDING_DIFFERENCE);
    CommonsAssert.assertNotEquals (s1, s2);
    CommonsAssert.assertNotEquals (s2, s1);
    CommonsAssert.assertNotEquals (s1, null);
    CommonsAssert.assertNotEquals (null, s2);
    CommonsAssert.assertEquals ((double []) null, (double []) null);
  }

  public void _test (final float [] s1, final float [] s2)
  {
    assertArrayEquals (s1, s1, CommonsAssert.FLOAT_ALLOWED_ROUNDING_DIFFERENCE);
    CommonsAssert.assertNotEquals (s1, s2);
    CommonsAssert.assertNotEquals (s2, s1);
    CommonsAssert.assertNotEquals (s1, null);
    CommonsAssert.assertNotEquals (null, s2);
    CommonsAssert.assertEquals ((float []) null, (float []) null);
  }

  public void _test (final int [] s1, final int [] s2)
  {
    assertArrayEquals (s1, s1);
    CommonsAssert.assertNotEquals (s1, s2);
    CommonsAssert.assertNotEquals (s2, s1);
    CommonsAssert.assertNotEquals (s1, null);
    CommonsAssert.assertNotEquals (null, s2);
    CommonsAssert.assertEquals ((int []) null, (int []) null);
  }

  public void _test (final long [] s1, final long [] s2)
  {
    assertArrayEquals (s1, s1);
    CommonsAssert.assertNotEquals (s1, s2);
    CommonsAssert.assertNotEquals (s2, s1);
    CommonsAssert.assertNotEquals (s1, null);
    CommonsAssert.assertNotEquals (null, s2);
    CommonsAssert.assertEquals ((long []) null, (long []) null);
  }

  public void _test (final short [] s1, final short [] s2)
  {
    assertArrayEquals (s1, s1);
    CommonsAssert.assertNotEquals (s1, s2);
    CommonsAssert.assertNotEquals (s2, s1);
    CommonsAssert.assertNotEquals (s1, null);
    CommonsAssert.assertNotEquals (null, s2);
    CommonsAssert.assertEquals ((short []) null, (short []) null);
  }

  @Test
  public void testNullSafeEquals () throws MalformedURLException
  {
    _test ("s1", "s2");
    _test (StringParser.parseBigDecimal ("12562136756"), StringParser.parseBigDecimal ("67673455"));
    _test (Double.valueOf (3.1234d), Double.valueOf (23.456d));
    _test (Float.valueOf (3.1234f), Float.valueOf (23.456f));
    _test (new URL ("http://www.helger.com"), new URL ("http://www.google.com"));
    _test (new boolean [] { true }, new boolean [] { false });
    _test (new byte [] { 1 }, new byte [] { 2 });
    _test (new char [] { 'a' }, new char [] { 'b' });
    _test (new double [] { 2.1 }, new double [] { 2 });
    _test (new float [] { 2.1f }, new float [] { 1.9f });
    _test (new int [] { 5 }, new int [] { 6 });
    _test (new long [] { 7 }, new long [] { 8 });
    _test (new short [] { -9 }, new short [] { -10 });

    final String s1 = "s1";
    final String s2 = "S1";
    assertTrue (EqualsHelper.equalsIgnoreCase (s1, s1));
    assertTrue (EqualsHelper.equalsIgnoreCase (s1, s2));
    assertTrue (EqualsHelper.equalsIgnoreCase (s2, s1));
    assertFalse (EqualsHelper.equalsIgnoreCase (s1, null));
    assertFalse (EqualsHelper.equalsIgnoreCase (null, s2));
    assertTrue (EqualsHelper.equalsIgnoreCase (null, null));
  }

  @Test
  public void testEqualsTypeSpecific ()
  {
    final StringBuffer aSB1 = new StringBuffer ("Hi");
    CommonsAssert.assertEquals (aSB1, new StringBuffer ("Hi"));
    CommonsAssert.assertNotEquals (aSB1, new StringBuffer ("Hallo"));

    CommonsAssert.assertEquals (aSB1, new StringBuffer ("Hi"));
    CommonsAssert.assertNotEquals (aSB1, new StringBuffer ("Hallo"));
    CommonsAssert.assertNotEquals (aSB1, null);

    CommonsAssert.assertEquals (new CommonsArrayList <> ("a", "b", "c"), new CommonsArrayList <> ("a", "b", "c"));
    CommonsAssert.assertEquals (StackHelper.newStack ("a", "b", "c"), StackHelper.newStack ("a", "b", "c"));
    CommonsAssert.assertEquals (new CommonsArrayList <> ("a", "b", "c").iterator (),
                                new CommonsArrayList <> ("a", "b", "c").iterator ());
    CommonsAssert.assertEquals (IteratorHelper.getEnumeration ("a", "b", "c"),
                                IteratorHelper.getEnumeration ("a", "b", "c"));
    CommonsAssert.assertNotEquals (CollectionHelper.makeUnmodifiable (new CommonsArrayList <> ("a", "b", "c")),
                                   new CommonsArrayList <> ("a", "b", "c"));
  }

  @Test
  public void testBasic ()
  {
    // Same objects - are tested first
    assertTrue (EqualsHelper.equalsCollection (null, null));
    assertTrue (EqualsHelper.equalsCollection ("abc", "abc"));

    // One argument is null
    assertFalse (EqualsHelper.equalsCollection ("abc", null));
    assertFalse (EqualsHelper.equalsCollection (null, "abc"));

    // Non-containers
    try
    {
      EqualsHelper.equalsCollection ("abc", "def");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      EqualsHelper.equalsCollection (new CommonsArrayList <> ("abc"), "abc");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      EqualsHelper.equalsCollection ("abc", new CommonsArrayList <> ("abc"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testList ()
  {
    final ICommonsList <String> aCont = new CommonsArrayList <> ("a", "b", "c");
    assertTrue (EqualsHelper.equalsCollection (aCont, aCont));
    assertTrue (EqualsHelper.equalsCollection (aCont, CollectionHelper.makeUnmodifiable (aCont)));
    assertTrue (EqualsHelper.equalsCollection (aCont, Collections.synchronizedList (aCont)));
    assertTrue (EqualsHelper.equalsCollection (aCont, new CommonsArrayList <> (aCont)));
    assertTrue (EqualsHelper.equalsCollection (aCont, new CommonsLinkedList <> (aCont)));
    assertTrue (EqualsHelper.equalsCollection (aCont, new CommonsVector <> (aCont)));
    assertTrue (EqualsHelper.equalsCollection (aCont, new NonBlockingStack <> (aCont)));
    assertTrue (EqualsHelper.equalsCollection (new CommonsArrayList <String> (), new CommonsLinkedList <String> ()));
    assertTrue (EqualsHelper.equalsCollection (new NonBlockingStack <String> (), new CommonsVector <String> ()));
    assertTrue (EqualsHelper.equalsCollection (new NonBlockingStack <String> (), new Stack <String> ()));

    assertFalse (EqualsHelper.equalsCollection (aCont, new CommonsLinkedList <String> ()));
    assertFalse (EqualsHelper.equalsCollection (new CommonsLinkedList <String> (), aCont));
    assertFalse (EqualsHelper.equalsCollection (aCont, new CommonsArrayList <String> ()));
    assertFalse (EqualsHelper.equalsCollection (aCont, new CommonsArrayList <> ("a", "b")));
    assertFalse (EqualsHelper.equalsCollection (aCont, new CommonsArrayList <> ("A", "b", "c")));
    assertFalse (EqualsHelper.equalsCollection (aCont, new CommonsArrayList <> ("a", "B", "c")));
    assertFalse (EqualsHelper.equalsCollection (aCont, new CommonsArrayList <> ("a", "b", "C")));
    assertFalse (EqualsHelper.equalsCollection (aCont, new CommonsArrayList <> ("a", "b", "c", "d")));
    assertFalse (EqualsHelper.equalsCollection (aCont, new CommonsHashSet <> ("a", "b", "c")));
    assertFalse (EqualsHelper.equalsCollection (aCont, ArrayHelper.newArray ("a", "b", "c")));
  }

  @Test
  public void testSet ()
  {
    final ICommonsSet <String> aCont = new CommonsHashSet <> ("a", "b", "c");
    assertTrue (EqualsHelper.equalsCollection (aCont, aCont));
    assertTrue (EqualsHelper.equalsCollection (aCont, CollectionHelper.makeUnmodifiable (aCont)));
    assertTrue (EqualsHelper.equalsCollection (aCont, Collections.synchronizedSet (aCont)));
    assertTrue (EqualsHelper.equalsCollection (aCont, new CommonsHashSet <> (aCont)));
    assertTrue (EqualsHelper.equalsCollection (aCont, new CommonsLinkedHashSet <> (aCont)));
    assertTrue (EqualsHelper.equalsCollection (aCont, new CommonsTreeSet <> (aCont)));
    assertTrue (EqualsHelper.equalsCollection (new CommonsHashSet <String> (), new CommonsLinkedHashSet <String> ()));
    assertTrue (EqualsHelper.equalsCollection (new CommonsTreeSet <String> (), new CommonsHashSet <String> ()));

    assertFalse (EqualsHelper.equalsCollection (aCont, new CommonsHashSet <String> ()));
    assertFalse (EqualsHelper.equalsCollection (new CommonsHashSet <String> (), aCont));
    assertFalse (EqualsHelper.equalsCollection (aCont, new CommonsTreeSet <String> ()));
    assertFalse (EqualsHelper.equalsCollection (aCont, new CommonsHashSet <> ("a", "b")));
    assertFalse (EqualsHelper.equalsCollection (aCont, new CommonsHashSet <> ("A", "b", "c")));
    assertFalse (EqualsHelper.equalsCollection (aCont, new CommonsHashSet <> ("a", "B", "c")));
    assertFalse (EqualsHelper.equalsCollection (aCont, new CommonsHashSet <> ("a", "b", "C")));
    assertFalse (EqualsHelper.equalsCollection (aCont, new CommonsHashSet <> ("a", "b", "c", "d")));
    assertFalse (EqualsHelper.equalsCollection (aCont, new CommonsArrayList <> ("a", "b", "c")));
    assertFalse (EqualsHelper.equalsCollection (aCont, ArrayHelper.newArray ("a", "b", "c")));
  }

  @Test
  public void testMap ()
  {
    final StringMap aMap = new StringMap ("a", "b").add ("c", "d");
    assertTrue (EqualsHelper.equalsCollection (aMap, aMap));
    assertTrue (EqualsHelper.equalsCollection (aMap, CollectionHelper.makeUnmodifiable (aMap)));
    assertTrue (EqualsHelper.equalsCollection (aMap, Collections.synchronizedMap (aMap)));
    assertTrue (EqualsHelper.equalsCollection (aMap, new StringMap ("a", "b").add ("c", "d")));
    assertTrue (EqualsHelper.equalsCollection (new CommonsHashMap <Integer, Integer> (),
                                               new CommonsHashMap <Double, Float> ()));

    assertFalse (EqualsHelper.equalsCollection (aMap, new CommonsHashMap <Integer, Integer> ()));
    assertFalse (EqualsHelper.equalsCollection (new CommonsHashMap <Integer, Integer> (), aMap));
    assertFalse (EqualsHelper.equalsCollection (aMap, new StringMap ("a", "b")));
    assertFalse (EqualsHelper.equalsCollection (aMap, new StringMap ("A", "b").add ("c", "d")));
    assertFalse (EqualsHelper.equalsCollection (aMap, new StringMap ("a", "B").add ("c", "d")));
    assertFalse (EqualsHelper.equalsCollection (aMap, new StringMap ("a", "b").add ("C", "d")));
    assertFalse (EqualsHelper.equalsCollection (aMap, new StringMap ("a", "b").add ("c", "D")));
    assertFalse (EqualsHelper.equalsCollection (aMap, new StringMap ("a", "b").add ("c", "d").add ("e", "f")));
    assertFalse (EqualsHelper.equalsCollection (aMap, new CommonsArrayList <> ("a", "b", "c")));
    assertFalse (EqualsHelper.equalsCollection (aMap, new CommonsHashSet <> ("a", "b", "c")));
    assertFalse (EqualsHelper.equalsCollection (aMap, ArrayHelper.newArray ("a", "b", "c")));
  }

  @Test
  public void testArray ()
  {
    final String [] aArray = ArrayHelper.newArray ("a", "b", "c");
    assertTrue (EqualsHelper.equalsCollection (aArray, aArray));
    assertTrue (EqualsHelper.equalsCollection (aArray, ArrayHelper.newArray ("a", "b", "c")));
    assertTrue (EqualsHelper.equalsCollection (new String [0], new String [] {}));

    assertFalse (EqualsHelper.equalsCollection (aArray, new String [0]));
    assertFalse (EqualsHelper.equalsCollection (new String [0], aArray));
    assertFalse (EqualsHelper.equalsCollection (aArray, ArrayHelper.newArray ("a", "b")));
    assertFalse (EqualsHelper.equalsCollection (aArray, ArrayHelper.newArray ("A", "b", "c")));
    assertFalse (EqualsHelper.equalsCollection (aArray, ArrayHelper.newArray ("a", "B", "c")));
    assertFalse (EqualsHelper.equalsCollection (aArray, ArrayHelper.newArray ("a", "b", "C")));
    assertFalse (EqualsHelper.equalsCollection (aArray, ArrayHelper.newArray ("a", "b", "c", "d")));
    assertFalse (EqualsHelper.equalsCollection (aArray, new CommonsArrayList <> ("a", "b", "c")));
    assertFalse (EqualsHelper.equalsCollection (aArray, new CommonsHashSet <> ("a", "b", "c")));
  }

  @Test
  public void testArrayComplex ()
  {
    final ICommonsList <String> [] aArray = ArrayHelper.newArray (new CommonsArrayList <> ("a", "b"),
                                                                  new CommonsArrayList <> ("c", "d"));
    assertTrue (EqualsHelper.equalsCollection (aArray, aArray));
    assertTrue (EqualsHelper.equalsCollection (aArray,
                                               ArrayHelper.newArray (new CommonsArrayList <> ("a", "b"),
                                                                     new CommonsArrayList <> ("c", "d"))));
    assertTrue (EqualsHelper.equalsCollection (new ICommonsList <?> [0], new ICommonsList <?> [] {}));

    assertFalse (EqualsHelper.equalsCollection (aArray, new ICommonsList <?> [0]));
    assertFalse (EqualsHelper.equalsCollection (new ICommonsList <?> [0], aArray));
    assertFalse (EqualsHelper.equalsCollection (aArray, ArrayHelper.newArray (new CommonsArrayList <> ("a", "b"))));
    assertFalse (EqualsHelper.equalsCollection (aArray,
                                                ArrayHelper.newArray (new CommonsArrayList <> ("A", "b"),
                                                                      new CommonsArrayList <> ("c", "d"))));
    assertFalse (EqualsHelper.equalsCollection (aArray,
                                                ArrayHelper.newArray (new CommonsArrayList <> ("a", "b"),
                                                                      new CommonsArrayList <> ("c", "D"))));
    assertFalse (EqualsHelper.equalsCollection (aArray,
                                                ArrayHelper.newArray (new CommonsArrayList <> ("a", "b"),
                                                                      new CommonsArrayList <> ("c", "d"),
                                                                      new CommonsArrayList <> ("e", "f"))));
    assertFalse (EqualsHelper.equalsCollection (aArray,
                                                ArrayHelper.newArray (new CommonsArrayList <> ("a", "b"),
                                                                      (ICommonsList <String>) null)));
    assertFalse (EqualsHelper.equalsCollection (aArray, new CommonsArrayList <> ("a", "b", "c")));
    assertFalse (EqualsHelper.equalsCollection (aArray, new CommonsHashSet <> ("a", "b", "c")));
    assertFalse (EqualsHelper.equalsCollection (aArray, ArrayHelper.newArray ("a", "b", "c")));
    assertFalse (EqualsHelper.equalsCollection (aArray, ArrayHelper.newArray ("a", null, "c")));
  }

  @Test
  public void testComplex ()
  {
    final ICommonsMap <ICommonsList <String>, ICommonsSet <String>> aMap = new CommonsHashMap <> ();
    aMap.put (new CommonsArrayList <> ("a", "b", "c"), new CommonsHashSet <> ("a", "b", "c"));
    aMap.put (new CommonsArrayList <> ("a", "b", "d"), new CommonsHashSet <> ("a", "b", "d"));
    assertTrue (EqualsHelper.equalsCollection (aMap, CollectionHelper.newMap (aMap)));

    assertFalse (EqualsHelper.equalsCollection (aMap, ArrayHelper.newArray ("a", "b", "c", "d")));
    assertFalse (EqualsHelper.equalsCollection (aMap, new CommonsArrayList <> ("a", "b", "c")));
    assertFalse (EqualsHelper.equalsCollection (aMap, new CommonsHashSet <> ("a", "b", "c")));
    final ICommonsMap <String, String> aMap1a = new CommonsHashMap <> ();
    aMap1a.put ("a", "b");
    assertFalse (EqualsHelper.equalsCollection (aMap, aMap1a));
    final ICommonsMap <ICommonsList <String>, String> aMap2 = new CommonsHashMap <> ();
    aMap2.put (new CommonsArrayList <> ("a", "b", "c"), "d");
    aMap2.put (new CommonsArrayList <> ("a", "b", "d"), "e");
    aMap2.put (new CommonsArrayList <> ("a", "b", "e"), null);
    aMap2.put (null, "g");
    assertFalse (EqualsHelper.equalsCollection (aMap, aMap2));
    assertFalse (EqualsHelper.equalsCollection (aMap2, aMap));
    final ICommonsMap <String, ICommonsList <String>> aMap3 = new CommonsHashMap <> ();
    aMap3.put ("d", new CommonsArrayList <> ("a", "b", "c"));
    aMap3.put ("e", new CommonsArrayList <> ("a", "b", "d"));
    aMap3.put (null, new CommonsArrayList <> ("a", "b", "e"));
    aMap3.put ("g", null);
    assertFalse (EqualsHelper.equalsCollection (aMap, aMap3));
    assertFalse (EqualsHelper.equalsCollection (aMap3, aMap));
  }

  @Test
  public void testIterator ()
  {
    final ICommonsList <String> aCont = new CommonsArrayList <> ("a", "b", "c");
    assertTrue (EqualsHelper.equalsCollection (aCont.iterator (), aCont.iterator ()));
    assertTrue (EqualsHelper.equalsCollection (aCont.iterator (),
                                               CollectionHelper.makeUnmodifiable (aCont).iterator ()));
    assertTrue (EqualsHelper.equalsCollection (aCont.iterator (), Collections.synchronizedList (aCont).iterator ()));
    assertTrue (EqualsHelper.equalsCollection (aCont.iterator (), new CommonsArrayList <> (aCont).iterator ()));
    assertTrue (EqualsHelper.equalsCollection (aCont.iterator (), new CommonsLinkedList <> (aCont).iterator ()));
    assertTrue (EqualsHelper.equalsCollection (aCont.iterator (), new CommonsVector <> (aCont).iterator ()));
    assertTrue (EqualsHelper.equalsCollection (aCont.iterator (), new NonBlockingStack <> (aCont).iterator ()));
    assertTrue (EqualsHelper.equalsCollection (new CommonsArrayList <String> ().iterator (),
                                               new CommonsLinkedList <String> ().iterator ()));
    assertTrue (EqualsHelper.equalsCollection (new NonBlockingStack <String> ().iterator (),
                                               new CommonsVector <String> ().iterator ()));
    assertTrue (EqualsHelper.equalsCollection (new NonBlockingStack <String> ().iterator (),
                                               new Stack <String> ().iterator ()));

    assertFalse (EqualsHelper.equalsCollection (aCont.iterator (), new CommonsLinkedList <String> ().iterator ()));
    assertFalse (EqualsHelper.equalsCollection (new CommonsLinkedList <String> ().iterator (), aCont.iterator ()));
    assertFalse (EqualsHelper.equalsCollection (aCont.iterator (), new CommonsArrayList <String> ().iterator ()));
    assertFalse (EqualsHelper.equalsCollection (aCont.iterator (), new CommonsArrayList <> ("a", "b").iterator ()));
    assertFalse (EqualsHelper.equalsCollection (aCont.iterator (),
                                                new CommonsArrayList <> ("A", "b", "c").iterator ()));
    assertFalse (EqualsHelper.equalsCollection (aCont.iterator (),
                                                new CommonsArrayList <> ("a", "B", "c").iterator ()));
    assertFalse (EqualsHelper.equalsCollection (aCont.iterator (),
                                                new CommonsArrayList <> ("a", "b", "C").iterator ()));
    assertFalse (EqualsHelper.equalsCollection (aCont.iterator (),
                                                new CommonsArrayList <> ("a", "b", "c", "d").iterator ()));
    assertFalse (EqualsHelper.equalsCollection (aCont.iterator (), new CommonsHashSet <> ("a", "b", "c")));
    assertFalse (EqualsHelper.equalsCollection (aCont.iterator (), ArrayHelper.newArray ("a", "b", "c")));
  }

  private static final class MockIterable implements ICommonsIterable <String>
  {
    private final ICommonsList <String> m_aList;

    public MockIterable (final String... aValues)
    {
      m_aList = new CommonsArrayList <> (aValues);
    }

    @Nonnull
    public Iterator <String> iterator ()
    {
      return m_aList.iterator ();
    }
  }

  @Test
  public void testIterable ()
  {
    final Iterable <String> aCont = new MockIterable ("a", "b", "c");
    assertTrue (EqualsHelper.equalsCollection (aCont, aCont));
    assertTrue (EqualsHelper.equalsCollection (aCont, new MockIterable ("a", "b", "c")));
    assertTrue (EqualsHelper.equalsCollection (new MockIterable (), new MockIterable ()));

    assertFalse (EqualsHelper.equalsCollection (aCont, new MockIterable ()));
    assertFalse (EqualsHelper.equalsCollection (new MockIterable (), aCont));
    assertFalse (EqualsHelper.equalsCollection (aCont, new MockIterable ("a", "b")));
    assertFalse (EqualsHelper.equalsCollection (aCont, new MockIterable ("A", "b", "c")));
    assertFalse (EqualsHelper.equalsCollection (aCont, new MockIterable ("a", "B", "c")));
    assertFalse (EqualsHelper.equalsCollection (aCont, new MockIterable ("a", "b", "C")));
    assertFalse (EqualsHelper.equalsCollection (aCont, new MockIterable ("a", "b", "c", "d")));
    assertFalse (EqualsHelper.equalsCollection (aCont, new CommonsHashSet <> ("a", "b", "c")));
    assertFalse (EqualsHelper.equalsCollection (aCont, ArrayHelper.newArray ("a", "b", "c")));
  }

  @Test
  public void testEnumeration ()
  {
    final ICommonsList <String> aCont = new CommonsArrayList <> ("a", "b", "c");
    assertTrue (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (aCont),
                                               IteratorHelper.getEnumeration (aCont)));
    assertTrue (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (aCont),
                                               IteratorHelper.getEnumeration (CollectionHelper.makeUnmodifiable (aCont))));
    assertTrue (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (aCont),
                                               IteratorHelper.getEnumeration (Collections.synchronizedList (aCont))));
    assertTrue (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (aCont),
                                               IteratorHelper.getEnumeration (new CommonsArrayList <> (aCont))));
    assertTrue (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (aCont),
                                               IteratorHelper.getEnumeration (new CommonsLinkedList <> (aCont))));
    assertTrue (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (aCont),
                                               IteratorHelper.getEnumeration (new CommonsVector <> (aCont))));
    assertTrue (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (aCont),
                                               IteratorHelper.getEnumeration (new NonBlockingStack <> (aCont))));
    assertTrue (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (new CommonsArrayList <String> ()),
                                               IteratorHelper.getEnumeration (new CommonsLinkedList <String> ())));
    assertTrue (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (new NonBlockingStack <String> ()),
                                               IteratorHelper.getEnumeration (new CommonsVector <String> ())));
    assertTrue (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (new NonBlockingStack <String> ()),
                                               IteratorHelper.getEnumeration (new Stack <String> ())));

    assertFalse (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (aCont),
                                                IteratorHelper.getEnumeration (new CommonsLinkedList <String> ())));
    assertFalse (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (new CommonsLinkedList <String> ()),
                                                IteratorHelper.getEnumeration (aCont)));
    assertFalse (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (aCont),
                                                IteratorHelper.getEnumeration (new CommonsArrayList <String> ())));
    assertFalse (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (aCont),
                                                IteratorHelper.getEnumeration (new CommonsArrayList <> ("a", "b"))));
    assertFalse (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (aCont),
                                                IteratorHelper.getEnumeration (new CommonsArrayList <> ("A",
                                                                                                        "b",
                                                                                                        "c"))));
    assertFalse (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (aCont),
                                                IteratorHelper.getEnumeration (new CommonsArrayList <> ("a",
                                                                                                        "B",
                                                                                                        "c"))));
    assertFalse (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (aCont),
                                                IteratorHelper.getEnumeration (new CommonsArrayList <> ("a",
                                                                                                        "b",
                                                                                                        "C"))));
    assertFalse (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (aCont),
                                                IteratorHelper.getEnumeration (new CommonsArrayList <> ("a",
                                                                                                        "b",
                                                                                                        "c",
                                                                                                        "d"))));
    assertFalse (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (aCont),
                                                IteratorHelper.getEnumeration (new StringMap ("a", "b").add ("c",
                                                                                                             "d"))));
    assertFalse (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (aCont),
                                                new CommonsHashSet <> ("a", "b", "c")));
    assertFalse (EqualsHelper.equalsCollection (IteratorHelper.getEnumeration (aCont),
                                                ArrayHelper.newArray ("a", "b", "c")));
  }

  @Test
  public void testEqualsAsCollection ()
  {
    assertTrue (EqualsHelper.equalsAsList (null, null));
    assertTrue (EqualsHelper.equalsAsList (new CommonsArrayList <> ("a", "b"), ArrayHelper.newArray ("a", "b")));
    assertTrue (EqualsHelper.equalsAsList (new CommonsArrayList <> ("a", "b"),
                                           new CommonsArrayList <> ("a", "b").iterator ()));
    assertTrue (EqualsHelper.equalsAsList (new CommonsArrayList <> ("a", "b"),
                                           IteratorHelper.getEnumeration ("a", "b")));
    assertTrue (EqualsHelper.equalsAsList (new CommonsArrayList <> ("a", "b"),
                                           CollectionHelper.newOrderedSet ("a", "b")));
    assertTrue (EqualsHelper.equalsAsList (new CommonsArrayList <> ("a", "b"), new MockIterable ("a", "b")));

    assertFalse (EqualsHelper.equalsAsList (null, "abc"));
    assertFalse (EqualsHelper.equalsAsList ("abc", null));
    assertFalse (EqualsHelper.equalsAsList (new CommonsArrayList <> ("a", "b"), ArrayHelper.newArray ("a", "B")));
    assertFalse (EqualsHelper.equalsAsList (new CommonsArrayList <> (null, "b"),
                                            ArrayHelper.newArray ("a", (String) null)));
  }
}
