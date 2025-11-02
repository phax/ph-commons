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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringHex;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.http.EHttpMethod;
import com.helger.http.RFC1945Helper;
import com.helger.security.messagedigest.EMessageDigestAlgorithm;
import com.helger.security.messagedigest.MessageDigestValue;

/**
 * Handling for HTTP Digest Authentication
 *
 * @author Philip Helger
 */
@Immutable
public final class HttpDigestAuth
{
  public static final String HEADER_VALUE_PREFIX_DIGEST = "Digest";

  public static final String ALGORITHM_MD5 = "MD5";
  public static final String ALGORITHM_MD5_SESS = "MD5-sess";
  public static final String DEFAULT_ALGORITHM = ALGORITHM_MD5;

  public static final String QOP_AUTH = "auth";
  public static final String QOP_AUTH_INT = "auth-int";
  public static final String DEFAULT_QOP = QOP_AUTH;

  private static final Logger LOGGER = LoggerFactory.getLogger (HttpDigestAuth.class);
  private static final char SEPARATOR = ':';
  private static final Charset CHARSET = StandardCharsets.ISO_8859_1;

  @PresentForCodeCoverage
  private static final HttpDigestAuth INSTANCE = new HttpDigestAuth ();

  private HttpDigestAuth ()
  {}

  /**
   * Get the parameters of a Digest authentication string. It may be used for both client and server
   * handling.
   *
   * @param sAuthHeader
   *        The HTTP header value to be interpreted. May be <code>null</code>.
   * @return <code>null</code> if the passed value cannot be parsed as a HTTP Digest Authentication
   *         value, a {@link ICommonsOrderedMap} with all parameter name-value pairs in the order
   *         they are contained.
   */
  @Nullable
  public static ICommonsOrderedMap <String, String> getDigestAuthParams (@Nullable final String sAuthHeader)
  {
    final String sRealHeader = StringHelper.trim (sAuthHeader);
    if (StringHelper.isEmpty (sRealHeader))
      return null;
    if (!sRealHeader.startsWith (HEADER_VALUE_PREFIX_DIGEST))
    {
      LOGGER.error ("String does not start with '" + HEADER_VALUE_PREFIX_DIGEST + "'");
      return null;
    }
    final char [] aChars = sRealHeader.toCharArray ();
    int nIndex = HEADER_VALUE_PREFIX_DIGEST.length ();
    if (nIndex >= aChars.length || !RFC1945Helper.isLinearWhitespaceChar (aChars[nIndex]))
    {
      LOGGER.error ("No whitespace after '" + HEADER_VALUE_PREFIX_DIGEST + "'");
      return null;
    }
    nIndex++;

    final ICommonsOrderedMap <String, String> aParams = new CommonsLinkedHashMap <> ();
    while (true)
    {
      // Skip all spaces
      while (nIndex < aChars.length && RFC1945Helper.isLinearWhitespaceChar (aChars[nIndex]))
        nIndex++;

      // Find token name
      int nStartIndex = nIndex;
      while (nIndex < aChars.length && RFC1945Helper.isTokenChar (aChars[nIndex]))
        nIndex++;
      if (nStartIndex == nIndex)
      {
        LOGGER.error ("No token and no whitespace found for auth-param name: '" + aChars[nIndex] + "'");
        return null;
      }
      final String sToken = sRealHeader.substring (nStartIndex, nIndex);

      // Skip all spaces
      while (nIndex < aChars.length && RFC1945Helper.isLinearWhitespaceChar (aChars[nIndex]))
        nIndex++;
      if (nIndex >= aChars.length || aChars[nIndex] != '=')
      {
        LOGGER.error ("No separator char '=' found after '" + sToken + "'");
        return null;
      }
      nIndex++;

      // Skip all spaces
      while (nIndex < aChars.length && RFC1945Helper.isLinearWhitespaceChar (aChars[nIndex]))
        nIndex++;
      if (nIndex >= aChars.length)
      {
        LOGGER.error ("Found nothing after '=' of '" + sToken + "'");
        return null;
      }
      String sValue;
      if (aChars[nIndex] == RFC1945Helper.QUOTEDTEXT_BEGIN)
      {
        // Quoted string
        ++nIndex;
        nStartIndex = nIndex;
        while (nIndex < aChars.length && RFC1945Helper.isQuotedTextChar (aChars[nIndex]))
          nIndex++;
        if (nIndex >= aChars.length)
        {
          LOGGER.error ("Unexpected EOF in quoted text for '" + sToken + "'");
          return null;
        }
        if (aChars[nIndex] != RFC1945Helper.QUOTEDTEXT_END)
        {
          LOGGER.error ("Quoted string of token '" +
                        sToken +
                        "' is not terminated correctly: '" +
                        aChars[nIndex] +
                        "'");
          return null;
        }
        sValue = sRealHeader.substring (nStartIndex, nIndex);

        // Skip termination char
        nIndex++;
      }
      else
      {
        // Token
        nStartIndex = nIndex;
        while (nIndex < aChars.length && RFC1945Helper.isTokenChar (aChars[nIndex]))
          nIndex++;
        if (nStartIndex == nIndex)
        {
          LOGGER.error ("No token and no whitespace found for auth-param value of '" +
                        sToken +
                        "': '" +
                        aChars[nIndex] +
                        "'");
          return null;
        }
        sValue = sRealHeader.substring (nStartIndex, nIndex);
      }
      // Remember key/value pair
      aParams.put (sToken, sValue);

      // Skip all spaces
      while (nIndex < aChars.length && RFC1945Helper.isLinearWhitespaceChar (aChars[nIndex]))
        nIndex++;

      // Check if there are any additional parameters
      if (nIndex >= aChars.length)
      {
        // No more tokens - we're done
        break;
      }
      // If there is a comma, another parameter is expected
      if (aChars[nIndex] != ',')
      {
        LOGGER.error ("Illegal character after auth-param '" + sToken + "': '" + aChars[nIndex] + "'");
        return null;
      }
      ++nIndex;
      if (nIndex >= aChars.length)
      {
        LOGGER.error ("Found nothing after continuation of auth-param '" + sToken + "'");
        return null;
      }
    }
    return aParams;
  }

