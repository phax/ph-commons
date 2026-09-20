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
package com.helger.base.numeric.mutable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;

import org.junit.Test;

import com.helger.base.BaseTestHelper;
import com.helger.base.state.EChange;

/**
 * Test class for class {@link MutableBigInteger}.
 *
 * @author Philip Helger
 */
public final class MutableBigIntegerTest
{
  @Test
  public void testCtors ()
  {
    assertEquals (BigInteger.valueOf (5), new MutableBigInteger (5L).getAsBigInteger ());
    assertEquals (BigInteger.TEN, new MutableBigInteger (BigInteger.TEN).getAsBigInteger ());
    assertEquals (BigInteger.TEN, new MutableBigInteger (new MutableBigInteger (BigInteger.TEN)).getAsBigInteger ());
    assertNotNull (new MutableBigInteger (1L).toString ());

    try
    {
      new MutableBigInteger ((BigInteger) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {}
  }

  @Test
  public void testNumberValues ()
  {
    final MutableBigInteger a = new MutableBigInteger (7L);
    assertEquals (7d, a.doubleValue (), 0.0001);
    assertEquals (7f, a.floatValue (), 0.0001f);
    assertEquals (7, a.intValue ());
    assertEquals (7L, a.longValue ());
    assertEquals (BigInteger.valueOf (7), a.getAsBigInteger ());
    assertEquals (java.math.BigDecimal.valueOf (7), a.getAsBigDecimal ());

    // The default methods of IMutableNumeric
    assertEquals (Byte.valueOf ((byte) 7), a.getAsByte ());
    assertEquals (Character.valueOf ((char) 7), a.getAsCharacter ());
    assertEquals (Double.valueOf (7), a.getAsDouble ());
    assertEquals (Float.valueOf (7), a.getAsFloat ());
    assertEquals (Integer.valueOf (7), a.getAsInteger ());
    assertEquals (Long.valueOf (7), a.getAsLong ());
    assertEquals (Short.valueOf ((short) 7), a.getAsShort ());
  }

  @Test
  public void testInc ()
  {
    final MutableBigInteger a = new MutableBigInteger (0L);
    assertEquals (BigInteger.ONE, a.inc ());
    assertEquals (BigInteger.valueOf (6), a.inc (5L));
    assertEquals (BigInteger.valueOf (11), a.inc (new MutableBigInteger (5L)));
    assertEquals (BigInteger.valueOf (16), a.inc (BigInteger.valueOf (5)));

    try
    {
      a.inc ((BigInteger) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {}
    try
    {
      a.inc ((MutableBigInteger) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {}
  }

  @Test
  public void testDec ()
  {
    final MutableBigInteger a = new MutableBigInteger (20L);
    assertEquals (BigInteger.valueOf (19), a.dec ());
    assertEquals (BigInteger.valueOf (14), a.dec (5L));
    assertEquals (BigInteger.valueOf (9), a.dec (new MutableBigInteger (5L)));
    assertEquals (BigInteger.valueOf (4), a.dec (BigInteger.valueOf (5)));

    try
    {
      a.dec ((BigInteger) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {}
    try
    {
      a.dec ((MutableBigInteger) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {}
  }

  @Test
  public void testDivideAndMultiply ()
  {
    final MutableBigInteger a = new MutableBigInteger (100L);
    assertEquals (BigInteger.valueOf (50), a.divide (2L));
    assertEquals (BigInteger.valueOf (25), a.divide (new MutableBigInteger (2L)));
    assertEquals (BigInteger.valueOf (5), a.divide (BigInteger.valueOf (5)));

    assertEquals (BigInteger.valueOf (10), a.multiply (2L));
    assertEquals (BigInteger.valueOf (20), a.multiply (new MutableBigInteger (2L)));
    assertEquals (BigInteger.valueOf (100), a.multiply (BigInteger.valueOf (5)));
  }

  @Test
  public void testSet ()
  {
    final MutableBigInteger a = new MutableBigInteger (0L);
    assertSame (EChange.CHANGED, a.set (5L));
    assertSame (EChange.UNCHANGED, a.set (5L));
    assertSame (EChange.CHANGED, a.set (new MutableBigInteger (7L)));
    assertSame (EChange.UNCHANGED, a.set (new MutableBigInteger (7L)));
    assertSame (EChange.CHANGED, a.set (BigInteger.TEN));
    assertSame (EChange.UNCHANGED, a.set (BigInteger.TEN));
  }

  @Test
  public void testComparisons ()
  {
    assertTrue (new MutableBigInteger (0L).is0 ());
    assertFalse (new MutableBigInteger (1L).is0 ());
    assertTrue (new MutableBigInteger (1L).isNot0 ());

    assertTrue (new MutableBigInteger (-1L).isLT0 ());
    assertFalse (new MutableBigInteger (0L).isLT0 ());
    assertTrue (new MutableBigInteger (0L).isLE0 ());
    assertFalse (new MutableBigInteger (1L).isLE0 ());
    assertTrue (new MutableBigInteger (1L).isGT0 ());
    assertFalse (new MutableBigInteger (0L).isGT0 ());
    assertTrue (new MutableBigInteger (0L).isGE0 ());
    assertFalse (new MutableBigInteger (-1L).isGE0 ());
  }

  @Test
  public void testGetAndIncAndGet ()
  {
    final MutableBigInteger a = new MutableBigInteger (5L);
    assertEquals (BigInteger.valueOf (5), a.getAndInc ());
    assertEquals (BigInteger.valueOf (6), a.getAsBigInteger ());
    assertEquals (BigInteger.valueOf (7), a.incAndGet ());
  }

  @Test
  public void testCompareToAndClone ()
  {
    final MutableBigInteger a = new MutableBigInteger (5L);
    assertEquals (0, a.compareTo (new MutableBigInteger (5L)));
    assertTrue (a.compareTo (new MutableBigInteger (6L)) < 0);
    assertTrue (a.compareTo (new MutableBigInteger (4L)) > 0);

    final MutableBigInteger aClone = a.getClone ();
    assertNotSame (a, aClone);
    assertEquals (a, aClone);
  }

  @Test
  public void testEqualsHashcode ()
  {
    BaseTestHelper.testDefaultImplementationWithEqualContentObject (new MutableBigInteger (5L),
                                                                    new MutableBigInteger (5L));
    BaseTestHelper.testDefaultImplementationWithDifferentContentObject (new MutableBigInteger (5L),
                                                                        new MutableBigInteger (6L));
  }
}
