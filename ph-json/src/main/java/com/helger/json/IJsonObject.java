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

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsIterable;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.state.EChange;
import com.helger.commons.traits.IGetterByKeyTrait;
import com.helger.json.convert.JsonConverter;

/**
 * Base interface for a JSON object that is a map from String to IJson
 *
 * @author Philip Helger
 */
public interface IJsonObject extends
                             IJsonCollection,
                             ICommonsIterable <Map.Entry <String, IJson>>,
                             IGetterByKeyTrait <String>
{
  @Nonnull
  IJsonObject add (@Nonnull String sName, @Nonnull IJson aValue);

  @Nonnull
  default IJsonObject addIf (@Nonnull final String sName,
                             @Nonnull final IJson aValue,
                             @Nonnull final Predicate <? super IJson> aFilter)
  {
    if (aFilter.test (aValue))
      add (sName, aValue);
    return this;
  }

  /**
   * Add at the specified value using the JSON converter
   *
   * @param sName
   *        The name of the item. May not be <code>null</code>.
   * @param aValue
   *        The value to be added. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  default IJsonObject add (@Nonnull final String sName, @Nullable final Object aValue)
  {
    final IJson aJson = JsonConverter.convertToJson (aValue);
    return add (sName, aJson);
  }

  @Nonnull
  default IJsonObject addIfNotNull (@Nonnull final String sName, @Nullable final Object aValue)
  {
    if (aValue != null)
      add (sName, aValue);
    return this;
  }

  @Nonnull
  default IJsonObject addIf (@Nonnull final String sName,
                             @Nullable final Object aValue,
                             @Nonnull final Predicate <? super Object> aFilter)
  {
    if (aFilter.test (aValue))
      add (sName, aValue);
    return this;
  }

  @Nonnull
  default IJsonObject add (@Nonnull final Map.Entry <String, ?> aEntry)
  {
    return add (aEntry.getKey (), aEntry.getValue ());
  }

  @Nonnull
  default IJsonObject add (@Nonnull final String sName, final boolean bValue)
  {
    return add (sName, JsonValue.create (bValue));
  }

  @Nonnull
  default IJsonObject add (@Nonnull final String sName, final byte nValue)
  {
    return add (sName, JsonValue.create (nValue));
  }

  @Nonnull
  default IJsonObject add (@Nonnull final String sName, final char cValue)
  {
    return add (sName, JsonValue.create (cValue));
  }

  @Nonnull
  default IJsonObject add (@Nonnull final String sName, final double dValue)
  {
    return add (sName, JsonValue.create (dValue));
  }

  @Nonnull
  default IJsonObject add (@Nonnull final String sName, final float fValue)
  {
    return add (sName, JsonValue.create (fValue));
  }

  @Nonnull
  default IJsonObject add (@Nonnull final String sName, final int nValue)
  {
    return add (sName, JsonValue.create (nValue));
  }

  @Nonnull
  default IJsonObject add (@Nonnull final String sName, final long nValue)
  {
    return add (sName, JsonValue.create (nValue));
  }

  @Nonnull
  default IJsonObject add (@Nonnull final String sName, final short nValue)
  {
    return add (sName, JsonValue.create (nValue));
  }

  @Nonnull
  default IJsonObject addAll (@Nullable final Map <String, ?> aMap)
  {
    if (aMap != null)
      for (final Map.Entry <String, ?> aEntry : aMap.entrySet ())
        add (aEntry);
    return this;
  }

  /**
   * Add all entries of the passed object to this object. So this is a
   * "flattening add all".
   *
   * @param aObject
   *        The object to add from. May be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  default IJsonObject addAll (@Nonnull final IJsonObject aObject)
  {
    if (aObject != null)
      for (final Map.Entry <String, IJson> aEntry : aObject)
        add (aEntry);
    return this;
  }

  @Nonnull
  default <VALUETYPE> IJsonObject addAllMapped (@Nullable final Map <String, ? extends VALUETYPE> aMap,
                                                @Nonnull final Function <? super VALUETYPE, IJson> aValueMapper)
  {
    if (aMap != null)
      for (final Map.Entry <String, ? extends VALUETYPE> aEntry : aMap.entrySet ())
        add (aEntry.getKey (), aValueMapper.apply (aEntry.getValue ()));
    return this;
  }

  @Nonnull
  default <KEYTYPE, VALUETYPE> IJsonObject addAllMapped (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap,
                                                         @Nonnull final Function <? super KEYTYPE, String> aKeyMapper,
                                                         @Nonnull final Function <? super VALUETYPE, IJson> aValueMapper)
  {
    if (aMap != null)
      for (final Map.Entry <? extends KEYTYPE, ? extends VALUETYPE> aEntry : aMap.entrySet ())
        add (aKeyMapper.apply (aEntry.getKey ()), aValueMapper.apply (aEntry.getValue ()));
    return this;
  }

  @Nullable
  IJson removeKeyAndReturnValue (@Nullable String sName);

  @Nonnull
  EChange removeKey (@Nullable String sName);

  boolean containsKey (@Nullable String sName);

  @Nonnull
  @ReturnsMutableCopy
  ICommonsOrderedSet <String> keySet ();

  @Nonnull
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
   * Get the element with the specified key. This is the {@link IJsonValue}
   * specific version of {@link #get(String)}.
   *
   * @param sName
   *        The name of the value to retrieve. May be <code>null</code>.
   * @return <code>null</code> if no value for the name exists or if the value
   *         is not a {@link IJsonValue}.
   */
  @Nullable
  default Object getValue (@Nullable final String sName)
  {
    final IJson aJson = get (sName);
    return aJson != null ? aJson.getAsValue ().getValue () : null;
  }

  /**
   * Get the element with the specified key. This is the {@link IJsonValue}
   * specific version of {@link #get(String)}.
   *
   * @param sName
   *        The name of the value to retrieve. May be <code>null</code>.
   * @return <code>null</code> if no value for the name exists or if the value
   *         is not a {@link IJsonValue}.
   */
  @Nullable
  default IJsonValue getAsValue (@Nullable final String sName)
  {
    final IJson aJson = get (sName);
    return aJson != null ? aJson.getAsValue () : null;
  }

  /**
   * Get the element with the specified key. This is the {@link IJsonArray}
   * specific version of {@link #get(String)}.
   *
   * @param sName
   *        The name of the value to retrieve. May be <code>null</code>.
   * @return <code>null</code> if no value for the name exists or if the value
   *         is not a {@link IJsonArray}.
   */
  @Nullable
  default IJsonArray getAsArray (@Nullable final String sName)
  {
    final IJson aJson = get (sName);
    return aJson != null ? aJson.getAsArray () : null;
  }

  /**
   * Get the element with the specified key. This is the {@link IJsonObject}
   * specific version of {@link #get(String)}.
   *
   * @param sName
   *        The name of the value to retrieve. May be <code>null</code>.
   * @return <code>null</code> if no value for the name exists or if the value
   *         is not a {@link IJsonObject}.
   */
  @Nullable
  default IJsonObject getAsObject (@Nullable final String sName)
  {
    final IJson aJson = get (sName);
    return aJson != null ? aJson.getAsObject () : null;
  }

  /**
   * @return A copy of all contained items. Never <code>null</code> but maybe
   *         empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsMap <String, IJson> getAll ();

  /**
   * Invoke the passed consumer on all entries of this object.
   *
   * @param aConsumer
   *        Consumer with the first param being the key and second param being
   *        the value.
   */
  void forEach (@Nonnull BiConsumer <? super String, ? super IJson> aConsumer);

  /**
   * Check if the passed value is directly contained in the object or not.
   *
   * @param aValue
   *        The value to be checked for containment. May be <code>null</code>.
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
   */
  boolean containsValue (@Nullable IJson aValue);

  /**
   * Check if the passed value is directly contained in the object or not.
   *
   * @param aValue
   *        The value to be checked for containment. May be <code>null</code>.
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
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
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
   */
  default boolean containsValue (final boolean bValue)
  {
    return containsValue (JsonValue.create (bValue));
  }

  /**
   * Check if the passed value is directly contained in the object or not.
   *
   * @param nValue
   *        The value to be checked for containment.
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
   */
  default boolean containsValue (final byte nValue)
  {
    return containsValue (JsonValue.create (nValue));
  }

  /**
   * Check if the passed value is directly contained in the object or not.
   *
   * @param cValue
   *        The value to be checked for containment.
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
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
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
   */
  default boolean containsValue (final double dValue)
  {
    return containsValue (JsonValue.create (dValue));
  }

  /**
   * Check if the passed value is directly contained in the object or not.
   *
   * @param fValue
   *        The value to be checked for containment.
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
   */
  default boolean containsValue (final float fValue)
  {
    return containsValue (JsonValue.create (fValue));
  }

  /**
   * Check if the passed value is directly contained in the object or not.
   *
   * @param nValue
   *        The value to be checked for containment.
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
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
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
   */
  default boolean containsValue (final long nValue)
  {
    return containsValue (JsonValue.create (nValue));
  }

  /**
   * Check if the passed value is directly contained in the object or not.
   *
   * @param nValue
   *        The value to be checked for containment.
   * @return <code>true</code> if the value is contained, <code>false</code> if
   *         not.
   */
  default boolean containsValue (final short nValue)
  {
    return containsValue (JsonValue.create (nValue));
  }

  /**
   * Compute a JSON value if it is not present.
   *
   * @param sName
   *        The name of the property. May not be <code>null</code>.
   * @param aValueProvider
   *        The value provider of the property. May not be <code>null</code>. Is
   *        only invoked, if the property is not present.
   * @return Either the existing property value, or the newly calculated
   *         property value.
   * @since 8.6.4
   */
  @Nullable
  default IJson computeIfAbsent (@Nonnull final String sName,
                                 @Nonnull final Function <? super String, ? extends IJson> aValueProvider)
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
   * @return A map of all cloned values contained in this object in the same
   *         order. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsOrderedMap <String, IJson> getClonedValues ();

  @Nonnull
  IJsonObject getClone ();
}
