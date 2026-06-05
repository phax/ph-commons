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
import java.time.ZoneOffset;
import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Read-only interface for a cache that supports expiration of entries.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Cache key type.
 * @param <VALUETYPE>
 *        Cache value type.
 * @since 12.3.0
 */
public interface ICacheWithExpiration <KEYTYPE, VALUETYPE> extends ICache <KEYTYPE, VALUETYPE>
{
  /**
   * UTC is used to avoid DST jumps in the expiration arithmetic; the absolute time zone is
   * irrelevant because the same supplier is used for writes and expiry checks.
   */
  Supplier <LocalDateTime> DEFAULT_CLOCK_SUPPLIER = () -> LocalDateTime.now (ZoneOffset.UTC);

  /**
   * @return The configured time to live for cache entries, or <code>null</code> if no time-based
   *         expiration is configured.
   */
  @Nullable
  Duration getTimeToLive ();

  /**
   * @return <code>true</code> if a positive time to live is configured, <code>false</code>
   *         otherwise.
   */
  default boolean hasTimeToLive ()
  {
    return getTimeToLive () != null;
  }

  /**
   * @return The clock supplier currently in use. Never <code>null</code>.
   */
  @NonNull
  Supplier <LocalDateTime> getClockSupplier ();
}
