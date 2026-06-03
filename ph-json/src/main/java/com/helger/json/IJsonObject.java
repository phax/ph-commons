/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

  /**
   * Replace the value of an existing key with the provided new value. The original insertion order
   * of the keys is maintained. If the provided key is not present, no change is made.
   *
   * @param sName
   *        The name of the key whose value should be replaced. May be <code>null</code>.
   * @param aNewValue
   *        The new value to associate with the key. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the value of an existing key was replaced;
   *         {@link EChange#UNCHANGED} if the key was not present.
   * @since 12.2.7
   */
  @NonNull
  EChange replaceKey (@Nullable String sName, @NonNull IJson aNewValue);

  /**
   * Replace the value of an existing key with the provided new value, converting it to an
   * {@link IJson} using the registered type converter. The original insertion order of the keys is
   * maintained. If the provided key is not present, no change is made.
   *
   * @param sName
   *        The name of the key whose value should be replaced. May be <code>null</code>.
   * @param aNewValue
   *        The new value to associate with the key. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if the value of an existing key was replaced;
   *         {@link EChange#UNCHANGED} if the key was not present.
   * @since 12.2.7
   */
  @NonNull
  default EChange replaceKey (@Nullable final String sName, @Nullable final Object aNewValue)
  {
    return replaceKey (sName, getTypeConverterTo ().convert (aNewValue));
  }

  /**
   * Modify the value associated with the provided key by applying the supplied function to the
   * existing value and storing the result. The function receives the current value (which is
   * <code>null</code> if the key is not present) and returns the new value. If the function
   * returns <code>null</code>, no change is performed (so the function can opt out of the
   * replacement). If the function returns a non-<code>null</code> value, that value is associated
   * with the key. When the key already existed, its original insertion position is preserved.
   * When the key did not exist, it is appended at the end.
   *
   * @param sName
   *        The name of the key whose value should be modified. May be <code>null</code>.
   * @param aOldToNewMapper
   *        Function applied to the existing value to compute the new value. The first parameter
   *        passed in is the current value, which may be <code>null</code> if the key is not
   *        present. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the mapping was inserted or replaced;
   *         {@link EChange#UNCHANGED} if the mapper returned <code>null</code>.
   * @since 12.2.7
   */
  @NonNull
  default EChange replaceKey (@Nullable final String sName,
                              @NonNull final Function <? super IJson, ? extends IJson> aOldToNewMapper)
  {
    final IJson aOldValue = get (sName);
    final IJson aNewValue = aOldToNewMapper.apply (aOldValue);
    if (aNewValue == null)
      return EChange.UNCHANGED;
    if (aOldValue == null)
    {
      // Key did not exist before -> append
      add (sName, aNewValue);
      return EChange.CHANGED;
    }
    // Key existed -> replace in place (preserves insertion order)
    return replaceKey (sName, aNewValue);
  }

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
