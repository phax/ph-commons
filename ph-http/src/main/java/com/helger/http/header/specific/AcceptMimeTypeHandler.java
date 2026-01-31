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
import com.helger.mime.EMimeContentType;
import com.helger.mime.IMimeType;
import com.helger.mime.MimeType;
import com.helger.mime.parse.MimeTypeParser;

/**
 * Handler for the request HTTP header field "Accept"
 *
 * @author Philip Helger
 */
@Immutable
public final class AcceptMimeTypeHandler
{
  /** Any MIME type */
  public static final IMimeType ANY_MIMETYPE = new MimeType (EMimeContentType._STAR, "*");

  private static final Logger LOGGER = LoggerFactory.getLogger (AcceptMimeTypeHandler.class);

  @PresentForCodeCoverage
  private static final AcceptMimeTypeHandler INSTANCE = new AcceptMimeTypeHandler ();

  private AcceptMimeTypeHandler ()
  {}

  @NonNull
  public static AcceptMimeTypeList getAcceptMimeTypes (@Nullable final String sAcceptMimeTypes)
  {
    final AcceptMimeTypeList ret = new AcceptMimeTypeList ();
    if (StringHelper.isEmpty (sAcceptMimeTypes))
    {
      // No definition - access all
      ret.addMimeType (ANY_MIMETYPE, QValue.MAX_QUALITY);
    }
    else
    {
      // MIME types are separated by "," or ", "
      for (final String sItem : StringHelper.getExploded (',', sAcceptMimeTypes))
      {
        // Qualities are separated by ";"
        final String [] aParts = StringHelper.getExplodedArray (';', sItem.trim (), 2);

        // Default quality is 1
        double dQuality = QValue.MAX_QUALITY;
        if (aParts.length == 2 && aParts[1].trim ().startsWith ("q="))
          dQuality = StringParser.parseDouble (aParts[1].trim ().substring (2), QValue.MAX_QUALITY);

        final String sMimeType = aParts[0];
        IMimeType aMimeType = MimeTypeParser.safeParseMimeType (sMimeType);
        if (aMimeType != null)
        {
          if (aMimeType.hasAnyParameters ())
          {
            LOGGER.warn ("Ignoring all contained MIME type parameter from '" + sMimeType + "'!");
            aMimeType = aMimeType.getCopyWithoutParameters ();
          }
          ret.addMimeType (aMimeType, dQuality);
        }
        else
          if ("*".equals (sMimeType))
            ret.addMimeType (ANY_MIMETYPE, dQuality);
          else
          {
            // Certain invalid MIME types occur often so they are manually
            // ignored
            if (!"xml/xml".equals (sMimeType))
            {
              LOGGER.warn ("Failed to parse Mime type '" + sMimeType + "' as part of '" + sAcceptMimeTypes + "'!");
            }
          }
      }
    }
    return ret;
  }
}
