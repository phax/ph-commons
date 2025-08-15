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

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.CGlobal;
import com.helger.base.array.ArrayHelper;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.functional.ICharConsumer;
import com.helger.base.functional.ICharPredicate;
import com.helger.base.numeric.MathHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Simple string utility methods, originally in StringHelper.
 *
 * @author Philip Helger
 * @since v12.0.0
 */
@Immutable
public class Strings
{
  @PresentForCodeCoverage
  private static final Strings INSTANCE = new Strings ();

  protected Strings ()
  {}

  /**
   * Check if the string is <code>null</code> or empty.
   *
   * @param aCS
   *        The character sequence to check. May be <code>null</code>.
   * @return <code>true</code> if the string is <code>null</code> or empty, <code>false</code>
   *         otherwise
   * @since 10.1.8
   */
  public static boolean isEmpty (@Nullable final CharSequence aCS)
  {
    return aCS == null || aCS.length () == 0;
  }

  /**
   * Check if the string is <code>null</code> or empty.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is <code>null</code> or empty, <code>false</code>
   *         otherwise
   * @since 10.1.8
   */
  public static boolean isEmpty (@Nullable final String sStr)
  {
    return sStr == null || sStr.isEmpty ();
  }

  /**
   * Check if the string is <code>null</code> or empty after trimming.
   *
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is <code>null</code> or empty or consists only of
   *         whitespaces, <code>false</code> otherwise
   * @since 10.1.8
   */
  public static boolean isEmptyAfterTrim (@Nullable final String s)
  {
    return s == null || s.trim ().isEmpty ();
  }

  /**
   * Check if the string contains any char.
   *
   * @param aCS
   *        The character sequence to check. May be <code>null</code>.
   * @return <code>true</code> if the string contains at least one, <code>false</code> otherwise
   * @since 10.1.8
   */
  public static boolean isNotEmpty (@Nullable final CharSequence aCS)
  {
    return aCS != null && aCS.length () > 0;
  }

  /**
   * Check if the string contains any char.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string contains at least one char, <code>false</code>
   *         otherwise
   * @since 10.1.8
   */
  public static boolean isNotEmpty (@Nullable final String sStr)
  {
    return sStr != null && !sStr.isEmpty ();
  }

  /**
   * Check if the string neither <code>null</code> nor empty after trimming.
   *
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is neither <code>null</code> nor empty nor consists
   *         only of whitespaces, <code>false</code> otherwise
   * @since 10.1.8
   */
  public static boolean isNotEmptyAfterTrim (@Nullable final String s)
  {
    return s != null && !s.trim ().isEmpty ();
  }

  /**
   * Check if the string is <code>null</code> or empty.
   *
   * @param aCS
   *        The character sequence to check. May be <code>null</code>.
   * @return <code>true</code> if the string is <code>null</code> or empty, <code>false</code>
   *         otherwise
   * @deprecated Use {@link #isEmpty(CharSequence)} instead
   */
  @Deprecated (forRemoval = true, since = "12.0.0")
  public static boolean hasNoText (@Nullable final CharSequence aCS)
  {
    return isEmpty (aCS);
  }

  /**
   * Check if the string is <code>null</code> or empty.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is <code>null</code> or empty, <code>false</code>
   *         otherwise
   * @deprecated Use {@link #isEmpty(String)} instead
   */
  @Deprecated (forRemoval = true, since = "12.0.0")
  public static boolean hasNoText (@Nullable final String sStr)
  {
    return isEmpty (sStr);
  }

  /**
   * Check if the string contains any char.
   *
   * @param aCS
   *        The character sequence to check. May be <code>null</code>.
   * @return <code>true</code> if the string contains at least one, <code>false</code> otherwise
   * @deprecated Use {@link #isNotEmpty(CharSequence)} instead
   */
  @Deprecated (forRemoval = true, since = "12.0.0")
  public static boolean hasText (@Nullable final CharSequence aCS)
  {
    return isNotEmpty (aCS);
  }

  /**
   * Check if the string contains any char.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string contains at least one char, <code>false</code>
   *         otherwise
   * @deprecated Use {@link #isNotEmpty(String)} instead
   */
  @Deprecated (forRemoval = true, since = "12.0.0")
  public static boolean hasText (@Nullable final String sStr)
  {
    return isNotEmpty (sStr);
  }

  /**
   * Check if the string neither <code>null</code> nor empty after trimming.
   *
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is neither <code>null</code> nor empty nor consists
   *         only of whitespaces, <code>false</code> otherwise
   * @deprecated Use {@link #isNotEmptyAfterTrim(String)} instead
   */
  @Deprecated (forRemoval = true, since = "12.0.0")
  public static boolean hasTextAfterTrim (@Nullable final String s)
  {
    return isNotEmptyAfterTrim (s);
  }

  /**
   * Get the length of the passed character sequence.
   *
   * @param aCS
   *        The character sequence who's length is to be determined. May be <code>null</code>.
   * @return 0 if the parameter is <code>null</code>, its length otherwise.
   * @see CharSequence#length()
   */
  @Nonnegative
  public static int getLength (@Nullable final CharSequence aCS)
  {
    return aCS == null ? 0 : aCS.length ();
  }

