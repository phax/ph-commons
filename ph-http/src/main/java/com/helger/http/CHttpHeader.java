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
package com.helger.http;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;

/**
 * Predefined HTTP header names and values.
 *
 * @author Philip Helger
 */
@Immutable
public final class CHttpHeader
{
  public static final String ACCEPT = "Accept";
  public static final String ACCEPT_CHARSET = "Accept-Charset";
  public static final String ACCEPT_ENCODING = "Accept-Encoding";
  public static final String ACCEPT_LANGUAGE = "Accept-Language";
  public static final String ACCEPT_PATCH = "Accept-Patch";
  public static final String AGE = "Age";
  public static final String ALLOW = "Allow";
  public static final String CACHE_CONTROL = "Cache-Control";
  public static final String CONNECTION = "Connection";
  public static final String CONTENT_DESCRIPTION = "Content-Description";
  public static final String CONTENT_DISPOSITION = "Content-Disposition";
  public static final String CONTENT_ENCODING = "Content-Encoding";
  public static final String CONTENT_ID = "Content-ID";
  public static final String CONTENT_TYPE = "Content-Type";
  public static final String COOKIE = "Cookie";
  public static final String DATE = "Date";
  public static final String ETAG = "ETag";
  public static final String EXPIRES = "Expires";
  public static final String FORWARDED = "Forwarded";
  public static final String HOST = "Host";
  public static final String IF_NON_MATCH = "If-None-Match";
  public static final String IF_MATCH = "If-Match";
  public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
  public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
  public static final String LAST_MODIFIED = "Last-Modified";
  public static final String LOCATION = "Location";
  /**
   * Deprecated in RFC 9111 Section 5.4. Use {@link #CACHE_CONTROL} instead.
   */
  @Deprecated (forRemoval = false, since = "12.1.5")
  public static final String PRAGMA = "Pragma";
  public static final String REFERER = "Referer";
  public static final String USER_AGENT = "User-Agent";
  public static final String VARY = "Vary";
  /**
   * Removed in RFC 9111 (HTTP Caching, 2022). No longer defined in the HTTP standard.
   */
  @Deprecated (forRemoval = false, since = "12.1.5")
  public static final String WARNING = "Warning";
  public static final String AUTHORIZATION = "Authorization";
  public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
  public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
  /**
   * https://www.owasp.org/index.php/HTTP_Strict_Transport_Security<br>
   * e.g. "max-age=16070400; includeSubDomains"
   */
  public static final String STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";
  /**
   * https://www.owasp.org/index.php/List_of_useful_HTTP_headers<br>
   * e.g. X-Frame-Options: deny
   */
  public static final String X_FRAME_OPTIONS = "X-Frame-Options";
  /**
   * https://www.owasp.org/index.php/List_of_useful_HTTP_headers<br>
   * e.g. X-XSS-Protection: 1; mode=block
   */
  @Deprecated (forRemoval = false)
  public static final String X_XSS_PROTECTION = "X-XSS-Protection";
  /**
   * https://www.owasp.org/index.php/List_of_useful_HTTP_headers<br>
   * e.g. X-Content-Type-Options: nosniff
   */
  public static final String X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";
  /**
   * https://scotthelme.co.uk/a-new-security-header-referrer-policy/<br>
   * E.g. Referrer-Policy: strict-origin
   */
  public static final String REFERRER_POLICY = "Referrer-Policy";
  /**
   * The "Proxy" header famous from the Httpoxy attack.<br>
   * https://www.apache.org/security/asf-httpoxy-response.txt
   */
  public static final String PROXY = "proxy";

  /**
   * The Retry-After response-header field can be used with a 503 (Service Unavailable) response to
   * indicate how long the service is expected to be unavailable to the requesting client. This
   * field MAY also be used with any 3xx (Redirection) response to indicate the minimum time the
   * user-agent is asked wait before issuing the redirected request. The value of this field can be
   * either an HTTP-date or an integer number of seconds (in decimal) after the time of the response
   */
  public static final String RETRY_AFTER = "Retry-After";

  /**
   * Do Not Track header. Abandoned; never standardized, removed from most browsers.
   */
  @Deprecated (forRemoval = false, since = "12.1.5")
  public static final String DNT = "DNT";

