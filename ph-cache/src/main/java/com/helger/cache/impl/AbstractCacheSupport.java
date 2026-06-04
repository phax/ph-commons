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
package com.helger.cache.impl;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.statistics.api.IMutableStatisticsHandlerCache;
import com.helger.statistics.api.IMutableStatisticsHandlerCounter;
import com.helger.statistics.impl.StatisticsManager;

/**
 * Abstract base class for a mutable cache.
 *
 * @author Philip Helger
 * @since 12.3.0
 */
@ThreadSafe
public abstract class AbstractCacheSupport
{
  /** The prefix to be used for statistics elements */
  public static final String STATISTICS_PREFIX = "cache:";

  private final IMutableStatisticsHandlerCache m_aStatsCacheAccess;
  private final IMutableStatisticsHandlerCounter m_aStatsCountAdd;
  private final IMutableStatisticsHandlerCounter m_aStatsCountRemove;
  private final IMutableStatisticsHandlerCounter m_aStatsCountClear;
  private final IMutableStatisticsHandlerCounter m_aStatsCountExpired;

  protected final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  private final String m_sName;

  /**
   * Constructor with optional time-based expiration.
   *
   * @param sCacheName
   *        The internal name of the cache. May neither be <code>null</code> nor empty. This name is
   *        NOT checked for uniqueness.
   */
  protected AbstractCacheSupport (@NonNull @Nonempty final String sCacheName)
  {
    ValueEnforcer.notEmpty (sCacheName, "CacheName");

    m_aStatsCacheAccess = StatisticsManager.getCacheHandler (STATISTICS_PREFIX + sCacheName + "$access");
    m_aStatsCountAdd = StatisticsManager.getCounterHandler (STATISTICS_PREFIX + sCacheName + "$add");
    m_aStatsCountRemove = StatisticsManager.getCounterHandler (STATISTICS_PREFIX + sCacheName + "$remove");
    m_aStatsCountClear = StatisticsManager.getCounterHandler (STATISTICS_PREFIX + sCacheName + "$clear");
    m_aStatsCountExpired = StatisticsManager.getCounterHandler (STATISTICS_PREFIX + sCacheName + "$expired");

    m_sName = sCacheName;
  }

  /**
   * Increment the cache-hit counter.
   */
  protected final void incCacheHit ()
  {
    m_aStatsCacheAccess.cacheHit ();
  }

  /**
   * Increment the cache-miss counter.
   */
  protected final void incCacheMiss ()
  {
    m_aStatsCacheAccess.cacheMiss ();
  }

  /**
   * Increment the add to cache counter.
   */
  protected final void incCacheAdd ()
  {
    m_aStatsCountAdd.increment ();
  }

  /**
   * Increment the remove from cache counter.
   */
  protected final void incCacheRemove ()
  {
    m_aStatsCountRemove.increment ();
  }

  /**
   * Increment the clear cache counter.
   */
  protected final void incCacheClear ()
  {
    m_aStatsCountClear.increment ();
  }

  /**
   * Increment the expired entry in cache counter.
   */
  protected final void incCacheExpired ()
  {
    m_aStatsCountExpired.increment ();
  }

  /**
   * @return The internal name of this cache. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public final String getName ()
  {
    return m_sName;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Name", m_sName).getToString ();
  }
}
