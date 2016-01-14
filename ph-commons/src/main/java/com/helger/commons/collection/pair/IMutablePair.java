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
package com.helger.commons.collection.pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.state.EChange;

/**
 * Interface for a writable pair.
 *
 * @author Philip Helger
 * @param <DATA1TYPE>
 *        First type.
 * @param <DATA2TYPE>
 *        Second type.
 */
public interface IMutablePair <DATA1TYPE, DATA2TYPE> extends IPair <DATA1TYPE, DATA2TYPE>
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
}
