/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.settings.factory;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.functional.IFunction;
import com.helger.settings.ISettings;
import com.helger.settings.Settings;
import com.helger.settings.SettingsWithDefault;

/**
 * This is just a type definition for the settings factory. The factory
 * parameter is the name of the factory set to read. This may e.g. be a file
 * name for file based settings factories.
 *
 * @author Philip Helger
 * @param <T>
 *        The effective data type to create
 */
public interface ISettingsFactory <T extends ISettings> extends IFunction <String, T>
{
  /**
   * Create a new settings object.
   *
   * @param sName
   *        The name of the settings. May neither be <code>null</code> nor
   *        empty.
   * @return The created settings object. May not be <code>null</code>.
   */
  @Nonnull
  T apply (@Nonnull @Nonempty String sName);

  @Nonnull
  static ISettingsFactory <Settings> newInstance ()
  {
    return Settings::new;
  }

  @Nonnull
  static ISettingsFactory <SettingsWithDefault> newInstance (@Nonnull final ISettings aDefaultSettings)
  {
    ValueEnforcer.notNull (aDefaultSettings, "DefaultSettings");
    return sName -> new SettingsWithDefault (sName, aDefaultSettings);
  }
}
