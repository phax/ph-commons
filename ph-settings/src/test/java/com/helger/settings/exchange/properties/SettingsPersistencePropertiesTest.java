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
package com.helger.settings.exchange.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Period;
import java.time.ZonedDateTime;

import org.junit.Test;

import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.string.StringParser;
import com.helger.settings.ISettings;
import com.helger.settings.Settings;

public final class SettingsPersistencePropertiesTest
{
  @Test
  public void testViceVersaConversion () throws UnsupportedEncodingException
  {
    // Name is important!
    final ISettings aSrc = new Settings ("anonymous");
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

    final SettingsPersistenceProperties aSPP = new SettingsPersistenceProperties ();
    final String sSrc = aSPP.writeSettings (aSrc);
    assertNotNull (sSrc);

    // The created object is different, because now all values are String typed!
    final ISettings aDst1 = aSPP.readSettings (sSrc);
    assertNotNull (aDst1);

    // Reading the String typed version again should result in the same object
    final ISettings aDst2 = aSPP.readSettings (aSPP.writeSettings (aDst1));
    assertNotNull (aDst2);
    assertEquals (aDst1, aDst2);

    assertNotNull (aDst2.getAsLocalDate ("fieldxa"));
    assertNotNull (aDst2.getAsLocalTime ("fieldxb"));
    assertNotNull (aDst2.getAsLocalDateTime ("fieldxc"));
    assertNotNull (aDst2.getConvertedValue ("fieldxd", ZonedDateTime.class));
    assertNotNull (aDst2.getConvertedValue ("fieldxe", Duration.class));
    assertNotNull (aDst2.getConvertedValue ("fieldxf", Period.class));
  }
}
