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
package com.helger.collection.pair;

import java.io.Serializable;

/**
 * Represents a basic read-only pair.
 *
 * @author Philip Helger
 * @param <DATA1TYPE>
 *        First type.
 * @param <DATA2TYPE>
 *        Second type.
 */
public interface IPair <DATA1TYPE, DATA2TYPE> extends Serializable
{
  /**
   * @return The first element. May be <code>null</code> depending on the
   *         implementation.
   */
  DATA1TYPE getFirst ();

  /**
   * @return <code>true</code> if {@link #getFirst()} != <code>null</code>
   */
  default boolean hasFirst ()
  {
    return getFirst () != null;
  }

  /**
   * @return The second element. May be <code>null</code> depending on the
   *         implementation.
   */
  DATA2TYPE getSecond ();

  /**
   * @return <code>true</code> if {@link #getSecond()} != <code>null</code>
   */
  default boolean hasSecond ()
  {
    return getSecond () != null;
  }
}
