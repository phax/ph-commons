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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collections.CollectionHelper;
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
   * This is the character that the CSVParser will treat as the separator.
   */
  private final char m_cSeparator;
  /**
   * This is the character that the CSVParser will treat as the quotation
   * character.
   */
  private final char m_cQuotechar;
  /**
   * This is the character that the CSVParser will treat as the escape
   * character.
   */
  private final char m_cEscape;
  /**
   * Determines if the field is between quotes (true) or between separators
   * (false).
   */
  private final boolean m_bStrictQuotes;
  /**
   * Ignore any leading white space at the start of the field.
   */
  private final boolean m_bIgnoreLeadingWhiteSpace;
  /**
   * Skip over quotation characters when parsing.
   */
  private final boolean m_bIgnoreQuotations;
  private String m_sPending;
  private boolean m_bInField = false;

  /**
   * Constructs CSVParser using a comma for the separator.
   */
  public CSVParser ()
  {
    this (CCSV.DEFAULT_SEPARATOR, CCSV.DEFAULT_QUOTE_CHARACTER, CCSV.DEFAULT_ESCAPE_CHARACTER);
  }

  /**
   * Constructs CSVParser with supplied separator.
   *
   * @param cSeparator
   *        the delimiter to use for separating entries.
   */
  public CSVParser (final char cSeparator)
  {
    this (cSeparator, CCSV.DEFAULT_QUOTE_CHARACTER, CCSV.DEFAULT_ESCAPE_CHARACTER);
  }

  /**
   * Constructs CSVParser with supplied separator and quote char.
   *
   * @param cSeparator
   *        the delimiter to use for separating entries
   * @param cQuotechar
   *        the character to use for quoted elements
   */
  public CSVParser (final char cSeparator, final char cQuotechar)
  {
    this (cSeparator, cQuotechar, CCSV.DEFAULT_ESCAPE_CHARACTER);
  }

  /**
   * Constructs CSVReader with supplied separator and quote char.
   *
   * @param cSeparator
   *        the delimiter to use for separating entries
   * @param cQuotechar
   *        the character to use for quoted elements
   * @param cEscape
   *        the character to use for escaping a separator or quote
   */
  public CSVParser (final char cSeparator, final char cQuotechar, final char cEscape)
  {
    this (cSeparator, cQuotechar, cEscape, CCSV.DEFAULT_STRICT_QUOTES);
  }

  /**
   * Constructs CSVParser with supplied separator and quote char. Allows setting
   * the "strict quotes" flag
   *
   * @param cSeparator
   *        the delimiter to use for separating entries
   * @param cQuotechar
   *        the character to use for quoted elements
   * @param cEscape
   *        the character to use for escaping a separator or quote
   * @param bStrictQuotes
   *        if true, characters outside the quotes are ignored
   */
  public CSVParser (final char cSeparator, final char cQuotechar, final char cEscape, final boolean bStrictQuotes)
  {
    this (cSeparator, cQuotechar, cEscape, bStrictQuotes, CCSV.DEFAULT_IGNORE_LEADING_WHITESPACE);
  }

  /**
   * Constructs CSVParser with supplied separator and quote char. Allows setting
   * the "strict quotes" and "ignore leading whitespace" flags
   *
   * @param cSeparator
   *        the delimiter to use for separating entries
   * @param cQuotechar
   *        the character to use for quoted elements
   * @param cEscape
   *        the character to use for escaping a separator or quote
   * @param bStrictQuotes
   *        if true, characters outside the quotes are ignored
   * @param bIgnoreLeadingWhiteSpace
   *        if true, white space in front of a quote in a field is ignored
   */
  public CSVParser (final char cSeparator,
                    final char cQuotechar,
                    final char cEscape,
                    final boolean bStrictQuotes,
                    final boolean bIgnoreLeadingWhiteSpace)
  {
    this (cSeparator, cQuotechar, cEscape, bStrictQuotes, bIgnoreLeadingWhiteSpace, CCSV.DEFAULT_IGNORE_QUOTATIONS);
  }

  /**
   * Constructs CSVParser with supplied separator and quote char. Allows setting
   * the "strict quotes" and "ignore leading whitespace" flags
   *
   * @param cSeparator
   *        the delimiter to use for separating entries
   * @param cQuotechar
   *        the character to use for quoted elements
   * @param cEscape
   *        the character to use for escaping a separator or quote
   * @param bStrictQuotes
   *        if true, characters outside the quotes are ignored
   * @param bIgnoreLeadingWhiteSpace
   *        if true, white space in front of a quote in a field is ignored
   * @param bIgnoreQuotations
   *        if true, treat quotations like any other character.
   */
  public CSVParser (final char cSeparator,
                    final char cQuotechar,
                    final char cEscape,
                    final boolean bStrictQuotes,
                    final boolean bIgnoreLeadingWhiteSpace,
                    final boolean bIgnoreQuotations)
  {
    if (_anyCharactersAreTheSame (cSeparator, cQuotechar, cEscape))
      throw new UnsupportedOperationException ("The separator, quote, and escape characters must be different!");
    if (cSeparator == CCSV.NULL_CHARACTER)
      throw new UnsupportedOperationException ("The separator character must be defined!");
    m_cSeparator = cSeparator;
    m_cQuotechar = cQuotechar;
    m_cEscape = cEscape;
    m_bStrictQuotes = bStrictQuotes;
    m_bIgnoreLeadingWhiteSpace = bIgnoreLeadingWhiteSpace;
    m_bIgnoreQuotations = bIgnoreQuotations;
  }

  /**
   * @return The default separator for this parser.
   */
  public char getSeparator ()
  {
    return m_cSeparator;
  }

  /**
   * @return The default quotation character for this parser.
   */
  public char getQuotechar ()
  {
    return m_cQuotechar;
  }

  /**
   * @return The default escape character for this parser.
   */
  public char getEscape ()
  {
    return m_cEscape;
  }

  /**
   * @return The default strictQuotes setting for this parser.
   */
  public boolean isStrictQuotes ()
  {
    return m_bStrictQuotes;
  }

  /**
   * @return The default ignoreLeadingWhiteSpace setting for this parser.
   */
  public boolean isIgnoreLeadingWhiteSpace ()
  {
    return m_bIgnoreLeadingWhiteSpace;
  }

  /**
   * @return the default ignoreQuotation setting for this parser.
   */
  public boolean isIgnoreQuotations ()
  {
    return m_bIgnoreQuotations;
  }

  /**
   * checks to see if any two of the three characters are the same. This is
   * because in openCSV the separator, quote, and escape characters must the
   * different.
   *
   * @param cSeparator
   *        the defined separator character
   * @param cQuoteChar
   *        the defined quotation character
   * @param cEscape
   *        the defined escape character
   * @return true if any two of the three are the same.
   */
  private boolean _anyCharactersAreTheSame (final char cSeparator, final char cQuoteChar, final char cEscape)
  {
    return _isSameCharacter (cSeparator, cQuoteChar) ||
           _isSameCharacter (cSeparator, cEscape) ||
           _isSameCharacter (cQuoteChar, cEscape);
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
    StringBuilder aSB = new StringBuilder (CCSV.INITIAL_READ_SIZE);
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
      if (c == m_cEscape)
      {
        if (isNextCharacterEscapable (sNextLine, _isInQuotes (bInQuotes), nIndex))
        {
          nIndex = _appendNextCharacterAndAdvanceLoop (sNextLine, aSB, nIndex);
        }
      }
      else
        if (c == m_cQuotechar)
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
                  sNextLine.charAt (nIndex - 1) != m_cSeparator &&
                  sNextLine.length () > (nIndex + 1) &&
                  sNextLine.charAt (nIndex + 1) != m_cSeparator)
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
          if (c == m_cSeparator && !(bInQuotes && !m_bIgnoreQuotations))
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
    return c == m_cQuotechar;
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
    return c == m_cEscape;
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
  protected boolean isNextCharacterEscapable (@Nonnull final String sNextLine, final boolean bInQuotes, final int nIndex)
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
