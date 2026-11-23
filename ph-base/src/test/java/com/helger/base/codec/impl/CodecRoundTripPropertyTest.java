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
import java.util.Arrays;

import org.junit.Test;

import com.helger.base.codec.DecodeException;
import com.helger.base.codec.IByteArrayCodec;
import com.helger.base.codec.base64.Base64Codec;
import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based round trip tests for several {@link IByteArrayCodec} implementations.
 *
 * @author Philip Helger
 */
public final class CodecRoundTripPropertyTest
{
  private static void _assertRoundTrip (final IByteArrayCodec aCodec, final byte [] aBytes)
  {
    assertArrayEquals (aBytes, aCodec.getDecoded (aCodec.getEncoded (aBytes)));
  }

  /**
   * One property, many codecs: the codec is generated as well. On failure, the message names the
   * codec instance that failed.
   */
  @Test
  public void testRoundTripAllCodecs ()
  {
    final IGenerator <IByteArrayCodec> aCodecs = Generators.of (new Base16Codec (),
                                                                new Base32Codec (),
                                                                new Base32Codec (true),
                                                                new Base32Codec ().setAddPadding (false),
                                                                new RFC3986Codec (),
                                                                new QuotedPrintableCodec (),
                                                                new RunLengthCodec (),
                                                                new LZWCodec (),
                                                                new FlateCodec (),
                                                                new GZIPCodec (),
                                                                new Base64Codec ());
    new PropertyChecker ().setTries (1000)
                          .forAll (aCodecs, Generators.byteArrays (), CodecRoundTripPropertyTest::_assertRoundTrip);
  }

  /**
   * Random bytes rarely contain long runs or repeated sequences, so compression codecs would mostly
   * be tested with their worst case. This generator only uses 3 different byte values, which creates
   * lots of runs and repetitions. Large inputs fill the LZW dictionary (4096 entries), which forces
   * the clear-table code path.
   */
  @Test
  public void testCompressionCodecsWithRedundantInput ()
  {
    final IGenerator <byte []> aRedundant = Generators.byteArrays (Generators.ints (0, 2)
                                                                             .map (aValue -> Byte.valueOf (aValue.byteValue ())),
                                                                   0,
                                                                   5000);
    new PropertyChecker ().forAll (aRedundant, aBytes -> _assertRoundTrip (new RunLengthCodec (), aBytes));
    new PropertyChecker ().forAll (aRedundant, aBytes -> _assertRoundTrip (new LZWCodec (), aBytes));
  }

  /**
   * Invariant property: Base16 output has exactly twice the input length and only uses lower case
   * hex digits.
   */
  @Test
  public void testBase16Shape ()
  {
    new PropertyChecker ().forAll (Generators.byteArrays (), aBytes -> {
      final String sEncoded = new Base16Codec ().getEncodedAsString (aBytes, StandardCharsets.ISO_8859_1);
      assertEquals (aBytes.length * 2, sEncoded.length ());
      assertTrue (sEncoded, sEncoded.matches ("[0-9a-f]*"));
    });
  }

  /**
   * Robustness property: the Base16 decoder must reject invalid input with a {@link DecodeException}
   * only and never with another runtime exception.
   */
  @Test
  public void testBase16DecodeOnlyThrowsDecodeException ()
  {
    new PropertyChecker ().forAll (Generators.byteArrays (), aBytes -> {
      try
      {
        new Base16Codec ().getDecoded (aBytes);
      }
      catch (final DecodeException ex)
      {
        // expected for invalid input
      }
    });
  }

  /**
   * RFC 3986 encoded output must only consist of unreserved characters, '+' for a space and
   * percent-encoded triplets with upper case hex digits. The generated strings contain any Unicode
   * code point, so this also tests multi-byte UTF-8 sequences.
   */
  @Test
  public void testRFC3986OutputAlphabet ()
  {
    final RFC3986Codec aCodec = new RFC3986Codec ();
    new PropertyChecker ().forAll (Generators.unicodeStrings (), sText -> {
      final String sEncoded = aCodec.getEncodedAsString (sText, StandardCharsets.UTF_8);
      assertTrue (sEncoded, sEncoded.matches ("([A-Za-z0-9\\-_.~+]|%[0-9A-F]{2})*"));
      assertEquals (sText, aCodec.getDecodedAsString (sEncoded, StandardCharsets.UTF_8));
    });
  }
  /**
   * The decode-only ASCIIHex codec must decode the Base16 encoding, also with whitespace, an odd
   * number of digits and the "&gt;" end marker. Whitespace is inserted at a generated position.
   */
  @Test
  public void testASCIIHexDecodesBase16 ()
  {
    final ASCIIHexCodec aASCIIHex = new ASCIIHexCodec ();
    new PropertyChecker ().forAll (Generators.byteArrays (),
                                   Generators.ints (0, 100),
                                   Generators.of (" ", "\n", "\r\n", "\t"),
                                   (aBytes, aPos, sWhitespace) -> {
                                     final String sHex = new Base16Codec ().getEncodedAsString (aBytes,
                                                                                                StandardCharsets.ISO_8859_1);
                                     final int nPos = aPos.intValue () % (sHex.length () + 1);
                                     final String sEncoded = sHex.substring (0, nPos) + sWhitespace + sHex.substring (nPos) + ">";
                                     assertArrayEquals (aBytes,
                                                        aASCIIHex.getDecoded (sEncoded, StandardCharsets.ISO_8859_1));
                                   });
  }

