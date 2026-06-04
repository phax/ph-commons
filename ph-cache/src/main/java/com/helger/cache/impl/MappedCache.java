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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.VisibleForTesting;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.cache.ICache;
import com.helger.cache.IMutableCache;
import com.helger.cache.IMutableCacheWithExpiration;

/**
 * Cache implementation that maps a public-facing key type {@code KEYTYPE} to an internal storage
 * key type {@code KEYSTORETYPE} via a key provider function, and resolves values on a cache miss
 * via a value provider function. The storage, locking and TTL handling are provided by the
 * {@link ManualCache} super class.
 *
 * @author Philip Helger
 * @since 9.3.8 generalized from the existing {@link Cache} class.
 * @param <KEYTYPE>
 *        The cache source type
 * @param <KEYSTORETYPE>
 *        The internal storage key type
 * @param <VALUETYPE>
 *        The cache value type
 * @see ICache
 * @see IMutableCache
 */
@Deprecated (forRemoval = true, since = "12.3.0")
@ThreadSafe
public class MappedCache <KEYTYPE, KEYSTORETYPE, VALUETYPE> implements IMutableCacheWithExpiration <KEYTYPE, VALUETYPE>
{
  /** A constant indicating, that a cache has no max size */
  @Deprecated (forRemoval = true, since = "12.3.0")
  public static final int NO_MAX_SIZE = AbstractMapBasedCache.NO_MAX_SIZE;
  /** Default value of {@link #isAllowNullValues()} */
  @Deprecated (forRemoval = true, since = "12.3.0")
  public static final boolean DEFAULT_ALLOW_NULL_VALUES = AbstractMapBasedCache.DEFAULT_ALLOW_NULL_VALUES;

  private final Function <KEYTYPE, KEYSTORETYPE> m_aCacheKeyProvider;
  private final Function <KEYTYPE, VALUETYPE> m_aValueProvider;
  private final ManualCache <KEYSTORETYPE, VALUETYPE> m_aCache;

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
  @Deprecated (forRemoval = true, since = "12.3.0")
  public MappedCache (@NonNull final Function <KEYTYPE, KEYSTORETYPE> aCacheKeyProvider,
                      @NonNull final Function <KEYTYPE, VALUETYPE> aValueProvider,
                      @CheckForSigned final int nMaxSize,
                      @NonNull @Nonempty final String sCacheName,
                      final boolean bAllowNullValues)
  {
    this (sCacheName,
          nMaxSize,
          bAllowNullValues,
          null,
          AbstractMapBasedCache.DEFAULT_CLOCK_SUPPLIER,
          aCacheKeyProvider,
          aValueProvider);
  }

  /**
   * Constructor with optional time-based expiration.
   *
   * @param sCacheName
   *        The internal name of the cache. May neither be <code>null</code> nor empty. This name is
   *        NOT checked for uniqueness.
   * @param nMaxSize
   *        The maximum size of the cache. All values &le; 0 indicate an unlimited size.
   * @param bAllowNullValues
   *        <code>true</code> if <code>null</code> values are allowed to be in the cache,
   *        <code>false</code> if not.
   * @param aTimeToLive
   *        Time after which a cache entry is considered expired (counted from the time it was put
   *        in the cache). May be <code>null</code> or zero or negative to disable time-based
   *        expiration.
   * @param aClockSupplier
   *        The clock supplier. May not be <code>null</code>.
   * @param aCacheKeyProvider
   *        The cache key provider, that takes any KEYTYPE and creates a non-<code>null</code>
   *        KEYSTORETYPE instance. May not be <code>null</code>.
   * @param aValueProvider
   *        The cache value provider. The value to be cached may be <code>null</code> depending on
   *        the parameter {@code bAllowNullValues}. May not be <code>null</code>.
   * @since 12.3.0
   */
  @Deprecated (forRemoval = true, since = "12.3.0")
  public MappedCache (@NonNull @Nonempty final String sCacheName,
                      @CheckForSigned final int nMaxSize,
                      final boolean bAllowNullValues,
                      @Nullable final Duration aTimeToLive,
                      @NonNull final Supplier <LocalDateTime> aClockSupplier,
                      @NonNull final Function <KEYTYPE, KEYSTORETYPE> aCacheKeyProvider,
                      @NonNull final Function <KEYTYPE, VALUETYPE> aValueProvider)
  {
    ValueEnforcer.notNull (aCacheKeyProvider, "CacheKeyProvider");
    ValueEnforcer.notNull (aValueProvider, "ValueProvider");

    m_aCacheKeyProvider = aCacheKeyProvider;
    m_aValueProvider = aValueProvider;
    m_aCache = new ManualCache <> (sCacheName, nMaxSize, bAllowNullValues, aTimeToLive, aClockSupplier);
  }

  @Deprecated (forRemoval = true, since = "12.3.0")
  @VisibleForTesting
  final ManualCache <KEYSTORETYPE, VALUETYPE> internalGetCache ()
  {
    return m_aCache;
  }

  /**
   * @return The cache key provider from the constructor. Never <code>null</code>.
   * @since 9.3.8
   */
  @Deprecated (forRemoval = true, since = "12.3.0")
  @NonNull
  protected final Function <KEYTYPE, KEYSTORETYPE> getCacheKeyProvider ()
  {
    return m_aCacheKeyProvider;
  }

