/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.collections.triple;

import javax.annotation.Nullable;

/**
 * Represents an abstract triple (an object with exactly three element) that is
 * readonly.
 * 
 * @author Philip Helger
 * @param <DATA1TYPE>
 *        Type of the first element
 * @param <DATA2TYPE>
 *        Type of the second element
 * @param <DATA3TYPE>
 *        Type of the third element
 */
public interface IReadonlyTriple <DATA1TYPE, DATA2TYPE, DATA3TYPE>
{
  /**
   * @return The first element. May be <code>null</code>.
   */
  @Nullable
  DATA1TYPE getFirst ();

  /**
   * @return The second element. May be <code>null</code>.
   */
  @Nullable
  DATA2TYPE getSecond ();

  /**
   * @return The third element. May be <code>null</code>.
   */
  @Nullable
  DATA3TYPE getThird ();
}
