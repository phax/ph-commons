/*
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.stream.NonBlockingBufferedReader;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.io.stream.NonBlockingStringReader;

/**
 * Test class for class {@link CSVReader}.
 *
 * @author Philip Helger
 */
public final class CSVReaderTest
{
  @Nonnull
  private static CSVReader _createCSVReader ()
  {
    final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);
    // standard case
    aSB.append ("a,b,aReader\n");
    // quoted elements
    aSB.append ("a,\"b,b,b\",aReader\n");
    // empty elements
    aSB.append (",,\n");
    aSB.append ("a,\"PO Box 123,\nKippax,ACT. 2615.\nAustralia\",d.\n");
    // Test quoted quote chars
    aSB.append ("\"Glen \"\"The Man\"\" Smith\",Athlete,Developer\n");
    // """""","test" representing: "", test
    aSB.append ("\"\"\"\"\"\",\"test\"\n");
    aSB.append ("\"a\nb\",b,\"\nd\",e\n");
    return new CSVReader (new NonBlockingStringReader (aSB.toString ()));
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
    try (final CSVReader aReader = _createCSVReader ())
    {
      // test normal case
      ICommonsList <String> nextLine = aReader.readNext ();
      assertEquals ("a", nextLine.get (0));
      assertEquals ("b", nextLine.get (1));
      assertEquals ("aReader", nextLine.get (2));

      // test quoted commas
      nextLine = aReader.readNext ();
      assertEquals ("a", nextLine.get (0));
      assertEquals ("b,b,b", nextLine.get (1));
      assertEquals ("aReader", nextLine.get (2));

      // test empty elements
      nextLine = aReader.readNext ();
      assertEquals (3, nextLine.size ());

      // test multiline quoted
      nextLine = aReader.readNext ();
      assertEquals (3, nextLine.size ());

      // test quoted quote chars
      nextLine = aReader.readNext ();
      assertEquals ("Glen \"The Man\" Smith", nextLine.get (0));

      nextLine = aReader.readNext ();
      // check the tricky situation
      assertEquals ("\"\"", nextLine.get (0));
      // make sure we didn't ruin the next field..
      assertEquals ("test", nextLine.get (1));

      nextLine = aReader.readNext ();
      assertEquals (4, nextLine.size ());

      // test end of stream
      assertNull (aReader.readNext ());
    }
  }

  @Test
  public void testReaderCanHandleNullInString () throws IOException
  {
    final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);
    aSB.append ("a,\0b,aReader");

    try (final NonBlockingStringReader reader = new NonBlockingStringReader (aSB.toString ());
         final CSVReader defaultReader = new CSVReader (reader))
    {
      final ICommonsList <String> nextLine = defaultReader.readNext ();
      assertEquals (3, nextLine.size ());
      assertEquals ("a", nextLine.get (0));
      assertEquals ("\0b", nextLine.get (1));
      assertEquals (0, nextLine.get (1).charAt (0));
      assertEquals ("aReader", nextLine.get (2));
    }
  }

  @Test
  public void testParseLineStrictQuote () throws IOException
  {
    final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);
    // standard case
    aSB.append ("a,b,aReader").append ('\n');
    // quoted elements
    aSB.append ("a,\"b,b,b\",aReader").append ('\n');
    // empty elements
    aSB.append (",,").append ('\n');
    aSB.append ("a,\"PO Box 123,\nKippax,ACT. 2615.\nAustralia\",d.\n");
    // Test quoted quote chars
    aSB.append ("\"Glen \"\"The Man\"\" Smith\",Athlete,Developer\n");
    // """""","test" representing: "",test
    aSB.append ("\"\"\"\"\"\",\"test\"\n");
    aSB.append ("\"a\nb\",b,\"\nd\",e\n");
    try (final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSB.toString ())))
    {
      aReader.setStrictQuotes (true);

      // test normal case
      ICommonsList <String> nextLine = aReader.readNext ();
      assertEquals ("", nextLine.get (0));
      assertEquals ("", nextLine.get (1));
      assertEquals ("", nextLine.get (2));

      // test quoted commas
      nextLine = aReader.readNext ();
      assertEquals ("", nextLine.get (0));
      assertEquals ("b,b,b", nextLine.get (1));
      assertEquals ("", nextLine.get (2));

      // test empty elements
      nextLine = aReader.readNext ();
      assertEquals (3, nextLine.size ());

      // test multiline quoted
      nextLine = aReader.readNext ();
      assertEquals (3, nextLine.size ());

      // test quoted quote chars
      nextLine = aReader.readNext ();
      assertEquals ("Glen \"The Man\" Smith", nextLine.get (0));

      nextLine = aReader.readNext ();
      // check the tricky situation
      assertTrue (nextLine.get (0).equals ("\"\""));
      // make sure we didn't ruin the next field..
      assertTrue (nextLine.get (1).equals ("test"));
      nextLine = aReader.readNext ();
      assertEquals (4, nextLine.size ());
      assertEquals ("a\nb", nextLine.get (0));
      assertEquals ("", nextLine.get (1));
      assertEquals ("\nd", nextLine.get (2));
      assertEquals ("", nextLine.get (3));

      // test end of stream
      assertNull (aReader.readNext ());
    }
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
    try (final CSVReader aReader = _createCSVReader ())
    {
      assertEquals (7, aReader.readAll ().size ());
    }
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

    final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);
    aSB.append ("a\tb\tc").append ('\n'); // tab separated case
    aSB.append ("a\t'b\tb\tb'\tc").append ('\n'); // single quoted elements
    try (final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSB.toString ())))
    {
      aReader.setSeparatorChar ('\t').setQuoteChar ('\'');
      ICommonsList <String> nextLine = aReader.readNext ();
      assertEquals (3, nextLine.size ());

      nextLine = aReader.readNext ();
      assertEquals (3, nextLine.size ());
    }
  }

  @Test
  public void testParseQuotedStringWithDefinedSeperator () throws IOException
  {
    final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);
    aSB.append ("a\tb\tc").append ('\n'); // tab separated case

    try (final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSB.toString ())))
    {
      aReader.setSeparatorChar ('\t');
      final ICommonsList <String> nextLine = aReader.readNext ();
      assertEquals (3, nextLine.size ());
    }
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

    final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);
    // should skip this
    aSB.append ("Skip this line\t with tab").append ('\n');
    // and this
    aSB.append ("And this line too").append ('\n');
    // single quoted elements
    aSB.append ("a\t'b\tb\tb'\tc").append ('\n');
    try (final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSB.toString ())))
    {
      aReader.setSeparatorChar ('\t').setQuoteChar ('\'').setSkipLines (2);
      final ICommonsList <String> nextLine = aReader.readNext ();
      assertEquals (3, nextLine.size ());

      assertEquals ("a", nextLine.get (0));
    }
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

    final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);
    // should skip this
    aSB.append ("Skip this line?t with tab").append ('\n');
    // and this
    aSB.append ("And this line too").append ('\n');
    // single quoted elements
    aSB.append ("a\t'b\tb\tb'\t'aReader'").append ('\n');
    try (final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSB.toString ())))
    {
      aReader.setSeparatorChar ('\t').setQuoteChar ('\'').setEscapeChar ('?').setSkipLines (2);

      final ICommonsList <String> nextLine = aReader.readNext ();
      assertEquals (3, nextLine.size ());

      assertEquals ("a", nextLine.get (0));
      assertEquals ("aReader", nextLine.get (2));
    }
  }

  /**
   * Test a normal non quoted line with three elements
   *
   * @throws IOException
   *         never
   */
  @Test
  public void testNormalParsedLine () throws IOException
  {

    final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);

    aSB.append ("a,1234567,aReader").append ('\n');// a,1234,aReader

    try (final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSB.toString ())))
    {
      final ICommonsList <String> nextLine = aReader.readNext ();
      assertEquals (3, nextLine.size ());

      assertEquals ("a", nextLine.get (0));
      assertEquals ("1234567", nextLine.get (1));
      assertEquals ("aReader", nextLine.get (2));
    }
  }

  /**
   * Same as testADoubleQuoteAsDataElement but I changed the quotechar to a
   * single quote.
   *
   * @throws IOException
   *         never
   */
  @Test
  public void testASingleQuoteAsDataElement () throws IOException
  {
    final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);

    // a,',aReader
    aSB.append ("a,'''',aReader").append ('\n');

    try (final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSB.toString ())))
    {
      aReader.setQuoteChar ('\'');

      final ICommonsList <String> nextLine = aReader.readNext ();
      assertEquals (3, nextLine.size ());

      assertEquals ("a", nextLine.get (0));
      assertEquals (1, nextLine.get (1).length ());
      assertEquals ("\'", nextLine.get (1));
      assertEquals ("aReader", nextLine.get (2));
    }
  }

  /**
   * Same as testADoubleQuoteAsDataElement but I changed the quotechar to a
   * single quote. Also the middle field is empty.
   *
   * @throws IOException
   *         never
   */
  @Test
  public void testASingleQuoteAsDataElementWithEmptyField () throws IOException
  {
    final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);

    // a,,aReader
    aSB.append ("a,'',aReader").append ('\n');

    try (final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSB.toString ())))
    {
      aReader.setQuoteChar ('\'');
      final ICommonsList <String> nextLine = aReader.readNext ();
      assertEquals (3, nextLine.size ());

      assertEquals ("a", nextLine.get (0));
      assertEquals (0, nextLine.get (1).length ());
      assertEquals ("", nextLine.get (1));
      assertEquals ("aReader", nextLine.get (2));
    }
  }

  @Test
  public void testSpacesAtEndOfString () throws IOException
  {
    final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);

    aSB.append ("\"a\",\"b\",\"aReader\"   ");

    try (final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSB.toString ())))
    {
      aReader.setStrictQuotes (true);

      final ICommonsList <String> nextLine = aReader.readNext ();
      assertEquals (3, nextLine.size ());

      assertEquals ("a", nextLine.get (0));
      assertEquals ("b", nextLine.get (1));
      assertEquals ("aReader", nextLine.get (2));
    }
  }

  @Test
  public void testEscapedQuote () throws IOException
  {

    final StringBuilder aSB = new StringBuilder ();

    // a,123"4",aReader
    aSB.append ("a,\"123\\\"4567\",aReader").append ('\n');

    try (final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSB.toString ())))
    {
      final ICommonsList <String> nextLine = aReader.readNext ();
      assertEquals (3, nextLine.size ());

      assertEquals ("123\"4567", nextLine.get (1));
    }
  }

  @Test
  public void testEscapedEscape () throws IOException
  {

    final StringBuilder aSB = new StringBuilder ();

    // a,123"4",aReader
    aSB.append ("a,\"123\\\\4567\",aReader").append ('\n');

    try (final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSB.toString ())))
    {
      final ICommonsList <String> nextLine = aReader.readNext ();
      assertEquals (3, nextLine.size ());

      assertEquals ("123\\4567", nextLine.get (1));
    }
  }

  /**
   * Test a line where one of the elements is two single quotes and the quote
   * character is the default double quote. The expected result is two single
   * quotes.
   *
   * @throws IOException
   *         never
   */
  @Test
  public void testSingleQuoteWhenDoubleQuoteIsQuoteChar () throws IOException
  {
    final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);

    // a,'',aReader
    aSB.append ("a,'',aReader").append ('\n');

    try (final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSB.toString ())))
    {
      final ICommonsList <String> nextLine = aReader.readNext ();
      assertEquals (3, nextLine.size ());

      assertEquals ("a", nextLine.get (0));
      assertEquals (2, nextLine.get (1).length ());
      assertEquals ("''", nextLine.get (1));
      assertEquals ("aReader", nextLine.get (2));
    }
  }

  /**
   * Test a normal line with three elements and all elements are quoted
   *
   * @throws IOException
   *         never
   */
  @Test
  public void testQuotedParsedLine () throws IOException
  {
    final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);

    // "a","1234567","aReader"
    aSB.append ("\"a\",\"1234567\",\"aReader\"").append ('\n');

    try (final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSB.toString ())))
    {
      aReader.setStrictQuotes (true);

      final ICommonsList <String> nextLine = aReader.readNext ();
      assertEquals (3, nextLine.size ());

      assertEquals ("a", nextLine.get (0));
      assertEquals (1, nextLine.get (0).length ());

      assertEquals ("1234567", nextLine.get (1));
      assertEquals ("aReader", nextLine.get (2));
    }
  }

  @Test
  public void testBug106ParseLineWithCarriageReturnNewLineStrictQuotes () throws IOException
  {
    final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);

    // "a","123\r\n4567","aReader"
    aSB.append ("\"a\",\"123\r\n4567\",\"aReader\"").append ('\n');

    // public CSVReader(Reader reader, char separator, char quotechar, char
    // escape, int line, boolean strictQuotes,
    // boolean ignoreLeadingWhiteSpace, boolean keepCarriageReturn)
    try (final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSB.toString ()), true))
    {
      aReader.setStrictQuotes (true);

      final ICommonsList <String> nextLine = aReader.readNext ();
      assertEquals (3, nextLine.size ());

      assertEquals ("a", nextLine.get (0));
      assertEquals (1, nextLine.get (0).length ());

      assertEquals ("123\r\n4567", nextLine.get (1));
      assertEquals ("aReader", nextLine.get (2));
    }
  }

  @Test
  public void testIssue2992134OutOfPlaceQuotes () throws IOException
  {
    final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);

    aSB.append ("a,b,aReader,ddd\\\"eee\nf,g,h,\"iii,jjj\"");

    try (final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSB.toString ())))
    {
      final ICommonsList <String> nextLine = aReader.readNext ();

      assertEquals ("a", nextLine.get (0));
      assertEquals ("b", nextLine.get (1));
      assertEquals ("aReader", nextLine.get (2));
      assertEquals ("ddd\"eee", nextLine.get (3));
    }
  }

  @SuppressWarnings ("resource")
  @Test (expected = UnsupportedOperationException.class)
  public void testQuoteAndEscapeMustBeDifferent ()
  {
    final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);

    aSB.append ("a,b,aReader,ddd\\\"eee\nf,g,h,\"iii,jjj\"");

    new CSVReader (new NonBlockingStringReader (aSB.toString ())).setEscapeChar (CCSV.DEFAULT_QUOTE_CHARACTER);
  }

  @Test (expected = UnsupportedOperationException.class)
  @SuppressWarnings ("resource")
  public void testSeparatorAndEscapeMustBeDifferent ()
  {
    final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);

    aSB.append ("a,b,aReader,ddd\\\"eee\nf,g,h,\"iii,jjj\"");

    new CSVReader (new NonBlockingStringReader (aSB.toString ())).setEscapeChar (CCSV.DEFAULT_SEPARATOR);
  }

  @Test (expected = UnsupportedOperationException.class)
  @SuppressWarnings ("resource")
  public void testSeparatorAndQuoteMustBeDifferent ()
  {
    final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);

    aSB.append ("a,b,aReader,ddd\\\"eee\nf,g,h,\"iii,jjj\"");

    new CSVReader (new NonBlockingStringReader (aSB.toString ())).setQuoteChar (CCSV.DEFAULT_SEPARATOR);
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
    final ICommonsList <ICommonsList <String>> expectedResult = new CommonsArrayList <> ();
    expectedResult.add (CollectionHelper.newList ("a", "b", "aReader"));
    expectedResult.add (CollectionHelper.newList ("a", "b,b,b", "aReader"));
    expectedResult.add (CollectionHelper.newList ("", "", ""));
    expectedResult.add (CollectionHelper.newList ("a", "PO Box 123,\nKippax,ACT. 2615.\nAustralia", "d."));
    expectedResult.add (CollectionHelper.newList ("Glen \"The Man\" Smith", "Athlete", "Developer"));
    expectedResult.add (CollectionHelper.newList ("\"\"", "test"));
    expectedResult.add (CollectionHelper.newList ("a\nb", "b", "\nd", "e"));
    int idx = 0;
    try (final CSVReader aReader = _createCSVReader ())
    {
      for (final ICommonsList <String> line : aReader)
      {
        final ICommonsList <String> expectedLine = expectedResult.get (idx++);
        assertEquals (expectedLine, line);
      }
    }
  }

  @Test
  public void testCanCloseReader () throws IOException
  {
    _createCSVReader ().close ();
  }

  @Test
  public void testCanCreateIteratorFromReader () throws IOException
  {
    try (final CSVReader aReader = _createCSVReader ())
    {
      assertNotNull (aReader.iterator ());
    }
  }

  @Test
  public void testAttemptToReadCloseStreamReturnsNull () throws IOException
  {
    @SuppressWarnings ("resource")
    final NonBlockingBufferedReader aBufferedReader = new NonBlockingBufferedReader (new NonBlockingStringReader ("abc"));
    aBufferedReader.close ();
    try (final CSVReader aReader = new CSVReader (aBufferedReader))
    {
      assertNull (aReader.readNext ());
    }
  }

  @Test
  public void testIssue102 () throws IOException
  {
    try (final CSVReader aReader = new CSVReader (new NonBlockingStringReader ("\"\",a\n\"\",b\n")))
    {
      final ICommonsList <String> firstRow = aReader.readNext ();
      assertEquals (2, firstRow.size ());
      assertTrue (firstRow.get (0).isEmpty ());
      assertEquals ("a", firstRow.get (1));

      final ICommonsList <String> secondRow = aReader.readNext ();
      assertEquals (2, secondRow.size ());
      assertTrue (secondRow.get (0).isEmpty ());
      assertEquals ("b", secondRow.get (1));
    }
  }

  @Test
  public void testIssue108ReaderPlaysWellWithChannels () throws IOException
  {
    final byte [] bytes = "name\r\nvalue\r\n".getBytes ("UTF-8");
    final NonBlockingByteArrayInputStream bais = new NonBlockingByteArrayInputStream (bytes);
    final ReadableByteChannel ch = Channels.newChannel (bais);
    final InputStream in = Channels.newInputStream (ch);
    final InputStreamReader reader = new InputStreamReader (in, StandardCharsets.UTF_8);
    try (final CSVReader aReader = new CSVReader (reader))
    {
      aReader.setVerifyReader (false);
      assertEquals (2, aReader.readAll ().size ());
    }
  }
}
