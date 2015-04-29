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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.io.streams.NonBlockingBufferedReader;
import com.helger.commons.io.streams.StreamUtils;

/**
 * A very simple CSV reader released under a commercial-friendly license.
 *
 * @author Glen Smith
 */
public class CSVReader implements Closeable, Iterable <List <String>>
{
  private final Reader m_aReader;
  private final ICSVLineReader m_aLineReader;
  private final CSVParser m_aParser;
  private int m_nSkipLines = CCSV.DEFAULT_SKIP_LINES;
  private boolean m_bHasNext = true;
  private boolean m_bLinesSkiped = false;
  private boolean m_bKeepCR = CCSV.DEFAULT_KEEP_CR;
  private boolean m_bVerifyReader = CCSV.DEFAULT_VERIFY_READER;

  /**
   * Constructs CSVReader using a comma for the separator.
   *
   * @param reader
   *        the reader to an underlying CSV source.
   */
  public CSVReader (final Reader reader)
  {
    this (reader, CCSV.DEFAULT_SEPARATOR, CCSV.DEFAULT_QUOTE_CHARACTER, CCSV.DEFAULT_ESCAPE_CHARACTER);
  }

  /**
   * Constructs CSVReader with supplied separator.
   *
   * @param reader
   *        the reader to an underlying CSV source.
   * @param separator
   *        the delimiter to use for separating entries.
   */
  public CSVReader (final Reader reader, final char separator)
  {
    this (reader, separator, CCSV.DEFAULT_QUOTE_CHARACTER, CCSV.DEFAULT_ESCAPE_CHARACTER);
  }

  /**
   * Constructs CSVReader with supplied separator and quote char.
   *
   * @param reader
   *        the reader to an underlying CSV source.
   * @param separator
   *        the delimiter to use for separating entries
   * @param quotechar
   *        the character to use for quoted elements
   */
  public CSVReader (final Reader reader, final char separator, final char quotechar)
  {
    this (reader,
          separator,
          quotechar,
          CCSV.DEFAULT_ESCAPE_CHARACTER,
          CCSV.DEFAULT_SKIP_LINES,
          CCSV.DEFAULT_STRICT_QUOTES);
  }

  /**
   * Constructs CSVReader with supplied separator, quote char and quote handling
   * behavior.
   *
   * @param reader
   *        the reader to an underlying CSV source.
   * @param separator
   *        the delimiter to use for separating entries
   * @param quotechar
   *        the character to use for quoted elements
   * @param strictQuotes
   *        sets if characters outside the quotes are ignored
   */
  public CSVReader (final Reader reader, final char separator, final char quotechar, final boolean strictQuotes)
  {
    this (reader, separator, quotechar, CCSV.DEFAULT_ESCAPE_CHARACTER, CCSV.DEFAULT_SKIP_LINES, strictQuotes);
  }

  /**
   * Constructs CSVReader.
   *
   * @param reader
   *        the reader to an underlying CSV source.
   * @param separator
   *        the delimiter to use for separating entries
   * @param quotechar
   *        the character to use for quoted elements
   * @param escape
   *        the character to use for escaping a separator or quote
   */

  public CSVReader (final Reader reader, final char separator, final char quotechar, final char escape)
  {
    this (reader, separator, quotechar, escape, CCSV.DEFAULT_SKIP_LINES, CCSV.DEFAULT_STRICT_QUOTES);
  }

  /**
   * Constructs CSVReader.
   *
   * @param reader
   *        the reader to an underlying CSV source.
   * @param separator
   *        the delimiter to use for separating entries
   * @param quotechar
   *        the character to use for quoted elements
   * @param line
   *        the line number to skip for start reading
   */
  public CSVReader (final Reader reader, final char separator, final char quotechar, final int line)
  {
    this (reader, separator, quotechar, CCSV.DEFAULT_ESCAPE_CHARACTER, line, CCSV.DEFAULT_STRICT_QUOTES);
  }

