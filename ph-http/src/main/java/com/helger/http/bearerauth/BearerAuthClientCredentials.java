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
package com.helger.http.bearerauth;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.http.IHttpClientCredentials;

/**
 * Credentials for HTTP Bearer Token authentication as defined in RFC 6750.
 *
 * @author Philip Helger
 * @since 12.5.1
 */
@Immutable
public class BearerAuthClientCredentials implements IHttpClientCredentials
{
  private final String m_sToken;

  /**
   * Constructor
   *
   * @param sToken
   *        The Bearer token to use. May neither be <code>null</code> nor empty and must not contain
   *        whitespace or control characters.
   * @see HttpBearerAuth#isValidToken(String)
   */
  public BearerAuthClientCredentials (@NonNull @Nonempty final String sToken)
  {
    if (!HttpBearerAuth.isValidToken (sToken))
      throw new IllegalArgumentException ("The provided Bearer token is invalid");
    m_sToken = sToken;
  }

  /**
   * @return The Bearer token. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getToken ()
  {
    return m_sToken;
  }

  @NonNull
  @Nonempty
  public String getRequestValue ()
  {
    return HttpBearerAuth.getHttpHeaderValue (m_sToken);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final BearerAuthClientCredentials rhs = (BearerAuthClientCredentials) o;
    return m_sToken.equals (rhs.m_sToken);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sToken).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendPassword ("Token").getToString ();
  }
}