  /**
   * Get the passed string but never return <code>null</code>. If the passed parameter is
   * <code>null</code> an empty string is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code>.
   * @return An empty string if the passed parameter is <code>null</code>, the passed string
   *         otherwise.
   */
  @Nonnull
  public static String getNotNull (@Nullable final String s)
  {
    return getNotNull (s, "");
  }

  /**
   * Get the passed string but never return <code>null</code>. If the passed parameter is
   * <code>null</code> the second parameter is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code>.
   * @param sDefaultIfNull
   *        The value to be used if the first parameter is <code>null</code>. May be
   *        <code>null</code> but in this case the call to this method is obsolete.
   * @return The passed default value if the string is <code>null</code>, otherwise the input
   *         string.
   */
  @Nullable
  public static String getNotNull (@Nullable final String s, @Nullable final String sDefaultIfNull)
  {
    return s == null ? sDefaultIfNull : s;
  }

  /**
   * Get the passed string but never return <code>null</code>. If the passed parameter is
   * <code>null</code> the second parameter is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code>.
   * @param aDefaultIfNull
   *        The value supplier to be used if the first parameter is <code>null</code>. May not be
   *        <code>null</code>.
   * @return The passed default value if the string is <code>null</code>, otherwise the input
   *         string.
   * @since 10.2.0
   */
  @Nullable
  public static String getNotNull (@Nullable final String s, @Nonnull final Supplier <String> aDefaultIfNull)
  {
    return s == null ? aDefaultIfNull.get () : s;
  }

  /**
   * Get the passed {@link CharSequence} but never return <code>null</code>. If the passed parameter
   * is <code>null</code> an empty string is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code>.
   * @return An empty string if the passed parameter is <code>null</code>, the passed
   *         {@link CharSequence} otherwise.
   */
  @Nonnull
  public static CharSequence getNotNull (@Nullable final CharSequence s)
  {
    return getNotNull (s, "");
  }

  /**
   * Get the passed {@link CharSequence} but never return <code>null</code>. If the passed parameter
   * is <code>null</code> the second parameter is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code>.
   * @param sDefaultIfNull
   *        The value to be used if the first parameter is <code>null</code>. May be
   *        <code>null</code> but in this case the call to this method is obsolete.
   * @return The passed default value if the string is <code>null</code>, otherwise the input
   *         {@link CharSequence}.
   */
  @Nullable
  public static CharSequence getNotNull (@Nullable final CharSequence s, @Nullable final CharSequence sDefaultIfNull)
  {
    return s == null ? sDefaultIfNull : s;
  }

  /**
   * Get the passed {@link CharSequence} but never return <code>null</code>. If the passed parameter
   * is <code>null</code> the second parameter is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code>.
   * @param aDefaultIfNull
   *        The value supplier to be used if the first parameter is <code>null</code>. May not be
   *        <code>null</code>.
   * @return The passed default value if the string is <code>null</code>, otherwise the input
   *         {@link CharSequence}.
   * @since 10.2.0
   */
  @Nullable
  public static CharSequence getNotNull (@Nullable final CharSequence s,
                                         @Nonnull final Supplier <? extends CharSequence> aDefaultIfNull)
  {
    return s == null ? aDefaultIfNull.get () : s;
  }

  /**
   * Get the passed string but never return an empty string. If the passed parameter is
   * <code>null</code> or empty the second parameter is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code> nor empty.
   * @param sDefaultIfEmpty
   *        The value to be used if the first parameter is <code>null</code> or empty. May be
   *        <code>null</code> but in this case the call to this method is obsolete.
   * @return The passed default value if the string is <code>null</code> or empty, otherwise the
   *         input string.
   */
  @Nullable
  public static String getNotEmpty (@Nullable final String s, @Nullable final String sDefaultIfEmpty)
  {
    return hasNoText (s) ? sDefaultIfEmpty : s;
  }

  /**
   * Get the passed string but never return an empty string. If the passed parameter is
   * <code>null</code> or empty the second parameter is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code> nor empty.
   * @param aDefaultIfEmpty
   *        The value supplier to be used if the first parameter is <code>null</code> or empty. May
   *        not be <code>null</code>.
   * @return The passed default value if the string is <code>null</code> or empty, otherwise the
   *         input string.
   * @since 10.2.0
   */
  @Nullable
  public static String getNotEmpty (@Nullable final String s, @Nonnull final Supplier <String> aDefaultIfEmpty)
  {
    return hasNoText (s) ? aDefaultIfEmpty.get () : s;
  }

  /**
   * Get the passed char sequence but never return an empty char sequence. If the passed parameter
   * is <code>null</code> or empty the second parameter is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code> nor empty.
   * @param sDefaultIfEmpty
   *        The value to be used if the first parameter is <code>null</code> or empty. May be
   *        <code>null</code> but in this case the call to this method is obsolete.
   * @return The passed default value if the char sequence is <code>null</code> or empty, otherwise
   *         the input char sequence.
   */
  @Nullable
  public static CharSequence getNotEmpty (@Nullable final CharSequence s, @Nullable final CharSequence sDefaultIfEmpty)
  {
    return isEmpty (s) ? sDefaultIfEmpty : s;
  }

