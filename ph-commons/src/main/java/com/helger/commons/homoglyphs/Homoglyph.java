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
package com.helger.commons.homoglyphs;

import java.nio.IntBuffer;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.map.IntObjectMap;
import com.helger.collection.set.IntSet;

/**
 * Use this class to detect occurrences of target words inside a String, where the target words may
 * have been disguised using homoglyph substitution and/or by mixing upper/lower case letters (for
 * example, the class will find the word "credit" in the String "Free Ꮯгⴹԁ1ｔ"). You can supply your
 * own list of homoglyphs, or use the char_codes.txt which should accompany this source file. You
 * can find the latest version of char_codes.txt at https://github.com/codebox/homoglyph
 *
 * @author Rob Dawson
 * @author Philip Helger
 */
@Immutable
public class Homoglyph
{
  // Map from char to all other chars that are homoglyphs
  private final IntObjectMap <IntSet> m_aLookup = new IntObjectMap <> ();

  /**
   * Supply a List of Sets, with each Set containing a group of Unicode codepoints that are
   * homoglyphs. Codepoints must be represented using Integer rather than Character values because
   * some are too large to be held by the 16-bit Character type.
   *
   * @param aHomoglyphs
   *        a List of Sets, with each Set containing a group of Unicode codepoints that are
   *        homoglyphs
   */
  public Homoglyph (@NonNull final ICommonsList <@NonNull IntSet> aHomoglyphs)
  {
    ValueEnforcer.notNull (aHomoglyphs, "Homoglyphs");
    for (final IntSet aSet : aHomoglyphs)
      aSet.forEach (nValue -> m_aLookup.put (nValue, aSet));
  }

  private boolean _checkForHomoglyphs (final int nCP1, final int nCP2)
  {
    if (nCP1 == nCP2)
      return true;
    final IntSet aCP1Set = m_aLookup.get (nCP1);
    return aCP1Set != null && aCP1Set.contains (nCP2);
  }

  private boolean _hasWordAtIndex (@NonNull final CodePoints aText,
                                   @NonNull final CodePoints aTargetWord,
                                   final int nIndex)
  {
    for (int i = 0; i < aTargetWord.getLength (); i++)
    {
      final int nCP = aTargetWord.getValue (i);
      final int nTargetCharLower = Character.toLowerCase (nCP);
      final int nTargetCharUpper = Character.toUpperCase (nCP);
      final int nTextChar = aText.getValue (nIndex + i);
      if (!_checkForHomoglyphs (nTargetCharLower, nTextChar) && !_checkForHomoglyphs (nTargetCharUpper, nTextChar))
        return false;
    }
    return true;
  }

  @NonNull
  private ICommonsList <HomoglyphSearchResult> _checkForWord (@NonNull final CodePoints aText,
                                                              @NonNull final CodePoints aTargetWord)
  {
    final ICommonsList <HomoglyphSearchResult> ret = new CommonsArrayList <> ();
    final int nLastIndex = aText.getLength () - aTargetWord.getLength ();
    for (int i = 0; i <= nLastIndex; i++)
      if (_hasWordAtIndex (aText, aTargetWord, i))
        ret.add (new HomoglyphSearchResult (i, aText.subStringAt (i, aTargetWord.getLength ()), aTargetWord.getText ()));
    return ret;
  }

  /**
   * Search the String {@code sText} to locate all occurrences of the words contained in
   * {@code aTargetWords}, accounting for homoglyph substitution and variations of case. Empty target
   * words are ignored.
   *
   * @param sText
   *        text to be searched. May not be <code>null</code>.
   * @param aTargetWords
   *        words to be located. May not be <code>null</code>.
   * @return a List containing the results of the search, if no matches were found an empty list
   *         will be returned
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <HomoglyphSearchResult> search (@NonNull final String sText,
                                                      @NonNull final Iterable <String> aTargetWords)
  {
    ValueEnforcer.notNull (sText, "Text");
    ValueEnforcer.notNull (aTargetWords, "TargetWords");

    final ICommonsList <HomoglyphSearchResult> aAllResults = new CommonsArrayList <> ();
    final CodePoints aTextCodepoints = new CodePoints (sText);
    for (final String sTargetWord : aTargetWords)
      if (StringHelper.isNotEmpty (sTargetWord))
        aAllResults.addAll (_checkForWord (aTextCodepoints, new CodePoints (sTargetWord)));
    return aAllResults;
  }

  /**
   * Search the String {@code sText} to locate all occurrences of the words contained in
   * {@code aTargetWords}, accounting for homoglyph substitution and variations of case. Empty target
   * words are ignored.
   *
   * @param sText
   *        text to be searched. May not be <code>null</code>.
   * @param aTargetWords
   *        words to be located. May not be <code>null</code>.
   * @return a List containing the results of the search, if no matches were found an empty list
   *         will be returned
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <HomoglyphSearchResult> search (@NonNull final String sText,
                                                      @NonNull final String... aTargetWords)
  {
    return search (sText, new CommonsArrayList <> (aTargetWords));
  }

  private static final class CodePoints
  {
    private final String m_sText;
    private final int [] m_aCodepoints;

    /**
     * Constructor extracting the code points from the provided text.
     *
     * @param sText
     *        The text to extract code points from. May not be <code>null</code>.
     */
    public CodePoints (@NonNull final String sText)
    {
      m_sText = sText;

      final IntBuffer aBuf = IntBuffer.allocate (sText.length ());
      StringHelper.iterateCodePoints (sText, aBuf::put);
      aBuf.flip ();
      m_aCodepoints = new int [aBuf.limit ()];
      aBuf.get (m_aCodepoints);
    }

    /**
     * Get the code point value at the specified index.
     *
     * @param i
     *        The index of the code point to retrieve.
     * @return The code point value at the given index.
     */
    public int getValue (final int i)
    {
      return m_aCodepoints[i];
    }

    /**
     * @return The number of code points in this text.
     */
    @Nonnegative
    public int getLength ()
    {
      return m_aCodepoints.length;
    }

    /**
     * @return The original text string. Never <code>null</code>.
     */
    @NonNull
    public String getText ()
    {
      return m_sText;
    }

    /**
     * Extract a substring from the code points starting at the given offset with the specified
     * length.
     *
     * @param nOfs
     *        The zero-based offset to start at.
     * @param nLen
     *        The number of code points to include.
     * @return The extracted substring. Never <code>null</code>.
     */
    @NonNull
    public String subStringAt (@Nonnegative final int nOfs, @Nonnegative final int nLen)
    {
      final StringBuilder sb = new StringBuilder (nLen);
      for (int i = 0; i < nLen; i++)
        sb.appendCodePoint (m_aCodepoints[nOfs + i]);
      return sb.toString ();
    }
  }
}
