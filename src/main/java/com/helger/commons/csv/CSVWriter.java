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

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collections.iterate.ArrayIterator;
import com.helger.commons.io.streams.StreamUtils;

/**
 * A very simple CSV writer released under a commercial-friendly license.
 *
 * @author Glen Smith
 */
public class CSVWriter implements Closeable, Flushable
{
  public static final int INITIAL_STRING_SIZE = 128;
  /**
   * The character used for escaping quotes.
   */
  public static final char DEFAULT_ESCAPE_CHARACTER = '"';
  /**
   * The default separator to use if none is supplied to the constructor.
   */
  public static final char DEFAULT_SEPARATOR = ',';
  /**
   * The default quote character to use if none is supplied to the constructor.
   */
  public static final char DEFAULT_QUOTE_CHARACTER = '"';
  /**
   * The quote constant to use when you wish to suppress all quoting.
   */
  public static final char NO_QUOTE_CHARACTER = '\u0000';
  /**
   * The escape constant to use when you wish to suppress all escaping.
   */
  public static final char NO_ESCAPE_CHARACTER = '\u0000';
  /**
   * Default line terminator uses platform encoding.
   */
  public static final String DEFAULT_LINE_END = "\n";

  public static final boolean DEFAULT_QUOTE_ALL = true;

  private final Writer m_aRawWriter;
  private final PrintWriter m_aPW;
  private final char m_cSeparator;
  private final char m_cQuotechar;
  private final char m_cEscapechar;
  private final String m_sLineEnd;

  /**
   * Constructs CSVWriter using a comma for the separator.
   *
   * @param writer
   *        the writer to an underlying CSV source.
   */
  public CSVWriter (final Writer writer)
  {
    this (writer, DEFAULT_SEPARATOR);
  }

  /**
   * Constructs CSVWriter with supplied separator.
   *
   * @param writer
   *        the writer to an underlying CSV source.
   * @param cSeparator
   *        the delimiter to use for separating entries.
   */
  public CSVWriter (final Writer writer, final char cSeparator)
  {
    this (writer, cSeparator, DEFAULT_QUOTE_CHARACTER);
  }

  /**
   * Constructs CSVWriter with supplied separator and quote char.
   *
   * @param writer
   *        the writer to an underlying CSV source.
   * @param separator
   *        the delimiter to use for separating entries
   * @param quotechar
   *        the character to use for quoted elements
   */
  public CSVWriter (final Writer writer, final char separator, final char quotechar)
  {
    this (writer, separator, quotechar, DEFAULT_ESCAPE_CHARACTER);
  }

  /**
   * Constructs CSVWriter with supplied separator and quote char.
   *
   * @param writer
   *        the writer to an underlying CSV source.
   * @param separator
   *        the delimiter to use for separating entries
   * @param quotechar
   *        the character to use for quoted elements
   * @param escapechar
   *        the character to use for escaping quotechars or escapechars
   */
  public CSVWriter (final Writer writer, final char separator, final char quotechar, final char escapechar)
  {
    this (writer, separator, quotechar, escapechar, DEFAULT_LINE_END);
  }

  /**
   * Constructs CSVWriter with supplied separator and quote char.
   *
   * @param writer
   *        the writer to an underlying CSV source.
   * @param separator
   *        the delimiter to use for separating entries
   * @param quotechar
   *        the character to use for quoted elements
   * @param lineEnd
   *        the line feed terminator to use
   */
  public CSVWriter (final Writer writer, final char separator, final char quotechar, final String lineEnd)
  {
    this (writer, separator, quotechar, DEFAULT_ESCAPE_CHARACTER, lineEnd);
  }

  /**
   * Constructs CSVWriter with supplied separator, quote char, escape char and
   * line ending.
   *
   * @param writer
   *        the writer to an underlying CSV source.
   * @param separator
   *        the delimiter to use for separating entries
   * @param quotechar
   *        the character to use for quoted elements
   * @param escapechar
   *        the character to use for escaping quotechars or escapechars
   * @param lineEnd
   *        the line feed terminator to use
   */
  public CSVWriter (final Writer writer,
                    final char separator,
                    final char quotechar,
                    final char escapechar,
                    final String lineEnd)
  {
    m_aRawWriter = writer;
    m_aPW = new PrintWriter (writer);
    m_cSeparator = separator;
    m_cQuotechar = quotechar;
    m_cEscapechar = escapechar;
    m_sLineEnd = lineEnd;
  }

