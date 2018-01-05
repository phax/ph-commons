/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.datetime.domain;

import javax.annotation.Nullable;

/**
 * Base interface for an object that has a start and an end.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        Date and time type
 */
public interface IHasStartAndEnd <DATATYPE>
{
  /**
   * @return The start. May be <code>null</code>.
   */
  @Nullable
  DATATYPE getStart ();

  /**
   * @return <code>true</code> if a start is present, <code>false</code> if not.
   * @since 9.0.0
   */
  default boolean hasStart ()
  {
    return getStart () != null;
  }

  /**
   * @return The end. May be <code>null</code>.
   */
  @Nullable
  DATATYPE getEnd ();

  /**
   * @return <code>true</code> if an end is present, <code>false</code> if not.
   * @since 9.0.0
   */
  default boolean hasEnd ()
  {
    return getEnd () != null;
  }
}