  /**
   * Get the passed char sequence but never return an empty char sequence. If the passed parameter
   * is <code>null</code> or empty the second parameter is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code> nor empty.
   * @param aDefaultIfEmpty
   *        The value supplier to be used if the first parameter is <code>null</code> or empty. May
   *        not be <code>null</code>.
   * @return The passed default value if the char sequence is <code>null</code> or empty, otherwise
   *         the input char sequence.
   * @since 10.2.0
   */
  @Nullable
  public static CharSequence getNotEmpty (@Nullable final CharSequence s,
                                          @Nullable final Supplier <? extends CharSequence> aDefaultIfEmpty)
  {
    return isEmpty (s) ? aDefaultIfEmpty.get () : s;
  }

  /**
   * Get the passed string element repeated for a certain number of times. Each string element is
   * simply appended at the end of the string.
   *
   * @param cElement
   *        The character to get repeated.
   * @param nRepeats
   *        The number of repetitions to retrieve. May not be &lt; 0.
   * @return A non-<code>null</code> string containing the string element for the given number of
   *         times.
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
   * Get the passed string element repeated for a certain number of times. Each string element is
   * simply appended at the end of the string.
   *
   * @param sElement
   *        The string to get repeated. May not be <code>null</code>.
   * @param nRepeats
   *        The number of repetitions to retrieve. May not be &lt; 0.
   * @return A non-<code>null</code> string containing the string element for the given number of
   *         times.
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
   * Get the result string with at least the desired length, and fill the lead or trail with the
   * provided char
   *
   * @param sSrc
   *        Source string. May be <code>null</code> or empty.
   * @param nMinLen
   *        The destination minimum length. Should be &gt; 0 to have an impact.
   * @param cGap
   *        The character to use to fill the gap
   * @param bLeading
   *        <code>true</code> to fill at the front (like "00" in "007") or at the end (like "00" in
   *        "700")
   * @return Never <code>null</code>.
   */
  @Nonnull
  private static String _getWithLeadingOrTrailing (@Nullable final String sSrc,
                                                   @Nonnegative final int nMinLen,
                                                   final char cGap,
                                                   final boolean bLeading)
  {
    if (nMinLen <= 0)
    {
      // Requested length is too short - return as is
      return getNotNull (sSrc, "");
    }

    final int nSrcLen = getLength (sSrc);
    if (nSrcLen == 0)
    {
      // Input string is empty
      return getRepeated (cGap, nMinLen);
    }

    final int nCharsToAdd = nMinLen - nSrcLen;
    if (nCharsToAdd <= 0)
    {
      // Input string is already longer than requested minimum length
      return sSrc;
    }

    final StringBuilder aSB = new StringBuilder (nMinLen);
    if (!bLeading)
      aSB.append (sSrc);
    for (int i = 0; i < nCharsToAdd; ++i)
      aSB.append (cGap);
    if (bLeading)
      aSB.append (sSrc);
    return aSB.toString ();
  }

  /**
   * Get a string that is filled at the beginning with the passed character until the minimum length
   * is reached. If the input string is empty, the result is a string with the provided len only
   * consisting of the passed characters. If the input String is longer than the provided length, it
   * is returned unchanged.
   *
   * @param sSrc
   *        Source string. May be <code>null</code>.
   * @param nMinLen
   *        Minimum length. Should be &gt; 0.
   * @param cFront
   *        The character to be used at the beginning
   * @return A non-<code>null</code> string that has at least nLen chars
   */
  @Nonnull
  public static String getWithLeading (@Nullable final String sSrc, @Nonnegative final int nMinLen, final char cFront)
  {
    return _getWithLeadingOrTrailing (sSrc, nMinLen, cFront, true);
  }

  /**
   * Get a string that is filled at the beginning with the passed character until the minimum length
   * is reached. If the input String is longer than the provided length, it is returned unchanged.
   *
   * @param nValue
   *        Source string. May be <code>null</code>.
   * @param nMinLen
   *        Minimum length. Should be &gt; 0.
   * @param cFront
   *        The character to be used at the beginning
   * @return A non-<code>null</code> string that has at least nLen chars
   * @see #getWithLeading(String, int, char)
   */
  @Nonnull
  public static String getWithLeading (final int nValue, @Nonnegative final int nMinLen, final char cFront)
  {
    return _getWithLeadingOrTrailing (Integer.toString (nValue), nMinLen, cFront, true);
  }

  /**
   * Get a string that is filled at the beginning with the passed character until the minimum length
   * is reached. If the input String is longer than the provided length, it is returned unchanged.
   *
   * @param nValue
   *        Source string. May be <code>null</code>.
   * @param nMinLen
   *        Minimum length. Should be &gt; 0.
   * @param cFront
   *        The character to be used at the beginning
   * @return A non-<code>null</code> string that has at least nLen chars
   * @see #getWithLeading(String, int, char)
   */
  @Nonnull
  public static String getWithLeading (final long nValue, @Nonnegative final int nMinLen, final char cFront)
  {
    return _getWithLeadingOrTrailing (Long.toString (nValue), nMinLen, cFront, true);
  }

