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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.base.codec.DecodeException;
import com.helger.base.codec.IByteArrayCodec;

/**
 * Test class for the error paths and the special cases of the byte array
 * codecs.
 *
 * @author Philip Helger
 */
public final class CodecErrorPathsTest
{
  private static final byte [] BYTES = "Hello world".getBytes (StandardCharsets.ISO_8859_1);

  private static void _testNullAndEmpty (@NonNull final IByteArrayCodec c)
  {
    final String sName = c.getClass ().getName ();

    // null in - null out
    assertNull (sName, c.getEncoded ((byte []) null));
    assertNull (sName, c.getDecoded ((byte []) null));
    assertNull (sName, c.getEncoded ((String) null, StandardCharsets.ISO_8859_1));
    assertNull (sName, c.getDecoded ((String) null, StandardCharsets.ISO_8859_1));

    // Empty in - empty out
    assertNotNull (sName, c.getEncoded (new byte [0]));
    assertArrayEquals (sName, new byte [0], c.getDecoded (c.getEncoded (new byte [0])));

    // Round trip
    assertArrayEquals (sName, BYTES, c.getDecoded (c.getEncoded (BYTES)));
  }

  @Test
  public void testNullAndEmpty ()
  {
    _testNullAndEmpty (new Base16Codec ());
    _testNullAndEmpty (new Base32Codec ());
    _testNullAndEmpty (new FlateCodec ());
    _testNullAndEmpty (new GZIPCodec ());
    _testNullAndEmpty (new LZWCodec ());
    _testNullAndEmpty (new QuotedPrintableCodec ());
    _testNullAndEmpty (new RFC3986Codec ());
    _testNullAndEmpty (new com.helger.base.codec.base64.Base64Codec ());
  }

  @Test
  public void testBase16Invalid ()
  {
    final Base16Codec c = new Base16Codec ();
    assertEquals ("48656c6c6f", c.getEncodedAsString ("Hello".getBytes (StandardCharsets.ISO_8859_1),
                                                      StandardCharsets.ISO_8859_1));

    // An odd number of characters
    try
    {
      c.getDecoded ("486".getBytes (StandardCharsets.ISO_8859_1));
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }

    // Not a hex character
    try
    {
      c.getDecoded ("4X".getBytes (StandardCharsets.ISO_8859_1));
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }
  }

  @Test
  public void testBase32 ()
  {
    final Base32Codec c = new Base32Codec ();
    assertEquals ('=', c.getPad ());
    assertEquals (true, c.isAddPadding ());
    assertEquals (false, c.isHexEncoding ());

    // Without padding
    c.setAddPaddding (false);
    final byte [] aEncoded = c.getEncoded (BYTES);
    assertNotNull (aEncoded);
    assertArrayEquals (BYTES, c.getDecoded (aEncoded));

    // Another pad character
    final Base32Codec c2 = new Base32Codec ();
    c2.setPad ((byte) '-');
    assertEquals ('-', c2.getPad ());
    assertArrayEquals (BYTES, c2.getDecoded (c2.getEncoded (BYTES)));

    // A pad character from the alphabet is not allowed
    try
    {
      new Base32Codec ().setPad ((byte) 'A');
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }

    // Hex encoding
    final Base32Codec cHex = new Base32Codec (true);
    assertEquals (true, cHex.isHexEncoding ());
    assertArrayEquals (BYTES, cHex.getDecoded (cHex.getEncoded (BYTES)));
  }

  @Test
  public void testASCIIHex ()
  {
    final ASCIIHexCodec c = new ASCIIHexCodec ();
    assertArrayEquals ("Hello".getBytes (StandardCharsets.ISO_8859_1),
                       c.getDecoded ("48656C6C6F>".getBytes (StandardCharsets.ISO_8859_1)));
    // Whitespace is ignored
    assertArrayEquals ("Hello".getBytes (StandardCharsets.ISO_8859_1),
                       c.getDecoded ("48 65 6C 6C 6F>".getBytes (StandardCharsets.ISO_8859_1)));
    assertNull (c.getDecoded ((byte []) null));

    // Not a hex character
    try
    {
      c.getDecoded ("4X>".getBytes (StandardCharsets.ISO_8859_1));
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }
  }

  @Test
  public void testRunLength ()
  {
    final RunLengthCodec c = new RunLengthCodec ();
    assertNull (c.getDecoded ((byte []) null));

    // 2 literal bytes ("ab"), then 3 times "c" (257-254) and the EOD marker
    final byte [] aEncoded = new byte [] { 2, 'a', 'b', (byte) 254, 'c', (byte) 0x80 };
    assertArrayEquals ("abccc".getBytes (StandardCharsets.ISO_8859_1), c.getDecoded (aEncoded));

    // Premature end in a literal run
    try
    {
      c.getDecoded (new byte [] { 5, 'a' });
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }

    // Premature end in a repeated run
    try
    {
      c.getDecoded (new byte [] { (byte) 254 });
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }
  }

