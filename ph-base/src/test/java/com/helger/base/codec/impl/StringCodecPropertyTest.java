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
package com.helger.base.codec.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.codec.DecodeException;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based tests for the String and char[] codecs.
 *
 * @author Philip Helger
 */
public final class StringCodecPropertyTest
{
  /** attr-char of RFC 5987, section 3.2.1 */
  private static final String RFC5987_ATTR_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$&+-.^_`|~";

  /**
   * RFC 1522 "B" encoding round trip with UTF-8 and UTF-16 for any Unicode text.
   */
  @Test
  public void testRFC1522BRoundTripUnicode ()
  {
    new PropertyChecker ().forAll (Generators.of (StandardCharsets.UTF_8, StandardCharsets.UTF_16),
                                   Generators.unicodeStrings (),
                                   (aCharset, sText) -> {
                                     final RFC1522BCodec aCodec = new RFC1522BCodec (aCharset);
                                     assertEquals (sText, aCodec.getDecoded (aCodec.getEncoded (sText)));
                                   });
  }

  /**
   * RFC 1522 "B" encoding round trip with ISO-8859-1, which can only represent Latin-1 text.
   */
  @Test
  public void testRFC1522BRoundTripLatin1 ()
  {
    final RFC1522BCodec aCodec = new RFC1522BCodec (StandardCharsets.ISO_8859_1);
    new PropertyChecker ().forAll (Generators.strings ('\u0000', '\u00ff', 0, 50),
                                   sText -> assertEquals (sText, aCodec.getDecoded (aCodec.getEncoded (sText))));
  }

  /**
   * RFC 1522 "Q" encoding round trip, with and without encoded blanks.
   */
  @Test
  public void testRFC1522QRoundTrip ()
  {
    new PropertyChecker ().forAll (Generators.unicodeStrings (), Generators.booleans (), (sText, aEncodeBlanks) -> {
      final RFC1522QCodec aCodec = new RFC1522QCodec (StandardCharsets.UTF_8);
      aCodec.setEncodeBlanks (aEncodeBlanks.booleanValue ());
      assertEquals (sText, aCodec.getDecoded (aCodec.getEncoded (sText)));
    });
  }

  /**
   * The RFC 1522 decoders must reject invalid input with a {@link DecodeException} only. Valid
   * encodings with a few random edits reach the deeper decoding logic.
   */
  @Test
  public void testRFC1522DecodeOnlyThrowsDecodeException ()
  {
    final IGenerator <AbstractRFC1522Codec> aCodecs = Generators.of (new RFC1522BCodec (), new RFC1522QCodec ());
    // Mutated valid encodings, and short strings from the "grammar alphabet" of RFC 1522
    final IGenerator <String> aMutated = Generators.mutatedByteArrays (Generators.unicodeStrings ()
                                                                                 .map (sText -> new RFC1522BCodec ().getEncoded (sText)
                                                                                                                    .getBytes (StandardCharsets.ISO_8859_1)))
                                                   .map (aBytes -> new String (aBytes, StandardCharsets.ISO_8859_1));
    final IGenerator <String> aGrammar = Generators.strings ("=?BQUTF-8A_", 0, 20);
    new PropertyChecker ().setTries (1000)
                          .forAll (aCodecs, Generators.oneOf (aMutated, aGrammar), (aCodec, sEncoded) -> {
                            try
                            {
                              aCodec.getDecoded (sEncoded);
                            }
                            catch (final DecodeException ex)
                            {
                              // expected for invalid input
                            }
                          });
  }

  /**
   * Short strings from the "grammar alphabet" of RFC 1522 find structural edge cases, like the
   * overlapping prefix and postfix in "=?=" - the only input of that kind. The "Q" codec is used,
   * so that the "B" codec's Base64 problems do not hide other failures - a property stops at its
   * first failure.
   */
  @Test
  public void testRFC1522QGrammarFuzzing ()
  {
    final RFC1522QCodec aCodec = new RFC1522QCodec ();
    // Delimiters only: few, short combinations, so each one is likely to be generated. A rare
    // needle like
    // "=?=" is practically never found within a larger alphabet and longer strings.
    final IGenerator <String> aDelimiters = Generators.strings ("=?", 0, 6);
    final IGenerator <String> aGrammar = Generators.strings ("=?QUTF-8A_", 0, 20);
    new PropertyChecker ().setTries (1000).forAll (Generators.oneOf (aDelimiters, aGrammar), sEncoded -> {
      try
      {
        aCodec.getDecoded (sEncoded);
      }
      catch (final DecodeException ex)
      {
        // expected for invalid input
      }
    });
  }

  /**
   * RFC 2616 quoted string round trip.
   */
  @Test
  public void testRFC2616RoundTrip ()
  {
    final RFC2616Codec aCodec = new RFC2616Codec ();
    new PropertyChecker ().forAll (Generators.unicodeStrings (),
                                   sText -> assertEquals (sText,
                                                          aCodec.getDecodedAsString (aCodec.getEncodedAsString (sText))));
  }

  private static byte [] _percentDecode (final String sEncoded)
  {
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      for (int i = 0; i < sEncoded.length (); ++i)
      {
        final char c = sEncoded.charAt (i);
        if (c == '%')
        {
          aBAOS.write (Integer.parseInt (sEncoded.substring (i + 1, i + 3), 16));
          i += 2;
        }
        else
          aBAOS.write (c);
      }
      return aBAOS.toByteArray ();
    }
  }

  /**
   * RFC 5987 output may only contain attr-chars and percent-encoded bytes, attr-chars are never
   * escaped, and percent-decoding gives the UTF-8 bytes of the input.
   */
  @Test
  public void testRFC5987 ()
  {
    new PropertyChecker ().forAll (Generators.oneOf (Generators.unicodeStrings (),
                                                     Generators.strings (RFC5987_ATTR_CHARS, 0, 50)), sText -> {
                                                       final String sEncoded = RFC5987Codec.getRFC5987EncodedUTF8 (sText);
                                                       for (int i = 0; i < sEncoded.length (); ++i)
                                                       {
                                                         final char c = sEncoded.charAt (i);
                                                         assertTrue (sEncoded,
                                                                     c == '%' || RFC5987_ATTR_CHARS.indexOf (c) >= 0);
                                                       }
                                                       boolean bAllAttrChars = true;
                                                       for (int i = 0; i < sText.length (); ++i)
                                                         if (RFC5987_ATTR_CHARS.indexOf (sText.charAt (i)) < 0)
                                                           bAllAttrChars = false;
                                                       if (bAllAttrChars)
                                                         assertEquals (sText, sEncoded);
                                                       assertArrayEquals (sText.getBytes (StandardCharsets.UTF_8),
                                                                          _percentDecode (sEncoded));
                                                     });
  }
}
