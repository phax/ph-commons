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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.json.serialize.JsonReader;

/**
 * Default implementation of {@link IJsonObject}.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class JsonObject implements IJsonObject
{
  public static final int DEFAULT_INITIAL_CAPACITY = 16;

  private ICommonsOrderedMap <String, IJson> m_aValues;

  public JsonObject ()
  {
    this (DEFAULT_INITIAL_CAPACITY);
  }

  public JsonObject (@Nonnegative final int nInitialCapacity)
  {
    m_aValues = new CommonsLinkedHashMap <> (nInitialCapacity);
  }

  public JsonObject (@Nonnull final Map <String, ? extends IJson> aJsons)
  {
    ValueEnforcer.notNull (aJsons, "Jsons");
    m_aValues = new CommonsLinkedHashMap <> (aJsons);
  }

  private void writeObject (@Nonnull final ObjectOutputStream aOOS) throws IOException
  {
    aOOS.writeInt (m_aValues.size ());
    final String sJson = getAsJsonString ();
    StreamHelper.writeSafeUTF (aOOS, sJson);
  }

  private void readObject (@Nonnull final ObjectInputStream aOIS) throws IOException
  {
    final int nInitialSize = aOIS.readInt ();
    m_aValues = new CommonsLinkedHashMap <> (nInitialSize);
    final String sJson = StreamHelper.readSafeUTF (aOIS);
    final JsonObject aJson = (JsonObject) JsonReader.readFromString (sJson);
    m_aValues.putAll (aJson.m_aValues);
  }

  public final boolean isArray ()
  {
    return false;
  }

  public final boolean isObject ()
  {
    return true;
  }

  public final boolean isValue ()
  {
    return false;
  }

  @Nonnegative
  public int size ()
  {
    return m_aValues.size ();
  }

  public boolean isEmpty ()
  {
    return m_aValues.isEmpty ();
  }

  @Nonnull
  public Iterator <Map.Entry <String, IJson>> iterator ()
  {
    return m_aValues.entrySet ().iterator ();
  }

  @Nonnull
  public JsonObject add (@Nonnull final String sName, @Nonnull final IJson aValue)
  {
    ValueEnforcer.notNull (sName, "Name");
    ValueEnforcer.notNull (aValue, "Value");

    m_aValues.put (sName, aValue);
    return this;
  }

  @Nullable
  public IJson removeKeyAndReturnValue (@Nullable final String sName)
  {
    return m_aValues.remove (sName);
  }

  @Nonnull
  public EChange removeKey (@Nullable final String sName)
  {
    return m_aValues.removeObject (sName);
  }

  public boolean containsKey (@Nullable final String sName)
  {
    return m_aValues.containsKey (sName);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> keySet ()
  {
    return m_aValues.copyOfKeySet ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IJson> values ()
  {
    return m_aValues.copyOfValues ();
  }

  @Nullable
  public IJson get (@Nullable final String sName)
  {
    return m_aValues.get (sName);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedMap <String, IJson> getAll ()
  {
    return m_aValues.getClone ();
  }

  public void forEach (@Nonnull final BiConsumer <? super String, ? super IJson> aConsumer)
  {
    m_aValues.forEach (aConsumer);
  }

  public boolean containsValue (@Nullable final IJson aValue)
  {
    return aValue != null && m_aValues.containsValue (aValue);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedMap <String, IJson> getClonedValues ()
  {
    final ICommonsOrderedMap <String, IJson> ret = new CommonsLinkedHashMap <> ();
    for (final Map.Entry <String, IJson> aEntry : m_aValues.entrySet ())
      ret.put (aEntry.getKey (), aEntry.getValue ().getClone ());
    return ret;
  }

  @Nonnull
  public JsonObject getClone ()
  {
    return new JsonObject (getClonedValues ());
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final JsonObject rhs = (JsonObject) o;
    return m_aValues.equals (rhs.m_aValues);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aValues).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("values", m_aValues).getToString ();
  }
}
