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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.string.StringHelper;

/**
 * A very simple CSV parser released under a commercial-friendly license. This
 * just implements splitting a single line into fields.
 *
 * @author Glen Smith
 * @author Rainer Pruy
 */
public class CSVParser
{
  /**
   * This is the character that the {@link CSVParser} will treat as the
   * separator.
   */
  private char m_cSeparatorChar = CCSV.DEFAULT_SEPARATOR;
  /**
   * This is the character that the {@link CSVParser} will treat as the
   * quotation character.
   */
  private char m_cQuoteChar = CCSV.DEFAULT_QUOTE_CHARACTER;
  /**
   * This is the character that the {@link CSVParser} will treat as the escape
   * character.
   */
  private char m_cEscapeChar = CCSV.DEFAULT_ESCAPE_CHARACTER;
  /**
   * Determines if the field is between quotes (<code>true</code>) or between
   * separators (<code>false</code>).
   */
  private boolean m_bStrictQuotes = CCSV.DEFAULT_STRICT_QUOTES;
  /**
   * Ignore any leading white space at the start of the field.
   */
  private boolean m_bIgnoreLeadingWhiteSpace = CCSV.DEFAULT_IGNORE_LEADING_WHITESPACE;
  /**
   * Skip over quotation characters when parsing.
   */
  private boolean m_bIgnoreQuotations = CCSV.DEFAULT_IGNORE_QUOTATIONS;
  private String m_sPending;
  private boolean m_bInField = false;

  /**
   * Constructs CSVParser using a comma for the separator.
   */
  public CSVParser ()
  {}

