/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.array.ArrayHelper;
import com.helger.collection.CollectionHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

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
  public CommonsLinkedHashSet ()
  {}

  public CommonsLinkedHashSet (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsLinkedHashSet (final int nInitialCapacity, final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  public CommonsLinkedHashSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    super (CollectionHelper.getSize (aCont));
    if (aCont != null)
      addAll (aCont);
  }

  public CommonsLinkedHashSet (@Nullable final Iterable <? extends ELEMENTTYPE> aIterable)
  {
    addAll (aIterable);
  }

  public <SRCTYPE> CommonsLinkedHashSet (@Nullable final Collection <? extends SRCTYPE> aValues,
                                         @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    super (CollectionHelper.getSize (aValues));
    addAllMapped (aValues, aMapper);
  }

  public <SRCTYPE> CommonsLinkedHashSet (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                         @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  public CommonsLinkedHashSet (@Nullable final ELEMENTTYPE aValue)
  {
    super (1);
    add (aValue);
  }

  @SafeVarargs
  public CommonsLinkedHashSet (@Nullable final ELEMENTTYPE... aValues)
  {
    super (ArrayHelper.getSize (aValues));
    addAll (aValues);
  }

  public <SRCTYPE> CommonsLinkedHashSet (@Nullable final SRCTYPE [] aValues,
                                         @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    super (ArrayHelper.getSize (aValues));
    addAllMapped (aValues, aMapper);
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  public <T> CommonsLinkedHashSet <T> createInstance ()
  {
    return new CommonsLinkedHashSet <> ();
  }

  @Nonnull
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
  @Nonnull
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
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createFiltered (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                                                                          @Nullable final Predicate <? super SRCTYPE> aFilter,
                                                                                          @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
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
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createFiltered (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                                                                          @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper,
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
  @Nonnull
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
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createFiltered (@Nullable final SRCTYPE [] aValues,
                                                                                          @Nullable final Predicate <? super SRCTYPE> aFilter,
                                                                                          @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
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
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsLinkedHashSet <ELEMENTTYPE> createFiltered (@Nullable final SRCTYPE [] aValues,
                                                                                          @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper,
                                                                                          @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsLinkedHashSet <ELEMENTTYPE> ret = new CommonsLinkedHashSet <> (ArrayHelper.getSize (aValues));
    ret.addAllMapped (aValues, aMapper, aFilter);
    return ret;
  }
}
