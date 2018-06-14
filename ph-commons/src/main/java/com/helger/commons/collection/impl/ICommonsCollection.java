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
package com.helger.commons.collection.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.iterate.IIterableIterator;
import com.helger.commons.collection.iterate.IterableIterator;
import com.helger.commons.lang.IHasSize;
import com.helger.commons.state.EChange;

/**
 * Case collection interface for my extended collection classes.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The data type stored in the collection
 */
public interface ICommonsCollection <ELEMENTTYPE> extends
                                    Collection <ELEMENTTYPE>,
                                    ICommonsIterable <ELEMENTTYPE>,
                                    IHasSize
{
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsList <ELEMENTTYPE> getCopyAsList ()
  {
    return new CommonsArrayList <> (this);
  }

  /**
   * Count the number of elements matching the provided filter.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return The number of elements matching the provided filter or the total
   *         number of elements if no filter is provided. Always &ge; 0.
   */
  @Override
  @Nonnegative
  default int getCount (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return CollectionHelper.getCount (this, aFilter);
  }

  /**
   * Get the element at the specified index or return <code>null</code> upon
   * invalid index.
   *
   * @param nIndex
   *        The index to access. Should be &ge; 0.
   * @return <code>null</code> if the element cannot be accessed.
   * @see #getAtIndex(int, Object)
   */
  @Nullable
  default ELEMENTTYPE getAtIndex (@Nonnegative final int nIndex)
  {
    return getAtIndex (nIndex, null);
  }

  /**
   * Get the element at the specified index or return the provided default value
   * upon invalid index.
   *
   * @param nIndex
   *        The index to access. Should be &ge; 0.
   * @param aDefault
   *        The value to be returned, if the index is out of bounds. May be
   *        <code>null</code>.
   * @return The default parameter if the element cannot be accessed
   * @see #getAtIndex(int)
   */
  @Nullable
  default ELEMENTTYPE getAtIndex (@Nonnegative final int nIndex, @Nullable final ELEMENTTYPE aDefault)
  {
    return getAtIndex (null, nIndex, aDefault);
  }

  /**
   * Get the element at the specified index counting only elements matching the
   * specified filter. If no filter is provided this call is identical to
   * {@link #getAtIndex(int)}.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @param nIndex
   *        The index to be retrieved. Should be &ge; 0.
   * @return <code>null</code> if no matching element could be accessed.
   * @see #getAtIndex(Predicate, int, Object)
   */
  @Nullable
  default ELEMENTTYPE getAtIndex (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                  @Nonnegative final int nIndex)
  {
    return getAtIndex (aFilter, nIndex, null);
  }

  /**
   * Get the element at the specified index counting only elements matching the
   * specified filter or return the provided default value upon invalid index.
   * If no filter is provided this call is identical to
   * {@link #getAtIndex(int, Object)}.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @param nIndex
   *        The index to access. Should be &ge; 0.
   * @param aDefault
   *        The value to be returned, if the index is out of bounds. May be
   *        <code>null</code>.
   * @return The default parameter if the element cannot be accessed
   * @see #getAtIndex(Predicate,int)
   */
  @Nullable
  default ELEMENTTYPE getAtIndex (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                  @Nonnegative final int nIndex,
                                  @Nullable final ELEMENTTYPE aDefault)
  {
    return CollectionHelper.getAtIndex (this, aFilter, nIndex, aDefault);
  }

  /**
   * Get the element at the specified index or return <code>null</code> upon
   * invalid index.
   *
   * @param nIndex
   *        The index to access. Should be &ge; 0.
   * @param aMapper
   *        The mapping function to be executed for the matching element. May
   *        not be <code>null</code>.
   * @return <code>null</code> if the element cannot be accessed.
   * @param <DSTTYPE>
   *        The destination type to be mapped to
   * @see #getAtIndexMapped(int,Function, Object)
   */
  @Nullable
  default <DSTTYPE> DSTTYPE getAtIndexMapped (@Nonnegative final int nIndex,
                                              @Nonnull final Function <? super ELEMENTTYPE, ? extends DSTTYPE> aMapper)
  {
    return getAtIndexMapped (nIndex, aMapper, null);
  }

  /**
   * Get the element at the specified index or return the provided default value
   * upon invalid index.
   *
   * @param nIndex
   *        The index to access. Should be &ge; 0.
   * @param aMapper
   *        The mapping function to be executed for the matching element. May
   *        not be <code>null</code>.
   * @param aDefault
   *        The value to be returned, if the index is out of bounds. May be
   *        <code>null</code>.
   * @return The default parameter if the element cannot be accessed
   * @param <DSTTYPE>
   *        The destination type to be mapped to
   * @see #getAtIndexMapped(int, Function)
   */
  @Nullable
  default <DSTTYPE> DSTTYPE getAtIndexMapped (@Nonnegative final int nIndex,
                                              @Nonnull final Function <? super ELEMENTTYPE, ? extends DSTTYPE> aMapper,
                                              @Nullable final DSTTYPE aDefault)
  {
    return CollectionHelper.getAtIndexMapped (this, nIndex, aMapper, aDefault);
  }

  /**
   * Get the element at the specified index, counting only elements matching the
   * provided filter and map the resulting element using the provided mapper.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @param aMapper
   *        The mapping function to be executed for the matching element. May
   *        not be <code>null</code>.
   * @return <code>null</code> if no such element at the specified index was
   *         found.
   * @param <DSTTYPE>
   *        The destination type to be mapped to
   * @see #getAtIndexMapped(Predicate, int, Function, Object)
   */
  @Nullable
  default <DSTTYPE> DSTTYPE getAtIndexMapped (@Nonnull final Predicate <? super ELEMENTTYPE> aFilter,
                                              @Nonnegative final int nIndex,
                                              @Nonnull final Function <? super ELEMENTTYPE, ? extends DSTTYPE> aMapper)
  {
    return getAtIndexMapped (aFilter, nIndex, aMapper, null);
  }

  /**
   * Get the element at the specified index, counting only elements matching the
   * provided filter and map the resulting element using the provided mapper.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @param nIndex
   *        The index to be accessed. Should be &ge; 0.
   * @param aMapper
   *        The mapping function to be executed for the matching element. May
   *        not be <code>null</code>.
   * @param aDefault
   *        The default value to be returned if no matching element could be
   *        found.
   * @return The provided default value if no such element at the specified
   *         index was found.
   * @param <DSTTYPE>
   *        The destination type to be mapped to
   * @see #getAtIndexMapped(Predicate, int, Function)
   */
  @Nullable
  default <DSTTYPE> DSTTYPE getAtIndexMapped (@Nonnull final Predicate <? super ELEMENTTYPE> aFilter,
                                              @Nonnegative final int nIndex,
                                              @Nonnull final Function <? super ELEMENTTYPE, ? extends DSTTYPE> aMapper,
                                              @Nullable final DSTTYPE aDefault)
  {
    return CollectionHelper.getAtIndexMapped (this, aFilter, nIndex, aMapper, aDefault);
  }

  /**
   * Return a sorted version of this collection. The default implementation
   * returns a copy of this collection as a {@link CommonsArrayList} and sort
   * this list.
   *
   * @param aComparator
   *        The comparator used for sorting. May not be <code>null</code>.
   * @return A non-<code>null</code> list of element. Never <code>null</code>.
   */
  @Nonnull
  default ICommonsList <ELEMENTTYPE> getSorted (@Nonnull final Comparator <? super ELEMENTTYPE> aComparator)
  {
    return new CommonsArrayList <> (this).getSortedInline (aComparator);
  }

  /**
   * add the provided element to the collection using {@link #add(Object)} but
   * returning a more structured return value.
   *
   * @param aElement
   *        The element to be add. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if the element was added successfully,
   *         {@link EChange#UNCHANGED} otherwise (e.g. because if is already
   *         contained).
   * @see #add(Object)
   */
  @Nonnull
  default EChange addObject (@Nullable final ELEMENTTYPE aElement)
  {
    return EChange.valueOf (add (aElement));
  }

  /**
   * Add the passed element to this collection if passed predicate is fulfilled
   *
   * @param aElement
   *        The element to be added. May be <code>null</code>.
   * @param aFilter
   *        The predicate to be executed. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the element was added,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #add(Object)
   */
  @Nonnull
  default EChange addIf (@Nullable final ELEMENTTYPE aElement, @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter != null && !aFilter.test (aElement))
      return EChange.UNCHANGED;
    return addObject (aElement);
  }

  /**
   * Add the passed element to this collection if it is non-<code>null</code>.
   * This is an optimized version for {@link #addIf(Object, Predicate)} with the
   * fixed predicate of <code>!= null</code>.
   *
   * @param aElement
   *        The element to be added if non-<code>null</code>.
   * @return {@link EChange#CHANGED} if the element was added,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #add(Object)
   * @see #addIf(Object, Predicate)
   */
  @Nonnull
  default EChange addIfNotNull (@Nullable final ELEMENTTYPE aElement)
  {
    if (aElement == null)
      return EChange.UNCHANGED;
    return addObject (aElement);
  }

  /**
   * Add an array of elements to this collection.
   *
   * @param aElements
   *        The elements to be added. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if at least one element was added,
   *         {@link EChange#UNCHANGED}. Never <code>null</code>.
   */
  @Nonnull
  default EChange addAll (@SuppressWarnings ("unchecked") @Nullable final ELEMENTTYPE... aElements)
  {
    EChange eChange = EChange.UNCHANGED;
    if (aElements != null)
      for (final ELEMENTTYPE aElement : aElements)
        eChange = eChange.or (add (aElement));
    return eChange;
  }

  /**
   * Add all elements of the passed iterable to this collection.
   *
   * @param aElements
   *        The elements to be added. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if at least one element was added,
   *         {@link EChange#UNCHANGED}. Never <code>null</code>.
   */
  @Nonnull
  default EChange addAll (@Nullable final Iterable <? extends ELEMENTTYPE> aElements)
  {
    EChange eChange = EChange.UNCHANGED;
    if (aElements != null)
      for (final ELEMENTTYPE aElement : aElements)
        eChange = eChange.or (add (aElement));
    return eChange;
  }

  /**
   * Add all elements of the passed enumeration to this collection.
   *
   * @param aEnum
   *        The enumeration to be iterated and the elements to be added. May be
   *        <code>null</code>.
   * @return {@link EChange#CHANGED} if at least one element was added,
   *         {@link EChange#UNCHANGED}. Never <code>null</code>.
   */
  @Nonnull
  default EChange addAll (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    EChange eChange = EChange.UNCHANGED;
    if (aEnum != null)
      while (aEnum.hasMoreElements ())
        eChange = eChange.or (add (aEnum.nextElement ()));
    return eChange;
  }

  /**
   * Add all elements of the passed iterator to this collection.
   *
   * @param aIter
   *        The iterator to be iterated and the elements to be added. May be
   *        <code>null</code>.
   * @return {@link EChange#CHANGED} if at least one element was added,
   *         {@link EChange#UNCHANGED}. Never <code>null</code>.
   */
  @Nonnull
  default EChange addAll (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    EChange eChange = EChange.UNCHANGED;
    if (aIter != null)
      while (aIter.hasNext ())
        eChange = eChange.or (add (aIter.next ()));
    return eChange;
  }

  /**
   * Add all matching elements of an array this collection.
   *
   * @param aElements
   *        The elements to be added. May be <code>null</code>.
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if at least one element was added,
   *         {@link EChange#UNCHANGED}. Never <code>null</code>.
   * @since 8.5.2
   */
  @Nonnull
  default EChange addAll (@Nullable final ELEMENTTYPE [] aElements,
                          @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return addAll (aElements);

    EChange eChange = EChange.UNCHANGED;
    if (aElements != null)
      for (final ELEMENTTYPE aElement : aElements)
        if (aFilter.test (aElement))
          eChange = eChange.or (add (aElement));
    return eChange;
  }

  /**
   * Add all elements of the passed iterable to this collection.
   *
   * @param aElements
   *        The elements to be added. May be <code>null</code>.
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if at least one element was added,
   *         {@link EChange#UNCHANGED}. Never <code>null</code>.
   * @since 8.5.2
   */
  @Nonnull
  default EChange addAll (@Nullable final Iterable <? extends ELEMENTTYPE> aElements,
                          @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return addAll (aElements);

    EChange eChange = EChange.UNCHANGED;
    if (aElements != null)
      for (final ELEMENTTYPE aElement : aElements)
        if (aFilter.test (aElement))
          eChange = eChange.or (add (aElement));
    return eChange;
  }

  /**
   * Add all elements of the passed enumeration to this collection.
   *
   * @param aEnum
   *        The enumeration to be iterated and the elements to be added. May be
   *        <code>null</code>.
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if at least one element was added,
   *         {@link EChange#UNCHANGED}. Never <code>null</code>.
   * @since 8.5.2
   */
  @Nonnull
  default EChange addAll (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum,
                          @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return addAll (aEnum);

    EChange eChange = EChange.UNCHANGED;
    if (aEnum != null)
      while (aEnum.hasMoreElements ())
      {
        final ELEMENTTYPE aElement = aEnum.nextElement ();
        if (aFilter.test (aElement))
          eChange = eChange.or (add (aElement));
      }
    return eChange;
  }

  /**
   * Add all elements of the passed iterator to this collection.
   *
   * @param aIter
   *        The iterator to be iterated and the elements to be added. May be
   *        <code>null</code>.
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if at least one element was added,
   *         {@link EChange#UNCHANGED}. Never <code>null</code>.
   * @since 8.5.2
   */
  @Nonnull
  default EChange addAll (@Nullable final Iterator <? extends ELEMENTTYPE> aIter,
                          @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return addAll (aIter);

    EChange eChange = EChange.UNCHANGED;
    if (aIter != null)
      while (aIter.hasNext ())
      {
        final ELEMENTTYPE aElement = aIter.next ();
        if (aFilter.test (aElement))
          eChange = eChange.or (add (aElement));
      }
    return eChange;
  }

  /**
   * Add all passed elements after performing a mapping using the provided
   * function.
   *
   * @param aElements
   *        The elements to be added after mapping. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May
   *        not be <code>null</code>.
   * @return {@link EChange#CHANGED} if at least one element was added,
   *         {@link EChange#UNCHANGED}. Never <code>null</code>.
   * @param <SRCTYPE>
   *        The source type to be mapped from
   */
  @Nonnull
  default <SRCTYPE> EChange addAllMapped (@Nullable final Iterable <? extends SRCTYPE> aElements,
                                          @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");

    EChange eChange = EChange.UNCHANGED;
    if (aElements != null)
      for (final SRCTYPE aValue : aElements)
        eChange = eChange.or (add (aMapper.apply (aValue)));
    return eChange;
  }

  /**
   * Add all passed elements after performing a mapping using the provided
   * function.
   *
   * @param aElements
   *        The elements to be added after mapping. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May
   *        not be <code>null</code>.
   * @return {@link EChange#CHANGED} if at least one element was added,
   *         {@link EChange#UNCHANGED}. Never <code>null</code>.
   * @param <SRCTYPE>
   *        The source type to be mapped from
   */
  @Nonnull
  default <SRCTYPE> EChange addAllMapped (@Nullable final SRCTYPE [] aElements,
                                          @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");

    EChange eChange = EChange.UNCHANGED;
    if (aElements != null)
      for (final SRCTYPE aValue : aElements)
        eChange = eChange.or (add (aMapper.apply (aValue)));
    return eChange;
  }

  /**
   * Add all passed elements matching the provided filter after performing a
   * mapping using the provided function.
   *
   * @param aElements
   *        The elements to be added after mapping. May be <code>null</code>.
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May
   *        not be <code>null</code>.
   * @return {@link EChange#CHANGED} if at least one element was added,
   *         {@link EChange#UNCHANGED}. Never <code>null</code>.
   * @param <SRCTYPE>
   *        The source type to be mapped from
   */
  @Nonnull
  default <SRCTYPE> EChange addAllMapped (@Nullable final Iterable <? extends SRCTYPE> aElements,
                                          @Nullable final Predicate <? super SRCTYPE> aFilter,
                                          @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");

    if (aFilter == null)
      return addAllMapped (aElements, aMapper);

    EChange eChange = EChange.UNCHANGED;
    if (aElements != null)
      for (final SRCTYPE aValue : aElements)
        if (aFilter.test (aValue))
          eChange = eChange.or (add (aMapper.apply (aValue)));
    return eChange;
  }

  /**
   * Add all passed elements matching the provided filter after performing a
   * mapping using the provided function.
   *
   * @param aElements
   *        The elements to be added after mapping. May be <code>null</code>.
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May
   *        not be <code>null</code>.
   * @return {@link EChange#CHANGED} if at least one element was added,
   *         {@link EChange#UNCHANGED}. Never <code>null</code>.
   * @param <SRCTYPE>
   *        The source type to be mapped from
   */
  @Nonnull
  default <SRCTYPE> EChange addAllMapped (@Nullable final SRCTYPE [] aElements,
                                          @Nullable final Predicate <? super SRCTYPE> aFilter,
                                          @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");

    if (aFilter == null)
      return addAllMapped (aElements, aMapper);

    EChange eChange = EChange.UNCHANGED;
    if (aElements != null)
      for (final SRCTYPE aValue : aElements)
        if (aFilter.test (aValue))
          eChange = eChange.or (add (aMapper.apply (aValue)));
    return eChange;
  }

  /**
   * Add all passed elements matching the provided filter after performing a
   * mapping using the provided function.
   *
   * @param aElements
   *        The elements to be added after mapping. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May
   *        not be <code>null</code>.
   * @param aFilter
   *        The filter to be applied on the mapped element. May be
   *        <code>null</code>.
   * @return {@link EChange#CHANGED} if at least one element was added,
   *         {@link EChange#UNCHANGED}. Never <code>null</code>.
   * @param <SRCTYPE>
   *        The source type to be mapped from
   * @since 8.5.2
   */
  @Nonnull
  default <SRCTYPE> EChange addAllMapped (@Nullable final Iterable <? extends SRCTYPE> aElements,
                                          @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper,
                                          @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");

    if (aFilter == null)
      return addAllMapped (aElements, aMapper);

    EChange eChange = EChange.UNCHANGED;
    if (aElements != null)
      for (final SRCTYPE aValue : aElements)
      {
        final ELEMENTTYPE aMapped = aMapper.apply (aValue);
        if (aFilter.test (aMapped))
          eChange = eChange.or (add (aMapped));
      }
    return eChange;
  }

  /**
   * Add all passed elements matching the provided filter after performing a
   * mapping using the provided function.
   *
   * @param aElements
   *        The elements to be added after mapping. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May
   *        not be <code>null</code>.
   * @param aFilter
   *        The filter to be applied on the mapped element. May be
   *        <code>null</code>.
   * @return {@link EChange#CHANGED} if at least one element was added,
   *         {@link EChange#UNCHANGED}. Never <code>null</code>.
   * @param <SRCTYPE>
   *        The source type to be mapped from
   * @since 8.5.2
   */
  @Nonnull
  default <SRCTYPE> EChange addAllMapped (@Nullable final SRCTYPE [] aElements,
                                          @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper,
                                          @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");

    if (aFilter == null)
      return addAllMapped (aElements, aMapper);

    EChange eChange = EChange.UNCHANGED;
    if (aElements != null)
      for (final SRCTYPE aValue : aElements)
      {
        final ELEMENTTYPE aMapped = aMapper.apply (aValue);
        if (aFilter.test (aMapped))
          eChange = eChange.or (add (aMapped));
      }
    return eChange;
  }

  /**
   * Clear all elements and add only the passed value.
   *
   * @param aValue
   *        The value to be added. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if something was changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #clear()
   * @see #add(Object)
   */
  @Nonnull
  default EChange set (@Nullable final ELEMENTTYPE aValue)
  {
    return removeAll ().or (add (aValue));
  }

  /**
   * Clear all elements and add all provided values. If no value is provided,
   * the collection is empty afterwards.
   *
   * @param aValues
   *        The values to be added. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if something was changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #removeAll()
   * @see #addAll(Iterable)
   */
  @Nonnull
  default EChange setAll (@Nullable final Iterable <? extends ELEMENTTYPE> aValues)
  {
    return removeAll ().or (addAll (aValues));
  }

  /**
   * Clear all elements and add all provided values. If no value is provided,
   * the collection is empty afterwards.
   *
   * @param aValues
   *        The values to be added. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if something was changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #removeAll()
   * @see #addAll(Object...)
   */
  @Nonnull
  default EChange setAll (@SuppressWarnings ("unchecked") @Nullable final ELEMENTTYPE... aValues)
  {
    return removeAll ().or (addAll (aValues));
  }

  /**
   * Clear all elements and add all provided values. If no value is provided,
   * the collection is empty afterwards.
   *
   * @param aValues
   *        The values to be added. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May
   *        not be <code>null</code>.
   * @return {@link EChange#CHANGED} if something was changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #removeAll()
   * @see #addAllMapped(Iterable, Function)
   * @param <SRCTYPE>
   *        The source type to be mapped from
   * @since 9.1.0
   */
  @Nonnull
  default <SRCTYPE> EChange setAllMapped (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                          @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    return removeAll ().or (addAllMapped (aValues, aMapper));
  }

  /**
   * Clear all elements and add all provided values. If no value is provided,
   * the collection is empty afterwards.
   *
   * @param aValues
   *        The values to be added. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May
   *        not be <code>null</code>.
   * @return {@link EChange#CHANGED} if something was changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #removeAll()
   * @see #addAllMapped(Object[], Function)
   * @param <SRCTYPE>
   *        The source type to be mapped from
   * @since 9.1.0
   */
  @Nonnull
  default <SRCTYPE> EChange setAllMapped (@Nullable final SRCTYPE [] aValues,
                                          @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    return removeAll ().or (addAllMapped (aValues, aMapper));
  }

  /**
   * Remove all elements from this collection. This is similar to
   * {@link #clear()} but it returns a different value whether something was
   * cleared or not.
   *
   * @return {@link EChange#CHANGED} if the collection was not empty and
   *         something was removed, {@link EChange#UNCHANGED} otherwise.
   * @see #clear()
   */
  @Nonnull
  default EChange removeAll ()
  {
    if (isEmpty ())
      return EChange.UNCHANGED;
    clear ();
    return EChange.CHANGED;
  }

  /**
   * Remove the provided element from the collection using
   * {@link #remove(Object)} but returning a more structured return value.
   *
   * @param aElement
   *        The element to be removed. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if the element was removed successfully,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #remove(Object)
   */
  @Nonnull
  default EChange removeObject (@Nullable final ELEMENTTYPE aElement)
  {
    return EChange.valueOf (remove (aElement));
  }

  /**
   * @return An unmodifiable version of this collection. Never <code>null</code>
   *         .
   * @see Collections
   */
  @Nonnull
  @CodingStyleguideUnaware
  default Collection <ELEMENTTYPE> getAsUnmodifiable ()
  {
    return Collections.unmodifiableCollection (this);
  }

  /**
   * @return An iterable iterator on this collection. This is similar to
   *         {@link #iterator()} but the returned type is more flexible. Never
   *         <code>null</code>.
   * @see #iterator()
   * @see IterableIterator
   */
  @Nonnull
  default IIterableIterator <ELEMENTTYPE> iterator2 ()
  {
    return new IterableIterator <> (this);
  }
}
