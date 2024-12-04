/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.config.value;

import javax.annotation.Nonnull;

/**
 * Callback interface for enumeration of available configuration value
 * providers.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IConfigurationValueProviderWithPriorityCallback
{
  /**
   * Invoked for a single configuration value provider.<br>
   * Was renamed to "onConfigurationValueProvider" in v11
   *
   * @param aCVP
   *        The Configuration value provider. Never <code>null</code>
   * @param nPriority
   *        The priority the Configuration value provide has.
   */
  void onConfigurationValueProvider (@Nonnull IConfigurationValueProvider aCVP, int nPriority);
}
