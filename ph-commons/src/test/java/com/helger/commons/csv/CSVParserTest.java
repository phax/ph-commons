/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.helger.commons.collection.impl.ICommonsList;

/**
 * Test class for class {@link CSVParser}.
 *
 * @author OpenCSV
 */
public final class CSVParserTest
{
  // \\1\2\"\
  private static final String ESCAPE_TEST_STRING = "\\\\1\\2\\\"3\\";

  private CSVParser m_aParser;

  @Before
  public void setUp ()
  {
    m_aParser = new CSVParser ();
  }

  @Test
  public void testParseLine () throws Exception
  {
    final ICommonsList <String> nextItem = m_aParser.parseLine ("This, is, a, test.");
    assertEquals (4, nextItem.size ());
    assertEquals ("This", nextItem.get (0));
    assertEquals (" is", nextItem.get (1));
    assertEquals (" a", nextItem.get (2));
    assertEquals (" test.", nextItem.get (3));
  }

  @Test
  public void testParseSimpleString () throws IOException
  {
    final ICommonsList <String> aNextLine = m_aParser.parseLine ("a,b,c");
    assertEquals (3, aNextLine.size ());
    assertEquals ("a", aNextLine.get (0));
    assertEquals ("b", aNextLine.get (1));
    assertEquals ("c", aNextLine.get (2));
    assertFalse (m_aParser.isPending ());
  }

  @Test
  public void testParseSimpleQuotedString () throws IOException
  {
    final ICommonsList <String> aNextLine = m_aParser.parseLine ("\"a\",\"b\",\"c\"");
    assertEquals (3, aNextLine.size ());
    assertEquals ("a", aNextLine.get (0));
    assertEquals ("b", aNextLine.get (1));
    assertEquals ("c", aNextLine.get (2));
    assertFalse (m_aParser.isPending ());
  }

  @Test
  public void testParseSimpleQuotedStringWithSpaces () throws IOException
  {
    final CSVParser parser = new CSVParser ().setStrictQuotes (true).setIgnoreLeadingWhiteSpace (false);

    final ICommonsList <String> aNextLine = parser.parseLine (" \"a\" , \"b\" , \"c\" ");
    assertEquals (3, aNextLine.size ());
    assertEquals ("a", aNextLine.get (0));
    assertEquals ("b", aNextLine.get (1));
    assertEquals ("c", aNextLine.get (2));
    assertFalse (parser.isPending ());
  }

  /**
   * Tests quotes in the middle of an element.
   *
   * @throws IOException
   *         if bad things happen
   */
  @Test
  public void testParsedLineWithInternalQuota () throws IOException
  {
    final ICommonsList <String> aNextLine = m_aParser.parseLine ("a,123\"4\"567,c");
    assertEquals (3, aNextLine.size ());

    assertEquals ("123\"4\"567", aNextLine.get (1));

  }

  @Test
  public void testParseQuotedStringWithCommas () throws IOException
  {
    final ICommonsList <String> aNextLine = m_aParser.parseLine ("a,\"b,b,b\",c");
    assertEquals ("a", aNextLine.get (0));
    assertEquals ("b,b,b", aNextLine.get (1));
    assertEquals ("c", aNextLine.get (2));
    assertEquals (3, aNextLine.size ());
  }

  @Test
  public void testParseQuotedStringWithDefinedSeperator () throws IOException
  {
    m_aParser = new CSVParser ().setSeparatorChar (':');

    final ICommonsList <String> aNextLine = m_aParser.parseLine ("a:\"b:b:b\":c");
    assertEquals ("a", aNextLine.get (0));
    assertEquals ("b:b:b", aNextLine.get (1));
    assertEquals ("c", aNextLine.get (2));
    assertEquals (3, aNextLine.size ());
  }

  @Test
  public void testParseQuotedStringWithDefinedSeperatorAndQuote () throws IOException
  {
    m_aParser = new CSVParser ().setSeparatorChar (':').setQuoteChar ('\'');

    final ICommonsList <String> aNextLine = m_aParser.parseLine ("a:'b:b:b':c");
    assertEquals ("a", aNextLine.get (0));
    assertEquals ("b:b:b", aNextLine.get (1));
    assertEquals ("c", aNextLine.get (2));
    assertEquals (3, aNextLine.size ());
  }

