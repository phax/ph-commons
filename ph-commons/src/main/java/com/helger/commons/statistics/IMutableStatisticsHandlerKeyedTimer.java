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
package com.helger.commons.statistics;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

/**
 * Statistics handler for keyed timer.
 *
 * @author Philip Helger
 */
public interface IMutableStatisticsHandlerKeyedTimer extends IStatisticsHandlerKeyedTimer
{
  /**
   * Add a single execution time.
   *
   * @param sKey
   *        The key to be incremented.
   * @param nMillis
   *        The milli seconds it took to execute something. Should not be
   *        negative.
   */
  void addTime (@Nullable String sKey, @Nonnegative long nMillis);
}
