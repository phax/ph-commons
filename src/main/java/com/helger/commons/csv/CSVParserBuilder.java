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

import javax.annotation.Nonnull;

/**
 * Builder for creating a CSVParser.<br/>
 * <code>
 * final CSVParser parser = new CSVParserBuilder()
 * .withSeparator('\t')
 * .withIgnoreQuotations(true)
 * .build();
 * </code>
 *
 * @see CSVParser
 */
public class CSVParserBuilder
{
  private char m_cSeparator = CCSV.DEFAULT_SEPARATOR;
  private char m_cQuoteChar = CCSV.DEFAULT_QUOTE_CHARACTER;
  private char m_cEscapeChar = CCSV.DEFAULT_ESCAPE_CHARACTER;
  private boolean m_bStrictQuotes = CCSV.DEFAULT_STRICT_QUOTES;
  private boolean m_bIgnoreLeadingWhiteSpace = CCSV.DEFAULT_IGNORE_LEADING_WHITESPACE;
  private boolean m_bIgnoreQuotations = CCSV.DEFAULT_IGNORE_QUOTATIONS;

  /**
   * Default constructor.
   */
  public CSVParserBuilder ()
  {}

  /**
   * Sets the delimiter to use for separating entries.
   *
   * @param cSeparator
   *        the delimiter to use for separating entries
   * @return The CSVParserBuilder
   */
  @Nonnull
  public CSVParserBuilder withSeparator (final char cSeparator)
  {
    m_cSeparator = cSeparator;
    return this;
  }

  /**
   * @return the defined separator.
   */
  public char getSeparator ()
  {
    return m_cSeparator;
  }

  /**
   * Sets the character to use for quoted elements.
   *
   * @param cQuoteChar
   *        the character to use for quoted element.
   * @return The CSVParserBuilder
   */
  @Nonnull
  public CSVParserBuilder withQuoteChar (final char cQuoteChar)
  {
    m_cQuoteChar = cQuoteChar;
    return this;
  }

  /**
   * Sets the character to use for escaping a separator or quote.
   *
   * @param cEscapeChar
   *        the character to use for escaping a separator or quote.
   * @return The CSVParserBuilder
   */
  @Nonnull
  public CSVParserBuilder withEscapeChar (final char cEscapeChar)
  {
    m_cEscapeChar = cEscapeChar;
    return this;
  }

  /**
   * Sets the strict quotes setting - if true, characters outside the quotes are
   * ignored.
   *
   * @param bStrictQuotes
   *        if true, characters outside the quotes are ignored
   * @return The CSVParserBuilder
   */
  @Nonnull
  public CSVParserBuilder withStrictQuotes (final boolean bStrictQuotes)
  {
    m_bStrictQuotes = bStrictQuotes;
    return this;
  }

  /**
   * Sets the ignore leading whitespace setting - if true, white space in front
   * of a quote in a field is ignored.
   *
   * @param bIgnoreLeadingWhiteSpace
   *        if true, white space in front of a quote in a field is ignored
   * @return The CSVParserBuilder
   */
  @Nonnull
  public CSVParserBuilder withIgnoreLeadingWhiteSpace (final boolean bIgnoreLeadingWhiteSpace)
  {
    m_bIgnoreLeadingWhiteSpace = bIgnoreLeadingWhiteSpace;
    return this;
  }

  /**
   * Sets the ignore quotations mode - if true, quotations are ignored.
   *
   * @param bIgnoreQuotations
   *        if true, quotations are ignored
   * @return The CSVParserBuilder
   */
  @Nonnull
  public CSVParserBuilder withIgnoreQuotations (final boolean bIgnoreQuotations)
  {
    m_bIgnoreQuotations = bIgnoreQuotations;
    return this;
  }

  /**
   * Constructs CSVParser.
   *
   * @return a new CSVParser with defined settings.
   */
  @Nonnull
  public CSVParser build ()
  {
    return new CSVParser (m_cSeparator,
                          m_cQuoteChar,
                          m_cEscapeChar,
                          m_bStrictQuotes,
                          m_bIgnoreLeadingWhiteSpace,
                          m_bIgnoreQuotations);
  }

  /**
   * @return the defined quotation character.
   */
  public char getQuoteChar ()
  {
    return m_cQuoteChar;
  }

  /**
   * @return the defined escape character.
   */
  public char getEscapeChar ()
  {
    return m_cEscapeChar;
  }

  /**
   * @return the defined strict quotation setting.
   */
  public boolean isStrictQuotes ()
  {
    return m_bStrictQuotes;
  }

  /**
   * @return the defined ignoreLeadingWhiteSpace setting.
   */
  public boolean isIgnoreLeadingWhiteSpace ()
  {
    return m_bIgnoreLeadingWhiteSpace;
  }

  /**
   * @return the defined ignoreQuotation setting.
   */
  public boolean isIgnoreQuotations ()
  {
    return m_bIgnoreQuotations;
  }
}
