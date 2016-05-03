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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.state.EChange;

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
    return new CommonsArrayList<> ();
  }

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

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsList <DSTTYPE> getAllMapped (@Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    final ICommonsList <DSTTYPE> ret = createInstance ();
    findAllMapped (aMapper, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsList <DSTTYPE> getAllMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                         @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    final ICommonsList <DSTTYPE> ret = createInstance ();
    findAllMapped (aFilter, aMapper, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE extends ELEMENTTYPE> ICommonsList <DSTTYPE> getAllInstanceOf (@Nonnull final Class <DSTTYPE> aDstClass)
  {
    final ICommonsList <DSTTYPE> ret = createInstance ();
    findAllInstanceOf (aDstClass, ret::add);
    return ret;
  }

  /**
   * Special forEach that takes an {@link ObjIntConsumer} which is provided the
   * value AND the index.
   * 
   * @param aConsumer
   *        The consumer to use. May not be <code>null</code>.
   */
  default void forEach (@Nonnull final ObjIntConsumer <? super ELEMENTTYPE> aConsumer)
  {
    int nIndex = 0;
    for (final ELEMENTTYPE aItem : this)
    {
      aConsumer.accept (aItem, nIndex);
      ++nIndex;
    }
  }

  @Nullable
  default ELEMENTTYPE getFirst ()
  {
    return isEmpty () ? null : get (0);
  }

  @Nullable
  default ELEMENTTYPE getLast ()
  {
    final int nSize = size ();
    return nSize == 0 ? null : get (nSize - 1);
  }

  /**
   * Safe list element accessor method.
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
    return nIndex >= 0 && nIndex < size () ? get (nIndex) : aDefault;
  }

  @Nullable
  default ELEMENTTYPE setFirst (@Nullable final ELEMENTTYPE aNewElement)
  {
    return set (0, aNewElement);
  }

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

  @Nullable
  default ELEMENTTYPE removeFirst ()
  {
    return isEmpty () ? null : remove (0);
  }

  @Nullable
  default ELEMENTTYPE removeLast ()
  {
    final int nSize = size ();
    return nSize == 0 ? null : remove (nSize - 1);
  }

  @Nonnull
  default List <ELEMENTTYPE> getAsUnmodifiable ()
  {
    return Collections.unmodifiableList (this);
  }

  @Nonnull
  default ICommonsList <ELEMENTTYPE> getSorted (@Nonnull final Comparator <? super ELEMENTTYPE> aComparator)
  {
    sort (aComparator);
    return this;
  }

  default void reverse ()
  {
    Collections.reverse (this);
  }
}
