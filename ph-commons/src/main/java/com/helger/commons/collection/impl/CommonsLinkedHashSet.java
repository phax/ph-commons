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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.CollectionHelper;

/**
 * A special {@link LinkedHashSet} implementation based on
 * {@link ICommonsOrderedSet}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        Set element type
 */
public class CommonsLinkedHashSet <ELEMENTTYPE> extends LinkedHashSet <ELEMENTTYPE> implements
                                  ICommonsOrderedSet <ELEMENTTYPE>
{
  public CommonsLinkedHashSet ()
  {}

  public CommonsLinkedHashSet (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsLinkedHashSet (final int nInitialCapacity, final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  public CommonsLinkedHashSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    super (CollectionHelper.getSize (aCont));
    if (aCont != null)
      addAll (aCont);
  }

  public CommonsLinkedHashSet (@Nullable final Iterable <? extends ELEMENTTYPE> aIterable)
  {
    addAll (aIterable);
  }

  public <SRCTYPE> CommonsLinkedHashSet (@Nullable final Collection <? extends SRCTYPE> aValues,
                                         @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    super (CollectionHelper.getSize (aValues));
    addAllMapped (aValues, aMapper);
  }

  public <SRCTYPE> CommonsLinkedHashSet (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                         @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  public CommonsLinkedHashSet (@Nullable final ELEMENTTYPE aValue)
  {
    super (1);
    add (aValue);
  }

  @SafeVarargs
  public CommonsLinkedHashSet (@Nullable final ELEMENTTYPE... aValues)
  {
    super (ArrayHelper.getSize (aValues));
    addAll (aValues);
  }

  public <SRCTYPE> CommonsLinkedHashSet (@Nullable final SRCTYPE [] aValues,
                                         @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    super (ArrayHelper.getSize (aValues));
    addAllMapped (aValues, aMapper);
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  public <T> CommonsLinkedHashSet <T> createInstance ()
  {
    return new CommonsLinkedHashSet <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsLinkedHashSet <ELEMENTTYPE> getClone ()
  {
    return new CommonsLinkedHashSet <> (this);
  }
}
