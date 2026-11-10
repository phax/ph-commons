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
package com.helger.base.compare;

import java.text.Collator;
import java.util.Comparator;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.equals.EqualsHelper;

/**
 * A small helper class that provides comparison methods that are <code>null</code> safe. Also
 * provides secure ways to compare double and float values.
 *
 * @author Philip Helger
 */
@Immutable
public class CompareHelper
{
  /** By default <code>null</code> values come first */
  public static final boolean DEFAULT_NULL_VALUES_COME_FIRST = true;

  @PresentForCodeCoverage
  private static final CompareHelper INSTANCE = new CompareHelper ();

  protected CompareHelper ()
  {}

  /**
   * Special version for boolean, <code>true</code> before <code>false</code>
   *
   * @param b1
   *        First value
   * @param b2
   *        Second value
   * @return -1, 0 or + 1
   */
  public static int compareTrueBeforeFalse (final boolean b1, final boolean b2)
  {
    if (b1 == b2)
      return 0;
    return b1 ? -1 : +1;
  }

  /**
   * Special version for boolean, <code>false</code> before <code>true</code>
   *
   * @param b1
   *        First value
   * @param b2
   *        Second value
   * @return -1, 0 or + 1
   */
  public static int compareFalseBeforeTrue (final boolean b1, final boolean b2)
  {
    if (b1 == b2)
      return 0;
    return b1 ? +1 : -1;
  }

  /**
   * Special version for byte
   *
   * @param n1
   *        First value
   * @param n2
   *        Second value
   * @return value &lt;0, 0 or &gt;0.
   */
  public static int compare (final byte n1, final byte n2)
  {
    return Byte.compare (n1, n2);
  }

  /**
   * Special version for byte
   *
   * @param n1
   *        First value
   * @param n2
   *        Second value
   * @return value &lt;0, 0 or &gt;0.
   */
  public static int compare (final char n1, final char n2)
  {
    return Character.compare (n1, n2);
  }

  /**
   * Special version for doubles
   *
   * @param d1
   *        First value
   * @param d2
   *        Second value
   * @return value &lt;0, 0 or &gt;0.
   */
  public static int compare (final double d1, final double d2)
  {
    return Double.compare (d1, d2);
  }

  /**
   * Special version for floats
   *
   * @param f1
   *        First value
   * @param f2
   *        Second value
   * @return value &lt;0, 0 or &gt;0.
   */
  public static int compare (final float f1, final float f2)
  {
    return Float.compare (f1, f2);
  }

  /**
   * Special version for int
   *
   * @param n1
   *        First value
   * @param n2
   *        Second value
   * @return value &lt;0, 0 or &gt;0.
   */
  public static int compare (final int n1, final int n2)
  {
    return Integer.compare (n1, n2);
  }

  /**
   * Special version for long
   *
   * @param n1
   *        First value
   * @param n2
   *        Second value
   * @return value &lt;0, 0 or &gt;0.
   */
  public static int compare (final long n1, final long n2)
  {
    return Long.compare (n1, n2);
  }

  /**
   * Special version for short
   *
   * @param n1
   *        First value
   * @param n2
   *        Second value
   * @return value &lt;0, 0 or &gt;0.
   */
  public static int compare (final short n1, final short n2)
  {
    return Short.compare (n1, n2);
  }

  /**
   * Compare the passed items and handle <code>null</code> values correctly. A <code>null</code>
   * value is always smaller than a non-<code>null</code> value.
   *
   * @param <DATATYPE>
   *        Any comparable object to be used. Both need to be of the same type.
   * @param aObj1
   *        First object to compare. May be <code>null</code>.
   * @param aObj2
   *        Second object to compare. May be <code>null</code>.
   * @return 0 if they are equal (or both <code>null</code>), -1 or +1.
   */
  public static <DATATYPE extends Comparable <? super DATATYPE>> int compare (@Nullable final DATATYPE aObj1,
                                                                              @Nullable final DATATYPE aObj2)
  {
    // Legacy behavior: null values come first
    return compare (aObj1, aObj2, DEFAULT_NULL_VALUES_COME_FIRST);
  }

