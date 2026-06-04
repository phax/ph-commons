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

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.builder.IBuilder;
import com.helger.base.string.StringHelper;
import com.helger.cache.eviction.CacheEvictionScheduler;

/**
 * Builder class for {@link Cache} objects.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The cache key type
 * @param <VALUETYPE>
 *        The cache value type
 * @since v12.0.0
 */
@NotThreadSafe
public class CacheBuilder <KEYTYPE, VALUETYPE> implements IBuilder <Cache <KEYTYPE, VALUETYPE>>
{
  private String m_sName;
  private int m_nMaxSize = AbstractMapBasedCache.NO_MAX_SIZE;
  private boolean m_bAllowNullValues = AbstractMapBasedCache.DEFAULT_ALLOW_NULL_VALUES;
  private Duration m_aTimeToLive;
  private Duration m_aEvictionInterval;
  private Supplier <LocalDateTime> m_aClockSupplier = AbstractMapBasedCache.DEFAULT_CLOCK_SUPPLIER;
  private Function <KEYTYPE, VALUETYPE> m_aValueProvider;

  /**
   * Default constructor.
   */
  public CacheBuilder ()
  {}

  /**
   * Set the name of the cache.
   *
   * @param s
   *        The cache name. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public CacheBuilder <KEYTYPE, VALUETYPE> name (@Nullable final String s)
  {
    m_sName = s;
    return this;
  }

  /**
   * Set the maximum number of entries in the cache.
   *
   * @param n
   *        The maximum cache size. Values &le; 0 mean no limit.
   * @return this for chaining
   */
  @NonNull
  public CacheBuilder <KEYTYPE, VALUETYPE> maxSize (final int n)
  {
    m_nMaxSize = n;
    return this;
  }

  /**
   * Set whether <code>null</code> values are allowed in the cache.
   *
   * @param b
   *        <code>true</code> to allow <code>null</code> values, <code>false</code> to disallow.
   * @return this for chaining
   */
  @NonNull
  public CacheBuilder <KEYTYPE, VALUETYPE> allowNullValues (final boolean b)
  {
    m_bAllowNullValues = b;
    return this;
  }

  /**
   * Set the time-to-live for cache entries. Entries are considered expired after this duration has
   * passed since they were stored, and are automatically refreshed on the next read (lazy
   * expiration).
   *
   * @param a
   *        Time after which a cache entry is considered expired. May be <code>null</code>, zero or
   *        negative to disable time-based expiration.
   * @return this for chaining
   * @since 12.3.0
   */
  @NonNull
  public CacheBuilder <KEYTYPE, VALUETYPE> expireAfterWrite (@Nullable final Duration a)
  {
    m_aTimeToLive = a;
    return this;
  }

  /**
   * Register the built cache with the shared {@link CacheEvictionScheduler} so that expired entries
   * are actively removed on the provided interval. Requires {@link #expireAfterWrite(Duration)} to
   * be set; otherwise there is nothing to evict.
   *
   * @param a
   *        The eviction interval. May be <code>null</code>, zero or negative to disable background
   *        eviction (the default).
   * @return this for chaining
   * @since 12.3.0
   */
  @NonNull
  public CacheBuilder <KEYTYPE, VALUETYPE> evictionInterval (@Nullable final Duration a)
  {
    m_aEvictionInterval = a;
    return this;
  }

  /**
   * Set the clock supplier to use. By default a UTC-based LocalDateTime is used.
   *
   * @param a
   *        The supplier to use. May not be <code>null</code>.
   * @return this for chaining
   * @since 12.3.0
   */
  @NonNull
  public CacheBuilder <KEYTYPE, VALUETYPE> clockSupplier (@Nullable final Supplier <LocalDateTime> a)
  {
    m_aClockSupplier = a;
    return this;
  }

