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
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import org.junit.Test;

import com.helger.base.CGlobal;

/**
 * Test class for the remaining methods of {@link StringHelper} and
 * {@link StringParser}.
 *
 * @author Philip Helger
 */
public final class StringHelperGapsTest
{
  private static final Locale L = Locale.US;

  @Test
  public void testGetIndexOfIgnoreCase ()
  {
    assertEquals (1, StringHelper.getIndexOfIgnoreCase ("aBcBd", "b", L));
    assertEquals (-1, StringHelper.getIndexOfIgnoreCase ("abc", "x", L));
    assertEquals (-1, StringHelper.getIndexOfIgnoreCase (null, "b", L));
    assertEquals (-1, StringHelper.getIndexOfIgnoreCase ("abc", null, L));
    // The search String is longer than the text
    assertEquals (-1, StringHelper.getIndexOfIgnoreCase ("a", "abc", L));

    assertEquals (3, StringHelper.getIndexOfIgnoreCase ("aBcBd", 2, "b", L));
    assertEquals (-1, StringHelper.getIndexOfIgnoreCase (null, 2, "b", L));
    assertEquals (-1, StringHelper.getIndexOfIgnoreCase ("aBcBd", 2, null, L));

    assertEquals (1, StringHelper.getIndexOfIgnoreCase ("aBcBd", 'b', L));
    assertEquals (-1, StringHelper.getIndexOfIgnoreCase ("abc", 'x', L));
    assertEquals (-1, StringHelper.getIndexOfIgnoreCase (null, 'b', L));
    assertEquals (-1, StringHelper.getIndexOfIgnoreCase ("", 'b', L));

    assertEquals (3, StringHelper.getIndexOfIgnoreCase ("aBcBd", 2, 'b', L));
    assertEquals (-1, StringHelper.getIndexOfIgnoreCase (null, 2, 'b', L));
  }

  @Test
  public void testGetLastIndexOfIgnoreCase ()
  {
    assertEquals (3, StringHelper.getLastIndexOfIgnoreCase ("aBcBd", "b", L));
    assertEquals (-1, StringHelper.getLastIndexOfIgnoreCase ("abc", "x", L));
    assertEquals (-1, StringHelper.getLastIndexOfIgnoreCase (null, "b", L));
    assertEquals (-1, StringHelper.getLastIndexOfIgnoreCase ("abc", null, L));
    assertEquals (-1, StringHelper.getLastIndexOfIgnoreCase ("a", "abc", L));

    // Searching backwards from index 2
    assertEquals (1, StringHelper.getLastIndexOfIgnoreCase ("aBcBd", 2, "b", L));
    assertEquals (-1, StringHelper.getLastIndexOfIgnoreCase (null, 2, "b", L));

    assertEquals (3, StringHelper.getLastIndexOfIgnoreCase ("aBcBd", 'b', L));
    assertEquals (-1, StringHelper.getLastIndexOfIgnoreCase ("abc", 'x', L));
    assertEquals (-1, StringHelper.getLastIndexOfIgnoreCase (null, 'b', L));
    assertEquals (-1, StringHelper.getLastIndexOfIgnoreCase ("", 'b', L));

    assertEquals (1, StringHelper.getLastIndexOfIgnoreCase ("aBcBd", 2, 'b', L));
    assertEquals (-1, StringHelper.getLastIndexOfIgnoreCase (null, 2, 'b', L));
  }

  @Test
  public void testGetConcatenatedOnDemand ()
  {
    assertEquals ("ab", StringHelper.getConcatenatedOnDemand ("a", "b"));
    assertEquals ("a", StringHelper.getConcatenatedOnDemand ("a", null));
    assertEquals ("b", StringHelper.getConcatenatedOnDemand (null, "b"));
    assertEquals ("", StringHelper.getConcatenatedOnDemand (null, null));
    assertEquals ("a", StringHelper.getConcatenatedOnDemand ("a", ""));
    assertEquals ("b", StringHelper.getConcatenatedOnDemand ("", "b"));

    assertEquals ("a-b", StringHelper.getConcatenatedOnDemand ("a", "-", "b"));
    assertEquals ("a", StringHelper.getConcatenatedOnDemand ("a", "-", null));
    assertEquals ("b", StringHelper.getConcatenatedOnDemand (null, "-", "b"));
    assertEquals ("", StringHelper.getConcatenatedOnDemand (null, "-", null));
    assertEquals ("ab", StringHelper.getConcatenatedOnDemand ("a", null, "b"));
  }

  @Test
  public void testParseBool ()
  {
    assertTrue (StringParser.parseBool ("true"));
    assertTrue (StringParser.parseBool ("TRUE"));
    assertFalse (StringParser.parseBool ("false"));
    assertFalse (StringParser.parseBool ((String) null));
    assertTrue (StringParser.parseBool ("bla", true));
    assertFalse (StringParser.parseBool ("bla", false));

    assertTrue (StringParser.parseBool ((Object) "true"));
    assertFalse (StringParser.parseBool ((Object) null));
    assertTrue (StringParser.parseBool ((Object) Boolean.TRUE));
    assertTrue (StringParser.parseBool ((Object) null, true));
  }

  @Test
  public void testParseShort ()
  {
    assertEquals (17, StringParser.parseShort ("17", (short) 0));
    assertEquals (0, StringParser.parseShort ("bla", (short) 0));
    assertEquals (0, StringParser.parseShort ((String) null, (short) 0));
    assertEquals (255, StringParser.parseShort ("FF", 16, (short) 0));

    assertEquals (17, StringParser.parseShort ((Object) "17", (short) 0));
    assertEquals (0, StringParser.parseShort ((Object) null, (short) 0));
    assertEquals (255, StringParser.parseShort ((Object) "FF", 16, (short) 0));

    assertEquals (Short.valueOf ((short) 17), StringParser.parseShortObj ((Object) "17"));
    assertNull (StringParser.parseShortObj ((Object) "bla"));
    assertEquals (Short.valueOf ((short) 2), StringParser.parseShortObj ((Object) "bla", Short.valueOf ((short) 2)));
    assertEquals (Short.valueOf ((short) 17), StringParser.parseShortObj ("17"));
    assertEquals (Short.valueOf ((short) 2), StringParser.parseShortObj ("bla", Short.valueOf ((short) 2)));
  }

  @Test
  public void testParseBigDecimal ()
  {
    assertEquals (new BigDecimal ("1.5"), StringParser.parseBigDecimal ("1.5"));
    assertNull (StringParser.parseBigDecimal ("bla"));
    assertNull (StringParser.parseBigDecimal ((String) null));
    assertEquals (BigDecimal.ONE, StringParser.parseBigDecimal ("bla", BigDecimal.ONE));

    // With scale and rounding mode
    assertEquals (new BigDecimal ("1.2"), StringParser.parseBigDecimal ("1.25", 1, RoundingMode.DOWN));
    assertEquals (new BigDecimal ("1.3"), StringParser.parseBigDecimal ("1.25", 1, RoundingMode.HALF_UP));
    assertNull (StringParser.parseBigDecimal ("bla", 1, RoundingMode.HALF_UP));
    assertEquals (BigDecimal.ONE, StringParser.parseBigDecimal ("bla", 1, RoundingMode.HALF_UP, BigDecimal.ONE));
  }

  @Test
  public void testCGlobal ()
  {
    // Just to ensure the class is loaded
    assertEquals (-1, CGlobal.ILLEGAL_UINT);
  }
}
