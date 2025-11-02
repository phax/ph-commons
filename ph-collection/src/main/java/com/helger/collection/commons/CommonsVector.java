/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.collection.commons;

import java.util.Collection;
import java.util.Vector;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.array.ArrayHelper;
import com.helger.collection.CollectionHelper;

/**
 * A special {@link Vector} implementation based on {@link ICommonsList}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        List element type
 */
public class CommonsVector <ELEMENTTYPE> extends Vector <ELEMENTTYPE> implements ICommonsList <ELEMENTTYPE>
{
  public CommonsVector ()
  {}

  public CommonsVector (@Nonnegative final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsVector (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    super (CollectionHelper.getSize (aCont));
    if (aCont != null)
      addAll (aCont);
  }

  public CommonsVector (@Nullable final Iterable <? extends ELEMENTTYPE> aIterable)
  {
    addAll (aIterable);
  }

  public <SRCTYPE> CommonsVector (@Nullable final Collection <? extends SRCTYPE> aValues,
                                  @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    super (CollectionHelper.getSize (aValues));
    addAllMapped (aValues, aMapper);
  }

  public <SRCTYPE> CommonsVector (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                  @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  public CommonsVector (@Nullable final ELEMENTTYPE aValue)
  {
    super (1);
    add (aValue);
  }

  @SafeVarargs
  public CommonsVector (@Nullable final ELEMENTTYPE... aValues)
  {
    super (ArrayHelper.getSize (aValues));
    addAll (aValues);
  }

  public <SRCTYPE> CommonsVector (@Nullable final SRCTYPE [] aValues,
                                  @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    super (ArrayHelper.getSize (aValues));
    addAllMapped (aValues, aMapper);
  }

  @Override
  @NonNull
  @ReturnsMutableCopy
  public <T> CommonsVector <T> createInstance ()
  {
    return new CommonsVector <> ();
  }

  @NonNull
  @ReturnsMutableCopy
  public CommonsVector <ELEMENTTYPE> getClone ()
  {
    return new CommonsVector <> (this);
  }

  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> createFiltered (@Nullable final Iterable <? extends ELEMENTTYPE> aValues,
                                                                          @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsVector <ELEMENTTYPE> ret = new CommonsVector <> (CollectionHelper.getSize (aValues));
    ret.addAll (aValues, aFilter);
    return ret;
  }

  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsVector <ELEMENTTYPE> createFiltered (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                                                                   @Nullable final Predicate <? super SRCTYPE> aFilter,
                                                                                   @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    final CommonsVector <ELEMENTTYPE> ret = new CommonsVector <> (CollectionHelper.getSize (aValues));
    ret.addAllMapped (aValues, aFilter, aMapper);
    return ret;
  }

  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> CommonsVector <ELEMENTTYPE> createFiltered (@Nullable final ELEMENTTYPE [] aValues,
                                                                          @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    final CommonsVector <ELEMENTTYPE> ret = new CommonsVector <> (ArrayHelper.getSize (aValues));
    ret.addAll (aValues, aFilter);
    return ret;
  }

  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, ELEMENTTYPE> CommonsVector <ELEMENTTYPE> createFiltered (@Nullable final SRCTYPE [] aValues,
                                                                                   @Nullable final Predicate <? super SRCTYPE> aFilter,
                                                                                   @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    final CommonsVector <ELEMENTTYPE> ret = new CommonsVector <> (ArrayHelper.getSize (aValues));
    ret.addAllMapped (aValues, aFilter, aMapper);
    return ret;
  }
}
