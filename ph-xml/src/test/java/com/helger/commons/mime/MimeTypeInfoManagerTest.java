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
package com.helger.commons.mime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.helger.commons.string.StringHelper;

/**
 * Test class for class {@link MimeTypeInfoManager}.
 *
 * @author Philip Helger
 */
public final class MimeTypeInfoManagerTest
{
  @Test
  public void testGetDefaultInstance ()
  {
    assertNotNull (MimeTypeInfoManager.getDefaultInstance ());
  }

  @Test
  public void testGetMimeType ()
  {
    final MimeTypeInfoManager aMgr = MimeTypeInfoManager.getDefaultInstance ();
    assertTrue (aMgr.getAllMimeTypesForFilename ("test.xls").contains (CMimeType.APPLICATION_MS_EXCEL));
    assertTrue (aMgr.getAllMimeTypesForFilename ("TEST.XLS").contains (CMimeType.APPLICATION_MS_EXCEL));
    assertTrue (aMgr.getAllMimeTypesForFilename ("test.hastenichgesehen").isEmpty ());

    assertTrue (aMgr.getAllMimeTypeStringsForFilename ("test.xls")
                    .contains (CMimeType.APPLICATION_MS_EXCEL.getAsString ()));
    assertTrue (aMgr.getAllMimeTypeStringsForFilename ("TEST.XLS")
                    .contains (CMimeType.APPLICATION_MS_EXCEL.getAsString ()));
    assertTrue (aMgr.getAllMimeTypeStringsForFilename ("test.hastenichgesehen").isEmpty ());

    assertTrue (aMgr.containsMimeTypeForFilename ("test.xls"));
    assertTrue (aMgr.containsMimeTypeForFilename ("TEST.XLS"));
    assertFalse (aMgr.containsMimeTypeForFilename ("test.hastenichgesehen"));

    assertTrue (aMgr.getAllMimeTypesForExtension ("xls").contains (CMimeType.APPLICATION_MS_EXCEL));
    assertTrue (aMgr.getAllMimeTypesForExtension ("XLS").contains (CMimeType.APPLICATION_MS_EXCEL));
    assertTrue (aMgr.getAllMimeTypesForExtension ("abersichernicht").isEmpty ());

    assertEquals (CMimeType.APPLICATION_MS_EXCEL, aMgr.getPrimaryMimeTypeForExtension ("xls"));
    assertEquals (CMimeType.APPLICATION_MS_EXCEL, aMgr.getPrimaryMimeTypeForExtension ("XLS"));
    assertNull (aMgr.getPrimaryMimeTypeForExtension ("waerhaettedasgedacht"));

    assertTrue (aMgr.getAllMimeTypeStringsForExtension ("xls")
                    .contains (CMimeType.APPLICATION_MS_EXCEL.getAsString ()));
    assertTrue (aMgr.getAllMimeTypeStringsForExtension ("XLS")
                    .contains (CMimeType.APPLICATION_MS_EXCEL.getAsString ()));
    assertTrue (aMgr.getAllMimeTypeStringsForExtension ("abersichernicht").isEmpty ());

    assertTrue (aMgr.getAllMimeTypesForExtension ("xml").contains (CMimeType.APPLICATION_XML));
    assertTrue (aMgr.getAllMimeTypesForExtension ("xml").contains (CMimeType.TEXT_XML));

    assertEquals (CMimeType.APPLICATION_MS_EXCEL.getAsString (), aMgr.getPrimaryMimeTypeStringForExtension ("xls"));
    assertEquals (CMimeType.APPLICATION_MS_EXCEL.getAsString (), aMgr.getPrimaryMimeTypeStringForExtension ("XLS"));
    assertEquals (CMimeType.APPLICATION_MS_EXCEL_2007.getAsString (),
                  aMgr.getPrimaryMimeTypeStringForExtension ("xlsx"));
    assertEquals (CMimeType.APPLICATION_MS_WORD.getAsString (), aMgr.getPrimaryMimeTypeStringForExtension ("doc"));
    assertEquals (CMimeType.APPLICATION_MS_WORD_2007.getAsString (),
                  aMgr.getPrimaryMimeTypeStringForExtension ("docx"));
    assertEquals (CMimeType.APPLICATION_MS_POWERPOINT.getAsString (),
                  aMgr.getPrimaryMimeTypeStringForExtension ("ppt"));
    assertEquals (CMimeType.APPLICATION_MS_POWERPOINT_2007.getAsString (),
                  aMgr.getPrimaryMimeTypeStringForExtension ("pptx"));
    assertEquals (CMimeType.APPLICATION_XML.getAsString (), aMgr.getPrimaryMimeTypeStringForExtension ("xml"));
    assertNull (aMgr.getPrimaryMimeTypeStringForExtension ("waerhaettedasgedacht"));

    for (final String sExt : new String [] { "xls",
                                             "XLS",
                                             "xlsx",
                                             "XLSX",
                                             "doc",
                                             "DOC",
                                             "docx",
                                             "DOCX",
                                             "ppt",
                                             "PPT",
                                             "pptx",
                                             "PPTX",
                                             "xml",
                                             "XML",
                                             "mp3",
                                             "MP3",
                                             "",
                                             "exe",
                                             "EXE" })
    {
      assertTrue (sExt + " not found", aMgr.containsMimeTypeForExtension (sExt));
      assertFalse (sExt + " not found", aMgr.getAllMimeTypesForExtension (sExt).isEmpty ());
      assertFalse (sExt + " not found", aMgr.getAllMimeTypeStringsForExtension (sExt).isEmpty ());
      assertNotNull (sExt + " not found", aMgr.getPrimaryMimeTypeForExtension (sExt));
      assertNotNull (sExt + " not found", aMgr.getPrimaryMimeTypeStringForExtension (sExt));
    }
    assertFalse (aMgr.containsMimeTypeForExtension ("aberhallo"));
  }

