/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.json.convert;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.WillNotClose;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.array.ArrayHelper;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringHex;

/**
 * Some escaping utility methods for Json
 *
 * @author Philip Helger
 */
@Immutable
public final class JsonEscapeHelper
{
  public static final char MASK_CHAR = '\\';
  public static final String MASK_STRING = Character.toString (MASK_CHAR);

  /**
   * Lookup table from a character to its JSON escape sequence. A <code>null</code> entry means the
   * character is written as is. It covers all control characters (U+0000 - U+001F) per RFC 8259,
   * the double quote and the backslash. Single quotes must NOT be escaped in valid JSON (See
   * http://www.json.org/). Characters outside of the table are never escaped.
   */
  private static final String [] ESCAPE_TABLE = new String [MASK_CHAR + 1];

  static
  {
    for (char c = 0; c < 0x20; ++c)
      ESCAPE_TABLE[c] = "\\u00" + StringHex.getHexStringLeadingZero (c, 2);
    ESCAPE_TABLE['"'] = "\\\"";
    ESCAPE_TABLE['\\'] = "\\\\";
    ESCAPE_TABLE['\b'] = "\\b";
    ESCAPE_TABLE['\t'] = "\\t";
    ESCAPE_TABLE['\n'] = "\\n";
    ESCAPE_TABLE['\r'] = "\\r";
    ESCAPE_TABLE['\f'] = "\\f";
  }

  @PresentForCodeCoverage
  private static final JsonEscapeHelper INSTANCE = new JsonEscapeHelper ();

  private JsonEscapeHelper ()
  {}

  /**
   * Get the JSON escape sequence of a single character.
   *
   * @param c
   *        The character to check.
   * @return The escape sequence or <code>null</code> if the character does not need to be escaped.
   */
  @Nullable
  private static String _getEscape (final char c)
  {
    return c < ESCAPE_TABLE.length ? ESCAPE_TABLE[c] : null;
  }

  /**
   * Find the first character in the provided character sequence that needs to be escaped for JSON
   * output. This covers the double quote, the backslash as well as all control characters (U+0000
   * - U+001F) per RFC 8259.
   *
   * @param aInput
   *        The character sequence to check. May not be <code>null</code>.
   * @return The index of the first character that needs escaping or -1 if no character needs
   *         escaping.
   */
  private static int _getFirstIndexToEscape (@NonNull final CharSequence aInput)
  {
    final int nLen = aInput.length ();
    for (int i = 0; i < nLen; ++i)
      if (_getEscape (aInput.charAt (i)) != null)
        return i;
    return -1;
  }

  /**
   * Escape a JSON character sequence and append the result to a {@link StringBuilder}.
   *
   * @param aInput
   *        The character sequence to escape. May not be <code>null</code>.
   * @param nFirstIndex
   *        The index of the first character to check. All characters before are known to not need
   *        escaping.
   * @param aSB
   *        The StringBuilder to append the escaped output to. May not be <code>null</code>.
   */
  private static void _jsonEscapeToStringBuilder (@NonNull final CharSequence aInput,
                                                  final int nFirstIndex,
                                                  @NonNull final StringBuilder aSB)
  {
    final int nLen = aInput.length ();
    // Start of the current run of characters that need no escaping
    int nRunStart = 0;
    for (int i = nFirstIndex; i < nLen; ++i)
    {
      final String sEscape = _getEscape (aInput.charAt (i));
      if (sEscape != null)
      {
        if (i > nRunStart)
          aSB.append (aInput, nRunStart, i);
        aSB.append (sEscape);
        nRunStart = i + 1;
      }
    }
    if (nLen > nRunStart)
      aSB.append (aInput, nRunStart, nLen);
  }

  /**
   * Escape a JSON character array and append the result to a {@link StringBuilder}.
   *
   * @param aInput
   *        The character array to escape. May not be <code>null</code>.
   * @param aSB
   *        The StringBuilder to append the escaped output to. May not be <code>null</code>.
   */
  public static void jsonEscapeToStringBuilder (final char @NonNull [] aInput, @NonNull final StringBuilder aSB)
  {
    ValueEnforcer.notNull (aInput, "Input");
    ValueEnforcer.notNull (aSB, "StringBuilder");

    _jsonEscapeToStringBuilder (CharBuffer.wrap (aInput), 0, aSB);
  }

  /**
   * Escape a JSON string.
   *
   * @param sInput
   *        The string to escape. May be <code>null</code>.
   * @return The escaped string, or <code>null</code> if the input was <code>null</code> or empty.
   */
  @Nullable
  public static String jsonEscape (@Nullable final String sInput)
  {
    if (StringHelper.isEmpty (sInput))
      return sInput;

    final int nFirstIndex = _getFirstIndexToEscape (sInput);
    if (nFirstIndex < 0)
      return sInput;

    final StringBuilder aSB = new StringBuilder (sInput.length () * 2);
    _jsonEscapeToStringBuilder (sInput, nFirstIndex, aSB);
    return aSB.toString ();
  }

