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

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Test;

import com.helger.base.BaseTestHelper;
import com.helger.base.state.EChange;

/**
 * Test class for class {@link MutableBigDecimal}.
 *
 * @author Philip Helger
 */
public final class MutableBigDecimalTest
{
  private static final int SCALE = 2;
  private static final RoundingMode RM = RoundingMode.HALF_UP;

  @Test
  public void testCtors ()
  {
    assertEquals (0, BigDecimal.valueOf (5).compareTo (new MutableBigDecimal (5L).getAsBigDecimal ()));
    assertEquals (0, BigDecimal.valueOf (5.5).compareTo (new MutableBigDecimal (5.5).getAsBigDecimal ()));
    assertEquals (0, BigDecimal.TEN.compareTo (new MutableBigDecimal (BigDecimal.TEN).getAsBigDecimal ()));
    assertEquals (0,
                  BigDecimal.TEN.compareTo (new MutableBigDecimal (new MutableBigDecimal (BigDecimal.TEN)).getAsBigDecimal ()));
    assertNotNull (new MutableBigDecimal (1L).toString ());

    try
    {
      new MutableBigDecimal ((BigDecimal) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {}
  }

  @Test
  public void testNumberValues ()
  {
    final MutableBigDecimal a = new MutableBigDecimal (7L);
    assertEquals (7d, a.doubleValue (), 0.0001);
    assertEquals (7f, a.floatValue (), 0.0001f);
    assertEquals (7, a.intValue ());
    assertEquals (7L, a.longValue ());
    assertEquals (java.math.BigInteger.valueOf (7), a.getAsBigInteger ());

    assertEquals (Byte.valueOf ((byte) 7), a.getAsByte ());
    assertEquals (Double.valueOf (7), a.getAsDouble ());
    assertEquals (Integer.valueOf (7), a.getAsInteger ());
    assertEquals (Long.valueOf (7), a.getAsLong ());
  }

  @Test
  public void testInc ()
  {
    final MutableBigDecimal a = new MutableBigDecimal (0L);
    assertEquals (0, BigDecimal.ONE.compareTo (a.inc ()));
    assertEquals (0, BigDecimal.valueOf (6).compareTo (a.inc (5L)));
    assertEquals (0, BigDecimal.valueOf (11.5).compareTo (a.inc (5.5)));
    assertEquals (0, BigDecimal.valueOf (16.5).compareTo (a.inc (new MutableBigDecimal (5L))));
    assertEquals (0, BigDecimal.valueOf (21.5).compareTo (a.inc (BigDecimal.valueOf (5))));

    try
    {
      a.inc ((BigDecimal) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {}
    try
    {
      a.inc ((MutableBigDecimal) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {}
  }

  @Test
  public void testDec ()
  {
    final MutableBigDecimal a = new MutableBigDecimal (30L);
    assertEquals (0, BigDecimal.valueOf (29).compareTo (a.dec ()));
    assertEquals (0, BigDecimal.valueOf (24).compareTo (a.dec (5L)));
    assertEquals (0, BigDecimal.valueOf (18.5).compareTo (a.dec (5.5)));
    assertEquals (0, BigDecimal.valueOf (13.5).compareTo (a.dec (new MutableBigDecimal (5L))));
    assertEquals (0, BigDecimal.valueOf (8.5).compareTo (a.dec (BigDecimal.valueOf (5))));

    try
    {
      a.dec ((BigDecimal) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {}
    try
    {
      a.dec ((MutableBigDecimal) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {}
  }

  @Test
  public void testDivideAndMultiply ()
  {
    final MutableBigDecimal a = new MutableBigDecimal (100L);
    assertEquals (0, BigDecimal.valueOf (50).compareTo (a.divide (2L, SCALE, RM)));
    assertEquals (0, BigDecimal.valueOf (25).compareTo (a.divide (2.0, SCALE, RM)));
    assertEquals (0, BigDecimal.valueOf (12.5).compareTo (a.divide (new MutableBigDecimal (2L), SCALE, RM)));
    assertEquals (0, BigDecimal.valueOf (6.25).compareTo (a.divide (BigDecimal.valueOf (2), SCALE, RM)));

    assertEquals (0, BigDecimal.valueOf (12.5).compareTo (a.multiply (2L)));
    assertEquals (0, BigDecimal.valueOf (25).compareTo (a.multiply (2.0)));
    assertEquals (0, BigDecimal.valueOf (50).compareTo (a.multiply (new MutableBigDecimal (2L))));
    assertEquals (0, BigDecimal.valueOf (100).compareTo (a.multiply (BigDecimal.valueOf (2))));
  }

  @Test
  public void testSet ()
  {
    final MutableBigDecimal a = new MutableBigDecimal (0L);
    assertSame (EChange.CHANGED, a.set (5L));
    assertSame (EChange.UNCHANGED, a.set (5L));
    assertSame (EChange.CHANGED, a.set (5.5));
    assertSame (EChange.UNCHANGED, a.set (5.5));
    assertSame (EChange.CHANGED, a.set (new MutableBigDecimal (7L)));
    assertSame (EChange.UNCHANGED, a.set (new MutableBigDecimal (7L)));
    assertSame (EChange.CHANGED, a.set (BigDecimal.TEN));
    assertSame (EChange.UNCHANGED, a.set (BigDecimal.TEN));
  }

  @Test
  public void testComparisons ()
  {
    assertTrue (new MutableBigDecimal (0L).is0 ());
    assertFalse (new MutableBigDecimal (1L).is0 ());
    assertTrue (new MutableBigDecimal (1L).isNot0 ());

    assertTrue (new MutableBigDecimal (-1L).isLT0 ());
    assertFalse (new MutableBigDecimal (0L).isLT0 ());
    assertTrue (new MutableBigDecimal (0L).isLE0 ());
    assertFalse (new MutableBigDecimal (1L).isLE0 ());
    assertTrue (new MutableBigDecimal (1L).isGT0 ());
    assertFalse (new MutableBigDecimal (0L).isGT0 ());
    assertTrue (new MutableBigDecimal (0L).isGE0 ());
    assertFalse (new MutableBigDecimal (-1L).isGE0 ());
  }

  @Test
  public void testGetAndIncAndGet ()
  {
    final MutableBigDecimal a = new MutableBigDecimal (5L);
    assertEquals (0, BigDecimal.valueOf (5).compareTo (a.getAndInc ()));
    assertEquals (0, BigDecimal.valueOf (6).compareTo (a.getAsBigDecimal ()));
    assertEquals (0, BigDecimal.valueOf (7).compareTo (a.incAndGet ()));
  }

  @Test
  public void testCompareToAndClone ()
  {
    final MutableBigDecimal a = new MutableBigDecimal (5L);
    assertEquals (0, a.compareTo (new MutableBigDecimal (5L)));
    assertTrue (a.compareTo (new MutableBigDecimal (6L)) < 0);
    assertTrue (a.compareTo (new MutableBigDecimal (4L)) > 0);

    final MutableBigDecimal aClone = a.getClone ();
    assertNotSame (a, aClone);
    assertEquals (a, aClone);
  }

  @Test
  public void testEqualsHashcode ()
  {
    BaseTestHelper.testDefaultImplementationWithEqualContentObject (new MutableBigDecimal (5L),
                                                                    new MutableBigDecimal (5L));
    BaseTestHelper.testDefaultImplementationWithDifferentContentObject (new MutableBigDecimal (5L),
                                                                        new MutableBigDecimal (6L));
  }
}
