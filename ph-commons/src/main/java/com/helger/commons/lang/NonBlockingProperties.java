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
package com.helger.commons.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

import com.helger.commons.charset.CCharset;
import com.helger.commons.io.stream.NonBlockingBufferedWriter;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.system.ENewLineMode;

/**
 * The <code>NonBlockingProperties</code> class represents a persistent set of
 * properties. The <code>NonBlockingProperties</code> can be saved to a stream
 * or loaded from a stream. Each key and its corresponding value in the property
 * list is a string.
 * <p>
 * A property list can contain another property list as its "defaults"; this
 * second property list is searched if the property key is not found in the
 * original property list.
 * <p>
 * Because <code>NonBlockingProperties</code> inherits from <code>TreeMap</code>
 * , the <code>put</code> and <code>putAll</code> methods can be applied to a
 * <code>NonBlockingProperties</code> object. Their use is strongly discouraged
 * as they allow the caller to insert entries whose keys or values are not
 * <code>Strings</code>. The <code>setProperty</code> method should be used
 * instead. If the <code>store</code> or <code>save</code> method is called on a
 * "compromised" <code>NonBlockingProperties</code> object that contains a non-
 * <code>String</code> key or value, the call will fail. Similarly, the call to
 * the <code>propertyNames</code> or <code>list</code> method will fail if it is
 * called on a "compromised" <code>NonBlockingProperties</code> object that
 * contains a non- <code>String</code> key.
 * <p>
 * The {@link #load(java.io.Reader) load(Reader)} <tt>/</tt>
 * {@link #store(java.io.Writer, java.lang.String) store(Writer, String)}
 * methods load and store properties from and to a character based stream in a
 * simple line-oriented format specified below. The
 * {@link #load(java.io.InputStream) load(InputStream)} <tt>/</tt>
 * {@link #store(java.io.OutputStream, java.lang.String) store(OutputStream,
 * String)} methods work the same way as the load(Reader)/store(Writer, String)
 * pair, except the input/output stream is encoded in ISO 8859-1 character
 * encoding. Characters that cannot be directly represented in this encoding can
 * be written using <a href=
 * "http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.3" >
 * Unicode escapes</a> ; only a single 'u' character is allowed in an escape
 * sequence. The native2ascii tool can be used to convert property files to and
 * from other character encodings.
 *
 * @author Arthur van Hoff
 * @author Michael McCloskey
 * @author Xueming Shen
 * @author Philip Helger
 */
public class NonBlockingProperties extends TreeMap <String, String>
{
  /**
   * A property list that contains default values for any keys not found in this
   * property list.
   *
   * @serial
   */
  protected NonBlockingProperties m_aDefaults;

  /**
   * Creates an empty property list with no default values.
   */
  public NonBlockingProperties ()
  {
    this (null);
  }

  /**
   * Creates an empty property list with the specified defaults.
   *
   * @param aDefaults
   *        the defaults.
   */
  public NonBlockingProperties (@Nullable final NonBlockingProperties aDefaults)
  {
    m_aDefaults = aDefaults;
  }

  /**
   * @return The default properties as passed in the constructor. May be
   *         <code>null</code>.
   */
  @Nullable
  public NonBlockingProperties getDefaults ()
  {
    return m_aDefaults;
  }

  /**
   * Calls the <tt>Hashtable</tt> method <code>put</code>. Provided for
   * parallelism with the <tt>getProperty</tt> method. Enforces use of strings
   * for property keys and values. The value returned is the result of the
   * <tt>Hashtable</tt> call to <code>put</code>.
   *
   * @param sKey
   *        the key to be placed into this property list.
   * @param sValue
   *        the value corresponding to <tt>key</tt>.
   * @return the previous value of the specified key in this property list, or
   *         <code>null</code> if it did not have one.
   * @see #getProperty
   * @since 1.2
   */
  @Nullable
  public String setProperty (final String sKey, final String sValue)
  {
    return put (sKey, sValue);
  }

