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

import java.util.Locale;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.misc.DevelopersNote;
import com.helger.base.CGlobal;
import com.helger.base.math.MathHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Helper class for counting things in Strings
 *
 * @author Philip Helger
 */
@Immutable
public final class StringCount
{
  private static final int [] SIZE_TABLE_INT = { 9,
                                                 99,
                                                 999,
                                                 9999,
                                                 99999,
                                                 999999,
                                                 9999999,
                                                 99999999,
                                                 999999999,
                                                 Integer.MAX_VALUE };
  private static final long [] SIZE_TABLE_LONG = { 9L,
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

  private StringCount ()
  {}

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
   * Get the number of characters the passed value would occupy in a string representation.<br>
   * Copied from java.lang.Integer#StringSize
   *
   * @param nValue
   *        The integer value to check. May be be positive or negative.
   * @return Number of characters required. Always &gt; 0.
   */
  @Nonnegative
  public static int getCharacterCount (final int nValue)
  {
    // index is always one character less than the real size; that's why nPrefix
    // is 1 more!
    final int nPrefix = nValue < 0 ? 2 : 1;
    final int nRealValue = MathHelper.abs (nValue);

    for (int nIndex = 0;; nIndex++)
      if (nRealValue <= SIZE_TABLE_INT[nIndex])
        return nPrefix + nIndex;
  }

  /**
   * Get the number of characters the passed value would occupy in a string representation.
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
      if (nRealValue <= SIZE_TABLE_LONG[nIndex])
        return nPrefix + nIndex;
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
    final int nTextLength = Strings.getLength (sText);
    final int nSearchLength = Strings.getLength (sSearch);
    if (nSearchLength > 0 && nTextLength >= nSearchLength)
    {
      int nLastIndex = 0;
      int nIndex;
      do
      {
        // Start searching from the last result
        nIndex = StringFind.getIndexOf (sText, nLastIndex, sSearch);
        if (nIndex != CGlobal.STRING_NOT_FOUND)
        {
          // Match found
          ++ret;

          // Identify the next starting position (relative index + number of
          // search strings)
          nLastIndex = nIndex + nSearchLength;
        }
      } while (nIndex != CGlobal.STRING_NOT_FOUND);
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
                                                                  sSearch.toLowerCase (aSortLocale)) : 0;
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
  @DevelopersNote ("This is the same as getCharCount")
  public static int getOccurrenceCount (@Nullable final String sText, final char cSearch)
  {
    int ret = 0;
    final int nTextLength = Strings.getLength (sText);
    if (nTextLength >= 1)
    {
      int nLastIndex = 0;
      int nIndex;
      do
      {
        // Start searching from the last result
        nIndex = StringFind.getIndexOf (sText, nLastIndex, cSearch);
        if (nIndex != CGlobal.STRING_NOT_FOUND)
        {
          // Match found
          ++ret;

          // Identify the next starting position (relative index + number of
          // search strings)
          nLastIndex = nIndex + 1;
        }
      } while (nIndex != CGlobal.STRING_NOT_FOUND);
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
}
