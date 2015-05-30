/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.collections.attrs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IAttributeContainer}
 * based on a hash map. This implementation may carry <code>null</code> values
 * but that is not recommended.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <VALUETYPE>
 *        Value type
 */
@NotThreadSafe
public class MapBasedGenericReadonlyAttributeContainer <KEYTYPE, VALUETYPE> extends AbstractReadonlyAttributeContainer <KEYTYPE, VALUETYPE>
{
  private final Map <KEYTYPE, VALUETYPE> m_aAttrs = new HashMap <KEYTYPE, VALUETYPE> ();

  public MapBasedGenericReadonlyAttributeContainer (@Nonnull final KEYTYPE aKey, @Nullable final VALUETYPE aValue)
  {
    m_aAttrs.put (aKey, aValue);
  }

  public MapBasedGenericReadonlyAttributeContainer (@Nonnull final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    ValueEnforcer.notNull (aMap, "Map");
    m_aAttrs.putAll (aMap);
  }

  public MapBasedGenericReadonlyAttributeContainer (@Nonnull final IAttributeContainer <? extends KEYTYPE, ? extends VALUETYPE> aCont)
  {
    ValueEnforcer.notNull (aCont, "Container");
    m_aAttrs.putAll (aCont.getAllAttributes ());
  }

  public boolean containsAttribute (@Nullable final KEYTYPE aName)
  {
    return m_aAttrs.containsKey (aName);
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <KEYTYPE, VALUETYPE> getAllAttributes ()
  {
    return CollectionHelper.newMap (m_aAttrs);
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <KEYTYPE> getAllAttributeNames ()
  {
    return CollectionHelper.newSet (m_aAttrs.keySet ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public Collection <VALUETYPE> getAllAttributeValues ()
  {
    return CollectionHelper.newList (m_aAttrs.values ());
  }

  @Nullable
  public VALUETYPE getAttributeObject (@Nullable final KEYTYPE aName)
  {
    return m_aAttrs.get (aName);
  }

  @Nonnegative
  public int getAttributeCount ()
  {
    return m_aAttrs.size ();
  }

  public boolean containsNoAttribute ()
  {
    return m_aAttrs.isEmpty ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MapBasedGenericReadonlyAttributeContainer <?, ?> rhs = (MapBasedGenericReadonlyAttributeContainer <?, ?>) o;
    return m_aAttrs.equals (rhs.m_aAttrs);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aAttrs).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("map", m_aAttrs).toString ();
  }
}
