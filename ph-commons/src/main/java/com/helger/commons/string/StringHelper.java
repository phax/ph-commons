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
package com.helger.commons.string;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.CheckForSigned;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.function.ICharConsumer;
import com.helger.commons.math.MathHelper;

/**
 * Generic string transformation and helper methods. If you need to modify a
 * string, start looking in this class.
 *
 * @author Philip Helger
 */
@Immutable
public final class StringHelper
{
  /**
   * The constant to be returned if an String.indexOf call did not find a match!
   */
  public static final int STRING_NOT_FOUND = -1;

  private static final int [] s_aSizeTableInt = { 9,
                                                  99,
                                                  999,
                                                  9999,
                                                  99999,
                                                  999999,
                                                  9999999,
                                                  99999999,
                                                  999999999,
                                                  Integer.MAX_VALUE };
  private static final long [] s_aSizeTableLong = { 9L,
                                                    99L,
                                                    999L,
                                                    9999L,
                                                    99999L,
                                                    999999L,
                                                    9999999L,
                                                    99999999L,
                                                    999999999L,
                                                    9999999999L,
                                                    99999999999L,
                                                    999999999999L,
                                                    9999999999999L,
                                                    99999999999999L,
                                                    999999999999999L,
                                                    9999999999999999L,
                                                    99999999999999999L,
                                                    999999999999999999L,
                                                    Long.MAX_VALUE };
  @PresentForCodeCoverage
  private static final StringHelper s_aInstance = new StringHelper ();

  private StringHelper ()
  {}

  /**
   * Check if the string is <code>null</code> or empty.
   *
   * @param aCS
   *        The character sequence to check. May be <code>null</code>.
   * @return <code>true</code> if the string is <code>null</code> or empty,
   *         <code>false</code> otherwise
   */
  public static boolean hasNoText (@Nullable final CharSequence aCS)
  {
    return aCS == null || aCS.length () == 0;
  }

  /**
   * Check if the string is <code>null</code> or empty.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is <code>null</code> or empty,
   *         <code>false</code> otherwise
   */
  public static boolean hasNoText (@Nullable final String sStr)
  {
    return sStr == null || sStr.length () == 0;
  }

  /**
   * Check if the string is <code>null</code> or empty.
   *
   * @param aCS
   *        The character sequence to check. May be <code>null</code>.
   * @return <code>true</code> if the string is <code>null</code> or empty,
   *         <code>false</code> otherwise
   */
  public static boolean isEmpty (@Nullable final CharSequence aCS)
  {
    return hasNoText (aCS);
  }

  /**
   * Check if the string is <code>null</code> or empty.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is <code>null</code> or empty,
   *         <code>false</code> otherwise
   */
  public static boolean isEmpty (@Nullable final String sStr)
  {
    return hasNoText (sStr);
  }

  /**
   * Check if the string is <code>null</code> or empty after trimming.
   *
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is <code>null</code> or empty or
   *         consists only of whitespaces, <code>false</code> otherwise
   */
  public static boolean hasNoTextAfterTrim (@Nullable final String s)
  {
    return s == null || s.trim ().length () == 0;
  }

  /**
   * Check if the string is <code>null</code> or empty after trimming.
   *
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is <code>null</code> or empty or
   *         consists only of whitespaces, <code>false</code> otherwise
   */
  public static boolean isEmptyAfterTrim (@Nullable final String s)
  {
    return hasNoTextAfterTrim (s);
  }

  /**
   * Check if the string contains any char.
   *
   * @param aCS
   *        The character sequence to check. May be <code>null</code>.
   * @return <code>true</code> if the string contains at least one,
   *         <code>false</code> otherwise
   */
  public static boolean hasText (@Nullable final CharSequence aCS)
  {
    return aCS != null && aCS.length () > 0;
  }

  /**
   * Check if the string contains any char.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string contains at least one char,
   *         <code>false</code> otherwise
   */
  public static boolean hasText (@Nullable final String sStr)
  {
    return sStr != null && sStr.length () > 0;
  }

  /**
   * Check if the string contains any char.
   *
   * @param aCS
   *        The character sequence to check. May be <code>null</code>.
   * @return <code>true</code> if the string contains at least one char,
   *         <code>false</code> otherwise
   */
  public static boolean isNotEmpty (@Nullable final CharSequence aCS)
  {
    return hasText (aCS);
  }

  /**
   * Check if the string contains any char.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string contains at least one char,
   *         <code>false</code> otherwise
   */
  public static boolean isNotEmpty (@Nullable final String sStr)
  {
    return hasText (sStr);
  }

  /**
   * Check if the string neither <code>null</code> nor empty after trimming.
   *
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is neither <code>null</code> nor
   *         empty nor consists only of whitespaces, <code>false</code>
   *         otherwise
   */
  public static boolean hasTextAfterTrim (@Nullable final String s)
  {
    return s != null && s.trim ().length () > 0;
  }

  /**
   * Check if the string neither <code>null</code> nor empty after trimming.
   *
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is neither <code>null</code> nor
   *         empty nor consists only of whitespaces, <code>false</code>
   *         otherwise
   */
  public static boolean isNotEmptyAfterTrim (@Nullable final String s)
  {
    return hasTextAfterTrim (s);
  }

  /**
   * Check if the passed character sequence is only whitespace or not.
   *
   * @param s
   *        The character sequence to be checked. May not be <code>null</code>.
   * @return <code>true</code> if the passed sequence is empty or if only
   *         whitespace characters are contained.
   * @see Character#isWhitespace(char)
   */
  public static boolean isAllWhitespace (@Nonnull final CharSequence s)
  {
    final int nLen = s.length ();
    for (int i = 0; i < nLen; ++i)
      if (!Character.isWhitespace (s.charAt (i)))
        return false;
    return true;
  }

  @Nullable
  public static String getLeadingZero (@Nullable final Byte aValue, final int nChars)
  {
    return aValue == null ? null : getLeadingZero (aValue.byteValue (), nChars);
  }

  @Nullable
  public static String getLeadingZero (@Nullable final Integer aValue, final int nChars)
  {
    return aValue == null ? null : getLeadingZero (aValue.longValue (), nChars);
  }

  @Nullable
  public static String getLeadingZero (@Nullable final Long aValue, final int nChars)
  {
    return aValue == null ? null : getLeadingZero (aValue.longValue (), nChars);
  }

  @Nullable
  public static String getLeadingZero (@Nullable final Short aValue, final int nChars)
  {
    return aValue == null ? null : getLeadingZero (aValue.shortValue (), nChars);
  }

  @Nonnull
  public static String getLeadingZero (final int nValue, final int nChars)
  {
    final boolean bNeg = nValue < 0;
    final String sValue = Integer.toString (MathHelper.abs (nValue));
    if (sValue.length () >= nChars)
      return bNeg ? '-' + sValue : sValue;

    // prepend '0's
    final StringBuilder aSB = new StringBuilder ((bNeg ? 1 : 0) + nChars);
    if (bNeg)
      aSB.append ('-');
    for (int i = 0; i < nChars - sValue.length (); ++i)
      aSB.append ('0');
    return aSB.append (sValue).toString ();
  }

  @Nonnull
  public static String getLeadingZero (final long nValue, final int nChars)
  {
    final boolean bNeg = nValue < 0;
    final String sValue = Long.toString (MathHelper.abs (nValue));
    if (sValue.length () >= nChars)
      return bNeg ? '-' + sValue : sValue;

    // prepend '0's
    final StringBuilder aSB = new StringBuilder ((bNeg ? 1 : 0) + nChars);
    if (bNeg)
      aSB.append ('-');
    for (int i = 0; i < nChars - sValue.length (); ++i)
      aSB.append ('0');
    return aSB.append (sValue).toString ();
  }

  @Nonnull
  public static String getLeadingZero (@Nonnull final String sValue, final int nChars)
  {
    return getWithLeading (sValue, nChars, '0');
  }

  @Nonnull
  public static String getWithLeading (@Nonnull final String sValue, final int nChars, final char cPrefix)
  {
    final int nLen = sValue.length ();
    if (nLen >= nChars)
      return sValue;

    // prepend prefix chars
    final StringBuilder aSB = new StringBuilder (nChars);
    for (int i = 0; i < nChars - nLen; ++i)
      aSB.append (cPrefix);
    return aSB.append (sValue).toString ();
  }

  /**
   * Get the matching hex digit as a lower case character.
   *
   * @param n
   *        The value to get the hex digit from. Must be between 0 and 15.
   * @return The hex character (one of 0-9 or a-f), or '\0' if the value could
   *         not be converted
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
   * @return The hex character (one of 0-9 or A-F), or '\0' if the value could
   *         not be converted
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

    return getHexEncoded (CharsetManager.getAsBytes (sInput, aCharset));
  }

  /**
   * Convert a byte array to a hexadecimal encoded string.
   *
   * @param aInput
   *        The byte array to be converted to a String. May not be
   *        <code>null</code>.
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
   *        The byte array to be converted to a String. May not be
   *        <code>null</code>.
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
   * @return A value between 0 and 15, or -1 if the input character is not a hex
   *         char!
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
   * @return A value between 0 and 255, or -1 if any input character is not a
   *         hex char!
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
    ValueEnforcer.isTrue ((nLen % 2) == 0, "Passed chars have no even length: " + nLen);

    final byte [] ret = new byte [nLen / 2];
    int nRetIdx = 0;
    for (int i = 0; i < nLen; i += 2)
    {
      final char c0 = aInput[nOfs + i];
      final char c1 = aInput[nOfs + i + 1];
      final int nHexByte = getHexByte (c0, c1);
      if (nHexByte == -1)
        throw new IllegalArgumentException ("Failed to convert '" + c0 + "' or '" + c1 + "' to a hex value!");
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
    return getLeadingZero (getHexString (nValue), nDigits);
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
      return "-" + getLeadingZero (getHexString (-nValue), nDigits - 1);
    return getLeadingZero (getHexString (nValue), nDigits);
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
      return "-" + getLeadingZero (getHexString (-nValue), nDigits - 1);
    return getLeadingZero (getHexString (nValue), nDigits);
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
    return getLeadingZero (getHexString (nValue), nDigits);
  }

  /**
   * Get the number of leading white spaces according to
   * {@link Character#isWhitespace(char)}
   *
   * @param s
   *        The string to be parsed. May be <code>null</code>.
   * @return Always &ge; 0.
   */
  @Nonnegative
  public static int getLeadingWhitespaceCount (@Nullable final String s)
  {
    int ret = 0;
    if (s != null)
    {
      final int nMax = s.length ();
      while (ret < nMax && Character.isWhitespace (s.charAt (ret)))
        ++ret;
    }
    return ret;
  }

  /**
   * Get the number of trailing white spaces according to
   * {@link Character#isWhitespace(char)}
   *
   * @param s
   *        The string to be parsed. May be <code>null</code>.
   * @return Always &ge; 0.
   */
  @Nonnegative
  public static int getTrailingWhitespaceCount (@Nullable final String s)
  {
    int ret = 0;
    if (s != null)
    {
      int nLast = s.length () - 1;
      while (nLast >= 0 && Character.isWhitespace (s.charAt (nLast)))
      {
        ++ret;
        --nLast;
      }
    }
    return ret;
  }

  /**
   * Get the number of specified chars, the passed string starts with.
   *
   * @param s
   *        The string to be parsed. May be <code>null</code>.
   * @param c
   *        The char to be searched.
   * @return Always &ge; 0.
   */
  @Nonnegative
  public static int getLeadingCharCount (@Nullable final String s, final char c)
  {
    int ret = 0;
    if (s != null)
    {
      final int nMax = s.length ();
      while (ret < nMax && s.charAt (ret) == c)
        ++ret;
    }
    return ret;
  }

  /**
   * Get the number of specified chars, the passed string ends with.
   *
   * @param s
   *        The string to be parsed. May be <code>null</code>.
   * @param c
   *        The char to be searched.
   * @return Always &ge; 0.
   */
  @Nonnegative
  public static int getTrailingCharCount (@Nullable final String s, final char c)
  {
    int ret = 0;
    if (s != null)
    {
      int nLast = s.length () - 1;
      while (nLast >= 0 && s.charAt (nLast) == c)
      {
        ++ret;
        --nLast;
      }
    }
    return ret;
  }

  /**
   * Get a concatenated String from all elements of the passed container,
   * without a separator. Even <code>null</code> elements are added.
   *
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImploded (@Nullable final Iterable <?> aElements)
  {
    return getImploded (aElements, String::valueOf);
  }

  /**
   * Get a concatenated String from all elements of the passed container,
   * without a separator. Even <code>null</code> elements are added.
   *
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not
   *        be <code>null</code>.
   * @return The concatenated string.
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImploded (@Nullable final Iterable <ELEMENTTYPE> aElements,
                                                  @Nonnull final Function <ELEMENTTYPE, String> aMapper)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");

    final StringBuilder aSB = new StringBuilder ();
    if (aElements != null)
      for (final ELEMENTTYPE aElement : aElements)
        aSB.append (aMapper.apply (aElement));
    return aSB.toString ();
  }

  /**
   * Get a concatenated String from all elements of the passed container,
   * separated by the specified separator string. Even <code>null</code>
   * elements are added.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImploded (@Nonnull final String sSep, @Nullable final Iterable <?> aElements)
  {
    return getImploded (sSep, aElements, String::valueOf);
  }

  /**
   * Get a concatenated String from all elements of the passed container,
   * separated by the specified separator char. Even <code>null</code> elements
   * are added.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImploded (final char cSep, @Nullable final Iterable <?> aElements)
  {
    return getImploded (cSep, aElements, String::valueOf);
  }

  /**
   * Get a concatenated String from all elements of the passed container,
   * separated by the specified separator string. Even <code>null</code>
   * elements are added.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not
   *        be <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The element type of the collection to be imploded
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImploded (@Nonnull final String sSep,
                                                  @Nullable final Iterable <ELEMENTTYPE> aElements,
                                                  @Nonnull final Function <ELEMENTTYPE, String> aMapper)
  {
    ValueEnforcer.notNull (sSep, "Separator");
    ValueEnforcer.notNull (aMapper, "Mapper");

    final StringBuilder aSB = new StringBuilder ();
    if (aElements != null)
    {
      int nIndex = 0;
      for (final ELEMENTTYPE aElement : aElements)
      {
        if (nIndex++ > 0)
          aSB.append (sSep);
        aSB.append (aMapper.apply (aElement));
      }
    }
    return aSB.toString ();
  }

  /**
   * Get a concatenated String from all elements of the passed container,
   * separated by the specified separator char. Even <code>null</code> elements
   * are added.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not
   *        be <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The element type of the collection to be imploded
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImploded (final char cSep,
                                                  @Nullable final Iterable <ELEMENTTYPE> aElements,
                                                  @Nonnull final Function <ELEMENTTYPE, String> aMapper)
  {
    return getImploded (Character.toString (cSep), aElements, aMapper);
  }

  /**
   * Get a concatenated String from all elements of the passed map, separated by
   * the specified separator strings.
   *
   * @param sSepOuter
   *        The separator to use for separating the map entries. May not be
   *        <code>null</code>.
   * @param sSepInner
   *        The separator to use for separating the key from the value. May not
   *        be <code>null</code>.
   * @param aElements
   *        The map to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   * @param <KEYTYPE>
   *        Map key type
   * @param <VALUETYPE>
   *        Map value type
   */
  @Nonnull
  public static <KEYTYPE, VALUETYPE> String getImploded (@Nonnull final String sSepOuter,
                                                         @Nonnull final String sSepInner,
                                                         @Nullable final Map <KEYTYPE, VALUETYPE> aElements)
  {
    return getImploded (sSepOuter, sSepInner, aElements, String::valueOf, String::valueOf);
  }

