/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.annotation.style.UseDirectEqualsAndHashCode;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.ICommonsOrderedSet;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A special ordered set, that has an upper limit of contained elements. It is
 * therefore a "Last Recently Used" cache.<br>
 * The underlying data structure is a {@link LRUMap} map.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        Element type
 */
@NotThreadSafe
@UseDirectEqualsAndHashCode
public class LRUSet <ELEMENTTYPE> extends AbstractSet <ELEMENTTYPE> implements ICommonsOrderedSet <ELEMENTTYPE>
{
  @UseDirectEqualsAndHashCode
  private final class LRUCacheMap extends LRUMap <ELEMENTTYPE, Boolean>
  {
    LRUCacheMap (@Nonnegative final int nMaxSize)
    {
      super (nMaxSize);
    }

    LRUCacheMap (@Nonnull final LRUCacheMap rhs)
    {
      super (rhs);
    }

    @Override
    protected void onRemoveEldestEntry (@Nonnegative final int nSize, @Nonnull final Map.Entry <ELEMENTTYPE, Boolean> aEldest)
    {
      LRUSet.this.onRemoveEldestEntry (nSize, aEldest.getKey ());
    }
  }

  private final LRUCacheMap m_aMap;

  public LRUSet (@Nonnegative final int nMaxSize)
  {
    m_aMap = new LRUCacheMap (nMaxSize);
  }

  public LRUSet (@Nonnegative final LRUSet <ELEMENTTYPE> rhs)
  {
    m_aMap = new LRUCacheMap (rhs.m_aMap);
  }

  @Nonnull
  public LRUSet <ELEMENTTYPE> getClone ()
  {
    return new LRUSet <> (this);
  }

  /**
   * Protected method that is invoked every time an element is removed from the
   * cache, because the maximum size is exceeded.
   *
   * @param nSize
   *        Current size of the map. Always &ge; 0.
   * @param aEldest
   *        The entry that is to be removed. Never <code>null</code>.
   */
  @OverrideOnDemand
  protected void onRemoveEldestEntry (@Nonnegative final int nSize, @Nonnull final ELEMENTTYPE aEldest)
  {}

  /**
   * @return The maximum number of elements that can reside in this cache.
   */
  @Nonnegative
  public final int getMaxSize ()
  {
    return m_aMap.getMaxSize ();
  }

  @Override
  public boolean add (@Nullable final ELEMENTTYPE aItem)
  {
    if (contains (aItem))
      return false;
    m_aMap.put (aItem, Boolean.TRUE);
    return true;
  }

  @Override
  @Nonnull
  public Iterator <ELEMENTTYPE> iterator ()
  {
    return m_aMap.keySet ().iterator ();
  }

  @Override
  @Nonnegative
  public int size ()
  {
    return m_aMap.size ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final LRUSet <?> rhs = (LRUSet <?>) o;
    return m_aMap.equals (rhs.m_aMap);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aMap).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("map", m_aMap).getToString ();
  }
}
