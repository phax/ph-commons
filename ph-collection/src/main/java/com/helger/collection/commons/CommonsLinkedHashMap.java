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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.array.ArrayHelper;
import com.helger.collection.CollectionHelper;

/**
 * A special {@link LinkedHashMap} implementation based on
 * {@link ICommonsOrderedMap}.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Map key type
 * @param <VALUETYPE>
 *        Map value type
 */
public class CommonsLinkedHashMap <KEYTYPE, VALUETYPE> extends LinkedHashMap <KEYTYPE, VALUETYPE> implements
                                  ICommonsOrderedMap <KEYTYPE, VALUETYPE>
{
  /**
   * Create a new empty linked hash map. The default initial capacity is used.
   */
  public CommonsLinkedHashMap ()
  {}

  /**
   * Create a new empty linked hash map with the specified initial capacity.
   *
   * @param nInitialCapacity
   *        The initial capacity for which memory is reserved. Must be &gt; 0.
   */
  public CommonsLinkedHashMap (@Nonnegative final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  /**
   * Create a new empty linked hash map with the specified initial capacity and
   * load factor.
   *
   * @param nInitialCapacity
   *        The initial capacity for which memory is reserved. Must be &gt; 0.
   * @param fLoadFactor
   *        The load factor for the hash map.
   */
  public CommonsLinkedHashMap (@Nonnegative final int nInitialCapacity, final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  /**
   * Create a new linked hash map that contains the same entries as the provided
   * map.
   *
   * @param aMap
   *        The map to copy the entries from. May be <code>null</code>.
   */
  public CommonsLinkedHashMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    super (CollectionHelper.getSize (aMap));
    if (aMap != null)
      putAll (aMap);
  }

  /**
   * Create a new empty linked hash map with the specified initial capacity,
   * load factor, and ordering mode.
   *
   * @param nInitialCapacity
   *        The initial capacity for which memory is reserved. Must be &gt; 0.
   * @param fLoadFactor
   *        The load factor for the hash map.
   * @param bAccessOrder
   *        <code>true</code> for access-order, <code>false</code> for
   *        insertion-order.
   */
  public CommonsLinkedHashMap (@Nonnegative final int nInitialCapacity,
                               final float fLoadFactor,
                               final boolean bAccessOrder)
  {
    super (nInitialCapacity, fLoadFactor, bAccessOrder);
  }

  /**
   * Create a new linked hash map that contains mapped entries from the provided
   * array.
   *
   * @param aValues
   *        The array to copy the elements from. May be <code>null</code>.
   * @param aKeyMapper
   *        The mapping function to extract keys. May not be <code>null</code>.
   * @param aValueMapper
   *        The mapping function to extract values. May not be
   *        <code>null</code>.
   * @param <COLLTYPE>
   *        source element type
   */
  public <COLLTYPE> CommonsLinkedHashMap (@Nullable final COLLTYPE [] aValues,
                                          @NonNull final Function <? super COLLTYPE, ? extends KEYTYPE> aKeyMapper,
                                          @NonNull final Function <? super COLLTYPE, ? extends VALUETYPE> aValueMapper)
  {
    super (ArrayHelper.getSize (aValues));
    putAllMapped (aValues, aKeyMapper, aValueMapper);
  }

  /**
   * Create a new linked hash map that contains mapped entries from the provided
   * collection.
   *
   * @param aValues
   *        The collection to copy the elements from. May be
   *        <code>null</code>.
   * @param aKeyMapper
   *        The mapping function to extract keys. May not be <code>null</code>.
   * @param aValueMapper
   *        The mapping function to extract values. May not be
   *        <code>null</code>.
   * @param <COLLTYPE>
   *        source element type
   */
  public <COLLTYPE> CommonsLinkedHashMap (@Nullable final Collection <? extends COLLTYPE> aValues,
                                          @NonNull final Function <? super COLLTYPE, ? extends KEYTYPE> aKeyMapper,
                                          @NonNull final Function <? super COLLTYPE, ? extends VALUETYPE> aValueMapper)
  {
    super (CollectionHelper.getSize (aValues));
    putAllMapped (aValues, aKeyMapper, aValueMapper);
  }

  /**
   * Create a new linked hash map that contains mapped entries from the provided
   * map.
   *
   * @param aValues
   *        The map to copy the entries from. May be <code>null</code>.
   * @param aKeyMapper
   *        The mapping function to transform keys. May not be
   *        <code>null</code>.
   * @param aValueMapper
   *        The mapping function to transform values. May not be
   *        <code>null</code>.
   * @param <SRCKEYTYPE>
   *        source key type
   * @param <SRCVALUETYPE>
   *        source value type
   */
  public <SRCKEYTYPE, SRCVALUETYPE> CommonsLinkedHashMap (@Nullable final Map <? extends SRCKEYTYPE, ? extends SRCVALUETYPE> aValues,
                                                          @NonNull final Function <? super SRCKEYTYPE, ? extends KEYTYPE> aKeyMapper,
                                                          @NonNull final Function <? super SRCVALUETYPE, ? extends VALUETYPE> aValueMapper)
  {
    super (CollectionHelper.getSize (aValues));
    putAllMapped (aValues, aKeyMapper, aValueMapper);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NonNull
  @ReturnsMutableCopy
  public <K, V> CommonsLinkedHashMap <K, V> createInstance ()
  {
    return new CommonsLinkedHashMap <> ();
  }

  /**
   * @return A mutable copy of this map. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public CommonsLinkedHashMap <KEYTYPE, VALUETYPE> getClone ()
  {
    return new CommonsLinkedHashMap <> (this);
  }
}
