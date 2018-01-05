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
package com.helger.security.password.hash;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;
import com.helger.security.password.salt.IPasswordSalt;

/**
 * This class combines password hash and the used algorithm.
 *
 * @author Philip Helger
 */
@Immutable
public final class PasswordHash implements Serializable
{
  private final String m_sAlgorithmName;
  private final IPasswordSalt m_aSalt;
  private final String m_sPasswordHashValue;

  public PasswordHash (@Nonnull @Nonempty final String sAlgorithmName,
                       @Nullable final IPasswordSalt aSalt,
                       @Nonnull @Nonempty final String sPasswordHashValue)
  {
    m_sAlgorithmName = ValueEnforcer.notEmpty (sAlgorithmName, "AlgorithmName");
    m_aSalt = aSalt;
    m_sPasswordHashValue = ValueEnforcer.notEmpty (sPasswordHashValue, "PasswordHashValue");
  }

  @Nonnull
  @Nonempty
  public String getAlgorithmName ()
  {
    return m_sAlgorithmName;
  }

  public boolean hasSalt ()
  {
    return m_aSalt != null;
  }

  @Nullable
  public IPasswordSalt getSalt ()
  {
    return m_aSalt;
  }

  @Nullable
  public String getSaltAsString ()
  {
    return m_aSalt == null ? null : m_aSalt.getSaltString ();
  }

  @Nonnull
  @Nonempty
  public String getPasswordHashValue ()
  {
    return m_sPasswordHashValue;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final PasswordHash rhs = (PasswordHash) o;
    return m_sAlgorithmName.equals (rhs.m_sAlgorithmName) &&
           EqualsHelper.equals (m_aSalt, rhs.m_aSalt) &&
           m_sPasswordHashValue.equals (rhs.m_sPasswordHashValue);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sAlgorithmName)
                                       .append (m_aSalt)
                                       .append (m_sPasswordHashValue)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("algorithmName", m_sAlgorithmName)
                                       .appendIfNotNull ("salt", m_aSalt)
                                       .append ("passwordHashValue", m_sPasswordHashValue)
                                       .getToString ();
  }
}
