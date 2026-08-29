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
  /** RFC 9110 Section 12.5.1 */
  public static final String ACCEPT = "Accept";
  /** RFC 9110 Section 12.5.2 */
  public static final String ACCEPT_CHARSET = "Accept-Charset";
  /** RFC 9110 Section 12.5.3 */
  public static final String ACCEPT_ENCODING = "Accept-Encoding";
  /** RFC 9110 Section 12.5.4 */
  public static final String ACCEPT_LANGUAGE = "Accept-Language";
  /** RFC 5789 Section 3.1 */
  public static final String ACCEPT_PATCH = "Accept-Patch";
  /** RFC 9111 Section 5.1 */
  public static final String AGE = "Age";
  /** RFC 9110 Section 10.2.1 */
  public static final String ALLOW = "Allow";
  /** RFC 9111 Section 5.2 */
  public static final String CACHE_CONTROL = "Cache-Control";
  /** RFC 9110 Section 7.6.1 */
  public static final String CONNECTION = "Connection";
  /** RFC 2045 Section 8 */
  public static final String CONTENT_DESCRIPTION = "Content-Description";
  /** RFC 6266 */
  public static final String CONTENT_DISPOSITION = "Content-Disposition";
  /** RFC 9110 Section 8.4 */
  public static final String CONTENT_ENCODING = "Content-Encoding";
  /** RFC 2045 Section 7 */
  public static final String CONTENT_ID = "Content-ID";
  /** RFC 9110 Section 8.3 */
  public static final String CONTENT_TYPE = "Content-Type";
  /** RFC 6265 Section 5.4 */
  public static final String COOKIE = "Cookie";
  /** RFC 9110 Section 6.6.1 */
  public static final String DATE = "Date";
  /** RFC 9110 Section 8.8.3 */
  public static final String ETAG = "ETag";
  /** RFC 9111 Section 5.3 */
  public static final String EXPIRES = "Expires";
  /** RFC 7239 */
  public static final String FORWARDED = "Forwarded";
  /** RFC 9110 Section 7.2 */
  public static final String HOST = "Host";
  /** RFC 9110 Section 13.1.2 */
  public static final String IF_NON_MATCH = "If-None-Match";
  /** RFC 9110 Section 13.1.1 */
  public static final String IF_MATCH = "If-Match";
  /** RFC 9110 Section 13.1.3 */
  public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
  /** RFC 9110 Section 13.1.4 */
  public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
  /** RFC 9110 Section 8.8.2 */
  public static final String LAST_MODIFIED = "Last-Modified";
  /** RFC 9110 Section 10.2.2 */
  public static final String LOCATION = "Location";
  /**
   * Deprecated in RFC 9111 Section 5.4. Use {@link #CACHE_CONTROL} instead.
   */
  @Deprecated (forRemoval = false, since = "12.1.5")
  public static final String PRAGMA = "Pragma";
  /** RFC 9110 Section 10.1.3 */
  public static final String REFERER = "Referer";
  /** RFC 9110 Section 10.1.5 */
  public static final String USER_AGENT = "User-Agent";
  /** RFC 9110 Section 12.5.5 */
  public static final String VARY = "Vary";
  /**
   * Removed in RFC 9111 (HTTP Caching, 2022). No longer defined in the HTTP standard.
   */
  @Deprecated (forRemoval = false, since = "12.1.5")
  public static final String WARNING = "Warning";
  /** RFC 9110 Section 11.6.2 */
  public static final String AUTHORIZATION = "Authorization";
  /** RFC 9110 Section 11.7.2 */
  public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
  /** RFC 9110 Section 11.6.1 */
  public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
  /**
   * RFC 6797. E.g. "max-age=16070400; includeSubDomains"
   */
  public static final String STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";
  /**
   * RFC 7034. E.g. X-Frame-Options: deny
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
   * RFC 9110 Section 10.2.3. The Retry-After response-header field can be used with a 503 (Service
   * Unavailable) response to indicate how long the service is expected to be unavailable to the
   * requesting client. This field MAY also be used with any 3xx (Redirection) response to indicate
   * the minimum time the user-agent is asked wait before issuing the redirected request. The value
   * of this field can be either an HTTP-date or an integer number of seconds (in decimal) after the
   * time of the response
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

  /** RFC 9110 Section 8.5 */
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
  /** Selects the preferred style sheet when multiple alternates are present. */
  public static final String DEFAULT_STYLE = "Default-Style";
  /**
   * Non-standard header instructing the client to reload or redirect after a given number of
   * seconds. Commonly used as an HTML {@code <meta http-equiv>} equivalent.
   */
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

  /** RFC 9110 Section 8.6 */
  public static final String CONTENT_LENGTH = "Content-Length";
  /** RFC 6265 Section 4.1 */
  public static final String SET_COOKIE = "Set-Cookie";
  /** RFC 9112 Section 6.1 */
  public static final String TRANSFER_ENCODING = "Transfer-Encoding";

  // CORS
  /**
   * CORS response header indicating whether the response may be exposed when credentials are set.
   */
  public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
  /** CORS response header listing the request headers allowed in the actual request. */
  public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
  /** CORS response header listing the HTTP methods allowed for the resource. */
  public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
  /** CORS response header indicating which origins may access the resource. */
  public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
  /** CORS response header listing the response headers exposed to the client. */
  public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
  /** CORS response header indicating how long a preflight response may be cached. */
  public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
  /** CORS preflight request header indicating the HTTP method of the actual request. */
  public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
  /** CORS preflight request header listing the headers used in the actual request. */
  public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
  /** RFC 6454. Indicates the origin of the request. */
  public static final String ORIGIN = "Origin";

  // CSP
  /** Content Security Policy header controlling the resources the user agent may load. */
  public static final String CONTENT_SECURITY_POLICY = "Content-Security-Policy";
  /**
   * Legacy, vendor-prefixed variant of {@link #CONTENT_SECURITY_POLICY}. Use
   * {@link #CONTENT_SECURITY_POLICY} instead.
   */
  @Deprecated (forRemoval = true, since = "12.1.4")
  public static final String X_CONTENT_SECURITY_POLICY = "X-Content-Security-Policy";
  /**
   * Legacy, WebKit-prefixed variant of {@link #CONTENT_SECURITY_POLICY}. Use
   * {@link #CONTENT_SECURITY_POLICY} instead.
   */
  @Deprecated (forRemoval = true, since = "12.1.4")
  public static final String X_WEBKIT_CSP = "X-WebKit-CSP";
  /** Content Security Policy header that reports violations without enforcing the policy. */
  public static final String CONTENT_SECURITY_POLICY_REPORT_ONLY = "Content-Security-Policy-Report-Only";
  /**
   * Legacy, vendor-prefixed variant of {@link #CONTENT_SECURITY_POLICY_REPORT_ONLY}. Use
   * {@link #CONTENT_SECURITY_POLICY_REPORT_ONLY} instead.
   */
  @Deprecated (forRemoval = true, since = "12.1.4")
  public static final String X_CONTENT_SECURITY_POLICY_REPORT_ONLY = "X-Content-Security-Policy-Report-Only";
  /**
   * Reporting API header that declares named reporting endpoints, to be referenced from the CSP
   * <code>report-to</code> directive. The value is a structured field dictionary, mapping an
   * endpoint name to a quoted URL - e.g. <code>csp-endpoint="/cspreporting"</code>.
   *
   * @since 12.4.0
   */
  public static final String REPORTING_ENDPOINTS = "Reporting-Endpoints";

  // WAP-248-UAPROF-20011020-a
  /** WAP UAProf header referencing the user agent profile document. */
  public static final String X_WAP_PROFILE = "X-Wap-Profile";
  /** WAP UAProf header referencing the user agent profile document. */
  public static final String PROFILE = "Profile";
  /** WAP UAProf header referencing the user agent profile document. */
  public static final String WAP_PROFILE = "Wap-Profile";
  /** RFC 2774. Mandatory extension declaration used by the WAP UAProf protocol. */
  public static final String MAN = "Man";
  /** RFC 2774. Optional extension declaration used by the WAP UAProf protocol. */
  public static final String OPT = "Opt";
  /** WAP UAProf header carrying the differences to the referenced profile. */
  public static final String X_WAP_PROFILE_DIFF = "X-Wap-Profile-Diff";
  /** WAP UAProf header carrying the differences to the referenced profile. */
  public static final String PROFILE_DIFF = "Profile-Diff";
  /** WAP UAProf header carrying the differences to the referenced profile. */
  public static final String WAP_PROFILE_DIFF = "Wap-Profile-Diff";

  // AS2 headers (RFC 4130)
  /** RFC 4130 Section 6.1 */
  public static final String AS2_FROM = "AS2-From";
  /** RFC 4130 Section 6.1 */
  public static final String AS2_TO = "AS2-To";
  /** RFC 4130 Section 6.1 */
  public static final String AS2_VERSION = "AS2-Version";
  /** RFC 2045 Section 6 */
  public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
  /** RFC 8098 Section 2.2 */
  public static final String DISPOSITION_NOTIFICATION_OPTIONS = "Disposition-Notification-Options";
  /** RFC 8098 Section 2.1. The URL where the async MDN should be sent to - limited to RFC 2822 */
  public static final String DISPOSITION_NOTIFICATION_TO = "Disposition-Notification-To";
  /** RFC 9110 Section 10.1.2 */
  public static final String FROM = "From";
  /** RFC 5322 Section 3.6.4 */
  public static final String MESSAGE_ID = "Message-ID";
  /** RFC 2045 Section 4 */
  public static final String MIME_VERSION = "Mime-Version";
  /** RFC 4130 Section 7.3. The URL where the async MDN should be sent to */
  public static final String RECEIPT_DELIVERY_OPTION = "Receipt-Delivery-Option";
  /** RFC 4130 Section 6.2 */
  public static final String RECIPIENT_ADDRESS = "Recipient-Address";
  /** RFC 9110 Section 10.2.4 */
  public static final String SERVER = "Server";
  /** RFC 5322 Section 3.6.5 */
  public static final String SUBJECT = "Subject";
  /** RFC 6017 */
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

  /**
   * The name of the de-facto standard HTTP header that contains the original client IP address when
   * running behind a reverse proxy.
   *
   * @since 12.3.4
   */
  public static final String X_FORWARDED_FOR = "X-Forwarded-For";
  /**
   * De-facto standard header identifying the protocol (HTTP or HTTPS) used by the client to connect
   * to a reverse proxy.
   *
   * @since 12.3.4
   */
  public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
  /**
   * De-facto standard header identifying the original host requested by the client when running
   * behind a reverse proxy.
   *
   * @since 12.3.4
   */
  public static final String X_FORWARDED_HOST = "X-Forwarded-Host";
  /**
   * De-facto standard header identifying the original port requested by the client when running
   * behind a reverse proxy.
   *
   * @since 12.3.4
   */
  public static final String X_FORWARDED_PORT = "X-Forwarded-Port";

  // Special values
  /** Header value for {@link #X_FRAME_OPTIONS} allowing framing only by the given origin. */
  public static final String VALUE_ALLOW_FROM = "ALLOW-FROM";
  /** Header value for {@link #X_FRAME_OPTIONS} denying all framing of the resource. */
  public static final String VALUE_DENY = "DENY";
  /**
   * Header value for {@link #STRICT_TRANSPORT_SECURITY} extending the policy to all subdomains.
   */
  public static final String VALUE_INCLUDE_SUBDOMAINS = "includeSubDomains";
  /** Header value for {@link #X_CONTENT_TYPE_OPTIONS} disabling MIME type sniffing. */
  public static final String VALUE_NOSNIFF = "nosniff";
  /** Header value for {@link #X_FRAME_OPTIONS} allowing framing only by the same origin. */
  public static final String VALUE_SAMEORIGIN = "SAMEORIGIN";

  @PresentForCodeCoverage
  private static final CHttpHeader INSTANCE = new CHttpHeader ();

  private CHttpHeader ()
  {}
}
