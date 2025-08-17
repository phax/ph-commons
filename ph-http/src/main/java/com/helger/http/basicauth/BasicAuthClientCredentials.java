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
package com.helger.http.basicauth;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Credentials for HTTP basic authentication
 *
 * @author Philip Helger
 */
@Immutable
public class BasicAuthClientCredentials
{
  private final String m_sUserName;
  private final String m_sPassword;

  /**
   * Create credentials with a user name only and no password.
   *
   * @param sUserName
   *        The user name to use. May neither be <code>null</code> nor empty.
   */
  public BasicAuthClientCredentials (@Nonnull @Nonempty final String sUserName)
  {
    this (sUserName, null);
  }

  /**
   * Create credentials with a user name and a password.
   *
   * @param sUserName
   *        The user name to use. May neither be <code>null</code> nor empty.
   * @param sPassword
   *        The password to use. May be <code>null</code> or empty to indicate that no password is
   *        present.
   */
  public BasicAuthClientCredentials (@Nonnull @Nonempty final String sUserName, @Nullable final String sPassword)
  {
    m_sUserName = ValueEnforcer.notEmpty (sUserName, "UserName");
    // No difference between null and empty string
    m_sPassword = StringHelper.isEmpty (sPassword) ? null : sPassword;
  }

  /**
   * @return The user name. Neither <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  public String getUserName ()
  {
    return m_sUserName;
  }

  /**
   * @return The password. May be <code>null</code> or empty.
   */
  @Nullable
  public String getPassword ()
  {
    return m_sPassword;
  }

  /**
   * @return <code>true</code> if a non-<code>null</code> non-empty password is present.
   */
  public boolean hasPassword ()
  {
    return m_sPassword != null;
  }

  /**
   * Create the request HTTP header value for use with the
   * {@link com.helger.http.CHttpHeader#AUTHORIZATION} header name.
   *
   * @return The HTTP header value to use. Neither <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  public String getRequestValue ()
  {
    return HttpBasicAuth.getHttpHeaderValue (m_sUserName, m_sPassword);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final BasicAuthClientCredentials rhs = (BasicAuthClientCredentials) o;
    return m_sUserName.equals (rhs.m_sUserName) && EqualsHelper.equals (m_sPassword, rhs.m_sPassword);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sUserName).append (m_sPassword).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("UserName", m_sUserName).appendPassword ("Password").getToString ();
  }
}