  /**
   * Reads a property list (key and element pairs) from the input character
   * stream in a simple line-oriented format.
   * <p>
   * Properties are processed in terms of lines. There are two kinds of line,
   * <i>natural lines</i> and <i>logical lines</i>. A natural line is defined as
   * a line of characters that is terminated either by a set of line terminator
   * characters (<code>\n</code> or <code>\r</code> or <code>\r\n</code>) or by
   * the end of the stream. A natural line may be either a blank line, a comment
   * line, or hold all or some of a key-element pair. A logical line holds all
   * the data of a key-element pair, which may be spread out across several
   * adjacent natural lines by escaping the line terminator sequence with a
   * backslash character <code>\</code>. Note that a comment line cannot be
   * extended in this manner; every natural line that is a comment must have its
   * own comment indicator, as described below. Lines are read from input until
   * the end of the stream is reached.
   * </p>
   * <p>
   * A natural line that contains only white space characters is considered
   * blank and is ignored. A comment line has an ASCII <code>'#'</code> or
   * <code>'!'</code> as its first non-white space character; comment lines are
   * also ignored and do not encode key-element information. In addition to line
   * terminators, this format considers the characters space (<code>' '</code>,
   * <code>'&#92;u0020'</code>), tab (<code>'\t'</code>,
   * <code>'&#92;u0009'</code>), and form feed (<code>'\f'</code>,
   * <code>'&#92;u000C'</code>) to be white space.
   * </p>
   * <p>
   * If a logical line is spread across several natural lines, the backslash
   * escaping the line terminator sequence, the line terminator sequence, and
   * any white space at the start of the following line have no affect on the
   * key or element values. The remainder of the discussion of key and element
   * parsing (when loading) will assume all the characters constituting the key
   * and element appear on a single natural line after line continuation
   * characters have been removed. Note that it is <i>not</i> sufficient to only
   * examine the character preceding a line terminator sequence to decide if the
   * line terminator is escaped; there must be an odd number of contiguous
   * backslashes for the line terminator to be escaped. Since the input is
   * processed from left to right, a non-zero even number of 2<i>n</i>
   * contiguous backslashes before a line terminator (or elsewhere) encodes
   * <i>n</i> backslashes after escape processing.
   * </p>
   * <p>
   * The key contains all of the characters in the line starting with the first
   * non-white space character and up to, but not including, the first unescaped
   * <code>'='</code>, <code>':'</code>, or white space character other than a
   * line terminator. All of these key termination characters may be included in
   * the key by escaping them with a preceding backslash character; for example,
   * </p>
   * <p>
   * <code>\:\=</code>
   * </p>
   * <p>
   * would be the two-character key <code>":="</code>. Line terminator
   * characters can be included using <code>\r</code> and <code>\n</code> escape
   * sequences. Any white space after the key is skipped; if the first non-white
   * space character after the key is <code>'='</code> or <code>':'</code>, then
   * it is ignored and any white space characters after it are also skipped. All
   * remaining characters on the line become part of the associated element
   * string; if there are no remaining characters, the element is the empty
   * string <code>&quot;&quot;</code>. Once the raw character sequences
   * constituting the key and element are identified, escape processing is
   * performed as described above.
   * </p>
   * <p>
   * As an example, each of the following three lines specifies the key
   * <code>"Truth"</code> and the associated element value <code>"Beauty"</code>
   * :
   * </p>
   *
   * <pre>
   * Truth = Beauty
   *  Truth:Beauty
   * Truth      :Beauty
   * </pre>
   * <p>
   * As another example, the following three lines specify a single property:
   * </p>
   *
   * <pre>
   * fruits                           apple, banana, pear, \
   *                                  cantaloupe, watermelon, \
   *                                  kiwi, mango
   * </pre>
   * <p>
   * The key is <code>"fruits"</code> and the associated element is:
   * </p>
   *
   * <pre>
   * &quot;apple, banana, pear, cantaloupe, watermelon, kiwi, mango&quot;
   * </pre>
   * <p>
   * Note that a space appears before each <code>\</code> so that a space will
   * appear after each comma in the final result; the <code>\</code>, line
   * terminator, and leading white space on the continuation line are merely
   * discarded and are <i>not</i> replaced by one or more other characters.
   * </p>
   * <p>
   * As a third example, the line:
   * </p>
   *
   * <pre>
   * cheeses
   * </pre>
   * <p>
   * specifies that the key is <code>"cheeses"</code> and the associated element
   * is the empty string <code>""</code>.
   * </p>
   * <p>
   * <a name="unicodeescapes"></a> Characters in keys and elements can be
   * represented in escape sequences similar to those used for character and
   * string literals (see <a href=
   * "http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.3" >
   * &sect;3.3</a> and <a href=
   * "http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.6"
   * >&sect;3.10.6</a> of the <i>Java Language Specification</i>). The
   * differences from the character escape sequences and Unicode escapes used
   * for characters and strings are:
   * </p>
   * <ul>
   * <li>Octal escapes are not recognized.
   * <li>The character sequence <code>\b</code> does <i>not</i> represent a
   * backspace character.
   * <li>The method does not treat a backslash character, <code>\</code>, before
   * a non-valid escape character as an error; the backslash is silently
   * dropped. For example, in a Java string the sequence <code>"\z"</code> would
   * cause a compile time error. In contrast, this method silently drops the
   * backslash. Therefore, this method treats the two character sequence
   * <code>"\b"</code> as equivalent to the single character <code>'b'</code>.
   * <li>Escapes are not necessary for single and double quotes; however, by the
   * rule above, single and double quote characters preceded by a backslash
   * still yield single and double quote characters, respectively.
   * <li>Only a single 'u' character is allowed in a Unicode escape sequence.
   * </ul>
   * <p>
   * The specified stream remains open after this method returns.
   * </p>
   *
   * @param aReader
   *        the input character stream.
   * @throws IOException
   *         if an error occurred when reading from the input stream.
   * @throws IllegalArgumentException
   *         if a malformed Unicode escape appears in the input.
   * @since 1.6
   */
  public void load (@WillNotClose final Reader aReader) throws IOException
  {
    _load (new LineReader (aReader));
  }

