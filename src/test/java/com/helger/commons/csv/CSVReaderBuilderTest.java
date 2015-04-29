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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.Reader;

import org.junit.Before;
import org.junit.Test;

import com.helger.commons.io.streams.NonBlockingStringReader;

/**
 * Test class for class {@link CSVReaderBuilder}.
 * 
 * @author OpenCSV
 */
public final class CSVReaderBuilderTest
{
  private CSVReaderBuilder m_aBuilder;
  private Reader m_aReader;

  @Before
  public void setUp () throws Exception
  {
    m_aReader = new NonBlockingStringReader ("blafoo");
    m_aBuilder = new CSVReaderBuilder (m_aReader);
  }

  @Test
  public void testDefaultBuilder ()
  {
    assertSame (m_aReader, m_aBuilder.getReader ());
    assertNull (m_aBuilder.getCsvParser ());
    assertEquals (CCSV.DEFAULT_SKIP_LINES, m_aBuilder.getSkipLines ());

    final CSVReader csvReader = m_aBuilder.build ();
    assertEquals (CCSV.DEFAULT_SKIP_LINES, csvReader.getSkipLines ());
    assertTrue (CCSV.DEFAULT_KEEP_CR == csvReader.isKeepCarriageReturns ());
    assertTrue (CCSV.DEFAULT_VERIFY_READER == csvReader.isVerifyReader ());
  }

  @Test (expected = NullPointerException.class)
  public void testNullReader ()
  {
    m_aBuilder = new CSVReaderBuilder (null);
  }

  @Test
  public void testWithCSVParserNull ()
  {
    m_aBuilder.withCSVParser (new CSVParser ());
    m_aBuilder.withCSVParser (null);
    assertNull (m_aBuilder.getCsvParser ());
  }

  @Test
  public void testWithCSVParser ()
  {
    final CSVParser csvParser = new CSVParser ();
    m_aBuilder.withCSVParser (csvParser);
    assertSame (csvParser, m_aBuilder.getCsvParser ());

    final CSVReader aCSVReader = m_aBuilder.build ();
    assertSame (csvParser, aCSVReader.getParser ());
  }

  @Test
  public void testWithSkipLines ()
  {
    m_aBuilder.withSkipLines (99);
    assertEquals (99, m_aBuilder.getSkipLines ());

    final CSVReader aCSVReader = m_aBuilder.build ();
    assertEquals (99, aCSVReader.getSkipLines ());
  }

  @Test
  public void testWithKeepCR ()
  {
    m_aBuilder.withKeepCarriageReturn (true);
    assertTrue (m_aBuilder.keepCarriageReturn ());

    final CSVReader aCSVReader = m_aBuilder.build ();
    assertTrue (aCSVReader.isKeepCarriageReturns ());
  }

  @Test
  public void testWithSkipLinesZero ()
  {
    m_aBuilder.withSkipLines (0);

    assertEquals (0, m_aBuilder.getSkipLines ());

    final CSVReader aCSVReader = m_aBuilder.build ();
    assertEquals (0, aCSVReader.getSkipLines ());
  }

  @Test (expected = IllegalArgumentException.class)
  public void testWithSkipLinesNegative ()
  {
    m_aBuilder.withSkipLines (-1);
  }

  @Test
  public void testWithVerifyReader ()
  {
    final CSVReader aCSVReader = m_aBuilder.withVerifyReader (false).build ();
    assertFalse (aCSVReader.isVerifyReader ());
  }
}
