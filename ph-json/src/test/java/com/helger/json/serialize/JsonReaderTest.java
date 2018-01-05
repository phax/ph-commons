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
package com.helger.json.serialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.annotation.Nonnull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.file.FileSystemIterator;
import com.helger.commons.io.stream.NonBlockingStringReader;
import com.helger.commons.timing.StopWatch;
import com.helger.json.IJson;
import com.helger.json.parser.handler.StringAssemblyJsonParserHandler;

/**
 * Test class for class {@link JsonReader}.
 *
 * @author Philip Helger
 */
public final class JsonReaderTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (JsonReaderTest.class);

  private static void _testReassemble (@Nonnull final String sJson)
  {
    final StringAssemblyJsonParserHandler aHandler = new StringAssemblyJsonParserHandler ();
    JsonReader.parseJson (new NonBlockingStringReader (sJson), aHandler);
    assertEquals (sJson, aHandler.getJsonString ());
  }

  @Test
  public void testBasic ()
  {
    for (final String sJson : new String [] { "true",
                                              "false",
                                              "null",
                                              "10",
                                              "0",
                                              "199999",
                                              "-10",
                                              "-0",
                                              "-199999",
                                              "1.5",
                                              "1.0",
                                              "1.00000",
                                              "-1.5",
                                              "-1.0",
                                              "-1.00000",
                                              "10e+3",
                                              "0e+3",
                                              "199999e+3",
                                              "-10e+3",
                                              "-0e+3",
                                              "-199999e+3",
                                              "1.5e+3",
                                              "1.0e+3",
                                              "1.00000e+3",
                                              "-1.5e+3",
                                              "-1.0e+3",
                                              "-1.00000e+3",
                                              "10e-3",
                                              "0e-3",
                                              "199999e-3",
                                              "-10e-3",
                                              "-0e-3",
                                              "-199999e-3",
                                              "1.5e-3",
                                              "1.0e-3",
                                              "1.00000e-3",
                                              "-1.5e-3",
                                              "-1.0e-3",
                                              "-1.00000e-3",
                                              "10E15",
                                              "0E15",
                                              "199999E15",
                                              "-10E15",
                                              "-0E15",
                                              "-199999E15",
                                              "1.5E15",
                                              "1.0E15",
                                              "1.00000E15",
                                              "-1.5E15",
                                              "-1.0E15",
                                              "-1.00000E15",
                                              "9223372036854775807",
                                              "-9223372036854775808",
                                              "\"abc'def'hgi\"",
                                              "\"ab\\\"ab\\/ab\\\\aa\\bab\\nab\\fab\\rab\\nab\\tab\"",
                                              "\"ab\\u1234ab\"",
                                              "[]",
                                              "[3]",
                                              "[3,4,5]",
                                              "[3,\"abc\",4,[],5]",
                                              "{}",
                                              " { } ",
                                              "{\"key\":56}",
                                              "{\"key\":56, \"value2\": \"abc\"}",
                                              "  {  \"key\"  :  -1.00000E15  ,  \"value2\"  :  \"abc\"  }  ",
                                              "{\"key with spaces\":\"value with spaces\"}",
                                              "{\"key:with:colon\":\"value:with:colon\"}",
                                              "{\"key\\/with\\/slash\":\"value\\/with\\/slash\"}",
                                              "{\"key/with/slash\":\"value/with/slash\"}",
                                              "{\"key\":{\"key2\":{\"key3\":3}}}",
                                              "{}",
                                              "{ \"v\":\"1\"}",
                                              "{ \"v\":\"1\"\r\n}",
                                              "{ \"v\":1}",
                                              "{ \"v\":\"ab'c\"}",
                                              "{ \"PI\":3.141E-10}",
                                              "{ \"PI\":3.141e-10}",
                                              "{ \"v\":12345123456789}",
                                              "{ \"v\":123456789123456789123456789}",
                                              "[ 1,2,3,4]",
                                              "[ \"1\",\"2\",\"3\",\"4\"]",
                                              "[ { }, { },[]]",
                                              "{ \"v\":\"\u2000\u20ff\"}",
                                              "{ \"v\":\"\u2000\u20FF\"}",
                                              "{ \"a\":\"hp://foo\"}",
                                              "{ \"a\":null}",
                                              "{ \"a\":true}",
                                              "{ \"a\" : true }",
                                              "{ \"v\":1.7976931348623157E308}",
                                              "{ \"v\":'ab\"c'}",
                                              "{ 'value':'string'}" })
    {
      assertTrue ("Failed to parse: " + sJson, JsonReader.isValidJson (sJson));
      final IJson aJson = JsonReader.readFromString (sJson);
      assertNotNull ("Failed to parse: " + sJson, aJson);
      _testReassemble (sJson);
    }

    assertEquals ("\"slashed/value\"", JsonReader.readFromString ("\"slashed/value\"").getAsJsonString ());
    assertEquals ("\"slashed/value\"", JsonReader.readFromString ("\"slashed\\/value\"").getAsJsonString ());
  }

  @Test
  public void testParseWithComments ()
  {
    for (final String sJson : new String [] { "true",
                                              " true",
                                              " /* x */ true /* y */ ",
                                              "/* a *//* b */false/**//**/",
                                              "  null /*** a */ /* foo ***/",
                                              "  /* 111 */ 10 /* a */",
                                              " /* a */ [  /* a */ ]  /* a */",
                                              "  /* a */ [  /* a */ 11  /* a */ ]  /* a */",
                                              "  /* a */ [  /* a */ 11  /* a */ ,  /* a */ 22  /* a */ ]  /* a */",
                                              "  /* a */ {  /* a */ } /* a */ ",
                                              "  /* a */ {  /* a */ \"name\"  /* a */ :  /* a */ 999  /* a */ }  /* a */",
                                              "  /* a */ {  /* a */ \"name\"  /* a */ :  /* a */ 999  /* a */ , /* a */ \"name2\"  /* a */ :  /* a */ \"string\"  /* a */ }  /* a */" })
    {
      assertTrue ("Failed to parse: " + sJson, JsonReader.isValidJson (sJson));
      final IJson aJson = JsonReader.readFromString (sJson);
      assertNotNull ("Failed to parse: " + sJson, aJson);
      _testReassemble (sJson);
    }
  }

  @Test
  public void testSyntaxErrors ()
  {
    for (final String sJson : new String [] { "tru",
                                              "tRue",
                                              "foo",
                                              "\"unclosed string",
                                              "\"invalid char \r\"",
                                              "/* unclosed comment1",
                                              "/* unclosed comment2*",
                                              "/",
                                              "//",
                                              "0123 /* invalid number */",
                                              "123.",
                                              "123.foo",
                                              "123ea",
                                              "123e+",
                                              "-a",
                                              "-+",
                                              "[",
                                              "[[[[[[[[[[[[[",
                                              "[,]",
                                              "[,1]",
                                              "[1,]",
                                              "[1 1]",
                                              "[][",
                                              "{",
                                              "{{{{{{{{{{{{{{{{{{",
                                              "{,}",
                                              "{:5}",
                                              "{\"key\"}",
                                              "{\"key\":}",
                                              "{\"key\":,}",
                                              "{\"key\":55,}",
                                              "{\"key\" 55}",
                                              "{\"key\":55 \"str\"}",
                                              "\"bla\\u123x\"",
                                              "\"bla\\x\"",
                                              "{'X':'s",
                                              "{'X",
                                              "{ \"v\":str}",
                                              "{ \"v\":It's'Work}",
                                              "{ a:1234}",
                                              "[ a,bc]",
                                              "{ \"v\":s1 s2}",
                                              "{ \"v\":s1 s2 }",
                                              "{ \"a\":\"foo.bar\"}#toto",
                                              "{v:15-55}",
                                              "{v:15%}",
                                              "{v:15.06%}",
                                              "{ \"v\":s1' s2}",
                                              "{ \"v\":s1\" \"s2}",
                                              "{ \"NaN\":NaN}",
                                              "[ a},b]",
                                              "[ a:,b]",
                                              "{ a,b:123}",
                                              "{ a]b:123}", })
    {
      assertFalse ("Parsed even if error expected: " + sJson, JsonReader.isValidJson (sJson));
      final IJson aJson = JsonReader.readFromString (sJson);
      assertNull ("Parsed even if error expected: " + sJson, aJson);
    }
  }

  @Test
  public void testFilesSuccess ()
  {
    for (final File f : new FileSystemIterator ("src/test/resources/json"))
      if (f.isFile () && f.getName ().endsWith (".json"))
      {
        s_aLogger.info ("Reading file " + f.getName ());
        final StopWatch aSW1 = StopWatch.createdStarted ();
        assertTrue ("Failed to parse file: " + f.getName (), JsonReader.isValidJson (f));
        s_aLogger.info ("  Validation: " + aSW1.stopAndGetMillis () + " ms");

        final StopWatch aSW2 = StopWatch.createdStarted ();
        final IJson aJson = JsonReader.readFromFile (f);
        assertNotNull ("Failed to parse: " + f.getAbsolutePath (), aJson);
        s_aLogger.info ("  Reading: " + aSW2.stopAndGetMillis () + " ms");
      }
  }

  @Test
  public void testFilesFailure ()
  {
    for (final File f : new FileSystemIterator ("src/test/resources/json/fail"))
      if (f.isFile () && f.getName ().endsWith (".json"))
      {
        s_aLogger.info ("Reading file " + f.getName ());
        assertFalse ("Parsed even if error expected: " + f.getName (), JsonReader.isValidJson (f));
        final IJson aJson = JsonReader.readFromFile (f);
        assertNull ("Parsed even if error expected: " + f.getName (), aJson);
      }
  }
}