  /**
   * Non-standard user agent variant.
   */
  @Deprecated (forRemoval = false, since = "12.1.5")
  public static final String UA = "UA";
  /**
   * Non-standard device user agent header.
   */
  @Deprecated (forRemoval = false, since = "12.1.5")
  public static final String X_DEVICE_USER_AGENT = "x-device-user-agent";

  // HTML
  public static final String CONTENT_LANGUAGE = "Content-Language";
  /**
   * HTML 4.01 only; obsolete in HTML5.
   */
  @Deprecated (forRemoval = false, since = "12.1.5")
  public static final String CONTENT_SCRIPT_TYPE = "Content-Script-Type";
  /**
   * HTML 4.01 only; obsolete in HTML5.
   */
  @Deprecated (forRemoval = false, since = "12.1.5")
  public static final String CONTENT_STYLE_TYPE = "Content-Style-Type";
  public static final String DEFAULT_STYLE = "Default-Style";
  public static final String REFRESH = "Refresh";
  /**
   * Non-standard header; never widely supported.
   */
  @Deprecated (forRemoval = false, since = "12.1.5")
  public static final String WINDOW_TARGET = "Window-target";
  /**
   * Non-standard header.
   */
  @Deprecated (forRemoval = false, since = "12.1.5")
  public static final String EXT_CACHE = "Ext-cache";
  /**
   * PICS was discontinued by W3C in 2006; obsolete.
   */
  @Deprecated (forRemoval = false, since = "12.1.5")
  public static final String PICS_LABEL = "PICS-Label";
  /**
   * IE-specific header; no longer relevant.
   */
  @Deprecated (forRemoval = false, since = "12.1.5")
  public static final String X_UA_COMPATIBLE = "X-UA-Compatible";

  // Response codes
  public static final String CONTENT_LENGTH = "Content-Length";
  public static final String SET_COOKIE = "Set-Cookie";
  public static final String TRANSFER_ENCODING = "Transfer-Encoding";

  // CORS
  public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
  public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
  public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
  public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
  public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
  public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
  public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
  public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
  public static final String ORIGIN = "Origin";

  // CSP
  public static final String CONTENT_SECURITY_POLICY = "Content-Security-Policy";
  @Deprecated (forRemoval = true, since = "12.1.4")
  public static final String X_CONTENT_SECURITY_POLICY = "X-Content-Security-Policy";
  @Deprecated (forRemoval = true, since = "12.1.4")
  public static final String X_WEBKIT_CSP = "X-WebKit-CSP";
  public static final String CONTENT_SECURITY_POLICY_REPORT_ONLY = "Content-Security-Policy-Report-Only";
  @Deprecated (forRemoval = true, since = "12.1.4")
  public static final String X_CONTENT_SECURITY_POLICY_REPORT_ONLY = "X-Content-Security-Policy-Report-Only";

  // WAP-248-UAPROF-20011020-a
  public static final String X_WAP_PROFILE = "X-Wap-Profile";
  public static final String PROFILE = "Profile";
  public static final String WAP_PROFILE = "Wap-Profile";
  public static final String MAN = "Man";
  public static final String OPT = "Opt";
  public static final String X_WAP_PROFILE_DIFF = "X-Wap-Profile-Diff";
  public static final String PROFILE_DIFF = "Profile-Diff";
  public static final String WAP_PROFILE_DIFF = "Wap-Profile-Diff";

  // AS2 headers
  public static final String AS2_FROM = "AS2-From";
  public static final String AS2_TO = "AS2-To";
  public static final String AS2_VERSION = "AS2-Version";
  public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
  public static final String DISPOSITION_NOTIFICATION_OPTIONS = "Disposition-Notification-Options";
  /** The URL where the async MDN should be send to - limited to RFC 2822 */
  public static final String DISPOSITION_NOTIFICATION_TO = "Disposition-Notification-To";
  public static final String FROM = "From";
  public static final String MESSAGE_ID = "Message-ID";
  public static final String MIME_VERSION = "Mime-Version";
  /** The URL where the async MDN should be send to */
  public static final String RECEIPT_DELIVERY_OPTION = "Receipt-Delivery-Option";
  public static final String RECIPIENT_ADDRESS = "Recipient-Address";
  public static final String SERVER = "Server";
  public static final String SUBJECT = "Subject";
  /** Defined by RFC 6017 */
  public static final String EDIINT_FEATURES = "EDIINT-Features";

