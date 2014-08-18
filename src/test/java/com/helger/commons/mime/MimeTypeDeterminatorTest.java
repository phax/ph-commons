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
package com.helger.commons.mime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.mock.AbstractPhlocTestCase;

/**
 * Test class for class {@link MimeTypeDeterminator}
 * 
 * @author Philip Helger
 */
public final class MimeTypeDeterminatorTest extends AbstractPhlocTestCase
{
  /**
   * Test for method getMIMETypeFromBytes
   */
  @Test
  public void testGetMIMEType ()
  {
    assertEquals (CMimeType.APPLICATION_OCTET_STREAM, MimeTypeDeterminator.getMimeTypeFromBytes (null));
    assertEquals (CMimeType.APPLICATION_OCTET_STREAM,
                  MimeTypeDeterminator.getMimeTypeFromString ("Anything", CCharset.CHARSET_ISO_8859_1_OBJ));
    assertEquals (CMimeType.TEXT_XML,
                  MimeTypeDeterminator.getMimeTypeFromString ("<?xml ", CCharset.CHARSET_ISO_8859_1_OBJ));
    assertEquals (CMimeType.APPLICATION_PDF,
                  MimeTypeDeterminator.getMimeTypeFromString ("%PDF\n", CCharset.CHARSET_ISO_8859_1_OBJ));
    assertEquals (CMimeType.IMAGE_GIF,
                  MimeTypeDeterminator.getMimeTypeFromString ("GIF87a\n", CCharset.CHARSET_ISO_8859_1_OBJ));
    assertEquals (CMimeType.IMAGE_GIF,
                  MimeTypeDeterminator.getMimeTypeFromString ("GIF89a\n", CCharset.CHARSET_ISO_8859_1_OBJ));
    assertEquals (CMimeType.IMAGE_TIFF,
                  MimeTypeDeterminator.getMimeTypeFromString ("MM\n", CCharset.CHARSET_ISO_8859_1_OBJ));
    assertEquals (CMimeType.IMAGE_TIFF,
                  MimeTypeDeterminator.getMimeTypeFromString ("II\n", CCharset.CHARSET_ISO_8859_1_OBJ));
    assertEquals (CMimeType.IMAGE_PSD,
                  MimeTypeDeterminator.getMimeTypeFromString ("8BPS\n", CCharset.CHARSET_ISO_8859_1_OBJ));
    assertEquals (CMimeType.IMAGE_JPG,
                  MimeTypeDeterminator.getMimeTypeFromBytes (new byte [] { (byte) 0xff, (byte) 0xd8, 0 }));
    assertEquals (CMimeType.IMAGE_PNG,
                  MimeTypeDeterminator.getMimeTypeFromBytes (new byte [] { (byte) 0x89,
                                                                          0x50,
                                                                          0x4e,
                                                                          0x47,
                                                                          0x0d,
                                                                          0x0a,
                                                                          0x1a,
                                                                          0x0a,
                                                                          0 }));
  }

  @Test
  public void testGetFromFileName ()
  {
    assertEquals (CMimeType.APPLICATION_MS_EXCEL.getAsString (),
                  MimeTypeDeterminator.getMimeTypeFromFilename ("test.xls"));
    assertNull (MimeTypeDeterminator.getMimeTypeFromFilename ("test.hastenichgesehen"));

    assertEquals (CMimeType.APPLICATION_MS_EXCEL, MimeTypeDeterminator.getMimeTypeObjectFromFilename ("test.xls"));
    assertNull (MimeTypeDeterminator.getMimeTypeObjectFromFilename ("test.hastenichgesehen"));

    assertEquals (CMimeType.APPLICATION_MS_EXCEL.getAsString (), MimeTypeDeterminator.getMimeTypeFromExtension ("xls"));
    assertEquals (CMimeType.APPLICATION_MS_EXCEL.getAsString (), MimeTypeDeterminator.getMimeTypeFromExtension ("XLS"));
    assertNull (MimeTypeDeterminator.getMimeTypeFromExtension ("abersichernicht"));

    assertEquals (CMimeType.APPLICATION_MS_EXCEL, MimeTypeDeterminator.getMimeTypeObjectFromExtension ("xls"));
    assertEquals (CMimeType.APPLICATION_MS_EXCEL, MimeTypeDeterminator.getMimeTypeObjectFromExtension ("XLS"));
    assertNull (MimeTypeDeterminator.getMimeTypeFromExtension ("abersichernicht"));
  }

  @Test
  public void testTypeOf ()
  {
    for (final String sMimeType : MimeTypeDeterminator.getAllKnownMimeTypes ())
      assertNotNull (sMimeType);
    for (final String sMimeType : MimeTypeDeterminator.getAllKnownMimeTypeFilenameMappings ().values ())
      assertNotNull (sMimeType);
  }

  @Test
  public void testConstantsKnown ()
  {
    final Collection <String> aAllKnown = MimeTypeDeterminator.getAllKnownMimeTypes ();

    // applications
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_ATOM_XML.getAsString ()));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_MS_EXCEL.getAsString ()));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_MS_EXCEL_2007.getAsString ()));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_MS_POWERPOINT.getAsString ()));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_MS_POWERPOINT_2007.getAsString ()));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_MS_WORD.getAsString ()));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_MS_WORD_2007.getAsString ()));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_OCTET_STREAM.getAsString ()));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_PDF.getAsString ()));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_RSS_XML.getAsString ()));
    if (false) // special!
      assertTrue (aAllKnown.contains (CMimeType.APPLICATION_FORCE_DOWNLOAD.getAsString ()));
    assertTrue (aAllKnown.contains (CMimeType.APPLICATION_ZIP.getAsString ()));

    // audio
    assertTrue (aAllKnown.contains (CMimeType.AUDIO_MP3.getAsString ()));

    // images
    assertTrue (aAllKnown.contains (CMimeType.IMAGE_BMP.getAsString ()));
    assertTrue (aAllKnown.contains (CMimeType.IMAGE_GIF.getAsString ()));
    assertTrue (aAllKnown.contains (CMimeType.IMAGE_JPG.getAsString ()));
    assertTrue (aAllKnown.contains (CMimeType.IMAGE_PNG.getAsString ()));

    // text
    assertTrue (aAllKnown.contains (CMimeType.TEXT_CSS.getAsString ()));
    assertTrue (aAllKnown.contains (CMimeType.TEXT_HTML.getAsString ()));
    assertTrue (aAllKnown.contains (CMimeType.TEXT_JAVASCRIPT.getAsString ()));
    assertTrue (aAllKnown.contains (CMimeType.TEXT_PLAIN.getAsString ()));
    assertTrue (aAllKnown.contains (CMimeType.TEXT_XML.getAsString ()));
  }
}
