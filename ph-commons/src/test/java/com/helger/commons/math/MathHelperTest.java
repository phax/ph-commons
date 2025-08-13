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
package com.helger.commons.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Test;

import com.helger.base.CGlobal;
import com.helger.commons.mock.CommonsAssert;

/**
 * Test class for class {@link MathHelper}.
 *
 * @author Philip Helger
 */
public final class MathHelperTest
{
  @Test
  public void testGetRoundedUp ()
  {
    assertEquals (8, MathHelper.getRoundedUp (1, 8));
    assertEquals (8, MathHelper.getRoundedUp (8, 8));
    assertEquals (24, MathHelper.getRoundedUp (17, 8));
    assertEquals (80, MathHelper.getRoundedUp (79, 8));
    assertEquals (0, MathHelper.getRoundedUp (-1, 8));
    assertEquals (-8, MathHelper.getRoundedUp (-9, 8));
    assertEquals (-8, MathHelper.getRoundedUp (-15, 8));
    assertEquals (-16, MathHelper.getRoundedUp (-16, 8));

    assertEquals (100, MathHelper.getRoundedUp (1, 100));
    assertEquals (100, MathHelper.getRoundedUp (8, 100));
    assertEquals (100, MathHelper.getRoundedUp (17, 100));
    assertEquals (1200, MathHelper.getRoundedUp (1179, 100));
    assertEquals (0, MathHelper.getRoundedUp (-1, 100));
    assertEquals (0, MathHelper.getRoundedUp (-9, 100));
    assertEquals (0, MathHelper.getRoundedUp (-15, 100));
    assertEquals (0, MathHelper.getRoundedUp (-16, 100));
    assertEquals (-100, MathHelper.getRoundedUp (-100, 100));
    assertEquals (-100, MathHelper.getRoundedUp (-100, 100));
    assertEquals (-100, MathHelper.getRoundedUp (-199, 100));
  }

  @Test
  public void testGetDividedDouble ()
  {
    CommonsAssert.assertEquals (1.5, MathHelper.getDividedDouble (3, 2));
    CommonsAssert.assertEquals (Double.NaN, MathHelper.getDividedDouble (5, 0));
    CommonsAssert.assertEquals (Double.NaN, MathHelper.getDividedDouble (0, 0));
  }

  @Test
  public void testGetDividedBigDecimal ()
  {
    assertEquals (MathHelper.toBigDecimal (1.5), MathHelper.getDividedBigDecimal (3, 2));
    assertEquals (MathHelper.toBigDecimal (1.5), MathHelper.getDividedBigDecimal (3, 2, 1, RoundingMode.HALF_UP));
    assertEquals (MathHelper.toBigDecimal ("1.33"), MathHelper.getDividedBigDecimal (4, 3, 2, RoundingMode.HALF_UP));
    assertEquals (MathHelper.toBigDecimal ("1.333"), MathHelper.getDividedBigDecimal (4, 3, 3, RoundingMode.HALF_UP));

    try
    {
      MathHelper.getDividedBigDecimal (5, 0);
      fail ();
    }
    catch (final ArithmeticException ex)
    {
      // expected
    }

    try
    {
      MathHelper.getDividedBigDecimal (0, 0);
      fail ();
    }
    catch (final ArithmeticException ex)
    {
      // expected
    }

    try
    {
      MathHelper.getDividedBigDecimal (5, 0, 10, RoundingMode.HALF_UP);
      fail ();
    }
    catch (final ArithmeticException ex)
    {
      // expected
    }

    try
    {
      MathHelper.getDividedBigDecimal (0, 0, 10, RoundingMode.HALF_UP);
      fail ();
    }
    catch (final ArithmeticException ex)
    {
      // expected
    }
  }

  @Test
  public void testIntDivide ()
  {
    assertEquals (3, MathHelper.getIntDividedCeil (5, 2));
    assertEquals (2, MathHelper.getIntDividedFloor (5, 2));
    assertEquals (-2, MathHelper.getIntDividedCeil (-5, 2));
    assertEquals (-3, MathHelper.getIntDividedFloor (-5, 2));

    assertEquals (2, MathHelper.getIntDividedCeil (4, 2));
    assertEquals (2, MathHelper.getIntDividedFloor (4, 2));
    assertEquals (-2, MathHelper.getIntDividedCeil (-4, 2));
    assertEquals (-2, MathHelper.getIntDividedFloor (-4, 2));
  }