  /**
   * Reads a property list (key and element pairs) from the input byte stream.
   * The input stream is in a simple line-oriented format as specified in
   * {@link #load(java.io.Reader) load(Reader)} and is assumed to use the ISO
   * 8859-1 character encoding; that is each byte is one Latin1 character.
   * Characters not in Latin1, and certain special characters, are represented
   * in keys and elements using <a href=
   * "http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.3" >
   * Unicode escapes</a>.
   * <p>
   * The specified stream remains open after this method returns.
   *
   * @param aIS
   *        the input stream.
   * @exception IOException
   *            if an error occurred when reading from the input stream.
   * @throws IllegalArgumentException
   *         if the input stream contains a malformed Unicode escape sequence.
   * @since 1.2
   */
  public void load (@WillNotClose final InputStream aIS) throws IOException
  {
    _load (new LineReader (aIS));
  }

  private void _load (@WillNotClose final LineReader aLineReader) throws IOException
  {
    final char [] aBuf = new char [1024];
    int nLimit;
    while ((nLimit = aLineReader.readLine ()) >= 0)
    {
      char c = 0;
      int nKeyLen = 0;
      int nValueStart = nLimit;
      boolean bHasSep = false;

      // System.out.println("line=<" + new String(lineBuf, 0, limit) + ">");
      boolean bPrecedingBackslash = false;
      while (nKeyLen < nLimit)
      {
        c = aLineReader.m_aLineBuf[nKeyLen];
        // need check if escaped.
        if ((c == '=' || c == ':') && !bPrecedingBackslash)
        {
          nValueStart = nKeyLen + 1;
          bHasSep = true;
          break;
        }
        else
          if ((c == ' ' || c == '\t' || c == '\f') && !bPrecedingBackslash)
          {
            nValueStart = nKeyLen + 1;
            break;
          }
        if (c == '\\')
          bPrecedingBackslash = !bPrecedingBackslash;
        else
          bPrecedingBackslash = false;
        nKeyLen++;
      }
      while (nValueStart < nLimit)
      {
        c = aLineReader.m_aLineBuf[nValueStart];
        if (c != ' ' && c != '\t' && c != '\f')
        {
          if (!bHasSep && (c == '=' || c == ':'))
            bHasSep = true;
          else
            break;
        }
        nValueStart++;
      }
      final String sKey = _loadConvert (aLineReader.m_aLineBuf, 0, nKeyLen, aBuf);
      final String value = _loadConvert (aLineReader.m_aLineBuf, nValueStart, nLimit - nValueStart, aBuf);
      put (sKey, value);
    }
  }

