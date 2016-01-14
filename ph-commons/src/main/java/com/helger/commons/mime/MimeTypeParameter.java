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
package com.helger.commons.mime;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * This class represents a single MIME type parameter.
 *
 * @author Philip Helger
 */
@Immutable
public class MimeTypeParameter implements Serializable
{
  private final String m_sAttribute;
  private final String m_sValue;
  private final boolean m_bValueRequiresQuoting;

  /**
   * Constructor.
   *
   * @param sAttribute
   *        Parameter name. Must neither be <code>null</code> nor empty and must
   *        match {@link MimeTypeParser#isToken(String)}.
   * @param sValue
   *        The value to use. May neither be <code>null</code> nor empty. Must
   *        not be a valid MIME token.
   */
  public MimeTypeParameter (@Nonnull @Nonempty final String sAttribute, @Nonnull @Nonempty final String sValue)
  {
    if (!MimeTypeParser.isToken (sAttribute))
      throw new IllegalArgumentException ("MimeType parameter name is not a valid token: " + sAttribute);
    ValueEnforcer.notEmpty (sValue, "Value");

    m_sAttribute = sAttribute;
    m_sValue = sValue;
    m_bValueRequiresQuoting = !MimeTypeParser.isToken (sValue);
  }

  /**
   * @return The parameter name. Neither <code>null</code> nor empty and
   *         confirmed to be a valid MIME token.
   */
  @Nonnull
  @Nonempty
  public String getAttribute ()
  {
    return m_sAttribute;
  }

  /**
   * @return The value of the parameter. Neither <code>null</code> nor empty. No
   *         quoting or escaping is applied to this value!
   */
  @Nonnull
  @Nonempty
  public String getValue ()
  {
    return m_sValue;
  }

  /**
   * @return <code>true</code> if the value required quoting/escaping. This is
   *         determined by checking, if the value is a valid MIME token in which
   *         case no quoting is necessary.
   */
  public boolean isValueRequiringQuoting ()
  {
    return m_bValueRequiresQuoting;
  }

  /**
   * @param eQuotingAlgorithm
   *        The quoting algorithm to be used. May not be <code>null</code>.
   * @return The value of the parameter. Neither <code>null</code> nor empty. If
   *         necessary, quoting is applied according to the passed algorithm.
   */
  @Nonnull
  @Nonempty
  public String getValueQuotedIfNecessary (@Nonnull final EMimeQuoting eQuotingAlgorithm)
  {
    return m_bValueRequiresQuoting ? eQuotingAlgorithm.getQuotedString (m_sValue) : m_sValue;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MimeTypeParameter rhs = (MimeTypeParameter) o;
    return m_sAttribute.equals (rhs.m_sAttribute) && m_sValue.equals (rhs.m_sValue);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sAttribute).append (m_sValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("attribute", m_sAttribute)
                                       .append ("value", m_sValue)
                                       .append ("valueRequiresQuoting", m_bValueRequiresQuoting)
                                       .toString ();
  }
}
