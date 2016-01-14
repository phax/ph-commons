/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;

import com.helger.commons.state.EChange;

/**
 * Interface for a very simple Map-like cache.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Cache key type.
 * @param <VALUETYPE>
 *        Cache value type.
 */
public interface IMutableCache <KEYTYPE, VALUETYPE> extends ICache <KEYTYPE, VALUETYPE>
{
  /**
   * Remove the given key from the cache.
   *
   * @param aKey
   *        The key to be removed. May be <code>null</code>able or not - depends
   *        upon the implementation.
   * @return {@link EChange#CHANGED} upon success, {@link EChange#UNCHANGED} if
   *         the key was not within the cache,
   */
  @Nonnull
  EChange removeFromCache (KEYTYPE aKey);

  /**
   * Remove all cached elements.
   *
   * @return {@link EChange}.
   */
  @Nonnull
  EChange clearCache ();
}
