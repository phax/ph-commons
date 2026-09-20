/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.helger.base.classloader.ClassLoaderHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.config.source.appl.ConfigurationSourceFunction;
import com.helger.config.source.resource.properties.ConfigurationSourceProperties;
import com.helger.config.value.IConfigurationValueProvider;
import com.helger.io.resource.FileSystemResource;
import com.helger.io.resource.URLResource;

/**
 * Test class for class {@link MultiConfigurationValueProvider}.
 *
 * @author Philip Helger
 */
public final class MultiConfigurationValueProviderTest
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

  @Test
  public void testCtorList ()
  {
    assertEquals (0, new MultiConfigurationValueProvider ((List <IConfigurationSource>) null).getConfigurationSourceCount ());

    final ICommonsMap <String, String> aMap1 = new CommonsHashMap <> ();
    aMap1.put ("key1", "value1");
    final ICommonsMap <String, String> aMap2 = new CommonsHashMap <> ();
    aMap2.put ("key2", "value2");

    final ICommonsList <IConfigurationSource> aSources = new CommonsArrayList <> (new ConfigurationSourceFunction (110,
                                                                                                                   aMap1::get),
                                                                                  new ConfigurationSourceFunction (111,
                                                                                                                   aMap2::get));
    final MultiConfigurationValueProvider aMCSVP = new MultiConfigurationValueProvider (aSources);
    assertEquals (2, aMCSVP.getConfigurationSourceCount ());
    assertEquals ("value1", aMCSVP.getConfigurationValue ("key1").getValue ());
    assertEquals ("value2", aMCSVP.getConfigurationValue ("key2").getValue ());
  }

  @Test
  public void testCtorArray ()
  {
    assertEquals (0, new MultiConfigurationValueProvider ((IConfigurationSource []) null).getConfigurationSourceCount ());

    final ICommonsMap <String, String> aMap1 = new CommonsHashMap <> ();
    aMap1.put ("key1", "value1");
    final ICommonsMap <String, String> aMap2 = new CommonsHashMap <> ();
    aMap2.put ("key2", "value2");

    final MultiConfigurationValueProvider aMCSVP = new MultiConfigurationValueProvider (new ConfigurationSourceFunction (110,
                                                                                                                         aMap1::get),
                                                                                        new ConfigurationSourceFunction (111,
                                                                                                                         aMap2::get));
    assertEquals (2, aMCSVP.getConfigurationSourceCount ());
    assertEquals ("value1", aMCSVP.getConfigurationValue ("key1").getValue ());
    assertEquals ("value2", aMCSVP.getConfigurationValue ("key2").getValue ());
  }

  @Test
  public void testContainsConfigurationValue ()
  {
    final MultiConfigurationValueProvider aMCSVP = new MultiConfigurationValueProvider ();
    assertFalse (aMCSVP.containsConfigurationValue ("key1"));

    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("key1", "value1");
    aMCSVP.addConfigurationSource (new ConfigurationSourceFunction (110, aMap::get));

    assertTrue (aMCSVP.containsConfigurationValue ("key1"));
    assertFalse (aMCSVP.containsConfigurationValue ("key2"));
  }

  @Test
  public void testAddNullConfigurationValueProvider ()
  {
    final MultiConfigurationValueProvider aMCSVP = new MultiConfigurationValueProvider ();
    aMCSVP.addConfigurationSource ((IConfigurationValueProvider) null, 1234);
    assertEquals (0, aMCSVP.getConfigurationSourceCount ());

    try
    {
      aMCSVP.addConfigurationSource ((IConfigurationSource) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testUseOnlyInitializedConfigSources ()
  {
    // A resource that does not exist is not initialized and usable
    final ConfigurationSourceProperties aNotUsable = new ConfigurationSourceProperties (new FileSystemResource (new File ("does-not-exist.properties")));
    assertFalse (aNotUsable.isInitializedAndUsable ());

    final MultiConfigurationValueProvider aMCSVP = new MultiConfigurationValueProvider ();
    assertTrue (aMCSVP.isUseOnlyInitializedConfigSources ());
    assertEquals (Boolean.valueOf (MultiConfigurationValueProvider.DEFAULT_USE_ONLY_INTIIALIZED_CONFIG_SOURCES),
                  Boolean.valueOf (aMCSVP.isUseOnlyInitializedConfigSources ()));

    // Not added, because it is not usable
    aMCSVP.addConfigurationSource (aNotUsable);
    assertEquals (0, aMCSVP.getConfigurationSourceCount ());

    assertSame (aMCSVP, aMCSVP.setUseOnlyInitializedConfigSources (false));
    assertFalse (aMCSVP.isUseOnlyInitializedConfigSources ());

    // Now it is added
    aMCSVP.addConfigurationSource (aNotUsable);
    assertEquals (1, aMCSVP.getConfigurationSourceCount ());
  }

  @Test
  public void testForEachConfigurationValueProvider ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("key1", "value1");

    final MultiConfigurationValueProvider aMCSVP = new MultiConfigurationValueProvider ();
    aMCSVP.addConfigurationSource (new ConfigurationSourceFunction (110, aMap::get));
    aMCSVP.addConfigurationSource (new ConfigurationSourceFunction (111, aMap::get));

    final ICommonsList <Integer> aPrios = new CommonsArrayList <> ();
    aMCSVP.forEachConfigurationValueProvider ( (aCVP, nPrio) -> {
      assertNotNull (aCVP);
      aPrios.add (Integer.valueOf (nPrio));
    });
    // Highest priority comes first
    assertEquals (new CommonsArrayList <> (Integer.valueOf (111), Integer.valueOf (110)), aPrios);
  }

  @Test
  public void testGetClone ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("key1", "value1");

    // A nested MultiConfigurationValueProvider is ICloneable and is therefore
    // cloned as well
    final MultiConfigurationValueProvider aNested = new MultiConfigurationValueProvider ();
    aNested.addConfigurationSource (new ConfigurationSourceFunction (110, aMap::get));

    final MultiConfigurationValueProvider aMCSVP = new MultiConfigurationValueProvider ();
    aMCSVP.addConfigurationSource (aNested, 120);
    // A ConfigurationSourceFunction is not ICloneable and is reused
    aMCSVP.addConfigurationSource (new ConfigurationSourceFunction (100, aMap::get));

    final MultiConfigurationValueProvider aClone = aMCSVP.getClone ();
    assertNotNull (aClone);
    assertNotSame (aMCSVP, aClone);
    assertEquals (2, aClone.getConfigurationSourceCount ());
    assertEquals ("value1", aClone.getConfigurationValue ("key1").getValue ());
    assertNull (aClone.getConfigurationValue ("key2"));
  }

  @Test
  public void testToString ()
  {
    final MultiConfigurationValueProvider aMCSVP = new MultiConfigurationValueProvider ();
    aMCSVP.addConfigurationSource (new ConfigurationSourceFunction (110, x -> null));
    assertNotNull (aMCSVP.toString ());
  }

  @Test
  public void testCreateForClassPath ()
  {
    final ClassLoader aCL = ClassLoaderHelper.getDefaultClassLoader ();
    final MultiConfigurationValueProvider aMCSVP = MultiConfigurationValueProvider.createForClassPath (aCL,
                                                                                                       "application.properties",
                                                                                                       aURL -> new ConfigurationSourceProperties (new URLResource (aURL)));
    assertNotNull (aMCSVP);
    assertEquals ("from-application-properties0", aMCSVP.getConfigurationValue ("element0").getValue ());

    // Nothing to be found
    assertNull (MultiConfigurationValueProvider.createForClassPath (aCL,
                                                                    "does-not-exist-at-all.properties",
                                                                    aURL -> new ConfigurationSourceProperties (new URLResource (aURL))));
  }

  @Test
  public void testCreateForAllOccurrancesWithFile ()
  {
    final ClassLoader aCL = ClassLoaderHelper.getDefaultClassLoader ();
    final MultiConfigurationValueProvider aMCSVP = MultiConfigurationValueProvider.createForAllOccurrances (aCL,
                                                                                                            "src/test/resources/file/test.properties",
                                                                                                            aURL -> new ConfigurationSourceProperties (new URLResource (aURL)),
                                                                                                            true);
    assertNotNull (aMCSVP);
    assertEquals ("string", aMCSVP.getConfigurationValue ("element1").getValue ());

    // Without the file check nothing is found
    assertNull (MultiConfigurationValueProvider.createForAllOccurrances (aCL,
                                                                         "src/test/resources/file/test.properties",
                                                                         aURL -> new ConfigurationSourceProperties (new URLResource (aURL)),
                                                                         false));
  }
}
