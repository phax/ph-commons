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
package com.helger.settings.exchange.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Period;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.string.StringParser;
import com.helger.commons.datetime.PDTFactory;
import com.helger.settings.ISettings;
import com.helger.settings.Settings;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.convert.MicroTypeConverter;
import com.helger.xml.microdom.serialize.MicroWriter;

/**
 * Test class for class {@link SettingsMicroDocumentConverter}.
 *
 * @author Philip Helger
 */
public final class SettingsMicroDocumentConverterTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (SettingsMicroDocumentConverterTest.class);

  @Test
  public void testConversionWithTypes () throws UnsupportedEncodingException
  {
    final Settings aSrc = new Settings ("myName");
    aSrc.putIn ("field1a", BigInteger.valueOf (1234));
    aSrc.putIn ("field1b", BigInteger.valueOf (-23423424));
    aSrc.putIn ("field2a", BigDecimal.valueOf (12.34));
    aSrc.putIn ("field2b", BigDecimal.valueOf (-2342.334599424));
    aSrc.putIn ("field3a", "My wonderbra string\n(incl newline)");
    aSrc.putIn ("field3b", "");
    aSrc.putIn ("field9a", Boolean.TRUE);
    aSrc.putIn ("field9b", StringParser.parseByteObj ("5"));
    aSrc.putIn ("field9c", Character.valueOf ('ä'));
    aSrc.putIn ("fieldxa", PDTFactory.getCurrentLocalDate ());
    aSrc.putIn ("fieldxb", PDTFactory.getCurrentLocalTime ());
    aSrc.putIn ("fieldxc", PDTFactory.getCurrentLocalDateTime ());
    aSrc.putIn ("fieldxd", PDTFactory.getCurrentZonedDateTime ());
    aSrc.putIn ("fieldxe", Duration.ofHours (5));
    aSrc.putIn ("fieldxf", Period.ofDays (3));
    aSrc.putIn ("fieldxg", "Any byte ärräy".getBytes (StandardCharsets.UTF_8.name ()));

    final Settings aNestedSettings = new Settings ("nestedSettings");
    aNestedSettings.putIn ("a", "b");
    aNestedSettings.putIn ("c", "d");
    aNestedSettings.putIn ("e", Clock.systemDefaultZone ().millis ());
    aSrc.putIn ("fieldxh", aNestedSettings);

    // null value
    aSrc.putIn ("fieldnull", null);

    // To XML
    final IMicroElement eSrcElement = MicroTypeConverter.convertToMicroElement (aSrc, "root");
    assertNotNull (eSrcElement);
    if (false)
      LOGGER.info (MicroWriter.getNodeAsString (eSrcElement));

    // From XML
    final ISettings aDst = MicroTypeConverter.convertToNative (eSrcElement, Settings.class);
    assertNotNull (aDst);

    // No longer true, because now all values are String
    if (false)
    {
      assertEquals (aSrc, aDst);

      // Compare list
      assertEquals (BigInteger.valueOf (1234), aDst.getValue ("field1a"));
    }
    else
    {
      assertEquals ("1234", aDst.getValue ("field1a"));
    }

    final ISettings aDst2 = new Settings (aDst.getName ());
    aDst2.putAllIn (aDst);
    assertEquals (aDst, aDst2);
    assertTrue (aDst2.putIn ("field3b", "doch was").isChanged ());
    assertFalse (aDst.equals (aDst2));
  }
}
