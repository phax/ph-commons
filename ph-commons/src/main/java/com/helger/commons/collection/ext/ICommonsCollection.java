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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.iterate.IIterableIterator;
import com.helger.commons.collection.iterate.IterableIterator;
import com.helger.commons.state.EChange;

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
    return new CommonsArrayList<> (this);
  }

  default void findAll (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                        @Nonnull final Collection <? super ELEMENTTYPE> aDst)
  {
    findAll (aFilter, aDst::add);
  }

  default void findAll (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                        @Nonnull final Consumer <? super ELEMENTTYPE> aConsumer)
  {
    CollectionHelper.findAll (this, aFilter, aConsumer);
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> void findAllMapped (@Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper,
                                        @Nonnull final Collection <? super DSTTYPE> aDst)
  {
    findAllMapped (aMapper, aDst::add);
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> void findAllMapped (@Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper,
                                        @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    CollectionHelper.findAllMapped (this, aMapper, aConsumer);
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> void findAllMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                        @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper,
                                        @Nonnull final Collection <? super DSTTYPE> aDst)
  {
    findAllMapped (aFilter, aMapper, aDst::add);
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> void findAllMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                        @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper,
                                        @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    CollectionHelper.findAllMapped (this, aFilter, aMapper, aConsumer);
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE extends ELEMENTTYPE> void findAllInstanceOf (@Nonnull final Class <DSTTYPE> aDstClass,
                                                                @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    findAllMapped (e -> aDstClass.isInstance (e), e -> aDstClass.cast (e), aConsumer);
  }

  @Nullable
  default ELEMENTTYPE findFirst (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return findFirst (aFilter, null);
  }

  @Nullable
  default ELEMENTTYPE findFirst (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                 @Nullable final ELEMENTTYPE aDefault)
  {
    return CollectionHelper.findFirst (this, aFilter, aDefault);
  }

  @Nullable
  default <DSTTYPE> DSTTYPE findFirstMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                             @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    return findFirstMapped (aFilter, aMapper, null);
  }

  @Nullable
  default <DSTTYPE> DSTTYPE findFirstMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                             @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper,
                                             @Nullable final DSTTYPE aDefault)
  {
    return CollectionHelper.findFirstMapped (this, aFilter, aMapper, aDefault);
  }

  @Nonnegative
  default int getCount (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return CollectionHelper.getCount (this, aFilter);
  }

  default boolean containsAny (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return CollectionHelper.containsAny (this, aFilter);
  }

  default boolean containsNone (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return CollectionHelper.containsNone (this, aFilter);
  }

  default boolean containsOnly (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return CollectionHelper.containsOnly (this, aFilter);
  }

  /**
   * Safe collection element accessor method.
   *
   * @param nIndex
   *        The index to access. Should be &ge; 0.
   * @return <code>null</code> if the element cannot be accessed.
   */
  @Nullable
  default ELEMENTTYPE getAtIndex (@Nonnegative final int nIndex)
  {
    return getAtIndex (nIndex, null);
  }

  /**
   * Safe collection element accessor method.
   *
   * @param nIndex
   *        The index to access. Should be &ge; 0.
   * @param aDefault
   *        The value to be returned, if the index is out of bounds.
   * @return The default parameter if the element cannot be accessed.
   */
  @Nullable
  default ELEMENTTYPE getAtIndex (@Nonnegative final int nIndex, @Nullable final ELEMENTTYPE aDefault)
  {
    return getAtIndex (null, nIndex, aDefault);
  }

  @Nullable
  default ELEMENTTYPE getAtIndex (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                  @Nonnegative final int nIndex)
  {
    return getAtIndex (aFilter, nIndex, null);
  }

  @Nullable
  default ELEMENTTYPE getAtIndex (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                  @Nonnegative final int nIndex,
                                  @Nullable final ELEMENTTYPE aDefault)
  {
    return CollectionHelper.getAtIndex (this, aFilter, nIndex, aDefault);
  }

  @Nullable
  default <DSTTYPE> DSTTYPE getAtIndexMapped (@Nonnull final Predicate <? super ELEMENTTYPE> aFilter,
                                              @Nonnegative final int nIndex,
                                              @Nonnull final Function <? super ELEMENTTYPE, ? extends DSTTYPE> aMapper)
  {
    return getAtIndexMapped (aFilter, nIndex, aMapper, null);
  }

  @Nullable
  default <DSTTYPE> DSTTYPE getAtIndexMapped (@Nonnull final Predicate <? super ELEMENTTYPE> aFilter,
                                              @Nonnegative final int nIndex,
                                              @Nonnull final Function <? super ELEMENTTYPE, ? extends DSTTYPE> aMapper,
                                              @Nullable final DSTTYPE aDefault)
  {
    return CollectionHelper.getAtIndexMapped (this, aFilter, nIndex, aMapper, aDefault);
  }

  @Nonnull
  default ICommonsList <ELEMENTTYPE> getSorted (@Nonnull final Comparator <? super ELEMENTTYPE> aComparator)
  {
    return new CommonsArrayList<> (this).getSortedInline (aComparator);
  }

  /**
   * Add the passed element to this collection if passed predicate is fulfilled
   *
   * @param aElement
   *        The element to be added. May be <code>null</code>.
   * @param aFilter
   *        The predicate to be executed. May not be <code>null</code>.
   */
  default void addIf (@Nullable final ELEMENTTYPE aElement, @Nonnull final Predicate <ELEMENTTYPE> aFilter)
  {
    if (aFilter.test (aElement))
      add (aElement);
  }

  /**
   * Add the passed element to this collection if it is non-<code>null</code>.
   *
   * @param aElement
   *        The element to be added if non-<code>null</code>.
   * @see #add(Object)
   */
  default void addIfNotNull (@Nullable final ELEMENTTYPE aElement)
  {
    if (aElement != null)
      add (aElement);
  }

  /**
   * Add an array of elements to this collection.
   *
   * @param aElements
   *        The elements to be added. May be <code>null</code>.
   */
  default void addAll (@SuppressWarnings ("unchecked") @Nullable final ELEMENTTYPE... aElements)
  {
    if (aElements != null)
      for (final ELEMENTTYPE aElement : aElements)
        add (aElement);
  }

  /**
   * Add all elements of the passed enumeration to this collection.
   *
   * @param aEnum
   *        The enumeration to be iterated and the elements to be added. May be
   *        <code>null</code>.
   */
  default void addAll (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    if (aEnum != null)
      while (aEnum.hasMoreElements ())
        add (aEnum.nextElement ());
  }

  /**
   * Add all elements of the passed iterator to this collection.
   *
   * @param aIter
   *        The iterator to be iterated and the elements to be added. May be
   *        <code>null</code>.
   */
  default void addAll (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter != null)
      while (aIter.hasNext ())
        add (aIter.next ());
  }

  /**
   * Add all elements of the passed iterable to this collection.
   *
   * @param aElements
   *        The elements to be added. May be <code>null</code>.
   */
  default void addAll (@Nullable final Iterable <? extends ELEMENTTYPE> aElements)
  {
    if (aElements != null)
      for (final ELEMENTTYPE aElement : aElements)
        add (aElement);
  }

  default <SRCTYPE> void addAllMapped (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                       @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");
    if (aValues != null)
      for (final SRCTYPE aValue : aValues)
        add (aMapper.apply (aValue));
  }

  default <SRCTYPE> void addAllMapped (@Nullable final SRCTYPE [] aValues,
                                       @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");
    if (aValues != null)
      for (final SRCTYPE aValue : aValues)
        add (aMapper.apply (aValue));
  }

  /**
   * Clear and add the passed value
   *
   * @param aValue
   *        The value to be added.
   */
  default void set (@Nullable final ELEMENTTYPE aValue)
  {
    clear ();
    add (aValue);
  }

  /**
   * Clear and add all provided values.
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
   * Clear and add all provided values.
   *
   * @param aValues
   *        The values to be added. May be <code>null</code>.
   */
  default void setAll (@SuppressWarnings ("unchecked") @Nullable final ELEMENTTYPE... aValues)
  {
    clear ();
    addAll (aValues);
  }

  @Nonnull
  default EChange removeAll ()
  {
    if (isEmpty ())
      return EChange.UNCHANGED;
    clear ();
    return EChange.CHANGED;
  }

  @Nonnull
  default EChange removeObject (@Nullable final ELEMENTTYPE aElement)
  {
    return EChange.valueOf (remove (aElement));
  }

  @Nonnull
  default Collection <ELEMENTTYPE> getAsUnmodifiable ()
  {
    return Collections.unmodifiableCollection (this);
  }

  @Nonnull
  default IIterableIterator <ELEMENTTYPE> iterator2 ()
  {
    return new IterableIterator<> (this);
  }
}
