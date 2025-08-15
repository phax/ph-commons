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
package com.helger.config.source.envvar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.base.system.EOperatingSystem;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.config.source.EConfigSourceType;

/**
 * Test class for class {@link ConfigurationSourceEnvVar}.
 *
 * @author Philip Helger
 */
public final class ConfigurationSourceEnvVarTest
{
  @Test
  public void testBasic ()
  {
    final ConfigurationSourceEnvVar c = new ConfigurationSourceEnvVar ();
    assertSame (EConfigSourceType.ENVIRONMENT_VARIABLE, c.getSourceType ());
    assertEquals (EConfigSourceType.ENVIRONMENT_VARIABLE.getDefaultPriority (), c.getPriority ());
    assertTrue (c.isInitializedAndUsable ());
    // It is not available on Travis
    if (EOperatingSystem.WINDOWS.isCurrentOS ())
      assertNotNull (c.getConfigurationValue ("OS"));
    assertNull (c.getConfigurationValue ("I really don't know that env var!"));

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (c, new ConfigurationSourceEnvVar ());
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (c, new ConfigurationSourceEnvVar (1234));
  }

  @Test
  public void testDifferentPrio ()
  {
    final ConfigurationSourceEnvVar c = new ConfigurationSourceEnvVar (1234);
    assertSame (EConfigSourceType.ENVIRONMENT_VARIABLE, c.getSourceType ());
    assertEquals (1234, c.getPriority ());
    assertTrue (c.isInitializedAndUsable ());
    // It is not available on Travis
    if (EOperatingSystem.WINDOWS.isCurrentOS ())
      assertNotNull (c.getConfigurationValue ("OS"));
    assertNull (c.getConfigurationValue ("I really don't know that env var!"));
  }
}
