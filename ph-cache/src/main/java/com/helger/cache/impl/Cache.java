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
import java.util.function.Function;
import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.cache.ICache;
import com.helger.cache.IMutableCache;

/**
 * An implementation of {@link ICache} and {@link IMutableCache}. Since v9.3.8 this class is based
 * on {@link MappedCache}.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The cache key type
 * @param <VALUETYPE>
 *        The cache value type
 * @deprecated Use {@link ProviderCache} instead
 */
@ThreadSafe
@Deprecated (forRemoval = true, since = "12.3.0")
public class Cache <KEYTYPE, VALUETYPE> extends MappedCache <KEYTYPE, KEYTYPE, VALUETYPE>
{
  /** Default value of {@link #isAllowNullValues()} */
  @Deprecated (forRemoval = true, since = "12.3.0")
  public static final boolean DEFAULT_ALLOW_NULL_VALUES = AbstractMapBasedCache.DEFAULT_ALLOW_NULL_VALUES;

  /**
   * Constructor with no maximum size.
   *
   * @param aCacheValueProvider
   *        The function to compute cache values. May not be <code>null</code>.
   * @param sCacheName
   *        The name of the cache. May neither be <code>null</code> nor empty.
   */
  @Deprecated (forRemoval = true, since = "12.3.0")
  public Cache (@NonNull final Function <KEYTYPE, VALUETYPE> aCacheValueProvider,
                @NonNull @Nonempty final String sCacheName)
  {
    this (aCacheValueProvider, AbstractMapBasedCache.NO_MAX_SIZE, sCacheName);
  }

  /**
   * Constructor with a maximum size.
   *
   * @param aCacheValueProvider
   *        The function to compute cache values. May not be <code>null</code>.
   * @param nMaxSize
   *        The maximum number of entries in the cache. Values &le; 0 mean no limit.
   * @param sCacheName
   *        The name of the cache. May neither be <code>null</code> nor empty.
   */
  @Deprecated (forRemoval = true, since = "12.3.0")
  public Cache (@NonNull final Function <KEYTYPE, VALUETYPE> aCacheValueProvider,
                final int nMaxSize,
                @NonNull @Nonempty final String sCacheName)
  {
    this (aCacheValueProvider, nMaxSize, sCacheName, AbstractMapBasedCache.DEFAULT_ALLOW_NULL_VALUES);
  }

  /**
   * Constructor with all parameters.
   *
   * @param aCacheValueProvider
   *        The function to compute cache values. May not be <code>null</code>.
   * @param nMaxSize
   *        The maximum number of entries in the cache. Values &le; 0 mean no limit.
   * @param sCacheName
   *        The name of the cache. May neither be <code>null</code> nor empty.
   * @param bAllowNullValues
   *        <code>true</code> if <code>null</code> values are allowed in the cache,
   *        <code>false</code> if not.
   */
  @Deprecated (forRemoval = true, since = "12.3.0")
  public Cache (@NonNull final Function <KEYTYPE, VALUETYPE> aCacheValueProvider,
                final int nMaxSize,
                @NonNull @Nonempty final String sCacheName,
                final boolean bAllowNullValues)
  {
    this (sCacheName,
          nMaxSize,
          bAllowNullValues,
          null,
          AbstractMapBasedCache.DEFAULT_CLOCK_SUPPLIER,
          aCacheValueProvider);
  }

  /**
   * Constructor with all parameters including time-based expiration.
   *
   * @param sCacheName
   *        The name of the cache. May neither be <code>null</code> nor empty.
   * @param nMaxSize
   *        The maximum number of entries in the cache. Values &le; 0 mean no limit.
   * @param bAllowNullValues
   *        <code>true</code> if <code>null</code> values are allowed in the cache,
   *        <code>false</code> if not.
   * @param aTimeToLive
   *        Time after which a cache entry is considered expired. May be <code>null</code> or zero
   *        or negative to disable time-based expiration.
   * @param aClockSupplier
   *        The clock supplier. May not be <code>null</code>.
   * @param aCacheValueProvider
   *        The function to compute cache values. May not be <code>null</code>.
   * @since 12.3.0
   */
  @Deprecated (forRemoval = true, since = "12.3.0")
  public Cache (@NonNull @Nonempty final String sCacheName,
                final int nMaxSize,
                final boolean bAllowNullValues,
                @Nullable final Duration aTimeToLive,
                @NonNull final Supplier <LocalDateTime> aClockSupplier,
                @NonNull final Function <KEYTYPE, VALUETYPE> aCacheValueProvider)
  {
    super (sCacheName, nMaxSize, bAllowNullValues, aTimeToLive, aClockSupplier, x -> x, aCacheValueProvider);
  }

  /**
   * @return A new builder for {@link Cache} objects. Never <code>null</code>.
   * @param <KEYTYPE>
   *        The cache key type
   * @param <VALUETYPE>
   *        The cache value type
   */
  @Deprecated (forRemoval = true, since = "12.3.0")
  @NonNull
  public static <KEYTYPE, VALUETYPE> CacheBuilder <KEYTYPE, VALUETYPE> builder ()
  {
    return new CacheBuilder <> ();
  }

  /**
   * Create a new builder pre-configured with the provided value provider.
   *
   * @param a
   *        The function to compute cache values. May be <code>null</code>.
   * @param <KEYTYPE>
   *        The cache key type
   * @param <VALUETYPE>
   *        The cache value type
   * @return A new builder for {@link Cache} objects. Never <code>null</code>.
   */
  @Deprecated (forRemoval = true, since = "12.3.0")
  @NonNull
  public static <KEYTYPE, VALUETYPE> CacheBuilder <KEYTYPE, VALUETYPE> builder (@Nullable final Function <KEYTYPE, VALUETYPE> a)
  {
    return new CacheBuilder <KEYTYPE, VALUETYPE> ().valueProvider (a);
  }
}
