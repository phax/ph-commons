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
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.ReturnsMutableCopy;

/**
 * A special {@link CopyOnWriteArraySet} implementation based on
 * {@link ICommonsSet}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        Set element type
 */
public class CommonsCopyOnWriteArraySet <ELEMENTTYPE> extends CopyOnWriteArraySet <ELEMENTTYPE> implements ICommonsSet <ELEMENTTYPE>
{
  /**
   * Create a new empty copy-on-write array set.
   */
  public CommonsCopyOnWriteArraySet ()
  {}

  /**
   * Create a new copy-on-write array set with the elements of the provided collection.
   *
   * @param aCont
   *        The collection to copy elements from. May be <code>null</code>.
   */
  public CommonsCopyOnWriteArraySet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (aCont != null)
      addAll (aCont);
  }

  /**
   * Create a new copy-on-write array set with the elements of the provided iterable.
   *
   * @param aIterable
   *        The iterable to copy elements from. May be <code>null</code>.
   */
  public CommonsCopyOnWriteArraySet (@Nullable final Iterable <? extends ELEMENTTYPE> aIterable)
  {
    addAll (aIterable);
  }

  /**
   * Create a new copy-on-write array set with mapped elements of the provided iterable.
   *
   * @param aValues
   *        The iterable to copy elements from. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to apply to each element. May not be <code>null</code>.
   * @param <SRCTYPE>
   *        Source element type
   */
  public <SRCTYPE> CommonsCopyOnWriteArraySet (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                               @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  /**
   * Create a new copy-on-write array set with exactly one element.
   *
   * @param aValue
   *        The value to add. May be <code>null</code>.
   */
  public CommonsCopyOnWriteArraySet (@Nullable final ELEMENTTYPE aValue)
  {
    add (aValue);
  }

  /**
   * Create a new copy-on-write array set with the provided values.
   *
   * @param aValues
   *        The values to add. May be <code>null</code>.
   */
  @SafeVarargs
  public CommonsCopyOnWriteArraySet (@Nullable final ELEMENTTYPE... aValues)
  {
    addAll (aValues);
  }

  /**
   * Create a new copy-on-write array set with mapped elements of the provided array.
   *
   * @param aValues
   *        The array to copy elements from. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to apply to each element. May not be <code>null</code>.
   * @param <SRCTYPE>
   *        Source element type
   */
  public <SRCTYPE> CommonsCopyOnWriteArraySet (@Nullable final SRCTYPE [] aValues,
                                               @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  @Override
  @NonNull
  @ReturnsMutableCopy
  public <T> CommonsCopyOnWriteArraySet <T> createInstance ()
  {
    return new CommonsCopyOnWriteArraySet <> ();
  }

  /**
   * @return A mutable copy of this set. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public CommonsCopyOnWriteArraySet <ELEMENTTYPE> getClone ()
  {
    return new CommonsCopyOnWriteArraySet <> (this);
  }
}
