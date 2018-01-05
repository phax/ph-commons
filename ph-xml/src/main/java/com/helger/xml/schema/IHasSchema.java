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
package com.helger.xml.schema;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.validation.Schema;

import com.helger.commons.annotation.DevelopersNote;
import com.helger.commons.lang.IHasClassLoader;

/**
 * A simple interface, indicating that an item has a Schema object.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IHasSchema
{
  /**
   * @return The compiled {@link Schema} object.
   */
  @DevelopersNote ("No nullable assumption can be provided.")
  default Schema getSchema ()
  {
    return getSchema ((ClassLoader) null);
  }

  /**
   * @param aClassLoaderProvider
   *        ClassLoader provider. May not be <code>null</code>.
   * @return The compiled {@link Schema} object.
   */
  @DevelopersNote ("No nullable assumption can be provided.")
  default Schema getSchema (@Nonnull final IHasClassLoader aClassLoaderProvider)
  {
    return getSchema (aClassLoaderProvider.getClassLoader ());
  }

  /**
   * @param aClassLoader
   *        The class loader to be used. May be <code>null</code> indicating
   *        that the default class loader should be used.
   * @return The compiled {@link Schema} object.
   */
  @DevelopersNote ("No nullable assumption can be provided.")
  Schema getSchema (@Nullable ClassLoader aClassLoader);
}
