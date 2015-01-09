/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.url;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.encode.IdentityEncoder;
import com.helger.commons.io.streams.StreamUtils;
import com.helger.commons.mime.CMimeType;
import com.helger.commons.url.encode.URLParameterEncoder;

/**
 * Test class for class {@link URLUtils}.
 *
 * @author Philip Helger
 */
public final class URLUtilsTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (URLUtilsTest.class);

  @Test
  public void testGetCleanURL ()
  {
    assertEquals ("aeoeue", URLUtils.getCleanURLPartWithoutUmlauts ("äöü"));
    assertEquals ("AeoeUe", URLUtils.getCleanURLPartWithoutUmlauts ("ÄöÜ"));
    assertEquals ("Ae-Uesz", URLUtils.getCleanURLPartWithoutUmlauts ("Ä Üß"));
    assertEquals ("Weisze-Waeste", URLUtils.getCleanURLPartWithoutUmlauts ("Weiße Wäste"));
    assertEquals ("hallo", URLUtils.getCleanURLPartWithoutUmlauts ("hállò"));
    assertEquals ("ffi", URLUtils.getCleanURLPartWithoutUmlauts ("\uFB03"));
    assertEquals ("ffl", URLUtils.getCleanURLPartWithoutUmlauts ("\uFB04"));
    assertEquals ("hallo;jsessionid=1234", URLUtils.getCleanURLPartWithoutUmlauts ("hállò;jsessionid=1234"));
  }

  @Test
  public void testUrlEncodeDecode ()
  {
    String sDec = "hallo welt";
    String sEnc = URLUtils.urlEncode (sDec);
    assertEquals ("hallo+welt", sEnc);
    assertEquals (sDec, URLUtils.urlDecode (sEnc));

    // default: UTF-8
    sDec = "äöü";
    sEnc = URLUtils.urlEncode (sDec);
    assertEquals ("%C3%A4%C3%B6%C3%BC", sEnc);
    assertEquals (sDec, URLUtils.urlDecode (sEnc));

    sDec = "äöü";
    sEnc = URLUtils.urlEncode (sDec, CCharset.CHARSET_ISO_8859_1);
    assertEquals ("%E4%F6%FC", sEnc);
    assertEquals (sDec, URLUtils.urlDecode (sEnc, CCharset.CHARSET_ISO_8859_1));
  }

  @Test
  public void testGetURLData ()
  {
    IURLData aData = URLUtils.getAsURLData ("http://www.phloc.com/folder?x=y&a=b#c");
    assertNotNull (aData);
    assertEquals (EURLProtocol.HTTP, aData.getProtocol ());
    assertEquals ("http://www.phloc.com/folder", aData.getPath ());
    assertEquals (2, aData.getParamCount ());
    assertEquals ("y", aData.getAllParams ().get ("x"));
    assertEquals ("b", aData.getAllParams ().get ("a"));
    assertEquals ("c", aData.getAnchor ());

    aData = URLUtils.getAsURLData ("?x=y&a=b#c");
    assertNotNull (aData);
    assertNull (aData.getProtocol ());
    assertEquals ("", aData.getPath ());
    assertEquals (2, aData.getParamCount ());
    assertEquals ("y", aData.getAllParams ().get ("x"));
    assertEquals ("b", aData.getAllParams ().get ("a"));
    assertEquals ("c", aData.getAnchor ());

    aData = URLUtils.getAsURLData ("?x=y&=b#c");
    assertNotNull (aData);
    assertNull (aData.getProtocol ());
    assertEquals ("", aData.getPath ());
    assertEquals (1, aData.getParamCount ());
    assertEquals ("y", aData.getAllParams ().get ("x"));
    assertEquals ("c", aData.getAnchor ());
  }

  @Test
  @Ignore ("Works only when being online")
  public void testGetInputStream ()
  {
    try
    {
      final InputStream aIS = URLUtils.getInputStream (new URL ("http://www.orf.at"), 3000, -1, null, null);
      final byte [] aContent = StreamUtils.getAllBytes (aIS);
      s_aLogger.info ("Read " + aContent.length + " bytes");
    }
    catch (final Throwable t)
    {
      // ignore
      s_aLogger.info ("Failed to GET: " + t.getMessage ());
    }
  }

  @Test
  @Ignore ("Works only when being online")
  public void testPosttInputStream ()
  {
    try
    {
      final InputStream aIS = URLUtils.postAndGetInputStream (new URL ("http://localhost:8080/view/?menuitem=e2p"),
                                                              1000,
                                                              -1,
                                                              CMimeType.APPLICATION_X_WWW_FORM_URLENCODED,
                                                              CharsetManager.getAsBytes ("sender=true",
                                                                                         CCharset.CHARSET_UTF_8_OBJ),
                                                              null,
                                                              null,
                                                              null);
      final byte [] aContent = StreamUtils.getAllBytes (aIS);
      s_aLogger.info ("Read " +
                      aContent.length +
                      " bytes: " +
                      CharsetManager.getAsString (aContent, CCharset.CHARSET_UTF_8_OBJ));
    }
    catch (final Throwable t)
    {
      // ignore
      s_aLogger.info ("Failed to POST: " + t.getMessage ());
    }
  }

  @Test
  public void testGetApplicationFormEncoded ()
  {
    final URLParameterEncoder enc = new URLParameterEncoder (CCharset.CHARSET_UTF_8_OBJ);
    assertEquals ("", URLUtils.getApplicationFormEncoded (null, enc));
    assertEquals ("", URLUtils.getApplicationFormEncoded (new SMap (), enc));
    assertEquals ("a=b", URLUtils.getApplicationFormEncoded (new SMap ().add ("a", "b"), enc));
    assertEquals ("a=b&c=d", URLUtils.getApplicationFormEncoded (new SMap ().add ("a", "b").add ("c", "d"), enc));
    assertEquals ("a=b&c=d&e=f+g",
                  URLUtils.getApplicationFormEncoded (new SMap ().add ("a", "b").add ("c", "d").add ("e", "f g"), enc));
    assertEquals ("a=b&c=d%26e", URLUtils.getApplicationFormEncoded (new SMap ().add ("a", "b").add ("c", "d&e"), enc));

    // Using identity encoder
    assertEquals ("a=b&c=d&e",
                  URLUtils.getApplicationFormEncoded (new SMap ().add ("a", "b").add ("c", "d&e"),
                                                      IdentityEncoder.<String> create ()));
  }
}
