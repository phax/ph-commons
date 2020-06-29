/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.config.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.config.source.appl.ConfigurationSourceFunction;

/**
 * Test class for class {@link MultiConfigurationValueProvider}.
 *
 * @author Philip Helger
 */
public class MultiConfigurationValueProviderTest
{
  @Test
  public void testBasic ()
  {
    final MultiConfigurationValueProvider aMCSVP = new MultiConfigurationValueProvider ();

    // Lower priority
    final ICommonsMap <String, String> aMap1 = new CommonsHashMap <> ();
    aMap1.put ("key1", "value1");
    aMap1.put ("key2", "value2");
    aMCSVP.addConfigurationSource (new ConfigurationSourceFunction (110, aMap1::get));

    // Higher priority - should be returned
    final ICommonsMap <String, String> aMap2 = new CommonsHashMap <> ();
    aMap2.put ("key1", "value2");
    aMap2.put ("key3", "value3");
    aMCSVP.addConfigurationSource (new ConfigurationSourceFunction (111, aMap2::get));

    // Resolve
    assertEquals ("value2", aMCSVP.getConfigurationValue ("key1").getValue ());
    assertEquals ("value2", aMCSVP.getConfigurationValue ("key2").getValue ());
    assertEquals ("value3", aMCSVP.getConfigurationValue ("key3").getValue ());
    assertNull (aMCSVP.getConfigurationValue ("key4"));
  }

  @Test
  public void testEmpty ()
  {
    final MultiConfigurationValueProvider aMCSVP = new MultiConfigurationValueProvider ();
    // Resolve
    assertNull (aMCSVP.getConfigurationValue ("key1"));
    assertNull (aMCSVP.getConfigurationValue ("key2"));
    assertNull (aMCSVP.getConfigurationValue ("key3"));
    assertNull (aMCSVP.getConfigurationValue ("key4"));
  }
}
