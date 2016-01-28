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

import java.util.Comparator;

import javax.annotation.Nonnull;

/**
 * Base interface for all objects having a long ID.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IHasLongID
{
  /**
   * @return The simple long ID of this object.
   */
  long getID ();

  @Nonnull
  static Comparator <IHasLongID> getComparatorID ()
  {
    return Comparator.comparingLong (IHasLongID::getID);
  }
}
