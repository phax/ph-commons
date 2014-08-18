/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnegative;

/**
 * Base interface for iterating a hierarchy.
 * 
 * @author Philip Helger
 */
public interface IBaseHierarchyWalker
{
  /**
   * Called before the tree walking starts.
   */
  void begin ();

  /**
   * Called before the tree walker descends into the next tree level.
   */
  void onLevelDown ();

  /**
   * @return The level of the current node within the tree. The root item has
   *         level 0.
   */
  @Nonnegative
  int getLevel ();

  /**
   * Called after the tree walker ascends into the previous tree level.
   */
  void onLevelUp ();

  /**
   * Called after the tree walking ended.
   */
  void end ();
}
