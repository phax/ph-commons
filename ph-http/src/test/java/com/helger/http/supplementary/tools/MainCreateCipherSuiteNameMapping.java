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
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.string.StringHelper;
import com.helger.base.string.StringReplace;
import com.helger.cache.regex.RegExHelper;
import com.helger.collection.commons.ICommonsList;
import com.helger.io.file.SimpleFileIO;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.serialize.MicroReader;
import com.helger.xml.microdom.serialize.MicroWriter;

/**
 * Main table is from https://testssl.sh/openssl-iana.mapping.html
 *
 * @author Philip Helger
 */
public final class MainCreateCipherSuiteNameMapping
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainCreateCipherSuiteNameMapping.class);

  public static void main (final String [] args)
  {
    String s = SimpleFileIO.getFileAsString (new File ("src/test/resources/external/ciphersuites/Mapping OpenSSL cipher suite names to IANA names.html"),
                                             StandardCharsets.UTF_8);
    s += "</html>";
    s = StringReplace.replaceAll (s, "<br>", "<br/>");
    s = RegExHelper.stringReplacePattern ("<meta[^>]+>", s, "");
    final IMicroElement aHtml = MicroReader.readMicroXML (s).getDocumentElement ();
    final IMicroElement aBody = aHtml.getFirstChildElement ("body");
    final IMicroElement aDiv = aBody.getFirstChildElement ("div");
    final IMicroElement aTable = aDiv.getFirstChildElement ("table");
    final IMicroElement aTBody = aTable.getFirstChildElement ("tbody");
    final IMicroDocument aDst = new MicroDocument ();
    final IMicroElement aRoot = aDst.addElement ("mapping");
    for (final IMicroElement aTR : aTBody.getAllChildElements ("tr"))
    {
      final ICommonsList <IMicroElement> aTDs = aTR.getAllChildElements ("td");
      if (aTDs.size () >= 6)
      {
        final String sName1 = StringHelper.trimEnd (aTDs.get (1).getTextContentTrimmed (), "-OLD");
        final String sName2 = StringHelper.trimEnd (aTDs.get (5).getTextContentTrimmed (), "_OLD");
        if (StringHelper.isNotEmpty (sName1) && StringHelper.isNotEmpty (sName2))
        {
          aRoot.addElement ("item").setAttribute ("openssl", sName1).setAttribute ("iana", sName2);
        }
      }
    }
    MicroWriter.writeToFile (aDst, new File ("src/test/resources/external/ciphersuites/mapping.xml"));
    LOGGER.info ("Wrote file");
  }
}
