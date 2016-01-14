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

import javax.annotation.Nullable;

/**
 * This helper interface is required to build a generic tree. This is required
 * because the used interfaces do not provide a base interface implementing a
 * getParent method.
 *
 * @author Philip Helger
 * @param <PARENTTYPE>
 *        The type of object to get a parent from
 */
@FunctionalInterface
public interface IParentProvider <PARENTTYPE>
{
  /**
   * Get the parent of the passed object.
   *
   * @param aCurrent
   *        The object to determine the parent of. No <code>null</code> or non-
   *        <code>null</code> constraint possible.
   * @return The parent object, or <code>null</code> if the object has no
   *         parent.
   */
  @Nullable
  PARENTTYPE getParent (PARENTTYPE aCurrent);
}
