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
package com.helger.commons.cache;

import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * A caching class that has the ability to fill itself with the abstract
 * getValueToCache(Object) method and has an upper limit of elements that can
 * reside in the cache.
 * 
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Cache key type
 * @param <VALUETYPE>
 *        Cache value type
 */
@NotThreadSafe
public abstract class AbstractNotifyingCacheWithMaxSize <KEYTYPE, VALUETYPE> extends AbstractNotifyingCache <KEYTYPE, VALUETYPE>
{
  private final int m_nMaxSize;

  public AbstractNotifyingCacheWithMaxSize (@Nonnull final String sCacheName, @Nonnegative final int nMaxSize)
  {
    super (sCacheName);
    m_nMaxSize = ValueEnforcer.isGE0 (nMaxSize, "MaxSize");
  }

  /**
   * @return The maximum number of entries in this cache. Always &gt; 0.
   */
  @Nonnegative
  public final int getMaxSize ()
  {
    // No need to lock, as it is final
    return m_nMaxSize;
  }

  /**
   * @return A special map to hold the cache objects with a maximum size. Never
   *         <code>null</code>.
   */
  @Override
  @Nonnull
  protected final Map <KEYTYPE, VALUETYPE> createCache ()
  {
    return new LoggingLRUCache <KEYTYPE, VALUETYPE> (getName (), m_nMaxSize);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("maxSize", m_nMaxSize).toString ();
  }
}
