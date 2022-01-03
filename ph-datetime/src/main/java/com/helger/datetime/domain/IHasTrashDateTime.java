/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;

/**
 * Interface for objects having a trash date time.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IHasTrashDateTime
{
  /**
   * @return The trash date time or <code>null</code> if the object has not been
   *         trashed yet.
   */
  @Nullable
  LocalDateTime getTrashDateTime ();

  default boolean hasTrashDateTime ()
  {
    return getTrashDateTime () != null;
  }

  @Nullable
  default LocalDate getTrashDate ()
  {
    final LocalDateTime aLDT = getTrashDateTime ();
    return aLDT == null ? null : aLDT.toLocalDate ();
  }

  @Nullable
  default LocalTime getTrashTime ()
  {
    final LocalDateTime aLDT = getTrashDateTime ();
    return aLDT == null ? null : aLDT.toLocalTime ();
  }

  /**
   * Check if the object was trashed at the specified local date time. This is
   * <code>true</code>, if the trashed time is &le; than the specified local
   * date time.
   *
   * @param aDT
   *        The time to check for trashing. May not be <code>null</code>.
   * @return <code>true</code> if this object was trashed, <code>false</code> if
   *         not.
   * @since 9.1.6
   */
  default boolean isTrashedAt (@Nonnull final LocalDateTime aDT)
  {
    ValueEnforcer.notNull (aDT, "LocalDateTime");
    return hasTrashDateTime () && getTrashDateTime ().compareTo (aDT) <= 0;
  }
}
