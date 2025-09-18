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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import com.helger.annotation.CheckReturnValue;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.CGlobal;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * BigDecimal and BigInteger helper
 *
 * @author Philip Helger
 * @since v12.0.0
 */
@Immutable
public final class BigHelper
{
  @PresentForCodeCoverage
  private static final BigHelper INSTANCE = new BigHelper ();

  private BigHelper ()
  {}

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
  public static BigDecimal getDividedBigDecimal (final long nDividend, final long nDivisor)
  {
    return toBigDecimal (nDividend).divide (toBigDecimal (nDivisor));
  }

  /**
   * Get the division result using {@link BigDecimal}.
   *
   * @param nDividend
   *        the dividend
   * @param nDivisor
   *        the divisor
   * @param nScale
   *        Number of fraction digits. Must be &ge; 0.
   * @param eRoundingMode
   *        Round mode to be used. May not be <code>null</code>.
   * @return the result of the division
   * @throws ArithmeticException
   *         if the divisor is 0.
   * @since v11.0.2
   */
  @Nonnull
  public static BigDecimal getDividedBigDecimal (final long nDividend,
                                                 final long nDivisor,
                                                 @Nonnegative final int nScale,
                                                 @Nonnull final RoundingMode eRoundingMode)
  {
    return toBigDecimal (nDividend).divide (toBigDecimal (nDivisor), nScale, eRoundingMode);
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
   * This is a sanity method wrapping <code>BigDecimal.abs (double)</code>, so that you don't have
   * to think whether you need to invoke the abs method from this class or the one from BigDecimal
   * directly.
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
   * This is a sanity method wrapping <code>BigInteger.abs (double)</code>, so that you don't have
   * to think whether you need to invoke the abs method from this class or the one from BigInteger
   * directly.
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
   * Special equals implementation for BigDecimal because <code>BigDecimal.equals</code> returns
   * <code>false</code> if they have a different scale so that "5.5" is not equal "5.50".
   *
   * @param aObj1
   *        first value. May not be <code>null</code>.
   * @param aObj2
   *        second value. May not be <code>null</code>.
   * @return <code>true</code> if they contain the same value
   */
  public static boolean equalValues (@Nonnull final BigDecimal aObj1, @Nonnull final BigDecimal aObj2)
  {
    // Compare is ~15% quicker than the setScale version
    if (true)
      return aObj1.compareTo (aObj2) == 0;

    final int nMaxScale = Math.max (aObj1.scale (), aObj2.scale ());
    // Use the same rounding mode for both
    return aObj1.setScale (nMaxScale, RoundingMode.HALF_UP).equals (aObj2.setScale (nMaxScale, RoundingMode.HALF_UP));
  }

  /**
   * Special equals implementation for BigDecimal because <code>BigDecimal.equals</code> returns
   * <code>false</code> if they have a different scale so that "5.5" is not equal "5.50".
   *
   * @param aObj1
   *        first value. May be <code>null</code>.
   * @param aObj2
   *        second value. May be <code>null</code>.
   * @return <code>true</code> if they contain the same value
   * @since 12.0.2
   */
  public static boolean equalValuesNullSafe (@Nullable final BigDecimal aObj1, @Nullable final BigDecimal aObj2)
  {
    if (EqualsHelper.identityEqual (aObj1, aObj2))
      return true;
    if (aObj1 == null || aObj2 == null)
      return false;
    return equalValues (aObj1, aObj2);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is = 0.
   */
  public static boolean isEQ0 (@Nonnull final BigDecimal aValue)
  {
    return equalValues (aValue, BigDecimal.ZERO);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is != 0.
   */
  public static boolean isNE0 (@Nonnull final BigDecimal aValue)
  {
    return !equalValues (aValue, BigDecimal.ZERO);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &lt; 0.
   */
  public static boolean isLT0 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.ZERO) < 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &le; 0.
   */
  public static boolean isLE0 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.ZERO) <= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &gt; 0.
   */
  public static boolean isGT0 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.ZERO) > 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &ge; 0.
   */
  public static boolean isGE0 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.ZERO) >= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is = 1.
   */
  public static boolean isEQ1 (@Nonnull final BigDecimal aValue)
  {
    return equalValues (aValue, BigDecimal.ONE);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is != 1.
   */
  public static boolean isNE1 (@Nonnull final BigDecimal aValue)
  {
    return !equalValues (aValue, BigDecimal.ONE);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &lt; 1.
   */
  public static boolean isLT1 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.ONE) < 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &le; 1.
   */
  public static boolean isLE1 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.ONE) <= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &gt; 1.
   */
  public static boolean isGT1 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.ONE) > 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &ge; 1.
   */
  public static boolean isGE1 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.ONE) >= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is = 10.
   */
  public static boolean isEQ10 (@Nonnull final BigDecimal aValue)
  {
    return equalValues (aValue, BigDecimal.TEN);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is != 10.
   */
  public static boolean isNE10 (@Nonnull final BigDecimal aValue)
  {
    return !equalValues (aValue, BigDecimal.TEN);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &lt; 10.
   */
  public static boolean isLT10 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.TEN) < 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &le; 10.
   */
  public static boolean isLE10 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.TEN) <= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &gt; 10.
   */
  public static boolean isGT10 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.TEN) > 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &ge; 10.
   */
  public static boolean isGE10 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (BigDecimal.TEN) >= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is = 100.
   */
  public static boolean isEQ100 (@Nonnull final BigDecimal aValue)
  {
    return equalValues (aValue, CGlobal.BIGDEC_100);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is != 100.
   */
  public static boolean isNE100 (@Nonnull final BigDecimal aValue)
  {
    return !equalValues (aValue, CGlobal.BIGDEC_100);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &lt; 100.
   */
  public static boolean isLT100 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (CGlobal.BIGDEC_100) < 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &le; 100.
   */
  public static boolean isLE100 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (CGlobal.BIGDEC_100) <= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &gt; 100.
   */
  public static boolean isGT100 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (CGlobal.BIGDEC_100) > 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &ge; 100.
   */
  public static boolean isGE100 (@Nonnull final BigDecimal aValue)
  {
    return aValue.compareTo (CGlobal.BIGDEC_100) >= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is = 0.
   */
  public static boolean isEQ0 (@Nonnull final BigInteger aValue)
  {
    return aValue.equals (BigInteger.ZERO);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is != 0.
   */
  public static boolean isNE0 (@Nonnull final BigInteger aValue)
  {
    return !aValue.equals (BigInteger.ZERO);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &lt; 0.
   */
  public static boolean isLT0 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.ZERO) < 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &le; 0.
   */
  public static boolean isLE0 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.ZERO) <= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &gt; 0.
   */
  public static boolean isGT0 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.ZERO) > 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &ge; 0.
   */
  public static boolean isGE0 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.ZERO) >= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is = 1.
   */
  public static boolean isEQ1 (@Nonnull final BigInteger aValue)
  {
    return aValue.equals (BigInteger.ONE);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is != 1.
   */
  public static boolean isNE1 (@Nonnull final BigInteger aValue)
  {
    return !aValue.equals (BigInteger.ONE);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &lt; 1.
   */
  public static boolean isLT1 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.ONE) < 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &le; 1.
   */
  public static boolean isLE1 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.ONE) <= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &gt; 1.
   */
  public static boolean isGT1 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.ONE) > 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &ge; 1.
   */
  public static boolean isGE1 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.ONE) >= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is = 10.
   */
  public static boolean isEQ10 (@Nonnull final BigInteger aValue)
  {
    return aValue.equals (BigInteger.TEN);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is != 10.
   */
  public static boolean isNE10 (@Nonnull final BigInteger aValue)
  {
    return !aValue.equals (BigInteger.TEN);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &lt; 10.
   */
  public static boolean isLT10 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.TEN) < 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &le; 10.
   */
  public static boolean isLE10 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.TEN) <= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &gt; 10.
   */
  public static boolean isGT10 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.TEN) > 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &ge; 10.
   */
  public static boolean isGE10 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (BigInteger.TEN) >= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is = 100.
   */
  public static boolean isEQ100 (@Nonnull final BigInteger aValue)
  {
    return aValue.equals (CGlobal.BIGINT_100);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is != 100.
   */
  public static boolean isNE100 (@Nonnull final BigInteger aValue)
  {
    return !aValue.equals (CGlobal.BIGINT_100);
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &lt; 100.
   */
  public static boolean isLT100 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (CGlobal.BIGINT_100) < 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &le; 100.
   */
  public static boolean isLE100 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (CGlobal.BIGINT_100) <= 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &gt; 100.
   */
  public static boolean isGT100 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (CGlobal.BIGINT_100) > 0;
  }

  /**
   * @param aValue
   *        Value to compare. May not be <code>null</code>.
   * @return <code>true</code> if the value is &ge; 100.
   */
  public static boolean isGE100 (@Nonnull final BigInteger aValue)
  {
    return aValue.compareTo (CGlobal.BIGINT_100) >= 0;
  }

  /**
   * Get the passed String as a BigDecimal without any trailing zeroes.
   *
   * @param sValue
   *        The String to be used as a BigDecimal to be modified. May be <code>null</code>.
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
   * Get the number of effective fraction digits by the specified BigDecimal. Examples:
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
   * @return base + x% (<code>=aBase * (100 + perc) / 100</code>). Never <code>null</code>.
   */
  @Nonnull
  public static BigDecimal addPercent (@Nonnull final BigDecimal aBase, @Nonnull final BigDecimal aPercentage)
  {
    return aBase.multiply (CGlobal.BIGDEC_100.add (aPercentage)).divide (CGlobal.BIGDEC_100);
  }

  /**
   * Add x% to base
   *
   * @param aBase
   *        Base value. May not be <code>null</code>.
   * @param aPercentage
   *        Percentage value (0-100). May not be <code>null</code>.
   * @param nScale
   *        Maximum scale to achieve.
   * @param eRoundingMode
   *        Rounding mode to used. May not be <code>null</code>.
   * @return base + x% (<code>=aBase * (100 + perc) / 100</code>). Never <code>null</code>.
   */
  @Nonnull
  public static BigDecimal addPercent (@Nonnull final BigDecimal aBase,
                                       @Nonnull final BigDecimal aPercentage,
                                       @Nonnegative final int nScale,
                                       @Nonnull final RoundingMode eRoundingMode)
  {
    return aBase.multiply (CGlobal.BIGDEC_100.add (aPercentage)).divide (CGlobal.BIGDEC_100, nScale, eRoundingMode);
  }

  /**
   * Subtract x% from base
   *
   * @param aBase
   *        Base value. May not be <code>null</code>.
   * @param aPercentage
   *        Percentage value (0-100). May not be <code>null</code>.
   * @return base - x% (<code>=aBase * (100 - perc) / 100</code>). Never <code>null</code>.
   */
  @Nonnull
  public static BigDecimal subtractPercent (@Nonnull final BigDecimal aBase, @Nonnull final BigDecimal aPercentage)
  {
    return aBase.multiply (CGlobal.BIGDEC_100.subtract (aPercentage)).divide (CGlobal.BIGDEC_100);
  }

  /**
   * Subtract x% from base
   *
   * @param aBase
   *        Base value. May not be <code>null</code>.
   * @param aPercentage
   *        Percentage value (0-100). May not be <code>null</code>.
   * @param nScale
   *        Maximum scale to achieve.
   * @param eRoundingMode
   *        Rounding mode to used. May not be <code>null</code>.
   * @return base - x% (<code>=aBase * (100 - perc) / 100</code>). Never <code>null</code>.
   */
  @Nonnull
  public static BigDecimal subtractPercent (@Nonnull final BigDecimal aBase,
                                            @Nonnull final BigDecimal aPercentage,
                                            @Nonnegative final int nScale,
                                            @Nonnull final RoundingMode eRoundingMode)
  {
    return aBase.multiply (CGlobal.BIGDEC_100.subtract (aPercentage))
                .divide (CGlobal.BIGDEC_100, nScale, eRoundingMode);
  }

  /**
   * Get x% from base
   *
   * @param aBase
   *        Base value. May not be <code>null</code>.
   * @param aPercentage
   *        Percentage value (0-100). May not be <code>null</code>.
   * @return x% from base (<code>=aBase * perc / 100</code>). Never <code>null</code>.
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
   * @return x% from base (<code>=aBase * perc / 100</code>). Never <code>null</code>.
   */
  @Nonnull
  public static BigDecimal getPercentValue (@Nonnull final BigDecimal aBase,
                                            @Nonnull final BigDecimal aPercentage,
                                            @Nonnegative final int nScale,
                                            @Nonnull final RoundingMode eRoundingMode)
  {
    return aBase.multiply (aPercentage).divide (CGlobal.BIGDEC_100, nScale, eRoundingMode);
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
  public static BigDecimal toBigDecimal (@Nonnull final String sNumber)
  {
    ValueEnforcer.notNull (sNumber, "Number");
    return new BigDecimal (sNumber);
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
    return new BigInteger (aNumber.toString (), 10);
  }

  @Nonnull
  public static BigInteger toBigInteger (@Nonnull final String sNumber)
  {
    ValueEnforcer.notNull (sNumber, "Number");
    return new BigInteger (sNumber, 10);
  }
}
