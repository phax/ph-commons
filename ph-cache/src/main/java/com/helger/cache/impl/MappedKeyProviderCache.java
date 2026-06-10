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
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Provider cache that maps a public-facing query key type {@code KEYTYPE} to the internal storage
 * key type {@code KEYSTORETYPE} via a key mapper function. On a cache miss the value provider is
 * invoked with the original query key. All storage, locking and TTL handling is inherited from
 * {@link AbstractProviderCache}.
 *
 * @author Philip Helger
 * @since 12.3.0
 * @param <KEYTYPE>
 *        The cache query key type
 * @param <KEYSTORETYPE>
 *        The internal storage key type
 * @param <VALUETYPE>
 *        The cache value type as stored and returned
 */
@ThreadSafe
public class MappedKeyProviderCache <KEYTYPE, KEYSTORETYPE, VALUETYPE> extends
                                    AbstractProviderCache <KEYTYPE, KEYSTORETYPE, VALUETYPE>
{
  private final Function <KEYTYPE, KEYSTORETYPE> m_aKeyMapper;

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
   * @param aKeyMapper
   *        The cache key mapper, that takes any KEYTYPE and creates a non-<code>null</code>
   *        KEYSTORETYPE instance. May not be <code>null</code>.
   * @param aValueProvider
   *        The value provider invoked on a cache miss. Takes the original KEYTYPE and produces a
   *        VALUETYPE. May not be <code>null</code>.
   */
  public MappedKeyProviderCache (@NonNull @Nonempty final String sCacheName,
                                 @CheckForSigned final int nMaxSize,
                                 final boolean bAllowNullValues,
                                 @Nullable final Duration aTimeToLive,
                                 @NonNull final Supplier <LocalDateTime> aClockSupplier,
                                 @NonNull final Function <KEYTYPE, KEYSTORETYPE> aKeyMapper,
                                 @NonNull final Function <KEYTYPE, VALUETYPE> aValueProvider)
  {
    super (sCacheName, nMaxSize, bAllowNullValues, aTimeToLive, null, aClockSupplier, aValueProvider);
    ValueEnforcer.notNull (aKeyMapper, "KeyMapper");
    m_aKeyMapper = aKeyMapper;
  }

  /**
   * @return The cache key mapper from the constructor. Never <code>null</code>.
   */
  @NonNull
  protected final Function <KEYTYPE, KEYSTORETYPE> getKeyMapper ()
  {
    return m_aKeyMapper;
  }

  @Override
  @NonNull
  protected KEYSTORETYPE getStorageKey (final KEYTYPE aKey)
  {
    final KEYSTORETYPE aStorageKey = m_aKeyMapper.apply (aKey);
    if (aStorageKey == null)
      throw new IllegalStateException ("Cache '" +
                                       getName () +
                                       "': The created storage key of '" +
                                       aKey +
                                       "' is null.");
    return aStorageKey;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("KeyMapper", m_aKeyMapper).getToString ();
  }

  /**
   * @return A new builder for {@link MappedKeyProviderCache} objects. Never <code>null</code>.
   * @param <KEYTYPE>
   *        The cache query key type
   * @param <KEYSTORETYPE>
   *        The internal storage key type
   * @param <VALUETYPE>
   *        The cache value type
   */
  @NonNull
  public static <KEYTYPE, KEYSTORETYPE, VALUETYPE> MappedKeyProviderCacheBuilder <KEYTYPE, KEYSTORETYPE, VALUETYPE> builder ()
  {
    return new MappedKeyProviderCacheBuilder <> ();
  }
}
