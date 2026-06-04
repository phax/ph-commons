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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.annotation.concurrent.ELockType;
import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.IsLocked;
import com.helger.annotation.concurrent.MustBeLocked;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.cache.ICache;
import com.helger.cache.IMutableCache;
import com.helger.collection.CollectionHelper;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.map.SoftHashMap;
import com.helger.collection.map.SoftLinkedHashMap;
import com.helger.statistics.api.IMutableStatisticsHandlerCache;
import com.helger.statistics.api.IMutableStatisticsHandlerCounter;
import com.helger.statistics.impl.StatisticsManager;

/**
 * Base implementation of {@link ICache} and {@link IMutableCache}.
 *
 * @author Philip Helger
 * @since 9.3.8 generalized from the existing {@link Cache} class.
 * @param <KEYTYPE>
 *        The cache source type
 * @param <KEYSTORETYPE>
 *        The internal storage key type
 * @param <VALUETYPE>
 *        The cache value type
 */
@ThreadSafe
public class MappedCache <KEYTYPE, KEYSTORETYPE, VALUETYPE> implements IMutableCache <KEYTYPE, VALUETYPE>
{
  /** The prefix to be used for statistics elements */
  public static final String STATISTICS_PREFIX = "cache:";
  /** A constant indicating, that a cache has no max size */
  public static final int NO_MAX_SIZE = 0;

  private static final Logger LOGGER = LoggerFactory.getLogger (MappedCache.class);

  private final IMutableStatisticsHandlerCache m_aStatsCacheAccess;
  private final IMutableStatisticsHandlerCounter m_aStatsCountRemove;
  private final IMutableStatisticsHandlerCounter m_aStatsCountClear;
  private final IMutableStatisticsHandlerCounter m_aStatsCountExpired;

  protected final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  private final Function <KEYTYPE, KEYSTORETYPE> m_aCacheKeyProvider;
  private final Function <KEYTYPE, VALUETYPE> m_aValueProvider;
  private final int m_nMaxSize;
  private final String m_sName;
  private final boolean m_bAllowNullValues;
  private final Duration m_aTimeToLive;
  // Clock supplier; volatile to allow tests to inject a deterministic clock
  // without locking on the hot path. UTC is used to avoid DST jumps in the
  // expiration arithmetic; the absolute time zone is irrelevant because the
  // same supplier is used for writes and expiry checks.
  private volatile Supplier <LocalDateTime> m_aClockSupplier = () -> LocalDateTime.now (ZoneOffset.UTC);
  // Status vars
  // The main cache. The presence of a CacheEntry is the marker for
  // "key is in the cache" (including cached null values). Lazily created.
  @GuardedBy ("m_aRWLock")
  private ICommonsMap <KEYSTORETYPE, CacheEntry <VALUETYPE>> m_aCache;

  /**
   * Constructor without time-based expiration.
   *
   * @param aCacheKeyProvider
   *        The cache key provider, that takes any KEYTYPE and creates a non-<code>null</code>
   *        KEYSTORETYPE instance. May not be <code>null</code>.
   * @param aValueProvider
   *        The cache value provider. The value to be cached may be <code>null</code> depending on
   *        the parameter {@code bAllowNullValues}. May not be <code>null</code>.
   * @param nMaxSize
   *        The maximum size of the cache. All values &le; 0 indicate an unlimited size.
   * @param sCacheName
   *        The internal name of the cache. May neither be <code>null</code> nor empty. This name is
   *        NOT checked for uniqueness.
   * @param bAllowNullValues
   *        <code>true</code> if <code>null</code> values are allowed to be in the cache,
   *        <code>false</code> if not.
   */
  public MappedCache (@NonNull final Function <KEYTYPE, KEYSTORETYPE> aCacheKeyProvider,
                      @NonNull final Function <KEYTYPE, VALUETYPE> aValueProvider,
                      @CheckForSigned final int nMaxSize,
                      @NonNull @Nonempty final String sCacheName,
                      final boolean bAllowNullValues)
  {
    this (aCacheKeyProvider, aValueProvider, nMaxSize, sCacheName, bAllowNullValues, null);
  }

