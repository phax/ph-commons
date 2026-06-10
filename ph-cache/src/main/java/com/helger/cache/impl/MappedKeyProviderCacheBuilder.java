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

import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.builder.IBuilder;
import com.helger.cache.eviction.CacheEvictionScheduler;

/**
 * Builder class for {@link MappedKeyProviderCache} objects.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The cache query key type
 * @param <KEYSTORETYPE>
 *        The internal storage key type
 * @param <VALUETYPE>
 *        The cache value type
 * @since v12.3.0
 */
@NotThreadSafe
public class MappedKeyProviderCacheBuilder <KEYTYPE, KEYSTORETYPE, VALUETYPE> extends
                                           CacheBuilderBase <MappedKeyProviderCacheBuilder <KEYTYPE, KEYSTORETYPE, VALUETYPE>>
                                           implements
                                           IBuilder <MappedKeyProviderCache <KEYTYPE, KEYSTORETYPE, VALUETYPE>>
{
  private Function <KEYTYPE, KEYSTORETYPE> m_aKeyMapper;
  private Function <KEYTYPE, VALUETYPE> m_aValueProvider;

  /**
   * Default constructor.
   */
  public MappedKeyProviderCacheBuilder ()
  {}

  /**
   * Set the function that maps the public-facing query key to the internal storage key.
   *
   * @param a
   *        The key mapper function. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public MappedKeyProviderCacheBuilder <KEYTYPE, KEYSTORETYPE, VALUETYPE> keyMapper (@Nullable final Function <KEYTYPE, KEYSTORETYPE> a)
  {
    m_aKeyMapper = a;
    return this;
  }

  /**
   * Set the function to compute cache values on a cache miss. Invoked with the original query key.
   *
   * @param a
   *        The value provider function. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public MappedKeyProviderCacheBuilder <KEYTYPE, KEYSTORETYPE, VALUETYPE> valueProvider (@Nullable final Function <KEYTYPE, VALUETYPE> a)
  {
    m_aValueProvider = a;
    return this;
  }

  /**
   * Build the {@link MappedKeyProviderCache} instance from the configured parameters.
   *
   * @return A new {@link MappedKeyProviderCache} instance. Never <code>null</code>.
   * @throws IllegalStateException
   *         if the key mapper, value provider or cache name is missing, or if an eviction interval
   *         is set without a time-to-live.
   */
  @NonNull
  public MappedKeyProviderCache <KEYTYPE, KEYSTORETYPE, VALUETYPE> build ()
  {
    checkCommonFields ();
    if (m_aKeyMapper == null)
      throw new IllegalStateException ("The mandatory Cache Key Mapper is missing");
    if (m_aValueProvider == null)
      throw new IllegalStateException ("The mandatory Cache Value Provider is missing");

    final MappedKeyProviderCache <KEYTYPE, KEYSTORETYPE, VALUETYPE> ret = new MappedKeyProviderCache <> (m_sName,
                                                                                                         m_nMaxSize,
                                                                                                         m_bAllowNullValues,
                                                                                                         m_aTimeToLive,
                                                                                                         m_aClockSupplier,
                                                                                                         m_aKeyMapper,
                                                                                                         m_aValueProvider);
    if (hasEvictionInterval ())
      CacheEvictionScheduler.getInstance ().register (ret, m_aEvictionInterval);
    return ret;
  }
}