  /*
   * Read in a "logical line" from an InputStream/Reader, skip all comment and
   * blank lines and filter out those leading whitespace characters ( , and )
   * from the beginning of a "natural line". Method returns the char length of
   * the "logical line" and stores the line in "lineBuf".
   */
  static class LineReader
  {
    private byte [] m_aInByteBuf;
    private char [] m_aInCharBuf;
    private char [] m_aLineBuf = new char [1024];
    private int m_nInLimit = 0;
    private int m_nInOff = 0;
    private InputStream m_aIS;
    private Reader m_aReader;

    public LineReader (final InputStream aIS)
    {
      m_aIS = aIS;
      m_aInByteBuf = new byte [8192];
    }

    public LineReader (final Reader aReader)
    {
      m_aReader = aReader;
      m_aInCharBuf = new char [8192];
    }

    int readLine () throws IOException
    {
      int nLen = 0;
      char c = 0;

      boolean bSkipWhiteSpace = true;
      boolean bIsCommentLine = false;
      boolean bIsNewLine = true;
      boolean bAppendedLineBegin = false;
      boolean bPrecedingBackslash = false;
      boolean bSkipLF = false;

      while (true)
      {
        if (m_nInOff >= m_nInLimit)
        {
          m_nInLimit = (m_aIS == null) ? m_aReader.read (m_aInCharBuf) : m_aIS.read (m_aInByteBuf);
          m_nInOff = 0;
          if (m_nInLimit <= 0)
          {
            if (nLen == 0 || bIsCommentLine)
              return -1;
            return nLen;
          }
        }
        if (m_aIS != null)
        {
          // The line below is equivalent to calling a
          // ISO8859-1 decoder.
          c = (char) (0xff & m_aInByteBuf[m_nInOff++]);
        }
        else
        {
          c = m_aInCharBuf[m_nInOff++];
        }
        if (bSkipLF)
        {
          bSkipLF = false;
          if (c == '\n')
            continue;
        }
        if (bSkipWhiteSpace)
        {
          if (c == ' ' || c == '\t' || c == '\f')
            continue;
          if (!bAppendedLineBegin && (c == '\r' || c == '\n'))
            continue;
          bSkipWhiteSpace = false;
          bAppendedLineBegin = false;
        }
        if (bIsNewLine)
        {
          bIsNewLine = false;
          if (c == '#' || c == '!')
          {
            bIsCommentLine = true;
            continue;
          }
        }

        if (c != '\n' && c != '\r')
        {
          m_aLineBuf[nLen++] = c;
          if (nLen == m_aLineBuf.length)
          {
            int newLength = m_aLineBuf.length * 2;
            if (newLength < 0)
            {
              newLength = Integer.MAX_VALUE;
            }
            final char [] buf = new char [newLength];
            System.arraycopy (m_aLineBuf, 0, buf, 0, m_aLineBuf.length);
            m_aLineBuf = buf;
          }
          // flip the preceding backslash flag
          if (c == '\\')
            bPrecedingBackslash = !bPrecedingBackslash;
          else
            bPrecedingBackslash = false;
        }
        else
        {
          // reached EOL
          if (bIsCommentLine || nLen == 0)
          {
            bIsCommentLine = false;
            bIsNewLine = true;
            bSkipWhiteSpace = true;
            nLen = 0;
            continue;
          }
          if (m_nInOff >= m_nInLimit)
          {
            m_nInLimit = m_aIS == null ? m_aReader.read (m_aInCharBuf) : m_aIS.read (m_aInByteBuf);
            m_nInOff = 0;
            if (m_nInLimit <= 0)
              return nLen;
          }
          if (bPrecedingBackslash)
          {
            nLen -= 1;
            // skip the leading whitespace characters in following line
            bSkipWhiteSpace = true;
            bAppendedLineBegin = true;
            bPrecedingBackslash = false;
            if (c == '\r')
              bSkipLF = true;
          }
          else
            return nLen;
        }
      }
    }
  }

