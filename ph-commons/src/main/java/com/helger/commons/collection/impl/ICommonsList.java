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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.state.EChange;

/**
 * A special {@link List} interface with extended functionality based on
 * {@link ICommonsCollection}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The data type of the elements in the list.
 */
public interface ICommonsList <ELEMENTTYPE> extends
                              List <ELEMENTTYPE>,
                              ICommonsCollection <ELEMENTTYPE>,
                              ICloneable <ICommonsList <ELEMENTTYPE>>
{
  /**
   * Create a new empty list. Overwrite this if you don't want to use
   * {@link CommonsArrayList}.
   *
   * @return A new empty list. Never <code>null</code>.
   * @param <T>
   *        List element type
   */
  @Nonnull
  @ReturnsMutableCopy
  default <T> ICommonsList <T> createInstance ()
  {
    return new CommonsArrayList <> ();
  }

  /**
   * Get all elements matching the provided filter. If no filter is provided,
   * the return value is same as {@link #getClone()}.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return A non-<code>null</code> copy of this list containing all matching
   *         entries (or all entries if no filter is provided).
   * @see #findAll(Predicate, java.util.function.Consumer)
   */
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsList <ELEMENTTYPE> getAll (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return getClone ();

    final ICommonsList <ELEMENTTYPE> ret = createInstance ();
    findAll (aFilter, ret::add);
    return ret;
  }

  /**
   * Create a new list where all existing elements are mapped with the provided
   * function.
   *
   * @param aMapper
   *        The mapping function to be executed. May not be <code>null</code>.
   * @return A new non-<code>null</code> list with all mapped elements.
   * @param <DSTTYPE>
   *        The destination type to be mapped to
   * @see #getAllMapped(Predicate, Function)
   * @see #findAllMapped(Function, java.util.function.Consumer)
   */
  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsList <DSTTYPE> getAllMapped (@Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    final ICommonsList <DSTTYPE> ret = createInstance ();
    findAllMapped (aMapper, ret::add);
    return ret;
  }

  /**
   * Create a new list where all elements matching the filter are mapped with
   * the provided function.
   *
   * @param aFilter
   *        The filter to be applied. Maybe <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed. May not be <code>null</code>.
   * @return A new non-<code>null</code> list with all mapped elements. If no
   *         filter is provided the result is the same as of
   *         {@link #getAllMapped(Function)}.
   * @param <DSTTYPE>
   *        The destination type to be mapped to
   * @see #getAllMapped(Function)
   * @see #findAllMapped(Predicate, Function, java.util.function.Consumer)
   */
  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsList <DSTTYPE> getAllMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                         @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    final ICommonsList <DSTTYPE> ret = createInstance ();
    findAllMapped (aFilter, aMapper, ret::add);
    return ret;
  }

  /**
   * Get all instances of the provided class that are contained in this list.
   *
   * @param aDstClass
   *        The class to search all instances of. May not be <code>null</code>.
   * @return A list with all instances of the provided class, already casted.
   *         Never <code>null</code>.
   * @param <DSTTYPE>
   *        The destination type to be casted to
   * @see #findAllInstanceOf(Class, java.util.function.Consumer)
   */
  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE extends ELEMENTTYPE> ICommonsList <DSTTYPE> getAllInstanceOf (@Nonnull final Class <DSTTYPE> aDstClass)
  {
    final ICommonsList <DSTTYPE> ret = createInstance ();
    findAllInstanceOf (aDstClass, ret::add);
    return ret;
  }

  /**
   * @return The first element of the list or <code>null</code> if the list is
   *         empty.
   * @see #getFirst(Object)
   * @see #findFirst(Predicate)
   */
  @Nullable
  default ELEMENTTYPE getFirst ()
  {
    return getFirst (null);
  }

  /**
   * @param aDefault
   *        The default value to be returned if this list is empty. May be
   *        <code>null</code>.
   * @return The first element of the list or the provided default value if the
   *         list is empty.
   * @see #getFirst()
   * @see #findFirst(Predicate)
   */
  @Nullable
  default ELEMENTTYPE getFirst (@Nullable final ELEMENTTYPE aDefault)
  {
    return isEmpty () ? aDefault : get (0);
  }

  /**
   * @return The last element of the list or <code>null</code> if the list is
   *         empty.
   * @see #getLast(Object)
   */
  @Nullable
  default ELEMENTTYPE getLast ()
  {
    return getLast (null);
  }

  /**
   * @param aDefault
   *        The default value to be returned if this list is empty. May be
   *        <code>null</code>.
   * @return The last element of the list or <code>null</code> if the list is
   *         empty.
   * @see #getLast()
   */
  @Nullable
  default ELEMENTTYPE getLast (@Nullable final ELEMENTTYPE aDefault)
  {
    final int nSize = size ();
    return nSize == 0 ? aDefault : get (nSize - 1);
  }

  /*
   * Special overload with the index-based access.
   */
  @Override
  @Nullable
  default ELEMENTTYPE getAtIndex (@Nonnegative final int nIndex, @Nullable final ELEMENTTYPE aDefault)
  {
    return nIndex >= 0 && nIndex < size () ? get (nIndex) : aDefault;
  }

  /**
   * Set the first element of this list.
   *
   * @param aNewElement
   *        The new element at index 0.
   * @return The previous element. May be <code>null</code>.
   * @see #set(int, Object)
   */
  @Nullable
  default ELEMENTTYPE setFirst (@Nullable final ELEMENTTYPE aNewElement)
  {
    return set (0, aNewElement);
  }

  /**
   * Set the last element of this list.
   *
   * @param aNewElement
   *        The new element at index <code>size()-1</code>.
   * @return The previous element. May be <code>null</code>.
   * @see #set(int, Object)
   */
  @Nullable
  default ELEMENTTYPE setLast (@Nullable final ELEMENTTYPE aNewElement)
  {
    return set (size () - 1, aNewElement);
  }

  /**
   * Remove the element at the specified index from the passed list. This works
   * if the list is not <code>null</code> and the index is &ge; 0 and &lt;
   * <code>list.size()</code>
   *
   * @param nIndex
   *        The index to be removed. May be arbitrary.
   * @return {@link EChange#CHANGED} if removal was successful
   * @see #removeAndReturnElementAtIndex(int)
   */
  @Nonnull
  default EChange removeAtIndex (final int nIndex)
  {
    if (nIndex < 0 || nIndex >= size ())
      return EChange.UNCHANGED;
    remove (nIndex);
    return EChange.CHANGED;
  }

  /**
   * Remove the element at the specified index from the passed list. This works
   * if the list is not <code>null</code> and the index is &ge; 0 and &lt;
   * <code>list.size()</code>
   *
   * @param nIndex
   *        The index to be removed. May be arbitrary.
   * @return <code>null</code> if removal failed or the removed element. Note:
   *         the removed element may also be <code>null</code> so it may be
   *         tricky to determine if removal succeeded or not!
   * @see #removeAtIndex(int)
   */
  @Nullable
  default ELEMENTTYPE removeAndReturnElementAtIndex (final int nIndex)
  {
    return nIndex < 0 || nIndex >= size () ? null : remove (nIndex);
  }

  /**
   * Remove the first element of the list.
   *
   * @return <code>null</code> if the list is empty or the previously contained
   *         element at index 0.
   */
  @Nullable
  default ELEMENTTYPE removeFirst ()
  {
    return isEmpty () ? null : remove (0);
  }

  /**
   * Remove the last element of the list.
   *
   * @return <code>null</code> if the list is empty or the previously contained
   *         element at index <code>size()-1</code>.
   */
  @Nullable
  default ELEMENTTYPE removeLast ()
  {
    final int nSize = size ();
    return nSize == 0 ? null : remove (nSize - 1);
  }

  @Override
  @Nonnull
  @CodingStyleguideUnaware
  default List <ELEMENTTYPE> getAsUnmodifiable ()
  {
    return Collections.unmodifiableList (this);
  }

  /**
   * Sort this line without creating a copy.
   *
   * @param aComparator
   *        The comparator used for sorting. May not be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  default ICommonsList <ELEMENTTYPE> getSortedInline (@Nonnull final Comparator <? super ELEMENTTYPE> aComparator)
  {
    sort (aComparator);
    return this;
  }

  /**
   * Reverse the order of this list.
   *
   * @return this for chaining
   */
  @Nonnull
  default ICommonsList <ELEMENTTYPE> reverse ()
  {
    Collections.reverse (this);
    return this;
  }

  /**
   * Swap list items.
   *
   * @param nFirstIndex
   *        The first index to swap.
   * @param nSecondIndex
   *        The second index to swap.
   * @return this for chaining
   * @since 8.6.0
   */
  @Nonnull
  default ICommonsList <ELEMENTTYPE> swapItems (final int nFirstIndex, final int nSecondIndex)
  {
    if (nFirstIndex != nSecondIndex)
    {
      final ELEMENTTYPE aTmp = get (nFirstIndex);
      set (nFirstIndex, get (nSecondIndex));
      set (nSecondIndex, aTmp);
    }
    return this;
  }
}
