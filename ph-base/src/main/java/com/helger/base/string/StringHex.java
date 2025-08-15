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
package com.helger.base.string;

import java.nio.charset.Charset;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.CGlobal;
import com.helger.base.equals.ValueEnforcer;

import jakarta.annotation.Nonnull;

@Immutable
public class StringHex
{
  private StringHex ()
  {}

  /**
   * Get the matching hex digit as a lower case character.
   *
   * @param n
   *        The value to get the hex digit from. Must be between 0 and 15.
   * @return The hex character (one of 0-9 or a-f), or '\0' if the value could not be converted
   */
  public static char getHexChar (final int n)
  {
    return Character.forDigit (n, CGlobal.HEX_RADIX);
  }

  /**
   * Get the matching hex digit as an upper case character.
   *
   * @param n
   *        The value to get the hex digit from. Must be between 0 and 15.
   * @return The hex character (one of 0-9 or A-F), or '\0' if the value could not be converted
   */
  public static char getHexCharUpperCase (final int n)
  {
    return Character.toUpperCase (getHexChar (n));
  }

  /**
   * Convert a string to a byte array and than to a hexadecimal encoded string.
   *
   * @param sInput
   *        The source string. May not be <code>null</code>.
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @return The String representation of the byte array of the string.
   */
  @Nonnull
  public static String getHexEncoded (@Nonnull final String sInput, @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (sInput, "Input");
    ValueEnforcer.notNull (aCharset, "Charset");

    return getHexEncoded (sInput.getBytes (aCharset));
  }

  /**
   * Convert a byte array to a hexadecimal encoded string.
   *
   * @param aInput
   *        The byte array to be converted to a String. May not be <code>null</code>.
   * @return The String representation of the byte array.
   */
  @Nonnull
  public static String getHexEncoded (@Nonnull final byte [] aInput)
  {
    ValueEnforcer.notNull (aInput, "Input");

    return getHexEncoded (aInput, 0, aInput.length);
  }

  /**
   * Convert a byte array to a hexadecimal encoded string.
   *
   * @param aInput
   *        The byte array to be converted to a String. May not be <code>null</code>.
   * @param nOfs
   *        Byte array offset
   * @param nLen
   *        Number of bytes to encode
   * @return The String representation of the byte array.
   */
  @Nonnull
  public static String getHexEncoded (@Nonnull final byte [] aInput, final int nOfs, final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (aInput, nOfs, nLen);

    final StringBuilder aSB = new StringBuilder (nLen * 2);
    for (int i = nOfs; i < (nOfs + nLen); ++i)
    {
      final byte b = aInput[i];
      final char c1 = getHexChar ((b & 0xf0) >> 4);
      final char c2 = getHexChar (b & 0x0f);
      aSB.append (c1).append (c2);
    }
    return aSB.toString ();
  }

  /**
   * Get the decimal value of the passed hex character
   *
   * @param c
   *        The hex char to convert
   * @return A value between 0 and 15, or -1 if the input character is not a hex char!
   */
  @CheckForSigned
  public static int getHexValue (@Nonnegative final char c)
  {
    return Character.digit (c, CGlobal.HEX_RADIX);
  }

  /**
   * @param cHigh
   *        High hex part
   * @param cLow
   *        Low hex part
   * @return A value between 0 and 255, or -1 if any input character is not a hex char!
   */
  public static int getHexByte (@Nonnegative final char cHigh, @Nonnegative final char cLow)
  {
    final int nHex1 = getHexValue (cHigh);
    final int nHex2 = getHexValue (cLow);
    return nHex1 < 0 || nHex2 < 0 ? -1 : (nHex1 << 4) | nHex2;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static byte [] getHexDecoded (@Nonnull final String sInput)
  {
    ValueEnforcer.notNull (sInput, "Input");

    return getHexDecoded (sInput.toCharArray (), 0, sInput.length ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public static byte [] getHexDecoded (@Nonnull final char [] aInput)
  {
    ValueEnforcer.notNull (aInput, "Input");

    return getHexDecoded (aInput, 0, aInput.length);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static byte [] getHexDecoded (@Nonnull final char [] aInput,
                                       @Nonnegative final int nOfs,
                                       @Nonnegative final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (aInput, nOfs, nLen);
    ValueEnforcer.isTrue ((nLen % 2) == 0, () -> "Passed chars have no even length: " + nLen);

    final byte [] ret = new byte [nLen / 2];
    int nRetIdx = 0;
    for (int i = 0; i < nLen; i += 2)
    {
      final char c0 = aInput[nOfs + i];
      final char c1 = aInput[nOfs + i + 1];
      final int nHexByte = getHexByte (c0, c1);
      if (nHexByte == -1)
        throw new IllegalArgumentException ("Failed to convert '" + c0 + "' and '" + c1 + "' to a hex value!");
      ret[nRetIdx++] = (byte) nHexByte;
    }
    return ret;
  }

  @Nonnull
  public static String getHexString (final byte nValue)
  {
    // Bytes are always handled unsigned
    return Integer.toString (nValue & 0xff, CGlobal.HEX_RADIX);
  }

  @Nonnull
  public static String getHexStringLeadingZero (final byte nValue, final int nDigits)
  {
    return StringHelper.getLeadingZero (getHexString (nValue), nDigits);
  }

  @Nonnull
  public static String getHexStringLeadingZero2 (final byte nValue)
  {
    final String ret = getHexString (nValue);
    return ret.length () >= 2 ? ret : '0' + ret;
  }

  @Nonnull
  public static String getHexString (final int nValue)
  {
    return Integer.toString (nValue, CGlobal.HEX_RADIX);
  }

  @Nonnull
  public static String getHexStringLeadingZero (final int nValue, final int nDigits)
  {
    if (nValue < 0)
      return "-" + StringHelper.getLeadingZero (getHexString (-nValue), nDigits - 1);
    return StringHelper.getLeadingZero (getHexString (nValue), nDigits);
  }

  @Nonnull
  public static String getHexString (final long nValue)
  {
    return Long.toString (nValue, CGlobal.HEX_RADIX);
  }

  @Nonnull
  public static String getHexStringLeadingZero (final long nValue, final int nDigits)
  {
    if (nValue < 0)
      return "-" + StringHelper.getLeadingZero (getHexString (-nValue), nDigits - 1);
    return StringHelper.getLeadingZero (getHexString (nValue), nDigits);
  }

  @Nonnull
  public static String getHexString (final short nValue)
  {
    return Integer.toString (nValue & 0xffff, CGlobal.HEX_RADIX);
  }

  @Nonnull
  public static String getHexStringLeadingZero (final short nValue, final int nDigits)
  {
    // Short are always handled unsigned
    return StringHelper.getLeadingZero (getHexString (nValue), nDigits);
  }

}
