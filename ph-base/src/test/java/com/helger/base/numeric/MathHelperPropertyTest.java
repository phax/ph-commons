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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.junit.Test;

import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based tests for {@link MathHelper}, compared with exact {@link BigDecimal} and
 * {@link BigInteger} arithmetic.
 *
 * @author Philip Helger
 */
public final class MathHelperPropertyTest
{
  private static final IGenerator <RoundingMode> ROUNDING_MODES = Generators.of (RoundingMode.values ());

  /** Small values (many exact divisions and ties), edge cases and arbitrary values */
  private static final IGenerator <Integer> INTS = Generators.oneOf (Generators.ints (-10, 10), Generators.ints ());
  private static final IGenerator <Long> LONGS = Generators.oneOf (Generators.longs (-10, 10), Generators.longs ());

  /**
   * The exact quotient rounded with the passed mode, or the exception class name if there is none.
   */
  private static String _expectedDivision (final long nDividend, final long nDivisor, final RoundingMode eMode)
  {
    try
    {
      return new BigDecimal (nDividend).divide (new BigDecimal (nDivisor), 0, eMode).toBigInteger ().toString ();
    }
    catch (final ArithmeticException ex)
    {
      return ex.getClass ().getName ();
    }
  }

  @Test
  public void testIntDivided ()
  {
    new PropertyChecker ().setTries (2000).forAll (INTS, INTS, ROUNDING_MODES, (aDividend, aDivisor, eMode) -> {
      final int nDividend = aDividend.intValue ();
      final int nDivisor = aDivisor.intValue ();
      String sActual;
      try
      {
        sActual = Integer.toString (MathHelper.getIntDivided (nDividend, nDivisor, eMode));
      }
      catch (final ArithmeticException ex)
      {
        sActual = ex.getClass ().getName ();
      }
      String sExpected = _expectedDivision (nDividend, nDivisor, eMode);
      // The only quotient outside the int range is MIN_VALUE / -1 - it wraps around like Java division
      if (nDividend == Integer.MIN_VALUE && nDivisor == -1)
        sExpected = Integer.toString (Integer.MIN_VALUE);
      assertEquals (nDividend + " / " + nDivisor + " " + eMode, sExpected, sActual);
    });
  }

  @Test
  public void testLongDivided ()
  {
    new PropertyChecker ().setTries (2000).forAll (LONGS, LONGS, ROUNDING_MODES, (aDividend, aDivisor, eMode) -> {
      final long nDividend = aDividend.longValue ();
      final long nDivisor = aDivisor.longValue ();
      String sActual;
      try
      {
        sActual = Long.toString (MathHelper.getLongDivided (nDividend, nDivisor, eMode));
      }
      catch (final ArithmeticException ex)
      {
        sActual = ex.getClass ().getName ();
      }
      String sExpected = _expectedDivision (nDividend, nDivisor, eMode);
      if (nDividend == Long.MIN_VALUE && nDivisor == -1)
        sExpected = Long.toString (Long.MIN_VALUE);
      assertEquals (nDividend + " / " + nDivisor + " " + eMode, sExpected, sActual);
    });
  }

  @Test
  public void testRoundedUp ()
  {
    new PropertyChecker ().setTries (2000).forAll (INTS, Generators.ints (0, 1000), (aValue, aMultiple) -> {
      final int nValue = aValue.intValue ();
      final int nMultiple = aMultiple.intValue ();
      // The smallest multiple of nMultiple that is >= nValue
      final BigInteger aExpected = nMultiple == 0 ? BigInteger.valueOf (nValue)
                                                  : new BigDecimal (nValue).divide (new BigDecimal (nMultiple), 0, RoundingMode.CEILING)
                                                                           .toBigInteger ()
                                                                           .multiply (BigInteger.valueOf (nMultiple));
      // Only results that fit into an int are defined
      if (aExpected.bitLength () < 32)
        assertEquals (nValue + " to " + nMultiple, aExpected.intValue (), MathHelper.getRoundedUp (nValue, nMultiple));
    });
  }

  @Test
  public void testSmallHelpers ()
  {
    new PropertyChecker ().setTries (2000).forAll (LONGS, aValue -> {
      final long n = aValue.longValue ();
      assertEquals (Boolean.valueOf ((int) n == n), Boolean.valueOf (MathHelper.canConvertLongToInt (n)));
      assertEquals (Boolean.valueOf (Long.bitCount (n) == 1), Boolean.valueOf (MathHelper.isExactlyOneBitSetToOne (n)));
      assertEquals (Boolean.valueOf (Integer.bitCount ((int) n) == 1),
                    Boolean.valueOf (MathHelper.isExactlyOneBitSetToOne ((int) n)));
      assertEquals (Integer.toUnsignedLong ((int) n), MathHelper.getUnsignedInt ((int) n));
    });
  }
}
