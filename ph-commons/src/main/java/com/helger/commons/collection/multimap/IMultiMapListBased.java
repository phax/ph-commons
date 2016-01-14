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
package com.helger.commons.collection.multimap;

import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.state.EChange;

/**
 * Interface for a multi map that uses a {@link List} for the storage.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <VALUETYPE>
 *        Value type
 */
public interface IMultiMapListBased <KEYTYPE, VALUETYPE> extends IMultiMap <KEYTYPE, VALUETYPE, List <VALUETYPE>>
{
  /**
   * Add a single value into the container identified by the passed key at the
   * specified index.
   *
   * @param aKey
   *        The key to use. May not be <code>null</code>.
   * @param aValue
   *        The value to be added. May be <code>null</code>.
   * @param nIndex
   *        The index to add the element to the list. Must be &ge; 0.
   * @return {@link EChange}
   */
  @Nonnull
  default EChange putSingle (@Nonnull final KEYTYPE aKey,
                             @Nullable final VALUETYPE aValue,
                             @Nonnegative final int nIndex)
  {
    getOrCreate (aKey).add (nIndex, aValue);
    return EChange.CHANGED;
  }
}
