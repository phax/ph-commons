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
package com.helger.base.numeric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.junit.Test;

/**
 * Additional test class for class {@link BigHelper}, covering the comparison helpers and the
 * conversions.
 *
 * @author Philip Helger
 */
public final class BigHelperExtTest
{
  private static final BigDecimal BD0 = BigDecimal.ZERO;
  private static final BigDecimal BD1 = BigDecimal.ONE;
  private static final BigDecimal BD10 = BigDecimal.TEN;
  private static final BigDecimal BD100 = BigDecimal.valueOf (100);
  private static final BigInteger BI0 = BigInteger.ZERO;
  private static final BigInteger BI1 = BigInteger.ONE;
  private static final BigInteger BI10 = BigInteger.TEN;
  private static final BigInteger BI100 = BigInteger.valueOf (100);

  @Test
  public void testGetDividedBigDecimal ()
  {
    assertEquals (0, BigDecimal.valueOf (5).compareTo (BigHelper.getDividedBigDecimal (10, 2)));
    assertEquals (0,
                  BigDecimal.valueOf (3.33)
                            .compareTo (BigHelper.getDividedBigDecimal (10, 3, 2, RoundingMode.HALF_UP)));
  }

  @Test
  public void testMinMaxAndAbs ()
  {
    assertEquals (BD10, BigHelper.getMaxBigDecimal (BD1, BD10, BD0));
    assertEquals (BD0, BigHelper.getMinBigDecimal (BD1, BD10, BD0));
    assertEquals (BI10, BigHelper.getMaxBigInteger (BI1, BI10, BI0));
    assertEquals (BI0, BigHelper.getMinBigInteger (BI1, BI10, BI0));

    assertEquals (BD1, BigHelper.abs (BigDecimal.valueOf (-1)));
    assertEquals (BD1, BigHelper.abs (BD1));
    assertEquals (BI1, BigHelper.abs (BigInteger.valueOf (-1)));
  }

  @Test
  public void testEqualValues ()
  {
    assertTrue (BigHelper.equalValues (null, null));
    assertTrue (BigHelper.equalValues (BD1, BigDecimal.valueOf (1.00)));
    assertFalse (BigHelper.equalValues (BD1, BD10));
    assertFalse (BigHelper.equalValues (BD1, null));
    assertFalse (BigHelper.equalValues (null, BD1));
  }

  @Test
  public void testBigDecimalComparisonsAgainst0 ()
  {
    assertTrue (BigHelper.isEQ0 (BD0));
    assertFalse (BigHelper.isEQ0 (BD1));
    assertTrue (BigHelper.isNE0 (BD1));
    assertFalse (BigHelper.isNE0 (BD0));
    assertTrue (BigHelper.isLT0 (BigDecimal.valueOf (-1)));
    assertFalse (BigHelper.isLT0 (BD0));
    assertTrue (BigHelper.isLE0 (BD0));
    assertFalse (BigHelper.isLE0 (BD1));
    assertTrue (BigHelper.isGT0 (BD1));
    assertFalse (BigHelper.isGT0 (BD0));
    assertTrue (BigHelper.isGE0 (BD0));
    assertFalse (BigHelper.isGE0 (BigDecimal.valueOf (-1)));
  }

  @Test
  public void testBigDecimalComparisonsAgainst1And10And100 ()
  {
    assertTrue (BigHelper.isEQ1 (BD1));
    assertTrue (BigHelper.isNE1 (BD0));
    assertTrue (BigHelper.isLT1 (BD0));
    assertTrue (BigHelper.isLE1 (BD1));
    assertTrue (BigHelper.isGT1 (BD10));
    assertTrue (BigHelper.isGE1 (BD1));
    assertFalse (BigHelper.isEQ1 (BD0));
    assertFalse (BigHelper.isLT1 (BD1));
    assertFalse (BigHelper.isGT1 (BD1));

    assertTrue (BigHelper.isEQ10 (BD10));
    assertTrue (BigHelper.isNE10 (BD1));
    assertTrue (BigHelper.isLT10 (BD1));
    assertTrue (BigHelper.isLE10 (BD10));
    assertTrue (BigHelper.isGT10 (BD100));
    assertTrue (BigHelper.isGE10 (BD10));
    assertFalse (BigHelper.isEQ10 (BD1));
    assertFalse (BigHelper.isGT10 (BD10));

    assertTrue (BigHelper.isEQ100 (BD100));
    assertTrue (BigHelper.isNE100 (BD10));
    assertTrue (BigHelper.isLT100 (BD10));
    assertTrue (BigHelper.isLE100 (BD100));
    assertTrue (BigHelper.isGT100 (BigDecimal.valueOf (101)));
    assertTrue (BigHelper.isGE100 (BD100));
    assertFalse (BigHelper.isEQ100 (BD10));
    assertFalse (BigHelper.isGT100 (BD100));
  }

