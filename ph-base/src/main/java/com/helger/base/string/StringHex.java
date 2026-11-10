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

import java.nio.charset.Charset;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.CGlobal;
import com.helger.base.enforce.ValueEnforcer;

/**
 * Helper class for hexadecimal string encoding and decoding of byte arrays and individual values.
 *
 * @author Philip Helger
 */
@Immutable
public class StringHex
{
  private static final char [] HEX_CHARS_LOWER = "0123456789abcdef".toCharArray ();
  private static final char [] HEX_CHARS_UPPER = "0123456789ABCDEF".toCharArray ();
  /** Maps ASCII chars to their hex value or -1 */
  private static final byte [] HEX_VALUES = new byte [128];

  static
  {
    for (int i = 0; i < HEX_VALUES.length; ++i)
      HEX_VALUES[i] = -1;
    for (int i = 0; i < 10; ++i)
      HEX_VALUES['0' + i] = (byte) i;
    for (int i = 0; i < 6; ++i)
    {
      HEX_VALUES['a' + i] = (byte) (10 + i);
      HEX_VALUES['A' + i] = (byte) (10 + i);
    }
  }

  @PresentForCodeCoverage
  private static final StringHex INSTANCE = new StringHex ();

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
    return (n & ~0xf) == 0 ? HEX_CHARS_LOWER[n] : '\0';
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
    return (n & ~0xf) == 0 ? HEX_CHARS_UPPER[n] : '\0';
  }

  @NonNull
  private static String _getHexEncoded (final byte @NonNull [] aInput,
                                        final int nOfs,
                                        final int nLen,
                                        final char @NonNull [] aHexChars)
  {
    final char [] ret = new char [nLen * 2];
    int nDst = 0;
    for (int i = nOfs; i < nOfs + nLen; ++i)
    {
      final int b = aInput[i];
      ret[nDst++] = aHexChars[(b >> 4) & 0x0f];
      ret[nDst++] = aHexChars[b & 0x0f];
    }
    return new String (ret);
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
  @NonNull
  public static String getHexEncoded (@NonNull final String sInput, @NonNull final Charset aCharset)
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
  @NonNull
  public static String getHexEncoded (final byte @NonNull [] aInput)
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
  @NonNull
  public static String getHexEncoded (final byte @NonNull [] aInput, final int nOfs, final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (aInput, nOfs, nLen);

    return _getHexEncoded (aInput, nOfs, nLen, HEX_CHARS_LOWER);
  }

  /**
   * Convert a byte array to an upper case hexadecimal encoded string.
   *
   * @param aInput
   *        The byte array to be converted to a String. May not be <code>null</code>.
   * @return The String representation of the byte array using the characters 0-9 and A-F.
   * @since 12.6.0
   */
  @NonNull
  public static String getHexEncodedUpperCase (final byte @NonNull [] aInput)
  {
    ValueEnforcer.notNull (aInput, "Input");

    return getHexEncodedUpperCase (aInput, 0, aInput.length);
  }

  /**
   * Convert a byte array to an upper case hexadecimal encoded string.
   *
   * @param aInput
   *        The byte array to be converted to a String. May not be <code>null</code>.
   * @param nOfs
   *        Byte array offset
   * @param nLen
   *        Number of bytes to encode
   * @return The String representation of the byte array using the characters 0-9 and A-F.
   * @since 12.6.0
   */
  @NonNull
  public static String getHexEncodedUpperCase (final byte @NonNull [] aInput, final int nOfs, final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (aInput, nOfs, nLen);

    return _getHexEncoded (aInput, nOfs, nLen, HEX_CHARS_UPPER);
  }

  /**
   * Convert a byte array to a hexadecimal encoded byte array.
   *
   * @param aInput
   *        The byte array to be converted to a String. May not be <code>null</code>.
   * @return The hex encoded byte array representation of the byte array.
   * @since 12.1.1
   */
  public static byte @NonNull [] getHexEncodedByteArray (final byte @NonNull [] aInput)
  {
    ValueEnforcer.notNull (aInput, "Input");

    return getHexEncodedByteArray (aInput, 0, aInput.length);
  }

  /**
   * Convert a byte array to a hexadecimal encoded byte array.
   *
   * @param aInput
   *        The byte array to be converted to a String. May not be <code>null</code>.
   * @param nOfs
   *        Byte array offset
   * @param nLen
   *        Number of bytes to encode
   * @return The hex encoded byte array representation of the byte array.
   * @since 12.1.1
   */
  public static byte @NonNull [] getHexEncodedByteArray (final byte @NonNull [] aInput, final int nOfs, final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (aInput, nOfs, nLen);

    final byte [] ret = new byte [nLen * 2];
    int nDst = 0;
    for (int i = nOfs; i < (nOfs + nLen); ++i)
    {
      final int b = aInput[i];
      ret[nDst++] = (byte) HEX_CHARS_LOWER[(b >> 4) & 0x0f];
      ret[nDst++] = (byte) HEX_CHARS_LOWER[b & 0x0f];
    }
    return ret;
  }

  /**
   * Get the decimal value of the passed hex character. Only the ASCII characters 0-9, a-f and A-F
   * are considered hex characters. Since 12.6.0 non-ASCII digits and letters (like fullwidth digits)
   * are no longer accepted.
   *
   * @param c
   *        The hex char to convert (0-9, a-f, A-F)
   * @return A value between 0 and 15, or -1 if the input character is not a hex char!
   */
  @CheckForSigned
  public static int getHexValue (@Nonnegative final char c)
  {
    return c < HEX_VALUES.length ? HEX_VALUES[c] : -1;
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
    return nHex1 < 0 || nHex2 < 0 ? -1 : (nHex1 << 4) | (nHex2 & 0xff);
  }

  /**
   * Decode a hex encoded string to a byte array.
   *
   * @param sInput
   *        The hex encoded string. May not be <code>null</code>.
   * @return The decoded byte array. Never <code>null</code>.
   */
  @ReturnsMutableCopy
  public static byte @NonNull [] getHexDecoded (@NonNull final String sInput)
  {
    ValueEnforcer.notNull (sInput, "Input");

    return getHexDecoded (sInput.toCharArray (), 0, sInput.length ());
  }

  /**
   * Decode a hex encoded char array to a byte array.
   *
   * @param aInput
   *        The hex encoded char array. May not be <code>null</code>.
   * @return The decoded byte array. Never <code>null</code>.
   */
  @ReturnsMutableCopy
  public static byte @NonNull [] getHexDecoded (final char @NonNull [] aInput)
  {
    ValueEnforcer.notNull (aInput, "Input");

    return getHexDecoded (aInput, 0, aInput.length);
  }

  /**
   * Decode a hex encoded char array to a byte array.
   *
   * @param aInput
   *        The hex encoded char array. May not be <code>null</code>.
   * @param nOfs
   *        The offset in the char array to start decoding from.
   * @param nLen
   *        The number of chars to decode. Must be an even number.
   * @return The decoded byte array. Never <code>null</code>.
   */
  @ReturnsMutableCopy
  public static byte @NonNull [] getHexDecoded (final char @NonNull [] aInput,
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

  /**
   * Decode a hex encoded character sequence to a byte array, without throwing an exception on
   * invalid input. Both lower case and upper case hex characters are accepted.
   *
   * @param aInput
   *        The hex encoded character sequence. May be <code>null</code>.
   * @return The decoded byte array or <code>null</code> if the input is <code>null</code>, has an
   *         odd length or contains a character that is not a hex char (0-9, a-f, A-F).
   * @since 12.6.0
   */
  @ReturnsMutableCopy
  public static byte @Nullable [] getHexDecodedOrNull (@Nullable final CharSequence aInput)
  {
    if (aInput == null)
      return null;
    final int nLen = aInput.length ();
    if ((nLen & 1) != 0)
      return null;

    final byte [] ret = new byte [nLen / 2];
    int nRetIdx = 0;
    for (int i = 0; i < nLen; i += 2)
    {
      final int n0 = getHexValue (aInput.charAt (i));
      final int n1 = getHexValue (aInput.charAt (i + 1));
      if (n0 < 0 || n1 < 0)
        return null;
      ret[nRetIdx++] = (byte) ((n0 << 4) | n1);
    }
    return ret;
  }

  /**
   * Decode a hex encoded byte array to a byte array.
   *
   * @param aInput
   *        The hex encoded byte array. May not be <code>null</code>.
   * @return The decoded byte array. Never <code>null</code>.
   */
  @ReturnsMutableCopy
  public static byte @NonNull [] getHexDecoded (final byte @NonNull [] aInput)
  {
    ValueEnforcer.notNull (aInput, "Input");

    return getHexDecoded (aInput, 0, aInput.length);
  }

  /**
   * Decode a hex encoded byte array to a byte array.
   *
   * @param aInput
   *        The hex encoded byte array. May not be <code>null</code>.
   * @param nOfs
   *        The offset in the byte array to start decoding from.
   * @param nLen
   *        The number of bytes to decode. Must be an even number.
   * @return The decoded byte array. Never <code>null</code>.
   */
  @ReturnsMutableCopy
  public static byte @NonNull [] getHexDecoded (final byte @NonNull [] aInput,
                                                @Nonnegative final int nOfs,
                                                @Nonnegative final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (aInput, nOfs, nLen);
    ValueEnforcer.isTrue ((nLen % 2) == 0, () -> "Passed chars have no even length: " + nLen);

    final byte [] ret = new byte [nLen / 2];
    int nRetIdx = 0;
    for (int i = 0; i < nLen; i += 2)
    {
      final char c0 = (char) (aInput[nOfs + i] & 0xff);
      final char c1 = (char) (aInput[nOfs + i + 1] & 0xff);
      final int n0 = getHexValue (c0);
      final int n1 = getHexValue (c1);
      if (n0 < 0 || n1 < 0)
        throw new IllegalArgumentException ("Failed to convert '" + c0 + "' and '" + c1 + "' to a hex value!");
      ret[nRetIdx++] = (byte) ((n0 << 4) | n1);
    }
    return ret;
  }

  /**
   * Convert a byte value to a hex string. The byte is handled as unsigned.
   *
   * @param nValue
   *        The byte value to convert.
   * @return The hex string representation of the unsigned byte value.
   */
  @NonNull
  public static String getHexString (final byte nValue)
  {
    // Bytes are always handled unsigned
    return Integer.toString (nValue & 0xff, CGlobal.HEX_RADIX);
  }

  /**
   * Convert a byte value to a hex string with leading zeros up to the specified number of digits.
   *
   * @param nValue
   *        The byte value to convert.
   * @param nDigits
   *        The minimum number of digits in the output string.
   * @return The hex string representation with leading zeros.
   */
  @NonNull
  public static String getHexStringLeadingZero (final byte nValue, final int nDigits)
  {
    return StringHelper.getLeadingZero (getHexString (nValue), nDigits);
  }

  /**
   * Convert a byte value to a hex string with a leading zero to ensure at least 2 digits. This is a
   * optimised shortcut for <code>getHexStringLeadingZero(nValue, 2)</code>.
   *
   * @param nValue
   *        The byte value to convert.
   * @return The hex string representation with at least 2 digits.
   */
  @NonNull
  public static String getHexStringLeadingZero2 (final byte nValue)
  {
    final String ret = getHexString (nValue);
    return ret.length () >= 2 ? ret : '0' + ret;
  }

  /**
   * Convert an int value to a hex string.
   *
   * @param nValue
   *        The int value to convert.
   * @return The hex string representation of the int value.
   */
  @NonNull
  public static String getHexString (final int nValue)
  {
    return Integer.toString (nValue, CGlobal.HEX_RADIX);
  }

  /**
   * Convert an int value to a hex string with leading zeros up to the specified number of digits.
   * Negative values are prefixed with a minus sign.
   *
   * @param nValue
   *        The int value to convert.
   * @param nDigits
   *        The minimum number of digits in the output string.
   * @return The hex string representation with leading zeros.
   */
  @NonNull
  public static String getHexStringLeadingZero (final int nValue, final int nDigits)
  {
    if (nValue < 0)
    {
      // Unsigned conversion so that Integer.MIN_VALUE (where -nValue == nValue) works as well
      return "-" + StringHelper.getLeadingZero (Integer.toUnsignedString (-nValue, CGlobal.HEX_RADIX), nDigits - 1);
    }
    return StringHelper.getLeadingZero (getHexString (nValue), nDigits);
  }

  /**
   * Append the hex representation of an int value with leading zeros up to the specified number of
   * digits to the passed {@link StringBuilder}, without creating intermediate String objects. The
   * result is identical to <code>aSB.append (getHexStringLeadingZero (nValue, nDigits))</code> (for
   * lower case), so negative values are prefixed with a minus sign and the minus sign counts as a
   * digit.
   *
   * @param aSB
   *        The string builder to append to. May not be <code>null</code>.
   * @param nValue
   *        The int value to convert.
   * @param nDigits
   *        The minimum number of characters to append.
   * @param bUpperCase
   *        <code>true</code> to use the characters A-F, <code>false</code> to use a-f.
   * @return The passed string builder. Never <code>null</code>.
   * @since 12.6.0
   */
  @NonNull
  public static StringBuilder appendHexStringLeadingZero (@NonNull final StringBuilder aSB,
                                                          final int nValue,
                                                          final int nDigits,
                                                          final boolean bUpperCase)
  {
    ValueEnforcer.notNull (aSB, "StringBuilder");

    final char [] aHexChars = bUpperCase ? HEX_CHARS_UPPER : HEX_CHARS_LOWER;
    int nMinDigits = nDigits;
    // Magnitude as unsigned value, so that Integer.MIN_VALUE works as well
    final int nMagnitude;
    if (nValue < 0)
    {
      aSB.append ('-');
      nMinDigits--;
      nMagnitude = -nValue;
    }
    else
      nMagnitude = nValue;

    // Number of significant hex digits (at least 1)
    final int nHexDigits = nMagnitude == 0 ? 1 : (Integer.SIZE - Integer.numberOfLeadingZeros (nMagnitude) + 3) / 4;
    for (int i = nHexDigits; i < nMinDigits; ++i)
      aSB.append ('0');
    for (int nShift = (nHexDigits - 1) * 4; nShift >= 0; nShift -= 4)
      aSB.append (aHexChars[(nMagnitude >>> nShift) & 0x0f]);
    return aSB;
  }

  /**
   * Convert a long value to a hex string.
   *
   * @param nValue
   *        The long value to convert.
   * @return The hex string representation of the long value.
   */
  @NonNull
  public static String getHexString (final long nValue)
  {
    return Long.toString (nValue, CGlobal.HEX_RADIX);
  }

  /**
   * Convert a long value to a hex string with leading zeros up to the specified number of digits.
   * Negative values are prefixed with a minus sign.
   *
   * @param nValue
   *        The long value to convert.
   * @param nDigits
   *        The minimum number of digits in the output string.
   * @return The hex string representation with leading zeros.
   */
  @NonNull
  public static String getHexStringLeadingZero (final long nValue, final int nDigits)
  {
    if (nValue < 0)
    {
      // Unsigned conversion so that Long.MIN_VALUE (where -nValue == nValue) works as well
      return "-" + StringHelper.getLeadingZero (Long.toUnsignedString (-nValue, CGlobal.HEX_RADIX), nDigits - 1);
    }
    return StringHelper.getLeadingZero (getHexString (nValue), nDigits);
  }

  /**
   * Convert a short value to a hex string. The short is handled as unsigned.
   *
   * @param nValue
   *        The short value to convert.
   * @return The hex string representation of the unsigned short value.
   */
  @NonNull
  public static String getHexString (final short nValue)
  {
    return Integer.toString (nValue & 0xffff, CGlobal.HEX_RADIX);
  }

  /**
   * Convert a short value to a hex string with leading zeros up to the specified number of digits.
   * The short is handled as unsigned.
   *
   * @param nValue
   *        The short value to convert.
   * @param nDigits
   *        The minimum number of digits in the output string.
   * @return The hex string representation with leading zeros.
   */
  @NonNull
  public static String getHexStringLeadingZero (final short nValue, final int nDigits)
  {
    // Short are always handled unsigned
    return StringHelper.getLeadingZero (getHexString (nValue), nDigits);
  }

}
