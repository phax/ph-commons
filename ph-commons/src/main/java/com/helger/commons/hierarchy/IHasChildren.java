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
package com.helger.commons.hierarchy;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsCollection;
import com.helger.commons.collection.impl.ICommonsIterable;
import com.helger.commons.state.EContinue;

/**
 * A simple interface, indicating that an item has direct children.
 *
 * @author Philip Helger
 * @param <CHILDTYPE>
 *        The type of the children.
 */
public interface IHasChildren <CHILDTYPE>
{
  /**
   * @return <code>true</code> if this item has direct children,
   *         <code>false</code> otherwise.
   */
  default boolean hasChildren ()
  {
    return getChildCount () > 0;
  }

  /**
   * @return <code>true</code> if this item has no direct children,
   *         <code>false</code> otherwise.
   */
  default boolean hasNoChildren ()
  {
    return !hasChildren ();
  }

  /**
   * @return The number of contained direct children. Always &ge; 0.
   */
  @Nonnegative
  int getChildCount ();

  /**
   * @return A collection of all direct child elements. May be <code>null</code>
   *         if no children are contained. This method should always return a
   *         copy of the collection to avoid
   *         {@link java.util.ConcurrentModificationException} when modifying
   *         it.
   * @see #hasChildren()
   * @see #getChildren()
   */
  @Nullable
  @ReturnsMutableCopy
  ICommonsCollection <? extends CHILDTYPE> getAllChildren ();

  /**
   * @return An iterable over all direct children. May be <code>null</code> if
   *         no children are contained. Compared to {@link #getAllChildren()}
   *         this method is not supposed to create a copy of the underlying
   *         container but instead return the iterable only. Be careful when
   *         using this method to modify a collection - it will lead to a
   *         {@link java.util.ConcurrentModificationException}.
   * @see #hasChildren()
   * @see #getChildren()
   * @since 9.0.0
   */
  @Nullable
  ICommonsIterable <? extends CHILDTYPE> getChildren ();

  /**
   * Perform something on all children (if any).<br>
   * Note: use this only for reading. Writing operations will potentially cause
   * concurrent modification exceptions!
   *
   * @param aConsumer
   *        The consumer to be invoked. May not be <code>null</code>.
   */
  default void forAllChildren (@Nonnull final Consumer <? super CHILDTYPE> aConsumer)
  {
    if (hasChildren ())
      getChildren ().forEach (aConsumer);
  }

  /**
   * Perform something on all children (if any).<br>
   * Note: use this only for reading. Writing operations will potentially cause
   * concurrent modification exceptions!
   *
   * @param aConsumer
   *        The breakable consumer to be invoked. May not be <code>null</code>.
   * @return {@link EContinue#BREAK} if iteration was stopped,
   *         {@link EContinue#CONTINUE} otherwise.
   */
  @Nonnull
  default EContinue forAllChildrenBreakable (@Nonnull final Function <? super CHILDTYPE, EContinue> aConsumer)
  {
    if (hasChildren ())
      return getChildren ().forEachBreakable (aConsumer);
    return EContinue.CONTINUE;
  }

  /**
   * Iterate all direct children (if at least one is present) and invoke the
   * provided consumer if the passed predicate is fulfilled.<br>
   * Note: use this only for reading. Writing operations will potentially cause
   * concurrent modification exceptions!
   *
   * @param aFilter
   *        The filter that is applied to all children. May not be
   *        <code>null</code>.
   * @param aConsumer
   *        The consumer to be invoked for all children matching the filter. May
   *        not be <code>null</code>.
   */
  default void forAllChildren (@Nonnull final Predicate <? super CHILDTYPE> aFilter,
                               @Nonnull final Consumer <? super CHILDTYPE> aConsumer)
  {
    if (hasChildren ())
      getChildren ().findAll (aFilter, aConsumer);
  }

  /**
   * Iterate all direct children (if at least one is present) and invoked the
   * provided consumer if the passed predicate is fulfilled.<br>
   * Note: use this only for reading. Writing operations will potentially cause
   * concurrent modification exceptions!
   *
   * @param aFilter
   *        The filter that is applied to all children. May not be
   *        <code>null</code>.
   * @param aMapper
   *        The mapping function from child type to the target type. May not be
   *        <code>null</code>.
   * @param aConsumer
   *        The consumer to be invoked for all target types matching the filter.
   *        May not be <code>null</code>.
   * @param <DSTTYPE>
   *        The destination data type.
   */
  default <DSTTYPE> void forAllChildrenMapped (@Nonnull final Predicate <? super CHILDTYPE> aFilter,
                                               @Nonnull final Function <? super CHILDTYPE, ? extends DSTTYPE> aMapper,
                                               @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    if (hasChildren ())
      getChildren ().findAllMapped (aFilter, aMapper, aConsumer);
  }
}
