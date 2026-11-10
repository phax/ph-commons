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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

/**
 * Test class for class {@link XmlWhitespace}.
 *
 * @author Philip Helger
 */
public final class XmlWhitespaceTest
{
  private static boolean _isXmlWs (final char c)
  {
    return c == ' ' || c == '\t' || c == '\r' || c == '\n';
  }

  /**
   * Straightforward reference implementation of the XSD "collapse" processing.
   */
  private static String _collapseReference (final String s)
  {
    final StringBuilder aSB = new StringBuilder ();
    boolean bPendingSpace = false;
    for (final char c : s.toCharArray ())
    {
      if (_isXmlWs (c))
        bPendingSpace = aSB.length () > 0;
      else
      {
        if (bPendingSpace)
          aSB.append (' ');
        bPendingSpace = false;
        aSB.append (c);
      }
    }
    return aSB.toString ();
  }

  @Test
  public void testIsXmlWhitespace ()
  {
    for (char c = 0; c < 0xffff; ++c)
      assertEquals (_isXmlWs (c), XmlWhitespace.isXmlWhitespace (c));
    assertTrue (XmlWhitespace.isXmlWhitespace ((int) ' '));
    assertTrue (XmlWhitespace.isXmlWhitespace (0x9));
    assertTrue (XmlWhitespace.isXmlWhitespace (0xA));
    assertTrue (XmlWhitespace.isXmlWhitespace (0xD));
    assertFalse (XmlWhitespace.isXmlWhitespace (0xA0));
    assertFalse (XmlWhitespace.isXmlWhitespace (0x10020));
    assertFalse (XmlWhitespace.isXmlWhitespace (-1));
    assertFalse (XmlWhitespace.isXmlWhitespace (' '));
    assertFalse (XmlWhitespace.isXmlWhitespace ('\u000C'));
  }

  @Test
  public void testIsAllXmlWhitespace ()
  {
    assertFalse (XmlWhitespace.isAllXmlWhitespace (null));
    assertFalse (XmlWhitespace.isAllXmlWhitespace (""));
    assertTrue (XmlWhitespace.isAllXmlWhitespace (" "));
    assertTrue (XmlWhitespace.isAllXmlWhitespace (" \t\r\n"));
    assertTrue (XmlWhitespace.isAllXmlWhitespace (new StringBuilder ("\n\n")));
    assertFalse (XmlWhitespace.isAllXmlWhitespace (" a "));
    assertFalse (XmlWhitespace.isAllXmlWhitespace (" "));
    assertFalse (XmlWhitespace.isAllXmlWhitespace (" \u000C"));
  }

  @Test
  public void testTrimXmlWhitespace ()
  {
    assertNull (XmlWhitespace.trimXmlWhitespace (null));
    final String s = "a b";
    assertSame (s, XmlWhitespace.trimXmlWhitespace (s));
    final String sEmpty = "";
    assertSame (sEmpty, XmlWhitespace.trimXmlWhitespace (sEmpty));
    assertEquals ("", XmlWhitespace.trimXmlWhitespace (" \t\r\n"));
    assertEquals ("a \t b", XmlWhitespace.trimXmlWhitespace ("\n a \t b\r "));
    // Other control characters are kept (unlike String.trim)
    assertEquals ("\u000Ca\u0000", XmlWhitespace.trimXmlWhitespace (" \u000Ca\u0000 "));
  }

  @Test
  public void testReplaceXmlWhitespace ()
  {
    assertNull (XmlWhitespace.replaceXmlWhitespace (null));
    final String s = " a  b ";
    assertSame (s, XmlWhitespace.replaceXmlWhitespace (s));
    assertEquals ("    ", XmlWhitespace.replaceXmlWhitespace ("\t\r\n "));
    assertEquals ("a b  c\u000C", XmlWhitespace.replaceXmlWhitespace ("a\tb\r\nc\u000C"));
  }

  @Test
  public void testCollapseXmlWhitespace ()
  {
    assertNull (XmlWhitespace.collapseXmlWhitespace (null));
    final String s = "a b c";
    assertSame (s, XmlWhitespace.collapseXmlWhitespace (s));
    final String sEmpty = "";
    assertSame (sEmpty, XmlWhitespace.collapseXmlWhitespace (sEmpty));
    assertEquals ("", XmlWhitespace.collapseXmlWhitespace (" \t\r\n "));
    assertEquals ("a b", XmlWhitespace.collapseXmlWhitespace ("  a b\n"));
    assertEquals ("a b c", XmlWhitespace.collapseXmlWhitespace ("a\tb  c"));
    assertEquals ("a b c", XmlWhitespace.collapseXmlWhitespace ("\r\n a \t\t b\nc  "));
    assertEquals ("a  b", XmlWhitespace.collapseXmlWhitespace ("a  b"));

    // Compare with a reference implementation
    final char [] aAlphabet = { 'a', 'b', ' ', '\t', '\r', '\n', ' ' };
    final Random aRandom = new Random (20261110);
    for (int n = 0; n < 20_000; ++n)
    {
      final StringBuilder aSB = new StringBuilder ();
      final int nLen = aRandom.nextInt (10);
      for (int i = 0; i < nLen; ++i)
        aSB.append (aAlphabet[aRandom.nextInt (aAlphabet.length)]);
      final String sInput = aSB.toString ();
      final String sExpected = _collapseReference (sInput);
      final String sActual = XmlWhitespace.collapseXmlWhitespace (sInput);
      assertEquals (sExpected, sActual);
      if (sExpected.equals (sInput))
        assertSame (sInput, sActual);
      // Replace and then collapse is the same as collapse
      assertEquals (sExpected, XmlWhitespace.collapseXmlWhitespace (XmlWhitespace.replaceXmlWhitespace (sInput)));
    }
  }
}
