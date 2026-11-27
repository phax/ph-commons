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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based tests for the optimised methods of {@link StringHelper}, compared with
 * straightforward reference implementations.
 *
 * @author Philip Helger
 */
public final class StringHelperPropertyTest
{
  /** Few different characters, so that matches are frequent */
  private static final IGenerator <String> TEXT = Generators.strings ("abAB-", 0, 12);
  private static final IGenerator <String> SHORT_TEXT = Generators.strings ("abAB-", 0, 3);

  /** ASCII texts with the letters that have special casing rules in some locales */
  private static final IGenerator <String> CASE_TEXT = Generators.strings ("iIaAjJ-", 0, 12);
  private static final IGenerator <String> CASE_SEARCH = Generators.strings ("iIaAjJ-", 0, 3);
  private static final IGenerator <Locale> LOCALES = Generators.of (Locale.ROOT,
                                                                   Locale.GERMAN,
                                                                   Locale.forLanguageTag ("tr"),
                                                                   Locale.forLanguageTag ("az"),
                                                                   Locale.forLanguageTag ("lt"));

  @Test
  public void testIndexOfIgnoreCase ()
  {
    // For ASCII input, lower casing never changes the length, so the lower case copies are a valid
    // reference
    new PropertyChecker ().setTries (2000)
                          .forAll (CASE_TEXT, CASE_SEARCH, Generators.ints (-3, 15), LOCALES, (sText, sSearch, aFrom, aLocale) -> {
                            final int nFrom = aFrom.intValue ();
                            final String sTextLC = sText.toLowerCase (aLocale);
                            final String sSearchLC = sSearch.toLowerCase (aLocale);
                            assertEquals (sTextLC.indexOf (sSearchLC),
                                          StringHelper.getIndexOfIgnoreCase (sText, sSearch, aLocale));
                            assertEquals (sTextLC.indexOf (sSearchLC, nFrom),
                                          StringHelper.getIndexOfIgnoreCase (sText, nFrom, sSearch, aLocale));
                            assertEquals (sTextLC.lastIndexOf (sSearchLC),
                                          StringHelper.getLastIndexOfIgnoreCase (sText, sSearch, aLocale));
                            assertEquals (sTextLC.lastIndexOf (sSearchLC, nFrom),
                                          StringHelper.getLastIndexOfIgnoreCase (sText, nFrom, sSearch, aLocale));
                          });
  }

  @Test
  public void testStartsAndEndsWith ()
  {
    new PropertyChecker ().setTries (1000).forAll (TEXT, SHORT_TEXT, (sText, sSearch) -> {
      final boolean bStarts = sText.startsWith (sSearch);
      final boolean bEnds = sText.endsWith (sSearch);
      assertEquals (Boolean.valueOf (bStarts), Boolean.valueOf (StringHelper.startsWith (sText, sSearch)));
      assertEquals (Boolean.valueOf (bEnds), Boolean.valueOf (StringHelper.endsWith (sText, sSearch)));

      // Only the characters are compared, independent of the type of the char sequences
      final CharSequence aTextSB = new StringBuilder (sText);
      final CharSequence aSearchCB = CharBuffer.wrap (sSearch);
      assertEquals (Boolean.valueOf (bStarts), Boolean.valueOf (StringHelper.startsWith (aTextSB, aSearchCB)));
      assertEquals (Boolean.valueOf (bEnds), Boolean.valueOf (StringHelper.endsWith (aTextSB, aSearchCB)));
      assertEquals (Boolean.valueOf (bStarts),
                    Boolean.valueOf (StringHelper.startsWith ((CharSequence) sText, new StringBuilder (sSearch))));
      assertEquals (Boolean.valueOf (bEnds),
                    Boolean.valueOf (StringHelper.endsWith ((CharSequence) sText, new StringBuilder (sSearch))));

      final boolean bStartsIC = sText.length () >= sSearch.length () &&
                                sText.substring (0, sSearch.length ()).equalsIgnoreCase (sSearch);
      final boolean bEndsIC = sText.length () >= sSearch.length () &&
                              sText.substring (sText.length () - sSearch.length ()).equalsIgnoreCase (sSearch);
      assertEquals (Boolean.valueOf (bStartsIC), Boolean.valueOf (StringHelper.startsWithIgnoreCase (sText, sSearch)));
      assertEquals (Boolean.valueOf (bEndsIC), Boolean.valueOf (StringHelper.endsWithIgnoreCase (sText, sSearch)));
    });
  }

