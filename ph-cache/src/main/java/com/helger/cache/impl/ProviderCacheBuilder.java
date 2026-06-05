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
 * Builder class for {@link ProviderCache} objects.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The cache key type
 * @param <VALUETYPE>
 *        The cache value type
 * @since v12.3.0
 */
@NotThreadSafe
public class ProviderCacheBuilder <KEYTYPE, VALUETYPE> extends
                                  CacheBuilderBase <ProviderCacheBuilder <KEYTYPE, VALUETYPE>> implements
                                  IBuilder <ProviderCache <KEYTYPE, VALUETYPE>>
{
  private Function <KEYTYPE, VALUETYPE> m_aValueProvider;

  /**
   * Default constructor.
   */
  public ProviderCacheBuilder ()
  {}

  /**
   * Set the function to compute cache values.
   *
   * @param a
   *        The value provider function. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public ProviderCacheBuilder <KEYTYPE, VALUETYPE> valueProvider (@Nullable final Function <KEYTYPE, VALUETYPE> a)
  {
    m_aValueProvider = a;
    return this;
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
  public ProviderCache <KEYTYPE, VALUETYPE> build ()
  {
    checkCommonFields ();
    if (m_aValueProvider == null)
      throw new IllegalStateException ("The mandatory Cache Value Provider is missing");

    final ProviderCache <KEYTYPE, VALUETYPE> ret = new ProviderCache <> (m_sName,
                                                                         m_nMaxSize,
                                                                         m_bAllowNullValues,
                                                                         m_aTimeToLive,
                                                                         m_aClockSupplier,
                                                                         m_aValueProvider);
    if (hasEvictionInterval ())
      CacheEvictionScheduler.getInstance ().register (ret, m_aEvictionInterval);
    return ret;
  }
}
