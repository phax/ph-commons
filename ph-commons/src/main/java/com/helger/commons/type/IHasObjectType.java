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
package com.helger.commons.type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;

/**
 * Base interface for all objects having a certain {@link ObjectType}.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IHasObjectType
{
  /**
   * @return The type of the object. Never <code>null</code>.
   */
  @Nonnull
  ObjectType getObjectType ();

  /**
   * @return The name of the object type. Neither <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  default String getObjectTypeName ()
  {
    return getObjectType ().getName ();
  }

  /**
   * Check if this object has the provided {@link ObjectType}.
   * 
   * @param aOT
   *        The object type to check. May be <code>null</code>.
   * @return <code>true</code> if this object has the passed ObjectType,
   *         <code>false</code> otherwise.
   */
  default boolean hasObjectType (@Nullable final ObjectType aOT)
  {
    return getObjectType ().equals (aOT);
  }
}
