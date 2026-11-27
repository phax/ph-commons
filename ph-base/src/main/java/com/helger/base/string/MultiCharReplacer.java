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

import java.io.IOException;
import java.io.Writer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.CGlobal;
import com.helger.base.array.ArrayHelper;
import com.helger.base.enforce.ValueEnforcer;

/**
 * Replaces a set of characters with a set of strings, like
 * {@link StringReplace#replaceMultiple(String, char[], char[][])}. All the preparation work is done
 * once in the constructor, so an instance should be created once and reused for all texts that use
 * the same replacements (e.g. for XML masking). For characters below 256 the replacement is looked
 * up in a table, so that no search character needs to be compared at all.
 * <p>
 * If a search character is contained more than once, the replacement of the first occurrence is
 * used.
 *
 * @author Philip Helger
 * @since 12.9.0
 */
@Immutable
public final class MultiCharReplacer
{
  private static final int LOW_CHAR_COUNT = 256;
  /**
   * The minimum input length for which a single use instance creates a {@link CharSearchSet}. For
   * shorter inputs, creating it costs more than comparing all search characters.
   */
  private static final int SINGLE_USE_SEARCH_SET_MIN_LENGTH = 32;

  private final char [] m_aSearchChars;
  private final char [] [] m_aReplacementStrings;
  /** <code>null</code> for a short single use, in which case all search chars are compared */
  private final CharSearchSet m_aSearchSet;
  /**
   * For each character below 256 the index of its replacement plus 1, or 0 if it is not searched.
   * <code>null</code> if not pre-computed, in which case the search set is used.
   */
  private final int [] m_aLowIndex;

  private static char @NonNull [] _getCopy (final char @NonNull [] aSearchChars,
                                            final char @NonNull [] [] aReplacementStrings)
  {
    ValueEnforcer.notNull (aSearchChars, "SearchChars");
    ValueEnforcer.notNull (aReplacementStrings, "ReplacementStrings");
    ValueEnforcer.isEqual (aSearchChars.length, aReplacementStrings.length, "array length mismatch");
    ValueEnforcer.noNullValue (aReplacementStrings, "ReplacementStrings");
    return aSearchChars.clone ();
  }

  private static char @NonNull [] [] _getDeepCopy (final char @NonNull [] [] aReplacementStrings)
  {
    final char [] [] ret = new char [aReplacementStrings.length] [];
    for (int i = 0; i < aReplacementStrings.length; ++i)
      ret[i] = aReplacementStrings[i].clone ();
    return ret;
  }

  private MultiCharReplacer (final char @NonNull [] aSearchChars,
                             final char @NonNull [] [] aReplacementStrings,
                             final boolean bPrecompute,
                             final boolean bUseSearchSet)
  {
    m_aSearchChars = aSearchChars;
    m_aReplacementStrings = aReplacementStrings;
    m_aSearchSet = bUseSearchSet ? new CharSearchSet (aSearchChars) : null;
    if (bPrecompute)
    {
      m_aLowIndex = new int [LOW_CHAR_COUNT];
      // Iterate backwards, so that the first occurrence of a duplicate search char wins
      for (int i = aSearchChars.length - 1; i >= 0; --i)
      {
        final char c = aSearchChars[i];
        if (c < LOW_CHAR_COUNT)
          m_aLowIndex[c] = i + 1;
      }
    }
    else
      m_aLowIndex = null;
  }

  /**
   * Constructor.
   *
   * @param aSearchChars
   *        The characters to replace. May not be <code>null</code>. The array is copied.
   * @param aReplacementStrings
   *        The new strings to be inserted instead. May not be <code>null</code> and may not contain
   *        <code>null</code> elements. Must have the same array length as aSearchChars. The arrays
   *        are copied.
   */
  public MultiCharReplacer (final char @NonNull [] aSearchChars, final char @NonNull [] [] aReplacementStrings)
  {
    this (_getCopy (aSearchChars, aReplacementStrings), _getDeepCopy (aReplacementStrings), true, true);
  }

