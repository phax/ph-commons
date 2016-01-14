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
 * @param <KEYTYPE1>
 *        Key type of outer map
 * @param <KEYTYPE2>
 *        Key type of inner map
 * @param <VALUETYPE>
 *        Element type
 */
public interface IMultiMapMapBased <KEYTYPE1, KEYTYPE2, VALUETYPE> extends Map <KEYTYPE1, Map <KEYTYPE2, VALUETYPE>>
{
  /**
   * Get or create the collection of the specified key.
   *
   * @param aKey
   *        The key to use. May not be <code>null</code>.
   * @return The mutable map that can be modified later on. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableObject ("design")
  Map <KEYTYPE2, VALUETYPE> getOrCreate (@Nonnull KEYTYPE1 aKey);

  /**
   * Add a single value into the container identified by the passed key.
   *
   * @param aKey
   *        The key to use. May not be <code>null</code>.
   * @param aInnerKey
   *        The key for the inner map to use. May not be <code>null</code>.
   * @param aValue
   *        The value to be added. May be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  default EChange putSingle (@Nonnull final KEYTYPE1 aKey,
                             @Nonnull final KEYTYPE2 aInnerKey,
                             @Nullable final VALUETYPE aValue)
  {
    return EChange.valueOf (getOrCreate (aKey).put (aInnerKey, aValue) != null);
  }

  /**
   * Add all values into the container identified by the passed key-value-map.
   *
   * @param aMap
   *        The key-value-map to use. May not be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  default EChange putAllIn (@Nonnull final Map <? extends KEYTYPE1, ? extends Map <KEYTYPE2, VALUETYPE>> aMap)
  {
    EChange eChange = EChange.UNCHANGED;
    for (final Map.Entry <? extends KEYTYPE1, ? extends Map <KEYTYPE2, VALUETYPE>> aEntry : aMap.entrySet ())
      for (final Map.Entry <KEYTYPE2, VALUETYPE> aEntry2 : aEntry.getValue ().entrySet ())
        eChange = eChange.or (putSingle (aEntry.getKey (), aEntry2.getKey (), aEntry2.getValue ()));
    return eChange;
  }

  /**
   * Remove a single element from the container identified by the passed key.
   *
   * @param aKey
   *        The key to use. May not be <code>null</code>.
   * @param aInnerKey
   *        The key for the inner map to be removed. May be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  default EChange removeSingle (@Nonnull final KEYTYPE1 aKey, @Nonnull final KEYTYPE2 aInnerKey)
  {
    final Map <KEYTYPE2, VALUETYPE> aCont = get (aKey);
    return aCont == null ? EChange.UNCHANGED : EChange.valueOf (aCont.remove (aInnerKey) != null);
  }

  /**
   * Get a single value from the container identified by the passed keys.
   *
   * @param aKey
   *        The key to use. May not be <code>null</code>.
   * @param aInnerKey
   *        The key for the inner map to use. May not be <code>null</code>.
   * @return <code>null</code> if no such value exists.
   */
  @Nullable
  default VALUETYPE getSingle (@Nonnull final KEYTYPE1 aKey, @Nonnull final KEYTYPE2 aInnerKey)
  {
    final Map <KEYTYPE2, VALUETYPE> aCont = get (aKey);
    return aCont == null ? null : aCont.get (aInnerKey);
  }

  /**
   * Check a single element from the container identified by the passed keys is
   * present.
   *
   * @param aKey
   *        The key to use. May not be <code>null</code>.
   * @param aInnerKey
   *        The key of the inner map to be checked. May be <code>null</code>.
   * @return <code>true</code> if contained, <code>false</code> otherwise.
   */
  default boolean containsSingle (@Nonnull final KEYTYPE1 aKey, @Nonnull final KEYTYPE2 aInnerKey)
  {
    final Map <KEYTYPE2, VALUETYPE> aCont = get (aKey);
    return aCont != null && aCont.containsKey (aInnerKey);
  }

  /**
   * @return The total number of contained values recursively over all contained
   *         maps. Always &ge; 0.
   */
  @Nonnegative
  default long getTotalValueCount ()
  {
    long ret = 0;
    for (final Map <?, ?> aChild : values ())
      ret += aChild.size ();
    return ret;
  }
}
