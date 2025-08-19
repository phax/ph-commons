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
package com.helger.http.supplementary.tools;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.serialize.MicroReader;

import jakarta.annotation.Nonnull;

/**
 * Map OpenSSL cipher suite names to IANA cipher suite names
 *
 * @author Philip Helger
 */
public class MainMapCipherSuites
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainMapCipherSuites.class);

  @Nonnull
  @ReturnsMutableCopy
  private static ICommonsMap <String, String> readMap ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    final IMicroDocument aDoc = MicroReader.readMicroXML (new File ("src/test/resources/external/ciphersuites/mapping.xml"));
    for (final IMicroElement e : aDoc.getDocumentElement ().getAllChildElements ())
      aMap.put (e.getAttributeValue ("openssl"), e.getAttributeValue ("iana"));
    return aMap;
  }

  public static void main (final String [] args)
  {
    final ICommonsMap <String, String> aMap = readMap ();

    final StringBuilder aSB = new StringBuilder ();
    for (final String sOld : new String [] { "ECDHE-ECDSA-AES128-GCM-SHA256",
                                             "ECDHE-RSA-AES128-GCM-SHA256",
                                             "ECDHE-ECDSA-AES256-GCM-SHA384",
                                             "ECDHE-RSA-AES256-GCM-SHA384",
                                             "ECDHE-ECDSA-CHACHA20-POLY1305",
                                             "ECDHE-RSA-CHACHA20-POLY1305",
                                             "DHE-RSA-AES128-GCM-SHA256",
                                             "DHE-RSA-AES256-GCM-SHA384",
                                             "DHE-RSA-CHACHA20-POLY1305",
                                             "ECDHE-ECDSA-AES128-SHA256",
                                             "ECDHE-RSA-AES128-SHA256",
                                             "ECDHE-ECDSA-AES128-SHA",
                                             "ECDHE-RSA-AES128-SHA",
                                             "ECDHE-ECDSA-AES256-SHA384",
                                             "ECDHE-RSA-AES256-SHA384",
                                             "ECDHE-ECDSA-AES256-SHA",
                                             "ECDHE-RSA-AES256-SHA",
                                             "DHE-RSA-AES128-SHA256",
                                             "DHE-RSA-AES256-SHA256",
                                             "AES128-GCM-SHA256",
                                             "AES256-GCM-SHA384",
                                             "AES128-SHA256",
                                             "AES256-SHA256",
                                             "AES128-SHA",
                                             "AES256-SHA",
                                             "DES-CBC3-SHA" })
    {
      final String sNew = aMap.get (sOld);
      if (sNew == null)
        aSB.append ("\n   // ").append (sOld);
      else
        aSB.append ("\n   \"").append (sNew).append ("\",");
    }
    LOGGER.info (aSB.toString ());
  }
}
