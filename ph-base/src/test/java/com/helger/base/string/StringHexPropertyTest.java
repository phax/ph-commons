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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.junit.Test;

import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based tests for the optimised methods of {@link StringHex}, compared with
 * straightforward reference implementations.
 *
 * @author Philip Helger
 */
public final class StringHexPropertyTest
{
  private static final IGenerator <Integer> DIGITS = Generators.ints (-2, 20);

  /** The reference: JDK hex string, prefixed with zeros, and the minus sign counts as a digit */
  private static String _leadingZeroReference (final boolean bNegative, final String sMagnitude, final int nDigits)
  {
    final StringBuilder aSB = new StringBuilder ();
    if (bNegative)
      aSB.append ('-');
    for (int i = sMagnitude.length () + (bNegative ? 1 : 0); i < nDigits; ++i)
      aSB.append ('0');
    return aSB.append (sMagnitude).toString ();
  }

  @Test
  public void testLeadingZero ()
  {
    new PropertyChecker ().setTries (2000).forAll (Generators.longs (), DIGITS, (aValue, aDigits) -> {
      final long n = aValue.longValue ();
      final int nDigits = aDigits.intValue ();

      final String sLong = _leadingZeroReference (n < 0, Long.toUnsignedString (n < 0 ? -n : n, 16), nDigits);
      assertEquals (sLong, StringHex.getHexStringLeadingZero (n, nDigits));
      assertEquals (sLong, StringHex.appendHexStringLeadingZero (new StringBuilder (), n, nDigits, false).toString ());
      assertEquals (sLong.toUpperCase (Locale.ROOT),
                    StringHex.appendHexStringLeadingZero (new StringBuilder (), n, nDigits, true).toString ());

      final int nInt = (int) n;
      final String sInt = _leadingZeroReference (nInt < 0,
                                                 Integer.toUnsignedString (nInt < 0 ? -nInt : nInt, 16),
                                                 nDigits);
      assertEquals (sInt, StringHex.getHexStringLeadingZero (nInt, nDigits));
      assertEquals (sInt, StringHex.appendHexStringLeadingZero (new StringBuilder (), nInt, nDigits, false).toString ());

      // Short and byte are handled unsigned
      final short nShort = (short) n;
      assertEquals (_leadingZeroReference (false, Integer.toString (nShort & 0xffff, 16), nDigits),
                    StringHex.getHexStringLeadingZero (nShort, nDigits));
      final byte nByte = (byte) n;
      assertEquals (_leadingZeroReference (false, Integer.toString (nByte & 0xff, 16), nDigits),
                    StringHex.getHexStringLeadingZero (nByte, nDigits));
      assertEquals (_leadingZeroReference (false, Integer.toString (nByte & 0xff, 16), 2),
                    StringHex.getHexStringLeadingZero2 (nByte));
    });
  }

  @Test
  public void testEncodeDecode ()
  {
    new PropertyChecker ().setTries (1000).forAll (Generators.byteArrays (0, 40), aBytes -> {
      final StringBuilder aRef = new StringBuilder ();
      for (final byte b : aBytes)
        aRef.append (Character.forDigit ((b >> 4) & 0xf, 16)).append (Character.forDigit (b & 0xf, 16));
      final String sExpected = aRef.toString ();
      final String sExpectedUC = sExpected.toUpperCase (Locale.ROOT);

      assertEquals (sExpected, StringHex.getHexEncoded (aBytes));
      assertEquals (sExpectedUC, StringHex.getHexEncodedUpperCase (aBytes));
      assertEquals (sExpected, new String (StringHex.getHexEncodedByteArray (aBytes), StandardCharsets.ISO_8859_1));
      assertEquals ("x" + sExpected, StringHex.appendHexEncoded (new StringBuilder ("x"), aBytes, false).toString ());
      assertEquals (sExpectedUC, StringHex.appendHexEncoded (new StringBuilder (), aBytes, true).toString ());

      assertArrayEquals (aBytes, StringHex.getHexDecoded (sExpected));
      assertArrayEquals (aBytes, StringHex.getHexDecoded (sExpectedUC.toCharArray ()));
      assertArrayEquals (aBytes, StringHex.getHexDecoded ((CharSequence) new StringBuilder (sExpectedUC)));
      assertArrayEquals (aBytes, StringHex.getHexDecodedOrNull (sExpected));
    });
  }
}
