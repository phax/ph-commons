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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.CGlobal;
import com.helger.datetime.helper.PDTFactory;

/**
 * Predefined HTTP constants.
 *
 * @author Philip Helger
 */
@Immutable
public final class CHttp
{
  public static final Charset HTTP_CHARSET = StandardCharsets.ISO_8859_1;

  /* End of line MUST be \r and \n */
  public static final String EOL = "\r\n";

  /**
   * The response codes for HTTP as of RFC 9110 (HTTP Semantics) plus extensions.
   */

  /**
   * HTTP Status-Code 100: Continue.
   */
  public static final int HTTP_CONTINUE = 100;

  /**
   * HTTP Status-Code 101: Switching Protocols.
   */
  public static final int HTTP_SWITCHING_PROTOCOLS = 101;

  /**
   * HTTP Status-Code 102: Processing (WebDAV, RFC 2518). Note: deprecated in RFC 9110 but still
   * encountered in practice.
   *
   * @since 12.1.5
   */
  @Deprecated (forRemoval = false)
  public static final int HTTP_PROCESSING = 102;

  /**
   * HTTP Status-Code 103: Early Hints.
   */
  public static final int HTTP_EARLY_HINTS = 103;

  /* 2XX: generally "OK" */

  /**
   * HTTP Status-Code 200: OK.
   */
  public static final int HTTP_OK = 200;

  /**
   * HTTP Status-Code 201: Created.
   */
  public static final int HTTP_CREATED = 201;

  /**
   * HTTP Status-Code 202: Accepted.
   */
  public static final int HTTP_ACCEPTED = 202;

  /**
   * HTTP Status-Code 203: Non-Authoritative Information.
   */
  public static final int HTTP_NON_AUTHORITATIVE_INFORMATION = 203;

  /**
   * HTTP Status-Code 204: No Content.
   */
  public static final int HTTP_NO_CONTENT = 204;

  /**
   * HTTP Status-Code 205: Reset Content.
   */
  public static final int HTTP_RESET_CONTENT = 205;

  /**
   * HTTP Status-Code 206: Partial Content.
   */
  public static final int HTTP_PARTIAL_CONTENT = 206;

  /**
   * HTTP Status-Code 207: Multi-Status (WebDAV, RFC 4918).
   *
   * @since 12.1.5
   */
  public static final int HTTP_MULTI_STATUS = 207;

  /**
   * HTTP Status-Code 208: Already Reported (WebDAV, RFC 5842).
   *
   * @since 12.1.5
   */
  public static final int HTTP_ALREADY_REPORTED = 208;

  /**
   * HTTP Status-Code 226: IM Used (RFC 3229).
   *
   * @since 12.1.5
   */
  public static final int HTTP_IM_USED = 226;

  /* 3XX: relocation/redirect */

  /**
   * HTTP Status-Code 300: Multiple Choices.
   */
  public static final int HTTP_MULTIPLE_CHOICES = 300;

  /**
   * HTTP Status-Code 301: Moved Permanently.
   */
  public static final int HTTP_MOVED_PERMANENTLY = 301;

  /**
   * HTTP Status-Code 302: Found. This is the RFC 9110 name for this status code.
   *
   * @since 12.1.5
   */
  public static final int HTTP_FOUND = 302;

  /**
   * This is the old name of HTTP 302. Use {@link #HTTP_FOUND} instead.
   */
  @Deprecated (forRemoval = true, since = "12.1.5")
  public static final int HTTP_MOVED_TEMPORARY = HTTP_FOUND;

  /**
   * HTTP Status-Code 303: See Other.
   */
  public static final int HTTP_SEE_OTHER = 303;

  /**
   * HTTP Status-Code 304: Not Modified.
   */
  public static final int HTTP_NOT_MODIFIED = 304;

  /**
   * HTTP Status-Code 305: Use Proxy. Deprecated in RFC 9110 Section 15.4.6 due to security
   * concerns.
   */
  @Deprecated (forRemoval = false, since = "12.1.5")
  public static final int HTTP_USE_PROXY = 305;

  /**
   * HTTP Status-Code 306: Switch Proxy. Unused and reserved; not defined in RFC 9110.
   */
  @Deprecated (forRemoval = false, since = "12.1.5")
  public static final int HTTP_SWITCH_PROXY = 306;

  /**
   * HTTP Status-Code 307: Temporary Redirect.
   */
  public static final int HTTP_TEMPORARY_REDIRECT = 307;

  /**
   * HTTP Status-Code 308: Permanent Redirect.
   */
  public static final int HTTP_PERMANENT_REDIRECT = 308;