  /**
   * Constructs CSVReader.
   *
   * @param reader
   *        the reader to an underlying CSV source.
   * @param separator
   *        the delimiter to use for separating entries
   * @param quotechar
   *        the character to use for quoted elements
   * @param escape
   *        the character to use for escaping a separator or quote
   * @param line
   *        the line number to skip for start reading
   */
  public CSVReader (final Reader reader, final char separator, final char quotechar, final char escape, final int line)
  {
    this (reader, separator, quotechar, escape, line, CCSV.DEFAULT_STRICT_QUOTES);
  }

  /**
   * Constructs CSVReader.
   *
   * @param reader
   *        the reader to an underlying CSV source.
   * @param separator
   *        the delimiter to use for separating entries
   * @param quotechar
   *        the character to use for quoted elements
   * @param escape
   *        the character to use for escaping a separator or quote
   * @param line
   *        the line number to skip for start reading
   * @param strictQuotes
   *        sets if characters outside the quotes are ignored
   */
  public CSVReader (final Reader reader,
                    final char separator,
                    final char quotechar,
                    final char escape,
                    final int line,
                    final boolean strictQuotes)
  {
    this (reader, separator, quotechar, escape, line, strictQuotes, CCSV.DEFAULT_IGNORE_LEADING_WHITESPACE);
  }

  /**
   * Constructs CSVReader with all data entered.
   *
   * @param reader
   *        the reader to an underlying CSV source.
   * @param separator
   *        the delimiter to use for separating entries
   * @param quotechar
   *        the character to use for quoted elements
   * @param escape
   *        the character to use for escaping a separator or quote
   * @param line
   *        the line number to skip for start reading
   * @param strictQuotes
   *        sets if characters outside the quotes are ignored
   * @param ignoreLeadingWhiteSpace
   *        it true, parser should ignore white space before a quote in a field
   */
  public CSVReader (final Reader reader,
                    final char separator,
                    final char quotechar,
                    final char escape,
                    final int line,
                    final boolean strictQuotes,
                    final boolean ignoreLeadingWhiteSpace)
  {
    this (reader, line, new CSVParser ().setSeparator (separator)
                                        .setQuoteChar (quotechar)
                                        .setEscapeChar (escape)
                                        .setStrictQuotes (strictQuotes)
                                        .setIgnoreLeadingWhiteSpace (ignoreLeadingWhiteSpace));
  }

  /**
   * Constructs CSVReader with all data entered.
   *
   * @param aReader
   *        the reader to an underlying CSV source.
   * @param separator
   *        the delimiter to use for separating entries
   * @param quotechar
   *        the character to use for quoted elements
   * @param escape
   *        the character to use for escaping a separator or quote
   * @param line
   *        the line number to skip for start reading
   * @param strictQuotes
   *        sets if characters outside the quotes are ignored
   * @param ignoreLeadingWhiteSpace
   *        if true, parser should ignore white space before a quote in a field
   * @param keepCR
   *        if true the reader will keep carriage returns, otherwise it will
   *        discard them.
   */
  public CSVReader (final Reader aReader,
                    final char separator,
                    final char quotechar,
                    final char escape,
                    final int line,
                    final boolean strictQuotes,
                    final boolean ignoreLeadingWhiteSpace,
                    final boolean keepCR)
  {
    this (aReader,
          line,
          new CSVParser ().setSeparator (separator)
                          .setQuoteChar (quotechar)
                          .setEscapeChar (escape)
                          .setStrictQuotes (strictQuotes)
                          .setIgnoreLeadingWhiteSpace (ignoreLeadingWhiteSpace),
          keepCR,
          CCSV.DEFAULT_VERIFY_READER);
  }

