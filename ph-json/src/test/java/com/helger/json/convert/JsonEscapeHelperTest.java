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
package com.helger.json.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.commons.string.StringHelper;
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
  public void testArbitrary ()
  {
    for (int i = 0; i < 100; ++i)
    {
      // Build a random test string
      final int nStringLength = 123;
      final StringBuilder aTestString = new StringBuilder (nStringLength);
      for (int j = 0; j < nStringLength; ++j)
        aTestString.append ((char) (Math.random () * Character.MAX_VALUE));
      final String sTestString = aTestString.toString ();

      // Escape
      final String sEscaped = JsonEscapeHelper.jsonEscape (sTestString);
      // Try to parse escaped string
      assertNotNull (StringHelper.getHexEncoded (sEscaped, StandardCharsets.UTF_8),
                     JsonReader.readFromString ("\"" + sEscaped + "\""));
      // Unescape
      final String sUnescaped = JsonEscapeHelper.jsonUnescape (sEscaped);
      // Must be identical to source string
      assertEquals (StringHelper.getHexEncoded (sTestString, StandardCharsets.UTF_8) +
                    "\nvs.\n" +
                    StringHelper.getHexEncoded (sEscaped, StandardCharsets.UTF_8),
                    sTestString,
                    sUnescaped);
    }
  }
}