  /* 4XX: client error */

  /**
   * HTTP Status-Code 400: Bad Request.
   */
  public static final int HTTP_BAD_REQUEST = 400;

  /**
   * HTTP Status-Code 401: Unauthorized.
   */
  public static final int HTTP_UNAUTHORIZED = 401;

  /**
   * HTTP Status-Code 402: Payment Required.
   */
  public static final int HTTP_PAYMENT_REQUIRED = 402;

  /**
   * HTTP Status-Code 403: Forbidden.
   */
  public static final int HTTP_FORBIDDEN = 403;

  /**
   * HTTP Status-Code 404: Not Found.
   */
  public static final int HTTP_NOT_FOUND = 404;

  /**
   * HTTP Status-Code 405: Method Not Allowed.
   */
  public static final int HTTP_METHOD_NOT_ALLOWED = 405;

  /**
   * HTTP Status-Code 406: Not Acceptable.
   */
  public static final int HTTP_NOT_ACCEPTABLE = 406;

  /**
   * HTTP Status-Code 407: Proxy Authentication Required.
   */
  public static final int HTTP_PROXY_AUTH_REQUIRED = 407;

  /**
   * HTTP Status-Code 408: Request Timeout.
   */
  public static final int HTTP_REQUEST_TIMEOUT = 408;

  /**
   * HTTP Status-Code 409: Conflict.
   */
  public static final int HTTP_CONFLICT = 409;

  /**
   * HTTP Status-Code 410: Gone.
   */
  public static final int HTTP_GONE = 410;

  /**
   * HTTP Status-Code 411: Length Required.
   */
  public static final int HTTP_LENGTH_REQUIRED = 411;

  /**
   * HTTP Status-Code 412: Precondition Failed.
   */
  public static final int HTTP_PRECONDITION_FAILED = 412;

  /**
   * HTTP Status-Code 413: Content Too Large. This is the RFC 9110 name for this status code.
   *
   * @since 12.1.5
   */
  public static final int HTTP_CONTENT_TOO_LARGE = 413;

  /**
   * This is the old name of HTTP 413. Use {@link #HTTP_CONTENT_TOO_LARGE} instead.
   */
  @Deprecated (forRemoval = true, since = "12.1.5")
  public static final int HTTP_ENTITY_TOO_LARGE = HTTP_CONTENT_TOO_LARGE;

  /**
   * HTTP Status-Code 414: URI Too Long. This is the RFC 9110 name for this status code.
   *
   * @since 12.1.5
   */
  public static final int HTTP_URI_TOO_LONG = 414;

  /**
   * This is the old name of HTTP 414. Use {@link #HTTP_URI_TOO_LONG} instead.
   */
  @Deprecated (forRemoval = true, since = "12.1.5")
  public static final int HTTP_REQUEST_URI_TOO_LONG = HTTP_URI_TOO_LONG;

  /**
   * HTTP Status-Code 415: Unsupported Media Type.
   */
  public static final int HTTP_UNSUPPORTED_MEDIA_TYPE = 415;

  /**
   * HTTP Status-Code 416: Range Not Satisfiable. This is the RFC 9110 name for this status code.
   *
   * @since 12.1.5
   */
  public static final int HTTP_RANGE_NOT_SATISFIABLE = 416;

  /**
   * This is the old name of HTTP 416. Use {@link #HTTP_RANGE_NOT_SATISFIABLE} instead.
   */
  @Deprecated (forRemoval = true, since = "12.1.5")
  public static final int HTTP_REQUESTED_RANGE_NOT_SATISFIABLE = HTTP_RANGE_NOT_SATISFIABLE;

  /**
   * HTTP Status-Code 417: Expectation Failed.
   */
  public static final int HTTP_EXPECTATION_FAILED = 417;

  /**
   * HTTP Status-Code 418: I'm a teapot.
   */
  public static final int HTTP_IM_A_TEAPOT = 418;

  /**
   * HTTP Status-Code 422: Unprocessable Content.
   *
   * @since 12.1.4
   */
  public static final int HTTP_UNPROCESSABLE_CONTENT = 422;

  /**
   * This is the old name of HTTP 422. Use {@link #HTTP_UNPROCESSABLE_CONTENT} instead
   */
  @Deprecated (forRemoval = true, since = "12.1.3")
  public static final int HTTP_UNPROCESSABLE_ENTITY = HTTP_UNPROCESSABLE_CONTENT;

  /**
   * HTTP Status-Code 423: Locked (WebDAV, RFC 4918).
   *
   * @since 12.1.5
   */
  public static final int HTTP_LOCKED = 423;

