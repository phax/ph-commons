/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.commons.collection.impl;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.CollectionHelper;

/**
 * A special {@link HashSet} implementation based on {@link ICommonsSet}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        Set element type
 */
public class CommonsHashSet <ELEMENTTYPE> extends HashSet <ELEMENTTYPE> implements ICommonsSet <ELEMENTTYPE>
{
  public CommonsHashSet ()
  {}

  public CommonsHashSet (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsHashSet (final int nInitialCapacity, @Nonnegative final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  public CommonsHashSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    super (CollectionHelper.getSize (aCont));
    if (aCont != null)
      addAll (aCont);
  }

  public CommonsHashSet (@Nullable final Iterable <? extends ELEMENTTYPE> aIterable)
  {
    addAll (aIterable);
  }

  public CommonsHashSet (@Nullable final Enumeration <? extends ELEMENTTYPE> aIterable)
  {
    addAll (aIterable);
  }

  public <SRCTYPE> CommonsHashSet (@Nullable final Collection <? extends SRCTYPE> aValues,
                                   @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    super (CollectionHelper.getSize (aValues));
    addAllMapped (aValues, aMapper);
  }

  public <SRCTYPE> CommonsHashSet (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                   @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  public CommonsHashSet (@Nullable final ELEMENTTYPE aValue)
  {
    super (1);
    add (aValue);
  }

  @SafeVarargs
  public CommonsHashSet (@Nullable final ELEMENTTYPE... aValues)
  {
    super (ArrayHelper.getSize (aValues));
    addAll (aValues);
  }

  public <SRCTYPE> CommonsHashSet (@Nullable final SRCTYPE [] aValues,
                                   @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    super (ArrayHelper.getSize (aValues));
    addAllMapped (aValues, aMapper);
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  public <T> CommonsHashSet <T> createInstance ()
  {
    return new CommonsHashSet <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsHashSet <ELEMENTTYPE> getClone ()
  {
    return new CommonsHashSet <> (this);
  }

  /**
   * Create a new hash set that contains a subset of the provided iterable.<br>
   * Note: this method is a static factory method because the compiler sometimes
   * cannot deduce between {@link Predicate} and {@link Function} and the
   * mapping case occurs more often.
   *
   * @param aValues
   *        The iterable from which the elements are copied from. May be
   *        <code>null</code>.
   * @param aFilter
   *        The filter to be applied to check if the element should be added or
   *        not.
   * @return The created hash set. Never <code>null</code>.
   * @see #addAll(Iterable, Predicate)
   * @since 10.0.0
   * @param <ELEMENTTYPE>
   *        data type to create the list of
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> createFiltered (@Nullable final Iterable <? extends ELEMENTTYPE> aValues,
                                                                           @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsHashSet <ELEMENTTYPE> ret = new CommonsHashSet <> ();
    ret.addAll (aValues, aFilter);
    return ret;
  }

  /**
   * Create a new hash set that contains a subset of the provided iterable. This
   * method filters the elements before they are mapped.<br>
   * Note: this method is a static factory method because the compiler sometimes
   * cannot deduce between {@link Predicate} and {@link Function} and the
   * mapping case occurs more often.
   *
   * @param aValues
   *        The iterable from which the elements are copied from. May be
   *        <code>null</code>.
   * @param aFilter
   *        The filter to be applied to check if the element should be added or
   *        not.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May
   *        not be <code>null</code>.
   * @return The created hash set. Never <code>null</code>.
   * @see #addAllMapped(Iterable, Predicate, Function)
   * @since 10.0.0
   * @param <SRCTYPE>
   *        source data type
   * @param <ELEMENTTYPE>
   *        final data type of the list
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> createFiltered (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                                                                    @Nullable final Predicate <? super SRCTYPE> aFilter,
                                                                                    @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    final CommonsHashSet <ELEMENTTYPE> ret = new CommonsHashSet <> (CollectionHelper.getSize (aValues));
    ret.addAllMapped (aValues, aFilter, aMapper);
    return ret;
  }

  /**
   * Create a new hash set that contains a subset of the provided iterable. This
   * method maps the elements before they are filtered.<br>
   * Note: this method is a static factory method because the compiler sometimes
   * cannot deduce between {@link Predicate} and {@link Function} and the
   * mapping case occurs more often.
   *
   * @param aValues
   *        The iterable from which the elements are copied from. May be
   *        <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May
   *        not be <code>null</code>.
   * @param aFilter
   *        The filter to be applied on the mapped element to check if the
   *        element should be added or not.
   * @return The created hash set. Never <code>null</code>.
   * @see #addAllMapped(Iterable, Function, Predicate)
   * @since 10.0.0
   * @param <SRCTYPE>
   *        source data type
   * @param <ELEMENTTYPE>
   *        final data type of the list
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> createFiltered (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                                                                    @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper,
                                                                                    @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsHashSet <ELEMENTTYPE> ret = new CommonsHashSet <> (CollectionHelper.getSize (aValues));
    ret.addAllMapped (aValues, aMapper, aFilter);
    return ret;
  }

  /**
   * Create a new hash set that contains a subset of the provided array.<br>
   * Note: this method is a static factory method because the compiler sometimes
   * cannot deduce between {@link Predicate} and {@link Function} and the
   * mapping case occurs more often.
   *
   * @param aValues
   *        The array from which the elements are copied from. May be
   *        <code>null</code>.
   * @param aFilter
   *        The filter to be applied to check if the element should be added or
   *        not.
   * @return The created hash set. Never <code>null</code>.
   * @since 10.0.0
   * @see #addAll(Object[], Predicate)
   * @param <ELEMENTTYPE>
   *        data type of the list
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> createFiltered (@Nullable final ELEMENTTYPE [] aValues,
                                                                           @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsHashSet <ELEMENTTYPE> ret = new CommonsHashSet <> (ArrayHelper.getSize (aValues));
    ret.addAll (aValues, aFilter);
    return ret;
  }

  /**
   * Create a new hash set that contains a subset of the provided array. This
   * method filters the elements before they are mapped.<br>
   * Note: this method is a static factory method because the compiler sometimes
   * cannot deduce between {@link Predicate} and {@link Function} and the
   * mapping case occurs more often.
   *
   * @param aValues
   *        The array from which the elements are copied from. May be
   *        <code>null</code>.
   * @param aFilter
   *        The filter to be applied to check if the element should be added or
   *        not.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May
   *        not be <code>null</code>.
   * @return The created hash set. Never <code>null</code>.
   * @see #addAllMapped(Object[], Predicate, Function)
   * @since 10.0.0
   * @param <SRCTYPE>
   *        source data type
   * @param <ELEMENTTYPE>
   *        final data type of the list
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> createFiltered (@Nullable final SRCTYPE [] aValues,
                                                                                    @Nullable final Predicate <? super SRCTYPE> aFilter,
                                                                                    @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    final CommonsHashSet <ELEMENTTYPE> ret = new CommonsHashSet <> (ArrayHelper.getSize (aValues));
    ret.addAllMapped (aValues, aFilter, aMapper);
    return ret;
  }

  /**
   * Create a new hash set that contains a subset of the provided array. This
   * method maps the elements before they are filtered.<br>
   * Note: this method is a static factory method because the compiler sometimes
   * cannot deduce between {@link Predicate} and {@link Function} and the
   * mapping case occurs more often.
   *
   * @param aValues
   *        The array from which the elements are copied from. May be
   *        <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May
   *        not be <code>null</code>.
   * @param aFilter
   *        The filter to be applied on the mapped element to check if the
   *        element should be added or not.
   * @return The created hash set. Never <code>null</code>.
   * @see #addAllMapped(Iterable, Function, Predicate)
   * @since 10.0.0
   * @param <SRCTYPE>
   *        source data type
   * @param <ELEMENTTYPE>
   *        final data type of the list
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsHashSet <ELEMENTTYPE> createFiltered (@Nullable final SRCTYPE [] aValues,
                                                                                    @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper,
                                                                                    @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsHashSet <ELEMENTTYPE> ret = new CommonsHashSet <> (ArrayHelper.getSize (aValues));
    ret.addAllMapped (aValues, aMapper, aFilter);
    return ret;
  }
}
