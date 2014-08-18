/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.supplementary.main;

import java.util.HashMap;
import java.util.Map;

import com.helger.commons.collections.ArrayHelper;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.serialize.MicroReader;
import com.helger.commons.mime.MimeTypeDeterminator;
import com.helger.commons.regex.RegExHelper;

/**
 * Utility class for reading the freedesktop shared-mime-info file and create
 * our MIME type list.<br>
 * http://www.freedesktop.org/wiki/Software/shared-mime-info<br>
 * <br>
 * The following mappings where not taken into the list because they are too
 * unix specific:
 *
 * <pre>
 * For list 0.71:
 * docm: known=application/vnd.ms-word.document.macroEnabled.12; new=application/vnd.openxmlformats-officedocument.wordprocessingml.document
 * dot: known=application/msword; new=text/vnd.graphviz
 * exe: known=application/octet-stream; new=application/x-ms-dos-executable
 * mpp: known=application/vnd.ms-project; new=audio/x-musepack
 * p7s: known=application/x-pkcs7-signature; new=application/pkcs7-signature
 * pot: known=application/vnd.ms-powerpoint; new=text/x-gettext-translation-template
 * pptm: known=application/vnd.ms-powerpoint.presentation.macroEnabled.12; new=application/vnd.openxmlformats-officedocument.presentationml.presentation
 * ram: known=audio/x-pn-realaudio; new=application/ram
 * vcf: known=text/x-vcard; new=text/directory
 * wmf: known=application/x-msmetafile; new=image/x-wmf
 * xlsm: known=application/vnd.ms-excel.sheet.macroEnabled.12; new=application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
 * For list 0.90:
 * js: known=text/javascript; new=application/javascript
 * xml: known=text/xml; new=application/xml
 * xps: known=application/vnd.ms-xpsdocument; new=application/oxps
 * </pre>
 *
 * @author Philip Helger
 */
public final class MainReadSharedMimeInfo
{
  private static final String [] EXCLUDES = { "docm",
                                             "dot",
                                             "exe",
                                             "mpp",
                                             "p7s",
                                             "pot",
                                             "pptm",
                                             "ram",
                                             "vcf",
                                             "wmf",
                                             "xlsm",
                                             "js",
                                             "xml",
                                             "xps" };
  private static final String NS = "http://www.freedesktop.org/standards/shared-mime-info";

  public static void main (final String [] args)
  {
    final Map <String, String> aExtToMimeType = new HashMap <String, String> ();
    final IMicroDocument aDoc = MicroReader.readMicroXML (new ClassPathResource ("shared-mime-info/freedesktop.org.xml"));
    if (aDoc == null)
      throw new IllegalStateException ("Failed to read mime type info file!");
    for (final IMicroElement eMimeType : aDoc.getDocumentElement ().getAllChildElements (NS, "mime-type"))
    {
      final String sMIMEType = eMimeType.getAttribute ("type");
      for (final IMicroElement eChild : eMimeType.getAllChildElements ())
      {
        final String sLocalName = eChild.getLocalName ();
        if (sLocalName.equals ("glob"))
        {
          final String sPattern = eChild.getAttribute ("pattern");
          if (RegExHelper.stringMatchesPattern ("\\*\\.[0-9a-zA-Z]+", sPattern))
            aExtToMimeType.put (sPattern.substring (2), sMIMEType);
        }
      }
    }

    System.out.println ("All items:");
    final Map <String, String> aNewExtToMimeType = new HashMap <String, String> ();
    final Map <String, String> aChangedExtToMimeType = new HashMap <String, String> ();
    for (final Map.Entry <String, String> aEntry : ContainerHelper.getSortedByKey (aExtToMimeType).entrySet ())
    {
      final String sExt = aEntry.getKey ();
      final String sMIME = aEntry.getValue ();
      final String sKnownMimeType = MimeTypeDeterminator.getMimeTypeFromExtension (sExt);
      if (sKnownMimeType == null)
        aNewExtToMimeType.put (sExt, sMIME);
      else
        if (!sKnownMimeType.equals (sMIME))
        {
          if (!ArrayHelper.contains (EXCLUDES, sExt))
            aChangedExtToMimeType.put (sExt, sMIME);
        }
      System.out.println ("  <map key=\"" + sExt + "\" value=\"" + sMIME + "\" />");
    }

    if (!aChangedExtToMimeType.isEmpty ())
    {
      System.out.println ("Changed items:");
      for (final Map.Entry <String, String> aEntry : ContainerHelper.getSortedByKey (aChangedExtToMimeType).entrySet ())
        System.out.println (aEntry.getKey () +
                            ": known=" +
                            MimeTypeDeterminator.getMimeTypeFromExtension (aEntry.getKey ()) +
                            "; new=" +
                            aEntry.getValue ());
      for (final Map.Entry <String, String> aEntry : ContainerHelper.getSortedByKey (aChangedExtToMimeType).entrySet ())
        System.out.println ("  <map key=\"" + aEntry.getKey () + "\" value=\"" + aEntry.getValue () + "\" />");
    }

    if (!aNewExtToMimeType.isEmpty ())
    {
      System.out.println ("New items:");
      for (final Map.Entry <String, String> aEntry : ContainerHelper.getSortedByKey (aNewExtToMimeType).entrySet ())
        System.out.println ("  <map key=\"" + aEntry.getKey () + "\" value=\"" + aEntry.getValue () + "\" />");
    }
    System.out.println ("done");
  }
}