  /**
   * Escape a JSON character array.
   *
   * @param aInput
   *        The character array to escape. May be <code>null</code>.
   * @return The escaped string, or <code>null</code> if the input was <code>null</code>.
   */
  @Nullable
  public static String jsonEscape (final char @Nullable [] aInput)
  {
    if (aInput == null)
      return null;

    final CharBuffer aCB = CharBuffer.wrap (aInput);
    final int nFirstIndex = _getFirstIndexToEscape (aCB);
    if (nFirstIndex < 0)
      return new String (aInput);

    final StringBuilder aSB = new StringBuilder (aInput.length * 2);
    _jsonEscapeToStringBuilder (aCB, nFirstIndex, aSB);
    return aSB.toString ();
  }

  /**
   * Escape a JSON string and append the result to a {@link StringBuilder}.
   *
   * @param sInput
   *        The string to escape. May be <code>null</code> or empty.
   * @param aSB
   *        The StringBuilder to append the escaped output to. May not be <code>null</code>.
   */
  public static void jsonEscape (@Nullable final String sInput, @NonNull final StringBuilder aSB)
  {
    ValueEnforcer.notNull (aSB, "StringBuilder");

    if (StringHelper.isNotEmpty (sInput))
    {
      final int nFirstIndex = _getFirstIndexToEscape (sInput);
      if (nFirstIndex < 0)
        aSB.append (sInput);
      else
        _jsonEscapeToStringBuilder (sInput, nFirstIndex, aSB);
    }
  }

  /**
   * Escape a JSON string and write the result to a {@link Writer}. Runs of characters that need no
   * escaping are written in one go.
   *
   * @param sInput
   *        The string to escape. May not be <code>null</code>.
   * @param nFirstIndex
   *        The index of the first character to check. All characters before are known to not need
   *        escaping.
   * @param aWriter
   *        The Writer to write the escaped output to. May not be <code>null</code>. Will not be
   *        closed.
   * @throws IOException
   *         On IO error
   */
  private static void _jsonEscapeToWriter (@NonNull final String sInput,
                                           final int nFirstIndex,
                                           @NonNull @WillNotClose final Writer aWriter) throws IOException
  {
    final int nLen = sInput.length ();
    // Start of the current run of characters that need no escaping
    int nRunStart = 0;
    for (int i = nFirstIndex; i < nLen; ++i)
    {
      final String sEscape = _getEscape (sInput.charAt (i));
      if (sEscape != null)
      {
        if (i > nRunStart)
          aWriter.write (sInput, nRunStart, i - nRunStart);
        aWriter.write (sEscape);
        nRunStart = i + 1;
      }
    }
    if (nLen > nRunStart)
      aWriter.write (sInput, nRunStart, nLen - nRunStart);
  }

  /**
   * Escape a JSON character array and write the result to a {@link Writer}. Runs of characters
   * that need no escaping are written in one go.
   *
   * @param aInput
   *        The character array to escape. May not be <code>null</code>.
   * @param aWriter
   *        The Writer to write the escaped output to. May not be <code>null</code>. Will not be
   *        closed.
   * @throws IOException
   *         On IO error
   */
  private static void _jsonEscapeToWriter (final char @NonNull [] aInput,
                                           @NonNull @WillNotClose final Writer aWriter) throws IOException
  {
    final int nLen = aInput.length;
    // Start of the current run of characters that need no escaping
    int nRunStart = 0;
    for (int i = 0; i < nLen; ++i)
    {
      final String sEscape = _getEscape (aInput[i]);
      if (sEscape != null)
      {
        if (i > nRunStart)
          aWriter.write (aInput, nRunStart, i - nRunStart);
        aWriter.write (sEscape);
        nRunStart = i + 1;
      }
    }
    if (nLen > nRunStart)
      aWriter.write (aInput, nRunStart, nLen - nRunStart);
  }

  /**
   * Escape a JSON character array and write the result to a {@link Writer}.
   *
   * @param aInput
   *        The character array to escape. May not be <code>null</code>.
   * @param aWriter
   *        The Writer to write the escaped output to. May not be <code>null</code>. Will not be
   *        closed.
   * @throws IOException
   *         On IO error
   */
  public static void jsonEscapeToWriter (final char @NonNull [] aInput,
                                         @NonNull @WillNotClose final Writer aWriter) throws IOException
  {
    ValueEnforcer.notNull (aInput, "Input");
    ValueEnforcer.notNull (aWriter, "Writer");

    _jsonEscapeToWriter (aInput, aWriter);
  }

