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
package com.helger.commons.collection.map;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.Nonnull;

import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.hashcode.HashCodeGenerator;

/**
 * Soft {@link HashMap} implementation based on
 * http://www.javaspecialists.eu/archive/Issue015.html<br>
 * The <code>entrySet</code> implementation is from
 * <code>org.hypergraphdb.util</code><br>
 * Note: {@link SoftLinkedHashMap} is <b>NOT</b> serializable!
 *
 * @author Philip Helger
 * @param <K>
 *        Key type
 * @param <V>
 *        Value type
 */
public class SoftLinkedHashMap <K, V> extends AbstractSoftMap <K, V>
{
  private final int m_nMaxSize;

  private static class InternalLinkedHashMap <K, V> extends CommonsLinkedHashMap <K, SoftValue <K, V>>
  {
    // Note: 0.75f is the same as HashMap.DEFAULT_LOAD_FACTOR
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private transient Predicate <? super Map.Entry <K, V>> m_aFilter;

    public InternalLinkedHashMap (@Nonnegative final int nMaxSize)
    {
      super (nMaxSize, DEFAULT_LOAD_FACTOR, true);
    }

    @Override
    protected final boolean removeEldestEntry (@Nonnull final Map.Entry <K, SoftValue <K, V>> aEldest)
    {
      final MapEntry <K, V> aEntry = new MapEntry <> (aEldest.getKey (), aEldest.getValue ().get ());
      return m_aFilter.test (aEntry);
    }

    @Override
    public boolean equals (final Object o)
    {
      if (o == this)
        return true;
      if (o == null || !getClass ().equals (o.getClass ()))
        return false;
      return super.equals (o);
    }

    @Override
    public int hashCode ()
    {
      return super.hashCode ();
    }
  }

  public SoftLinkedHashMap (@Nonnegative final int nMaxSize)
  {
    super (new InternalLinkedHashMap <> (nMaxSize));
    m_nMaxSize = nMaxSize;
    // Must be set explicitly for dependency handling
    ((InternalLinkedHashMap <K, V>) m_aSrcMap).m_aFilter = aEldest -> {
      final int nSize = size ();
      if (nSize <= m_nMaxSize)
      {
        // No need to remove anything
        return false;
      }

      // Invoke protected method
      SoftLinkedHashMap.this.onRemoveEldestEntry (nSize, aEldest);
      return true;
    };
  }

  /**
   * @return The maximum number of elements that can reside inside this object.
   *         Never &lt; 0.
   */
  @Nonnegative
  public final int getMaxSize ()
  {
    return m_nMaxSize;
  }

  /**
   * Protected method that is invoked every time the oldest entry is removed.
   *
   * @param nSize
   *        Current size of the map. Always &ge; 0.
   * @param aEldest
   *        The map entry that is removed. Never <code>null</code>.
   */
  @OverrideOnDemand
  protected void onRemoveEldestEntry (@Nonnegative final int nSize, @Nonnull final Map.Entry <K, V> aEldest)
  {}

  @Nonnull
  @ReturnsMutableCopy
  public SoftLinkedHashMap <K, V> getClone ()
  {
    final SoftLinkedHashMap <K, V> ret = new SoftLinkedHashMap <> (m_nMaxSize);
    ret.putAll (this);
    return ret;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final SoftLinkedHashMap <?, ?> rhs = (SoftLinkedHashMap <?, ?>) o;
    return m_nMaxSize == rhs.m_nMaxSize;
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_nMaxSize).getHashCode ();
  }
}
