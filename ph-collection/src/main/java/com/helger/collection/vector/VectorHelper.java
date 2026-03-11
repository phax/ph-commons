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
package com.helger.collection.vector;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.array.ArrayHelper;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.collection.CollectionHelper;
import com.helger.collection.commons.CommonsVector;
import com.helger.collection.commons.ICommonsIterableIterator;

/**
 * Helper class for creating and populating {@link CommonsVector} instances from
 * various sources.
 *
 * @author Philip Helger
 */
@Immutable
public final class VectorHelper
{
  private VectorHelper ()
  {}

  /**
   * Create a new empty vector with the given initial capacity.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param nInitialCapacity
   *        The initial capacity. Must be &ge; 0.
   * @return A new empty {@link CommonsVector}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector (@Nonnegative final int nInitialCapacity)
  {
    return new CommonsVector <> (nInitialCapacity);
  }

  /** @see #newVector(int) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector ()
  {
    return new CommonsVector <> ();
  }

  /**
   * Create a new vector by mapping elements from the given collection.
   *
   * @param <SRCTYPE>
   *        The source element type.
   * @param <DSTTYPE>
   *        The destination element type.
   * @param aCollection
   *        The source collection. May be <code>null</code>.
   * @param aMapper
   *        The mapping function. May not be <code>null</code>.
   * @return A new {@link CommonsVector}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> CommonsVector <DSTTYPE> newVectorMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                            @NonNull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (CollectionHelper.isEmpty (aCollection))
      return newVector (0);
    final CommonsVector <DSTTYPE> ret = newVector (aCollection.size ());
    ret.addAllMapped (aCollection, aMapper);
    return ret;
  }

  /** @see #newVectorMapped(Collection, Function) */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> CommonsVector <DSTTYPE> newVectorMapped (@Nullable final SRCTYPE [] aArray,
                                                                            @NonNull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (ArrayHelper.isEmpty (aArray))
      return newVector (0);
    final CommonsVector <DSTTYPE> ret = newVector (aArray.length);
    ret.addAllMapped (aArray, aMapper);
    return ret;
  }

  /**
   * Create a new vector from the given collection, applying a filter.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param aCollection
   *        The source collection. May be <code>null</code>.
   * @param aFilter
   *        The filter to apply. May not be <code>null</code>.
   * @return A new {@link CommonsVector}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                                     @NonNull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (CollectionHelper.isEmpty (aCollection))
      return newVector (0);
    final CommonsVector <ELEMENTTYPE> ret = newVector (aCollection.size ());
    ret.addAll (aCollection, aFilter);
    return ret;
  }

  /**
   * Create a new vector prefilled with the given value.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param aValue
   *        The value to fill the vector with. May be <code>null</code>.
   * @param nElements
   *        The number of elements. Must be &ge; 0.
   * @return A new {@link CommonsVector}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVectorPrefilled (@Nullable final ELEMENTTYPE aValue,
                                                                              @Nonnegative final int nElements)
  {
    ValueEnforcer.isGE0 (nElements, "Elements");

    final CommonsVector <ELEMENTTYPE> ret = newVector (nElements);
    for (int i = 0; i < nElements; ++i)
      ret.add (aValue);
    return ret;
  }

  /**
   * Create a new vector with a single element.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param aValue
   *        The value to add. May be <code>null</code>.
   * @return A new {@link CommonsVector} with one element. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector (@Nullable final ELEMENTTYPE aValue)
  {
    return new CommonsVector <> (aValue);
  }

  /**
   * Create a new vector from the given varargs array.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param aValues
   *        The values to add. May be <code>null</code>.
   * @return A new {@link CommonsVector}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector (@Nullable final ELEMENTTYPE... aValues)
  {
    // Don't user Arrays.asVector since aIter returns an unmodifiable list!
    if (ArrayHelper.isEmpty (aValues))
      return newVector (0);

    return new CommonsVector <> (aValues);
  }

  /**
   * Compared to {@link Collections#list(Enumeration)} this method is more
   * flexible in Generics parameter.
   *
   * @param <ELEMENTTYPE>
   *        Type of the elements
   * @param aEnum
   *        The enumeration to be converted
   * @return The non-<code>null</code> created {@link CommonsVector}.
   * @see Collections#list(Enumeration)
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    final CommonsVector <ELEMENTTYPE> ret = newVector ();
    ret.addAll (aEnum);
    return ret;
  }

  /** @see #newVector(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    final CommonsVector <ELEMENTTYPE> ret = newVector ();
    ret.addAll (aIter);
    return ret;
  }

  /** @see #newVector(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector (@Nullable final Iterable <? extends ELEMENTTYPE> aIter)
  {
    final CommonsVector <ELEMENTTYPE> ret = newVector ();
    ret.addAll (aIter);
    return ret;
  }

  /** @see #newVector(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (CollectionHelper.isEmpty (aCont))
      return newVector (0);

    return new CommonsVector <> (aCont);
  }

  /** @see #newVector(Object[]) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector (@Nullable final ICommonsIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return newVector (0);
    return newVector (aIter.iterator ());
  }
}
