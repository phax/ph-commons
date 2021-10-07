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
package com.helger.config.source.res;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.state.ESuccess;
import com.helger.config.source.IConfigurationSource;

/**
 * Specific configuration source based on a readable resource.
 *
 * @author Philip Helger
 */
public interface IConfigurationSourceResource extends IConfigurationSource
{
  /**
   * @return The resource of this configuration source. Never <code>null</code>.
   */
  @Nonnull
  IReadableResource getResource ();

  /**
   * Try to reload the configuration source from the configured resource. The
   * reloading should be an atomic operation and be thread-safe.
   *
   * @return {@link ESuccess#SUCCESS} if the resource was found and loaded,
   *         {@link ESuccess#FAILURE} otherwise.
   * @since 9.4.5
   */
  @Nonnull
  ESuccess reload ();

  /**
   * @return An ordered map of all contained keys and values in this resource.
   *         The order of the items should follow the order of their declaration
   *         in the underlying resource. Never <code>null</code> but maybe
   *         empty.
   * @since 9.4.3
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsOrderedMap <String, String> getAllConfigItems ();
}