  /**
   * HTTP Status-Code 424: Failed Dependency (WebDAV, RFC 4918).
   *
   * @since 12.1.5
   */
  public static final int HTTP_FAILED_DEPENDENCY = 424;

  /**
   * HTTP Status-Code 425: Too Early (RFC 8470).
   *
   * @since 12.1.5
   */
  public static final int HTTP_TOO_EARLY = 425;

  /**
   * HTTP Status-Code 426: Upgrade Required.
   */
  public static final int HTTP_UPGRADE_REQUIRED = 426;

  /**
   * HTTP Status-Code 428: Precondition Required.
   */
  public static final int HTTP_PRECONDITION_REQUIRED = 428;

  /**
   * HTTP Status-Code 429: Too Many Requests.
   */
  public static final int HTTP_TOO_MANY_REQUESTS = 429;

  /**
   * HTTP Status-Code 431: Request Header Fields Too Large.
   */
  public static final int HTTP_REQUEST_HEADER_FIELDS_TOO_LARGE = 431;

  /**
   * HTTP Status-Code 451: Unavailable For Legal Reasons.
   */
  public static final int HTTP_UNAVAILABLE_FOR_LEGAL_REASONS = 451;

  /* 5XX: server error */

  /**
   * HTTP Status-Code 500: Internal Server Error.
   */
  public static final int HTTP_INTERNAL_SERVER_ERROR = 500;

  /**
   * HTTP Status-Code 501: Not Implemented.
   */
  public static final int HTTP_NOT_IMPLEMENTED = 501;

  /**
   * HTTP Status-Code 502: Bad Gateway.
   */
  public static final int HTTP_BAD_GATEWAY = 502;

  /**
   * HTTP Status-Code 503: Service Unavailable.
   */
  public static final int HTTP_SERVICE_UNAVAILABLE = 503;

  /**
   * HTTP Status-Code 504: Gateway Timeout.
   */
  public static final int HTTP_GATEWAY_TIMEOUT = 504;

  /**
   * HTTP Status-Code 505: HTTP Version Not Supported.
   */
  public static final int HTTP_VERSION_NOT_SUPPORTED = 505;

  /**
   * HTTP Status-Code 506: Variant Also Negotiates.
   */
  public static final int HTTP_VARIANT_ALSO_NEGOTIATES = 506;

  /**
   * HTTP Status-Code 507: Insufficient Storage.
   */
  public static final int HTTP_INSUFFICIENT_STORAGE = 507;

  /**
   * HTTP Status-Code 508: Loop Detected.
   */
  public static final int HTTP_LOOP_DETECTED = 508;

  /**
   * HTTP Status-Code 511: Network Authentication Required.
   */
  public static final int HTTP_NETWORK_AUTHENTICATION_REQUIRED = 511;

  @PresentForCodeCoverage
  private static final CHttp INSTANCE = new CHttp ();

  private CHttp ()
  {}

