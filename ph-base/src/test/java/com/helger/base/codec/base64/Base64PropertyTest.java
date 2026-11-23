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
package com.helger.base.codec.base64;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based tests for {@link Base64}, {@link Base64Codec} and the Base64 streams.
 * <p>
 * Instead of hand picked examples, each test describes a property that must hold for all inputs.
 * {@link PropertyChecker} generates the inputs and, on failure, shrinks them to the smallest
 * counterexample it can find.
 *
 * @author Philip Helger
 */
public final class Base64PropertyTest
{
  private static final String BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

  /**
   * Oracle property: the JDK encoder serves as the reference implementation. The JDK class is
   * written fully qualified, because its simple name collides with {@link Base64} of this package.
   */
  @Test
  public void testEncodingMatchesJdk ()
  {
    new PropertyChecker ().forAll (Generators.byteArrays (),
                                   aBytes -> assertEquals (java.util.Base64.getEncoder ().encodeToString (aBytes),
                                                           Base64.encodeBytes (aBytes)));
  }

  /**
   * Oracle property for the URL safe alphabet.
   */
  @Test
  public void testUrlSafeEncodingMatchesJdk ()
  {
    new PropertyChecker ().forAll (Generators.byteArrays (),
                                   aBytes -> assertEquals (java.util.Base64.getUrlEncoder ().encodeToString (aBytes),
                                                           Base64.encodeBytes (aBytes, Base64.URL_SAFE)));
  }

  /**
   * Round trip property: decoding an encoded value gives the original value.
   */
  @Test
  public void testRoundTrip ()
  {
    final Base64Codec aCodec = new Base64Codec ();
    new PropertyChecker ().forAll (Generators.byteArrays (),
                                   aBytes -> assertArrayEquals (aBytes, aCodec.getDecoded (aCodec.getEncoded (aBytes))));
  }

  /**
   * Invariant property: the predicted length must be exact for unwrapped Base64.
   */
  @Test
  public void testEncodedLengthMatchesPrediction ()
  {
    final Base64Codec aCodec = new Base64Codec ();
    new PropertyChecker ().forAll (Generators.byteArrays (),
                                   aBytes -> assertEquals (aCodec.getMaximumEncodedLength (aBytes.length),
                                                           aCodec.getEncoded (aBytes).length));
  }

  /**
   * Model based property with two generated arguments: encoding through the stream in chunks of any
   * size must give the same result as encoding the whole buffer at once. This catches bugs in the
   * internal buffering of {@link Base64OutputStream}.
   */
  @Test
  public void testStreamEncodingInChunksMatchesBlockEncoding ()
  {
    new PropertyChecker ().forAll (Generators.byteArrays (), Generators.ints (1, 17), (aBytes, aChunkSize) -> {
      final int nChunkSize = aChunkSize.intValue ();
      final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
      try (final Base64OutputStream aB64OS = new Base64OutputStream (aBAOS))
      {
        for (int nOfs = 0; nOfs < aBytes.length; nOfs += nChunkSize)
          aB64OS.write (aBytes, nOfs, Math.min (nChunkSize, aBytes.length - nOfs));
      }
      assertEquals (Base64.encodeBytes (aBytes), aBAOS.getAsString (StandardCharsets.ISO_8859_1));
    });
  }

  /**
   * Robustness property ("fuzzing"): the safe decoder must never throw, whatever the input. The
   * generator creates strings that look almost like Base64. Purely random strings are rejected by the
   * decoder very early, so this reaches the deeper decoding logic much more often.
   */
  @Test
  public void testSafeDecodeNeverThrows ()
  {
    final IGenerator <String> aAlmostBase64 = Generators.strings (BASE64_ALPHABET + "= \r\n\t", 0, 64);
    new PropertyChecker ().setTries (1000).forAll (aAlmostBase64, Base64::safeDecode);
  }

  /**
   * Random bytes read through the decoding input stream must either decode or fail with an
   * {@link IOException} - never with a runtime exception. This property found the bug that bytes
   * &ge; 0x80 caused an {@link ArrayIndexOutOfBoundsException}.
   */
  @Test
  public void testDecodingInputStreamOnlyThrowsIOException ()
  {
    new PropertyChecker ().forAll (Generators.byteArrays (), aBytes -> {
      try (final Base64InputStream aB64IS = new Base64InputStream (new NonBlockingByteArrayInputStream (aBytes)))
      {
        aB64IS.readAllBytes ();
      }
      catch (final IOException ex)
      {
        // expected for invalid input
      }
    });
  }

  /**
   * Same as {@link #testDecodingInputStreamOnlyThrowsIOException()} for the decoding output stream.
   */
  @Test
  public void testDecodingOutputStreamOnlyThrowsIOException ()
  {
    new PropertyChecker ().forAll (Generators.byteArrays (), aBytes -> {
      try (final Base64OutputStream aB64OS = new Base64OutputStream (new NonBlockingByteArrayOutputStream (),
                                                                     Base64.DECODE))
      {
        aB64OS.write (aBytes);
      }
      catch (final IOException ex)
      {
        // expected for invalid input
      }
    });
  }
  /**
   * Oracle property for decoding: the output of the JDK encoder must decode to the original bytes,
   * for the standard and the URL safe alphabet.
   */
  @Test
  public void testDecodingJdkEncoding ()
  {
    new PropertyChecker ().forAll (Generators.byteArrays (), aBytes -> {
      assertArrayEquals (aBytes,
                         Base64.decode (java.util.Base64.getEncoder ().encodeToString (aBytes), Base64.DONT_GUNZIP));
      assertArrayEquals (aBytes,
                         Base64.decode (java.util.Base64.getUrlEncoder ().encodeToString (aBytes),
                                        Base64.URL_SAFE | Base64.DONT_GUNZIP));
    });
  }

