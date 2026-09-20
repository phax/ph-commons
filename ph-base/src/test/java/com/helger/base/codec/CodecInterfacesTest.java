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
package com.helger.base.codec;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.array.ArrayHelper;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.io.nonblocking.NonBlockingStringWriter;

/**
 * Test class for the default methods of the codec interfaces.
 *
 * @author Philip Helger
 */
public final class CodecInterfacesTest
{
  private static final byte [] BYTES = "abc".getBytes (StandardCharsets.ISO_8859_1);
  private static final char [] CHARS = "abc".toCharArray ();

  /** Reverses the provided range */
  private static final IByteArrayEncoder BYTE_ENCODER = (aBuf, nOfs, nLen) -> {
    if (aBuf == null)
      return null;
    final byte [] ret = new byte [nLen];
    for (int i = 0; i < nLen; ++i)
      ret[i] = aBuf[nOfs + nLen - 1 - i];
    return ret;
  };

  /** Reverses the provided range */
  private static final ICharArrayEncoder CHAR_ENCODER = (aBuf, nOfs, nLen) -> {
    if (aBuf == null)
      return null;
    final char [] ret = new char [nLen];
    for (int i = 0; i < nLen; ++i)
      ret[i] = aBuf[nOfs + nLen - 1 - i];
    return ret;
  };

  /** Reverses the provided range */
  private static final IByteArrayDecoder BYTE_DECODER = (aBuf, nOfs, nLen) -> BYTE_ENCODER.getEncoded (aBuf,
                                                                                                       nOfs,
                                                                                                       nLen);

  /** Reverses the provided range */
  private static final ICharArrayDecoder CHAR_DECODER = (aBuf, nOfs, nLen) -> CHAR_ENCODER.getEncoded (aBuf,
                                                                                                       nOfs,
                                                                                                       nLen);

  @Test
  public void testByteArrayEncoder ()
  {
    assertEquals (17, BYTE_ENCODER.getMaximumEncodedLength (17));

    assertArrayEquals ("cba".getBytes (StandardCharsets.ISO_8859_1), BYTE_ENCODER.getEncoded (BYTES));
    assertArrayEquals ("ba".getBytes (StandardCharsets.ISO_8859_1), BYTE_ENCODER.getEncoded (BYTES, 0, 2));
    assertArrayEquals ("cba".getBytes (StandardCharsets.ISO_8859_1),
                       BYTE_ENCODER.getEncoded ("abc", StandardCharsets.ISO_8859_1));

    assertNull (BYTE_ENCODER.getEncoded ((byte []) null));
    assertNull (BYTE_ENCODER.getEncoded ((String) null, StandardCharsets.ISO_8859_1));
  }

  @Test
  public void testByteArrayDecoder ()
  {
    assertEquals (17, BYTE_DECODER.getMaximumDecodedLength (17));

    assertArrayEquals ("cba".getBytes (StandardCharsets.ISO_8859_1), BYTE_DECODER.getDecoded (BYTES));
    assertArrayEquals ("cba".getBytes (StandardCharsets.ISO_8859_1),
                       BYTE_DECODER.getDecoded ("abc", StandardCharsets.ISO_8859_1));
    assertNull (BYTE_DECODER.getDecoded ((byte []) null));
    assertNull (BYTE_DECODER.getDecoded ((String) null, StandardCharsets.ISO_8859_1));
  }

  @Test
  public void testCharArrayEncoder ()
  {
    assertEquals (17, CHAR_ENCODER.getMaximumEncodedLength (17));

    assertArrayEquals ("cba".toCharArray (), CHAR_ENCODER.getEncoded (CHARS));
    assertArrayEquals ("ba".toCharArray (), CHAR_ENCODER.getEncoded (CHARS, 0, 2));
    assertArrayEquals ("cba".toCharArray (), CHAR_ENCODER.getEncoded ("abc"));

    assertNull (CHAR_ENCODER.getEncoded ((char []) null));
    assertNull (CHAR_ENCODER.getEncoded ((String) null));
  }

