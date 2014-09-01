/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.cache.convert;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.cache.AbstractCache;
import com.helger.commons.convert.IUnidirectionalConverter;

/**
 * A special cache that can create the value to be cache automatically from the
 * key.
 * 
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Cache key type
 * @param <VALUETYPE>
 *        Cache value type
 */
@ThreadSafe
public class SimpleCacheWithConversion <KEYTYPE, VALUETYPE> extends AbstractCache <KEYTYPE, VALUETYPE>
{
  public SimpleCacheWithConversion (@Nonnull final String sCacheName)
  {
    super (sCacheName);
  }

  /**
   * Get the value from the cache. If no value is yet in the cache, the passed
   * converter is used to get the value to cache.
   * 
   * @param aKey
   *        The key of the cached object. May not be <code>null</code>.
   * @param aValueRetriever
   *        The converter to be used to retrieve the object to cache. May not be
   *        <code>null</code>. This converter may not return <code>null</code>
   *        objects to cache!
   * @return The cached value. Never <code>null</code>.
   */
  @Nonnull
  public final VALUETYPE getFromCache (@Nonnull final KEYTYPE aKey,
                                       @Nonnull final IUnidirectionalConverter <KEYTYPE, VALUETYPE> aValueRetriever)
  {
    // Already in the cache?
    VALUETYPE aValue = super.getFromCacheNoStats (aKey);
    if (aValue == null)
    {
      // No old value in the cache
      m_aRWLock.writeLock ().lock ();
      try
      {
        // Read again, in case the value was set between the two locking
        // sections
        // Note: do not increase statistics in this second try
        aValue = super.getFromCacheNoStatsNotLocked (aKey);
        if (aValue == null)
        {
          // Get the value to cache
          aValue = aValueRetriever.convert (aKey);

          // We cannot cache null values!
          if (aValue == null)
            throw new IllegalStateException ("The converter returned a null object for the key '" + aKey + "'");

          super.putInCacheNotLocked (aKey, aValue);
          m_aCacheAccessStats.cacheMiss ();
        }
        else
          m_aCacheAccessStats.cacheHit ();
      }
      finally
      {
        m_aRWLock.writeLock ().unlock ();
      }
    }
    else
      m_aCacheAccessStats.cacheHit ();
    return aValue;
  }
}
