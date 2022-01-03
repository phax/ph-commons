/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;

import com.helger.config.value.IConfigurationValueProvider;

/**
 * An abstract configuration source.
 *
 * @author Philip Helger
 */
public interface IConfigurationSource extends IConfigurationValueProvider
{
  /**
   * @return The configuration source type. Never <code>null</code>.
   */
  @Nonnull
  EConfigSourceType getSourceType ();

  /**
   * @return THe higher the priority the earlier it is checked. Values between
   *         {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE} are
   *         allowed.
   */
  int getPriority ();

  /**
   * @return <code>true</code> if the file was successfully initialized and can
   *         be used as a configuration source.
   */
  boolean isInitializedAndUsable ();
}
