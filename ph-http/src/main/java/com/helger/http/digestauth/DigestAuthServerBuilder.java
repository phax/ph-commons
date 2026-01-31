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
package com.helger.http.digestauth;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.ETriState;
import com.helger.base.string.StringImplode;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.ICommonsOrderedSet;
import com.helger.http.RFC1945Helper;
import com.helger.url.ISimpleURL;

/**
 * Helper class to build the value of the {@link com.helger.http.CHttpHeader#WWW_AUTHENTICATE} value
 * send from the server to client.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class DigestAuthServerBuilder
{
  private String m_sRealm;
  private final ICommonsOrderedSet <String> m_aDomains = new CommonsLinkedHashSet <> ();
  private String m_sNonce;
  private String m_sOpaque;
  private ETriState m_eStale = ETriState.UNDEFINED;
  private String m_sAlgorithm;
  private final ICommonsOrderedSet <String> m_aQOPs = new CommonsLinkedHashSet <> ();

  public DigestAuthServerBuilder ()
  {}

  /**
   * A string to be displayed to users so they know which username and password to use. This string
   * should contain at least the name of the host performing the authentication and might
   * additionally indicate the collection of users who might have access. An example might be
   * "registered_users@gotham.news.com".
   *
   * @param sRealm
   *        The realm to be used. May not be <code>null</code> and should not be empty.
   * @return this
   */
  @NonNull
  public DigestAuthServerBuilder setRealm (@NonNull final String sRealm)
  {
    if (!RFC1945Helper.isQuotedTextContent (sRealm))
      throw new IllegalArgumentException ("realm is invalid: " + sRealm);

    m_sRealm = sRealm;
    return this;
  }

  /**
   * Add an URIs, as specified in RFC XURI, that define the protection space. If a URI is an
   * abs_path, it is relative to the canonical root URL of the server being accessed. An absoluteURI
   * in this list may refer to a different server than the one being accessed. The client can use
   * this list to determine the set of URIs for which the same authentication information may be
   * sent: any URI that has a URI in this list as a prefix (after both have been made absolute) may
   * be assumed to be in the same protection space. If this directive is omitted or its value is
   * empty, the client should assume that the protection space consists of all URIs on the
   * responding server. This directive is not meaningful in Proxy-Authenticate headers, for which
   * the protection space is always the entire proxy; if present it should be ignored.
   *
   * @param aURL
   *        The absolute or relative path which is protected. May not be <code>null</code>.
   * @return this
   */
  @NonNull
  public DigestAuthServerBuilder addDomain (@NonNull final ISimpleURL aURL)
  {
    ValueEnforcer.notNull (aURL, "Url");

    final String sURL = aURL.getAsString ();
    // Check for spaces, as all URLs are concatenated with spaces!
    if (sURL.indexOf (' ') >= 0)
      throw new IllegalArgumentException ("URL may not contain spaces: '" + sURL + "'");
    m_aDomains.add (sURL);
    return this;
  }

  /**
   * A server-specified data string which should be uniquely generated each time a 401 response is
   * made. It is recommended that this string be base64 or hexadecimal data. Specifically, since the
   * string is passed in the header lines as a quoted string, the double-quote character is not
   * allowed.<br>
   * The contents of the nonce are implementation dependent. The quality of the implementation
   * depends on a good choice. A nonce might, for example, be constructed as the base 64 encoding
   * of<br>
   * time-stamp H(time-stamp ":" ETag ":" private-key)<br>
   * where time-stamp is a server-generated time or other non-repeating value, ETag is the value of
   * the HTTP ETag header associated with the requested entity, and private-key is data known only
   * to the server. With a nonce of this form a server would recalculate the hash portion after
   * receiving the client authentication header and reject the request if it did not match the nonce
   * from that header or if the time-stamp value is not recent enough. In this way the server can
   * limit the time of the nonce’s validity. The inclusion of the ETag prevents a replay request for
   * an updated version of the resource. (Note: including the IP address of the client in the nonce
   * would appear to offer the server the ability to limit the reuse of the nonce to the same client
   * that originally got it. However, that would break proxy farms, where requests from a single
   * user often go through different proxies in the farm. Also, IP address spoofing is not that
   * hard.)<br>
   * An implementation might choose not to accept a previously used nonce or a previously used
   * digest, in order to protect against a replay attack. Or, an implementation might choose to use
   * one-time nonces or digests for POST or PUT requests and a time-stamp for GET requests. For more
   * details on the issues involved see section 4. of this document.<br>
   * The nonce is opaque to the client.
   *
   * @param sNonce
   *        The nonce value to be set. May not be <code>null</code>.
   * @return this
   */
  @NonNull
  public DigestAuthServerBuilder setNonce (@NonNull final String sNonce)
  {
    if (!RFC1945Helper.isQuotedTextContent (sNonce))
      throw new IllegalArgumentException ("nonce is invalid: " + sNonce);

    m_sNonce = sNonce;
    return this;
  }

  /**
   * A string of data, specified by the server, which should be returned by the client unchanged in
   * the Authorization header of subsequent requests with URIs in the same protection space. It is
   * recommended that this string be base64 or hexadecimal data.
   *
   * @param sOpaque
   *        The opaque value. May not be <code>null</code>.
   * @return this
   */
  @NonNull
  public DigestAuthServerBuilder setOpaque (@NonNull final String sOpaque)
  {
    if (!RFC1945Helper.isQuotedTextContent (sOpaque))
      throw new IllegalArgumentException ("opaque is invalid: " + sOpaque);

    m_sOpaque = sOpaque;
    return this;
  }

  /**
   * A flag, indicating that the previous request from the client was rejected because the nonce
   * value was stale. If stale is TRUE (case-insensitive), the client may wish to simply retry the
   * request with a new encrypted response, without reprompting the user for a new username and
   * password. The server should only set stale to TRUE if it receives a request for which the nonce
   * is invalid but with a valid digest for that nonce (indicating that the client knows the correct
   * username/password). If stale is FALSE, or anything other than TRUE, or the stale directive is
   * not present, the username and/or password are invalid, and new values must be obtained.
   *
   * @param eStale
   *        Stale value. May not be <code>null</code>.
   * @return this
   */
  @NonNull
  public DigestAuthServerBuilder setStale (@NonNull final ETriState eStale)
  {
    m_eStale = ValueEnforcer.notNull (eStale, "Stale");
    return this;
  }

  /**
   * A string indicating a pair of algorithms used to produce the digest and a checksum. If this is
   * not present it is assumed to be "MD5". If the algorithm is not understood, the challenge should
   * be ignored (and a different one used, if there is more than one). In this document the string
   * obtained by applying the digest algorithm to the data "data" with secret "secret" will be
   * denoted by KD(secret, data), and the string obtained by applying the checksum algorithm to the
   * data "data" will be denoted H(data). The notation unq(X) means the value of the quoted-string X
   * without the surrounding quotes.<br>
   * For the "MD5" and "MD5-sess" algorithms<br>
   * H(data) = MD5(data)<br>
   * and<br>
   * KD(secret, data) = H(concat(secret, ":", data))<br>
   * i.e., the digest is the MD5 of the secret concatenated with a colon concatenated with the data.
   * The "MD5-sess" algorithm is intended to allow efficient 3rd party authentication servers; for
   * the difference in usage, see the description in section 3.2.2.2.
   *
   * @param sAlgorithm
   *        Algorithm name
   * @return this
   */
  @NonNull
  public DigestAuthServerBuilder setAlgorithm (@NonNull final String sAlgorithm)
  {
    if (!RFC1945Helper.isToken (sAlgorithm))
      throw new IllegalArgumentException ("The passed algorithm is not a valid token: " + sAlgorithm);

    m_sAlgorithm = sAlgorithm;
    return this;
  }

  /**
   * This directive is optional, but is made so only for backward compatibility with RFC 2069 [6];
   * it SHOULD be used by all implementations compliant with this version of the Digest scheme. If
   * present, it is a quoted string of one or more tokens indicating the "quality of protection"
   * values supported by the server. The value "auth" indicates authentication; the value "auth-int"
   * indicates authentication with integrity protection; see the descriptions below for calculating
   * the response directive value for the application of this choice. Unrecognized options MUST be
   * ignored.
   *
   * @param sQOP
   *        The qop-option to add. May not be <code>null</code>.
   * @return this
   */
  @NonNull
  public DigestAuthServerBuilder addQOP (@NonNull final String sQOP)
  {
    if (!RFC1945Helper.isToken (sQOP))
      throw new IllegalArgumentException ("The passed qop-option is not a token: " + sQOP);

    m_aQOPs.add (sQOP);
    return this;
  }

  public boolean isValid ()
  {
    return m_sRealm != null && m_sNonce != null;
  }

  @NonNull
  @Nonempty
  public String build ()
  {
    if (!isValid ())
      throw new IllegalStateException ("Built Digest auth is not valid!");

    final StringBuilder ret = new StringBuilder (HttpDigestAuth.HEADER_VALUE_PREFIX_DIGEST);
    // Realm is required
    ret.append (" realm=").append (RFC1945Helper.getQuotedTextString (m_sRealm));
    if (m_aDomains.isNotEmpty ())
    {
      ret.append (", domain=")
         .append (RFC1945Helper.getQuotedTextString (StringImplode.getImploded (' ', m_aDomains)));
    }
    // Nonce is required
    ret.append (", nonce=").append (RFC1945Helper.getQuotedTextString (m_sNonce));
    if (m_sOpaque != null)
      ret.append (", opaque=").append (RFC1945Helper.getQuotedTextString (m_sOpaque));
    if (m_eStale.isDefined ())
      ret.append (", stale=").append (m_eStale.isTrue () ? "true" : "false");
    if (m_sAlgorithm != null)
      ret.append (", algorithm=").append (m_sAlgorithm);
    if (m_aQOPs.isNotEmpty ())
      ret.append (", qop=").append (RFC1945Helper.getQuotedTextString (StringImplode.getImploded (',', m_aQOPs)));

    return ret.toString ();
  }
}
