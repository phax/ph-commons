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
package com.helger.commons.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.equals.EqualsHelper;

/**
 * Contains several math help routines.
 *
 * @author Philip Helger
 */
@Immutable
public final class MathHelper
{
  private static final long LONG_HIGH_BITS = 0xFFFFFFFF80000000L;

  @PresentForCodeCoverage
  private static final MathHelper s_aInstance = new MathHelper ();

  private MathHelper ()
  {}

  /**
   * Divides the passed int dividend through the passed divisor (nDividend /
   * nDivisor)
   *
   * @param nDividend
   *        the dividend
   * @param nDivisor
   *        the divisor
   * @return a double representing the exact quotient. Returns
   *         {@link Double#NaN} if the divisor is 0.
   */
  public static double getDividedDouble (final int nDividend, final int nDivisor)
  {
    final double dDividend = nDividend;
    final double dDivisor = nDivisor;
    return dDividend / dDivisor;
  }

  /**
   * Get the division result using {@link BigDecimal}.
   *
   * @param nDividend
   *        the dividend
   * @param nDivisor
   *        the divisor
   * @return the result of the division
   * @throws ArithmeticException
   *         if the divisor is 0.
   */
  @Nonnull
  public static BigDecimal getDividedBigDecimal (final int nDividend, final int nDivisor) throws ArithmeticException
  {
    final BigDecimal aDividend = BigDecimal.valueOf (nDividend);
    final BigDecimal aDivisor = BigDecimal.valueOf (nDivisor);
    return aDividend.divide (aDivisor);
  }

  public static int getIntDividedCeil (final int nDividend, final int nDivisor)
  {
    return getIntDivided (nDividend, nDivisor, RoundingMode.CEILING);
  }

  public static int getIntDividedFloor (final int nDividend, final int nDivisor)
  {
    return getIntDivided (nDividend, nDivisor, RoundingMode.FLOOR);
  }

  public static int getIntDivided (final int nDividend, final int nDivisor, @Nonnull final RoundingMode eRoundingMode)
  {
    return toBigDecimal (nDividend).divide (toBigDecimal (nDivisor), eRoundingMode).intValue ();
  }

  public static long getLongDividedCeil (final long nDividend, final long nDivisor)
  {
    return getLongDivided (nDividend, nDivisor, RoundingMode.CEILING);
  }

  public static long getLongDividedFloor (final long nDividend, final long nDivisor)
  {
    return getLongDivided (nDividend, nDivisor, RoundingMode.FLOOR);
  }

  public static long getLongDivided (final long nDividend,
                                     final long nDivisor,
                                     @Nonnull final RoundingMode eRoundingMode)
  {
    return toBigDecimal (nDividend).divide (toBigDecimal (nDivisor), eRoundingMode).longValue ();
  }

  public static boolean canConvertLongToInt (final long nValue)
  {
    return (nValue & LONG_HIGH_BITS) == 0 || (nValue & LONG_HIGH_BITS) == LONG_HIGH_BITS;
  }

  @CheckReturnValue
  public static int getLongAsInt (final long nValue, final int nFallback)
  {
    return canConvertLongToInt (nValue) ? (int) nValue : nFallback;
  }

  public static int getMaxInt (final int nValue, @Nonnull final int... aValues)
  {
    int ret = nValue;
    for (final int n : aValues)
      ret = Math.max (ret, n);
    return ret;
  }

  public static long getMaxLong (final long nValue, @Nonnull final long... aValues)
  {
    long ret = nValue;
    for (final long n : aValues)
      ret = Math.max (ret, n);
    return ret;
  }

  public static double getMaxFloat (final float fValue, @Nonnull final float... aValues)
  {
    float ret = fValue;
    for (final float f : aValues)
      ret = Math.max (ret, f);
    return ret;
  }

