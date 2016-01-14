/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.charset.CCharset;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.exception.InitializationException;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.serialize.MicroReader;
import com.helger.commons.microdom.serialize.MicroWriter;
import com.helger.commons.microdom.util.XMLMapHandler;
import com.helger.commons.mime.MimeType;
import com.helger.commons.mime.MimeTypeInfo;
import com.helger.commons.mime.MimeTypeInfo.ExtensionWithSource;
import com.helger.commons.mime.MimeTypeInfo.MimeTypeWithSource;
import com.helger.commons.mime.MimeTypeInfoManager;
import com.helger.commons.mime.MimeTypeParser;
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
  private static final Logger s_aLogger = LoggerFactory.getLogger (MainReadSharedMimeInfo.class);
  private static final String NS = "http://www.freedesktop.org/standards/shared-mime-info";

  public static void main (final String [] args)
  {
    s_aLogger.info ("Reading shared-mime-info/freedesktop.org.xml");
    final IMicroDocument aDoc = MicroReader.readMicroXML (new File ("src/test/resources/shared-mime-info/freedesktop.org.xml"));
    if (aDoc == null)
      throw new IllegalStateException ("Failed to read mime type info file!");

    final MimeTypeInfoManager aMgr = new MimeTypeInfoManager ();
    for (final IMicroElement eSrcMimeType : aDoc.getDocumentElement ().getAllChildElements (NS, "mime-type"))
    {
      final String sMIMEType = eSrcMimeType.getAttributeValue ("type");
      final Set <MimeTypeWithSource> aLocalNames = new LinkedHashSet <MimeTypeWithSource> ();

      // Names
      aLocalNames.add (new MimeTypeWithSource (sMIMEType));
      for (final IMicroElement eSrcChild : eSrcMimeType.getAllChildElements (NS, "alias"))
      {
        final String sAlias = eSrcChild.getAttributeValue ("type");
        aLocalNames.add (new MimeTypeWithSource (sAlias));
      }

      // Description
      String sComment = null;
      for (final IMicroElement eSrcChild : eSrcMimeType.getAllChildElements (NS, "comment"))
        if (!eSrcChild.hasAttribute ("xml:lang"))
        {
          sComment = eSrcChild.getTextContentTrimmed ();
          break;
        }

      // Sub class of
      final Set <String> aSubClassOf = new LinkedHashSet <String> ();
      for (final IMicroElement eSrcChild : eSrcMimeType.getAllChildElements (NS, "sub-class-of"))
      {
        final String s = eSrcChild.getAttributeValue ("type");
        aSubClassOf.add (s);
      }

      boolean bHasAnyGlob = false;
      final Set <String> aGlobs = new LinkedHashSet <String> ();
      final Set <ExtensionWithSource> aExts = new LinkedHashSet <ExtensionWithSource> ();
      for (final IMicroElement eSrcChild : eSrcMimeType.getAllChildElements (NS, "glob"))
      {
        final String sPattern = eSrcChild.getAttributeValue ("pattern");
        if (RegExHelper.stringMatchesPattern ("\\*\\.[0-9a-zA-Z]+", sPattern))
        {
          final String sExt = sPattern.substring (2);
          aExts.add (new ExtensionWithSource (sExt));
        }
        else
          aGlobs.add (sPattern);
        bHasAnyGlob = true;
      }

      if (bHasAnyGlob)
      {
        // Append only if at least on filename pattern is present
        aMgr.registerMimeType (new MimeTypeInfo (aLocalNames,
                                                 sComment,
                                                 aSubClassOf,
                                                 aGlobs,
                                                 aExts,
                                                 "shared-mime-info"));
      }
    }

    s_aLogger.info ("Read " + aMgr.getAllMimeTypeInfos ().size () + " mime type infos");

    // FIXME read existing MimeTypeInfo data

    // Maps file extension to MIME type
    s_aLogger.info ("Reading shared-mime-info/fileext-mimetype-mapping-local.xml");
    final Map <String, String> m_aFileExtMap = new HashMap <String, String> ();
    if (XMLMapHandler.readMap (new FileSystemResource ("src/test/resources/shared-mime-info/fileext-mimetype-mapping-local.xml"),
                               m_aFileExtMap)
                     .isFailure ())
      throw new InitializationException ("Failed to init file extension to mimetype mapping file");

    s_aLogger.info ("Read " + m_aFileExtMap.size () + " entries");

    // Check old data
    for (final Map.Entry <String, String> aEntry : CollectionHelper.getSortedByKey (m_aFileExtMap).entrySet ())
    {
      final String sOldExt = aEntry.getKey ();
      final String sOldMimeType = aEntry.getValue ();
      final MimeType aOldMimeType = MimeTypeParser.parseMimeType (sOldMimeType);
      List <MimeTypeInfo> aNew;

      // First check for Mime Type, as they are unique
      aNew = aMgr.getAllInfosOfMimeType (aOldMimeType);
      if (aNew != null)
      {
        // Mime type is present - check if extension is also present
        boolean bFound = false;
        for (final MimeTypeInfo aInfo : aNew)
          if (aInfo.containsExtension (sOldExt))
          {
            bFound = true;
            break;
          }
        if (!bFound)
        {
          if (aNew.size () == 1)
          {
            aMgr.addExtension (aNew.get (0), new ExtensionWithSource (sOldExt, "old"));
            if (false)
              System.out.println ("Added extension '" + sOldExt + "' to " + sOldMimeType + "!");
          }
          else
            System.err.println (sOldMimeType + ": '" + sOldExt + "' not found in " + aNew + "!");
        }
      }
      else
      {
        // no such mime type present - Check other direction: ext 2 mimetype
        aNew = aMgr.getAllInfosOfExtension (sOldExt);
        if (aNew != null)
        {
          // Found extension - check if MIME type matches that type
          boolean bFound = false;
          for (final MimeTypeInfo aInfo : aNew)
            if (aInfo.containsMimeType (sOldMimeType))
            {
              bFound = true;
              break;
            }
          if (!bFound)
          {
            if (aNew.size () == 1)
            {
              aMgr.addMimeType (aNew.get (0), new MimeTypeWithSource (aOldMimeType, "old"));
              if (false)
                System.out.println ("'" + sOldExt + "': " + sOldMimeType + " not found in " + aNew.get (0) + "!");
            }
            else
              System.err.println ("'" + sOldExt + "': " + sOldMimeType + " not found in any of " + aNew + "!");
          }
        }
        else
        {
          // No such mapping from ext to mime type

          // Create a new entry
          aMgr.registerMimeType (new MimeTypeInfo (CollectionHelper.newSet (new MimeTypeWithSource (sOldMimeType)),
                                                   null,
                                                   new HashSet <String> (),
                                                   new HashSet <String> (),
                                                   CollectionHelper.newSet (new ExtensionWithSource (sOldExt)),
                                                   "old"));
          if (false)
            System.out.println ("Creating new: " + sOldMimeType + " = '" + sOldExt + "'");
        }
      }
    }

    s_aLogger.info ("Finally having " + aMgr.getAllMimeTypeInfos ().size () + " mime type infos");

    if (SimpleFileIO.writeFile (new File ("src/main/resources/codelists/mime-type-info.xml"),
                                MicroWriter.getXMLString (aMgr.getAsDocument ()),
                                CCharset.CHARSET_UTF_8_OBJ)
                    .isSuccess ())
      s_aLogger.info ("done - run mvn license:format !!");
    else
      s_aLogger.error ("Error writing file");
  }
}