  /**
   * Writes the entire list to a CSV file. The list is assumed to be a String[]
   *
   * @param aAllLines
   *        a List of String[], with each String[] representing a line of the
   *        file.
   * @param bApplyQuotesToAll
   *        <code>true</code> if all values are to be quoted. <code>false</code>
   *        if quotes only to be applied to values which contain the separator,
   *        escape, quote or new line characters.
   */
  public void writeAll (final List <List <String>> aAllLines, final boolean bApplyQuotesToAll)
  {
    for (final List <String> aLine : aAllLines)
      writeNext (aLine, bApplyQuotesToAll);
  }

  /**
   * Writes the entire list to a CSV file. The list is assumed to be a String[]
   *
   * @param aAllLines
   *        a List of String[], with each String[] representing a line of the
   *        file.
   */
  public void writeAll (final List <List <String>> aAllLines)
  {
    for (final List <String> aLine : aAllLines)
      writeNext (aLine, DEFAULT_QUOTE_ALL);
  }

  /**
   * Writes the next line to the file.
   *
   * @param aNextLine
   *        a string array with each comma-separated element as a separate
   *        entry.
   * @param bApplyQuotesToAll
   *        true if all values are to be quoted. false applies quotes only to
   *        values which contain the separator, escape, quote or new line
   *        characters.
   */
  public void writeNext (@Nullable final Iterator <String> aNextLine, final boolean bApplyQuotesToAll)
  {
    if (aNextLine != null)
    {
      final StringBuilder aSB = new StringBuilder (INITIAL_STRING_SIZE);
      boolean bFirst = true;
      while (aNextLine.hasNext ())
      {
        if (bFirst)
          bFirst = false;
        else
          aSB.append (m_cSeparator);

        final String sNextElement = aNextLine.next ();
        if (sNextElement == null)
          continue;

        final boolean bElementContainsSpecialChars = _stringContainsSpecialCharacters (sNextElement);
        final boolean bDoQuote = (bApplyQuotesToAll || bElementContainsSpecialChars) &&
                                 m_cQuotechar != NO_QUOTE_CHARACTER;

        if (bDoQuote)
          aSB.append (m_cQuotechar);

        if (bElementContainsSpecialChars)
          aSB.append (escapeElement (sNextElement));
        else
          aSB.append (sNextElement);

        if (bDoQuote)
          aSB.append (m_cQuotechar);
      }

      aSB.append (m_sLineEnd);
      m_aPW.write (aSB.toString ());
    }
  }

  /**
   * Writes the next line to the file.
   *
   * @param aNextLine
   *        a string array with each comma-separated element as a separate
   *        entry.
   * @param bApplyQuotesToAll
   *        <code>true</code> if all values are to be quoted. <code>false</code>
   *        applies quotes only to values which contain the separator, escape,
   *        quote or new line characters.
   */
  public void writeNext (@Nullable final List <String> aNextLine, final boolean bApplyQuotesToAll)
  {
    if (aNextLine != null)
      writeNext (aNextLine.iterator (), bApplyQuotesToAll);
  }

  /**
   * Writes the next line to the file.
   *
   * @param aNextLine
   *        a string array with each comma-separated element as a separate
   *        entry.
   */
  public void writeNext (@Nullable final List <String> aNextLine)
  {
    writeNext (aNextLine, DEFAULT_QUOTE_ALL);
  }

  /**
   * Writes the next line to the file.
   *
   * @param aNextLine
   *        a string array with each comma-separated element as a separate
   *        entry.
   * @param bApplyQuotesToAll
   *        <code>true</code> if all values are to be quoted. <code>false</code>
   *        applies quotes only to values which contain the separator, escape,
   *        quote or new line characters.
   */
  public void writeNext (@Nullable final String [] aNextLine, final boolean bApplyQuotesToAll)
  {
    if (aNextLine != null)
      writeNext (ArrayIterator.create (aNextLine), bApplyQuotesToAll);
  }

