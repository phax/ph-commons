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
 * This interface can be used to generically resolved children of a certain
 * object in a sorted way.
 *
 * @author Philip Helger
 * @param <CHILDTYPE>
 *        The type of the children to retrieve.
 */
public interface IChildrenProviderSorted <CHILDTYPE> extends IChildrenProvider <CHILDTYPE>
{
  /**
   * Get the children of the passed object.
   *
   * @param aCurrent
   *        The object to determine the children of. May be <code>null</code>
   *        depending on the concrete implementation.
   * @return The child objects, or <code>null</code> if there are no children.
   *         If <code>null</code> is passed, the resolver is expected to return
   *         any possible top level (root) elements. This method may NOT return
   *         <code>null</code> if the call to {@link #hasChildren(Object)} with
   *         the same object returned <code>true</code>.
   */
  @Nullable
  List <? extends CHILDTYPE> getAllChildren (@Nullable CHILDTYPE aCurrent);

  /**
   * Get the child of the passed object at the given index. It is assumed that
   * objects are accessed in exactly the same order as they are delivered by
   * {@link #getAllChildren(Object)}.
   *
   * @param aCurrent
   *        The object to get the child of. May be <code>null</code> depending
   *        on the concrete implementation.
   * @param nIndex
   *        The index to retrieve the child at.
   * @return The child at the specified index. May not be &lt; 0.
   * @throws IndexOutOfBoundsException
   *         in case the specified index is invalid.
   */
  @Nullable
  CHILDTYPE getChildAtIndex (@Nullable CHILDTYPE aCurrent, @Nonnegative int nIndex);
}