  /**
   * Get a concatenated String from all elements of the passed map, separated by
   * the specified separator chars.
   *
   * @param cSepOuter
   *        The separator to use for separating the map entries.
   * @param cSepInner
   *        The separator to use for separating the key from the value.
   * @param aElements
   *        The map to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   * @param <KEYTYPE>
   *        Map key type
   * @param <VALUETYPE>
   *        Map value type
   */
  @Nonnull
  public static <KEYTYPE, VALUETYPE> String getImploded (final char cSepOuter,
                                                         final char cSepInner,
                                                         @Nullable final Map <KEYTYPE, VALUETYPE> aElements)
  {
    return getImploded (cSepOuter, cSepInner, aElements, String::valueOf, String::valueOf);
  }

  /**
   * Get a concatenated String from all elements of the passed map, separated by
   * the specified separator strings.
   *
   * @param sSepOuter
   *        The separator to use for separating the map entries. May not be
   *        <code>null</code>.
   * @param sSepInner
   *        The separator to use for separating the key from the value. May not
   *        be <code>null</code>.
   * @param aElements
   *        The map to convert. May be <code>null</code> or empty.
   * @param aKeyMapper
   *        The mapping function to convert from KEYTYPE to String. May not be
   *        <code>null</code>.
   * @param aValueMapper
   *        The mapping function to convert from VALUETYPE to String. May not be
   *        <code>null</code>.
   * @return The concatenated string.
   * @param <KEYTYPE>
   *        Map key type
   * @param <VALUETYPE>
   *        Map value type
   */
  @Nonnull
  public static <KEYTYPE, VALUETYPE> String getImploded (@Nonnull final String sSepOuter,
                                                         @Nonnull final String sSepInner,
                                                         @Nullable final Map <KEYTYPE, VALUETYPE> aElements,
                                                         @Nonnull final Function <KEYTYPE, String> aKeyMapper,
                                                         @Nonnull final Function <VALUETYPE, String> aValueMapper)
  {
    ValueEnforcer.notNull (sSepOuter, "SepOuter");
    ValueEnforcer.notNull (sSepInner, "SepInner");

    final StringBuilder aSB = new StringBuilder ();
    if (aElements != null)
    {
      int nIndex = 0;
      for (final Map.Entry <KEYTYPE, VALUETYPE> aElement : aElements.entrySet ())
      {
        if (nIndex++ > 0)
          aSB.append (sSepOuter);
        aSB.append (aKeyMapper.apply (aElement.getKey ()))
           .append (sSepInner)
           .append (aValueMapper.apply (aElement.getValue ()));
      }
    }
    return aSB.toString ();
  }

  /**
   * Get a concatenated String from all elements of the passed map, separated by
   * the specified separator chars.
   *
   * @param cSepOuter
   *        The separator to use for separating the map entries.
   * @param cSepInner
   *        The separator to use for separating the key from the value.
   * @param aElements
   *        The map to convert. May be <code>null</code> or empty.
   * @param aKeyMapper
   *        The mapping function to convert from KEYTYPE to String. May not be
   *        <code>null</code>.
   * @param aValueMapper
   *        The mapping function to convert from VALUETYPE to String. May not be
   *        <code>null</code>.
   * @return The concatenated string.
   * @param <KEYTYPE>
   *        Map key type
   * @param <VALUETYPE>
   *        Map value type
   */
  @Nonnull
  public static <KEYTYPE, VALUETYPE> String getImploded (final char cSepOuter,
                                                         final char cSepInner,
                                                         @Nullable final Map <KEYTYPE, VALUETYPE> aElements,
                                                         @Nonnull final Function <KEYTYPE, String> aKeyMapper,
                                                         @Nonnull final Function <VALUETYPE, String> aValueMapper)
  {
    return getImploded (Character.toString (cSepOuter),
                        Character.toString (cSepInner),
                        aElements,
                        aKeyMapper,
                        aValueMapper);
  }

  /**
   * Get a concatenated String from all elements of the passed array, without a
   * separator.
   *
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   */
  @Nonnull
  @SafeVarargs
  public static <ELEMENTTYPE> String getImploded (@Nullable final ELEMENTTYPE... aElements)
  {
    return getImploded (aElements, String::valueOf);
  }

