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
package com.helger.commons.factory;

import com.helger.commons.annotation.DevelopersNote;

/**
 * This is a generic interface for creating objects of a certain type that have
 * a parent item.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of object to create.
 * @param <PARAMTYPE>
 *        The type of the parameter object.
 */
@FunctionalInterface
public interface IHierarchicalFactoryWithParameter <DATATYPE, PARAMTYPE>
{
  /**
   * Create an object of the desired type.
   *
   * @param aParent
   *        The parent item to use. May be <code>null</code> depending on the
   *        implementation.
   * @param aParam
   *        The parameter to be passed. May be <code>null</code> depending on
   *        the implementation.
   * @return The created object. May be <code>null</code> depending on the
   *         implementation.
   */
  @DevelopersNote ("No @Nullable annotation as we can make no assumptions on the state")
  DATATYPE create (DATATYPE aParent, PARAMTYPE aParam);
}
