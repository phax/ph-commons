/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import com.helger.annotation.Nonnegative;

/**
 * Base interface for all objects having a certain size with long precision.
 *
 * @author Philip Helger
 * @since 11.1.5
 */
public interface IHasSizeLong
{
  /**
   * @return The number of contained elements. Always &ge; 0.
   */
  @Nonnegative
  long size ();

  /**
   * @return <code>true</code> if no items are present, <code>false</code> if at
   *         least a single item is present.
   * @see #size()
   * @see #isNotEmpty()
   */
  boolean isEmpty ();

  /**
   * @return <code>true</code> if at least one item is present,
   *         <code>false</code> if no item is present.
   * @see #size()
   * @see #isEmpty()
   */
  default boolean isNotEmpty ()
  {
    return !isEmpty ();
  }
}
