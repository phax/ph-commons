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

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.ELockType;
import com.helger.commons.annotation.IsLocked;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A caching class that has the ability to fill itself with the abstract
 * getValueToCache(Object) method.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Cache key type
 * @param <VALUETYPE>
 *        Cache value type
 */
@ThreadSafe
public abstract class AbstractNotifyingCache <KEYTYPE, VALUETYPE> extends AbstractCache <KEYTYPE, VALUETYPE>
{
  public AbstractNotifyingCache (@Nonnull final String sCacheName)
  {
    this (CGlobal.ILLEGAL_UINT, sCacheName);
  }

  public AbstractNotifyingCache (@CheckForSigned final int nMaxSize, @Nonnull final String sCacheName)
  {
    super (nMaxSize, sCacheName);
  }

  /**
   * This abstract method is invoked, once a new value needs to be put into the
   * cache. This method is invoked within a locked section.
   *
   * @param aKey
   *        The key for which the value to cache is required. May be
   *        <code>null</code>able or not - depends upon the implementation.
   * @return The value to be cached. May not be <code>null</code>.
   */
  @Nonnull
  @IsLocked (ELockType.WRITE)
  protected abstract VALUETYPE getValueToCache (KEYTYPE aKey);

  @Override
  @SuppressFBWarnings ("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
  public final VALUETYPE getFromCache (final KEYTYPE aKey)
  {
    // read existing value
    VALUETYPE aValue = super.getFromCacheNoStats (aKey);

    if (aValue == null)
    {
      // No old value in the cache
      aValue = m_aRWLock.writeLocked ( () -> {
        // Read again, in case the value was set between the two locking
        // sections
        // Note: do not increase statistics in this second try
        VALUETYPE aWLValue = super.getFromCacheNoStatsNotLocked (aKey);
        if (aWLValue == null)
        {
          // Call the abstract method to create the value to cache
          aWLValue = getValueToCache (aKey);

          // Just a consistency check
          if (aWLValue == null)
            throw new IllegalStateException ("The value to cache was null for key '" + aKey + "'");

          // Put the new value into the cache
          super.putInCacheNotLocked (aKey, aWLValue);
          m_aCacheAccessStats.cacheMiss ();
        }
        else
          m_aCacheAccessStats.cacheHit ();
        return aWLValue;
      });
    }
    else
      m_aCacheAccessStats.cacheHit ();

    return aValue;
  }
}
