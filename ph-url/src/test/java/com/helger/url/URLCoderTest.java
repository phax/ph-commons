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
package com.helger.url;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.url.URLHelper;

/**
 * Test class for class {@link URLCoder}
 *
 * @author Philip Helger
 */
public final class URLCoderTest
{
  @Test
  public void testUrlEncodeDecode ()
  {
    String sDec = "hallo welt";
    String sEnc = URLCoder.urlEncode (sDec);
    assertEquals ("hallo+welt", sEnc);
    assertEquals (sDec, URLCoder.urlDecode (sEnc));

    // default: UTF-8
    sDec = "äöü";
    sEnc = URLCoder.urlEncode (sDec);
    assertEquals ("%C3%A4%C3%B6%C3%BC", sEnc);
    assertEquals (sDec, URLCoder.urlDecode (sEnc));

    sDec = "äöü";
    sEnc = URLCoder.urlEncode (sDec, StandardCharsets.ISO_8859_1);
    assertEquals ("%E4%F6%FC", sEnc);
    assertEquals (sDec, URLCoder.urlDecode (sEnc, StandardCharsets.ISO_8859_1));

    // Crappy
    try
    {
      URLCoder.urlDecode (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      URLCoder.urlDecode ("a%%%b");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      URLCoder.urlDecode ("a%%%b", StandardCharsets.UTF_8);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    assertNull (URLCoder.urlDecodeOrNull (null));
    assertNull (URLCoder.urlDecodeOrNull ("a%%%b"));
    assertNull (URLCoder.urlDecodeOrNull ("a%%%b", StandardCharsets.UTF_8));
    assertEquals ("Storedsearch_clip_2021-10-27_09-22-59+0200--2021-10-27_09-27-59+0200_1_2.jpg",
                  URLCoder.urlDecodeOrNull ("Storedsearch_clip_2021-10-27_09-22-59%2B0200--2021-10-27_09-27-59%2B0200_1_2.jpg"));

    final URL aURL = URLHelper.getAsURL ("http://www.helger.com/iso6523%3A%3A0088%3A01234");
    assertNotNull (aURL);
    assertEquals ("http://www.helger.com/iso6523%3A%3A0088%3A01234", aURL.toExternalForm ());

    final SimpleURL aSimpleURL = new SimpleURL ("http://www.helger.com/iso6523%3A%3A0088%3A01234");
    assertEquals ("http://www.helger.com/iso6523%3A%3A0088%3A01234", aSimpleURL.getAsStringWithEncodedParameters ());
  }

}
