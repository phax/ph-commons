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
package com.helger.base.codec;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link RFC5234Helper}.
 *
 * @author Philip Helger
 */
public final class RFC5234HelperTest
{
  @Test
  public void testIsAlpha ()
  {
    for (int i = 'a'; i <= 'z'; ++i)
      assertTrue (RFC5234Helper.isAlpha (i));
    for (int i = 'A'; i <= 'Z'; ++i)
      assertTrue (RFC5234Helper.isAlpha (i));
    assertFalse (RFC5234Helper.isAlpha (0));
  }

  @Test
  public void testIsBit ()
  {
    assertTrue (RFC5234Helper.isBit ('0'));
    assertTrue (RFC5234Helper.isBit ('1'));
    assertFalse (RFC5234Helper.isBit (0));
  }

  @Test
  public void testIsChar ()
  {
    for (int i = 1; i <= 0x7f; ++i)
      assertTrue (RFC5234Helper.isChar (i));
    assertFalse (RFC5234Helper.isChar (0));
  }

  @Test
  public void testIsCR ()
  {
    assertTrue (RFC5234Helper.isCR ('\r'));
    assertFalse (RFC5234Helper.isCR (0));
  }

  @Test
  public void testIsCtl ()
  {
    for (int i = 0; i < 0x1f; ++i)
      assertTrue (RFC5234Helper.isCtl (i));
    assertTrue (RFC5234Helper.isCtl (0x7f));
    assertFalse (RFC5234Helper.isCtl (0x20));
  }

  @Test
  public void testIsDigit ()
  {
    for (int i = '0'; i < '9'; ++i)
      assertTrue (RFC5234Helper.isDigit (i));
    assertFalse (RFC5234Helper.isDigit (0));
  }

  @Test
  public void testIsDQuote ()
  {
    assertTrue (RFC5234Helper.isDQuote ('"'));
    assertFalse (RFC5234Helper.isDQuote (0));
  }

  @Test
  public void testIsHexDigit ()
  {
    for (int i = '0'; i < '9'; ++i)
      assertTrue (RFC5234Helper.isHexDigit (i));
    for (int i = 'A'; i < 'F'; ++i)
      assertTrue (RFC5234Helper.isHexDigit (i));
    for (int i = 'a'; i < 'f'; ++i)
      assertTrue (RFC5234Helper.isHexDigit (i));
    assertFalse (RFC5234Helper.isHexDigit (0));
  }

  @Test
  public void testIsHTab ()
  {
    assertTrue (RFC5234Helper.isHTab (0x09));
    assertFalse (RFC5234Helper.isHTab (0));
  }

  @Test
  public void testIsLF ()
  {
    assertTrue (RFC5234Helper.isLF ('\n'));
    assertFalse (RFC5234Helper.isLF (0));
  }

  @Test
  public void testIsOctet ()
  {
    for (int i = 0x00; i <= 0xff; ++i)
      assertTrue (RFC5234Helper.isOctet (i));
    assertFalse (RFC5234Helper.isOctet (-1));
    assertFalse (RFC5234Helper.isOctet (256));
  }

  @Test
  public void testIsSP ()
  {
    assertTrue (RFC5234Helper.isSP (0x20));
    assertFalse (RFC5234Helper.isSP (0));
  }

  @Test
  public void testIsVChar ()
  {
    for (int i = 0x21; i <= 0x7e; ++i)
      assertTrue (RFC5234Helper.isVChar (i));
    assertFalse (RFC5234Helper.isVChar (0));
  }

  @Test
  public void testIsWSP ()
  {
    assertTrue (RFC5234Helper.isWSP (0x09));
    assertTrue (RFC5234Helper.isWSP (0x20));
    assertFalse (RFC5234Helper.isWSP (0));
  }
}
