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

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.string.StringHelper;
import com.helger.base.trait.IGenericImplTrait;
import com.helger.cache.eviction.CacheEvictionScheduler;

/**
 * Cache builder base class
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The cache key type
 * @param <VALUETYPE>
 *        The cache value type
 * @since 12.3.0
 */
@NotThreadSafe
class CacheBuilderBase <IMPLTYPE extends CacheBuilderBase <IMPLTYPE>> implements IGenericImplTrait <IMPLTYPE>
{
  protected String m_sName;
  protected int m_nMaxSize = AbstractMapBasedCache.NO_MAX_SIZE;
  protected boolean m_bAllowNullValues = AbstractMapBasedCache.DEFAULT_ALLOW_NULL_VALUES;
  protected Duration m_aTimeToLive;
  protected Duration m_aEvictionInterval;
  protected Supplier <LocalDateTime> m_aClockSupplier = AbstractMapBasedCache.DEFAULT_CLOCK_SUPPLIER;

  /**
   * Default constructor.
   */
  public CacheBuilderBase ()
  {}

  /**
   * Set the name of the cache.
   *
   * @param s
   *        The cache name. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public IMPLTYPE name (@Nullable final String s)
  {
    m_sName = s;
    return thisAsT ();
  }

  /**
   * Set the maximum number of entries in the cache.
   *
   * @param n
   *        The maximum cache size. Values &le; 0 mean no limit.
   * @return this for chaining
   */
  @NonNull
  public IMPLTYPE maxSize (final int n)
  {
    m_nMaxSize = n;
    return thisAsT ();
  }

  /**
   * Set whether <code>null</code> values are allowed in the cache.
   *
   * @param b
   *        <code>true</code> to allow <code>null</code> values, <code>false</code> to disallow.
   * @return this for chaining
   */
  @NonNull
  public IMPLTYPE allowNullValues (final boolean b)
  {
    m_bAllowNullValues = b;
    return thisAsT ();
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
   */
  @NonNull
  public IMPLTYPE expireAfterWrite (@Nullable final Duration a)
  {
    m_aTimeToLive = a;
    return thisAsT ();
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
   */
  @NonNull
  public IMPLTYPE evictionInterval (@Nullable final Duration a)
  {
    m_aEvictionInterval = a;
    return thisAsT ();
  }

  /**
   * Set the clock supplier to use. By default a UTC-based LocalDateTime is used.
   *
   * @param a
   *        The supplier to use. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public IMPLTYPE clockSupplier (@Nullable final Supplier <LocalDateTime> a)
  {
    m_aClockSupplier = a;
    return thisAsT ();
  }

  protected boolean hasEvictionInterval ()
  {
    return m_aEvictionInterval != null && !m_aEvictionInterval.isZero () && !m_aEvictionInterval.isNegative ();
  }

  protected void checkCommonFields ()
  {
    if (StringHelper.isEmpty (m_sName))
      throw new IllegalStateException ("The mandatory Cache Name is missing");

    final boolean bHasTimeToLive = m_aTimeToLive != null && !m_aTimeToLive.isZero () && !m_aTimeToLive.isNegative ();
    if (hasEvictionInterval () && !bHasTimeToLive)
      throw new IllegalStateException ("An eviction interval is configured but no positive time-to-live is set - nothing to evict");
    if (m_aClockSupplier == null)
      throw new IllegalStateException ("The mandatory Clock Supplier is missing");
  }
}
