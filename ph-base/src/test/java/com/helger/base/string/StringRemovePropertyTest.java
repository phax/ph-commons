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

import org.junit.Test;

import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based tests for the optimised methods of {@link StringRemove} and {@link StringCount},
 * compared with straightforward reference implementations.
 *
 * @author Philip Helger
 */
public final class StringRemovePropertyTest
{
  /** Few different characters incl. characters &ge; 256, so that matches are frequent */
  private static final IGenerator <String> TEXT = Generators.strings ("abÿĀ€", 0, 20);
  private static final IGenerator <String> SEARCH = Generators.strings ("abÿĀ€", 0, 3);

  private static String _removeReference (final String sText, final String sRemoveChars)
  {
    final StringBuilder aSB = new StringBuilder ();
    for (final char c : sText.toCharArray ())
      if (sRemoveChars.indexOf (c) < 0)
        aSB.append (c);
    return aSB.toString ();
  }

  @Test
  public void testRemove ()
  {
    new PropertyChecker ().setTries (1000).forAll (TEXT, SEARCH, (sText, sSearch) -> {
      assertEquals (_removeReference (sText, sSearch), StringRemove.removeMultiple (sText, sSearch.toCharArray ()));
      if (sSearch.length () > 0)
      {
        final char c = sSearch.charAt (0);
        assertEquals (_removeReference (sText, Character.toString (c)), StringRemove.removeAll (sText, c));
      }
    });
  }

  @Test
  public void testCharCount ()
  {
    new PropertyChecker ().setTries (1000).forAll (TEXT, SEARCH, (sText, sSearch) -> {
      if (sSearch.length () > 0)
      {
        final char c = sSearch.charAt (0);
        final int nExpected = sText.length () - _removeReference (sText, Character.toString (c)).length ();
        assertEquals (nExpected, StringCount.getCharCount (sText, c));
        assertEquals (nExpected, StringCount.getCharCount (sText.toCharArray (), c));
        assertEquals (nExpected, StringCount.getOccurrenceCount (sText, c));
      }
    });
  }
}
