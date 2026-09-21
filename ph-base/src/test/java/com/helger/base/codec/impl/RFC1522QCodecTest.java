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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

import org.junit.Test;

import com.helger.base.codec.DecodeException;

/**
 * Test class for class {@link RFC1522QCodec} and {@link AbstractRFC1522Codec}.
 *
 * @author Philip Helger
 */
public final class RFC1522QCodecTest
{
  @Test
  public void testGetAllPrintableChars ()
  {
    final BitSet aBS = RFC1522QCodec.getAllPrintableChars ();
    assertNotNull (aBS);
    assertTrue (aBS.get ('a'));
    assertTrue (aBS.get ('~'));
    // It is a copy
    aBS.clear ('a');
    assertTrue (RFC1522QCodec.getAllPrintableChars ().get ('a'));
  }

  @Test
  public void testDefaults ()
  {
    final RFC1522QCodec a = new RFC1522QCodec ();
    assertSame (StandardCharsets.UTF_8, a.getCharset ());
    assertTrue (RFC1522QCodec.DEFAULT_ENCODE_BLANKS == a.isEncodeBlanks ());
    assertFalse (a.isEncodeBlanks ());

    a.setEncodeBlanks (true);
    assertTrue (a.isEncodeBlanks ());
  }

  @Test
  public void testRoundTrip ()
  {
    final RFC1522QCodec a = new RFC1522QCodec (StandardCharsets.ISO_8859_1);
    assertSame (StandardCharsets.ISO_8859_1, a.getCharset ());

    for (final String sText : new String [] { "", "abc", "Hello World", "äöü", "a=b?c" })
    {
      final String sEncoded = a.getEncoded (sText);
      assertNotNull (sEncoded);
      assertTrue (sEncoded.startsWith ("=?"));
      assertTrue (sEncoded.endsWith ("?="));
      assertEquals (sText, a.getDecoded (sEncoded));
    }
  }

  @Test
  public void testRoundTripWithBlanks ()
  {
    final RFC1522QCodec a = new RFC1522QCodec (StandardCharsets.ISO_8859_1);
    a.setEncodeBlanks (true);

    final String sText = "Hello World";
    final String sEncoded = a.getEncoded (sText);
    assertEquals (sText, a.getDecoded (sEncoded));
  }

  @Test
  public void testNullHandling ()
  {
    final RFC1522QCodec a = new RFC1522QCodec ();
    assertNull (a.getEncoded ((String) null));
    assertNull (a.getDecoded ((String) null));
  }

  @Test
  public void testDecodeMalformed ()
  {
    final RFC1522QCodec a = new RFC1522QCodec ();

    // Missing prefix
    try
    {
      a.getDecoded ("abc?=");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    // Missing postfix
    try
    {
      a.getDecoded ("=?abc");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    // Empty charset
    try
    {
      a.getDecoded ("=??Q?abc?=");
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }
    // Unknown charset
    try
    {
      a.getDecoded ("=?does-not-exist?Q?abc?=");
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }
    // Wrong encoding token
    try
    {
      a.getDecoded ("=?UTF-8?X?abc?=");
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }
  }
}