  @Test
  public void testFlateInvalid ()
  {
    assertEquals (false, FlateCodec.isZlibHead (BYTES, 0, BYTES.length));
    final byte [] aEncoded = new FlateCodec ().getEncoded (BYTES);
    assertEquals (true, FlateCodec.isZlibHead (aEncoded, 0, aEncoded.length));

    // Not deflated at all
    try
    {
      new FlateCodec ().getDecoded (BYTES);
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }
  }

  @Test
  public void testGZIPInvalid ()
  {
    // Not gzipped at all
    try
    {
      new GZIPCodec ().getDecoded (BYTES);
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }
  }

  @Test
  public void testQuotedPrintable ()
  {
    final QuotedPrintableCodec c = new QuotedPrintableCodec ();
    assertNotNull (c.getPrintableChars ());
    assertNotNull (QuotedPrintableCodec.getDefaultPrintableChars ());

    // A blank is printable and therefore not encoded
    assertEquals ("Hello W=F6rld",
                  c.getEncodedAsString ("Hello Wörld".getBytes (StandardCharsets.ISO_8859_1),
                                        StandardCharsets.ISO_8859_1));
    assertEquals ("Hello Wörld",
                  c.getDecodedAsString ("Hello=20W=F6rld".getBytes (StandardCharsets.ISO_8859_1),
                                        StandardCharsets.ISO_8859_1));

    // Premature end after the escape character
    try
    {
      c.getDecoded ("abc=".getBytes (StandardCharsets.ISO_8859_1));
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }

    // Invalid hex characters after the escape character
    try
    {
      c.getDecoded ("abc=XY".getBytes (StandardCharsets.ISO_8859_1));
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }

    // With an explicit set of printable characters
    final QuotedPrintableCodec c2 = new QuotedPrintableCodec (QuotedPrintableCodec.getDefaultPrintableChars ());
    assertArrayEquals (BYTES, c2.getDecoded (c2.getEncoded (BYTES)));
  }

  @Test
  public void testRFC3986 ()
  {
    final RFC3986Codec c = new RFC3986Codec ();
    assertNotNull (c.getPrintableChars ());
    assertNotNull (RFC3986Codec.getDefaultPrintableChars ());

    // A blank is encoded as "+"
    assertEquals ("Hello+World",
                  c.getEncodedAsString ("Hello World".getBytes (StandardCharsets.ISO_8859_1),
                                        StandardCharsets.ISO_8859_1));
    assertEquals ("Hello World",
                  c.getDecodedAsString ("Hello%20World".getBytes (StandardCharsets.ISO_8859_1),
                                        StandardCharsets.ISO_8859_1));

    // Premature end after the escape character
    try
    {
      c.getDecoded ("abc%".getBytes (StandardCharsets.ISO_8859_1));
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }

    // Invalid hex characters after the escape character
    try
    {
      c.getDecoded ("abc%XY".getBytes (StandardCharsets.ISO_8859_1));
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }

    // With an explicit set of printable characters
    final RFC3986Codec c2 = new RFC3986Codec (RFC3986Codec.getDefaultPrintableChars ());
    assertArrayEquals (BYTES, c2.getDecoded (c2.getEncoded (BYTES)));
  }

  @Test
  public void testRFC5987 ()
  {
    assertEquals ("Hello%20W%c3%b6rld", RFC5987Codec.getRFC5987EncodedUTF8 ("Hello Wörld"));
    assertEquals ("Hello%20W%f6rld", RFC5987Codec.getRFC5987Encoded ("Hello Wörld", StandardCharsets.ISO_8859_1));

    final RFC5987Codec c = new RFC5987Codec ();
    assertEquals ("Hello%20W%c3%b6rld", c.getEncoded ("Hello Wörld"));
    assertNull (c.getEncoded (null));

    assertEquals ("Hello%20W%f6rld", new RFC5987Codec (StandardCharsets.ISO_8859_1).getEncoded ("Hello Wörld"));
  }

  @Test
  public void testRFC2616 ()
  {
    final RFC2616Codec c = new RFC2616Codec ();

    assertEquals ("\"Hello World\"", c.getEncodedAsString ("Hello World"));
    assertEquals ("Hello World", c.getDecodedAsString ("\"Hello World\""));
    assertEquals ("\"a\\\"b\"", c.getEncodedAsString ("a\"b"));
    assertEquals ("a\"b", c.getDecodedAsString ("\"a\\\"b\""));

    assertNull (c.getEncoded ((char []) null));
    assertNull (c.getDecoded ((char []) null));

    // Less than 2 characters
    try
    {
      c.getDecoded ("\"".toCharArray ());
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }

    // Not starting with a quote character
    try
    {
      c.getDecoded ("ab".toCharArray ());
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }

    assertEquals (true, RFC2616Codec.isToken ("abc"));
    assertEquals (false, RFC2616Codec.isToken ("a b"));
    assertEquals (false, RFC2616Codec.isToken ((String) null));
    assertEquals (false, RFC2616Codec.isToken ((char []) null));
    assertEquals (true, RFC2616Codec.isMaybeEncoded ("\"abc\""));
    assertEquals (false, RFC2616Codec.isMaybeEncoded ("abc"));
    assertEquals (false, RFC2616Codec.isMaybeEncoded ((String) null));
    assertEquals (false, RFC2616Codec.isMaybeEncoded ((char []) null));
  }
}
