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

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.state.EChange;

/**
 * Base interface for a multi map (one key with several values).
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <VALUETYPE>
 *        Element type
 * @param <COLLTYPE>
 *        Container type containing value types
 */
public interface IMultiMap <KEYTYPE, VALUETYPE, COLLTYPE extends Collection <VALUETYPE>> extends Map <KEYTYPE, COLLTYPE>
{
  /**
   * Get or create the collection of the specified key.
   *
   * @param aKey
   *        The key to use. May not be <code>null</code>.
   * @return The mutable collection to be used. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableObject ("design")
  COLLTYPE getOrCreate (@Nonnull KEYTYPE aKey);

  /**
   * Add a single value into the container identified by the passed key.
   *
   * @param aKey
   *        The key to use. May not be <code>null</code>.
   * @param aValue
   *        The value to be added. May be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  default EChange putSingle (@Nonnull final KEYTYPE aKey, @Nullable final VALUETYPE aValue)
  {
    return EChange.valueOf (getOrCreate (aKey).add (aValue));
  }

  /**
   * Add all values into the container identified by the passed key-value-map.
   *
   * @param aMap
   *        The key-value-map to use. May not be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  default EChange putAllIn (@Nonnull final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    EChange eChange = EChange.UNCHANGED;
    for (final Map.Entry <? extends KEYTYPE, ? extends VALUETYPE> aEntry : aMap.entrySet ())
      eChange = eChange.or (putSingle (aEntry.getKey (), aEntry.getValue ()));
    return eChange;
  }

  /**
   * Remove a single element from the container identified by the passed key.
   *
   * @param aKey
   *        The key to use. May not be <code>null</code>.
   * @param aValue
   *        The value to be removed. May be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  default EChange removeSingle (@Nonnull final KEYTYPE aKey, @Nullable final VALUETYPE aValue)
  {
    final COLLTYPE aCont = get (aKey);
    return aCont == null ? EChange.UNCHANGED : EChange.valueOf (aCont.remove (aValue));
  }

  /**
   * Check a single element from the container identified by the passed key is
   * present.
   *
   * @param aKey
   *        The key to use. May not be <code>null</code>.
   * @param aValue
   *        The value to be checked. May be <code>null</code>.
   * @return <code>true</code> if the value is contained, <code>false</code>
   *         otherwise
   */
  default boolean containsSingle (@Nonnull final KEYTYPE aKey, @Nullable final VALUETYPE aValue)
  {
    final COLLTYPE aCont = get (aKey);
    return aCont != null && aCont.contains (aValue);
  }

  /**
   * @return The total number of contained values recursively over all contained
   *         maps. Always &ge; 0.
   */
  @Nonnegative
  default long getTotalValueCount ()
  {
    long ret = 0;
    for (final Collection <?> aChild : values ())
      ret += aChild.size ();
    return ret;
  }
}
