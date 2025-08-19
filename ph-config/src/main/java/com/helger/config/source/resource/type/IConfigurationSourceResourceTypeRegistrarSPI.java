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
package com.helger.config.source.resource.type;

import com.helger.annotation.style.IsSPIInterface;

import jakarta.annotation.Nonnull;

/**
 * SPI interface to be implemented by other configuration source resource types
 *
 * @author Philip Helger
 */
@IsSPIInterface
public interface IConfigurationSourceResourceTypeRegistrarSPI
{
  /**
   * Register your configuration source resource type converters.
   *
   * @param aRegistry
   *        The destination registry. Never <code>null</code>.
   */
  void registerResourceType (@Nonnull ConfigurationSourceResourceTypeRegistry aRegistry);
}