  @Test
  public void testGetAllMimeTypes ()
  {
    final MimeTypeInfoManager aMgr = MimeTypeInfoManager.getDefaultInstance ();
    for (final IMimeType aMimeType : aMgr.getAllMimeTypes ())
      assertNotNull (aMimeType);
    for (final String sMimeType : aMgr.getAllMimeTypeStrings ())
      assertTrue (StringHelper.hasText (sMimeType));
  }

  @Test
  public void testGetFromExtension ()
  {
    final MimeTypeInfoManager aMgr = MimeTypeInfoManager.getDefaultInstance ();
    assertTrue (aMgr.getAllExtensionsOfMimeType (CMimeType.APPLICATION_MS_EXCEL).contains ("xls"));
    assertFalse (aMgr.getAllExtensionsOfMimeType (CMimeType.APPLICATION_MS_EXCEL).contains ("XLS"));
    assertEquals ("xls", aMgr.getPrimaryExtensionOfMimeType (CMimeType.APPLICATION_MS_EXCEL));

    assertTrue (aMgr.getAllExtensionsOfMimeType (CMimeType.APPLICATION_MS_EXCEL_2007).contains ("xlsx"));
    assertFalse (aMgr.getAllExtensionsOfMimeType (CMimeType.APPLICATION_MS_EXCEL_2007).contains ("XLSX"));
    assertEquals ("xlsx", aMgr.getPrimaryExtensionOfMimeType (CMimeType.APPLICATION_MS_EXCEL_2007));

    assertNull (aMgr.getPrimaryExtensionOfMimeType (CMimeType.MULTIPART_FORMDATA));
  }

  @Test
  public void testKnownSpecial ()
  {
    final Set <IMimeType> aAllKnown = MimeTypeInfoManager.getDefaultInstance ().getAllMimeTypes ();
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_MS_WORD));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_MS_WORD_2007));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_MS_EXCEL));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_MS_EXCEL_2007));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_MS_POWERPOINT));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_MS_POWERPOINT_2007));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_OCTET_STREAM));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_PDF));
    if (false) // special!
      assertTrue (aAllKnown.contains (CMimeType.APPLICATION_FORCE_DOWNLOAD));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_ZIP));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_JSON));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_ATOM_XML));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_RSS_XML));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_SHOCKWAVE_FLASH));
    if (false) // not contained
      assertTrue (aAllKnown.contains (CMimeType.APPLICATION_JAVA_APPLET));
    if (false) // special!
      assertTrue (aAllKnown.contains (CMimeType.APPLICATION_X_WWW_FORM_URLENCODED));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_XML));

    // audio
    assertTrue (aAllKnown.contains (CMimeType.AUDIO_MP3));

    // images
    assertTrue (aAllKnown.contains (CMimeType.IMAGE_BMP));
    assertTrue (aAllKnown.contains (CMimeType.IMAGE_GIF));
    assertTrue (aAllKnown.contains (CMimeType.IMAGE_JPG));
    assertTrue (aAllKnown.contains (CMimeType.IMAGE_PNG));
    assertTrue (aAllKnown.contains (CMimeType.IMAGE_TIFF));
    assertTrue (aAllKnown.contains (CMimeType.IMAGE_PSD));
    assertTrue (aAllKnown.contains (CMimeType.IMAGE_WEBP));
    assertTrue (aAllKnown.contains (CMimeType.IMAGE_X_ICON));

    // multipart
    if (false) // special
      assertTrue (aAllKnown.contains (CMimeType.MULTIPART_FORMDATA));

    // text
    assertTrue (aAllKnown.contains (CMimeType.TEXT_CSV));
    assertTrue (aAllKnown.contains (CMimeType.TEXT_HTML));
    if (false) // not contained
      assertTrue (aAllKnown.contains (CMimeType.TEXT_HTML_SANDBOXED));
    assertTrue (aAllKnown.contains (CMimeType.TEXT_JAVASCRIPT));
    assertTrue (aAllKnown.contains (CMimeType.TEXT_PLAIN));
    assertTrue (aAllKnown.contains (CMimeType.TEXT_XML));
    assertTrue (aAllKnown.contains (CMimeType.TEXT_CSS));
    if (false) // special
      assertTrue (aAllKnown.contains (CMimeType.TEXT_CONTENT_SECURITY_POLICY));
  }
}
