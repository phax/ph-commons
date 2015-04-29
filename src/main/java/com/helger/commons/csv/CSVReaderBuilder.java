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
package com.helger.commons.csv;

import java.io.Reader;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;

/**
 * Builder for creating a CSVReader. This should be the preferred method of
 * creating a Reader as there are so many possible values to be set it is
 * impossible to have constructors for all of them and keep backwards
 * compatibility with previous constructors. <code>
 * final CSVParser parser =
 * new CSVParserBuilder()
 * .withSeparator('\t')
 * .withIgnoreQuotations(true)
 * .build();
 * final CSVReader reader =
 * new CSVReaderBuilder(new StringReader(csv))
 * .withSkipLines(1)
 * .withCSVParser(parser)
 * .build();
 * </code>
 *
 * @see CSVReader
 */
public class CSVReaderBuilder
{
  private final Reader m_aReader;
  private int m_nSkipLines = CCSV.DEFAULT_SKIP_LINES;
  private CSVParser m_aParser;
  private boolean m_bKeepCR;
  private boolean m_bVerifyReader = CCSV.DEFAULT_VERIFY_READER;

  /**
   * Sets the reader to an underlying CSV source.
   *
   * @param aReader
   *        the reader to an underlying CSV source.
   */
  public CSVReaderBuilder (@Nonnull final Reader aReader)
  {
    ValueEnforcer.notNull (aReader, "Reader");
    m_aReader = aReader;
  }

  /**
   * Used by unit tests.
   *
   * @return the reader.
   */
  @Nonnull
  protected Reader getReader ()
  {
    return m_aReader;
  }

  /**
   * used by unit tests.
   *
   * @return The set number of lines to skip
   */
  @Nonnegative
  protected int getSkipLines ()
  {
    return m_nSkipLines;
  }

  /**
   * used by unit tests.
   *
   * @return the csvParser used by the builder.
   */
  @Nullable
  protected CSVParser getCsvParser ()
  {
    return m_aParser;
  }

  /**
   * Sets the line number to skip for start reading.
   *
   * @param nSkipLines
   *        the line number to skip for start reading.
   * @return the CSVReaderBuilder with skipLines set.
   */
  @Nonnull
  public CSVReaderBuilder withSkipLines (@Nonnegative final int nSkipLines)
  {
    ValueEnforcer.isGE0 (nSkipLines, "SkipLines");
    m_nSkipLines = nSkipLines;
    return this;
  }

  /**
   * Sets the parser to use to parse the input.
   *
   * @param csvParser
   *        the parser to use to parse the input.
   * @return the CSVReaderBuilder with the CSVParser set.
   */
  @Nonnull
  public CSVReaderBuilder withCSVParser (@Nullable final CSVParser csvParser)
  {
    m_aParser = csvParser;
    return this;
  }

  /**
   * Creates the CSVParser.
   *
   * @return the CSVParser based on the set criteria.
   */
  @Nonnull
  public CSVReader build ()
  {
    final CSVParser aParser = m_aParser != null ? m_aParser : new CSVParser ();
    return new CSVReader (m_aReader, m_nSkipLines, aParser, m_bKeepCR, m_bVerifyReader);
  }

  /**
   * Sets if the reader will keep or discard carriage returns.
   *
   * @param bKeepCR
   *        - true to keep carriage returns, false to discard.
   * @return the CSVParser based on the set criteria.
   */
  @Nonnull
  public CSVReaderBuilder withKeepCarriageReturn (final boolean bKeepCR)
  {
    m_bKeepCR = bKeepCR;
    return this;
  }

  /**
   * Returns if the reader built will keep or discard carriage returns.
   *
   * @return true if the reader built will keep carriage returns, false
   *         otherwise.
   */
  protected boolean keepCarriageReturn ()
  {
    return m_bKeepCR;
  }

  /**
   * Checks to see if the CSVReader should verify the reader state before reads
   * or not. This should be set to false if you are using some form of
   * asynchronous reader (like readers created by the java.nio.* classes). The
   * default value is true.
   *
   * @param bVerifyReader
   *        true if CSVReader should verify reader before each read, false
   *        otherwise.
   * @return The CSVParser based on this criteria.
   */
  @Nonnull
  public CSVReaderBuilder withVerifyReader (final boolean bVerifyReader)
  {
    m_bVerifyReader = bVerifyReader;
    return this;
  }
}
