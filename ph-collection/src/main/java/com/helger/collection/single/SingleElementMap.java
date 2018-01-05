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
package com.helger.collection.single;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.ReturnsImmutableObject;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.collection.map.MapEntry;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * {@link ICommonsMap} implementation that can only keep 0 or 1 element.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The key type.
 * @param <VALUETYPE>
 *        The value type.
 */
@NotThreadSafe
public class SingleElementMap <KEYTYPE, VALUETYPE> implements ICommonsMap <KEYTYPE, VALUETYPE>
{
  private boolean m_bHasElement = false;
  private KEYTYPE m_aKey;
  private VALUETYPE m_aValue;

  public SingleElementMap ()
  {}

  public SingleElementMap (@Nullable final KEYTYPE aKey, @Nullable final VALUETYPE aValue)
  {
    put (aKey, aValue);
  }

  @Nonnull
  @ReturnsMutableCopy
  public SingleElementMap <KEYTYPE, VALUETYPE> getClone ()
  {
    return m_bHasElement ? new SingleElementMap<> (m_aKey, m_aValue) : new SingleElementMap<> ();
  }

  public void clear ()
  {
    m_bHasElement = false;
    m_aKey = null;
    m_aValue = null;
  }

  public boolean containsKey (@Nullable final Object aKey)
  {
    return m_bHasElement && EqualsHelper.equals (m_aKey, aKey);
  }

  public boolean containsValue (@Nullable final Object aValue)
  {
    return m_bHasElement && EqualsHelper.equals (m_aValue, aValue);
  }

  @Nullable
  public VALUETYPE get (@Nullable final Object aKey)
  {
    return containsKey (aKey) ? m_aValue : null;
  }

  public boolean isEmpty ()
  {
    return !m_bHasElement;
  }

  @Nullable
  public final VALUETYPE put (@Nullable final KEYTYPE aKey, @Nullable final VALUETYPE aElement)
  {
    VALUETYPE aOldElement = null;
    if (EqualsHelper.equals (aKey, m_aKey))
    {
      // Key is the same as before -> return old value
      aOldElement = m_aValue;
    }
    else
    {
      // Key changed
      m_aKey = aKey;
    }
    m_aValue = aElement;
    m_bHasElement = true;
    return aOldElement;
  }

  public void putAll (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    if (aMap != null && !aMap.isEmpty ())
    {
      if (aMap.size () != 1)
        throw new IllegalArgumentException ("Only maps with exactly one element are allowed!");

      final Map.Entry <? extends KEYTYPE, ? extends VALUETYPE> aEntry = aMap.entrySet ().iterator ().next ();
      put (aEntry.getKey (), aEntry.getValue ());
    }
  }

  @Nullable
  public VALUETYPE remove (@Nullable final Object aKey)
  {
    if (!containsKey (aKey))
      return null;
    final VALUETYPE aOldElement = m_aValue;
    m_bHasElement = false;
    m_aValue = null;
    m_aKey = null;
    return aOldElement;
  }

  @Nonnegative
  public int size ()
  {
    return m_bHasElement ? 1 : 0;
  }

  @ReturnsImmutableObject
  @Nonnull
  @CodingStyleguideUnaware
  public Set <KEYTYPE> keySet ()
  {
    return m_bHasElement ? new CommonsHashSet<> (m_aKey) : new CommonsHashSet<> ();
  }

  @ReturnsImmutableObject
  @Nonnull
  public Collection <VALUETYPE> values ()
  {
    return m_bHasElement ? new CommonsArrayList<> (m_aValue) : new CommonsArrayList<> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  @CodingStyleguideUnaware
  public Set <Map.Entry <KEYTYPE, VALUETYPE>> entrySet ()
  {
    final ICommonsSet <Map.Entry <KEYTYPE, VALUETYPE>> aSet = new CommonsHashSet<> (size ());
    if (m_bHasElement)
      aSet.add (new MapEntry<> (m_aKey, m_aValue));
    return aSet;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final SingleElementMap <?, ?> rhs = (SingleElementMap <?, ?>) o;
    return m_bHasElement == rhs.m_bHasElement &&
           EqualsHelper.equals (m_aKey, rhs.m_aKey) &&
           EqualsHelper.equals (m_aValue, rhs.m_aValue);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_bHasElement).append (m_aKey).append (m_aValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("hasElement", m_bHasElement)
                                       .append ("key", m_aKey)
                                       .append ("value", m_aValue)
                                       .getToString ();
  }
}
