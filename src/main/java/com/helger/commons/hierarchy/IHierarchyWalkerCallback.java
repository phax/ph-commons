/**
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



/**
 * Interface for walking a hierarchy without the possibilities to alter they way
 * how the hierarchy is iterated.
 * 
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of object to be iterated.
 */
public interface IHierarchyWalkerCallback <DATATYPE> extends IBaseHierarchyWalker
{
  /**
   * Called before eventual children of the current item are iterated.
   * 
   * @param aItem
   *        The current tree item. May be <code>null</code>.
   */
  void onItemBeforeChildren (DATATYPE aItem);

  /**
   * Called after eventual children of the current item were iterated.
   * 
   * @param aItem
   *        The current tree item. May be <code>null</code>.
   */
  void onItemAfterChildren (DATATYPE aItem);
}
