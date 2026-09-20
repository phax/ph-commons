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
package com.helger.settings.exchange.configfile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Test;

import com.helger.base.system.SystemProperties;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.io.resourceprovider.ClassPathResourceProvider;
import com.helger.io.resourceprovider.IReadableResourceProvider;
import com.helger.settings.exchange.ISettingsPersistence;
import com.helger.settings.exchange.properties.SettingsPersistenceProperties;

/**
 * Test class for class {@link ConfigFileBuilder}.
 *
 * @author Philip Helger
 */
public final class ConfigFileBuilderTest
{
  private static final String SYS_PROP = "ph-commons.junittest.configfile";
  private static final String ENV_VAR = "PH_COMMONS_JUNITTEST_DOES_NOT_EXIST";

  @After
  public void clearSystemProperty ()
  {
    SystemProperties.removePropertyValue (SYS_PROP);
  }

  @Test
  public void testDefaults ()
  {
    final ConfigFileBuilder aBuilder = new ConfigFileBuilder ();
    assertNotNull (aBuilder.settingsPersistence ());
    assertNotNull (aBuilder.resourceProvider ());
    assertTrue (aBuilder.getAllPaths ().isEmpty ());
    assertNotNull (aBuilder.paths ());
    assertNotNull (aBuilder.toString ());
  }

  @Test
  public void testSetters ()
  {
    final ConfigFileBuilder aBuilder = new ConfigFileBuilder ();

    final ISettingsPersistence aSPP = new SettingsPersistenceProperties ();
    assertSame (aBuilder, aBuilder.settingsPersistence (aSPP));
    assertSame (aSPP, aBuilder.settingsPersistence ());

    final IReadableResourceProvider aRP = new ClassPathResourceProvider ();
    assertSame (aBuilder, aBuilder.resourceProvider (aRP));
    assertSame (aRP, aBuilder.resourceProvider ());
  }

  @Test
  public void testAddPaths ()
  {
    final ConfigFileBuilder aBuilder = new ConfigFileBuilder ();

    // A null path is ignored
    assertSame (aBuilder, aBuilder.addPath ((String) null));
    assertTrue (aBuilder.getAllPaths ().isEmpty ());

    aBuilder.addPath ("p1");
    aBuilder.addPaths ("p2", "p3");
    aBuilder.addPaths (new CommonsArrayList <> ("p4", "p5"));

    final ICommonsList <String> aPaths = aBuilder.getAllPaths ();
    assertEquals (new CommonsArrayList <> ("p1", "p2", "p3", "p4", "p5"), aPaths);
    // Must be a copy
    assertTrue (aBuilder.getAllPaths () != aPaths);
  }

  @Test
  public void testSinglePathReplacesAll ()
  {
    final ConfigFileBuilder aBuilder = new ConfigFileBuilder ().addPaths ("p1", "p2");
    assertEquals (2, aBuilder.getAllPaths ().size ());

    assertSame (aBuilder, aBuilder.path ("only"));
    assertEquals (new CommonsArrayList <> ("only"), aBuilder.getAllPaths ());

    aBuilder.paths ("a", "b");
    assertEquals (new CommonsArrayList <> ("a", "b"), aBuilder.getAllPaths ());

    aBuilder.paths (new CommonsArrayList <> ("c", "d"));
    assertEquals (new CommonsArrayList <> ("c", "d"), aBuilder.getAllPaths ());
  }

  @Test
  public void testAddPathFromSupplier ()
  {
    final ConfigFileBuilder aBuilder = new ConfigFileBuilder ();
    aBuilder.addPath (() -> "from-supplier");
    assertEquals (new CommonsArrayList <> ("from-supplier"), aBuilder.getAllPaths ());

    // A supplier returning null adds nothing
    aBuilder.addPath (() -> null);
    assertEquals (1, aBuilder.getAllPaths ().size ());
  }

  @Test
  public void testAddPathFromSystemProperty ()
  {
    final ConfigFileBuilder aBuilder = new ConfigFileBuilder ();

    // Not set yet
    aBuilder.addPathFromSystemProperty (SYS_PROP);
    assertTrue (aBuilder.getAllPaths ().isEmpty ());

    SystemProperties.setPropertyValue (SYS_PROP, "test.properties");
    aBuilder.addPathFromSystemProperty (SYS_PROP);
    assertEquals (new CommonsArrayList <> ("test.properties"), aBuilder.getAllPaths ());
  }

  @Test
  public void testAddPathFromEnvVar ()
  {
    final ConfigFileBuilder aBuilder = new ConfigFileBuilder ();
    // The environment variable is not set
    aBuilder.addPathFromEnvVar (ENV_VAR);
    assertTrue (aBuilder.getAllPaths ().isEmpty ());
  }

  @Test
  public void testBuildWithoutPath ()
  {
    try
    {
      new ConfigFileBuilder ().build ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testBuildFallsBackToSecondPath ()
  {
    final ConfigFile aCF = new ConfigFileBuilder ().addPath ("does-not-exist.properties")
                                                   .addPath ("test.properties")
                                                   .build ();
    assertTrue (aCF.isRead ());
    assertNotNull (aCF.getReadResource ());
    assertEquals ("string", aCF.getAsString ("element1"));
  }

  @Test
  public void testBuildAllPathsMissing ()
  {
    final ConfigFile aCF = new ConfigFileBuilder ().addPath ("does-not-exist.properties").build ();
    assertFalse (aCF.isRead ());
    assertNull (aCF.getReadResource ());
    assertNull (aCF.getSettings ());
    assertNull (aCF.getValue ("any"));
  }

  @Test
  public void testInvalidParams ()
  {
    final ConfigFileBuilder aBuilder = new ConfigFileBuilder ();
    try
    {
      aBuilder.settingsPersistence (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      aBuilder.resourceProvider (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      aBuilder.addPathFromSystemProperty (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
