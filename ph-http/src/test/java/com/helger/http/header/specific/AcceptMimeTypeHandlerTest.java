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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.base.equals.EqualsHelper;
import com.helger.mime.CMimeType;
import com.helger.mime.EMimeContentType;
import com.helger.mime.parse.MimeTypeParserException;

/**
 * Test class for class {@link AcceptMimeTypeHandler}
 *
 * @author Philip Helger
 */
public final class AcceptMimeTypeHandlerTest
{
  @Test
  public void testChrome13 ()
  {
    final AcceptMimeTypeList c = AcceptMimeTypeHandler.getAcceptMimeTypes ("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    assertNotNull (c);
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("text/html")));
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("application/xhtml+xml")));
    assertTrue (EqualsHelper.equals (0.9d, c.getQualityOfMimeType ("application/xml")));
    assertTrue (EqualsHelper.equals (0.8d, c.getQualityOfMimeType ("text/other")));
  }

  @Test
  public void testFirefox1_5 ()
  {
    final AcceptMimeTypeList c = AcceptMimeTypeHandler.getAcceptMimeTypes ("text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
    assertNotNull (c);
    assertTrue (EqualsHelper.equals (0.9d, c.getQualityOfMimeType ("text/html")));
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("application/xhtml+xml")));
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("application/xml")));
    assertTrue (EqualsHelper.equals (0.5d, c.getQualityOfMimeType ("text/other")));

    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("text/xml")));
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("application/xml")));
  }

  @Test
  public void testFirefox6 ()
  {
    final AcceptMimeTypeList c = AcceptMimeTypeHandler.getAcceptMimeTypes ("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    assertNotNull (c);
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("text/html")));
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("application/xhtml+xml")));
    assertTrue (EqualsHelper.equals (0.9d, c.getQualityOfMimeType ("application/xml")));
    assertTrue (EqualsHelper.equals (0.8d, c.getQualityOfMimeType ("text/other")));
  }

  @Test
  public void testIE6 ()
  {
    final AcceptMimeTypeList c = AcceptMimeTypeHandler.getAcceptMimeTypes ("*/*");
    assertNotNull (c);
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("text/html")));
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("application/xhtml+xml")));
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("application/xml")));
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("text/other")));
  }

  @Test
  public void testIE8 ()
  {
    final AcceptMimeTypeList c = AcceptMimeTypeHandler.getAcceptMimeTypes ("image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, */*");
    assertNotNull (c);
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("text/html")));
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("application/xhtml+xml")));
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("application/xml")));
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("text/other")));
  }

  @Test
  public void testIE9 ()
  {
    final AcceptMimeTypeList c = AcceptMimeTypeHandler.getAcceptMimeTypes ("text/html, application/xhtml+xml, */*");
    assertNotNull (c);
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("text/html")));
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("application/xhtml+xml")));
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("application/xml")));
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("text/other")));
  }

  @Test
  public void testGenericSubtype ()
  {
    final AcceptMimeTypeList c = AcceptMimeTypeHandler.getAcceptMimeTypes ("text/*,application/html;q=0.9,application/*;q=0.8");
    assertNotNull (c);
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("text/html")));
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("text/*")));
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfMimeType ("text/anythingelse")));
    assertTrue (EqualsHelper.equals (0.9d, c.getQualityOfMimeType ("application/html")));
    assertTrue (EqualsHelper.equals (0.8d, c.getQualityOfMimeType ("application/xml")));
    assertTrue (EqualsHelper.equals (0d, c.getQualityOfMimeType ("image/gif")));

    assertFalse (c.explicitlySupportsMimeType ("text/html"));
    assertTrue (c.explicitlySupportsMimeType ("text/*"));
    assertFalse (c.explicitlySupportsMimeType ("text/anythingelse"));
    assertTrue (c.explicitlySupportsMimeType ("application/html"));
    assertFalse (c.explicitlySupportsMimeType ("application/xml"));
    assertFalse (c.explicitlySupportsMimeType ("image/gif"));
  }

  @Test
  public void testGetAsHttpHeaderValue () throws MimeTypeParserException
  {
    final AcceptMimeTypeList aList1 = new AcceptMimeTypeList ();
    aList1.addMimeType ("text/xml", 1);
    aList1.addMimeType ("application/xml", 1);
    aList1.addMimeType ("application/json", 0.9);
    aList1.addMimeType ("*", 0);
    final String s = aList1.getAsHttpHeaderValue ();
    assertEquals ("text/xml; q=1.0, application/xml; q=1.0, application/json; q=0.9, */*; q=0.0", s);
    final AcceptMimeTypeList aList2 = AcceptMimeTypeHandler.getAcceptMimeTypes (s);
    assertNotNull (aList2);
    assertEquals (aList1, aList2);
  }

  @Test
  public void testGetPreferredOneWithDefault ()
  {
    final AcceptMimeTypeList aList = new AcceptMimeTypeList ();
    aList.addMimeType (CMimeType.APPLICATION_XML, 1);
    aList.addMimeType (CMimeType.APPLICATION_JSON, 0.9);
    aList.addMimeType (AcceptMimeTypeHandler.ANY_MIMETYPE, 0.1);

    // Straight forward cases
    assertEquals (CMimeType.APPLICATION_XML,
                  aList.getPreferredMimeType (CMimeType.APPLICATION_XML,
                                              CMimeType.APPLICATION_JSON,
                                              CMimeType.APPLICATION_GZIP));
    assertEquals (CMimeType.APPLICATION_JSON,
                  aList.getPreferredMimeType (CMimeType.APPLICATION_JSON, CMimeType.APPLICATION_GZIP));
    assertEquals (CMimeType.APPLICATION_GZIP, aList.getPreferredMimeType (CMimeType.APPLICATION_GZIP));
    assertNull (aList.getPreferredMimeType ());

    // Straight forward cases - reverse array order
    assertEquals (CMimeType.APPLICATION_XML,
                  aList.getPreferredMimeType (CMimeType.APPLICATION_GZIP,
                                              CMimeType.APPLICATION_JSON,
                                              CMimeType.APPLICATION_XML));
    assertEquals (CMimeType.APPLICATION_JSON,
                  aList.getPreferredMimeType (CMimeType.APPLICATION_GZIP, CMimeType.APPLICATION_JSON));
    assertEquals (CMimeType.APPLICATION_JSON, aList.getPreferredMimeType (CMimeType.APPLICATION_JSON));

    // Multi of the same
    assertEquals (CMimeType.APPLICATION_XML,
                  aList.getPreferredMimeType (CMimeType.APPLICATION_XML,
                                              CMimeType.APPLICATION_XML,
                                              CMimeType.APPLICATION_JSON,
                                              CMimeType.APPLICATION_JSON,
                                              CMimeType.APPLICATION_GZIP,
                                              CMimeType.APPLICATION_GZIP));
    assertEquals (CMimeType.APPLICATION_JSON,
                  aList.getPreferredMimeType (CMimeType.APPLICATION_JSON,
                                              CMimeType.APPLICATION_JSON,
                                              CMimeType.APPLICATION_GZIP,
                                              CMimeType.APPLICATION_GZIP));
    assertEquals (CMimeType.APPLICATION_GZIP,
                  aList.getPreferredMimeType (CMimeType.APPLICATION_GZIP, CMimeType.APPLICATION_GZIP));
  }

  @Test
  public void testGetPreferredOneWithDefaultButMinimum ()
  {
    final AcceptMimeTypeList aList = new AcceptMimeTypeList ();
    aList.addMimeType (CMimeType.APPLICATION_XML, 1);
    aList.addMimeType (CMimeType.APPLICATION_JSON, 0.9);
    aList.addMimeType (AcceptMimeTypeHandler.ANY_MIMETYPE, 0);

    // Straight forward cases
    assertEquals (CMimeType.APPLICATION_XML,
                  aList.getPreferredMimeType (CMimeType.APPLICATION_XML,
                                              CMimeType.APPLICATION_JSON,
                                              CMimeType.APPLICATION_GZIP));
    assertEquals (CMimeType.APPLICATION_JSON,
                  aList.getPreferredMimeType (CMimeType.APPLICATION_JSON, CMimeType.APPLICATION_GZIP));
    assertNull (aList.getPreferredMimeType (CMimeType.APPLICATION_GZIP));
    assertNull (aList.getPreferredMimeType (CMimeType.APPLICATION_GZIP, CMimeType.TEXT_CSS, CMimeType.AUDIO_MP3));
    assertNull (aList.getPreferredMimeType ());

    // Straight forward cases - reverse array order
    assertEquals (CMimeType.APPLICATION_XML,
                  aList.getPreferredMimeType (CMimeType.APPLICATION_GZIP,
                                              CMimeType.APPLICATION_JSON,
                                              CMimeType.APPLICATION_XML));
    assertEquals (CMimeType.APPLICATION_JSON,
                  aList.getPreferredMimeType (CMimeType.APPLICATION_GZIP, CMimeType.APPLICATION_JSON));
    assertEquals (CMimeType.APPLICATION_JSON, aList.getPreferredMimeType (CMimeType.APPLICATION_JSON));
  }

  @Test
  public void testGetPreferredOneWithSpecificDefault ()
  {
    final AcceptMimeTypeList aList = new AcceptMimeTypeList ();
    aList.addMimeType (CMimeType.APPLICATION_XML, 1);
    aList.addMimeType (CMimeType.APPLICATION_JSON, 0.9);
    aList.addMimeType (EMimeContentType.AUDIO.buildMimeType ("*"), 0.1);

    // Straight forward cases
    assertEquals (CMimeType.APPLICATION_XML,
                  aList.getPreferredMimeType (CMimeType.APPLICATION_XML,
                                              CMimeType.APPLICATION_JSON,
                                              CMimeType.AUDIO_MP3));
    assertEquals (CMimeType.APPLICATION_JSON,
                  aList.getPreferredMimeType (CMimeType.APPLICATION_JSON, CMimeType.AUDIO_MP3));
    assertEquals (CMimeType.AUDIO_MP3, aList.getPreferredMimeType (CMimeType.AUDIO_MP3));
    assertNull (aList.getPreferredMimeType ());

    // Straight forward cases - reverse array order
    assertEquals (CMimeType.APPLICATION_XML,
                  aList.getPreferredMimeType (CMimeType.AUDIO_MP3,
                                              CMimeType.APPLICATION_JSON,
                                              CMimeType.APPLICATION_XML));
    assertEquals (CMimeType.APPLICATION_JSON,
                  aList.getPreferredMimeType (CMimeType.AUDIO_MP3, CMimeType.APPLICATION_JSON));
    assertEquals (CMimeType.APPLICATION_JSON, aList.getPreferredMimeType (CMimeType.APPLICATION_JSON));
  }

  @Test
  public void testGetPreferredOneNoDefault ()
  {
    final AcceptMimeTypeList aList = new AcceptMimeTypeList ();
    aList.addMimeType (CMimeType.APPLICATION_XML, 1);
    aList.addMimeType (CMimeType.APPLICATION_JSON, 0.9);

    // Straight forward cases
    assertEquals (CMimeType.APPLICATION_XML,
                  aList.getPreferredMimeType (CMimeType.APPLICATION_XML,
                                              CMimeType.APPLICATION_JSON,
                                              CMimeType.APPLICATION_GZIP));
    assertEquals (CMimeType.APPLICATION_JSON,
                  aList.getPreferredMimeType (CMimeType.APPLICATION_JSON, CMimeType.APPLICATION_GZIP));
    assertNull (aList.getPreferredMimeType (CMimeType.APPLICATION_GZIP));
    assertNull (aList.getPreferredMimeType ());

    // Straight forward cases - reverse array order
    assertEquals (CMimeType.APPLICATION_XML,
                  aList.getPreferredMimeType (CMimeType.APPLICATION_GZIP,
                                              CMimeType.APPLICATION_JSON,
                                              CMimeType.APPLICATION_XML));
    assertEquals (CMimeType.APPLICATION_JSON,
                  aList.getPreferredMimeType (CMimeType.APPLICATION_GZIP, CMimeType.APPLICATION_JSON));
    assertEquals (CMimeType.APPLICATION_JSON, aList.getPreferredMimeType (CMimeType.APPLICATION_JSON));
    assertNull (aList.getPreferredMimeType ());
  }

  @Test
  public void testGetPreferredOneComplex ()
  {
    final AcceptMimeTypeList aList = new AcceptMimeTypeList ();
    aList.addMimeType (CMimeType.APPLICATION_XML, 1);
    aList.addMimeType (CMimeType.APPLICATION_JSON, 0.9);
    aList.addMimeType (EMimeContentType.IMAGE.buildMimeType ("*"), 0.3);
    aList.addMimeType (EMimeContentType.AUDIO.buildMimeType ("*"), 0.2);
    aList.addMimeType (AcceptMimeTypeHandler.ANY_MIMETYPE, 0.1);

    // Straight forward cases
    assertEquals (CMimeType.APPLICATION_XML,
                  aList.getPreferredMimeType (CMimeType.APPLICATION_XML,
                                              CMimeType.APPLICATION_JSON,
                                              CMimeType.IMAGE_GIF,
                                              CMimeType.AUDIO_MP3,
                                              CMimeType.TEXT_CSS));
    assertEquals (CMimeType.APPLICATION_JSON,
                  aList.getPreferredMimeType (CMimeType.APPLICATION_JSON,
                                              CMimeType.IMAGE_GIF,
                                              CMimeType.AUDIO_MP3,
                                              CMimeType.TEXT_CSS));
    assertEquals (CMimeType.IMAGE_GIF,
                  aList.getPreferredMimeType (CMimeType.IMAGE_GIF, CMimeType.AUDIO_MP3, CMimeType.TEXT_CSS));
    assertEquals (CMimeType.AUDIO_MP3, aList.getPreferredMimeType (CMimeType.AUDIO_MP3, CMimeType.TEXT_CSS));
    assertEquals (CMimeType.TEXT_CSS, aList.getPreferredMimeType (CMimeType.TEXT_CSS));
    assertNull (aList.getPreferredMimeType ());
  }
}
