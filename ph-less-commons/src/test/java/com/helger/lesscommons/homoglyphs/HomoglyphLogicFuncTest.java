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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.helger.collection.map.IntSet;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

public final class HomoglyphLogicFuncTest
{
  private Homoglyph m_aHomoglyph;

  private static IntSet _makeSet (final char... chrs)
  {
    final IntSet s = new IntSet (chrs.length);
    for (final char c : chrs)
      s.add (c);
    return s;
  }

  @Before
  public void setup ()
  {
    final ICommonsList <IntSet> s = new CommonsArrayList <> (_makeSet ('1', 'I', 'l', '|'),
                                                             _makeSet ('0', 'O'),
                                                             _makeSet ('5', 'S'));

    m_aHomoglyph = new Homoglyph (s);
  }

  @Test
  public void whenTextDoesNotContainAnyTargetWords_thenNoMatchesFound ()
  {
    final ICommonsList <HomoglyphSearchResult> r = m_aHomoglyph.search ("Nothing to see here", "TARGET");
    assertEquals (0, r.size ());
  }

  @Test
  public void whenTextIdenticalToTargetWord_thenMatchFound ()
  {
    final ICommonsList <HomoglyphSearchResult> r = m_aHomoglyph.search ("SOIL", "SOIL");
    assertEquals (1, r.size ());
    checkResult (r.get (0), 0, "SOIL", "SOIL");
  }

  @Test
  public void whenTextContainsTargetWord_thenMatchFound ()
  {
    final ICommonsList <HomoglyphSearchResult> r = m_aHomoglyph.search ("I have SOIL in my garden", "SOIL");
    assertEquals (1, r.size ());
    checkResult (r.get (0), 7, "SOIL", "SOIL");
  }

  @Test
  public void whenTextContainsOneOfTheTargetWords_thenMatchFound ()
  {
    final ICommonsList <HomoglyphSearchResult> r = m_aHomoglyph.search ("I have SOIL in my garden",
                                                                        "CHEESE",
                                                                        "SOIL",
                                                                        "FALCONS");
    assertEquals (1, r.size ());
    checkResult (r.get (0), 7, "SOIL", "SOIL");
  }

  @Test
  public void whenTargetWordContainsHomoglyphs_thenMatchFound ()
  {
    final ICommonsList <HomoglyphSearchResult> r = m_aHomoglyph.search ("I have 501L in my garden",
                                                                        "CHEESE",
                                                                        "SOIL",
                                                                        "FALCONS");
    assertEquals (1, r.size ());
    checkResult (r.get (0), 7, "SOIL", "501L");
  }

  @Test
  public void whenTargetWordIsAtStartOfText_thenMatchFound ()
  {
    final ICommonsList <HomoglyphSearchResult> r = m_aHomoglyph.search ("FALC0N5 fly", "CHEESE", "SOIL", "FALCONS");
    assertEquals (1, r.size ());
    checkResult (r.get (0), 0, "FALCONS", "FALC0N5");
  }

  @Test
  public void whenTargetWordIsAtEndOfText_thenMatchFound ()
  {
    final ICommonsList <HomoglyphSearchResult> r = m_aHomoglyph.search ("I like FALC0N5", "CHEESE", "SOIL", "FALCONS");
    assertEquals (1, r.size ());
    checkResult (r.get (0), 7, "FALCONS", "FALC0N5");
  }

  @Test
  public void whenTargetWordHasDifferentCaseInText_thenMatchFound ()
  {
    final ICommonsList <HomoglyphSearchResult> r = m_aHomoglyph.search ("I like fALc0N5 fly", "Falcons");
    assertEquals (1, r.size ());
    checkResult (r.get (0), 7, "Falcons", "fALc0N5");
  }

  @Test
  public void whenTargetWordContainsMultipleMatchesWithDifferentHomoglyphs_thenMatchFound ()
  {
    final ICommonsList <HomoglyphSearchResult> r = m_aHomoglyph.search ("I have 501L and FALC0N5 in my garden, I prefer the SO|L",
                                                                        "CHEESE",
                                                                        "SOIL",
                                                                        "FALCONS");
    assertEquals (3, r.size ());
    checkResult (r.get (0), 7, "SOIL", "501L");
    checkResult (r.get (1), 51, "SOIL", "SO|L");
    checkResult (r.get (2), 16, "FALCONS", "FALC0N5");
  }

  private void checkResult (final HomoglyphSearchResult result,
                            final int expectedIndex,
                            final String expectedWord,
                            final String expectedMatch)
  {
    assertEquals (expectedIndex, result.getIndex ());
    assertEquals (expectedWord, result.getWord ());
    assertEquals (expectedMatch, result.getMatch ());
  }
}
