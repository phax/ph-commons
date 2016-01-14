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
package com.helger.commons.lang;

import javax.annotation.Nonnegative;

/**
 * Base interface for all components having a size. This may apply to files or
 * in-memory data structures but not to collections (list, set, map, ...).
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IHasByteSize
{
  /**
   * @return The number of bytes. The values must be &ge; 0.
   */
  @Nonnegative
  long getSizeInBytes ();
}