  public static double getMaxDouble (final double dValue, @Nonnull final double... aValues)
  {
    double ret = dValue;
    for (final double d : aValues)
      ret = Math.max (ret, d);
    return ret;
  }

  @Nonnull
  public static BigDecimal getMaxBigDecimal (@Nonnull final BigDecimal aValue, @Nonnull final BigDecimal... aValues)
  {
    BigDecimal ret = aValue;
    for (final BigDecimal a : aValues)
      if (a.compareTo (ret) > 0)
        ret = a;
    return ret;
  }

  @Nonnull
  public static BigInteger getMaxBigInteger (@Nonnull final BigInteger aValue, @Nonnull final BigInteger... aValues)
  {
    BigInteger ret = aValue;
    for (final BigInteger a : aValues)
      if (a.compareTo (ret) > 0)
        ret = a;
    return ret;
  }

  public static int getMinInt (final int nValue, @Nonnull final int... aValues)
  {
    int ret = nValue;
    for (final int n : aValues)
      ret = Math.min (ret, n);
    return ret;
  }

  public static long getMinLong (final long nValue, @Nonnull final long... aValues)
  {
    long ret = nValue;
    for (final long n : aValues)
      ret = Math.min (ret, n);
    return ret;
  }

  public static double getMinFloat (final float fValue, @Nonnull final float... aValues)
  {
    float ret = fValue;
    for (final float f : aValues)
      ret = Math.min (ret, f);
    return ret;
  }

  public static double getMinDouble (final double dValue, @Nonnull final double... aValues)
  {
    double ret = dValue;
    for (final double d : aValues)
      ret = Math.min (ret, d);
    return ret;
  }

  @Nonnull
  public static BigDecimal getMinBigDecimal (@Nonnull final BigDecimal aValue, @Nonnull final BigDecimal... aValues)
  {
    BigDecimal ret = aValue;
    for (final BigDecimal a : aValues)
      if (a.compareTo (ret) < 0)
        ret = a;
    return ret;
  }

  @Nonnull
  public static BigInteger getMinBigInteger (@Nonnull final BigInteger aValue, @Nonnull final BigInteger... aValues)
  {
    BigInteger ret = aValue;
    for (final BigInteger a : aValues)
      if (a.compareTo (ret) < 0)
        ret = a;
    return ret;
  }

  /**
   * This is a fix for <code>Math.abs</code> as it would return
   * {@link Integer#MIN_VALUE} for {@link Integer#MIN_VALUE} which is very
   * unexpected. Instead an exception is thrown.
   *
   * @param nValue
   *        Input value
   * @return the absolute value of the argument.
   * @throws IllegalArgumentException
   *         if the input value is {@link Integer#MIN_VALUE}
   */
  @Nonnegative
  public static int abs (final int nValue)
  {
    // As Integer.MIN_VALUE is -2^31 and Integer.MAX_VALUE is 2^31-1 it means
    // that there is not integer value matching 2^31!!!
    if (nValue == Integer.MIN_VALUE)
      throw new IllegalArgumentException ("There is not absolute value for Integer.MIN_VALUE!");
    return Math.abs (nValue);
  }

  /**
   * This is a fix for <code>Math.abs</code> as it would return
   * {@link Long#MIN_VALUE} for {@link Long#MIN_VALUE} which is very unexpected.
   * Instead an exception is thrown.
   *
   * @param nValue
   *        Input value
   * @return the absolute value of the argument.
   * @throws IllegalArgumentException
   *         if the input value is {@link Long#MIN_VALUE}
   */
  @Nonnegative
  public static long abs (final long nValue)
  {
    // As Long.MIN_VALUE is -2^63 and Integer.MAX_VALUE is 2^63-1 it means
    // that there is not integer value matching 2^63!!!
    if (nValue == Long.MIN_VALUE)
      throw new IllegalArgumentException ("There is not absolute value for Long.MIN_VALUE!");
    return Math.abs (nValue);
  }

