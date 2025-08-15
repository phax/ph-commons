/*
 * Copyright (C) 2015-2025 Philip Helger (www.helger.com)
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
package com.helger.config.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import com.helger.base.equals.EqualsHelper;
import com.helger.base.numeric.BigHelper;
import com.helger.base.system.SystemProperties;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.config.Config;
import com.helger.config.IConfig;
import com.helger.config.json.source.ConfigurationSourceJson;
import com.helger.config.source.envvar.ConfigurationSourceEnvVar;
import com.helger.config.source.resource.IConfigurationSourceResource;
import com.helger.config.source.resource.properties.ConfigurationSourceProperties;
import com.helger.config.source.sysprop.ConfigurationSourceSystemProperty;
import com.helger.config.value.IConfigurationValueProvider;
import com.helger.io.resource.ClassPathResource;
import com.helger.typeconvert.TypeConverterException;

import jakarta.annotation.Nonnull;

/**
 * Test class for class {@link ConfigFactoryJson}.
 *
 * @author Philip Helger
 */
public final class ConfigFactoryJsonTest
{
  private void _testDefault (@Nonnull final IConfig aConfig)
  {
    assertEquals ("from-private-application-json0", aConfig.getAsString ("element0"));
    assertEquals ("from-private-application-properties1", aConfig.getAsString ("element1"));
    assertEquals ("from-application-json2", aConfig.getAsString ("element2"));
    assertEquals ("from-application-properties3", aConfig.getAsString ("element3"));
    assertEquals ("from-reference-properties4", aConfig.getAsString ("element4"));
    assertNull (aConfig.getAsString ("element5"));
    assertTrue (aConfig.getAsBoolean ("element.boolean.t", false));
    assertFalse (aConfig.getAsBoolean ("element.boolean.f", true));

    assertEquals (-1, aConfig.getAsInt ("element1", -1));
    try
    {
      aConfig.getAsInt ("element1");
      fail ();
    }
    catch (final TypeConverterException ex)
    {
      // Expected
    }

    assertEquals (123456, aConfig.getAsInt ("int"));
    assertEquals ("123456", aConfig.getAsString ("int"));

    assertTrue (EqualsHelper.equals (BigHelper.toBigDecimal (123.45678), aConfig.getAsBigDecimal ("dbl")));
    assertEquals ("123.45678", aConfig.getAsString ("dbl"));

    // get all
    final ICommonsList <IConfigurationValueProvider> aCVPs = new CommonsArrayList <> ();
    aConfig.forEachConfigurationValueProvider ( (cvp, prio) -> aCVPs.add (cvp));
    assertEquals (7, aCVPs.size ());
    assertTrue (aCVPs.get (0) instanceof ConfigurationSourceSystemProperty);
    assertTrue (aCVPs.get (1) instanceof ConfigurationSourceEnvVar);
    assertTrue (aCVPs.get (2) instanceof ConfigurationSourceJson);
    assertTrue (((ConfigurationSourceJson) aCVPs.get (2)).getResource ()
                                                         .getPath ()
                                                         .endsWith ("private-application.json"));
    assertTrue (aCVPs.get (3) instanceof ConfigurationSourceProperties);
    assertTrue (((ConfigurationSourceProperties) aCVPs.get (3)).getResource ()
                                                               .getPath ()
                                                               .endsWith ("private-application.properties"));
    assertTrue (aCVPs.get (4) instanceof ConfigurationSourceJson);
    assertTrue (((ConfigurationSourceJson) aCVPs.get (4)).getResource ().getPath ().endsWith ("application.json"));
    assertTrue (aCVPs.get (5) instanceof ConfigurationSourceProperties);
    assertTrue (((ConfigurationSourceProperties) aCVPs.get (5)).getResource ()
                                                               .getPath ()
                                                               .endsWith ("application.properties"));
    assertTrue (aCVPs.get (6) instanceof ConfigurationSourceProperties);
    assertTrue (((ConfigurationSourceProperties) aCVPs.get (6)).getResource ()
                                                               .getPath ()
                                                               .endsWith ("reference.properties"));

    for (final IConfigurationValueProvider aCVP : aCVPs)
      if (aCVP instanceof IConfigurationSourceResource)
        assertNotNull (((IConfigurationSourceResource) aCVP).getAllConfigItems ());
  }

  @Test
  public void testDefault ()
  {
    _testDefault (Config.create (ConfigFactoryJson.createDefaultValueProvider ()));
  }

  @Test
  public void testSysPropConfigResourceJson ()
  {
    SystemProperties.setPropertyValue ("config.resource", "sysprops/resource.json");
    try
    {
      final IConfig aConfig = Config.create (ConfigFactoryJson.createDefaultValueProvider ());
      assertEquals ("from-sysprop-resource-json0", aConfig.getAsString ("element0"));
      assertEquals ("from-private-application-properties1", aConfig.getAsString ("element1"));
      assertEquals ("from-application-json2", aConfig.getAsString ("element2"));
      assertEquals ("from-application-properties3", aConfig.getAsString ("element3"));
      assertEquals ("from-reference-properties4", aConfig.getAsString ("element4"));
      assertNull (aConfig.getAsString ("element5"));
    }
    finally
    {
      SystemProperties.removePropertyValue ("config.resource");
    }
  }

  @Test
  public void testSysPropFileResourceJson ()
  {
    SystemProperties.setPropertyValue ("config.file",
                                       new File ("src/test/resources/sysprops/file.json").getAbsolutePath ());
    try
    {
      final IConfig aConfig = Config.create (ConfigFactoryJson.createDefaultValueProvider ());
      assertEquals ("from-sysprop-file-json0", aConfig.getAsString ("element0"));
      assertEquals ("from-private-application-properties1", aConfig.getAsString ("element1"));
      assertEquals ("from-application-json2", aConfig.getAsString ("element2"));
      assertEquals ("from-application-properties3", aConfig.getAsString ("element3"));
      assertEquals ("from-reference-properties4", aConfig.getAsString ("element4"));
      assertNull (aConfig.getAsString ("element5"));
    }
    finally
    {
      SystemProperties.removePropertyValue ("config.file");
    }
  }

  @Test
  public void testSysPropConfigUrlJson ()
  {
    SystemProperties.setPropertyValue ("config.url",
                                       new ClassPathResource ("sysprops/url.json").getAsURL ().toExternalForm ());
    try
    {
      final IConfig aConfig = Config.create (ConfigFactoryJson.createDefaultValueProvider ());
      assertEquals ("from-sysprop-url-json0", aConfig.getAsString ("element0"));
      assertEquals ("from-private-application-properties1", aConfig.getAsString ("element1"));
      assertEquals ("from-application-json2", aConfig.getAsString ("element2"));
      assertEquals ("from-application-properties3", aConfig.getAsString ("element3"));
      assertEquals ("from-reference-properties4", aConfig.getAsString ("element4"));
      assertNull (aConfig.getAsString ("element5"));
    }
    finally
    {
      SystemProperties.removePropertyValue ("config.url");
    }
  }

}
