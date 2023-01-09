/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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
package com.helger.config.source;

import java.util.function.Function;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.config.value.ConfiguredValue;

/**
 * Interface for a configuration source that provides all its possible keys.
 * This is an optional interface to be implemented by configuration sources
 * supporting it.
 *
 * @author Philip Helger
 * @since 11.0.0
 */
public interface IIterableConfigurationSource extends IConfigurationSource
{
  /**
   * @return A map of all contained keys with their values in this configuration
   *         source. If the underlying source uses some kind of ordering (e.g.
   *         in files), this order should be maintained. Never <code>null</code>
   *         but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsMap <String, String> getAllConfigItems ();

  /**
   * @return A map of all contained keys and {@link ConfiguredValue} in this
   *         configuration source. If the underlying source uses some kind of
   *         ordering (e.g. in files), this order should be maintained. Never
   *         <code>null</code> but maybe empty.
   * @see #getAllConfigItems()
   */
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsMap <String, ConfiguredValue> getAllConfiguredValues ()
  {
    return new CommonsLinkedHashMap <> (getAllConfigItems (), Function.identity (), x -> new ConfiguredValue (this, x));
  }
}
