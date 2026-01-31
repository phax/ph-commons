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
package com.helger.statistics.visit;

import org.jspecify.annotations.NonNull;

import com.helger.base.callback.ICallback;
import com.helger.statistics.api.IStatisticsHandlerCache;
import com.helger.statistics.api.IStatisticsHandlerCounter;
import com.helger.statistics.api.IStatisticsHandlerKeyedCounter;
import com.helger.statistics.api.IStatisticsHandlerKeyedSize;
import com.helger.statistics.api.IStatisticsHandlerKeyedTimer;
import com.helger.statistics.api.IStatisticsHandlerSize;
import com.helger.statistics.api.IStatisticsHandlerTimer;

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
  default void onCache (@NonNull final String sName, @NonNull final IStatisticsHandlerCache aHandler)
  {}

  /**
   * Called per timer entry
   * 
   * @param sName
   *        Name. Never <code>null</code>.
   * @param aHandler
   *        Statistics handler. Never <code>null</code>.
   */
  default void onTimer (@NonNull final String sName, @NonNull final IStatisticsHandlerTimer aHandler)
  {}

  /**
   * Called per keyed-timer entry
   * 
   * @param sName
   *        Name. Never <code>null</code>.
   * @param aHandler
   *        Statistics handler. Never <code>null</code>.
   */
  default void onKeyedTimer (@NonNull final String sName, @NonNull final IStatisticsHandlerKeyedTimer aHandler)
  {}

  /**
   * Called per size entry
   * 
   * @param sName
   *        Name. Never <code>null</code>.
   * @param aHandler
   *        Statistics handler. Never <code>null</code>.
   */
  default void onSize (@NonNull final String sName, @NonNull final IStatisticsHandlerSize aHandler)
  {}

  /**
   * Called per keyed-size entry
   * 
   * @param sName
   *        Name. Never <code>null</code>.
   * @param aHandler
   *        Statistics handler. Never <code>null</code>.
   */
  default void onKeyedSize (@NonNull final String sName, @NonNull final IStatisticsHandlerKeyedSize aHandler)
  {}

  /**
   * Called per counter entry
   * 
   * @param sName
   *        Name. Never <code>null</code>.
   * @param aHandler
   *        Statistics handler. Never <code>null</code>.
   */
  default void onCounter (@NonNull final String sName, @NonNull final IStatisticsHandlerCounter aHandler)
  {}

  /**
   * Called per keyed-counter entry
   * 
   * @param sName
   *        Name. Never <code>null</code>.
   * @param aHandler
   *        Statistics handler. Never <code>null</code>.
   */
  default void onKeyedCounter (@NonNull final String sName, @NonNull final IStatisticsHandlerKeyedCounter aHandler)
  {}
}
