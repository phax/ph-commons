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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;

/**
 * A special {@link CommonsCopyOnWriteArrayList} implementation based on
 * {@link ICommonsList}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        List element type
 */
public class CommonsCopyOnWriteArrayList <ELEMENTTYPE> extends CopyOnWriteArrayList <ELEMENTTYPE> implements ICommonsList <ELEMENTTYPE>
{
  /**
   * Create a new empty array list.
   */
  public CommonsCopyOnWriteArrayList ()
  {}

  /**
   * Create a new array list that contains the same elements as the provided
   * collection.
   *
   * @param aValues
   *        The collection to copy the elements from. May be <code>null</code>.
   * @see #addAll(Collection)
   */
  public CommonsCopyOnWriteArrayList (@Nullable final Collection <? extends ELEMENTTYPE> aValues)
  {
    if (aValues != null)
      addAll (aValues);
  }

  /**
   * Create a new array list with all provided elements.
   *
   * @param aValues
   *        The iterable from which the elements are copied from. May be
   *        <code>null</code>.
   * @see #addAll(Iterable)
   */
  public CommonsCopyOnWriteArrayList (@Nullable final Iterable <? extends ELEMENTTYPE> aValues)
  {
    // Uses a special overload of "addAll"!
    addAll (aValues);
  }

  /**
   * Create a new array list with all mapped items of the provided iterable.
   *
   * @param aValues
   *        The iterable from which the elements are copied from. May be
   *        <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May
   *        not be <code>null</code>.
   * @see #addAllMapped(Iterable, Function)
   * @param <SRCTYPE>
   *        source data type
   */
  public <SRCTYPE> CommonsCopyOnWriteArrayList (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                                @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  /**
   * Create a new array list with an initial capacity of 1 and exactly the
   * provided value, even if it is <code>null</code>.
   *
   * @param aValue
   *        The value to be added. May be <code>null</code>.
   */
  public CommonsCopyOnWriteArrayList (@Nullable final ELEMENTTYPE aValue)
  {
    add (aValue);
  }

  /**
   * Create a new array list that contains the same elements as the provided
   * array.
   *
   * @param aValues
   *        The array to copy the elements from. May be <code>null</code>.
   * @see #addAll(Object...)
   */
  @SafeVarargs
  public CommonsCopyOnWriteArrayList (@Nullable final ELEMENTTYPE... aValues)
  {
    addAll (aValues);
  }

  /**
   * Create a new array list that contains mapped elements of the provided
   * array.
   *
   * @param aValues
   *        The array to copy the elements from. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May
   *        not be <code>null</code>.
   * @see #addAllMapped(Object[], Function)
   * @param <SRCTYPE>
   *        source data type
   */
  public <SRCTYPE> CommonsCopyOnWriteArrayList (@Nullable final SRCTYPE [] aValues,
                                                @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  public <T> CommonsCopyOnWriteArrayList <T> createInstance ()
  {
    return new CommonsCopyOnWriteArrayList <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsCopyOnWriteArrayList <ELEMENTTYPE> getClone ()
  {
    return new CommonsCopyOnWriteArrayList <> (this);
  }

  /**
   * Create a new array list that contains a subset of the provided
   * iterable.<br>
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
   * @return The created array list. Never <code>null</code>.
   * @see #addAll(Iterable, Predicate)
   * @since 9.1.3
   * @param <ELEMENTTYPE>
   *        data type of the list
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsCopyOnWriteArrayList <ELEMENTTYPE> createFiltered (@Nullable final Iterable <? extends ELEMENTTYPE> aValues,
                                                                                        @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsCopyOnWriteArrayList <ELEMENTTYPE> ret = new CommonsCopyOnWriteArrayList <> ();
    ret.addAll (aValues, aFilter);
    return ret;
  }

  /**
   * Create a new array list that contains a subset of the provided iterable.
   * This method filters the elements before they are mapped.<br>
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
   * @return The created array list. Never <code>null</code>.
   * @see #addAllMapped(Iterable, Predicate, Function)
   * @since 9.1.3
   * @param <SRCTYPE>
   *        source data type
   * @param <ELEMENTTYPE>
   *        final data type of the list
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsCopyOnWriteArrayList <ELEMENTTYPE> createFiltered (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                                                                                 @Nullable final Predicate <? super SRCTYPE> aFilter,
                                                                                                 @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    final CommonsCopyOnWriteArrayList <ELEMENTTYPE> ret = new CommonsCopyOnWriteArrayList <> ();
    ret.addAllMapped (aValues, aFilter, aMapper);
    return ret;
  }

  /**
   * Create a new array list that contains a subset of the provided iterable.
   * This method maps the elements before they are filtered.<br>
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
   * @return The created array list. Never <code>null</code>.
   * @see #addAllMapped(Iterable, Function, Predicate)
   * @since 9.1.3
   * @param <SRCTYPE>
   *        source data type
   * @param <ELEMENTTYPE>
   *        final data type of the list
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsCopyOnWriteArrayList <ELEMENTTYPE> createFiltered (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                                                                                 @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper,
                                                                                                 @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsCopyOnWriteArrayList <ELEMENTTYPE> ret = new CommonsCopyOnWriteArrayList <> ();
    ret.addAllMapped (aValues, aMapper, aFilter);
    return ret;
  }

  /**
   * Create a new array list that contains a subset of the provided array.<br>
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
   * @return The created array list. Never <code>null</code>.
   * @see #addAll(Object[], Predicate)
   * @since 9.1.3
   * @param <ELEMENTTYPE>
   *        data type of the list
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsCopyOnWriteArrayList <ELEMENTTYPE> createFiltered (@Nullable final ELEMENTTYPE [] aValues,
                                                                                        @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsCopyOnWriteArrayList <ELEMENTTYPE> ret = new CommonsCopyOnWriteArrayList <> ();
    ret.addAll (aValues, aFilter);
    return ret;
  }

  /**
   * Create a new array list that contains a subset of the provided array. This
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
   * @return The created array list. Never <code>null</code>.
   * @see #addAllMapped(Object[], Predicate, Function)
   * @since 9.1.3
   * @param <SRCTYPE>
   *        source data type
   * @param <ELEMENTTYPE>
   *        final data type of the list
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsCopyOnWriteArrayList <ELEMENTTYPE> createFiltered (@Nullable final SRCTYPE [] aValues,
                                                                                                 @Nullable final Predicate <? super SRCTYPE> aFilter,
                                                                                                 @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    final CommonsCopyOnWriteArrayList <ELEMENTTYPE> ret = new CommonsCopyOnWriteArrayList <> ();
    ret.addAllMapped (aValues, aFilter, aMapper);
    return ret;
  }

  /**
   * Create a new array list that contains a subset of the provided array. This
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
   * @return The created array list. Never <code>null</code>.
   * @see #addAllMapped(Iterable, Function, Predicate)
   * @since 9.1.3
   * @param <SRCTYPE>
   *        source data type
   * @param <ELEMENTTYPE>
   *        final data type of the list
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsCopyOnWriteArrayList <ELEMENTTYPE> createFiltered (@Nullable final SRCTYPE [] aValues,
                                                                                                 @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper,
                                                                                                 @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsCopyOnWriteArrayList <ELEMENTTYPE> ret = new CommonsCopyOnWriteArrayList <> ();
    ret.addAllMapped (aValues, aMapper, aFilter);
    return ret;
  }
}
