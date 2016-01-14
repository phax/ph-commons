/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.url;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;

/**
 * Specifies a list of known protocols.<br>
 * Should be extended to the list defined at
 * <a href="http://www.iana.org/assignments/uri-schemes.html">the IANA</a>
 *
 * @author Philip Helger
 */
public enum EURLProtocol implements IURLProtocol
{
  /** http (RFC2616). */
  HTTP ("http://"),

  /** https (RFC2818). */
  HTTPS ("https://"),

  /** Local file (RFC1738). */
  FILE ("file://"),

  /** ftp (RFC1738). */
  FTP ("ftp://"),

  /** OSGI bundle */
  BUNDLE ("bundle://"),

  /** Skype. */
  CALLTO ("callto:"),

  /** Embedded data (RFC2397). */
  DATA ("data:"),

  /** ftps. */
  FTPS ("ftps://"),

  /** geographic coordinates (RFC5870). */
  GEO ("geo:"),

  /** Gopher (RFC4266). */
  GOPHER ("gopher://"),

  /** regular JAR */
  JAR ("jar:"),

  /** JavaScript */
  JAVASCRIPT ("javascript:"),

  /** LDAP (RFC4516). */
  LDAP ("ldap:"),

  /** Email (RFC6068). */
  MAILTO ("mailto:"),

  /** MS Media Server. */
  MMS ("mms:"),

  /** News (RFC5538). */
  NEWS ("news:"),

  /** NNTP (RFC5538). */
  NNTP ("nntp:"),

  /** POP3 (RFC2384). */
  POP ("pop://"),

  /** RSync. */
  RSYNC ("rsync:"),

  /** RTMP */
  RTMP ("rtmp://"),

  /** Real time streaming protocol (RFC2326). */
  RTSP ("rtsp://"),

  /** Real time streaming protocol (unreliable) (RFC2326). */
  RTSPU ("rtspu://"),

  /** scp. */
  SCP ("scp://"),

  /** sftp. */
  SFTP ("sftp://"),

  /** shttp (RFC2660). */
  SHTTP ("shttp://"),

  /** session initiation protocol (RFC3261). */
  SIP ("sip:"),

  /** secure session initiation protocol (RFC3261). */
  SIPS ("sips:"),

  /** Telephone (RFC3966). */
  TEL ("tel:"),

  /** Reference to interactive sessions (RFC4248). */
  TELNET ("telnet://"),

  /** URN (RFC2141) */
  URN ("urn:"),

  /** Web socket (RFC6455). */
  WS ("ws://"),

  /** WebSphere JAR */
  WSJAR ("wsjar:"),

  /** Encrypted web socket (RFC6455). */
  WSS ("wss://"),

  /** ZIP file */
  ZIP ("zip:");

  private final String m_sProtocol;

  private EURLProtocol (@Nonnull @Nonempty final String sProtocol)
  {
    m_sProtocol = sProtocol;
  }

  /**
   * @return The underlying text representation of the protocol.
   */
  @Nonnull
  @Nonempty
  public String getProtocol ()
  {
    return m_sProtocol;
  }

  /**
   * Tells if the passed String (URL) belongs to this protocol.
   *
   * @param sURL
   *        The URL to check. May be <code>null</code>.
   * @return <code>true</code> if the passed URL starts with this protocol
   */
  public boolean isUsedInURL (@Nullable final String sURL)
  {
    return sURL != null && sURL.startsWith (m_sProtocol, 0);
  }

  @Nullable
  public String getWithProtocol (@Nullable final String sURL)
  {
    if (sURL == null)
      return null;
    return m_sProtocol + sURL;
  }

  @Nullable
  public String getWithProtocolIfNone (@Nullable final String sURL)
  {
    if (sURL == null || URLProtocolRegistry.getInstance ().hasKnownProtocol (sURL))
      return sURL;
    return m_sProtocol + sURL;
  }

  public boolean allowsForQueryParameters ()
  {
    return this == HTTP || this == HTTPS || this == MAILTO || this == SHTTP;
  }
}
