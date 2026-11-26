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
 * Interface for objects having a deletion date time with an offset. Other than
 * {@link IHasDeletionDateTime}, the value is unambiguous, so it can be compared across time zones
 * and daylight saving time changes.
 *
 * @author Philip Helger
 * @since 12.9.0
 */
@FunctionalInterface
public interface IHasDeletionOffsetDateTime
{
  /**
   * @return The deletion date time or <code>null</code> if the object was not deleted.
   */
  @Nullable
  OffsetDateTime getDeletionDateTime ();

  /**
   * @return <code>true</code> if a deletion date time is present, <code>false</code> if not.
   * @see #getDeletionDateTime()
   */
  default boolean hasDeletionDateTime ()
  {
    return getDeletionDateTime () != null;
  }

  /**
   * Check if the object was deleted at the specified date time. This is <code>true</code>, if the
   * deletion instant is &le; than the instant of the specified date time - the offsets are not
   * relevant.
   *
   * @param aDT
   *        The time to check for deletion. May not be <code>null</code>.
   * @return <code>true</code> if this object was deleted, <code>false</code> if not.
   */
  default boolean isDeletedAt (@NonNull final OffsetDateTime aDT)
  {
    ValueEnforcer.notNull (aDT, "OffsetDateTime");
    final OffsetDateTime aDeletionDT = getDeletionDateTime ();
    return aDeletionDT != null && !aDeletionDT.isAfter (aDT);
  }
}
