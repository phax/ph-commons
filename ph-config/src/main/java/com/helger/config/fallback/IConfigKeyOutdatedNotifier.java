/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.config.fallback;

import com.helger.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;

/**
 * Sanity callback interface to notify about the usage of an outdated
 * configuration key.
 *
 * @author Philip Helger
 * @since 10.2.0
 */
@FunctionalInterface
public interface IConfigKeyOutdatedNotifier
{
  /**
   * Called to indicate that an outdated configuration key was used.
   *
   * @param sOldConfigKey
   *        The outdated configuration key used. Neither <code>null</code> nor
   *        empty.
   * @param sNewConfigKey
   *        The new and corrected configuration key used. Neither
   *        <code>null</code> nor empty.
   */
  void onOutdatedConfigurationKey (@Nonnull @Nonempty String sOldConfigKey, @Nonnull @Nonempty String sNewConfigKey);
}