  // Standard headers from RFC 9110 (HTTP Semantics)
  /**
   * RFC 9110 Section 14.3
   *
   * @since 12.1.5
   */
  public static final String ACCEPT_RANGES = "Accept-Ranges";
  /**
   * RFC 9110 Section 14.4
   *
   * @since 12.1.5
   */
  public static final String CONTENT_RANGE = "Content-Range";
  /**
   * RFC 9110 Section 10.1.1
   *
   * @since 12.1.5
   */
  public static final String EXPECT = "Expect";
  /**
   * RFC 9110 Section 13.1.5
   *
   * @since 12.1.5
   */
  public static final String IF_RANGE = "If-Range";
  /**
   * RFC 9110 Section 7.6.2
   *
   * @since 12.1.5
   */
  public static final String MAX_FORWARDS = "Max-Forwards";
  /**
   * RFC 9110 Section 11.7.1
   *
   * @since 12.1.5
   */
  public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";
  /**
   * RFC 9110 Section 14.2
   *
   * @since 12.1.5
   */
  public static final String RANGE = "Range";
  /**
   * RFC 9110 Section 10.1.4
   *
   * @since 12.1.5
   */
  public static final String TE = "TE";
  /**
   * RFC 9110 Section 6.6.2
   *
   * @since 12.1.5
   */
  public static final String TRAILER = "Trailer";
  /**
   * RFC 9110 Section 7.8
   *
   * @since 12.1.5
   */
  public static final String UPGRADE = "Upgrade";
  /**
   * RFC 9110 Section 7.6.3
   *
   * @since 12.1.5
   */
  public static final String VIA = "Via";

  // Modern security headers
  /**
   * Permissions-Policy header (successor to Feature-Policy). See
   * https://w3c.github.io/webappsec-permissions-policy/
   *
   * @since 12.1.5
   */
  public static final String PERMISSIONS_POLICY = "Permissions-Policy";
  /**
   * Cross-Origin-Embedder-Policy (COEP) header. See
   * https://html.spec.whatwg.org/multipage/origin.html#coep
   *
   * @since 12.1.5
   */
  public static final String CROSS_ORIGIN_EMBEDDER_POLICY = "Cross-Origin-Embedder-Policy";
  /**
   * Cross-Origin-Opener-Policy (COOP) header. See
   * https://html.spec.whatwg.org/multipage/origin.html#cross-origin-opener-policies
   *
   * @since 12.1.5
   */
  public static final String CROSS_ORIGIN_OPENER_POLICY = "Cross-Origin-Opener-Policy";
  /**
   * Cross-Origin-Resource-Policy (CORP) header. See
   * https://fetch.spec.whatwg.org/#cross-origin-resource-policy-header
   *
   * @since 12.1.5
   */
  public static final String CROSS_ORIGIN_RESOURCE_POLICY = "Cross-Origin-Resource-Policy";
  /**
   * Alt-Svc header (RFC 7838). Used to advertise alternative services.
   *
   * @since 12.1.5
   */
  public static final String ALT_SVC = "Alt-Svc";
  /**
   * Priority header (RFC 9218). Used for HTTP extensible prioritization.
   *
   * @since 12.1.5
   */
  public static final String PRIORITY = "Priority";

  // Special values
  public static final String VALUE_ALLOW_FROM = "ALLOW-FROM";
  public static final String VALUE_DENY = "DENY";
  public static final String VALUE_INCLUDE_SUBDOMAINS = "includeSubDomains";
  public static final String VALUE_NOSNIFF = "nosniff";
  public static final String VALUE_SAMEORIGIN = "SAMEORIGIN";

  @PresentForCodeCoverage
  private static final CHttpHeader INSTANCE = new CHttpHeader ();

  private CHttpHeader ()
  {}
}
