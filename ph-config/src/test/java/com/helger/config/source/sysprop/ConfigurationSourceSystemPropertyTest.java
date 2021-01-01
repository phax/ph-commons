/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.config.source.sysprop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.system.SystemProperties;
import com.helger.config.source.EConfigSourceType;

/**
 * Test class for class {@link ConfigurationSourceSystemProperty}.
 *
 * @author Philip Helger
 */
public final class ConfigurationSourceSystemPropertyTest
{
  @Test
  public void testBasic ()
  {
    final ConfigurationSourceSystemProperty c = new ConfigurationSourceSystemProperty ();
    assertSame (EConfigSourceType.SYSTEM_PROPERTY, c.getSourceType ());
    assertEquals (EConfigSourceType.SYSTEM_PROPERTY.getDefaultPriority (), c.getPriority ());
    assertTrue (c.isInitializedAndUsable ());
    assertNotNull (c.getConfigurationValue (SystemProperties.SYSTEM_PROPERTY_JAVA_HOME));
    assertNull (c.getConfigurationValue ("I really don't know that system property!"));

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (c, new ConfigurationSourceSystemProperty ());
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (c, new ConfigurationSourceSystemProperty (1234));
  }

  @Test
  public void testDifferentPrio ()
  {
    final ConfigurationSourceSystemProperty c = new ConfigurationSourceSystemProperty (7654);
    assertSame (EConfigSourceType.SYSTEM_PROPERTY, c.getSourceType ());
    assertEquals (7654, c.getPriority ());
    assertTrue (c.isInitializedAndUsable ());
    assertNotNull (c.getConfigurationValue (SystemProperties.SYSTEM_PROPERTY_JAVA_HOME));
    assertNull (c.getConfigurationValue ("I really don't know that system property!"));
  }
}
