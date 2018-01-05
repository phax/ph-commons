/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.collection.helper;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.iterate.IIterableIterator;

@Immutable
public final class QueueHelper
{
  private QueueHelper ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nonnegative final int nInitialCapacity)
  {
    return new PriorityQueue <> (nInitialCapacity);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue ()
  {
    return new PriorityQueue <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> PriorityQueue <DSTTYPE> newQueueMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                           @Nonnull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (CollectionHelper.isEmpty (aCollection))
      return newQueue (0);
    final PriorityQueue <DSTTYPE> ret = newQueue (aCollection.size ());
    for (final SRCTYPE aValue : aCollection)
      ret.add (aMapper.apply (aValue));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> PriorityQueue <DSTTYPE> newQueueMapped (@Nullable final SRCTYPE [] aArray,
                                                                           @Nonnull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (ArrayHelper.isEmpty (aArray))
      return newQueue (0);
    final PriorityQueue <DSTTYPE> ret = newQueue (aArray.length);
    for (final SRCTYPE aValue : aArray)
      ret.add (aMapper.apply (aValue));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nonnull final ELEMENTTYPE aValue)
  {
    final PriorityQueue <ELEMENTTYPE> ret = newQueue (1);
    ret.add (aValue);
    return ret;
  }

  @Nonnull
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
   * Compared to {@link Collections#list(Enumeration)} this method is more
   * flexible in Generics parameter.
   *
   * @param <ELEMENTTYPE>
   *        Type of the elements
   * @param aEnum
   *        The enumeration to be converted
   * @return The non-<code>null</code> created {@link PriorityQueue}.
   * @see Collections#list(Enumeration)
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    final PriorityQueue <ELEMENTTYPE> ret = newQueue ();
    if (aEnum != null)
      while (aEnum.hasMoreElements ())
        ret.add (aEnum.nextElement ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    final PriorityQueue <ELEMENTTYPE> ret = newQueue ();
    if (aIter != null)
      while (aIter.hasNext ())
        ret.add (aIter.next ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nullable final Iterable <? extends ELEMENTTYPE> aIter)
  {
    final PriorityQueue <ELEMENTTYPE> ret = newQueue ();
    if (aIter != null)
      for (final ELEMENTTYPE aObj : aIter)
        ret.add (aObj);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (CollectionHelper.isEmpty (aCont))
      return newQueue (0);
    return new PriorityQueue <> (aCont);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                                    @Nonnull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (CollectionHelper.isEmpty (aCollection))
      return newQueue (0);
    final PriorityQueue <ELEMENTTYPE> ret = newQueue (aCollection.size ());
    CollectionHelper.findAll (aCollection, aFilter, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> PriorityQueue <ELEMENTTYPE> newQueue (@Nullable final IIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return newQueue (0);
    return newQueue (aIter.iterator ());
  }
}