  /**
   * Compare the passed items and handle <code>null</code> values correctly. A <code>null</code>
   * value is always smaller than a non-<code>null</code> value.
   *
   * @param <DATATYPE>
   *        Any comparable object to be used. Both need to be of the same type.
   * @param aObj1
   *        First object to compare. May be <code>null</code>.
   * @param aObj2
   *        Second object to compare. May be <code>null</code>.
   * @param bNullValuesComeFirst
   *        if <code>true</code> <code>null</code> values are ordered before non-<code>null</code>
   *        values
   * @return 0 if they are equal (or both <code>null</code>), -1 or +1.
   */
  public static <DATATYPE extends Comparable <? super DATATYPE>> int compare (@Nullable final DATATYPE aObj1,
                                                                              @Nullable final DATATYPE aObj2,
                                                                              final boolean bNullValuesComeFirst)
  {
    if (EqualsHelper.identityEqual (aObj1, aObj2))
      return 0;
    if (aObj1 == null)
      return bNullValuesComeFirst ? -1 : +1;
    if (aObj2 == null)
      return bNullValuesComeFirst ? +1 : -1;
    return aObj1.compareTo (aObj2);
  }

  /**
   * Compare the passed items and handle <code>null</code> values correctly. A <code>null</code>
   * value is always smaller than a non-<code>null</code> value.
   *
   * @param <DATATYPE>
   *        Any object to be used. Both need to be of the same type.
   * @param aObj1
   *        First object to compare. May be <code>null</code>.
   * @param aObj2
   *        Second object to compare. May be <code>null</code>.
   * @param aComp
   *        The comparator to be used if both parameters are not <code>null</code>. The comparator
   *        itself may not be <code>null</code>.
   * @return 0 if they are equal (or both <code>null</code>), -1 or +1.
   */
  public static <DATATYPE> int compare (@Nullable final DATATYPE aObj1,
                                        @Nullable final DATATYPE aObj2,
                                        @NonNull final Comparator <? super DATATYPE> aComp)
  {
    // Legacy behavior: null values come first
    return compare (aObj1, aObj2, aComp, DEFAULT_NULL_VALUES_COME_FIRST);
  }

  /**
   * Compare the passed items and handle <code>null</code> values correctly. A <code>null</code>
   * value is always smaller than a non-<code>null</code> value.
   *
   * @param <DATATYPE>
   *        Any object to be used. Both need to be of the same type.
   * @param aObj1
   *        First object to compare. May be <code>null</code>.
   * @param aObj2
   *        Second object to compare. May be <code>null</code>.
   * @param aComp
   *        The comparator to be used if both parameters are not <code>null</code>. The comparator
   *        itself may not be <code>null</code>.
   * @param bNullValuesComeFirst
   *        if <code>true</code> <code>null</code> values are ordered before non-<code>null</code>
   *        values
   * @return 0 if they are equal (or both <code>null</code>), -1 or +1.
   */
  public static <DATATYPE> int compare (@Nullable final DATATYPE aObj1,
                                        @Nullable final DATATYPE aObj2,
                                        @NonNull final Comparator <? super DATATYPE> aComp,
                                        final boolean bNullValuesComeFirst)
  {
    if (EqualsHelper.identityEqual (aObj1, aObj2))
      return 0;
    if (aObj1 == null)
      return bNullValuesComeFirst ? -1 : +1;
    if (aObj2 == null)
      return bNullValuesComeFirst ? +1 : -1;
    return aComp.compare (aObj1, aObj2);
  }

  /**
   * Compare the passed strings using the specified {@link Collator} and handle <code>null</code>
   * values correctly. A <code>null</code> value is always smaller than a non-<code>null</code>
   * value.
   *
   * @param sStr1
   *        First string to compare. May be <code>null</code>.
   * @param sStr2
   *        Second string to compare. May be <code>null</code>.
   * @param aCollator
   *        The collator to use for comparison. May not be <code>null</code>.
   * @return 0 if they are equal (or both <code>null</code>), -1 or +1.
   */
  public static int compare (@Nullable final String sStr1,
                             @Nullable final String sStr2,
                             @NonNull final Collator aCollator)
  {
    // Legacy behavior: null values come first
    return compare (sStr1, sStr2, aCollator, DEFAULT_NULL_VALUES_COME_FIRST);
  }

