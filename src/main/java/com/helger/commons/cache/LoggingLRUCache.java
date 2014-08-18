/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.collections.LRUCache;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * A specific {@link LRUCache} that emits a warning once the cache is full.
 * 
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Cache key type
 * @param <VALUETYPE>
 *        Cache value type
 */
@NotThreadSafe
public final class LoggingLRUCache <KEYTYPE, VALUETYPE> extends LRUCache <KEYTYPE, VALUETYPE>
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (LoggingLRUCache.class);

  private final String m_sCacheName;

  public LoggingLRUCache (@Nonnull @Nonempty final String sCacheName, @Nonnegative final int nMaxSize)
  {
    super (nMaxSize);
    m_sCacheName = ValueEnforcer.notEmpty (sCacheName, "CacheName");
  }

  @Nonnull
  @Nonempty
  public String getCacheName ()
  {
    return m_sCacheName;
  }

  @Override
  protected void onRemoveEldestEntry (@Nonnull final Map.Entry <KEYTYPE, VALUETYPE> aEntry)
  {
    s_aLogger.warn ("Cache '" +
                    m_sCacheName +
                    "' is full with " +
                    getMaxSize () +
                    " items! Removed " +
                    aEntry.getKey () +
                    "//" +
                    aEntry.getValue ());
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final LoggingLRUCache <?, ?> rhs = (LoggingLRUCache <?, ?>) o;
    return m_sCacheName.equals (rhs.m_sCacheName);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_sCacheName).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("cacheName", m_sCacheName).toString ();
  }
}
