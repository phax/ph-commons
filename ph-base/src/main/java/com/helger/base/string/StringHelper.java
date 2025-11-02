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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.CheckReturnValue;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.CGlobal;
import com.helger.base.array.ArrayHelper;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.functional.ICharConsumer;
import com.helger.base.functional.ICharPredicate;
import com.helger.base.numeric.MathHelper;

/**
 * Simple string utility methods, originally in StringHelper.
 *
 * @author Philip Helger
 * @since v12.0.0
 */
@Immutable
public class StringHelper
{
  @PresentForCodeCoverage
  private static final StringHelper INSTANCE = new StringHelper ();

  protected StringHelper ()
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
  @NonNull
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
  public static String getNotNull (@Nullable final String s, @NonNull final Supplier <String> aDefaultIfNull)
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
  @NonNull
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
                                         @NonNull final Supplier <? extends CharSequence> aDefaultIfNull)
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
  public static String getNotEmpty (@Nullable final String s, @NonNull final Supplier <String> aDefaultIfEmpty)
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
  @NonNull
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
  @NonNull
  public static String getRepeated (@NonNull final String sElement, @Nonnegative final int nRepeats)
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
  @NonNull
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
  @NonNull
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
  @NonNull
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
  @NonNull
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
  @NonNull
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

  @NonNull
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

  @NonNull
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

  @NonNull
  public static String getLeadingZero (@NonNull final String sValue, final int nChars)
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
                                          @NonNull final Locale aSortLocale)
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
                                          @NonNull final Locale aSortLocale)
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
                                              @NonNull final Locale aSortLocale)
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
                                              @NonNull final Locale aSortLocale)
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
                                          @NonNull final Locale aSortLocale)
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
                                          @NonNull final Locale aSortLocale)
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
                                              @NonNull final Locale aSortLocale)
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
                                              @NonNull final Locale aSortLocale)
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
                                            @NonNull final Locale aSortLocale)
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
                                            @NonNull final Locale aSortLocale)
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
  public static boolean containsAny (@Nullable final char [] aInput, @NonNull final char [] aSearchChars)
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
  public static boolean containsAny (@Nullable final String sInput, @NonNull final char [] aSearchChars)
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
  public static void iterateChars (@Nullable final String sInputString, @NonNull final ICharConsumer aConsumer)
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
    return isNotEmpty (aCS) && StringHelper.getLastChar (aCS) == c;
  }

  public static boolean endsWithAny (@Nullable final CharSequence aCS, @Nullable final char [] aChars)
  {
    if (isNotEmpty (aCS) && aChars != null)
      if (ArrayHelper.contains (aChars, StringHelper.getLastChar (aCS)))
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
    return isNotEmpty (aCS) && Character.toLowerCase (StringHelper.getLastChar (aCS)) == Character.toLowerCase (c);
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
  public static void iterateCodePoints (@Nullable final String sInputString, @NonNull final IntConsumer aConsumer)
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
                              @NonNull final Consumer <? super String> aConsumer)
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
                              @NonNull final Consumer <? super String> aConsumer)
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
  public static void explode (@NonNull final String sSep,
                              @Nullable final String sElements,
                              @NonNull final Consumer <? super String> aConsumer)
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
  public static void explode (@NonNull final String sSep,
                              @Nullable final String sElements,
                              final int nMaxItems,
                              @NonNull final Consumer <? super String> aConsumer)
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

  /**
   * Take a concatenated String and return the passed String array of all elements in the passed
   * string, using specified separator char.
   *
   * @param cSep
   *        The separator to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le; 0 all items are
   *        used. If max items is 1, than the result string is returned as is. If max items is
   *        larger than the number of elements found, it has no effect.
   * @return The passed collection and never <code>null</code>.
   */
  @NonNull
  public static String [] getExplodedArray (final char cSep,
                                            @Nullable final String sElements,
                                            @CheckForSigned final int nMaxItems)
  {
    if (nMaxItems == 1)
      return new String [] { sElements };
    if (isEmpty (sElements))
      return CGlobal.EMPTY_STRING_ARRAY;

    final int nMaxResultElements = 1 + StringCount.getCharCount (sElements, cSep);
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
    int nItemsAdded = 0;
    while (true)
    {
      final int nMatchIndex = sElements.indexOf (cSep, nStartIndex);
      if (nMatchIndex < 0)
        break;

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
   * Take a concatenated String and return the passed String array of all elements in the passed
   * string, using specified separator char.
   *
   * @param cSep
   *        The separator to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @return The passed collection and never <code>null</code>.
   */
  @NonNull
  public static String [] getExplodedArray (final char cSep, @Nullable final String sElements)
  {
    return getExplodedArray (cSep, sElements, -1);
  }

  /**
   * Take a concatenated String and return the passed Collection of all elements in the passed
   * string, using specified separator string.
   *
   * @param <COLLTYPE>
   *        The collection type to be passed and returned
   * @param cSep
   *        The separator to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le; 0 all items are
   *        used. If max items is 1, than the result string is returned as is. If max items is
   *        larger than the number of elements found, it has no effect.
   * @param aCollection
   *        The non-<code>null</code> target collection that should be filled with the exploded
   *        elements
   * @return The passed collection and never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableObject ("The passed parameter")
  @CodingStyleguideUnaware
  public static <COLLTYPE extends Collection <String>> COLLTYPE getExploded (final char cSep,
                                                                             @Nullable final String sElements,
                                                                             final int nMaxItems,
                                                                             @NonNull final COLLTYPE aCollection)
  {
    explode (cSep, sElements, nMaxItems, aCollection::add);
    return aCollection;
  }

  /**
   * Take a concatenated String and return a {@link List} of all elements in the passed string,
   * using specified separator string.
   *
   * @param cSep
   *        The separator character to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @return The {@link List} represented by the passed string. Never <code>null</code>. If the
   *         passed input string is <code>null</code> or "" an empty list is returned.
   */
  @NonNull
  @ReturnsMutableCopy
  public static List <String> getExploded (final char cSep, @Nullable final String sElements)
  {
    return getExploded (cSep, sElements, -1);
  }

  /**
   * Take a concatenated String and return a {@link List} of all elements in the passed string,
   * using specified separator string.
   *
   * @param cSep
   *        The separator character to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le; 0 all items are
   *        used. If max items is 1, than the result string is returned as is. If max items is
   *        larger than the number of elements found, it has no effect.
   * @return The {@link List} represented by the passed string. Never <code>null</code>. If the
   *         passed input string is <code>null</code> or "" an empty list is returned.
   */
  @NonNull
  @ReturnsMutableCopy
  public static List <String> getExploded (final char cSep, @Nullable final String sElements, final int nMaxItems)
  {
    return getExploded (cSep,
                        sElements,
                        nMaxItems,
                        nMaxItems >= 1 ? new ArrayList <> (nMaxItems) : new ArrayList <> ());
  }

  /**
   * Take a concatenated String and return the passed Collection of all elements in the passed
   * string, using specified separator string.
   *
   * @param <COLLTYPE>
   *        The collection type to be used and returned
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le; 0 all items are
   *        used. If max items is 1, than the result string is returned as is. If max items is
   *        larger than the number of elements found, it has no effect.
   * @param aCollection
   *        The non-<code>null</code> target collection that should be filled with the exploded
   *        elements
   * @return The passed collection and never <code>null</code>.
   */
  @NonNull
  @CodingStyleguideUnaware
  public static <COLLTYPE extends Collection <String>> COLLTYPE getExploded (@NonNull final String sSep,
                                                                             @Nullable final String sElements,
                                                                             final int nMaxItems,
                                                                             @NonNull final COLLTYPE aCollection)
  {
    explode (sSep, sElements, nMaxItems, aCollection::add);
    return aCollection;
  }

  /**
   * Take a concatenated String and return a {@link List} of all elements in the passed string,
   * using specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @return The {@link List} represented by the passed string. Never <code>null</code>. If the
   *         passed input string is <code>null</code> or "" an empty list is returned.
   */
  @NonNull
  @ReturnsMutableCopy
  public static List <String> getExploded (@NonNull final String sSep, @Nullable final String sElements)
  {
    return getExploded (sSep, sElements, -1);
  }

  /**
   * Take a concatenated String and return a {@link List} of all elements in the passed string,
   * using specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le; 0 all items are
   *        used. If max items is 1, than the result string is returned as is. If max items is
   *        larger than the number of elements found, it has no effect.
   * @return The {@link List} represented by the passed string. Never <code>null</code>. If the
   *         passed input string is <code>null</code> or "" an empty list is returned.
   */
  @NonNull
  @ReturnsMutableCopy
  public static List <String> getExploded (@NonNull final String sSep,
                                           @Nullable final String sElements,
                                           final int nMaxItems)
  {
    return getExploded (sSep,
                        sElements,
                        nMaxItems,
                        nMaxItems >= 1 ? new ArrayList <> (nMaxItems) : new ArrayList <> ());
  }

  /**
   * Take a concatenated String and return a {@link Set} of all elements in the passed string, using
   * specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @return The {@link Set} represented by the passed string. Never <code>null</code>. If the
   *         passed input string is <code>null</code> or "" an empty list is returned.
   */
  @NonNull
  @ReturnsMutableCopy
  public static HashSet <String> getExplodedToSet (@NonNull final String sSep, @Nullable final String sElements)
  {
    return getExploded (sSep, sElements, -1, new HashSet <> ());
  }

  /**
   * Take a concatenated String and return an ordered {@link LinkedHashSet} of all elements in the
   * passed string, using specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @return The ordered {@link Set} represented by the passed string. Never <code>null</code>. If
   *         the passed input string is <code>null</code> or "" an empty list is returned.
   */
  @NonNull
  @ReturnsMutableCopy
  public static LinkedHashSet <String> getExplodedToOrderedSet (@NonNull final String sSep,
                                                                @Nullable final String sElements)
  {
    return getExploded (sSep, sElements, -1, new LinkedHashSet <> ());
  }

  /**
   * Take a concatenated String and return a sorted {@link TreeSet} of all elements in the passed
   * string, using specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @return The sorted {@link Set} represented by the passed string. Never <code>null</code>. If
   *         the passed input string is <code>null</code> or "" an empty list is returned.
   */
  @NonNull
  @ReturnsMutableCopy
  public static TreeSet <String> getExplodedToSortedSet (@NonNull final String sSep, @Nullable final String sElements)
  {
    return getExploded (sSep, sElements, -1, new TreeSet <> ());
  }

  /**
   * Concatenate the strings sFront and sEnd. If either front or back is <code>null</code> or empty
   * only the other element is returned. If both strings are <code>null</code> or empty and empty
   * String is returned.
   *
   * @param sFront
   *        Front string. May be <code>null</code>.
   * @param sEnd
   *        May be <code>null</code>.
   * @return The concatenated string. Never <code>null</code>.
   */
  @NonNull
  public static String getConcatenatedOnDemand (@Nullable final String sFront, @Nullable final String sEnd)
  {
    if (sFront == null)
      return sEnd == null ? "" : sEnd;
    if (sEnd == null)
      return sFront;
    return sFront + sEnd;
  }

  /**
   * Concatenate the strings sFront and sEnd by the "sSep" string. If either front or back is
   * <code>null</code> or empty, the separator is not applied.
   *
   * @param sFront
   *        Front string. May be <code>null</code>.
   * @param sSep
   *        Separator string. May be <code>null</code>.
   * @param sEnd
   *        May be <code>null</code>.
   * @return The concatenated string.
   */
  @NonNull
  public static String getConcatenatedOnDemand (@Nullable final String sFront,
                                                @Nullable final String sSep,
                                                @Nullable final String sEnd)
  {
    final StringBuilder aSB = new StringBuilder ();
    if (isNotEmpty (sFront))
    {
      aSB.append (sFront);
      if (isNotEmpty (sSep) && isNotEmpty (sEnd))
        aSB.append (sSep);
    }
    if (isNotEmpty (sEnd))
      aSB.append (sEnd);
    return aSB.toString ();
  }

  /**
   * Concatenate the strings sFront and sEnd by the "cSep" separator. If either front or back is
   * <code>null</code> or empty, the separator is not applied.
   *
   * @param sFront
   *        Front string. May be <code>null</code>.
   * @param cSep
   *        Separator character.
   * @param sEnd
   *        May be <code>null</code>.
   * @return The concatenated string.
   */
  @NonNull
  public static String getConcatenatedOnDemand (@Nullable final String sFront,
                                                final char cSep,
                                                @Nullable final String sEnd)
  {
    final StringBuilder aSB = new StringBuilder ();
    if (isNotEmpty (sFront))
    {
      aSB.append (sFront);
      if (isNotEmpty (sEnd))
        aSB.append (cSep);
    }
    if (isNotEmpty (sEnd))
      aSB.append (sEnd);
    return aSB.toString ();
  }

  /**
   * Get the provided string quoted or unquoted if it is <code>null</code>.
   *
   * @param sSource
   *        Source string. May be <code>null</code>.
   * @return The String <code>"null"</code> if the source is <code>null</code>,
   *         <code>"'" + sSource + "'"</code> otherwise.
   * @since 9.2.0
   */
  @NonNull
  public static String getQuoted (@Nullable final String sSource)
  {
    return sSource == null ? "null" : "'" + sSource + "'";
  }

  /**
   * Append the provided string quoted or unquoted if it is <code>null</code>.
   *
   * @param aTarget
   *        The target to write to. May not be <code>null</code>.
   * @param sSource
   *        Source string. May be <code>null</code>.
   * @see #getQuoted(String)
   * @since 9.2.0
   */
  public static void appendQuoted (@NonNull final StringBuilder aTarget, @Nullable final String sSource)
  {
    if (sSource == null)
      aTarget.append ("null");
    else
      aTarget.append ('\'').append (sSource).append ('\'');
  }

  /**
   * Append the provided string quoted or unquoted if it is <code>null</code>.
   *
   * @param aTarget
   *        The target to write to. May not be <code>null</code>.
   * @param sSource
   *        Source string. May be <code>null</code>.
   * @throws IOException
   *         in case of IO error
   * @see #getQuoted(String)
   * @since 9.2.0
   */
  public static void appendQuoted (@NonNull final Appendable aTarget, @Nullable final String sSource) throws IOException
  {
    if (sSource == null)
      aTarget.append ("null");
    else
      aTarget.append ('\'').append (sSource).append ('\'');
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
    final int n = StringCount.getLeadingWhitespaceCount (s);
    return n == 0 ? s : s.substring (n);
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
    final int n = StringCount.getTrailingWhitespaceCount (s);
    return n == 0 ? s : s.substring (0, s.length () - n);
  }

  /**
   * Trim the passed lead from the source value. If the source value does not start with the passed
   * lead, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param sLead
   *        The string to be trimmed of the beginning
   * @return The trimmed string, or the original input string, if the lead was not found
   * @see #trimEnd(String, String)
   * @see #trimStartAndEnd(String, String)
   * @see #trimStartAndEnd(String, String, String)
   */
  @Nullable
  @CheckReturnValue
  public static String trimStart (@Nullable final String sSrc, @Nullable final String sLead)
  {
    return startsWith (sSrc, sLead) ? sSrc.substring (sLead.length ()) : sSrc;
  }

  @Nullable
  @CheckReturnValue
  public static String trimStartRepeatedly (@Nullable final String sSrc, @Nullable final String sLead)
  {
    if (isEmpty (sSrc))
      return sSrc;

    final int nLeadLength = getLength (sLead);
    if (nLeadLength == 0)
      return sSrc;

    String ret = sSrc;
    while (startsWith (ret, sLead))
      ret = ret.substring (nLeadLength);
    return ret;
  }

  /**
   * Trim the passed lead from the source value. If the source value does not start with the passed
   * lead, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param cLead
   *        The char to be trimmed of the beginning
   * @return The trimmed string, or the original input string, if the lead was not found
   * @see #trimEnd(String, String)
   * @see #trimStartAndEnd(String, String)
   * @see #trimStartAndEnd(String, String, String)
   */
  @Nullable
  @CheckReturnValue
  public static String trimStart (@Nullable final String sSrc, final char cLead)
  {
    return startsWith (sSrc, cLead) ? sSrc.substring (1) : sSrc;
  }

  @Nullable
  @CheckReturnValue
  public static String trimStartRepeatedly (@Nullable final String sSrc, final char cLead)
  {
    if (isEmpty (sSrc))
      return sSrc;

    String ret = sSrc;
    while (startsWith (ret, cLead))
      ret = ret.substring (1);
    return ret;
  }

  /**
   * Trim the passed lead from the source value. If the source value does not start with the passed
   * lead, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param nCount
   *        The number of characters to trim at the end.
   * @return The trimmed string, or an empty string if nCount is &ge; the length of the source
   *         string
   */
  @Nullable
  @CheckReturnValue
  public static String trimStart (@Nullable final String sSrc, @Nonnegative final int nCount)
  {
    if (nCount <= 0)
      return sSrc;
    return getLength (sSrc) <= nCount ? "" : sSrc.substring (nCount);
  }

  /**
   * Trim the passed tail from the source value. If the source value does not end with the passed
   * tail, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param sTail
   *        The string to be trimmed of the end
   * @return The trimmed string, or the original input string, if the tail was not found
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
    if (isEmpty (sSrc))
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
   * Trim the passed tail from the source value. If the source value does not end with the passed
   * tail, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param cTail
   *        The char to be trimmed of the end
   * @return The trimmed string, or the original input string, if the tail was not found
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
    if (isEmpty (sSrc))
      return sSrc;

    String ret = sSrc;
    while (endsWith (ret, cTail))
      ret = ret.substring (0, ret.length () - 1);
    return ret;
  }

  /**
   * Trim the passed tail from the source value. If the source value does not end with the passed
   * tail, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param nCount
   *        The number of characters to trim at the end.
   * @return The trimmed string, or an empty string if nCount is &ge; the length of the source
   *         string
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
   * Trim the passed lead and tail from the source value. If the source value does not start with
   * the passed trimmed value, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param sValueToTrim
   *        The string to be trimmed of the beginning and the end
   * @return The trimmed string, or the original input string, if the value to trim was not found
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
   * Trim the passed lead and tail from the source value. If the source value does not start with
   * the passed lead and does not end with the passed tail, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param sLead
   *        The string to be trimmed of the beginning
   * @param sTail
   *        The string to be trimmed of the end
   * @return The trimmed string, or the original input string, if the lead and the tail were not
   *         found
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
   * Trim the passed lead and tail from the source value. If the source value does not start with
   * the passed trimmed value, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param cValueToTrim
   *        The char to be trimmed of the beginning and the end
   * @return The trimmed string, or the original input string, if the value to trim was not found
   * @see #trimStart(String, String)
   * @see #trimEnd(String, String)
   * @see #trimStartAndEnd(String, String, String)
   */
  @Nullable
  @CheckReturnValue
  public static String trimStartAndEnd (@Nullable final String sSrc, final char cValueToTrim)
  {
    return trimStartAndEnd (sSrc, cValueToTrim, cValueToTrim);
  }

  /**
   * Trim the passed lead and tail from the source value. If the source value does not start with
   * the passed lead and does not end with the passed tail, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param cLead
   *        The char to be trimmed of the beginning
   * @param cTail
   *        The char to be trimmed of the end
   * @return The trimmed string, or the original input string, if the lead and the tail were not
   *         found
   * @see #trimStart(String, char)
   * @see #trimEnd(String, char)
   * @see #trimStartAndEnd(String, char)
   */
  @Nullable
  @CheckReturnValue
  public static String trimStartAndEnd (@Nullable final String sSrc, final char cLead, final char cTail)
  {
    final String sInbetween = trimStart (sSrc, cLead);
    return trimEnd (sInbetween, cTail);
  }

  /**
   * Trim the passed string, if it is not <code>null</code>.
   *
   * @param s
   *        The string to be trimmed. May be <code>null</code>.
   * @return <code>null</code> if the input string was <code>null</code>, the non-<code>null</code>
   *         trimmed string otherwise.
   * @see String#trim()
   */
  @Nullable
  @CheckReturnValue
  public static String trim (@Nullable final String s)
  {
    return isEmpty (s) ? s : s.trim ();
  }

  @NonNull
  public static String getCutAfterLength (@NonNull final String sValue, @Nonnegative final int nMaxLength)
  {
    return getCutAfterLength (sValue, nMaxLength, null);
  }

  @NonNull
  public static String getCutAfterLength (@NonNull final String sValue,
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
   * Convert the passed object to a string using the {@link Object#toString()} method.
   *
   * @param aObject
   *        The value to be converted. May be <code>null</code>.
   * @return An empty string in case the passed object was <code>null</code>. Never
   *         <code>null</code>.
   * @see Object#toString()
   */
  @NonNull
  public static String getToString (@Nullable final Object aObject)
  {
    return getToString (aObject, "");
  }

  /**
   * Convert the passed object to a string using the {@link Object#toString()} method or otherwise
   * return the passed default value.
   *
   * @param aObject
   *        The value to be converted. May be <code>null</code>.
   * @param sNullValue
   *        The value to be returned in case the passed object is <code>null</code>. May be
   *        <code>null</code> itself.
   * @return The passed default value in case the passed object was <code>null</code> or the result
   *         of {@link Object#toString()} on the passed object.
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
   * @return An empty, non-<code>null</code> string if the passed string has a length &le; 1.
   */
  @NonNull
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
   * @return An empty, non-<code>null</code> string if the passed string has a length &le;
   *         <code>nCount</code>.
   */
  @NonNull
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
   * @return An empty, non-<code>null</code> string if the passed string has a length &le; 1.
   */
  @NonNull
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
   * @return An empty, non-<code>null</code> string if the passed string has a length &le;
   *         <code>nCount</code>.
   */
  @NonNull
  public static String getWithoutTrailingChars (@Nullable final String sStr, @Nonnegative final int nCount)
  {
    ValueEnforcer.isGE0 (nCount, "Count");

    if (nCount == 0)
      return sStr;
    final int nLength = getLength (sStr);
    return nLength <= nCount ? "" : sStr.substring (0, nLength - nCount);
  }

  /**
   * Get the passed string where all spaces (white spaces or unicode spaces) have been removed.
   *
   * @param sStr
   *        The source string. May be <code>null</code>
   * @return A non-<code>null</code> string representing the passed string without any spaces
   */
  @NonNull
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
    if (nIndex == CGlobal.STRING_NOT_FOUND)
      return null;
    return sStr.substring (0, nIndex + (bIncludingSearchChar ? 1 : 0));
  }

  /**
   * Get everything from the string up to and including the first passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search character.
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
   * @return <code>null</code> if the passed string does not contain the search character.
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
    if (isEmpty (sSearch))
      return "";

    final int nIndex = getIndexOf (sStr, sSearch);
    if (nIndex == CGlobal.STRING_NOT_FOUND)
      return null;
    return sStr.substring (0, nIndex + (bIncludingSearchChar ? sSearch.length () : 0));
  }

  /**
   * Get everything from the string up to and including the first passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search string. If the
   *         search string is empty, the empty string is returned.
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
   * @return <code>null</code> if the passed string does not contain the search string. If the
   *         search string is empty, the empty string is returned.
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
    if (nIndex == CGlobal.STRING_NOT_FOUND)
      return null;
    return sStr.substring (0, nIndex + (bIncludingSearchChar ? 1 : 0));
  }

  /**
   * Get everything from the string up to and including the first passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search character.
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
   * @return <code>null</code> if the passed string does not contain the search character.
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
    if (isEmpty (sSearch))
      return "";

    final int nIndex = getLastIndexOf (sStr, sSearch);
    if (nIndex == CGlobal.STRING_NOT_FOUND)
      return null;
    return sStr.substring (0, nIndex + (bIncludingSearchChar ? sSearch.length () : 0));
  }

  /**
   * Get everything from the string up to and including the first passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search string. If the
   *         search string is empty, the empty string is returned.
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
   * @return <code>null</code> if the passed string does not contain the search string. If the
   *         search string is empty, the empty string is returned.
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
    if (nIndex == CGlobal.STRING_NOT_FOUND)
      return null;
    return sStr.substring (nIndex + (bIncludingSearchChar ? 0 : 1));
  }

  /**
   * Get everything from the string from and including the first passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search character.
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
   * @return <code>null</code> if the passed string does not contain the search character.
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
    if (isEmpty (sSearch))
      return sStr;

    final int nIndex = getIndexOf (sStr, sSearch);
    if (nIndex == CGlobal.STRING_NOT_FOUND)
      return null;
    return sStr.substring (nIndex + (bIncludingSearchString ? 0 : sSearch.length ()));
  }

  /**
   * Get everything from the string from and including the passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search string. If the
   *         search string is empty, the input string is returned unmodified.
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
   * @return <code>null</code> if the passed string does not contain the search string. If the
   *         search string is empty, the input string is returned unmodified.
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
    if (nIndex == CGlobal.STRING_NOT_FOUND)
      return null;
    return sStr.substring (nIndex + (bIncludingSearchChar ? 0 : 1));
  }

  /**
   * Get everything from the string from and including the first passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search character.
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
   * @return <code>null</code> if the passed string does not contain the search character.
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
    if (isEmpty (sSearch))
      return sStr;

    final int nIndex = getLastIndexOf (sStr, sSearch);
    if (nIndex == CGlobal.STRING_NOT_FOUND)
      return null;
    return sStr.substring (nIndex + (bIncludingSearchString ? 0 : sSearch.length ()));
  }

  /**
   * Get everything from the string from and including the passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search string. If the
   *         search string is empty, the input string is returned unmodified.
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
   * @return <code>null</code> if the passed string does not contain the search string. If the
   *         search string is empty, the input string is returned unmodified.
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
    return nIndex == CGlobal.STRING_NOT_FOUND ? sStr : sStr.substring (0, nIndex);
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
    if (StringHelper.isEmpty (sSearch))
      return sStr;
    final int nIndex = getIndexOf (sStr, sSearch);
    return nIndex == CGlobal.STRING_NOT_FOUND ? sStr : sStr.substring (0, nIndex);
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
    return nIndex == CGlobal.STRING_NOT_FOUND ? sStr : sStr.substring (nIndex + 1);
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
    if (StringHelper.isEmpty (sSearch))
      return sStr;
    final int nIndex = getLastIndexOf (sStr, sSearch);
    return nIndex == CGlobal.STRING_NOT_FOUND ? sStr : sStr.substring (nIndex + getLength (sSearch));
  }
}
