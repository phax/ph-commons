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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link XmlNameChars}.
 *
 * @author Philip Helger
 */
public final class XmlNameCharsTest
{
  /** NameStartChar without colon from XML 1.0 5th edition as inclusive ranges */
  private static final int [] NC_NAME_START_RANGES = { 'A',
                                                       'Z',
                                                       '_',
                                                       '_',
                                                       'a',
                                                       'z',
                                                       0xC0,
                                                       0xD6,
                                                       0xD8,
                                                       0xF6,
                                                       0xF8,
                                                       0x2FF,
                                                       0x370,
                                                       0x37D,
                                                       0x37F,
                                                       0x1FFF,
                                                       0x200C,
                                                       0x200D,
                                                       0x2070,
                                                       0x218F,
                                                       0x2C00,
                                                       0x2FEF,
                                                       0x3001,
                                                       0xD7FF,
                                                       0xF900,
                                                       0xFDCF,
                                                       0xFDF0,
                                                       0xFFFD,
                                                       0x10000,
                                                       0xEFFFF };
  /** Additional NameChar ranges */
  private static final int [] NAME_EXTRA_RANGES = { '-',
                                                    '-',
                                                    '.',
                                                    '.',
                                                    '0',
                                                    '9',
                                                    0xB7,
                                                    0xB7,
                                                    0x300,
                                                    0x36F,
                                                    0x203F,
                                                    0x2040 };

  private static boolean _inRanges (final int [] aRanges, final int n)
  {
    for (int i = 0; i < aRanges.length; i += 2)
      if (n >= aRanges[i] && n <= aRanges[i + 1])
        return true;
    return false;
  }

  @Test
  public void testCharsAgainstRanges ()
  {
    for (int n = -2; n <= 0x10FFFF + 1; ++n)
    {
      final boolean bNCStart = _inRanges (NC_NAME_START_RANGES, n);
      final boolean bNCChar = bNCStart || _inRanges (NAME_EXTRA_RANGES, n);
      assertEquals (Integer.toHexString (n), bNCStart, XmlNameChars.isNCNameStartChar (n));
      assertEquals (Integer.toHexString (n), bNCChar, XmlNameChars.isNCNameChar (n));
      assertEquals (Integer.toHexString (n), bNCStart || n == ':', XmlNameChars.isNameStartChar (n));
      assertEquals (Integer.toHexString (n), bNCChar || n == ':', XmlNameChars.isNameChar (n));
    }
  }

  @Test
  public void testIsName ()
  {
    assertFalse (XmlNameChars.isName (null));
    assertFalse (XmlNameChars.isName (""));
    assertTrue (XmlNameChars.isName ("a"));
    assertTrue (XmlNameChars.isName ("_a-b.c9"));
    assertTrue (XmlNameChars.isName (":"));
    assertTrue (XmlNameChars.isName ("xs:element"));
    assertTrue (XmlNameChars.isName ("a::b:"));
    assertTrue (XmlNameChars.isName ("ä·"));
    assertFalse (XmlNameChars.isName ("9a"));
    assertFalse (XmlNameChars.isName ("-a"));
    assertFalse (XmlNameChars.isName (".a"));
    assertFalse (XmlNameChars.isName ("·"));
    assertFalse (XmlNameChars.isName ("a b"));
    assertFalse (XmlNameChars.isName ("a×"));

    // Supplementary characters
    final String s10000 = new String (Character.toChars (0x10000));
    final String sF0000 = new String (Character.toChars (0xF0000));
    assertTrue (XmlNameChars.isName (s10000));
    assertTrue (XmlNameChars.isName ("a" + s10000));
    assertFalse (XmlNameChars.isName (sF0000));
    assertFalse (XmlNameChars.isName ("a" + sF0000));
    // Unpaired surrogates
    assertFalse (XmlNameChars.isName ("\uD800"));
    assertFalse (XmlNameChars.isName ("a\uDC00"));
  }

  @Test
  public void testIsNCName ()
  {
    assertFalse (XmlNameChars.isNCName (null));
    assertFalse (XmlNameChars.isNCName (""));
    assertTrue (XmlNameChars.isNCName ("a"));
    assertTrue (XmlNameChars.isNCName ("_a-b.c9"));
    assertFalse (XmlNameChars.isNCName (":"));
    assertFalse (XmlNameChars.isNCName ("xs:element"));
    assertFalse (XmlNameChars.isNCName ("a:"));
    assertFalse (XmlNameChars.isNCName ("9a"));
    assertTrue (XmlNameChars.isNCName (new String (Character.toChars (0xEFFFF))));
    assertFalse (XmlNameChars.isNCName ("\uD800"));
  }
}
