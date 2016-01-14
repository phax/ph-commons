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
package com.helger.commons.collection.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.annotation.OverrideOnDemand;

/**
 * Soft {@link HashMap} implementation based on
 * http://www.javaspecialists.eu/archive/Issue015.html<br>
 * The <code>entrySet</code> implementation is from
 * <code>org.hypergraphdb.util</code>
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

  @FunctionalInterface
  private static interface IRemoveEldest <K, V>
  {
    boolean removeEldestSoftEntry (Map.Entry <K, V> aEntry);
  }

  private static class InternalLinkedHashMap <K, V> extends LinkedHashMap <K, SoftValue <K, V>>
  {
    // Note: 0.75f is the same as HashMap.DEFAULT_LOAD_FACTOR
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private IRemoveEldest <K, V> m_aCallback;

    public InternalLinkedHashMap (@Nonnegative final int nMaxSize)
    {
      super (nMaxSize, DEFAULT_LOAD_FACTOR, true);
    }

    @Override
    protected final boolean removeEldestEntry (@Nonnull final Map.Entry <K, SoftValue <K, V>> aEldest)
    {
      final MapEntry <K, V> aEntry = new MapEntry <K, V> (aEldest.getKey (), aEldest.getValue ().get ());
      return m_aCallback.removeEldestSoftEntry (aEntry);
    }
  }

  public SoftLinkedHashMap (@Nonnegative final int nMaxSize)
  {
    super (new InternalLinkedHashMap <K, V> (nMaxSize));
    m_nMaxSize = nMaxSize;
    ((InternalLinkedHashMap <K, V>) m_aSrcMap).m_aCallback = aEldest -> {
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
}
