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

import java.math.RoundingMode;

import com.helger.annotation.CheckReturnValue;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;

import jakarta.annotation.Nonnull;

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
  private static final MathHelper INSTANCE = new MathHelper ();

  private MathHelper ()
  {}

  /**
   * Round up to the nearest multiple of the value to round.
   *
   * @param nToRound
   *        Value to round. May be positive or negative.
   * @param nMultiple
   *        Multiple to use. Must be &ge; 0.
   * @return The rounded value.
   */
  public static int getRoundedUp (final int nToRound, @Nonnegative final int nMultiple)
  {
    if (nMultiple == 0)
      return nToRound;

    final int nRest = nToRound % nMultiple;
    if (nRest == 0)
      return nToRound;
    if (nToRound < 0)
      return -abs (nToRound - nRest);
    return nToRound + nMultiple - nRest;
  }

  /**
   * Divides the passed int dividend through the passed divisor (nDividend / nDivisor)
   *
   * @param nDividend
   *        the dividend
   * @param nDivisor
   *        the divisor
   * @return a double representing the exact quotient. Returns {@link Double#NaN} if the divisor is
   *         0.
   */
  public static double getDividedDouble (final int nDividend, final int nDivisor)
  {
    final double dDividend = nDividend;
    final double dDivisor = nDivisor;
    return dDividend / dDivisor;
  }

  /**
   * Divides the passed int dividend through the passed divisor (nDividend / nDivisor)
   *
   * @param nDividend
   *        the dividend
   * @param nDivisor
   *        the divisor
   * @return a double representing the exact quotient. Returns {@link Double#NaN} if the divisor is
   *         0.
   */
  public static double getDividedDouble (final long nDividend, final long nDivisor)
  {
    final double dDividend = nDividend;
    final double dDivisor = nDivisor;
    return dDividend / dDivisor;
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
    return BigHelper.toBigDecimal (nDividend).divide (BigHelper.toBigDecimal (nDivisor), eRoundingMode).intValue ();
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
    return BigHelper.toBigDecimal (nDividend).divide (BigHelper.toBigDecimal (nDivisor), eRoundingMode).longValue ();
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

  /**
   * This is a fix for <code>Math.abs</code> as it would return {@link Integer#MIN_VALUE} for
   * {@link Integer#MIN_VALUE} which is very unexpected. Instead an exception is thrown.
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
   * This is a fix for <code>Math.abs</code> as it would return {@link Long#MIN_VALUE} for
   * {@link Long#MIN_VALUE} which is very unexpected. Instead an exception is thrown.
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
   * This is a sanity method wrapping <code>Math.abs (float)</code>, so that you don't have to think
   * whether you need to invoke the abs method from this class or the one from Math directly.
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
   * This is a sanity method wrapping <code>Math.abs (double)</code>, so that you don't have to
   * think whether you need to invoke the abs method from this class or the one from Math directly.
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

  /**
   * Check if only a single bit is set.<br>
   * Source:
   * http://stackoverflow.com/questions/12483843/test-if-a-bitboard-have-only-one-bit-set-to-1 <br>
   * Say n has any bits set, the least significant is bit number k. Then n-1 has the same bits as n
   * for indices above k, a 0-bit in place k and 1-bits in the less significant places, so the
   * bitwise and removes the least significant set bit from n. If n had only one bit set, the result
   * becomes 0, if n had more bits set, the result is nonzero.
   *
   * @param n
   *        Source number
   * @return <code>true</code> if exactly one bit is set
   */
  public static boolean isExactlyOneBitSetToOne (final int n)
  {
    return n != 0 && (n & (n - 1)) == 0;
  }

  /**
   * Check if only a single bit is set.<br>
   * Source:
   * http://stackoverflow.com/questions/12483843/test-if-a-bitboard-have-only-one-bit-set-to-1 <br>
   * Say n has any bits set, the least significant is bit number k. Then n-1 has the same bits as n
   * for indices above k, a 0-bit in place k and 1-bits in the less significant places, so the
   * bitwise and removes the least significant set bit from n. If n had only one bit set, the result
   * becomes 0, if n had more bits set, the result is nonzero.
   *
   * @param n
   *        Source number
   * @return <code>true</code> if exactly one bit is set
   */
  public static boolean isExactlyOneBitSetToOne (final long n)
  {
    return n != 0 && (n & (n - 1)) == 0;
  }
}
