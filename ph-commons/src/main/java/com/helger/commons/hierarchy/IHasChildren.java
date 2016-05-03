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
package com.helger.commons.hierarchy;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.ext.ICommonsCollection;

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
   *         .
   */
  @Nullable
  ICommonsCollection <? extends CHILDTYPE> getAllChildren ();

  /**
   * Perform something on all children (if any).<br>
   * Note: use this only for reading. Writing operations will potentially cause
   * concurrent modification exceptions!
   *
   * @param aConsumer
   *        The consumer to be invoked. May not be <code>null</code>.
   */
  void forAllChildren (@Nonnull Consumer <? super CHILDTYPE> aConsumer);

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
  void forAllChildren (@Nonnull Predicate <? super CHILDTYPE> aFilter, @Nonnull Consumer <? super CHILDTYPE> aConsumer);

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
  <DSTTYPE> void forAllChildrenMapped (@Nonnull final Predicate <? super CHILDTYPE> aFilter,
                                       @Nonnull final Function <? super CHILDTYPE, ? extends DSTTYPE> aMapper,
                                       @Nonnull final Consumer <? super DSTTYPE> aConsumer);
}
