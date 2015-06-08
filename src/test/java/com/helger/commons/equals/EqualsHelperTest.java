/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.string.StringParser;

/**
 * Test class for class {@link EqualsHelper}.
 *
 * @author Philip Helger
 */
public final class EqualsHelperTest extends AbstractCommonsTestCase
{
  @Test
  public void testEquals_Float ()
  {
    assertTrue (EqualsHelper.equals (1.1f, 1.1f));
    assertTrue (EqualsHelper.equals (Float.NaN, Float.NaN));
    assertTrue (EqualsHelper.equals (1f / 0f, Float.POSITIVE_INFINITY));
    assertTrue (EqualsHelper.equals (-1f / 0f, Float.NEGATIVE_INFINITY));
    assertTrue (EqualsHelper.equals (Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
    assertTrue (EqualsHelper.equals (Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY));
    assertTrue (EqualsHelper.equals (Float.MIN_VALUE, Float.MIN_VALUE));
    assertTrue (EqualsHelper.equals (Float.MAX_VALUE, Float.MAX_VALUE));
  }

  @Test
  public void testEquals_Double ()
  {
    assertTrue (EqualsHelper.equals (1.1d, 1.1d));
    assertTrue (EqualsHelper.equals (Double.NaN, Double.NaN));
    assertTrue (EqualsHelper.equals (1d / 0d, Double.POSITIVE_INFINITY));
    assertTrue (EqualsHelper.equals (-1d / 0d, Double.NEGATIVE_INFINITY));
    assertTrue (EqualsHelper.equals (Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    assertTrue (EqualsHelper.equals (Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
    assertTrue (EqualsHelper.equals (Double.MIN_VALUE, Double.MIN_VALUE));
    assertTrue (EqualsHelper.equals (Double.MAX_VALUE, Double.MAX_VALUE));
  }

  @Test
  public void testEquals_URL () throws MalformedURLException
  {
    final URL u1 = new URL ("http://www.phloc.com");
    final URL u2 = new URL ("http://www.mydomain.at");
    assertTrue (EqualsHelper.equals (u1, u1));
    assertTrue (EqualsHelper.equals (u1, new URL ("http://www.phloc.com")));
    assertFalse (EqualsHelper.equals (u1, u2));
  }

  @Test
  public void testEquals_BigDecimal ()
  {
    final BigDecimal bd1 = StringParser.parseBigDecimal ("5.5");
    final BigDecimal bd2 = StringParser.parseBigDecimal ("5.49999");
    assertTrue (EqualsHelper.equals (bd1, bd1));
    assertTrue (EqualsHelper.equals (bd1, StringParser.parseBigDecimal ("5.5000")));
    assertTrue (EqualsHelper.equals (bd1, StringParser.parseBigDecimal ("5.50000000000000000")));
    assertFalse (EqualsHelper.equals (bd1, bd2));
  }

  public void _test (final String s1, final String s2)
  {
    assertTrue (EqualsHelper.equals (s1, s1));
    assertFalse (EqualsHelper.equals (s1, s2));
    assertFalse (EqualsHelper.equals (s2, s1));
    assertFalse (EqualsHelper.equals (s1, null));
    assertFalse (EqualsHelper.equals (null, s2));
    assertTrue (EqualsHelper.equals ((String) null, (String) null));
  }

  public void _test (final BigDecimal s1, final BigDecimal s2)
  {
    assertTrue (EqualsHelper.equals (s1, s1));
    assertFalse (EqualsHelper.equals (s1, s2));
    assertFalse (EqualsHelper.equals (s2, s1));
    assertFalse (EqualsHelper.equals (s1, null));
    assertFalse (EqualsHelper.equals (null, s2));
    assertTrue (EqualsHelper.equals ((Float) null, (Float) null));
  }

  public void _test (final Double s1, final Double s2)
  {
    assertTrue (EqualsHelper.equals (s1, s1));
    assertFalse (EqualsHelper.equals (s1, s2));
    assertFalse (EqualsHelper.equals (s2, s1));
    assertFalse (EqualsHelper.equals (s1, null));
    assertFalse (EqualsHelper.equals (null, s2));
    assertTrue (EqualsHelper.equals ((Double) null, (Double) null));
  }

  public void _test (final Float s1, final Float s2)
  {
    assertTrue (EqualsHelper.equals (s1, s1));
    assertFalse (EqualsHelper.equals (s1, s2));
    assertFalse (EqualsHelper.equals (s2, s1));
    assertFalse (EqualsHelper.equals (s1, null));
    assertFalse (EqualsHelper.equals (null, s2));
    assertTrue (EqualsHelper.equals ((Float) null, (Float) null));
  }

  public void _test (final URL s1, final URL s2)
  {
    assertTrue (EqualsHelper.equals (s1, s1));
    assertFalse (EqualsHelper.equals (s1, s2));
    assertFalse (EqualsHelper.equals (s2, s1));
    assertFalse (EqualsHelper.equals (s1, null));
    assertFalse (EqualsHelper.equals (null, s2));
    assertTrue (EqualsHelper.equals ((Float) null, (Float) null));
  }

  public void _test (final boolean [] s1, final boolean [] s2)
  {
    assertTrue (EqualsHelper.equals (s1, s1));
    assertFalse (EqualsHelper.equals (s1, s2));
    assertFalse (EqualsHelper.equals (s2, s1));
    assertFalse (EqualsHelper.equals (s1, null));
    assertFalse (EqualsHelper.equals (null, s2));
    assertTrue (EqualsHelper.equals ((boolean []) null, (boolean []) null));
  }

  public void _test (final byte [] s1, final byte [] s2)
  {
    assertTrue (EqualsHelper.equals (s1, s1));
    assertFalse (EqualsHelper.equals (s1, s2));
    assertFalse (EqualsHelper.equals (s2, s1));
    assertFalse (EqualsHelper.equals (s1, null));
    assertFalse (EqualsHelper.equals (null, s2));
    assertTrue (EqualsHelper.equals ((byte []) null, (byte []) null));
  }

  public void _test (final char [] s1, final char [] s2)
  {
    assertTrue (EqualsHelper.equals (s1, s1));
    assertFalse (EqualsHelper.equals (s1, s2));
    assertFalse (EqualsHelper.equals (s2, s1));
    assertFalse (EqualsHelper.equals (s1, null));
    assertFalse (EqualsHelper.equals (null, s2));
    assertTrue (EqualsHelper.equals ((char []) null, (char []) null));
  }

  public void _test (final double [] s1, final double [] s2)
  {
    assertTrue (EqualsHelper.equals (s1, s1));
    assertFalse (EqualsHelper.equals (s1, s2));
    assertFalse (EqualsHelper.equals (s2, s1));
    assertFalse (EqualsHelper.equals (s1, null));
    assertFalse (EqualsHelper.equals (null, s2));
    assertTrue (EqualsHelper.equals ((double []) null, (double []) null));
  }

  public void _test (final float [] s1, final float [] s2)
  {
    assertTrue (EqualsHelper.equals (s1, s1));
    assertFalse (EqualsHelper.equals (s1, s2));
    assertFalse (EqualsHelper.equals (s2, s1));
    assertFalse (EqualsHelper.equals (s1, null));
    assertFalse (EqualsHelper.equals (null, s2));
    assertTrue (EqualsHelper.equals ((float []) null, (float []) null));
  }

  public void _test (final int [] s1, final int [] s2)
  {
    assertTrue (EqualsHelper.equals (s1, s1));
    assertFalse (EqualsHelper.equals (s1, s2));
    assertFalse (EqualsHelper.equals (s2, s1));
    assertFalse (EqualsHelper.equals (s1, null));
    assertFalse (EqualsHelper.equals (null, s2));
    assertTrue (EqualsHelper.equals ((int []) null, (int []) null));
  }

  public void _test (final long [] s1, final long [] s2)
  {
    assertTrue (EqualsHelper.equals (s1, s1));
    assertFalse (EqualsHelper.equals (s1, s2));
    assertFalse (EqualsHelper.equals (s2, s1));
    assertFalse (EqualsHelper.equals (s1, null));
    assertFalse (EqualsHelper.equals (null, s2));
    assertTrue (EqualsHelper.equals ((long []) null, (long []) null));
  }

  public void _test (final short [] s1, final short [] s2)
  {
    assertTrue (EqualsHelper.equals (s1, s1));
    assertFalse (EqualsHelper.equals (s1, s2));
    assertFalse (EqualsHelper.equals (s2, s1));
    assertFalse (EqualsHelper.equals (s1, null));
    assertFalse (EqualsHelper.equals (null, s2));
    assertTrue (EqualsHelper.equals ((short []) null, (short []) null));
  }

  @Test
  public void testNullSafeEquals () throws MalformedURLException
  {
    _test ("s1", "s2");
    _test (StringParser.parseBigDecimal ("12562136756"), StringParser.parseBigDecimal ("67673455"));
    _test (Double.valueOf (3.1415d), Double.valueOf (23.456d));
    _test (Float.valueOf (3.1415f), Float.valueOf (23.456f));
    _test (new URL ("http://www.phloc.com"), new URL ("http://www.google.com"));
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
    assertTrue (EqualsHelper.equals (aSB1, new StringBuffer ("Hi")));
    assertFalse (EqualsHelper.equals (aSB1, new StringBuffer ("Hallo")));

    assertTrue (EqualsHelper.equals (aSB1, new StringBuffer ("Hi")));
    assertFalse (EqualsHelper.equals (aSB1, new StringBuffer ("Hallo")));
    assertFalse (EqualsHelper.equals (aSB1, null));

    assertTrue (EqualsHelper.equals (CollectionHelper.newList ("a", "b", "c"), CollectionHelper.newList ("a", "b", "c")));
    assertTrue (EqualsHelper.equals (CollectionHelper.newUnmodifiableList ("a", "b", "c"),
                                    CollectionHelper.newUnmodifiableList ("a", "b", "c")));
    assertTrue (EqualsHelper.equals (CollectionHelper.newStack ("a", "b", "c"),
                                    CollectionHelper.newStack ("a", "b", "c")));
    assertTrue (EqualsHelper.equals (CollectionHelper.newList ("a", "b", "c").iterator (),
                                    CollectionHelper.newList ("a", "b", "c").iterator ()));
    assertTrue (EqualsHelper.equals (CollectionHelper.getEnumeration ("a", "b", "c"),
                                    CollectionHelper.getEnumeration ("a", "b", "c")));
    assertFalse (EqualsHelper.equals (CollectionHelper.newUnmodifiableList ("a", "b", "c"),
                                     CollectionHelper.newList ("a", "b", "c")));
  }
}
