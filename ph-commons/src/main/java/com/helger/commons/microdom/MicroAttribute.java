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
package com.helger.commons.microdom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.namespace.QName;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IMicroAttribute} interface.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class MicroAttribute implements IMicroAttribute
{
  private final IMicroQName m_aQName;
  private String m_sAttributeValue;

  public MicroAttribute (@Nonnull @Nonempty final String sAttributeName, @Nonnull final String sAttributeValue)
  {
    this (null, sAttributeName, sAttributeValue);
  }

  public MicroAttribute (@Nullable final String sNamespaceURI,
                         @Nonnull @Nonempty final String sAttributeName,
                         @Nonnull final String sAttributeValue)
  {
    this (new MicroQName (sNamespaceURI, sAttributeName), sAttributeValue);
  }

  public MicroAttribute (@Nonnull final IMicroQName aQName, @Nonnull final String sAttributeValue)
  {
    m_aQName = ValueEnforcer.notNull (aQName, "QName");
    m_sAttributeValue = ValueEnforcer.notNull (sAttributeValue, "AttributeValue");
  }

  @Nullable
  public String getNamespaceURI ()
  {
    return m_aQName.getNamespaceURI ();
  }

  public boolean hasNamespaceURI ()
  {
    return m_aQName.hasNamespaceURI ();
  }

  public boolean hasNoNamespaceURI ()
  {
    return m_aQName.hasNoNamespaceURI ();
  }

  public boolean hasNamespaceURI (@Nullable final String sNamespaceURI)
  {
    return m_aQName.hasNamespaceURI (sNamespaceURI);
  }

  @Nonnull
  public String getAttributeName ()
  {
    return m_aQName.getName ();
  }

  @Nonnull
  public QName getAsXMLQName ()
  {
    return m_aQName.getAsXMLQName ();
  }

  @Nonnull
  public QName getAsXMLQName (@Nonnull final String sPrefix)
  {
    return m_aQName.getAsXMLQName (sPrefix);
  }

  @Nonnull
  public IMicroQName getAttributeQName ()
  {
    return m_aQName;
  }

  @Nonnull
  public String getAttributeValue ()
  {
    return m_sAttributeValue;
  }

  @Nonnull
  public EChange setAttributeValue (@Nonnull final String sAttributeValue)
  {
    ValueEnforcer.notNull (sAttributeValue, "AttributeValue");
    if (sAttributeValue.equals (m_sAttributeValue))
      return EChange.UNCHANGED;
    m_sAttributeValue = sAttributeValue;
    return EChange.CHANGED;
  }

  @Nonnull
  public MicroAttribute getClone ()
  {
    // QName is immutable!
    return new MicroAttribute (m_aQName, m_sAttributeValue);
  }

  @Override
  public boolean equals (@Nullable final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MicroAttribute rhs = (MicroAttribute) o;
    return m_aQName.equals (rhs.m_aQName) && m_sAttributeValue.equals (rhs.m_sAttributeValue);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aQName).append (m_sAttributeValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("QName", m_aQName)
                                       .append ("AttributeValue", m_sAttributeValue)
                                       .toString ();
  }
}
