/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.helger.commons.collections.CollectionHelper;

/**
 * Test class for class {@link CSVReader}.
 * 
 * @author Philip Helger
 */
public final class CSVReaderTest
{
  private CSVReader m_aCSVReader;

  @Before
  public void setUp () throws Exception
  {
    final StringBuilder sb = new StringBuilder (CCSV.INITIAL_READ_SIZE);
    // standard case
    sb.append ("a,b,c").append ("\n");
    // quoted elements
    sb.append ("a,\"b,b,b\",c").append ("\n");
    // empty elements
    sb.append (",,").append ("\n");
    sb.append ("a,\"PO Box 123,\nKippax,ACT. 2615.\nAustralia\",d.\n");
    // Test quoted quote chars
    sb.append ("\"Glen \"\"The Man\"\" Smith\",Athlete,Developer\n");
    // """""","test" representing: "", test
    sb.append ("\"\"\"\"\"\",\"test\"\n");
    sb.append ("\"a\nb\",b,\"\nd\",e\n");
    m_aCSVReader = new CSVReader (new StringReader (sb.toString ()));
  }

  /**
   * Tests iterating over a reader.
   *
   * @throws IOException
   *         if the reader fails.
   */
  @Test
  public void testParseLine () throws IOException
  {
    // test normal case
    List <String> nextLine = m_aCSVReader.readNext ();
    assertEquals ("a", nextLine.get (0));
    assertEquals ("b", nextLine.get (1));
    assertEquals ("c", nextLine.get (2));

    // test quoted commas
    nextLine = m_aCSVReader.readNext ();
    assertEquals ("a", nextLine.get (0));
    assertEquals ("b,b,b", nextLine.get (1));
    assertEquals ("c", nextLine.get (2));

    // test empty elements
    nextLine = m_aCSVReader.readNext ();
    assertEquals (3, nextLine.size ());

    // test multiline quoted
    nextLine = m_aCSVReader.readNext ();
    assertEquals (3, nextLine.size ());

    // test quoted quote chars
    nextLine = m_aCSVReader.readNext ();
    assertEquals ("Glen \"The Man\" Smith", nextLine.get (0));

    nextLine = m_aCSVReader.readNext ();
    assertEquals ("\"\"", nextLine.get (0)); // check the tricky situation
    assertEquals ("test", nextLine.get (1)); // make sure we didn't ruin the
                                             // next
    // field..

    nextLine = m_aCSVReader.readNext ();
    assertEquals (4, nextLine.size ());

    // test end of stream
    assertNull (m_aCSVReader.readNext ());
  }

  @Test
  public void readerCanHandleNullInString () throws IOException
  {
    final StringBuilder sb = new StringBuilder (CCSV.INITIAL_READ_SIZE);
    sb.append ("a,\0b,c");

    final StringReader reader = new StringReader (sb.toString ());

    final CSVReaderBuilder builder = new CSVReaderBuilder (reader);
    final CSVReader defaultReader = builder.build ();

    final List <String> nextLine = defaultReader.readNext ();
    assertEquals (3, nextLine.size ());
    assertEquals ("a", nextLine.get (0));
    assertEquals ("\0b", nextLine.get (1));
    assertEquals (0, nextLine.get (1).charAt (0));
    assertEquals ("c", nextLine.get (2));
  }

