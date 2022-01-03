/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.helger.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.mutable.MutableInt;
import com.helger.config.source.MultiConfigurationValueProvider;
import com.helger.config.source.res.ConfigurationSourceJson;
import com.helger.config.value.IConfigurationValueProviderWithPriorityCallback;

/**
 * Test class for class {@link Config}
 *
 * @author Philip Helger
 */
public final class ConfigTest
{
  private static final ConfigurationSourceJson CS1 = new ConfigurationSourceJson (17,
                                                                                  new FileSystemResource ("src/test/resources/application.json"));
  private static final ConfigurationSourceJson CS2 = new ConfigurationSourceJson (41,
                                                                                  new FileSystemResource ("src/test/resources/private-application.json"));

  @Test
  public void testForEachOneConfigSource ()
  {

    final IConfig aConfig = new Config (CS1);
    assertEquals ("from-application-json0", aConfig.getAsString ("element0"));
    assertNull (aConfig.getAsString ("element5"));

    final MutableInt aCounter = new MutableInt (0);
    final IConfigurationValueProviderWithPriorityCallback aCallback = (aCVP, nPriority) -> {
      final int n = aCounter.getAndInc ();
      switch (n)
      {
        case 0:
          assertEquals (17, nPriority);
          break;
        default:
          throw new IllegalStateException ("Unsupported index");
      }
    };
    aConfig.forEachConfigurationValueProvider (aCallback);
  }

  @Test
  public void testForEachMultiConfigDefaultPriority ()
  {
    final IConfig aConfig = new Config (new MultiConfigurationValueProvider (CS1, CS2));
    assertEquals ("from-private-application-json0", aConfig.getAsString ("element0"));
    assertNull (aConfig.getAsString ("element5"));

    final MutableInt aCounter = new MutableInt (0);
    final IConfigurationValueProviderWithPriorityCallback aCallback = (aCVP, nPriority) -> {
      final int n = aCounter.getAndInc ();
      switch (n)
      {
        case 0:
          assertEquals (41, nPriority);
          break;
        case 1:
          assertEquals (17, nPriority);
          break;
        default:
          throw new IllegalStateException ("Unsupported index");
      }
    };
    aConfig.forEachConfigurationValueProvider (aCallback);
  }

  @Test
  public void testForEachMultiConfigCustomPriority ()
  {
    final MultiConfigurationValueProvider aMCVP = new MultiConfigurationValueProvider ();
    aMCVP.addConfigurationSource (CS1, 18);
    aMCVP.addConfigurationSource (CS2, 42);
    final IConfig aConfig = new Config (aMCVP);
    assertEquals ("from-private-application-json0", aConfig.getAsString ("element0"));
    assertNull (aConfig.getAsString ("element5"));

    final MutableInt aCounter = new MutableInt (0);
    final IConfigurationValueProviderWithPriorityCallback aCallback = (aCVP, nPriority) -> {
      final int n = aCounter.getAndInc ();
      switch (n)
      {
        case 0:
          assertEquals (42, nPriority);
          break;
        case 1:
          assertEquals (18, nPriority);
          break;
        default:
          throw new IllegalStateException ("Unsupported index");
      }
    };
    aConfig.forEachConfigurationValueProvider (aCallback);
  }
}