  @Test
  public void testTrimRepeatedly ()
  {
    new PropertyChecker ().setTries (1000).forAll (TEXT, SHORT_TEXT, (sText, sTrim) -> {
      String sStart = sText;
      String sEnd = sText;
      if (sTrim.length () > 0)
      {
        while (sStart.startsWith (sTrim))
          sStart = sStart.substring (sTrim.length ());
        while (sEnd.endsWith (sTrim))
          sEnd = sEnd.substring (0, sEnd.length () - sTrim.length ());
      }
      assertEquals (sStart, StringHelper.trimStartRepeatedly (sText, sTrim));
      assertEquals (sEnd, StringHelper.trimEndRepeatedly (sText, sTrim));

      if (sTrim.length () > 0)
      {
        final char c = sTrim.charAt (0);
        String sStartChar = sText;
        while (sStartChar.length () > 0 && sStartChar.charAt (0) == c)
          sStartChar = sStartChar.substring (1);
        String sEndChar = sText;
        while (sEndChar.length () > 0 && sEndChar.charAt (sEndChar.length () - 1) == c)
          sEndChar = sEndChar.substring (0, sEndChar.length () - 1);
        assertEquals (sStartChar, StringHelper.trimStartRepeatedly (sText, c));
        assertEquals (sEndChar, StringHelper.trimEndRepeatedly (sText, c));
      }
    });
  }

  private static String [] _explodeReference (final char cSep, final String sText, final int nMaxItems)
  {
    final List <String> ret = new ArrayList <> ();
    int nStart = 0;
    while (true)
    {
      if (nMaxItems > 0 && ret.size () == nMaxItems - 1)
        break;
      final int nIndex = sText.indexOf (cSep, nStart);
      if (nIndex < 0)
        break;
      ret.add (sText.substring (nStart, nIndex));
      nStart = nIndex + 1;
    }
    ret.add (sText.substring (nStart));
    return ret.toArray (new String [0]);
  }

  @Test
  public void testExplodedArray ()
  {
    new PropertyChecker ().setTries (1000).forAll (TEXT, Generators.ints (-1, 5), (sText, aMax) -> {
      final int nMax = aMax.intValue ();
      final String [] aExpected = sText.isEmpty () && nMax != 1 ? new String [0] : _explodeReference ('-',
                                                                                                       sText,
                                                                                                       nMax);
      assertArrayEquals (aExpected, StringHelper.getExplodedArray ('-', sText, nMax));
    });
  }

  @Test
  public void testRepeated ()
  {
    new PropertyChecker ().forAll (SHORT_TEXT, Generators.ints (0, 20), (sText, aRepeats) -> {
      final int nRepeats = aRepeats.intValue ();
      final StringBuilder aSB = new StringBuilder ();
      for (int i = 0; i < nRepeats; ++i)
        aSB.append (sText);
      assertEquals (aSB.toString (), StringHelper.getRepeated (sText, nRepeats));
      if (sText.length () > 0)
      {
        final StringBuilder aSBChar = new StringBuilder ();
        for (int i = 0; i < nRepeats; ++i)
          aSBChar.append (sText.charAt (0));
        assertEquals (aSBChar.toString (), StringHelper.getRepeated (sText.charAt (0), nRepeats));
      }
    });
  }
}