  @Test
  public void testParseLineStrictQuote () throws IOException
  {
    final StringBuilder sb = new StringBuilder (CCSV.INITIAL_READ_SIZE);
    sb.append ("a,b,c").append ("\n"); // standard case
    sb.append ("a,\"b,b,b\",c").append ("\n"); // quoted elements
    sb.append (",,").append ("\n"); // empty elements
    sb.append ("a,\"PO Box 123,\nKippax,ACT. 2615.\nAustralia\",d.\n");
    sb.append ("\"Glen \"\"The Man\"\" Smith\",Athlete,Developer\n"); // Test
                                                                      // quoted
                                                                      // quote
                                                                      // chars
    sb.append ("\"\"\"\"\"\",\"test\"\n"); // """""","test" representing: "",
                                           // test
    sb.append ("\"a\nb\",b,\"\nd\",e\n");
    m_aCSVReader = new CSVReader (new StringReader (sb.toString ()), ',', '\"', true);

    // test normal case
    List <String> nextLine = m_aCSVReader.readNext ();
    assertEquals ("", nextLine.get (0));
    assertEquals ("", nextLine.get (1));
    assertEquals ("", nextLine.get (2));

    // test quoted commas
    nextLine = m_aCSVReader.readNext ();
    assertEquals ("", nextLine.get (0));
    assertEquals ("b,b,b", nextLine.get (1));
    assertEquals ("", nextLine.get (2));

    // test empty elements
    nextLine = m_aCSVReader.readNext ();
    assertEquals (3, nextLine.size ());

    // test multiline quoted
    nextLine = m_aCSVReader.readNext ();
    assertEquals (3, nextLine.size ());

    // test quoted quote chars
    nextLine = m_aCSVReader.readNext ();
    assertEquals ("Glen \"The Man\" Smith", nextLine.get (0));

    nextLine = m_aCSVReader.readNext ();
    assertTrue (nextLine.get (0).equals ("\"\"")); // check the tricky situation
    assertTrue (nextLine.get (1).equals ("test")); // make sure we didn't ruin
                                                   // the
    // next field..

    nextLine = m_aCSVReader.readNext ();
    assertEquals (4, nextLine.size ());
    assertEquals ("a\nb", nextLine.get (0));
    assertEquals ("", nextLine.get (1));
    assertEquals ("\nd", nextLine.get (2));
    assertEquals ("", nextLine.get (3));

    // test end of stream
    assertNull (m_aCSVReader.readNext ());
  }

  /**
   * Test parsing to a list.
   *
   * @throws IOException
   *         if the reader fails.
   */
  @Test
  public void testParseAll () throws IOException
  {
    assertEquals (7, m_aCSVReader.readAll ().size ());
  }

  /**
   * Tests constructors with optional delimiters and optional quote char.
   *
   * @throws IOException
   *         if the reader fails.
   */
  @Test
  public void testOptionalConstructors () throws IOException
  {

    final StringBuilder sb = new StringBuilder (CCSV.INITIAL_READ_SIZE);
    sb.append ("a\tb\tc").append ("\n"); // tab separated case
    sb.append ("a\t'b\tb\tb'\tc").append ("\n"); // single quoted elements
    final CSVReader c = new CSVReader (new StringReader (sb.toString ()), '\t', '\'');

    List <String> nextLine = c.readNext ();
    assertEquals (3, nextLine.size ());

    nextLine = c.readNext ();
    assertEquals (3, nextLine.size ());
  }

  @Test
  public void parseQuotedStringWithDefinedSeperator () throws IOException
  {
    final StringBuilder sb = new StringBuilder (CCSV.INITIAL_READ_SIZE);
    sb.append ("a\tb\tc").append ("\n"); // tab separated case

    final CSVReader c = new CSVReader (new StringReader (sb.toString ()), '\t');

    final List <String> nextLine = c.readNext ();
    assertEquals (3, nextLine.size ());
  }

  /**
   * Tests option to skip the first few lines of a file.
   *
   * @throws IOException
   *         if bad things happen
   */
  @Test
  public void testSkippingLines () throws IOException
  {

    final StringBuilder sb = new StringBuilder (CCSV.INITIAL_READ_SIZE);
    sb.append ("Skip this line\t with tab").append ("\n"); // should skip this
    sb.append ("And this line too").append ("\n"); // and this
    sb.append ("a\t'b\tb\tb'\tc").append ("\n"); // single quoted elements
    final CSVReader c = new CSVReader (new StringReader (sb.toString ()), '\t', '\'', 2);

    final List <String> nextLine = c.readNext ();
    assertEquals (3, nextLine.size ());

    assertEquals ("a", nextLine.get (0));
  }

  /**
   * Tests option to skip the first few lines of a file.
   *
   * @throws IOException
   *         if bad things happen
   */
  @Test
  public void testSkippingLinesWithDifferentEscape () throws IOException
  {

    final StringBuilder sb = new StringBuilder (CCSV.INITIAL_READ_SIZE);
    sb.append ("Skip this line?t with tab").append ("\n"); // should skip this
    sb.append ("And this line too").append ("\n"); // and this
    sb.append ("a\t'b\tb\tb'\t'c'").append ("\n"); // single quoted elements
    final CSVReader c = new CSVReader (new StringReader (sb.toString ()), '\t', '\'', '?', 2);

    final List <String> nextLine = c.readNext ();

    assertEquals (3, nextLine.size ());

    assertEquals ("a", nextLine.get (0));
    assertEquals ("c", nextLine.get (2));
  }

