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
package com.helger.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.settings.factory.ISettingsFactory;

/**
 * Test class for class {@link SettingsCache} and {@link ISettingsFactory}.
 *
 * @author Philip Helger
 */
public final class SettingsCacheTest
{
  @Test
  public void testDefaultFactory ()
  {
    final ISettingsFactory <Settings> aFactory = ISettingsFactory.newInstance ();
    assertNotNull (aFactory);

    final Settings aSettings = aFactory.apply ("name1");
    assertNotNull (aSettings);
    assertEquals ("name1", aSettings.getName ());
  }

  @Test
  public void testFactoryWithDefault ()
  {
    final Settings aDefault = new Settings ("default");
    aDefault.putIn ("key1", "default1");

    final ISettingsFactory <SettingsWithDefault> aFactory = ISettingsFactory.newInstance (aDefault);
    assertNotNull (aFactory);

    final SettingsWithDefault aSettings = aFactory.apply ("name1");
    assertEquals ("name1", aSettings.getName ());
    assertSame (aDefault, aSettings.getDefaultSettings ());
    assertEquals ("default1", aSettings.getValue ("key1"));

    try
    {
      ISettingsFactory.newInstance (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testCache ()
  {
    final ISettingsFactory <Settings> aFactory = ISettingsFactory.newInstance ();
    final SettingsCache aCache = new SettingsCache (aFactory);
    assertSame (aFactory, aCache.getSettingsFactory ());
    assertNotNull (aCache.toString ());

    final ISettings aSettings = aCache.getFromCache ("name1");
    assertNotNull (aSettings);
    assertEquals ("name1", aSettings.getName ());
    // The very same instance is returned again
    assertSame (aSettings, aCache.getFromCache ("name1"));
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      new SettingsCache (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
