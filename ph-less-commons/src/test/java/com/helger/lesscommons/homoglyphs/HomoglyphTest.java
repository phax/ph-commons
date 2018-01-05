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

import java.io.IOException;
import java.io.UncheckedIOException;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

public final class HomoglyphTest
{
  private static Homoglyph s_aHomoglyph;
  static
  {
    try
    {
      s_aHomoglyph = HomoglyphBuilder.build ();
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
  }

  private void _check (final String text, final String targetWord)
  {
    final ICommonsList <HomoglyphSearchResult> r = s_aHomoglyph.search (text, new CommonsArrayList <> (targetWord));
    assertEquals (1, r.size ());
    assertEquals (targetWord, r.get (0).getWord ());
  }

  @Test
  public void testLowerCase ()
  {
    _check ("free ϲгеԁıｔ!", "credit");
    _check ("free ϲrEd1ᴛ", "credit");
    _check ("see best ｗ℮Ꮟｃ⍺ｍｓ here", "webcams");
    _check ("get blue ｐɪ|ǀs", "pills");
    _check ("саｓℎ prizes!!", "cash");
  }

  @Test
  public void testUpperCase ()
  {
    _check ("FREE ᏟƦⴹⅅ1Ⲧ", "credit");
    _check ("SEE BEST ᎳℰᛒℂᴀᗰᏕ HERE", "webcams");
    _check ("GET BLUE ᑭӀⳑℒＳ", "pills");
    _check ("ℭᴀՏн PRIZES!!", "cash");
  }

  @Test
  public void testMixedCase ()
  {
    _check ("free Ꮯгⴹԁ1ｔ!", "credit");
    _check ("see best ｗℰᛒｃ⍺ᗰｓ here", "webcams");
    _check ("get blue ᑭɪ|Ｌs", "pills");
    _check ("ℭaՏℎ prizes!!", "cash");
  }
}