  @Test
  public void testLongDivide ()
  {
    assertEquals (3, MathHelper.getLongDividedCeil (5, 2));
    assertEquals (2, MathHelper.getLongDividedFloor (5, 2));
    assertEquals (-2, MathHelper.getLongDividedCeil (-5, 2));
    assertEquals (-3, MathHelper.getLongDividedFloor (-5, 2));

    assertEquals (2, MathHelper.getLongDividedCeil (4, 2));
    assertEquals (2, MathHelper.getLongDividedFloor (4, 2));
    assertEquals (-2, MathHelper.getLongDividedCeil (-4, 2));
    assertEquals (-2, MathHelper.getLongDividedFloor (-4, 2));
  }

  @Test
  public void testLongToInt ()
  {
    assertEquals (Integer.MIN_VALUE, MathHelper.getLongAsInt (Integer.MIN_VALUE, 5));
    assertEquals (-1, MathHelper.getLongAsInt (-1, 5));
    assertEquals (0, MathHelper.getLongAsInt (0, 5));
    assertEquals (1, MathHelper.getLongAsInt (1, 5));
    assertEquals (Integer.MAX_VALUE, MathHelper.getLongAsInt (Integer.MAX_VALUE, 5));

    assertEquals (5, MathHelper.getLongAsInt (Integer.MIN_VALUE - 1L, 5));
    assertEquals (5, MathHelper.getLongAsInt (Integer.MAX_VALUE + 1L, 5));
    assertEquals (5, MathHelper.getLongAsInt (Long.MIN_VALUE, 5));
    assertEquals (5, MathHelper.getLongAsInt (Long.MAX_VALUE, 5));
  }

  @Test
  public void testGetMaxInt ()
  {
    assertEquals (5, MathHelper.getMaxInt (5));
    assertEquals (5, MathHelper.getMaxInt (5, 5, 5, 5));
    assertEquals (5, MathHelper.getMaxInt (5, 3, 2, 1));
    assertEquals (7, MathHelper.getMaxInt (5, 3, 7, 4));
  }

  @Test
  public void testGetMaxLong ()
  {
    assertEquals (5, MathHelper.getMaxLong (5));
    assertEquals (5, MathHelper.getMaxLong (5, 5, 5, 5));
    assertEquals (5, MathHelper.getMaxLong (5, 3, 2, 1));
    assertEquals (7, MathHelper.getMaxLong (5, 3, 7, 4));
  }

  @Test
  public void testGetMaxFloat ()
  {
    CommonsAssert.assertEquals (5, MathHelper.getMaxFloat (5));
    CommonsAssert.assertEquals (5, MathHelper.getMaxFloat (5, 5, 5, 5));
    CommonsAssert.assertEquals (5, MathHelper.getMaxFloat (5, 3, 2, 1));
    CommonsAssert.assertEquals (7, MathHelper.getMaxFloat (5, 3, 7, 4));
  }

  @Test
  public void testGetMaxDouble ()
  {
    CommonsAssert.assertEquals (5, MathHelper.getMaxDouble (5));
    CommonsAssert.assertEquals (5, MathHelper.getMaxDouble (5, 5, 5, 5));
    CommonsAssert.assertEquals (5, MathHelper.getMaxDouble (5, 3, 2, 1));
    CommonsAssert.assertEquals (7, MathHelper.getMaxDouble (5, 3, 7, 4));
  }

  @Test
  public void testGetMinInt ()
  {
    assertEquals (5, MathHelper.getMinInt (5));
    assertEquals (5, MathHelper.getMinInt (5, 5, 5, 5));
    assertEquals (1, MathHelper.getMinInt (5, 3, 2, 1));
    assertEquals (3, MathHelper.getMinInt (5, 3, 7, 4));
  }

  @Test
  public void testGetMinLong ()
  {
    assertEquals (5, MathHelper.getMinLong (5));
    assertEquals (5, MathHelper.getMinLong (5, 5, 5, 5));
    assertEquals (1, MathHelper.getMinLong (5, 3, 2, 1));
    assertEquals (3, MathHelper.getMinLong (5, 3, 7, 4));
  }

  @Test
  public void testGetMinFloat ()
  {
    CommonsAssert.assertEquals (5, MathHelper.getMinFloat (5));
    CommonsAssert.assertEquals (5, MathHelper.getMinFloat (5, 5, 5, 5));
    CommonsAssert.assertEquals (1, MathHelper.getMinFloat (5, 3, 2, 1));
    CommonsAssert.assertEquals (3, MathHelper.getMinFloat (5, 3, 7, 4));
  }

