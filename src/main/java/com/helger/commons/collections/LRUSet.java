/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.collections;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.IHasSize;
import com.helger.commons.annotations.OverrideOnDemand;
import com.helger.commons.annotations.UseDirectEqualsAndHashCode;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * A special ordered set, that has an upper limit of contained elements. It is
 * therefore a "Last Recently Used" cache.<br>
 * The underlying data structure is a {@link LRUCache} map.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        Element type
 */
@NotThreadSafe
@UseDirectEqualsAndHashCode
public class LRUSet <ELEMENTTYPE> extends AbstractSet <ELEMENTTYPE> implements IHasSize, Serializable
{
  @UseDirectEqualsAndHashCode
  private static final class LRUCacheMap <ELEMENTTYPE> extends LRUCache <ELEMENTTYPE, Boolean>
  {
    private final LRUSet <ELEMENTTYPE> m_aOwningSet;

    LRUCacheMap (@Nonnegative final int nMaxSize, @Nonnull final LRUSet <ELEMENTTYPE> aOwningSet)
    {
      super (nMaxSize);
      m_aOwningSet = aOwningSet;
    }

    @Override
    protected void onRemoveEldestEntry (final Map.Entry <ELEMENTTYPE, Boolean> aEldest)
    {
      m_aOwningSet.onRemoveEldestEntry (aEldest.getKey ());
    }

    @Override
    public boolean equals (final Object o)
    {
      return super.equals (o);
    }

    @Override
    public int hashCode ()
    {
      return super.hashCode ();
    }
  }

  private final LRUCacheMap <ELEMENTTYPE> m_aCache;

  public LRUSet (@Nonnegative final int nMaxSize)
  {
    m_aCache = new LRUCacheMap <ELEMENTTYPE> (nMaxSize, this);
  }

  /**
   * Protected method that is invoked every time an element is removed from the
   * cache, because the maximum size is exceeded.
   *
   * @param aEldest
   *        The entry that is to be removed. Never <code>null</code>.
   */
  @OverrideOnDemand
  protected void onRemoveEldestEntry (@Nonnull final ELEMENTTYPE aEldest)
  {}

  /**
   * @return The maximum number of elements that can reside in this cache.
   */
  @Nonnegative
  public final int getMaxSize ()
  {
    return m_aCache.getMaxSize ();
  }

  @Override
  public boolean add (@Nullable final ELEMENTTYPE aItem)
  {
    if (contains (aItem))
      return false;
    m_aCache.put (aItem, Boolean.TRUE);
    return true;
  }

  @Override
  @Nonnull
  public Iterator <ELEMENTTYPE> iterator ()
  {
    return m_aCache.keySet ().iterator ();
  }

  @Override
  @Nonnegative
  public int size ()
  {
    return m_aCache.size ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final LRUSet <?> rhs = (LRUSet <?>) o;
    return m_aCache.equals (rhs.m_aCache);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aCache).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("cache", m_aCache).toString ();
  }
}
