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
package com.helger.commons.cache;

import java.util.Map;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ELockType;
import com.helger.commons.annotation.MustBeLocked;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.SoftHashMap;
import com.helger.commons.collection.impl.SoftLinkedHashMap;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.state.EChange;
import com.helger.commons.statistics.IMutableStatisticsHandlerCache;
import com.helger.commons.statistics.IMutableStatisticsHandlerCounter;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.string.ToStringGenerator;

/**
 * Abstract base implementation of {@link ICache}
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The cache key type
 * @param <VALUETYPE>
 *        The cache value type
 */
@ThreadSafe
public abstract class AbstractCache <KEYTYPE, VALUETYPE> implements IMutableCache <KEYTYPE, VALUETYPE>
{
  /** The prefix to be used for statistics elements */
  public static final String STATISTICS_PREFIX = "cache:";

  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractCache.class);

  protected final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  private final int m_nMaxSize;
  private final String m_sCacheName;
  protected final IMutableStatisticsHandlerCache m_aCacheAccessStats;
  private final IMutableStatisticsHandlerCounter m_aCacheRemoveStats;
  private final IMutableStatisticsHandlerCounter m_aCacheClearStats;
  private Map <KEYTYPE, VALUETYPE> m_aCache;

  public AbstractCache (@Nonnull final String sCacheName)
  {
    this (CGlobal.ILLEGAL_UINT, sCacheName);
  }

  public AbstractCache (@CheckForSigned final int nMaxSize, @Nonnull @Nonempty final String sCacheName)
  {
    m_nMaxSize = nMaxSize;
    m_sCacheName = ValueEnforcer.notEmpty (sCacheName, "cacheName");
    m_aCacheAccessStats = StatisticsManager.getCacheHandler (STATISTICS_PREFIX + sCacheName + "$access");
    m_aCacheRemoveStats = StatisticsManager.getCounterHandler (STATISTICS_PREFIX + sCacheName + "$remove");
    m_aCacheClearStats = StatisticsManager.getCounterHandler (STATISTICS_PREFIX + sCacheName + "$clear");
  }

  public final int getMaxSize ()
  {
    // No need to lock, as it is final
    return m_nMaxSize;
  }

  public final boolean hasMaxSize ()
  {
    // No need to lock, as it is final
    return m_nMaxSize > 0;
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
  @ReturnsMutableCopy
  @OverrideOnDemand
  protected Map <KEYTYPE, VALUETYPE> createCache ()
  {
    return hasMaxSize () ? new SoftLinkedHashMap <KEYTYPE, VALUETYPE> (m_nMaxSize)
                         : new SoftHashMap <KEYTYPE, VALUETYPE> ();
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

    m_aRWLock.writeLocked ( () -> putInCacheNotLocked (aKey, aValue));
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
    return m_aRWLock.readLocked ( () -> getFromCacheNoStatsNotLocked (aKey));
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
    return m_aRWLock.writeLocked ( () -> {
      if (m_aCache == null || m_aCache.remove (aKey) == null)
        return EChange.UNCHANGED;
      m_aCacheRemoveStats.increment ();
      return EChange.CHANGED;
    });
  }

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public EChange clearCache ()
  {
    return m_aRWLock.writeLocked ( () -> {
      if (m_aCache == null || m_aCache.isEmpty ())
        return EChange.UNCHANGED;

      m_aCache.clear ();
      m_aCacheClearStats.increment ();

      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("Cache was cleared: " + getClass ().getName ());

      return EChange.CHANGED;
    });
  }

  @Nonnegative
  public int getSize ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.getSize (m_aCache));
  }

  public boolean isEmpty ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.isEmpty (m_aCache));
  }

  public boolean isNotEmpty ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.isNotEmpty (m_aCache));
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("cacheName", m_sCacheName).append ("content", m_aCache).toString ();
  }
}
