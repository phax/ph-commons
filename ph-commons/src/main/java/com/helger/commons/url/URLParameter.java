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
package com.helger.commons.url;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.codec.IEncoder;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.hashcode.IHashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * This class represents a single URL parameter. It consists of a mandatory name
 * and an optional value.
 *
 * @author Philip Helger
 */
@Immutable
public class URLParameter implements Serializable
{
  private final String m_sName;
  private final String m_sValue;

  // Status vars
  private transient int m_nHashCode = IHashCodeGenerator.ILLEGAL_HASHCODE;

  public URLParameter (@Nonnull @Nonempty final String sName)
  {
    this (sName, "");
  }

  public URLParameter (@Nonnull @Nonempty final String sName, @Nonnull final String sValue)
  {
    m_sName = ValueEnforcer.notEmpty (sName, "Name");
    m_sValue = ValueEnforcer.notNull (sValue, "Value");
  }

  /**
   * @return The name of the URL parameter. Neither <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  /**
   * Check if this parameter has the specified name.
   *
   * @param sName
   *        The name to check. May be <code>null</code>.
   * @return <code>true</code> if name matches, <code>false</code> otherwise.
   */
  public boolean hasName (@Nullable final String sName)
  {
    return m_sName.equals (sName);
  }

  /**
   * @return The value of the URL parameter. Never <code>null</code> but maybe
   *         empty.
   */
  @Nonnull
  public String getValue ()
  {
    return m_sValue;
  }

  /**
   * @return <code>true</code> if this parameter has a value, <code>false</code>
   *         otherwise.
   */
  public boolean hasValue ()
  {
    return !m_sValue.isEmpty ();
  }

  /**
   * Check if this parameter has the specified value.
   *
   * @param sValue
   *        The value to check. May be <code>null</code>.
   * @return <code>true</code> if value matches, <code>false</code> otherwise.
   */
  public boolean hasValue (@Nullable final String sValue)
  {
    return m_sValue.equals (sValue);
  }

  public void appendTo (@Nonnull final StringBuilder aSB,
                        @Nullable final IEncoder <String, String> aQueryParameterEncoder)
  {
    // Name
    if (aQueryParameterEncoder != null)
      aSB.append (aQueryParameterEncoder.getEncoded (m_sName));
    else
      aSB.append (m_sName);

    // Value
    if (hasValue ())
    {
      aSB.append (URLHelper.EQUALS);
      if (aQueryParameterEncoder != null)
        aSB.append (aQueryParameterEncoder.getEncoded (m_sValue));
      else
        aSB.append (m_sValue);
    }
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final URLParameter rhs = (URLParameter) o;
    return m_sName.equals (rhs.m_sName) && m_sValue.equals (rhs.m_sValue);
  }

  @Override
  public int hashCode ()
  {
    int ret = m_nHashCode;
    if (ret == IHashCodeGenerator.ILLEGAL_HASHCODE)
      ret = m_nHashCode = new HashCodeGenerator (this).append (m_sName).append (m_sValue).getHashCode ();
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Name", m_sName).append ("Value", m_sValue).getToString ();
  }
}