  @Test
  public void testGetMinDouble ()
  {
    CommonsAssert.assertEquals (5, MathHelper.getMinDouble (5));
    CommonsAssert.assertEquals (5, MathHelper.getMinDouble (5, 5, 5, 5));
    CommonsAssert.assertEquals (1, MathHelper.getMinDouble (5, 3, 2, 1));
    CommonsAssert.assertEquals (3, MathHelper.getMinDouble (5, 3, 7, 4));
  }

  @Test
  public void testAbsInt ()
  {
    assertEquals (0, MathHelper.abs (0));
    assertEquals (5, MathHelper.abs (-5));
    assertEquals (5, MathHelper.abs (5));
    assertEquals (Integer.MAX_VALUE, MathHelper.abs (Integer.MIN_VALUE + 1));

    try
    {
      MathHelper.abs (Integer.MIN_VALUE);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testAbsLong ()
  {
    assertEquals (0, MathHelper.abs (0L));
    assertEquals (5, MathHelper.abs (-5L));
    assertEquals (5, MathHelper.abs (5L));
    assertEquals (Long.MAX_VALUE, MathHelper.abs (Long.MIN_VALUE + 1));

    try
    {
      MathHelper.abs (Long.MIN_VALUE);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testAbsFloat ()
  {
    CommonsAssert.assertEquals (0F, MathHelper.abs (0F));
    CommonsAssert.assertEquals (5F, MathHelper.abs (-5F));
    CommonsAssert.assertEquals (5F, MathHelper.abs (5F));
    CommonsAssert.assertEquals (Float.MAX_VALUE, MathHelper.abs (-Float.MAX_VALUE));
    CommonsAssert.assertEquals (Float.MIN_VALUE, MathHelper.abs (Float.MIN_VALUE));
  }

  @Test
  public void testAbsDouble ()
  {
    CommonsAssert.assertEquals (0D, MathHelper.abs (0D));
    CommonsAssert.assertEquals (5D, MathHelper.abs (-5D));
    CommonsAssert.assertEquals (5D, MathHelper.abs (5D));
    CommonsAssert.assertEquals (Double.MAX_VALUE, MathHelper.abs (-Double.MAX_VALUE));
    CommonsAssert.assertEquals (Double.MIN_VALUE, MathHelper.abs (Double.MIN_VALUE));
  }

  @Test
  public void testGetUnsignedInt ()
  {
    assertEquals (3554287977L, MathHelper.getUnsignedInt (-740679319));
  }

  @Test
  public void testGetWithoutTrailingZeroes ()
  {
    assertNull (MathHelper.getWithoutTrailingZeroes ((String) null));
    assertNull (MathHelper.getWithoutTrailingZeroes ((BigDecimal) null));

    assertEquals (BigDecimal.ZERO, MathHelper.getWithoutTrailingZeroes (BigDecimal.ZERO));
    assertEquals (BigDecimal.ONE, MathHelper.getWithoutTrailingZeroes (BigDecimal.ONE));
    assertEquals (BigDecimal.TEN, MathHelper.getWithoutTrailingZeroes (BigDecimal.TEN));
    assertEquals (BigDecimal.ONE, MathHelper.getWithoutTrailingZeroes ("1.0000"));
    assertEquals (CGlobal.BIGDEC_100, MathHelper.getWithoutTrailingZeroes ("100.000"));
    assertEquals (new BigDecimal ("100.01"), MathHelper.getWithoutTrailingZeroes ("100.0100"));
    assertEquals (new BigDecimal ("600"), MathHelper.getWithoutTrailingZeroes ("6e2"));
    assertEquals (new BigDecimal ("0.1"), MathHelper.getWithoutTrailingZeroes ("0.1000"));
    assertEquals (new BigDecimal ("0.001"), MathHelper.getWithoutTrailingZeroes ("0.001000"));
    assertEquals (BigDecimal.ZERO, MathHelper.getWithoutTrailingZeroes ("0.00000"));
  }

  @Test
  public void testGetFractionDigits ()
  {
    assertEquals (0, MathHelper.getFractionDigits (new BigDecimal ("-1")));
    assertEquals (0, MathHelper.getFractionDigits (BigDecimal.ZERO));
    assertEquals (0, MathHelper.getFractionDigits (BigDecimal.ONE));
    assertEquals (0, MathHelper.getFractionDigits (BigDecimal.TEN));
    assertEquals (0, MathHelper.getFractionDigits (new BigDecimal ("10.000000")));
    assertEquals (0, MathHelper.getFractionDigits (new BigDecimal (".00000000")));

    assertEquals (1, MathHelper.getFractionDigits (new BigDecimal ("-1.1")));
    assertEquals (1, MathHelper.getFractionDigits (new BigDecimal ("-.1")));
    assertEquals (1, MathHelper.getFractionDigits (new BigDecimal (".1")));
    assertEquals (1, MathHelper.getFractionDigits (new BigDecimal ("0.1")));
    assertEquals (1, MathHelper.getFractionDigits (new BigDecimal ("1.1")));
    assertEquals (1, MathHelper.getFractionDigits (new BigDecimal ("10.1000000000000000")));
    assertEquals (1, MathHelper.getFractionDigits (new BigDecimal ("999999999999.1000000000000000")));

    assertEquals (3, MathHelper.getFractionDigits (new BigDecimal ("-1.102")));
    assertEquals (3, MathHelper.getFractionDigits (new BigDecimal ("-.102")));
    assertEquals (3, MathHelper.getFractionDigits (new BigDecimal (".102")));
    assertEquals (3, MathHelper.getFractionDigits (new BigDecimal ("0.102")));
    assertEquals (3, MathHelper.getFractionDigits (new BigDecimal ("1.102")));
    assertEquals (3, MathHelper.getFractionDigits (new BigDecimal ("10.102000000000000000")));
    assertEquals (3, MathHelper.getFractionDigits (new BigDecimal ("999999999999.102000000000000000")));
  }

  @Test
  public void testAddPercent ()
  {
    assertEquals (new BigDecimal ("100"), MathHelper.addPercent (CGlobal.BIGDEC_100, BigDecimal.ZERO));
    assertEquals (new BigDecimal ("105"), MathHelper.addPercent (CGlobal.BIGDEC_100, BigDecimal.valueOf (5)));
    assertEquals (new BigDecimal ("200"), MathHelper.addPercent (CGlobal.BIGDEC_100, CGlobal.BIGDEC_100));
  }

  @Test
  public void testSubtractPercent ()
  {
    assertEquals (new BigDecimal ("100"), MathHelper.subtractPercent (CGlobal.BIGDEC_100, BigDecimal.ZERO));
    assertEquals (new BigDecimal ("95"), MathHelper.subtractPercent (CGlobal.BIGDEC_100, BigDecimal.valueOf (5)));
    assertEquals (BigDecimal.ZERO, MathHelper.subtractPercent (CGlobal.BIGDEC_100, CGlobal.BIGDEC_100));
    assertEquals (new BigDecimal ("96"), MathHelper.subtractPercent (new BigDecimal ("120"), BigDecimal.valueOf (20)));
  }

  @Test
  public void testGetPercentValue ()
  {
    assertEquals (BigDecimal.ZERO, MathHelper.getPercentValue (CGlobal.BIGDEC_100, BigDecimal.ZERO));
    assertEquals (new BigDecimal ("5"), MathHelper.getPercentValue (CGlobal.BIGDEC_100, BigDecimal.valueOf (5)));
    assertEquals (new BigDecimal ("100"), MathHelper.getPercentValue (CGlobal.BIGDEC_100, CGlobal.BIGDEC_100));
  }

  @Test
  public void testIsExactlyOneBitSetToOneInt ()
  {
    for (int i = 0; i < 32; ++i)
    {
      final int nValue = 1 << i;
      assertTrue (MathHelper.isExactlyOneBitSetToOne (nValue));
      if (i > 0)
      {
        // for i == 0, nValue is 1 and 1+1 is 2 which is also a single bit
        // (0x10)
        assertFalse (MathHelper.isExactlyOneBitSetToOne (nValue + 1));
      }
    }
  }

  @Test
  public void testIsExactlyOneBitSetToOneLong ()
  {
    for (int i = 0; i < 64; ++i)
    {
      final long nValue = 1L << i;
      assertTrue (MathHelper.isExactlyOneBitSetToOne (nValue));
      if (i > 0)
      {
        // for i == 0, nValue is 1 and 1+1 is 2 which is also a single bit
        // (0x10)
        assertFalse (MathHelper.isExactlyOneBitSetToOne (nValue + 1));
      }
    }
  }
}
