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

import javax.annotation.Nullable;

/**
 * Visitor interface
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to be visited
 */
public interface IHierarchyVisitor <DATATYPE>
{
  /**
   * Visit all objects, starting specified one.
   *
   * @param aStartObject
   *        The object to start from. May be <code>null</code> to indicate the
   *        hierarchy root.
   * @param bInvokeOnStartObject
   *        <code>true</code> to indicate that the callback should also be
   *        invoked on the first element, <code>false</code> to indicate that
   *        the callback should only be invoked for the children of the passed
   *        element.
   */
  void visit (@Nullable DATATYPE aStartObject, boolean bInvokeOnStartObject);
}
