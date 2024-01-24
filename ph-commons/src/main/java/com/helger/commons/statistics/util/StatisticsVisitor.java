/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.statistics.IStatisticsHandlerCache;
import com.helger.commons.statistics.IStatisticsHandlerCounter;
import com.helger.commons.statistics.IStatisticsHandlerKeyedCounter;
import com.helger.commons.statistics.IStatisticsHandlerKeyedSize;
import com.helger.commons.statistics.IStatisticsHandlerKeyedTimer;
import com.helger.commons.statistics.IStatisticsHandlerSize;
import com.helger.commons.statistics.IStatisticsHandlerTimer;
import com.helger.commons.statistics.StatisticsManager;

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
