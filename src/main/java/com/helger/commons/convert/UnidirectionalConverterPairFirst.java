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
package com.helger.commons.convert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collections.pair.IReadonlyPair;

/**
 * A unidirectional converter that extracts the first element from an
 * {@link IReadonlyPair}.
 * 
 * @author Philip Helger
 */
public final class UnidirectionalConverterPairFirst <DATA1TYPE, DATA2TYPE> implements
                                                                           IUnidirectionalConverter <IReadonlyPair <DATA1TYPE, DATA2TYPE>, DATA1TYPE>
{
  @Nullable
  public DATA1TYPE convert (@Nullable final IReadonlyPair <DATA1TYPE, DATA2TYPE> aPair)
  {
    return aPair == null ? null : aPair.getFirst ();
  }

  /**
   * Get a generic data converter that extracts the first element out of a pair.
   * 
   * @param <FIRST>
   *        First type of the pair
   * @param <SECOND>
   *        Second type of the pair.
   * @return The data converter capable of extracting the first item out of a
   *         pair.
   */
  @Nonnull
  public static <FIRST, SECOND> UnidirectionalConverterPairFirst <FIRST, SECOND> create ()
  {
    return new UnidirectionalConverterPairFirst <FIRST, SECOND> ();
  }
}
