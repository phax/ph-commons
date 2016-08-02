/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;

/**
 * Read-only settings with default are a special kind of settings. The behave
 * like regular settings but offer the possibility to revert back to the default
 * value easily.
 *
 * @author philip
 */
public interface ISettingsWithDefault extends ISettings
{
  /**
   * @return The underlying default settings object. Never <code>null</code>.
   */
  @Nonnull
  ISettings getDefault ();

  /**
   * Check if the value of the passed field is equal to the default value.
   *
   * @param sFieldName
   *        The name of the field to check. May not be <code>null</code>.
   * @return <code>true</code> if the field value equals the default value or if
   *         the field does neither exist in this settings nor in the default
   *         settings.
   */
  boolean isSetToDefault (@Nonnull String sFieldName);
}
