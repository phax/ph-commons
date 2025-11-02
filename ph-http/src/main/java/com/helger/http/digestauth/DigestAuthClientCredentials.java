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
package com.helger.http.digestauth;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.CGlobal;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringParser;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.http.RFC1945Helper;

/**
 * Credentials for HTTP digest authentication
 *
 * @author Philip Helger
 */
@Immutable
public class DigestAuthClientCredentials
{
  public static final int EXPECTED_RESPONSE_LENGTH = 32;
  public static final int EXPECTED_NONCE_COUNT_LENGTH = 8;

  private final String m_sUserName;
  private final String m_sRealm;
  private final String m_sServerNonce;
  private final String m_sDigestURI;
  private final String m_sResponse;
  private final String m_sAlgorithm;
  private final String m_sClientNonce;
  private final String m_sOpaque;
  private final String m_sMessageQOP;
  private final int m_nNonceCount;

  public DigestAuthClientCredentials (@NonNull @Nonempty final String sUserName,
                                      @NonNull @Nonempty final String sRealm,
                                      @NonNull @Nonempty final String sServerNonce,
                                      @NonNull @Nonempty final String sDigestURI,
                                      @NonNull @Nonempty final String sResponse,
                                      @Nullable final String sAlgorithm,
                                      @Nullable final String sClientNonce,
                                      @Nullable final String sOpaque,
                                      @Nullable final String sMessageQOP,
                                      @Nullable final String sNonceCount)
  {
    ValueEnforcer.notEmpty (sUserName, "UserName");
    ValueEnforcer.notEmpty (sRealm, "Realm");
    ValueEnforcer.notEmpty (sServerNonce, "ServerNonce");
    ValueEnforcer.notEmpty (sDigestURI, "DigestURI");
    ValueEnforcer.notEmpty (sResponse, "Response");
    if (sResponse.length () != EXPECTED_RESPONSE_LENGTH)
      throw new IllegalArgumentException ("The 'response' value must be a 32-byte hex string!");
    if (!RFC1945Helper.isLowerHexNotEmpty (sResponse))
      throw new IllegalArgumentException ("The 'response' value must consist of all lowercase hex chars!");
    if (StringHelper.isNotEmpty (sMessageQOP) && StringHelper.isEmpty (sClientNonce))
      throw new IllegalArgumentException ("If 'qop' is present 'cnonce' must also be present!");
    if (StringHelper.isEmpty (sMessageQOP) && StringHelper.isNotEmpty (sClientNonce))
      throw new IllegalArgumentException ("If 'qop' is not present 'cnonce' must also not be present!");
    if (StringHelper.isNotEmpty (sMessageQOP) && StringHelper.isEmpty (sNonceCount))
      throw new IllegalArgumentException ("If 'qop' is present 'nc' must also be present!");
    if (StringHelper.isEmpty (sMessageQOP) && StringHelper.isNotEmpty (sNonceCount))
      throw new IllegalArgumentException ("If 'qop' is not present 'nc' must also not be present!");
    if (sNonceCount != null && sNonceCount.length () != EXPECTED_NONCE_COUNT_LENGTH)
      throw new IllegalArgumentException ("The 'nonce-count' value must be a 8-byte hex string!");
    if (sNonceCount != null && !RFC1945Helper.isHexNotEmpty (sNonceCount))
      throw new IllegalArgumentException ("The 'nonce-count' value must consist only of hex chars!");
    m_sUserName = sUserName;
    m_sRealm = sRealm;
    m_sServerNonce = sServerNonce;
    m_sDigestURI = sDigestURI;
    m_sResponse = sResponse;
    m_sAlgorithm = sAlgorithm;
    m_sClientNonce = sClientNonce;
    m_sOpaque = sOpaque;
    m_sMessageQOP = sMessageQOP;
    m_nNonceCount = sNonceCount == null ? -1 : StringParser.parseInt (sNonceCount, CGlobal.HEX_RADIX, -1);
    if (sNonceCount != null && m_nNonceCount == -1)
      throw new IllegalArgumentException ("The 'nonce-count' parameter is invalid: '" + sNonceCount + "'");
  }