  /**
   * The decode-only ASCII85 codec must decode the output of a reference encoder.
   */
  @Test
  public void testASCII85DecodesReferenceEncoding ()
  {
    final ASCII85Codec aASCII85 = new ASCII85Codec ();
    new PropertyChecker ().forAll (Generators.byteArrays (1, Generators.DEFAULT_MAX_SIZE),
                                   aBytes -> assertArrayEquals (aBytes, aASCII85.getDecoded (ASCII85Reference.encode (aBytes))));
  }

  private static IGenerator <IByteArrayCodec> _allCodecs ()
  {
    return Generators.of (new Base16Codec (),
                          new Base32Codec (),
                          new RFC3986Codec (),
                          new QuotedPrintableCodec (),
                          new RunLengthCodec (),
                          new LZWCodec (),
                          new FlateCodec (),
                          new GZIPCodec (),
                          new Base64Codec ());
  }

  /**
   * Encoding a slice of a buffer (offset and length) must give the same result as encoding a copy of
   * that slice. Offset and length are derived from generated numbers with modulo, so that they are
   * always valid and still shrink towards 0.
   */
  @Test
  public void testEncodeSliceMatchesCopy ()
  {
    new PropertyChecker ().forAll (_allCodecs (),
                                   Generators.byteArrays (),
                                   Generators.ints (0, Integer.MAX_VALUE),
                                   Generators.ints (0, Integer.MAX_VALUE),
                                   (aCodec, aBytes, aOfs, aLen) -> {
                                     final int nOfs = aOfs.intValue () % (aBytes.length + 1);
                                     final int nLen = aLen.intValue () % (aBytes.length - nOfs + 1);
                                     assertArrayEquals (aCodec.getEncoded (Arrays.copyOfRange (aBytes, nOfs, nOfs + nLen)),
                                                        aCodec.getEncoded (aBytes, nOfs, nLen));
                                   });
  }

  /**
   * Decoding an encoded value that is embedded in other bytes, via offset and length, must give the
   * original value.
   */
  @Test
  public void testDecodeSliceRoundTrip ()
  {
    new PropertyChecker ().forAll (_allCodecs (),
                                   Generators.byteArrays (),
                                   Generators.byteArrays (0, 10),
                                   (aCodec, aBytes, aPadding) -> {
                                     final byte [] aEncoded = aCodec.getEncoded (aBytes);
                                     final byte [] aEmbedded = new byte [aPadding.length + aEncoded.length + aPadding.length];
                                     System.arraycopy (aPadding, 0, aEmbedded, 0, aPadding.length);
                                     System.arraycopy (aEncoded, 0, aEmbedded, aPadding.length, aEncoded.length);
                                     System.arraycopy (aPadding, 0, aEmbedded, aPadding.length + aEncoded.length, aPadding.length);
                                     assertArrayEquals (aBytes, aCodec.getDecoded (aEmbedded, aPadding.length, aEncoded.length));
                                   });
  }

  /**
   * Codecs that calculate their maximum encoded and decoded length must never produce more.
   */
  @Test
  public void testMaximumLengths ()
  {
    final IGenerator <IByteArrayCodec> aCodecs = Generators.of (new Base16Codec (),
                                                                new Base32Codec (),
                                                                new Base32Codec ().setAddPadding (false),
                                                                new RunLengthCodec (),
                                                                new Base64Codec ());
    new PropertyChecker ().forAll (aCodecs, Generators.byteArrays (), (aCodec, aBytes) -> {
      final byte [] aEncoded = aCodec.getEncoded (aBytes);
      assertTrue ("encoded " + aEncoded.length + " > maximum " + aCodec.getMaximumEncodedLength (aBytes.length),
                  aEncoded.length <= aCodec.getMaximumEncodedLength (aBytes.length));
      assertTrue ("decoded " + aBytes.length + " > maximum " + aCodec.getMaximumDecodedLength (aEncoded.length),
                  aBytes.length <= aCodec.getMaximumDecodedLength (aEncoded.length));
    });
  }
}
