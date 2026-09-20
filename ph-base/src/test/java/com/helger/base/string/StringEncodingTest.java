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
package com.helger.base.string;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

/**
 * Test class for class {@link StringEncoding}.
 *
 * @author Philip Helger
 */
public final class StringEncodingTest
{
  private static final String TEXT = "Hello World";

  @Test
  public void testEncodeCharToBytes ()
  {
    final char [] aChars = TEXT.toCharArray ();

    final byte [] aBytes = StringEncoding.encodeCharToBytes (aChars, StandardCharsets.ISO_8859_1);
    assertEquals (TEXT, new String (aBytes, StandardCharsets.ISO_8859_1));

    final byte [] aPart = StringEncoding.encodeCharToBytes (aChars, 0, 5, StandardCharsets.ISO_8859_1);
    assertEquals ("Hello", new String (aPart, StandardCharsets.ISO_8859_1));

    // An empty range gives an empty array
    assertEquals (0, StringEncoding.encodeCharToBytes (aChars, 0, 0, StandardCharsets.ISO_8859_1).length);

    // UTF-8 encoding of a multi byte character
    assertNotNull (StringEncoding.encodeCharToBytes ("äöü".toCharArray (), StandardCharsets.UTF_8));
  }

  @Test
  public void testDecodeBytesToChars ()
  {
    final byte [] aBytes = TEXT.getBytes (StandardCharsets.ISO_8859_1);

    assertArrayEquals (TEXT.toCharArray (),
                       StringEncoding.decodeBytesToChars (aBytes, StandardCharsets.ISO_8859_1));
    assertArrayEquals ("Hello".toCharArray (),
                       StringEncoding.decodeBytesToChars (aBytes, 0, 5, StandardCharsets.ISO_8859_1));
    assertEquals (0, StringEncoding.decodeBytesToChars (aBytes, 0, 0, StandardCharsets.ISO_8859_1).length);

    assertNotNull (StringEncoding.decodeBytesToChars ("äöü".getBytes (StandardCharsets.UTF_8),
                                                       StandardCharsets.UTF_8));
  }

  @Test
  public void testRoundTrip ()
  {
    for (final String sText : new String [] { "", "abc", TEXT, "äöü" })
    {
      final byte [] aBytes = StringEncoding.encodeCharToBytes (sText.toCharArray (), StandardCharsets.UTF_8);
      final char [] aChars = StringEncoding.decodeBytesToChars (aBytes, StandardCharsets.UTF_8);
      assertEquals (sText, new String (aChars).trim ());
    }
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      StringEncoding.encodeCharToBytes (TEXT.toCharArray (), -1, 5, StandardCharsets.ISO_8859_1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      StringEncoding.decodeBytesToChars (TEXT.getBytes (StandardCharsets.ISO_8859_1),
                                          0,
                                          1000,
                                          StandardCharsets.ISO_8859_1);
      fail ();
    }
    catch (final IllegalArgumentException | IndexOutOfBoundsException ex)
    {
      // expected
    }
  }
}
