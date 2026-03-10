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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.array.ArrayHelper;
import com.helger.collection.CollectionHelper;

/**
 * A special {@link HashMap} implementation based on {@link ICommonsMap}.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Map key type
 * @param <VALUETYPE>
 *        Map value type
 */
public class CommonsHashMap <KEYTYPE, VALUETYPE> extends HashMap <KEYTYPE, VALUETYPE> implements
                            ICommonsMap <KEYTYPE, VALUETYPE>
{
  /**
   * Create a new empty hash map. The default initial capacity is used.
   */
  public CommonsHashMap ()
  {}

  /**
   * Create a new empty hash map with the specified initial capacity.
   *
   * @param nInitialCapacity
   *        The initial capacity for which memory is reserved. Must be &gt; 0.
   */
  public CommonsHashMap (@Nonnegative final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  /**
   * Create a new empty hash map with the specified initial capacity and load
   * factor.
   *
   * @param nInitialCapacity
   *        The initial capacity for which memory is reserved. Must be &gt; 0.
   * @param fLoadFactor
   *        The load factor for the hash map. Must be &gt; 0.
   */
  public CommonsHashMap (@Nonnegative final int nInitialCapacity, @Nonnegative final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  /**
   * Create a new hash map that contains the same entries as the provided map.
   *
   * @param aMap
   *        The map to copy the entries from. May be <code>null</code>.
   */
  public CommonsHashMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    super (CollectionHelper.getSize (aMap));
    if (aMap != null)
      putAll (aMap);
  }

  /**
   * Create a new hash map that contains mapped entries from the provided array.
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
  public <COLLTYPE> CommonsHashMap (@Nullable final COLLTYPE [] aValues,
                                    @NonNull final Function <? super COLLTYPE, ? extends KEYTYPE> aKeyMapper,
                                    @NonNull final Function <? super COLLTYPE, ? extends VALUETYPE> aValueMapper)
  {
    super (ArrayHelper.getSize (aValues));
    putAllMapped (aValues, aKeyMapper, aValueMapper);
  }

  /**
   * Create a new hash map that contains mapped entries from the provided
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
  public <COLLTYPE> CommonsHashMap (@Nullable final Collection <? extends COLLTYPE> aValues,
                                    @NonNull final Function <? super COLLTYPE, ? extends KEYTYPE> aKeyMapper,
                                    @NonNull final Function <? super COLLTYPE, ? extends VALUETYPE> aValueMapper)
  {
    super (CollectionHelper.getSize (aValues));
    putAllMapped (aValues, aKeyMapper, aValueMapper);
  }

  /**
   * Create a new hash map that contains mapped entries from the provided map.
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
  public <SRCKEYTYPE, SRCVALUETYPE> CommonsHashMap (@Nullable final Map <? extends SRCKEYTYPE, ? extends SRCVALUETYPE> aValues,
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
  public <K, V> CommonsHashMap <K, V> createInstance ()
  {
    return new CommonsHashMap <> ();
  }

  /**
   * @return A mutable copy of this map. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public CommonsHashMap <KEYTYPE, VALUETYPE> getClone ()
  {
    return new CommonsHashMap <> (this);
  }
}