  @Test
  public void testParseEmptyElements () throws IOException
  {
    final ICommonsList <String> aNextLine = m_aParser.parseLine (",,");
    assertEquals (3, aNextLine.size ());
    assertEquals ("", aNextLine.get (0));
    assertEquals ("", aNextLine.get (1));
    assertEquals ("", aNextLine.get (2));
  }

  @Test
  public void testParseMultiLinedQuoted () throws IOException
  {
    final ICommonsList <String> aNextLine = m_aParser.parseLine ("a,\"PO Box 123,\nKippax,ACT. 2615.\nAustralia\",d.\n");
    assertEquals (3, aNextLine.size ());
    assertEquals ("a", aNextLine.get (0));
    assertEquals ("PO Box 123,\nKippax,ACT. 2615.\nAustralia", aNextLine.get (1));
    assertEquals ("d.\n", aNextLine.get (2));
  }

  @Test
  public void testParseMultiLinedQuotedwithCarriageReturns () throws IOException
  {
    final ICommonsList <String> aNextLine = m_aParser.parseLine ("a,\"PO Box 123,\r\nKippax,ACT. 2615.\r\nAustralia\",d.\n");
    assertEquals (3, aNextLine.size ());
    assertEquals ("a", aNextLine.get (0));
    assertEquals ("PO Box 123,\r\nKippax,ACT. 2615.\r\nAustralia", aNextLine.get (1));
    assertEquals ("d.\n", aNextLine.get (2));
  }

  @Test
  public void testADoubleQuoteAsDataElement () throws IOException
  {
    final ICommonsList <String> aNextLine = m_aParser.parseLine ("a,\"\"\"\",c");// a,"""",c

    assertEquals (3, aNextLine.size ());

    assertEquals ("a", aNextLine.get (0));
    assertEquals (1, aNextLine.get (1).length ());
    assertEquals ("\"", aNextLine.get (1));
    assertEquals ("c", aNextLine.get (2));

  }

  @Test
  public void testEscapedDoubleQuoteAsDataElement () throws IOException
  {

    final ICommonsList <String> aNextLine = m_aParser.parseLine ("\"test\",\"this,test,is,good\",\"\\\"test\\\"\",\"\\\"quote\\\"\""); // "test","this,test,is,good","\"test\",\"quote\""

    assertEquals (4, aNextLine.size ());

    assertEquals ("test", aNextLine.get (0));
    assertEquals ("this,test,is,good", aNextLine.get (1));
    assertEquals ("\"test\"", aNextLine.get (2));
    assertEquals ("\"quote\"", aNextLine.get (3));

  }

  @Test
  public void testParseQuotedQuoteCharacters () throws IOException
  {
    final ICommonsList <String> aNextLine = m_aParser.parseLineMulti ("\"Glen \"\"The Man\"\" Smith\",Athlete,Developer\n");
    assertEquals (3, aNextLine.size ());
    assertEquals ("Glen \"The Man\" Smith", aNextLine.get (0));
    assertEquals ("Athlete", aNextLine.get (1));
    assertEquals ("Developer\n", aNextLine.get (2));
  }

  @Test
  public void testParseMultipleQuotes () throws IOException
  {
    final ICommonsList <String> aNextLine = m_aParser.parseLine ("\"\"\"\"\"\",\"test\"\n"); // """""","test"
    // representing:
    // "",
    // test
    assertEquals ("\"\"", aNextLine.get (0)); // check the tricky situation
    assertEquals ("test\"\n", aNextLine.get (1)); // make sure we didn't ruin
                                                  // the
                                                  // next
    // field..
    assertEquals (2, aNextLine.size ());
  }

