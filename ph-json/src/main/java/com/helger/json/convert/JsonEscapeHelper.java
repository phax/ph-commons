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
package com.helger.json.convert;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.exception.InitializationException;
import com.helger.commons.string.StringHelper;

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
  private static final char [] CHARS_TO_MASK = new char [] { '\0', '"', '\\', '\b', '\t', '\n', '\r', '\f' };
  private static final String [] REPLACEMENT_STRINGS = new String [] { "\\u0000",
                                                                       "\\\"",
                                                                       "\\\\",
                                                                       "\\b",
                                                                       "\\t",
                                                                       "\\n",
                                                                       "\\r",
                                                                       "\\f" };

  static
  {
    if (CHARS_TO_MASK.length != REPLACEMENT_STRINGS.length)
      throw new InitializationException ("Length are inconsistent");
  }

  @PresentForCodeCoverage
  private static final JsonEscapeHelper s_aInstance = new JsonEscapeHelper ();

  private JsonEscapeHelper ()
  {}

  public static void jsonEscapeToStringBuilder (@Nonnull final char [] aInput, @Nonnull final StringBuilder aSB)
  {
    ValueEnforcer.notNull (aInput, "Input");
    ValueEnforcer.notNull (aSB, "StringBuilder");

    for (final char cCurrent : aInput)
    {
      final int nIndex = ArrayHelper.getFirstIndex (CHARS_TO_MASK, cCurrent);
      if (nIndex >= 0)
        aSB.append (REPLACEMENT_STRINGS[nIndex]);
      else
        aSB.append (cCurrent);
    }
  }

  @Nullable
  public static String jsonEscape (@Nullable final String sInput)
  {
    if (StringHelper.hasNoText (sInput))
      return sInput;

    return jsonEscape (sInput.toCharArray ());
  }

  @Nullable
  public static String jsonEscape (@Nullable final char [] aInput)
  {
    if (aInput == null)
      return null;

    if (!StringHelper.containsAny (aInput, CHARS_TO_MASK))
      return new String (aInput);

    final StringBuilder aSB = new StringBuilder (aInput.length * 2);
    jsonEscapeToStringBuilder (aInput, aSB);
    return aSB.toString ();
  }

  public static void jsonEscape (@Nullable final String sInput, @Nonnull final StringBuilder aSB)
  {
    ValueEnforcer.notNull (aSB, "StringBuilder");

    if (StringHelper.hasText (sInput))
    {
      final char [] aInput = sInput.toCharArray ();
      if (!StringHelper.containsAny (aInput, CHARS_TO_MASK))
        aSB.append (sInput);
      else
        jsonEscapeToStringBuilder (aInput, aSB);
    }
  }

  public static void jsonEscapeToWriter (@Nonnull final char [] aInput,
                                         @Nonnull @WillNotClose final Writer aWriter) throws IOException
  {
    ValueEnforcer.notNull (aInput, "Input");
    ValueEnforcer.notNull (aWriter, "Writer");

    for (final char cCurrent : aInput)
    {
      final int nIndex = ArrayHelper.getFirstIndex (CHARS_TO_MASK, cCurrent);
      if (nIndex >= 0)
        aWriter.write (REPLACEMENT_STRINGS[nIndex]);
      else
        aWriter.write (cCurrent);
    }
  }

  public static void jsonEscapeToWriter (@Nullable final String sInput,
                                         @Nonnull @WillNotClose final Writer aWriter) throws IOException
  {
    ValueEnforcer.notNull (aWriter, "Writer");

    if (StringHelper.hasText (sInput))
    {
      final char [] aInput = sInput.toCharArray ();
      if (!StringHelper.containsAny (aInput, CHARS_TO_MASK))
        aWriter.write (aInput, 0, aInput.length);
      else
        jsonEscapeToWriter (aInput, aWriter);
    }
  }

  private static int _hexval (final char c)
  {
    final int ret = StringHelper.getHexValue (c);
    if (ret < 0)
      throw new IllegalArgumentException ("Illegal hex char '" + c + "'");
    return ret;
  }

  public static void jsonUnescapeToStringBuilder (@Nonnull final char [] aInput, @Nonnull final StringBuilder aSB)
  {
    ValueEnforcer.notNull (aInput, "Input");
    ValueEnforcer.notNull (aSB, "StringBuilder");

    final int nMax = aInput.length;
    for (int i = 0; i < nMax; ++i)
    {
      final char c = aInput[i];
      if (c == MASK_CHAR)
      {
        if (i > nMax - 1)
          throw new IllegalArgumentException ("JSON string ends with escape char");
        final char cNext = aInput[++i];
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
                                                  new String (aInput, i - 1, 6));
            final char cU1 = aInput[++i];
            final char cU2 = aInput[++i];
            final char cU3 = aInput[++i];
            final char cU4 = aInput[++i];
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

  @Nonnull
  public static String jsonUnescape (@Nonnull final String sInput)
  {
    ValueEnforcer.notNull (sInput, "Input");

    if (sInput.indexOf (MASK_CHAR) < 0)
    {
      // Nothing to unescape
      return sInput;
    }

    // Perform unescape
    final StringBuilder aSB = new StringBuilder (sInput.length ());
    jsonUnescapeToStringBuilder (sInput.toCharArray (), aSB);
    return aSB.toString ();
  }

  @Nonnull
  public static String jsonUnescape (@Nonnull final char [] aInput)
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
