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

/**
 * Return value for hierarchy iteration.
 *
 * @author Philip Helger
 */
public enum EHierarchyVisitorReturn
{
  /**
   * Continue with the next element. This may either be the first child element
   * or the next sibling.
   */
  CONTINUE,

  /**
   * Skip the child elements of the current element and go to the next sibling.
   * This can only be returned before children are iterated.<br>
   * Important: this enum constant is never explicitly queried but it IS used!
   */
  USE_NEXT_SIBLING,

  /**
   * Skip the child elements and all siblings of the current content element and
   * go to the parent element's sibling.
   */
  USE_PARENTS_NEXT_SIBLING,

  /**
   * Stop the iteration completely and immediately.
   */
  STOP_ITERATION;

  public boolean isContinue ()
  {
    return this == CONTINUE;
  }
}
