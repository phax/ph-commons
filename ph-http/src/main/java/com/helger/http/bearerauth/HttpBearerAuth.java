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
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.string.StringHelper;
import com.helger.cache.regex.RegExHelper;

/**
 * Handling for HTTP Bearer Token Authentication as defined in RFC 6750.
 *
 * @author Philip Helger
 * @since 12.5.1
 */
@Immutable
public final class HttpBearerAuth
{
  public static final String HEADER_VALUE_PREFIX_BEARER = "Bearer";

  private static final Logger LOGGER = LoggerFactory.getLogger (HttpBearerAuth.class);

  @PresentForCodeCoverage
  private static final HttpBearerAuth INSTANCE = new HttpBearerAuth ();

  private HttpBearerAuth ()
  {}

  /**
   * Check if the passed token can be used as a Bearer token. A valid token must not be empty and
   * must not contain any whitespace or control character, as these would break the HTTP header
   * value.
   *
   * @param sToken
   *        The token to check. May be <code>null</code>.
   * @return <code>true</code> if the token is valid, <code>false</code> if not.
   */
  public static boolean isValidToken (@Nullable final String sToken)
  {
    if (StringHelper.isEmpty (sToken))
      return false;
    for (final char c : sToken.toCharArray ())
      if (Character.isWhitespace (c) || Character.isISOControl (c))
        return false;
    return true;
  }

  /**
   * Get the Bearer authentication credentials from the passed HTTP header value. The
   * authentication scheme is matched case-insensitively according to RFC 9110 section 11.1.
   *
   * @param sAuthHeader
   *        The HTTP header value to be interpreted. May be <code>null</code>.
   * @return <code>null</code> if the passed value is not a correct HTTP Bearer Authentication header
   *         value.
   */
  @Nullable
  public static BearerAuthClientCredentials getBearerAuthClientCredentials (@Nullable final String sAuthHeader)
  {
    final String sRealHeader = StringHelper.trim (sAuthHeader);
    if (StringHelper.isEmpty (sRealHeader))
      return null;

    final String [] aElements = RegExHelper.getSplitToArray (sRealHeader, "\\s+", 2);
    if (aElements.length != 2)
    {
      LOGGER.error ("String is not Bearer Auth");
      return null;
    }
    if (!aElements[0].equalsIgnoreCase (HEADER_VALUE_PREFIX_BEARER))
    {
      LOGGER.error ("String does not start with '" + HEADER_VALUE_PREFIX_BEARER + "'");
      return null;
    }
    final String sToken = aElements[1];
    if (!isValidToken (sToken))
    {
      LOGGER.error ("String contains an invalid Bearer token");
      return null;
    }
    return new BearerAuthClientCredentials (sToken);
  }

  /**
   * Create the request HTTP header value for use with the
   * {@link com.helger.http.CHttpHeader#AUTHORIZATION} header name.
   *
   * @param sToken
   *        The Bearer token to use. May neither be <code>null</code> nor empty.
   * @return The HTTP header value to use. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public static String getHttpHeaderValue (@NonNull @Nonempty final String sToken)
  {
    return HEADER_VALUE_PREFIX_BEARER + " " + sToken;
  }
}
