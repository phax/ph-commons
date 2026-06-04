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

import com.helger.base.iface.IHasSize;
import com.helger.base.name.IHasName;

/**
 * Read-only interface for a very simple Map-like cache.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Cache key type.
 * @param <VALUETYPE>
 *        Cache value type.
 */
public interface ICache <KEYTYPE, VALUETYPE> extends IHasName, IHasSize
{
  /**
   * Check if the passed key is already in the cache or not. An entry that is past its time-based
   * expiration is considered as <em>not</em> in the cache for the purposes of this check.
   *
   * @param aKey
   *        The key to check. May be <code>null</code>.
   * @return <code>true</code> if the value is already in the cache, <code>false</code> if not.
   * @since 12.3.0 in this interface
   */
  boolean isInCache (KEYTYPE aKey);

  /**
   * Get the cached value associated with the passed key. If the value is not in the cache, it might
   * be automatically retrieved from a respective provider.
   *
   * @param aKey
   *        The key to be looked up. May be <code>null</code>able or not - depends upon the
   *        implementation.
   * @return <code>null</code> if no such value is in the cache.
   */
  VALUETYPE getFromCache (KEYTYPE aKey);

  /**
   * @return The maximum number of entries allowed in this cache. Values &le; 0 indicate that the
   *         cache size is not limited at all.
   * @since 12.3.0 in this interface
   * @see #hasMaxSize()
   */
  int getMaxSize ();

  /**
   * @return <code>true</code> if this cache has a size limit, <code>false</code> if not.
   * @since 12.3.0 in this interface
   * @see #getMaxSize()
   */
  default boolean hasMaxSize ()
  {
    return getMaxSize () > 0;
  }

  /**
   * @return <code>true</code> if <code>null</code> can be in the cache, <code>false</code> if not.
   * @since 12.3.0 in this interface
   */
  boolean isAllowNullValues ();
}