  /**
   * Get the Digest authentication credentials from the passed HTTP header value.
   *
   * @param sAuthHeader
   *        The HTTP header value to be interpreted. May be <code>null</code>.
   * @return <code>null</code> if the passed value is not a correct HTTP Digest Authentication
   *         header value.
   */
  @Nullable
  public static DigestAuthClientCredentials getDigestAuthClientCredentials (@Nullable final String sAuthHeader)
  {
    final ICommonsOrderedMap <String, String> aParams = getDigestAuthParams (sAuthHeader);
    if (aParams == null)
      return null;

    final String sUserName = aParams.remove ("username");
    if (sUserName == null)
    {
      LOGGER.error ("Digest Auth does not container 'username'");
      return null;
    }
    final String sRealm = aParams.remove ("realm");
    if (sRealm == null)
    {
      LOGGER.error ("Digest Auth does not container 'realm'");
      return null;
    }
    final String sNonce = aParams.remove ("nonce");
    if (sNonce == null)
    {
      LOGGER.error ("Digest Auth does not container 'nonce'");
      return null;
    }
    final String sDigestURI = aParams.remove ("uri");
    if (sDigestURI == null)
    {
      LOGGER.error ("Digest Auth does not container 'uri'");
      return null;
    }
    final String sResponse = aParams.remove ("response");
    if (sResponse == null)
    {
      LOGGER.error ("Digest Auth does not container 'response'");
      return null;
    }
    final String sAlgorithm = aParams.remove ("algorithm");
    final String sCNonce = aParams.remove ("cnonce");
    final String sOpaque = aParams.remove ("opaque");
    final String sMessageQOP = aParams.remove ("qop");
    final String sNonceCount = aParams.remove ("nc");
    if (aParams.isNotEmpty ())
      LOGGER.warn ("Digest Auth contains unhandled parameters: " + aParams.toString ());

    return new DigestAuthClientCredentials (sUserName,
                                            sRealm,
                                            sNonce,
                                            sDigestURI,
                                            sResponse,
                                            sAlgorithm,
                                            sCNonce,
                                            sOpaque,
                                            sMessageQOP,
                                            sNonceCount);
  }

