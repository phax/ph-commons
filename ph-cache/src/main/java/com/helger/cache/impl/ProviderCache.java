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

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.ThreadSafe;

/**
 * Cache implementation that resolves values on a cache miss via a value provider function. The
 * storage, locking and TTL handling are inherited from {@link AbstractProviderCache}. This concrete
 * subclass uses the query key as the storage key directly (no key mapping).
 *
 * @author Philip Helger
 * @since 12.3.0
 * @param <KEYTYPE>
 *        The cache key type
 * @param <VALUETYPE>
 *        The cache value type
 */
@ThreadSafe
public class ProviderCache <KEYTYPE, VALUETYPE> extends AbstractProviderCache <KEYTYPE, KEYTYPE, VALUETYPE>
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
    super (sCacheName, nMaxSize, bAllowNullValues, aTimeToLive, null, aClockSupplier, aValueProvider);
  }

  @Override
  protected KEYTYPE getStorageKey (final KEYTYPE aKey)
  {
    return aKey;
  }

  /**
   * @return A new builder for {@link ProviderCache} objects. Never <code>null</code>.
   * @param <KEYTYPE>
   *        The cache key type
   * @param <VALUETYPE>
   *        The cache value type
   */
  @NonNull
  public static <KEYTYPE, VALUETYPE> ProviderCacheBuilder <KEYTYPE, VALUETYPE> builder ()
  {
    return new ProviderCacheBuilder <> ();
  }
}
