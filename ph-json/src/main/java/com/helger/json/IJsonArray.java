/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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

import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.ICommonsIterable;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.state.EChange;
import com.helger.commons.traits.IGetterByIndexTrait;
import com.helger.json.convert.JsonConverter;

/**
 * JSON array.
 *
 * @author Philip Helger
 */
public interface IJsonArray extends IJsonCollection, ICommonsIterable <IJson>, IGetterByIndexTrait
{
  @Nonnull
  IJsonArray add (@Nonnull IJson aValue);

  @Nonnull
  default IJsonArray addIf (@Nonnull final IJson aValue, @Nonnull final Predicate <? super IJson> aFilter)
  {
    if (aFilter.test (aValue))
      add (aValue);
    return this;
  }

  /**
   * Add using the JSON converter
   *
   * @param aValue
   *        The value to be added. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  default IJsonArray add (@Nullable final Object aValue)
  {
    final IJson aJson = JsonConverter.convertToJson (aValue);
    return add (aJson);
  }

  @Nonnull
  default IJsonArray addIfNotNull (@Nullable final Object aValue)
  {
    if (aValue != null)
      add (aValue);
    return this;
  }

  @Nonnull
  default IJsonArray addIf (@Nullable final Object aValue, @Nonnull final Predicate <? super Object> aFilter)
  {
    if (aFilter.test (aValue))
      add (aValue);
    return this;
  }

  @Nonnull
  default IJsonArray add (final boolean bValue)
  {
    return add (JsonValue.create (bValue));
  }

  @Nonnull
  default IJsonArray add (final byte nValue)
  {
    return add (JsonValue.create (nValue));
  }

  @Nonnull
  default IJsonArray add (final char cValue)
  {
    return add (JsonValue.create (cValue));
  }

  @Nonnull
  default IJsonArray add (final double dValue)
  {
    return add (JsonValue.create (dValue));
  }

  @Nonnull
  default IJsonArray add (final float fValue)
  {
    return add (JsonValue.create (fValue));
  }

  @Nonnull
  default IJsonArray add (final int nValue)
  {
    return add (JsonValue.create (nValue));
  }

  @Nonnull
  default IJsonArray add (final long nValue)
  {
    return add (JsonValue.create (nValue));
  }

  @Nonnull
  default IJsonArray add (final short nValue)
  {
    return add (JsonValue.create (nValue));
  }

  @Nonnull
  IJsonArray add (@Nonnegative int nIndex, @Nonnull IJson aValue);

  /**
   * Add at the specified index using the JSON converter
   *
   * @param nIndex
   *        The index where the item should be added. Must be &ge; 0.
   * @param aValue
   *        The value to be added. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  default IJsonArray add (@Nonnegative final int nIndex, @Nullable final Object aValue)
  {
    final IJson aJson = JsonConverter.convertToJson (aValue);
    return add (nIndex, aJson);
  }

  @Nonnull
  default IJsonArray add (@Nonnegative final int nIndex, final boolean bValue)
  {
    return add (nIndex, JsonValue.create (bValue));
  }

  @Nonnull
  default IJsonArray add (@Nonnegative final int nIndex, final byte nValue)
  {
    return add (nIndex, JsonValue.create (nValue));
  }

  @Nonnull
  default IJsonArray add (@Nonnegative final int nIndex, final char cValue)
  {
    return add (nIndex, JsonValue.create (cValue));
  }

  @Nonnull
  default IJsonArray add (@Nonnegative final int nIndex, final double dValue)
  {
    return add (nIndex, JsonValue.create (dValue));
  }

  @Nonnull
  default IJsonArray add (@Nonnegative final int nIndex, final float fValue)
  {
    return add (nIndex, JsonValue.create (fValue));
  }

  @Nonnull
  default IJsonArray add (@Nonnegative final int nIndex, final int nValue)
  {
    return add (nIndex, JsonValue.create (nValue));
  }

  @Nonnull
  default IJsonArray add (@Nonnegative final int nIndex, final long nValue)
  {
    return add (nIndex, JsonValue.create (nValue));
  }

  @Nonnull
  default IJsonArray add (@Nonnegative final int nIndex, final short nValue)
  {
    return add (nIndex, JsonValue.create (nValue));
  }

  @Nonnull
  default IJsonArray addAll (@Nullable final boolean... aValues)
  {
    if (aValues != null)
      for (final boolean aValue : aValues)
        add (aValue);
    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nullable final byte... aValues)
  {
    if (aValues != null)
      for (final byte aValue : aValues)
        add (aValue);
    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nullable final char... aValues)
  {
    if (aValues != null)
      for (final char aValue : aValues)
        add (aValue);
    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nullable final double... aValues)
  {
    if (aValues != null)
      for (final double aValue : aValues)
        add (aValue);
    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nullable final float... aValues)
  {
    if (aValues != null)
      for (final float aValue : aValues)
        add (aValue);
    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nullable final int... aValues)
  {
    if (aValues != null)
      for (final int aValue : aValues)
        add (aValue);
    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nullable final long... aValues)
  {
    if (aValues != null)
      for (final long aValue : aValues)
        add (aValue);
    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nullable final short... aValues)
  {
    if (aValues != null)
      for (final short aValue : aValues)
        add (aValue);
    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nullable final Object... aValues)
  {
    if (aValues != null)
      for (final Object aValue : aValues)
        add (aValue);
    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nullable final Iterable <?> aValues)
  {
    if (aValues != null)
      for (final Object aValue : aValues)
        add (aValue);
    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nullable final IJsonArray aArray)
  {
    if (aArray != null)
      for (final IJson aValue : aArray)
        add (aValue);
    return this;
  }

  @Nonnull
  default <T> IJsonArray addAllMapped (@Nullable final Iterable <? extends T> aValues,
                                       @Nonnull final Function <? super T, IJson> aMapper)
  {
    if (aValues != null)
      for (final T aItem : aValues)
        add (aMapper.apply (aItem));
    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nonnegative final int nIndex, @Nullable final boolean... aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final boolean aValue : aValues)
      {
        add (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nonnegative final int nIndex, @Nullable final byte... aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final byte aValue : aValues)
      {
        add (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nonnegative final int nIndex, @Nullable final char... aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final char aValue : aValues)
      {
        add (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nonnegative final int nIndex, @Nullable final double... aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final double aValue : aValues)
      {
        add (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nonnegative final int nIndex, @Nullable final float... aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final float aValue : aValues)
      {
        add (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nonnegative final int nIndex, @Nullable final int... aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final int aValue : aValues)
      {
        add (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nonnegative final int nIndex, @Nullable final long... aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final long aValue : aValues)
      {
        add (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nonnegative final int nIndex, @Nullable final short... aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final short aValue : aValues)
      {
        add (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nonnegative final int nIndex, @Nullable final Object... aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final Object aValue : aValues)
      {
        add (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nonnegative final int nIndex, @Nullable final Iterable <?> aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final Object aValue : aValues)
      {
        add (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return this;
  }

  @Nonnull
  default IJsonArray addAll (@Nonnegative final int nIndex, @Nullable final IJsonArray aArray)
  {
    if (aArray != null)
    {
      int nRealIndex = nIndex;
      for (final IJson aValue : aArray)
      {
        add (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return this;
  }

  @Nonnull
  default <T> IJsonArray addAllMapped (@Nonnegative final int nIndex,
                                       @Nullable final Iterable <? extends T> aValues,
                                       @Nonnull final Function <? super T, IJson> aMapper)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final T aItem : aValues)
      {
        add (nRealIndex, aMapper.apply (aItem));
        nRealIndex++;
      }
    }
    return this;
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
  void forEach (@Nonnull ObjIntConsumer <? super IJson> aConsumer);

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
