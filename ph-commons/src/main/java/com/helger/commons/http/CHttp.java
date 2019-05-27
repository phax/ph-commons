/**
 * Copyright (C) 2014-2019 Philip Helger (www.helger.com)
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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.datetime.PDTFactory;

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
   * The response codes for HTTP, as of version 1.1.
   */

  /**
   * HTTP Status-Code 100: Continue.
   */
  public static final int HTTP_CONTINUE = 100;
  /**
   * HTTP Status-Code 101: Switching Protocols.
   */
  public static final int HTTP_SWITCHING_PROTOCOLS = 101;

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
   * HTTP Status-Code 302: Temporary Redirect.
   */
  public static final int HTTP_MOVED_TEMPORARY = 302;

  /**
   * HTTP Status-Code 303: See Other.
   */
  public static final int HTTP_SEE_OTHER = 303;

  /**
   * HTTP Status-Code 304: Not Modified.
   */
  public static final int HTTP_NOT_MODIFIED = 304;

  /**
   * HTTP Status-Code 305: Use Proxy.
   */
  public static final int HTTP_USE_PROXY = 305;

  /**
   * HTTP Status-Code 306: Switch Proxy.
   */
  public static final int HTTP_SWITCH_PROXY = 306;

  /**
   * HTTP Status-Code 307: Temporary Redirect.
   */
  public static final int HTTP_TEMPORARY_REDIRECT = 307;

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
   * HTTP Status-Code 408: Request Time-Out.
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
   * HTTP Status-Code 413: Request Entity Too Large.
   */
  public static final int HTTP_ENTITY_TOO_LARGE = 413;

  /**
   * HTTP Status-Code 414: Request-URI Too Large.
   */
  public static final int HTTP_REQUEST_URI_TOO_LONG = 414;

  /**
   * HTTP Status-Code 415: Unsupported Media Type.
   */
  public static final int HTTP_UNSUPPORTED_MEDIA_TYPE = 415;

  /**
   * HTTP Status-Code 416: Requested Range not satisfiable.
   */
  public static final int HTTP_REQUESTED_RANGE_NOT_SATISFIABLE = 416;

  /**
   * HTTP Status-Code 417: Expectation failed.
   */
  public static final int HTTP_EXPECTATION_FAILED = 417;

  /**
   * HTTP Status-Code 418: I'm a teapot.
   */
  public static final int HTTP_IM_A_TEAPOT = 418;

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

  @PresentForCodeCoverage
  private static final CHttp s_aInstance = new CHttp ();

  private CHttp ()
  {}

  @Nonnull
  @Nonempty
  public static String getHttpResponseMessage (final int nResponseCode)
  {
    // All codes from HttpServletReponse in servlet Spec 3.1.0 are contained!
    String sMsg;
    switch (nResponseCode)
    {
      case HTTP_CONTINUE:
        sMsg = "Continue";
        break;
      case HTTP_SWITCHING_PROTOCOLS:
        sMsg = "Switching Protocols";
        break;
      case HTTP_OK:
        sMsg = "OK";
        break;
      case HTTP_CREATED:
        sMsg = "Created";
        break;
      case HTTP_ACCEPTED:
        sMsg = "Accepted";
        break;
      case HTTP_NON_AUTHORITATIVE_INFORMATION:
        sMsg = "Non-Authoritative Information";
        break;
      case HTTP_NO_CONTENT:
        sMsg = "No Content";
        break;
      case HTTP_RESET_CONTENT:
        sMsg = "Reset Content";
        break;
      case HTTP_PARTIAL_CONTENT:
        sMsg = "Partial Content";
        break;
      case HTTP_MULTIPLE_CHOICES:
        sMsg = "Multiple Choices";
        break;
      case HTTP_MOVED_PERMANENTLY:
        sMsg = "Moved Permanently";
        break;
      case HTTP_MOVED_TEMPORARY:
        sMsg = "Moved Temporary";
        break;
      case HTTP_SEE_OTHER:
        sMsg = "See Other";
        break;
      case HTTP_NOT_MODIFIED:
        sMsg = "Not Modified";
        break;
      case HTTP_USE_PROXY:
        sMsg = "Use Proxy";
        break;
      case HTTP_SWITCH_PROXY:
        sMsg = "Switch Proxy";
        break;
      case HTTP_TEMPORARY_REDIRECT:
        sMsg = "Temporary Redirect";
        break;
      case HTTP_BAD_REQUEST:
        sMsg = "Bad Request";
        break;
      case HTTP_UNAUTHORIZED:
        sMsg = "Unauthorized";
        break;
      case HTTP_PAYMENT_REQUIRED:
        sMsg = "Payment Required";
        break;
      case HTTP_FORBIDDEN:
        sMsg = "Forbidden";
        break;
      case HTTP_NOT_FOUND:
        sMsg = "Not Found";
        break;
      case HTTP_METHOD_NOT_ALLOWED:
        sMsg = "Method Not Allowed";
        break;
      case HTTP_NOT_ACCEPTABLE:
        sMsg = "Not Acceptable";
        break;
      case HTTP_PROXY_AUTH_REQUIRED:
        sMsg = "Proxy Authentication Required";
        break;
      case HTTP_REQUEST_TIMEOUT:
        sMsg = "Request Time-out";
        break;
      case HTTP_CONFLICT:
        sMsg = "Conflict";
        break;
      case HTTP_GONE:
        sMsg = "Gone";
        break;
      case HTTP_LENGTH_REQUIRED:
        sMsg = "Length Required";
        break;
      case HTTP_PRECONDITION_FAILED:
        sMsg = "Precondition Failed";
        break;
      case HTTP_ENTITY_TOO_LARGE:
        sMsg = "Request Entity Too Large";
        break;
      case HTTP_REQUEST_URI_TOO_LONG:
        sMsg = "Request-URI Too Large";
        break;
      case HTTP_UNSUPPORTED_MEDIA_TYPE:
        sMsg = "Unsupported Media Type";
        break;
      case HTTP_REQUESTED_RANGE_NOT_SATISFIABLE:
        sMsg = "Requested Range not satisfiable";
        break;
      case HTTP_EXPECTATION_FAILED:
        sMsg = "Expectation Failed";
        break;
      case HTTP_IM_A_TEAPOT:
        sMsg = "I'm a teapot";
        break;
      case HTTP_INTERNAL_SERVER_ERROR:
        sMsg = "Internal Server Error";
        break;
      case HTTP_NOT_IMPLEMENTED:
        sMsg = "Not Implemented";
        break;
      case HTTP_BAD_GATEWAY:
        sMsg = "Bad Gateway";
        break;
      case HTTP_SERVICE_UNAVAILABLE:
        sMsg = "Service Unavailable";
        break;
      case HTTP_GATEWAY_TIMEOUT:
        sMsg = "Gateway Time-out";
        break;
      case HTTP_VERSION_NOT_SUPPORTED:
        sMsg = "HTTP Version not supported";
        break;
      default:
        sMsg = "Unknown (" + nResponseCode + ")";
        break;
    }
    return sMsg;
  }

  /**
   * Get milliseconds suitable for HTTP requests/responses. Rounds down to the
   * nearest second for a proper compare. Java has milliseconds, HTTP
   * requests/responses have not.
   *
   * @param nMillis
   *        Milliseconds to use
   * @return The rounded milliseconds
   */
  public static long getUnifiedMillis (final long nMillis)
  {
    return nMillis / CGlobal.MILLISECONDS_PER_SECOND * CGlobal.MILLISECONDS_PER_SECOND;
  }

  @Nonnull
  public static LocalDateTime convertMillisToLocalDateTime (final long nMillis)
  {
    // Round down to the nearest second for a proper compare
    return PDTFactory.createLocalDateTime (getUnifiedMillis (nMillis));
  }
}