  /**
   * Test a normal non quoted line with three elements
   *
   * @throws IOException
   */
  @Test
  public void testNormalParsedLine () throws IOException
  {

    final StringBuilder sb = new StringBuilder (CCSV.INITIAL_READ_SIZE);

    sb.append ("a,1234567,c").append ("\n");// a,1234,c

    final CSVReader c = new CSVReader (new StringReader (sb.toString ()));

    final List <String> nextLine = c.readNext ();
    assertEquals (3, nextLine.size ());

    assertEquals ("a", nextLine.get (0));
    assertEquals ("1234567", nextLine.get (1));
    assertEquals ("c", nextLine.get (2));
  }

  /**
   * Same as testADoubleQuoteAsDataElement but I changed the quotechar to a
   * single quote.
   *
   * @throws IOException
   */
  @Test
  public void testASingleQuoteAsDataElement () throws IOException
  {

    final StringBuilder sb = new StringBuilder (CCSV.INITIAL_READ_SIZE);

    sb.append ("a,'''',c").append ("\n");// a,',c

    final CSVReader c = new CSVReader (new StringReader (sb.toString ()), ',', '\'');

    final List <String> nextLine = c.readNext ();
    assertEquals (3, nextLine.size ());

    assertEquals ("a", nextLine.get (0));
    assertEquals (1, nextLine.get (1).length ());
    assertEquals ("\'", nextLine.get (1));
    assertEquals ("c", nextLine.get (2));
  }

  /**
   * Same as testADoubleQuoteAsDataElement but I changed the quotechar to a
   * single quote. Also the middle field is empty.
   *
   * @throws IOException
   */
  @Test
  public void testASingleQuoteAsDataElementWithEmptyField () throws IOException
  {

    final StringBuilder sb = new StringBuilder (CCSV.INITIAL_READ_SIZE);

    sb.append ("a,'',c").append ("\n");// a,,c

    final CSVReader c = new CSVReader (new StringReader (sb.toString ()), ',', '\'');

    final List <String> nextLine = c.readNext ();
    assertEquals (3, nextLine.size ());

    assertEquals ("a", nextLine.get (0));
    assertEquals (0, nextLine.get (1).length ());
    assertEquals ("", nextLine.get (1));
    assertEquals ("c", nextLine.get (2));
  }

  @Test
  public void testSpacesAtEndOfString () throws IOException
  {
    final StringBuilder sb = new StringBuilder (CCSV.INITIAL_READ_SIZE);

    sb.append ("\"a\",\"b\",\"c\"   ");

    final CSVReader c = new CSVReader (new StringReader (sb.toString ()),
                                       CCSV.DEFAULT_SEPARATOR,
                                       CCSV.DEFAULT_QUOTE_CHARACTER,
                                       true);

    final List <String> nextLine = c.readNext ();
    assertEquals (3, nextLine.size ());

    assertEquals ("a", nextLine.get (0));
    assertEquals ("b", nextLine.get (1));
    assertEquals ("c", nextLine.get (2));
  }

  @Test
  public void testEscapedQuote () throws IOException
  {

    final StringBuilder sb = new StringBuilder ();

    sb.append ("a,\"123\\\"4567\",c").append ("\n");// a,123"4",c

    final CSVReader c = new CSVReader (new StringReader (sb.toString ()));

    final List <String> nextLine = c.readNext ();
    assertEquals (3, nextLine.size ());

    assertEquals ("123\"4567", nextLine.get (1));
  }

  @Test
  public void testEscapedEscape () throws IOException
  {

    final StringBuilder sb = new StringBuilder ();

    sb.append ("a,\"123\\\\4567\",c").append ("\n");// a,123"4",c

    final CSVReader c = new CSVReader (new StringReader (sb.toString ()));

    final List <String> nextLine = c.readNext ();
    assertEquals (3, nextLine.size ());

    assertEquals ("123\\4567", nextLine.get (1));
  }

