/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
import java.util.Enumeration;
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
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.state.EContinue;
import com.helger.commons.string.ToStringGenerator;

/**
 * Base class for all kind of string-object mapping container. This
 * implementation is not thread-safe!
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class MapBasedAttributeContainer extends AbstractReadonlyAttributeContainer implements IAttributeContainer, ICloneable <MapBasedAttributeContainer>
{
  /**
   * attribute storage.
   */
  private final Map <String, Object> m_aAttrs;

  public MapBasedAttributeContainer ()
  {
    m_aAttrs = new HashMap <String, Object> ();
  }

  public MapBasedAttributeContainer (@Nonnull final Map <String, ?> aMap)
  {
    ValueEnforcer.notNull (aMap, "Map");
    m_aAttrs = ContainerHelper.newMap (aMap);
  }

  public MapBasedAttributeContainer (@Nonnull final IReadonlyAttributeContainer aCont)
  {
    ValueEnforcer.notNull (aCont, "Container");
    // Must already be a copy!
    m_aAttrs = aCont.getAllAttributes ();
  }

  public boolean containsAttribute (@Nullable final String sName)
  {
    // ConcurrentHashMap cannot handle null keys
    return sName != null && m_aAttrs.containsKey (sName);
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <String, Object> getAllAttributes ()
  {
    return ContainerHelper.newMap (m_aAttrs);
  }

  @Nullable
  public Object getAttributeObject (@Nullable final String sName)
  {
    // ConcurrentHashMap cannot handle null keys
    return sName == null ? null : m_aAttrs.get (sName);
  }

  /**
   * Internal callback method that can be used to check constraints on an
   * attribute name or value.
   * 
   * @param sName
   *        The attribute name. Never <code>null</code>.
   * @param aValue
   *        The attribute value. Never <code>null</code>.
   * @return {@link EContinue#CONTINUE} to indicate that the name-value-pair is
   *         OK. May not be <code>null</code>.
   */
  @OverrideOnDemand
  @Nonnull
  protected EContinue onBeforeSetAttributeValue (@Nonnull final String sName, @Nonnull final Object aValue)
  {
    return EContinue.CONTINUE;
  }

  @Nonnull
  public EChange setAttribute (@Nonnull final String sName, @Nullable final Object aValue)
  {
    ValueEnforcer.notNull (sName, "Name");

    if (aValue == null)
      return removeAttribute (sName);

    // Callback for checks etc.
    if (onBeforeSetAttributeValue (sName, aValue).isBreak ())
      return EChange.UNCHANGED;

    final Object aOldValue = m_aAttrs.put (sName, aValue);
    return EChange.valueOf (!EqualsUtils.equals (aValue, aOldValue));
  }

  @Nonnull
  public final EChange setAttribute (@Nonnull final String sName, final boolean dValue)
  {
    return setAttribute (sName, Boolean.valueOf (dValue));
  }

  @Nonnull
  public final EChange setAttribute (@Nonnull final String sName, final int nValue)
  {
    return setAttribute (sName, Integer.valueOf (nValue));
  }

  @Nonnull
  public final EChange setAttribute (@Nonnull final String sName, final long nValue)
  {
    return setAttribute (sName, Long.valueOf (nValue));
  }

  @Nonnull
  public final EChange setAttribute (@Nonnull final String sName, final double dValue)
  {
    return setAttribute (sName, Double.valueOf (dValue));
  }

  @Nonnull
  public final EChange setAttributes (@Nullable final Map <String, ?> aValues)
  {
    EChange ret = EChange.UNCHANGED;
    if (aValues != null)
      for (final Map.Entry <String, ?> aEntry : aValues.entrySet ())
        ret = ret.or (setAttribute (aEntry.getKey (), aEntry.getValue ()));
    return ret;
  }

  @Nonnull
  public final EChange setAttributes (@Nullable final IReadonlyAttributeContainer aValues)
  {
    if (aValues == null)
      return EChange.UNCHANGED;
    return setAttributes (aValues.getAllAttributes ());
  }

  /**
   * Internal callback method that can be used to avoid removal of an attribute.
   * 
   * @param sName
   *        The attribute name. Never <code>null</code>.
   * @return {@link EContinue#CONTINUE} to indicate that the name-value-pair is
   *         OK. May not be <code>null</code>.
   */
  @OverrideOnDemand
  @Nonnull
  protected EContinue onBeforeRemoveAttribute (@Nonnull final String sName)
  {
    return EContinue.CONTINUE;
  }

  @Nonnull
  public EChange removeAttribute (@Nullable final String sName)
  {
    if (sName == null)
      return EChange.UNCHANGED;

    // Callback method
    if (onBeforeRemoveAttribute (sName).isBreak ())
      return EChange.UNCHANGED;

    // Returned value may be null
    return EChange.valueOf (m_aAttrs.remove (sName) != null);
  }

  @Nonnull
  public Enumeration <String> getAttributeNames ()
  {
    // Build an enumerator on top of the set
    return ContainerHelper.getEnumeration (m_aAttrs.keySet ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllAttributeNames ()
  {
    return ContainerHelper.newSet (m_aAttrs.keySet ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public Collection <Object> getAllAttributeValues ()
  {
    return ContainerHelper.newList (m_aAttrs.values ());
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

  public boolean getAndSetAttributeFlag (@Nonnull final String sName)
  {
    final Object aOldValue = getAttributeObject (sName);
    if (aOldValue != null)
    {
      // Attribute flag is already present
      return true;
    }
    // Attribute flag is not yet present -> set it
    setAttribute (sName, Boolean.TRUE);
    return false;
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
  public MapBasedAttributeContainer getClone ()
  {
    return new MapBasedAttributeContainer (m_aAttrs);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MapBasedAttributeContainer rhs = (MapBasedAttributeContainer) o;
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
