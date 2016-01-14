/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.iterate.ArrayIterator;
import com.helger.commons.io.stream.StreamHelper;

/**
 * A very simple CSV writer released under a commercial-friendly license.
 *
 * @author Glen Smith
 */
public class CSVWriter implements Closeable, Flushable
{
  /**
   * The quote constant to use when you wish to suppress all quoting.
   */
  public static final char NO_QUOTE_CHARACTER = CCSV.NULL_CHARACTER;
  /**
   * The escape constant to use when you wish to suppress all escaping.
   */
  public static final char NO_ESCAPE_CHARACTER = CCSV.NULL_CHARACTER;

  /**
   * The default line delimiter to be used.
   */
  public static final String DEFAULT_LINE_END = "\n";

  public static final boolean DEFAULT_QUOTE_ALL = true;

  private final Writer m_aRawWriter;
  private final PrintWriter m_aPW;
  private char m_cSeparatorChar = CCSV.DEFAULT_SEPARATOR;
  private char m_cQuoteChar = CCSV.DEFAULT_QUOTE_CHARACTER;
  private char m_cEscapeChar = CCSV.DEFAULT_ESCAPE_CHARACTER;
  private String m_sLineEnd = DEFAULT_LINE_END;

  /**
   * Constructs {@link CSVWriter} with all default settings.
   *
   * @param aWriter
   *        the writer to an underlying CSV source. May not be <code>null</code>
   *        .
   */
  public CSVWriter (@Nonnull final Writer aWriter)
  {
    ValueEnforcer.notNull (aWriter, "Writer");
    m_aRawWriter = aWriter;
    m_aPW = new PrintWriter (aWriter);
  }

  /**
   * @return The default separator for this writer.
   */
  public char getSeparatorChar ()
  {
    return m_cSeparatorChar;
  }

  /**
   * Sets the delimiter to use for separating entries.
   *
   * @param cSeparator
   *        the delimiter to use for separating entries
   * @return this
   */
  @Nonnull
  public CSVWriter setSeparatorChar (final char cSeparator)
  {
    if (cSeparator == CCSV.NULL_CHARACTER)
      throw new UnsupportedOperationException ("The separator character must be defined!");
    m_cSeparatorChar = cSeparator;
    return this;
  }

  /**
   * @return The default quotation character for this writer.
   */
  public char getQuoteChar ()
  {
    return m_cQuoteChar;
  }

  /**
   * Sets the character to use for quoted elements.
   *
   * @param cQuoteChar
   *        the character to use for quoted element.
   * @return this
   */
  @Nonnull
  public CSVWriter setQuoteChar (final char cQuoteChar)
  {
    m_cQuoteChar = cQuoteChar;
    return this;
  }

  /**
   * @return The default escape character for this writer.
   */
  public char getEscapeChar ()
  {
    return m_cEscapeChar;
  }

  /**
   * Sets the character to use for escaping a separator or quote.
   *
   * @param cEscapeChar
   *        the character to use for escaping a separator or quote.
   * @return this
   */
  @Nonnull
  public CSVWriter setEscapeChar (final char cEscapeChar)
  {
    m_cEscapeChar = cEscapeChar;
    return this;
  }

  /**
   * @return the line delimiting string. Neither <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  public String getLineEnd ()
  {
    return m_sLineEnd;
  }

  /**
   * Set the line delimiting string.
   *
   * @param sLineEnd
   *        The line end. May neither be <code>null</code> nor empty.
   * @return this
   */
  @Nonnull
  public CSVWriter setLineEnd (@Nonnull @Nonempty final String sLineEnd)
  {
    ValueEnforcer.notNull (sLineEnd, "LineEnd");
    m_sLineEnd = sLineEnd;
    return this;
  }

