/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.http;

import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;

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
  public static final String CONTENT_DESCRIPTION = "Content-Description";
  public static final String CONTENT_DISPOSITION = "Content-Disposition";
  public static final String CONTENT_ENCODING = "Content-Encoding";
  public static final String CONTENT_ID = "Content-ID";
  public static final String CONTENT_TYPE = "Content-Type";
  public static final String COOKIE = "Cookie";
  public static final String DATE = "Date";
  public static final String ETAG = "ETag";
  public static final String EXPIRES = "Expires";
  public static final String IF_NON_MATCH = "If-None-Match";
  public static final String IF_MATCH = "If-Match";
  public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
  public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
  public static final String LAST_MODIFIED = "Last-Modified";
  public static final String LOCATION = "Location";
  public static final String PRAGMA = "Pragma";
  public static final String REFERER = "Referer";
  public static final String USER_AGENT = "User-Agent";
  public static final String VARY = "Vary";
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
   * e.g. Frame-Options: deny
   */
  public static final String FRAME_OPTIONS = "Frame-Options";
  /**
   * https://www.owasp.org/index.php/List_of_useful_HTTP_headers<br>
   * e.g. X-XSS-Protection: 1; mode=block
   */
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
   * The Retry-After response-header field can be used with a 503 (Service
   * Unavailable) response to indicate how long the service is expected to be
   * unavailable to the requesting client. This field MAY also be used with any
   * 3xx (Redirection) response to indicate the minimum time the user-agent is
   * asked wait before issuing the redirected request. The value of this field
   * can be either an HTTP-date or an integer number of seconds (in decimal)
   * after the time of the response
   */
  public static final String RETRY_AFTER = "Retry-After";

  // Do Not Track header
  public static final String DNT = "DNT";

  // Different user agent headers
  public static final String UA = "UA";
  public static final String X_DEVICE_USER_AGENT = "x-device-user-agent";

  // HTML
  public static final String CONTENT_LANGUAGE = "Content-Language";
  public static final String CONTENT_SCRIPT_TYPE = "Content-Script-Type";
  public static final String CONTENT_STYLE_TYPE = "Content-Style-Type";
  public static final String DEFAULT_STYLE = "Default-Style";
  public static final String REFRESH = "Refresh";
  public static final String WINDOW_TARGET = "Window-target";
  public static final String EXT_CACHE = "Ext-cache";
  public static final String PICS_LABEL = "PICS-Label";
  public static final String X_UA_COMPATIBLE = "X-UA-Compatible";

  // Response codes
  public static final String CONNECTION = "Connection";
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
  public static final String X_CONTENT_SECURITY_POLICY = "X-Content-Security-Policy";
  public static final String X_WEBKIT_CSP = "X-WebKit-CSP";
  public static final String CONTENT_SECURITY_POLICY_REPORT_ONLY = "Content-Security-Policy-Report-Only";

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

  // Special values
  public static final String VALUE_ALLOW_FROM = "ALLOW-FROM";
  public static final String VALUE_DENY = "DENY";
  public static final String VALUE_INCLUDE_SUBDOMAINS = "includeSubDomains";
  public static final String VALUE_NOSNIFF = "nosniff";
  public static final String VALUE_SAMEORIGIN = "SAMEORIGIN";

  @PresentForCodeCoverage
  private static final CHttpHeader s_aInstance = new CHttpHeader ();

  private CHttpHeader ()
  {}
}
