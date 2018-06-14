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
package com.helger.commons.cache;

import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.ELockType;
import com.helger.commons.annotation.MustBeLocked;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.map.SoftHashMap;
import com.helger.commons.collection.map.SoftLinkedHashMap;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.functional.IFunction;
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
public class Cache <KEYTYPE, VALUETYPE> implements IMutableCache <KEYTYPE, VALUETYPE>
{
  /** The prefix to be used for statistics elements */
  public static final String STATISTICS_PREFIX = "cache:";

  private static final Logger s_aLogger = LoggerFactory.getLogger (Cache.class);

  protected final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  private final IFunction <KEYTYPE, VALUETYPE> m_aCacheValueProvider;
  private final int m_nMaxSize;
  private final String m_sName;
  private final IMutableStatisticsHandlerCache m_aCacheAccessStats;
  private final IMutableStatisticsHandlerCounter m_aCacheRemoveStats;
  private final IMutableStatisticsHandlerCounter m_aCacheClearStats;
  @CodingStyleguideUnaware
  private Map <KEYTYPE, VALUETYPE> m_aCache;

  public Cache (@Nonnull final IFunction <KEYTYPE, VALUETYPE> aCacheValueProvider,
                final int nMaxSize,
                @Nonnull @Nonempty final String sCacheName)
  {
    m_aCacheValueProvider = ValueEnforcer.notNull (aCacheValueProvider, "CacheValueProvider");
    m_nMaxSize = nMaxSize;
    m_sName = ValueEnforcer.notEmpty (sCacheName, "CacheName");
    m_aCacheAccessStats = StatisticsManager.getCacheHandler (STATISTICS_PREFIX + sCacheName + "$access");
    m_aCacheRemoveStats = StatisticsManager.getCounterHandler (STATISTICS_PREFIX + sCacheName + "$remove");
    m_aCacheClearStats = StatisticsManager.getCounterHandler (STATISTICS_PREFIX + sCacheName + "$clear");
  }

  /**
   * @return The maximum number of entries allowed in this cache. Values &le; 0
   *         indicate that the cache size is not limited at all.
   * @see #hasMaxSize()
   */
  public final int getMaxSize ()
  {
    // No need to lock, as it is final
    return m_nMaxSize;
  }

  /**
   * @return <code>true</code> if this cache has a size limit,
   *         <code>false</code> if not.
   * @see #getMaxSize()
   */
  public final boolean hasMaxSize ()
  {
    // No need to lock, as it is final
    return m_nMaxSize > 0;
  }

  @Nonnull
  @Nonempty
  public final String getName ()
  {
    return m_sName;
  }

  /**
   * Create a new cache map. This is the internal map that is used to store the
   * items.
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  @OverrideOnDemand
  @CodingStyleguideUnaware
  protected ICommonsMap <KEYTYPE, VALUETYPE> createCache ()
  {
    return hasMaxSize () ? new SoftLinkedHashMap <> (m_nMaxSize) : new SoftHashMap <> ();
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

  @Nullable
  @OverridingMethodsMustInvokeSuper
  public VALUETYPE getFromCache (final KEYTYPE aKey)
  {
    VALUETYPE aValue = getFromCacheNoStats (aKey);
    if (aValue == null)
    {
      // No old value in the cache
      m_aRWLock.writeLock ().lock ();
      try
      {
        // Read again, in case the value was set between the two locking
        // sections
        // Note: do not increase statistics in this second try
        aValue = getFromCacheNoStatsNotLocked (aKey);
        if (aValue == null)
        {
          // Call the abstract method to create the value to cache
          aValue = m_aCacheValueProvider.apply (aKey);

          // Just a consistency check
          if (aValue == null)
            throw new IllegalStateException ("The value to cache was null for key '" + aKey + "'");

          // Put the new value into the cache
          putInCacheNotLocked (aKey, aValue);
          m_aCacheAccessStats.cacheMiss ();
        }
        else
          m_aCacheAccessStats.cacheHit ();
      }
      finally
      {
        m_aRWLock.writeLock ().unlock ();
      }
    }
    else
      m_aCacheAccessStats.cacheHit ();
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
    m_aRWLock.writeLock ().lock ();
    try
    {
      if (m_aCache == null || m_aCache.isEmpty ())
        return EChange.UNCHANGED;

      m_aCache.clear ();
      m_aCacheClearStats.increment ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Cache '" + m_sName + "' was cleared");
    return EChange.CHANGED;
  }

  @Nonnegative
  public int size ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.getSize (m_aCache));
  }

  public boolean isEmpty ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.isEmpty (m_aCache));
  }

  @Override
  public boolean isNotEmpty ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.isNotEmpty (m_aCache));
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("CacheValueProvider", m_aCacheValueProvider)
                                       .append ("Name", m_sName)
                                       .append ("MaxSize", m_nMaxSize)
                                       .append ("Cache", m_aCache)
                                       .getToString ();
  }
}
