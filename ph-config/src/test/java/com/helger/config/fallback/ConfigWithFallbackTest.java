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
package com.helger.config.fallback;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.base.numeric.BigHelper;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.config.source.res.ConfigurationSourceJson;

/**
 * Test class for class {@link ConfigWithFallback}
 *
 * @author Philip Helger
 */
public final class ConfigWithFallbackTest
{
  private static final ConfigurationSourceJson CS1 = new ConfigurationSourceJson (17,
                                                                                  new FileSystemResource ("src/test/resources/application.json"));

  @Test
  public void testGetAsConfiguredValueOrFallback ()
  {
    final ConfigWithFallback aConfig = new ConfigWithFallback (CS1);
    aConfig.setOutdatedNotifier ( (sOld, sNew) -> {
      assertEquals ("element0", sOld);
      assertEquals ("element0-new", sNew);
    });
    assertEquals ("from-application-json0",
                  aConfig.getConfiguredValueOrFallback ("element0", "element0-new").getValue ());
    assertEquals ("from-application-json0",
                  aConfig.getConfiguredValueOrFallback ("element0-new", "element0").getValue ());
    assertNull (aConfig.getConfiguredValueOrFallback ("foo", "bla"));
  }

  @Test
  public void testGetAsStringOrFallback ()
  {
    final ConfigWithFallback aConfig = new ConfigWithFallback (CS1);
    aConfig.setOutdatedNotifier ( (sOld, sNew) -> {
      assertEquals ("element0", sOld);
      assertEquals ("element0-new", sNew);
    });
    assertEquals ("from-application-json0", aConfig.getAsStringOrFallback ("element0", "element0-new"));
    assertEquals ("from-application-json0", aConfig.getAsStringOrFallback ("element0-new", "element0"));
    assertNull (aConfig.getAsStringOrFallback ("foo", "bla"));
  }

  @Test
  public void testGetAsBigDecimalOrFallback ()
  {
    final ConfigWithFallback aConfig = new ConfigWithFallback (CS1);
    aConfig.setOutdatedNotifier ( (sOld, sNew) -> {
      assertEquals ("dbl", sOld);
      assertEquals ("dbl-new", sNew);
    });
    assertEquals (BigHelper.toBigDecimal (123.45678), aConfig.getAsBigDecimalOrFallback ("dbl", "dbl-new"));
    assertEquals (BigHelper.toBigDecimal (123.45678), aConfig.getAsBigDecimalOrFallback ("dbl-new", "dbl"));
    assertNull (aConfig.getAsBigDecimalOrFallback ("foo", "bla"));
  }

  @Test
  public void testGetAsIntOrFallback ()
  {
    final ConfigWithFallback aConfig = new ConfigWithFallback (CS1);
    aConfig.setOutdatedNotifier ( (sOld, sNew) -> {
      assertEquals ("int", sOld);
      assertEquals ("int-new", sNew);
    });
    assertEquals (123456, aConfig.getAsIntOrFallback ("int", -1, -1, "int-new"));
    assertEquals (123456, aConfig.getAsIntOrFallback ("int-new", -1, -1, "int"));
    assertEquals (-1, aConfig.getAsIntOrFallback ("foo", -2, -1, "bla"));
  }

  @Test
  public void testGetAsLongOrFallback ()
  {
    final ConfigWithFallback aConfig = new ConfigWithFallback (CS1);
    aConfig.setOutdatedNotifier ( (sOld, sNew) -> {
      assertEquals ("int", sOld);
      assertEquals ("int-new", sNew);
    });
    assertEquals (123456, aConfig.getAsLongOrFallback ("int", -1, -1, "int-new"));
    assertEquals (123456, aConfig.getAsLongOrFallback ("int-new", -1, -1, "int"));
    assertEquals (-1, aConfig.getAsLongOrFallback ("foo", -2, -1, "bla"));
  }

  @Test
  public void testGetAsCharArrayOrFallback ()
  {
    final ConfigWithFallback aConfig = new ConfigWithFallback (CS1);
    aConfig.setOutdatedNotifier ( (sOld, sNew) -> {
      assertEquals ("chararray", sOld);
      assertEquals ("chararray-new", sNew);
    });
    final char [] aExpected = "from-application-to-chararray".toCharArray ();
    assertArrayEquals (aExpected, aConfig.getAsCharArrayOrFallback ("chararray", "chararray-new"));
    assertArrayEquals (aExpected, aConfig.getAsCharArrayOrFallback ("chararray-new", "chararray"));
    assertNull (aConfig.getAsCharArrayOrFallback ("foo", "bla"));
  }
}
