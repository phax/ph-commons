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
package com.helger.commons.text.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link ABNF}.
 *
 * @author Philip Helger
 */
public final class ABNFTest
{
  @Test
  public void testIsAlpha ()
  {
    for (int i = 'a'; i <= 'z'; ++i)
      assertTrue (ABNF.isAlpha (i));
    for (int i = 'A'; i <= 'Z'; ++i)
      assertTrue (ABNF.isAlpha (i));
    assertFalse (ABNF.isAlpha (0));
  }

  @Test
  public void testIsBit ()
  {
    assertTrue (ABNF.isBit ('0'));
    assertTrue (ABNF.isBit ('1'));
    assertFalse (ABNF.isBit (0));
  }

  @Test
  public void testIsChar ()
  {
    for (int i = 1; i <= 0x7f; ++i)
      assertTrue (ABNF.isChar (i));
    assertFalse (ABNF.isChar (0));
  }

  @Test
  public void testIsCR ()
  {
    assertTrue (ABNF.isCR ('\r'));
    assertFalse (ABNF.isCR (0));
  }

  @Test
  public void testIsCtl ()
  {
    for (int i = 0; i < 0x1f; ++i)
      assertTrue (ABNF.isCtl (i));
    assertTrue (ABNF.isCtl (0x7f));
    assertFalse (ABNF.isCtl (0x20));
  }

  @Test
  public void testIsDigit ()
  {
    for (int i = '0'; i < '9'; ++i)
      assertTrue (ABNF.isDigit (i));
    assertFalse (ABNF.isDigit (0));
  }

  @Test
  public void testIsDQuote ()
  {
    assertTrue (ABNF.isDQuote ('"'));
    assertFalse (ABNF.isDQuote (0));
  }

  @Test
  public void testIsHexDigit ()
  {
    for (int i = '0'; i < '9'; ++i)
      assertTrue (ABNF.isHexDigit (i));
    for (int i = 'A'; i < 'F'; ++i)
      assertTrue (ABNF.isHexDigit (i));
    for (int i = 'a'; i < 'f'; ++i)
      assertFalse (ABNF.isHexDigit (i));
    assertFalse (ABNF.isHexDigit (0));
  }

  @Test
  public void testIsHexDigitCaseInsensitive ()
  {
    for (int i = '0'; i < '9'; ++i)
      assertTrue (ABNF.isHexDigitCaseInsensitive (i));
    for (int i = 'A'; i < 'F'; ++i)
      assertTrue (ABNF.isHexDigitCaseInsensitive (i));
    for (int i = 'a'; i < 'f'; ++i)
      assertTrue (ABNF.isHexDigitCaseInsensitive (i));
    assertFalse (ABNF.isHexDigitCaseInsensitive (0));
  }

  @Test
  public void testIsHTab ()
  {
    assertTrue (ABNF.isHTab (0x09));
    assertFalse (ABNF.isHTab (0));
  }

  @Test
  public void testIsLF ()
  {
    assertTrue (ABNF.isLF ('\n'));
    assertFalse (ABNF.isLF (0));
  }

  @Test
  public void testIsOctet ()
  {
    for (int i = 0x00; i <= 0xff; ++i)
      assertTrue (ABNF.isOctet (i));
    assertFalse (ABNF.isOctet (-1));
    assertFalse (ABNF.isOctet (256));
  }

  @Test
  public void testIsSP ()
  {
    assertTrue (ABNF.isSP (0x20));
    assertFalse (ABNF.isSP (0));
  }

  @Test
  public void testIsVChar ()
  {
    for (int i = 0x21; i <= 0x7e; ++i)
      assertTrue (ABNF.isVChar (i));
    assertFalse (ABNF.isVChar (0));
  }

  @Test
  public void testIsWSP ()
  {
    assertTrue (ABNF.isWSP (0x09));
    assertTrue (ABNF.isWSP (0x20));
    assertFalse (ABNF.isWSP (0));
  }
}
