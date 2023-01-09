/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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
package com.helger.config.source.appl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.config.source.EConfigSourceType;

/**
 * Test class for class {@link ConfigurationSourceFunction}.
 *
 * @author Philip Helger
 */
public final class ConfigurationSourceFunctionTest
{
  private static final ICommonsMap <String, String> MAP = new CommonsHashMap <> ();
  static
  {
    MAP.put ("key1", "value1");
    MAP.put ("key2", "value2");
  }

  @Test
  public void testBasic ()
  {
    final ConfigurationSourceFunction c = new ConfigurationSourceFunction (MAP::get);
    assertSame (EConfigSourceType.APPLICATION, c.getSourceType ());
    assertEquals (EConfigSourceType.APPLICATION.getDefaultPriority (), c.getPriority ());
    assertTrue (c.isInitializedAndUsable ());
    assertEquals ("value1", c.getConfigurationValue ("key1").getValue ());
    assertNull (c.getConfigurationValue ("I really don't know that key!"));

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (c, new ConfigurationSourceFunction (MAP::get));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (c, new ConfigurationSourceFunction (9797, MAP::get));
  }

  @Test
  public void testDifferentPrio ()
  {
    final ConfigurationSourceFunction c = new ConfigurationSourceFunction (9797, MAP::get);
    assertSame (EConfigSourceType.APPLICATION, c.getSourceType ());
    assertEquals (9797, c.getPriority ());
    assertTrue (c.isInitializedAndUsable ());
    assertEquals ("value1", c.getConfigurationValue ("key1").getValue ());
    assertNull (c.getConfigurationValue ("I really don't know that key!"));
  }
}