  /*
   * Converts encoded &#92;uxxxx to unicode chars and changes special saved
   * chars to their original forms
   */
  @Nonnull
  private String _loadConvert (final char [] aIn, final int nOfs, final int nLen, @Nonnull final char [] aConvBuf)
  {
    int nCurOfs = nOfs;
    char [] aOut;
    if (aConvBuf.length < nLen)
    {
      int nNewLen = nLen * 2;
      if (nNewLen < 0)
        nNewLen = Integer.MAX_VALUE;
      aOut = new char [nNewLen];
    }
    else
      aOut = aConvBuf;
    char aChar;
    int nOutLen = 0;
    final int nEndOfs = nCurOfs + nLen;

    while (nCurOfs < nEndOfs)
    {
      aChar = aIn[nCurOfs++];
      if (aChar == '\\')
      {
        aChar = aIn[nCurOfs++];
        if (aChar == 'u')
        {
          // Read the xxxx
          int nValue = 0;
          for (int i = 0; i < 4; i++)
          {
            aChar = aIn[nCurOfs++];
            switch (aChar)
            {
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
                nValue = (nValue << 4) + aChar - '0';
                break;
              case 'a':
              case 'b':
              case 'c':
              case 'd':
              case 'e':
              case 'f':
                nValue = (nValue << 4) + 10 + aChar - 'a';
                break;
              case 'A':
              case 'B':
              case 'C':
              case 'D':
              case 'E':
              case 'F':
                nValue = (nValue << 4) + 10 + aChar - 'A';
                break;
              default:
                throw new IllegalArgumentException ("Malformed \\uxxxx encoding.");
            }
          }
          aOut[nOutLen++] = (char) nValue;
        }
        else
        {
          if (aChar == 't')
            aChar = '\t';
          else
            if (aChar == 'r')
              aChar = '\r';
            else
              if (aChar == 'n')
                aChar = '\n';
              else
                if (aChar == 'f')
                  aChar = '\f';
          aOut[nOutLen++] = aChar;
        }
      }
      else
      {
        aOut[nOutLen++] = aChar;
      }
    }
    return new String (aOut, 0, nOutLen);
  }

  /*
   * Converts unicodes to encoded &#92;uxxxx and escapes special characters with
   * a preceding slash
   */
  @Nonnull
  private String _saveConvert (final String sStr, final boolean bEscapeSpace, final boolean bEscapeUnicode)
  {
    final int nLen = sStr.length ();
    int nBufLen = nLen * 2;
    if (nBufLen < 0)
      nBufLen = Integer.MAX_VALUE;
    final StringBuilder aSB = new StringBuilder (nBufLen);

    for (int x = 0; x < nLen; x++)
    {
      final char aChar = sStr.charAt (x);
      // Handle common case first, selecting largest block that
      // avoids the specials below
      // 61 == '='
      if (aChar > 61 && aChar < 127)
      {
        if (aChar == '\\')
        {
          aSB.append ('\\').append ('\\');
          continue;
        }
        aSB.append (aChar);
        continue;
      }
      switch (aChar)
      {
        case ' ':
          if (x == 0 || bEscapeSpace)
            aSB.append ('\\');
          aSB.append (' ');
          break;
        case '\t':
          aSB.append ('\\').append ('t');
          break;
        case '\n':
          aSB.append ('\\').append ('n');
          break;
        case '\r':
          aSB.append ('\\').append ('r');
          break;
        case '\f':
          aSB.append ('\\').append ('f');
          break;
        case '=':
        case ':':
        case '#':
        case '!':
          aSB.append ('\\').append (aChar);
          break;
        default:
          if ((aChar < 0x0020 || aChar > 0x007e) && bEscapeUnicode)
          {
            aSB.append ('\\')
               .append ('u')
               .append (StringHelper.getHexChar ((aChar >> 12) & 0xF))
               .append (StringHelper.getHexChar ((aChar >> 8) & 0xF))
               .append (StringHelper.getHexChar ((aChar >> 4) & 0xF))
               .append (StringHelper.getHexChar (aChar & 0xF));
          }
          else
          {
            aSB.append (aChar);
          }
      }
    }
    return aSB.toString ();
  }

