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

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.function.Consumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillCloseWhenClosed;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsIterable;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.stream.NonBlockingBufferedReader;
import com.helger.commons.io.stream.StreamHelper;

/**
 * A very simple CSV reader released under a commercial-friendly license.
 *
 * @author Glen Smith
 * @author Philip Helger
 */
public class CSVReader implements Closeable, ICommonsIterable <ICommonsList <String>>
{
  private final Reader m_aReader;
  private final ICSVLineReader m_aLineReader;
  private final CSVParser m_aParser;
  private int m_nSkipLines = CCSV.DEFAULT_SKIP_LINES;
  private final boolean m_bKeepCR;
  private boolean m_bVerifyReader = CCSV.DEFAULT_VERIFY_READER;

  private boolean m_bHasNext = true;
  private boolean m_bLinesSkiped = false;

  /**
   * Constructs CSVReader using a comma for the separator.
   *
   * @param aReader
   *        the reader to an underlying CSV source.
   */
  public CSVReader (@Nonnull @WillCloseWhenClosed final Reader aReader)
  {
    this (aReader, new CSVParser (), CCSV.DEFAULT_KEEP_CR);
  }

  /**
   * Constructs {@link CSVReader} using a comma for the separator.
   *
   * @param aReader
   *        the reader to an underlying CSV source.
   * @param bKeepCR
   *        <code>true</code> to keep carriage returns in data read,
   *        <code>false</code> otherwise
   */
  public CSVReader (@Nonnull @WillCloseWhenClosed final Reader aReader, final boolean bKeepCR)
  {
    this (aReader, new CSVParser (), bKeepCR);
  }

