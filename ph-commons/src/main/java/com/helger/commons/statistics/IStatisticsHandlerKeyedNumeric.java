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

import java.math.BigInteger;

import javax.annotation.CheckForSigned;
import javax.annotation.Nullable;

/**
 * Base interface for size and timer handler.
 *
 * @author Philip Helger
 */
public interface IStatisticsHandlerKeyedNumeric extends IStatisticsHandlerKeyed
{
  /**
   * @param sKey
   *        The key to be queried.
   * @return The sum of aggregated values. <code>null</code> if no such key
   *         exists.
   */
  @Nullable
  BigInteger getSum (@Nullable String sKey);

  /**
   * @param sKey
   *        The key to be queried.
   * @return The smallest value. Returns
   *         {@link com.helger.commons.CGlobal#ILLEGAL_ULONG} if no such key
   *         exists.
   */
  @CheckForSigned
  long getMin (@Nullable String sKey);

  /**
   * @param sKey
   *        The key to be queried.
   * @return The average value (=sum/invocationCount). Returns
   *         {@link com.helger.commons.CGlobal#ILLEGAL_ULONG} if no such key
   *         exists.
   */
  @CheckForSigned
  long getAverage (@Nullable String sKey);

  /**
   * @param sKey
   *        The key to be queried.
   * @return The biggest value. Returns
   *         {@link com.helger.commons.CGlobal#ILLEGAL_ULONG} if no such key
   *         exists.
   */
  @CheckForSigned
  long getMax (@Nullable String sKey);
}
