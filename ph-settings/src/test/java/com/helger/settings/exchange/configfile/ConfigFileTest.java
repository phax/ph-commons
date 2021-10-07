/*
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
package com.helger.settings.exchange.configfile;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.settings.exchange.json.SettingsPersistenceJson;
import com.helger.settings.exchange.properties.SettingsPersistenceProperties;
import com.helger.settings.exchange.xml.SettingsPersistenceXML;

/**
 * Test class for class {@link ConfigFile}.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class ConfigFileTest
{
  @Test
  public void testProperties ()
  {
    final ConfigFile aCF = new ConfigFileBuilder ().addPath ("test.properties").build ();
    assertTrue (aCF.isRead ());
    // Existing elements
    assertEquals ("string", aCF.getAsString ("element1"));
    assertArrayEquals ("string".toCharArray (), aCF.getAsCharArray ("element1"));
    assertEquals (6, aCF.getAsCharArray ("element1").length);

    assertEquals (2, aCF.getAsInt ("element2", 5));

    assertFalse (aCF.getAsBoolean ("element3", true));
    assertFalse (aCF.getAsBoolean ("element3"));

    assertEquals ("abc", aCF.getAsString ("element4"));

    // Non-existing elements
    assertNull (aCF.getAsString ("element1a"));
    assertNull (aCF.getAsCharArray ("element1a"));
    assertEquals (5, aCF.getAsInt ("element2a", 5));
    assertTrue (aCF.getAsBoolean ("element3a", true));

    // All keys
    assertEquals (5, aCF.getSettings ().size ());

    assertNotNull (aCF.toString ());
  }

  @Test
  public void testPropertiesNonExisting ()
  {
    final ConfigFile aCF = new ConfigFileBuilder ().settingsPersistence (new SettingsPersistenceProperties ())
                                                   .addPath ("non-existent-file.properties")
                                                   .build ();
    assertFalse (aCF.isRead ());
    assertNull (aCF.getAsString ("any"));
    assertEquals (0, aCF.getAllEntries ().size ());

    assertNotNull (aCF.toString ());
  }

  @Test
  public void testXML ()
  {
    final ConfigFile aCF = new ConfigFileBuilder ().settingsPersistence (new SettingsPersistenceXML <> (TrimmedValueSettings::new))
                                                   .addPath ("test.xml")
                                                   .build ();
    assertTrue (aCF.isRead ());
    // Existing elements
    assertEquals ("string", aCF.getAsString ("element1"));
    assertArrayEquals ("string".toCharArray (), aCF.getAsCharArray ("element1"));
    assertEquals (6, aCF.getAsCharArray ("element1").length);

    assertEquals (2, aCF.getAsInt ("element2", 5));

    assertFalse (aCF.getAsBoolean ("element3", true));
    assertFalse (aCF.getAsBoolean ("element3"));

    assertEquals ("abc", aCF.getAsString ("element4"));

    // Non-existing elements
    assertNull (aCF.getAsString ("element1a"));
    assertNull (aCF.getAsCharArray ("element1a"));
    assertEquals (5, aCF.getAsInt ("element2a", 5));
    assertTrue (aCF.getAsBoolean ("element3a", true));

    // All keys
    assertEquals (5, aCF.getSettings ().size ());

    assertNotNull (aCF.toString ());
  }

  @Test
  public void testXMLOld ()
  {
    // Read the old layout from before 9.3.2
    final ConfigFile aCF = new ConfigFileBuilder ().settingsPersistence (new SettingsPersistenceXML <> (TrimmedValueSettings::new))
                                                   .addPath ("test-old.xml")
                                                   .build ();
    assertTrue (aCF.isRead ());
    // Existing elements
    assertEquals ("string", aCF.getAsString ("element1"));
    assertArrayEquals ("string".toCharArray (), aCF.getAsCharArray ("element1"));
    assertEquals (6, aCF.getAsCharArray ("element1").length);

    assertEquals (2, aCF.getAsInt ("element2", 5));

    assertFalse (aCF.getAsBoolean ("element3", true));
    assertFalse (aCF.getAsBoolean ("element3"));

    assertEquals ("abc", aCF.getAsString ("element4"));

    // Non-existing elements
    assertNull (aCF.getAsString ("element1a"));
    assertNull (aCF.getAsCharArray ("element1a"));
    assertEquals (5, aCF.getAsInt ("element2a", 5));
    assertTrue (aCF.getAsBoolean ("element3a", true));

    // All keys
    assertEquals (5, aCF.getSettings ().size ());

    assertNotNull (aCF.toString ());
  }

  @Test
  public void testXMLNonExisting ()
  {
    final ConfigFile aCF = new ConfigFileBuilder ().settingsPersistence (SettingsPersistenceXML.createDefault ())
                                                   .addPath ("non-existent-file.xml")
                                                   .build ();
    assertFalse (aCF.isRead ());
    assertNull (aCF.getAsString ("any"));
    assertEquals (0, aCF.getAllEntries ().size ());

    assertNotNull (aCF.toString ());
  }

  @Test
  public void testJson ()
  {
    final ConfigFile aCF = new ConfigFileBuilder ().settingsPersistence (new SettingsPersistenceJson (TrimmedValueSettings::new))
                                                   .addPath ("test.json")
                                                   .build ();
    assertTrue (aCF.isRead ());
    // Existing elements
    assertEquals ("string", aCF.getAsString ("element1"));
    assertArrayEquals ("string".toCharArray (), aCF.getAsCharArray ("element1"));
    assertEquals (6, aCF.getAsCharArray ("element1").length);

    assertEquals (2, aCF.getAsInt ("element2", 5));

    assertFalse (aCF.getAsBoolean ("element3", true));
    assertFalse (aCF.getAsBoolean ("element3"));

    assertEquals ("abc", aCF.getAsString ("element4"));

    // Check nested settings
    assertNull (aCF.getAsString ("element5.network"));
    assertEquals ("1234", aCF.getAsString ("element5.network.port"));
    assertEquals ("example.org", aCF.getAsString ("element5.network.host"));
    assertEquals ("true", aCF.getAsString ("element5.enabled"));

    // Non-existing elements
    assertNull (aCF.getAsString ("element1a"));
    assertNull (aCF.getAsCharArray ("element1a"));
    assertEquals (5, aCF.getAsInt ("element2a", 5));
    assertTrue (aCF.getAsBoolean ("element3a", true));

    // All keys
    assertEquals (7, aCF.getSettings ().size ());

    assertNotNull (aCF.toString ());
  }

  @Test
  public void testJsonNonExisting ()
  {
    final ConfigFile aCF = new ConfigFileBuilder ().settingsPersistence (new SettingsPersistenceJson ())
                                                   .addPath ("non-existent-file.json")
                                                   .build ();
    assertFalse (aCF.isRead ());
    assertNull (aCF.getAsString ("any"));
    assertEquals (0, aCF.getAllEntries ().size ());

    assertNotNull (aCF.toString ());
  }

  @Test
  public void testNoPath ()
  {
    try
    {
      // No path contained
      new ConfigFileBuilder ().build ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
    try
    {
      // Invalid path
      new ConfigFileBuilder ().paths ((String []) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // Invalid path
      new ConfigFileBuilder ().paths ((Iterable <String>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // Invalid path
      new ConfigFileBuilder ().paths (new String [0]);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // Invalid path
      new ConfigFileBuilder ().paths (null, "bla");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
