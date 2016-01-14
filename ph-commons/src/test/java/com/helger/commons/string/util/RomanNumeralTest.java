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
package com.helger.commons.string.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link RomanNumeral}.
 *
 * @author Philip Helger
 */
public final class RomanNumeralTest
{
  @Test
  @SuppressFBWarnings (value = "NP_NULL_PARAM_DEREF_NONVIRTUAL")
  public void testBasic ()
  {
    assertEquals ("V", RomanNumeral.intToRomanString (5));
    // "use" + to avoid interpreting this as a to do
    assertEquals ("X" + "XX", RomanNumeral.intToRomanString (30));
    assertEquals (29, RomanNumeral.romanStringToInt ("IXXX"));
    assertEquals (29, RomanNumeral.romanStringToInt ("ixxx"));
    assertEquals (29, RomanNumeral.romanStringToInt ("xixx"));
    assertEquals (29, RomanNumeral.romanStringToInt ("xxix"));
    assertEquals ("XXIX", RomanNumeral.intToRomanString (29));

    try
    {
      // too small
      RomanNumeral.intToRomanString (RomanNumeral.MIN_VAL - 1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // too large
      RomanNumeral.intToRomanString (RomanNumeral.MAX_VAL + 1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // empty not allowed
      RomanNumeral.romanStringToInt (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // empty not allowed
      RomanNumeral.romanStringToInt ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // y: not roman
      RomanNumeral.romanStringToInt ("xyz");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // too big
      RomanNumeral.romanStringToInt ("mmmmmmmmmmmmm");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testAll ()
  {
    for (int i = RomanNumeral.MIN_VAL; i <= RomanNumeral.MAX_VAL; ++i)
    {
      // Convert to String
      final String s = RomanNumeral.intToRomanString (i);

      // and convert back -> check that it matches the original number
      assertEquals (i, RomanNumeral.romanStringToInt (s));
    }
  }
}
