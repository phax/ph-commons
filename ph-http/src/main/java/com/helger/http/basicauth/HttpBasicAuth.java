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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.codec.base64.Base64;
import com.helger.base.string.StringHelper;
import com.helger.cache.regex.RegExHelper;
import com.helger.http.digestauth.HttpDigestAuth;

/**
 * Handling for HTTP Basic Authentication
 *
 * @author Philip Helger
 */
@Immutable
public final class HttpBasicAuth
{
  public static final String HEADER_VALUE_PREFIX_BASIC = "Basic";
  public static final char USERNAME_PASSWORD_SEPARATOR = ':';
  public static final Charset CHARSET = StandardCharsets.ISO_8859_1;

  private static final Logger LOGGER = LoggerFactory.getLogger (HttpDigestAuth.class);

  @PresentForCodeCoverage
  private static final HttpBasicAuth INSTANCE = new HttpBasicAuth ();

  private HttpBasicAuth ()
  {}

  /**
   * Get the Basic authentication credentials from the passed HTTP header value.
   *
   * @param sAuthHeader
   *        The HTTP header value to be interpreted. May be <code>null</code>.
   * @return <code>null</code> if the passed value is not a correct HTTP Basic Authentication header
   *         value.
   */
  @Nullable
  public static BasicAuthClientCredentials getBasicAuthClientCredentials (@Nullable final String sAuthHeader)
  {
    final String sRealHeader = StringHelper.trim (sAuthHeader);
    if (StringHelper.isEmpty (sRealHeader))
      return null;

    final String [] aElements = RegExHelper.getSplitToArray (sRealHeader, "\\s+", 2);
    if (aElements.length != 2)
    {
      LOGGER.error ("String is not Basic Auth: " + sRealHeader);
      return null;
    }
    if (!aElements[0].equals (HEADER_VALUE_PREFIX_BASIC))
    {
      LOGGER.error ("String does not start with 'Basic': " + sRealHeader);
      return null;
    }
    // Apply Base64 decoding
    final String sEncodedCredentials = aElements[1];
    final String sUsernamePassword = Base64.safeDecodeAsString (sEncodedCredentials, CHARSET);
    if (sUsernamePassword == null)
    {
      LOGGER.error ("Illegal Base64 encoded value '" + sEncodedCredentials + "'");
      return null;
    }
    // Do we have a username/password separator?
    final int nIndex = sUsernamePassword.indexOf (USERNAME_PASSWORD_SEPARATOR);
    if (nIndex >= 0)
      return new BasicAuthClientCredentials (sUsernamePassword.substring (0, nIndex),
                                             sUsernamePassword.substring (nIndex + 1));
    return new BasicAuthClientCredentials (sUsernamePassword);
  }

  /**
   * Create the request HTTP header value for use with the
   * {@link com.helger.http.CHttpHeader#AUTHORIZATION} header name.
   *
   * @param sUserName
   *        The user name to use. May neither be <code>null</code> nor empty.
   * @param sPassword
   *        The password to use. May be <code>null</code> or empty to indicate that no password is
   *        present.
   * @return The HTTP header value to use. Neither <code>null</code> nor empty.
   * @since 9.3.5
   */
  @NonNull
  @Nonempty
  public static String getHttpHeaderValue (@NonNull @Nonempty final String sUserName, @Nullable final String sPassword)
  {
    final String sCombined = StringHelper.getConcatenatedOnDemand (sUserName, USERNAME_PASSWORD_SEPARATOR, sPassword);
    return HEADER_VALUE_PREFIX_BASIC + " " + Base64.safeEncode (sCombined, CHARSET);
  }
}
