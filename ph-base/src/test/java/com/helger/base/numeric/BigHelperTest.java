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
package com.helger.base.numeric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Test;

import com.helger.base.CGlobal;

/**
 * Test class for class {@link BigHelper}.
 *
 * @author Philip Helger
 */
public final class BigHelperTest
{

  @Test
  public void testGetDividedBigDecimal ()
  {
    assertEquals (BigHelper.toBigDecimal (1.5), BigHelper.getDividedBigDecimal (3, 2));
    assertEquals (BigHelper.toBigDecimal (1.5), BigHelper.getDividedBigDecimal (3, 2, 1, RoundingMode.HALF_UP));
    assertEquals (BigHelper.toBigDecimal ("1.33"), BigHelper.getDividedBigDecimal (4, 3, 2, RoundingMode.HALF_UP));
    assertEquals (BigHelper.toBigDecimal ("1.333"), BigHelper.getDividedBigDecimal (4, 3, 3, RoundingMode.HALF_UP));

    try
    {
      BigHelper.getDividedBigDecimal (5, 0);
      fail ();
    }
    catch (final ArithmeticException ex)
    {
      // expected
    }

    try
    {
      BigHelper.getDividedBigDecimal (0, 0);
      fail ();
    }
    catch (final ArithmeticException ex)
    {
      // expected
    }

    try
    {
      BigHelper.getDividedBigDecimal (5, 0, 10, RoundingMode.HALF_UP);
      fail ();
    }
    catch (final ArithmeticException ex)
    {
      // expected
    }

    try
    {
      BigHelper.getDividedBigDecimal (0, 0, 10, RoundingMode.HALF_UP);
      fail ();
    }
    catch (final ArithmeticException ex)
    {
      // expected
    }
  }

  @Test
  public void testGetWithoutTrailingZeroes ()
  {
    assertNull (BigHelper.getWithoutTrailingZeroes ((String) null));
    assertNull (BigHelper.getWithoutTrailingZeroes ((BigDecimal) null));

    assertEquals (BigDecimal.ZERO, BigHelper.getWithoutTrailingZeroes (BigDecimal.ZERO));
    assertEquals (BigDecimal.ONE, BigHelper.getWithoutTrailingZeroes (BigDecimal.ONE));
    assertEquals (BigDecimal.TEN, BigHelper.getWithoutTrailingZeroes (BigDecimal.TEN));
    assertEquals (BigDecimal.ONE, BigHelper.getWithoutTrailingZeroes ("1.0000"));
    assertEquals (CGlobal.BIGDEC_100, BigHelper.getWithoutTrailingZeroes ("100.000"));
    assertEquals (new BigDecimal ("100.01"), BigHelper.getWithoutTrailingZeroes ("100.0100"));
    assertEquals (new BigDecimal ("600"), BigHelper.getWithoutTrailingZeroes ("6e2"));
    assertEquals (new BigDecimal ("0.1"), BigHelper.getWithoutTrailingZeroes ("0.1000"));
    assertEquals (new BigDecimal ("0.001"), BigHelper.getWithoutTrailingZeroes ("0.001000"));
    assertEquals (BigDecimal.ZERO, BigHelper.getWithoutTrailingZeroes ("0.00000"));
  }

  @Test
  public void testGetFractionDigits ()
  {
    assertEquals (0, BigHelper.getFractionDigits (new BigDecimal ("-1")));
    assertEquals (0, BigHelper.getFractionDigits (BigDecimal.ZERO));
    assertEquals (0, BigHelper.getFractionDigits (BigDecimal.ONE));
    assertEquals (0, BigHelper.getFractionDigits (BigDecimal.TEN));
    assertEquals (0, BigHelper.getFractionDigits (new BigDecimal ("10.000000")));
    assertEquals (0, BigHelper.getFractionDigits (new BigDecimal (".00000000")));

    assertEquals (1, BigHelper.getFractionDigits (new BigDecimal ("-1.1")));
    assertEquals (1, BigHelper.getFractionDigits (new BigDecimal ("-.1")));
    assertEquals (1, BigHelper.getFractionDigits (new BigDecimal (".1")));
    assertEquals (1, BigHelper.getFractionDigits (new BigDecimal ("0.1")));
    assertEquals (1, BigHelper.getFractionDigits (new BigDecimal ("1.1")));
    assertEquals (1, BigHelper.getFractionDigits (new BigDecimal ("10.1000000000000000")));
    assertEquals (1, BigHelper.getFractionDigits (new BigDecimal ("999999999999.1000000000000000")));

    assertEquals (3, BigHelper.getFractionDigits (new BigDecimal ("-1.102")));
    assertEquals (3, BigHelper.getFractionDigits (new BigDecimal ("-.102")));
    assertEquals (3, BigHelper.getFractionDigits (new BigDecimal (".102")));
    assertEquals (3, BigHelper.getFractionDigits (new BigDecimal ("0.102")));
    assertEquals (3, BigHelper.getFractionDigits (new BigDecimal ("1.102")));
    assertEquals (3, BigHelper.getFractionDigits (new BigDecimal ("10.102000000000000000")));
    assertEquals (3, BigHelper.getFractionDigits (new BigDecimal ("999999999999.102000000000000000")));
  }

  @Test
  public void testAddPercent ()
  {
    assertEquals (new BigDecimal ("100"), BigHelper.addPercent (CGlobal.BIGDEC_100, BigDecimal.ZERO));
    assertEquals (new BigDecimal ("105"), BigHelper.addPercent (CGlobal.BIGDEC_100, BigDecimal.valueOf (5)));
    assertEquals (new BigDecimal ("200"), BigHelper.addPercent (CGlobal.BIGDEC_100, CGlobal.BIGDEC_100));
  }

  @Test
  public void testSubtractPercent ()
  {
    assertEquals (new BigDecimal ("100"), BigHelper.subtractPercent (CGlobal.BIGDEC_100, BigDecimal.ZERO));
    assertEquals (new BigDecimal ("95"), BigHelper.subtractPercent (CGlobal.BIGDEC_100, BigDecimal.valueOf (5)));
    assertEquals (BigDecimal.ZERO, BigHelper.subtractPercent (CGlobal.BIGDEC_100, CGlobal.BIGDEC_100));
    assertEquals (new BigDecimal ("96"), BigHelper.subtractPercent (new BigDecimal ("120"), BigDecimal.valueOf (20)));
  }

  @Test
  public void testGetPercentValue ()
  {
    assertEquals (BigDecimal.ZERO, BigHelper.getPercentValue (CGlobal.BIGDEC_100, BigDecimal.ZERO));
    assertEquals (new BigDecimal ("5"), BigHelper.getPercentValue (CGlobal.BIGDEC_100, BigDecimal.valueOf (5)));
    assertEquals (new BigDecimal ("100"), BigHelper.getPercentValue (CGlobal.BIGDEC_100, CGlobal.BIGDEC_100));
  }
}