  @Test
  public void testBigIntegerComparisons ()
  {
    assertTrue (BigHelper.isEQ0 (BI0));
    assertTrue (BigHelper.isNE0 (BI1));
    assertTrue (BigHelper.isLT0 (BigInteger.valueOf (-1)));
    assertTrue (BigHelper.isLE0 (BI0));
    assertTrue (BigHelper.isGT0 (BI1));
    assertTrue (BigHelper.isGE0 (BI0));
    assertFalse (BigHelper.isEQ0 (BI1));
    assertFalse (BigHelper.isGT0 (BI0));

    assertTrue (BigHelper.isEQ1 (BI1));
    assertTrue (BigHelper.isNE1 (BI0));
    assertTrue (BigHelper.isLT1 (BI0));
    assertTrue (BigHelper.isLE1 (BI1));
    assertTrue (BigHelper.isGT1 (BI10));
    assertTrue (BigHelper.isGE1 (BI1));

    assertTrue (BigHelper.isEQ10 (BI10));
    assertTrue (BigHelper.isNE10 (BI1));
    assertTrue (BigHelper.isLT10 (BI1));
    assertTrue (BigHelper.isLE10 (BI10));
    assertTrue (BigHelper.isGT10 (BI100));
    assertTrue (BigHelper.isGE10 (BI10));

    assertTrue (BigHelper.isEQ100 (BI100));
    assertTrue (BigHelper.isNE100 (BI10));
    assertTrue (BigHelper.isLT100 (BI10));
    assertTrue (BigHelper.isLE100 (BI100));
    assertTrue (BigHelper.isGT100 (BigInteger.valueOf (101)));
    assertTrue (BigHelper.isGE100 (BI100));
  }

  @Test
  public void testGetWithoutTrailingZeroes ()
  {
    assertNull (BigHelper.getWithoutTrailingZeroes ((String) null));
    assertNull (BigHelper.getWithoutTrailingZeroes ((BigDecimal) null));

    assertEquals (0, BigDecimal.valueOf (1).compareTo (BigHelper.getWithoutTrailingZeroes ("1.000")));
    assertEquals (0,
                  BigDecimal.valueOf (1.1).compareTo (BigHelper.getWithoutTrailingZeroes (BigDecimal.valueOf (1.100))));
    assertEquals (0, BigDecimal.ZERO.compareTo (BigHelper.getWithoutTrailingZeroes (BigDecimal.valueOf (0.00))));
  }

  @Test
  public void testGetFractionDigits ()
  {
    assertEquals (0, BigHelper.getFractionDigits (BigDecimal.valueOf (1)));
    assertEquals (2, BigHelper.getFractionDigits (new BigDecimal ("1.23")));
  }

  @Test
  public void testPercent ()
  {
    assertEquals (0, BigDecimal.valueOf (110).compareTo (BigHelper.addPercent (BD100, BigDecimal.TEN)));
    assertEquals (0,
                  BigDecimal.valueOf (110)
                            .compareTo (BigHelper.addPercent (BD100, BigDecimal.TEN, 2, RoundingMode.HALF_UP)));
    assertEquals (0, BigDecimal.valueOf (90).compareTo (BigHelper.subtractPercent (BD100, BigDecimal.TEN)));
    assertEquals (0,
                  BigDecimal.valueOf (90)
                            .compareTo (BigHelper.subtractPercent (BD100, BigDecimal.TEN, 2, RoundingMode.HALF_UP)));
    assertEquals (0, BigDecimal.TEN.compareTo (BigHelper.getPercentValue (BD100, BigDecimal.TEN)));
    assertEquals (0,
                  BigDecimal.TEN.compareTo (BigHelper.getPercentValue (BD100,
                                                                       BigDecimal.TEN,
                                                                       2,
                                                                       RoundingMode.HALF_UP)));
  }

  @Test
  public void testConversions ()
  {
    assertEquals (0, BigDecimal.valueOf (5).compareTo (BigHelper.toBigDecimal (5)));
    assertEquals (0, BigDecimal.valueOf (5).compareTo (BigHelper.toBigDecimal (5L)));
    assertEquals (0, BigDecimal.valueOf (5.5).compareTo (BigHelper.toBigDecimal (5.5f)));
    assertEquals (0, BigDecimal.valueOf (5.5).compareTo (BigHelper.toBigDecimal (5.5d)));
    assertEquals (0, BigDecimal.valueOf (5).compareTo (BigHelper.toBigDecimal (Integer.valueOf (5))));
    assertEquals (0, BigDecimal.valueOf (5).compareTo (BigHelper.toBigDecimal ("5")));
    assertNotNull (BigHelper.toBigDecimal (BigDecimal.ONE));

    assertEquals (BigInteger.valueOf (5), BigHelper.toBigInteger (5));
    assertEquals (BigInteger.valueOf (5), BigHelper.toBigInteger (5L));
    assertEquals (BigInteger.valueOf (5), BigHelper.toBigInteger (Integer.valueOf (5)));
    assertEquals (BigInteger.valueOf (5), BigHelper.toBigInteger ("5"));
    assertNotNull (BigHelper.toBigInteger (BigInteger.ONE));
  }
}
