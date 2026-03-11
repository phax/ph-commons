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
import java.util.Vector;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.array.ArrayHelper;
import com.helger.collection.CollectionHelper;

/**
 * A special {@link Vector} implementation based on {@link ICommonsList}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        List element type
 */
public class CommonsVector <ELEMENTTYPE> extends Vector <ELEMENTTYPE> implements ICommonsList <ELEMENTTYPE>
{
  /**
   * Create a new empty vector.
   */
  public CommonsVector ()
  {}

  /**
   * Create a new vector with the specified initial capacity.
   *
   * @param nInitialCapacity
   *        The initial capacity.
   */
  public CommonsVector (@Nonnegative final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  /**
   * Create a new vector with the elements of the provided collection.
   *
   * @param aCont
   *        The collection to copy elements from. May be <code>null</code>.
   */
  public CommonsVector (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    super (CollectionHelper.getSize (aCont));
    if (aCont != null)
      addAll (aCont);
  }

  /**
   * Create a new vector with the elements of the provided iterable.
   *
   * @param aIterable
   *        The iterable to copy elements from. May be <code>null</code>.
   */
  public CommonsVector (@Nullable final Iterable <? extends ELEMENTTYPE> aIterable)
  {
    addAll (aIterable);
  }

  /**
   * Create a new vector with mapped elements of the provided collection.
   *
   * @param aValues
   *        The collection to copy elements from. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to apply. May not be <code>null</code>.
   * @param <SRCTYPE>
   *        Source element type
   */
  public <SRCTYPE> CommonsVector (@Nullable final Collection <? extends SRCTYPE> aValues,
                                  @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    super (CollectionHelper.getSize (aValues));
    addAllMapped (aValues, aMapper);
  }

  /**
   * Create a new vector with mapped elements of the provided iterable.
   *
   * @param aValues
   *        The iterable to copy elements from. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to apply. May not be <code>null</code>.
   * @param <SRCTYPE>
   *        Source element type
   */
  public <SRCTYPE> CommonsVector (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                  @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  /**
   * Create a new vector with exactly one element.
   *
   * @param aValue
   *        The value to add. May be <code>null</code>.
   */
  public CommonsVector (@Nullable final ELEMENTTYPE aValue)
  {
    super (1);
    add (aValue);
  }

  /**
   * Create a new vector with the provided values.
   *
   * @param aValues
   *        The values to add. May be <code>null</code>.
   */
  @SafeVarargs
  public CommonsVector (@Nullable final ELEMENTTYPE... aValues)
  {
    super (ArrayHelper.getSize (aValues));
    addAll (aValues);
  }

  /**
   * Create a new vector with mapped elements of the provided array.
   *
   * @param aValues
   *        The array to copy elements from. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to apply. May not be <code>null</code>.
   * @param <SRCTYPE>
   *        Source element type
   */
  public <SRCTYPE> CommonsVector (@Nullable final SRCTYPE [] aValues,
                                  @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    super (ArrayHelper.getSize (aValues));
    addAllMapped (aValues, aMapper);
  }

  @Override
  @NonNull
  @ReturnsMutableCopy
  public <T> CommonsVector <T> createInstance ()
  {
    return new CommonsVector <> ();
  }

  /**
   * @return A mutable copy of this vector. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public CommonsVector <ELEMENTTYPE> getClone ()
  {
    return new CommonsVector <> (this);
  }

  /**
   * Create a new vector that contains a subset of the provided iterable.
   *
   * @param aValues
   *        The iterable from which the elements are copied from. May be <code>null</code>.
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return The created vector. Never <code>null</code>.
   * @param <ELEMENTTYPE>
   *        Data type of the vector
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> createFiltered (@Nullable final Iterable <? extends ELEMENTTYPE> aValues,
                                                                          @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsVector <ELEMENTTYPE> ret = new CommonsVector <> (CollectionHelper.getSize (aValues));
    ret.addAll (aValues, aFilter);
    return ret;
  }

  /**
   * Create a new vector that contains a filtered and mapped subset of the provided iterable.
   *
   * @param aValues
   *        The iterable from which the elements are copied from. May be <code>null</code>.
   * @param aFilter
   *        The filter to be applied before mapping. May be <code>null</code>.
   * @param aMapper
   *        The mapping function. May not be <code>null</code>.
   * @return The created vector. Never <code>null</code>.
   * @param <SRCTYPE>
   *        Source data type
   * @param <ELEMENTTYPE>
   *        Final data type of the vector
   */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsVector <ELEMENTTYPE> createFiltered (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                                                                   @Nullable final Predicate <? super SRCTYPE> aFilter,
                                                                                   @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    final CommonsVector <ELEMENTTYPE> ret = new CommonsVector <> (CollectionHelper.getSize (aValues));
    ret.addAllMapped (aValues, aFilter, aMapper);
    return ret;
  }

  /**
   * Create a new vector that contains a subset of the provided array.
   *
   * @param aValues
   *        The array from which the elements are copied from. May be <code>null</code>.
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return The created vector. Never <code>null</code>.
   * @param <ELEMENTTYPE>
   *        Data type of the vector
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> createFiltered (@Nullable final ELEMENTTYPE [] aValues,
                                                                          @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsVector <ELEMENTTYPE> ret = new CommonsVector <> (ArrayHelper.getSize (aValues));
    ret.addAll (aValues, aFilter);
    return ret;
  }

  /**
   * Create a new vector that contains a filtered and mapped subset of the provided array.
   *
   * @param aValues
   *        The array from which the elements are copied from. May be <code>null</code>.
   * @param aFilter
   *        The filter to be applied before mapping. May be <code>null</code>.
   * @param aMapper
   *        The mapping function. May not be <code>null</code>.
   * @return The created vector. Never <code>null</code>.
   * @param <SRCTYPE>
   *        Source data type
   * @param <ELEMENTTYPE>
   *        Final data type of the vector
   */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsVector <ELEMENTTYPE> createFiltered (@Nullable final SRCTYPE [] aValues,
                                                                                   @Nullable final Predicate <? super SRCTYPE> aFilter,
                                                                                   @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    final CommonsVector <ELEMENTTYPE> ret = new CommonsVector <> (ArrayHelper.getSize (aValues));
    ret.addAllMapped (aValues, aFilter, aMapper);
    return ret;
  }
}
