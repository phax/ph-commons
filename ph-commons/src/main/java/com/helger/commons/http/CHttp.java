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
      case 100:
        sMsg = "Continue";
        break;
      case 101:
        sMsg = "Switching Protocols";
        break;
      case 200:
        sMsg = "OK";
        break;
      case 201:
        sMsg = "Created";
        break;
      case 202:
        sMsg = "Accepted";
        break;
      case 203:
        sMsg = "Non-Authoritative Information";
        break;
      case 204:
        sMsg = "No Content";
        break;
      case 205:
        sMsg = "Reset Content";
        break;
      case 206:
        sMsg = "Partial Content";
        break;
      case 300:
        sMsg = "Multiple Choices";
        break;
      case 301:
        sMsg = "Moved Permanently";
        break;
      case 302:
        sMsg = "Found";
        break;
      case 303:
        sMsg = "See Other";
        break;
      case 304:
        sMsg = "Not Modified";
        break;
      case 305:
        sMsg = "Use Proxy";
        break;
      case 307:
        sMsg = "Temporary Redirect";
        break;
      case 400:
        sMsg = "Bad Request";
        break;
      case 401:
        sMsg = "Unauthorized";
        break;
      case 402:
        sMsg = "Payment Required";
        break;
      case 403:
        sMsg = "Forbidden";
        break;
      case 404:
        sMsg = "Not Found";
        break;
      case 405:
        sMsg = "Method Not Allowed";
        break;
      case 406:
        sMsg = "Not Acceptable";
        break;
      case 407:
        sMsg = "Proxy Authentication Required";
        break;
      case 408:
        sMsg = "Request Time-out";
        break;
      case 409:
        sMsg = "Conflict";
        break;
      case 410:
        sMsg = "Gone";
        break;
      case 411:
        sMsg = "Length Required";
        break;
      case 412:
        sMsg = "Precondition Failed";
        break;
      case 413:
        sMsg = "Request Entity Too Large";
        break;
      case 414:
        sMsg = "Request-URI Too Large";
        break;
      case 415:
        sMsg = "Unsupported Media Type";
        break;
      case 416:
        sMsg = "Requested range not satisfiable";
        break;
      case 417:
        sMsg = "Expectation Failed";
        break;
      case 418:
        sMsg = "I'm a teapot";
        break;
      case 500:
        sMsg = "Internal Server Error";
        break;
      case 501:
        sMsg = "Not Implemented";
        break;
      case 502:
        sMsg = "Bad Gateway";
        break;
      case 503:
        sMsg = "Service Unavailable";
        break;
      case 504:
        sMsg = "Gateway Time-out";
        break;
      case 505:
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
