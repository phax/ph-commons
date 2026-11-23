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
package com.helger.url.codec;

import static org.junit.Assert.assertEquals;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.unittest.property.Generators;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based tests for {@link URLCoder}, {@link URLParameterEncoder} and
 * {@link URLParameterDecoder}, using the JDK URL encoder and decoder as oracles in both directions.
 *
 * @author Philip Helger
 */
public final class URLCoderPropertyTest
{
  @Test
  public void testRoundTrip ()
  {
    new PropertyChecker ().forAll (Generators.unicodeStrings (),
                                   sText -> assertEquals (sText, URLCoder.urlDecode (URLCoder.urlEncode (sText))));
  }

  /**
   * The JDK decoder must understand our encoding.
   */
  @Test
  public void testJdkDecodesOurEncoding ()
  {
    new PropertyChecker ().forAll (Generators.unicodeStrings (),
                                   sText -> assertEquals (sText,
                                                          URLDecoder.decode (URLCoder.urlEncode (sText),
                                                                             StandardCharsets.UTF_8)));
  }

  /**
   * Our decoder must understand the JDK encoding, although the two differ in details (the JDK keeps
   * '*' and encodes '~').
   */
  @Test
  public void testWeDecodeJdkEncoding ()
  {
    new PropertyChecker ().forAll (Generators.unicodeStrings (),
                                   sText -> assertEquals (sText,
                                                          URLCoder.urlDecode (URLEncoder.encode (sText,
                                                                                                 StandardCharsets.UTF_8))));
  }

  /**
   * The parameter encoder and decoder must work together for all charsets that can represent the
   * text.
   */
  @Test
  public void testParameterRoundTrip ()
  {
    new PropertyChecker ().forAll (Generators.strings ('\u0000', '\u00ff', 0, 50),
                                   Generators.of (StandardCharsets.UTF_8, StandardCharsets.ISO_8859_1),
                                   (sText, aCharset) -> assertEquals (sText,
                                                                      new URLParameterDecoder (aCharset).getDecoded (new URLParameterEncoder (aCharset).getEncoded (sText))));
  }

  /**
   * Decoding arbitrary input must either work or fail with an {@link IllegalArgumentException}, as
   * documented by {@link URLCoder#urlDecode(String)}. The "OrNull" variant must never throw.
   */
  @Test
  public void testDecodeArbitraryInput ()
  {
    new PropertyChecker ().forAll (Generators.strings ("%+0123456789abcdefABCDEFxyz\u00e4\u20ac", 0, 30), sEncoded -> {
      URLCoder.urlDecodeOrNull (sEncoded);
      try
      {
        URLCoder.urlDecode (sEncoded);
      }
      catch (final IllegalArgumentException ex)
      {
        // expected for invalid input
      }
    });
  }
}
