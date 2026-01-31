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
package com.helger.http.header.specific;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringParser;
import com.helger.http.header.QValue;

/**
 * Handler for the request HTTP header field "Accept-Encoding"
 *
 * @author Philip Helger
 */
@Immutable
public final class AcceptEncodingHandler
{
  /** Any encoding */
  public static final String ANY_ENCODING = "*";
  // Standard encodings - must all be lowercase!
  /** Standard encoding "identity" */
  public static final String IDENTITY_ENCODING = "identity";
  /** Standard encoding "gzip" */
  public static final String GZIP_ENCODING = "gzip";
  /** Standard encoding "x-gzip" */
  public static final String X_GZIP_ENCODING = "x-gzip";
  /** Standard encoding "deflate" */
  public static final String DEFLATE_ENCODING = "deflate";
  /** Standard encoding "compress" */
  public static final String COMPRESS_ENCODING = "compress";
  /** Standard encoding "x-compress" */
  public static final String X_COMPRESS_ENCODING = "x-compress";

  private static final Logger LOGGER = LoggerFactory.getLogger (AcceptEncodingHandler.class);

  @PresentForCodeCoverage
  private static final AcceptEncodingHandler INSTANCE = new AcceptEncodingHandler ();

  private AcceptEncodingHandler ()
  {}

  @NonNull
  public static AcceptEncodingList getAcceptEncodings (@Nullable final String sAcceptEncoding)
  {
    final AcceptEncodingList ret = new AcceptEncodingList ();
    if (StringHelper.isEmpty (sAcceptEncoding))
    {
      // No definition - Identity encoding only
      ret.addEncoding (IDENTITY_ENCODING, QValue.MAX_QUALITY);
    }
    else
    {
      // Encodings are separated by "," or ", "
      for (final String sItem : StringHelper.getExploded (',', sAcceptEncoding))
      {
        // Qualities are separated by ";"
        final String [] aParts = StringHelper.getExplodedArray (';', sItem.trim (), 2);
        final String sEncoding = aParts[0];
        if (StringHelper.isEmpty (sEncoding))
        {
          LOGGER.warn ("Accept-Encoding item '" + sItem + "' has no encoding!");
          continue;
        }
        // Default quality is 1
        double dQuality = QValue.MAX_QUALITY;
        if (aParts.length == 2)
        {
          final String sQuality = aParts[1].trim ();
          if (sQuality.startsWith ("q="))
            dQuality = StringParser.parseDouble (sQuality.substring (2), QValue.MAX_QUALITY);
        }
        ret.addEncoding (sEncoding, dQuality);
      }
    }
    return ret;
  }
}
