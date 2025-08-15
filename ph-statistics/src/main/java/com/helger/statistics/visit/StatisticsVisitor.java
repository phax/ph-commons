/*
 * Copyright (C) 2015-2025 Philip Helger (www.helger.com)
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

import java.util.Comparator;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.equals.ValueEnforcer;
import com.helger.collection.commons.ICommonsList;
import com.helger.statistics.api.IStatisticsHandlerCache;
import com.helger.statistics.api.IStatisticsHandlerCounter;
import com.helger.statistics.api.IStatisticsHandlerKeyedCounter;
import com.helger.statistics.api.IStatisticsHandlerKeyedSize;
import com.helger.statistics.api.IStatisticsHandlerKeyedTimer;
import com.helger.statistics.api.IStatisticsHandlerSize;
import com.helger.statistics.api.IStatisticsHandlerTimer;
import com.helger.statistics.impl.StatisticsManager;

import jakarta.annotation.Nonnull;

/**
 * Class for iterating all available statistics
 *
 * @author Philip Helger
 */
@Immutable
public final class StatisticsVisitor
{
  @PresentForCodeCoverage
  private static final StatisticsVisitor INSTANCE = new StatisticsVisitor ();

  private StatisticsVisitor ()
  {}

  /**
   * Walk all available statistics elements with the passed statistics visitor.
   *
   * @param aCallback
   *        The visitor to use. May not be <code>null</code>.
   */
  public static void visitStatistics (@Nonnull final IStatisticsVisitorCallback aCallback)
  {
    ValueEnforcer.notNull (aCallback, "Callback");

    // For all cache handler
    ICommonsList <String> aHandlers = StatisticsManager.getAllCacheHandler ().getSorted (Comparator.naturalOrder ());
    for (final String sName : aHandlers)
    {
      final IStatisticsHandlerCache aHandler = StatisticsManager.getCacheHandler (sName);
      aCallback.onCache (sName, aHandler);
    }

    // For all timer handler
    aHandlers = StatisticsManager.getAllTimerHandler ().getSorted (Comparator.naturalOrder ());
    for (final String sName : aHandlers)
    {
      final IStatisticsHandlerTimer aHandler = StatisticsManager.getTimerHandler (sName);
      aCallback.onTimer (sName, aHandler);
    }

    // For all keyed timer handler
    aHandlers = StatisticsManager.getAllKeyedTimerHandler ().getSorted (Comparator.naturalOrder ());
    for (final String sName : aHandlers)
    {
      final IStatisticsHandlerKeyedTimer aHandler = StatisticsManager.getKeyedTimerHandler (sName);
      aCallback.onKeyedTimer (sName, aHandler);
    }

    // For all size handler
    aHandlers = StatisticsManager.getAllSizeHandler ().getSorted (Comparator.naturalOrder ());
    for (final String sName : aHandlers)
    {
      final IStatisticsHandlerSize aHandler = StatisticsManager.getSizeHandler (sName);
      aCallback.onSize (sName, aHandler);
    }

    // For all keyed size handler
    aHandlers = StatisticsManager.getAllKeyedSizeHandler ().getSorted (Comparator.naturalOrder ());
    for (final String sName : aHandlers)
    {
      final IStatisticsHandlerKeyedSize aHandler = StatisticsManager.getKeyedSizeHandler (sName);
      aCallback.onKeyedSize (sName, aHandler);
    }

    // For all counter handler
    aHandlers = StatisticsManager.getAllCounterHandler ().getSorted (Comparator.naturalOrder ());
    for (final String sName : aHandlers)
    {
      final IStatisticsHandlerCounter aHandler = StatisticsManager.getCounterHandler (sName);
      aCallback.onCounter (sName, aHandler);
    }

    // For all keyed counter handler
    aHandlers = StatisticsManager.getAllKeyedCounterHandler ().getSorted (Comparator.naturalOrder ());
    for (final String sName : aHandlers)
    {
      final IStatisticsHandlerKeyedCounter aHandler = StatisticsManager.getKeyedCounterHandler (sName);
      aCallback.onKeyedCounter (sName, aHandler);
    }
  }
}
