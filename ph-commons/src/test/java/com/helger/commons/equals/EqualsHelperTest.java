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
package com.helger.commons.equals;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.function.BiPredicate;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.IteratorHelper;
import com.helger.commons.collection.StackHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.mock.CommonsAssert;
import com.helger.commons.mutable.MutableBoolean;
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

  @Test
  public void testEquals_PasswordAuthentication ()
  {
    final PasswordAuthentication o1 = new PasswordAuthentication ("user", "pw".toCharArray ());
    CommonsAssert.assertEquals (o1, o1);
    CommonsAssert.assertEquals (o1, new PasswordAuthentication ("user", "pw".toCharArray ()));
    CommonsAssert.assertNotEquals (o1, new PasswordAuthentication ("user2", "pw".toCharArray ()));
    CommonsAssert.assertNotEquals (o1, new PasswordAuthentication ("user", "pw2".toCharArray ()));
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
  public void testEqualsCustom ()
  {
    final MutableBoolean aPredCalled = new MutableBoolean (false);
    final BiPredicate <String, String> aPredicate = (x, y) -> {
      aPredCalled.set (true);
      return true;
    };

    assertTrue (EqualsHelper.equalsCustom (null, null, aPredicate));
    assertFalse (aPredCalled.booleanValue ());

    assertFalse (EqualsHelper.equalsCustom ("a", null, aPredicate));
    assertFalse (aPredCalled.booleanValue ());

    assertFalse (EqualsHelper.equalsCustom (null, "a", aPredicate));
    assertFalse (aPredCalled.booleanValue ());

    // Predicate is only called here
    assertTrue (EqualsHelper.equalsCustom ("b", "a", aPredicate));
    assertTrue (aPredCalled.booleanValue ());
  }
}