  /**
   * Create an instance for a single use, without copying the arrays and without precomputing the
   * lookup table. All parameters must have been validated by the caller.
   *
   * @param aSearchChars
   *        The characters to replace.
   * @param aReplacementStrings
   *        The new strings to be inserted instead.
   * @param nInputLength
   *        The length of the input to be processed. Used to decide if a {@link CharSearchSet} pays
   *        off.
   * @return A new instance. Never <code>null</code>.
   */
  @NonNull
  static MultiCharReplacer createForSingleUse (final char @NonNull [] aSearchChars,
                                               final char @NonNull [] [] aReplacementStrings,
                                               @Nonnegative final int nInputLength)
  {
    return new MultiCharReplacer (aSearchChars,
                                  aReplacementStrings,
                                  false,
                                  nInputLength >= SINGLE_USE_SEARCH_SET_MIN_LENGTH);
  }

  /**
   * @param c
   *        The input character.
   * @return The index of the replacement or -1 if the character is not replaced.
   */
  private int _getIndex (final char c)
  {
    final int [] aLowIndex = m_aLowIndex;
    if (aLowIndex != null && c < LOW_CHAR_COUNT)
      return aLowIndex[c] - 1;
    final CharSearchSet aSearchSet = m_aSearchSet;
    if (aSearchSet != null)
      return aSearchSet.getFirstIndex (c);
    return ArrayHelper.getFirstIndex (m_aSearchChars, c);
  }

  /**
   * Get the result length (in characters) when replacing all characters of the passed input.
   *
   * @param sInput
   *        Input string. May not be <code>null</code>.
   * @return {@link CGlobal#ILLEGAL_UINT} if no replacement is needed, and therefore the length of
   *         the input can be used.
   */
  int getResultLengthOrIllegal (@NonNull final String sInput)
  {
    final int nLen = sInput.length ();
    int ret = nLen;
    boolean bAnyReplacement = false;
    for (int i = 0; i < nLen; ++i)
    {
      final int nIndex = _getIndex (sInput.charAt (i));
      if (nIndex >= 0)
      {
        ret += m_aReplacementStrings[nIndex].length - 1;
        bAnyReplacement = true;
      }
    }
    return bAnyReplacement ? ret : CGlobal.ILLEGAL_UINT;
  }

  /**
   * Get the result length (in characters) when replacing all characters of the passed input.
   *
   * @param aInput
   *        Input char array. May not be <code>null</code>.
   * @return {@link CGlobal#ILLEGAL_UINT} if no replacement is needed, and therefore the length of
   *         the input can be used.
   */
  int getResultLengthOrIllegal (final char @NonNull [] aInput)
  {
    int ret = aInput.length;
    boolean bAnyReplacement = false;
    for (final char c : aInput)
    {
      final int nIndex = _getIndex (c);
      if (nIndex >= 0)
      {
        ret += m_aReplacementStrings[nIndex].length - 1;
        bAnyReplacement = true;
      }
    }
    return bAnyReplacement ? ret : CGlobal.ILLEGAL_UINT;
  }

  /**
   * Get the result length (in characters) when replacing all characters of the passed input.
   *
   * @param sInput
   *        Input string. May be <code>null</code>.
   * @return The length of the result. Always &ge; 0.
   */
  @Nonnegative
  public int getResultLength (@Nullable final String sInput)
  {
    if (sInput == null)
      return 0;
    final int ret = getResultLengthOrIllegal (sInput);
    return ret == CGlobal.ILLEGAL_UINT ? sInput.length () : ret;
  }

  /**
   * Get the result length (in characters) when replacing all characters of the passed input.
   *
   * @param aInput
   *        Input char array. May be <code>null</code>.
   * @return The length of the result. Always &ge; 0.
   */
  @Nonnegative
  public int getResultLength (final char @Nullable [] aInput)
  {
    if (aInput == null)
      return 0;
    final int ret = getResultLengthOrIllegal (aInput);
    return ret == CGlobal.ILLEGAL_UINT ? aInput.length : ret;
  }

