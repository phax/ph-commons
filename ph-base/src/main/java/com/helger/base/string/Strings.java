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
import java.util.function.Supplier;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.equals.ValueEnforcer;
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
}