  /**
   * Constructs CSVReader with supplied CSVParser.
   *
   * @param reader
   *        the reader to an underlying CSV source.
   * @param line
   *        the line number to skip for start reading
   * @param csvParser
   *        the parser to use to parse input
   */
  public CSVReader (final Reader reader, final int line, final CSVParser csvParser)
  {
    this (reader, line, csvParser, CCSV.DEFAULT_KEEP_CR, CCSV.DEFAULT_VERIFY_READER);
  }

  /**
   * Constructs CSVReader with supplied CSVParser.
   *
   * @param aReader
   *        the reader to an underlying CSV source.
   * @param nSkipLines
   *        the line number to skip for start reading
   * @param aParser
   *        the parser to use to parse input
   * @param bKeepCR
   *        true to keep carriage returns in data read, false otherwise
   * @param bVerifyReader
   *        true to verify reader before each read, false otherwise
   */
  CSVReader (@Nonnull final Reader aReader,
             @Nonnegative final int nSkipLines,
             @Nonnull final CSVParser aParser,
             final boolean bKeepCR,
             final boolean bVerifyReader)
  {
    ValueEnforcer.notNull (aReader, "Reader");
    ValueEnforcer.isGE0 (nSkipLines, "SkipLines");
    ValueEnforcer.notNull (aParser, "Parser");

    Reader aInternallyBufferedReader = StreamUtils.getBuffered (aReader);
    if (bKeepCR)
      m_aLineReader = new CSVLineReaderKeepCR (aInternallyBufferedReader);
    else
      if (aInternallyBufferedReader instanceof BufferedReader)
        m_aLineReader = new CSVLineReaderBufferedReader ((BufferedReader) aInternallyBufferedReader);
      else
      {
        if (!(aInternallyBufferedReader instanceof NonBlockingBufferedReader))
        {
          // It is buffered, but we need it to support readLine
          aInternallyBufferedReader = new NonBlockingBufferedReader (aInternallyBufferedReader);
        }
        m_aLineReader = new CSVLineReaderNonBlockingBufferedReader ((NonBlockingBufferedReader) aInternallyBufferedReader);
      }
    m_aReader = aInternallyBufferedReader;
    m_nSkipLines = nSkipLines;
    m_aParser = aParser;
    m_bKeepCR = bKeepCR;
    m_bVerifyReader = bVerifyReader;
  }

  /**
   * @return the CSVParser used by the reader.
   */
  @Nonnull
  public CSVParser getParser ()
  {
    return m_aParser;
  }

  /**
   * Returns the number of lines in the csv file to skip before processing. This
   * is useful when there is miscellaneous data at the beginning of a file.
   *
   * @return the number of lines in the csv file to skip before processing.
   */
  @Nonnegative
  public int getSkipLines ()
  {
    return m_nSkipLines;
  }

  /**
   * Sets the line number to skip for start reading.
   *
   * @param nSkipLines
   *        the line number to skip for start reading.
   * @return this
   */
  @Nonnull
  public CSVReader setSkipLines (@Nonnegative final int nSkipLines)
  {
    ValueEnforcer.isGE0 (nSkipLines, "SkipLines");
    m_nSkipLines = nSkipLines;
    return this;
  }

  /**
   * Returns if the reader will keep carriage returns found in data or remove
   * them.
   *
   * @return true if reader will keep carriage returns, false otherwise.
   */
  public boolean isKeepCarriageReturns ()
  {
    return m_bKeepCR;
  }

  /**
   * Sets if the reader will keep or discard carriage returns.
   *
   * @param bKeepCR
   *        <code>true</code> to keep carriage returns, <code>false</code> to
   *        discard.
   * @return this
   */
  @Nonnull
  public CSVReader setKeepCarriageReturn (final boolean bKeepCR)
  {
    m_bKeepCR = bKeepCR;
    return this;
  }

