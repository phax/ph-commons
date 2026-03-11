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
package com.helger.collection.queue;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.array.ArrayHelper;
import com.helger.collection.CollectionFind;
import com.helger.collection.CollectionHelper;
import com.helger.collection.commons.ICommonsIterableIterator;

/**
 * Helper class for creating and populating {@link java.util.PriorityQueue}
 * instances from various sources.
 *
 * @author Philip Helger
 */
@Immutable
public final class QueueHelper
{
  private QueueHelper ()
  {}

  /**
   * Create a new empty priority queue with the specified initial capacity.
   *
   * @param <ELEMENTTYPE>
   *        Queue element type
   * @param nInitialCapacity
   *        The initial capacity.
   * @return A new empty priority queue. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nonnegative final int nInitialCapacity)
  {
    return new PriorityQueue <> (nInitialCapacity);
  }

  /**
   * Create a new empty priority queue.
   *
   * @param <ELEMENTTYPE>
   *        Queue element type
   * @return A new empty priority queue. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue ()
  {
    return new PriorityQueue <> ();
  }

  /**
   * Create a new priority queue with mapped elements from the provided collection.
   *
   * @param <SRCTYPE>
   *        Source element type
   * @param <DSTTYPE>
   *        Destination element type
   * @param aCollection
   *        The source collection. May be <code>null</code>.
   * @param aMapper
   *        The mapping function. May not be <code>null</code>.
   * @return A new priority queue. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> PriorityQueue <DSTTYPE> newQueueMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                           @NonNull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (CollectionHelper.isEmpty (aCollection))
      return newQueue (0);
    final PriorityQueue <DSTTYPE> ret = newQueue (aCollection.size ());
    for (final SRCTYPE aValue : aCollection)
      ret.add (aMapper.apply (aValue));
    return ret;
  }

  /**
   * Create a new priority queue with mapped elements from the provided array.
   *
   * @param <SRCTYPE>
   *        Source element type
   * @param <DSTTYPE>
   *        Destination element type
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param aMapper
   *        The mapping function. May not be <code>null</code>.
   * @return A new priority queue. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> PriorityQueue <DSTTYPE> newQueueMapped (@Nullable final SRCTYPE [] aArray,
                                                                           @NonNull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (ArrayHelper.isEmpty (aArray))
      return newQueue (0);
    final PriorityQueue <DSTTYPE> ret = newQueue (aArray.length);
    for (final SRCTYPE aValue : aArray)
      ret.add (aMapper.apply (aValue));
    return ret;
  }

  /**
   * Create a new priority queue with exactly one element.
   *
   * @param <ELEMENTTYPE>
   *        Queue element type
   * @param aValue
   *        The value to add. May not be <code>null</code>.
   * @return A new priority queue. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@NonNull final ELEMENTTYPE aValue)
  {
    final PriorityQueue <ELEMENTTYPE> ret = newQueue (1);
    ret.add (aValue);
    return ret;
  }

  /**
   * Create a new priority queue with the provided values.
   *
   * @param <ELEMENTTYPE>
   *        Queue element type
   * @param aValues
   *        The values to add. May be <code>null</code>.
   * @return A new priority queue. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nullable final ELEMENTTYPE... aValues)
  {
    // Don't user Arrays.asQueue since aIter returns an unmodifiable list!
    if (ArrayHelper.isEmpty (aValues))
      return newQueue (0);

    final PriorityQueue <ELEMENTTYPE> ret = newQueue (aValues.length);
    Collections.addAll (ret, aValues);
    return ret;
  }

  /**
   * Compared to {@link Collections#list(Enumeration)} this method is more flexible in Generics
   * parameter.
   *
   * @param <ELEMENTTYPE>
   *        Type of the elements
   * @param aEnum
   *        The enumeration to be converted
   * @return The non-<code>null</code> created {@link PriorityQueue}.
   * @see Collections#list(Enumeration)
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    final PriorityQueue <ELEMENTTYPE> ret = newQueue ();
    if (aEnum != null)
      while (aEnum.hasMoreElements ())
        ret.add (aEnum.nextElement ());
    return ret;
  }

  /**
   * Create a new priority queue from the provided iterator.
   *
   * @param <ELEMENTTYPE>
   *        Queue element type
   * @param aIter
   *        The iterator to copy elements from. May be <code>null</code>.
   * @return A new priority queue. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    final PriorityQueue <ELEMENTTYPE> ret = newQueue ();
    if (aIter != null)
      while (aIter.hasNext ())
        ret.add (aIter.next ());
    return ret;
  }

  /**
   * Create a new priority queue from the provided iterable.
   *
   * @param <ELEMENTTYPE>
   *        Queue element type
   * @param aIter
   *        The iterable to copy elements from. May be <code>null</code>.
   * @return A new priority queue. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nullable final Iterable <? extends ELEMENTTYPE> aIter)
  {
    final PriorityQueue <ELEMENTTYPE> ret = newQueue ();
    if (aIter != null)
      for (final ELEMENTTYPE aObj : aIter)
        ret.add (aObj);
    return ret;
  }

  /**
   * Create a new priority queue from the provided collection.
   *
   * @param <ELEMENTTYPE>
   *        Queue element type
   * @param aCont
   *        The collection to copy elements from. May be <code>null</code>.
   * @return A new priority queue. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (CollectionHelper.isEmpty (aCont))
      return newQueue (0);
    return new PriorityQueue <> (aCont);
  }

  /**
   * Create a new priority queue with filtered elements from the provided collection.
   *
   * @param <ELEMENTTYPE>
   *        Queue element type
   * @param aCollection
   *        The source collection. May be <code>null</code>.
   * @param aFilter
   *        The filter to apply. May not be <code>null</code>.
   * @return A new priority queue. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                                    @NonNull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (CollectionHelper.isEmpty (aCollection))
      return newQueue (0);
    final PriorityQueue <ELEMENTTYPE> ret = newQueue (aCollection.size ());
    CollectionFind.findAll (aCollection, aFilter, ret::add);
    return ret;
  }

  /**
   * Create a new priority queue from the provided iterable iterator.
   *
   * @param <ELEMENTTYPE>
   *        Queue element type
   * @param aIter
   *        The iterable iterator to copy elements from. May be <code>null</code>.
   * @return A new priority queue. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nullable final ICommonsIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return newQueue (0);
    return newQueue (aIter.iterator ());
  }
}
