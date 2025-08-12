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
package com.helger.commons.statistics;

import jakarta.annotation.Nullable;

/**
 * Statistics handler for a keyed counter.
 *
 * @author Philip Helger
 */
public interface IMutableStatisticsHandlerKeyedCounter extends IStatisticsHandlerKeyedCounter
{
  /**
   * Increment by 1
   *
   * @param sKey
   *        The key to be incremented.
   */
  default void increment (@Nullable final String sKey)
  {
    increment (sKey, 1L);
  }

  /**
   * Increment by <i>n</i>
   *
   * @param sKey
   *        The key to be incremented.
   * @param nByHowMany
   *        The amount to increment. May be negative as well
   */
  void increment (@Nullable String sKey, long nByHowMany);
}
