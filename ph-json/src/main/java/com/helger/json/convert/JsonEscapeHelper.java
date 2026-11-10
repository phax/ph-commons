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
import com.helger.base.exception.InitializationException;
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
   * single quotes must NOT be escaped in valid JSON (See http://www.json.org/)
   */
  private static final char [] CHARS_TO_MASK = { '\0', '"', '\\', '\b', '\t', '\n', '\r', '\f' };
  private static final String [] REPLACEMENT_STRINGS = { "\\u0000", "\\\"", "\\\\", "\\b", "\\t", "\\n", "\\r", "\\f" };

  static
  {
    if (CHARS_TO_MASK.length != REPLACEMENT_STRINGS.length)
      throw new InitializationException ("Length are inconsistent");
  }

  @PresentForCodeCoverage
  private static final JsonEscapeHelper INSTANCE = new JsonEscapeHelper ();

  private JsonEscapeHelper ()
  {}

  /**
   * Check if the provided character sequence contains at least one character that needs to be
   * escaped for JSON output. This covers the explicitly masked characters as well as all control
   * characters (U+0000 - U+001F) per RFC 8259.
   *
   * @param aInput
   *        The character sequence to check. May not be <code>null</code>.
   * @return <code>true</code> if at least one character needs escaping.
   */
  private static boolean _containsCharToEscape (@NonNull final CharSequence aInput)
  {
    final int nLen = aInput.length ();
    for (int i = 0; i < nLen; ++i)
    {
      final char c = aInput.charAt (i);
      if (c < 0x20 || ArrayHelper.contains (CHARS_TO_MASK, c))
        return true;
    }
    return false;
  }

  /**
   * Escape a JSON character sequence and append the result to a {@link StringBuilder}.
   *
   * @param aInput
   *        The character sequence to escape. May not be <code>null</code>.
   * @param aSB
   *        The StringBuilder to append the escaped output to. May not be <code>null</code>.
   */
  private static void _jsonEscapeToStringBuilder (@NonNull final CharSequence aInput,
                                                  @NonNull final StringBuilder aSB)
  {
    final int nLen = aInput.length ();
    for (int i = 0; i < nLen; ++i)
    {
      final char cCurrent = aInput.charAt (i);
      final int nIndex = ArrayHelper.getFirstIndex (CHARS_TO_MASK, cCurrent);
      if (nIndex >= 0)
        aSB.append (REPLACEMENT_STRINGS[nIndex]);
      else
        if (cCurrent < 0x20)
        {
          // Per RFC 8259 all control characters (U+0000 - U+001F) must be
          // escaped. The named ones are already handled above.
          aSB.append ("\\u00").append (StringHex.getHexStringLeadingZero (cCurrent, 2));
        }
        else
          aSB.append (cCurrent);
    }
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

    _jsonEscapeToStringBuilder (CharBuffer.wrap (aInput), aSB);
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

    if (!_containsCharToEscape (sInput))
      return sInput;

    final StringBuilder aSB = new StringBuilder (sInput.length () * 2);
    _jsonEscapeToStringBuilder (sInput, aSB);
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

    if (!_containsCharToEscape (CharBuffer.wrap (aInput)))
      return new String (aInput);

    final StringBuilder aSB = new StringBuilder (aInput.length * 2);
    jsonEscapeToStringBuilder (aInput, aSB);
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
      if (!_containsCharToEscape (sInput))
        aSB.append (sInput);
      else
        _jsonEscapeToStringBuilder (sInput, aSB);
    }
  }

  /**
   * Escape a JSON character sequence and write the result to a {@link Writer}.
   *
   * @param aInput
   *        The character sequence to escape. May not be <code>null</code>.
   * @param aWriter
   *        The Writer to write the escaped output to. May not be <code>null</code>. Will not be
   *        closed.
   * @throws IOException
   *         On IO error
   */
  private static void _jsonEscapeToWriter (@NonNull final CharSequence aInput,
                                           @NonNull @WillNotClose final Writer aWriter) throws IOException
  {
    final int nLen = aInput.length ();
    for (int i = 0; i < nLen; ++i)
    {
      final char cCurrent = aInput.charAt (i);
      final int nIndex = ArrayHelper.getFirstIndex (CHARS_TO_MASK, cCurrent);
      if (nIndex >= 0)
        aWriter.write (REPLACEMENT_STRINGS[nIndex]);
      else
        if (cCurrent < 0x20)
        {
          // Per RFC 8259 all control characters (U+0000 - U+001F) must be
          // escaped. The named ones are already handled above.
          aWriter.write ("\\u00");
          aWriter.write (StringHex.getHexStringLeadingZero (cCurrent, 2));
        }
        else
          aWriter.write (cCurrent);
    }
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

    _jsonEscapeToWriter (CharBuffer.wrap (aInput), aWriter);
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
      if (!_containsCharToEscape (sInput))
        aWriter.write (sInput);
      else
        _jsonEscapeToWriter (sInput, aWriter);
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
