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
package com.helger.commons.csv;

/**
 Copyright 2005 Bytecode Pty Ltd.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.stream.NonBlockingStringWriter;
import com.helger.commons.io.stream.NonClosingWriter;
import com.helger.commons.string.StringHelper;

public final class CSVWriterTest
{
  private static final Charset CHARSET = StandardCharsets.ISO_8859_1;

  /**
   * Test routine for converting output to a string.
   *
   * @param args
   *        the elements of a line of the CSV file
   * @return a String version
   * @throws IOException
   *         if there are problems writing
   */
  @Nonnull
  private static String _invokeWriter (final String [] args) throws IOException
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final CSVWriter aWriter = new CSVWriter (aSW).setQuoteChar ('\'').setEscapeChar ('"'))
    {
      aWriter.writeNext (args);
    }
    return aSW.getAsString ();
  }

  @Nonnull
  private static String _invokeNoEscapeWriter (final String [] args) throws IOException
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final CSVWriter aWriter = new CSVWriter (aSW).setQuoteChar ('\'')
                                                      .setEscapeChar (CSVWriter.NO_ESCAPE_CHARACTER))
    {
      aWriter.writeNext (args);
    }
    return aSW.getAsString ();
  }

  @Test
  public void correctlyParseNullString () throws IOException
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final CSVWriter aWriter = new CSVWriter (aSW).setQuoteChar ('\''))
    {
      aWriter.writeNext ((String []) null);
      assertEquals (0, aSW.getAsString ().length ());
      aWriter.writeNext ((ICommonsList <String>) null);
      assertEquals (0, aSW.getAsString ().length ());
    }
  }

  @Test
  public void correctlyParserNullObject () throws IOException
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final CSVWriter aWriter = new CSVWriter (aSW).setQuoteChar ('\''))
    {
      aWriter.writeNext ((String []) null, false);
      assertEquals (0, aSW.getAsString ().length ());
      aWriter.writeNext ((ICommonsList <String>) null, false);
      assertEquals (0, aSW.getAsString ().length ());
    }
  }

  /**
   * Tests parsing individual lines.
   *
   * @throws IOException
   *         if the reader fails.
   */
  @Test
  public void testParseLine () throws IOException
  {
    // test normal case
    final String [] normal = { "a", "b", "c" };
    String output = _invokeWriter (normal);
    assertEquals ("'a','b','c'\n", output);

    // test quoted commas
    final String [] quoted = { "a", "b,b,b", "c" };
    output = _invokeWriter (quoted);
    assertEquals ("'a','b,b,b','c'\n", output);

    // test empty elements
    final String [] empty = {,};
    output = _invokeWriter (empty);
    assertEquals ("\n", output);

    // test multiline quoted
    final String [] multiline = { "This is a \n multiline entry", "so is \n this" };
    output = _invokeWriter (multiline);
    assertEquals ("'This is a \n multiline entry','so is \n this'\n", output);

    // test quoted line
    final String [] quoteLine = { "This is a \" multiline entry", "so is \n this" };
    output = _invokeWriter (quoteLine);
    assertEquals ("'This is a \"\" multiline entry','so is \n this'\n", output);

  }

  @Test
  public void testSpecialCharacters () throws IOException
  {
    // test quoted line
    final String [] quoteLine = { "This is a \r multiline entry", "so is \n this" };
    final String output = _invokeWriter (quoteLine);
    assertEquals ("'This is a \r multiline entry','so is \n this'\n", output);
  }

  @Test
  public void parseLineWithBothEscapeAndQuoteChar () throws IOException
  {
    // test quoted line
    final String [] quoteLine = { "This is a 'multiline' entry", "so is \n this" };
    final String output = _invokeWriter (quoteLine);
    assertEquals ("'This is a \"'multiline\"' entry','so is \n this'\n", output);
  }

  /**
   * Tests parsing individual lines.
   *
   * @throws IOException
   *         if the reader fails.
   */
  @Test
  public void testParseLineWithNoEscapeChar () throws IOException
  {

    // test normal case
    final String [] normal = { "a", "b", "c" };
    String output = _invokeNoEscapeWriter (normal);
    assertEquals ("'a','b','c'\n", output);

    // test quoted commas
    final String [] quoted = { "a", "b,b,b", "c" };
    output = _invokeNoEscapeWriter (quoted);
    assertEquals ("'a','b,b,b','c'\n", output);

    // test empty elements
    final String [] empty = {,};
    output = _invokeNoEscapeWriter (empty);
    assertEquals ("\n", output);

    // test multiline quoted
    final String [] multiline = { "This is a \n multiline entry", "so is \n this" };
    output = _invokeNoEscapeWriter (multiline);
    assertEquals ("'This is a \n multiline entry','so is \n this'\n", output);
  }

  @Test
  public void parseLineWithNoEscapeCharAndQuotes () throws IOException
  {
    final String [] quoteLine = { "This is a \" 'multiline' entry", "so is \n this" };
    final String output = _invokeNoEscapeWriter (quoteLine);
    assertEquals ("'This is a \" 'multiline' entry','so is \n this'\n", output);
  }

  /**
   * Test writing to a list.
   *
   * @throws IOException
   *         if the reader fails.
   */
  @Test
  public void testWriteAll () throws IOException
  {
    final ICommonsList <ICommonsList <String>> allElements = new CommonsArrayList <> ();
    allElements.add (StringHelper.getExploded ('#', "Name#Phone#Email"));
    allElements.add (StringHelper.getExploded ('#', "Glen#1234#glen@abcd.com"));
    allElements.add (StringHelper.getExploded ('#', "John#5678#john@efgh.com"));

    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final CSVWriter aWriter = new CSVWriter (aSW))
    {
      aWriter.writeAll (allElements);
    }

    final String sResult = aSW.getAsString ().trim ();
    final String [] aLines = StringHelper.getExplodedArray ('\n', sResult);

    assertEquals (3, aLines.length);
  }

  /**
   * Test writing from a list.
   *
   * @throws IOException
   *         if the reader fails.
   */
  @Test
  public void testWriteAllObjects () throws IOException
  {
    final ICommonsList <ICommonsList <String>> allElements = new CommonsArrayList <> ();
    allElements.add (StringHelper.getExploded ('#', "Name#Phone#Email"));
    allElements.add (StringHelper.getExploded ('#', "Glen#1234#glen@abcd.com"));
    allElements.add (StringHelper.getExploded ('#', "John#5678#john@efgh.com"));

    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final CSVWriter aWriter = new CSVWriter (aSW))
    {
      aWriter.writeAll (allElements, false);
    }

    final String sResult = aSW.getAsString ();
    final String [] aLines = StringHelper.getExplodedArray ('\n', sResult.trim ());
    assertEquals (3, aLines.length);

    final String [] aValues = StringHelper.getExplodedArray (',', aLines[1]);
    assertEquals (3, aValues.length);
    assertEquals ("1234", aValues[1]);
  }

  /**
   * Tests the option of having omitting quotes in the output stream.
   *
   * @throws IOException
   *         if bad things happen
   */
  @Test
  public void testNoQuoteChars () throws IOException
  {
    final String [] line = { "Foo", "Bar", "Baz" };
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final CSVWriter aWriter = new CSVWriter (aSW).setQuoteChar (CSVWriter.NO_QUOTE_CHARACTER))
    {
      aWriter.writeNext (line);
    }

    final String result = aSW.getAsString ();
    assertEquals ("Foo,Bar,Baz\n", result);
  }

  /**
   * Tests the option of having omitting quotes in the output stream.
   *
   * @throws IOException
   *         if bad things happen
   */
  @Test
  public void testNoQuoteCharsAndNoEscapeChars () throws IOException
  {

    final String [] line = { "Foo", "Bar", "Baz" };
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final CSVWriter aWriter = new CSVWriter (aSW).setQuoteChar (CSVWriter.NO_QUOTE_CHARACTER)
                                                      .setEscapeChar (CSVWriter.NO_ESCAPE_CHARACTER))
    {
      aWriter.writeNext (line);
    }
    final String result = aSW.getAsString ();

    assertEquals ("Foo,Bar,Baz\n", result);
  }

  /**
   * Tests the ability for the writer to apply quotes only where strings contain
   * the separator, escape, quote or new line characters.
   *
   * @throws IOException
   *         On IO error
   */
  @Test
  public void testIntelligentQuotes () throws IOException
  {
    final String [] line = { "1", "Foo", "With,Separator", "Line\nBreak", "Hello \"Foo Bar\" World", "Bar" };
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final CSVWriter aWriter = new CSVWriter (aSW))
    {
      aWriter.writeNext (line, false);
    }
    final String result = aSW.getAsString ();

    assertEquals ("1,Foo,\"With,Separator\",\"Line\nBreak\",\"Hello \\\"Foo Bar\\\" World\",Bar\n", result);
  }

  /**
   * Test null values.
   *
   * @throws IOException
   *         if bad things happen
   */
  @Test
  public void testNullValues () throws IOException
  {

    final String [] line = { "Foo", null, "Bar", "baz" };
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final CSVWriter aWriter = new CSVWriter (aSW))
    {
      aWriter.writeNext (line);
    }
    final String result = aSW.getAsString ();
    assertEquals ("\"Foo\",,\"Bar\",\"baz\"\n", result);
  }

  @Test
  public void testStreamFlushing () throws IOException
  {
    final String WRITE_FILE = "target/myfile.csv";
    final String [] nextLine = new String [] { "aaaa", "bbbb", "cccc", "dddd" };

    try (final Writer fileWriter = new NonClosingWriter (new OutputStreamWriter (new FileOutputStream (WRITE_FILE),
                                                                                 CHARSET)))
    {
      final CSVWriter writer = new CSVWriter (fileWriter);
      writer.writeNext (nextLine);

      // If close is not executed, it is not written in the file.
    }
  }

  @Test (expected = IOException.class)
  public void flushWillThrowIOException () throws IOException
  {
    final String [] line = { "Foo", "bar's" };
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    final CSVWriter aWriter = new MockThrowingCSVWriter (aSW);
    aWriter.writeNext (line);
    aWriter.flush ();
  }

  @Test
  public void flushQuietlyWillNotThrowException ()
  {
    final String [] line = { "Foo", "bar's" };
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    final CSVWriter aWriter = new MockThrowingCSVWriter (aSW);
    aWriter.writeNext (line);
    aWriter.flushQuietly ();
  }

  @Test
  public void testAlternateEscapeChar () throws IOException
  {
    final String [] line = { "Foo", "bar's" };
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final CSVWriter aWriter = new CSVWriter (aSW).setEscapeChar ('\''))
    {
      aWriter.writeNext (line);
    }
    assertEquals ("\"Foo\",\"bar''s\"\n", aSW.getAsString ());
  }

  @Test
  public void testNoQuotingNoEscaping () throws IOException
  {
    final String [] line = { "\"Foo\",\"Bar\"" };
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final CSVWriter aWriter = new CSVWriter (aSW).setQuoteChar (CSVWriter.NO_QUOTE_CHARACTER)
                                                      .setEscapeChar (CSVWriter.NO_ESCAPE_CHARACTER))
    {
      aWriter.writeNext (line);
    }
    assertEquals ("\"Foo\",\"Bar\"\n", aSW.getAsString ());
  }

  @Test
  public void testNestedQuotes () throws IOException
  {
    final String [] data = new String [] { "\"\"", "test" };
    final String oracle = "\"\\\"\\\"\",\"test\"\n";

    final File tempFile = File.createTempFile ("csvWriterTest", ".csv");
    tempFile.deleteOnExit ();
    final Writer fwriter = new OutputStreamWriter (new FileOutputStream (tempFile), CHARSET);
    try (final CSVWriter writer = new CSVWriter (fwriter))
    {
      // write the test data:
      writer.writeNext (data);
    }

    try
    {
      // assert that the writer was also closed.
      fwriter.flush ();
      fail ();
    }
    catch (final IOException e)
    {
      // we should go through here..
    }

    // read the data and compare.
    try (final Reader in = new InputStreamReader (new FileInputStream (tempFile), CHARSET))
    {
      final StringBuilder aFileContents = new StringBuilder (CCSV.INITIAL_STRING_SIZE);
      int ch;
      while ((ch = in.read ()) != -1)
      {
        aFileContents.append ((char) ch);
      }
      assertEquals (oracle, aFileContents.toString ());
    }
  }

  @Test
  public void testAlternateLineFeeds () throws IOException
  {
    final String [] aLine = { "Foo", "Bar", "baz" };
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final CSVWriter aWriter = new CSVWriter (aSW).setLineEnd ("\r"))
    {
      aWriter.writeNext (aLine);
    }
    final String result = aSW.getAsString ();
    assertTrue (result.endsWith ("\r"));
  }

  @Test
  public void testAvoidFinalLineEnd () throws IOException
  {
    final String [] aLine = { "Foo", "Bar", "baz" };
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final CSVWriter aWriter = new CSVWriter (aSW).setAvoidFinalLineEnd (true))
    {
      aWriter.writeNext (aLine, false);
      assertEquals ("Foo,Bar,baz", aSW.getAsString ());
      aWriter.writeNext (aLine, false);
      assertEquals ("Foo,Bar,baz\nFoo,Bar,baz", aSW.getAsString ());
      aWriter.writeNext (aLine, false);
      assertEquals ("Foo,Bar,baz\nFoo,Bar,baz\nFoo,Bar,baz", aSW.getAsString ());
    }
  }

  @Test
  public void testApplyQuotesToAll () throws IOException
  {
    final String [] aLine = { "Foo", "ab\"cd", "baz" };
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final CSVWriter aWriter = new CSVWriter (aSW).setApplyQuotesToAll (false))
    {
      aWriter.writeNext (aLine);
      assertEquals ("Foo,\"ab\\\"cd\",baz\n", aSW.getAsString ());
      aWriter.writeNext (aLine);
      assertEquals ("Foo,\"ab\\\"cd\",baz\nFoo,\"ab\\\"cd\",baz\n", aSW.getAsString ());
    }
  }
}
