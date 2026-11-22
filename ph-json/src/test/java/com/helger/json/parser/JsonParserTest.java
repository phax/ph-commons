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
package com.helger.json.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.io.nonblocking.NonBlockingStringReader;
import com.helger.base.io.stream.LoggingInputStream;
import com.helger.base.io.stream.StringInputStream;
import com.helger.json.IJson;
import com.helger.json.IJsonObject;
import com.helger.json.IJsonValue;
import com.helger.json.JsonArray;
import com.helger.json.JsonObject;
import com.helger.json.parser.handler.CollectingJsonParserHandler;
import com.helger.json.parser.handler.StringAssemblyJsonParserHandler;
import com.helger.json.serialize.JsonReader;

/**
 * Test class for class {@link JsonParser}.
 *
 * @author Philip Helger
 */
public final class JsonParserTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (JsonParserTest.class);

  @Nullable
  private static IJson _read (@NonNull final String sJson, @Nullable final IJsonParserSettings aParserSettings)
  {
    final CollectingJsonParserHandler aHandler = new CollectingJsonParserHandler ();
    if (JsonReader.parseJson (new NonBlockingStringReader (sJson), aHandler, aParserSettings, null).isFailure ())
      return null;
    return aHandler.getJson ();
  }

  @Test
  public void testAlwaysUseBigNumberInt ()
  {
    final String sJson = "5";

    assertEquals (Integer.class, ((IJsonValue) _read (sJson, null)).getValueClass ());
    assertEquals (BigInteger.class,
                  ((IJsonValue) _read (sJson, new JsonParserSettings ().setAlwaysUseBigNumber (true)))
                                                                                                      .getValueClass ());
  }

  @Test
  public void testAlwaysUseBigNumberDecimal ()
  {
    final String sJson = "5.1";

    assertEquals (Double.class, ((IJsonValue) _read (sJson, null)).getValueClass ());
    assertEquals (BigDecimal.class,
                  ((IJsonValue) _read (sJson, new JsonParserSettings ().setAlwaysUseBigNumber (true)))
                                                                                                      .getValueClass ());
  }

  @Test
  public void testNumber ()
  {
    assertEquals (BigInteger.class, ((IJsonValue) _read ("-5e10", null)).getValueClass ());
    assertEquals (BigInteger.class, ((IJsonValue) _read ("-5e7897", null)).getValueClass ());
    assertEquals (BigInteger.class, ((IJsonValue) _read ("5e10", null)).getValueClass ());
    assertEquals (BigInteger.class, ((IJsonValue) _read ("5e7897", null)).getValueClass ());
    assertEquals (BigDecimal.class, ((IJsonValue) _read ("-5e-10", null)).getValueClass ());
    assertEquals (BigDecimal.class, ((IJsonValue) _read ("-5e-7897", null)).getValueClass ());
    assertEquals (BigDecimal.class, ((IJsonValue) _read ("5e-10", null)).getValueClass ());
    assertEquals (BigDecimal.class, ((IJsonValue) _read ("5e-7897", null)).getValueClass ());
  }

  @Test
  public void testHugePositiveExponent ()
  {
    // With the default max exponent, "1e999999999" is rejected while reading -
    // and must be a graceful parse failure (null), not an escaping exception.
    assertNull (_read ("1e999999999", null));
    assertNull (_read ("-1e999999999", null));
    assertNull (_read ("9e2147483647", null));
    assertNull (JsonReader.readFromString ("1e999999999"));

    // Even when the exponent limit is raised so far that the exponent itself is
    // accepted, "9e2147483647" makes BigDecimal.toBigIntegerExact() throw an
    // ArithmeticException ("BigInteger would overflow supported range"). This
    // backstop must be turned into a graceful parse failure (null) instead of
    // letting the RuntimeException escape the parser.
    final IJsonParserSettings aHighLimit = new JsonParserSettings ().setMaxExponent (Integer.MAX_VALUE);
    assertNull (_read ("9e2147483647", aHighLimit));
  }

  @Test
  public void testMaxExponent ()
  {
    // Default limit is 100.000 (absolute value), aligned with Jackson
    assertEquals (JsonParserSettings.DEFAULT_MAX_EXPONENT, new JsonParserSettings ().getMaxExponent ());

    // Exactly at the limit is still allowed
    assertNotNull (_read ("1e100000", null));
    assertNotNull (_read ("1e-100000", null));

    // Beyond the limit is rejected - for both positive and negative exponents
    assertNull (_read ("1e100001", null));
    assertNull (_read ("1e-100001", null));

    // An exponent with far more digits than a long can hold must not overflow
    // but be rejected gracefully
    assertNull (_read ("1e99999999999999999999", null));

    // Regular exponents are unaffected
    assertNotNull (_read ("1e5", null));
    assertNotNull (_read ("1e-5", null));

    // Custom lower limit
    final IJsonParserSettings aSettings = new JsonParserSettings ().setMaxExponent (10);
    assertNotNull (_read ("1e10", aSettings));
    assertNull (_read ("1e11", aSettings));

    // A too small max exponent value is not allowed
    try
    {
      new JsonParserSettings ().setMaxExponent (0);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testDuplicateObjectKeyLastWins ()
  {
    // Duplicate keys are tolerated (a warning is logged); the last value wins
    // and the position of the first occurrence is kept.
    final IJsonObject aJson = JsonReader.builder ().source ("{\"a\":1,\"b\":2,\"a\":3}").readAsObject ();
    assertNotNull (aJson);
    assertEquals (2, aJson.size ());
    assertEquals (3, aJson.getAsInt ("a", -1));
    assertEquals (2, aJson.getAsInt ("b", -1));
  }

  @Test
  public void testReadEscaped ()
  {
    final String sJson = "'\\u1234'";

    assertEquals (String.class, ((IJsonValue) _read (sJson, null)).getValueClass ());
    final String sValue = _read (sJson, null).getAsValue ().getAsString ();
    assertNotNull (sValue);
    assertEquals (1, sValue.length ());
    assertEquals ('\u1234', sValue.charAt (0));
  }

  @Test
  public void testReadMultipleObjectsFromReader ()
  {
    final String sJson = "{ 'a': 1}{ 'b':true}";

    try (final NonBlockingStringReader aReader = new NonBlockingStringReader (sJson))
    {
      IJson aJson;
      int nCount = 0;
      do
      {
        aJson = JsonReader.builderMultiObject ().source (aReader).read ();
        if (aJson != null)
          ++nCount;
      } while (aJson != null);
      assertEquals (2, nCount);
    }
  }

  /**
   * Does not work with InputStream, because the used StreamDecoder reads to eagerly:
   *
   * <pre>
  StringInputStream(NonBlockingByteArrayInputStream).read(byte[], int, int) line: 216
  LoggingInputStream(FilterInputStream).read(byte[], int, int) line: 133
  LoggingInputStream.read(byte[], int, int) line: 109
  NonBlockingPushbackInputStream(FilterInputStream).read(byte[], int, int) line: 133
  NonBlockingPushbackInputStream.read(byte[], int, int) line: 197
  StreamDecoder.readBytes() line: 284 [local variables unavailable]
  StreamDecoder.implRead(char[], int, int) line: 326
  StreamDecoder.read(char[], int, int) line: 178
  StreamDecoder.read0() line: 127 [local variables unavailable]
  StreamDecoder.read() line: 112 [local variables unavailable]
  InputStreamReader.read() line: 168 [local variables unavailable]
  NonClosingReader(FilterReader).read() line: 65
  NonBlockingPushbackReader(FilterReader).read() line: 65
  NonBlockingPushbackReader.read() line: 111
  JsonParser._readChar() line: 240
  JsonParser._skipSpaces() line: 349
  JsonParser._readValue() line: 910
  JsonParser.parse() line: 985
  JsonReader.parseJson(Reader, IJsonParserHandler, IJsonParserCustomizeCallback, IJsonParseExceptionCallback) line: 156
  JsonReader.readJson(Reader, IJsonParserCustomizeCallback, IJsonParseExceptionCallback) line: 445
  JsonReader$Builder.read() line: 1138
  JsonParserTest.testReadMultipleObjectsFromStream() line: 137
   * </pre>
   *
   * @throws IOException
   *         hopefully never
   */
  @Test
  @Ignore
  public void testReadMultipleObjectsFromStream () throws IOException
  {
    final String sJson = "{ 'a': 1}{ 'b':true}";

    try (final InputStream aIS = new LoggingInputStream (new StringInputStream (sJson, StandardCharsets.UTF_8)))
    {
      IJson aJson;
      int nCount = 0;
      do
      {
        LOGGER.info ("Run " + nCount);
        aJson = JsonReader.builderMultiObject ().source (aIS).read ();
        if (aJson != null)
        {
          ++nCount;
          LOGGER.info ("Output: " + aJson);
        }
      } while (aJson != null);
      assertEquals (2, nCount);
    }
  }

  /**
   * Create a reader that returns at most the provided number of chars per bulk read, to test the
   * refill handling of the internal buffer.
   */
  @NonNull
  private static Reader _createChunkedReader (@NonNull final String sJson, final int nMaxChars)
  {
    return new FilterReader (new NonBlockingStringReader (sJson))
    {
      @Override
      public int read (final char [] aBuf, final int nOfs, final int nLen) throws IOException
      {
        return super.read (aBuf, nOfs, Math.min (nLen, nMaxChars));
      }
    };
  }

  @NonNull
  private static String _createLargeJson ()
  {
    final StringBuilder aLongText = new StringBuilder ();
    for (int i = 0; i < 1000; ++i)
      aLongText.append ("Long text ").append (i).append (i % 7 == 0 ? " with \"quote\"\t\n" : " ");

    final JsonArray aArray = new JsonArray ();
    for (int i = 0; i < 500; ++i)
    {
      final JsonObject aObj = new JsonObject ();
      aObj.add ("id", i);
      aObj.add ("name", "Name " + i);
      aObj.add ("escaped \"name\"", "Escaped\\ \"value\"\r\n\u0001 " + i);
      aObj.add ("unicode", "\u00e4\u00f6\u00fc\u20ac " + i);
      aObj.add ("double", i * 1.5);
      aObj.add ("long", 10_000_000_000L + i);
      aObj.add ("bool", (i & 1) == 0);
      aObj.add ("array", new JsonArray ().add (i).add ("x").add (new JsonObject ()));
      if (i % 50 == 0)
        aObj.add ("long text", aLongText.toString ());
      aArray.add (aObj);
    }
    return aArray.getAsJsonString ();
  }

  @Test
  public void testReadLargeWithBufferBoundaries () throws JsonParseException
  {
    final String sJson = _createLargeJson ();
    // Ensure the internal buffer must be refilled multiple times
    assertTrue (sJson.length () > 100_000);
    final IJson aExpected = _read (sJson, null);
    assertNotNull (aExpected);

    for (final int nMaxChars : new int [] { 1, 2, 3, 7, 100, 8191, 8192, 8193, Integer.MAX_VALUE })
      for (final boolean bTrackPosition : new boolean [] { false, true })
      {
        // Read as object tree
        final CollectingJsonParserHandler aHandler = new CollectingJsonParserHandler ();
        final JsonParser aParser = new JsonParser (_createChunkedReader (sJson, nMaxChars), aHandler);
        aParser.jsonParserSettings ().setTrackPosition (bTrackPosition);
        aParser.parse ();
        assertEquals ("MaxChars=" + nMaxChars, aExpected, aHandler.getJson ());

        // Ensure the original strings are retained 1:1
        final StringAssemblyJsonParserHandler aHandler2 = new StringAssemblyJsonParserHandler ();
        final JsonParser aParser2 = new JsonParser (_createChunkedReader (sJson, nMaxChars), aHandler2);
        aParser2.jsonParserSettings ().setTrackPosition (bTrackPosition);
        aParser2.parse ();
        assertEquals ("MaxChars=" + nMaxChars, sJson, aHandler2.getJsonString ());
      }
  }

  @Test
  public void testReadInvalidWithBufferBoundaries ()
  {
    final String sValid = _createLargeJson ();
    // Unclosed string at the end
    final String sUnclosed = sValid.substring (0, sValid.length () - 1) + ",\"abc";
    // Tab character inside a string
    final String sTab = sValid.substring (0, sValid.length () - 1) + ",\"a\tb\"]";

    for (final String sJson : new String [] { sUnclosed, sTab })
      for (final int nMaxChars : new int [] { 1, 3, 8192, Integer.MAX_VALUE })
      {
        final JsonParser aParser = new JsonParser (_createChunkedReader (sJson, nMaxChars),
                                                   new CollectingJsonParserHandler ());
        try
        {
          aParser.parse ();
          fail ("MaxChars=" + nMaxChars);
        }
        catch (final JsonParseException ex)
        {
          // expected
        }
      }
    assertFalse (JsonReader.builder ().source (sUnclosed).isValidJson ());
    assertFalse (JsonReader.builder ().source (sTab).isValidJson ());
  }

  @Test
  public void testDoublePushbackWithBufferBoundaries ()
  {
    // "/" followed by a non-"*" requires two pushback chars, also across a buffer refill
    for (final int nMaxChars : new int [] { 1, 2, 3, 4, Integer.MAX_VALUE })
    {
      final JsonParser aParser = new JsonParser (_createChunkedReader ("[1 /x]", nMaxChars),
                                                 new CollectingJsonParserHandler ());
      try
      {
        aParser.parse ();
        fail ("MaxChars=" + nMaxChars);
      }
      catch (final JsonParseException ex)
      {
        assertTrue (ex.getMessage (), ex.getMessage ().endsWith ("Unexpected character '/' in JSON array"));
      }
    }
  }
}
