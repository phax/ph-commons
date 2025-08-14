/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.datetime.expiration;

import java.time.LocalDateTime;

import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.state.EChange;
import com.helger.base.string.ToStringGenerator;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Default implementation of {@link IMutableExpirable}
 *
 * @author Philip Helger
 */
public class Expirable implements IMutableExpirable
{
  private LocalDateTime m_aExpirationDateTime;

  public Expirable ()
  {}

  public Expirable (@Nullable final LocalDateTime aExpirationDateTime)
  {
    m_aExpirationDateTime = aExpirationDateTime;
  }

  @Nullable
  public LocalDateTime getExpirationDateTime ()
  {
    return m_aExpirationDateTime;
  }

  @Nonnull
  public EChange setExpirationDateTime (@Nullable final LocalDateTime aExpirationDateTime)
  {
    final Object aObj2 = m_aExpirationDateTime;
    if (EqualsHelper.equals (aExpirationDateTime, aObj2))
      return EChange.UNCHANGED;
    m_aExpirationDateTime = aExpirationDateTime;
    return EChange.CHANGED;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final Expirable rhs = (Expirable) o;
    final Object aObj1 = m_aExpirationDateTime;
    return EqualsHelper.equals (aObj1, rhs.m_aExpirationDateTime);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aExpirationDateTime).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("expirationDT", m_aExpirationDateTime).getToString ();
  }
}