  /**
   * Perform the replacement on the non-empty input.
   *
   * @param sInput
   *        The input string. May not be <code>null</code>.
   * @return <code>null</code> if no replacement was needed, the newly created result array
   *         otherwise.
   */
  char @Nullable [] replaceOrNull (@NonNull final String sInput)
  {
    final int nResultLen = getResultLengthOrIllegal (sInput);
    if (nResultLen == CGlobal.ILLEGAL_UINT)
      return null;

    final char [] ret = new char [nResultLen];
    int nDst = 0;
    int nFirstNonReplace = 0;
    final int nLen = sInput.length ();
    for (int i = 0; i < nLen; ++i)
    {
      final int nIndex = _getIndex (sInput.charAt (i));
      if (nIndex >= 0)
      {
        // Copy the characters before as a block
        if (nFirstNonReplace < i)
        {
          sInput.getChars (nFirstNonReplace, i, ret, nDst);
          nDst += i - nFirstNonReplace;
        }
        final char [] aReplacement = m_aReplacementStrings[nIndex];
        System.arraycopy (aReplacement, 0, ret, nDst, aReplacement.length);
        nDst += aReplacement.length;
        nFirstNonReplace = i + 1;
      }
    }
    if (nFirstNonReplace < nLen)
      sInput.getChars (nFirstNonReplace, nLen, ret, nDst);
    return ret;
  }

  /**
   * Perform the replacement on the non-empty input.
   *
   * @param aInput
   *        The input characters. May not be <code>null</code>.
   * @return <code>null</code> if no replacement was needed, the newly created result array
   *         otherwise.
   */
  char @Nullable [] replaceOrNull (final char @NonNull [] aInput)
  {
    final int nResultLen = getResultLengthOrIllegal (aInput);
    if (nResultLen == CGlobal.ILLEGAL_UINT)
      return null;

    final char [] ret = new char [nResultLen];
    int nDst = 0;
    int nFirstNonReplace = 0;
    final int nLen = aInput.length;
    for (int i = 0; i < nLen; ++i)
    {
      final int nIndex = _getIndex (aInput[i]);
      if (nIndex >= 0)
      {
        // Copy the characters before as a block
        if (nFirstNonReplace < i)
        {
          System.arraycopy (aInput, nFirstNonReplace, ret, nDst, i - nFirstNonReplace);
          nDst += i - nFirstNonReplace;
        }
        final char [] aReplacement = m_aReplacementStrings[nIndex];
        System.arraycopy (aReplacement, 0, ret, nDst, aReplacement.length);
        nDst += aReplacement.length;
        nFirstNonReplace = i + 1;
      }
    }
    if (nFirstNonReplace < nLen)
      System.arraycopy (aInput, nFirstNonReplace, ret, nDst, nLen - nFirstNonReplace);
    return ret;
  }

  /**
   * Replace all search characters of the passed string.
   *
   * @param sInput
   *        The input string. May be <code>null</code>.
   * @return The replaced characters, a copy of the input characters if nothing was replaced or an
   *         empty array if the input was <code>null</code> or empty. Never <code>null</code>.
   */
  public char @NonNull [] replace (@Nullable final String sInput)
  {
    if (StringHelper.isEmpty (sInput))
      return CGlobal.EMPTY_CHAR_ARRAY;

    final char [] ret = replaceOrNull (sInput);
    return ret != null ? ret : sInput.toCharArray ();
  }

  /**
   * Replace all search characters of the passed char array.
   *
   * @param aInput
   *        The input characters. May be <code>null</code>.
   * @return The replaced characters, the input array itself if nothing was replaced or an empty
   *         array if the input was <code>null</code> or empty. Never <code>null</code>.
   */
  public char @NonNull [] replace (final char @Nullable [] aInput)
  {
    if (aInput == null || aInput.length == 0)
      return CGlobal.EMPTY_CHAR_ARRAY;

    final char [] ret = replaceOrNull (aInput);
    return ret != null ? ret : aInput;
  }

