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
package com.helger.commons.email;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.string.Strings;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * This class handles a single email address. It is split into an address part and an optional name.
 * The personal name is optional and may be <code>null</code>.
 *
 * @author Philip Helger
 */
@Immutable
public class EmailAddress implements IEmailAddress
{
  private final String m_sAddress;
  private final String m_sPersonal;

  public EmailAddress (@Nonnull final IEmailAddress aAddress)
  {
    this (aAddress.getAddress (), aAddress.getPersonal ());
  }

  public EmailAddress (@Nonnull final String sAddress)
  {
    this (sAddress, null);
  }

  public EmailAddress (@Nonnull final String sAddress, @Nullable final String sPersonal)
  {
    ValueEnforcer.notNull (sAddress, "EmailAddress");
    ValueEnforcer.isTrue (EmailAddressHelper.isValid (sAddress),
                          () -> "The passed email address '" + sAddress + "' is illegal!");
    m_sAddress = EmailAddressHelper.getUnifiedEmailAddress (sAddress);
    m_sPersonal = Strings.isEmpty (sPersonal) ? null : sPersonal;
  }

  @Nonnull
  public String getAddress ()
  {
    return m_sAddress;
  }

  @Nullable
  public String getPersonal ()
  {
    return m_sPersonal;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final EmailAddress rhs = (EmailAddress) o;
    return m_sAddress.equals (rhs.m_sAddress) && EqualsHelper.equals (m_sPersonal, rhs.m_sPersonal);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sAddress).append (m_sPersonal).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Address", m_sAddress)
                                       .appendIfNotNull ("Personal", m_sPersonal)
                                       .getToString ();
  }

  @Nullable
  public static EmailAddress createOnDemand (@Nullable final String sAddress)
  {
    return Strings.isEmpty (sAddress) ? null : new EmailAddress (sAddress);
  }

  @Nullable
  public static EmailAddress createOnDemand (@Nullable final String sAddress, @Nullable final String sPersonal)
  {
    return Strings.isEmpty (sAddress) ? null : new EmailAddress (sAddress, sPersonal);
  }
}
