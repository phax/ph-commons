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

import java.math.BigInteger;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * Smart class for calculating factorials.
 *
 * @author Philip Helger
 */
@Immutable
public final class FactorialHelper
{
  private static final long [] PREDEFINED_FACTORIALS_LONG = { 1,
                                                              1,
                                                              2,
                                                              6,
                                                              24,
                                                              120,
                                                              720,
                                                              5040,
                                                              40320,
                                                              362880,
                                                              3628800,
                                                              39916800,
                                                              479001600,
                                                              6227020800L,
                                                              87178291200L,
                                                              1307674368000L,
                                                              20922789888000L,
                                                              355687428096000L,
                                                              6402373705728000L,
                                                              121645100408832000L,
                                                              2432902008176640000L };
  /** The minimum value for which pre-computed factorial values are present */
  public static final int PREDEFINED_MIN_INDEX = 0;
  /** The maximum value for which pre-computed factorial values are present */
  public static final int PREDEFINED_MAX_INDEX = PREDEFINED_FACTORIALS_LONG.length - 1;

  @PresentForCodeCoverage
  private static final FactorialHelper s_aInstance = new FactorialHelper ();

  private FactorialHelper ()
  {}

  /**
   * Calculate n! whereas n must be in the range of
   * {@value #PREDEFINED_MIN_INDEX} to {@link #PREDEFINED_MAX_INDEX}.
   *
   * @param n
   *        Input value
   * @return The factorial value.
   */
  @Nonnegative
  public static long getSmallFactorial (@Nonnegative final int n)
  {
    ValueEnforcer.isBetweenInclusive (n, "n", PREDEFINED_MIN_INDEX, PREDEFINED_MAX_INDEX);
    return PREDEFINED_FACTORIALS_LONG[n];
  }

  /**
   * Split algorithm for factorials.<br>
   * Based on http://www.luschny.de/math/factorial/java/FactorialSplit.java.html
   *
   * @author Philip Helger
   */
  private static final class FactorialSplit
  {
    private long m_nCurrentN;

    FactorialSplit ()
    {}

    @Nonnull
    private BigInteger _getProduct (final int n)
    {
      final int m = n / 2;
      if (m == 0)
      {
        m_nCurrentN += 2;
        return BigInteger.valueOf (m_nCurrentN);
      }
      if (n == 2)
      {
        m_nCurrentN += 2;
        final long n1 = m_nCurrentN;
        m_nCurrentN += 2;
        final long n2 = m_nCurrentN;
        return BigInteger.valueOf (n1 * n2);
      }
      return _getProduct (n - m).multiply (_getProduct (m));
    }

    @Nonnull
    public BigInteger getFactorial (@Nonnegative final int n)
    {
      ValueEnforcer.isGE0 (n, "n");
      if (n < 2)
        return BigInteger.ONE;
      BigInteger aP = BigInteger.ONE;
      BigInteger aR = BigInteger.ONE;
      m_nCurrentN = 1;
      int nH = 0;
      int nShift = 0;
      int nHigh = 1;
      int nLog2n = true ? 31 - Integer.numberOfLeadingZeros (n) : (int) Math.floor (Math.log (n) / Math.log (2));
      while (nH != n)
      {
        nShift += nH;
        nH = n >> nLog2n--;
        int nLen = nHigh;
        nHigh = (nH - 1) | 1;
        nLen = (nHigh - nLen) / 2;
        if (nLen > 0)
        {
          aP = aP.multiply (_getProduct (nLen));
          aR = aR.multiply (aP);
        }
      }
      return aR.shiftLeft (nShift);
    }
  }

  @Nonnull
  public static BigInteger getAnyFactorialLinear (@Nonnegative final int n)
  {
    return new FactorialSplit ().getFactorial (n);
  }
}
