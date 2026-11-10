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
package com.helger.collection.commons;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.array.ArrayHelper;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.reflection.GenericReflection;
import com.helger.collection.CollectionHelper;

/**
 * A small insertion ordered map based on {@link ICommonsOrderedMap}, that stores keys and values
 * in two parallel arrays and uses a linear scan for all lookups. It is intended for maps with
 * only a few entries (roughly 0 to 8), where it needs less memory and is usually faster than a
 * {@link CommonsLinkedHashMap}, because no hash codes are calculated and no entry objects are
 * created. For larger maps the linear scan makes all lookups O(n), so use
 * {@link CommonsLinkedHashMap} instead.<br>
 * Like {@link CommonsLinkedHashMap}, <code>null</code> keys and <code>null</code> values are
 * supported, re-inserting an existing key does not change the order, and
 * {@link #equals(Object)} and {@link #hashCode()} follow the {@link Map} contract. The views
 * returned by {@link #entrySet()}, {@link #keySet()} and {@link #values()} are live views and
 * support removal via their iterators.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Map key type
 * @param <VALUETYPE>
 *        Map value type
 * @since 12.6.0
 */
@NotThreadSafe
public class CommonsArrayMap <KEYTYPE, VALUETYPE> extends AbstractMap <KEYTYPE, VALUETYPE> implements
                             ICommonsOrderedMap <KEYTYPE, VALUETYPE>,
                             Serializable
{
  /** The default initial capacity */
  public static final int DEFAULT_INITIAL_CAPACITY = 4;

  private Object [] m_aKeys;
  private Object [] m_aValues;
  private int m_nSize;
  private transient int m_nModCount;
  private transient Set <Map.Entry <KEYTYPE, VALUETYPE>> m_aEntrySet;

  /**
   * A map entry that writes through to the map on {@link #setValue(Object)}.
   */
  private final class ArrayMapEntry extends AbstractMap.SimpleEntry <KEYTYPE, VALUETYPE>
  {
    ArrayMapEntry (@Nullable final KEYTYPE aKey, @Nullable final VALUETYPE aValue)
    {
      super (aKey, aValue);
    }

    @Override
    @Nullable
    public VALUETYPE setValue (@Nullable final VALUETYPE aValue)
    {
      final int nIndex = _getIndexOfKey (getKey ());
      if (nIndex < 0)
        throw new IllegalStateException ("The entry was removed from the map");
      m_aValues[nIndex] = aValue;
      return super.setValue (aValue);
    }
  }

  private final class ArrayMapEntryIterator implements Iterator <Map.Entry <KEYTYPE, VALUETYPE>>
  {
    private int m_nNext = 0;
    private int m_nLast = -1;
    private int m_nExpectedModCount = m_nModCount;

    private void _checkForComodification ()
    {
      if (m_nModCount != m_nExpectedModCount)
        throw new ConcurrentModificationException ();
    }

    public boolean hasNext ()
    {
      return m_nNext < m_nSize;
    }

    public Map.@NonNull Entry <KEYTYPE, VALUETYPE> next ()
    {
      _checkForComodification ();
      if (m_nNext >= m_nSize)
        throw new NoSuchElementException ();
      m_nLast = m_nNext++;
      return new ArrayMapEntry (_getKey (m_nLast), _getValue (m_nLast));
    }

    @Override
    public void remove ()
    {
      if (m_nLast < 0)
        throw new IllegalStateException ();
      _checkForComodification ();
      _removeAtIndex (m_nLast);
      m_nNext = m_nLast;
      m_nLast = -1;
      m_nExpectedModCount = m_nModCount;
    }
  }

  private final class ArrayMapEntrySet extends AbstractSet <Map.Entry <KEYTYPE, VALUETYPE>>
  {
    @Override
    @NonNull
    public Iterator <Map.Entry <KEYTYPE, VALUETYPE>> iterator ()
    {
      return new ArrayMapEntryIterator ();
    }

    @Override
    @Nonnegative
    public int size ()
    {
      return m_nSize;
    }

    @Override
    public boolean contains (@Nullable final Object aObj)
    {
      if (aObj instanceof final Map.Entry <?, ?> aEntry)
      {
        final int nIndex = _getIndexOfKey (aEntry.getKey ());
        return nIndex >= 0 && _isEqual (m_aValues[nIndex], aEntry.getValue ());
      }
      return false;
    }

    @Override
    public boolean remove (@Nullable final Object aObj)
    {
      if (aObj instanceof final Map.Entry <?, ?> aEntry)
      {
        final int nIndex = _getIndexOfKey (aEntry.getKey ());
        if (nIndex >= 0 && _isEqual (m_aValues[nIndex], aEntry.getValue ()))
        {
          _removeAtIndex (nIndex);
          return true;
        }
      }
      return false;
    }

    @Override
    public void clear ()
    {
      CommonsArrayMap.this.clear ();
    }
  }

  /**
   * Create a new empty array map with the default initial capacity.
   */
  public CommonsArrayMap ()
  {
    this (DEFAULT_INITIAL_CAPACITY);
  }

  /**
   * Create a new empty array map with the specified initial capacity.
   *
   * @param nInitialCapacity
   *        The number of entries for which memory is reserved. Must be &ge; 0.
   */
  public CommonsArrayMap (@Nonnegative final int nInitialCapacity)
  {
    ValueEnforcer.isGE0 (nInitialCapacity, "InitialCapacity");
    m_aKeys = new Object [nInitialCapacity];
    m_aValues = new Object [nInitialCapacity];
  }

  /**
   * Create a new array map that contains the same entries as the provided map.
   *
   * @param aMap
   *        The map to copy the entries from. May be <code>null</code>.
   */
  public CommonsArrayMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    this (CollectionHelper.getSize (aMap));
    if (aMap != null)
      putAll (aMap);
  }

  /**
   * Create a new array map that contains mapped entries from the provided array.
   *
   * @param aValues
   *        The array to copy the elements from. May be <code>null</code>.
   * @param aKeyMapper
   *        The mapping function to extract keys. May not be <code>null</code>.
   * @param aValueMapper
   *        The mapping function to extract values. May not be <code>null</code>.
   * @param <COLLTYPE>
   *        source element type
   */
  public <COLLTYPE> CommonsArrayMap (@Nullable final COLLTYPE [] aValues,
                                     @NonNull final Function <? super COLLTYPE, ? extends KEYTYPE> aKeyMapper,
                                     @NonNull final Function <? super COLLTYPE, ? extends VALUETYPE> aValueMapper)
  {
    this (ArrayHelper.getSize (aValues));
    putAllMapped (aValues, aKeyMapper, aValueMapper);
  }

  /**
   * Create a new array map that contains mapped entries from the provided collection.
   *
   * @param aValues
   *        The collection to copy the elements from. May be <code>null</code>.
   * @param aKeyMapper
   *        The mapping function to extract keys. May not be <code>null</code>.
   * @param aValueMapper
   *        The mapping function to extract values. May not be <code>null</code>.
   * @param <COLLTYPE>
   *        source element type
   */
  public <COLLTYPE> CommonsArrayMap (@Nullable final Collection <? extends COLLTYPE> aValues,
                                     @NonNull final Function <? super COLLTYPE, ? extends KEYTYPE> aKeyMapper,
                                     @NonNull final Function <? super COLLTYPE, ? extends VALUETYPE> aValueMapper)
  {
    this (CollectionHelper.getSize (aValues));
    putAllMapped (aValues, aKeyMapper, aValueMapper);
  }

  /**
   * Create a new array map that contains mapped entries from the provided map.
   *
   * @param aValues
   *        The map to copy the entries from. May be <code>null</code>.
   * @param aKeyMapper
   *        The mapping function to transform keys. May not be <code>null</code>.
   * @param aValueMapper
   *        The mapping function to transform values. May not be <code>null</code>.
   * @param <SRCKEYTYPE>
   *        source key type
   * @param <SRCVALUETYPE>
   *        source value type
   */
  public <SRCKEYTYPE, SRCVALUETYPE> CommonsArrayMap (@Nullable final Map <? extends SRCKEYTYPE, ? extends SRCVALUETYPE> aValues,
                                                     @NonNull final Function <? super SRCKEYTYPE, ? extends KEYTYPE> aKeyMapper,
                                                     @NonNull final Function <? super SRCVALUETYPE, ? extends VALUETYPE> aValueMapper)
  {
    this (CollectionHelper.getSize (aValues));
    putAllMapped (aValues, aKeyMapper, aValueMapper);
  }

  private static boolean _isEqual (@Nullable final Object aObj1, @Nullable final Object aObj2)
  {
    return aObj1 == aObj2 || (aObj1 != null && aObj1.equals (aObj2));
  }

  @CheckForSigned
  private int _getIndexOfKey (@Nullable final Object aKey)
  {
    final Object [] aKeys = m_aKeys;
    final int nSize = m_nSize;
    if (aKey == null)
    {
      for (int i = 0; i < nSize; ++i)
        if (aKeys[i] == null)
          return i;
    }
    else
    {
      for (int i = 0; i < nSize; ++i)
        if (aKey.equals (aKeys[i]))
          return i;
    }
    return -1;
  }

  @Nullable
  private KEYTYPE _getKey (@Nonnegative final int nIndex)
  {
    return GenericReflection.uncheckedCast (m_aKeys[nIndex]);
  }

  @Nullable
  private VALUETYPE _getValue (@Nonnegative final int nIndex)
  {
    return GenericReflection.uncheckedCast (m_aValues[nIndex]);
  }

  private void _removeAtIndex (@Nonnegative final int nIndex)
  {
    final int nMoved = m_nSize - nIndex - 1;
    if (nMoved > 0)
    {
      System.arraycopy (m_aKeys, nIndex + 1, m_aKeys, nIndex, nMoved);
      System.arraycopy (m_aValues, nIndex + 1, m_aValues, nIndex, nMoved);
    }
    m_nSize--;
    // Allow garbage collection
    m_aKeys[m_nSize] = null;
    m_aValues[m_nSize] = null;
    m_nModCount++;
  }

  @Override
  @Nonnegative
  public int size ()
  {
    return m_nSize;
  }

  @Override
  public boolean isEmpty ()
  {
    return m_nSize == 0;
  }

  @Override
  public boolean containsKey (@Nullable final Object aKey)
  {
    return _getIndexOfKey (aKey) >= 0;
  }

  @Override
  public boolean containsValue (@Nullable final Object aValue)
  {
    for (int i = 0; i < m_nSize; ++i)
      if (_isEqual (aValue, m_aValues[i]))
        return true;
    return false;
  }

  @Override
  @Nullable
  public VALUETYPE get (@Nullable final Object aKey)
  {
    final int nIndex = _getIndexOfKey (aKey);
    return nIndex < 0 ? null : _getValue (nIndex);
  }

  @Override
  @Nullable
  public VALUETYPE put (@Nullable final KEYTYPE aKey, @Nullable final VALUETYPE aValue)
  {
    final int nIndex = _getIndexOfKey (aKey);
    if (nIndex >= 0)
    {
      // Replace value - order stays the same
      final VALUETYPE ret = _getValue (nIndex);
      m_aValues[nIndex] = aValue;
      return ret;
    }

    if (m_nSize == m_aKeys.length)
    {
      final int nNewCapacity = Math.max (DEFAULT_INITIAL_CAPACITY, m_nSize * 2);
      m_aKeys = Arrays.copyOf (m_aKeys, nNewCapacity);
      m_aValues = Arrays.copyOf (m_aValues, nNewCapacity);
    }
    m_aKeys[m_nSize] = aKey;
    m_aValues[m_nSize] = aValue;
    m_nSize++;
    m_nModCount++;
    return null;
  }

  @Override
  @Nullable
  public VALUETYPE remove (@Nullable final Object aKey)
  {
    final int nIndex = _getIndexOfKey (aKey);
    if (nIndex < 0)
      return null;
    final VALUETYPE ret = _getValue (nIndex);
    _removeAtIndex (nIndex);
    return ret;
  }

  @Override
  public void clear ()
  {
    if (m_nSize > 0)
    {
      Arrays.fill (m_aKeys, 0, m_nSize, null);
      Arrays.fill (m_aValues, 0, m_nSize, null);
      m_nSize = 0;
      m_nModCount++;
    }
  }

  @Override
  @NonNull
  public Set <Map.Entry <KEYTYPE, VALUETYPE>> entrySet ()
  {
    Set <Map.Entry <KEYTYPE, VALUETYPE>> ret = m_aEntrySet;
    if (ret == null)
    {
      ret = new ArrayMapEntrySet ();
      m_aEntrySet = ret;
    }
    return ret;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NonNull
  @ReturnsMutableCopy
  public <K, V> CommonsArrayMap <K, V> createInstance ()
  {
    return new CommonsArrayMap <> ();
  }

  /**
   * @return A mutable copy of this map. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public CommonsArrayMap <KEYTYPE, VALUETYPE> getClone ()
  {
    return new CommonsArrayMap <> (this);
  }
}