  /**
   * Compare the passed strings using the specified {@link Collator} and handle <code>null</code>
   * values correctly.
   *
   * @param sStr1
   *        First string to compare. May be <code>null</code>.
   * @param sStr2
   *        Second string to compare. May be <code>null</code>.
   * @param aCollator
   *        The collator to use for comparison. May not be <code>null</code>.
   * @param bNullValuesComeFirst
   *        if <code>true</code> <code>null</code> values are ordered before non-<code>null</code>
   *        values
   * @return 0 if they are equal (or both <code>null</code>), -1 or +1.
   */
  public static int compare (@Nullable final String sStr1,
                             @Nullable final String sStr2,
                             @NonNull final Collator aCollator,
                             final boolean bNullValuesComeFirst)
  {
    if (EqualsHelper.identityEqual (sStr1, sStr2))
      return 0;
    if (sStr1 == null)
      return bNullValuesComeFirst ? -1 : +1;
    if (sStr2 == null)
      return bNullValuesComeFirst ? +1 : -1;
    return aCollator.compare (sStr1, sStr2);
  }

  /**
   * Compare the passed strings case insensitive and handle <code>null</code> values correctly. A
   * <code>null</code> value is always smaller than a non-<code>null</code> value.
   *
   * @param sStr1
   *        First string to compare. May be <code>null</code>.
   * @param sStr2
   *        Second string to compare. May be <code>null</code>.
   * @return 0 if they are equal (or both <code>null</code>), -1 or +1.
   */
  public static int compareIgnoreCase (@Nullable final String sStr1, @Nullable final String sStr2)
  {
    // Legacy behavior: null values come first
    return compareIgnoreCase (sStr1, sStr2, DEFAULT_NULL_VALUES_COME_FIRST);
  }

  /**
   * Compare the passed strings case insensitive and handle <code>null</code> values correctly.
   *
   * @param sStr1
   *        First string to compare. May be <code>null</code>.
   * @param sStr2
   *        Second string to compare. May be <code>null</code>.
   * @param bNullValuesComeFirst
   *        if <code>true</code> <code>null</code> values are ordered before non-<code>null</code>
   *        values
   * @return 0 if they are equal (or both <code>null</code>), -1 or +1.
   */
  public static int compareIgnoreCase (@Nullable final String sStr1,
                                       @Nullable final String sStr2,
                                       final boolean bNullValuesComeFirst)
  {
    if (EqualsHelper.identityEqual (sStr1, sStr2))
      return 0;
    if (sStr1 == null)
      return bNullValuesComeFirst ? -1 : +1;
    if (sStr2 == null)
      return bNullValuesComeFirst ? +1 : -1;
    return sStr1.compareToIgnoreCase (sStr2);
  }
  /**
   * Get the code point that contains the UTF-16 unit at the passed index, where the index is the
   * first index at which two strings differ. If the char at the index is a low surrogate that forms
   * a pair with the preceding high surrogate, the supplementary code point of that pair is returned.
   * If the preceding char is a high surrogate that is not followed by a low surrogate, that lone
   * high surrogate is returned, because it is the code point at which the sequences differ.
   */
  private static int _getCodePointAtDifference (@NonNull final String sStr,
                                                final int nIndex,
                                                final boolean bPrevIsHighSurrogate,
                                                final boolean bOtherIsLowSurrogate)
  {
    final char c = sStr.charAt (nIndex);
    if (bPrevIsHighSurrogate)
    {
      final char cPrev = sStr.charAt (nIndex - 1);
      if (Character.isLowSurrogate (c))
        return Character.toCodePoint (cPrev, c);
      if (bOtherIsLowSurrogate)
      {
        // The other string forms a pair at nIndex-1, this one has a lone high surrogate there
        return cPrev;
      }
    }
    return sStr.codePointAt (nIndex);
  }

