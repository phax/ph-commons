/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
import java.math.BigInteger;
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
 * Test class for class {@link LocaleFormatter}.
 *
 * @author Philip Helger
 */
public final class LocaleFormatterTest extends AbstractCommonsTestCase
{
  @Test
  public void testGetFormatted_Double ()
  {
    assertEquals ("1,1", LocaleFormatter.getFormatted (1.1, L_DE));
    assertEquals ("1.1", LocaleFormatter.getFormatted (1.1, L_EN));
  }

  @Test
  public void testGetFormatted_Int ()
  {
    assertEquals ("1.000", LocaleFormatter.getFormatted (1000, L_DE));
    assertEquals ("1,000", LocaleFormatter.getFormatted (1000, L_EN));
  }

  @Test
  public void testGetFormatted_Long ()
  {
    assertEquals ("1.000.000", LocaleFormatter.getFormatted (1000000L, L_DE));
    assertEquals ("1,000,000", LocaleFormatter.getFormatted (1000000L, L_EN));
  }

  @Test
  public void testGetFormattedBigInteger ()
  {
    assertEquals ("9.223.372.036.854.775.807", LocaleFormatter.getFormatted (BigInteger.valueOf (Long.MAX_VALUE), L_DE));
    assertEquals ("9,223,372,036,854,775,807", LocaleFormatter.getFormatted (BigInteger.valueOf (Long.MAX_VALUE), L_EN));
    assertEquals ("9.223.372.036.854.775.808",
                  LocaleFormatter.getFormatted (BigInteger.valueOf (Long.MAX_VALUE).add (BigInteger.ONE), L_DE));
    assertEquals ("9,223,372,036,854,775,808",
                  LocaleFormatter.getFormatted (BigInteger.valueOf (Long.MAX_VALUE).add (BigInteger.ONE), L_EN));
  }

  @Test
  public void testGetFormattedPercent ()
  {
    assertEquals ("12%", LocaleFormatter.getFormattedPercent (0.123, L_DE));
    assertEquals ("12%", LocaleFormatter.getFormattedPercent (0.123, L_DE_AT));
    assertEquals ("12%", LocaleFormatter.getFormattedPercent (0.123, L_EN));
    assertEquals ("12%", LocaleFormatter.getFormattedPercent (0.123, L_EN_US));
    assertEquals ("12 %", LocaleFormatter.getFormattedPercent (0.123, L_FR_FR));
  }

  @Test
  public void testGetFormattedPercent_Scale ()
  {
    assertEquals ("12,3%", LocaleFormatter.getFormattedPercent (0.123, 1, L_DE));
    assertEquals ("12,3%", LocaleFormatter.getFormattedPercent (0.123, 1, L_DE_AT));
    assertEquals ("12.3%", LocaleFormatter.getFormattedPercent (0.123, 1, L_EN));
    assertEquals ("12.3%", LocaleFormatter.getFormattedPercent (0.123, 1, L_EN_US));
    assertEquals ("12,3 %", LocaleFormatter.getFormattedPercent (0.123, 1, L_FR_FR));
  }

  @Test
  public void testParse ()
  {
    CommonsAssert.assertEquals (1.1, LocaleFormatter.parse ("1,1", L_DE).doubleValue ());
    CommonsAssert.assertEquals (1.1, LocaleFormatter.parse ("1.1", L_EN).doubleValue ());
    assertNull (LocaleFormatter.parse ("wir sitzen da und denken nach", L_DE));
    assertNull (LocaleFormatter.parse ("", L_DE));
    assertNull (LocaleFormatter.parse (null, L_DE));

    try
    {
      LocaleFormatter.parse ("1", (Locale) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      LocaleFormatter.parse ("1", (NumberFormat) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testParseDouble ()
  {
    CommonsAssert.assertEquals (1.1, LocaleFormatter.parseDouble ("1,1", L_DE, CGlobal.ILLEGAL_DOUBLE));
    CommonsAssert.assertEquals (1.1, LocaleFormatter.parseDouble ("1.1", L_EN, CGlobal.ILLEGAL_DOUBLE));
    CommonsAssert.assertEquals (CGlobal.ILLEGAL_DOUBLE,
                           LocaleFormatter.parseDouble ("und wir denken und denken", L_EN, CGlobal.ILLEGAL_DOUBLE));
  }

  @Test
  public void testParseInt ()
  {
    assertEquals (1000, LocaleFormatter.parseInt ("1.000", L_DE, CGlobal.ILLEGAL_UINT));
    assertEquals (1000, LocaleFormatter.parseInt ("1,000", L_EN, CGlobal.ILLEGAL_UINT));
    assertEquals (CGlobal.ILLEGAL_UINT, LocaleFormatter.parseInt ("... und denken", L_EN, CGlobal.ILLEGAL_UINT));
  }

  @Test
  public void testParseLong ()
  {
    assertEquals (1000000L, LocaleFormatter.parseLong ("1.000.000", L_DE, CGlobal.ILLEGAL_UINT));
    assertEquals (1000000L, LocaleFormatter.parseLong ("1,000,000", L_EN, CGlobal.ILLEGAL_UINT));
    assertEquals (CGlobal.ILLEGAL_UINT, LocaleFormatter.parseLong ("... und denken", L_EN, CGlobal.ILLEGAL_UINT));
  }

  @Test
  public void testParseBigDecimal ()
  {
    final BigDecimal aBD1M = StringParser.parseBigDecimal ("1000000");
    assertEquals (aBD1M, LocaleFormatter.parseBigDecimal ("1.000.000", L_DE, CGlobal.BIGDEC_MINUS_ONE));
    assertEquals (aBD1M, LocaleFormatter.parseBigDecimal ("1,000,000", L_EN, CGlobal.BIGDEC_MINUS_ONE));
    assertEquals (aBD1M, LocaleFormatter.parseBigDecimal ("1,000,000", (DecimalFormat) NumberFormat.getInstance (L_EN)));
    assertEquals (new BigDecimal ("1234567.8901"),
                  LocaleFormatter.parseBigDecimal ("1.234.567,8901", L_DE, CGlobal.BIGDEC_MINUS_ONE));
    assertEquals (CGlobal.BIGDEC_MINUS_ONE,
                  LocaleFormatter.parseBigDecimal ("... und denken", L_EN, CGlobal.BIGDEC_MINUS_ONE));
    final ChoiceFormat aCF = new ChoiceFormat ("-1#negative|0#zero|1.0#one");
    assertEquals (BigDecimal.valueOf (0.0), LocaleFormatter.parseBigDecimal ("zero", aCF, CGlobal.BIGDEC_MINUS_ONE));

    try
    {
      LocaleFormatter.parseBigDecimal ("0", (DecimalFormat) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
