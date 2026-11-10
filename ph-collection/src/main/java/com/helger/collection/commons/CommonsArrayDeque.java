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

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.array.ArrayHelper;
import com.helger.base.clone.ICloneable;
import com.helger.collection.CollectionHelper;

/**
 * A special {@link ArrayDeque} implementation based on {@link ICommonsCollection}. Like
 * {@link ArrayDeque} it does not permit <code>null</code> elements and does not override
 * {@link Object#equals(Object)} and {@link Object#hashCode()}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        Deque element type
 * @since 12.5.4
 */
@NotThreadSafe
public class CommonsArrayDeque <ELEMENTTYPE> extends ArrayDeque <ELEMENTTYPE> implements
                               ICommonsCollection <ELEMENTTYPE>,
                               ICloneable <CommonsArrayDeque <ELEMENTTYPE>>
{
  /**
   * Create a new empty array deque.
   */
  public CommonsArrayDeque ()
  {}

  /**
   * Create a new empty array deque with space for the specified number of elements.
   *
   * @param nNumElements
   *        The lower bound of the number of elements for which space is reserved. Must be &ge; 0.
   */
  public CommonsArrayDeque (@Nonnegative final int nNumElements)
  {
    super (nNumElements);
  }

  /**
   * Create a new array deque that contains the same elements as the provided collection.
   *
   * @param aCont
   *        The collection to copy the elements from. May be <code>null</code>. It must not contain
   *        <code>null</code> elements.
   */
  public CommonsArrayDeque (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    super (CollectionHelper.getSize (aCont));
    if (aCont != null)
      addAll (aCont);
  }

  /**
   * Create a new array deque with all elements of the provided iterable.
   *
   * @param aIterable
   *        The iterable from which the elements are copied from. May be <code>null</code>. It must
   *        not contain <code>null</code> elements.
   */
  public CommonsArrayDeque (@Nullable final Iterable <? extends ELEMENTTYPE> aIterable)
  {
    addAll (aIterable);
  }

  /**
   * Create a new array deque with all mapped elements of the provided iterable.
   *
   * @param aValues
   *        The iterable from which the elements are copied from. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May not be
   *        <code>null</code> and must not return <code>null</code>.
   * @param <SRCTYPE>
   *        source data type
   */
  public <SRCTYPE> CommonsArrayDeque (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                      @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  /**
   * Create a new array deque with exactly the provided value.
   *
   * @param aValue
   *        The value to be added. May not be <code>null</code>.
   */
  public CommonsArrayDeque (@NonNull final ELEMENTTYPE aValue)
  {
    super (1);
    add (aValue);
  }

  /**
   * Create a new array deque that contains the same elements as the provided array.
   *
   * @param aValues
   *        The array to copy the elements from. May be <code>null</code>. It must not contain
   *        <code>null</code> elements.
   */
  @SafeVarargs
  public CommonsArrayDeque (@Nullable final ELEMENTTYPE... aValues)
  {
    super (ArrayHelper.getSize (aValues));
    addAll (aValues);
  }

  /**
   * Create a new array deque that contains mapped elements of the provided array.
   *
   * @param aValues
   *        The array to copy the elements from. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May not be
   *        <code>null</code> and must not return <code>null</code>.
   * @param <SRCTYPE>
   *        source data type
   */
  public <SRCTYPE> CommonsArrayDeque (@Nullable final SRCTYPE [] aValues,
                                      @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    super (ArrayHelper.getSize (aValues));
    addAllMapped (aValues, aMapper);
  }

  /**
   * @return A mutable copy of this deque with the same element order. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public CommonsArrayDeque <ELEMENTTYPE> getClone ()
  {
    return new CommonsArrayDeque <> (this);
  }
}
