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
package com.helger.json;

import java.util.function.ObjIntConsumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsIterable;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.state.EChange;
import com.helger.commons.traits.IGenericAdderTrait;
import com.helger.commons.traits.IGetterByIndexTrait;
import com.helger.commons.traits.IPrimitiveConverterTo;

/**
 * JSON array.
 *
 * @author Philip Helger
 */
public interface IJsonArray extends
                            IJsonCollection,
                            ICommonsIterable <IJson>,
                            IGetterByIndexTrait,
                            IGenericAdderTrait <IJson, IJsonArray>
{
  @Nonnull
  default IPrimitiveConverterTo <IJson> getPrimitiveConverterTo ()
  {
    return PrimitiveConvererToIJson.INSTANCE;
  }

  @Nullable
  IJson removeAndReturnAtIndex (@Nonnegative int nIndex);

  @Nonnull
  EChange removeAtIndex (@Nonnegative int nIndex);

  /**
   * Get the element at the specified index.
   *
   * @param nIndex
   *        The index to retrieve.
   * @return <code>null</code> if the index is invalid.
   */
  @Nullable
  IJson get (@Nonnegative int nIndex);

  /**
   * Get the element at the specified index. This is the {@link IJsonValue}
   * specific version of {@link #get(int)}.
   *
   * @param nIndex
   *        The index to retrieve.
   * @return <code>null</code> if the index is invalid or if the value is not a
   *         {@link IJsonValue}.
   */
  @Nullable
  default IJsonValue getValueAtIndex (@Nonnegative final int nIndex)
  {
    final IJson aJson = get (nIndex);
    return aJson != null && aJson.isValue () ? (IJsonValue) aJson : null;
  }

  /**
   * Get the element at the specified index. This is the {@link IJsonArray}
   * specific version of {@link #get(int)}.
   *
   * @param nIndex
   *        The index to retrieve.
   * @return <code>null</code> if the index is invalid or if the value is not a
   *         {@link IJsonArray}.
   */
  @Nullable
  default IJsonArray getArrayAtIndex (@Nonnegative final int nIndex)
  {
    final IJson aJson = get (nIndex);
    return aJson != null && aJson.isArray () ? (IJsonArray) aJson : null;
  }

  /**
   * Get the element at the specified index. This is the {@link IJsonObject}
   * specific version of {@link #get(int)}.
   *
   * @param nIndex
   *        The index to retrieve.
   * @return <code>null</code> if the index is invalid or if the value is not a
   *         {@link IJsonObject}.
   */
  @Nullable
  default IJsonObject getObjectAtIndex (@Nonnegative final int nIndex)
  {
    final IJson aJson = get (nIndex);
    return aJson != null && aJson.isObject () ? (IJsonObject) aJson : null;
  }

  /**
   * Get the plain Object value of the element at the specified index. If the
   * element at the specified index is not a Json value, <code>null</code> is
   * returned.
   *
   * @param nIndex
   *        The index to retrieve.
   * @return <code>null</code> if the index is invalid or if the value is not a
   *         {@link IJsonValue}.
   */
  @Nullable
  default Object getValue (@Nonnegative final int nIndex)
  {
    final IJson aJson = get (nIndex);
    return aJson != null && aJson.isValue () ? aJson.getAsValue ().getValue () : null;
  }

  /**
   * Get a sub array of this array from the specified start index (incl.) up to
   * the specified end index (excl.).
   *
   * @param nStartIndex
   *        The start index. Must be &ge; 0.
   * @param nEndIndex
   *        The end index. Must be &ge; start index.
   * @return A non-<code>null</code> JSON array.
   */
  @Nonnull
  @ReturnsMutableCopy
  IJsonArray getSubArray (@Nonnegative int nStartIndex, @Nonnegative int nEndIndex);

  /**
   * @return A copy of all contained items. Never <code>null</code> but maybe
   *         empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsList <IJson> getAll ();

  /**
   * Invoke the passed consumer on all entries of this array.
   *
   * @param aConsumer
   *        Consumer with the first param being the value and second param being
   *        the 0-based index.
   */
  @Override
  void forEachByIndex (@Nonnull ObjIntConsumer <? super IJson> aConsumer);

  /**
   * Check if the passed value is directly contained in the array or not.
   *
   * @param aValue
   *        The value to be checked for containment. May be <code>null</code>.
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
   */
  boolean contains (@Nullable IJson aValue);

  /**
   * Check if the passed value is directly contained in the array or not.
   *
   * @param aValue
   *        The value to be checked for containment. May be <code>null</code>.
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
   */
  default boolean contains (@Nullable final Object aValue)
  {
    return contains (JsonValue.create (aValue));
  }

  /**
   * Check if the passed value is directly contained in the array or not.
   *
   * @param bValue
   *        The value to be checked for containment.
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
   */
  default boolean contains (final boolean bValue)
  {
    return contains (JsonValue.create (bValue));
  }

  /**
   * Check if the passed value is directly contained in the array or not.
   *
   * @param nValue
   *        The value to be checked for containment.
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
   */
  default boolean contains (final byte nValue)
  {
    return contains (JsonValue.create (nValue));
  }

  /**
   * Check if the passed value is directly contained in the array or not.
   *
   * @param cValue
   *        The value to be checked for containment.
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
   */
  default boolean contains (final char cValue)
  {
    return contains (JsonValue.create (cValue));
  }

  /**
   * Check if the passed value is directly contained in the array or not.
   *
   * @param dValue
   *        The value to be checked for containment.
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
   */
  default boolean contains (final double dValue)
  {
    return contains (JsonValue.create (dValue));
  }

  /**
   * Check if the passed value is directly contained in the array or not.
   *
   * @param fValue
   *        The value to be checked for containment.
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
   */
  default boolean contains (final float fValue)
  {
    return contains (JsonValue.create (fValue));
  }

  /**
   * Check if the passed value is directly contained in the array or not.
   *
   * @param nValue
   *        The value to be checked for containment.
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
   */
  default boolean contains (final int nValue)
  {
    return contains (JsonValue.create (nValue));
  }

  /**
   * Check if the passed value is directly contained in the array or not.
   *
   * @param nValue
   *        The value to be checked for containment.
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
   */
  default boolean contains (final long nValue)
  {
    return contains (JsonValue.create (nValue));
  }

  /**
   * Check if the passed value is directly contained in the array or not.
   *
   * @param nValue
   *        The value to be checked for containment.
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
   */
  default boolean contains (final short nValue)
  {
    return contains (JsonValue.create (nValue));
  }

  /**
   * @return A list of all cloned values contained in this array. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsList <IJson> getClonedValues ();

  @Nonnull
  IJsonArray getClone ();
}
