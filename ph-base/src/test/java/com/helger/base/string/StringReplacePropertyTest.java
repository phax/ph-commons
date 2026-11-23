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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingStringWriter;
import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based tests for the optimised multi character replacement of {@link StringReplace},
 * compared with a straightforward reference implementation.
 *
 * @author Philip Helger
 */
public final class StringReplacePropertyTest
{
  /**
   * Characters around the boundaries of the internal bit mask (64 characters per long, 256 in
   * total), plus some arbitrary ones. Search characters and input both use them, so that matches are
   * frequent.
   */
  /**
   * Characters around the boundaries of the internal bit mask (64 characters per long, 256 in total)
   * plus some typical ones.
   */
  private static final String BOUNDARY_CHARS = "\u0000\u003f\u0040\u007f\u0080\u00bf\u00c0\u00ff\u0100\u0101\uffff&<a";

  /**
   * Boundary characters or arbitrary characters. Search characters and input both use them, so that
   * matches are frequent.
   */
  private static final IGenerator <Character> CHARS = Generators.oneOf (Generators.ints (0,
                                                                                         BOUNDARY_CHARS.length () - 1)
                                                                                  .map (aIndex -> Character.valueOf (BOUNDARY_CHARS.charAt (aIndex.intValue ()))),
                                                                        Generators.ints (0, 0xffff)
                                                                                  .map (aValue -> Character.valueOf ((char) aValue.intValue ())));

  private static final IGenerator <String> TEXT = Generators.lists (CHARS, 0, 50).map (StringReplacePropertyTest::_toString);

  private static String _toString (final List <Character> aChars)
  {
    final StringBuilder aSB = new StringBuilder (aChars.size ());
    for (final Character aChar : aChars)
      aSB.append (aChar.charValue ());
    return aSB.toString ();
  }

  /**
   * The reference: for each input character, the replacement of the first matching search character
   * is used.
   */
  private static String _replaceReference (final String sInput,
                                           final char [] aSearchChars,
                                           final char [] [] aReplacements)
  {
    final StringBuilder aSB = new StringBuilder ();
    for (final char c : sInput.toCharArray ())
    {
      int nFound = -1;
      for (int i = 0; i < aSearchChars.length && nFound < 0; ++i)
        if (aSearchChars[i] == c)
          nFound = i;
      if (nFound >= 0)
        aSB.append (aReplacements[nFound]);
      else
        aSB.append (c);
    }
    return aSB.toString ();
  }

  private static int _countReference (final String sInput, final char [] aSearchChars)
  {
    int ret = 0;
    for (final char c : sInput.toCharArray ())
      if (new String (aSearchChars).indexOf (c) >= 0)
        ret++;
    return ret;
  }

  @Test
  public void testReplaceMultipleWithStrings ()
  {
    // The replacement strings are derived from the search characters, so that their number always
    // matches - "x" + the search character, or empty for every third one
    new PropertyChecker ().setTries (1000).forAll (TEXT, TEXT, (sInput, sSearch) -> {
      final char [] aSearchChars = sSearch.toCharArray ();
      final char [] [] aReplacements = new char [aSearchChars.length] [];
      for (int i = 0; i < aSearchChars.length; ++i)
        aReplacements[i] = i % 3 == 2 ? new char [0] : new char [] { 'x', aSearchChars[i] };

      final String sExpected = _replaceReference (sInput, aSearchChars, aReplacements);
      assertEquals (sExpected, new String (StringReplace.replaceMultiple (sInput, aSearchChars, aReplacements)));
      assertEquals (sExpected,
                    new String (StringReplace.replaceMultiple (sInput.toCharArray (), aSearchChars, aReplacements)));

      final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
      final int nReplacements = StringReplace.replaceMultipleTo (sInput, aSearchChars, aReplacements, aSW);
      assertEquals (sExpected, aSW.getAsString ());
      assertEquals (_countReference (sInput, aSearchChars), nReplacements);
    });
  }

  @Test
  public void testReplaceMultipleWithStringsSlice ()
  {
    new PropertyChecker ().forAll (TEXT,
                                   TEXT,
                                   Generators.ints (0, Integer.MAX_VALUE),
                                   Generators.ints (0, Integer.MAX_VALUE),
                                   (sInput, sSearch, aOfs, aLen) -> {
                                     final int nOfs = aOfs.intValue () % (sInput.length () + 1);
                                     final int nLen = aLen.intValue () % (sInput.length () - nOfs + 1);
                                     final char [] aSearchChars = sSearch.toCharArray ();
                                     final char [] [] aReplacements = new char [aSearchChars.length] [];
                                     for (int i = 0; i < aSearchChars.length; ++i)
                                       aReplacements[i] = new char [] { '[', aSearchChars[i], ']' };

                                     final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
                                     StringReplace.replaceMultipleTo (sInput.toCharArray (),
                                                                      nOfs,
                                                                      nLen,
                                                                      aSearchChars,
                                                                      aReplacements,
                                                                      aSW);
                                     assertEquals (_replaceReference (sInput.substring (nOfs, nOfs + nLen),
                                                                      aSearchChars,
                                                                      aReplacements),
                                                   aSW.getAsString ());
                                   });
  }

  @Test
  public void testReplaceMultipleWithChar ()
  {
    new PropertyChecker ().setTries (1000).forAll (TEXT, TEXT, CHARS, (sInput, sSearch, aReplacement) -> {
      final char [] aSearchChars = sSearch.toCharArray ();
      final char [] [] aReplacements = new char [aSearchChars.length] [];
      for (int i = 0; i < aSearchChars.length; ++i)
        aReplacements[i] = new char [] { aReplacement.charValue () };

      final String sExpected = _replaceReference (sInput, aSearchChars, aReplacements);
      assertEquals (sExpected, new String (StringReplace.replaceMultiple (sInput, aSearchChars, aReplacement.charValue ())));
      assertEquals (sExpected, StringReplace.replaceMultipleAsString (sInput, aSearchChars, aReplacement.charValue ()));

      final StringBuilder aSB = new StringBuilder ();
      StringReplace.replaceMultipleTo (sInput, aSearchChars, aReplacement.charValue (), aSB);
      assertEquals (sExpected, aSB.toString ());
    });
  }
}