  /**
   * Get a concatenated String from all elements of the passed array, without a
   * separator.
   *
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param nOfs
   *        The offset to start from.
   * @param nLen
   *        The number of elements to implode.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImploded (@Nullable final ELEMENTTYPE [] aElements,
                                                  @Nonnegative final int nOfs,
                                                  @Nonnegative final int nLen)
  {
    return getImploded (aElements, nOfs, nLen, String::valueOf);
  }

  /**
   * Get a concatenated String from all elements of the passed array, without a
   * separator.
   *
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not
   *        be <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImploded (@Nullable final ELEMENTTYPE [] aElements,
                                                  @Nonnull final Function <ELEMENTTYPE, String> aMapper)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");

    if (ArrayHelper.isEmpty (aElements))
      return "";
    return getImploded (aElements, 0, aElements.length, aMapper);
  }

  /**
   * Get a concatenated String from all elements of the passed array, without a
   * separator.
   *
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param nOfs
   *        The offset to start from.
   * @param nLen
   *        The number of elements to implode.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not
   *        be <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImploded (@Nullable final ELEMENTTYPE [] aElements,
                                                  @Nonnegative final int nOfs,
                                                  @Nonnegative final int nLen,
                                                  @Nonnull final Function <ELEMENTTYPE, String> aMapper)
  {
    if (aElements != null)
      ValueEnforcer.isArrayOfsLen (aElements, nOfs, nLen);
    ValueEnforcer.notNull (aMapper, "Mapper");

    final StringBuilder aSB = new StringBuilder ();
    if (aElements != null)
      for (int i = nOfs; i < nOfs + nLen; ++i)
        aSB.append (aMapper.apply (aElements[i]));
    return aSB.toString ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated
   * by the specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   */
  @Nonnull
  @SafeVarargs
  public static <ELEMENTTYPE> String getImploded (@Nonnull final String sSep, @Nullable final ELEMENTTYPE... aElements)
  {
    return getImploded (sSep, aElements, String::valueOf);
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated
   * by the specified separator char.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   */
  @Nonnull
  @SafeVarargs
  public static <ELEMENTTYPE> String getImploded (final char cSep, @Nullable final ELEMENTTYPE... aElements)
  {
    return getImploded (cSep, aElements, String::valueOf);
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated
   * by the specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param nOfs
   *        The offset to start from.
   * @param nLen
   *        The number of elements to implode.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImploded (@Nonnull final String sSep,
                                                  @Nullable final ELEMENTTYPE [] aElements,
                                                  @Nonnegative final int nOfs,
                                                  @Nonnegative final int nLen)
  {
    return getImploded (sSep, aElements, nOfs, nLen, String::valueOf);
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated
   * by the specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not
   *        be <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImploded (@Nonnull final String sSep,
                                                  @Nullable final ELEMENTTYPE [] aElements,
                                                  @Nonnull final Function <ELEMENTTYPE, String> aMapper)
  {
    ValueEnforcer.notNull (sSep, "Separator");
    ValueEnforcer.notNull (aMapper, "Mapper");

    if (ArrayHelper.isEmpty (aElements))
      return "";
    return getImploded (sSep, aElements, 0, aElements.length, aMapper);
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated
   * by the specified separator char.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not
   *        be <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImploded (final char cSep,
                                                  @Nullable final ELEMENTTYPE [] aElements,
                                                  @Nonnull final Function <ELEMENTTYPE, String> aMapper)
  {
    return getImploded (Character.toString (cSep), aElements, aMapper);
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated
   * by the specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param nOfs
   *        The offset to start from.
   * @param nLen
   *        The number of elements to implode.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not
   *        be <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImploded (@Nonnull final String sSep,
                                                  @Nullable final ELEMENTTYPE [] aElements,
                                                  @Nonnegative final int nOfs,
                                                  @Nonnegative final int nLen,
                                                  @Nonnull final Function <ELEMENTTYPE, String> aMapper)
  {
    ValueEnforcer.notNull (sSep, "Separator");
    if (aElements != null)
      ValueEnforcer.isArrayOfsLen (aElements, nOfs, nLen);
    ValueEnforcer.notNull (aMapper, "Mapper");

    final StringBuilder aSB = new StringBuilder ();
    if (aElements != null)
    {
      for (int i = nOfs; i < nOfs + nLen; ++i)
      {
        final ELEMENTTYPE sElement = aElements[i];
        if (i > nOfs)
          aSB.append (sSep);
        aSB.append (aMapper.apply (sElement));
      }
    }
    return aSB.toString ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated
   * by the specified separator char.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param nOfs
   *        The offset to start from.
   * @param nLen
   *        The number of elements to implode.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImploded (final char cSep,
                                                  @Nullable final ELEMENTTYPE [] aElements,
                                                  @Nonnegative final int nOfs,
                                                  @Nonnegative final int nLen)
  {
    return getImploded (Character.toString (cSep), aElements, nOfs, nLen, String::valueOf);
  }

  /**
   * Get a concatenated String from all non-<code>null</code> and non empty
   * elements of the passed container without a separator string. This the very
   * generic version of {@link #getConcatenatedOnDemand(String, String)} for an
   * arbitrary number of elements.
   *
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImplodedNonEmpty (@Nullable final Iterable <String> aElements)
  {
    return getImplodedNonEmpty (aElements, Function.identity ());
  }

  /**
   * Get a concatenated String from all non-<code>null</code> and non empty
   * elements of the passed container without a separator string. This the very
   * generic version of {@link #getConcatenatedOnDemand(String, String)} for an
   * arbitrary number of elements.
   *
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not
   *        be <code>null</code>.
   * @return The concatenated string.
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedNonEmpty (@Nullable final Iterable <ELEMENTTYPE> aElements,
                                                          @Nonnull final Function <ELEMENTTYPE, String> aMapper)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");

    final StringBuilder aSB = new StringBuilder ();
    if (aElements != null)
      for (final ELEMENTTYPE aElement : aElements)
      {
        final String sElement = aMapper.apply (aElement);
        if (hasText (sElement))
          aSB.append (sElement);
      }
    return aSB.toString ();
  }

  /**
   * Get a concatenated String from all non-<code>null</code> and non empty
   * elements of the passed container, separated by the specified separator
   * string. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary
   * number of elements.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImplodedNonEmpty (@Nonnull final String sSep, @Nullable final Iterable <String> aElements)
  {
    return getImplodedNonEmpty (sSep, aElements, Function.identity ());
  }

  /**
   * Get a concatenated String from all non-<code>null</code> and non empty
   * elements of the passed container, separated by the specified separator
   * char. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary
   * number of elements.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImplodedNonEmpty (final char cSep, @Nullable final Iterable <String> aElements)
  {
    return getImplodedNonEmpty (cSep, aElements, Function.identity ());
  }

  /**
   * Get a concatenated String from all non-<code>null</code> and non empty
   * elements of the passed container, separated by the specified separator
   * string. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary
   * number of elements.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not
   *        be <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The element type of the collection to be imploded
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedNonEmpty (@Nonnull final String sSep,
                                                          @Nullable final Iterable <ELEMENTTYPE> aElements,
                                                          @Nonnull final Function <ELEMENTTYPE, String> aMapper)
  {
    ValueEnforcer.notNull (sSep, "Separator");
    ValueEnforcer.notNull (aMapper, "Mapper");

    final StringBuilder aSB = new StringBuilder ();
    if (aElements != null)
    {
      int nElementsAdded = 0;
      for (final ELEMENTTYPE aElement : aElements)
      {
        final String sElement = aMapper.apply (aElement);
        if (hasText (sElement))
        {
          if (nElementsAdded++ > 0)
            aSB.append (sSep);
          aSB.append (sElement);
        }
      }
    }
    return aSB.toString ();
  }

  /**
   * Get a concatenated String from all non-<code>null</code> and non empty
   * elements of the passed container, separated by the specified separator
   * char. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary
   * number of elements.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not
   *        be <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The element type of the collection to be imploded
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedNonEmpty (final char cSep,
                                                          @Nullable final Iterable <ELEMENTTYPE> aElements,
                                                          @Nonnull final Function <ELEMENTTYPE, String> aMapper)
  {
    return getImplodedNonEmpty (Character.toString (cSep), aElements, aMapper);
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated
   * by the specified separator string. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary
   * number of elements.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImplodedNonEmpty (@Nonnull final String sSep, @Nullable final String... aElements)
  {
    return getImplodedNonEmpty (sSep, aElements, Function.identity ());
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated
   * by the specified separator char. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary
   * number of elements.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImplodedNonEmpty (final char cSep, @Nullable final String... aElements)
  {
    return getImplodedNonEmpty (cSep, aElements, Function.identity ());
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated
   * by the specified separator string. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary
   * number of elements.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not
   *        be <code>null</code>.
   * @return The concatenated string.
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedNonEmpty (@Nonnull final String sSep,
                                                          @Nullable final ELEMENTTYPE [] aElements,
                                                          @Nonnull final Function <ELEMENTTYPE, String> aMapper)
  {
    ValueEnforcer.notNull (sSep, "Separator");
    ValueEnforcer.notNull (aMapper, "Mapper");

    if (ArrayHelper.isEmpty (aElements))
      return "";
    return getImplodedNonEmpty (sSep, aElements, 0, aElements.length, aMapper);
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated
   * by the specified separator char. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary
   * number of elements.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not
   *        be <code>null</code>.
   * @return The concatenated string.
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedNonEmpty (final char cSep,
                                                          @Nullable final ELEMENTTYPE [] aElements,
                                                          @Nonnull final Function <ELEMENTTYPE, String> aMapper)
  {
    return getImplodedNonEmpty (Character.toString (cSep), aElements, aMapper);
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated
   * by the specified separator string. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary
   * number of elements.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param nOfs
   *        The offset to start from.
   * @param nLen
   *        The number of elements to implode.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImplodedNonEmpty (@Nonnull final String sSep,
                                            @Nullable final String [] aElements,
                                            @Nonnegative final int nOfs,
                                            @Nonnegative final int nLen)
  {
    return getImplodedNonEmpty (sSep, aElements, nOfs, nLen, Function.identity ());
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated
   * by the specified separator char. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary
   * number of elements.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param nOfs
   *        The offset to start from.
   * @param nLen
   *        The number of elements to implode.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImplodedNonEmpty (final char cSep,
                                            @Nullable final String [] aElements,
                                            @Nonnegative final int nOfs,
                                            @Nonnegative final int nLen)
  {
    return getImplodedNonEmpty (cSep, aElements, nOfs, nLen, Function.identity ());
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated
   * by the specified separator string. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary
   * number of elements.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param nOfs
   *        The offset to start from.
   * @param nLen
   *        The number of elements to implode.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not
   *        be <code>null</code>.
   * @return The concatenated string.
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedNonEmpty (@Nonnull final String sSep,
                                                          @Nullable final ELEMENTTYPE [] aElements,
                                                          @Nonnegative final int nOfs,
                                                          @Nonnegative final int nLen,
                                                          @Nonnull final Function <ELEMENTTYPE, String> aMapper)
  {
    ValueEnforcer.notNull (sSep, "Separator");
    if (aElements != null)
      ValueEnforcer.isArrayOfsLen (aElements, nOfs, nLen);
    ValueEnforcer.notNull (aMapper, "Mapper");

    final StringBuilder aSB = new StringBuilder ();
    if (aElements != null)
    {
      int nElementsAdded = 0;
      for (int i = nOfs; i < nOfs + nLen; ++i)
      {
        final String sElement = aMapper.apply (aElements[i]);
        if (hasText (sElement))
        {
          if (nElementsAdded++ > 0)
            aSB.append (sSep);
          aSB.append (sElement);
        }
      }
    }
    return aSB.toString ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated
   * by the specified separator char. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary
   * number of elements.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param nOfs
   *        The offset to start from.
   * @param nLen
   *        The number of elements to implode.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not
   *        be <code>null</code>.
   * @return The concatenated string.
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedNonEmpty (final char cSep,
                                                          @Nullable final ELEMENTTYPE [] aElements,
                                                          @Nonnegative final int nOfs,
                                                          @Nonnegative final int nLen,
                                                          @Nonnull final Function <ELEMENTTYPE, String> aMapper)
  {
    return getImplodedNonEmpty (Character.toString (cSep), aElements, nOfs, nLen, aMapper);
  }

  /**
   * Take a concatenated String and return the passed String array of all
   * elements in the passed string, using specified separator char.
   *
   * @param cSep
   *        The separator to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or
   *        empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le;
   *        0 all items are used. If max items is 1, than the result string is
   *        returned as is. If max items is larger than the number of elements
   *        found, it has no effect.
   * @return The passed collection and never <code>null</code>.
   */
  @Nonnull
  public static String [] getExplodedArray (final char cSep,
                                            @Nullable final String sElements,
                                            @CheckForSigned final int nMaxItems)
  {
    if (nMaxItems == 1)
      return new String [] { sElements };
    if (hasNoText (sElements))
      return ArrayHelper.EMPTY_STRING_ARRAY;

    final int nMaxResultElements = 1 + getCharCount (sElements, cSep);
    if (nMaxResultElements == 1)
    {
      // Separator not found
      return new String [] { sElements };
    }
    final String [] ret = new String [nMaxItems < 1 ? nMaxResultElements : Math.min (nMaxResultElements, nMaxItems)];

    // Do not use RegExCache.stringReplacePattern because of package
    // dependencies
    // Do not use String.split because it trims empty tokens from the end
    int nStartIndex = 0;
    int nMatchIndex;
    int nItemsAdded = 0;
    while ((nMatchIndex = sElements.indexOf (cSep, nStartIndex)) >= 0)
    {
      ret[nItemsAdded++] = sElements.substring (nStartIndex, nMatchIndex);
      // 1 == length of separator char
      nStartIndex = nMatchIndex + 1;
      if (nMaxItems > 0 && nItemsAdded == nMaxItems - 1)
      {
        // We have exactly one item the left: the rest of the string
        break;
      }
    }
    ret[nItemsAdded++] = sElements.substring (nStartIndex);
    if (nItemsAdded != ret.length)
      throw new IllegalStateException ("Added " + nItemsAdded + " but expected " + ret.length);
    return ret;
  }

  /**
   * Take a concatenated String and return the passed String array of all
   * elements in the passed string, using specified separator char.
   *
   * @param cSep
   *        The separator to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or
   *        empty.
   * @return The passed collection and never <code>null</code>.
   */
  @Nonnull
  public static String [] getExplodedArray (final char cSep, @Nullable final String sElements)
  {
    return getExplodedArray (cSep, sElements, -1);
  }

  /**
   * Take a concatenated String and return the passed Collection of all elements
   * in the passed string, using specified separator string.
   *
   * @param <COLLTYPE>
   *        The collection type to be passed and returned
   * @param cSep
   *        The separator to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or
   *        empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le;
   *        0 all items are used. If max items is 1, than the result string is
   *        returned as is. If max items is larger than the number of elements
   *        found, it has no effect.
   * @param aCollection
   *        The non-<code>null</code> target collection that should be filled
   *        with the exploded elements
   * @return The passed collection and never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableObject ("The passed parameter")
  public static <COLLTYPE extends Collection <String>> COLLTYPE getExploded (final char cSep,
                                                                             @Nullable final String sElements,
                                                                             final int nMaxItems,
                                                                             @Nonnull final COLLTYPE aCollection)
  {
    ValueEnforcer.notNull (aCollection, "Collection");

    if (nMaxItems == 1)
      aCollection.add (sElements);
    else
      if (hasText (sElements))
      {
        // Do not use RegExPool.stringReplacePattern because of package
        // dependencies
        // Do not use String.split because it trims empty tokens from the end
        int nStartIndex = 0;
        int nMatchIndex;
        int nItemsAdded = 0;
        while ((nMatchIndex = sElements.indexOf (cSep, nStartIndex)) >= 0)
        {
          aCollection.add (sElements.substring (nStartIndex, nMatchIndex));
          // 1 == length of separator char
          nStartIndex = nMatchIndex + 1;
          ++nItemsAdded;
          if (nMaxItems > 0 && nItemsAdded == nMaxItems - 1)
          {
            // We have exactly one item the left: the rest of the string
            break;
          }
        }
        aCollection.add (sElements.substring (nStartIndex));
      }
    return aCollection;
  }

  /**
   * Take a concatenated String and return a {@link List} of all elements in the
   * passed string, using specified separator string.
   *
   * @param cSep
   *        The separator character to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or
   *        empty.
   * @return The {@link List} represented by the passed string. Never
   *         <code>null</code>. If the passed input string is <code>null</code>
   *         or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static List <String> getExploded (final char cSep, @Nullable final String sElements)
  {
    return getExploded (cSep, sElements, -1);
  }

  /**
   * Take a concatenated String and return a {@link List} of all elements in the
   * passed string, using specified separator string.
   *
   * @param cSep
   *        The separator character to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or
   *        empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le;
   *        0 all items are used. If max items is 1, than the result string is
   *        returned as is. If max items is larger than the number of elements
   *        found, it has no effect.
   * @return The {@link List} represented by the passed string. Never
   *         <code>null</code>. If the passed input string is <code>null</code>
   *         or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static List <String> getExploded (final char cSep, @Nullable final String sElements, final int nMaxItems)
  {
    return getExploded (cSep,
                        sElements,
                        nMaxItems,
                        nMaxItems >= 1 ? new ArrayList <> (nMaxItems) : new ArrayList <> ());
  }

  /**
   * Take a concatenated String and return the passed Collection of all elements
   * in the passed string, using specified separator string.
   *
   * @param <COLLTYPE>
   *        The collection type to be used and returned
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or
   *        empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le;
   *        0 all items are used. If max items is 1, than the result string is
   *        returned as is. If max items is larger than the number of elements
   *        found, it has no effect.
   * @param aCollection
   *        The non-<code>null</code> target collection that should be filled
   *        with the exploded elements
   * @return The passed collection and never <code>null</code>.
   */
  @Nonnull
  public static <COLLTYPE extends Collection <String>> COLLTYPE getExploded (@Nonnull final String sSep,
                                                                             @Nullable final String sElements,
                                                                             final int nMaxItems,
                                                                             @Nonnull final COLLTYPE aCollection)
  {
    ValueEnforcer.notNull (sSep, "Separator");
    ValueEnforcer.notNull (aCollection, "Collection");

    if (nMaxItems == 1)
      aCollection.add (sElements);
    else
    {
      // If the separator consists only of a single character, use the
      // char-optimized version for better performance
      // Note: do it before the "hasText (sElements)" check, because the same
      // check is performed in the char version as well
      if (sSep.length () == 1)
        return getExploded (sSep.charAt (0), sElements, nMaxItems, aCollection);

      if (hasText (sElements))
      {
        // Do not use RegExPool.stringReplacePattern because of package
        // dependencies
        // Do not use String.split because it trims empty tokens from the end
        int nStartIndex = 0;
        int nMatchIndex;
        int nItemsAdded = 0;
        while ((nMatchIndex = sElements.indexOf (sSep, nStartIndex)) >= 0)
        {
          aCollection.add (sElements.substring (nStartIndex, nMatchIndex));
          nStartIndex = nMatchIndex + sSep.length ();
          ++nItemsAdded;
          if (nMaxItems > 0 && nItemsAdded == nMaxItems - 1)
          {
            // We have exactly one item the left: the rest of the string
            break;
          }
        }
        aCollection.add (sElements.substring (nStartIndex));
      }
    }
    return aCollection;
  }

  /**
   * Take a concatenated String and return a {@link List} of all elements in the
   * passed string, using specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or
   *        empty.
   * @return The {@link List} represented by the passed string. Never
   *         <code>null</code>. If the passed input string is <code>null</code>
   *         or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static List <String> getExploded (@Nonnull final String sSep, @Nullable final String sElements)
  {
    return getExploded (sSep, sElements, -1);
  }

  /**
   * Take a concatenated String and return a {@link List} of all elements in the
   * passed string, using specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or
   *        empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le;
   *        0 all items are used. If max items is 1, than the result string is
   *        returned as is. If max items is larger than the number of elements
   *        found, it has no effect.
   * @return The {@link List} represented by the passed string. Never
   *         <code>null</code>. If the passed input string is <code>null</code>
   *         or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static List <String> getExploded (@Nonnull final String sSep,
                                           @Nullable final String sElements,
                                           final int nMaxItems)
  {
    return getExploded (sSep,
                        sElements,
                        nMaxItems,
                        nMaxItems >= 1 ? new ArrayList <String> (nMaxItems) : new ArrayList <String> ());
  }

  /**
   * Take a concatenated String and return a {@link Set} of all elements in the
   * passed string, using specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or
   *        empty.
   * @return The {@link Set} represented by the passed string. Never
   *         <code>null</code>. If the passed input string is <code>null</code>
   *         or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static HashSet <String> getExplodedToSet (@Nonnull final String sSep, @Nullable final String sElements)
  {
    return getExploded (sSep, sElements, -1, new HashSet <String> ());
  }

  /**
   * Take a concatenated String and return an ordered {@link LinkedHashSet} of
   * all elements in the passed string, using specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or
   *        empty.
   * @return The ordered {@link Set} represented by the passed string. Never
   *         <code>null</code>. If the passed input string is <code>null</code>
   *         or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static LinkedHashSet <String> getExplodedToOrderedSet (@Nonnull final String sSep,
                                                                @Nullable final String sElements)
  {
    return getExploded (sSep, sElements, -1, new LinkedHashSet <String> ());
  }

  /**
   * Take a concatenated String and return a sorted {@link TreeSet} of all
   * elements in the passed string, using specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or
   *        empty.
   * @return The sorted {@link Set} represented by the passed string. Never
   *         <code>null</code>. If the passed input string is <code>null</code>
   *         or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static TreeSet <String> getExplodedToSortedSet (@Nonnull final String sSep, @Nullable final String sElements)
  {
    return getExploded (sSep, sElements, -1, new TreeSet <String> ());
  }

  /**
   * Get the passed string element repeated for a certain number of times. Each
   * string element is simply appended at the end of the string.
   *
   * @param cElement
   *        The character to get repeated.
   * @param nRepeats
   *        The number of repetitions to retrieve. May not be &lt; 0.
   * @return A non-<code>null</code> string containing the string element for
   *         the given number of times.
   */
  @Nonnull
  public static String getRepeated (final char cElement, @Nonnegative final int nRepeats)
  {
    ValueEnforcer.isGE0 (nRepeats, "Repeats");

    if (nRepeats == 0)
      return "";
    if (nRepeats == 1)
      return Character.toString (cElement);

    final char [] aElement = new char [nRepeats];
    Arrays.fill (aElement, cElement);
    return new String (aElement);
  }

  /**
   * Get the passed string element repeated for a certain number of times. Each
   * string element is simply appended at the end of the string.
   *
   * @param sElement
   *        The string to get repeated. May not be <code>null</code>.
   * @param nRepeats
   *        The number of repetitions to retrieve. May not be &lt; 0.
   * @return A non-<code>null</code> string containing the string element for
   *         the given number of times.
   */
  @Nonnull
  public static String getRepeated (@Nonnull final String sElement, @Nonnegative final int nRepeats)
  {
    ValueEnforcer.notNull (sElement, "Element");
    ValueEnforcer.isGE0 (nRepeats, "Repeats");

    final int nElementLength = sElement.length ();

    // Check if result length would exceed int range
    if ((long) nElementLength * nRepeats > Integer.MAX_VALUE)
      throw new IllegalArgumentException ("Resulting string exceeds the maximum integer length");

    if (nElementLength == 0 || nRepeats == 0)
      return "";
    if (nRepeats == 1)
      return sElement;

    // use character version
    if (nElementLength == 1)
      return getRepeated (sElement.charAt (0), nRepeats);

    // combine via StringBuilder
    final StringBuilder ret = new StringBuilder (nElementLength * nRepeats);
    for (int i = 0; i < nRepeats; ++i)
      ret.append (sElement);
    return ret.toString ();
  }

  /**
   * Concatenate the strings sFront and sEnd. If either front or back is
   * <code>null</code> or empty only the other element is returned. If both
   * strings are <code>null</code> or empty and empty String is returned.
   *
   * @param sFront
   *        Front string. May be <code>null</code>.
   * @param sEnd
   *        May be <code>null</code>.
   * @return The concatenated string. Never <code>null</code>.
   */
  @Nonnull
  public static String getConcatenatedOnDemand (@Nullable final String sFront, @Nullable final String sEnd)
  {
    if (sFront == null)
      return sEnd == null ? "" : sEnd;
    if (sEnd == null)
      return sFront;
    return sFront + sEnd;
  }

  /**
   * Concatenate the strings sFront and sEnd by the "sSep" string. If either
   * front or back is <code>null</code> or empty, the separator is not applied.
   *
   * @param sFront
   *        Front string. May be <code>null</code>.
   * @param sSep
   *        Separator string. May be <code>null</code>.
   * @param sEnd
   *        May be <code>null</code>.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getConcatenatedOnDemand (@Nullable final String sFront,
                                                @Nullable final String sSep,
                                                @Nullable final String sEnd)
  {
    final StringBuilder aSB = new StringBuilder ();
    if (hasText (sFront))
    {
      aSB.append (sFront);
      if (hasText (sSep) && hasText (sEnd))
        aSB.append (sSep);
    }
    if (hasText (sEnd))
      aSB.append (sEnd);
    return aSB.toString ();
  }

  /**
   * Concatenate the strings sFront and sEnd by the "cSep" separator. If either
   * front or back is <code>null</code> or empty, the separator is not applied.
   *
   * @param sFront
   *        Front string. May be <code>null</code>.
   * @param cSep
   *        Separator character.
   * @param sEnd
   *        May be <code>null</code>.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getConcatenatedOnDemand (@Nullable final String sFront,
                                                final char cSep,
                                                @Nullable final String sEnd)
  {
    final StringBuilder aSB = new StringBuilder ();
    if (hasText (sFront))
    {
      aSB.append (sFront);
      if (hasText (sEnd))
        aSB.append (cSep);
    }
    if (hasText (sEnd))
      aSB.append (sEnd);
    return aSB.toString ();
  }

  public static boolean startsWith (@Nullable final CharSequence aCS, final char c)
  {
    return hasText (aCS) && aCS.charAt (0) == c;
  }

  public static boolean startsWithAny (@Nullable final CharSequence aCS, @Nullable final char [] aChars)
  {
    if (hasText (aCS) && aChars != null)
      if (ArrayHelper.contains (aChars, aCS.charAt (0)))
        return true;
    return false;
  }

  public static boolean startsWithIgnoreCase (@Nullable final CharSequence aCS, final char c)
  {
    return hasText (aCS) && Character.toLowerCase (aCS.charAt (0)) == Character.toLowerCase (c);
  }

  public static boolean startsWith (@Nullable final CharSequence aCS, @Nullable final CharSequence aSearch)
  {
    if (aCS == null || aSearch == null)
      return false;
    final int nSearchLength = aSearch.length ();
    if (nSearchLength == 0)
      return true;
    final int nCSLength = aCS.length ();
    if (nCSLength < nSearchLength)
      return false;
    return aCS.subSequence (0, nSearchLength).equals (aSearch);
  }

  public static boolean startsWith (@Nullable final String sStr, @Nullable final String sSearch)
  {
    if (sStr == null || sSearch == null)
      return false;
    final int nSearchLength = sSearch.length ();
    if (nSearchLength == 0)
      return true;
    final int nStrLength = sStr.length ();
    if (nStrLength < nSearchLength)
      return false;
    return sStr.subSequence (0, nSearchLength).equals (sSearch);
  }

  public static boolean startsWithIgnoreCase (@Nullable final String sStr, @Nullable final String sSearch)
  {
    if (sStr == null || sSearch == null)
      return false;
    final int nSearchLength = sSearch.length ();
    if (nSearchLength == 0)
      return true;
    final int nStrLength = sStr.length ();
    if (nStrLength < nSearchLength)
      return false;
    return sStr.substring (0, nSearchLength).equalsIgnoreCase (sSearch);
  }

  public static boolean endsWith (@Nullable final CharSequence aCS, final char c)
  {
    return hasText (aCS) && getLastChar (aCS) == c;
  }

  public static boolean endsWithAny (@Nullable final CharSequence aCS, @Nullable final char [] aChars)
  {
    if (hasText (aCS) && aChars != null)
      if (ArrayHelper.contains (aChars, getLastChar (aCS)))
        return true;
    return false;
  }

  public static boolean endsWith (@Nullable final CharSequence aCS, @Nullable final CharSequence aSearch)
  {
    if (aCS == null || aSearch == null)
      return false;
    final int nSearchLength = aSearch.length ();
    if (nSearchLength == 0)
      return true;
    final int nCSLength = aCS.length ();
    if (nCSLength < nSearchLength)
      return false;
    return aCS.subSequence (nCSLength - nSearchLength, nCSLength).equals (aSearch);
  }

  public static boolean endsWith (@Nullable final String sStr, @Nullable final String sSearch)
  {
    if (sStr == null || sSearch == null)
      return false;
    final int nSearchLength = sSearch.length ();
    if (nSearchLength == 0)
      return true;
    final int nStrLength = sStr.length ();
    if (nStrLength < nSearchLength)
      return false;
    return sStr.startsWith (sSearch, nStrLength - nSearchLength);
  }

  public static boolean endsWithIgnoreCase (@Nullable final CharSequence aCS, final char c)
  {
    return hasText (aCS) && Character.toLowerCase (getLastChar (aCS)) == Character.toLowerCase (c);
  }

  public static boolean endsWithIgnoreCase (@Nullable final String sStr, @Nullable final String sSearch)
  {
    if (sStr == null || sSearch == null)
      return false;
    final int nSearchLength = sSearch.length ();
    if (nSearchLength == 0)
      return true;
    final int nStrLength = sStr.length ();
    if (nStrLength < nSearchLength)
      return false;
    return sStr.substring (nStrLength - nSearchLength, nStrLength).equalsIgnoreCase (sSearch);
  }

  /**
   * Get the first index of sSearch within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @return The first index of sSearch within sText or
   *         {@value #STRING_NOT_FOUND} if sSearch was not found or if any
   *         parameter was <code>null</code>.
   * @see String#indexOf(String)
   */
  public static int getIndexOf (@Nullable final String sText, @Nullable final String sSearch)
  {
    return sText != null && sSearch != null && sText.length () >= sSearch.length () ? sText.indexOf (sSearch)
                                                                                    : STRING_NOT_FOUND;
  }

  /**
   * Get the first index of sSearch within sText starting at index nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @return The first index of sSearch within sText or
   *         {@value #STRING_NOT_FOUND} if sSearch was not found or if any
   *         parameter was <code>null</code>.
   * @see String#indexOf(String,int)
   */
  public static int getIndexOf (@Nullable final String sText,
                                @Nonnegative final int nFromIndex,
                                @Nullable final String sSearch)
  {
    return sText != null && sSearch != null && (sText.length () - nFromIndex) >= sSearch.length ()
                                                                                                   ? sText.indexOf (sSearch,
                                                                                                                    nFromIndex)
                                                                                                   : STRING_NOT_FOUND;
  }

  /**
   * Get the last index of sSearch within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @return The last index of sSearch within sText or
   *         {@value #STRING_NOT_FOUND} if sSearch was not found or if any
   *         parameter was <code>null</code>.
   * @see String#lastIndexOf(String)
   */
  public static int getLastIndexOf (@Nullable final String sText, @Nullable final String sSearch)
  {
    return sText != null && sSearch != null && sText.length () >= sSearch.length () ? sText.lastIndexOf (sSearch)
                                                                                    : STRING_NOT_FOUND;
  }

  /**
   * Get the last index of sSearch within sText starting at index nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @return The last index of sSearch within sText or
   *         {@value #STRING_NOT_FOUND} if sSearch was not found or if any
   *         parameter was <code>null</code>.
   * @see String#lastIndexOf(String,int)
   */
  public static int getLastIndexOf (@Nullable final String sText,
                                    @Nonnegative final int nFromIndex,
                                    @Nullable final String sSearch)
  {
    return sText != null && sSearch != null && (sText.length () - nFromIndex) >= sSearch.length ()
                                                                                                   ? sText.lastIndexOf (sSearch,
                                                                                                                        nFromIndex)
                                                                                                   : STRING_NOT_FOUND;
  }

  /**
   * Get the first index of cSearch within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param cSearch
   *        The character to search for. May be <code>null</code>.
   * @return The first index of sSearch within sText or
   *         {@value #STRING_NOT_FOUND} if cSearch was not found or if any
   *         parameter was <code>null</code>.
   * @see String#indexOf(int)
   */
  public static int getIndexOf (@Nullable final String sText, final char cSearch)
  {
    return sText != null && sText.length () >= 1 ? sText.indexOf (cSearch) : STRING_NOT_FOUND;
  }

  /**
   * Get the first index of cSearch within sText starting at index nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param cSearch
   *        The character to search for. May be <code>null</code>.
   * @return The first index of sSearch within sText or
   *         {@value #STRING_NOT_FOUND} if cSearch was not found or if any
   *         parameter was <code>null</code>.
   * @see String#indexOf(int,int)
   */
  public static int getIndexOf (@Nullable final String sText, @Nonnegative final int nFromIndex, final char cSearch)
  {
    return sText != null && (sText.length () - nFromIndex) >= 1 ? sText.indexOf (cSearch, nFromIndex)
                                                                : STRING_NOT_FOUND;
  }

  /**
   * Get the last index of cSearch within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param cSearch
   *        The character to search for. May be <code>null</code>.
   * @return The last index of sSearch within sText or
   *         {@value #STRING_NOT_FOUND} if cSearch was not found or if any
   *         parameter was <code>null</code>.
   * @see String#lastIndexOf(int)
   */
  public static int getLastIndexOf (@Nullable final String sText, final char cSearch)
  {
    return sText != null && sText.length () >= 1 ? sText.lastIndexOf (cSearch) : STRING_NOT_FOUND;
  }

  /**
   * Get the last index of cSearch within sText starting at index nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param cSearch
   *        The character to search for. May be <code>null</code>.
   * @return The last index of sSearch within sText or
   *         {@value #STRING_NOT_FOUND} if cSearch was not found or if any
   *         parameter was <code>null</code>.
   * @see String#lastIndexOf(int,int)
   */
  public static int getLastIndexOf (@Nullable final String sText, @Nonnegative final int nFromIndex, final char cSearch)
  {
    return sText != null && (sText.length () - nFromIndex) >= 1 ? sText.lastIndexOf (cSearch, nFromIndex)
                                                                : STRING_NOT_FOUND;
  }

  /**
   * Get the first index of sSearch within sText ignoring case.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The first index of sSearch within sText or
   *         {@value #STRING_NOT_FOUND} if sSearch was not found or if any
   *         parameter was <code>null</code>.
   * @see String#indexOf(String)
   */
  public static int getIndexOfIgnoreCase (@Nullable final String sText,
                                          @Nullable final String sSearch,
                                          @Nonnull final Locale aSortLocale)
  {
    return sText != null && sSearch != null && sText.length () >= sSearch.length ()
                                                                                    ? sText.toLowerCase (aSortLocale)
                                                                                           .indexOf (sSearch.toLowerCase (aSortLocale))
                                                                                    : STRING_NOT_FOUND;
  }

  /**
   * Get the first index of sSearch within sText ignoring case starting at index
   * nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The first index of sSearch within sText or
   *         {@value #STRING_NOT_FOUND} if sSearch was not found or if any
   *         parameter was <code>null</code>.
   * @see String#indexOf(String)
   */
  public static int getIndexOfIgnoreCase (@Nullable final String sText,
                                          @Nonnegative final int nFromIndex,
                                          @Nullable final String sSearch,
                                          @Nonnull final Locale aSortLocale)
  {
    return sText != null && sSearch != null && (sText.length () - nFromIndex) >= sSearch.length ()
                                                                                                   ? sText.toLowerCase (aSortLocale)
                                                                                                          .indexOf (sSearch.toLowerCase (aSortLocale),
                                                                                                                    nFromIndex)
                                                                                                   : STRING_NOT_FOUND;
  }

  /**
   * Get the last index of sSearch within sText ignoring case.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The last index of sSearch within sText or
   *         {@value #STRING_NOT_FOUND} if sSearch was not found or if any
   *         parameter was <code>null</code>.
   * @see String#lastIndexOf(String)
   */
  public static int getLastIndexOfIgnoreCase (@Nullable final String sText,
                                              @Nullable final String sSearch,
                                              @Nonnull final Locale aSortLocale)
  {
    return sText != null && sSearch != null && sText.length () >= sSearch.length ()
                                                                                    ? sText.toLowerCase (aSortLocale)
                                                                                           .lastIndexOf (sSearch.toLowerCase (aSortLocale))
                                                                                    : STRING_NOT_FOUND;
  }

  /**
   * Get the last index of sSearch within sText ignoring case starting at index
   * nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The last index of sSearch within sText or
   *         {@value #STRING_NOT_FOUND} if sSearch was not found or if any
   *         parameter was <code>null</code>.
   * @see String#lastIndexOf(String)
   */
  public static int getLastIndexOfIgnoreCase (@Nullable final String sText,
                                              @Nonnegative final int nFromIndex,
                                              @Nullable final String sSearch,
                                              @Nonnull final Locale aSortLocale)
  {
    return sText != null && sSearch != null && (sText.length () - nFromIndex) >= sSearch.length ()
                                                                                                   ? sText.toLowerCase (aSortLocale)
                                                                                                          .lastIndexOf (sSearch.toLowerCase (aSortLocale),
                                                                                                                        nFromIndex)
                                                                                                   : STRING_NOT_FOUND;
  }

  /**
   * Get the first index of cSearch within sText ignoring case.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param cSearch
   *        The char to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The first index of sSearch within sText or
   *         {@value #STRING_NOT_FOUND} if sSearch was not found or if any
   *         parameter was <code>null</code>.
   * @see String#indexOf(int)
   */
  public static int getIndexOfIgnoreCase (@Nullable final String sText,
                                          final char cSearch,
                                          @Nonnull final Locale aSortLocale)
  {
    return sText != null && sText.length () >= 1 ? sText.toLowerCase (aSortLocale)
                                                        .indexOf (Character.toLowerCase (cSearch))
                                                 : STRING_NOT_FOUND;
  }

  /**
   * Get the first index of cSearch within sText ignoring case starting at index
   * nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param cSearch
   *        The char to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The first index of sSearch within sText or
   *         {@value #STRING_NOT_FOUND} if sSearch was not found or if any
   *         parameter was <code>null</code>.
   * @see String#indexOf(int)
   */
  public static int getIndexOfIgnoreCase (@Nullable final String sText,
                                          @Nonnegative final int nFromIndex,
                                          final char cSearch,
                                          @Nonnull final Locale aSortLocale)
  {
    return sText != null && (sText.length () - nFromIndex) >= 1 ? sText.toLowerCase (aSortLocale)
                                                                       .indexOf (Character.toLowerCase (cSearch),
                                                                                 nFromIndex)
                                                                : STRING_NOT_FOUND;
  }

  /**
   * Get the last index of cSearch within sText ignoring case.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param cSearch
   *        The char to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The last index of sSearch within sText or
   *         {@value #STRING_NOT_FOUND} if sSearch was not found or if any
   *         parameter was <code>null</code>.
   * @see String#lastIndexOf(int)
   */
  public static int getLastIndexOfIgnoreCase (@Nullable final String sText,
                                              final char cSearch,
                                              @Nonnull final Locale aSortLocale)
  {
    return sText != null && sText.length () >= 1
                                                 ? sText.toLowerCase (aSortLocale)
                                                        .lastIndexOf (Character.toLowerCase (cSearch))
                                                 : STRING_NOT_FOUND;
  }

  /**
   * Get the last index of cSearch within sText ignoring case starting at index
   * nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param cSearch
   *        The char to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The last index of sSearch within sText or
   *         {@value #STRING_NOT_FOUND} if sSearch was not found or if any
   *         parameter was <code>null</code>.
   * @see String#lastIndexOf(int)
   */
  public static int getLastIndexOfIgnoreCase (@Nullable final String sText,
                                              @Nonnegative final int nFromIndex,
                                              final char cSearch,
                                              @Nonnull final Locale aSortLocale)
  {
    return sText != null && (sText.length () - nFromIndex) >= 1 ? sText.toLowerCase (aSortLocale)
                                                                       .lastIndexOf (Character.toLowerCase (cSearch),
                                                                                     nFromIndex)
                                                                : STRING_NOT_FOUND;
  }

  /**
   * Check if sSearch is contained within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @return <code>true</code> if sSearch is contained in sText,
   *         <code>false</code> otherwise.
   * @see String#contains(CharSequence)
   */
  public static boolean contains (@Nullable final String sText, @Nullable final String sSearch)
  {
    return getIndexOf (sText, sSearch) != STRING_NOT_FOUND;
  }

  /**
   * Check if cSearch is contained within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param cSearch
   *        The character to search for. May be <code>null</code>.
   * @return <code>true</code> if cSearch is contained in sText,
   *         <code>false</code> otherwise.
   * @see String#contains(CharSequence)
   */
  public static boolean contains (@Nullable final String sText, final char cSearch)
  {
    return getIndexOf (sText, cSearch) != STRING_NOT_FOUND;
  }

  /**
   * Check if sSearch is contained within sText ignoring case.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return <code>true</code> if sSearch is contained in sText,
   *         <code>false</code> otherwise.
   * @see String#contains(CharSequence)
   */
  public static boolean containsIgnoreCase (@Nullable final String sText,
                                            @Nullable final String sSearch,
                                            @Nonnull final Locale aSortLocale)
  {
    return getIndexOfIgnoreCase (sText, sSearch, aSortLocale) != STRING_NOT_FOUND;
  }

  /**
   * Check if cSearch is contained within sText ignoring case.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param cSearch
   *        The char to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return <code>true</code> if sSearch is contained in sText,
   *         <code>false</code> otherwise.
   * @see String#indexOf(int)
   */
  public static boolean containsIgnoreCase (@Nullable final String sText,
                                            final char cSearch,
                                            @Nonnull final Locale aSortLocale)
  {
    return getIndexOfIgnoreCase (sText, cSearch, aSortLocale) != STRING_NOT_FOUND;
  }

  /**
   * Check if any of the passed searched characters is contained in the input
   * char array.
   *
   * @param aInput
   *        The input char array. May be <code>null</code>.
   * @param aSearchChars
   *        The char array to search. May not be <code>null</code>.
   * @return <code>true</code> if at least any of the search char is contained
   *         in the input char array, <code>false</code> otherwise.
   */
  public static boolean containsAny (@Nullable final char [] aInput, @Nonnull final char [] aSearchChars)
  {
    ValueEnforcer.notNull (aSearchChars, "SearchChars");

    if (aInput != null)
      for (final char cIn : aInput)
        if (ArrayHelper.contains (aSearchChars, cIn))
          return true;
    return false;
  }

  /**
   * Check if any of the passed searched characters in contained in the input
   * string.
   *
   * @param sInput
   *        The input string. May be <code>null</code>.
   * @param aSearchChars
   *        The char array to search. May not be <code>null</code>.
   * @return <code>true</code> if at least any of the search char is contained
   *         in the input char array, <code>false</code> otherwise.
   */
  public static boolean containsAny (@Nullable final String sInput, @Nonnull final char [] aSearchChars)
  {
    return sInput != null && containsAny (sInput.toCharArray (), aSearchChars);
  }

  /**
   * Count the number of occurrences of sSearch within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @return A non-negative number of occurrences.
   */
  @Nonnegative
  public static int getOccurrenceCount (@Nullable final String sText, @Nullable final String sSearch)
  {
    int ret = 0;
    final int nTextLength = getLength (sText);
    final int nSearchLength = getLength (sSearch);
    if (nSearchLength > 0 && nTextLength >= nSearchLength)
    {
      int nLastIndex = 0, nIndex;
      do
      {
        // Start searching from the last result
        nIndex = getIndexOf (sText, nLastIndex, sSearch);
        if (nIndex != STRING_NOT_FOUND)
        {
          // Match found
          ++ret;

          // Identify the next starting position (relative index + number of
          // search strings)
          nLastIndex = nIndex + nSearchLength;
        }
      } while (nIndex != STRING_NOT_FOUND);
    }
    return ret;
  }

  /**
   * Count the number of occurrences of sSearch within sText ignoring case.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return A non-negative number of occurrences.
   */
  @Nonnegative
  public static int getOccurrenceCountIgnoreCase (@Nullable final String sText,
                                                  @Nullable final String sSearch,
                                                  @Nonnull final Locale aSortLocale)
  {
    return sText != null && sSearch != null ? getOccurrenceCount (sText.toLowerCase (aSortLocale),
                                                                  sSearch.toLowerCase (aSortLocale))
                                            : 0;
  }

  /**
   * Count the number of occurrences of cSearch within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param cSearch
   *        The character to search for.
   * @return A non-negative number of occurrences.
   */
  @Nonnegative
  public static int getOccurrenceCount (@Nullable final String sText, final char cSearch)
  {
    int ret = 0;
    final int nTextLength = getLength (sText);
    if (nTextLength >= 1)
    {
      int nLastIndex = 0, nIndex;
      do
      {
        // Start searching from the last result
        nIndex = getIndexOf (sText, nLastIndex, cSearch);
        if (nIndex != STRING_NOT_FOUND)
        {
          // Match found
          ++ret;

          // Identify the next starting position (relative index + number of
          // search strings)
          nLastIndex = nIndex + 1;
        }
      } while (nIndex != STRING_NOT_FOUND);
    }
    return ret;
  }

  /**
   * Count the number of occurrences of cSearch within sText ignoring case.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param cSearch
   *        The character to search for.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return A non-negative number of occurrences.
   */
  @Nonnegative
  public static int getOccurrenceCountIgnoreCase (@Nullable final String sText,
                                                  final char cSearch,
                                                  @Nonnull final Locale aSortLocale)
  {
    return sText != null ? getOccurrenceCount (sText.toLowerCase (aSortLocale), Character.toLowerCase (cSearch)) : 0;
  }

  /**
   * Remove any leading whitespaces from the passed string.
   *
   * @param s
   *        the String to be trimmed
   * @return the original String with all leading whitespaces removed
   */
  @Nullable
  @CheckReturnValue
  public static String trimLeadingWhitespaces (@Nullable final String s)
  {
    final int n = getLeadingWhitespaceCount (s);
    return n == 0 ? s : s.substring (n, s.length ());
  }

  /**
   * Remove any trailing whitespaces from the passed string.
   *
   * @param s
   *        the String to be cut
   * @return the original String with all trailing whitespaces removed
   */
  @Nullable
  @CheckReturnValue
  public static String trimTrailingWhitespaces (@Nullable final String s)
  {
    final int n = getTrailingWhitespaceCount (s);
    return n == 0 ? s : s.substring (0, s.length () - n);
  }

  /**
   * Trim the passed lead from the source value. If the source value does not
   * start with the passed lead, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param sLead
   *        The string to be trimmed of the beginning
   * @return The trimmed string, or the original input string, if the lead was
   *         not found
   * @see #trimEnd(String, String)
   * @see #trimStartAndEnd(String, String)
   * @see #trimStartAndEnd(String, String, String)
   */
  @Nullable
  @CheckReturnValue
  public static String trimStart (@Nullable final String sSrc, @Nullable final String sLead)
  {
    return startsWith (sSrc, sLead) ? sSrc.substring (sLead.length (), sSrc.length ()) : sSrc;
  }

  @Nullable
  @CheckReturnValue
  public static String trimStartRepeatedly (@Nullable final String sSrc, @Nullable final String sLead)
  {
    if (hasNoText (sSrc))
      return sSrc;

    final int nLeadLength = getLength (sLead);
    if (nLeadLength == 0)
      return sSrc;

    String ret = sSrc;
    while (startsWith (ret, sLead))
      ret = ret.substring (nLeadLength, ret.length ());
    return ret;
  }

  /**
   * Trim the passed lead from the source value. If the source value does not
   * start with the passed lead, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param cLead
   *        The char to be trimmed of the beginning
   * @return The trimmed string, or the original input string, if the lead was
   *         not found
   * @see #trimEnd(String, String)
   * @see #trimStartAndEnd(String, String)
   * @see #trimStartAndEnd(String, String, String)
   */
  @Nullable
  @CheckReturnValue
  public static String trimStart (@Nullable final String sSrc, final char cLead)
  {
    return startsWith (sSrc, cLead) ? sSrc.substring (1, sSrc.length ()) : sSrc;
  }

  @Nullable
  @CheckReturnValue
  public static String trimStartRepeatedly (@Nullable final String sSrc, final char cLead)
  {
    if (hasNoText (sSrc))
      return sSrc;

    String ret = sSrc;
    while (startsWith (ret, cLead))
      ret = ret.substring (1, ret.length ());
    return ret;
  }

  /**
   * Trim the passed lead from the source value. If the source value does not
   * start with the passed lead, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param nCount
   *        The number of characters to trim at the end.
   * @return The trimmed string, or an empty string if nCount is &ge; the length
   *         of the source string
   */
  @Nullable
  @CheckReturnValue
  public static String trimStart (@Nullable final String sSrc, @Nonnegative final int nCount)
  {
    if (nCount <= 0)
      return sSrc;
    return getLength (sSrc) <= nCount ? "" : sSrc.substring (nCount, sSrc.length ());
  }

  /**
   * Trim the passed tail from the source value. If the source value does not
   * end with the passed tail, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param sTail
   *        The string to be trimmed of the end
   * @return The trimmed string, or the original input string, if the tail was
   *         not found
   * @see #trimStart(String, String)
   * @see #trimStartAndEnd(String, String)
   * @see #trimStartAndEnd(String, String, String)
   */
  @Nullable
  @CheckReturnValue
  public static String trimEnd (@Nullable final String sSrc, @Nullable final String sTail)
  {
    return endsWith (sSrc, sTail) ? sSrc.substring (0, sSrc.length () - sTail.length ()) : sSrc;
  }

  @Nullable
  @CheckReturnValue
  public static String trimEndRepeatedly (@Nullable final String sSrc, @Nullable final String sTail)
  {
    if (hasNoText (sSrc))
      return sSrc;

    final int nTailLength = getLength (sTail);
    if (nTailLength == 0)
      return sSrc;

    String ret = sSrc;
    while (endsWith (ret, sTail))
      ret = ret.substring (0, ret.length () - nTailLength);
    return ret;
  }

  /**
   * Trim the passed tail from the source value. If the source value does not
   * end with the passed tail, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param cTail
   *        The char to be trimmed of the end
   * @return The trimmed string, or the original input string, if the tail was
   *         not found
   * @see #trimStart(String, String)
   * @see #trimStartAndEnd(String, String)
   * @see #trimStartAndEnd(String, String, String)
   */
  @Nullable
  @CheckReturnValue
  public static String trimEnd (@Nullable final String sSrc, final char cTail)
  {
    return endsWith (sSrc, cTail) ? sSrc.substring (0, sSrc.length () - 1) : sSrc;
  }

  @Nullable
  @CheckReturnValue
  public static String trimEndRepeatedly (@Nullable final String sSrc, final char cTail)
  {
    if (hasNoText (sSrc))
      return sSrc;

    String ret = sSrc;
    while (endsWith (ret, cTail))
      ret = ret.substring (0, ret.length () - 1);
    return ret;
  }

  /**
   * Trim the passed tail from the source value. If the source value does not
   * end with the passed tail, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param nCount
   *        The number of characters to trim at the end.
   * @return The trimmed string, or an empty string if nCount is &ge; the length
   *         of the source string
   */
  @Nullable
  @CheckReturnValue
  public static String trimEnd (@Nullable final String sSrc, @Nonnegative final int nCount)
  {
    if (nCount <= 0)
      return sSrc;
    return getLength (sSrc) <= nCount ? "" : sSrc.substring (0, sSrc.length () - nCount);
  }

  /**
   * Trim the passed lead and tail from the source value. If the source value
   * does not start with the passed trimmed value, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param sValueToTrim
   *        The string to be trimmed of the beginning and the end
   * @return The trimmed string, or the original input string, if the value to
   *         trim was not found
   * @see #trimStart(String, String)
   * @see #trimEnd(String, String)
   * @see #trimStartAndEnd(String, String, String)
   */
  @Nullable
  @CheckReturnValue
  public static String trimStartAndEnd (@Nullable final String sSrc, @Nullable final String sValueToTrim)
  {
    return trimStartAndEnd (sSrc, sValueToTrim, sValueToTrim);
  }

  /**
   * Trim the passed lead and tail from the source value. If the source value
   * does not start with the passed lead and does not end with the passed tail,
   * nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param sLead
   *        The string to be trimmed of the beginning
   * @param sTail
   *        The string to be trimmed of the end
   * @return The trimmed string, or the original input string, if the lead and
   *         the tail were not found
   * @see #trimStart(String, String)
   * @see #trimEnd(String, String)
   * @see #trimStartAndEnd(String, String)
   */
  @Nullable
  @CheckReturnValue
  public static String trimStartAndEnd (@Nullable final String sSrc,
                                        @Nullable final String sLead,
                                        @Nullable final String sTail)
  {
    final String sInbetween = trimStart (sSrc, sLead);
    return trimEnd (sInbetween, sTail);
  }

  /**
   * Trim the passed string, if it is not <code>null</code>.
   *
   * @param s
   *        The string to be trimmed. May be <code>null</code>.
   * @return <code>null</code> if the input string was <code>null</code>, the
   *         non-<code>null</code> trimmed string otherwise.
   * @see String#trim()
   */
  @Nullable
  @CheckReturnValue
  public static String trim (@Nullable final String s)
  {
    return hasNoText (s) ? s : s.trim ();
  }

  /**
   * Get the first character of the passed character sequence
   *
   * @param aCS
   *        The source character sequence
   * @return {@link CGlobal#ILLEGAL_CHAR} if the passed sequence was empty
   */
  public static char getFirstChar (@Nullable final CharSequence aCS)
  {
    return hasText (aCS) ? aCS.charAt (0) : CGlobal.ILLEGAL_CHAR;
  }

  /**
   * Get the first character of the passed array
   *
   * @param aChars
   *        The character array
   * @return {@link CGlobal#ILLEGAL_CHAR} if the passed array was empty
   */
  public static char getFirstChar (@Nullable final char [] aChars)
  {
    return ArrayHelper.getFirst (aChars, CGlobal.ILLEGAL_CHAR);
  }

  /**
   * Get the last character of the passed character sequence
   *
   * @param aCS
   *        The source character sequence
   * @return {@link CGlobal#ILLEGAL_CHAR} if the passed sequence was empty
   */
  public static char getLastChar (@Nullable final CharSequence aCS)
  {
    final int nLength = getLength (aCS);
    return nLength > 0 ? aCS.charAt (nLength - 1) : CGlobal.ILLEGAL_CHAR;
  }

  /**
   * Get the last character of the passed array
   *
   * @param aChars
   *        The character array
   * @return {@link CGlobal#ILLEGAL_CHAR} if the passed array was empty
   */
  public static char getLastChar (@Nullable final char [] aChars)
  {
    return ArrayHelper.getLast (aChars, CGlobal.ILLEGAL_CHAR);
  }

  @Nonnegative
  public static int getCharCount (@Nullable final String s, final char cSearch)
  {
    return s == null ? 0 : getCharCount (s.toCharArray (), cSearch);
  }

  @Nonnegative
  public static int getCharCount (@Nullable final char [] aChars, final char cSearch)
  {
    int ret = 0;
    if (aChars != null)
      for (final char c : aChars)
        if (c == cSearch)
          ++ret;
    return ret;
  }

  @Nonnegative
  public static int getLineCount (@Nullable final String s)
  {
    return getLineCount (s, '\n');
  }

  @Nonnegative
  public static int getLineCount (@Nullable final String s, final char cLineSep)
  {
    return 1 + getCharCount (s, cLineSep);
  }

  /**
   * Get the number of characters the passed value would occupy in a string
   * representation.<br>
   * Copied from java.lang.Integer#StringSize
   *
   * @param nValue
   *        The integer value to check. May be be positive or negative.
   * @return Number of characters required. Alyways &gt; 0.
   */
  @Nonnegative
  public static int getCharacterCount (final int nValue)
  {
    // index is always one character less than the real size; that's why nPrefix
    // is 1 more!
    final int nPrefix = nValue < 0 ? 2 : 1;
    final int nRealValue = MathHelper.abs (nValue);

    for (int nIndex = 0;; nIndex++)
      if (nRealValue <= s_aSizeTableInt[nIndex])
        return nPrefix + nIndex;
  }

  /**
   * Get the number of characters the passed value would occupy in a string
   * representation.
   *
   * @param nValue
   *        The long value to check. May be be positive or negative.
   * @return Number of characters required. Always &gt; 0.
   */
  @Nonnegative
  public static int getCharacterCount (final long nValue)
  {
    // index is always one character less than the real size; that's why nPrefix
    // is 1 more!
    final int nPrefix = nValue < 0 ? 2 : 1;
    final long nRealValue = MathHelper.abs (nValue);

    for (int nIndex = 0;; nIndex++)
      if (nRealValue <= s_aSizeTableLong[nIndex])
        return nPrefix + nIndex;
  }

  @Nonnull
  public static String getCutAfterLength (@Nonnull final String sValue, @Nonnegative final int nMaxLength)
  {
    return getCutAfterLength (sValue, nMaxLength, null);
  }

  @Nonnull
  public static String getCutAfterLength (@Nonnull final String sValue,
                                          @Nonnegative final int nMaxLength,
                                          @Nullable final String sNewSuffix)
  {
    ValueEnforcer.notNull (sValue, "Value");
    ValueEnforcer.isGE0 (nMaxLength, "MaxLength");

    if (sValue.length () <= nMaxLength)
      return sValue;
    if (isEmpty (sNewSuffix))
      return sValue.substring (0, nMaxLength);
    return sValue.substring (0, nMaxLength) + sNewSuffix;
  }

  /**
   * Same as {@link #replaceAll(String, String, CharSequence)} but allowing for
   * a <code>null</code> new-value, which is than interpreted as an empty string
   * instead.
   *
   * @param sInputString
   *        The input string where the text should be replace. If this parameter
   *        is <code>null</code> or empty, no replacement is done.
   * @param sSearchText
   *        The string to be replaced. May neither be <code>null</code> nor
   *        empty.
   * @param aReplacementText
   *        The string with the replacement. May be <code>null</code> or empty.
   * @return The input string as is, if the input string is empty or if the
   *         string to be replaced is not contained.
   */
  public static String replaceAllSafe (@Nullable final String sInputString,
                                       @Nonnull final String sSearchText,
                                       @Nullable final CharSequence aReplacementText)
  {
    return replaceAll (sInputString, sSearchText, getNotNull (aReplacementText, ""));
  }

  /**
   * This is a fast replacement for
   * {@link String#replace(CharSequence, CharSequence)}. The problem with the
   * mentioned {@link String} method is, that is uses internally regular
   * expressions which use a synchronized block to compile the patterns. This
   * method is inherently thread safe since {@link String} is immutable and
   * we're operating on different temporary {@link StringBuilder} objects.
   *
   * @param sInputString
   *        The input string where the text should be replace. If this parameter
   *        is <code>null</code> or empty, no replacement is done.
   * @param sSearchText
   *        The string to be replaced. May neither be <code>null</code> nor
   *        empty.
   * @param aReplacementText
   *        The string with the replacement. May not be <code>null</code> but
   *        may be empty.
   * @return The input string as is, if the input string is empty or if the
   *         search pattern and the replacement are equal or if the string to be
   *         replaced is not contained.
   */
  @Nullable
  public static String replaceAll (@Nullable final String sInputString,
                                   @Nonnull final String sSearchText,
                                   @Nonnull final CharSequence aReplacementText)
  {
    ValueEnforcer.notEmpty (sSearchText, "SearchText");
    ValueEnforcer.notNull (aReplacementText, "ReplacementText");

    // Is input string empty?
    if (hasNoText (sInputString))
      return sInputString;

    // Replace old with the same new?
    final int nOldLength = sSearchText.length ();
    final int nNewLength = aReplacementText.length ();
    if (nOldLength == nNewLength)
    {
      // Any change?
      if (sSearchText.equals (aReplacementText))
        return sInputString;

      if (nOldLength == 1)
      {
        // Use char version which is more efficient
        return replaceAll (sInputString, sSearchText.charAt (0), aReplacementText.charAt (0));
      }
    }

    // Does the old text occur anywhere?
    int nIndex = sInputString.indexOf (sSearchText, 0);
    if (nIndex == STRING_NOT_FOUND)
      return sInputString;

    // build output buffer
    final StringBuilder ret = new StringBuilder (nOldLength >= nNewLength ? sInputString.length ()
                                                                          : sInputString.length () * 2);
    int nOldIndex = 0;
    do
    {
      ret.append (sInputString, nOldIndex, nIndex).append (aReplacementText);
      nIndex += nOldLength;
      nOldIndex = nIndex;
      nIndex = sInputString.indexOf (sSearchText, nIndex);
    } while (nIndex != STRING_NOT_FOUND);
    ret.append (sInputString, nOldIndex, sInputString.length ());
    return ret.toString ();
  }

  /**
   * This is a fast replacement for
   * {@link String#replace(CharSequence, CharSequence)} for characters. The
   * problem with the mentioned String method is, that is uses internally
   * regular expressions which use a synchronized block to compile the patterns.
   * This method is inherently thread safe since {@link String} is immutable and
   * we're operating on different temporary {@link StringBuilder} objects.
   *
   * @param sInputString
   *        The input string where the text should be replace. If this parameter
   *        is <code>null</code> or empty, no replacement is done.
   * @param cSearchChar
   *        The character to be replaced.
   * @param cReplacementChar
   *        The character with the replacement.
   * @return The input string as is, if the input string is empty or if the
   *         search pattern and the replacement are equal or if the string to be
   *         replaced is not contained.
   */
  @Nullable
  public static String replaceAll (@Nullable final String sInputString,
                                   final char cSearchChar,
                                   final char cReplacementChar)
  {
    // Is input string empty?
    if (hasNoText (sInputString))
      return sInputString;

    // Replace old with the same new?
    if (cSearchChar == cReplacementChar)
      return sInputString;

    // Does the old text occur anywhere?
    int nIndex = sInputString.indexOf (cSearchChar, 0);
    if (nIndex == STRING_NOT_FOUND)
      return sInputString;

    // build output buffer
    final StringBuilder ret = new StringBuilder (sInputString.length ());
    int nOldIndex = 0;
    do
    {
      ret.append (sInputString, nOldIndex, nIndex).append (cReplacementChar);
      nIndex++;
      nOldIndex = nIndex;
      nIndex = sInputString.indexOf (cSearchChar, nIndex);
    } while (nIndex != STRING_NOT_FOUND);
    ret.append (sInputString, nOldIndex, sInputString.length ());
    return ret.toString ();
  }

  /**
   * Just calls <code>replaceAll</code> as long as there are still replacements
   * found
   *
   * @param sInputString
   *        The input string where the text should be replace. If this parameter
   *        is <code>null</code> or empty, no replacement is done.
   * @param sSearchText
   *        The string to be replaced. May neither be <code>null</code> nor
   *        empty.
   * @param sReplacementText
   *        The string with the replacement. May not be <code>null</code> but
   *        may be empty.
   * @return The input string as is, if the input string is empty or if the
   *         string to be replaced is not contained.
   */
  @Nullable
  public static String replaceAllRepeatedly (@Nullable final String sInputString,
                                             @Nonnull final String sSearchText,
                                             @Nonnull final String sReplacementText)
  {
    ValueEnforcer.notEmpty (sSearchText, "SearchText");
    ValueEnforcer.notNull (sReplacementText, "ReplacementText");
    if (sReplacementText.contains (sSearchText))
      throw new IllegalArgumentException ("Loop detection: replacementText must not contain searchText");

    // Is input string empty?
    if (hasNoText (sInputString))
      return sInputString;

    String sRet = sInputString;
    String sLastLiteral;
    do
    {
      sLastLiteral = sRet;
      sRet = replaceAll (sRet, sSearchText, sReplacementText);
    } while (!sLastLiteral.equals (sRet));
    return sRet;
  }

  /**
   * Get the result length (in characters) when replacing all patterns with the
   * replacements on the passed input array.
   *
   * @param aInputString
   *        Input char array. May not be <code>null</code>.
   * @param aSearchChars
   *        The one-character search patterns. May not be <code>null</code>.
   * @param aReplacementStrings
   *        The replacements to be performed. May not be <code>null</code>. The
   *        first dimension of this array must have exactly the same amount of
   *        elements as the patterns parameter array.
   * @return {@link CGlobal#ILLEGAL_UINT} if no replacement was needed, and
   *         therefore the length of the input array could be used.
   */
  public static int getReplaceMultipleResultLength (@Nonnull final char [] aInputString,
                                                    @Nonnull @Nonempty final char [] aSearchChars,
                                                    @Nonnull @Nonempty final char [] [] aReplacementStrings)
  {
    int nResultLen = 0;
    boolean bAnyReplacement = false;
    for (final char cInput : aInputString)
    {
      // In case no replacement is found use a single char
      int nReplacementLength = 1;
      for (int nIndex = 0; nIndex < aSearchChars.length; nIndex++)
        if (cInput == aSearchChars[nIndex])
        {
          nReplacementLength = aReplacementStrings[nIndex].length;
          bAnyReplacement = true;
          break;
        }
      nResultLen += nReplacementLength;
    }
    return bAnyReplacement ? nResultLen : CGlobal.ILLEGAL_UINT;
  }

  /**
   * Optimized replace method that replaces a set of characters with a set of
   * strings. This method was created for efficient XML special character
   * replacements!
   *
   * @param sInputString
   *        The input string.
   * @param aSearchChars
   *        The characters to replace.
   * @param aReplacementStrings
   *        The new strings to be inserted instead. Must have the same array
   *        length as aPatterns.
   * @return The replaced version of the string or an empty char array if the
   *         input string was <code>null</code>.
   */
  @Nonnull
  public static char [] replaceMultiple (@Nullable final String sInputString,
                                         @Nonnull final char [] aSearchChars,
                                         @Nonnull final char [] [] aReplacementStrings)
  {
    // Any input text?
    if (hasNoText (sInputString))
      return ArrayHelper.EMPTY_CHAR_ARRAY;

    return replaceMultiple (sInputString.toCharArray (), aSearchChars, aReplacementStrings);
  }

  /**
   * Optimized replace method that replaces a set of characters with a set of
   * strings. This method was created for efficient XML special character
   * replacements!
   *
   * @param aInput
   *        The input string.
   * @param aSearchChars
   *        The characters to replace.
   * @param aReplacementStrings
   *        The new strings to be inserted instead. Must have the same array
   *        length as aPatterns.
   * @return The replaced version of the string or an empty char array if the
   *         input string was <code>null</code>.
   */
  @Nonnull
  public static char [] replaceMultiple (@Nullable final char [] aInput,
                                         @Nonnull final char [] aSearchChars,
                                         @Nonnull final char [] [] aReplacementStrings)
  {
    ValueEnforcer.notNull (aSearchChars, "SearchChars");
    ValueEnforcer.notNull (aReplacementStrings, "ReplacementStrings");
    if (aSearchChars.length != aReplacementStrings.length)
      throw new IllegalArgumentException ("array length mismatch");

    // Any input text?
    if (aInput == null || aInput.length == 0)
      return ArrayHelper.EMPTY_CHAR_ARRAY;

    // Any replacement patterns?
    if (aSearchChars.length == 0)
      return aInput;

    // get result length
    final int nResultLen = getReplaceMultipleResultLength (aInput, aSearchChars, aReplacementStrings);

    // nothing to replace in here?
    if (nResultLen == CGlobal.ILLEGAL_UINT)
      return aInput;

    // build result
    final char [] aOutput = new char [nResultLen];
    int nOutputIndex = 0;

    // For all input chars
    for (final char cInput : aInput)
    {
      boolean bFoundReplacement = false;
      for (int nPatternIndex = 0; nPatternIndex < aSearchChars.length; nPatternIndex++)
      {
        if (cInput == aSearchChars[nPatternIndex])
        {
          final char [] aReplacement = aReplacementStrings[nPatternIndex];
          final int nReplacementLength = aReplacement.length;
          System.arraycopy (aReplacement, 0, aOutput, nOutputIndex, nReplacementLength);
          nOutputIndex += nReplacementLength;
          bFoundReplacement = true;
          break;
        }
      }
      if (!bFoundReplacement)
      {
        // copy char as is
        aOutput[nOutputIndex++] = cInput;
      }
    }

    return aOutput;
  }

  /**
   * Specialized version of {@link #replaceMultiple(String, char[], char[][])}
   * where the object where the output should be appended is passed in as a
   * parameter. This has the advantage, that not length calculation needs to
   * take place!
   *
   * @param sInputString
   *        The input string.
   * @param aSearchChars
   *        The characters to replace.
   * @param aReplacementStrings
   *        The new strings to be inserted instead. Must have the same array
   *        length as aPatterns.
   * @param aTarget
   *        Where the replaced objects should be written to. May not be
   *        <code>null</code>.
   * @return The number of replacements performed. Always &ge; 0.
   * @throws IOException
   *         In case writing to the Writer fails
   */
  @Nonnegative
  public static int replaceMultipleTo (@Nullable final String sInputString,
                                       @Nonnull final char [] aSearchChars,
                                       @Nonnull final char [] [] aReplacementStrings,
                                       @Nonnull final Writer aTarget) throws IOException
  {
    if (hasNoText (sInputString))
      return 0;

    return replaceMultipleTo (sInputString.toCharArray (), aSearchChars, aReplacementStrings, aTarget);
  }

  /**
   * Specialized version of {@link #replaceMultiple(String, char[], char[][])}
   * where the object where the output should be appended is passed in as a
   * parameter. This has the advantage, that not length calculation needs to
   * take place!
   *
   * @param aInput
   *        The input string.
   * @param aSearchChars
   *        The characters to replace.
   * @param aReplacementStrings
   *        The new strings to be inserted instead. Must have the same array
   *        length as aPatterns.
   * @param aTarget
   *        Where the replaced objects should be written to. May not be
   *        <code>null</code>.
   * @return The number of replacements performed. Always &ge; 0.
   * @throws IOException
   *         In case writing to the Writer fails
   */
  @Nonnegative
  public static int replaceMultipleTo (@Nullable final char [] aInput,
                                       @Nonnull final char [] aSearchChars,
                                       @Nonnull final char [] [] aReplacementStrings,
                                       @Nonnull final Writer aTarget) throws IOException
  {
    ValueEnforcer.notNull (aSearchChars, "SearchChars");
    ValueEnforcer.notNull (aReplacementStrings, "ReplacementStrings");
    if (aSearchChars.length != aReplacementStrings.length)
      throw new IllegalArgumentException ("array length mismatch");
    ValueEnforcer.notNull (aTarget, "Target");

    if (aInput == null || aInput.length == 0)
      return 0;

    if (aSearchChars.length == 0)
    {
      // No modifications required
      aTarget.write (aInput);
      return 0;
    }

    // for all input string characters
    int nFirstNonReplace = 0;
    int nInputIndex = 0;
    int nTotalReplacements = 0;
    final int nMaxSearchChars = aSearchChars.length;
    for (final char cInput : aInput)
    {
      for (int nPatternIndex = 0; nPatternIndex < nMaxSearchChars; nPatternIndex++)
      {
        if (cInput == aSearchChars[nPatternIndex])
        {
          if (nFirstNonReplace < nInputIndex)
            aTarget.write (aInput, nFirstNonReplace, nInputIndex - nFirstNonReplace);
          nFirstNonReplace = nInputIndex + 1;
          aTarget.write (aReplacementStrings[nPatternIndex]);
          ++nTotalReplacements;
          break;
        }
      }
      nInputIndex++;
    }
    if (nFirstNonReplace < nInputIndex)
      aTarget.write (aInput, nFirstNonReplace, nInputIndex - nFirstNonReplace);
    return nTotalReplacements;
  }

  /**
   * Optimized replace method that replaces a set of characters with another
   * character. This method was created for efficient unsafe character
   * replacements!
   *
   * @param sInputString
   *        The input string.
   * @param aSearchChars
   *        The characters to replace.
   * @param cReplacementChar
   *        The new char to be used instead of the search chars.
   * @return The replaced version of the string or an empty char array if the
   *         input string was <code>null</code>.
   */
  @Nonnull
  public static char [] replaceMultiple (@Nullable final String sInputString,
                                         @Nonnull final char [] aSearchChars,
                                         final char cReplacementChar)
  {
    ValueEnforcer.notNull (aSearchChars, "SearchChars");

    // Any input text?
    if (hasNoText (sInputString))
      return ArrayHelper.EMPTY_CHAR_ARRAY;

    // Get char array
    final char [] aInput = sInputString.toCharArray ();

    // Any replacement patterns?
    if (aSearchChars.length == 0)
      return aInput;

    // build result
    final char [] aOutput = new char [aInput.length];
    int nOutputIndex = 0;
    for (final char c : aInput)
    {
      if (ArrayHelper.contains (aSearchChars, c))
        aOutput[nOutputIndex] = cReplacementChar;
      else
        aOutput[nOutputIndex] = c;
      nOutputIndex++;
    }
    return aOutput;
  }

  /**
   * Perform all string replacements on the input string as defined by the
   * passed map. All replacements are done using
   * {@link #replaceAll(String,String,CharSequence)} which is ok.
   *
   * @param sInputString
   *        The input string where the text should be replaced. May be
   *        <code>null</code>.
   * @param aTransTable
   *        The map with the replacements to execute. If <code>null</code> is
   *        passed, the input string is not altered.
   * @return <code>null</code> if the input string was <code>null</code>.
   */
  @Nullable
  public static String replaceMultiple (@Nullable final String sInputString,
                                        @Nullable final Map <String, String> aTransTable)
  {
    if (hasNoText (sInputString) || aTransTable == null || aTransTable.isEmpty ())
      return sInputString;

    String sOutput = sInputString;
    for (final Entry <String, String> aEntry : aTransTable.entrySet ())
      sOutput = replaceAll (sOutput, aEntry.getKey (), aEntry.getValue ());
    return sOutput;
  }

  /**
   * Perform all string replacements on the input string as defined by the
   * passed map. All replacements are done using
   * {@link #replaceAll(String,String,CharSequence)} which is ok.
   *
   * @param sInputString
   *        The input string where the text should be replaced. May be
   *        <code>null</code>.
   * @param aSearchTexts
   *        The texts to be searched. If <code>null</code> is passed, the input
   *        string is not altered.
   * @param aReplacementTexts
   *        The texts to be used as the replacements. This array must have
   *        exactly the same number of elements than the searched texts! If
   *        <code>null</code> is passed, the input string is not altered.
   * @return <code>null</code> if the input string was <code>null</code>. The
   *         unmodified input string if no search/replace patterns where
   *         provided.
   */
  @Nullable
  public static String replaceMultiple (@Nullable final String sInputString,
                                        @Nullable final String [] aSearchTexts,
                                        @Nullable final String [] aReplacementTexts)
  {
    if (hasNoText (sInputString))
      return sInputString;

    final int nSearchTextLength = aSearchTexts == null ? 0 : aSearchTexts.length;
    final int nReplacementTextLength = aReplacementTexts == null ? 0 : aReplacementTexts.length;
    if (nSearchTextLength != nReplacementTextLength)
      throw new IllegalArgumentException ("Array length mismatch!");

    // Nothing to replace?
    if (nSearchTextLength == 0)
      return sInputString;

    String sOutput = sInputString;
    for (int nIndex = 0; nIndex < nSearchTextLength; ++nIndex)
      sOutput = replaceAll (sOutput, aSearchTexts[nIndex], aReplacementTexts[nIndex]);
    return sOutput;
  }

  /**
   * Remove all occurrences of the passed character from the specified input
   * string
   *
   * @param sInputString
   *        The input string where the character should be removed. If this
   *        parameter is <code>null</code> or empty, no removing is done.
   * @param cRemoveChar
   *        The character to be removed.
   * @return The input string as is, if the input string is empty or if the
   *         remove char is not contained.
   */
  @Nullable
  public static String removeAll (@Nullable final String sInputString, final char cRemoveChar)
  {
    // Is input string empty?
    if (hasNoText (sInputString))
      return sInputString;

    // Does the char occur anywhere?
    final int nFirstIndex = sInputString.indexOf (cRemoveChar, 0);
    if (nFirstIndex == STRING_NOT_FOUND)
      return sInputString;

    // build output buffer
    final char [] aChars = sInputString.toCharArray ();
    final int nMax = aChars.length;
    final StringBuilder aSB = new StringBuilder (nMax);
    // Copy the first chars where we know it is not contained
    aSB.append (aChars, 0, nFirstIndex);
    // Start searching after the first occurrence because we know that this is a
    // char to be removed
    for (int i = nFirstIndex; i < nMax; ++i)
    {
      final char c = aChars[i];
      if (c != cRemoveChar)
        aSB.append (c);
    }
    return aSB.toString ();
  }

  /**
   * Remove all occurrences of the passed character from the specified input
   * string
   *
   * @param sInputString
   *        The input string where the character should be removed. If this
   *        parameter is <code>null</code> or empty, no removing is done.
   * @param sRemoveString
   *        The String to be removed. May be <code>null</code> or empty in which
   *        case nothing happens.
   * @return The input string as is, if the input string is empty or if the
   *         remove string is empty or not contained.
   */
  @Nullable
  public static String removeAll (@Nullable final String sInputString, @Nullable final String sRemoveString)
  {
    // Is input string empty?
    if (hasNoText (sInputString))
      return sInputString;

    final int nRemoveLength = getLength (sRemoveString);
    if (nRemoveLength == 0)
    {
      // Nothing to be removed
      return sInputString;
    }

    if (nRemoveLength == 1)
    {
      // Shortcut to char version
      return removeAll (sInputString, sRemoveString.charAt (0));
    }

    // Does the string occur anywhere?
    int nIndex = sInputString.indexOf (sRemoveString, 0);
    if (nIndex == STRING_NOT_FOUND)
      return sInputString;

    // build output buffer
    final StringBuilder ret = new StringBuilder (sInputString.length ());
    int nOldIndex = 0;
    do
    {
      ret.append (sInputString, nOldIndex, nIndex);
      nOldIndex = nIndex + nRemoveLength;
      nIndex = sInputString.indexOf (sRemoveString, nOldIndex);
    } while (nIndex != STRING_NOT_FOUND);
    ret.append (sInputString, nOldIndex, sInputString.length ());
    return ret.toString ();
  }

  /**
   * Get the length of the passed character sequence.
   *
   * @param aCS
   *        The character sequence who's length is to be determined. May be
   *        <code>null</code>.
   * @return 0 if the parameter is <code>null</code>, its length otherwise.
   * @see CharSequence#length()
   */
  @Nonnegative
  public static int getLength (@Nullable final CharSequence aCS)
  {
    return aCS == null ? 0 : aCS.length ();
  }

  /**
   * Get the passed string but never return <code>null</code>. If the passed
   * parameter is <code>null</code> an empty string is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code>.
   * @return An empty string if the passed parameter is <code>null</code>, the
   *         passed string otherwise.
   */
  @Nonnull
  public static String getNotNull (@Nullable final String s)
  {
    return getNotNull (s, "");
  }

  /**
   * Get the passed string but never return <code>null</code>. If the passed
   * parameter is <code>null</code> the second parameter is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code>.
   * @param sDefaultIfNull
   *        The value to be used of the first parameter is <code>null</code>.
   *        May be <code>null</code> but in this case the call to this method is
   *        obsolete.
   * @return The passed default value if the string is <code>null</code>,
   *         otherwise the input string.
   */
  @Nullable
  public static String getNotNull (@Nullable final String s, final String sDefaultIfNull)
  {
    return s == null ? sDefaultIfNull : s;
  }

  /**
   * Get the passed {@link CharSequence} but never return <code>null</code>. If
   * the passed parameter is <code>null</code> an empty string is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code>.
   * @return An empty string if the passed parameter is <code>null</code>, the
   *         passed {@link CharSequence} otherwise.
   */
  @Nonnull
  public static CharSequence getNotNull (@Nullable final CharSequence s)
  {
    return getNotNull (s, "");
  }

  /**
   * Get the passed {@link CharSequence} but never return <code>null</code>. If
   * the passed parameter is <code>null</code> the second parameter is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code>.
   * @param sDefaultIfNull
   *        The value to be used of the first parameter is <code>null</code>.
   *        May be <code>null</code> but in this case the call to this method is
   *        obsolete.
   * @return The passed default value if the string is <code>null</code>,
   *         otherwise the input {@link CharSequence}.
   */
  @Nullable
  public static CharSequence getNotNull (@Nullable final CharSequence s, final CharSequence sDefaultIfNull)
  {
    return s == null ? sDefaultIfNull : s;
  }

  /**
   * Get the passed string but never return an empty string. If the passed
   * parameter is <code>null</code> or empty the second parameter is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code> nor empty.
   * @param sDefaultIfEmpty
   *        The value to be used of the first parameter is <code>null</code> or
   *        empty. May be <code>null</code> but in this case the call to this
   *        method is obsolete.
   * @return The passed default value if the string is <code>null</code> or
   *         empty, otherwise the input string.
   */
  @Nullable
  public static String getNotEmpty (@Nullable final String s, @Nullable final String sDefaultIfEmpty)
  {
    return hasNoText (s) ? sDefaultIfEmpty : s;
  }

  /**
   * Get the passed string but never return an empty char sequence. If the
   * passed parameter is <code>null</code> or empty the second parameter is
   * returned.
   *
   * @param s
   *        The parameter to be not <code>null</code> nor empty.
   * @param sDefaultIfEmpty
   *        The value to be used of the first parameter is <code>null</code> or
   *        empty. May be <code>null</code> but in this case the call to this
   *        method is obsolete.
   * @return The passed default value if the char sequence is <code>null</code>
   *         or empty, otherwise the input char sequence.
   */
  @Nullable
  public static CharSequence getNotEmpty (@Nullable final CharSequence s, @Nullable final CharSequence sDefaultIfEmpty)
  {
    return hasNoText (s) ? sDefaultIfEmpty : s;
  }

  /**
   * Convert the passed object to a string using the {@link Object#toString()}
   * method.
   *
   * @param aObject
   *        The value to be converted. May be <code>null</code>.
   * @return An empty string in case the passed object was <code>null</code>.
   *         Never <code>null</code>.
   * @see Object#toString()
   */
  @Nonnull
  public static String getToString (@Nullable final Object aObject)
  {
    return getToString (aObject, "");
  }

  /**
   * Convert the passed object to a string using the {@link Object#toString()}
   * method or otherwise return the passed default value.
   *
   * @param aObject
   *        The value to be converted. May be <code>null</code>.
   * @param sNullValue
   *        The value to be returned in case the passed object is
   *        <code>null</code>. May be <code>null</code> itself.
   * @return The passed default value in case the passed object was
   *         <code>null</code> or the result of {@link Object#toString()} on the
   *         passed object.
   * @see Object#toString()
   */
  @Nullable
  public static String getToString (@Nullable final Object aObject, @Nullable final String sNullValue)
  {
    return aObject == null ? sNullValue : aObject.toString ();
  }

  /**
   * Get the passed string without the first char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @return An empty, non-<code>null</code> string if the passed string has a
   *         length &le; 1.
   */
  @Nonnull
  public static String getWithoutLeadingChar (@Nullable final String sStr)
  {
    return getWithoutLeadingChars (sStr, 1);
  }

  /**
   * Get the passed string without the specified number of leading chars.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param nCount
   *        The number of chars to remove.
   * @return An empty, non-<code>null</code> string if the passed string has a
   *         length &le; <code>nCount</code>.
   */
  @Nonnull
  public static String getWithoutLeadingChars (@Nullable final String sStr, @Nonnegative final int nCount)
  {
    ValueEnforcer.isGE0 (nCount, "Count");

    if (nCount == 0)
      return sStr;
    return getLength (sStr) <= nCount ? "" : sStr.substring (nCount);
  }

  /**
   * Get the passed string without the last char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @return An empty, non-<code>null</code> string if the passed string has a
   *         length &le; 1.
   */
  @Nonnull
  public static String getWithoutTrailingChar (@Nullable final String sStr)
  {
    return getWithoutTrailingChars (sStr, 1);
  }

  /**
   * Get the passed string without the specified number of trailing chars.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param nCount
   *        The number of chars to remove.
   * @return An empty, non-<code>null</code> string if the passed string has a
   *         length &le; <code>nCount</code>.
   */
  @Nonnull
  public static String getWithoutTrailingChars (@Nullable final String sStr, @Nonnegative final int nCount)
  {
    ValueEnforcer.isGE0 (nCount, "Count");

    if (nCount == 0)
      return sStr;
    final int nLength = getLength (sStr);
    return nLength <= nCount ? "" : sStr.substring (0, nLength - nCount);
  }

  /**
   * Get the passed string where all spaces (white spaces or unicode spaces)
   * have been removed.
   *
   * @param sStr
   *        The source string. May be <code>null</code>
   * @return A non-<code>null</code> string representing the passed string
   *         without any spaces
   */
  @Nonnull
  public static String getWithoutAnySpaces (@Nullable final String sStr)
  {
    if (sStr == null)
      return "";

    // Trim first
    final char [] aChars = sStr.trim ().toCharArray ();
    final StringBuilder aResult = new StringBuilder (aChars.length);
    for (final char c : aChars)
      if (!Character.isWhitespace (c) && !Character.isSpaceChar (c))
        aResult.append (c);
    return aResult.toString ();
  }

  @Nullable
  private static String _getUntilFirst (@Nullable final String sStr,
                                        final char cSearch,
                                        final boolean bIncludingSearchChar)
  {
    final int nIndex = getIndexOf (sStr, cSearch);
    return nIndex == STRING_NOT_FOUND ? null : sStr.substring (0, nIndex + (bIncludingSearchChar ? 1 : 0));
  }

  /**
   * Get everything from the string up to and including the first passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search
   *         character.
   */
  @Nullable
  public static String getUntilFirstIncl (@Nullable final String sStr, final char cSearch)
  {
    return _getUntilFirst (sStr, cSearch, true);
  }

  /**
   * Get everything from the string up to and excluding first the passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search
   *         character.
   */
  @Nullable
  public static String getUntilFirstExcl (@Nullable final String sStr, final char cSearch)
  {
    return _getUntilFirst (sStr, cSearch, false);
  }

  @Nullable
  private static String _getUntilFirst (@Nullable final String sStr,
                                        @Nullable final String sSearch,
                                        final boolean bIncludingSearchChar)
  {
    if (hasNoText (sSearch))
      return "";

    final int nIndex = getIndexOf (sStr, sSearch);
    return nIndex == STRING_NOT_FOUND ? null
                                      : sStr.substring (0, nIndex + (bIncludingSearchChar ? sSearch.length () : 0));
  }

  /**
   * Get everything from the string up to and including the first passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search
   *         string. If the search string is empty, the empty string is
   *         returned.
   */
  @Nullable
  public static String getUntilFirstIncl (@Nullable final String sStr, @Nullable final String sSearch)
  {
    return _getUntilFirst (sStr, sSearch, true);
  }

  /**
   * Get everything from the string up to and excluding the first passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search
   *         string. If the search string is empty, the empty string is
   *         returned.
   */
  @Nullable
  public static String getUntilFirstExcl (@Nullable final String sStr, @Nullable final String sSearch)
  {
    return _getUntilFirst (sStr, sSearch, false);
  }

  @Nullable
  private static String _getUntilLast (@Nullable final String sStr,
                                       final char cSearch,
                                       final boolean bIncludingSearchChar)
  {
    final int nIndex = getLastIndexOf (sStr, cSearch);
    return nIndex == STRING_NOT_FOUND ? null : sStr.substring (0, nIndex + (bIncludingSearchChar ? 1 : 0));
  }

  /**
   * Get everything from the string up to and including the first passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search
   *         character.
   */
  @Nullable
  public static String getUntilLastIncl (@Nullable final String sStr, final char cSearch)
  {
    return _getUntilLast (sStr, cSearch, true);
  }

  /**
   * Get everything from the string up to and excluding first the passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search
   *         character.
   */
  @Nullable
  public static String getUntilLastExcl (@Nullable final String sStr, final char cSearch)
  {
    return _getUntilLast (sStr, cSearch, false);
  }

  @Nullable
  private static String _getUntilLast (@Nullable final String sStr,
                                       @Nullable final String sSearch,
                                       final boolean bIncludingSearchChar)
  {
    if (hasNoText (sSearch))
      return "";

    final int nIndex = getLastIndexOf (sStr, sSearch);
    return nIndex == STRING_NOT_FOUND ? null
                                      : sStr.substring (0, nIndex + (bIncludingSearchChar ? sSearch.length () : 0));
  }

  /**
   * Get everything from the string up to and including the first passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search
   *         string. If the search string is empty, the empty string is
   *         returned.
   */
  @Nullable
  public static String getUntilLastIncl (@Nullable final String sStr, @Nullable final String sSearch)
  {
    return _getUntilLast (sStr, sSearch, true);
  }

  /**
   * Get everything from the string up to and excluding the first passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search
   *         string. If the search string is empty, the empty string is
   *         returned.
   */
  @Nullable
  public static String getUntilLastExcl (@Nullable final String sStr, @Nullable final String sSearch)
  {
    return _getUntilLast (sStr, sSearch, false);
  }

  @Nullable
  private static String _getFromFirst (@Nullable final String sStr,
                                       final char cSearch,
                                       final boolean bIncludingSearchChar)
  {
    final int nIndex = getIndexOf (sStr, cSearch);
    return nIndex == STRING_NOT_FOUND ? null : sStr.substring (nIndex + (bIncludingSearchChar ? 0 : 1));
  }

  /**
   * Get everything from the string from and including the first passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search
   *         character.
   */
  @Nullable
  public static String getFromFirstIncl (@Nullable final String sStr, final char cSearch)
  {
    return _getFromFirst (sStr, cSearch, true);
  }

  /**
   * Get everything from the string from and excluding the first passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search
   *         character.
   */
  @Nullable
  public static String getFromFirstExcl (@Nullable final String sStr, final char cSearch)
  {
    return _getFromFirst (sStr, cSearch, false);
  }

  @Nullable
  private static String _getFromFirst (@Nullable final String sStr,
                                       @Nullable final String sSearch,
                                       final boolean bIncludingSearchString)
  {
    if (hasNoText (sSearch))
      return sStr;

    final int nIndex = getIndexOf (sStr, sSearch);
    return nIndex == STRING_NOT_FOUND ? null
                                      : sStr.substring (nIndex + (bIncludingSearchString ? 0 : sSearch.length ()));
  }

  /**
   * Get everything from the string from and including the passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search
   *         string. If the search string is empty, the input string is returned
   *         unmodified.
   */
  @Nullable
  public static String getFromFirstIncl (@Nullable final String sStr, @Nullable final String sSearch)
  {
    return _getFromFirst (sStr, sSearch, true);
  }

  /**
   * Get everything from the string from and excluding the passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search
   *         string. If the search string is empty, the input string is returned
   *         unmodified.
   */
  @Nullable
  public static String getFromFirstExcl (@Nullable final String sStr, @Nullable final String sSearch)
  {
    return _getFromFirst (sStr, sSearch, false);
  }

  @Nullable
  private static String _getFromLast (@Nullable final String sStr,
                                      final char cSearch,
                                      final boolean bIncludingSearchChar)
  {
    final int nIndex = getLastIndexOf (sStr, cSearch);
    return nIndex == STRING_NOT_FOUND ? null : sStr.substring (nIndex + (bIncludingSearchChar ? 0 : 1));
  }

  /**
   * Get everything from the string from and including the first passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search
   *         character.
   */
  @Nullable
  public static String getFromLastIncl (@Nullable final String sStr, final char cSearch)
  {
    return _getFromLast (sStr, cSearch, true);
  }

  /**
   * Get everything from the string from and excluding the first passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search
   *         character.
   */
  @Nullable
  public static String getFromLastExcl (@Nullable final String sStr, final char cSearch)
  {
    return _getFromLast (sStr, cSearch, false);
  }

  @Nullable
  private static String _getFromLast (@Nullable final String sStr,
                                      @Nullable final String sSearch,
                                      final boolean bIncludingSearchString)
  {
    if (hasNoText (sSearch))
      return sStr;

    final int nIndex = getLastIndexOf (sStr, sSearch);
    return nIndex == STRING_NOT_FOUND ? null
                                      : sStr.substring (nIndex + (bIncludingSearchString ? 0 : sSearch.length ()));
  }

  /**
   * Get everything from the string from and including the passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search
   *         string. If the search string is empty, the input string is returned
   *         unmodified.
   */
  @Nullable
  public static String getFromLastIncl (@Nullable final String sStr, @Nullable final String sSearch)
  {
    return _getFromLast (sStr, sSearch, true);
  }

  /**
   * Get everything from the string from and excluding the passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search
   *         string. If the search string is empty, the input string is returned
   *         unmodified.
   */
  @Nullable
  public static String getFromLastExcl (@Nullable final String sStr, @Nullable final String sSearch)
  {
    return _getFromLast (sStr, sSearch, false);
  }

  /**
   * Get the first token up to (and excluding) the separating character.
   *
   * @param sStr
   *        The string to search. May be <code>null</code>.
   * @param cSearch
   *        The search character.
   * @return The passed string if no such separator token was found.
   */
  @Nullable
  public static String getFirstToken (@Nullable final String sStr, final char cSearch)
  {
    final int nIndex = getIndexOf (sStr, cSearch);
    return nIndex == StringHelper.STRING_NOT_FOUND ? sStr : sStr.substring (0, nIndex);
  }

  /**
   * Get the first token up to (and excluding) the separating string.
   *
   * @param sStr
   *        The string to search. May be <code>null</code>.
   * @param sSearch
   *        The search string. May be <code>null</code>.
   * @return The passed string if no such separator token was found.
   */
  @Nullable
  public static String getFirstToken (@Nullable final String sStr, @Nullable final String sSearch)
  {
    if (StringHelper.hasNoText (sSearch))
      return sStr;
    final int nIndex = getIndexOf (sStr, sSearch);
    return nIndex == StringHelper.STRING_NOT_FOUND ? sStr : sStr.substring (0, nIndex);
  }

  /**
   * Get the last token from (and excluding) the separating character.
   *
   * @param sStr
   *        The string to search. May be <code>null</code>.
   * @param cSearch
   *        The search character.
   * @return The passed string if no such separator token was found.
   */
  @Nullable
  public static String getLastToken (@Nullable final String sStr, final char cSearch)
  {
    final int nIndex = getLastIndexOf (sStr, cSearch);
    return nIndex == StringHelper.STRING_NOT_FOUND ? sStr : sStr.substring (nIndex + 1);
  }

  /**
   * Get the last token from (and excluding) the separating string.
   *
   * @param sStr
   *        The string to search. May be <code>null</code>.
   * @param sSearch
   *        The search string. May be <code>null</code>.
   * @return The passed string if no such separator token was found.
   */
  @Nullable
  public static String getLastToken (@Nullable final String sStr, @Nullable final String sSearch)
  {
    if (StringHelper.hasNoText (sSearch))
      return sStr;
    final int nIndex = getLastIndexOf (sStr, sSearch);
    return nIndex == StringHelper.STRING_NOT_FOUND ? sStr : sStr.substring (nIndex + getLength (sSearch));
  }

  @Nullable
  public static String getReverse (@Nullable final String sStr)
  {
    if (sStr == null)
      return null;

    final char [] aChars = sStr.toCharArray ();
    if (aChars.length <= 1)
      return sStr;

    final char [] ret = new char [aChars.length];
    for (int nSrc = aChars.length - 1, nDst = 0; nSrc != -1; nSrc--, nDst++)
      ret[nDst] = aChars[nSrc];
    return new String (ret);
  }

  /**
   * Optimized remove method that removes a set of characters from an input
   * string!
   *
   * @param sInputString
   *        The input string.
   * @param aRemoveChars
   *        The characters to remove. May not be <code>null</code>.
   * @return The version of the string without the passed characters or an empty
   *         String if the input string was <code>null</code>.
   */
  @Nonnull
  public static String removeMultiple (@Nullable final String sInputString, @Nonnull final char [] aRemoveChars)
  {
    ValueEnforcer.notNull (aRemoveChars, "RemoveChars");

    // Any input text?
    if (hasNoText (sInputString))
      return "";

    // Anything to remove?
    if (aRemoveChars.length == 0)
      return sInputString;

    final StringBuilder aSB = new StringBuilder (sInputString.length ());
    iterateChars (sInputString, cInput -> {
      if (!ArrayHelper.contains (aRemoveChars, cInput))
        aSB.append (cInput);
    });
    return aSB.toString ();
  }

  /**
   * Iterate all characters and pass them to the provided consumer.
   *
   * @param sInputString
   *        Input String to use. May be <code>null</code> or empty.
   * @param aConsumer
   *        The consumer to be used. May not be <code>null</code>.
   */
  public static void iterateChars (@Nullable final String sInputString, @Nonnull final ICharConsumer aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");

    if (sInputString != null)
    {
      final char [] aInput = sInputString.toCharArray ();
      for (final char cInput : aInput)
        aConsumer.accept (cInput);
    }
  }

  /**
   * Iterate all code points and pass them to the provided consumer.
   *
   * @param sInputString
   *        Input String to use. May be <code>null</code> or empty.
   * @param aConsumer
   *        The consumer to be used. May not be <code>null</code>.
   */
  public static void iterateCodePoints (@Nullable final String sInputString, @Nonnull final IntConsumer aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");

    if (sInputString != null)
    {
      final int nStringLength = sInputString.length ();
      int nOfs = 0;
      while (nOfs < nStringLength)
      {
        final int nCodePoint = sInputString.codePointAt (nOfs);
        nOfs += Character.charCount (nCodePoint);

        aConsumer.accept (nCodePoint);
      }
    }
  }
}