  @Test
  public void testCharArrayDecoder ()
  {
    assertEquals (17, CHAR_DECODER.getMaximumDecodedLength (17));

    assertArrayEquals ("cba".toCharArray (), CHAR_DECODER.getDecoded (CHARS));
    assertArrayEquals ("cba".toCharArray (), CHAR_DECODER.getDecoded ("abc"));
    assertNull (CHAR_DECODER.getDecoded ((char []) null));
    assertNull (CHAR_DECODER.getDecoded ((String) null));
  }

  @Test
  public void testIdentityCodec ()
  {
    final IdentityByteArrayCodec c = IdentityByteArrayCodec.INSTANCE;
    assertNotNull (c);
    assertEquals (17, c.getMaximumEncodedLength (17));
    assertEquals (17, c.getMaximumDecodedLength (17));

    assertArrayEquals (BYTES, c.getEncoded (BYTES));
    assertArrayEquals (BYTES, c.getDecoded (BYTES));
    assertArrayEquals (ArrayHelper.getCopy (BYTES, 1, 2), c.getEncoded (BYTES, 1, 2));
    assertArrayEquals (ArrayHelper.getCopy (BYTES, 1, 2), c.getDecoded (BYTES, 1, 2));

    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      c.encode (BYTES, 0, BYTES.length, aBAOS);
      assertArrayEquals (BYTES, aBAOS.toByteArray ());
    }
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      c.decode (BYTES, 0, BYTES.length, aBAOS);
      assertArrayEquals (BYTES, aBAOS.toByteArray ());
    }

    // The identity codec of any type
    final IdentityCodec <String> aAny = new IdentityCodec <> ();
    assertSame ("abc", aAny.getEncoded ("abc"));
    assertSame ("abc", aAny.getDecoded ("abc"));
    assertNull (aAny.getEncoded (null));
    assertNull (aAny.getDecoded (null));
  }

  @Test
  public void testByteArrayStreamCodec ()
  {
    // A codec that writes everything reversed
    final IByteArrayCodec c = new IByteArrayCodec ()
    {
      public void encode (final byte [] aDecodedBuffer, final int nOfs, final int nLen, final java.io.OutputStream aOS)
      {
        final byte [] aEncoded = BYTE_ENCODER.getEncoded (aDecodedBuffer, nOfs, nLen);
        if (aEncoded != null)
          try
          {
            aOS.write (aEncoded);
          }
          catch (final java.io.IOException ex)
          {
            throw new EncodeException (ex);
          }
      }

      public void decode (final byte [] aEncodedBuffer, final int nOfs, final int nLen, final java.io.OutputStream aOS)
      {
        encode (aEncodedBuffer, nOfs, nLen, aOS);
      }
    };

    assertEquals (17, c.getMaximumEncodedLength (17));
    assertEquals (17, c.getMaximumDecodedLength (17));

    assertArrayEquals ("cba".getBytes (StandardCharsets.ISO_8859_1), c.getEncoded (BYTES));
    assertArrayEquals ("cba".getBytes (StandardCharsets.ISO_8859_1), c.getDecoded (BYTES));
    assertArrayEquals ("cba".getBytes (StandardCharsets.ISO_8859_1),
                       c.getEncoded ("abc", StandardCharsets.ISO_8859_1));
    assertArrayEquals ("cba".getBytes (StandardCharsets.ISO_8859_1),
                       c.getDecoded ("abc", StandardCharsets.ISO_8859_1));

    assertEquals ("cba", c.getEncodedAsString (BYTES, StandardCharsets.ISO_8859_1));
    assertEquals ("cba", c.getEncodedAsString (BYTES, 0, BYTES.length, StandardCharsets.ISO_8859_1));
    assertEquals ("cba", c.getDecodedAsString (BYTES, StandardCharsets.ISO_8859_1));
    assertEquals ("cba", c.getDecodedAsString (BYTES, 0, BYTES.length, StandardCharsets.ISO_8859_1));
    assertEquals ("cba", c.getEncodedAsString ("abc", StandardCharsets.ISO_8859_1));
    assertEquals ("cba", c.getDecodedAsString ("abc", StandardCharsets.ISO_8859_1));

    assertNull (c.getEncoded ((byte []) null));
    assertNull (c.getDecoded ((byte []) null));
    assertNull (c.getEncoded ((String) null, StandardCharsets.ISO_8859_1));
    assertNull (c.getDecoded ((String) null, StandardCharsets.ISO_8859_1));
    assertNull (c.getEncodedAsString ((byte []) null, StandardCharsets.ISO_8859_1));
    assertNull (c.getDecodedAsString ((byte []) null, StandardCharsets.ISO_8859_1));

    // Explicitly write to a stream
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      c.encode (BYTES, aBAOS);
      c.decode (BYTES, aBAOS);
      assertEquals ("cbacba", aBAOS.getAsString (StandardCharsets.ISO_8859_1));
    }
  }

  @Test
  public void testCharArrayStreamCodec ()
  {
    // A codec that writes everything reversed
    final ICharArrayCodec c = new ICharArrayCodec ()
    {
      public void encode (final char [] aDecodedBuffer, final int nOfs, final int nLen, final java.io.Writer aWriter)
      {
        final char [] aEncoded = CHAR_ENCODER.getEncoded (aDecodedBuffer, nOfs, nLen);
        if (aEncoded != null)
          try
          {
            aWriter.write (aEncoded);
          }
          catch (final java.io.IOException ex)
          {
            throw new EncodeException (ex);
          }
      }

      public void decode (final char [] aEncodedBuffer, final int nOfs, final int nLen, final java.io.Writer aWriter)
      {
        encode (aEncodedBuffer, nOfs, nLen, aWriter);
      }
    };

    assertEquals (17, c.getMaximumEncodedLength (17));
    assertEquals (17, c.getMaximumDecodedLength (17));

    assertArrayEquals ("cba".toCharArray (), c.getEncoded (CHARS));
    assertArrayEquals ("cba".toCharArray (), c.getDecoded (CHARS));
    assertArrayEquals ("cba".toCharArray (), c.getEncoded ("abc"));
    assertArrayEquals ("cba".toCharArray (), c.getDecoded ("abc"));

    assertEquals ("cba", c.getEncodedAsString (CHARS));
    assertEquals ("cba", c.getEncodedAsString (CHARS, 0, CHARS.length));
    assertEquals ("cba", c.getDecodedAsString (CHARS));
    assertEquals ("cba", c.getDecodedAsString (CHARS, 0, CHARS.length));
    assertEquals ("cba", c.getEncodedAsString ("abc"));
    assertEquals ("cba", c.getDecodedAsString ("abc"));

    assertNull (c.getEncoded ((char []) null));
    assertNull (c.getDecoded ((char []) null));
    assertNull (c.getEncoded ((String) null));
    assertNull (c.getDecoded ((String) null));
    assertNull (c.getEncodedAsString ((char []) null));
    assertNull (c.getDecodedAsString ((char []) null));

    // Explicitly write to a Writer
    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ())
    {
      c.encode (CHARS, aSW);
      c.decode (CHARS, aSW);
      assertEquals ("cbacba", aSW.getAsString ());
    }
  }

  @Test
  public void testExceptions ()
  {
    final Exception aCause = new IllegalArgumentException ("cause");

    assertEquals ("msg", new EncodeException ("msg").getMessage ());
    assertSame (aCause, new EncodeException (aCause).getCause ());
    assertEquals ("msg", new EncodeException ("msg", aCause).getMessage ());
    assertSame (aCause, new EncodeException ("msg", aCause).getCause ());

    assertEquals ("msg", new DecodeException ("msg").getMessage ());
    assertSame (aCause, new DecodeException (aCause).getCause ());
    assertEquals ("msg", new DecodeException ("msg", aCause).getMessage ());
    assertSame (aCause, new DecodeException ("msg", aCause).getCause ());
  }
}