  @Test
  public void testParseTrickyString () throws IOException
  {
    final ICommonsList <String> aNextLine = m_aParser.parseLine ("\"a\nb\",b,\"\nd\",e\n");
    assertEquals (4, aNextLine.size ());
    assertEquals ("a\nb", aNextLine.get (0));
    assertEquals ("b", aNextLine.get (1));
    assertEquals ("\nd", aNextLine.get (2));
    assertEquals ("e\n", aNextLine.get (3));
  }

  private String _setUpMultiLineInsideQuotes ()
  {
    final StringBuilder sb = new StringBuilder (CCSV.INITIAL_STRING_SIZE);

    sb.append ("Small test,\"This is a test across \ntwo lines.\"");

    return sb.toString ();
  }

  @Test
  public void testAMultiLineInsideQuotes () throws IOException
  {

    final String testString = _setUpMultiLineInsideQuotes ();

    final ICommonsList <String> aNextLine = m_aParser.parseLine (testString);
    assertEquals (2, aNextLine.size ());
    assertEquals ("Small test", aNextLine.get (0));
    assertEquals ("This is a test across \ntwo lines.", aNextLine.get (1));
    assertFalse (m_aParser.isPending ());
  }

  @Test
  public void testStrictQuoteSimple () throws IOException
  {
    m_aParser = new CSVParser ().setStrictQuotes (true);
    final String testString = "\"a\",\"b\",\"c\"";

    final ICommonsList <String> aNextLine = m_aParser.parseLine (testString);
    assertEquals (3, aNextLine.size ());
    assertEquals ("a", aNextLine.get (0));
    assertEquals ("b", aNextLine.get (1));
    assertEquals ("c", aNextLine.get (2));
  }

  @Test
  public void testNotStrictQuoteSimple () throws IOException
  {
    m_aParser = new CSVParser ().setStrictQuotes (false);
    final String testString = "\"a\",\"b\",\"c\"";

    final ICommonsList <String> aNextLine = m_aParser.parseLine (testString);
    assertEquals (3, aNextLine.size ());
    assertEquals ("a", aNextLine.get (0));
    assertEquals ("b", aNextLine.get (1));
    assertEquals ("c", aNextLine.get (2));
  }

  @Test
  public void testStrictQuoteWithSpacesAndTabs () throws IOException
  {
    m_aParser = new CSVParser ().setStrictQuotes (true);
    final String testString = " \t      \"a\",\"b\"      \t       ,   \"c\"   ";

    final ICommonsList <String> aNextLine = m_aParser.parseLine (testString);
    assertEquals (3, aNextLine.size ());
    assertEquals ("a", aNextLine.get (0));
    assertEquals ("b", aNextLine.get (1));
    assertEquals ("c", aNextLine.get (2));
  }

  /**
   * Shows that without the strict quotes opencsv will read until the separator
   * or the end of the line.
   *
   * @throws IOException
   *         never
   */
  @Test
  public void testNotStrictQuoteWithSpacesAndTabs () throws IOException
  {
    m_aParser = new CSVParser ().setStrictQuotes (false);
    final String testString = " \t      \"a\",\"b\"      \t       ,   \"c\"   ";

    final ICommonsList <String> aNextLine = m_aParser.parseLine (testString);
    assertEquals (3, aNextLine.size ());
    assertEquals ("a", aNextLine.get (0));
    assertEquals ("b\"      \t       ", aNextLine.get (1));
    assertEquals ("c\"   ", aNextLine.get (2));
  }

  @Test
  public void testStrictQuoteWithGarbage () throws IOException
  {
    m_aParser = new CSVParser ().setStrictQuotes (true);
    final String testString = "abc',!@#\",\\\"\"   xyz,";

    final ICommonsList <String> aNextLine = m_aParser.parseLine (testString);
    assertEquals (3, aNextLine.size ());
    assertEquals ("", aNextLine.get (0));
    assertEquals (",\"", aNextLine.get (1));
    assertEquals ("", aNextLine.get (2));
  }