  /**
   * Compare the passed strings by Unicode code point and handle <code>null</code> values correctly.
   * A <code>null</code> value is always smaller than a non-<code>null</code> value. See
   * {@link #compareByCodePoint(String, String, boolean)} for details.
   *
   * @param sStr1
   *        First string to compare. May be <code>null</code>.
   * @param sStr2
   *        Second string to compare. May be <code>null</code>.
   * @return 0 if they are equal (or both <code>null</code>), a value &lt; 0 if the first string is
   *         smaller, a value &gt; 0 if the first string is larger.
   * @since 12.5.4
   */
  public static int compareByCodePoint (@Nullable final String sStr1, @Nullable final String sStr2)
  {
    return compareByCodePoint (sStr1, sStr2, DEFAULT_NULL_VALUES_COME_FIRST);
  }

  /**
   * Compare the passed strings by Unicode code point and handle <code>null</code> values correctly.
   * The result has the same sign as comparing the sequences of Unicode code points of both strings
   * lexicographically (with a proper prefix being smaller). This differs from
   * {@link String#compareTo(String)}, which compares UTF-16 code units and therefore orders
   * supplementary characters (U+10000 and above) before the characters U+E000 to U+FFFF.
   * Unpaired surrogates are treated as code points of their own, like
   * {@link String#codePoints()} does.<br>
   * The strings are not decoded into code points: the UTF-16 units are compared directly and only
   * the first differing position is examined more closely, if both units are &ge; U+D800.<br>
   * Only the sign of the result is meaningful, the absolute value is unspecified.
   *
   * @param sStr1
   *        First string to compare. May be <code>null</code>.
   * @param sStr2
   *        Second string to compare. May be <code>null</code>.
   * @param bNullValuesComeFirst
   *        if <code>true</code> <code>null</code> values are ordered before non-<code>null</code>
   *        values
   * @return 0 if they are equal (or both <code>null</code>), a value &lt; 0 if the first string is
   *         smaller, a value &gt; 0 if the first string is larger.
   * @since 12.5.4
   */
  public static int compareByCodePoint (@Nullable final String sStr1,
                                        @Nullable final String sStr2,
                                        final boolean bNullValuesComeFirst)
  {
    if (EqualsHelper.identityEqual (sStr1, sStr2))
      return 0;
    if (sStr1 == null)
      return bNullValuesComeFirst ? -1 : +1;
    if (sStr2 == null)
      return bNullValuesComeFirst ? +1 : -1;

    final int nLen1 = sStr1.length ();
    final int nLen2 = sStr2.length ();
    final int nMinLen = Math.min (nLen1, nLen2);
    for (int i = 0; i < nMinLen; ++i)
    {
      final char c1 = sStr1.charAt (i);
      final char c2 = sStr2.charAt (i);
      if (c1 != c2)
      {
        if (c1 < Character.MIN_SURROGATE || c2 < Character.MIN_SURROGATE)
        {
          // At least one side is a BMP character below the surrogate range, so the UTF-16 order
          // equals the code point order
          return c1 - c2;
        }
        if (c1 > Character.MAX_SURROGATE && c2 > Character.MAX_SURROGATE)
        {
          // Both are BMP characters above the surrogate range
          return c1 - c2;
        }
        // At least one surrogate is involved - determine the real code points
        final boolean bPrevIsHighSurrogate = i > 0 && Character.isHighSurrogate (sStr1.charAt (i - 1));
        final int nCP1 = _getCodePointAtDifference (sStr1,
                                                    i,
                                                    bPrevIsHighSurrogate,
                                                    Character.isLowSurrogate (c2));
        final int nCP2 = _getCodePointAtDifference (sStr2,
                                                    i,
                                                    bPrevIsHighSurrogate,
                                                    Character.isLowSurrogate (c1));
        return Integer.compare (nCP1, nCP2);
      }
    }
    return nLen1 - nLen2;
  }
}
