/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.cache;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.ELockType;
import com.helger.commons.annotations.MustBeLocked;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.OverrideOnDemand;
import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.jmx.JMXUtils;
import com.helger.commons.state.EChange;
import com.helger.commons.stats.IStatisticsHandlerCache;
import com.helger.commons.stats.IStatisticsHandlerCounter;
import com.helger.commons.stats.StatisticsManager;
import com.helger.commons.string.ToStringGenerator;

/**
 * Abstract base implementation of {@link ISimpleCache}
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The cache key type
 * @param <VALUETYPE>
 *        The cache value type
 */
@ThreadSafe
public abstract class AbstractCache <KEYTYPE, VALUETYPE> implements ISimpleCache <KEYTYPE, VALUETYPE>
{
  /** By default JMS is disabled */
  public static final boolean DEFAULT_JMX_ENABLED = false;
  /** The prefix to be used for statistics elements */
  public static final String STATISTICS_PREFIX = "cache:";

  private static final AtomicBoolean s_aJMXEnabled = new AtomicBoolean (DEFAULT_JMX_ENABLED);

  protected final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final String m_sCacheName;
  protected final IStatisticsHandlerCache m_aCacheAccessStats;
  private final IStatisticsHandlerCounter m_aCacheRemoveStats;
  private final IStatisticsHandlerCounter m_aCacheClearStats;
  private volatile Map <KEYTYPE, VALUETYPE> m_aCache;

  public AbstractCache (@Nonnull @Nonempty final String sCacheName)
  {
    m_sCacheName = ValueEnforcer.notEmpty (sCacheName, "cacheName");
    m_aCacheAccessStats = StatisticsManager.getCacheHandler (STATISTICS_PREFIX + sCacheName + "$access");
    m_aCacheRemoveStats = StatisticsManager.getCounterHandler (STATISTICS_PREFIX + sCacheName + "$remove");
    m_aCacheClearStats = StatisticsManager.getCounterHandler (STATISTICS_PREFIX + sCacheName + "$clear");
    if (isJMXEnabled ())
      JMXUtils.exposeMBeanWithAutoName (new SimpleCache (this), sCacheName);
  }

  public static boolean isJMXEnabled ()
  {
    return s_aJMXEnabled.get ();
  }

  public static void setJMXEnabled (final boolean bEnabled)
  {
    s_aJMXEnabled.set (bEnabled);
  }

  @Nonnull
  @Nonempty
  public final String getName ()
  {
    return m_sCacheName;
  }

  /**
   * Create a new cache map.
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  @OverrideOnDemand
  protected Map <KEYTYPE, VALUETYPE> createCache ()
  {
    return new WeakHashMap <KEYTYPE, VALUETYPE> ();
  }

  /**
   * Put a new value into the cache.
   *
   * @param aKey
   *        The cache key. May not be <code>null</code>.
   * @param aValue
   *        The cache value. May not be <code>null</code>.
   */
  @MustBeLocked (ELockType.WRITE)
  protected final void putInCacheNotLocked (@Nonnull final KEYTYPE aKey, @Nonnull final VALUETYPE aValue)
  {
    ValueEnforcer.notNull (aKey, "cacheKey");
    ValueEnforcer.notNull (aValue, "cacheValue");

    // try again in write lock
    if (m_aCache == null)
    {
      // Create a new map to cache the objects
      m_aCache = createCache ();
      if (m_aCache == null)
        throw new IllegalStateException ("No cache created!");
    }
    m_aCache.put (aKey, aValue);
  }

  /**
   * Put a new value into the cache.
   *
   * @param aKey
   *        The cache key. May not be <code>null</code>.
   * @param aValue
   *        The cache value. May not be <code>null</code>.
   */
  protected final void putInCache (@Nonnull final KEYTYPE aKey, @Nonnull final VALUETYPE aValue)
  {
    ValueEnforcer.notNull (aKey, "cacheKey");
    ValueEnforcer.notNull (aValue, "cacheValue");

    m_aRWLock.writeLock ().lock ();
    try
    {
      putInCacheNotLocked (aKey, aValue);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @MustBeLocked (ELockType.READ)
  @Nullable
  protected final VALUETYPE getFromCacheNoStatsNotLocked (@Nullable final KEYTYPE aKey)
  {
    // Since null is not allowed as value, we don't need to check with
    // containsKey before get!
    return m_aCache == null ? null : m_aCache.get (aKey);
  }

  @Nullable
  @OverridingMethodsMustInvokeSuper
  protected final VALUETYPE getFromCacheNoStats (@Nullable final KEYTYPE aKey)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return getFromCacheNoStatsNotLocked (aKey);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  private void _updateStats (final boolean bMiss)
  {
    if (bMiss)
      m_aCacheAccessStats.cacheMiss ();
    else
      m_aCacheAccessStats.cacheHit ();
  }

  @Nullable
  protected final VALUETYPE getFromCacheNotLocked (@Nullable final KEYTYPE aKey)
  {
    final VALUETYPE aValue = getFromCacheNoStatsNotLocked (aKey);
    _updateStats (aValue == null);
    return aValue;
  }

  @Nullable
  @OverridingMethodsMustInvokeSuper
  public VALUETYPE getFromCache (final KEYTYPE aKey)
  {
    final VALUETYPE aValue = getFromCacheNoStats (aKey);
    _updateStats (aValue == null);
    return aValue;
  }

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public EChange removeFromCache (final KEYTYPE aKey)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      if (m_aCache == null || m_aCache.remove (aKey) == null)
        return EChange.UNCHANGED;
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
    m_aCacheRemoveStats.increment ();
    return EChange.CHANGED;
  }

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public EChange clearCache ()
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      if (m_aCache == null || m_aCache.isEmpty ())
        return EChange.UNCHANGED;

      m_aCache.clear ();
      m_aCacheClearStats.increment ();
      return EChange.CHANGED;
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnegative
  public int size ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return CollectionHelper.getSize (m_aCache);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  public boolean isEmpty ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return CollectionHelper.isEmpty (m_aCache);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  public boolean isNotEmpty ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return CollectionHelper.isNotEmpty (m_aCache);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("cacheName", m_sCacheName).append ("content", m_aCache).toString ();
  }
}
