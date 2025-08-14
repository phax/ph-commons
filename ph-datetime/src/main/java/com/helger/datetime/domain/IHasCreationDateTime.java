/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.helger.base.equals.ValueEnforcer;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Interface for objects having a creation date time.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IHasCreationDateTime
{
  /**
   * @return The maybe <code>null</code> creation date time of the object.
   *         Usually it is not <code>null</code> but in case this interface is
   *         needed for legacy objects where the information was not yet stored,
   *         it may be <code>null</code>.
   */
  @Nullable
  LocalDateTime getCreationDateTime ();

  /**
   * @return <code>true</code> if a creation date time is present,
   *         <code>false</code> if not.
   * @see #getCreationDateTime()
   */
  default boolean hasCreationDateTime ()
  {
    return getCreationDateTime () != null;
  }

  /**
   * @return The extracted date from the creation date and time or
   *         <code>null</code> if no creation date time is present.
   */
  @Nullable
  default LocalDate getCreationDate ()
  {
    final LocalDateTime aLDT = getCreationDateTime ();
    return aLDT == null ? null : aLDT.toLocalDate ();
  }

  /**
   * @return The extracted time from the creation date and time or
   *         <code>null</code> if no creation date time is present.
   */
  @Nullable
  default LocalTime getCreationTime ()
  {
    final LocalDateTime aLDT = getCreationDateTime ();
    return aLDT == null ? null : aLDT.toLocalTime ();
  }

  /**
   * Check if the object was created at the specified local date time. This is
   * <code>true</code>, if the creation time is &le; than the specified local
   * date time.
   *
   * @param aDT
   *        The time to check for creation. May not be <code>null</code>.
   * @return <code>true</code> if this object was created, <code>false</code> if
   *         not.
   * @since 9.1.6
   */
  default boolean isCreatedAt (@Nonnull final LocalDateTime aDT)
  {
    ValueEnforcer.notNull (aDT, "LocalDateTime");
    return hasCreationDateTime () && getCreationDateTime ().compareTo (aDT) <= 0;
  }
}
