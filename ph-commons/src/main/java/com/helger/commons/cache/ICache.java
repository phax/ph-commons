/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.commons.cache;

import com.helger.commons.lang.IHasSize;
import com.helger.commons.name.IHasName;

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
   * Get the cached value associated with the passed key. If the value is not in
   * the cache, it might be automatically retrieved from a respective provider.
   *
   * @param aKey
   *        The key to be looked up. May be <code>null</code>able or not -
   *        depends upon the implementation.
   * @return <code>null</code> if no such value is in the cache.
   */
  VALUETYPE getFromCache (KEYTYPE aKey);
}
