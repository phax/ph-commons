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
   * Important: you may not invoke any modifying methods on the children owner
   * because otherwise you may get a ConcurrentModificationException.
   *
   * @param aConsumer
   *        The consumer to be invoked. May not be <code>null</code>.
   */
  void forAllChildren (@Nonnull Consumer <? super CHILDTYPE> aConsumer);
}