  private static void _writeComments (@Nonnull @WillNotClose final Writer aWriter,
                                      @Nonnull final String sComments) throws IOException
  {
    aWriter.write ("#");
    final int nLen = sComments.length ();
    int nCurrent = 0;
    int nLast = 0;
    final char [] uu = new char [6];
    uu[0] = '\\';
    uu[1] = 'u';
    while (nCurrent < nLen)
    {
      final char c = sComments.charAt (nCurrent);
      if (c > '\u00ff' || c == '\n' || c == '\r')
      {
        if (nLast != nCurrent)
          aWriter.write (sComments.substring (nLast, nCurrent));
        if (c > '\u00ff')
        {
          uu[2] = StringHelper.getHexChar ((c >> 12) & 0xf);
          uu[3] = StringHelper.getHexChar ((c >> 8) & 0xf);
          uu[4] = StringHelper.getHexChar ((c >> 4) & 0xf);
          uu[5] = StringHelper.getHexChar (c & 0xf);
          aWriter.write (uu);
        }
        else
        {
          aWriter.write (ENewLineMode.DEFAULT.getText ());
          if (c == '\r' && nCurrent != nLen - 1 && sComments.charAt (nCurrent + 1) == '\n')
            nCurrent++;

          if (nCurrent == nLen - 1 ||
              (sComments.charAt (nCurrent + 1) != '#' && sComments.charAt (nCurrent + 1) != '!'))
            aWriter.write ("#");
        }
        nLast = nCurrent + 1;
      }
      nCurrent++;
    }
    if (nLast != nCurrent)
      aWriter.write (sComments.substring (nLast, nCurrent));
    aWriter.write (ENewLineMode.DEFAULT.getText ());
  }

  /**
   * Writes this property list (key and element pairs) in this
   * <code>Properties</code> table to the output character stream in a format
   * suitable for using the {@link #load(java.io.Reader) load(Reader)} method.
   * <p>
   * Properties from the defaults table of this <code>Properties</code> table
   * (if any) are <i>not</i> written out by this method.
   * <p>
   * If the comments argument is not null, then an ASCII <code>#</code>
   * character, the comments string, and a line separator are first written to
   * the output stream. Thus, the <code>comments</code> can serve as an
   * identifying comment. Any one of a line feed ('\n'), a carriage return
   * ('\r'), or a carriage return followed immediately by a line feed in
   * comments is replaced by a line separator generated by the
   * <code>Writer</code> and if the next character in comments is not character
   * <code>#</code> or character <code>!</code> then an ASCII <code>#</code> is
   * written out after that line separator.
   * <p>
   * Next, a comment line is always written, consisting of an ASCII
   * <code>#</code> character, the current date and time (as if produced by the
   * <code>toString</code> method of <code>Date</code> for the current time),
   * and a line separator as generated by the <code>Writer</code>.
   * <p>
   * Then every entry in this <code>Properties</code> table is written out, one
   * per line. For each entry the key string is written, then an ASCII
   * <code>=</code>, then the associated element string. For the key, all space
   * characters are written with a preceding <code>\</code> character. For the
   * element, leading space characters, but not embedded or trailing space
   * characters, are written with a preceding <code>\</code> character. The key
   * and element characters <code>#</code>, <code>!</code>, <code>=</code>, and
   * <code>:</code> are written with a preceding backslash to ensure that they
   * are properly loaded.
   * <p>
   * After the entries have been written, the output stream is flushed. The
   * output stream remains open after this method returns.
   * <p>
   *
   * @param aWriter
   *        an output character stream writer.
   * @param sComments
   *        a description of the property list.
   * @exception IOException
   *            if writing this property list to the specified output stream
   *            throws an <tt>IOException</tt>.
   * @exception ClassCastException
   *            if this <code>Properties</code> object contains any keys or
   *            values that are not <code>Strings</code>.
   * @exception NullPointerException
   *            if <code>writer</code> is null.
   * @since 1.6
   */
  public void store (@Nonnull @WillNotClose final Writer aWriter, @Nullable final String sComments) throws IOException
  {
    _store (StreamHelper.getBuffered (aWriter), sComments, false);
  }

