/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.collection.ext;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
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
import com.helger.commons.function.IBreakableConsumer;
import com.helger.commons.state.EChange;

/**
 * Case collection interface for my extended collection classes.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The data type stored in the collection
 */
public interface ICommonsCollection <ELEMENTTYPE> extends Collection <ELEMENTTYPE>, Serializable
{
  /**
   * @return <code>true</code> if the map is not empty, <code>false</code>
   *         otherwise.
   */
  default boolean isNotEmpty ()
  {
    return !isEmpty ();
  }

  @Nonnull
  @ReturnsMutableCopy
  default ICommonsList <ELEMENTTYPE> getCopyAsList ()
  {
    return new CommonsArrayList <> (this);
  }

  /**
   * A special version of {@link #forEach(Consumer)} that can break iteration if
   * a certain requirement is fulfilled.
   * 
   * @param aConsumer
   *        The consumer to be invoked. May not be <code>null</code>.
   */
  default void forEachBreakable (final IBreakableConsumer <? super ELEMENTTYPE> aConsumer)
  {
    Objects.requireNonNull (aConsumer);
    for (final ELEMENTTYPE aElement : this)
      if (aConsumer.accept (aElement).isBreak ())
        break;
  }

  /**
   * Find all elements matching the supplied filter and add the matching
   * elements to the supplied target collection
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @param aDst
   *        The destination collection to be filled. May not be
   *        <code>null</code>.
   */
  default void findAll (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                        @Nonnull final Collection <? super ELEMENTTYPE> aDst)
  {
    findAll (aFilter, aDst::add);
  }

  /**
   * Find all elements matching the supplied filter and invoke the provided
   * consumer for each matching element.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @param aConsumer
   *        The consumer to be invoked for all matching elements. May not be
   *        <code>null</code>.
   */
  default void findAll (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                        @Nonnull final Consumer <? super ELEMENTTYPE> aConsumer)
  {
    CollectionHelper.findAll (this, aFilter, aConsumer);
  }

  /**
   * Convert all elements using the provided function and add it to the the
   * provided target collection.
   *
   * @param aMapper
   *        The mapping function to be executed for all elements. May not be
   *        <code>null</code>.
   * @param aDst
   *        The destination collection to be filled. May not be
   *        <code>null</code>.
   * @param <DSTTYPE>
   *        The destination type to be mapped to
   */
  default <DSTTYPE> void findAllMapped (@Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper,
                                        @Nonnull final Collection <? super DSTTYPE> aDst)
  {
    findAllMapped (aMapper, aDst::add);
  }

  /**
   * Convert all elements using the provided function and invoke the provided
   * consumer for all mapped elements.
   *
   * @param aMapper
   *        The mapping function to be executed for all elements. May not be
   *        <code>null</code>.
   * @param aConsumer
   *        The consumer to be invoked for all mapped elements. May not be
   *        <code>null</code>.
   * @param <DSTTYPE>
   *        The destination type to be mapped to
   */
  default <DSTTYPE> void findAllMapped (@Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper,
                                        @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    CollectionHelper.findAllMapped (this, aMapper, aConsumer);
  }

