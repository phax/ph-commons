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

import java.math.BigInteger;

import org.junit.Test;

import com.helger.commons.mock.AbstractCommonsTestCase;

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
    assertEquals ("9.223.372.036.854.775.807",
                  LocaleFormatter.getFormatted (BigInteger.valueOf (Long.MAX_VALUE), L_DE));
    assertEquals ("9,223,372,036,854,775,807",
                  LocaleFormatter.getFormatted (BigInteger.valueOf (Long.MAX_VALUE), L_EN));
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
}
