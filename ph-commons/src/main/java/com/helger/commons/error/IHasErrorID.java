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
package com.helger.commons.error;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.string.StringHelper;

/**
 * Interface for objects having an error ID
 *
 * @author Philip Helger
 */
public interface IHasErrorID
{
  /**
   * @return The error ID. May be <code>null</code>.
   */
  @Nullable
  String getErrorID ();

  /**
   * @return <code>true</code> if an error ID is present, <code>false</code>
   *         otherwise
   */
  default boolean hasErrorID ()
  {
    return StringHelper.hasText (getErrorID ());
  }

  @Nonnull
  static Comparator <IHasErrorID> getComparatorErrorID ()
  {
    return Comparator.nullsFirst (Comparator.comparing (IHasErrorID::getErrorID));
  }
}
