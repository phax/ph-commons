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
import java.util.TreeSet;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.ReturnsMutableCopy;

/**
 * A special {@link TreeSet} implementation based on
 * {@link ICommonsNavigableSet}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        Set element type
 */
public class CommonsTreeSet <ELEMENTTYPE> extends TreeSet <ELEMENTTYPE> implements ICommonsNavigableSet <ELEMENTTYPE>
{
  /**
   * Create a new empty tree set with natural ordering.
   */
  public CommonsTreeSet ()
  {}

  /**
   * Create a new empty tree set with the specified comparator.
   *
   * @param aComparator
   *        The comparator to use for element ordering. May be
   *        <code>null</code> to use natural ordering.
   */
  public CommonsTreeSet (@Nullable final Comparator <? super ELEMENTTYPE> aComparator)
  {
    super (aComparator);
  }

  /**
   * Create a new tree set that contains the same elements as the provided
   * collection.
   *
   * @param aCont
   *        The collection to copy the elements from. May be
   *        <code>null</code>.
   */
  public CommonsTreeSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (aCont != null)
      addAll (aCont);
  }

  /**
   * Create a new tree set with the default initial capacity and add all
   * provided elements.
   *
   * @param aIterable
   *        The iterable from which the elements are copied from. May be
   *        <code>null</code>.
   */
  public CommonsTreeSet (@Nullable final Iterable <? extends ELEMENTTYPE> aIterable)
  {
    addAll (aIterable);
  }

  /**
   * Create a new tree set with the default initial capacity and add all mapped
   * items of the provided iterable.
   *
   * @param aValues
   *        The iterable from which the elements are copied from. May be
   *        <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May
   *        not be <code>null</code>.
   * @param <SRCTYPE>
   *        source data type
   */
  public <SRCTYPE> CommonsTreeSet (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                   @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  /**
   * Create a new tree set with exactly the provided value, even if it is
   * <code>null</code>.
   *
   * @param aValue
   *        The value to be added. May be <code>null</code>.
   */
  public CommonsTreeSet (@Nullable final ELEMENTTYPE aValue)
  {
    add (aValue);
  }

  /**
   * Create a new tree set that contains the same elements as the provided
   * array.
   *
   * @param aValues
   *        The array to copy the elements from. May be <code>null</code>.
   */
  @SafeVarargs
  public CommonsTreeSet (@Nullable final ELEMENTTYPE... aValues)
  {
    addAll (aValues);
  }

  /**
   * Create a new tree set that contains mapped elements of the provided array.
   *
   * @param aValues
   *        The array to copy the elements from. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May
   *        not be <code>null</code>.
   * @param <SRCTYPE>
   *        source data type
   */
  public <SRCTYPE> CommonsTreeSet (@Nullable final SRCTYPE [] aValues,
                                   @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NonNull
  @ReturnsMutableCopy
  public <T> CommonsTreeSet <T> createInstance ()
  {
    return new CommonsTreeSet <> ();
  }

  /**
   * @return A mutable copy of this set, preserving the comparator. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public CommonsTreeSet <ELEMENTTYPE> getClone ()
  {
    final CommonsTreeSet <ELEMENTTYPE> ret = new CommonsTreeSet <> (comparator ());
    ret.addAll (this);
    return ret;
  }
}
