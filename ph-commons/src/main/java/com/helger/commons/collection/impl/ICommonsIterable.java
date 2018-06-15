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

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.IteratorHelper;
import com.helger.commons.state.EContinue;

/**
 * Extended version of {@link Iterable} with some additional default methods.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The data type to iterate
 */
public interface ICommonsIterable <ELEMENTTYPE> extends Iterable <ELEMENTTYPE>, Serializable
{
  /**
   * Special forEach that takes an {@link ObjIntConsumer} which is provided the
   * value AND the index.
   *
   * @param aConsumer
   *        The consumer to use. May not be <code>null</code>.
   */
  default void forEachByIndex (@Nonnull final ObjIntConsumer <? super ELEMENTTYPE> aConsumer)
  {
    int nIndex = 0;
    for (final ELEMENTTYPE aItem : this)
    {
      aConsumer.accept (aItem, nIndex);
      ++nIndex;
    }
  }

  /**
   * A special version of {@link #forEach(Consumer)} that can break iteration if
   * a certain requirement is fulfilled.
   *
   * @param aConsumer
   *        The consumer to be invoked. May not be <code>null</code>.
   * @return {@link EContinue#BREAK} if iteration was stopped,
   *         {@link EContinue#CONTINUE} otherwise.
   */
  @Nonnull
  default EContinue forEachBreakable (@Nonnull final Function <? super ELEMENTTYPE, EContinue> aConsumer)
  {
    Objects.requireNonNull (aConsumer);
    for (final ELEMENTTYPE aElement : this)
      if (aConsumer.apply (aElement).isBreak ())
        return EContinue.BREAK;
    return EContinue.CONTINUE;
  }

  /**
   * Special forEach that takes an additional filter so that the consumer is
   * only invoked for elements matching the provided filter.
   *
   * @param aConsumer
   *        The consumer to use. May not be <code>null</code>.
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>. If the filter is
   *        <code>null</code> this method behaves like
   *        {@link #forEach(Consumer)}.
   * @since 8.5.2
   */
  default void forEach (@Nonnull final Consumer <? super ELEMENTTYPE> aConsumer,
                        @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      forEach (aConsumer);
    else
      for (final ELEMENTTYPE aItem : this)
        if (aFilter.test (aItem))
          aConsumer.accept (aItem);
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
   * Convert all elements using the provided function, find all mapped elements
   * matching the provided filter and invoke the provided consumer for all
   * matching elements.
   *
   * @param aMapper
   *        The mapping function to be executed for all matching elements. May
   *        not be <code>null</code>.
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @param aConsumer
   *        The consumer to be invoked for all matching mapped elements. May not
   *        be <code>null</code>.
   * @param <DSTTYPE>
   *        The destination type to be mapped to
   * @since 8.5.2
   */
  default <DSTTYPE> void findAllMapped (@Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper,
                                        @Nullable final Predicate <? super DSTTYPE> aFilter,
                                        @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    CollectionHelper.findAllMapped (this, aMapper, aFilter, aConsumer);
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
    findAllMapped (aDstClass::isInstance, aDstClass::cast, aConsumer);
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
   * Check if this collection contains any (=at least one) element matching the
   * provided filter.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return <code>true</code> if the container is not empty and at least one
   *         element matches the provided filter, <code>false</code> otherwise.
   *         If no filter is provided the response is the same as
   *         {@link ICommonsCollection#isNotEmpty()}.
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
   *         filter is provided, this is the same as
   *         {@link ICommonsCollection#isEmpty()}.
   */
  default boolean containsNone (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return CollectionHelper.containsNone (this, aFilter);
  }

  /**
   * Check if this collection contains only (=all) elements matching the
   * provided filter. If this collection is empty, it does not fulfill this
   * requirement! If no filter is provided the return value is identical to
   * {@link ICommonsCollection#isNotEmpty()}.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return <code>true</code> if this collection is not empty and all elements
   *         matching the filter or if no filter is provided and this collection
   *         is not empty, <code>false</code> otherwise. If no filter is
   *         supplied the return value is identical to
   *         {@link ICommonsCollection#isNotEmpty()}.
   */
  default boolean containsOnly (@Nonnull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return CollectionHelper.containsOnly (this, aFilter);
  }

  /**
   * Retrieve the size of this {@link Iterable}. This method does by default
   * iterate all elements. Please provide a more efficient solution. If this is
   * a collection, consider using <code>size()</code> instead.
   *
   * @return The number objects contained. Always &ge; 0.
   */
  @Nonnegative
  default int getCount ()
  {
    return IteratorHelper.getSize (iterator ());
  }

  /**
   * Count the number of elements in this iterable matching the provided filter.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return The number of matching elements. Always &ge; 0. If no filter is
   *         provided this is the same as {@link #getCount()}.
   */
  @Nonnegative
  default int getCount (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return getCount ();

    int ret = 0;
    for (final ELEMENTTYPE aElement : this)
      if (aFilter.test (aElement))
        ret++;
    return ret;
  }
}
