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

import com.helger.annotation.Nonnegative;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.cache.IMutableCacheWithExpiration;

/**
 * Wrapper around an {@link IMutableCacheWithExpiration} which allows to use implicit mapping of key
 * types from the query type to the storage type.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key query type
 * @param <KEYSTORETYPE>
 *        Key storage type
 * @param <VALUETYPE>
 *        Value type
 * @since 12.3.0
 */
public class MappedKeyManualCache <KEYTYPE, KEYSTORETYPE, VALUETYPE> implements
                                  IMutableCacheWithExpiration <KEYTYPE, VALUETYPE>
{
  private final IMutableCacheWithExpiration <KEYSTORETYPE, VALUETYPE> m_aCache;
  private final Function <KEYTYPE, KEYSTORETYPE> m_aKeyMapper;

  public MappedKeyManualCache (@NonNull final ManualCache <KEYSTORETYPE, VALUETYPE> aCache,
                               @NonNull final Function <KEYTYPE, KEYSTORETYPE> aKeyMapper)
  {
    ValueEnforcer.notNull (aCache, "Cache");
    ValueEnforcer.notNull (aKeyMapper, "KeyMapper");
    m_aCache = aCache;
    m_aKeyMapper = aKeyMapper;
  }

  @NonNull
  protected KEYSTORETYPE getStorageKey (final @NonNull KEYTYPE aKey)
  {
    final KEYSTORETYPE ret = m_aKeyMapper.apply (aKey);
    if (ret == null)
      throw new IllegalStateException ("Failed to map source key " + aKey + " to storage key");
    return ret;
  }

  public void putInCache (final @NonNull KEYTYPE aKey, final VALUETYPE aValue)
  {
    m_aCache.putInCache (getStorageKey (aKey), aValue);
  }

  public @NonNull EChange removeFromCache (final @NonNull KEYTYPE aKey)
  {
    return m_aCache.removeFromCache (getStorageKey (aKey));
  }

  public @NonNull EChange clearCache ()
  {
    return m_aCache.clearCache ();
  }

  public boolean isInCache (final @NonNull KEYTYPE aKey)
  {
    try
    {
      return m_aCache.isInCache (getStorageKey (aKey));
    }
    catch (final IllegalStateException ex)
    {
      return false;
    }
  }

  @Nullable
  public VALUETYPE getFromCache (final @NonNull KEYTYPE aKey)
  {
    return m_aCache.getFromCache (getStorageKey (aKey));
  }

  public int getMaxSize ()
  {
    return m_aCache.getMaxSize ();
  }

  public boolean isAllowNullValues ()
  {
    return m_aCache.isAllowNullValues ();
  }

  public @NonNull String getName ()
  {
    return m_aCache.getName ();
  }

  @Nonnegative
  public int size ()
  {
    return m_aCache.size ();
  }

  public boolean isEmpty ()
  {
    return m_aCache.isEmpty ();
  }

  public @Nullable Duration getTimeToLive ()
  {
    return m_aCache.getTimeToLive ();
  }

  public @NonNull Supplier <LocalDateTime> getClockSupplier ()
  {
    return m_aCache.getClockSupplier ();
  }

  public void putInCache (final KEYTYPE aKey, final VALUETYPE aValue, @NonNull final Duration aTimeToLive)
  {
    m_aCache.putInCache (getStorageKey (aKey), aValue, aTimeToLive);
  }

  public void putInCache (final KEYTYPE aKey, final VALUETYPE aValue, @NonNull final LocalDateTime aExpirationDT)
  {
    m_aCache.putInCache (getStorageKey (aKey), aValue, aExpirationDT);
  }

  @Nonnegative
  public int evictExpired ()
  {
    return m_aCache.evictExpired ();
  }

  @NonNull
  public static <K, KS, V> MappedKeyManualCache <K, KS, V> of (@NonNull final ManualCache <KS, V> aCache,
                                                               @NonNull final Function <K, KS> aKeyMapper)
  {
    return new MappedKeyManualCache <> (aCache, aKeyMapper);
  }
}
