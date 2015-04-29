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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for class {@link CSVParserBuilder}.
 *
 * @author OpenCSV
 */
public final class CSVParserBuilderTest
{
  private CSVParserBuilder m_aBuilder;

  @Before
  public void setUp () throws Exception
  {
    m_aBuilder = new CSVParserBuilder ();
  }

  @Test
  public void testDefaultBuilder ()
  {
    assertEquals (CCSV.DEFAULT_SEPARATOR, m_aBuilder.getSeparator ());
    assertEquals (CCSV.DEFAULT_QUOTE_CHARACTER, m_aBuilder.getQuoteChar ());
    assertEquals (CCSV.DEFAULT_ESCAPE_CHARACTER, m_aBuilder.getEscapeChar ());
    assertTrue (CCSV.DEFAULT_STRICT_QUOTES == m_aBuilder.isStrictQuotes ());
    assertTrue (CCSV.DEFAULT_IGNORE_LEADING_WHITESPACE == m_aBuilder.isIgnoreLeadingWhiteSpace ());
    assertTrue (CCSV.DEFAULT_IGNORE_QUOTATIONS == m_aBuilder.isIgnoreQuotations ());

    final CSVParser parser = m_aBuilder.build ();
    assertEquals (CCSV.DEFAULT_SEPARATOR, parser.getSeparator ());
    assertEquals (CCSV.DEFAULT_QUOTE_CHARACTER, parser.getQuotechar ());
    assertEquals (CCSV.DEFAULT_ESCAPE_CHARACTER, parser.getEscape ());
    assertTrue (CCSV.DEFAULT_STRICT_QUOTES == parser.isStrictQuotes ());
    assertTrue (CCSV.DEFAULT_IGNORE_LEADING_WHITESPACE == parser.isIgnoreLeadingWhiteSpace ());
    assertTrue (CCSV.DEFAULT_IGNORE_QUOTATIONS == parser.isIgnoreQuotations ());
  }

  @Test
  public void testWithSeparator ()
  {
    final char expected = '1';
    m_aBuilder.withSeparator (expected);
    assertEquals (expected, m_aBuilder.getSeparator ());
    assertEquals (expected, m_aBuilder.build ().getSeparator ());
  }

  @Test
  public void testWithQuoteChar ()
  {
    final char expected = '2';
    m_aBuilder.withQuoteChar (expected);
    assertEquals (expected, m_aBuilder.getQuoteChar ());
    assertEquals (expected, m_aBuilder.build ().getQuotechar ());
  }

  @Test
  public void testWithEscapeChar ()
  {
    final char expected = '3';
    m_aBuilder.withEscapeChar (expected);
    assertEquals (expected, m_aBuilder.getEscapeChar ());
    assertEquals (expected, m_aBuilder.build ().getEscape ());
  }

  @Test
  public void testWithStrictQuotes ()
  {
    m_aBuilder.withStrictQuotes (true);
    assertTrue (m_aBuilder.isStrictQuotes ());
    assertTrue (m_aBuilder.build ().isStrictQuotes ());
  }

  @Test
  public void testWithIgnoreLeadingWhiteSpace ()
  {
    m_aBuilder.withIgnoreLeadingWhiteSpace (true);
    assertTrue (m_aBuilder.isIgnoreLeadingWhiteSpace ());
    assertTrue (m_aBuilder.build ().isIgnoreLeadingWhiteSpace ());
  }

  @Test
  public void testWithIgnoreQuotations ()
  {
    m_aBuilder.withIgnoreQuotations (true);
    assertTrue (m_aBuilder.isIgnoreQuotations ());
    assertTrue (m_aBuilder.build ().isIgnoreQuotations ());
  }
}
