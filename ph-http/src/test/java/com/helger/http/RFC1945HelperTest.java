/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.http;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.base.CGlobal;

/**
 * Test class for class {@link RFC1945Helper}.
 *
 * @author Philip Helger
 */
public final class RFC1945HelperTest
{
  @Test
  public void testIsChar ()
  {
    assertFalse (RFC1945Helper.isChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      assertTrue (RFC1945Helper.isChar (i));
    assertFalse (RFC1945Helper.isChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testUpperAlphaChar ()
  {
    assertFalse (RFC1945Helper.isUpperAlphaChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if (i >= 'A' && i <= 'Z')
        assertTrue (RFC1945Helper.isUpperAlphaChar (i));
      else
        assertFalse (RFC1945Helper.isUpperAlphaChar (i));
    assertFalse (RFC1945Helper.isUpperAlphaChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testLowerAlphaChar ()
  {
    assertFalse (RFC1945Helper.isLowerAlphaChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if (i >= 'a' && i <= 'z')
        assertTrue (RFC1945Helper.isLowerAlphaChar (i));
      else
        assertFalse (RFC1945Helper.isLowerAlphaChar (i));
    assertFalse (RFC1945Helper.isLowerAlphaChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testAlphaChar ()
  {
    assertFalse (RFC1945Helper.isAlphaChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if ((i >= 'A' && i <= 'Z') || (i >= 'a' && i <= 'z'))
        assertTrue (RFC1945Helper.isAlphaChar (i));
      else
        assertFalse (RFC1945Helper.isAlphaChar (i));
    assertFalse (RFC1945Helper.isAlphaChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testDigitChar ()
  {
    assertFalse (RFC1945Helper.isDigitChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if (i >= '0' && i <= '9')
        assertTrue (RFC1945Helper.isDigitChar (i));
      else
        assertFalse (RFC1945Helper.isDigitChar (i));
    assertFalse (RFC1945Helper.isDigitChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testControlChar ()
  {
    assertFalse (RFC1945Helper.isControlChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if (i < ' ' || i == 127)
        assertTrue (RFC1945Helper.isControlChar (i));
      else
        assertFalse (RFC1945Helper.isControlChar (i));
    assertFalse (RFC1945Helper.isControlChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testCRChar ()
  {
    assertFalse (RFC1945Helper.isCRChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if (i == '\r')
        assertTrue (RFC1945Helper.isCRChar (i));
      else
        assertFalse (RFC1945Helper.isCRChar (i));
    assertFalse (RFC1945Helper.isCRChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testLFChar ()
  {
    assertFalse (RFC1945Helper.isLFChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if (i == '\n')
        assertTrue (RFC1945Helper.isLFChar (i));
      else
        assertFalse (RFC1945Helper.isLFChar (i));
    assertFalse (RFC1945Helper.isLFChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testSpaceChar ()
  {
    assertFalse (RFC1945Helper.isSpaceChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if (i == ' ')
        assertTrue (RFC1945Helper.isSpaceChar (i));
      else
        assertFalse (RFC1945Helper.isSpaceChar (i));
    assertFalse (RFC1945Helper.isSpaceChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testTabChar ()
  {
    assertFalse (RFC1945Helper.isTabChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if (i == '\t')
        assertTrue (RFC1945Helper.isTabChar (i));
      else
        assertFalse (RFC1945Helper.isTabChar (i));
    assertFalse (RFC1945Helper.isTabChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testLinearWhitespaceChar ()
  {
    assertFalse (RFC1945Helper.isLinearWhitespaceChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if (i == ' ' || i == '\t' || i == '\r' || i == '\n')
        assertTrue (RFC1945Helper.isLinearWhitespaceChar (i));
      else
        assertFalse (RFC1945Helper.isLinearWhitespaceChar (i));
    assertFalse (RFC1945Helper.isSpaceChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testQuoteChar ()
  {
    assertFalse (RFC1945Helper.isQuoteChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if (i == '"')
        assertTrue (RFC1945Helper.isQuoteChar (i));
      else
        assertFalse (RFC1945Helper.isQuoteChar (i));
    assertFalse (RFC1945Helper.isQuoteChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testHexChar ()
  {
    assertFalse (RFC1945Helper.isHexChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if ((i >= 'A' && i <= 'F') || (i >= 'a' && i <= 'f') || (i >= '0' && i <= '9'))
        assertTrue (RFC1945Helper.isHexChar (i));
      else
        assertFalse (RFC1945Helper.isHexChar (i));
    assertFalse (RFC1945Helper.isHexChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testNonTokenChar ()
  {
    assertFalse (RFC1945Helper.isNonTokenChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if (i == '(' ||
          i == ')' ||
          i == '<' ||
          i == '>' ||
          i == '@' ||
          i == ',' ||
          i == ';' ||
          i == ':' ||
          i == '\\' ||
          i == '"' ||
          i == '/' ||
          i == '[' ||
          i == ']' ||
          i == '?' ||
          i == '=' ||
          i == '{' ||
          i == '}' ||
          i == ' ' ||
          i == '\t')
        assertTrue (RFC1945Helper.isNonTokenChar (i));
      else
        assertFalse (RFC1945Helper.isNonTokenChar (i));
    assertFalse (RFC1945Helper.isNonTokenChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testTokenChar ()
  {
    assertFalse (RFC1945Helper.isTokenChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if (!RFC1945Helper.isControlChar (i) && !RFC1945Helper.isNonTokenChar (i))
        assertTrue (RFC1945Helper.isTokenChar (i));
      else
        assertFalse (RFC1945Helper.isTokenChar (i));
    assertFalse (RFC1945Helper.isTokenChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testIsToken ()
  {
    assertFalse (RFC1945Helper.isToken ((String) null));
    assertFalse (RFC1945Helper.isToken ((char []) null));
    assertFalse (RFC1945Helper.isToken (CGlobal.EMPTY_CHAR_ARRAY));
    assertFalse (RFC1945Helper.isToken (new char [10]));
    assertFalse (RFC1945Helper.isToken (""));
    assertFalse (RFC1945Helper.isToken (" "));
    assertFalse (RFC1945Helper.isToken ("bla bla"));
    assertFalse (RFC1945Helper.isToken ("(bla"));
    assertFalse (RFC1945Helper.isToken ("bl(a"));
    assertFalse (RFC1945Helper.isToken ("bl)a"));
    assertFalse (RFC1945Helper.isToken ("bl<a"));
    assertFalse (RFC1945Helper.isToken ("bl>a"));
    assertTrue (RFC1945Helper.isToken ("bla"));
    assertTrue (RFC1945Helper.isToken ("bla_foo_fasel"));
    assertTrue (RFC1945Helper.isToken ("0123435678"));
  }

  @Test
  public void testTextChar ()
  {
    assertFalse (RFC1945Helper.isTextChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if (!RFC1945Helper.isControlChar (i) ||
          RFC1945Helper.isCRChar (i) ||
          RFC1945Helper.isLFChar (i) ||
          RFC1945Helper.isTabChar (i) ||
          RFC1945Helper.isSpaceChar (i))
        assertTrue (RFC1945Helper.isTextChar (i));
      else
        assertFalse (RFC1945Helper.isTextChar (i));
    // Any other octet is valid!
    assertTrue (RFC1945Helper.isTextChar (RFC1945Helper.MAX_INDEX + 1));
    assertTrue (RFC1945Helper.isTextChar (255));
    assertFalse (RFC1945Helper.isTextChar (256));
  }

  @Test
  public void testCommentChar ()
  {
    assertFalse (RFC1945Helper.isCommentChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if ((!RFC1945Helper.isControlChar (i) && i != RFC1945Helper.COMMENT_BEGIN && i != RFC1945Helper.COMMENT_END) ||
          RFC1945Helper.isCRChar (i) ||
          RFC1945Helper.isLFChar (i) ||
          RFC1945Helper.isTabChar (i) ||
          RFC1945Helper.isSpaceChar (i))
        assertTrue (RFC1945Helper.isCommentChar (i));
      else
        assertFalse (RFC1945Helper.isCommentChar (i));
    // Any other octet is valid!
    assertTrue (RFC1945Helper.isCommentChar (RFC1945Helper.MAX_INDEX + 1));
    assertTrue (RFC1945Helper.isCommentChar (255));
    assertFalse (RFC1945Helper.isCommentChar (256));
  }

  @Test
  public void testIsComment ()
  {
    assertFalse (RFC1945Helper.isComment ((String) null));
    assertFalse (RFC1945Helper.isComment ((char []) null));
    assertFalse (RFC1945Helper.isComment (CGlobal.EMPTY_CHAR_ARRAY));
    assertFalse (RFC1945Helper.isComment (new char [10]));
    assertFalse (RFC1945Helper.isComment (""));
    assertFalse (RFC1945Helper.isComment (" "));
    assertFalse (RFC1945Helper.isComment ("bla bla"));
    assertFalse (RFC1945Helper.isComment ("(bla"));
    assertFalse (RFC1945Helper.isComment ("(bl\u0000a)"));
    assertFalse (RFC1945Helper.isComment ("(bl(a)"));
    assertFalse (RFC1945Helper.isComment ("(bl)a)"));
    assertFalse (RFC1945Helper.isComment (" (bla)"));
    assertFalse (RFC1945Helper.isComment ("(bla) "));
    assertTrue (RFC1945Helper.isComment ("(bla)"));
    assertTrue (RFC1945Helper.isComment ("(bla foo fasel)"));
  }

  @Test
  public void testQuotedTextChar ()
  {
    assertFalse (RFC1945Helper.isQuotedTextChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if ((!RFC1945Helper.isControlChar (i) &&
           i != RFC1945Helper.QUOTEDTEXT_BEGIN &&
           i != RFC1945Helper.QUOTEDTEXT_END) ||
          RFC1945Helper.isCRChar (i) ||
          RFC1945Helper.isLFChar (i) ||
          RFC1945Helper.isTabChar (i) ||
          RFC1945Helper.isSpaceChar (i))
        assertTrue (Integer.toHexString (i), RFC1945Helper.isQuotedTextChar (i));
      else
        assertFalse (RFC1945Helper.isQuotedTextChar (i));
    assertFalse (RFC1945Helper.isQuotedTextChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testIsQuotedText ()
  {
    assertFalse (RFC1945Helper.isQuotedText ((String) null));
    assertFalse (RFC1945Helper.isQuotedText ((char []) null));
    assertFalse (RFC1945Helper.isQuotedText (CGlobal.EMPTY_CHAR_ARRAY));
    assertFalse (RFC1945Helper.isQuotedText (new char [10]));
    assertFalse (RFC1945Helper.isQuotedText (""));
    assertFalse (RFC1945Helper.isQuotedText (" "));
    assertFalse (RFC1945Helper.isQuotedText ("bla bla"));
    assertFalse (RFC1945Helper.isQuotedText ("\"bla"));
    assertFalse (RFC1945Helper.isQuotedText ("\"bl\u0000a\""));
    assertFalse (RFC1945Helper.isQuotedText ("\"bl\"a\""));
    assertFalse (RFC1945Helper.isQuotedText (" \"bla\""));
    assertFalse (RFC1945Helper.isQuotedText ("\"bla\" "));
    assertTrue (RFC1945Helper.isQuotedText ("\"bla\""));
    assertTrue (RFC1945Helper.isQuotedText ("\"bla foo fasel\""));
  }

  @Test
  public void testIsQuotedTextContent ()
  {
    assertFalse (RFC1945Helper.isQuotedTextContent ((String) null));
    assertFalse (RFC1945Helper.isQuotedTextContent ((char []) null));
    assertTrue (RFC1945Helper.isQuotedTextContent (CGlobal.EMPTY_CHAR_ARRAY));
    assertFalse (RFC1945Helper.isQuotedTextContent (new char [10]));
    assertTrue (RFC1945Helper.isQuotedTextContent (""));
    assertTrue (RFC1945Helper.isQuotedTextContent (" "));
    assertTrue (RFC1945Helper.isQuotedTextContent ("bla bla"));
    assertTrue (RFC1945Helper.isQuotedTextContent ("bla"));
    assertFalse (RFC1945Helper.isQuotedTextContent ("bl\u0000a"));
    assertFalse (RFC1945Helper.isQuotedTextContent ("bl\"a"));
    assertTrue (RFC1945Helper.isQuotedTextContent (" bla"));
    assertTrue (RFC1945Helper.isQuotedTextContent ("bla "));
    assertTrue (RFC1945Helper.isQuotedTextContent ("bla"));
    assertTrue (RFC1945Helper.isQuotedTextContent ("bla foo fasel"));
  }

  @Test
  public void testReservedChar ()
  {
    assertFalse (RFC1945Helper.isReservedChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if (i == ';' || i == '/' || i == '?' || i == ':' || i == '@' || i == '&' || i == '=' || i == '+')
        assertTrue (RFC1945Helper.isReservedChar (i));
      else
        assertFalse (RFC1945Helper.isReservedChar (i));
    assertFalse (RFC1945Helper.isReservedChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testExtraChar ()
  {
    assertFalse (RFC1945Helper.isExtraChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if (i == '!' || i == '*' || i == '\'' || i == '(' || i == ')' || i == ',')
        assertTrue (RFC1945Helper.isExtraChar (i));
      else
        assertFalse (RFC1945Helper.isExtraChar (i));
    assertFalse (RFC1945Helper.isExtraChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testSafeChar ()
  {
    assertFalse (RFC1945Helper.isSafeChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if (i == '$' || i == '-' || i == '_' || i == '.')
        assertTrue (RFC1945Helper.isSafeChar (i));
      else
        assertFalse (RFC1945Helper.isSafeChar (i));
    assertFalse (RFC1945Helper.isSafeChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testUnsafeChar ()
  {
    assertFalse (RFC1945Helper.isUnsafeChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if (RFC1945Helper.isControlChar (i) || i == ' ' || i == '"' || i == '#' || i == '%' || i == '<' || i == '>')
        assertTrue (RFC1945Helper.isUnsafeChar (i));
      else
        assertFalse (RFC1945Helper.isUnsafeChar (i));
    assertFalse (RFC1945Helper.isUnsafeChar (RFC1945Helper.MAX_INDEX + 1));
  }

  @Test
  public void testNationalChar ()
  {
    assertFalse (RFC1945Helper.isNationalChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if (RFC1945Helper.isAlphaChar (i) ||
          RFC1945Helper.isDigitChar (i) ||
          RFC1945Helper.isReservedChar (i) ||
          RFC1945Helper.isExtraChar (i) ||
          RFC1945Helper.isSafeChar (i) ||
          RFC1945Helper.isUnsafeChar (i))
        assertFalse (RFC1945Helper.isNationalChar (i));
      else
        assertTrue (RFC1945Helper.isNationalChar (i));
    // Any other octet is valid!
    assertTrue (RFC1945Helper.isNationalChar (RFC1945Helper.MAX_INDEX + 1));
    assertTrue (RFC1945Helper.isNationalChar (255));
    assertFalse (RFC1945Helper.isNationalChar (256));
  }

  @Test
  public void testUnreservedChar ()
  {
    assertFalse (RFC1945Helper.isUnreservedChar (RFC1945Helper.MIN_INDEX - 1));
    for (int i = RFC1945Helper.MIN_INDEX; i <= RFC1945Helper.MAX_INDEX; ++i)
      if (RFC1945Helper.isAlphaChar (i) ||
          RFC1945Helper.isDigitChar (i) ||
          RFC1945Helper.isSafeChar (i) ||
          RFC1945Helper.isExtraChar (i) ||
          RFC1945Helper.isNationalChar (i))
        assertTrue (RFC1945Helper.isUnreservedChar (i));
      else
        assertFalse (RFC1945Helper.isUnreservedChar (i));
    // Any other octet is valid!
    assertTrue (RFC1945Helper.isUnreservedChar (RFC1945Helper.MAX_INDEX + 1));
    assertTrue (RFC1945Helper.isUnreservedChar (255));
    assertFalse (RFC1945Helper.isUnreservedChar (256));
  }
}
