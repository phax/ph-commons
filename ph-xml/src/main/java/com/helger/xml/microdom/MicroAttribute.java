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
package com.helger.xml.microdom;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;

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

  /**
   * Constructor with just a local name.
   *
   * @param sAttributeName
   *        The attribute name. May neither be <code>null</code> nor empty.
   * @param sAttributeValue
   *        The attribute value. May not be <code>null</code>.
   */
  public MicroAttribute (@NonNull @Nonempty final String sAttributeName, @NonNull final String sAttributeValue)
  {
    this (null, sAttributeName, sAttributeValue);
  }

  /**
   * Constructor with namespace URI and local name.
   *
   * @param sNamespaceURI
   *        The namespace URI. May be <code>null</code>.
   * @param sAttributeName
   *        The attribute name. May neither be <code>null</code> nor empty.
   * @param sAttributeValue
   *        The attribute value. May not be <code>null</code>.
   */
  public MicroAttribute (@Nullable final String sNamespaceURI,
                         @NonNull @Nonempty final String sAttributeName,
                         @NonNull final String sAttributeValue)
  {
    this (new MicroQName (sNamespaceURI, sAttributeName), sAttributeValue);
  }

  /**
   * Constructor with a qualified name.
   *
   * @param aQName
   *        The qualified name. May not be <code>null</code>.
   * @param sAttributeValue
   *        The attribute value. May not be <code>null</code>.
   */
  public MicroAttribute (@NonNull final IMicroQName aQName, @NonNull final String sAttributeValue)
  {
    m_aQName = ValueEnforcer.notNull (aQName, "QName");
    m_sAttributeValue = ValueEnforcer.notNull (sAttributeValue, "AttributeValue");
  }

  /** {@inheritDoc} */
  @NonNull
  public IMicroQName getAttributeQName ()
  {
    return m_aQName;
  }

  /** {@inheritDoc} */
  @NonNull
  public String getAttributeValue ()
  {
    return m_sAttributeValue;
  }

  /** {@inheritDoc} */
  @NonNull
  public EChange setAttributeValue (@NonNull final String sAttributeValue)
  {
    ValueEnforcer.notNull (sAttributeValue, "AttributeValue");
    if (sAttributeValue.equals (m_sAttributeValue))
      return EChange.UNCHANGED;
    m_sAttributeValue = sAttributeValue;
    return EChange.CHANGED;
  }

  /** {@inheritDoc} */
  @NonNull
  public MicroAttribute getClone ()
  {
    // QName is immutable!
    return new MicroAttribute (m_aQName, m_sAttributeValue);
  }

  @Override
  public boolean equals (final Object o)
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
    return new ToStringGenerator (this).append ("QName", m_aQName).append ("AttributeValue", m_sAttributeValue).getToString ();
  }
}
