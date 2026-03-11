/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
import java.util.LinkedList;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.ReturnsMutableCopy;

/**
 * A special {@link LinkedList} implementation based on {@link ICommonsList}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        List element type
 */
public class CommonsLinkedList <ELEMENTTYPE> extends LinkedList <ELEMENTTYPE> implements ICommonsList <ELEMENTTYPE>
{
  /**
   * Create a new empty linked list.
   */
  public CommonsLinkedList ()
  {}

  /**
   * Create a new linked list with the elements of the provided collection.
   *
   * @param aCont
   *        The collection to copy elements from. May be <code>null</code>.
   */
  public CommonsLinkedList (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (aCont != null)
      addAll (aCont);
  }

  /**
   * Create a new linked list with the elements of the provided iterable.
   *
   * @param aIterable
   *        The iterable to copy elements from. May be <code>null</code>.
   */
  public CommonsLinkedList (@Nullable final Iterable <? extends ELEMENTTYPE> aIterable)
  {
    addAll (aIterable);
  }

  /**
   * Create a new linked list with mapped elements of the provided iterable.
   *
   * @param aValues
   *        The iterable to copy elements from. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to apply. May not be <code>null</code>.
   * @param <SRCTYPE>
   *        Source element type
   */
  public <SRCTYPE> CommonsLinkedList (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                      @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  /**
   * Create a new linked list with exactly one element.
   *
   * @param aValue
   *        The value to add. May be <code>null</code>.
   */
  public CommonsLinkedList (@Nullable final ELEMENTTYPE aValue)
  {
    add (aValue);
  }

  /**
   * Create a new linked list with the provided values.
   *
   * @param aValues
   *        The values to add. May be <code>null</code>.
   */
  @SafeVarargs
  public CommonsLinkedList (@Nullable final ELEMENTTYPE... aValues)
  {
    addAll (aValues);
  }

  /**
   * Create a new linked list with mapped elements of the provided array.
   *
   * @param aValues
   *        The array to copy elements from. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to apply. May not be <code>null</code>.
   * @param <SRCTYPE>
   *        Source element type
   */
  public <SRCTYPE> CommonsLinkedList (@Nullable final SRCTYPE [] aValues,
                                      @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  @Override
  @NonNull
  @ReturnsMutableCopy
  public <T> CommonsLinkedList <T> createInstance ()
  {
    return new CommonsLinkedList <> ();
  }

  /**
   * @return A mutable copy of this list. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public CommonsLinkedList <ELEMENTTYPE> getClone ()
  {
    return new CommonsLinkedList <> (this);
  }
}
