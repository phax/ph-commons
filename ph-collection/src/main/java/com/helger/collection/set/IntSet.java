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
package com.helger.collection.set;

import java.util.function.IntConsumer;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.iface.IHasSize;
import com.helger.collection.map.IntObjectMap;

/**
 * Special int-Set. Uses {@link IntObjectMap} internally.
 *
 * @author Philip Helger
 * @since 9.0.0
 */
@NotThreadSafe
public class IntSet implements IHasSize
{
  private final IntObjectMap <Boolean> m_aMap;

  /**
   * Create a new int set with default initial capacity.
   */
  public IntSet ()
  {
    this (16);
  }

  /**
   * Create a new int set with the specified initial capacity.
   *
   * @param nSize
   *        The initial capacity.
   */
  public IntSet (final int nSize)
  {
    this (nSize, 0.75f);
  }

  /**
   * Create a new int set with the specified initial capacity and fill factor.
   *
   * @param nSize
   *        The initial capacity. Must be &gt; 0.
   * @param fFillFactor
   *        The fill factor. Must be between 0 and 1 (inclusive).
   */
  public IntSet (final int nSize, final float fFillFactor)
  {
    m_aMap = new IntObjectMap <> (nSize, fFillFactor);
  }

  /** {@inheritDoc} */
  public int size ()
  {
    return m_aMap.size ();
  }

  /** {@inheritDoc} */
  public boolean isEmpty ()
  {
    return m_aMap.isEmpty ();
  }

  /**
   * Check if the set contains the given key.
   *
   * @param key
   *        The key to check.
   * @return <code>true</code> if the key is contained, <code>false</code> otherwise.
   */
  public boolean contains (final int key)
  {
    return m_aMap.get (key) != null;
  }

  /**
   * Add a key to this set.
   *
   * @param key
   *        The key to add.
   * @return <code>true</code> if the key was added, <code>false</code> if it was already present.
   */
  public boolean add (final int key)
  {
    return m_aMap.put (key, Boolean.TRUE) == null;
  }

  /**
   * Iterate over all keys in this set.
   *
   * @param aConsumer
   *        The consumer to invoke for each key. May not be <code>null</code>.
   */
  public void forEach (@NonNull final IntConsumer aConsumer)
  {
    m_aMap.forEach ( (k, v) -> aConsumer.accept (k));
  }
}
