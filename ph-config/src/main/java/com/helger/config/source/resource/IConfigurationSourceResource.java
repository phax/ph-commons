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
package com.helger.config.source.resource;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.state.ESuccess;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.config.source.IIterableConfigurationSource;
import com.helger.io.resource.IReadableResource;

/**
 * Specific configuration source based on a readable resource.
 *
 * @author Philip Helger
 */
public interface IConfigurationSourceResource extends IIterableConfigurationSource
{
  /**
   * @return The resource of this configuration source. Never <code>null</code>.
   */
  @NonNull
  IReadableResource getResource ();

  /**
   * Try to reload the configuration source from the configured resource. The
   * reloading should be an atomic operation and be thread-safe.
   *
   * @return {@link ESuccess#SUCCESS} if the resource was found and loaded,
   *         {@link ESuccess#FAILURE} otherwise.
   * @since 9.4.5
   */
  @NonNull
  ESuccess reload ();

  /**
   * @return An ordered map of all contained keys and values in this resource.
   *         The order of the items should follow the order of their declaration
   *         in the underlying resource. Never <code>null</code> but maybe
   *         empty.
   * @since 9.4.3
   */
  @NonNull
  @ReturnsMutableCopy
  ICommonsOrderedMap <String, String> getAllConfigItems ();
}
