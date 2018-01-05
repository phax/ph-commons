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
package com.helger.commons.statistics.util;

import javax.annotation.Nonnull;

import com.helger.commons.callback.ICallback;
import com.helger.commons.statistics.IStatisticsHandlerCache;
import com.helger.commons.statistics.IStatisticsHandlerCounter;
import com.helger.commons.statistics.IStatisticsHandlerKeyedCounter;
import com.helger.commons.statistics.IStatisticsHandlerKeyedSize;
import com.helger.commons.statistics.IStatisticsHandlerKeyedTimer;
import com.helger.commons.statistics.IStatisticsHandlerSize;
import com.helger.commons.statistics.IStatisticsHandlerTimer;

/**
 * Callback interface for visiting statistics.
 *
 * @author Philip Helger
 */
public interface IStatisticsVisitorCallback extends ICallback
{
  /**
   * Called per cache entry
   * 
   * @param sName
   *        Name. Never <code>null</code>.
   * @param aHandler
   *        Statistics handler. Never <code>null</code>.
   */
  default void onCache (@Nonnull final String sName, @Nonnull final IStatisticsHandlerCache aHandler)
  {}

  /**
   * Called per timer entry
   * 
   * @param sName
   *        Name. Never <code>null</code>.
   * @param aHandler
   *        Statistics handler. Never <code>null</code>.
   */
  default void onTimer (@Nonnull final String sName, @Nonnull final IStatisticsHandlerTimer aHandler)
  {}

  /**
   * Called per keyed-timer entry
   * 
   * @param sName
   *        Name. Never <code>null</code>.
   * @param aHandler
   *        Statistics handler. Never <code>null</code>.
   */
  default void onKeyedTimer (@Nonnull final String sName, @Nonnull final IStatisticsHandlerKeyedTimer aHandler)
  {}

  /**
   * Called per size entry
   * 
   * @param sName
   *        Name. Never <code>null</code>.
   * @param aHandler
   *        Statistics handler. Never <code>null</code>.
   */
  default void onSize (@Nonnull final String sName, @Nonnull final IStatisticsHandlerSize aHandler)
  {}

  /**
   * Called per keyed-size entry
   * 
   * @param sName
   *        Name. Never <code>null</code>.
   * @param aHandler
   *        Statistics handler. Never <code>null</code>.
   */
  default void onKeyedSize (@Nonnull final String sName, @Nonnull final IStatisticsHandlerKeyedSize aHandler)
  {}

  /**
   * Called per counter entry
   * 
   * @param sName
   *        Name. Never <code>null</code>.
   * @param aHandler
   *        Statistics handler. Never <code>null</code>.
   */
  default void onCounter (@Nonnull final String sName, @Nonnull final IStatisticsHandlerCounter aHandler)
  {}

  /**
   * Called per keyed-counter entry
   * 
   * @param sName
   *        Name. Never <code>null</code>.
   * @param aHandler
   *        Statistics handler. Never <code>null</code>.
   */
  default void onKeyedCounter (@Nonnull final String sName, @Nonnull final IStatisticsHandlerKeyedCounter aHandler)
  {}
}
