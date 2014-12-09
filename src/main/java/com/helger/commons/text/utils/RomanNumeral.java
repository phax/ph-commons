/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.text.utils;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * An object of type RomanNumeral is an integer between 1 and 3999. It can be
 * constructed either from an integer or from a string that represents a Roman
 * numeral in this range. The function toString() will return a standardized
 * Roman numeral representation of the number. The function toInt() will return
 * the number as a value of type int.
 *
 * @author Philip Helger
 * @deprecated Use {@link com.helger.commons.string.utils.RomanNumeral} instead.
 */
@Deprecated
@Immutable
public final class RomanNumeral
{
  private RomanNumeral ()
  {}

  @Nonnegative
  public static int romanStringToInt (final String sRoman)
  {
    return com.helger.commons.string.utils.RomanNumeral.romanStringToInt (sRoman);
  }

  @Nonnull
  public static String intToRomanString (final int nValue)
  {
    return com.helger.commons.string.utils.RomanNumeral.intToRomanString (nValue);
  }
}
