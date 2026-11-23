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
import static org.junit.Assert.assertSame;

import java.util.regex.Pattern;

import org.junit.Test;

import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based tests for {@link XmlWhitespace#collapseXmlWhitespace(String)}, compared with a
 * regular expression based reference implementation.
 *
 * @author Philip Helger
 */
public final class XmlWhitespacePropertyTest
{
  private static final Pattern XML_WHITESPACE_RUN = Pattern.compile ("[ \\t\\n\\r]+");

  /**
   * XML whitespace, other whitespace that must be kept (form feed, no-break space), control
   * characters and some letters - or arbitrary Unicode.
   */
  private static final IGenerator <String> TEXT = Generators.oneOf (Generators.strings (" \t\n\r\f\u00a0\u0001ab", 0, 30),
                                                                    Generators.unicodeStrings (0, 30));

  private static String _collapseReference (final String sText)
  {
    final String sCollapsed = XML_WHITESPACE_RUN.matcher (sText).replaceAll (" ");
    final int nStart = sCollapsed.startsWith (" ") ? 1 : 0;
    final int nEnd = sCollapsed.length () > nStart && sCollapsed.endsWith (" ") ? sCollapsed.length () - 1
                                                                                : sCollapsed.length ();
    return sCollapsed.substring (nStart, nEnd);
  }

  @Test
  public void testMatchesReference ()
  {
    new PropertyChecker ().setTries (1000)
                          .forAll (TEXT,
                                   sText -> assertEquals (_collapseReference (sText),
                                                          XmlWhitespace.collapseXmlWhitespace (sText)));
  }

  /**
   * Collapsing is idempotent, and an already collapsed string is returned as the same instance, as
   * documented.
   */
  @Test
  public void testIdempotentAndSameInstance ()
  {
    new PropertyChecker ().forAll (TEXT, sText -> {
      final String sCollapsed = XmlWhitespace.collapseXmlWhitespace (sText);
      assertSame (sCollapsed, XmlWhitespace.collapseXmlWhitespace (sCollapsed));
      if (sCollapsed.equals (sText))
        assertSame (sText, sCollapsed);
    });
  }
}
