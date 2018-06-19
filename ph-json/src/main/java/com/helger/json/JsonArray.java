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
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.json.serialize.JsonReader;

/**
 * Default implementation of {@link IJsonArray}
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class JsonArray implements IJsonArray
{
  private ICommonsList <IJson> m_aValues;

  public JsonArray ()
  {
    this (16);
  }

  public JsonArray (@Nonnegative final int nInitialCapacity)
  {
    m_aValues = new CommonsArrayList <> (nInitialCapacity);
  }

  public JsonArray (@Nonnull final Iterable <? extends IJson> aJsons)
  {
    ValueEnforcer.notNull (aJsons, "Jsons");
    m_aValues = new CommonsArrayList <> (aJsons);
  }

  public JsonArray (@Nonnull final IJson... aJsons)
  {
    ValueEnforcer.notNull (aJsons, "Jsons");
    m_aValues = new CommonsArrayList <> (aJsons);
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
    m_aValues = new CommonsArrayList <> (nInitialSize);
    final String sJson = StreamHelper.readSafeUTF (aOIS);
    final JsonArray aJson = (JsonArray) JsonReader.readFromString (sJson);
    m_aValues.addAll (aJson.m_aValues);
  }

  public final boolean isArray ()
  {
    return true;
  }

  public final boolean isObject ()
  {
    return false;
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
  public Iterator <IJson> iterator ()
  {
    return m_aValues.iterator ();
  }

  @Override
  @Nonnull
  public JsonArray add (@Nonnull final IJson aValue)
  {
    ValueEnforcer.notNull (aValue, "Value");

    m_aValues.add (aValue);
    return this;
  }

  @Nonnull
  public JsonArray addAt (@Nonnegative final int nIndex, @Nonnull final IJson aValue)
  {
    ValueEnforcer.notNull (aValue, "Value");

    m_aValues.add (nIndex, aValue);
    return this;
  }

  @Nonnull
  public IJson removeAndReturnAtIndex (@Nonnegative final int nIndex)
  {
    return m_aValues.removeAndReturnElementAtIndex (nIndex);
  }

  @Nonnull
  public EChange removeAtIndex (@Nonnegative final int nIndex)
  {
    return m_aValues.removeAtIndex (nIndex);
  }

  @Nullable
  public IJson get (@Nonnegative final int nIndex)
  {
    return m_aValues.getAtIndex (nIndex);
  }

  @Nonnull
  @ReturnsMutableCopy
  public JsonArray getSubArray (@Nonnegative final int nStartIndex, @Nonnegative final int nEndIndex)
  {
    ValueEnforcer.isGE0 (nStartIndex, "StartIndex");
    ValueEnforcer.isBetweenInclusive (nEndIndex, "EndIndex", nStartIndex, m_aValues.size ());

    final int nLength = nEndIndex - nStartIndex;
    final JsonArray ret = new JsonArray (nLength);
    ret.addAll (m_aValues.subList (nStartIndex, nEndIndex));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IJson> getAll ()
  {
    return m_aValues.getClone ();
  }

  @Override
  public void forEach (@Nonnull final Consumer <? super IJson> aConsumer)
  {
    m_aValues.forEach (aConsumer);
  }

  public void forEachByIndex (@Nonnull final ObjIntConsumer <? super IJson> aConsumer)
  {
    m_aValues.forEachByIndex (aConsumer);
  }

  public boolean contains (@Nullable final IJson aValue)
  {
    return aValue != null && m_aValues.contains (aValue);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IJson> getClonedValues ()
  {
    return m_aValues.getAllMapped (IJson::getClone);
  }

  @Nonnull
  public JsonArray getClone ()
  {
    return new JsonArray (getClonedValues ());
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final JsonArray rhs = (JsonArray) o;
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
