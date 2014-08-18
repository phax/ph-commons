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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.helger.commons.collections.ArrayHelper;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.collections.multimap.IMultiMapSetBased;
import com.helger.commons.collections.multimap.MultiTreeMapTreeSetBased;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.impl.MicroDocument;
import com.helger.commons.microdom.impl.MicroElement;
import com.helger.commons.microdom.serialize.MicroReader;
import com.helger.commons.microdom.serialize.MicroWriter;
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
    final IMicroDocument eDstDoc = new MicroDocument ();
    final IMicroElement eDstRoot = eDstDoc.appendElement ("mime-types");
    final Map <String, String> aGlobalMimeTypes = new HashMap <String, String> ();
    final IMultiMapSetBased <String, String> aGlobalExt2MT = new MultiTreeMapTreeSetBased <String, String> ();
    final IMultiMapSetBased <String, String> aGlobalMT2Ext = new MultiTreeMapTreeSetBased <String, String> ();
    for (final IMicroElement eSrcMimeType : aDoc.getDocumentElement ().getAllChildElements (NS, "mime-type"))
    {
      final String sMIMEType = eSrcMimeType.getAttribute ("type");
      final IMicroElement eDstMimeType = new MicroElement ("mime-type");

      // Names
      eDstMimeType.appendElement ("name").appendText (sMIMEType);
      if (aGlobalMimeTypes.put (sMIMEType, sMIMEType) != null)
        System.err.println ("MIME type " + sMIMEType + " already used!");
      final Set <String> aLocalAliases = new LinkedHashSet <String> ();
      for (final IMicroElement eSrcChild : eSrcMimeType.getAllChildElements (NS, "alias"))
      {
        final String sAlias = eSrcChild.getAttribute ("type");
        if (aLocalAliases.add (sAlias))
        {
          eDstMimeType.appendElement ("name").appendText (sAlias);
          final String sOldMimeType = aGlobalMimeTypes.put (sAlias, sMIMEType);
          if (sOldMimeType != null)
            System.err.println ("MIME type alias " + sAlias + " already used in mime type " + sOldMimeType);
        }
      }

      // Description
      for (final IMicroElement eSrcChild : eSrcMimeType.getAllChildElements (NS, "comment"))
        if (!eSrcChild.hasAttribute ("xml:lang"))
          eDstMimeType.appendElement ("comment").appendText (eSrcChild.getTextContentTrimmed ());

      // Sub class of
      for (final IMicroElement eSrcChild : eSrcMimeType.getAllChildElements (NS, "sub-class-of"))
        eDstMimeType.appendElement ("sub-class-of").appendText (eSrcChild.getAttribute ("type"));

      boolean bHasAnyGlob = false;
      final Set <String> aExts = new LinkedHashSet <String> ();
      for (final IMicroElement eSrcChild : eSrcMimeType.getAllChildElements (NS, "glob"))
      {
        final String sPattern = eSrcChild.getAttribute ("pattern");
        if (RegExHelper.stringMatchesPattern ("\\*\\.[0-9a-zA-Z]+", sPattern))
        {
          final String sExt = sPattern.substring (2);
          aExts.add (sExt);

          aGlobalExt2MT.putSingle (sExt, sMIMEType);
          aGlobalMT2Ext.putSingle (sMIMEType, sExt);
          for (final String sAlias : aLocalAliases)
          {
            aGlobalExt2MT.putSingle (sExt, sAlias);
            aGlobalMT2Ext.putSingle (sAlias, sExt);
          }
        }
        eDstMimeType.appendElement ("glob").appendText (sPattern);
        bHasAnyGlob = true;
      }
      for (final String sExt : aExts)
        eDstMimeType.appendElement ("ext").appendText (sExt);

      if (bHasAnyGlob)
      {
        // Append only if at least on filename pattern is present
        eDstRoot.appendChild (eDstMimeType);
      }
    }

    // Check old data
    for (final Map.Entry <String, String> aEntry : ContainerHelper.getSortedByKey (MimeTypeDeterminator.getAllKnownMimeTypeFilenameMappings ())
                                                                  .entrySet ())
    {
      final String sExt = aEntry.getKey ();
      final String sOldMimeType = aEntry.getValue ();
      final Set <String> aNew = aGlobalExt2MT.get (sExt);
      if (aNew != null)
      {
        if (!aNew.contains (sOldMimeType))
          System.out.println (sExt + " = " + sOldMimeType + " not found!");
      }
      else
      {
        // No such mapping from ext to mime type

        // Check mimetype to extension
        final IMicroElement eDstMimeType = eDstRoot.appendElement ("mime-type");
        // Names
        eDstMimeType.appendElement ("name").appendText (sOldMimeType);
        if (aGlobalMimeTypes.put (sOldMimeType, sOldMimeType) != null)
          System.err.println ("MIME type " + sOldMimeType + " already used!");
        eDstMimeType.appendElement ("glob").appendText ("*." + sExt);
        eDstMimeType.appendElement ("ext").appendText (sExt);
        aGlobalExt2MT.putSingle (sExt, sOldMimeType);
        aGlobalMT2Ext.putSingle (sOldMimeType, sExt);
      }
    }

    if (true)
    {
      if (false)
        System.out.println (MicroWriter.getXMLString (eDstDoc));

      final IMicroDocument eDstDoc2 = new MicroDocument ();
      final IMicroElement eDstRoot2 = eDstDoc2.appendElement ("mapping");
      for (final Map.Entry <String, Set <String>> aEntry : aGlobalExt2MT.entrySet ())
      {
        final String sExt = aEntry.getKey ();
        final IMicroElement eDstItem = eDstRoot2.appendElement ("ext2mt").setAttribute ("ext", sExt);
        for (final String sMimeType : aEntry.getValue ())
          eDstItem.appendElement ("mime-type").appendText (sMimeType);
      }
      for (final Map.Entry <String, Set <String>> aEntry : aGlobalMT2Ext.entrySet ())
      {
        final IMicroElement eDstItem = eDstRoot2.appendElement ("mt2ext").setAttribute ("mime-type", aEntry.getKey ());
        for (final String sExt : aEntry.getValue ())
          eDstItem.appendElement ("ext").appendText (sExt);
      }
      System.out.println (MicroWriter.getXMLString (eDstDoc2));
    }
    else
    {
      if (false)
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
        if (false)
          System.out.println ("  <map key=\"" + sExt + "\" value=\"" + sMIME + "\" />");
      }

      if (!aChangedExtToMimeType.isEmpty ())
      {
        System.out.println ("Changed items:");
        for (final Map.Entry <String, String> aEntry : ContainerHelper.getSortedByKey (aChangedExtToMimeType)
                                                                      .entrySet ())
          System.out.println (aEntry.getKey () +
                              ": known=" +
                              MimeTypeDeterminator.getMimeTypeFromExtension (aEntry.getKey ()) +
                              "; new=" +
                              aEntry.getValue ());
        for (final Map.Entry <String, String> aEntry : ContainerHelper.getSortedByKey (aChangedExtToMimeType)
                                                                      .entrySet ())
          System.out.println ("  <map key=\"" + aEntry.getKey () + "\" value=\"" + aEntry.getValue () + "\" />");
      }

      if (!aNewExtToMimeType.isEmpty ())
      {
        System.out.println ("New items:");
        for (final Map.Entry <String, String> aEntry : ContainerHelper.getSortedByKey (aNewExtToMimeType).entrySet ())
          System.out.println ("  <map key=\"" + aEntry.getKey () + "\" value=\"" + aEntry.getValue () + "\" />");
      }
    }
    System.out.println ("done");
  }
}