  /**
   * Writes the entire list to a CSV file.
   *
   * @param aAllLines
   *        a List of List of String, with each List of String representing a
   *        line of the file.
   * @param bApplyQuotesToAll
   *        <code>true</code> if all values are to be quoted. <code>false</code>
   *        if quotes only to be applied to values which contain the separator,
   *        escape, quote or new line characters.
   */
  public void writeAll (@Nonnull final List <List <String>> aAllLines, final boolean bApplyQuotesToAll)
  {
    for (final List <String> aLine : aAllLines)
      writeNext (aLine, bApplyQuotesToAll);
  }

  /**
   * Writes the entire list to a CSV file with quoting enabled.
   *
   * @param aAllLines
   *        a List of List of String, with each List of String representing a
   *        line of the file.
   */
  public void writeAll (@Nonnull final List <List <String>> aAllLines)
  {
    writeAll (aAllLines, DEFAULT_QUOTE_ALL);
  }

  /**
   * Writes the next line to the file.
   *
   * @param aNextLine
   *        A collection of Strings where each entry represents a single cell.
   * @param bApplyQuotesToAll
   *        <code>true</code> if all values are to be quoted. <code>false</code>
   *        applies quotes only to values which contain the separator, escape,
   *        quote or new line characters.
   */
  public void writeNext (@Nullable final Iterator <String> aNextLine, final boolean bApplyQuotesToAll)
  {
    if (aNextLine != null)
    {
      final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);
      final boolean bCanQuote = m_cQuoteChar != NO_QUOTE_CHARACTER;
      boolean bFirst = true;
      while (aNextLine.hasNext ())
      {
        if (bFirst)
          bFirst = false;
        else
          aSB.append (m_cSeparatorChar);

        final String sNextElement = aNextLine.next ();
        if (sNextElement != null)
        {
          final boolean bElementContainsSpecialChars = stringContainsSpecialCharacters (sNextElement);
          final boolean bDoQuoteElement = bCanQuote && (bApplyQuotesToAll || bElementContainsSpecialChars);

          if (bDoQuoteElement)
            aSB.append (m_cQuoteChar);

          if (bElementContainsSpecialChars)
            aSB.append (getEscapedText (sNextElement));
          else
            aSB.append (sNextElement);

          if (bDoQuoteElement)
            aSB.append (m_cQuoteChar);
        }
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
  public void writeNext (@Nullable final Iterable <String> aNextLine, final boolean bApplyQuotesToAll)
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
  public void writeNext (@Nullable final Iterable <String> aNextLine)
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
      writeNext (new ArrayIterator <> (aNextLine), bApplyQuotesToAll);
  }

  /**
   * Writes the next line to the file.
   *
   * @param aNextLine
   *        a string array with each comma-separated element as a separate
   *        entry.
   */
  public void writeNext (@Nullable final String... aNextLine)
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
      writeNext (new ArrayIterator <> (aNextLine, nOfs, nLength), bApplyQuotesToAll);
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
  protected boolean stringContainsSpecialCharacters (@Nonnull final String sLine)
  {
    return sLine.indexOf (m_cQuoteChar) != -1 ||
           sLine.indexOf (m_cEscapeChar) != -1 ||
           sLine.indexOf (m_cSeparatorChar) != -1 ||
           sLine.indexOf ('\r') != -1 ||
           sLine.indexOf ('\n') != -1;
  }

  /**
   * Processes all the characters in a line.
   *
   * @param sNextElement
   *        element to process.
   * @return a StringBuilder with the elements data.
   */
  @Nonnull
  protected StringBuilder getEscapedText (@Nonnull final String sNextElement)
  {
    if (m_cEscapeChar == NO_ESCAPE_CHARACTER)
      return new StringBuilder (sNextElement);

    final StringBuilder aSB = new StringBuilder (sNextElement.length () * 2);
    for (final char c : sNextElement.toCharArray ())
    {
      if (c == m_cQuoteChar || c == m_cEscapeChar)
        aSB.append (m_cEscapeChar);
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
    StreamHelper.flush (this);
  }
}
