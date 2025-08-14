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

import org.junit.Test;

import com.helger.base.mock.CommonsAssert;
import com.helger.collection.CollectionHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.iterator.IteratorHelper;
import com.helger.collection.stack.StackHelper;
import com.helger.commons.string.StringParser;

/**
 * Test class for class {@link EqualsHelperExt}.
 *
 * @author Philip Helger
 */
@Deprecated (forRemoval = true, since = "12.0.0")
public final class EqualsHelperExtTest
{
  @Test
  public void testEquals_URL () throws MalformedURLException
  {
    final URL u1 = new URL ("http://www.helger.com");
    final URL u2 = new URL ("http://www.mydomain.at");
    assertTrue (EqualsHelperExt.extEquals (u1, u1));
    assertTrue (EqualsHelperExt.extEquals (u1, new URL ("http://www.helger.com")));
    assertFalse (EqualsHelperExt.extEquals (u1, u2));
  }

  @Test
  public void testEquals_BigDecimal ()
  {
    final BigDecimal bd1 = StringParser.parseBigDecimal ("5.5");
    final BigDecimal bd2 = StringParser.parseBigDecimal ("5.49999");
    assertTrue (EqualsHelperExt.extEquals (bd1, bd1));
    assertTrue (EqualsHelperExt.extEquals (bd1, StringParser.parseBigDecimal ("5.5000")));
    assertTrue (EqualsHelperExt.extEquals (bd1, StringParser.parseBigDecimal ("5.50000000000000000")));
    assertFalse (EqualsHelperExt.extEquals (bd1, bd2));
  }

  @Test
  public void testEquals_PasswordAuthentication ()
  {
    final PasswordAuthentication o1 = new PasswordAuthentication ("user", "pw".toCharArray ());
    assertTrue (EqualsHelperExt.extEquals (o1, o1));
    assertTrue (EqualsHelperExt.extEquals (o1, new PasswordAuthentication ("user", "pw".toCharArray ())));
    assertFalse (EqualsHelperExt.extEquals (o1, new PasswordAuthentication ("user2", "pw".toCharArray ())));
    assertFalse (EqualsHelperExt.extEquals (o1, new PasswordAuthentication ("user", "pw2".toCharArray ())));
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
    CommonsAssert.assertEquals ((BigDecimal) null, (BigDecimal) null);
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
    CommonsAssert.assertEquals ((URL) null, (URL) null);
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
  }

  @Test
  public void testEqualsTypeSpecific ()
  {
    final StringBuffer aSB1 = new StringBuffer ("Hi");
    assertTrue (EqualsHelperExt.extEquals (aSB1, new StringBuffer ("Hi")));
    assertFalse (EqualsHelperExt.extEquals (aSB1, new StringBuffer ("Hallo")));

    assertTrue (EqualsHelperExt.extEquals (aSB1, new StringBuffer ("Hi")));
    assertFalse (EqualsHelperExt.extEquals (aSB1, new StringBuffer ("Hallo")));
    assertFalse (EqualsHelperExt.extEquals (aSB1, null));

    CommonsAssert.assertEquals (new CommonsArrayList <> ("a", "b", "c"), new CommonsArrayList <> ("a", "b", "c"));
    CommonsAssert.assertEquals (StackHelper.newStack ("a", "b", "c"), StackHelper.newStack ("a", "b", "c"));
    assertTrue (EqualsHelperExt.extEquals (new CommonsArrayList <> ("a", "b", "c").iterator (),
                                           new CommonsArrayList <> ("a", "b", "c").iterator ()));
    assertTrue (EqualsHelperExt.extEquals (IteratorHelper.getEnumeration ("a", "b", "c"),
                                           IteratorHelper.getEnumeration ("a", "b", "c")));
    assertFalse (EqualsHelperExt.extEquals (CollectionHelper.makeUnmodifiable (new CommonsArrayList <> ("a", "b", "c")),
                                            new CommonsArrayList <> ("a", "b", "c")));
  }
}