  /**
   * Replace all search characters of the passed string and write the result to the passed writer.
   *
   * @param sInput
   *        The input string. May be <code>null</code>.
   * @param aTarget
   *        Where the result should be written to. May not be <code>null</code>.
   * @return The number of replacements performed. Always &ge; 0.
   * @throws IOException
   *         In case writing to the Writer fails
   */
  @Nonnegative
  public int replaceTo (@Nullable final String sInput, @NonNull final Writer aTarget) throws IOException
  {
    ValueEnforcer.notNull (aTarget, "Target");
    if (sInput == null)
      return 0;

    final int nLen = sInput.length ();
    int nFirstNonReplace = 0;
    int nTotalReplacements = 0;
    for (int i = 0; i < nLen; ++i)
    {
      final int nIndex = _getIndex (sInput.charAt (i));
      if (nIndex >= 0)
      {
        if (nFirstNonReplace < i)
          aTarget.write (sInput, nFirstNonReplace, i - nFirstNonReplace);
        aTarget.write (m_aReplacementStrings[nIndex]);
        nFirstNonReplace = i + 1;
        ++nTotalReplacements;
      }
    }
    if (nFirstNonReplace < nLen)
      aTarget.write (sInput, nFirstNonReplace, nLen - nFirstNonReplace);
    return nTotalReplacements;
  }

  /**
   * Replace all search characters of the passed char array range and write the result to the passed
   * writer.
   *
   * @param aInput
   *        The input characters. May be <code>null</code>.
   * @param nOfs
   *        Offset into input array. Must be &ge; 0.
   * @param nLen
   *        Number of characters from input array. Must be &ge; 0.
   * @param aTarget
   *        Where the result should be written to. May not be <code>null</code>.
   * @return The number of replacements performed. Always &ge; 0.
   * @throws IOException
   *         In case writing to the Writer fails
   */
  @Nonnegative
  public int replaceTo (final char @Nullable [] aInput,
                        @Nonnegative final int nOfs,
                        @Nonnegative final int nLen,
                        @NonNull final Writer aTarget) throws IOException
  {
    ValueEnforcer.notNull (aTarget, "Target");
    if (aInput == null)
      return 0;
    ValueEnforcer.isArrayOfsLen (aInput, nOfs, nLen);

    final int nEnd = nOfs + nLen;
    int nFirstNonReplace = nOfs;
    int nTotalReplacements = 0;
    for (int i = nOfs; i < nEnd; ++i)
    {
      final int nIndex = _getIndex (aInput[i]);
      if (nIndex >= 0)
      {
        if (nFirstNonReplace < i)
          aTarget.write (aInput, nFirstNonReplace, i - nFirstNonReplace);
        aTarget.write (m_aReplacementStrings[nIndex]);
        nFirstNonReplace = i + 1;
        ++nTotalReplacements;
      }
    }
    if (nFirstNonReplace < nEnd)
      aTarget.write (aInput, nFirstNonReplace, nEnd - nFirstNonReplace);
    return nTotalReplacements;
  }

  /**
   * Replace all search characters of the passed char array and write the result to the passed
   * writer.
   *
   * @param aInput
   *        The input characters. May be <code>null</code>.
   * @param aTarget
   *        Where the result should be written to. May not be <code>null</code>.
   * @return The number of replacements performed. Always &ge; 0.
   * @throws IOException
   *         In case writing to the Writer fails
   */
  @Nonnegative
  public int replaceTo (final char @Nullable [] aInput, @NonNull final Writer aTarget) throws IOException
  {
    return aInput == null ? 0 : replaceTo (aInput, 0, aInput.length, aTarget);
  }
}