  /**
   * Get a string that is filled at the end with the passed character until the minimum length is
   * reached. If the input string is empty, the result is a string with the provided len only
   * consisting of the passed characters. If the input String is longer than the provided length, it
   * is returned unchanged.
   *
   * @param sSrc
   *        Source string. May be <code>null</code>.
   * @param nMinLen
   *        Minimum length. Should be &gt; 0.
   * @param cEnd
   *        The character to be used at the end
   * @return A non-<code>null</code> string that has at least nLen chars
   */
  @Nonnull
  public static String getWithTrailing (@Nullable final String sSrc, @Nonnegative final int nMinLen, final char cEnd)
  {
    return _getWithLeadingOrTrailing (sSrc, nMinLen, cEnd, false);
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

  @Nullable
  public static String getReverse (@Nullable final String sStr)
  {
    if (sStr == null)
      return null;

    final char [] aChars = sStr.toCharArray ();
    if (aChars.length <= 1)
      return sStr;

    final char [] ret = new char [aChars.length];
    int nSrc = aChars.length - 1;
    int nDst = 0;
    while (nSrc >= 0)
    {
      ret[nDst] = aChars[nSrc];
      nSrc--;
      nDst++;
    }
    return new String (ret);
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
    return isNotEmpty (aCS) ? aCS.charAt (0) : CGlobal.ILLEGAL_CHAR;
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

  /**
   * Get the first index of sSearch within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @return The first index of sSearch within sText or {@value CGlobal#STRING_NOT_FOUND} if sSearch
   *         was not found or if any parameter was <code>null</code>.
   * @see String#indexOf(String)
   */
  public static int getIndexOf (@Nullable final String sText, @Nullable final String sSearch)
  {
    return sText != null && sSearch != null && sText.length () >= sSearch.length () ? sText.indexOf (sSearch)
                                                                                    : CGlobal.STRING_NOT_FOUND;
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
   * @return The first index of sSearch within sText or {@value CGlobal#STRING_NOT_FOUND} if sSearch
   *         was not found or if any parameter was <code>null</code>.
   * @see String#indexOf(String,int)
   */
  public static int getIndexOf (@Nullable final String sText,
                                @Nonnegative final int nFromIndex,
                                @Nullable final String sSearch)
  {
    return sText != null && sSearch != null && (sText.length () - nFromIndex) >= sSearch.length () ? sText.indexOf (
                                                                                                                    sSearch,
                                                                                                                    nFromIndex)
                                                                                                   : CGlobal.STRING_NOT_FOUND;
  }

  /**
   * Get the last index of sSearch within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @return The last index of sSearch within sText or {@value CGlobal#STRING_NOT_FOUND} if sSearch
   *         was not found or if any parameter was <code>null</code>.
   * @see String#lastIndexOf(String)
   */
  public static int getLastIndexOf (@Nullable final String sText, @Nullable final String sSearch)
  {
    return sText != null && sSearch != null && sText.length () >= sSearch.length () ? sText.lastIndexOf (sSearch)
                                                                                    : CGlobal.STRING_NOT_FOUND;
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
   * @return The last index of sSearch within sText or {@value CGlobal#STRING_NOT_FOUND} if sSearch
   *         was not found or if any parameter was <code>null</code>.
   * @see String#lastIndexOf(String,int)
   */
  public static int getLastIndexOf (@Nullable final String sText,
                                    @Nonnegative final int nFromIndex,
                                    @Nullable final String sSearch)
  {
    return sText != null && sSearch != null && (sText.length () - nFromIndex) >= sSearch.length () ? sText.lastIndexOf (
                                                                                                                        sSearch,
                                                                                                                        nFromIndex)
                                                                                                   : CGlobal.STRING_NOT_FOUND;
  }

  /**
   * Get the first index of cSearch within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param cSearch
   *        The character to search for. May be <code>null</code>.
   * @return The first index of sSearch within sText or {@value CGlobal#STRING_NOT_FOUND} if cSearch
   *         was not found or if any parameter was <code>null</code>.
   * @see String#indexOf(int)
   */
  public static int getIndexOf (@Nullable final String sText, final char cSearch)
  {
    return sText != null && sText.length () >= 1 ? sText.indexOf (cSearch) : CGlobal.STRING_NOT_FOUND;
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
   * @return The first index of sSearch within sText or {@value CGlobal#STRING_NOT_FOUND} if cSearch
   *         was not found or if any parameter was <code>null</code>.
   * @see String#indexOf(int,int)
   */
  public static int getIndexOf (@Nullable final String sText, @Nonnegative final int nFromIndex, final char cSearch)
  {
    return sText != null && (sText.length () - nFromIndex) >= 1 ? sText.indexOf (cSearch, nFromIndex)
                                                                : CGlobal.STRING_NOT_FOUND;
  }

  /**
   * Get the last index of cSearch within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param cSearch
   *        The character to search for. May be <code>null</code>.
   * @return The last index of sSearch within sText or {@value CGlobal#STRING_NOT_FOUND} if cSearch
   *         was not found or if any parameter was <code>null</code>.
   * @see String#lastIndexOf(int)
   */
  public static int getLastIndexOf (@Nullable final String sText, final char cSearch)
  {
    return sText != null && sText.length () >= 1 ? sText.lastIndexOf (cSearch) : CGlobal.STRING_NOT_FOUND;
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
   * @return The last index of sSearch within sText or {@value CGlobal#STRING_NOT_FOUND} if cSearch
   *         was not found or if any parameter was <code>null</code>.
   * @see String#lastIndexOf(int,int)
   */
  public static int getLastIndexOf (@Nullable final String sText, @Nonnegative final int nFromIndex, final char cSearch)
  {
    return sText != null && (sText.length () - nFromIndex) >= 1 ? sText.lastIndexOf (cSearch, nFromIndex)
                                                                : CGlobal.STRING_NOT_FOUND;
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
   * @return The first index of sSearch within sText or {@value CGlobal#STRING_NOT_FOUND} if sSearch
   *         was not found or if any parameter was <code>null</code>.
   * @see String#indexOf(String)
   */
  public static int getIndexOfIgnoreCase (@Nullable final String sText,
                                          @Nullable final String sSearch,
                                          @Nonnull final Locale aSortLocale)
  {
    return sText != null && sSearch != null && sText.length () >= sSearch.length () ? sText.toLowerCase (aSortLocale)
                                                                                           .indexOf (sSearch.toLowerCase (aSortLocale))
                                                                                    : CGlobal.STRING_NOT_FOUND;
  }

  /**
   * Get the first index of sSearch within sText ignoring case starting at index nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The first index of sSearch within sText or {@value CGlobal#STRING_NOT_FOUND} if sSearch
   *         was not found or if any parameter was <code>null</code>.
   * @see String#indexOf(String)
   */
  public static int getIndexOfIgnoreCase (@Nullable final String sText,
                                          @Nonnegative final int nFromIndex,
                                          @Nullable final String sSearch,
                                          @Nonnull final Locale aSortLocale)
  {
    return sText != null && sSearch != null && (sText.length () - nFromIndex) >= sSearch.length () ? sText.toLowerCase (
                                                                                                                        aSortLocale)
                                                                                                          .indexOf (sSearch.toLowerCase (aSortLocale),
                                                                                                                    nFromIndex)
                                                                                                   : CGlobal.STRING_NOT_FOUND;
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
   * @return The last index of sSearch within sText or {@value CGlobal#STRING_NOT_FOUND} if sSearch
   *         was not found or if any parameter was <code>null</code>.
   * @see String#lastIndexOf(String)
   */
  public static int getLastIndexOfIgnoreCase (@Nullable final String sText,
                                              @Nullable final String sSearch,
                                              @Nonnull final Locale aSortLocale)
  {
    return sText != null && sSearch != null && sText.length () >= sSearch.length () ? sText.toLowerCase (aSortLocale)
                                                                                           .lastIndexOf (sSearch.toLowerCase (aSortLocale))
                                                                                    : CGlobal.STRING_NOT_FOUND;
  }

  /**
   * Get the last index of sSearch within sText ignoring case starting at index nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The last index of sSearch within sText or {@value CGlobal#STRING_NOT_FOUND} if sSearch
   *         was not found or if any parameter was <code>null</code>.
   * @see String#lastIndexOf(String)
   */
  public static int getLastIndexOfIgnoreCase (@Nullable final String sText,
                                              @Nonnegative final int nFromIndex,
                                              @Nullable final String sSearch,
                                              @Nonnull final Locale aSortLocale)
  {
    return sText != null && sSearch != null && (sText.length () - nFromIndex) >= sSearch.length () ? sText.toLowerCase (
                                                                                                                        aSortLocale)
                                                                                                          .lastIndexOf (sSearch.toLowerCase (aSortLocale),
                                                                                                                        nFromIndex)
                                                                                                   : CGlobal.STRING_NOT_FOUND;
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
   * @return The first index of sSearch within sText or {@value CGlobal#STRING_NOT_FOUND} if sSearch
   *         was not found or if any parameter was <code>null</code>.
   * @see String#indexOf(int)
   */
  public static int getIndexOfIgnoreCase (@Nullable final String sText,
                                          final char cSearch,
                                          @Nonnull final Locale aSortLocale)
  {
    return sText != null && sText.length () >= 1 ? sText.toLowerCase (aSortLocale)
                                                        .indexOf (Character.toLowerCase (cSearch))
                                                 : CGlobal.STRING_NOT_FOUND;
  }

  /**
   * Get the first index of cSearch within sText ignoring case starting at index nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param cSearch
   *        The char to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The first index of sSearch within sText or {@value CGlobal#STRING_NOT_FOUND} if sSearch
   *         was not found or if any parameter was <code>null</code>.
   * @see String#indexOf(int)
   */
  public static int getIndexOfIgnoreCase (@Nullable final String sText,
                                          @Nonnegative final int nFromIndex,
                                          final char cSearch,
                                          @Nonnull final Locale aSortLocale)
  {
    return sText != null && (sText.length () - nFromIndex) >= 1 ? sText.toLowerCase (aSortLocale)
                                                                       .indexOf (Character.toLowerCase (cSearch),
                                                                                 nFromIndex) : CGlobal.STRING_NOT_FOUND;
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
   * @return The last index of sSearch within sText or {@value CGlobal#STRING_NOT_FOUND} if sSearch
   *         was not found or if any parameter was <code>null</code>.
   * @see String#lastIndexOf(int)
   */
  public static int getLastIndexOfIgnoreCase (@Nullable final String sText,
                                              final char cSearch,
                                              @Nonnull final Locale aSortLocale)
  {
    return sText != null && sText.length () >= 1 ? sText.toLowerCase (aSortLocale)
                                                        .lastIndexOf (Character.toLowerCase (cSearch))
                                                 : CGlobal.STRING_NOT_FOUND;
  }

  /**
   * Get the last index of cSearch within sText ignoring case starting at index nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param cSearch
   *        The char to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The last index of sSearch within sText or {@value CGlobal#STRING_NOT_FOUND} if sSearch
   *         was not found or if any parameter was <code>null</code>.
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
                                                                : CGlobal.STRING_NOT_FOUND;
  }

  /**
   * Check if sSearch is contained within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @return <code>true</code> if sSearch is contained in sText, <code>false</code> otherwise.
   * @see String#contains(CharSequence)
   */
  public static boolean contains (@Nullable final String sText, @Nullable final String sSearch)
  {
    return getIndexOf (sText, sSearch) != CGlobal.STRING_NOT_FOUND;
  }

  /**
   * Check if cSearch is contained within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param cSearch
   *        The character to search for. May be <code>null</code>.
   * @return <code>true</code> if cSearch is contained in sText, <code>false</code> otherwise.
   * @see String#contains(CharSequence)
   */
  public static boolean contains (@Nullable final String sText, final char cSearch)
  {
    return getIndexOf (sText, cSearch) != CGlobal.STRING_NOT_FOUND;
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
   * @return <code>true</code> if sSearch is contained in sText, <code>false</code> otherwise.
   * @see String#contains(CharSequence)
   */
  public static boolean containsIgnoreCase (@Nullable final String sText,
                                            @Nullable final String sSearch,
                                            @Nonnull final Locale aSortLocale)
  {
    return getIndexOfIgnoreCase (sText, sSearch, aSortLocale) != CGlobal.STRING_NOT_FOUND;
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
   * @return <code>true</code> if sSearch is contained in sText, <code>false</code> otherwise.
   * @see String#indexOf(int)
   */
  public static boolean containsIgnoreCase (@Nullable final String sText,
                                            final char cSearch,
                                            @Nonnull final Locale aSortLocale)
  {
    return getIndexOfIgnoreCase (sText, cSearch, aSortLocale) != CGlobal.STRING_NOT_FOUND;
  }

  /**
   * Check if any of the passed searched characters is contained in the input char array.
   *
   * @param aInput
   *        The input char array. May be <code>null</code>.
   * @param aSearchChars
   *        The char array to search. May not be <code>null</code>.
   * @return <code>true</code> if at least any of the search char is contained in the input char
   *         array, <code>false</code> otherwise.
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
   * Check if any of the passed searched characters in contained in the input string.
   *
   * @param sInput
   *        The input string. May be <code>null</code>.
   * @param aSearchChars
   *        The char array to search. May not be <code>null</code>.
   * @return <code>true</code> if at least any of the search char is contained in the input char
   *         array, <code>false</code> otherwise.
   */
  public static boolean containsAny (@Nullable final String sInput, @Nonnull final char [] aSearchChars)
  {
    return sInput != null && containsAny (sInput.toCharArray (), aSearchChars);
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
   * Check if the passed {@link CharSequence} contains any character matching the provided filter.
   *
   * @param aCS
   *        String to check. May be <code>null</code>.
   * @param aFilter
   *        The filter to use. May be <code>null</code>.
   * @return <code>true</code> if the filter is <code>null</code> and the string is not empty.
   *         <code>true</code> if the filter is not <code>null</code> and at least one character of
   *         the string matches the filter. <code>false</code> otherwise.
   * @since 9.1.7
   */
  public static boolean containsAny (@Nullable final CharSequence aCS, @Nullable final ICharPredicate aFilter)
  {
    final int nLen = getLength (aCS);
    if (aFilter == null)
      return nLen > 0;

    if (nLen > 0)
      for (int i = 0; i < nLen; ++i)
        if (aFilter.test (aCS.charAt (i)))
          return true;
    return false;
  }

  /**
   * Check if the passed {@link String} contains any character matching the provided filter.
   *
   * @param sStr
   *        String to check. May be <code>null</code>.
   * @param aFilter
   *        The filter to use. May be <code>null</code>.
   * @return <code>true</code> if the filter is <code>null</code> and the string is not empty.
   *         <code>true</code> if the filter is not <code>null</code> and at least one character of
   *         the string matches the filter. <code>false</code> otherwise.
   * @since 9.1.7
   */
  public static boolean containsAny (@Nullable final String sStr, @Nullable final ICharPredicate aFilter)
  {
    final int nLen = getLength (sStr);
    if (aFilter == null)
      return nLen > 0;

    if (nLen > 0)
      for (final char c : sStr.toCharArray ())
        if (aFilter.test (c))
          return true;
    return false;
  }

  /**
   * Check if the passed {@link CharSequence} contains no character matching the provided filter.
   *
   * @param aCS
   *        String to check. May be <code>null</code>.
   * @param aFilter
   *        The filter to use. May be <code>null</code>.
   * @return <code>true</code> if the filter is <code>null</code> and the string is empty.
   *         <code>true</code> if the filter is not <code>null</code> and no character of the string
   *         matches the filter. <code>false</code> otherwise.
   * @since 9.1.7
   */
  public static boolean containsNone (@Nullable final CharSequence aCS, @Nullable final ICharPredicate aFilter)
  {
    final int nLen = getLength (aCS);
    if (aFilter == null)
      return nLen == 0;

    for (int i = 0; i < nLen; ++i)
      if (aFilter.test (aCS.charAt (i)))
        return false;
    return true;
  }

  /**
   * Check if the passed {@link String} contains no character matching the provided filter.
   *
   * @param sStr
   *        String to check. May be <code>null</code>.
   * @param aFilter
   *        The filter to use. May be <code>null</code>.
   * @return <code>true</code> if the filter is <code>null</code> and the string is empty.
   *         <code>true</code> if the filter is not <code>null</code> and no character of the string
   *         matches the filter. <code>false</code> otherwise.
   * @since 9.1.7
   */
  public static boolean containsNone (@Nullable final String sStr, @Nullable final ICharPredicate aFilter)
  {
    final int nLen = getLength (sStr);
    if (aFilter == null)
      return nLen == 0;

    if (nLen > 0)
      for (final char c : sStr.toCharArray ())
        if (aFilter.test (c))
          return false;
    return true;
  }

  /**
   * Check if the passed {@link CharSequence} contains only characters matching the provided filter.
   *
   * @param aCS
   *        String to check. May be <code>null</code>.
   * @param aFilter
   *        The filter to use. May be <code>null</code>.
   * @return <code>true</code> if the filter is <code>null</code> and the string is not empty.
   *         <code>true</code> if the filter is not <code>null</code> and the string has at least
   *         one character and all characters of the string match the filter. <code>false</code>
   *         otherwise.
   * @since 9.1.7
   */
  public static boolean containsOnly (@Nullable final CharSequence aCS, @Nullable final ICharPredicate aFilter)
  {
    final int nLen = getLength (aCS);
    if (nLen == 0)
      return false;

    if (aFilter == null)
      return true;

    for (int i = 0; i < nLen; ++i)
      if (!aFilter.test (aCS.charAt (i)))
        return false;
    return true;
  }

  /**
   * Check if the passed {@link String} contains only characters matching the provided filter.
   *
   * @param sStr
   *        String to check. May be <code>null</code>.
   * @param aFilter
   *        The filter to use. May be <code>null</code>.
   * @return <code>true</code> if the filter is <code>null</code> and the string is not empty.
   *         <code>true</code> if the filter is not <code>null</code> and the string has at least
   *         one character and all characters of the string match the filter. <code>false</code>
   *         otherwise.
   * @since 9.1.7
   */
  public static boolean containsOnly (@Nullable final String sStr, @Nullable final ICharPredicate aFilter)
  {
    final int nLen = getLength (sStr);
    if (nLen == 0)
      return false;

    if (aFilter == null)
      return true;

    for (final char c : sStr.toCharArray ())
      if (!aFilter.test (c))
        return false;
    return true;
  }

  /**
   * Check if the passed character sequence is only whitespace or not.
   *
   * @param s
   *        The character sequence to be checked. May be <code>null</code>.
   * @return <code>true</code> if the passed sequence is empty or if only whitespace characters are
   *         contained.
   * @see Character#isWhitespace(char)
   */
  public static boolean isAllWhitespace (@Nullable final CharSequence s)
  {
    return containsOnly (s, Character::isWhitespace);
  }

  public static boolean startsWith (@Nullable final CharSequence aCS, final char c)
  {
    return isNotEmpty (aCS) && aCS.charAt (0) == c;
  }

  public static boolean startsWithAny (@Nullable final CharSequence aCS, @Nullable final char [] aChars)
  {
    if (isNotEmpty (aCS) && aChars != null)
      if (ArrayHelper.contains (aChars, aCS.charAt (0)))
        return true;
    return false;
  }

  public static boolean startsWithIgnoreCase (@Nullable final CharSequence aCS, final char c)
  {
    return isNotEmpty (aCS) && Character.toLowerCase (aCS.charAt (0)) == Character.toLowerCase (c);
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

    if (nSearchLength == 1)
      return sStr.charAt (0) == sSearch.charAt (0);

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
    return isNotEmpty (aCS) && Strings.getLastChar (aCS) == c;
  }

  public static boolean endsWithAny (@Nullable final CharSequence aCS, @Nullable final char [] aChars)
  {
    if (isNotEmpty (aCS) && aChars != null)
      if (ArrayHelper.contains (aChars, Strings.getLastChar (aCS)))
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

    if (nSearchLength == 1)
      return aCS.charAt (nCSLength - 1) == aSearch.charAt (0);

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

    if (nSearchLength == 1)
      return sStr.charAt (nStrLength - 1) == sSearch.charAt (0);

    return sStr.startsWith (sSearch, nStrLength - nSearchLength);
  }

  public static boolean endsWithIgnoreCase (@Nullable final CharSequence aCS, final char c)
  {
    return isNotEmpty (aCS) && Character.toLowerCase (Strings.getLastChar (aCS)) == Character.toLowerCase (c);
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
   * Iterate all code points and pass them to the provided consumer. This implementation is
   * approximately 20% quicker than <code>CharSequence.codePoints().forEachOrdered(c)</code>
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

  /**
   * Split the provided string by the provided separator and invoke the consumer for each matched
   * element. The number of returned items is unlimited.
   *
   * @param cSep
   *        The separator to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param aConsumer
   *        The non-<code>null</code> consumer that is invoked for each exploded element
   */
  public static void explode (final char cSep,
                              @Nullable final String sElements,
                              @Nonnull final Consumer <? super String> aConsumer)
  {
    explode (cSep, sElements, -1, aConsumer);
  }

  /**
   * Split the provided string by the provided separator and invoke the consumer for each matched
   * element. The maximum number of elements can be specified.
   *
   * @param cSep
   *        The separator to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le; 0 all items are
   *        used. If max items is 1, than the result string is returned as is. If max items is
   *        larger than the number of elements found, it has no effect.
   * @param aConsumer
   *        The non-<code>null</code> consumer that is invoked for each exploded element
   */
  public static void explode (final char cSep,
                              @Nullable final String sElements,
                              final int nMaxItems,
                              @Nonnull final Consumer <? super String> aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");

    if (nMaxItems == 1)
      aConsumer.accept (sElements);
    else
      if (isNotEmpty (sElements))
      {
        // Do not use RegExPool.stringReplacePattern because of package
        // dependencies
        // Do not use String.split because it trims empty tokens from the end
        int nStartIndex = 0;
        int nMatchIndex;
        int nItemsAdded = 0;
        while ((nMatchIndex = sElements.indexOf (cSep, nStartIndex)) >= 0)
        {
          aConsumer.accept (sElements.substring (nStartIndex, nMatchIndex));
          // 1 == length of separator char
          nStartIndex = nMatchIndex + 1;
          ++nItemsAdded;
          if (nMaxItems > 0 && nItemsAdded == nMaxItems - 1)
          {
            // We have exactly one item the left: the rest of the string
            break;
          }
        }
        aConsumer.accept (sElements.substring (nStartIndex));
      }
  }

  /**
   * Split the provided string by the provided separator and invoke the consumer for each matched
   * element.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param aConsumer
   *        The non-<code>null</code> consumer that is invoked for each exploded element
   */
  public static void explode (@Nonnull final String sSep,
                              @Nullable final String sElements,
                              @Nonnull final Consumer <? super String> aConsumer)
  {
    explode (sSep, sElements, -1, aConsumer);
  }

  /**
   * Split the provided string by the provided separator and invoke the consumer for each matched
   * element. The maximum number of elements can be specified.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le; 0 all items are
   *        used. If max items is 1, than the result string is returned as is. If max items is
   *        larger than the number of elements found, it has no effect.
   * @param aConsumer
   *        The non-<code>null</code> consumer that is invoked for each exploded element
   */
  public static void explode (@Nonnull final String sSep,
                              @Nullable final String sElements,
                              final int nMaxItems,
                              @Nonnull final Consumer <? super String> aConsumer)
  {
    ValueEnforcer.notNull (sSep, "Separator");
    ValueEnforcer.notNull (aConsumer, "Collection");

    if (sSep.length () == 1)
    {
      // If the separator consists only of a single character, use the
      // char-optimized version for better performance
      // Note: do it before the "hasText (sElements)" check, because the same
      // check is performed in the char version as well
      explode (sSep.charAt (0), sElements, nMaxItems, aConsumer);
    }
    else
    {
      if (nMaxItems == 1)
        aConsumer.accept (sElements);
      else
      {
        if (isNotEmpty (sElements))
        {
          // Do not use RegExPool.stringReplacePattern because of package
          // dependencies
          // Do not use String.split because it trims empty tokens from the end
          int nStartIndex = 0;
          int nItemsAdded = 0;
          while (true)
          {
            final int nMatchIndex = sElements.indexOf (sSep, nStartIndex);
            if (nMatchIndex < 0)
              break;
            aConsumer.accept (sElements.substring (nStartIndex, nMatchIndex));
            nStartIndex = nMatchIndex + sSep.length ();
            ++nItemsAdded;
            if (nMaxItems > 0 && nItemsAdded == nMaxItems - 1)
            {
              // We have exactly one item the left: the rest of the string
              break;
            }
          }
          aConsumer.accept (sElements.substring (nStartIndex));
        }
      }
    }
  }
}
