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

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.CollectionHelper;

/**
 * A special {@link ArrayList} implementation based on {@link ICommonsList}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        List element type
 */
public class CommonsArrayList <ELEMENTTYPE> extends ArrayList <ELEMENTTYPE> implements ICommonsList <ELEMENTTYPE>
{
  // No logger here!

  public CommonsArrayList ()
  {}

  public CommonsArrayList (@Nonnegative final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsArrayList (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    super (CollectionHelper.getSize (aCont));
    if (aCont != null)
      addAll (aCont);
  }

  public CommonsArrayList (@Nullable final Iterable <? extends ELEMENTTYPE> aIterable)
  {
    addAll (aIterable);
  }

  public <SRCTYPE> CommonsArrayList (@Nullable final Collection <? extends SRCTYPE> aValues,
                                     @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    super (CollectionHelper.getSize (aValues));
    addAllMapped (aValues, aMapper);
  }

  public <SRCTYPE> CommonsArrayList (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                     @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  public CommonsArrayList (@Nullable final ELEMENTTYPE aValue)
  {
    super (1);
    add (aValue);
  }

  @SafeVarargs
  public CommonsArrayList (@Nullable final ELEMENTTYPE... aValues)
  {
    super (ArrayHelper.getSize (aValues));
    addAll (aValues);
  }

  public <SRCTYPE> CommonsArrayList (@Nullable final SRCTYPE [] aValues,
                                     @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    super (ArrayHelper.getSize (aValues));
    addAllMapped (aValues, aMapper);
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  public <T> CommonsArrayList <T> createInstance ()
  {
    return new CommonsArrayList <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsArrayList <ELEMENTTYPE> getClone ()
  {
    return new CommonsArrayList <> (this);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> createFiltered (@Nullable final Iterable <? extends ELEMENTTYPE> aValues,
                                                                             @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsArrayList <ELEMENTTYPE> ret = new CommonsArrayList <> ();
    ret.addAll (aValues, aFilter);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> createFiltered (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                                                                      @Nullable final Predicate <? super SRCTYPE> aFilter,
                                                                                      @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    final CommonsArrayList <ELEMENTTYPE> ret = new CommonsArrayList <> (ArrayHelper.getSize (aValues));
    ret.addAllMapped (aValues, aFilter, aMapper);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> createFiltered (@Nullable final ELEMENTTYPE [] aValues,
                                                                             @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsArrayList <ELEMENTTYPE> ret = new CommonsArrayList <> (ArrayHelper.getSize (aValues));
    ret.addAll (aValues, aFilter);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsArrayList <ELEMENTTYPE> createFiltered (@Nullable final SRCTYPE [] aValues,
                                                                                      @Nullable final Predicate <? super SRCTYPE> aFilter,
                                                                                      @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    final CommonsArrayList <ELEMENTTYPE> ret = new CommonsArrayList <> (ArrayHelper.getSize (aValues));
    ret.addAllMapped (aValues, aFilter, aMapper);
    return ret;
  }
}
