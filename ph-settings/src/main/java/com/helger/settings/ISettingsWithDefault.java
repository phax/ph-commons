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
package com.helger.settings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.state.EChange;

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
  ISettings getDefaultSettings ();

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

  /**
   * Change the preferences values of the given field name back to the default
   * as it is present in the configuration.
   *
   * @param sFieldName
   *        The field name to be reset. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the value was changed by setting it to
   *         default, {@link EChange#UNCHANGED} if nothing happened.
   */
  @Nonnull
  EChange setToDefault (@Nonnull String sFieldName);

  /**
   * Set all fields to default. This effects only fields that are present in the
   * default settings. Fields that are present in these settings but are not
   * present in the default settings are not altered by this method.
   *
   * @return {@link EChange#CHANGED} if at least one field value was changed.
   */
  @Nonnull
  EChange setAllToDefault ();

  /**
   * Check if the key is contained in the settings and is not part of the
   * default setting
   *
   * @param sFieldName
   *        Field name to query. May be <code>null</code>.
   * @return <code>true</code> if the key is directly in the settings,
   *         <code>false</code> otherwise
   */
  boolean containsKeyDirect (@Nullable String sFieldName);

  /**
   * Get the direct value with access to the default settings
   *
   * @param sFieldName
   *        Field name to query. May be <code>null</code>.
   * @return <code>null</code> if no such entry is present
   */
  @Nullable
  Object getValueDirect (@Nullable final String sFieldName);
}