  @Nullable
  public static String getNonceCountString (@CheckForSigned final int nNonceCount)
  {
    return nNonceCount <= 0 ? null : StringHelper.getLeadingZero (StringHex.getHexString (nNonceCount), 8);
  }

  @NonNull
  private static String _md5 (@NonNull final String s)
  {
    return MessageDigestValue.create (s.getBytes (CHARSET), EMessageDigestAlgorithm.MD5).getHexEncodedDigestString ();
  }

  /**
   * Create HTTP Digest auth credentials for a client
   *
   * @param eMethod
   *        The HTTP method of the request. May not be <code>null</code>.
   * @param sDigestURI
   *        The URI from Request-URI of the Request-Line; duplicated here because proxies are
   *        allowed to change the Request-Line in transit. May neither be <code>null</code> nor
   *        empty.
   * @param sUserName
   *        User name to use. May neither be <code>null</code> nor empty.
   * @param sPassword
   *        The user's password. May not be <code>null</code>.
   * @param sRealm
   *        The realm as provided by the server. May neither be <code>null</code> nor empty.
   * @param sServerNonce
   *        The nonce as supplied by the server. May neither be <code>null</code> nor empty.
   * @param sAlgorithm
   *        The algorithm as provided by the server. Currently only {@link #ALGORITHM_MD5} and
   *        {@link #ALGORITHM_MD5_SESS} is supported. If it is <code>null</code> than
   *        {@link #ALGORITHM_MD5} is used as default.
   * @param sClientNonce
   *        The client nonce to be used. Must be present if message QOP is specified or if algorithm
   *        is {@link #ALGORITHM_MD5_SESS}.<br>
   *        This MUST be specified if a qop directive is sent, and MUST NOT be specified if the
   *        server did not send a qop directive in the WWW-Authenticate header field. The
   *        cnonce-value is an opaque quoted string value provided by the client and used by both
   *        client and server to avoid chosen plain text attacks, to provide mutual authentication,
   *        and to provide some message integrity protection. See the descriptions below of the
   *        calculation of the response- digest and request-digest values.
   * @param sOpaque
   *        The opaque value as supplied by the server. May be <code>null</code> .
   * @param sMessageQOP
   *        The message QOP. Currently only {@link #QOP_AUTH} is supported. If <code>null</code> is
   *        passed, than {@link #QOP_AUTH} with backward compatibility handling for RFC 2069 is
   *        applied.<br>
   *        Indicates what "quality of protection" the client has applied to the message. If
   *        present, its value MUST be one of the alternatives the server indicated it supports in
   *        the WWW-Authenticate header. These values affect the computation of the request-digest.
   *        Note that this is a single token, not a quoted list of alternatives as in WWW-
   *        Authenticate. This directive is optional in order to preserve backward compatibility
   *        with a minimal implementation of RFC 2069 [6], but SHOULD be used if the server
   *        indicated that qop is supported by providing a qop directive in the WWW-Authenticate
   *        header field.
   * @param nNonceCount
   *        This MUST be specified if a qop directive is sent (see above), and MUST NOT be specified
   *        if the server did not send a qop directive in the WWW-Authenticate header field. The
   *        nc-value is the hexadecimal count of the number of requests (including the current
   *        request) that the client has sent with the nonce value in this request. For example, in
   *        the first request sent in response to a given nonce value, the client sends
   *        "nc=00000001". The purpose of this directive is to allow the server to detect request
   *        replays by maintaining its own copy of this count - if the same nc-value is seen twice,
   *        then the request is a replay.
   * @return The created DigestAuthCredentials
   */
  @NonNull
  public static DigestAuthClientCredentials createDigestAuthClientCredentials (@NonNull final EHttpMethod eMethod,
                                                                               @NonNull @Nonempty final String sDigestURI,
                                                                               @NonNull @Nonempty final String sUserName,
                                                                               @NonNull final String sPassword,
                                                                               @NonNull @Nonempty final String sRealm,
                                                                               @NonNull @Nonempty final String sServerNonce,
                                                                               @Nullable final String sAlgorithm,
                                                                               @Nullable final String sClientNonce,
                                                                               @Nullable final String sOpaque,
                                                                               @Nullable final String sMessageQOP,
                                                                               @CheckForSigned final int nNonceCount)
  {
    ValueEnforcer.notNull (eMethod, "Method");
    ValueEnforcer.notEmpty (sDigestURI, "DigestURI");
    ValueEnforcer.notEmpty (sUserName, "UserName");
    ValueEnforcer.notNull (sPassword, "Password");
    ValueEnforcer.notEmpty (sRealm, "Realm");
    ValueEnforcer.notEmpty (sServerNonce, "ServerNonce");
    if (sMessageQOP != null && StringHelper.isEmpty (sClientNonce))
      throw new IllegalArgumentException ("If a QOP is defined, client nonce must be set!");
    if (sMessageQOP != null && nNonceCount <= 0)
      throw new IllegalArgumentException ("If a QOP is defined, nonce count must be positive!");

    final String sRealAlgorithm = sAlgorithm == null ? DEFAULT_ALGORITHM : sAlgorithm;
    if (!sRealAlgorithm.equals (ALGORITHM_MD5) && !sRealAlgorithm.equals (ALGORITHM_MD5_SESS))
      throw new IllegalArgumentException ("Currently only '" +
                                          ALGORITHM_MD5 +
                                          "' and '" +
                                          ALGORITHM_MD5_SESS +
                                          "' algorithms are supported!");

    if (sMessageQOP != null && !sMessageQOP.equals (QOP_AUTH))
      throw new IllegalArgumentException ("Currently only '" + QOP_AUTH + "' QOP is supported!");

    // Nonce must always by 8 chars long
    final String sNonceCount = getNonceCountString (nNonceCount);

    // Create HA1
    String sHA1 = _md5 (sUserName + SEPARATOR + sRealm + SEPARATOR + sPassword);
    if (sRealAlgorithm.equals (ALGORITHM_MD5_SESS))
    {
      if (StringHelper.isEmpty (sClientNonce))
        throw new IllegalArgumentException ("Algorithm requires client nonce!");
      sHA1 = _md5 (sHA1 + SEPARATOR + sServerNonce + SEPARATOR + sClientNonce);
    }
    // Create HA2
    // Method name must be upper-case!
    final String sHA2 = _md5 (eMethod.getName () + SEPARATOR + sDigestURI);

    // Create the request digest - result must be all lowercase hex chars!
    String sRequestDigest;
    if (sMessageQOP == null)
    {
      // RFC 2069 backwards compatibility
      sRequestDigest = _md5 (sHA1 + SEPARATOR + sServerNonce + SEPARATOR + sHA2);
    }
    else
    {
      sRequestDigest = _md5 (sHA1 +
                             SEPARATOR +
                             sServerNonce +
                             SEPARATOR +
                             sNonceCount +
                             SEPARATOR +
                             sClientNonce +
                             SEPARATOR +
                             sMessageQOP +
                             SEPARATOR +
                             sHA2);
    }
    return new DigestAuthClientCredentials (sUserName,
                                            sRealm,
                                            sServerNonce,
                                            sDigestURI,
                                            sRequestDigest,
                                            sAlgorithm,
                                            sClientNonce,
                                            sOpaque,
                                            sMessageQOP,
                                            sNonceCount);
  }
}
