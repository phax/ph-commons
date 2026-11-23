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
package com.helger.base.string;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based tests for {@link StringParser}: the string form of every number is parsed back to
 * the same number, and arbitrary input never causes an exception.
 *
 * @author Philip Helger
 */
public final class StringParserPropertyTest
{
  private static final IGenerator <Integer> RADIXES = Generators.ints (Character.MIN_RADIX, Character.MAX_RADIX);

  /** Arbitrary doubles from their bit pattern - including NaN, infinities, subnormals and -0.0 */
  private static final IGenerator <Double> DOUBLES = Generators.longs ().map (aBits -> Double.valueOf (Double.longBitsToDouble (aBits.longValue ())));

  private static final IGenerator <BigInteger> BIG_INTEGERS = Generators.lists (Generators.longs (), 1, 3).map (aParts -> {
    BigInteger ret = BigInteger.ZERO;
    for (final Long aPart : aParts)
      ret = ret.shiftLeft (64).add (BigInteger.valueOf (aPart.longValue ()));
    return ret;
  });

  @Test
  public void testIntAndLong ()
  {
    new PropertyChecker ().setTries (2000).forAll (Generators.longs (), RADIXES, (aValue, aRadix) -> {
      final long n = aValue.longValue ();
      final int nRadix = aRadix.intValue ();
      assertEquals (n, StringParser.parseLong (Long.toString (n, nRadix), nRadix, 4711));
      assertEquals ((int) n, StringParser.parseInt (Integer.toString ((int) n, nRadix), nRadix, 4711));
      assertEquals (Integer.valueOf ((int) n), StringParser.parseIntObj (Integer.toString ((int) n)));
      assertEquals (Long.valueOf (n), StringParser.parseLongObj (Long.toString (n)));
    });
  }

  @Test
  public void testDoubleAndFloat ()
  {
    new PropertyChecker ().setTries (2000).forAll (DOUBLES, aValue -> {
      final double d = aValue.doubleValue ();
      // Compare the bits, so that NaN and -0.0 are checked as well
      assertEquals (Double.doubleToLongBits (d), Double.doubleToLongBits (StringParser.parseDouble (Double.toString (d), 47.11)));
      final float f = (float) d;
      assertEquals (Float.floatToIntBits (f), Float.floatToIntBits (StringParser.parseFloat (Float.toString (f), 47.11f)));
      // The localized form with a decimal comma
      if (Double.isFinite (d))
        assertEquals (Double.doubleToLongBits (d),
                      Double.doubleToLongBits (StringParser.parseDouble (StringReplace.replaceAll (Double.toString (d), '.', ','),
                                                                         47.11)));
    });
  }

  @Test
  public void testBigNumbers ()
  {
    new PropertyChecker ().setTries (2000).forAll (BIG_INTEGERS, Generators.ints (-20, 20), RADIXES, (aBI, aScale, aRadix) -> {
      final int nRadix = aRadix.intValue ();
      assertEquals (aBI, StringParser.parseBigInteger (aBI.toString (nRadix), nRadix));
      assertEquals (aBI, StringParser.parseBigInteger (aBI.toString ()));
      final BigDecimal aBD = new BigDecimal (aBI, aScale.intValue ());
      // equals also compares the scale
      assertEquals (aBD, StringParser.parseBigDecimal (aBD.toString ()));
      assertEquals (aBD, StringParser.parseBigDecimal (aBD.toPlainString ()).setScale (aBD.scale ()));
    });
  }

  @Test
  public void testArbitraryInputNeverThrows ()
  {
    final IGenerator <String> aText = Generators.oneOf (Generators.strings ("0123456789+-.,eE xXInfinityNaN", 0, 15),
                                                        Generators.unicodeStrings (0, 10));
    new PropertyChecker ().setTries (2000).forAll (aText, RADIXES, (sText, aRadix) -> {
      final int nRadix = aRadix.intValue ();
      StringParser.parseInt (sText, nRadix, 0);
      StringParser.parseLong (sText, nRadix, 0);
      StringParser.parseByte (sText, nRadix, (byte) 0);
      StringParser.parseDouble (sText, 0);
      StringParser.parseFloat (sText, 0);
      StringParser.parseBigInteger (sText, nRadix);
      StringParser.parseBigDecimal (sText);
      StringParser.parseBool (sText);
      StringParser.parseBoolObjExact (sText);
    });
  }
}
