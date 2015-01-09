/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.state.EChange;

/**
 * Represents an abstract triple (an object with exactly three element) that is
 * writable.
 * 
 * @author Philip Helger
 * @param <DATA1TYPE>
 *        Type of the first element
 * @param <DATA2TYPE>
 *        Type of the second element
 * @param <DATA3TYPE>
 *        Type of the third element
 */
public interface ITriple <DATA1TYPE, DATA2TYPE, DATA3TYPE> extends IReadonlyTriple <DATA1TYPE, DATA2TYPE, DATA3TYPE>
{
  /**
   * Set the first value.
   * 
   * @param aFirst
   *        The first value. May be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  EChange setFirst (@Nullable DATA1TYPE aFirst);

  /**
   * Set the second value.
   * 
   * @param aSecond
   *        The second value. May be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  EChange setSecond (@Nullable DATA2TYPE aSecond);

  /**
   * Set the third value.
   * 
   * @param aThird
   *        The third value. May be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  EChange setThird (@Nullable DATA3TYPE aThird);
}