  /**
   * Get the human-readable response message for the given HTTP status code.
   *
   * @param nResponseCode
   *        The HTTP response code.
   * @return The corresponding response message. Never <code>null</code>.
   */
  @NonNull
  @Nonempty
  public static String getHttpResponseMessage (final int nResponseCode)
  {
    // Status code reason phrases per RFC 9110 and extensions
    final String sMsg = switch (nResponseCode)
    {
      case HTTP_CONTINUE -> "Continue";
      case HTTP_SWITCHING_PROTOCOLS -> "Switching Protocols";
      case HTTP_PROCESSING -> "Processing";
      case HTTP_EARLY_HINTS -> "Early Hints";
      case HTTP_OK -> "OK";
      case HTTP_CREATED -> "Created";
      case HTTP_ACCEPTED -> "Accepted";
      case HTTP_NON_AUTHORITATIVE_INFORMATION -> "Non-Authoritative Information";
      case HTTP_NO_CONTENT -> "No Content";
      case HTTP_RESET_CONTENT -> "Reset Content";
      case HTTP_PARTIAL_CONTENT -> "Partial Content";
      case HTTP_MULTI_STATUS -> "Multi-Status";
      case HTTP_ALREADY_REPORTED -> "Already Reported";
      case HTTP_IM_USED -> "IM Used";
      case HTTP_MULTIPLE_CHOICES -> "Multiple Choices";
      case HTTP_MOVED_PERMANENTLY -> "Moved Permanently";
      case HTTP_FOUND -> "Found";
      case HTTP_SEE_OTHER -> "See Other";
      case HTTP_NOT_MODIFIED -> "Not Modified";
      case HTTP_USE_PROXY -> "Use Proxy";
      case HTTP_SWITCH_PROXY -> "Switch Proxy";
      case HTTP_TEMPORARY_REDIRECT -> "Temporary Redirect";
      case HTTP_PERMANENT_REDIRECT -> "Permanent Redirect";
      case HTTP_BAD_REQUEST -> "Bad Request";
      case HTTP_UNAUTHORIZED -> "Unauthorized";
      case HTTP_PAYMENT_REQUIRED -> "Payment Required";
      case HTTP_FORBIDDEN -> "Forbidden";
      case HTTP_NOT_FOUND -> "Not Found";
      case HTTP_METHOD_NOT_ALLOWED -> "Method Not Allowed";
      case HTTP_NOT_ACCEPTABLE -> "Not Acceptable";
      case HTTP_PROXY_AUTH_REQUIRED -> "Proxy Authentication Required";
      case HTTP_REQUEST_TIMEOUT -> "Request Timeout";
      case HTTP_CONFLICT -> "Conflict";
      case HTTP_GONE -> "Gone";
      case HTTP_LENGTH_REQUIRED -> "Length Required";
      case HTTP_PRECONDITION_FAILED -> "Precondition Failed";
      case HTTP_CONTENT_TOO_LARGE -> "Content Too Large";
      case HTTP_URI_TOO_LONG -> "URI Too Long";
      case HTTP_UNSUPPORTED_MEDIA_TYPE -> "Unsupported Media Type";
      case HTTP_RANGE_NOT_SATISFIABLE -> "Range Not Satisfiable";
      case HTTP_EXPECTATION_FAILED -> "Expectation Failed";
      case HTTP_IM_A_TEAPOT -> "I'm a teapot";
      case HTTP_UNPROCESSABLE_CONTENT -> "Unprocessable Content";
      case HTTP_LOCKED -> "Locked";
      case HTTP_FAILED_DEPENDENCY -> "Failed Dependency";
      case HTTP_TOO_EARLY -> "Too Early";
      case HTTP_UPGRADE_REQUIRED -> "Upgrade Required";
      case HTTP_PRECONDITION_REQUIRED -> "Precondition Required";
      case HTTP_TOO_MANY_REQUESTS -> "Too Many Requests";
      case HTTP_REQUEST_HEADER_FIELDS_TOO_LARGE -> "Request Header Fields Too Large";
      case HTTP_UNAVAILABLE_FOR_LEGAL_REASONS -> "Unavailable For Legal Reasons";
      case HTTP_INTERNAL_SERVER_ERROR -> "Internal Server Error";
      case HTTP_NOT_IMPLEMENTED -> "Not Implemented";
      case HTTP_BAD_GATEWAY -> "Bad Gateway";
      case HTTP_SERVICE_UNAVAILABLE -> "Service Unavailable";
      case HTTP_GATEWAY_TIMEOUT -> "Gateway Timeout";
      case HTTP_VERSION_NOT_SUPPORTED -> "HTTP Version Not Supported";
      case HTTP_VARIANT_ALSO_NEGOTIATES -> "Variant Also Negotiates";
      case HTTP_INSUFFICIENT_STORAGE -> "Insufficient Storage";
      case HTTP_LOOP_DETECTED -> "Loop Detected";
      case HTTP_NETWORK_AUTHENTICATION_REQUIRED -> "Network Authentication Required";
      default -> "Unknown (" + nResponseCode + ")";
    };
    return sMsg;
  }

  /**
   * Get milliseconds suitable for HTTP requests/responses. Rounds down to the nearest second for a
   * proper compare. Java has milliseconds, HTTP requests/responses have not.
   *
   * @param nMillis
   *        Milliseconds to use
   * @return The rounded milliseconds
   */
  public static long getUnifiedMillis (final long nMillis)
  {
    return nMillis / CGlobal.MILLISECONDS_PER_SECOND * CGlobal.MILLISECONDS_PER_SECOND;
  }

  /**
   * Convert the given milliseconds to a {@link LocalDateTime}, rounded down
   * to the nearest second.
   *
   * @param nMillis
   *        The milliseconds to convert.
   * @return The corresponding {@link LocalDateTime}. Never <code>null</code>.
   */
  @NonNull
  public static LocalDateTime convertMillisToLocalDateTime (final long nMillis)
  {
    // Round down to the nearest second for a proper compare
    return PDTFactory.createLocalDateTime (getUnifiedMillis (nMillis));
  }
}