  /**
   * Test a line where one of the elements is two single quotes and the quote
   * character is the default double quote. The expected result is two single
   * quotes.
   *
   * @throws IOException
   */
  @Test
  public void testSingleQuoteWhenDoubleQuoteIsQuoteChar () throws IOException
  {

    final StringBuilder sb = new StringBuilder (CCSV.INITIAL_READ_SIZE);

    sb.append ("a,'',c").append ("\n");// a,'',c

    final CSVReader c = new CSVReader (new StringReader (sb.toString ()));

    final List <String> nextLine = c.readNext ();
    assertEquals (3, nextLine.size ());

    assertEquals ("a", nextLine.get (0));
    assertEquals (2, nextLine.get (1).length ());
    assertEquals ("''", nextLine.get (1));
    assertEquals ("c", nextLine.get (2));
  }

  /**
   * Test a normal line with three elements and all elements are quoted
   *
   * @throws IOException
   */
  @Test
  public void testQuotedParsedLine () throws IOException
  {

    final StringBuilder sb = new StringBuilder (CCSV.INITIAL_READ_SIZE);

    sb.append ("\"a\",\"1234567\",\"c\"").append ("\n"); // "a","1234567","c"

    final CSVReader c = new CSVReader (new StringReader (sb.toString ()),
                                       CCSV.DEFAULT_SEPARATOR,
                                       CCSV.DEFAULT_QUOTE_CHARACTER,
                                       true);

    final List <String> nextLine = c.readNext ();
    assertEquals (3, nextLine.size ());

    assertEquals ("a", nextLine.get (0));
    assertEquals (1, nextLine.get (0).length ());

    assertEquals ("1234567", nextLine.get (1));
    assertEquals ("c", nextLine.get (2));
  }

  @Test
  public void bug106ParseLineWithCarriageReturnNewLineStrictQuotes () throws IOException
  {

    final StringBuilder sb = new StringBuilder (CCSV.INITIAL_READ_SIZE);

    sb.append ("\"a\",\"123\r\n4567\",\"c\"").append ("\n"); // "a","123\r\n4567","c"

    // public CSVReader(Reader reader, char separator, char quotechar, char
    // escape, int line, boolean strictQuotes,
    // boolean ignoreLeadingWhiteSpace, boolean keepCarriageReturn)
    final CSVReader c = new CSVReader (new StringReader (sb.toString ()),
                                       CCSV.DEFAULT_SEPARATOR,
                                       CCSV.DEFAULT_QUOTE_CHARACTER,
                                       CCSV.DEFAULT_ESCAPE_CHARACTER,
                                       CCSV.DEFAULT_SKIP_LINES,
                                       true,
                                       CCSV.DEFAULT_IGNORE_LEADING_WHITESPACE,
                                       true);

    final List <String> nextLine = c.readNext ();
    assertEquals (3, nextLine.size ());

    assertEquals ("a", nextLine.get (0));
    assertEquals (1, nextLine.get (0).length ());

    assertEquals ("123\r\n4567", nextLine.get (1));
    assertEquals ("c", nextLine.get (2));
  }

  @Test
  public void testIssue2992134OutOfPlaceQuotes () throws IOException
  {
    final StringBuilder sb = new StringBuilder (CCSV.INITIAL_READ_SIZE);

    sb.append ("a,b,c,ddd\\\"eee\nf,g,h,\"iii,jjj\"");

    final CSVReader c = new CSVReader (new StringReader (sb.toString ()));

    final List <String> nextLine = c.readNext ();

    assertEquals ("a", nextLine.get (0));
    assertEquals ("b", nextLine.get (1));
    assertEquals ("c", nextLine.get (2));
    assertEquals ("ddd\"eee", nextLine.get (3));
  }

  @Test (expected = UnsupportedOperationException.class)
  public void quoteAndEscapeMustBeDifferent ()
  {
    final StringBuilder sb = new StringBuilder (CCSV.INITIAL_READ_SIZE);

    sb.append ("a,b,c,ddd\\\"eee\nf,g,h,\"iii,jjj\"");

    new CSVReader (new StringReader (sb.toString ()),
                   CCSV.DEFAULT_SEPARATOR,
                   CCSV.DEFAULT_QUOTE_CHARACTER,
                   CCSV.DEFAULT_QUOTE_CHARACTER,
                   CCSV.DEFAULT_SKIP_LINES,
                   CCSV.DEFAULT_STRICT_QUOTES,
                   CCSV.DEFAULT_IGNORE_LEADING_WHITESPACE);
  }

