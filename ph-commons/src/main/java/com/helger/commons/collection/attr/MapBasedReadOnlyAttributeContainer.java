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
package com.helger.commons.collection.attr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IAttributeContainer} based on a
 * {@link HashMap}. This implementation may carry <code>null</code> values but
 * that is not recommended.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <VALUETYPE>
 *        Value type
 */
@NotThreadSafe
public class MapBasedReadOnlyAttributeContainer <KEYTYPE, VALUETYPE> implements IAttributeContainer <KEYTYPE, VALUETYPE>
{
  /** Main attribute storage. */
  protected final Map <KEYTYPE, VALUETYPE> m_aAttrs;

  public MapBasedReadOnlyAttributeContainer (@Nonnull final KEYTYPE aKey, @Nullable final VALUETYPE aValue)
  {
    this (true, new HashMap <KEYTYPE, VALUETYPE> ());
    m_aAttrs.put (aKey, aValue);
  }

  public MapBasedReadOnlyAttributeContainer (@Nonnull final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    this (true, new HashMap <KEYTYPE, VALUETYPE> (aMap));
  }

  public MapBasedReadOnlyAttributeContainer (@Nonnull final IAttributeContainer <? extends KEYTYPE, ? extends VALUETYPE> aCont)
  {
    this (true, new HashMap <KEYTYPE, VALUETYPE> (aCont.getAllAttributes ()));
  }

  /**
   * Constructor to use a custom map for the attribute container
   *
   * @param bDummy
   *        Dummy parameter to be used to uniquely specify the constructor
   * @param aAttrMap
   *        The attribute map to be used.
   */
  protected MapBasedReadOnlyAttributeContainer (final boolean bDummy, @Nonnull final Map <KEYTYPE, VALUETYPE> aAttrMap)
  {
    m_aAttrs = ValueEnforcer.notNull (aAttrMap, "AttrMap");
  }

  @Nonnull
  public Iterator <Map.Entry <KEYTYPE, VALUETYPE>> getIterator ()
  {
    return m_aAttrs.entrySet ().iterator ();
  }

  public boolean containsAttribute (@Nullable final KEYTYPE aName)
  {
    // ConcurrentHashMap cannot handle null keys
    return aName != null && m_aAttrs.containsKey (aName);
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
    // ConcurrentHashMap cannot handle null keys
    return aName == null ? null : m_aAttrs.get (aName);
  }

  @Nonnegative
  public int getAttributeCount ()
  {
    return m_aAttrs.size ();
  }

  public boolean isEmpty ()
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
    final MapBasedReadOnlyAttributeContainer <?, ?> rhs = (MapBasedReadOnlyAttributeContainer <?, ?>) o;
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
    return new ToStringGenerator (this).append ("attrs", m_aAttrs).toString ();
  }
}
