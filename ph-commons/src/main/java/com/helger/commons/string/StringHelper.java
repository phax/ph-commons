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
package com.helger.commons.string;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.CheckReturnValue;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.CGlobal;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.string.StringCount;
import com.helger.base.string.Strings;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.CommonsTreeSet;
import com.helger.collection.commons.ICommonsList;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Generic string transformation and helper methods. If you need to modify a string, start looking
 * in this class.
 *
 * @author Philip Helger
 */
@Immutable
public final class StringHelper extends Strings
{
  @PresentForCodeCoverage
  private static final StringHelper INSTANCE = new StringHelper ();

  private StringHelper ()
  {}

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
  @Nonnull
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
  @Nonnull
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
  @Nonnull
  @ReturnsMutableObject ("The passed parameter")
  @CodingStyleguideUnaware
  public static <COLLTYPE extends Collection <String>> COLLTYPE getExploded (final char cSep,
                                                                             @Nullable final String sElements,
                                                                             final int nMaxItems,
                                                                             @Nonnull final COLLTYPE aCollection)
  {
    explode (cSep, sElements, nMaxItems, aCollection::add);
    return aCollection;
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
   * Take a concatenated String and return a {@link ICommonsList} of all elements in the passed
   * string, using specified separator string.
   *
   * @param cSep
   *        The separator character to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @return The {@link ICommonsList} represented by the passed string. Never <code>null</code>. If
   *         the passed input string is <code>null</code> or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <String> getExploded (final char cSep, @Nullable final String sElements)
  {
    return getExploded (cSep, sElements, -1);
  }

  /**
   * Take a concatenated String and return a {@link ICommonsList} of all elements in the passed
   * string, using specified separator string.
   *
   * @param cSep
   *        The separator character to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le; 0 all items are
   *        used. If max items is 1, than the result string is returned as is. If max items is
   *        larger than the number of elements found, it has no effect.
   * @return The {@link ICommonsList} represented by the passed string. Never <code>null</code>. If
   *         the passed input string is <code>null</code> or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <String> getExploded (final char cSep,
                                                   @Nullable final String sElements,
                                                   final int nMaxItems)
  {
    return getExploded (cSep,
                        sElements,
                        nMaxItems,
                        nMaxItems >= 1 ? new CommonsArrayList <> (nMaxItems) : new CommonsArrayList <> ());
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
  @Nonnull
  @CodingStyleguideUnaware
  public static <COLLTYPE extends Collection <String>> COLLTYPE getExploded (@Nonnull final String sSep,
                                                                             @Nullable final String sElements,
                                                                             final int nMaxItems,
                                                                             @Nonnull final COLLTYPE aCollection)
  {
    explode (sSep, sElements, nMaxItems, aCollection::add);
    return aCollection;
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

  /**
   * Take a concatenated String and return a {@link ICommonsList} of all elements in the passed
   * string, using specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @return The {@link ICommonsList} represented by the passed string. Never <code>null</code>. If
   *         the passed input string is <code>null</code> or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <String> getExploded (@Nonnull final String sSep, @Nullable final String sElements)
  {
    return getExploded (sSep, sElements, -1);
  }

  /**
   * Take a concatenated String and return a {@link ICommonsList} of all elements in the passed
   * string, using specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le; 0 all items are
   *        used. If max items is 1, than the result string is returned as is. If max items is
   *        larger than the number of elements found, it has no effect.
   * @return The {@link ICommonsList} represented by the passed string. Never <code>null</code>. If
   *         the passed input string is <code>null</code> or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <String> getExploded (@Nonnull final String sSep,
                                                   @Nullable final String sElements,
                                                   final int nMaxItems)
  {
    return getExploded (sSep,
                        sElements,
                        nMaxItems,
                        nMaxItems >= 1 ? new CommonsArrayList <> (nMaxItems) : new CommonsArrayList <> ());
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
  @Nonnull
  @ReturnsMutableCopy
  public static CommonsHashSet <String> getExplodedToSet (@Nonnull final String sSep, @Nullable final String sElements)
  {
    return getExploded (sSep, sElements, -1, new CommonsHashSet <> ());
  }

  /**
   * Take a concatenated String and return an ordered {@link CommonsLinkedHashSet} of all elements
   * in the passed string, using specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @return The ordered {@link Set} represented by the passed string. Never <code>null</code>. If
   *         the passed input string is <code>null</code> or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static CommonsLinkedHashSet <String> getExplodedToOrderedSet (@Nonnull final String sSep,
                                                                       @Nullable final String sElements)
  {
    return getExploded (sSep, sElements, -1, new CommonsLinkedHashSet <> ());
  }

  /**
   * Take a concatenated String and return a sorted {@link CommonsTreeSet} of all elements in the
   * passed string, using specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @return The sorted {@link Set} represented by the passed string. Never <code>null</code>. If
   *         the passed input string is <code>null</code> or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static CommonsTreeSet <String> getExplodedToSortedSet (@Nonnull final String sSep,
                                                                @Nullable final String sElements)
  {
    return getExploded (sSep, sElements, -1, new CommonsTreeSet <> ());
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
  @Nonnull
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
  @Nonnull
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
  @Nonnull
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
  public static void appendQuoted (@Nonnull final StringBuilder aTarget, @Nullable final String sSource)
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
  public static void appendQuoted (@Nonnull final Appendable aTarget, @Nullable final String sSource) throws IOException
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
   * Convert the passed object to a string using the {@link Object#toString()} method.
   *
   * @param aObject
   *        The value to be converted. May be <code>null</code>.
   * @return An empty string in case the passed object was <code>null</code>. Never
   *         <code>null</code>.
   * @see Object#toString()
   */
  @Nonnull
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
   * @return An empty, non-<code>null</code> string if the passed string has a length &le;
   *         <code>nCount</code>.
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
   * @return An empty, non-<code>null</code> string if the passed string has a length &le; 1.
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
   * @return An empty, non-<code>null</code> string if the passed string has a length &le;
   *         <code>nCount</code>.
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
   * Get the passed string where all spaces (white spaces or unicode spaces) have been removed.
   *
   * @param sStr
   *        The source string. May be <code>null</code>
   * @return A non-<code>null</code> string representing the passed string without any spaces
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
    if (Strings.isEmpty (sSearch))
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
    if (Strings.isEmpty (sSearch))
      return sStr;
    final int nIndex = getLastIndexOf (sStr, sSearch);
    return nIndex == CGlobal.STRING_NOT_FOUND ? sStr : sStr.substring (nIndex + getLength (sSearch));
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
}