  /**
   * Oracle property for line breaks: with CRLF line breaks the output must equal the JDK MIME encoder
   * (76 characters per line, no line break after the last line).
   */
  @Test
  public void testLineBreaksMatchJdkMime ()
  {
    new PropertyChecker ().forAll (Generators.byteArrays (),
                                   aBytes -> assertEquals (java.util.Base64.getMimeEncoder ().encodeToString (aBytes),
                                                           Base64.encodeBytes (aBytes,
                                                                               Base64.DO_BREAK_LINES | Base64.DO_NEWLINE_CRLF)));
  }

  /**
   * Model based property: both streams must create the same line broken output as the static encoder
   * - the output stream for any chunk size, the input stream when read byte by byte.
   */
  @Test
  public void testStreamLineBreaksMatchBlockEncoding ()
  {
    new PropertyChecker ().forAll (Generators.byteArrays (), Generators.ints (1, 100), (aBytes, aChunkSize) -> {
      final String sExpected = Base64.encodeBytes (aBytes, Base64.DO_BREAK_LINES);

      final int nChunkSize = aChunkSize.intValue ();
      final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
      try (final Base64OutputStream aB64OS = new Base64OutputStream (aBAOS, Base64.ENCODE | Base64.DO_BREAK_LINES))
      {
        for (int nOfs = 0; nOfs < aBytes.length; nOfs += nChunkSize)
          aB64OS.write (aBytes, nOfs, Math.min (nChunkSize, aBytes.length - nOfs));
      }
      assertEquals (sExpected, aBAOS.getAsString (StandardCharsets.ISO_8859_1));

      try (final Base64InputStream aB64IS = new Base64InputStream (new NonBlockingByteArrayInputStream (aBytes),
                                                                   Base64.ENCODE | Base64.DO_BREAK_LINES))
      {
        assertEquals (sExpected, new String (aB64IS.readAllBytes (), StandardCharsets.ISO_8859_1));
      }
    });
  }

  /**
   * Line broken output must be decodable by the static decoder and by both streams.
   */
  @Test
  public void testLineBrokenRoundTrip ()
  {
    new PropertyChecker ().forAll (Generators.byteArrays (), Generators.booleans (), (aBytes, aCRLF) -> {
      final int nOptions = Base64.DO_BREAK_LINES | (aCRLF.booleanValue () ? Base64.DO_NEWLINE_CRLF : 0);
      final String sEncoded = Base64.encodeBytes (aBytes, nOptions);
      assertArrayEquals (aBytes, Base64.decode (sEncoded, Base64.DONT_GUNZIP));

      try (final Base64InputStream aB64IS = new Base64InputStream (new NonBlockingByteArrayInputStream (sEncoded.getBytes (StandardCharsets.ISO_8859_1))))
      {
        assertArrayEquals (aBytes, aB64IS.readAllBytes ());
      }

      final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
      try (final Base64OutputStream aB64OS = new Base64OutputStream (aBAOS, Base64.DECODE))
      {
        aB64OS.write (sEncoded.getBytes (StandardCharsets.ISO_8859_1));
      }
      assertArrayEquals (aBytes, aBAOS.toByteArray ());
    });
  }

  /**
   * Data encoded with the GZIP option is compressed before encoding and transparently decompressed
   * by {@link Base64#decode(String)}.
   */
  @Test
  public void testGZipOptionRoundTrip ()
  {
    new PropertyChecker ().forAll (Generators.byteArrays (),
                                   aBytes -> assertArrayEquals (aBytes,
                                                                Base64.decode (Base64.encodeBytes (aBytes, Base64.GZIP))));
  }

  /**
   * {@link Base64#decode(String)} without DONT_GUNZIP must return exactly the bytes that were
   * encoded. The generator mixes arbitrary bytes with arbitrary bytes that start with the GZIP magic
   * number 0x1f 0x8b.
   */
  @Test
  public void testDefaultDecodeRoundTrip ()
  {
    final IGenerator <byte []> aGZipMagicPrefixed = Generators.byteArrays ().map (aBytes -> {
      final byte [] ret = new byte [aBytes.length + 2];
      ret[0] = 0x1f;
      ret[1] = (byte) 0x8b;
      System.arraycopy (aBytes, 0, ret, 2, aBytes.length);
      return ret;
    });
    new PropertyChecker ().forAll (Generators.oneOf (Generators.byteArrays (), aGZipMagicPrefixed),
                                   aBytes -> assertArrayEquals (aBytes, Base64.decode (Base64.encodeBytes (aBytes))));
  }
}
