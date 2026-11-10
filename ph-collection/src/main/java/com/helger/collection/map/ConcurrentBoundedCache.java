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
package com.helger.collection.map;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.iface.IHasSize;
import com.helger.base.tostring.ToStringGenerator;

/**
 * A simple thread-safe cache with an upper bound of entries, backed by a {@link ConcurrentHashMap}.
 * Reading is lock-free.
 * <p>
 * Eviction is approximate and cheap: when an insertion makes the cache exceed the maximum size,
 * the thread that crossed the limit removes arbitrary entries (in the iteration order of the
 * backing map) until at most 75% of the maximum size is left. Only one thread evicts at a time;
 * other threads continue without waiting, so the size may temporarily exceed the maximum size
 * under contention.
 * </p>
 * <p>
 * Note: this is <b>not</b> an LRU cache - which entries are evicted is undefined. Use
 * {@link LRUMap} (with external synchronisation) if the least recently used entries must be
 * evicted.
 * </p>
 * Neither <code>null</code> keys nor <code>null</code> values are supported.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Cache key type
 * @param <VALUETYPE>
 *        Cache value type
 * @since 12.5.4
 */
@ThreadSafe
public class ConcurrentBoundedCache <KEYTYPE, VALUETYPE> implements IHasSize
{
  private final int m_nMaxSize;
  private final int m_nEvictToSize;
  private final ConcurrentHashMap <KEYTYPE, VALUETYPE> m_aMap;
  private final AtomicBoolean m_aEvicting = new AtomicBoolean (false);

  /**
   * Constructor.
   *
   * @param nMaxSize
   *        The maximum number of entries in the cache. Must be &gt; 0.
   */
  public ConcurrentBoundedCache (@Nonnegative final int nMaxSize)
  {
    ValueEnforcer.isGT0 (nMaxSize, "MaxSize");
    m_nMaxSize = nMaxSize;
    m_nEvictToSize = (int) (nMaxSize * 3L / 4);
    // The ConcurrentHashMap constructor takes the number of elements, not the table size
    m_aMap = new ConcurrentHashMap <> (Math.min (nMaxSize, 1024));
  }

  private void _evictIfNeeded ()
  {
    if (m_aMap.size () > m_nMaxSize && m_aEvicting.compareAndSet (false, true))
    {
      try
      {
        final Iterator <KEYTYPE> it = m_aMap.keySet ().iterator ();
        while (m_aMap.size () > m_nEvictToSize && it.hasNext ())
        {
          it.next ();
          it.remove ();
        }
      }
      finally
      {
        m_aEvicting.set (false);
      }
    }
  }

  /**
   * @return The maximum number of entries as provided in the constructor. Always &gt; 0.
   */
  @Nonnegative
  public final int getMaxSize ()
  {
    return m_nMaxSize;
  }

  /**
   * Get the cached value for the provided key. This method does not lock.
   *
   * @param aKey
   *        The key to look up. May not be <code>null</code>.
   * @return The cached value or <code>null</code> if no value is cached.
   */
  @Nullable
  public VALUETYPE get (@NonNull final KEYTYPE aKey)
  {
    return m_aMap.get (aKey);
  }

  /**
   * Get the cached value for the provided key or compute and cache it if it is not present.
   * <p>
   * The value is computed outside of any lock of the backing map, so that a slow computation does
   * not block other threads. As a consequence the value may be computed more than once for the
   * same key under contention; the first value stored wins and is returned to all callers. The
   * computation function should therefore be free of side effects.
   * </p>
   *
   * @param aKey
   *        The key to look up. May not be <code>null</code>.
   * @param aValueProvider
   *        The function to compute the value if it is not cached. May not be <code>null</code> and
   *        must not return <code>null</code>.
   * @return The cached or computed value. Never <code>null</code>.
   * @throws IllegalStateException
   *         if the value provider returned <code>null</code>
   */
  @NonNull
  public VALUETYPE getOrCompute (@NonNull final KEYTYPE aKey,
                                 @NonNull final Function <? super KEYTYPE, ? extends VALUETYPE> aValueProvider)
  {
    final VALUETYPE aExisting = m_aMap.get (aKey);
    if (aExisting != null)
      return aExisting;

    final VALUETYPE aNewValue = aValueProvider.apply (aKey);
    if (aNewValue == null)
      throw new IllegalStateException ("The value provider returned null for key '" + aKey + "'");

    final VALUETYPE aOldValue = m_aMap.putIfAbsent (aKey, aNewValue);
    if (aOldValue != null)
    {
      // Another thread was faster
      return aOldValue;
    }
    _evictIfNeeded ();
    return aNewValue;
  }

  /**
   * Put a value into the cache, replacing an eventually existing value.
   *
   * @param aKey
   *        The key to use. May not be <code>null</code>.
   * @param aValue
   *        The value to cache. May not be <code>null</code>.
   * @return The previous value or <code>null</code> if there was none.
   */
  @Nullable
  public VALUETYPE put (@NonNull final KEYTYPE aKey, @NonNull final VALUETYPE aValue)
  {
    ValueEnforcer.notNull (aValue, "Value");
    final VALUETYPE ret = m_aMap.put (aKey, aValue);
    if (ret == null)
      _evictIfNeeded ();
    return ret;
  }

  /**
   * Remove the value of the provided key from the cache.
   *
   * @param aKey
   *        The key to remove. May not be <code>null</code>.
   * @return The removed value or <code>null</code> if there was none.
   */
  @Nullable
  public VALUETYPE remove (@NonNull final KEYTYPE aKey)
  {
    return m_aMap.remove (aKey);
  }

  /**
   * Remove all entries from the cache.
   */
  public void clear ()
  {
    m_aMap.clear ();
  }

  /**
   * {@inheritDoc} Under contention this may temporarily be larger than {@link #getMaxSize()}.
   */
  @Nonnegative
  public int size ()
  {
    return m_aMap.size ();
  }

  /** {@inheritDoc} */
  public boolean isEmpty ()
  {
    return m_aMap.isEmpty ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("MaxSize", m_nMaxSize)
                                       .append ("EvictToSize", m_nEvictToSize)
                                       .append ("Size", m_aMap.size ())
                                       .getToString ();
  }
}
