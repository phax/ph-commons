/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.lesscommons.homoglyphs;

import java.io.Serializable;
import java.nio.IntBuffer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.collection.map.IntObjectMap;
import com.helger.collection.map.IntSet;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.string.StringHelper;

/**
 * Use this class to detect occurrences of target words inside a String, where
 * the target words may have been disguised using homoglyph substitution and/or
 * by mixing upper/lower case letters (for example, the class will find the word
 * "credit" in the String "Free Ꮯгⴹԁ1ｔ"). You can supply your own list of
 * homoglyphs, or use the char_codes.txt which should accompany this source
 * file. You can find the latest version of char_codes.txt at
 * https://github.com/codebox/homoglyph
 *
 * @author Rob Dawson
 * @author Philip Helger
 */
@Immutable
public class Homoglyph implements Serializable
{
  // Map from char to all other chars that are homoglyphs
  private final IntObjectMap <IntSet> m_aLookup = new IntObjectMap <> ();

  /**
   * Supply a List of Sets, with each Set containing a group of Unicode
   * codepoints that are homoglyphs. Codepoints must be represented using
   * Integer rather than Character values because some are too large to be held
   * by the 16-bit Character type.
   *
   * @param aHomoglyphs
   *        a List of Sets, with each Set containing a group of Unicode
   *        codepoints that are homoglyphs
   */
  public Homoglyph (@Nonnull final ICommonsList <IntSet> aHomoglyphs)
  {
    ValueEnforcer.notNull (aHomoglyphs, "Homoglyphs");
    for (final IntSet aSet : aHomoglyphs)
      aSet.forEach (nValue -> m_aLookup.put (nValue, aSet));
  }

  private boolean _checkForHomoglyphs (final int cp1, final int cp2)
  {
    if (cp1 == cp2)
      return true;
    final IntSet cp1Set = m_aLookup.get (cp1);
    return cp1Set != null && cp1Set.contains (cp2);
  }

  private boolean _hasWordAtIndex (final CodePoints text, final CodePoints targetWord, final int index)
  {
    for (int i = 0; i < targetWord.getLength (); i++)
    {
      final int nCP = targetWord.getValue (i);
      final int targetCharLower = Character.toLowerCase (nCP);
      final int targetCharUpper = Character.toUpperCase (nCP);
      final int textChar = text.getValue (index + i);
      if (!_checkForHomoglyphs (targetCharLower, textChar) && !_checkForHomoglyphs (targetCharUpper, textChar))
        return false;
    }
    return true;
  }

  @Nonnull
  private ICommonsList <HomoglyphSearchResult> _checkForWord (final CodePoints text, final CodePoints targetWord)
  {
    final ICommonsList <HomoglyphSearchResult> ret = new CommonsArrayList <> ();
    final int lastIndex = text.getLength () - targetWord.getLength ();
    for (int i = 0; i <= lastIndex; i++)
      if (_hasWordAtIndex (text, targetWord, i))
        ret.add (new HomoglyphSearchResult (i, text.subStringAt (i, targetWord.getLength ()), targetWord.getText ()));
    return ret;
  }

  /**
   * Search the String {@code text} to locate all occurrences of the words
   * contained in {@code targetWords}, accounting for homoglyph substitution and
   * variations of case.
   *
   * @param text
   *        text to be searched
   * @param aTargetWords
   *        words to be located
   * @return a List containing the results of the search, if no matches were
   *         found an empty list will be returned
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <HomoglyphSearchResult> search (final String text, final Iterable <String> aTargetWords)
  {
    final ICommonsList <HomoglyphSearchResult> allResults = new CommonsArrayList <> ();
    final CodePoints textCodepoints = new CodePoints (text);
    for (final String sTargetWord : aTargetWords)
      allResults.addAll (_checkForWord (textCodepoints, new CodePoints (sTargetWord)));
    return allResults;
  }

  /**
   * Search the String {@code text} to locate all occurrences of the words
   * contained in {@code targetWords}, accounting for homoglyph substitution and
   * variations of case.
   *
   * @param text
   *        text to be searched
   * @param targetWords
   *        words to be located
   * @return a List containing the results of the search, if no matches were
   *         found an empty list will be returned
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <HomoglyphSearchResult> search (final String text, final String... targetWords)
  {
    return search (text, new CommonsArrayList <> (targetWords));
  }

  private static final class CodePoints
  {
    private final String m_sText;
    private final int [] m_aCodepoints;

    public CodePoints (@Nonnull final String sText)
    {
      m_sText = sText;

      final IntBuffer aBuf = IntBuffer.allocate (sText.length ());
      StringHelper.iterateCodePoints (sText, aBuf::put);
      aBuf.flip ();
      m_aCodepoints = new int [aBuf.limit ()];
      aBuf.get (m_aCodepoints);
    }

    public int getValue (final int i)
    {
      return m_aCodepoints[i];
    }

    @Nonnegative
    public int getLength ()
    {
      return m_aCodepoints.length;
    }

    @Nonnull
    public String getText ()
    {
      return m_sText;
    }

    @Nonnull
    public String subStringAt (@Nonnegative final int nOfs, @Nonnegative final int nLen)
    {
      final StringBuilder sb = new StringBuilder (nLen);
      for (int i = 0; i < nLen; i++)
        sb.appendCodePoint (m_aCodepoints[nOfs + i]);
      return sb.toString ();
    }
  }
}
