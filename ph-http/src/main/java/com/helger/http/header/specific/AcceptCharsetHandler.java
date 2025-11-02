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
package com.helger.http.header.specific;

import java.nio.charset.StandardCharsets;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringParser;
import com.helger.http.header.QValue;

/**
 * Handler for the request HTTP header field "Accept-Charset"
 *
 * @author Philip Helger
 */
@Immutable
public final class AcceptCharsetHandler
{
  /** Any charset */
  public static final String ANY_CHARSET = "*";
  /** Default charset iso-8859-1 */
  public static final String DEFAULT_CHARSET = StandardCharsets.ISO_8859_1.name ();

  @PresentForCodeCoverage
  private static final AcceptCharsetHandler INSTANCE = new AcceptCharsetHandler ();

  private AcceptCharsetHandler ()
  {}

  @NonNull
  public static AcceptCharsetList getAcceptCharsets (@Nullable final String sAcceptCharset)
  {
    final AcceptCharsetList ret = new AcceptCharsetList ();
    if (StringHelper.isEmpty (sAcceptCharset))
    {
      // No definition - access all
      ret.addCharset (ANY_CHARSET, QValue.MAX_QUALITY);
    }
    else
    {
      // Charsets are separated by "," or ", "
      for (final String sItem : StringHelper.getExploded (',', sAcceptCharset))
      {
        // Qualities are separated by ";"
        final String [] aParts = StringHelper.getExplodedArray (';', sItem.trim (), 2);

        // Default quality is 1
        double dQuality = QValue.MAX_QUALITY;
        if (aParts.length == 2 && aParts[1].trim ().startsWith ("q="))
          dQuality = StringParser.parseDouble (aParts[1].trim ().substring (2), QValue.MAX_QUALITY);
        ret.addCharset (aParts[0], dQuality);
      }
    }
    return ret;
  }
}
