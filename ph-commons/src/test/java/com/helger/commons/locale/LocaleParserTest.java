/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.text.ChoiceFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.junit.Test;

import com.helger.commons.CGlobal;
import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.mock.CommonsAssert;
import com.helger.commons.string.StringParser;

/**
 * Test class for class {@link LocaleParser}.
 *
 * @author Philip Helger
 */
public final class LocaleParserTest extends AbstractCommonsTestCase
{
  @Test
  public void testParse ()
  {
    CommonsAssert.assertEquals (1.1, LocaleParser.parse ("1,1", L_DE).doubleValue ());
    CommonsAssert.assertEquals (1.1, LocaleParser.parse ("1.1", L_EN).doubleValue ());
    assertNull (LocaleParser.parse ("wir sitzen da und denken nach", L_DE));
    assertNull (LocaleParser.parse ("", L_DE));
    assertNull (LocaleParser.parse (null, L_DE));

    try
    {
      LocaleParser.parse ("1", (Locale) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      LocaleParser.parse ("1", (NumberFormat) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testParseDouble ()
  {
    CommonsAssert.assertEquals (1.1, LocaleParser.parseDouble ("1,1", L_DE, CGlobal.ILLEGAL_DOUBLE));
    CommonsAssert.assertEquals (1.1, LocaleParser.parseDouble ("1.1", L_EN, CGlobal.ILLEGAL_DOUBLE));
    CommonsAssert.assertEquals (CGlobal.ILLEGAL_DOUBLE,
                                LocaleParser.parseDouble ("und wir denken und denken", L_EN, CGlobal.ILLEGAL_DOUBLE));
  }

  @Test
  public void testParseInt ()
  {
    assertEquals (1000, LocaleParser.parseInt ("1.000", L_DE, CGlobal.ILLEGAL_UINT));
    assertEquals (1000, LocaleParser.parseInt ("1,000", L_EN, CGlobal.ILLEGAL_UINT));
    assertEquals (CGlobal.ILLEGAL_UINT, LocaleParser.parseInt ("... und denken", L_EN, CGlobal.ILLEGAL_UINT));
  }

  @Test
  public void testParseLong ()
  {
    assertEquals (1000000L, LocaleParser.parseLong ("1.000.000", L_DE, CGlobal.ILLEGAL_UINT));
    assertEquals (1000000L, LocaleParser.parseLong ("1,000,000", L_EN, CGlobal.ILLEGAL_UINT));
    assertEquals (CGlobal.ILLEGAL_UINT, LocaleParser.parseLong ("... und denken", L_EN, CGlobal.ILLEGAL_UINT));
  }

  @Test
  public void testParseBigDecimal ()
  {
    final BigDecimal aBD1M = StringParser.parseBigDecimal ("1000000");
    assertEquals (aBD1M, LocaleParser.parseBigDecimal ("1.000.000", L_DE, CGlobal.BIGDEC_MINUS_ONE));
    assertEquals (aBD1M, LocaleParser.parseBigDecimal ("1,000,000", L_EN, CGlobal.BIGDEC_MINUS_ONE));
    assertEquals (aBD1M, LocaleParser.parseBigDecimal ("1,000,000", (DecimalFormat) NumberFormat.getInstance (L_EN)));
    assertEquals (new BigDecimal ("1234567.8901"),
                  LocaleParser.parseBigDecimal ("1.234.567,8901", L_DE, CGlobal.BIGDEC_MINUS_ONE));
    assertEquals (CGlobal.BIGDEC_MINUS_ONE,
                  LocaleParser.parseBigDecimal ("... und denken", L_EN, CGlobal.BIGDEC_MINUS_ONE));
    final ChoiceFormat aCF = new ChoiceFormat ("-1#negative|0#zero|1.0#one");
    assertEquals (BigDecimal.valueOf (0.0), LocaleParser.parseBigDecimal ("zero", aCF, CGlobal.BIGDEC_MINUS_ONE));

    try
    {
      LocaleParser.parseBigDecimal ("0", (DecimalFormat) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