  @Test (expected = UnsupportedOperationException.class)
  public void separatorAndEscapeMustBeDifferent ()
  {
    final StringBuilder sb = new StringBuilder (CCSV.INITIAL_READ_SIZE);

    sb.append ("a,b,c,ddd\\\"eee\nf,g,h,\"iii,jjj\"");

    new CSVReader (new StringReader (sb.toString ()),
                   CCSV.DEFAULT_SEPARATOR,
                   CCSV.DEFAULT_QUOTE_CHARACTER,
                   CCSV.DEFAULT_SEPARATOR,
                   CCSV.DEFAULT_SKIP_LINES,
                   CCSV.DEFAULT_STRICT_QUOTES,
                   CCSV.DEFAULT_IGNORE_LEADING_WHITESPACE);
  }

  @Test (expected = UnsupportedOperationException.class)
  public void separatorAndQuoteMustBeDifferent ()
  {
    final StringBuilder sb = new StringBuilder (CCSV.INITIAL_READ_SIZE);

    sb.append ("a,b,c,ddd\\\"eee\nf,g,h,\"iii,jjj\"");

    new CSVReader (new StringReader (sb.toString ()),
                   CCSV.DEFAULT_SEPARATOR,
                   CCSV.DEFAULT_SEPARATOR,
                   CCSV.DEFAULT_ESCAPE_CHARACTER,
                   CCSV.DEFAULT_SKIP_LINES,
                   CCSV.DEFAULT_STRICT_QUOTES,
                   CCSV.DEFAULT_IGNORE_LEADING_WHITESPACE);
  }

  /**
   * Tests iterating over a reader.
   *
   * @throws IOException
   *         if the reader fails.
   */
  @Test
  public void testIteratorFunctionality () throws IOException
  {
    final List <List <String>> expectedResult = new ArrayList <List <String>> ();
    expectedResult.add (CollectionHelper.newList ("a", "b", "c"));
    expectedResult.add (CollectionHelper.newList ("a", "b,b,b", "c"));
    expectedResult.add (CollectionHelper.newList ("", "", ""));
    expectedResult.add (CollectionHelper.newList ("a", "PO Box 123,\nKippax,ACT. 2615.\nAustralia", "d."));
    expectedResult.add (CollectionHelper.newList ("Glen \"The Man\" Smith", "Athlete", "Developer"));
    expectedResult.add (CollectionHelper.newList ("\"\"", "test"));
    expectedResult.add (CollectionHelper.newList ("a\nb", "b", "\nd", "e"));
    int idx = 0;
    for (final List <String> line : m_aCSVReader)
    {
      final List <String> expectedLine = expectedResult.get (idx++);
      assertEquals (expectedLine, line);
    }
  }

  @Test
  public void canCloseReader () throws IOException
  {
    m_aCSVReader.close ();
  }

  @Test
  public void canCreateIteratorFromReader ()
  {
    assertNotNull (m_aCSVReader.iterator ());
  }

  @Test
  public void attemptToReadCloseStreamReturnsNull () throws IOException
  {
    final BufferedReader bufferedReader = new BufferedReader (new StringReader (""));
    bufferedReader.close ();
    final CSVReader csvReader = new CSVReader (bufferedReader);
    assertNull (csvReader.readNext ());
  }

  @Test
  public void testIssue102 () throws IOException
  {
    final CSVReader csvReader = new CSVReader (new StringReader ("\"\",a\n\"\",b\n"));

    final List <String> firstRow = csvReader.readNext ();
    assertEquals (2, firstRow.size ());
    assertTrue (firstRow.get (0).isEmpty ());
    assertEquals ("a", firstRow.get (1));

    final List <String> secondRow = csvReader.readNext ();
    assertEquals (2, secondRow.size ());
    assertTrue (secondRow.get (0).isEmpty ());
    assertEquals ("b", secondRow.get (1));
  }

  @Test
  public void issue108ReaderPlaysWellWithChannels () throws IOException
  {
    final byte [] bytes = "name\r\nvalue\r\n".getBytes ("UTF-8");
    final ByteArrayInputStream bais = new ByteArrayInputStream (bytes);
    final ReadableByteChannel ch = Channels.newChannel (bais);
    final InputStream in = Channels.newInputStream (ch);
    final InputStreamReader reader = new InputStreamReader (in, Charset.forName ("UTF-8"));
    final CSVReaderBuilder builder = new CSVReaderBuilder (reader);
    final CSVReader csv = builder.withVerifyReader (false).build ();
    assertEquals (2, csv.readAll ().size ());
  }
}
