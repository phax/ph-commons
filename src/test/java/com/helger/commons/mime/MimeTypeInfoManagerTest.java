package com.helger.commons.mime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
  public void testGetFromFileName ()
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

    assertTrue (aMgr.getAllMimeTypeStringsForExtension ("xls").contains (CMimeType.APPLICATION_MS_EXCEL.getAsString ()));
    assertTrue (aMgr.getAllMimeTypeStringsForExtension ("XLS").contains (CMimeType.APPLICATION_MS_EXCEL.getAsString ()));
    assertTrue (aMgr.getAllMimeTypeStringsForExtension ("abersichernicht").isEmpty ());

    assertTrue (aMgr.containsMimeTypeForExtension ("xls"));
    assertTrue (aMgr.containsMimeTypeForExtension ("XLS"));
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
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_XHTML_XML));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_WAP_XHTML_XML));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_TEXT_HTML));
    if (false) // special!
      assertTrue (aAllKnown.contains (CMimeType.APPLICATION_X_WWW_FORM_URLENCODED));

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
    assertTrue (aAllKnown.contains (CMimeType.TEXT_HTML_SANDBOXED));
    assertTrue (aAllKnown.contains (CMimeType.TEXT_JAVASCRIPT));
    assertTrue (aAllKnown.contains (CMimeType.TEXT_PLAIN));
    assertTrue (aAllKnown.contains (CMimeType.TEXT_XHTML_XML));
    assertTrue (aAllKnown.contains (CMimeType.TEXT_XML));
    assertTrue (aAllKnown.contains (CMimeType.TEXT_CSS));
    if (false) // special
      assertTrue (aAllKnown.contains (CMimeType.TEXT_CONTENT_SECURITY_POLICY));
  }
}
