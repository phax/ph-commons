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
package com.helger.commons.regex;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.RegEx;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.string.StringHelper;

/**
 * This class offers helper methods that work on cached regular expression
 * pattern as offered by {@link RegExCache}.
 *
 * @author Philip Helger
 */
@Immutable
public final class RegExHelper
{
  @PresentForCodeCoverage
  private static final RegExHelper s_aInstance = new RegExHelper ();

  private RegExHelper ()
  {}

  /**
   * Split the passed text with the given regular expression. If you do not need
   * a regular expression, {@link StringHelper#getExploded(String, String)} is a
   * faster option.
   *
   * @param sText
   *        The text to be split. May be <code>null</code>.
   * @param sRegEx
   *        The regular expression to use for splitting. May neither be
   *        <code>null</code> nor empty.
   * @return An empty array if the text is <code>null</code>, a non-
   *         <code>null</code> array otherwise. If both text and regular
   *         expression are <code>null</code> an empty array is returned as well
   *         since the text parameter is checked first.
   */
  @Nonnull
  public static String [] getSplitToArray (@Nullable final CharSequence sText, @Nonnull @RegEx final String sRegEx)
  {
    if (sText == null)
      return ArrayHelper.EMPTY_STRING_ARRAY;
    return RegExCache.getPattern (sRegEx).split (sText);
  }

  /**
   * Split the passed text with the given regular expression returning at most
   * the given number of tokens. If you do not need a regular expression,
   * {@link StringHelper#getExploded(String, String, int)} is a faster option.
   *
   * @param sText
   *        The text to be split. May be <code>null</code>.
   * @param sRegEx
   *        The regular expression to use for splitting. May neither be
   *        <code>null</code> nor empty.
   * @param nLimit
   *        The maximum number of tokens to return if the value is &gt; 0. If
   *        the value is &le; 0 it has no effect and all tokens are returned.
   * @return An empty array if the text is <code>null</code>, a non-
   *         <code>null</code> array otherwise. If both text and regular
   *         expression are <code>null</code> an empty array is returned as well
   *         since the text parameter is checked first.
   */
  @Nonnull
  public static String [] getSplitToArray (@Nullable final CharSequence sText,
                                           @Nonnull @RegEx final String sRegEx,
                                           @Nonnegative final int nLimit)
  {
    ValueEnforcer.notNull (sRegEx, "RegEx");
    if (sText == null)
      return ArrayHelper.EMPTY_STRING_ARRAY;
    return RegExCache.getPattern (sRegEx).split (sText, nLimit);
  }

  /**
   * Split the passed text with the given regular expression. If you do not need
   * a regular expression, {@link StringHelper#getExploded(String, String)} is a
   * faster option.
   *
   * @param sText
   *        The text to be split. May be <code>null</code>.
   * @param sRegEx
   *        The regular expression to use for splitting. May neither be
   *        <code>null</code> nor empty.
   * @return An empty list if the text is <code>null</code>, a non-
   *         <code>null</code> list otherwise. If both text and regular
   *         expression are <code>null</code> an empty list is returned as well
   *         since the text parameter is checked first.
   */
  @Nonnull
  public static List <String> getSplitToList (@Nullable final CharSequence sText, @Nonnull @RegEx final String sRegEx)
  {
    return CollectionHelper.newList (getSplitToArray (sText, sRegEx));
  }

  /**
   * Split the passed text with the given regular expression. If you do not need
   * a regular expression, {@link StringHelper#getExploded(String, String, int)}
   * is a faster option.
   *
   * @param sText
   *        The text to be split. May be <code>null</code>.
   * @param sRegEx
   *        The regular expression to use for splitting. May neither be
   *        <code>null</code> nor empty.
   * @param nLimit
   *        The maximum number of tokens to return if the value is &gt; 0. If
   *        the value is &le; 0 it has no effect and all tokens are returned.
   * @return An empty list if the text is <code>null</code>, a non-
   *         <code>null</code> list otherwise. If both text and regular
   *         expression are <code>null</code> an empty list is returned as well
   *         since the text parameter is checked first.
   */
  @Nonnull
  public static List <String> getSplitToList (@Nullable final CharSequence sText,
                                              @Nonnull @RegEx final String sRegEx,
                                              @Nonnegative final int nLimit)
  {
    return CollectionHelper.newList (getSplitToArray (sText, sRegEx, nLimit));
  }