  /**
   * Returns if the CSVReader will verify the reader before each read.
   * <p/>
   * By default the value is true which is the functionality for version 3.0. If
   * set to false the reader is always assumed ready to read - this is the
   * functionality for version 2.4 and before.
   * <p/>
   * The reason this method was needed was that certain types of Readers would
   * return false for its ready() method until a read was done (namely readers
   * created using Channels). This caused opencsv not to read from those
   * readers.
   * <p/>
   * Source: https://sourceforge.net/p/opencsv/bugs/108/
   *
   * @return <code>true</code> if CSVReader will verify the reader before reads.
   *         <code>false</code> otherwise.
   */
  public boolean isVerifyReader ()
  {
    return m_bVerifyReader;
  }

  /**
   * Checks to see if the CSVReader should verify the reader state before reads
   * or not. This should be set to false if you are using some form of
   * asynchronous reader (like readers created by the java.nio.* classes). The
   * default value is true.
   *
   * @param bVerifyReader
   *        <code>true</code> if CSVReader should verify reader before each
   *        read, <code>false</code> otherwise.
   * @return this
   */
  @Nonnull
  public CSVReader setVerifyReader (final boolean bVerifyReader)
  {
    m_bVerifyReader = bVerifyReader;
    return this;
  }

  /**
   * Reads the entire file into a List with each element being a List of
   * {@link String} of tokens.
   *
   * @return a List of List of String, with each inner List of {@link String}
   *         representing a line of the file.
   * @throws IOException
   *         if bad things happen during the read
   */
  @Nonnull
  @ReturnsMutableCopy
  public List <List <String>> readAll () throws IOException
  {
    final List <List <String>> ret = new ArrayList <List <String>> ();
    while (m_bHasNext)
    {
      final List <String> aNextLineAsTokens = readNext ();
      if (aNextLineAsTokens != null)
        ret.add (aNextLineAsTokens);
    }
    return ret;
  }

  /**
   * Reads the next line from the buffer and converts to a string array.
   *
   * @return a string array with each comma-separated element as a separate
   *         entry.
   * @throws IOException
   *         if bad things happen during the read
   */
  @Nullable
  public List <String> readNext () throws IOException
  {
    List <String> ret = null;
    do
    {
      final String sNextLine = _getNextLine ();
      if (!m_bHasNext)
      {
        // should throw if still pending?
        return ret;
      }
      final List <String> r = m_aParser.parseLineMulti (sNextLine);
      if (ret == null)
        ret = r;
      else
        ret.addAll (r);
    } while (m_aParser.isPending ());
    return ret;
  }

  /**
   * Reads the next line from the file.
   *
   * @return the next line from the file without trailing newline
   * @throws IOException
   *         if bad things happen during the read
   */
  @Nullable
  private String _getNextLine () throws IOException
  {
    if (_isClosed ())
    {
      m_bHasNext = false;
      return null;
    }

    if (!m_bLinesSkiped)
    {
      // Perform skip of lines exactly once
      for (int i = 0; i < m_nSkipLines; i++)
        m_aLineReader.readLine ();
      m_bLinesSkiped = true;
    }

    if (!m_bHasNext)
      return null;

    final String sNextLine = m_aLineReader.readLine ();
    if (sNextLine == null)
      m_bHasNext = false;

    return sNextLine;
  }

  /**
   * Checks to see if the file is closed.
   *
   * @return true if the reader can no longer be read from.
   */
  private boolean _isClosed ()
  {
    if (!m_bVerifyReader)
      return false;

    try
    {
      return !m_aReader.ready ();
    }
    catch (final IOException e)
    {
      return true;
    }
  }

  /**
   * Closes the underlying reader.
   *
   * @throws IOException
   *         if the close fails
   */
  public void close () throws IOException
  {
    m_aReader.close ();
  }

  /**
   * Creates an Iterator for processing the csv data.
   *
   * @return an List&lt;String&gt; iterator.
   */
  @Nonnull
  public Iterator <List <String>> iterator ()
  {
    try
    {
      return new CSVIterator (this);
    }
    catch (final IOException e)
    {
      throw new RuntimeException ("Error creating CSVIterator", e);
    }
  }
}
