/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.datetime.domain;

import java.time.OffsetDateTime;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.enforce.ValueEnforcer;

/**
 * Interface for objects having a creation date time with an offset. Other than
 * {@link IHasCreationDateTime}, the value is unambiguous, so it can be compared across time zones
 * and daylight saving time changes.
 *
 * @author Philip Helger
 * @since 12.9.0
 */
@FunctionalInterface
public interface IHasCreationOffsetDateTime
{
  /**
   * @return The maybe <code>null</code> creation date time of the object. Usually it is not
   *         <code>null</code> but in case this interface is needed for legacy objects where the
   *         information was not yet stored, it may be <code>null</code>.
   */
  @Nullable
  OffsetDateTime getCreationDateTime ();

  /**
   * @return <code>true</code> if a creation date time is present, <code>false</code> if not.
   * @see #getCreationDateTime()
   */
  default boolean hasCreationDateTime ()
  {
    return getCreationDateTime () != null;
  }

  /**
   * Check if the object was created at the specified date time. This is <code>true</code>, if the
   * creation instant is &le; than the instant of the specified date time - the offsets are not
   * relevant.
   *
   * @param aDT
   *        The time to check for creation. May not be <code>null</code>.
   * @return <code>true</code> if this object was created, <code>false</code> if not.
   */
  default boolean isCreatedAt (@NonNull final OffsetDateTime aDT)
  {
    ValueEnforcer.notNull (aDT, "OffsetDateTime");
    final OffsetDateTime aCreationDT = getCreationDateTime ();
    return aCreationDT != null && !aCreationDT.isAfter (aDT);
  }
}