  /**
   * Get the Java Matcher object for the passed pair of regular expression and
   * value.
   *
   * @param sRegEx
   *        The regular expression to use. May neither be <code>null</code> nor
   *        empty.
   * @param sValue
   *        The value to create the matcher for. May not be <code>null</code>.
   * @return A non-<code>null</code> matcher.
   */
  @Nonnull
  public static Matcher getMatcher (@Nonnull @RegEx final String sRegEx, @Nonnull final String sValue)
  {
    ValueEnforcer.notNull (sValue, "Value");

    return RegExCache.getPattern (sRegEx).matcher (sValue);
  }

  /**
   * Get the Java Matcher object for the passed pair of regular expression and
   * value.
   *
   * @param sRegEx
   *        The regular expression to use. May neither be <code>null</code> nor
   *        empty.
   * @param nOptions
   *        The pattern compilations options to be used.
   * @param sValue
   *        The value to create the matcher for. May not be <code>null</code>.
   * @return A non-<code>null</code> matcher.
   * @see Pattern#compile(String, int)
   */
  @Nonnull
  public static Matcher getMatcher (@Nonnull @RegEx final String sRegEx,
                                    @Nonnegative final int nOptions,
                                    @Nonnull final String sValue)
  {
    ValueEnforcer.notNull (sValue, "Value");

    return RegExCache.getPattern (sRegEx, nOptions).matcher (sValue);
  }

  /**
   * A shortcut helper method to determine whether a string matches a certain
   * regular expression or not.
   *
   * @param sRegEx
   *        The regular expression to be used. The compiled regular expression
   *        pattern is cached. May neither be <code>null</code> nor empty.
   * @param sValue
   *        The string value to compare against the regular expression.
   * @return <code>true</code> if the string matches the regular expression,
   *         <code>false</code> otherwise.
   */
  public static boolean stringMatchesPattern (@Nonnull @RegEx final String sRegEx, @Nonnull final String sValue)
  {
    return getMatcher (sRegEx, sValue).matches ();
  }

  /**
   * A shortcut helper method to determine whether a string matches a certain
   * regular expression or not.
   *
   * @param sRegEx
   *        The regular expression to be used. The compiled regular expression
   *        pattern is cached. May neither be <code>null</code> nor empty.
   * @param nOptions
   *        The pattern compilations options to be used.
   * @param sValue
   *        The string value to compare against the regular expression.
   * @return <code>true</code> if the string matches the regular expression,
   *         <code>false</code> otherwise.
   * @see Pattern#compile(String, int)
   */
  public static boolean stringMatchesPattern (@Nonnull @RegEx final String sRegEx,
                                              @Nonnegative final int nOptions,
                                              @Nonnull final String sValue)
  {
    return getMatcher (sRegEx, nOptions, sValue).matches ();
  }

  @Nonnull
  public static String stringReplacePattern (@Nonnull @RegEx final String sRegEx,
                                             @Nonnull final String sValue,
                                             @Nullable final String sReplacement)
  {
    // Avoid NPE on invalid replacement parameter
    return getMatcher (sRegEx, sValue).replaceAll (StringHelper.getNotNull (sReplacement));
  }

  @Nonnull
  public static String stringReplacePattern (@Nonnull @RegEx final String sRegEx,
                                             @Nonnegative final int nOptions,
                                             @Nonnull final String sValue,
                                             @Nullable final String sReplacement)
  {
    // Avoid NPE on invalid replacement parameter
    return getMatcher (sRegEx, nOptions, sValue).replaceAll (StringHelper.getNotNull (sReplacement));
  }

  /**
   * Convert an identifier to a programming language identifier by replacing all
   * non-word characters with an underscore ("_").
   *
   * @param s
   *        The string to convert. May be <code>null</code> or empty.
   * @return The converted string or <code>null</code> if the input string is
   *         <code>null</code>.
   */
  @Nullable
  public static String getAsIdentifier (@Nullable final String s)
  {
    return getAsIdentifier (s, "_");
  }

