package com.helger.lesscommons.homoglyphs;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;

import org.junit.Test;

import com.helger.commons.collection.impl.ICommonsList;

public class HomoglyphDataTest
{
  private static Homoglyph homoglyph;
  static
  {
    try
    {
      homoglyph = HomoglyphBuilder.build ();
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
  }

  private void _check (final String text, final String targetWord)
  {
    final ICommonsList <HomoglyphSearchResult> r = homoglyph.search (text, Arrays.asList (targetWord));
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
