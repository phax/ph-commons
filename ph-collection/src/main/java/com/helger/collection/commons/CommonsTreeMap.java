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
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.ReturnsMutableCopy;

/**
 * A special {@link TreeMap} implementation based on
 * {@link ICommonsNavigableMap}.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Map key type
 * @param <VALUETYPE>
 *        Map value type
 */
public class CommonsTreeMap <KEYTYPE, VALUETYPE> extends TreeMap <KEYTYPE, VALUETYPE> implements
                            ICommonsNavigableMap <KEYTYPE, VALUETYPE>
{
  /**
   * Create a new empty tree map with natural ordering.
   */
  public CommonsTreeMap ()
  {}

  /**
   * Create a new empty tree map with the specified comparator.
   *
   * @param aComparator
   *        The comparator to use for key ordering. May be <code>null</code>
   *        to use natural ordering.
   */
  public CommonsTreeMap (@Nullable final Comparator <? super KEYTYPE> aComparator)
  {
    super (aComparator);
  }

  /**
   * Create a new tree map that contains the same entries as the provided map.
   *
   * @param aMap
   *        The map to copy the entries from. May be <code>null</code>.
   */
  public CommonsTreeMap (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    if (aMap != null)
      putAll (aMap);
  }

  /**
   * Create a new tree map that contains mapped entries from the provided array.
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
  public <COLLTYPE> CommonsTreeMap (@Nullable final COLLTYPE [] aValues,
                                    @NonNull final Function <? super COLLTYPE, ? extends KEYTYPE> aKeyMapper,
                                    @NonNull final Function <? super COLLTYPE, ? extends VALUETYPE> aValueMapper)
  {
    putAllMapped (aValues, aKeyMapper, aValueMapper);
  }

  /**
   * Create a new tree map that contains mapped entries from the provided
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
  public <COLLTYPE> CommonsTreeMap (@Nullable final Collection <? extends COLLTYPE> aValues,
                                    @NonNull final Function <? super COLLTYPE, ? extends KEYTYPE> aKeyMapper,
                                    @NonNull final Function <? super COLLTYPE, ? extends VALUETYPE> aValueMapper)
  {
    putAllMapped (aValues, aKeyMapper, aValueMapper);
  }

  /**
   * Create a new tree map that contains mapped entries from the provided map.
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
  public <SRCKEYTYPE, SRCVALUETYPE> CommonsTreeMap (@Nullable final Map <? extends SRCKEYTYPE, ? extends SRCVALUETYPE> aValues,
                                                    @NonNull final Function <? super SRCKEYTYPE, ? extends KEYTYPE> aKeyMapper,
                                                    @NonNull final Function <? super SRCVALUETYPE, ? extends VALUETYPE> aValueMapper)
  {
    putAllMapped (aValues, aKeyMapper, aValueMapper);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NonNull
  @ReturnsMutableCopy
  public <K, V> CommonsTreeMap <K, V> createInstance ()
  {
    return new CommonsTreeMap <> ();
  }

  /**
   * @return A mutable copy of this map, preserving the comparator. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public CommonsTreeMap <KEYTYPE, VALUETYPE> getClone ()
  {
    final CommonsTreeMap <KEYTYPE, VALUETYPE> ret = new CommonsTreeMap <> (comparator ());
    ret.putAll (this);
    return ret;
  }
}
