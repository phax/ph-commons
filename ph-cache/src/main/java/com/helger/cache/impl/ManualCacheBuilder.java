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

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.builder.IBuilder;
import com.helger.cache.eviction.CacheEvictionScheduler;

/**
 * Builder class for {@link ManualCache} objects.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The cache key type
 * @param <VALUETYPE>
 *        The cache value type
 * @since v12.0.0
 */
@NotThreadSafe
public class ManualCacheBuilder <KEYTYPE, VALUETYPE> extends CacheBuilderBase <ManualCacheBuilder <KEYTYPE, VALUETYPE>>
                                implements
                                IBuilder <ManualCache <KEYTYPE, VALUETYPE>>
{
  /**
   * Default constructor.
   */
  public ManualCacheBuilder ()
  {}

  /**
   * Build the {@link ManualCache} instance from the configured parameters.
   *
   * @return A new {@link ManualCache} instance. Never <code>null</code>.
   * @throws IllegalStateException
   *         if the value provider or cache name is missing, or if an eviction interval is set
   *         without a time-to-live.
   */
  @NonNull
  public ManualCache <KEYTYPE, VALUETYPE> build ()
  {
    checkCommonFields ();

    final ManualCache <KEYTYPE, VALUETYPE> ret = new ManualCache <> (m_sName,
                                                                     m_nMaxSize,
                                                                     m_bAllowNullValues,
                                                                     m_aTimeToLive,
                                                                     m_aClockSupplier);
    if (hasEvictionInterval ())
      CacheEvictionScheduler.getInstance ().register (ret, m_aEvictionInterval);
    return ret;
  }
}
