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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.codec.DecodeException;

/**
 * Test class for class {@link RFC1522BCodec}.
 *
 * @author Philip Helger
 */
public final class RFC1522BCodecTest
{
  @Test
  public void testDefaults ()
  {
    assertSame (StandardCharsets.UTF_8, new RFC1522BCodec ().getCharset ());
    assertSame (StandardCharsets.ISO_8859_1, new RFC1522BCodec (StandardCharsets.ISO_8859_1).getCharset ());
  }

  @Test
  public void testEncodeAndDecode ()
  {
    final RFC1522BCodec a = new RFC1522BCodec ();

    final String sEncoded = a.getEncoded ("Hällö Wörld");
    assertTrue (sEncoded, sEncoded.startsWith ("=?UTF-8?B?"));
    assertTrue (sEncoded, sEncoded.endsWith ("?="));
    assertEquals ("Hällö Wörld", a.getDecoded (sEncoded));

    // Round trip with another charset
    final RFC1522BCodec aISO = new RFC1522BCodec (StandardCharsets.ISO_8859_1);
    final String sEncodedISO = aISO.getEncoded ("Hällö");
    assertTrue (sEncodedISO, sEncodedISO.startsWith ("=?ISO-8859-1?B?"));
    assertEquals ("Hällö", aISO.getDecoded (sEncodedISO));

    // An empty String
    assertEquals ("", a.getDecoded (a.getEncoded ("")));

    assertNull (a.getEncoded (null));
    assertNull (a.getDecoded (null));
  }

  @Test
  public void testDecodeInvalid ()
  {
    final RFC1522BCodec a = new RFC1522BCodec ();

    // Neither prefix nor postfix
    try
    {
      a.getDecoded ("Hello");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }

    // Charset token not found
    try
    {
      a.getDecoded ("=?UTF-8?=");
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }

    // Charset not specified
    try
    {
      a.getDecoded ("=??B?SGVsbG8=?=");
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }

    // Unknown charset
    try
    {
      a.getDecoded ("=?bla-foo-fasel?B?SGVsbG8=?=");
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }

    // Encoding token not found
    try
    {
      a.getDecoded ("=?UTF-8?B?=");
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }

    // Wrong encoding - "Q" is not handled by this codec
    try
    {
      a.getDecoded ("=?UTF-8?Q?Hello?=");
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }
  }
}