  /**
   * Set the function to compute cache values.
   *
   * @param a
   *        The value provider function. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public CacheBuilder <KEYTYPE, VALUETYPE> valueProvider (@Nullable final Function <KEYTYPE, VALUETYPE> a)
  {
    m_aValueProvider = a;
    return this;
  }

  private boolean _hasEvictionInterval ()
  {
    return m_aEvictionInterval != null && !m_aEvictionInterval.isZero () && !m_aEvictionInterval.isNegative ();
  }

  private void _checkCommonFields ()
  {
    if (StringHelper.isEmpty (m_sName))
      throw new IllegalStateException ("The mandatory Cache Name is missing");

    final boolean bHasTimeToLive = m_aTimeToLive != null && !m_aTimeToLive.isZero () && !m_aTimeToLive.isNegative ();
    if (_hasEvictionInterval () && !bHasTimeToLive)
      throw new IllegalStateException ("An eviction interval is configured but no positive time-to-live is set - nothing to evict");
    if (m_aClockSupplier == null)
      throw new IllegalStateException ("The mandatory Clock Supplier is missing");
  }

  /**
   * Build the {@link Cache} instance from the configured parameters.
   *
   * @return A new {@link Cache} instance. Never <code>null</code>.
   * @throws IllegalStateException
   *         if the value provider or cache name is missing, or if an eviction interval is set
   *         without a time-to-live.
   */
  @NonNull
  @Deprecated (forRemoval = true, since = "12.3.0")
  @SuppressWarnings ("removal")
  public Cache <KEYTYPE, VALUETYPE> build ()
  {
    _checkCommonFields ();
    if (m_aValueProvider == null)
      throw new IllegalStateException ("The mandatory Cache Value Provider is missing");

    final Cache <KEYTYPE, VALUETYPE> ret = new Cache <> (m_sName,
                                                         m_nMaxSize,
                                                         m_bAllowNullValues,
                                                         m_aTimeToLive,
                                                         m_aClockSupplier,
                                                         m_aValueProvider);
    if (_hasEvictionInterval ())
      CacheEvictionScheduler.getInstance ().register (ret, m_aEvictionInterval);
    return ret;
  }

  /**
   * Build the {@link ManualCache} instance from the configured parameters.
   *
   * @return A new {@link ManualCache} instance. Never <code>null</code>.
   * @throws IllegalStateException
   *         if the value provider or cache name is missing, or if an eviction interval is set
   *         without a time-to-live.
   */
  @NonNull
  public ManualCache <KEYTYPE, VALUETYPE> buildManualCache ()
  {
    _checkCommonFields ();

    final ManualCache <KEYTYPE, VALUETYPE> ret = new ManualCache <> (m_sName,
                                                                     m_nMaxSize,
                                                                     m_bAllowNullValues,
                                                                     m_aTimeToLive,
                                                                     m_aClockSupplier);
    if (_hasEvictionInterval ())
      CacheEvictionScheduler.getInstance ().register (ret, m_aEvictionInterval);
    return ret;
  }

  /**
   * Build the {@link ProviderCache} instance from the configured parameters.
   *
   * @return A new {@link ProviderCache} instance. Never <code>null</code>.
   * @throws IllegalStateException
   *         if the value provider or cache name is missing, or if an eviction interval is set
   *         without a time-to-live.
   */
  @NonNull
  public ProviderCache <KEYTYPE, VALUETYPE> buildProviderCache ()
  {
    _checkCommonFields ();
    if (m_aValueProvider == null)
      throw new IllegalStateException ("The mandatory Cache Value Provider is missing");

    final ProviderCache <KEYTYPE, VALUETYPE> ret = new ProviderCache <> (m_sName,
                                                                         m_nMaxSize,
                                                                         m_bAllowNullValues,
                                                                         m_aTimeToLive,
                                                                         m_aClockSupplier,
                                                                         m_aValueProvider);
    if (_hasEvictionInterval ())
      CacheEvictionScheduler.getInstance ().register (ret, m_aEvictionInterval);
    return ret;
  }
}
