/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.codec;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Test;

/**
 * Test class for class {@link Base32Codec}
 *
 * @author Philip Helger
 */
public final class Base32CodecTest
{
  private static final Charset CHARSET = StandardCharsets.UTF_8;

  @Test
  public void testGetEncodedLength ()
  {
    final Base32Codec aBase32 = new Base32Codec ();
    assertEquals (0, aBase32.getEncodedLength (0));
    for (int i = 1; i <= 5; ++i)
      assertEquals (8, aBase32.getEncodedLength (i));
    for (int i = 6; i <= 10; ++i)
      assertEquals (16, aBase32.getEncodedLength (i));
    for (int i = 11; i <= 15; ++i)
      assertEquals (24, aBase32.getEncodedLength (i));

    for (int i = 0; i < 256; ++i)
    {
      final byte [] aBuf = new byte [i];
      Arrays.fill (aBuf, (byte) i);
      assertEquals (aBase32.getEncoded (aBuf).length, aBase32.getEncodedLength (i));
    }
  }

  @Test
  public void testEncode ()
  {
    final Base32Codec aBase32 = new Base32Codec ();
    assertNull (aBase32.getEncodedAsString ((byte []) null, CHARSET));
    assertNull (aBase32.getEncodedAsString ((String) null, CHARSET));
    assertEquals ("", aBase32.getEncodedAsString (new byte [0], CHARSET));
    assertEquals ("AA======", aBase32.getEncodedAsString (new byte [] { 0 }, CHARSET));
    assertEquals ("74======", aBase32.getEncodedAsString (new byte [] { (byte) 0xff }, CHARSET));
    assertEquals ("IE======", aBase32.getEncodedAsString ("A", CHARSET));
    assertEquals ("MY======", aBase32.getEncodedAsString ("f", CHARSET));
    assertEquals ("IFBA====", aBase32.getEncodedAsString ("AB", CHARSET));
    assertEquals ("MZXW6===", aBase32.getEncodedAsString ("foo", CHARSET));
    assertEquals ("MZXW6YQ=", aBase32.getEncodedAsString ("foob", CHARSET));
    assertEquals ("ORSXG5A=", aBase32.getEncodedAsString ("test", CHARSET));
    assertEquals ("IFBEGRCF", aBase32.getEncodedAsString ("ABCDE", CHARSET));
    assertEquals ("MZXW6YTB", aBase32.getEncodedAsString ("fooba", CHARSET));
    assertEquals ("MZXW6YTBOI======", aBase32.getEncodedAsString ("foobar", CHARSET));
    assertEquals ("AAAAAAAA", aBase32.getEncodedAsString (new byte [] { 0, 0, 0, 0, 0 }, CHARSET));

    aBase32.setAddPaddding (false);
    assertEquals ("AA", aBase32.getEncodedAsString (new byte [] { 0 }, CHARSET));
    assertEquals ("74", aBase32.getEncodedAsString (new byte [] { (byte) 0xff }, CHARSET));
    assertEquals ("IE", aBase32.getEncodedAsString ("A", CHARSET));
    assertEquals ("MY", aBase32.getEncodedAsString ("f", CHARSET));
    assertEquals ("IFBA", aBase32.getEncodedAsString ("AB", CHARSET));
    assertEquals ("MZXW6", aBase32.getEncodedAsString ("foo", CHARSET));
    assertEquals ("MZXW6YQ", aBase32.getEncodedAsString ("foob", CHARSET));
    assertEquals ("ORSXG5A", aBase32.getEncodedAsString ("test", CHARSET));
    assertEquals ("IFBEGRCF", aBase32.getEncodedAsString ("ABCDE", CHARSET));
    assertEquals ("MZXW6YTB", aBase32.getEncodedAsString ("fooba", CHARSET));
    assertEquals ("MZXW6YTBOI", aBase32.getEncodedAsString ("foobar", CHARSET));
    assertEquals ("AAAAAAAA", aBase32.getEncodedAsString (new byte [] { 0, 0, 0, 0, 0 }, CHARSET));
  }

  @Test
  public void testGetDecodedLength ()
  {
    final Base32Codec aBase32 = new Base32Codec ();
    assertEquals (0, aBase32.getDecodedLength (0));
    for (int i = 1; i <= 8; ++i)
      assertEquals (5, aBase32.getDecodedLength (i));
    for (int i = 9; i <= 16; ++i)
      assertEquals (10, aBase32.getDecodedLength (i));
    for (int i = 17; i <= 24; ++i)
      assertEquals (15, aBase32.getDecodedLength (i));
  }

  @Test
  public void testDecode ()
  {
    final Base32Codec aBase32 = new Base32Codec ();
    assertNull (aBase32.getDecodedAsString ((byte []) null, CHARSET));
    assertEquals ("", aBase32.getDecodedAsString (new byte [0], CHARSET));
    assertArrayEquals (new byte [] { 0 }, aBase32.getDecoded ("AA", CHARSET));
    assertArrayEquals (new byte [] { 0, 0, 0, 0, 0 }, aBase32.getDecoded ("AAAAAAAA", CHARSET));
    assertEquals ("A", aBase32.getDecodedAsString ("IE", CHARSET));
    assertEquals ("A", aBase32.getDecodedAsString ("IE======", CHARSET));
    assertEquals ("f", aBase32.getDecodedAsString ("MY", CHARSET));
    assertEquals ("f", aBase32.getDecodedAsString ("MY======", CHARSET));
    assertEquals ("AB", aBase32.getDecodedAsString ("IFBA", CHARSET));
    assertEquals ("AB", aBase32.getDecodedAsString ("IFBA====", CHARSET));
    assertEquals ("foo", aBase32.getDecodedAsString ("MZXW6", CHARSET));
    assertEquals ("foo", aBase32.getDecodedAsString ("MZXW6===", CHARSET));
    assertEquals ("foob", aBase32.getDecodedAsString ("MZXW6YQ", CHARSET));
    assertEquals ("foob", aBase32.getDecodedAsString ("MZXW6YQ==", CHARSET));
    assertEquals ("test", aBase32.getDecodedAsString ("ORSXG5A", CHARSET));
    assertEquals ("test", aBase32.getDecodedAsString ("ORSXG5A==", CHARSET));
    assertEquals ("ABCDE", aBase32.getDecodedAsString ("IFBEGRCF", CHARSET));
    assertEquals ("fooba", aBase32.getDecodedAsString ("MZXW6YTB", CHARSET));
    assertEquals ("foobar", aBase32.getDecodedAsString ("MZXW6YTBOI", CHARSET));
    assertEquals ("foobar", aBase32.getDecodedAsString ("MZXW6YTBOI======", CHARSET));
    assertEquals ("foobar", aBase32.getDecodedAsString ("MZXW6YTBOI==================", CHARSET));
  }
}
