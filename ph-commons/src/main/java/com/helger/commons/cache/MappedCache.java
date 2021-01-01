/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import java.util.function.Function;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.ELockType;
import com.helger.commons.annotation.IsLocked;
import com.helger.commons.annotation.MustBeLocked;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.map.SoftHashMap;
import com.helger.commons.collection.map.SoftLinkedHashMap;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.state.EChange;
import com.helger.commons.statistics.IMutableStatisticsHandlerCache;
import com.helger.commons.statistics.IMutableStatisticsHandlerCounter;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.wrapper.Wrapper;

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

  protected final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  private final Function <KEYTYPE, KEYSTORETYPE> m_aCacheKeyProvider;
  private final Function <KEYTYPE, VALUETYPE> m_aValueProvider;
  private final int m_nMaxSize;
  private final String m_sName;
  private final boolean m_bAllowNullValues;
  // Status vars
  // The main cache. Uses a generic Wrapper to allow to store null values and
  // determine them as such. Lazily created.
  @GuardedBy ("m_aRWLock")
  private ICommonsMap <KEYSTORETYPE, Wrapper <VALUETYPE>> m_aCache;

  /**
   * Constructor
   *
   * @param aCacheKeyProvider
   *        The cache key provider, that takes any KEYTYPE and creates a
   *        non-<code>null</code> KEYSTORETYPE instance. May not be
   *        <code>null</code>.
   * @param aValueProvider
   *        The cache value provider. The value to be cached may be
   *        <code>null</code> depending on the parameter
   *        {@code bAllowNullValues}. May not be <code>null</code>.
   * @param nMaxSize
   *        The maximum size of the cache. All values &le; 0 indicate an
   *        unlimited size.
   * @param sCacheName
   *        The internal name of the cache. May neither be <code>null</code> nor
   *        empty. This name is NOT checked for uniqueness.
   * @param bAllowNullValues
   *        <code>true</code> if <code>null</code> values are allowed to be in
   *        the cache, <code>false</code> if not.
   */
  public MappedCache (@Nonnull final Function <KEYTYPE, KEYSTORETYPE> aCacheKeyProvider,
                      @Nonnull final Function <KEYTYPE, VALUETYPE> aValueProvider,
                      final int nMaxSize,
                      @Nonnull @Nonempty final String sCacheName,
                      final boolean bAllowNullValues)
  {
    ValueEnforcer.notNull (aCacheKeyProvider, "CacheKeyProvider");
    ValueEnforcer.notNull (aValueProvider, "ValueProvider");
    ValueEnforcer.notEmpty (sCacheName, "CacheName");

    m_aStatsCacheAccess = StatisticsManager.getCacheHandler (STATISTICS_PREFIX + sCacheName + "$access");
    m_aStatsCountRemove = StatisticsManager.getCounterHandler (STATISTICS_PREFIX + sCacheName + "$remove");
    m_aStatsCountClear = StatisticsManager.getCounterHandler (STATISTICS_PREFIX + sCacheName + "$clear");

    m_aCacheKeyProvider = aCacheKeyProvider;
    m_aValueProvider = aValueProvider;
    m_nMaxSize = nMaxSize;
    m_sName = sCacheName;
    m_bAllowNullValues = bAllowNullValues;
  }

  /**
   * @return The cache key provider from the constructor. Never
   *         <code>null</code>.
   * @since 9.3.8
   */
  @Nonnull
  protected final Function <KEYTYPE, KEYSTORETYPE> getCacheKeyProvider ()
  {
    return m_aCacheKeyProvider;
  }

  /**
   * @return The cache value provider from the constructor. Never
   *         <code>null</code>.
   * @since 9.3.8
   */
  @Nonnull
  protected final Function <KEYTYPE, VALUETYPE> getValueProvider ()
  {
    return m_aValueProvider;
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
   * @return <code>true</code> if <code>null</code> can be in the cache,
   *         <code>false</code> if not.
   * @since 9.3.8
   */
  public final boolean isAllowNullValues ()
  {
    return m_bAllowNullValues;
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
  protected ICommonsMap <KEYSTORETYPE, Wrapper <VALUETYPE>> createCache ()
  {
    return hasMaxSize () ? new SoftLinkedHashMap <> (m_nMaxSize) : new SoftHashMap <> ();
  }

  @Nonnull
  @Nonempty
  private String _getCacheLogText ()
  {
    String ret = "Cache '" + m_sName + "'";
    if (hasMaxSize ())
      ret += " with max size of " + m_nMaxSize;
    return ret + ": ";
  }

  /**
   * Put a new value into the cache.
   *
   * @param aCacheKey
   *        The cache key. May not be <code>null</code>.
   * @param aCacheValue
   *        The cache value. May not be <code>null</code>.
   */
  @MustBeLocked (ELockType.WRITE)
  protected final void putInCacheNotLocked (@Nonnull final KEYSTORETYPE aCacheKey, @Nonnull final Wrapper <VALUETYPE> aCacheValue)
  {
    ValueEnforcer.notNull (aCacheKey, "CacheKey");
    ValueEnforcer.notNull (aCacheValue, "CacheValue");

    // try again in write lock
    if (m_aCache == null)
    {
      // Lazily create a new map to cache the objects
      m_aCache = createCache ();
      if (m_aCache == null)
        throw new IllegalStateException (_getCacheLogText () + "Failed to create internal Map!");
    }
    m_aCache.put (aCacheKey, aCacheValue);
  }

  @Nonnull
  private KEYSTORETYPE _getCacheKeyNonnull (final KEYTYPE aKey)
  {
    final KEYSTORETYPE aCacheKey = m_aCacheKeyProvider.apply (aKey);
    if (aCacheKey == null)
      throw new IllegalStateException (_getCacheLogText () + "The created cache key of '" + aKey + "' is null.");
    return aCacheKey;
  }

  @Nonnull
  private Wrapper <VALUETYPE> _getCacheValue (final KEYTYPE aKey, final VALUETYPE aValue)
  {
    if (aValue == null)
    {
      if (!m_bAllowNullValues)
        throw new IllegalStateException (_getCacheLogText () +
                                         "The created cache value of key '" +
                                         aKey +
                                         "' is null. null values are not allowed in this cache.");
      return new Wrapper <> ();
    }
    return new Wrapper <> (aValue);
  }

  /**
   * Put a new value into the cache. Use this in derived classes to e.g. prefill
   * the cache with existing values.
   *
   * @param aKey
   *        The cache key. May be <code>null</code> depending on the cache key
   *        provider.
   * @param aValue
   *        The cache value. May be <code>null</code> depending on the settings.
   */
  @IsLocked (ELockType.WRITE)
  protected final void putInCache (final KEYTYPE aKey, final VALUETYPE aValue)
  {
    final KEYSTORETYPE aCacheKey = _getCacheKeyNonnull (aKey);
    final Wrapper <VALUETYPE> aCacheValue = _getCacheValue (aKey, aValue);
    m_aRWLock.writeLocked ( () -> putInCacheNotLocked (aCacheKey, aCacheValue));
  }

  @Nullable
  @MustBeLocked (ELockType.READ)
  protected final Wrapper <VALUETYPE> getFromCacheNoStatsNotLocked (@Nullable final KEYSTORETYPE aCacheKey)
  {
    return m_aCache == null ? null : m_aCache.get (aCacheKey);
  }

  @Nullable
  @IsLocked (ELockType.READ)
  protected final Wrapper <VALUETYPE> getFromCacheNoStats (@Nullable final KEYSTORETYPE aCacheKey)
  {
    // null cache keys can never be in the cache
    if (aCacheKey == null)
      return null;
    return m_aRWLock.readLockedGet ( () -> getFromCacheNoStatsNotLocked (aCacheKey));
  }

  /**
   * Check if the passed key is already in the cache or not.
   *
   * @param aKey
   *        The key to check. May be <code>null</code>.
   * @return <code>true</code> if the value is already in the cache,
   *         <code>false</code> if not.
   * @since 9.3.8
   */
  public final boolean isInCache (final KEYTYPE aKey)
  {
    // Determine the internal key - maybe null here
    final KEYSTORETYPE aCacheKey = m_aCacheKeyProvider.apply (aKey);

    return getFromCacheNoStats (aCacheKey) != null;
  }

  public VALUETYPE getFromCache (final KEYTYPE aKey)
  {
    // Determine the internal key
    final KEYSTORETYPE aCacheKey = _getCacheKeyNonnull (aKey);

    Wrapper <VALUETYPE> aCacheValue = getFromCacheNoStats (aCacheKey);
    if (aCacheValue == null)
    {
      // No old value in the cache
      m_aRWLock.writeLock ().lock ();
      try
      {
        // Read again, in case the value was set between the two locking
        // sections
        // Note: do not increase statistics in this second try
        aCacheValue = getFromCacheNoStatsNotLocked (aCacheKey);
        if (aCacheValue == null)
        {
          // Call the value provide to create the value to cache
          final VALUETYPE aValue = m_aValueProvider.apply (aKey);
          aCacheValue = _getCacheValue (aKey, aValue);

          // Put the new value into the cache
          putInCacheNotLocked (aCacheKey, aCacheValue);
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
    return aCacheValue.get ();
  }

  @Nonnull
  @OverridingMethodsMustInvokeSuper
  public EChange removeFromCache (final KEYTYPE aKey)
  {
    final KEYSTORETYPE aCacheKey = _getCacheKeyNonnull (aKey);

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

  @Nonnegative
  public int size ()
  {
    return m_aRWLock.readLockedInt ( () -> CollectionHelper.getSize (m_aCache));
  }

  public boolean isEmpty ()
  {
    return m_aRWLock.readLockedBoolean ( () -> CollectionHelper.isEmpty (m_aCache));
  }

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
                                       .append ("Cache", m_aCache)
                                       .getToString ();
  }
}
