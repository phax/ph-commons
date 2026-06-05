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
import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.concurrent.SimpleReadWriteLock;

/**
 * A simple Map-like cache that supports an optional maximum size, optional time-based expiration,
 * statistics and active eviction. Unlike {@link MappedCache}/{@link Cache}, this class does
 * <em>not</em> consult a value provider on a cache miss: {@link #getFromCache(Object)} simply
 * returns <code>null</code> when the key is absent or its entry has expired. Use the explicit
 * {@link #putInCache(Object, Object)} / {@link #removeFromCache(Object)} methods to manage the
 * contents.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The cache key type.
 * @param <VALUETYPE>
 *        The cache value type.
 * @since 12.3.0
 */
@ThreadSafe
public class ManualCache <KEYTYPE, VALUETYPE> extends AbstractMapBasedCache <KEYTYPE, VALUETYPE>
{
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
   */
  public ManualCache (@NonNull @Nonempty final String sCacheName,
                      @CheckForSigned final int nMaxSize,
                      final boolean bAllowNullValues,
                      @Nullable final Duration aTimeToLive,
                      @NonNull final Supplier <LocalDateTime> aClockSupplier)
  {
    super (sCacheName, nMaxSize, bAllowNullValues, aTimeToLive, aClockSupplier);
  }

  @NonNull
  final SimpleReadWriteLock internalRwLock ()
  {
    return m_aRWLock;
  }

  @Nullable
  final CacheEntry <VALUETYPE> internalGetCacheEntryNotLockedNoStats (final KEYTYPE aKey)
  {
    final CacheEntry <VALUETYPE> aCacheEntry = getFromCacheNoStatsNotLocked (aKey);
    if (aCacheEntry == null)
      return null;

    if (isExpired (aCacheEntry))
      return null;

    return aCacheEntry;
  }

  final void internalPutInCacheNotLocked (@NonNull final KEYTYPE aKey, @NonNull final VALUETYPE aValue)
  {
    putInCacheNotLocked (aKey, buildCacheEntry (aKey, aValue));
  }

  /**
   * @return A new builder for {@link ManualCache} objects. Never <code>null</code>.
   * @param <KEYTYPE>
   *        The cache key type
   * @param <VALUETYPE>
   *        The cache value type
   */
  @NonNull
  public static <KEYTYPE, VALUETYPE> ManualCacheBuilder <KEYTYPE, VALUETYPE> builder ()
  {
    return new ManualCacheBuilder <> ();
  }
}
