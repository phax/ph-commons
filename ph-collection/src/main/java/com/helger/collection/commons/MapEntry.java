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
package com.helger.collection.commons;

import java.util.Map;
import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Simple implementation of Map.Entry.<br>
 * The equals and hashCode implementation follow the contract of {@link Map.Entry}, so an instance of
 * this class is equal to any other {@link Map.Entry} with the same key and value.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The key type.
 * @param <VALUETYPE>
 *        The value type.
 */
@NotThreadSafe
public class MapEntry <KEYTYPE, VALUETYPE> implements Map.Entry <KEYTYPE, VALUETYPE>
{
  private final KEYTYPE m_aKey;
  private VALUETYPE m_aValue;

  /**
   * Constructor copying key and value from an existing map entry.
   *
   * @param aEntry
   *        The map entry to copy from. May not be <code>null</code>.
   */
  public MapEntry (final Map.@NonNull Entry <? extends KEYTYPE, ? extends VALUETYPE> aEntry)
  {
    this (aEntry.getKey (), aEntry.getValue ());
  }

  /**
   * Constructor with explicit key and value.
   *
   * @param aKey
   *        The key. May be <code>null</code>.
   * @param aValue
   *        The value. May be <code>null</code>.
   */
  public MapEntry (@Nullable final KEYTYPE aKey, @Nullable final VALUETYPE aValue)
  {
    m_aKey = aKey;
    m_aValue = aValue;
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  public KEYTYPE getKey ()
  {
    return m_aKey;
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  public VALUETYPE getValue ()
  {
    return m_aValue;
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  public VALUETYPE setValue (@Nullable final VALUETYPE aValue)
  {
    final VALUETYPE aOldValue = m_aValue;
    m_aValue = aValue;
    return aOldValue;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    // As defined by the Map.Entry contract
    if (!(o instanceof final Map.Entry <?, ?> rhs))
      return false;
    return Objects.equals (m_aKey, rhs.getKey ()) && Objects.equals (m_aValue, rhs.getValue ());
  }

  @Override
  public int hashCode ()
  {
    // As defined by the Map.Entry contract
    return Objects.hashCode (m_aKey) ^ Objects.hashCode (m_aValue);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Key", m_aKey).append ("Value", m_aValue).getToString ();
  }
}
