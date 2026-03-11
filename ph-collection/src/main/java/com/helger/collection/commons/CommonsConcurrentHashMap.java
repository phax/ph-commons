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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.collection.CollectionHelper;

/**
 * A special {@link ConcurrentHashMap} implementation based on
 * {@link ICommonsMap}.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Map key type
 * @param <VALUETYPE>
 *        Map value type
 */
public class CommonsConcurrentHashMap <KEYTYPE, VALUETYPE> extends ConcurrentHashMap <KEYTYPE, VALUETYPE> implements
                                      ICommonsMap <KEYTYPE, VALUETYPE>
{
  /**
   * Create a new empty concurrent hash map.
   */
  public CommonsConcurrentHashMap ()
  {}

  /**
   * Create a new concurrent hash map with the specified initial capacity.
   *
   * @param nInitialCapacity
   *        The initial capacity of the map.
   */
  public CommonsConcurrentHashMap (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  /**
   * Create a new concurrent hash map with the specified initial capacity and load factor.
   *
   * @param nInitialCapacity
   *        The initial capacity of the map.
   * @param fLoadFactor
   *        The load factor of the map.
   */
  public CommonsConcurrentHashMap (final int nInitialCapacity, @Nonnegative final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  /**
   * Create a new concurrent hash map that contains the same entries as the provided map.
   *
   * @param aMap
   *        The map to copy the entries from. May be <code>null</code>.
   */
  public CommonsConcurrentHashMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    super (CollectionHelper.getSize (aMap));
    if (aMap != null)
      putAll (aMap);
  }

  /**
   * Create a new concurrent hash map with mapped entries from the provided collection.
   *
   * @param aValues
   *        The collection to extract entries from. May be <code>null</code>.
   * @param aKeyMapper
   *        The function to derive the map key. May not be <code>null</code>.
   * @param aValueMapper
   *        The function to derive the map value. May not be <code>null</code>.
   * @param <COLLTYPE>
   *        Collection element type
   */
  public <COLLTYPE> CommonsConcurrentHashMap (@Nullable final Collection <? extends COLLTYPE> aValues,
                                              @NonNull final Function <? super COLLTYPE, ? extends KEYTYPE> aKeyMapper,
                                              @NonNull final Function <? super COLLTYPE, ? extends VALUETYPE> aValueMapper)
  {
    super (CollectionHelper.getSize (aValues));
    putAllMapped (aValues, aKeyMapper, aValueMapper);
  }

  /**
   * Create a new concurrent hash map with mapped entries from the provided map.
   *
   * @param aValues
   *        The map to extract and transform entries from. May be <code>null</code>.
   * @param aKeyMapper
   *        The function to transform the source key. May not be <code>null</code>.
   * @param aValueMapper
   *        The function to transform the source value. May not be <code>null</code>.
   * @param <SRCKEYTYPE>
   *        Source key type
   * @param <SRCVALUETYPE>
   *        Source value type
   */
  public <SRCKEYTYPE, SRCVALUETYPE> CommonsConcurrentHashMap (@Nullable final Map <? extends SRCKEYTYPE, ? extends SRCVALUETYPE> aValues,
                                                              @NonNull final Function <? super SRCKEYTYPE, ? extends KEYTYPE> aKeyMapper,
                                                              @NonNull final Function <? super SRCVALUETYPE, ? extends VALUETYPE> aValueMapper)
  {
    super (CollectionHelper.getSize (aValues));
    putAllMapped (aValues, aKeyMapper, aValueMapper);
  }

  @Override
  @NonNull
  @ReturnsMutableCopy
  public <K, V> CommonsConcurrentHashMap <K, V> createInstance ()
  {
    return new CommonsConcurrentHashMap <> ();
  }

  /**
   * @return A mutable copy of this map. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public CommonsConcurrentHashMap <KEYTYPE, VALUETYPE> getClone ()
  {
    return new CommonsConcurrentHashMap <> (this);
  }
}
