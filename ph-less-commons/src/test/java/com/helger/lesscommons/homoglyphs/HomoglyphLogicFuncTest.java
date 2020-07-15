/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertEquals;

import javax.annotation.Nonnull;

import org.junit.Before;
import org.junit.Test;

import com.helger.collection.map.IntSet;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

public final class HomoglyphLogicFuncTest
{
  private Homoglyph m_aHomoglyph;

  private static void _checkResult (final HomoglyphSearchResult result,
                                    final int expectedIndex,
                                    final String expectedWord,
                                    final String expectedMatch)
  {
    assertEquals (expectedIndex, result.getIndex ());
    assertEquals (expectedWord, result.getWord ());
    assertEquals (expectedMatch, result.getMatch ());
  }

  @Nonnull
  private static IntSet _makeSet (@Nonnull final char... aChars)
  {
    final IntSet s = new IntSet (aChars.length);
    for (final char c : aChars)
      s.add (c);
    return s;
  }

  @Before
  public void beforeTest ()
  {
    final ICommonsList <IntSet> s = new CommonsArrayList <> (_makeSet ('1', 'I', 'l', '|'), _makeSet ('0', 'O'), _makeSet ('5', 'S'));

    m_aHomoglyph = new Homoglyph (s);
  }

  @Test
  public void testWhenTextDoesNotContainAnyTargetWords_thenNoMatchesFound ()
  {
    final ICommonsList <HomoglyphSearchResult> r = m_aHomoglyph.search ("Nothing to see here", "TARGET");
    assertEquals (0, r.size ());
  }

  @Test
  public void testWhenTextIdenticalToTargetWord_thenMatchFound ()
  {
    final ICommonsList <HomoglyphSearchResult> r = m_aHomoglyph.search ("SOIL", "SOIL");
    assertEquals (1, r.size ());
    _checkResult (r.get (0), 0, "SOIL", "SOIL");
  }

  @Test
  public void testWhenTextContainsTargetWord_thenMatchFound ()
  {
    final ICommonsList <HomoglyphSearchResult> r = m_aHomoglyph.search ("I have SOIL in my garden", "SOIL");
    assertEquals (1, r.size ());
    _checkResult (r.get (0), 7, "SOIL", "SOIL");
  }

  @Test
  public void testWhenTextContainsOneOfTheTargetWords_thenMatchFound ()
  {
    final ICommonsList <HomoglyphSearchResult> r = m_aHomoglyph.search ("I have SOIL in my garden", "CHEESE", "SOIL", "FALCONS");
    assertEquals (1, r.size ());
    _checkResult (r.get (0), 7, "SOIL", "SOIL");
  }

  @Test
  public void testWhenTargetWordContainsHomoglyphs_thenMatchFound ()
  {
    final ICommonsList <HomoglyphSearchResult> r = m_aHomoglyph.search ("I have 501L in my garden", "CHEESE", "SOIL", "FALCONS");
    assertEquals (1, r.size ());
    _checkResult (r.get (0), 7, "SOIL", "501L");
  }

  @Test
  public void testWhenTargetWordIsAtStartOfText_thenMatchFound ()
  {
    final ICommonsList <HomoglyphSearchResult> r = m_aHomoglyph.search ("FALC0N5 fly", "CHEESE", "SOIL", "FALCONS");
    assertEquals (1, r.size ());
    _checkResult (r.get (0), 0, "FALCONS", "FALC0N5");
  }

  @Test
  public void testWhenTargetWordIsAtEndOfText_thenMatchFound ()
  {
    final ICommonsList <HomoglyphSearchResult> r = m_aHomoglyph.search ("I like FALC0N5", "CHEESE", "SOIL", "FALCONS");
    assertEquals (1, r.size ());
    _checkResult (r.get (0), 7, "FALCONS", "FALC0N5");
  }

  @Test
  public void testWhenTargetWordHasDifferentCaseInText_thenMatchFound ()
  {
    final ICommonsList <HomoglyphSearchResult> r = m_aHomoglyph.search ("I like fALc0N5 fly", "Falcons");
    assertEquals (1, r.size ());
    _checkResult (r.get (0), 7, "Falcons", "fALc0N5");
  }

  @Test
  public void testWhenTargetWordContainsMultipleMatchesWithDifferentHomoglyphs_thenMatchFound ()
  {
    final ICommonsList <HomoglyphSearchResult> r = m_aHomoglyph.search ("I have 501L and FALC0N5 in my garden, I prefer the SO|L",
                                                                        "CHEESE",
                                                                        "SOIL",
                                                                        "FALCONS");
    assertEquals (3, r.size ());
    _checkResult (r.get (0), 7, "SOIL", "501L");
    _checkResult (r.get (1), 51, "SOIL", "SO|L");
    _checkResult (r.get (2), 16, "FALCONS", "FALC0N5");
  }
}
