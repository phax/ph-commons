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
package com.helger.commons.collection.lru;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.UseDirectEqualsAndHashCode;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * A special ordered map, that has an upper limit of contained elements. It is
 * therefore a "Last Recently Used" map.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <VALUETYPE>
 *        Value type
 */
@NotThreadSafe
@UseDirectEqualsAndHashCode
public class LRUMap <KEYTYPE, VALUETYPE> extends LinkedHashMap <KEYTYPE, VALUETYPE>
{
  // Note: 0.75f is the same as HashMap.DEFAULT_LOAD_FACTOR
  private static final float DEFAULT_LOAD_FACTOR = 0.75f;

  private final int m_nMaxSize;

  /**
   * Create a new object with the specified max size.
   *
   * @param nMaxSize
   *        The maximum number of elements in this cache. May not be &lt; 0.
   */
  public LRUMap (@Nonnegative final int nMaxSize)
  {
    // We need the special constructor with access ordering!
    super (nMaxSize, DEFAULT_LOAD_FACTOR, true);
    m_nMaxSize = nMaxSize;
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
  protected void onRemoveEldestEntry (@Nonnegative final int nSize,
                                      @Nonnull final Map.Entry <KEYTYPE, VALUETYPE> aEldest)
  {}

  @Override
  protected final boolean removeEldestEntry (@Nonnull final Map.Entry <KEYTYPE, VALUETYPE> aEldest)
  {
    final int nSize = size ();
    if (nSize <= m_nMaxSize)
    {
      // No need to remove anything
      return false;
    }

    // Invoke protected method
    onRemoveEldestEntry (nSize, aEldest);
    return true;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    // Special case because LinkedHashMap implementation is a bit bogus
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final LRUMap <?, ?> rhs = (LRUMap <?, ?>) o;
    return EqualsHelper.equals (m_nMaxSize, rhs.m_nMaxSize) && entrySet ().equals (rhs.entrySet ());
  }

  @Override
  public int hashCode ()
  {
    // Special case because LinkedHashMap implementation is a bit bogus
    final HashCodeGenerator aHCG = new HashCodeGenerator (this).append (m_nMaxSize);
    for (final Map.Entry <KEYTYPE, VALUETYPE> aEntry : entrySet ())
      aHCG.append (aEntry.getKey ()).append (aEntry.getValue ());
    return aHCG.getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("maxSize", m_nMaxSize).append ("map", super.toString ()).toString ();
  }
}
