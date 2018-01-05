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
package com.helger.datetime.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.annotation.Nullable;

/**
 * Interface for objects having a deletion date time.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IHasDeletionDateTime
{
  /**
   * @return The deletion date time or <code>null</code> if the object has not
   *         been deleted yet.
   */
  @Nullable
  LocalDateTime getDeletionDateTime ();

  default boolean hasDeletionDateTime ()
  {
    return getDeletionDateTime () != null;
  }

  @Nullable
  default LocalDate getDeletionDate ()
  {
    final LocalDateTime aLDT = getDeletionDateTime ();
    return aLDT == null ? null : aLDT.toLocalDate ();
  }

  @Nullable
  default LocalTime getDeletionTime ()
  {
    final LocalDateTime aLDT = getDeletionDateTime ();
    return aLDT == null ? null : aLDT.toLocalTime ();
  }
}