  /**
   * Constructor with optional time-based expiration.
   *
   * @param aCacheKeyProvider
   *        The cache key provider, that takes any KEYTYPE and creates a non-<code>null</code>
   *        KEYSTORETYPE instance. May not be <code>null</code>.
   * @param aValueProvider
   *        The cache value provider. The value to be cached may be <code>null</code> depending on
   *        the parameter {@code bAllowNullValues}. May not be <code>null</code>.
   * @param nMaxSize
   *        The maximum size of the cache. All values &le; 0 indicate an unlimited size.
   * @param sCacheName
   *        The internal name of the cache. May neither be <code>null</code> nor empty. This name is
   *        NOT checked for uniqueness.
   * @param bAllowNullValues
   *        <code>true</code> if <code>null</code> values are allowed to be in the cache,
   *        <code>false</code> if not.
   * @param aTimeToLive
   *        Time after which a cache entry is considered expired (counted from the time it was put
   *        in the cache). May be <code>null</code> or zero or negative to disable time-based
   *        expiration.
   * @since 12.3.0
   */
  public MappedCache (@NonNull final Function <KEYTYPE, KEYSTORETYPE> aCacheKeyProvider,
                      @NonNull final Function <KEYTYPE, VALUETYPE> aValueProvider,
                      @CheckForSigned final int nMaxSize,
                      @NonNull @Nonempty final String sCacheName,
                      final boolean bAllowNullValues,
                      @Nullable final Duration aTimeToLive)
  {
    ValueEnforcer.notNull (aCacheKeyProvider, "CacheKeyProvider");
    ValueEnforcer.notNull (aValueProvider, "ValueProvider");
    ValueEnforcer.notEmpty (sCacheName, "CacheName");

    m_aStatsCacheAccess = StatisticsManager.getCacheHandler (STATISTICS_PREFIX + sCacheName + "$access");
    m_aStatsCountRemove = StatisticsManager.getCounterHandler (STATISTICS_PREFIX + sCacheName + "$remove");
    m_aStatsCountClear = StatisticsManager.getCounterHandler (STATISTICS_PREFIX + sCacheName + "$clear");
    m_aStatsCountExpired = StatisticsManager.getCounterHandler (STATISTICS_PREFIX + sCacheName + "$expired");

    m_aCacheKeyProvider = aCacheKeyProvider;
    m_aValueProvider = aValueProvider;
    m_nMaxSize = nMaxSize;
    m_sName = sCacheName;
    m_bAllowNullValues = bAllowNullValues;
    m_aTimeToLive = aTimeToLive != null && !aTimeToLive.isZero () && !aTimeToLive.isNegative () ? aTimeToLive : null;
  }

  /**
   * @return The cache key provider from the constructor. Never <code>null</code>.
   * @since 9.3.8
   */
  @NonNull
  protected final Function <KEYTYPE, KEYSTORETYPE> getCacheKeyProvider ()
  {
    return m_aCacheKeyProvider;
  }

  /**
   * @return The cache value provider from the constructor. Never <code>null</code>.
   * @since 9.3.8
   */
  @NonNull
  protected final Function <KEYTYPE, VALUETYPE> getValueProvider ()
  {
    return m_aValueProvider;
  }

  /**
   * @return The maximum number of entries allowed in this cache. Values &le; 0 indicate that the
   *         cache size is not limited at all.
   * @see #hasMaxSize()
   */
  public final int getMaxSize ()
  {
    // No need to lock, as it is final
    return m_nMaxSize;
  }