  @Test
  public void testCanIgnoreQuotations () throws IOException
  {
    m_aParser = new CSVParser ().setIgnoreQuotations (true);
    final String testString = "Bob,test\",Beaumont,TX";

    final ICommonsList <String> aNextLine = m_aParser.parseLine (testString);
    assertEquals (4, aNextLine.size ());
    assertEquals ("Bob", aNextLine.get (0));
    assertEquals ("test", aNextLine.get (1));
    assertEquals ("Beaumont", aNextLine.get (2));
    assertEquals ("TX", aNextLine.get (3));
  }

  @Test
  public void testFalseIgnoreQuotations ()
  {
    m_aParser = new CSVParser ().setIgnoreQuotations (false);
    final String testString = "Bob,test\",Beaumont,TX";

    try
    {
      m_aParser.parseLine (testString);
      fail ();
    }
    catch (final IOException ex)
    {
      // expected
    }
  }

  /**
   * This is an interesting issue where the data does not use quotes but IS
   * using a quote within the field as a inch symbol. So we want to keep that
   * quote as part of the field and not as the start or end of a field.
   * <p/>
   * Test data is as follows.
   * <p/>
   * RPO;2012;P; ; ; ;SDX;ACCESSORY WHEEL, 16", ALUMINUM, DESIGN 1 RPO;2012;P; ;
   * ; ;SDZ;ACCESSORY WHEEL - 17" - ALLOY - DESIGN 1
   *
   * @throws IOException
   *         never
   */
  @Test
  public void testIssue3314579 () throws IOException
  {
    m_aParser = new CSVParser ().setSeparatorChar (';').setIgnoreQuotations (true);
    final String testString = "RPO;2012;P; ; ; ;SDX;ACCESSORY WHEEL, 16\", ALUMINUM, DESIGN 1";

    final ICommonsList <String> aNextLine = m_aParser.parseLine (testString);
    assertEquals (8, aNextLine.size ());
    assertEquals ("RPO", aNextLine.get (0));
    assertEquals ("2012", aNextLine.get (1));
    assertEquals ("P", aNextLine.get (2));
    assertEquals (" ", aNextLine.get (3));
    assertEquals (" ", aNextLine.get (4));
    assertEquals (" ", aNextLine.get (5));
    assertEquals ("SDX", aNextLine.get (6));
    assertEquals ("ACCESSORY WHEEL, 16\", ALUMINUM, DESIGN 1", aNextLine.get (7));
  }

  /**
   * Test issue 2263439 where an escaped quote was causing the parse to fail.
   * <p/>
   * Special thanks to Chris Morris for fixing this (id 1979054)
   *
   * @throws IOException
   *         never
   */
  @Test
  public void testIssue2263439 () throws IOException
  {
    m_aParser = new CSVParser ().setQuoteChar ('\'');

    final ICommonsList <String> aNextLine = m_aParser.parseLine ("865,0,'AmeriKKKa\\'s_Most_Wanted','',294,0,0,0.734338696798625,'20081002052147',242429208,18448");

    assertEquals (11, aNextLine.size ());

    assertEquals ("865", aNextLine.get (0));
    assertEquals ("0", aNextLine.get (1));
    assertEquals ("AmeriKKKa's_Most_Wanted", aNextLine.get (2));
    assertEquals ("", aNextLine.get (3));
    assertEquals ("18448", aNextLine.get (10));
  }

  /**
   * Test issue 2859181 where an escaped character before a character that did
   * not need escaping was causing the parse to fail.
   *
   * @throws IOException
   *         never
   */
  @Test
  public void testIssue2859181 () throws IOException
  {
    m_aParser = new CSVParser ().setSeparatorChar (';');
    final ICommonsList <String> aNextLine = m_aParser.parseLine ("field1;\\=field2;\"\"\"field3\"\"\""); // field1;\=field2;"""field3"""

    assertEquals (3, aNextLine.size ());

    assertEquals ("field1", aNextLine.get (0));
    assertEquals ("=field2", aNextLine.get (1));
    assertEquals ("\"field3\"", aNextLine.get (2));
  }

