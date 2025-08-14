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
import com.helger.base.CGlobal;
import com.helger.base.array.ArrayHelper;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.functional.ICharConsumer;
import com.helger.base.functional.ICharPredicate;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public final class StringFind
{
  private StringFind ()
  {}

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
    final int nLen = Strings.getLength (aCS);
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
    final int nLen = Strings.getLength (sStr);
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
    final int nLen = Strings.getLength (aCS);
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
    final int nLen = Strings.getLength (sStr);
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
    final int nLen = Strings.getLength (aCS);
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
    final int nLen = Strings.getLength (sStr);
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
}
