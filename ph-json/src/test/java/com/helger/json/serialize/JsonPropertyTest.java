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
package com.helger.json.serialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingStringWriter;
import com.helger.json.IJson;
import com.helger.json.IJsonValue;
import com.helger.json.JsonArray;
import com.helger.json.JsonObject;
import com.helger.json.JsonValue;
import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based tests for {@link JsonWriter} and {@link JsonReader}: generated JSON trees must
 * survive writing and reading, independent of how the reader receives its input.
 *
 * @author Philip Helger
 */
public final class JsonPropertyTest
{
  /** Characters that need escaping in JSON, plus some that may be written as they are */
  private static final String SPECIAL_CHARS = "\"\\/\b\f\n\r\t\u0001\u001f\u007f\u2028\u2029{}[],: \u00e4a";

  private static final IGenerator <String> TEXT = Generators.oneOf (Generators.strings (SPECIAL_CHARS, 0, 20),
                                                                    Generators.unicodeStrings (0, 20));

  private static final IGenerator <IJson> SCALARS = Generators.oneOf (Generators.of (JsonValue.NULL,
                                                                                     JsonValue.TRUE,
                                                                                     JsonValue.FALSE),
                                                                      Generators.ints ().map (aValue -> JsonValue.create (aValue.intValue ())),
                                                                      TEXT.map (JsonValue::create));

  private static final IGenerator <IJson> JSON = _json (3);

  /**
   * A reader that returns the text in chunks of the generated sizes, to hit every buffer boundary of
   * the parser.
   */
  private static final class ChunkedReader extends Reader
  {
    private final String m_sText;
    private final List <Integer> m_aChunkSizes;
    private int m_nPos = 0;
    private int m_nChunk = 0;

    ChunkedReader (@NonNull final String sText, @NonNull final List <Integer> aChunkSizes)
    {
      m_sText = sText;
      m_aChunkSizes = aChunkSizes;
    }

    @Override
    public int read (final char [] aBuf, final int nOfs, final int nLen) throws IOException
    {
      if (m_nPos >= m_sText.length ())
        return -1;
      if (nLen == 0)
        return 0;
      final int nChunkSize = m_aChunkSizes.isEmpty () ? 1 : m_aChunkSizes.get (m_nChunk++ % m_aChunkSizes.size ())
                                                                          .intValue ();
      final int nCount = Math.min (Math.min (nChunkSize, nLen), m_sText.length () - m_nPos);
      m_sText.getChars (m_nPos, m_nPos + nCount, aBuf, nOfs);
      m_nPos += nCount;
      return nCount;
    }

    @Override
    public void close ()
    {}
  }

  /**
   * Generate scalars, arrays and objects up to the passed nesting depth.
   */
  @NonNull
  private static IGenerator <IJson> _json (final int nDepth)
  {
    if (nDepth == 0)
      return SCALARS;
    final IGenerator <IJson> aChild = _json (nDepth - 1);
    final IGenerator <IJson> aArray = Generators.lists (aChild, 0, 4).map (aList -> {
      final JsonArray ret = new JsonArray ();
      for (final IJson aJson : aList)
        ret.add (aJson);
      return ret;
    });
    final IGenerator <Object []> aEntry = TEXT.flatMap (sKey -> aChild.map (aValue -> new Object [] { sKey, aValue }));
    final IGenerator <IJson> aObject = Generators.lists (aEntry, 0, 4).map (aList -> {
      final JsonObject ret = new JsonObject ();
      for (final Object [] aPair : aList)
        ret.add ((String) aPair[0], (IJson) aPair[1]);
      return ret;
    });
    return Generators.oneOf (SCALARS, aArray, aObject);
  }

  private static String _write (final IJson aJson)
  {
    return new JsonWriter ().writeAsString (aJson);
  }

  /**
   * Writing, reading and writing again gives the same text - compact and indented.
   */
  @Test
  public void testWriteReadFixpoint ()
  {
    new PropertyChecker ().setTries (1000).forAll (JSON, aJson -> {
      final String sJson = _write (aJson);
      final IJson aRead = JsonReader.readFromString (sJson);
      assertNotNull (sJson, aRead);
      assertEquals (sJson, _write (aRead));

      final String sFormatted = new JsonWriter (JsonWriterSettings.DEFAULT_SETTINGS_FORMATTED).writeAsString (aJson);
      final IJson aReadFormatted = JsonReader.readFromString (sFormatted);
      assertNotNull (sFormatted, aReadFormatted);
      assertEquals (sJson, _write (aReadFormatted));
    });
  }

  /**
   * Every string value - including control characters, quotes, backslashes and line separators -
   * is read back exactly.
   */
  @Test
  public void testStringValuesReadBackExactly ()
  {
    new PropertyChecker ().setTries (1000).forAll (TEXT, sText -> {
      final String sJson = _write (new JsonArray ().add (JsonValue.create (sText)));
      final IJson aRead = JsonReader.readFromString (sJson);
      assertNotNull (sJson, aRead);
      final IJsonValue aValue = ((JsonArray) aRead).getValueAtIndex (0);
      assertEquals (sJson, sText, aValue.getValue ());
    });
  }

  /**
   * Reading from a reader that returns arbitrary chunks gives the same result as reading from a
   * String - with and without the end of input check, and with and without a buffered reader.
   */
  @Test
  public void testChunkedReading ()
  {
    new PropertyChecker ().forAll (JSON,
                                   Generators.lists (Generators.ints (1, 20), 0, 10),
                                   Generators.booleans (),
                                   Generators.booleans (),
                                   (aJson, aChunkSizes, aCheckForEOI, aUseBufferedReader) -> {
                                     final String sJson = _write (aJson);
                                     final IJson aRead = JsonReader.builder ()
                                                                   .source (new ChunkedReader (sJson, aChunkSizes))
                                                                   .checkForEOI (aCheckForEOI.booleanValue ())
                                                                   .useBufferedReader (aUseBufferedReader.booleanValue ())
                                                                   .read ();
                                     assertNotNull (sJson, aRead);
                                     assertEquals (sJson, _write (aRead));
                                   });
  }

  /**
   * The String, byte array and Writer output of the writer are identical.
   */
  @Test
  public void testWriterVariantsAreConsistent ()
  {
    new PropertyChecker ().forAll (JSON, aJson -> {
      final JsonWriter aWriter = new JsonWriter ();
      final String sJson = aWriter.writeAsString (aJson);
      assertEquals (sJson, new String (aWriter.writeAsByteArray (aJson, StandardCharsets.UTF_8), StandardCharsets.UTF_8));

      final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
      aWriter.writeToWriter (aJson, aSW);
      assertEquals (sJson, aSW.getAsString ());
    });
  }
}