  /**
   * Test issue 2726363
   * <p/>
   * Data given:
   * <p/>
   * "804503689","London",""London""shop","address","116.453182","39.918884"
   * "453074125","NewYork","brief","address"","121.514683","31.228511"
   *
   * @throws IOException
   *         never
   */
  @Test
  public void testIssue2726363 () throws IOException
  {
    final ICommonsList <String> aNextLine = m_aParser.parseLine ("\"804503689\",\"London\",\"\"London\"shop\",\"address\",\"116.453182\",\"39.918884\"");

    assertEquals (6, aNextLine.size ());

    assertEquals ("804503689", aNextLine.get (0));
    assertEquals ("London", aNextLine.get (1));
    assertEquals ("\"London\"shop", aNextLine.get (2));
    assertEquals ("address", aNextLine.get (3));
    assertEquals ("116.453182", aNextLine.get (4));
    assertEquals ("39.918884", aNextLine.get (5));
  }

  @Test (expected = IOException.class)
  public void testAnIOExceptionThrownifStringEndsInsideAQuotedString () throws IOException
  {
    m_aParser.parseLine ("This,is a \"bad line to parse.");
  }

  @Test
  public void testParseLineMultiAllowsQuotesAcrossMultipleLines () throws IOException
  {
    ICommonsList <String> aNextLine = m_aParser.parseLineMulti ("This,\"is a \"good\" line\\\\ to parse");

    assertEquals (1, aNextLine.size ());
    assertEquals ("This", aNextLine.get (0));
    assertTrue (m_aParser.isPending ());

    aNextLine = m_aParser.parseLineMulti ("because we are using parseLineMulti.\"");

    assertEquals (1, aNextLine.size ());
    assertEquals ("is a \"good\" line\\ to parse\nbecause we are using parseLineMulti.", aNextLine.get (0));
    assertFalse (m_aParser.isPending ());
  }

  @Test
  public void testPendingIsClearedAfterCallToParseLine () throws IOException
  {
    ICommonsList <String> aNextLine = m_aParser.parseLineMulti ("This,\"is a \"good\" line\\\\ to parse");

    assertEquals (1, aNextLine.size ());
    assertEquals ("This", aNextLine.get (0));
    assertTrue (m_aParser.isPending ());

    aNextLine = m_aParser.parseLine ("because we are using parseLineMulti.");

    assertEquals (1, aNextLine.size ());
    assertEquals ("because we are using parseLineMulti.", aNextLine.get (0));
    assertFalse (m_aParser.isPending ());
  }

  @Test
  public void testReturnPendingIfNullIsPassedIntoParseLineMulti () throws IOException
  {
    ICommonsList <String> aNextLine = m_aParser.parseLineMulti ("This,\"is a \"goo\\d\" line\\\\ to parse\\");

    assertEquals (1, aNextLine.size ());
    assertEquals ("This", aNextLine.get (0));
    assertTrue (m_aParser.isPending ());

    aNextLine = m_aParser.parseLineMulti (null);

    assertEquals (1, aNextLine.size ());
    assertEquals ("is a \"good\" line\\ to parse\n", aNextLine.get (0));
    assertFalse (m_aParser.isPending ());
  }

  @Test
  public void testSpacesAtEndOfQuotedStringDoNotCountIfStrictQuotesIsTrue () throws IOException
  {
    final CSVParser parser = new CSVParser ().setStrictQuotes (true);
    final ICommonsList <String> aNextLine = parser.parseLine ("\"Line with\", \"spaces at end\"  ");

    assertEquals (2, aNextLine.size ());
    assertEquals ("Line with", aNextLine.get (0));
    assertEquals ("spaces at end", aNextLine.get (1));
  }

  @Test
  public void testReturnNullWhenNullPassedIn () throws IOException
  {
    final ICommonsList <String> aNextLine = m_aParser.parseLine (null);
    assertNull (aNextLine);
  }

  @Test
  public void testValidateEscapeStringBeforeRealTest ()
  {
    assertEquals (9, ESCAPE_TEST_STRING.length ());
  }