  /**
   * @return The cache value provider from the constructor. Never <code>null</code>.
   * @since 9.3.8
   */
  @Deprecated (forRemoval = true, since = "12.3.0")
  @NonNull
  protected final Function <KEYTYPE, VALUETYPE> getValueProvider ()
  {
    return m_aValueProvider;
  }

  /**
   * @return The internal name of this cache. Neither <code>null</code> nor empty.
   */
  @Deprecated (forRemoval = true, since = "12.3.0")
  @Override
  @NonNull
  @Nonempty
  public final String getName ()
  {
    return m_aCache.getName ();
  }

  @Deprecated (forRemoval = true, since = "12.3.0")
  @Override
  @Nonnegative
  public int size ()
  {
    return m_aCache.size ();
  }

  @Deprecated (forRemoval = true, since = "12.3.0")
  @Override
  public boolean isEmpty ()
  {
    return m_aCache.isEmpty ();
  }

  @Deprecated (forRemoval = true, since = "12.3.0")
  @Override
  public boolean isNotEmpty ()
  {
    return m_aCache.isNotEmpty ();
  }

  @NonNull
  private final KEYSTORETYPE _getStorageKey (final KEYTYPE aKey)
  {
    final KEYSTORETYPE aCacheKey = m_aCacheKeyProvider.apply (aKey);
    if (aCacheKey == null)
      throw new IllegalStateException ("Cache '" + getName () + "': The created cache key of '" + aKey + "' is null.");
    return aCacheKey;
  }

  @Deprecated (forRemoval = true, since = "12.3.0")
  public boolean isInCache (final KEYTYPE aKey)
  {
    try
    {
      return m_aCache.isInCache (_getStorageKey (aKey));
    }
    catch (final IllegalStateException ex)
    {
      return false;
    }
  }

  /**
   * Get a value from the cache. If the value is not yet in the cache, or if it is past its
   * time-based expiration, it is resolved via the value provider and stored before being returned.
   *
   * @param aKey
   *        The key to look up. May be <code>null</code> depending on the cache key provider.
   * @return The cached value. May be <code>null</code> if null values are allowed.
   */
  @Deprecated (forRemoval = true, since = "12.3.0")
  @Override
  public VALUETYPE getFromCache (final KEYTYPE aKey)
  {
    // Determine the internal key
    final KEYSTORETYPE aStorageKey = _getStorageKey (aKey);

    final ReadWriteLock aRWLock = m_aCache.internalRwLock ();

    // Check for CacheEntry, so that it works with null values as well
    CacheEntry <VALUETYPE> aCacheEntry;
    aRWLock.readLock ().lock ();
    try
    {
      aCacheEntry = m_aCache.internalGetCacheEntryNotLockedNoStats (aStorageKey);
    }
    finally
    {
      aRWLock.readLock ().unlock ();
    }

    if (aCacheEntry == null)
    {
      // No old value in the cache, or the existing value is expired
      aRWLock.writeLock ().lock ();
      try
      {
        // Read again, in case the value was set between the two locking
        // sections. Note: do not increase statistics in this second try.
        aCacheEntry = m_aCache.internalGetCacheEntryNotLockedNoStats (aStorageKey);
        if (aCacheEntry == null)
        {
          // Call the value provider to create the value to cache
          final VALUETYPE aValue = m_aValueProvider.apply (aKey);

          // Store in cache
          m_aCache.internalPutInCacheNotLocked (aStorageKey, aValue);
          return aValue;
        }
      }
      finally
      {
        aRWLock.writeLock ().unlock ();
      }
    }

    return aCacheEntry.getValue ();
  }

  @Deprecated (forRemoval = true, since = "12.3.0")
  @Override
  public int getMaxSize ()
  {
    return m_aCache.getMaxSize ();
  }

  @Deprecated (forRemoval = true, since = "12.3.0")
  @Override
  public boolean isAllowNullValues ()
  {
    return m_aCache.isAllowNullValues ();
  }

  @Deprecated (forRemoval = true, since = "12.3.0")
  @Nullable
  public Duration getTimeToLive ()
  {
    return m_aCache.getTimeToLive ();
  }

  @Deprecated (forRemoval = true, since = "12.3.0")
  @NonNull
  public Supplier <LocalDateTime> getClockSupplier ()
  {
    return m_aCache.getClockSupplier ();
  }

  @Deprecated (forRemoval = true, since = "12.3.0")
  public void putInCache (final KEYTYPE aKey, final VALUETYPE aValue)
  {
    m_aCache.putInCache (_getStorageKey (aKey), aValue);
  }

  @Deprecated (forRemoval = true, since = "12.3.0")
  @NonNull
  public EChange removeFromCache (final KEYTYPE aKey)
  {
    return m_aCache.removeFromCache (_getStorageKey (aKey));
  }

  @Deprecated (forRemoval = true, since = "12.3.0")
  @Override
  @NonNull
  public EChange clearCache ()
  {
    return m_aCache.clearCache ();
  }

  @Deprecated (forRemoval = true, since = "12.3.0")
  @Override
  @Nonnegative
  public int evictExpired ()
  {
    return m_aCache.evictExpired ();
  }

  @Deprecated (forRemoval = true, since = "12.3.0")
  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("CacheKeyProvider", m_aCacheKeyProvider)
                            .append ("ValueProvider", m_aValueProvider)
                            .append ("Cache", m_aCache)
                            .getToString ();
  }
}
