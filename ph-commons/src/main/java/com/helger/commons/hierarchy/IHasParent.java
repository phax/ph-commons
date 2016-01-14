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
 * This helper interface is required to build a hierarchy of objects.
 *
 * @author Philip
 * @param <PARENTTYPE>
 *        The type of the parent objects
 */
@FunctionalInterface
public interface IHasParent <PARENTTYPE>
{
  /**
   * Get the parent object of this object.
   *
   * @return The parent object or <code>null</code> if this object has no
   *         parent.
   */
  @Nullable
  PARENTTYPE getParent ();
}
