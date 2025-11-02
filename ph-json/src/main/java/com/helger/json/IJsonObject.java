/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.state.EChange;
import com.helger.base.trait.IGenericMapAdderTrait;
import com.helger.base.trait.ITypeConverterTo;
import com.helger.collection.commons.ICommonsIterable;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.collection.commons.ICommonsOrderedSet;
import com.helger.typeconvert.trait.IGetterByKeyTrait;

/**
 * Base interface for a JSON object that is a map from String to IJson
 *
 * @author Philip Helger
 */
public interface IJsonObject extends
                             IJsonCollection,
                             ICommonsIterable <Map.Entry <String, IJson>>,
                             IGetterByKeyTrait <String>,
                             IGenericMapAdderTrait <String, IJson, IJsonObject>
{
  /* Implementation for IGenericMapAdderTrait */
  @NonNull
  default ITypeConverterTo <IJson> getTypeConverterTo ()
  {
    return TypeConverterToIJson.INSTANCE;
  }

  @Nonnegative
  int size ();

  @Nullable
  IJson removeKeyAndReturnValue (@Nullable String sName);

  @NonNull
  EChange removeKey (@Nullable String sName);

  boolean containsKey (@Nullable String sName);

  @NonNull
  @ReturnsMutableCopy
  ICommonsOrderedSet <String> keySet ();

  @NonNull
  @ReturnsMutableCopy
  ICommonsList <IJson> values ();

  /**
   * Get the element with the specified key.
   *
   * @param sName
   *        The name of the value to retrieve. May be <code>null</code>.
   * @return <code>null</code> if no value for the name exists.
   */
  @Nullable
  IJson get (@Nullable String sName);

  /**
   * Get the element with the specified key. This is the {@link IJsonValue} specific version of
   * {@link #get(String)}.
   *
   * @param sName
   *        The name of the value to retrieve. May be <code>null</code>.
   * @return <code>null</code> if no value for the name exists or if the value is not a
   *         {@link IJsonValue}.
   */
  @Nullable
  default Object getValue (@Nullable final String sName)
  {
    final IJson aJson = get (sName);
    if (aJson != null)
    {
      final IJsonValue aValue = aJson.getAsValue ();
      if (aValue != null)
        return aValue.getValue ();
    }
    return null;
  }

  /**
   * Get the element with the specified key. This is the {@link IJsonValue} specific version of
   * {@link #get(String)}.
   *
   * @param sName
   *        The name of the value to retrieve. May be <code>null</code>.
   * @return <code>null</code> if no value for the name exists or if the value is not a
   *         {@link IJsonValue}.
   */
  @Nullable
  default IJsonValue getAsValue (@Nullable final String sName)
  {
    final IJson aJson = get (sName);
    return aJson != null ? aJson.getAsValue () : null;
  }

  /**
   * Get the element with the specified key. This is the {@link IJsonArray} specific version of
   * {@link #get(String)}.
   *
   * @param sName
   *        The name of the value to retrieve. May be <code>null</code>.
   * @return <code>null</code> if no value for the name exists or if the value is not a
   *         {@link IJsonArray}.
   */
  @Nullable
  default IJsonArray getAsArray (@Nullable final String sName)
  {
    final IJson aJson = get (sName);
    return aJson != null ? aJson.getAsArray () : null;
  }

  /**
   * Get the element with the specified key. This is the {@link IJsonObject} specific version of
   * {@link #get(String)}.
   *
   * @param sName
   *        The name of the value to retrieve. May be <code>null</code>.
   * @return <code>null</code> if no value for the name exists or if the value is not a
   *         {@link IJsonObject}.
   */
  @Nullable
  default IJsonObject getAsObject (@Nullable final String sName)
  {
    final IJson aJson = get (sName);
    return aJson != null ? aJson.getAsObject () : null;
  }

  /**
   * @return A copy of all contained items. Never <code>null</code> but maybe empty.
   */
  @NonNull
  @ReturnsMutableCopy
  ICommonsMap <String, IJson> getAll ();

  /**
   * Invoke the passed consumer on all entries of this object.
   *
   * @param aConsumer
   *        Consumer with the first param being the key and second param being the value.
   */
  void forEach (@NonNull BiConsumer <? super String, ? super IJson> aConsumer);

  /**
   * Check if the passed value is directly contained in the object or not.
   *
   * @param aValue
   *        The value to be checked for containment. May be <code>null</code>.
   * @return <code>true</code> if the value is contained, <code>false</code> if not.
   */
  boolean containsValue (@Nullable IJson aValue);

  /**
   * Check if the passed value is directly contained in the object or not.
   *
   * @param aValue
   *        The value to be checked for containment. May be <code>null</code>.
   * @return <code>true</code> if the value is contained, <code>false</code> if not.
   */
  default boolean containsValue (@Nullable final Object aValue)
  {
    return containsValue (JsonValue.create (aValue));
  }

  /**
   * Check if the passed value is directly contained in the object or not.
   *
   * @param bValue
   *        The value to be checked for containment.
   * @return <code>true</code> if the value is contained, <code>false</code> if not.
   */
  default boolean containsValue (final boolean bValue)
  {
    return containsValue (JsonValue.create (bValue));
  }

  /**
   * Check if the passed value is directly contained in the object or not.
   *
   * @param cValue
   *        The value to be checked for containment.
   * @return <code>true</code> if the value is contained, <code>false</code> if not.
   */
  default boolean containsValue (final char cValue)
  {
    return containsValue (JsonValue.create (cValue));
  }

  /**
   * Check if the passed value is directly contained in the object or not.
   *
   * @param dValue
   *        The value to be checked for containment.
   * @return <code>true</code> if the value is contained, <code>false</code> if not.
   */
  default boolean containsValue (final double dValue)
  {
    return containsValue (JsonValue.create (dValue));
  }

  /**
   * Check if the passed value is directly contained in the object or not.
   *
   * @param nValue
   *        The value to be checked for containment.
   * @return <code>true</code> if the value is contained, <code>false</code> if not.
   */
  default boolean containsValue (final int nValue)
  {
    return containsValue (JsonValue.create (nValue));
  }

  /**
   * Check if the passed value is directly contained in the object or not.
   *
   * @param nValue
   *        The value to be checked for containment.
   * @return <code>true</code> if the value is contained, <code>false</code> if not.
   */
  default boolean containsValue (final long nValue)
  {
    return containsValue (JsonValue.create (nValue));
  }

  /**
   * Compute a JSON value if it is not present.
   *
   * @param sName
   *        The name of the property. May not be <code>null</code>.
   * @param aValueProvider
   *        The value provider of the property. May not be <code>null</code>. Is only invoked, if
   *        the property is not present.
   * @return Either the existing property value, or the newly calculated property value.
   * @since 8.6.4
   */
  @Nullable
  default IJson computeIfAbsent (@NonNull final String sName,
                                 @NonNull final Function <? super String, ? extends IJson> aValueProvider)
  {
    IJson ret = get (sName);
    if (ret == null)
    {
      ret = aValueProvider.apply (sName);
      if (ret != null)
        add (sName, ret);
    }
    return ret;
  }

  /**
   * @return A map of all cloned values contained in this object in the same order. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  ICommonsOrderedMap <String, IJson> getClonedValues ();

  @NonNull
  IJsonObject getClone ();
}
