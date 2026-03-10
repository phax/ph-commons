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
import java.util.LinkedHashSet;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.array.ArrayHelper;
import com.helger.collection.CollectionHelper;

/**
 * A special {@link LinkedHashSet} implementation based on {@link ICommonsOrderedSet}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        Set element type
 */
public class CommonsLinkedHashSet <ELEMENTTYPE> extends LinkedHashSet <ELEMENTTYPE> implements
                                  ICommonsOrderedSet <ELEMENTTYPE>
{
  /**
   * Create a new empty linked hash set. The default initial capacity is used.
   */
  public CommonsLinkedHashSet ()
  {}

  /**
   * Create a new empty linked hash set with the specified initial capacity.
   *
   * @param nInitialCapacity
   *        The initial capacity for which memory is reserved. Must be &gt; 0.
   */
  public CommonsLinkedHashSet (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  /**
   * Create a new empty linked hash set with the specified initial capacity and
   * load factor.
   *
   * @param nInitialCapacity
   *        The initial capacity for which memory is reserved. Must be &gt; 0.
   * @param fLoadFactor
   *        The load factor for the hash set.
   */
  public CommonsLinkedHashSet (final int nInitialCapacity, final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  /**
   * Create a new linked hash set that contains the same elements as the
   * provided collection.
   *
   * @param aCont
   *        The collection to copy the elements from. May be
   *        <code>null</code>.
   */
  public CommonsLinkedHashSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    super (CollectionHelper.getSize (aCont));
    if (aCont != null)
      addAll (aCont);
  }

  /**
   * Create a new linked hash set with the default initial capacity and add all
   * provided elements.
   *
   * @param aIterable
   *        The iterable from which the elements are copied from. May be
   *        <code>null</code>.
   */
  public CommonsLinkedHashSet (@Nullable final Iterable <? extends ELEMENTTYPE> aIterable)
  {
    addAll (aIterable);
  }

  /**
   * Create a new linked hash set that contains the mapped elements of the
   * provided collection.
   *
   * @param aValues
   *        The collection to copy the elements from. May be
   *        <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May
   *        not be <code>null</code>.
   * @param <SRCTYPE>
   *        source data type
   */
  public <SRCTYPE> CommonsLinkedHashSet (@Nullable final Collection <? extends SRCTYPE> aValues,
                                         @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    super (CollectionHelper.getSize (aValues));
    addAllMapped (aValues, aMapper);
  }

  /**
   * Create a new linked hash set with the default initial capacity and add all
   * mapped items of the provided iterable.
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
  public <SRCTYPE> CommonsLinkedHashSet (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                         @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  /**
   * Create a new linked hash set with an initial capacity of 1 and exactly the
   * provided value, even if it is <code>null</code>.
   *
   * @param aValue
   *        The value to be added. May be <code>null</code>.
   */
  public CommonsLinkedHashSet (@Nullable final ELEMENTTYPE aValue)
  {
    super (1);
    add (aValue);
  }

  /**
   * Create a new linked hash set that contains the same elements as the
   * provided array.
   *
   * @param aValues
   *        The array to copy the elements from. May be <code>null</code>.
   */
  @SafeVarargs
  public CommonsLinkedHashSet (@Nullable final ELEMENTTYPE... aValues)
  {
    super (ArrayHelper.getSize (aValues));
    addAll (aValues);
  }

  /**
   * Create a new linked hash set that contains mapped elements of the provided
   * array.
   *
   * @param aValues
   *        The array to copy the elements from. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May
   *        not be <code>null</code>.
   * @param <SRCTYPE>
   *        source data type
   */
  public <SRCTYPE> CommonsLinkedHashSet (@Nullable final SRCTYPE [] aValues,
                                         @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    super (ArrayHelper.getSize (aValues));
    addAllMapped (aValues, aMapper);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NonNull
  @ReturnsMutableCopy
  public <T> CommonsLinkedHashSet <T> createInstance ()
  {
    return new CommonsLinkedHashSet <> ();
  }

  /**
   * @return A mutable copy of this set. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public CommonsLinkedHashSet <ELEMENTTYPE> getClone ()
  {
    return new CommonsLinkedHashSet <> (this);
  }

  /**
   * Create a new linked hash set that contains a subset of the provided iterable.<br>
   * Note: this method is a static factory method because the compiler sometimes cannot deduce
   * between {@link Predicate} and {@link Function} and the mapping case occurs more often.
   *
   * @param aValues
   *        The iterable from which the elements are copied from. May be <code>null</code>.
   * @param aFilter
   *        The filter to be applied to check if the element should be added or not.
   * @return The created linked hash set. Never <code>null</code>.
   * @see #addAll(Iterable, Predicate)
   * @param <ELEMENTTYPE>
   *        data type to create the list of
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createFiltered (@Nullable final Iterable <? extends ELEMENTTYPE> aValues,
                                                                                 @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsLinkedHashSet <ELEMENTTYPE> ret = new CommonsLinkedHashSet <> ();
    ret.addAll (aValues, aFilter);
    return ret;
  }

  /**
   * Create a linked new hash set that contains a subset of the provided iterable. This method
   * filters the elements before they are mapped.<br>
   * Note: this method is a static factory method because the compiler sometimes cannot deduce
   * between {@link Predicate} and {@link Function} and the mapping case occurs more often.
   *
   * @param aValues
   *        The iterable from which the elements are copied from. May be <code>null</code>.
   * @param aFilter
   *        The filter to be applied to check if the element should be added or not.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May not be
   *        <code>null</code>.
   * @return The created linked hash set. Never <code>null</code>.
   * @see #addAllMapped(Iterable, Predicate, Function)
   * @param <SRCTYPE>
   *        source data type
   * @param <ELEMENTTYPE>
   *        final data type of the list
   */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createFiltered (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                                                                          @Nullable final Predicate <? super SRCTYPE> aFilter,
                                                                                          @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    final CommonsLinkedHashSet <ELEMENTTYPE> ret = new CommonsLinkedHashSet <> (CollectionHelper.getSize (aValues));
    ret.addAllMapped (aValues, aFilter, aMapper);
    return ret;
  }

  /**
   * Create a linked new hash set that contains a subset of the provided iterable. This method maps
   * the elements before they are filtered.<br>
   * Note: this method is a static factory method because the compiler sometimes cannot deduce
   * between {@link Predicate} and {@link Function} and the mapping case occurs more often.
   *
   * @param aValues
   *        The iterable from which the elements are copied from. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May not be
   *        <code>null</code>.
   * @param aFilter
   *        The filter to be applied on the mapped element to check if the element should be added
   *        or not.
   * @return The created linked hash set. Never <code>null</code>.
   * @see #addAllMapped(Iterable, Function, Predicate)
   * @param <SRCTYPE>
   *        source data type
   * @param <ELEMENTTYPE>
   *        final data type of the list
   */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createFiltered (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                                                                          @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper,
                                                                                          @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsLinkedHashSet <ELEMENTTYPE> ret = new CommonsLinkedHashSet <> (CollectionHelper.getSize (aValues));
    ret.addAllMapped (aValues, aMapper, aFilter);
    return ret;
  }

  /**
   * Create a new linked hash set that contains a subset of the provided array.<br>
   * Note: this method is a static factory method because the compiler sometimes cannot deduce
   * between {@link Predicate} and {@link Function} and the mapping case occurs more often.
   *
   * @param aValues
   *        The array from which the elements are copied from. May be <code>null</code>.
   * @param aFilter
   *        The filter to be applied to check if the element should be added or not.
   * @return The created linked hash set. Never <code>null</code>.
   * @see #addAll(Object[], Predicate)
   * @param <ELEMENTTYPE>
   *        data type of the list
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createFiltered (@Nullable final ELEMENTTYPE [] aValues,
                                                                                 @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsLinkedHashSet <ELEMENTTYPE> ret = new CommonsLinkedHashSet <> (ArrayHelper.getSize (aValues));
    ret.addAll (aValues, aFilter);
    return ret;
  }

  /**
   * Create a new linked hash set that contains a subset of the provided array. This method filters
   * the elements before they are mapped.<br>
   * Note: this method is a static factory method because the compiler sometimes cannot deduce
   * between {@link Predicate} and {@link Function} and the mapping case occurs more often.
   *
   * @param aValues
   *        The array from which the elements are copied from. May be <code>null</code>.
   * @param aFilter
   *        The filter to be applied to check if the element should be added or not.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May not be
   *        <code>null</code>.
   * @return The created linked hash set. Never <code>null</code>.
   * @see #addAllMapped(Object[], Predicate, Function)
   * @param <SRCTYPE>
   *        source data type
   * @param <ELEMENTTYPE>
   *        final data type of the list
   */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createFiltered (@Nullable final SRCTYPE [] aValues,
                                                                                          @Nullable final Predicate <? super SRCTYPE> aFilter,
                                                                                          @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    final CommonsLinkedHashSet <ELEMENTTYPE> ret = new CommonsLinkedHashSet <> (ArrayHelper.getSize (aValues));
    ret.addAllMapped (aValues, aFilter, aMapper);
    return ret;
  }

  /**
   * Create a new linked hash set that contains a subset of the provided array. This method maps the
   * elements before they are filtered.<br>
   * Note: this method is a static factory method because the compiler sometimes cannot deduce
   * between {@link Predicate} and {@link Function} and the mapping case occurs more often.
   *
   * @param aValues
   *        The array from which the elements are copied from. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May not be
   *        <code>null</code>.
   * @param aFilter
   *        The filter to be applied on the mapped element to check if the element should be added
   *        or not.
   * @return The created linked hash set. Never <code>null</code>.
   * @see #addAllMapped(Iterable, Function, Predicate)
   * @param <SRCTYPE>
   *        source data type
   * @param <ELEMENTTYPE>
   *        final data type of the list
   */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createFiltered (@Nullable final SRCTYPE [] aValues,
                                                                                          @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper,
                                                                                          @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsLinkedHashSet <ELEMENTTYPE> ret = new CommonsLinkedHashSet <> (ArrayHelper.getSize (aValues));
    ret.addAllMapped (aValues, aMapper, aFilter);
    return ret;
  }
}
