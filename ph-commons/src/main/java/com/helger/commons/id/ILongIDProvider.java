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
package com.helger.commons.id;

import java.io.Serializable;

import javax.annotation.Nonnull;

/**
 * Interface for objects having a long ID.
 *
 * @author Philip Helger
 * @param <VALUETYPE>
 *        Object type
 */
public interface ILongIDProvider <VALUETYPE> extends Serializable
{
  /**
   * Get the ID of the passed object.
   *
   * @param aObject
   *        The object who's ID is to be retrieved. May not be <code>null</code>
   *        .
   * @return The ID of the object.
   */
  long getID (@Nonnull VALUETYPE aObject);

  @Nonnull
  static <VALUETYPE extends IHasLongID> ILongIDProvider <VALUETYPE> createHasLongID ()
  {
    return aObject -> aObject.getID ();
  }
}
