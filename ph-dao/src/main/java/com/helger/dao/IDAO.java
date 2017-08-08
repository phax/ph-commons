/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.dao;

import java.time.LocalDateTime;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

/**
 * Base DAO interface. DAO = Data Access Object. A DAO implementation usually
 * has 3 basic operations:
 * <ul>
 * <li>Initialization - when no file exists and an initial setup can be
 * performed.</li>
 * <li>Reading - read existing data from a file</li>
 * <li>Write - write modified data to a file</li>
 * </ul>
 *
 * @author Philip Helger
 */
public interface IDAO extends IChangeable, IAutoSaveAware
{
  public enum EMode
  {
    READ,
    WRITE;
  }

  /**
   * This method is called to persist the content in disk in case there are
   * pending changes. If no pending changes are present, nothing happens. In
   * case the implementation is thread-safe, this method must be thread-safe!
   */
  void writeToFileOnPendingChanges ();

  /**
   * @return The number of times this DAO was initialized. Always &ge; 0.
   *         Usually this field is not persistent and only is valid until the
   *         application ends.
   */
  @Nonnegative
  int getInitCount ();

  /**
   * @return The last time this DAO was initialized (without error). May be
   *         <code>null</code> if it wasn't read before. Usually this field is
   *         not persistent and only is valid until the application ends.
   */
  @Nullable
  LocalDateTime getLastInitDateTime ();

  /**
   * @return The number of times this DAO was initialized. Always &ge; 0.
   *         Usually this field is not persistent and only is valid until the
   *         application ends.
   */
  @Nonnegative
  int getReadCount ();

  /**
   * @return The last time this DAO was read (without error). May be
   *         <code>null</code> if it wasn't read before. Usually this field is
   *         not persistent and only is valid until the application ends.
   */
  @Nullable
  LocalDateTime getLastReadDateTime ();

  /**
   * @return The number of times this DAO was initialized. Always &ge; 0.
   *         Usually this field is not persistent and only is valid until the
   *         application ends.
   */
  @Nonnegative
  int getWriteCount ();

  /**
   * @return The last time this DAO was written (without error). May be
   *         <code>null</code> if it wasn't written before. Usually this field
   *         is not persistent and only is valid until the application ends.
   */
  @Nullable
  LocalDateTime getLastWriteDateTime ();
}
