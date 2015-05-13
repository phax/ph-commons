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

import com.helger.commons.ICloneable;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.OverrideOnDemand;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.state.EContinue;
import com.helger.commons.string.ToStringGenerator;

/**
 * Base class for all kind of any-any mapping container. This implementation is
 * not thread-safe!
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <VALUETYPE>
 *        Value type
 */
@NotThreadSafe
public class MapBasedGenericAttributeContainer <KEYTYPE, VALUETYPE> extends AbstractGenericReadonlyAttributeContainer <KEYTYPE, VALUETYPE> implements IGenericAttributeContainer <KEYTYPE, VALUETYPE>, ICloneable <MapBasedGenericAttributeContainer <KEYTYPE, VALUETYPE>>
{
  /**
   * attribute storage.
   */
  private final Map <KEYTYPE, VALUETYPE> m_aAttrs = new HashMap <KEYTYPE, VALUETYPE> ();

  public MapBasedGenericAttributeContainer ()
  {}

  public MapBasedGenericAttributeContainer (@Nonnull final KEYTYPE aKey, @Nullable final VALUETYPE aValue)
  {
    m_aAttrs.put (aKey, aValue);
  }

  public MapBasedGenericAttributeContainer (@Nonnull final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    ValueEnforcer.notNull (aMap, "Map");
    m_aAttrs.putAll (aMap);
  }

  public MapBasedGenericAttributeContainer (@Nonnull final IGenericReadonlyAttributeContainer <? extends KEYTYPE, ? extends VALUETYPE> aCont)
  {
    ValueEnforcer.notNull (aCont, "Container");
    m_aAttrs.putAll (aCont.getAllAttributes ());
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

  @Nullable
  public VALUETYPE getAttributeObject (@Nullable final KEYTYPE aName)
  {
    // ConcurrentHashMap cannot handle null keys
    return aName == null ? null : m_aAttrs.get (aName);
  }

  /**
   * Internal callback method that can be used to check constraints on an
   * attribute name or value.
   *
   * @param aName
   *        The attribute name. Never <code>null</code>.
   * @param aValue
   *        The attribute value. Never <code>null</code>.
   * @return {@link EContinue#CONTINUE} to indicate that the name-value-pair is
   *         OK. May not be <code>null</code>.
   */
  @OverrideOnDemand
  @Nonnull
  protected EContinue onBeforeSetAttributeValue (@Nonnull final KEYTYPE aName, @Nonnull final VALUETYPE aValue)
  {
    return EContinue.CONTINUE;
  }

  @Nonnull
  public EChange setAttribute (@Nonnull final KEYTYPE aName, @Nullable final VALUETYPE aValue)
  {
    ValueEnforcer.notNull (aName, "Name");

    if (aValue == null)
      return removeAttribute (aName);

    // Callback for checks etc.
    if (onBeforeSetAttributeValue (aName, aValue).isBreak ())
      return EChange.UNCHANGED;

    final VALUETYPE aOldValue = m_aAttrs.put (aName, aValue);
    return EChange.valueOf (!EqualsUtils.equals (aValue, aOldValue));
  }

  @Nonnull
  public final EChange setAttributes (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aValues)
  {
    EChange ret = EChange.UNCHANGED;
    if (aValues != null)
      for (final Map.Entry <? extends KEYTYPE, ? extends VALUETYPE> aEntry : aValues.entrySet ())
        ret = ret.or (setAttribute (aEntry.getKey (), aEntry.getValue ()));
    return ret;
  }

  @Nonnull
  public final EChange setAttributes (@Nullable final IGenericReadonlyAttributeContainer <? extends KEYTYPE, ? extends VALUETYPE> aValues)
  {
    if (aValues == null)
      return EChange.UNCHANGED;
    return setAttributes (aValues.getAllAttributes ());
  }

  /**
   * Internal callback method that can be used to avoid removal of an attribute.
   *
   * @param aName
   *        The attribute name. Never <code>null</code>.
   * @return {@link EContinue#CONTINUE} to indicate that the name-value-pair is
   *         OK. May not be <code>null</code>.
   */
  @OverrideOnDemand
  @Nonnull
  protected EContinue onBeforeRemoveAttribute (@Nonnull final KEYTYPE aName)
  {
    return EContinue.CONTINUE;
  }

  @Nonnull
  public EChange removeAttribute (@Nullable final KEYTYPE aName)
  {
    if (aName == null)
      return EChange.UNCHANGED;

    // Callback method
    if (onBeforeRemoveAttribute (aName).isBreak ())
      return EChange.UNCHANGED;

    // Returned value may be null
    return EChange.valueOf (m_aAttrs.remove (aName) != null);
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

  @Nonnegative
  public int getAttributeCount ()
  {
    return m_aAttrs.size ();
  }

  public boolean containsNoAttribute ()
  {
    return m_aAttrs.isEmpty ();
  }

  @Nonnull
  public EChange clear ()
  {
    if (m_aAttrs.isEmpty ())
      return EChange.UNCHANGED;
    m_aAttrs.clear ();
    return EChange.CHANGED;
  }

  @Nonnull
  public MapBasedGenericAttributeContainer <KEYTYPE, VALUETYPE> getClone ()
  {
    return new MapBasedGenericAttributeContainer <KEYTYPE, VALUETYPE> (m_aAttrs);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MapBasedGenericAttributeContainer <?, ?> rhs = (MapBasedGenericAttributeContainer <?, ?>) o;
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
