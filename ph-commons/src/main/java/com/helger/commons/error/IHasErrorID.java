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
package com.helger.commons.error;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.equals.EqualsHelper;
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
   * @see #hasNoErrorID()
   */
  default boolean hasErrorID ()
  {
    return StringHelper.hasText (getErrorID ());
  }

  /**
   * @return <code>true</code> if no error ID is present, <code>false</code>
   *         otherwise
   * @see #hasErrorID()
   */
  default boolean hasNoErrorID ()
  {
    return StringHelper.hasNoText (getErrorID ());
  }

  /**
   * Check if this error has the passed error ID.
   *
   * @param sErrorID
   *        The error ID to check. May be null.
   * @return <code>true</code> if the error ID is equal, <code>false</code>
   *         otherwise
   * @since 8.5.0
   */
  default boolean hasErrorID (@Nullable final String sErrorID)
  {
    return EqualsHelper.equals (getErrorID (), sErrorID);
  }

  @Nonnull
  static Comparator <IHasErrorID> getComparatorErrorID ()
  {
    return Comparator.nullsFirst (Comparator.comparing (IHasErrorID::getErrorID));
  }
}
