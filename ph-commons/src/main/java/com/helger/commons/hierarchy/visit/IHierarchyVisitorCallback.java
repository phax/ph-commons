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
package com.helger.commons.hierarchy.visit;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.callback.ICallback;

/**
 * Base interface with callbacks for visiting a hierarchy.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of object to be iterated.
 */
public interface IHierarchyVisitorCallback <DATATYPE> extends ICallback
{
  /**
   * Called before the tree walking starts.
   */
  default void begin ()
  {}

  /**
   * @return The level of the current node within the hierarchy. Always &ge; 0.
   *         The root item has level 0.
   */
  @Nonnegative
  int getLevel ();

  /**
   * Called before the tree walker descends into the next tree level. After this
   * call {@link #getLevel()} should return a value increased by 1.
   */
  default void onLevelDown ()
  {}

  /**
   * Called after the tree walker ascends into the previous tree level. After
   * this call {@link #getLevel()} should return a value decreased by 1.
   */
  default void onLevelUp ()
  {}

  /**
   * Called before children of the current item are visited. This method is also
   * to be called if no children are present at all.
   *
   * @param aItem
   *        The current item. May be <code>null</code>.
   * @return A non-<code>null</code> status code that determines how to continue
   *         iteration.
   */
  @Nonnull
  EHierarchyVisitorReturn onItemBeforeChildren (DATATYPE aItem);

  /**
   * Called after eventual children of the current item were visited. This
   * method is also to be called if no children are present at all. This method
   * has always to be called if {@link #onItemBeforeChildren(Object)} was
   * called.
   *
   * @param aItem
   *        The current item. May be <code>null</code>.
   * @return A non-<code>null</code> status code that determines how to continue
   *         iteration.
   */
  @Nonnull
  EHierarchyVisitorReturn onItemAfterChildren (DATATYPE aItem);

  /**
   * Called after the tree walking ended.
   */
  default void end ()
  {}
}
