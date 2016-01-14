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
package com.helger.commons.collection.impl;

import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Simple implementation of Map.Entry.<br>
 * Important: The equals and hashCode implementation of this class is different
 * from the Map.Entry default implementation class! The default Map.Entry claims
 * to be equal with instances of this class, but this class is not equal to the
 * Map.Entry implementation!
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

  public MapEntry (@Nullable final KEYTYPE aKey, @Nullable final VALUETYPE aValue)
  {
    m_aKey = aKey;
    m_aValue = aValue;
  }

  @Nullable
  public KEYTYPE getKey ()
  {
    return m_aKey;
  }

  @Nullable
  public VALUETYPE getValue ()
  {
    return m_aValue;
  }

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
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MapEntry <?, ?> rhs = (MapEntry <?, ?>) o;
    return EqualsHelper.equals (m_aKey, rhs.m_aKey) && EqualsHelper.equals (m_aValue, rhs.m_aValue);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aKey).append (m_aValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("key", m_aKey).append ("value", m_aValue).toString ();
  }
}