  @Test
  public void testWhichCharactersAreEscapable ()
  {
    assertTrue (m_aParser.isNextCharacterEscapable (ESCAPE_TEST_STRING, true, 0));
    assertFalse (m_aParser.isNextCharacterEscapable (ESCAPE_TEST_STRING, false, 0));
    // Second character is not escapable because there is a non quote or non
    // slash after it.
    assertFalse (m_aParser.isNextCharacterEscapable (ESCAPE_TEST_STRING, true, 1));
    assertFalse (m_aParser.isNextCharacterEscapable (ESCAPE_TEST_STRING, false, 1));
    // Fourth character is not escapable because there is a non quote or non
    // slash after it.
    assertFalse (m_aParser.isNextCharacterEscapable (ESCAPE_TEST_STRING, true, 3));
    assertFalse (m_aParser.isNextCharacterEscapable (ESCAPE_TEST_STRING, false, 3));

    assertTrue (m_aParser.isNextCharacterEscapable (ESCAPE_TEST_STRING, true, 5));
    assertFalse (m_aParser.isNextCharacterEscapable (ESCAPE_TEST_STRING, false, 5));

    final int lastChar = ESCAPE_TEST_STRING.length () - 1;
    assertFalse (m_aParser.isNextCharacterEscapable (ESCAPE_TEST_STRING, true, lastChar));
    assertFalse (m_aParser.isNextCharacterEscapable (ESCAPE_TEST_STRING, false, lastChar));
  }

  @Test
  public void testWhitespaceBeforeEscape () throws IOException
  {
    final ICommonsList <String> nextItem = m_aParser.parseLine ("\"this\", \"is\",\"a test\""); // "this",
    // "is","a test"
    assertEquals ("this", nextItem.get (0));
    assertEquals ("is", nextItem.get (1));
    assertEquals ("a test", nextItem.get (2));
  }

  @Test
  public void testIssue2958242WithoutQuotes () throws IOException
  {
    final CSVParser testParser = new CSVParser ().setSeparatorChar ('\t');
    final ICommonsList <String> nextItem = testParser.parseLine ("zo\"\"har\"\"at\t10-04-1980\t29\tC:\\\\foo.txt");
    assertEquals (4, nextItem.size ());
    assertEquals ("zo\"har\"at", nextItem.get (0));
    assertEquals ("10-04-1980", nextItem.get (1));
    assertEquals ("29", nextItem.get (2));
    assertEquals ("C:\\foo.txt", nextItem.get (3));
  }

  @Test (expected = UnsupportedOperationException.class)
  public void testQuoteAndEscapeCannotBeTheSame ()
  {
    new CSVParser ().setQuoteChar (CCSV.DEFAULT_QUOTE_CHARACTER).setEscapeChar (CCSV.DEFAULT_QUOTE_CHARACTER);
  }

  @Test
  public void testQuoteAndEscapeCanBeTheSameIfNull ()
  {
    new CSVParser ().setQuoteChar (CCSV.NULL_CHARACTER).setEscapeChar (CCSV.NULL_CHARACTER);
  }

  @Test (expected = UnsupportedOperationException.class)
  public void testSeparatorCharacterCannotBeNull ()
  {
    new CSVParser ().setSeparatorChar (CCSV.NULL_CHARACTER);
  }

  @Test (expected = UnsupportedOperationException.class)
  public void testSeparatorAndEscapeCannotBeTheSame ()
  {
    new CSVParser ().setQuoteChar (CCSV.DEFAULT_QUOTE_CHARACTER).setEscapeChar (CCSV.DEFAULT_SEPARATOR);
  }

  @Test (expected = UnsupportedOperationException.class)
  public void testSeparatorAndQuoteCannotBeTheSame ()
  {
    new CSVParser ().setQuoteChar (CCSV.DEFAULT_SEPARATOR);
  }

  @Test
  public void testParserHandlesNullInString () throws IOException
  {
    final ICommonsList <String> aNextLine = m_aParser.parseLine ("because we are using\0 parseLineMulti.");

    assertEquals (1, aNextLine.size ());
    assertEquals ("because we are using\0 parseLineMulti.", aNextLine.get (0));
  }
}