  /**
   * Convert an identifier to a programming language identifier by replacing all
   * non-word characters with an underscore.
   *
   * @param s
   *        The string to convert. May be <code>null</code> or empty.
   * @param cReplacement
   *        The replacement character to be used for all non-identifier
   *        characters
   * @return The converted string or <code>null</code> if the input string is
   *         <code>null</code>.
   */
  @Nullable
  public static String getAsIdentifier (@Nullable final String s, final char cReplacement)
  {
    if (StringHelper.hasNoText (s))
      return s;

    String sReplacement;
    if (cReplacement == '$' || cReplacement == '\\')
    {
      // These 2 chars must be quoted, otherwise an
      // StringIndexOutOfBoundsException occurs!
      sReplacement = "\\" + cReplacement;
    }
    else
      sReplacement = Character.toString (cReplacement);

    // replace all non-word characters with the replacement character
    // Important: quote the replacement in case it is a backslash or another
    // special regex character
    final String ret = stringReplacePattern ("\\W", s, sReplacement);
    if (!Character.isJavaIdentifierStart (ret.charAt (0)))
      return sReplacement + ret;
    return ret;
  }

  /**
   * Convert an identifier to a programming language identifier by replacing all
   * non-word characters with an underscore.
   *
   * @param s
   *        The string to convert. May be <code>null</code> or empty.
   * @param sReplacement
   *        The replacement string to be used for all non-identifier characters.
   *        May be empty but not be <code>null</code>.
   * @return The converted string or <code>null</code> if the input string is
   *         <code>null</code>. Maybe an invalid identifier, if the replacement
   *         is empty, and the identifier starts with an illegal character, or
   *         if the replacement is empty and the source string only contains
   *         invalid characters!
   */
  @Nullable
  public static String getAsIdentifier (@Nullable final String s, @Nonnull final String sReplacement)
  {
    ValueEnforcer.notNull (sReplacement, "Replacement");

    if (StringHelper.hasNoText (s))
      return s;

    // replace all non-word characters with the replacement character
    // Important: replacement does not need to be quoted, because it is not
    // treated as a regular expression!
    final String ret = stringReplacePattern ("\\W", s, sReplacement);
    if (ret.length () == 0)
      return sReplacement;
    if (!Character.isJavaIdentifierStart (ret.charAt (0)))
      return sReplacement + ret;
    return ret;
  }

  /**
   * Check if the passed regular expression is invalid.<br>
   * Note: this method may be a performance killer, as it calls
   * {@link Pattern#compile(String)} each time, which is CPU intensive and has a
   * synchronization penalty.
   *
   * @param sRegEx
   *        The regular expression to validate. May not be <code>null</code>.
   * @return <code>true</code> if the pattern is valid, <code>false</code>
   *         otherwise.
   */
  public static boolean isValidPattern (@Nonnull @RegEx final String sRegEx)
  {
    try
    {
      Pattern.compile (sRegEx);
      return true;
    }
    catch (final PatternSyntaxException ex)
    {
      return false;
    }
  }

  /**
   * Get the values of all groups (RegEx <code>(...)</code>) for the passed
   * value.<br>
   * Note: groups starting with "?:" are non-capturing groups (e.g.
   * <code>(?:a|b)</code>)
   *
   * @param sRegEx
   *        The regular expression containing the groups
   * @param sValue
   *        The value to check
   * @return <code>null</code> if the passed value does not match the regular
   *         expression. An empty array if the regular expression contains no
   *         capturing group.
   */
  @Nullable
  public static String [] getAllMatchingGroupValues (@Nonnull @RegEx final String sRegEx, @Nonnull final String sValue)
  {
    final Matcher aMatcher = getMatcher (sRegEx, sValue);
    if (!aMatcher.find ())
    {
      // Values does not match RegEx
      return null;
    }

    // groupCount is excluding the .group(0) match!!!
    final int nGroupCount = aMatcher.groupCount ();
    final String [] ret = new String [nGroupCount];
    for (int i = 0; i < nGroupCount; ++i)
      ret[i] = aMatcher.group (i + 1);
    return ret;
  }
}
