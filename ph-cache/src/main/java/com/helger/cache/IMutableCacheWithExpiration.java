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
package com.helger.cache;

import java.time.Duration;
import java.time.LocalDateTime;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;

/**
 * Interface for a simple, map-like cache, including a "evictExpired" method.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Cache key type.
 * @param <VALUETYPE>
 *        Cache value type.
 * @since 12.3.0
 */
public interface IMutableCacheWithExpiration <KEYTYPE, VALUETYPE> extends
                                             IMutableCache <KEYTYPE, VALUETYPE>,
                                             ICacheWithExpiration <KEYTYPE, VALUETYPE>
{
  /**
   * Put a value into the cache with an explicit per-entry time-to-live. The provided duration
   * overrides the cache-wide TTL (if any) for this entry only; other entries are unaffected. The
   * absolute expiration timestamp is computed as
   * <code>getClockSupplier().get() + aTimeToLive</code> at insertion time.
   *
   * @param aKey
   *        The key to be added. Usually non-<code>null</code>.
   * @param aValue
   *        The value to be added. Nullability follows the configured null-value policy.
   * @param aTimeToLive
   *        Time after which this entry is considered expired, counted from "now" as provided by
   *        {@link #getClockSupplier()}. May not be <code>null</code> and must be strictly positive.
   */
  void putInCache (KEYTYPE aKey, VALUETYPE aValue, @NonNull Duration aTimeToLive);

  /**
   * Put a value into the cache that expires at the provided absolute date time. The provided
   * timestamp overrides the cache-wide TTL (if any) for this entry only; other entries are
   * unaffected.
   *
   * @param aKey
   *        The key to be added. Usually non-<code>null</code>.
   * @param aValue
   *        The value to be added. Nullability follows the configured null-value policy.
   * @param aExpirationDT
   *        The absolute date time at which this entry is considered expired. May not be
   *        <code>null</code>. Compared against {@link #getClockSupplier()}.
   */
  void putInCache (KEYTYPE aKey, VALUETYPE aValue, @NonNull LocalDateTime aExpirationDT);

  /**
   * Remove all entries whose per-entry expiration timestamp has passed. Both the cache-wide TTL and
   * any per-entry expirations set via {@link #putInCache(Object, Object, Duration)} or
   * {@link #putInCache(Object, Object, LocalDateTime)} are honored. For caches that contain no
   * expiring entries this is effectively a no-op returning <code>0</code>.
   *
   * @return The number of entries removed. Always &ge; 0.
   */
  @Nonnegative
  int evictExpired ();
}
