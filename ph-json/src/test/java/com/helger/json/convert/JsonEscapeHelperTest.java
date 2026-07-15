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
package com.helger.json.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.junit.Test;

import com.helger.base.string.StringHex;
import com.helger.json.serialize.JsonReader;

/**
 * Test class for class {@link JsonEscapeHelper}.
 *
 * @author Philip Helger
 */
public final class JsonEscapeHelperTest
{
  private static final String STRING_UNESCAPED = "this is a \"test\" containing \b \t \n \f \r \\ / <>\n##+{}()";
  private static final String STRING_ESCAPED = "this is a \\\"test\\\" containing \\b \\t \\n \\f \\r \\\\ / <>\\n##+{}()";

  @Test
  public void testEscape ()
  {
    assertEquals (STRING_ESCAPED, JsonEscapeHelper.jsonEscape (STRING_UNESCAPED));
    assertEquals ("", JsonEscapeHelper.jsonEscape (""));
  }

  @Test
  public void testUnscape ()
  {
    assertEquals (STRING_UNESCAPED, JsonEscapeHelper.jsonUnescape (STRING_ESCAPED));
    assertEquals ("", JsonEscapeHelper.jsonUnescape (""));
  }

  @Test
  public void testAllChars ()
  {
    final char [] aChars = new char [Character.MAX_VALUE];
    for (int i = 0; i < Character.MAX_VALUE; ++i)
      aChars[i] = (char) i;
    final String sEscaped = JsonEscapeHelper.jsonEscape (aChars);
    final String sUnescaped = JsonEscapeHelper.jsonUnescape (sEscaped);
    assertEquals (new String (aChars), sUnescaped);
  }

  @Test
  public void testEscapeAllControlChars ()
  {
    // Per RFC 8259 all control characters U+0000 - U+001F must be escaped,
    // regardless of whether they have a short named escape sequence.
    for (int i = 0; i <= 0x1f; ++i)
    {
      final String sSource = "a" + (char) i + "b";
      final String sEscaped = JsonEscapeHelper.jsonEscape (sSource);

      // The escaped output must not contain the raw control character
      assertEquals ("Control char 0x" + Integer.toHexString (i) + " was not escaped",
                    -1,
                    sEscaped.indexOf ((char) i));

      // The escaped output must be parseable as a JSON String by a strict-ish
      // reader and round-trip to the original value
      final String sRead = JsonReader.builder ().source ("\"" + sEscaped + "\"").readAsValue ().getAsString ();
      assertEquals (sSource, sRead);

      // jsonUnescape must reverse the escaping
      assertEquals (sSource, JsonEscapeHelper.jsonUnescape (sEscaped));
    }

    // Spot check a few specific representations
    assertEquals ("\\u0000", JsonEscapeHelper.jsonEscape (Character.toString ((char) 0x00)));
    assertEquals ("\\u0001", JsonEscapeHelper.jsonEscape (Character.toString ((char) 0x01)));
    assertEquals ("\\u001f", JsonEscapeHelper.jsonEscape (Character.toString ((char) 0x1f)));
    // Named escapes are still preferred over the generic \\u00XX form
    assertEquals ("\\b", JsonEscapeHelper.jsonEscape ("\b"));
    assertEquals ("\\t", JsonEscapeHelper.jsonEscape ("\t"));
    assertEquals ("\\n", JsonEscapeHelper.jsonEscape ("\n"));
    assertEquals ("\\f", JsonEscapeHelper.jsonEscape ("\f"));
    assertEquals ("\\r", JsonEscapeHelper.jsonEscape ("\r"));
    // 0x7f (DEL) is not a C0 control char and stays as-is
    assertEquals ("", JsonEscapeHelper.jsonEscape (""));
  }

  @Test
  public void testUnescapeMalformed ()
  {
    // A string ending with a lone escape char must throw a documented
    // IllegalArgumentException - not an ArrayIndexOutOfBoundsException
    assertThrows (IllegalArgumentException.class, () -> JsonEscapeHelper.jsonUnescape ("abc\\"));

    // An incomplete \\uXXXX sequence must throw IllegalArgumentException - not a
    // StringIndexOutOfBoundsException from constructing the error message
    assertThrows (IllegalArgumentException.class, () -> JsonEscapeHelper.jsonUnescape ("\\u"));
    assertThrows (IllegalArgumentException.class, () -> JsonEscapeHelper.jsonUnescape ("\\u1"));
    assertThrows (IllegalArgumentException.class, () -> JsonEscapeHelper.jsonUnescape ("\\u12"));
    assertThrows (IllegalArgumentException.class, () -> JsonEscapeHelper.jsonUnescape ("\\u123"));
    assertThrows (IllegalArgumentException.class, () -> JsonEscapeHelper.jsonUnescape ("ab\\u12"));

    // An invalid hex digit in a \\uXXXX sequence must throw
    assertThrows (IllegalArgumentException.class, () -> JsonEscapeHelper.jsonUnescape ("\\u123x"));

    // An unknown escape sequence must throw
    assertThrows (IllegalArgumentException.class, () -> JsonEscapeHelper.jsonUnescape ("\\x"));
  }

  @Test
  public void testArbitrary ()
  {
    final Random aRandom = new Random ();
    for (int i = 0; i < 100; ++i)
    {
      // Build a random test string
      final int nStringLength = 123;
      final StringBuilder aTestString = new StringBuilder (nStringLength);
      for (int j = 0; j < nStringLength; ++j)
        aTestString.append ((char) aRandom.nextInt (Character.MAX_VALUE));
      final String sTestString = aTestString.toString ();

      // Escape
      final String sEscaped = JsonEscapeHelper.jsonEscape (sTestString);
      // Try to parse escaped string
      assertNotNull (StringHex.getHexEncoded (sEscaped, StandardCharsets.UTF_8),
                     JsonReader.builder ().source ("\"" + sEscaped + "\"").read ());
      // Unescape
      final String sUnescaped = JsonEscapeHelper.jsonUnescape (sEscaped);
      // Must be identical to source string
      assertEquals (StringHex.getHexEncoded (sTestString, StandardCharsets.UTF_8) +
                    "\nvs.\n" +
                    StringHex.getHexEncoded (sEscaped, StandardCharsets.UTF_8),
                    sTestString,
                    sUnescaped);
    }
  }
}
