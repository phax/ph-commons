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
import java.util.LinkedList;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;

/**
 * A special {@link LinkedList} implementation based on {@link ICommonsList}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        List element type
 */
public class CommonsLinkedList <ELEMENTTYPE> extends LinkedList <ELEMENTTYPE> implements ICommonsList <ELEMENTTYPE>
{
  public CommonsLinkedList ()
  {}

  public CommonsLinkedList (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (aCont != null)
      addAll (aCont);
  }

  public CommonsLinkedList (@Nullable final Iterable <? extends ELEMENTTYPE> aIterable)
  {
    addAll (aIterable);
  }

  public <SRCTYPE> CommonsLinkedList (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                      @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  public CommonsLinkedList (@Nullable final ELEMENTTYPE aValue)
  {
    add (aValue);
  }

  @SafeVarargs
  public CommonsLinkedList (@Nullable final ELEMENTTYPE... aValues)
  {
    addAll (aValues);
  }

  public <SRCTYPE> CommonsLinkedList (@Nullable final SRCTYPE [] aValues,
                                      @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  public <T> CommonsLinkedList <T> createInstance ()
  {
    return new CommonsLinkedList <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsLinkedList <ELEMENTTYPE> getClone ()
  {
    return new CommonsLinkedList <> (this);
  }
}
