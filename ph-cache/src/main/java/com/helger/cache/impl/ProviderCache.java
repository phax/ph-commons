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
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.cache.IMutableCacheWithExpiration;

/**
 * Cache implementation that resolves values on a cache miss via a value provider function. The
 * storage, locking and TTL handling are provided by the {@link ManualCache} member.
 *
 * @author Philip Helger
 * @since 12.3.0
 * @param <KEYTYPE>
 *        The cache key type
 * @param <VALUETYPE>
 *        The cache value type
 */
@ThreadSafe
public class ProviderCache <KEYTYPE, VALUETYPE> implements IMutableCacheWithExpiration <KEYTYPE, VALUETYPE>
{
  private final ManualCache <KEYTYPE, VALUETYPE> m_aCache;
  private final Function <KEYTYPE, VALUETYPE> m_aValueProvider;

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
   * @param aValueProvider
   *        The cache value provider. The value to be cached may be <code>null</code> depending on
   *        the parameter {@code bAllowNullValues}. May not be <code>null</code>.
   */
  public ProviderCache (@NonNull @Nonempty final String sCacheName,
                        @CheckForSigned final int nMaxSize,
                        final boolean bAllowNullValues,
                        @Nullable final Duration aTimeToLive,
                        @NonNull final Supplier <LocalDateTime> aClockSupplier,
                        @NonNull final Function <KEYTYPE, VALUETYPE> aValueProvider)
  {
    ValueEnforcer.notNull (aValueProvider, "ValueProvider");

    m_aCache = new ManualCache <> (sCacheName, nMaxSize, bAllowNullValues, aTimeToLive, aClockSupplier);
    m_aValueProvider = aValueProvider;
  }

  /**
   * @return The cache value provider from the constructor. Never <code>null</code>.
   */
  @NonNull
  protected final Function <KEYTYPE, VALUETYPE> getValueProvider ()
  {
    return m_aValueProvider;
  }

  /**
   * @return The internal name of this cache. Neither <code>null</code> nor empty.
   */
  @Override
  @NonNull
  @Nonempty
  public final String getName ()
  {
    return m_aCache.getName ();
  }

  @Override
  @Nonnegative
  public int size ()
  {
    return m_aCache.size ();
  }

  @Override
  public boolean isEmpty ()
  {
    return m_aCache.isEmpty ();
  }

  @Override
  public boolean isNotEmpty ()
  {
    return m_aCache.isNotEmpty ();
  }

  public boolean isInCache (final KEYTYPE aKey)
  {
    return m_aCache.isInCache (aKey);
  }

  /**
   * Get a value from the cache. If the value is not yet in the cache, or if it is past its
   * time-based expiration, it is resolved via the value provider and stored before being returned.
   *
   * @param aKey
   *        The key to look up. May usually not be <code>null</code>.
   * @return The cached value. May be <code>null</code> if null values are allowed.
   */
  @Override
  public VALUETYPE getFromCache (final KEYTYPE aKey)
  {
    final ReadWriteLock aRWLock = m_aCache.internalRwLock ();

    // Check for CacheEntry, so that it works with null values as well
    CacheEntry <VALUETYPE> aCacheEntry;
    aRWLock.readLock ().lock ();
    try
    {
      aCacheEntry = m_aCache.internalGetCacheEntryNotLockedNoStats (aKey);
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
        aCacheEntry = m_aCache.internalGetCacheEntryNotLockedNoStats (aKey);
        if (aCacheEntry == null)
        {
          // Call the value provider to create the value to cache
          final VALUETYPE aValue = m_aValueProvider.apply (aKey);

          // Store in cache
          m_aCache.internalPutInCacheNotLocked (aKey, aValue);
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

  @Override
  public int getMaxSize ()
  {
    return m_aCache.getMaxSize ();
  }

  @Override
  public boolean isAllowNullValues ()
  {
    return m_aCache.isAllowNullValues ();
  }

  @Nullable
  public Duration getTimeToLive ()
  {
    return m_aCache.getTimeToLive ();
  }

  @NonNull
  public Supplier <LocalDateTime> getClockSupplier ()
  {
    return m_aCache.getClockSupplier ();
  }

  public void putInCache (final KEYTYPE aKey, final VALUETYPE aValue)
  {
    m_aCache.putInCache (aKey, aValue);
  }

  @NonNull
  public EChange removeFromCache (final KEYTYPE aKey)
  {
    return m_aCache.removeFromCache (aKey);
  }

  @Override
  @NonNull
  public EChange clearCache ()
  {
    return m_aCache.clearCache ();
  }

  @Override
  @Nonnegative
  public int evictExpired ()
  {
    return m_aCache.evictExpired ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("Cache", m_aCache)
                            .append ("ValueProvider", m_aValueProvider)
                            .getToString ();
  }
}