  /**
   * Constructs {@link CSVReader} with supplied {@link CSVParser}.
   *
   * @param aReader
   *        the reader to an underlying CSV source.
   * @param aParser
   *        the parser to use to parse input
   * @param bKeepCR
   *        <code>true</code> to keep carriage returns in data read,
   *        <code>false</code> otherwise
   */
  public CSVReader (@Nonnull @WillCloseWhenClosed final Reader aReader,
                    @Nonnull final CSVParser aParser,
                    final boolean bKeepCR)
  {
    ValueEnforcer.notNull (aReader, "Reader");
    ValueEnforcer.notNull (aParser, "Parser");

    Reader aInternallyBufferedReader = StreamHelper.getBuffered (aReader);
    if (bKeepCR)
      m_aLineReader = new CSVLineReaderKeepCR (aInternallyBufferedReader);
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
    m_aParser = aParser;
    m_bKeepCR = bKeepCR;
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
   * @return The default separator for this parser.
   */
  public char getSeparatorChar ()
  {
    return m_aParser.getSeparatorChar ();
  }

  /**
   * Sets the delimiter to use for separating entries.
   *
   * @param cSeparator
   *        the delimiter to use for separating entries
   * @return this
   */
  @Nonnull
  public CSVReader setSeparatorChar (final char cSeparator)
  {
    m_aParser.setSeparatorChar (cSeparator);
    return this;
  }

  /**
   * @return The default quotation character for this parser.
   */
  public char getQuoteChar ()
  {
    return m_aParser.getQuoteChar ();
  }

  /**
   * Sets the character to use for quoted elements.
   *
   * @param cQuoteChar
   *        the character to use for quoted element.
   * @return this
   */
  @Nonnull
  public CSVReader setQuoteChar (final char cQuoteChar)
  {
    m_aParser.setQuoteChar (cQuoteChar);
    return this;
  }

  /**
   * @return The default escape character for this parser.
   */
  public char getEscapeChar ()
  {
    return m_aParser.getEscapeChar ();
  }

  /**
   * Sets the character to use for escaping a separator or quote.
   *
   * @param cEscapeChar
   *        the character to use for escaping a separator or quote.
   * @return this
   */
  @Nonnull
  public CSVReader setEscapeChar (final char cEscapeChar)
  {
    m_aParser.setEscapeChar (cEscapeChar);
    return this;
  }

  /**
   * @return The default strictQuotes setting for this parser.
   */
  public boolean isStrictQuotes ()
  {
    return m_aParser.isStrictQuotes ();
  }

  /**
   * Sets the strict quotes setting - if true, characters outside the quotes are
   * ignored.
   *
   * @param bStrictQuotes
   *        if <code>true</code>, characters outside the quotes are ignored
   * @return this
   */
  @Nonnull
  public CSVReader setStrictQuotes (final boolean bStrictQuotes)
  {
    m_aParser.setStrictQuotes (bStrictQuotes);
    return this;
  }

  /**
   * @return The default ignoreLeadingWhiteSpace setting for this parser.
   */
  public boolean isIgnoreLeadingWhiteSpace ()
  {
    return m_aParser.isIgnoreLeadingWhiteSpace ();
  }

  /**
   * Sets the ignore leading whitespace setting - if true, white space in front
   * of a quote in a field is ignored.
   *
   * @param bIgnoreLeadingWhiteSpace
   *        if <code>true</code>, white space in front of a quote in a field is
   *        ignored
   * @return this
   */
  @Nonnull
  public CSVReader setIgnoreLeadingWhiteSpace (final boolean bIgnoreLeadingWhiteSpace)
  {
    m_aParser.setIgnoreLeadingWhiteSpace (bIgnoreLeadingWhiteSpace);
    return this;
  }

  /**
   * @return the default ignoreQuotation setting for this parser.
   */
  public boolean isIgnoreQuotations ()
  {
    return m_aParser.isIgnoreQuotations ();
  }

  /**
   * Sets the ignore quotations mode - if <code>true</code>, quotations are
   * ignored.
   *
   * @param bIgnoreQuotations
   *        if <code>true</code>, quotations are ignored
   * @return this
   */
  @Nonnull
  public CSVReader setIgnoreQuotations (final boolean bIgnoreQuotations)
  {
    m_aParser.setIgnoreQuotations (bIgnoreQuotations);
    return this;
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
   * Returns if the {@link CSVReader} will verify the reader before each read.
   * <br>
   * By default the value is true which is the functionality for version 3.0. If
   * set to false the reader is always assumed ready to read - this is the
   * functionality for version 2.4 and before.<br>
   * The reason this method was needed was that certain types of Readers would
   * return false for its ready() method until a read was done (namely readers
   * created using Channels). This caused opencsv not to read from those
   * readers.<br>
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
   * Reads the entire file into a list with each element being a list of
   * {@link String} of tokens.
   *
   * @return a list of list of String, with each inner list of {@link String}
   *         representing a line of the file.
   * @throws IOException
   *         if bad things happen during the read
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <ICommonsList <String>> readAll () throws IOException
  {
    final ICommonsList <ICommonsList <String>> ret = new CommonsArrayList <> ();
    while (m_bHasNext)
    {
      final ICommonsList <String> aNextLineAsTokens = readNext ();
      if (aNextLineAsTokens != null)
        ret.add (aNextLineAsTokens);
    }
    return ret;
  }

  /**
   * Reads the entire file line by line and invoke a callback for each line.
   *
   * @param aLineConsumer
   *        The consumer that is invoked for every line. May not be
   *        <code>null</code>.
   * @throws IOException
   *         if bad things happen during the read
   */
  public void readAll (@Nonnull final Consumer <? super ICommonsList <String>> aLineConsumer) throws IOException
  {
    while (m_bHasNext)
    {
      final ICommonsList <String> aNextLineAsTokens = readNext ();
      if (aNextLineAsTokens != null)
        aLineConsumer.accept (aNextLineAsTokens);
    }
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
  public ICommonsList <String> readNext () throws IOException
  {
    ICommonsList <String> ret = null;
    do
    {
      final String sNextLine = _getNextLine ();
      if (!m_bHasNext)
      {
        // should throw if still pending?
        return ret;
      }
      final ICommonsList <String> r = m_aParser.parseLineMulti (sNextLine);
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
   * @return an ICommonsList&lt;String&gt; iterator.
   */
  @Nonnull
  public Iterator <ICommonsList <String>> iterator ()
  {
    try
    {
      return new CSVIterator (this);
    }
    catch (final IOException e)
    {
      throw new UncheckedIOException ("Error creating CSVIterator", e);
    }
  }
}
