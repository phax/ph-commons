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
   * Remove all entries that are expired by their time-based expiration. For caches without a
   * time-based expiration policy this is a no-op.
   *
   * @return The number of entries removed. Always &ge; 0.
   */
  @Nonnegative
  int evictExpired ();
}
