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

import org.junit.Test;

import com.helger.base.codec.DecodeException;
import com.helger.base.codec.IByteArrayCodec;
import com.helger.base.codec.IByteArrayDecoder;
import com.helger.base.codec.base64.Base64Codec;
import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Robustness properties ("fuzzing") for all byte array decoders: invalid input must be rejected with
 * a {@link DecodeException} and never with another runtime exception.
 * <p>
 * Each decoder gets two kinds of input: purely random bytes, and valid encodings with 1 to 3 random
 * edits. The second kind is much more effective, because purely random input is usually rejected at
 * the first byte.
 *
 * @author Philip Helger
 */
public final class CodecRobustnessPropertyTest
{
  private static void _decodeExpectingOnlyDecodeException (final IByteArrayDecoder aDecoder, final byte [] aBytes)
  {
    try
    {
      aDecoder.getDecoded (aBytes);
    }
    catch (final DecodeException ex)
    {
      // expected for invalid input
    }
  }

  private static void _assertRobust (final IByteArrayDecoder aDecoder,
                                     final IGenerator <byte []> aValidEncodings,
                                     final int nMinLength)
  {
    new PropertyChecker ().forAll (Generators.byteArrays (nMinLength, Generators.DEFAULT_MAX_SIZE),
                                   aBytes -> _decodeExpectingOnlyDecodeException (aDecoder, aBytes));
    new PropertyChecker ().setTries (1000)
                          .forAll (Generators.mutatedByteArrays (aValidEncodings)
                                             .filter (aBytes -> aBytes.length >= nMinLength),
                                   aBytes -> _decodeExpectingOnlyDecodeException (aDecoder, aBytes));
  }

  private static void _assertRobust (final IByteArrayDecoder aDecoder, final IGenerator <byte []> aValidEncodings)
  {
    _assertRobust (aDecoder, aValidEncodings, 0);
  }

  private static void _assertRobust (final IByteArrayCodec aCodec)
  {
    _assertRobust (aCodec, Generators.byteArrays ().map (aCodec::getEncoded));
  }

  @Test
  public void testBase16 ()
  {
    _assertRobust (new Base16Codec ());
  }

  @Test
  public void testBase32 ()
  {
    _assertRobust (new Base32Codec ());
  }

  @Test
  public void testBase32Hex ()
  {
    _assertRobust (new Base32Codec (true));
  }

  @Test
  public void testBase32NoPadding ()
  {
    _assertRobust (new Base32Codec ().setAddPadding (false));
  }

  @Test
  public void testBase64 ()
  {
    _assertRobust (new Base64Codec ());
  }

  @Test
  public void testASCIIHex ()
  {
    _assertRobust (new ASCIIHexCodec (), Generators.byteArrays ().map (new Base16Codec ()::getEncoded));
  }

  @Test
  public void testASCII85 ()
  {
    // ASCII85Codec rejects input shorter than 4 bytes with an IllegalArgumentException by design - even
    // "~>", the valid encoding of empty input
    _assertRobust (new ASCII85Codec (), Generators.byteArrays ().map (ASCII85Reference::encode), 4);
  }

  @Test
  public void testFlate ()
  {
    _assertRobust (new FlateCodec ());
  }

  @Test
  public void testGZIP ()
  {
    _assertRobust (new GZIPCodec ());
  }

  @Test
  public void testLZW ()
  {
    _assertRobust (new LZWCodec ());
  }

  @Test
  public void testRunLength ()
  {
    _assertRobust (new RunLengthCodec ());
  }

  @Test
  public void testRFC3986 ()
  {
    _assertRobust (new RFC3986Codec ());
  }

  @Test
  public void testQuotedPrintable ()
  {
    _assertRobust (new QuotedPrintableCodec ());
  }
}
