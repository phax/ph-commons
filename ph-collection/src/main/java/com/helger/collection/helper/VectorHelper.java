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
package com.helger.collection.helper;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsVector;
import com.helger.commons.collection.iterate.IIterableIterator;

@Immutable
public final class VectorHelper
{
  private VectorHelper ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector (@Nonnegative final int nInitialCapacity)
  {
    return new CommonsVector <> (nInitialCapacity);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector ()
  {
    return new CommonsVector <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> CommonsVector <DSTTYPE> newVectorMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                            @Nonnull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (CollectionHelper.isEmpty (aCollection))
      return newVector (0);
    final CommonsVector <DSTTYPE> ret = newVector (aCollection.size ());
    ret.addAllMapped (aCollection, aMapper);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> CommonsVector <DSTTYPE> newVectorMapped (@Nullable final SRCTYPE [] aArray,
                                                                            @Nonnull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (ArrayHelper.isEmpty (aArray))
      return newVector (0);
    final CommonsVector <DSTTYPE> ret = newVector (aArray.length);
    ret.addAllMapped (aArray, aMapper);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                                     @Nonnull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (CollectionHelper.isEmpty (aCollection))
      return newVector (0);
    final CommonsVector <ELEMENTTYPE> ret = newVector (aCollection.size ());
    ret.addAll (aCollection, aFilter);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVectorPrefilled (@Nullable final ELEMENTTYPE aValue,
                                                                              @Nonnegative final int nElements)
  {
    ValueEnforcer.isGE0 (nElements, "Elements");

    final CommonsVector <ELEMENTTYPE> ret = newVector (nElements);
    for (int i = 0; i < nElements; ++i)
      ret.add (aValue);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector (@Nullable final ELEMENTTYPE aValue)
  {
    return new CommonsVector <> (aValue);
  }

  @Nonnull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector (@Nullable final ELEMENTTYPE... aValues)
  {
    // Don't user Arrays.asVector since aIter returns an unmodifiable list!
    if (ArrayHelper.isEmpty (aValues))
      return newVector (0);

    return new CommonsVector <> (aValues);
  }

  /**
   * Compared to {@link Collections#list(Enumeration)} this method is more
   * flexible in Generics parameter.
   *
   * @param <ELEMENTTYPE>
   *        Type of the elements
   * @param aEnum
   *        The enumeration to be converted
   * @return The non-<code>null</code> created {@link CommonsVector}.
   * @see Collections#list(Enumeration)
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum)
  {
    final CommonsVector <ELEMENTTYPE> ret = newVector ();
    ret.addAll (aEnum);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector (@Nullable final Iterator <? extends ELEMENTTYPE> aIter)
  {
    final CommonsVector <ELEMENTTYPE> ret = newVector ();
    ret.addAll (aIter);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector (@Nullable final Iterable <? extends ELEMENTTYPE> aIter)
  {
    final CommonsVector <ELEMENTTYPE> ret = newVector ();
    ret.addAll (aIter);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (CollectionHelper.isEmpty (aCont))
      return newVector (0);

    return new CommonsVector <> (aCont);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> newVector (@Nullable final IIterableIterator <? extends ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return newVector (0);
    return newVector (aIter.iterator ());
  }
}