  /**
   * @return The default separator for this parser.
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
  public CSVParser setSeparatorChar (final char cSeparator)
  {
    if (cSeparator == CCSV.NULL_CHARACTER)
      throw new UnsupportedOperationException ("The separator character must be defined!");
    m_cSeparatorChar = cSeparator;
    if (_anyCharactersAreTheSame ())
      throw new UnsupportedOperationException ("The separator, quote, and escape characters must be different!");
    return this;
  }

  /**
   * @return The default quotation character for this parser.
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
  public CSVParser setQuoteChar (final char cQuoteChar)
  {
    m_cQuoteChar = cQuoteChar;
    if (_anyCharactersAreTheSame ())
      throw new UnsupportedOperationException ("The separator, quote, and escape characters must be different!");
    return this;
  }

  /**
   * @return The default escape character for this parser.
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
  public CSVParser setEscapeChar (final char cEscapeChar)
  {
    m_cEscapeChar = cEscapeChar;
    if (_anyCharactersAreTheSame ())
      throw new UnsupportedOperationException ("The separator, quote, and escape characters must be different!");
    return this;
  }

  /**
   * @return The default strictQuotes setting for this parser.
   */
  public boolean isStrictQuotes ()
  {
    return m_bStrictQuotes;
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
  public CSVParser setStrictQuotes (final boolean bStrictQuotes)
  {
    m_bStrictQuotes = bStrictQuotes;
    return this;
  }

  /**
   * @return The default ignoreLeadingWhiteSpace setting for this parser.
   */
  public boolean isIgnoreLeadingWhiteSpace ()
  {
    return m_bIgnoreLeadingWhiteSpace;
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
  public CSVParser setIgnoreLeadingWhiteSpace (final boolean bIgnoreLeadingWhiteSpace)
  {
    m_bIgnoreLeadingWhiteSpace = bIgnoreLeadingWhiteSpace;
    return this;
  }

  /**
   * @return the default ignoreQuotation setting for this parser.
   */
  public boolean isIgnoreQuotations ()
  {
    return m_bIgnoreQuotations;
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
  public CSVParser setIgnoreQuotations (final boolean bIgnoreQuotations)
  {
    m_bIgnoreQuotations = bIgnoreQuotations;
    return this;
  }

  /**
   * checks to see if any two of the three characters are the same. This is
   * because in openCSV the separator, quote, and escape characters must the
   * different.
   *
   * @return <code>true</code> if any two of the three are the same.
   */
  private boolean _anyCharactersAreTheSame ()
  {
    return _isSameCharacter (m_cSeparatorChar, m_cQuoteChar) ||
           _isSameCharacter (m_cSeparatorChar, m_cEscapeChar) ||
           _isSameCharacter (m_cQuoteChar, m_cEscapeChar);
  }

  /**
   * checks that the two characters are the same and are not the defined
   * NULL_CHARACTER.
   *
   * @param c1
   *        first character
   * @param c2
   *        second character
   * @return true if both characters are the same and are not the defined
   *         NULL_CHARACTER
   */
  private static boolean _isSameCharacter (final char c1, final char c2)
  {
    return c1 != CCSV.NULL_CHARACTER && c1 == c2;
  }

  /**
   * @return true if something was left over from last call(s)
   */
  public boolean isPending ()
  {
    return m_sPending != null;
  }

  /**
   * Parses an incoming String and returns an array of elements. This method is
   * used when the data spans multiple lines.
   *
   * @param sNextLine
   *        current line to be processed
   * @return the tokenized list of elements, or <code>null</code> if nextLine is
   *         <code>null</code>
   * @throws IOException
   *         if bad things happen during the read
   */
  @Nullable
  public List <String> parseLineMulti (@Nullable final String sNextLine) throws IOException
  {
    return _parseLine (sNextLine, true);
  }

  /**
   * Parses an incoming String and returns an array of elements. This method is
   * used when all data is contained in a single line.
   *
   * @param sNextLine
   *        Line to be parsed.
   * @return the tokenized list of elements, or <code>null</code> if nextLine is
   *         <code>null</code>
   * @throws IOException
   *         if bad things happen during the read
   */
  @Nullable
  public List <String> parseLine (@Nullable final String sNextLine) throws IOException
  {
    return _parseLine (sNextLine, false);
  }

  /**
   * Parses an incoming String and returns an array of elements.
   *
   * @param sNextLine
   *        the string to parse
   * @param bMulti
   *        Does it take multiple lines to form a single record.
   * @return the tokenized list of elements, or <code>null</code> if sNextLine
   *         is <code>null</code>
   * @throws IOException
   *         if bad things happen during the read
   */
  @Nullable
  private List <String> _parseLine (@Nullable final String sNextLine, final boolean bMulti) throws IOException
  {
    if (!bMulti && m_sPending != null)
      m_sPending = null;

    if (sNextLine == null)
    {
      if (m_sPending != null)
      {
        final String s = m_sPending;
        m_sPending = null;
        return CollectionHelper.newList (s);
      }
      return null;
    }

    final List <String> aTokensOnThisLine = new ArrayList <String> ();
    StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);
    boolean bInQuotes = false;
    if (m_sPending != null)
    {
      aSB.append (m_sPending);
      m_sPending = null;
      bInQuotes = !m_bIgnoreQuotations;
    }

    final int nLen = sNextLine.length ();
    for (int nIndex = 0; nIndex < nLen; nIndex++)
    {
      final char c = sNextLine.charAt (nIndex);
      if (c == m_cEscapeChar)
      {
        if (isNextCharacterEscapable (sNextLine, _isInQuotes (bInQuotes), nIndex))
        {
          nIndex = _appendNextCharacterAndAdvanceLoop (sNextLine, aSB, nIndex);
        }
      }
      else
        if (c == m_cQuoteChar)
        {
          if (_isNextCharacterEscapedQuote (sNextLine, _isInQuotes (bInQuotes), nIndex))
          {
            nIndex = _appendNextCharacterAndAdvanceLoop (sNextLine, aSB, nIndex);
          }
          else
          {
            bInQuotes = !bInQuotes;

            // the tricky case of an embedded quote in the middle: a,bc"d"ef,g
            if (!m_bStrictQuotes)
            {
              // 1. not on the beginning of the line
              // 2. not at the beginning of an escape sequence
              // 4. not at the end of an escape sequence
              if (nIndex > 2 &&
                  sNextLine.charAt (nIndex - 1) != m_cSeparatorChar &&
                  sNextLine.length () > (nIndex + 1) &&
                  sNextLine.charAt (nIndex + 1) != m_cSeparatorChar)
              {
                if (m_bIgnoreLeadingWhiteSpace && aSB.length () > 0 && isAllWhiteSpace (aSB))
                {
                  aSB.setLength (0);
                }
                else
                {
                  aSB.append (c);
                }
              }
            }
          }
          m_bInField = !m_bInField;
        }
        else
          if (c == m_cSeparatorChar && !(bInQuotes && !m_bIgnoreQuotations))
          {
            aTokensOnThisLine.add (aSB.toString ());
            aSB.setLength (0);
            m_bInField = false;
          }
          else
            if (!m_bStrictQuotes || (bInQuotes && !m_bIgnoreQuotations))
            {
              aSB.append (c);
              m_bInField = true;
            }
            else
            {
              // Ignore character
            }
    }
    // line is done - check status
    if (bInQuotes && !m_bIgnoreQuotations)
    {
      if (bMulti)
      {
        // continuing a quoted section, re-append newline
        aSB.append ('\n');
        m_sPending = aSB.toString ();
        // this partial content is not to be added to field list yet
        aSB = null;
      }
      else
      {
        throw new IOException ("Un-terminated quoted field at end of CSV line");
      }
    }
    else
    {
      m_bInField = false;
    }

    if (aSB != null)
    {
      aTokensOnThisLine.add (aSB.toString ());
    }
    return aTokensOnThisLine;
  }

  /**
   * Appends the next character in the line to the {@link StringBuilder}.
   *
   * @param sLine
   *        line to process
   * @param aSB
   *        contains the processed character
   * @param nIndex
   *        current position in the line.
   * @return new position in the line.
   */
  @Nonnegative
  private int _appendNextCharacterAndAdvanceLoop (@Nonnull final String sLine,
                                                  @Nonnull final StringBuilder aSB,
                                                  @Nonnegative final int nIndex)
  {
    aSB.append (sLine.charAt (nIndex + 1));
    return nIndex + 1;
  }

  /**
   * Determines if we can process as if we were in quotes.
   *
   * @param bInQuotes
   *        - are we currently in quotes.
   * @return - true if we should process as if we are inside quotes.
   */
  private boolean _isInQuotes (final boolean bInQuotes)
  {
    return (bInQuotes && !m_bIgnoreQuotations) || m_bInField;
  }

  /**
   * Checks to see if the character after the index is a quotation character.
   * precondition: the current character is a quote or an escape
   *
   * @param sNextLine
   *        the current line
   * @param bInQuotes
   *        <code>true</code> if the current context is quoted
   * @param nIndex
   *        current index in line
   * @return true if the following character is a quote
   */
  private boolean _isNextCharacterEscapedQuote (@Nonnull final String sNextLine,
                                                final boolean bInQuotes,
                                                final int nIndex)
  {
    // 1. we are in quotes, therefore there can be escaped quotes in here.
    // 2. there is indeed another character to check.
    return bInQuotes &&
           sNextLine.length () > (nIndex + 1) &&
           _isCharacterQuoteCharacter (sNextLine.charAt (nIndex + 1));
  }

  /**
   * Checks to see if the passed in character is the defined quotation
   * character.
   *
   * @param c
   *        source character
   * @return true if c is the defined quotation character
   */
  private boolean _isCharacterQuoteCharacter (final char c)
  {
    return c == m_cQuoteChar;
  }

  /**
   * checks to see if the character is the defined escape character.
   *
   * @param c
   *        source character
   * @return true if the character is the defined escape character
   */
  private boolean _isCharacterEscapeCharacter (final char c)
  {
    return c == m_cEscapeChar;
  }

  /**
   * Checks to see if the character passed in could be escapable. Escapable
   * characters for openCSV are the quotation character or the escape character.
   *
   * @param c
   *        source character
   * @return true if the character could be escapable.
   */
  private boolean _isCharacterEscapable (final char c)
  {
    return _isCharacterQuoteCharacter (c) || _isCharacterEscapeCharacter (c);
  }

  /**
   * Checks to see if the character after the current index in a String is an
   * escapable character. Meaning the next character is either a quotation
   * character or the escape char and you are inside quotes. precondition: the
   * current character is an escape
   *
   * @param sNextLine
   *        the current line
   * @param bInQuotes
   *        true if the current context is quoted
   * @param nIndex
   *        current index in line
   * @return <code>true</code> if the following character is a quote
   */
  protected boolean isNextCharacterEscapable (@Nonnull final String sNextLine,
                                              final boolean bInQuotes,
                                              final int nIndex)
  {
    // we are in quotes, therefore there can be escaped quotes in here.
    // there is indeed another character to check.
    return bInQuotes && sNextLine.length () > (nIndex + 1) && _isCharacterEscapable (sNextLine.charAt (nIndex + 1));
  }

  /**
   * Checks if every element is the character sequence is whitespace.
   * precondition: sb.length() is greater than 0
   *
   * @param sb
   *        A sequence of characters to examine
   * @return <code>true</code> if every character in the sequence is whitespace
   */
  protected boolean isAllWhiteSpace (@Nonnull final CharSequence sb)
  {
    return StringHelper.isAllWhitespace (sb);
  }
}