  /**
   * Escape a JSON string and write the result to a {@link Writer}.
   *
   * @param sInput
   *        The string to escape. May be <code>null</code> or empty.
   * @param aWriter
   *        The Writer to write the escaped output to. May not be <code>null</code>. Will not be
   *        closed.
   * @throws IOException
   *         On IO error
   */
  public static void jsonEscapeToWriter (@Nullable final String sInput,
                                         @NonNull @WillNotClose final Writer aWriter) throws IOException
  {
    ValueEnforcer.notNull (aWriter, "Writer");

    if (StringHelper.isNotEmpty (sInput))
    {
      final int nFirstIndex = _getFirstIndexToEscape (sInput);
      if (nFirstIndex < 0)
        aWriter.write (sInput);
      else
        _jsonEscapeToWriter (sInput, nFirstIndex, aWriter);
    }
  }

  private static int _hexval (final char c)
  {
    final int ret = StringHex.getHexValue (c);
    if (ret < 0)
      throw new IllegalArgumentException ("Illegal hex char '" + c + "'");
    return ret;
  }

  /**
   * Unescape a JSON character sequence and append the result to a {@link StringBuilder}.
   *
   * @param aInput
   *        The character sequence to unescape. May not be <code>null</code>.
   * @param aSB
   *        The StringBuilder to append the unescaped output to. May not be <code>null</code>.
   */
  private static void _jsonUnescapeToStringBuilder (@NonNull final CharSequence aInput,
                                                    @NonNull final StringBuilder aSB)
  {
    final int nMax = aInput.length ();
    for (int i = 0; i < nMax; ++i)
    {
      final char c = aInput.charAt (i);
      if (c == MASK_CHAR)
      {
        if (i + 1 >= nMax)
          throw new IllegalArgumentException ("JSON string ends with escape char");
        ++i;
        final char cNext = aInput.charAt (i);
        switch (cNext)
        {
          case '"':
            // Unescape slash even though it will not be written escaped
          case '/':
          case '\\':
            aSB.append (cNext);
            break;
          case 'b':
            aSB.append ('\b');
            break;
          case 'f':
            aSB.append ('\f');
            break;
          case 'n':
            aSB.append ('\n');
            break;
          case 'r':
            aSB.append ('\r');
            break;
          case 't':
            aSB.append ('\t');
            break;
          case 'u':
          {
            // The parser ensures we get 4 chars!
            if (i + 4 > nMax - 1)
              throw new IllegalArgumentException ("JSON unicode escape sequence \\uXXXX is incomplete: " +
                                                  // Only the remaining characters are available
                                                  aInput.subSequence (i - 1, nMax).toString ());
            ++i;
            final char cU1 = aInput.charAt (i);
            ++i;
            final char cU2 = aInput.charAt (i);
            ++i;
            final char cU3 = aInput.charAt (i);
            ++i;
            final char cU4 = aInput.charAt (i);
            aSB.append ((char) (_hexval (cU1) << 12 | _hexval (cU2) << 8 | _hexval (cU3) << 4 | _hexval (cU4)));
            break;
          }
          default:
            throw new IllegalArgumentException ("Unexpected JSON escape sequence: \\" +
                                                cNext +
                                                " (" +
                                                (int) cNext +
                                                ")");
        }
      }
      else
        aSB.append (c);
    }
  }

  /**
   * Unescape a JSON character array and append the result to a {@link StringBuilder}.
   *
   * @param aInput
   *        The character array to unescape. May not be <code>null</code>.
   * @param aSB
   *        The StringBuilder to append the unescaped output to. May not be <code>null</code>.
   */
  public static void jsonUnescapeToStringBuilder (final char @NonNull [] aInput, @NonNull final StringBuilder aSB)
  {
    ValueEnforcer.notNull (aInput, "Input");
    ValueEnforcer.notNull (aSB, "StringBuilder");

    _jsonUnescapeToStringBuilder (CharBuffer.wrap (aInput), aSB);
  }

  /**
   * Unescape a JSON string.
   *
   * @param sInput
   *        The string to unescape. May not be <code>null</code>.
   * @return The unescaped string. Never <code>null</code>.
   */
  @NonNull
  public static String jsonUnescape (@NonNull final String sInput)
  {
    ValueEnforcer.notNull (sInput, "Input");

    if (sInput.indexOf (MASK_CHAR) < 0)
    {
      // Nothing to unescape
      return sInput;
    }

    // Perform unescape
    final StringBuilder aSB = new StringBuilder (sInput.length ());
    _jsonUnescapeToStringBuilder (sInput, aSB);
    return aSB.toString ();
  }

  /**
   * Unescape a JSON character array.
   *
   * @param aInput
   *        The character array to unescape. May not be <code>null</code>.
   * @return The unescaped string. Never <code>null</code>.
   */
  @NonNull
  public static String jsonUnescape (final char @NonNull [] aInput)
  {
    ValueEnforcer.notNull (aInput, "Input");

    if (!ArrayHelper.contains (aInput, MASK_CHAR))
    {
      // Nothing to unescape
      return new String (aInput);
    }

    // Perform unescape
    final StringBuilder aSB = new StringBuilder (aInput.length);
    jsonUnescapeToStringBuilder (aInput, aSB);
    return aSB.toString ();
  }
}