  /**
   * This is a sanity method wrapping <code>Math.abs (float)</code>, so that you
   * don't have to think whether you need to invoke the abs method from this
   * class or the one from Math directly.
   *
   * @param fValue
   *        Input value
   * @return the absolute value of the argument.
   */
  @Nonnegative
  public static float abs (final float fValue)
  {
    return Math.abs (fValue);
  }

  /**
   * This is a sanity method wrapping <code>Math.abs (double)</code>, so that
   * you don't have to think whether you need to invoke the abs method from this
   * class or the one from Math directly.
   *
   * @param dValue
   *        Input value
   * @return the absolute value of the argument.
   */
  @Nonnegative
  public static double abs (final double dValue)
  {
    return Math.abs (dValue);
  }

  /**
   * This is a sanity method wrapping <code>BigDecimal.abs (double)</code>, so
   * that you don't have to think whether you need to invoke the abs method from
   * this class or the one from BigDecimal directly.
   *
   * @param aValue
   *        Input value
   * @return the absolute value of the argument.
   */
  @Nonnull
  public static BigDecimal abs (@Nonnull final BigDecimal aValue)
  {
    return aValue.abs ();
  }

  /**
   * This is a sanity method wrapping <code>BigInteger.abs (double)</code>, so
   * that you don't have to think whether you need to invoke the abs method from
   * this class or the one from BigInteger directly.
   *
   * @param aValue
   *        Input value
   * @return the absolute value of the argument.
   */
  @Nonnull
  public static BigInteger abs (@Nonnull final BigInteger aValue)
  {
    return aValue.abs ();
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is = 0.
   */
  public static boolean isEqualToZero (@Nonnull final BigDecimal aValue)
  {
    return EqualsHelper.equals (aValue, BigDecimal.ZERO);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is != 0.
   */
  public static boolean isNotEqualToZero (@Nonnull final BigDecimal aValue)
  {
    return !EqualsHelper.equals (aValue, BigDecimal.ZERO);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &lt; 0.
   */
  public static boolean isLowerThanZero (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.ZERO) < 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &le; 0.
   */
  public static boolean isLowerOrEqualThanZero (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.ZERO) <= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &gt; 0.
   */
  public static boolean isGreaterThanZero (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.ZERO) > 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &ge; 0.
   */
  public static boolean isGreaterOrEqualThanZero (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.ZERO) >= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is = 1.
   */
  public static boolean isEqualToOne (@Nonnull final BigDecimal aValue)
  {
    return EqualsHelper.equals (aValue, BigDecimal.ONE);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is != 1.
   */
  public static boolean isNotEqualToOne (@Nonnull final BigDecimal aValue)
  {
    return !EqualsHelper.equals (aValue, BigDecimal.ONE);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &lt; 1.
   */
  public static boolean isLowerThanOne (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.ONE) < 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &le; 1.
   */
  public static boolean isLowerOrEqualThanOne (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.ONE) <= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &gt; 1.
   */
  public static boolean isGreaterThanOne (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.ONE) > 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &ge; 1.
   */
  public static boolean isGreaterOrEqualThanOne (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.ONE) >= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is = 10.
   */
  public static boolean isEqualToTen (@Nonnull final BigDecimal aValue)
  {
    return EqualsHelper.equals (aValue, BigDecimal.TEN);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is != 10.
   */
  public static boolean isNotEqualToTen (@Nonnull final BigDecimal aValue)
  {
    return !EqualsHelper.equals (aValue, BigDecimal.TEN);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &lt; 10.
   */
  public static boolean isLowerThanTen (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.TEN) < 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &le; 10.
   */
  public static boolean isLowerOrEqualThanTen (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.TEN) <= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &gt; 10.
   */
  public static boolean isGreaterThanTen (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.TEN) > 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &ge; 10.
   */
  public static boolean isGreaterOrEqualThanTen (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.TEN) >= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is = 100.
   */
  public static boolean isEqualTo100 (@Nonnull final BigDecimal aValue)
  {
    return EqualsHelper.equals (aValue, CGlobal.BIGDEC_100);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is != 100.
   */
  public static boolean isNotEqualTo100 (@Nonnull final BigDecimal aValue)
  {
    return !EqualsHelper.equals (aValue, CGlobal.BIGDEC_100);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &lt; 100.
   */
  public static boolean isLowerThan100 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (CGlobal.BIGDEC_100) < 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &le; 100.
   */
  public static boolean isLowerOrEqualThan100 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (CGlobal.BIGDEC_100) <= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &gt; 100.
   */
  public static boolean isGreaterThan100 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (CGlobal.BIGDEC_100) > 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &ge; 100.
   */
  public static boolean isGreaterOrEqualThan100 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (CGlobal.BIGDEC_100) >= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is = 0.
   */
  public static boolean isEqualToZero (@Nonnull final BigInteger aValue)
  {
    return EqualsHelper.equals (aValue, BigInteger.ZERO);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is != 0.
   */
  public static boolean isNotEqualToZero (@Nonnull final BigInteger aValue)
  {
    return !EqualsHelper.equals (aValue, BigInteger.ZERO);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &lt; 0.
   */
  public static boolean isLowerThanZero (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.ZERO) < 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &le; 0.
   */
  public static boolean isLowerOrEqualThanZero (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.ZERO) <= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &gt; 0.
   */
  public static boolean isGreaterThanZero (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.ZERO) > 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &ge; 0.
   */
  public static boolean isGreaterOrEqualThanZero (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.ZERO) >= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is = 1.
   */
  public static boolean isEqualToOne (@Nonnull final BigInteger aValue)
  {
    return EqualsHelper.equals (aValue, BigInteger.ONE);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is != 1.
   */
  public static boolean isNotEqualToOne (@Nonnull final BigInteger aValue)
  {
    return !EqualsHelper.equals (aValue, BigInteger.ONE);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &lt; 1.
   */
  public static boolean isLowerThanOne (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.ONE) < 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &le; 1.
   */
  public static boolean isLowerOrEqualThanOne (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.ONE) <= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &gt; 1.
   */
  public static boolean isGreaterThanOne (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.ONE) > 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &ge; 1.
   */
  public static boolean isGreaterOrEqualThanOne (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.ONE) >= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is = 10.
   */
  public static boolean isEqualToTen (@Nonnull final BigInteger aValue)
  {
    return EqualsHelper.equals (aValue, BigInteger.TEN);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is != 10.
   */
  public static boolean isNotEqualToTen (@Nonnull final BigInteger aValue)
  {
    return !EqualsHelper.equals (aValue, BigInteger.TEN);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &lt; 10.
   */
  public static boolean isLowerThanTen (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.TEN) < 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &le; 10.
   */
  public static boolean isLowerOrEqualThanTen (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.TEN) <= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &gt; 10.
   */
  public static boolean isGreaterThanTen (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.TEN) > 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &ge; 10.
   */
  public static boolean isGreaterOrEqualThanTen (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.TEN) >= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is = 100.
   */
  public static boolean isEqualTo100 (@Nonnull final BigInteger aValue)
  {
    return EqualsHelper.equals (aValue, CGlobal.BIGINT_100);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is != 100.
   */
  public static boolean isNotEqualTo100 (@Nonnull final BigInteger aValue)
  {
    return !EqualsHelper.equals (aValue, CGlobal.BIGINT_100);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &lt; 100.
   */
  public static boolean isLowerThan100 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (CGlobal.BIGINT_100) < 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &le; 100.
   */
  public static boolean isLowerOrEqualThan100 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (CGlobal.BIGINT_100) <= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &gt; 100.
   */
  public static boolean isGreaterThan100 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (CGlobal.BIGINT_100) > 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &ge; 100.
   */
  public static boolean isGreaterOrEqualThan100 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (CGlobal.BIGINT_100) >= 0;
  }

  /**
   * Get the passed String as a BigDecimal without any trailing zeroes.
   *
   * @param sValue
   *        The String to be used as a BigDecimal to be modified. May be
   *        <code>null</code>.
   * @return <code>null</code> if the input value is <code>null</code>.
   */
  @Nullable
  @CheckReturnValue
  public static BigDecimal getWithoutTrailingZeroes (@Nullable final String sValue)
  {
    if (sValue == null)
      return null;

    return getWithoutTrailingZeroes (new BigDecimal (sValue));
  }

  /**
   * Get the passed BigDecimal without any trailing zeroes. Examples:
   * <ul>
   * <li>new BigDecimal ("0.00000000") --&gt; 0</li>
   * <li>new BigDecimal ("10") --&gt; 10</li>
   * <li>new BigDecimal ("10.00000000") --&gt; 10</li>
   * <li>new BigDecimal ("10.1") --&gt; 10.1</li>
   * <li>new BigDecimal ("10.10000000") --&gt; 10.1</li>
   * <li>new BigDecimal ("10.345") --&gt; 10.345</li>
   * <li>new BigDecimal ("10.3450000000") --&gt; 10.345</li>
   * </ul>
   *
   * @param aValue
   *        The BigDecimal to be modified. May be <code>null</code>.
   * @return <code>null</code> if the input value is <code>null</code>.
   */
  @Nullable
  @CheckReturnValue
  public static BigDecimal getWithoutTrailingZeroes (@Nullable final BigDecimal aValue)
  {
    if (aValue == null)
      return null;

    // stripTrailingZeros does not work for "0"!
    if (BigDecimal.ZERO.compareTo (aValue) == 0)
      return BigDecimal.ZERO;

    final BigDecimal ret = aValue.stripTrailingZeros ();
    // Avoid stuff like "6E2"
    return ret.scale () >= 0 ? ret : ret.setScale (0);
  }

  /**
   * Get the number of effective fraction digits by the specified BigDecimal.
   * Examples:
   * <ul>
   * <li>new BigDecimal ("10") --&gt; 0</li>
   * <li>new BigDecimal ("10.00000000") --&gt; 0</li>
   * <li>new BigDecimal ("10.1") --&gt; 1</li>
   * <li>new BigDecimal ("10.10000000") --&gt; 1</li>
   * <li>new BigDecimal ("10.345") --&gt; 3</li>
   * <li>new BigDecimal ("10.3450000000") --&gt; 3</li>
   * </ul>
   *
   * @param aBD
   *        The BigDecimal to check. May not be <code>null</code>.
   * @return The minimum number of fraction digits. Always &ge; 0.
   */
  @Nonnegative
  public static int getFractionDigits (@Nonnull final BigDecimal aBD)
  {
    return getWithoutTrailingZeroes (aBD).scale ();
  }

  /**
   * Add x% to base
   *
   * @param aBase
   *        Base value. May not be <code>null</code>.
   * @param aPercentage
   *        Percentage value (0-100). May not be <code>null</code>.
   * @return base + x% (<code>=aBase * (100 + perc) / 100</code>). Never
   *         <code>null</code>.
   */
  @Nonnull
  public static BigDecimal addPercent (@Nonnull final BigDecimal aBase, @Nonnull final BigDecimal aPercentage)
  {
    return aBase.multiply (CGlobal.BIGDEC_100.add (aPercentage)).divide (CGlobal.BIGDEC_100);
  }

  /**
   * Subtract x% from base
   *
   * @param aBase
   *        Base value. May not be <code>null</code>.
   * @param aPercentage
   *        Percentage value (0-100). May not be <code>null</code>.
   * @return base - x% (<code>=aBase * (100 - perc) / 100</code>). Never
   *         <code>null</code>.
   */
  @Nonnull
  public static BigDecimal subtractPercent (@Nonnull final BigDecimal aBase, @Nonnull final BigDecimal aPercentage)
  {
    return aBase.multiply (CGlobal.BIGDEC_100.subtract (aPercentage)).divide (CGlobal.BIGDEC_100);
  }

  /**
   * Get x% from base
   *
   * @param aBase
   *        Base value. May not be <code>null</code>.
   * @param aPercentage
   *        Percentage value (0-100). May not be <code>null</code>.
   * @return x% from base (<code>=aBase * perc / 100</code>). Never
   *         <code>null</code>.
   */
  @Nonnull
  public static BigDecimal getPercentValue (@Nonnull final BigDecimal aBase, @Nonnull final BigDecimal aPercentage)
  {
    return aBase.multiply (aPercentage).divide (CGlobal.BIGDEC_100);
  }

  /**
   * Get x% from base with rounding etc.
   *
   * @param aBase
   *        Base value. May not be <code>null</code>.
   * @param aPercentage
   *        Percentage value (0-100). May not be <code>null</code>.
   * @param nScale
   *        Maximum scale to achieve.
   * @param eRoundingMode
   *        Rounding mode to used. May not be <code>null</code>.
   * @return x% from base (<code>=aBase * perc / 100</code>). Never
   *         <code>null</code>.
   */
  @Nonnull
  public static BigDecimal getPercentValue (@Nonnull final BigDecimal aBase,
                                            @Nonnull final BigDecimal aPercentage,
                                            @Nonnegative final int nScale,
                                            @Nonnull final RoundingMode eRoundingMode)
  {
    return aBase.multiply (aPercentage).divide (CGlobal.BIGDEC_100, nScale, eRoundingMode);
  }

  /**
   * @param a
   *        a
   * @param b
   *        b
   * @return sqrt(a*a + b*b) without under/overflow.
   */
  public static double hypot (final double a, final double b)
  {
    if (a == 0)
      return b;
    if (b == 0)
      return a;

    double r;
    final double dAbsA = abs (a);
    final double dAbsB = abs (b);
    if (dAbsA > dAbsB)
    {
      r = b / a;
      r = dAbsA * Math.sqrt (1 + r * r);
    }
    else
    {
      r = a / b;
      r = dAbsB * Math.sqrt (1 + r * r);
    }
    return r;
  }

  /**
   * Converts the passed signed integer to an unsigned long
   *
   * @param n
   *        Source int
   * @return The unsigned long
   */
  public static long getUnsignedInt (final int n)
  {
    return n & 0x00000000ffffffffL;
  }

  @Nonnull
  public static BigDecimal toBigDecimal (final int n)
  {
    // Compared to new BigDecimal(n) this may return constants
    return BigDecimal.valueOf (n);
  }

  @Nonnull
  public static BigDecimal toBigDecimal (final long n)
  {
    // Compared to new BigDecimal(n) this may return constants
    return BigDecimal.valueOf (n);
  }

  @Nonnull
  public static BigDecimal toBigDecimal (final float f)
  {
    return BigDecimal.valueOf (f);
  }

  @Nonnull
  public static BigDecimal toBigDecimal (final double d)
  {
    return BigDecimal.valueOf (d);
  }

  @Nonnull
  public static BigDecimal toBigDecimal (@Nonnull final Number aNumber)
  {
    ValueEnforcer.notNull (aNumber, "Number");
    return new BigDecimal (aNumber.toString ());
  }

  @Nonnull
  public static BigInteger toBigInteger (final int n)
  {
    return BigInteger.valueOf (n);
  }

  @Nonnull
  public static BigInteger toBigInteger (final long n)
  {
    return BigInteger.valueOf (n);
  }

  @Nonnull
  public static BigInteger toBigInteger (@Nonnull final Number aNumber)
  {
    ValueEnforcer.notNull (aNumber, "Number");
    return new BigInteger (aNumber.toString ());
  }
}
