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
package com.helger.commons.serialize.convert;

import javax.annotation.Nonnull;

/**
 * This is the callback interface implemented by
 * {@link SerializationConverterRegistry} for registration via the SPI
 * interface.
 *
 * @author Philip Helger
 */
public interface ISerializationConverterRegistry
{
  /**
   * Register a new serialization converter.
   *
   * @param aClass
   *        The class for which the converter is meant. May not be
   *        <code>null</code>.
   * @param aConverter
   *        The converter to be registered. May not be <code>null</code>.
   * @throws IllegalArgumentException
   *         If the passed class already implements the java.io.Serializable
   *         interface.
   */
  void registerSerializationConverter (@Nonnull Class <?> aClass, @Nonnull ISerializationConverter aConverter);
}
