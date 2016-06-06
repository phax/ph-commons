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

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

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

  public <T> CommonsArrayList (@Nullable final Collection <? extends T> aValues,
                               @Nonnull final Function <? super T, ? extends ELEMENTTYPE> aMapper)
  {
    super (CollectionHelper.getSize (aValues));
    if (aValues != null)
      addAllMapped (aValues, aMapper);
  }

  public <T> CommonsArrayList (@Nullable final Iterable <? extends T> aValues,
                               @Nonnull final Function <? super T, ? extends ELEMENTTYPE> aMapper)
  {
    if (aValues != null)
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
}
