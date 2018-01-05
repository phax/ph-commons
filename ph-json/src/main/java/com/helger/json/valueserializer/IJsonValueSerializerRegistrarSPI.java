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
package com.helger.json.valueserializer;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.IsSPIInterface;

/**
 * SPI interface to be implemented by other modules wishing to register their
 * own {@link IJsonValueSerializer}.
 *
 * @author Philip Helger
 */
@IsSPIInterface
public interface IJsonValueSerializerRegistrarSPI
{
  /**
   * Register all your dynamic {@link IJsonValueSerializer} in the passed
   * interface
   *
   * @param aRegistry
   *        The registry to register your serializers. Never <code>null</code>.
   */
  void registerJsonValueSerializer (@Nonnull IJsonValueSerializerRegistry aRegistry);
}
