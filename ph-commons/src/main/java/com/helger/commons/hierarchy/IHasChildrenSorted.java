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

import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

/**
 * Extends {@link IHasChildren} by indicating that the child items are sorted!
 *
 * @author Philip Helger
 * @param <CHILDTYPE>
 *        The type of the children.
 */
public interface IHasChildrenSorted <CHILDTYPE> extends IHasChildren <CHILDTYPE>
{
  /**
   * @return A ordered list of child elements. May be <code>null</code> if no
   *         children are present.
   */
  @Nullable
  List <? extends CHILDTYPE> getAllChildren ();

  /**
   * Get the child node at the specified index
   *
   * @param nIndex
   *        The index to be queried. May not be &lt; 0 or &ge; the number of
   *        children
   * @return The child at the specified index or <code>null</code> if the index
   *         is invalid.
   * @throws IndexOutOfBoundsException
   *         in case the index is invalid
   */
  @Nullable
  CHILDTYPE getChildAtIndex (@Nonnegative int nIndex);

  /**
   * Get the first child node or <code>null</code> if no child is present
   *
   * @return The first child or <code>null</code>.
   */
  @Nullable
  default CHILDTYPE getFirstChild ()
  {
    return getChildAtIndex (0);
  }

  /**
   * Get the last child node or <code>null</code> if no child is present
   *
   * @return The last child or <code>null</code>.
   */
  @Nullable
  default CHILDTYPE getLastChild ()
  {
    return getChildAtIndex (getChildCount () - 1);
  }
}
