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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.ICloneable;
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
public class MapBasedAttributeContainer <KEYTYPE, VALUETYPE>
                                        extends MapBasedReadOnlyAttributeContainer <KEYTYPE, VALUETYPE> implements
                                        IMutableAttributeContainer <KEYTYPE, VALUETYPE>,
                                        ICloneable <MapBasedAttributeContainer <KEYTYPE, VALUETYPE>>
{
  public MapBasedAttributeContainer ()
  {
    this (true, new HashMap <KEYTYPE, VALUETYPE> ());
  }

  public MapBasedAttributeContainer (@Nonnull final KEYTYPE aKey, @Nullable final VALUETYPE aValue)
  {
    this (true, new HashMap <KEYTYPE, VALUETYPE> ());
    m_aAttrs.put (aKey, aValue);
  }

  public MapBasedAttributeContainer (@Nonnull final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    this (true, new HashMap <KEYTYPE, VALUETYPE> (aMap));
  }

  public MapBasedAttributeContainer (@Nonnull final IAttributeContainer <? extends KEYTYPE, ? extends VALUETYPE> aCont)
  {
    this (true, new HashMap <KEYTYPE, VALUETYPE> (aCont.getAllAttributes ()));
  }

  protected MapBasedAttributeContainer (final boolean bDummy, @Nonnull final Map <KEYTYPE, VALUETYPE> aAttrMap)
  {
    super (bDummy, aAttrMap);
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
  public final EChange setAttributes (@Nullable final IAttributeContainer <? extends KEYTYPE, ? extends VALUETYPE> aValues)
  {
    if (aValues == null)
      return EChange.UNCHANGED;
    return setAttributes (aValues.getAllAttributes ());
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
    return EChange.valueOf (!EqualsHelper.equals (aValue, aOldValue));
  }

  /**
   * Internal callback method that can be used to avoid removal of an attribute.
   *
   * @param aName
   *        The attribute name. Never <code>null</code>.
   * @return {@link EContinue#CONTINUE} to indicate that the operation should
   *         continue. May not be <code>null</code>.
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
    final VALUETYPE aOldValue = m_aAttrs.remove (aName);
    return EChange.valueOf (aOldValue != null);
  }

  /**
   * Internal callback method that can be used to avoid removal of all
   * attributes.
   *
   * @return {@link EContinue#CONTINUE} to indicate that the operation should
   *         continue. May not be <code>null</code>.
   */
  @OverrideOnDemand
  @Nonnull
  protected EContinue onBeforeRemoveAllAttributes ()
  {
    return EContinue.CONTINUE;
  }

  @Nonnull
  public EChange clear ()
  {
    if (m_aAttrs.isEmpty ())
      return EChange.UNCHANGED;

    // Callback method
    if (onBeforeRemoveAllAttributes ().isBreak ())
      return EChange.UNCHANGED;

    m_aAttrs.clear ();
    return EChange.CHANGED;
  }

  @Nonnull
  @ReturnsMutableCopy
  public MapBasedAttributeContainer <KEYTYPE, VALUETYPE> getClone ()
  {
    return new MapBasedAttributeContainer <KEYTYPE, VALUETYPE> (m_aAttrs);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MapBasedAttributeContainer <?, ?> rhs = (MapBasedAttributeContainer <?, ?>) o;
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
