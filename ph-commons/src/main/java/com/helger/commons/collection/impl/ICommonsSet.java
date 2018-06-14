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
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.lang.ICloneable;

/**
 * A special {@link Set} interface with extended functionality based on
 * {@link ICommonsCollection}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The data type of the elements in the list.
 */
public interface ICommonsSet <ELEMENTTYPE> extends
                             Set <ELEMENTTYPE>,
                             ICommonsCollection <ELEMENTTYPE>,
                             ICloneable <ICommonsSet <ELEMENTTYPE>>
{
  /**
   * Create a new empty set. Overwrite this if you don't want to use
   * {@link CommonsHashSet}.
   *
   * @return A new empty set. Never <code>null</code>.
   * @param <T>
   *        Set element type
   */
  @Nonnull
  @ReturnsMutableCopy
  default <T> ICommonsSet <T> createInstance ()
  {
    return new CommonsHashSet <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  default ICommonsSet <ELEMENTTYPE> getAll (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return getClone ();

    final ICommonsSet <ELEMENTTYPE> ret = createInstance ();
    findAll (aFilter, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsSet <DSTTYPE> getAllMapped (@Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    final ICommonsSet <DSTTYPE> ret = createInstance ();
    findAllMapped (aMapper, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsSet <DSTTYPE> getAllMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                        @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    final ICommonsSet <DSTTYPE> ret = createInstance ();
    findAllMapped (aFilter, aMapper, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE extends ELEMENTTYPE> ICommonsSet <DSTTYPE> getAllInstanceOf (@Nonnull final Class <DSTTYPE> aDstClass)
  {
    final ICommonsSet <DSTTYPE> ret = createInstance ();
    findAllInstanceOf (aDstClass, ret::add);
    return ret;
  }

  /**
   * Replace an existing item in this Set.
   *
   * @param aElement
   *        The new element to be added.
   * @return <code>true</code> if a previous item was removed,
   *         <code>false</code> if this element was simply added.
   */
  default boolean replace (@Nullable final ELEMENTTYPE aElement)
  {
    final boolean bRemoved = remove (aElement);
    add (aElement);
    return bRemoved;
  }

  @Override
  @Nonnull
  @CodingStyleguideUnaware
  default Set <ELEMENTTYPE> getAsUnmodifiable ()
  {
    return Collections.unmodifiableSet (this);
  }
}