  /**
   * @return <code>true</code> if this cache has a size limit, <code>false</code> if not.
   * @see #getMaxSize()
   */
  public final boolean hasMaxSize ()
  {
    // No need to lock, as it is final
    return m_nMaxSize > 0;
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

  /**
   * @return <code>true</code> if <code>null</code> can be in the cache, <code>false</code> if not.
   * @since 9.3.8
   */
  public final boolean isAllowNullValues ()
  {
    return m_bAllowNullValues;
  }

  /**
   * @return The configured time to live for cache entries, or <code>null</code> if no time-based
   *         expiration is configured.
   * @since 12.3.0
   */
  @Nullable
  public final Duration getTimeToLive ()
  {
    return m_aTimeToLive;
  }

  /**
   * @return <code>true</code> if a positive time to live is configured, <code>false</code>
   *         otherwise.
   * @since 12.3.0
   */
  public final boolean hasTimeToLive ()
  {
    return m_aTimeToLive != null;
  }

  /**
   * @return The clock supplier currently in use. Never <code>null</code>.
   * @since 12.3.0
   */
  @NonNull
  public final Supplier <LocalDateTime> getClockSupplier ()
  {
    return m_aClockSupplier;
  }

  /**
   * Set the clock supplier used to determine "now" when checking for time-based expiration. Mainly
   * intended for deterministic unit tests; production code should rely on the default
   * ({@link LocalDateTime#now()}).
   *
   * @param aClockSupplier
   *        The clock supplier. May not be <code>null</code>.
   * @since 12.3.0
   */
  public final void setClockSupplier (@NonNull final Supplier <LocalDateTime> aClockSupplier)
  {
    ValueEnforcer.notNull (aClockSupplier, "ClockSupplier");
    m_aClockSupplier = aClockSupplier;
  }

  /**
   * Create a new cache map. This is the internal map that is used to store the items.
   *
   * @return Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  @OverrideOnDemand
  @CodingStyleguideUnaware
  protected ICommonsMap <KEYSTORETYPE, CacheEntry <VALUETYPE>> createCache ()
  {
    return hasMaxSize () ? new SoftLinkedHashMap <> (m_nMaxSize) : new SoftHashMap <> ();
  }

  @NonNull
  @Nonempty
  private String _getCacheLogText ()
  {
    final StringBuilder ret = new StringBuilder ("Cache '").append (m_sName).append ("'");
    if (hasMaxSize ())
      ret.append (" with max size of ").append (m_nMaxSize);
    return ret.append (": ").toString ();
  }

  /**
   * Put a new entry into the cache.
   *
   * @param aCacheKey
   *        The cache key. May not be <code>null</code>.
   * @param aCacheEntry
   *        The cache entry. May not be <code>null</code>.
   */
  @MustBeLocked (ELockType.WRITE)
  protected final void putInCacheNotLocked (@NonNull final KEYSTORETYPE aCacheKey,
                                            @NonNull final CacheEntry <VALUETYPE> aCacheEntry)
  {
    ValueEnforcer.notNull (aCacheKey, "CacheKey");
    ValueEnforcer.notNull (aCacheEntry, "CacheEntry");

    // try again in write lock
    if (m_aCache == null)
    {
      // Lazily create a new map to cache the objects
      m_aCache = createCache ();
      if (m_aCache == null)
        throw new IllegalStateException (_getCacheLogText () + "Failed to create internal Map!");
    }
    m_aCache.put (aCacheKey, aCacheEntry);
  }

  @NonNull
  private KEYSTORETYPE _getCacheKeyNonNull (final KEYTYPE aKey)
  {
    final KEYSTORETYPE aCacheKey = m_aCacheKeyProvider.apply (aKey);
    if (aCacheKey == null)
      throw new IllegalStateException (_getCacheLogText () + "The created cache key of '" + aKey + "' is null.");
    return aCacheKey;
  }

  @NonNull
  private CacheEntry <VALUETYPE> _buildCacheEntry (final KEYTYPE aKey, final VALUETYPE aValue)
  {
    if (aValue == null && !m_bAllowNullValues)
      throw new IllegalStateException (_getCacheLogText () +
                                       "The created cache value of key '" +
                                       aKey +
                                       "' is null. null values are not allowed in this cache.");
    if (m_aTimeToLive == null)
      return CacheEntry.ofNoExpiration (aValue);
    return CacheEntry.ofTimeToLive (aValue, m_aClockSupplier.get (), m_aTimeToLive);
  }

  /**
   * Put a new value into the cache. Use this in derived classes to e.g. prefill the cache with
   * existing values.
   *
   * @param aKey
   *        The cache key. May be <code>null</code> depending on the cache key provider.
   * @param aValue
   *        The cache value. May be <code>null</code> depending on the settings.
   */
  @IsLocked (ELockType.WRITE)
  public final void putInCache (final KEYTYPE aKey, final VALUETYPE aValue)
  {
    final KEYSTORETYPE aCacheKey = _getCacheKeyNonNull (aKey);
    final CacheEntry <VALUETYPE> aCacheEntry = _buildCacheEntry (aKey, aValue);
    m_aRWLock.writeLocked ( () -> putInCacheNotLocked (aCacheKey, aCacheEntry));
  }

  @Nullable
  @MustBeLocked (ELockType.READ)
  protected final CacheEntry <VALUETYPE> getFromCacheNoStatsNotLocked (@Nullable final KEYSTORETYPE aCacheKey)
  {
    return m_aCache == null ? null : m_aCache.get (aCacheKey);
  }

  @Nullable
  @IsLocked (ELockType.READ)
  protected final CacheEntry <VALUETYPE> getFromCacheNoStats (@Nullable final KEYSTORETYPE aCacheKey)
  {
    // null cache keys can never be in the cache
    if (aCacheKey == null)
      return null;
    return m_aRWLock.readLockedGet ( () -> getFromCacheNoStatsNotLocked (aCacheKey));
  }

  /**
   * Check if the passed key is already in the cache or not. An entry that is past its time-based
   * expiration is considered as <em>not</em> in the cache for the purposes of this check.
   *
   * @param aKey
   *        The key to check. May be <code>null</code>.
   * @return <code>true</code> if the value is already in the cache, <code>false</code> if not.
   * @since 9.3.8
   */
  public final boolean isInCache (final KEYTYPE aKey)
  {
    // Determine the internal key - maybe null here
    final KEYSTORETYPE aCacheKey = m_aCacheKeyProvider.apply (aKey);

    final CacheEntry <VALUETYPE> aEntry = getFromCacheNoStats (aCacheKey);
    if (aEntry == null)
      return false;
    if (m_aTimeToLive != null && aEntry.isExpiredAt (m_aClockSupplier.get ()))
      return false;
    return true;
  }

  /**
   * Get a value from the cache. If the value is not yet in the cache, or if it is past its
   * time-based expiration, it is resolved via the value provider and stored before being returned.
   *
   * @param aKey
   *        The key to look up. May be <code>null</code> depending on the cache key provider.
   * @return The cached value. May be <code>null</code> if null values are allowed.
   */
  public VALUETYPE getFromCache (final KEYTYPE aKey)
  {
    // Determine the internal key
    final KEYSTORETYPE aCacheKey = _getCacheKeyNonNull (aKey);

    CacheEntry <VALUETYPE> aCacheEntry = getFromCacheNoStats (aCacheKey);
    final boolean bExpired = aCacheEntry != null &&
                             m_aTimeToLive != null &&
                             aCacheEntry.isExpiredAt (m_aClockSupplier.get ());
    if (aCacheEntry == null || bExpired)
    {
      // No old value in the cache, or the existing value is expired
      m_aRWLock.writeLock ().lock ();
      try
      {
        // Read again, in case the value was set between the two locking
        // sections
        // Note: do not increase statistics in this second try
        aCacheEntry = getFromCacheNoStatsNotLocked (aCacheKey);
        final boolean bStillExpired = aCacheEntry != null &&
                                      m_aTimeToLive != null &&
                                      aCacheEntry.isExpiredAt (m_aClockSupplier.get ());
        if (aCacheEntry == null || bStillExpired)
        {
          if (bStillExpired)
          {
            // Drop the expired entry
            m_aCache.remove (aCacheKey);
            m_aStatsCountExpired.increment ();
            if (LOGGER.isDebugEnabled ())
              LOGGER.debug (_getCacheLogText () + "Cache key '" + aKey + "' expired and was removed.");
          }
          // Call the value provider to create the value to cache
          final VALUETYPE aValue = m_aValueProvider.apply (aKey);
          aCacheEntry = _buildCacheEntry (aKey, aValue);

          // Put the new value into the cache
          putInCacheNotLocked (aCacheKey, aCacheEntry);
          m_aStatsCacheAccess.cacheMiss ();
        }
        else
          m_aStatsCacheAccess.cacheHit ();
      }
      finally
      {
        m_aRWLock.writeLock ().unlock ();
      }
    }
    else
      m_aStatsCacheAccess.cacheHit ();

    // the get() may resolve to a null value
    return aCacheEntry.getValue ();
  }

  /**
   * Remove the entry with the specified key from the cache.
   *
   * @param aKey
   *        The key of the entry to remove. May be <code>null</code> depending on the cache key
   *        provider.
   * @return {@link EChange#CHANGED} if the entry was successfully removed,
   *         {@link EChange#UNCHANGED} if the key was not found in the cache.
   */
  @NonNull
  @OverridingMethodsMustInvokeSuper
  public EChange removeFromCache (final KEYTYPE aKey)
  {
    final KEYSTORETYPE aCacheKey = _getCacheKeyNonNull (aKey);

    m_aRWLock.writeLock ().lock ();
    try
    {
      if (m_aCache == null || m_aCache.remove (aCacheKey) == null)
        return EChange.UNCHANGED;
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }

    m_aStatsCountRemove.increment ();
    if (LOGGER.isDebugEnabled ())
      LOGGER.debug (_getCacheLogText () + "Cache key '" + aKey + "' was removed.");
    return EChange.CHANGED;
  }

  /**
   * Remove all entries from the cache.
   *
   * @return {@link EChange#CHANGED} if at least one entry was removed, {@link EChange#UNCHANGED} if
   *         the cache was already empty.
   */
  @NonNull
  @OverridingMethodsMustInvokeSuper
  public EChange clearCache ()
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      if (m_aCache == null || m_aCache.isEmpty ())
        return EChange.UNCHANGED;

      m_aCache.clear ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }

    m_aStatsCountClear.increment ();
    if (LOGGER.isDebugEnabled ())
      LOGGER.debug (_getCacheLogText () + "Cache was cleared");
    return EChange.CHANGED;
  }

  /**
   * Remove all entries that are expired by their time-based expiration. For caches without a
   * time-based expiration this is a no-op.
   *
   * @return The number of entries that were removed. Always &ge; 0.
   * @since 12.3.0
   */
  @Nonnegative
  @OverridingMethodsMustInvokeSuper
  public int evictExpired ()
  {
    // No time eviction enabled
    if (m_aTimeToLive == null)
      return 0;

    int nRemoved = 0;
    m_aRWLock.writeLock ().lock ();
    try
    {
      if (m_aCache != null && m_aCache.isNotEmpty ())
      {
        final LocalDateTime aNow = m_aClockSupplier.get ();
        final var it = m_aCache.entrySet ().iterator ();
        while (it.hasNext ())
        {
          final var aMapEntry = it.next ();
          if (aMapEntry.getValue ().isExpiredAt (aNow))
          {
            it.remove ();
            nRemoved++;
          }
        }
      }
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }

    if (nRemoved > 0)
    {
      m_aStatsCountExpired.increment (nRemoved);
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug (_getCacheLogText () + nRemoved + " expired entries were removed.");
    }
    return nRemoved;
  }

  /**
   * @return The number of entries currently in the cache. Always &ge; 0.
   */
  @Nonnegative
  public int size ()
  {
    return m_aRWLock.readLockedInt ( () -> CollectionHelper.getSize (m_aCache));
  }

  /**
   * @return <code>true</code> if the cache contains no entries, <code>false</code> if it contains
   *         at least one entry.
   */
  public boolean isEmpty ()
  {
    return m_aRWLock.readLockedBoolean ( () -> CollectionHelper.isEmpty (m_aCache));
  }

  /**
   * @return <code>true</code> if the cache contains at least one entry, <code>false</code> if it is
   *         empty.
   */
  @Override
  public boolean isNotEmpty ()
  {
    return m_aRWLock.readLockedBoolean ( () -> CollectionHelper.isNotEmpty (m_aCache));
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("CacheKeyProvider", m_aCacheKeyProvider)
                                       .append ("ValueProvider", m_aValueProvider)
                                       .append ("MaxSize", m_nMaxSize)
                                       .append ("Name", m_sName)
                                       .append ("AllowNullValues", m_bAllowNullValues)
                                       .appendIfNotNull ("TimeToLive", m_aTimeToLive)
                                       .append ("Cache", m_aCache)
                                       .getToString ();
  }
}