  /**
   * Writes the next line to the file.
   *
   * @param aNextLine
   *        a string array with each comma-separated element as a separate
   *        entry.
   */
  public void writeNext (@Nullable final String [] aNextLine)
  {
    writeNext (aNextLine, DEFAULT_QUOTE_ALL);
  }

  /**
   * Writes the next line to the file.
   *
   * @param aNextLine
   *        a string array with each comma-separated element as a separate
   *        entry.
   * @param nOfs
   *        Array Offset. Must be &ge; 0.
   * @param nLength
   *        Array Length. Must be &ge; 0.
   * @param bApplyQuotesToAll
   *        <code>true</code> if all values are to be quoted. <code>false</code>
   *        applies quotes only to values which contain the separator, escape,
   *        quote or new line characters.
   */
  public void writeNext (@Nullable final String [] aNextLine,
                         @Nonnegative final int nOfs,
                         @Nonnegative final int nLength,
                         final boolean bApplyQuotesToAll)
  {
    if (aNextLine != null)
      writeNext (ArrayIterator.createOfsLen (aNextLine, nOfs, nLength), bApplyQuotesToAll);
  }

  /**
   * Writes the next line to the file.
   *
   * @param aNextLine
   *        a string array with each comma-separated element as a separate
   *        entry.
   * @param nOfs
   *        Array Offset. Must be &ge; 0.
   * @param nLength
   *        Array Length. Must be &ge; 0.
   */
  public void writeNext (@Nullable final String [] aNextLine,
                         @Nonnegative final int nOfs,
                         @Nonnegative final int nLength)
  {
    writeNext (aNextLine, nOfs, nLength, DEFAULT_QUOTE_ALL);
  }

  /**
   * checks to see if the line contains special characters.
   *
   * @param sLine
   *        - element of data to check for special characters.
   * @return true if the line contains the quote, escape, separator, newline or
   *         return.
   */
  private boolean _stringContainsSpecialCharacters (@Nonnull final String sLine)
  {
    return sLine.indexOf (m_cQuotechar) != -1 ||
           sLine.indexOf (m_cEscapechar) != -1 ||
           sLine.indexOf (m_cSeparator) != -1 ||
           sLine.indexOf ('\r') != -1 ||
           sLine.indexOf ('\n') != -1;
  }

  /**
   * Processes all the characters in a line.
   *
   * @param sNextElement
   *        - element to process.
   * @return a StringBuilder with the elements data.
   */
  @Nonnull
  protected StringBuilder escapeElement (@Nonnull final String sNextElement)
  {
    if (m_cEscapechar == NO_ESCAPE_CHARACTER)
      return new StringBuilder (sNextElement);

    final StringBuilder aSB = new StringBuilder (INITIAL_STRING_SIZE);
    for (final char c : sNextElement.toCharArray ())
    {
      if (c == m_cQuotechar || c == m_cEscapechar)
        aSB.append (m_cEscapechar);
      aSB.append (c);
    }
    return aSB;
  }

  /**
   * Flush underlying stream to writer.
   *
   * @throws IOException
   *         if bad things happen
   */
  public void flush () throws IOException
  {
    m_aPW.flush ();
  }

  /**
   * Close the underlying stream writer flushing any buffered content.
   *
   * @throws IOException
   *         if bad things happen
   */
  public void close () throws IOException
  {
    flush ();
    m_aPW.close ();
    m_aRawWriter.close ();
  }

  /**
   * Checks to see if the there has been an error in the printstream.
   *
   * @return <code>true</code> if the print stream has encountered an error,
   *         either on the underlying output stream or during a format
   *         conversion.
   */
  public boolean checkError ()
  {
    return m_aPW.checkError ();
  }

  /**
   * flushes the writer without throwing any exceptions.
   */
  public void flushQuietly ()
  {
    StreamUtils.flush (this);
  }
}
