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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Ignore;
import org.junit.Test;

import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.mutable.MutableInt;
import com.helger.config.source.MultiConfigurationValueProvider;
import com.helger.config.source.appl.ConfigurationSourceFunction;
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
    final Config aConfig = new Config (CS1);
    assertEquals ("from-application-json0", aConfig.getAsString ("element0"));
    assertNull (aConfig.getAsString ("element5"));
    assertFalse (aConfig.isReplaceVariables ());

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

  @Test
  public void testWithDisabledVariableReplacement ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("key1", "value");
    aMap.put ("key2", "${key1}");
    aMap.put ("more.complex.nested.key", "complex ${key1} ${key2}!");
    aMap.put ("key3", "not so ${more.complex.nested.key}");
    final Config aConfig = new Config (new ConfigurationSourceFunction (aMap::get)).setReplaceVariables (false);
    assertEquals ("value", aConfig.getAsString ("key1"));
    assertEquals ("${key1}", aConfig.getAsString ("key2"));
    assertEquals ("complex ${key1} ${key2}!", aConfig.getAsString ("more.complex.nested.key"));
    assertEquals ("not so ${more.complex.nested.key}", aConfig.getAsString ("key3"));
  }

  @Test
  public void testWithVariableReplacement ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("key1", "value");
    aMap.put ("key2", "${key1}");
    aMap.put ("more.complex.nested.key", "complex ${key1} ${key2}!");
    aMap.put ("key3", "not so ${more.complex.nested.key}");
    final Config aConfig = new Config (new ConfigurationSourceFunction (aMap::get)).setReplaceVariables (true);
    assertEquals ("value", aConfig.getAsString ("key1"));
    assertEquals ("value", aConfig.getAsString ("key2"));
    assertEquals ("complex value value!", aConfig.getAsString ("more.complex.nested.key"));
    assertEquals ("not so complex value value!", aConfig.getAsString ("key3"));
  }

  @Test
  @Ignore ("TODO")
  // This class does not yet work as expected
  public void testWithVariableReplacementCyclicDep ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("key1", "1 and ${key2}");
    aMap.put ("key2", "2 and ${key1}");
    final Config aConfig = new Config (new ConfigurationSourceFunction (aMap::get)).setReplaceVariables (true);
    assertEquals ("1 and ${key2}", aConfig.getAsString ("key1"));
    assertEquals ("2 and ${key1}", aConfig.getAsString ("key2"));
  }
}