  /**
   * Find all elements matching the provided filter, convert the matching
   * elements using the provided function and add it to the the provided target
   * collection.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all elements. May not be
   *        <code>null</code>.
   * @param aDst
   *        The destination collection to be filled. May not be
   *        <code>null</code>.
   * @param <DSTTYPE>
   *        The destination type to be mapped to
   */
  default <DSTTYPE> void findAllMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                        @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper,
                                        @Nonnull final Collection <? super DSTTYPE> aDst)
  {
    findAllMapped (aFilter, aMapper, aDst::add);
  }

  /**
   * Find all elements matching the provided filter, convert the matching
   * elements using the provided function and invoke the provided consumer for
   * all mapped elements.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all matching elements. May
   *        not be <code>null</code>.
   * @param aConsumer
   *        The consumer to be invoked for all matching mapped elements. May not
   *        be <code>null</code>.
   * @param <DSTTYPE>
   *        The destination type to be mapped to
   */
  default <DSTTYPE> void findAllMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                        @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper,
                                        @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    CollectionHelper.findAllMapped (this, aFilter, aMapper, aConsumer);
  }

  /**
   * Find all elements that are <code>instanceOf</code> the provided class and
   * invoke the provided consumer for all matching elements. This is a special
   * implementation of {@link #findAllMapped(Predicate, Function, Consumer)}.
   *
   * @param aDstClass
   *        The class of which all elements should be find. May not be
   *        <code>null</code>.
   * @param aConsumer
   *        The consumer to be invoked for all instances of the provided class.
   *        May not be <code>null</code>.
   * @param <DSTTYPE>
   *        The destination type to be casted to
   */
  default <DSTTYPE extends ELEMENTTYPE> void findAllInstanceOf (@Nonnull final Class <DSTTYPE> aDstClass,
                                                                @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    findAllMapped (e -> aDstClass.isInstance (e), e -> aDstClass.cast (e), aConsumer);
  }

  /**
   * Find the first element that matches the provided filter. If no element
   * matches the provided filter or if the collection is empty,
   * <code>null</code> is returned by default. If this collection does not
   * maintain order (like Set) it is undefined which value is returned.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return <code>null</code> if no element matches the filter or if the
   *         collection is empty, the first matching element otherwise.
   * @see #findFirst(Predicate, Object)
   */
  @Nullable
  default ELEMENTTYPE findFirst (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return findFirst (aFilter, null);
  }

  /**
   * Find the first element that matches the provided filter. If no element
   * matches the provided filter or if the collection is empty, the provided
   * default value is returned. If this collection does not maintain order (like
   * Set) it is undefined which value is returned.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned if no element matches. May be
   *        <code>null</code>.
   * @return The provided default value if no element matches the filter or if
   *         the collection is empty, the first matching element otherwise.
   * @see #findFirst(Predicate)
   */
  @Nullable
  default ELEMENTTYPE findFirst (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                 @Nullable final ELEMENTTYPE aDefault)
  {
    return CollectionHelper.findFirst (this, aFilter, aDefault);
  }

  /**
   * Find the first element that matches the provided filter and convert it
   * using the provided mapper. If no element matches the provided filter or if
   * the collection is empty, <code>null</code> is returned by default. If this
   * collection does not maintain order (like Set) it is undefined which value
   * is returned.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for the first matching element.
   *        May not be <code>null</code>.
   * @return <code>null</code> if no element matches the filter or if the
   *         collection is empty, the first matching element otherwise.
   * @param <DSTTYPE>
   *        The destination type to be mapped to
   * @see #findFirstMapped(Predicate, Function, Object)
   */
  @Nullable
  default <DSTTYPE> DSTTYPE findFirstMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                             @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    return findFirstMapped (aFilter, aMapper, null);
  }

  /**
   * Find the first element that matches the provided filter and convert it
   * using the provided mapper. If no element matches the provided filter or if
   * the collection is empty, the provided default value is returned. If this
   * collection does not maintain order (like Set) it is undefined which value
   * is returned.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for the first matching element.
   *        May not be <code>null</code>.
   * @param aDefault
   *        The default value to be returned if no element matches. May be
   *        <code>null</code>.
   * @return The provided default value if no element matches the filter or if
   *         the collection is empty, the first matching element otherwise.
   * @param <DSTTYPE>
   *        The destination type to be mapped to
   * @see #findFirstMapped(Predicate, Function)
   */
  @Nullable
  default <DSTTYPE> DSTTYPE findFirstMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                             @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper,
                                             @Nullable final DSTTYPE aDefault)
  {
    return CollectionHelper.findFirstMapped (this, aFilter, aMapper, aDefault);
  }

  /**
   * Count the number of elements matching the provided filter.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return The number of elements matching the provided filter or the total
   *         number of elements if no filter is provided. Always &ge; 0.
   */
  @Nonnegative
  default int getCount (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return CollectionHelper.getCount (this, aFilter);
  }

  /**
   * Check if this collection contains any (=at least one) element matching the
   * provided filter.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return <code>true</code> if the container is not empty and at least one
   *         element matches the provided filter, <code>false</code> otherwise.
   *         If no filter is provided the response is the same as
   *         {@link #isNotEmpty()}.
   */
  default boolean containsAny (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return CollectionHelper.containsAny (this, aFilter);
  }

  /**
   * Check if this collection contains no (=zero) element matching the provided
   * filter.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return <code>true</code> if the container is empty or if no element
   *         matches the provided filter, <code>false</code> otherwise. If no
   *         filter is provided, this is the same as {@link #isEmpty()}.
   */
  default boolean containsNone (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return CollectionHelper.containsNone (this, aFilter);
  }

  /**
   * Check if this collection contains only (=all) elements matching the
   * provided filter. If this collection is empty, it does not fulfill this
   * requirement! If no filter is provided the return value is identical to
   * {@link #isNotEmpty()}.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return <code>true</code> if this collection is not empty and all elements
   *         matching the filter or if no filter is provided and this collection
   *         is not empty, <code>false</code> otherwise. If no filter is
   *         supplied the return value is identical to {@link #isNotEmpty()}.
   */
  default boolean containsOnly (@Nonnull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return CollectionHelper.containsOnly (this, aFilter);
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
  default EChange addIf (@Nullable final ELEMENTTYPE aElement, @Nullable final Predicate <ELEMENTTYPE> aFilter)
  {
    if (aFilter != null && !aFilter.test (aElement))
      return EChange.UNCHANGED;
    return EChange.valueOf (add (aElement));
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
    return EChange.valueOf (add (aElement));
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

    EChange eChange = EChange.UNCHANGED;
    if (aElements != null)
      for (final SRCTYPE aValue : aElements)
        if (aFilter == null || aFilter.test (aValue))
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

    EChange eChange = EChange.UNCHANGED;
    if (aElements != null)
      for (final SRCTYPE aValue : aElements)
        if (aFilter == null || aFilter.test (aValue))
          eChange = eChange.or (add (aMapper.apply (aValue)));
    return eChange;
  }

  /**
   * Clear all elements and add only the passed value.
   *
   * @param aValue
   *        The value to be added. May be <code>null</code>.
   * @see #clear()
   * @see #add(Object)
   */
  default void set (@Nullable final ELEMENTTYPE aValue)
  {
    clear ();
    add (aValue);
  }

  /**
   * Clear all elements and add all provided values. If no value is provided,
   * the collection is empty afterwards.
   *
   * @param aValues
   *        The values to be added. May be <code>null</code>.
   */
  default void setAll (@Nullable final Iterable <? extends ELEMENTTYPE> aValues)
  {
    clear ();
    addAll (aValues);
  }

  /**
   * Clear all elements and add all provided values. If no value is provided,
   * the collection is empty afterwards.
   *
   * @param aValues
   *        The values to be added. May be <code>null</code>.
   */
  default void setAll (@SuppressWarnings ("unchecked") @Nullable final ELEMENTTYPE... aValues)
  {
    clear ();
    addAll (aValues);
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
