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
package com.helger.json.parser;

import java.io.IOException;
import java.io.Reader;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.stream.NonBlockingPushbackReader;
import com.helger.commons.string.StringHelper;
import com.helger.json.CJson;
import com.helger.json.parser.handler.IJsonParserHandler;

/**
 * This is a generic JSON parser that invokes a custom callback for all found
 * elements. This can be used as the basis for a "SAX" like JSON parsing, if
 * required.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class JsonParser
{
  private enum EStringQuoteMode
  {
    DOUBLE ('"'),
    SINGLE ('\'');

    private char m_cQuote;

    private EStringQuoteMode (final char cQuote)
    {
      m_cQuote = cQuote;
    }

    public char getQuoteChar ()
    {
      return m_cQuote;
    }

    @Nonnull
    public static EStringQuoteMode getFromCharOrDefault (final int c)
    {
      if (c == '\'')
        return SINGLE;
      // Default is double quotes
      return DOUBLE;
    }
  }

  /** The end of input special value */
  public static final int EOI = -1;

  private static final int MAX_PUSH_BACK_CHARS = 2;

  // Constructor parameters
  private final NonBlockingPushbackReader m_aReader;
  private final IJsonParserHandler m_aCallback;

  // Settings
  private boolean m_bTrackPosition = false;
  private int m_nTabSize = 8;
  private boolean m_bAlwaysUseBigNumber = false;
  private boolean m_bRequireStringQuotes = true;
  private boolean m_bAllowSpecialCharsInStrings = false;

  // Status variables
  // Position tracking
  private final JsonParsePosition m_aPos = new JsonParsePosition ();
  private int m_nBackupChars = 0;

  public JsonParser (@Nonnull @WillNotClose final Reader aReader, @Nonnull final IJsonParserHandler aCallback)
  {
    ValueEnforcer.notNull (aReader, "Reader");
    ValueEnforcer.notNull (aCallback, "Callback");
    // Maximum of 2 pushbacks
    m_aReader = new NonBlockingPushbackReader (aReader, MAX_PUSH_BACK_CHARS);
    m_aCallback = aCallback;
  }

  public boolean isTrackPosition ()
  {
    return m_bTrackPosition;
  }

  @Nonnull
  public JsonParser setTrackPosition (final boolean bTrackPosition)
  {
    m_bTrackPosition = bTrackPosition;
    return this;
  }

  @Nonnegative
  public int getTabSize ()
  {
    return m_nTabSize;
  }

  @Nonnull
  public JsonParser setTabSize (@Nonnegative final int nTabSize)
  {
    ValueEnforcer.isGT0 (nTabSize, "TabSize");
    m_nTabSize = nTabSize;
    return this;
  }

  public boolean isAlwaysUseBigNumber ()
  {
    return m_bAlwaysUseBigNumber;
  }

  @Nonnull
  public JsonParser setAlwaysUseBigNumber (final boolean bAlwaysUseBigNumber)
  {
    m_bAlwaysUseBigNumber = bAlwaysUseBigNumber;
    return this;
  }

  public boolean isRequireStringQuotes ()
  {
    return m_bRequireStringQuotes;
  }

  @Nonnull
  public JsonParser setRequireStringQuotes (final boolean bRequireStringQuotes)
  {
    m_bRequireStringQuotes = bRequireStringQuotes;
    return this;
  }

  public boolean isAllowSpecialCharsInStrings ()
  {
    return m_bAllowSpecialCharsInStrings;
  }

  @Nonnull
  public JsonParser setAllowSpecialCharsInStrings (final boolean bAllowSpecialCharsInStrings)
  {
    m_bAllowSpecialCharsInStrings = bAllowSpecialCharsInStrings;
    return this;
  }

  /**
   * @return The current line number. First line has a value of 1.
   */
  @Nonnegative
  public int getLineNumber ()
  {
    return m_aPos.getLineNumber ();
  }

  /**
   * @return The current column number. First column has a value of 1.
   */
  @Nonnegative
  public int getColumn ()
  {
    return m_aPos.getColumnNumber ();
  }

  /**
   * Must return int to differentiate between the whole char range (0-0xffff)
   * and EOF (-1).
   *
   * @return the char read or {@link #EOI} (=-1) in case of EOF
   */
  private int _readChar ()
  {
    try
    {
      final int c = m_aReader.read ();

      if (m_bTrackPosition)
      {
        if (m_nBackupChars > 0)
        {
          // If previously a char was backed up, don't increase the position!
          m_nBackupChars--;
        }
        else
          m_aPos.updatePosition (c, m_nTabSize);
      }
      return c;
    }
    catch (final IOException ex)
    {
      return EOI;
    }
  }

  /**
   * Backup the provided char if it is not end of input
   *
   * @param c
   *        char to backup
   * @throws JsonParseException
   *         in case of error
   */
  private void _backupChar (final int c)
  {
    if (c != EOI)
      try
      {
        m_aReader.unread (c);
        m_nBackupChars++;
      }
      catch (final IOException ex)
      {
        throw new IllegalStateException ("Failed to unread character " + _getPrintableChar (c));
      }
  }

  @Nonnull
  private static String _getPrintableChar (final int c)
  {
    if (c == EOI)
      return "<EOI>";
    if (c <= 32)
      return "0x" + StringHelper.getHexStringLeadingZero (c, 2);
    if (c >= 127)
      return "0x" + StringHelper.getHexStringLeadingZero (c, 4);
    return "'" + (char) c + "'";
  }

  @Nonnull
  private JsonParseException _parseEx (@Nonnull final IJsonParsePosition aTokenStart, @Nonnull final String sMsg)
  {
    if (m_bTrackPosition)
      return new JsonParseException (aTokenStart, m_aPos, sMsg);

    return new JsonParseException (sMsg);
  }

  private void _readComment () throws JsonParseException
  {
    final IJsonParsePosition aStartPos = m_aPos.getClone ();
    final JsonStringBuilder aStrComment = new JsonStringBuilder (1024);

    while (true)
    {
      final int c1 = _readChar ();
      if (c1 == '*')
      {
        // End of comment?
        final int c2 = _readChar ();
        if (c2 == '/')
        {
          // End of comment!
          m_aCallback.onComment (aStrComment.getAsString ());
          return;
        }
        if (c2 == EOI)
          throw _parseEx (aStartPos, "Unclosed JSON comment at end of input");

        // Backup the "/" try
        _backupChar (c2);
      }

      if (c1 == EOI)
        throw _parseEx (aStartPos, "Unclosed JSON comment at end of input");

      aStrComment.append ((char) c1);
    }
  }

  private void _skipSpaces () throws JsonParseException
  {
    final JsonStringBuilder aStrSpaces = new JsonStringBuilder ();

    while (true)
    {
      final int c = _readChar ();

      // Check for comment
      if (c == '/')
      {
        final int c2 = _readChar ();
        if (c2 == '*')
        {
          if (aStrSpaces.hasContent ())
          {
            // Notify on previous whitespaces
            m_aCallback.onWhitespace (aStrSpaces.getAsString ());
            aStrSpaces.reset ();
          }

          // start comment
          _readComment ();

          // Finished comment - check for next whitespace
          continue;
        }
        // backup c2 as it is no comment
        _backupChar (c2);
      }

      if (c != ' ' && c != '\t' && c != '\r' && c != '\n' && c != '\f')
      {
        // End of whitespaces reached
        if (aStrSpaces.hasContent ())
          m_aCallback.onWhitespace (aStrSpaces.getAsString ());

        // backup c - if previously c2 was backed up this is where we need 2
        // chars pushback :)
        _backupChar (c);
        return;
      }

      // It's a whitespace character
      aStrSpaces.append ((char) c);
    }
  }

  /**
   * @param c
   *        character
   * @return The int representation of the read hex char (0-9, a-f)
   * @throws JsonParseException
   *         In case a non hex char was encountered
   */
  private int _getHexValue (@Nonnull final IJsonParsePosition aStartPos, final int c) throws JsonParseException
  {
    final int ret = StringHelper.getHexValue ((char) c);
    if (ret == -1)
      throw _parseEx (aStartPos, "Invalid hex character " + _getPrintableChar (c) + " provided!");
    return ret;
  }

  private void _readStringEscapeChar (@Nonnull final IJsonParsePosition aStartPos,
                                      @Nonnull final JsonStringBuilder aStrStringOriginalContent,
                                      @Nonnull final JsonStringBuilder aStrStringUnescapedContent) throws JsonParseException
  {
    final int c2 = _readChar ();
    aStrStringOriginalContent.append ((char) c2);
    if (c2 == '"' || c2 == '/' || c2 == '\\')
      aStrStringUnescapedContent.append ((char) c2);
    else
      if (c2 == 'b')
        aStrStringUnescapedContent.append ('\b');
      else
        if (c2 == 'f')
          aStrStringUnescapedContent.append ('\f');
        else
          if (c2 == 'n')
            aStrStringUnescapedContent.append ('\n');
          else
            if (c2 == 'r')
              aStrStringUnescapedContent.append ('\r');
            else
              if (c2 == 't')
                aStrStringUnescapedContent.append ('\t');
              else
                if (c2 == 'u')
                {
                  final int ch0 = _readChar ();
                  final int n0 = _getHexValue (aStartPos, ch0);
                  final int ch1 = _readChar ();
                  final int n1 = _getHexValue (aStartPos, ch1);
                  final int ch2 = _readChar ();
                  final int n2 = _getHexValue (aStartPos, ch2);
                  final int ch3 = _readChar ();
                  final int n3 = _getHexValue (aStartPos, ch3);
                  aStrStringOriginalContent.append ((char) ch0);
                  aStrStringOriginalContent.append ((char) ch1);
                  aStrStringOriginalContent.append ((char) ch2);
                  aStrStringOriginalContent.append ((char) ch3);

                  final int nUnescapedChar = (n0 & 0xff) << 12 | (n1 & 0xff) << 8 | (n2 & 0xff) << 4 | (n3 & 0xff);
                  aStrStringUnescapedContent.append ((char) nUnescapedChar);
                }
                else
                  throw _parseEx (aStartPos, "Invalid JSON String escape character " + _getPrintableChar (c2));
  }

  private static boolean _isUnquotedStringValidChar (final int c)
  {
    return (c >= 0x21 && c <= 0x7a) && c != ':';
  }

  private static final class TwoStrings
  {
    private final String m_sOriginal;
    private final String m_sUnescaped;

    private TwoStrings (@Nonnull final String sOriginal, @Nonnull final String sUnescaped)
    {
      m_sOriginal = sOriginal;
      m_sUnescaped = sUnescaped;
    }
  }

  /**
   * @param EStringQuoteMode
   *        The quoting mode used. May not be <code>null</code>.
   * @return A pair where the first string is the original read string whereas
   *         the second part is the unescaped read string without leading and
   *         trailing quotes
   * @throws JsonParseException
   */
  @Nonnull
  private TwoStrings _readString (@Nonnull final EStringQuoteMode eQuoteMode) throws JsonParseException
  {
    final IJsonParsePosition aStartPos = m_aPos.getClone ();
    final JsonStringBuilder aStrStringOriginalContent = new JsonStringBuilder (256);
    final JsonStringBuilder aStrStringUnescapedContent = new JsonStringBuilder (256);

    final int cQuoteChar = eQuoteMode.getQuoteChar ();

    final int cStart = _readChar ();
    final boolean bStringIsQuoted = cStart == cQuoteChar;
    if (bStringIsQuoted)
    {
      aStrStringOriginalContent.append ((char) cQuoteChar);
    }
    else
    {
      if (m_bRequireStringQuotes)
        throw _parseEx (aStartPos,
                        "Invalid JSON String start character " +
                                   _getPrintableChar (cStart) +
                                   " - expected " +
                                   _getPrintableChar (cQuoteChar));

      _backupChar (cStart);
      aStrStringOriginalContent.append ((char) cQuoteChar);
    }

    outer: while (true)
    {
      final int c = _readChar ();
      aStrStringOriginalContent.append ((char) c);

      switch (c)
      {
        case '\\':
        {
          // Escape char
          _readStringEscapeChar (aStartPos, aStrStringOriginalContent, aStrStringUnescapedContent);
          break;
        }
        case EOI:
          throw _parseEx (aStartPos, "Unclosed JSON String at end of input");
        case '\b':
        case '\f':
        case '\n':
        case '\r':
        case '\t':
          if (!m_bAllowSpecialCharsInStrings)
            throw _parseEx (aStartPos, "Invalid JSON String character " + _getPrintableChar (c));
          // else fall-though!
        default:
          if (bStringIsQuoted)
          {
            if (c == cQuoteChar)
            {
              // End of quoted string
              // No append to unescaped content
              break outer;
            }
          }
          else
          {
            if (!_isUnquotedStringValidChar (c))
            {
              // End of unquoted string

              // Remove from original content
              _backupChar (c);
              aStrStringOriginalContent.backup (1);

              if (aStrStringUnescapedContent.getLength () == 0)
                throw _parseEx (aStartPos, "Empty unquoted JSON String encountered");

              // Since it is present on open, it must also be present on close
              aStrStringOriginalContent.append ((char) cQuoteChar);
              break outer;
            }
          }

          // Regular string character
          aStrStringUnescapedContent.append ((char) c);
          break;
      }
    }

    return new TwoStrings (aStrStringOriginalContent.getAsString (), aStrStringUnescapedContent.getAsString ());
  }

  @Nonnull
  private static Number _parseNumberInt (@Nonnull final JsonStringBuilder s)
  {
    final int nLen = s.getLength ();

    // pos
    int nPos = 0;
    // max pos long base 10 len
    int nMax = 19;

    final boolean bNeg = s.charAt (0) == '-';
    if (bNeg)
    {
      nPos++;
      nMax++;
    }

    if (nLen > nMax)
    {
      // BigInteger anyway
      return s.getAsBigInteger ();
    }

    boolean bMustCheck;
    if (nLen < nMax)
    {
      // Never BigInteger
      nMax = nLen;
      bMustCheck = false;
    }
    else
    {
      // nLen == nMax
      nMax = nLen - 1;
      bMustCheck = true;
    }

    // r gets negative since the negative maximas are 1 "larger" than the
    // positive maximas
    long r = 0;
    while (nPos < nMax)
      r = (r * 10L) + ('0' - s.charAt (nPos++));

    if (bMustCheck)
    {
      boolean bIsBig;
      if (r > -922337203685477580L)
      {
        bIsBig = false;
      }
      else
        if (r < -922337203685477580L)
        {
          bIsBig = true;
        }
        else
        {
          if (bNeg)
            bIsBig = s.charAt (nPos) > '8';
          else
            bIsBig = s.charAt (nPos) > '7';
        }
      if (bIsBig)
        return s.getAsBigInteger ();

      // Add the last char
      r = (r * 10L) + ('0' - s.charAt (nPos));
    }

    if (!bNeg)
      r = -r;

    if (r >= Integer.MIN_VALUE && r <= Integer.MAX_VALUE)
      return Integer.valueOf ((int) r);

    return Long.valueOf (r);
  }

  @Nonnull
  private Number _parseNumber (@Nonnull final IJsonParsePosition aStartPos,
                               final boolean bIsDecimal,
                               final boolean bHasExponent,
                               final boolean bHasPositiveExponent,
                               @Nonnull final JsonStringBuilder aNumChars) throws JsonParseException
  {
    try
    {
      final int nCharCount = aNumChars.getLength ();

      if (bIsDecimal)
      {
        // Decimal number
        if (nCharCount > 18 || m_bAlwaysUseBigNumber)
          return aNumChars.getAsBigDecimal ();

        return aNumChars.getAsDouble ();
      }

      if (bHasExponent)
      {
        // Integer number
        if (bHasPositiveExponent)
        {
          // Required for correct "e" handling
          return aNumChars.getAsBigDecimal ().toBigIntegerExact ();
        }

        // Required for correct "e" handling
        return aNumChars.getAsBigDecimal ();
      }

      // No exponent present
      if (m_bAlwaysUseBigNumber)
        return aNumChars.getAsBigInteger ();

      return _parseNumberInt (aNumChars);
    }
    catch (final NumberFormatException ex)
    {
      // This should never happen, as our consistency check beforehand are quite
      // okay :)
      throw _parseEx (aStartPos, "Invalid JSON Number '" + aNumChars.getAsString () + "'");
    }
  }

  private void _readNumber () throws JsonParseException
  {
    final IJsonParsePosition aStartPos = m_aPos.getClone ();

    final JsonStringBuilder aStrNumber = new JsonStringBuilder (32);
    int c = _readChar ();
    if (c == '-')
    {
      // Leading minus?
      // Note: leading plus is not allowed
      aStrNumber.append ((char) c);
      c = _readChar ();
    }

    if (c == '0')
    {
      // No additional numbers allowed
      aStrNumber.append ((char) c);
      c = _readChar ();
    }
    else
      if (c >= '1' && c <= '9')
      {
        aStrNumber.append ((char) c);
        c = _readChar ();
        while (c >= '0' && c <= '9')
        {
          aStrNumber.append ((char) c);
          c = _readChar ();
        }
      }
      else
        throw _parseEx (aStartPos, "Invalid JSON Number start character " + _getPrintableChar (c));

    final boolean bIsDecimal = c == '.';
    if (bIsDecimal)
    {
      // read decimal part
      aStrNumber.append ((char) c);
      c = _readChar ();
      boolean bDecimalDigits = false;
      while (c >= '0' && c <= '9')
      {
        aStrNumber.append ((char) c);
        bDecimalDigits = true;

        c = _readChar ();
      }
      if (!bDecimalDigits)
        throw _parseEx (aStartPos,
                        "Missing digits after decimal point in JSON Number '" + aStrNumber.getAsString () + "'");
    }

    final boolean bHasExponent = c == 'e' || c == 'E';
    boolean bHasPositiveExponent = false;
    if (bHasExponent)
    {
      // read exponent
      aStrNumber.append ((char) c);
      c = _readChar ();

      // Any char other than "-" means the exponent is positive
      bHasPositiveExponent = c != '-';

      if (c == '+' || c == '-')
      {
        aStrNumber.append ((char) c);
        c = _readChar ();
      }

      boolean bExponentDigits = false;
      while (c >= '0' && c <= '9')
      {
        aStrNumber.append ((char) c);
        bExponentDigits = true;

        c = _readChar ();
      }
      if (!bExponentDigits)
        throw _parseEx (aStartPos,
                        "Missing digits after exponent sign in JSON Number '" + aStrNumber.getAsString () + "'");
    }

    // Backup last (unused) char
    _backupChar (c);

    final Number aNum = _parseNumber (aStartPos, bIsDecimal, bHasExponent, bHasPositiveExponent, aStrNumber);
    m_aCallback.onNumber (aStrNumber.getAsString (), aNum);
  }

  private void _expect (@Nonnull final String sKeyword) throws JsonParseException
  {
    final IJsonParsePosition aStartPos = m_aPos.getClone ();

    for (final char cExpected : sKeyword.toCharArray ())
    {
      final int c = _readChar ();
      if (c != cExpected)
        throw _parseEx (aStartPos,
                        "Expected " +
                                   _getPrintableChar (cExpected) +
                                   " but got " +
                                   _getPrintableChar (c) +
                                   " as part of JSON keyword \"" +
                                   sKeyword +
                                   "\"");
    }
  }

  private void _readArray () throws JsonParseException
  {
    final IJsonParsePosition aStartPos = m_aPos.getClone ();

    m_aCallback.onArrayStart ();
    int nIndex = 0;
    while (true)
    {
      _skipSpaces ();

      // Check for empty array
      int c = _readChar ();
      if (c == CJson.ARRAY_END)
      {
        if (nIndex != 0)
          throw _parseEx (aStartPos, "Expected another element in JSON Array");
        break;
      }
      _backupChar (c);

      _readValue ();

      _skipSpaces ();

      c = _readChar ();
      if (c == CJson.ITEM_SEPARATOR)
      {
        ++nIndex;
        m_aCallback.onArrayNextElement ();
        continue;
      }
      if (c == CJson.ARRAY_END)
        break;
      throw _parseEx (aStartPos, "Unexpected character " + _getPrintableChar (c) + " in JSON array");
    }
    m_aCallback.onArrayEnd ();
  }

  private void _readObject () throws JsonParseException
  {
    final IJsonParsePosition aStartPos = m_aPos.getClone ();

    m_aCallback.onObjectStart ();
    int nIndex = 0;
    while (true)
    {
      _skipSpaces ();

      // Check for empty object
      int c = _readChar ();
      if (c == CJson.OBJECT_END)
      {
        if (nIndex != 0)
          throw _parseEx (aStartPos, "Expected another element in JSON Object");
        break;
      }
      final EStringQuoteMode eQuoteMode = EStringQuoteMode.getFromCharOrDefault (c);
      _backupChar (c);

      final TwoStrings aName = _readString (eQuoteMode);
      m_aCallback.onObjectName (aName.m_sOriginal, aName.m_sUnescaped);

      _skipSpaces ();

      c = _readChar ();
      if (c != CJson.NAME_VALUE_SEPARATOR)
        throw _parseEx (aStartPos, "Expected colon character in JSON Object but found " + _getPrintableChar (c));
      m_aCallback.onObjectColon ();

      _skipSpaces ();

      _readValue ();

      _skipSpaces ();

      c = _readChar ();
      if (c == CJson.ITEM_SEPARATOR)
      {
        ++nIndex;
        m_aCallback.onObjectNextElement ();
        continue;
      }
      if (c == CJson.OBJECT_END)
        break;
      throw _parseEx (aStartPos, "Unexpected character " + _getPrintableChar (c) + " in JSON Object");
    }
    m_aCallback.onObjectEnd ();
  }

  private void _readValue () throws JsonParseException
  {
    _skipSpaces ();

    final IJsonParsePosition aStartPos = m_aPos.getClone ();

    final int cFirst = _readChar ();
    switch (cFirst)
    {
      case '\'':
      {
        _backupChar (cFirst);
        final TwoStrings aString = _readString (EStringQuoteMode.SINGLE);
        m_aCallback.onString (aString.m_sOriginal, aString.m_sUnescaped);
        break;
      }
      case '"':
      {
        _backupChar (cFirst);
        final TwoStrings aString = _readString (EStringQuoteMode.DOUBLE);
        m_aCallback.onString (aString.m_sOriginal, aString.m_sUnescaped);
        break;
      }
      case '-':
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        _backupChar (cFirst);
        _readNumber ();
        break;
      case 't':
        _backupChar (cFirst);
        _expect (CJson.KEYWORD_TRUE);
        m_aCallback.onTrue ();
        break;
      case 'f':
        _backupChar (cFirst);
        _expect (CJson.KEYWORD_FALSE);
        m_aCallback.onFalse ();
        break;
      case 'n':
        _backupChar (cFirst);
        _expect (CJson.KEYWORD_NULL);
        m_aCallback.onNull ();
        break;
      case CJson.ARRAY_START:
        _readArray ();
        break;
      case CJson.OBJECT_START:
        _readObject ();
        break;
      default:
        throw _parseEx (aStartPos, "Syntax error in JSON. Found " + _getPrintableChar (cFirst));
    }
  }

  /**
   * Main parsing routine
   *
   * @throws JsonParseException
   *         In case a parse error occurs.
   */
  public void parse () throws JsonParseException
  {
    _readValue ();

    // Check for trailing whitespaces
    _skipSpaces ();

    final IJsonParsePosition aStartPos = m_aPos.getClone ();

    // Check for expected end of input
    final int c = _readChar ();
    if (c != EOI)
      throw _parseEx (aStartPos, "Invalid character " + _getPrintableChar (c) + " after JSON root object");
  }
}