  /**
   * Writes this property list (key and element pairs) in this
   * <code>Properties</code> table to the output stream in a format suitable for
   * loading into a <code>Properties</code> table using the
   * {@link #load(InputStream) load(InputStream)} method.
   * <p>
   * Properties from the defaults table of this <code>Properties</code> table
   * (if any) are <i>not</i> written out by this method.
   * <p>
   * This method outputs the comments, properties keys and values in the same
   * format as specified in {@link #store(java.io.Writer, java.lang.String)
   * store(Writer)}, with the following differences:
   * <ul>
   * <li>The stream is written using the ISO 8859-1 character encoding.
   * <li>Characters not in Latin-1 in the comments are written as
   * <code>&#92;u</code><i>xxxx</i> for their appropriate unicode hexadecimal
   * value <i>xxxx</i>.
   * <li>Characters less than <code>&#92;u0020</code> and characters greater
   * than <code>&#92;u007E</code> in property keys or values are written as
   * <code>&#92;u</code><i>xxxx</i> for the appropriate hexadecimal value
   * <i>xxxx</i>.
   * </ul>
   * <p>
   * After the entries have been written, the output stream is flushed. The
   * output stream remains open after this method returns.
   * <p>
   *
   * @param aOS
   *        an output stream.
   * @param sComments
   *        a description of the property list.
   * @exception IOException
   *            if writing this property list to the specified output stream
   *            throws an <tt>IOException</tt>.
   * @exception ClassCastException
   *            if this <code>Properties</code> object contains any keys or
   *            values that are not <code>Strings</code>.
   * @exception NullPointerException
   *            if <code>out</code> is null.
   * @since 1.2
   */
  public void store (@Nonnull @WillNotClose final OutputStream aOS, @Nullable final String sComments) throws IOException
  {
    _store (new NonBlockingBufferedWriter (new OutputStreamWriter (aOS, CCharset.CHARSET_ISO_8859_1_OBJ)),
            sComments,
            true);
  }

  private void _store (@Nonnull @WillNotClose final Writer aWriter,
                       @Nullable final String sComments,
                       final boolean bEscapeUnicode) throws IOException
  {
    if (sComments != null)
      _writeComments (aWriter, sComments);

    final String sNewLine = ENewLineMode.DEFAULT.getText ();
    aWriter.write ("#" + ZonedDateTime.now ().toString () + sNewLine);

    for (final Map.Entry <String, String> aEntry : entrySet ())
    {
      String sKey = aEntry.getKey ();
      sKey = _saveConvert (sKey, true, bEscapeUnicode);

      String sValue = aEntry.getValue ();
      /*
       * No need to escape embedded and trailing spaces for value, hence pass
       * false to flag.
       */
      sValue = _saveConvert (sValue, false, bEscapeUnicode);

      aWriter.write (sKey);
      aWriter.write ('=');
      aWriter.write (sValue);
      aWriter.write (sNewLine);
    }
    aWriter.flush ();
  }

  /**
   * Searches for the property with the specified key in this property list. If
   * the key is not found in this property list, the default property list, and
   * its defaults, recursively, are then checked. The method returns
   * <code>null</code> if the property is not found.
   *
   * @param sKey
   *        the property key.
   * @return the value in this property list with the specified key value.
   * @see #setProperty
   */
  @Nullable
  public String getProperty (@Nullable final String sKey)
  {
    final String sValue = super.get (sKey);
    return sValue == null && m_aDefaults != null ? m_aDefaults.getProperty (sKey) : sValue;
  }

  /**
   * Searches for the property with the specified key in this property list. If
   * the key is not found in this property list, the default property list, and
   * its defaults, recursively, are then checked. The method returns the default
   * value argument if the property is not found.
   *
   * @param sKey
   *        the map key.
   * @param sDefaultValue
   *        a default value.
   * @return the value in this property list with the specified key value.
   * @see #setProperty
   */
  @Nullable
  public String getProperty (@Nullable final String sKey, @Nullable final String sDefaultValue)
  {
    final String sValue = getProperty (sKey);
    return sValue == null ? sDefaultValue : sValue;
  }

  /**
   * Create {@link NonBlockingProperties} from an existing {@link Properties}
   * object.
   *
   * @param aProperties
   *        Source properties. May be <code>null</code>.
   * @return The newly created {@link NonBlockingProperties}. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static NonBlockingProperties create (@Nullable final Properties aProperties)
  {
    final NonBlockingProperties ret = new NonBlockingProperties ();
    if (aProperties != null)
      for (final Map.Entry <Object, Object> aEntry : aProperties.entrySet ())
        ret.put ((String) aEntry.getKey (), (String) aEntry.getValue ());
    return ret;
  }
}