  /**
   * @return The user name. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getUserName ()
  {
    return m_sUserName;
  }

  /**
   * @return The realm. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getRealm ()
  {
    return m_sRealm;
  }

  /**
   * @return The nonce. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getServerNonce ()
  {
    return m_sServerNonce;
  }

  /**
   * @return The digest URI. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getDigestURI ()
  {
    return m_sDigestURI;
  }

  @Nullable
  public String getResponse ()
  {
    return m_sResponse;
  }

  @Nullable
  public String getAlgorithm ()
  {
    return m_sAlgorithm;
  }

  @Nullable
  public String getClientNonce ()
  {
    return m_sClientNonce;
  }

  @Nullable
  public String getOpaque ()
  {
    return m_sOpaque;
  }

  @Nullable
  public String getMessageQOP ()
  {
    return m_sMessageQOP;
  }

  @CheckForSigned
  public int getNonceCount ()
  {
    return m_nNonceCount;
  }

  @NonNull
  @Nonempty
  public String getRequestValue ()
  {
    final StringBuilder aSB = new StringBuilder (HttpDigestAuth.HEADER_VALUE_PREFIX_DIGEST);
    aSB.append (" username=")
       .append (RFC1945Helper.getQuotedTextString (m_sUserName))
       .append (", realm=")
       .append (RFC1945Helper.getQuotedTextString (m_sRealm))
       .append (", nonce=")
       .append (RFC1945Helper.getQuotedTextString (m_sServerNonce))
       .append (", uri=")
       .append (RFC1945Helper.getQuotedTextString (m_sDigestURI))
       .append (", response=")
       .append (RFC1945Helper.getQuotedTextString (m_sResponse));
    if (m_sAlgorithm != null)
      aSB.append (", algorithm=").append (m_sAlgorithm);
    if (m_sClientNonce != null)
      aSB.append (", cnonce=").append (RFC1945Helper.getQuotedTextString (m_sClientNonce));
    if (m_sOpaque != null)
      aSB.append (", opaque=").append (RFC1945Helper.getQuotedTextString (m_sOpaque));
    if (m_sMessageQOP != null)
      aSB.append (", qop=").append (m_sMessageQOP);
    if (m_nNonceCount > 0)
      aSB.append (", nc=").append (HttpDigestAuth.getNonceCountString (m_nNonceCount));
    return aSB.toString ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final DigestAuthClientCredentials rhs = (DigestAuthClientCredentials) o;
    return m_sUserName.equals (rhs.m_sUserName) &&
           m_sRealm.equals (rhs.m_sRealm) &&
           m_sServerNonce.equals (rhs.m_sServerNonce) &&
           m_sDigestURI.equals (rhs.m_sDigestURI) &&
           m_sResponse.equals (rhs.m_sResponse) &&
           EqualsHelper.equals (m_sAlgorithm, rhs.m_sAlgorithm) &&
           EqualsHelper.equals (m_sClientNonce, rhs.m_sClientNonce) &&
           EqualsHelper.equals (m_sOpaque, rhs.m_sOpaque) &&
           EqualsHelper.equals (m_sMessageQOP, rhs.m_sMessageQOP) &&
           m_nNonceCount == rhs.m_nNonceCount;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sUserName)
                                       .append (m_sRealm)
                                       .append (m_sServerNonce)
                                       .append (m_sDigestURI)
                                       .append (m_sResponse)
                                       .append (m_sAlgorithm)
                                       .append (m_sClientNonce)
                                       .append (m_sOpaque)
                                       .append (m_sMessageQOP)
                                       .append (m_nNonceCount)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("userName", m_sUserName)
                                       .append ("realm", m_sRealm)
                                       .append ("serverNonce", m_sServerNonce)
                                       .append ("digestUri", m_sDigestURI)
                                       .append ("response", m_sResponse)
                                       .appendIfNotNull ("algorithm", m_sAlgorithm)
                                       .appendIfNotNull ("clientNonce", m_sClientNonce)
                                       .appendIfNotNull ("opaque", m_sOpaque)
                                       .appendIfNotNull ("messageQop", m_sMessageQOP)
                                       .append ("noncecount", m_nNonceCount)
                                       .getToString ();
  }
}
