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
package com.helger.collection.stack;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.array.ArrayHelper;
import com.helger.collection.CollectionFind;
import com.helger.collection.CollectionHelper;

/**
 * Helper class for creating and populating {@link NonBlockingStack} instances
 * from various sources.
 *
 * @author Philip Helger
 */
@Immutable
public final class StackHelper
{
  private StackHelper ()
  {}

  /**
   * Create a new empty stack with the given initial capacity.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param nInitialCapacity
   *        The initial capacity. Must be &ge; 0.
   * @return A new empty {@link NonBlockingStack}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> NonBlockingStack <ELEMENTTYPE> newStack (@Nonnegative final int nInitialCapacity)
  {
    return new NonBlockingStack <> (nInitialCapacity);
  }

  /** @see #newStack(int) */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> NonBlockingStack <ELEMENTTYPE> newStack ()
  {
    return new NonBlockingStack <> ();
  }

  /**
   * Create a new stack by mapping elements from the given collection.
   *
   * @param <SRCTYPE>
   *        The source element type.
   * @param <DSTTYPE>
   *        The destination element type.
   * @param aCollection
   *        The source collection. May be <code>null</code>.
   * @param aMapper
   *        The mapping function. May not be <code>null</code>.
   * @return A new {@link NonBlockingStack}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> NonBlockingStack <DSTTYPE> newStackMapped (@Nullable final Collection <? extends SRCTYPE> aCollection,
                                                                              @NonNull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (CollectionHelper.isEmpty (aCollection))
      return newStack (0);
    final NonBlockingStack <DSTTYPE> ret = newStack (aCollection.size ());
    for (final SRCTYPE aValue : aCollection)
      ret.push (aMapper.apply (aValue));
    return ret;
  }

  /** @see #newStackMapped(Collection, Function) */
  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> NonBlockingStack <DSTTYPE> newStackMapped (@Nullable final SRCTYPE [] aArray,
                                                                              @NonNull final Function <? super SRCTYPE, DSTTYPE> aMapper)
  {
    if (ArrayHelper.isEmpty (aArray))
      return newStack (0);
    final NonBlockingStack <DSTTYPE> ret = newStack (aArray.length);
    for (final SRCTYPE aValue : aArray)
      ret.push (aMapper.apply (aValue));
    return ret;
  }

  /**
   * Create a new stack with a single element.
   *
   * @param <ELEMENTTYPE>
   *        The type of elements contained in the stack.
   * @param aValue
   *        The value to push. Maybe <code>null</code>.
   * @return A non-<code>null</code> stack.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> NonBlockingStack <ELEMENTTYPE> newStack (@Nullable final ELEMENTTYPE aValue)
  {
    final NonBlockingStack <ELEMENTTYPE> ret = newStack ();
    ret.push (aValue);
    return ret;
  }

  /**
   * Create a new stack from the given array.
   *
   * @param <ELEMENTTYPE>
   *        The type of elements contained in the stack.
   * @param aValues
   *        The values that are to be pushed on the stack. The last element will be the top element
   *        on the stack. May not be <code>null</code> .
   * @return A non-<code>null</code> stack object.
   */
  @NonNull
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> NonBlockingStack <ELEMENTTYPE> newStack (@Nullable final ELEMENTTYPE... aValues)
  {
    return new NonBlockingStack <> (aValues);
  }

  /**
   * Create a new stack from the given collection, applying a filter.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param aCollection
   *        The source collection. May be <code>null</code>.
   * @param aFilter
   *        The filter to apply. May not be <code>null</code>.
   * @return A new {@link NonBlockingStack}. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> NonBlockingStack <ELEMENTTYPE> newStack (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                                       @NonNull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (CollectionHelper.isEmpty (aCollection))
      return newStack (0);
    final NonBlockingStack <ELEMENTTYPE> ret = newStack (aCollection.size ());
    CollectionFind.findAll (aCollection, aFilter, ret::add);
    return ret;
  }

  /**
   * Create a new stack from the given collection.
   *
   * @param <ELEMENTTYPE>
   *        The type of elements contained in the stack.
   * @param aValues
   *        The values that are to be pushed on the stack. The last element will be the top element
   *        on the stack. May not be <code>null</code> .
   * @return A non-<code>null</code> stack object.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> NonBlockingStack <ELEMENTTYPE> newStack (@Nullable final Collection <? extends ELEMENTTYPE> aValues)
  {
    return new NonBlockingStack <> (aValues);
  }

  /**
   * Get a copy of the given stack without the top element.
   *
   * @param <ELEMENTTYPE>
   *        The element type.
   * @param aStack
   *        The source stack. May be <code>null</code>.
   * @return A copy of the stack without the top element, or <code>null</code> if the passed stack
   *         is empty or <code>null</code>.
   */
  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> NonBlockingStack <ELEMENTTYPE> getStackCopyWithoutTop (@Nullable final NonBlockingStack <ELEMENTTYPE> aStack)
  {
    if (CollectionHelper.isEmpty (aStack))
      return null;

    final NonBlockingStack <ELEMENTTYPE> ret = new NonBlockingStack <> (aStack);
    ret.pop ();
    return ret;
  }
}
