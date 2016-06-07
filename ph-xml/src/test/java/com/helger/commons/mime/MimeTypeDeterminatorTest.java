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

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.mock.AbstractCommonsTestCase;

/**
 * Test class for class {@link MimeTypeDeterminator}
 *
 * @author Philip Helger
 */
public final class MimeTypeDeterminatorTest extends AbstractCommonsTestCase
{
  /**
   * Test for method getMIMETypeFromBytes
   */
  @Test
  public void testGetMIMEType ()
  {
    assertEquals (CMimeType.APPLICATION_OCTET_STREAM, MimeTypeDeterminator.getInstance ().getMimeTypeFromBytes (null));
    assertEquals (CMimeType.APPLICATION_OCTET_STREAM,
                  MimeTypeDeterminator.getInstance ().getMimeTypeFromString ("Anything",
                                                                             CCharset.CHARSET_ISO_8859_1_OBJ));
    assertEquals (CMimeType.TEXT_XML,
                  MimeTypeDeterminator.getInstance ().getMimeTypeFromString ("<?xml ",
                                                                             CCharset.CHARSET_ISO_8859_1_OBJ));
    assertEquals (CMimeType.APPLICATION_PDF,
                  MimeTypeDeterminator.getInstance ().getMimeTypeFromString ("%PDF\n",
                                                                             CCharset.CHARSET_ISO_8859_1_OBJ));
    assertEquals (CMimeType.IMAGE_GIF,
                  MimeTypeDeterminator.getInstance ().getMimeTypeFromString ("GIF87a\n",
                                                                             CCharset.CHARSET_ISO_8859_1_OBJ));
    assertEquals (CMimeType.IMAGE_GIF,
                  MimeTypeDeterminator.getInstance ().getMimeTypeFromString ("GIF89a\n",
                                                                             CCharset.CHARSET_ISO_8859_1_OBJ));
    assertEquals (CMimeType.IMAGE_TIFF,
                  MimeTypeDeterminator.getInstance ().getMimeTypeFromString ("MM\n", CCharset.CHARSET_ISO_8859_1_OBJ));
    assertEquals (CMimeType.IMAGE_TIFF,
                  MimeTypeDeterminator.getInstance ().getMimeTypeFromString ("II\n", CCharset.CHARSET_ISO_8859_1_OBJ));
    assertEquals (CMimeType.IMAGE_PSD,
                  MimeTypeDeterminator.getInstance ().getMimeTypeFromString ("8BPS\n",
                                                                             CCharset.CHARSET_ISO_8859_1_OBJ));
    assertEquals (CMimeType.IMAGE_JPG,
                  MimeTypeDeterminator.getInstance ()
                                      .getMimeTypeFromBytes (new byte [] { (byte) 0xff, (byte) 0xd8, 0 }));
    assertEquals (CMimeType.IMAGE_PNG,
                  MimeTypeDeterminator.getInstance ()
                                      .getMimeTypeFromBytes (new byte [] { (byte) 0x89,
                                                                           0x50,
                                                                           0x4e,
                                                                           0x47,
                                                                           0x0d,
                                                                           0x0a,
                                                                           0x1a,
                                                                           0x0a,
                                                                           0 }));
  }
}
