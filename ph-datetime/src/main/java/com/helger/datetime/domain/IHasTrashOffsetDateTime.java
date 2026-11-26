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
 * Interface for objects having a trash date time with an offset. Other than
 * {@link IHasTrashDateTime}, the value is unambiguous, so it can be compared across time zones
 * and daylight saving time changes.
 *
 * @author Philip Helger
 * @since 12.9.0
 */
@FunctionalInterface
public interface IHasTrashOffsetDateTime
{
  /**
   * @return The trash date time or <code>null</code> if the object has not been trashed yet.
   */
  @Nullable
  OffsetDateTime getTrashDateTime ();

  /**
   * @return <code>true</code> if a trash date time is present, <code>false</code> if not.
   * @see #getTrashDateTime()
   */
  default boolean hasTrashDateTime ()
  {
    return getTrashDateTime () != null;
  }

  /**
   * Check if the object was trashed at the specified date time. This is <code>true</code>, if the
   * trash instant is &le; than the instant of the specified date time - the offsets are not
   * relevant.
   *
   * @param aDT
   *        The time to check for trashing. May not be <code>null</code>.
   * @return <code>true</code> if this object was trashed, <code>false</code> if not.
   */
  default boolean isTrashedAt (@NonNull final OffsetDateTime aDT)
  {
    ValueEnforcer.notNull (aDT, "OffsetDateTime");
    final OffsetDateTime aTrashDT = getTrashDateTime ();
    return aTrashDT != null && !aTrashDT.isAfter (aDT);
  }
}
