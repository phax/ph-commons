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
package com.helger.unittest.property;

import java.util.Arrays;
import java.util.Collection;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;

/**
 * Creates readable representations of generated samples for failure messages. Arrays show their
 * content, and strings and characters are quoted with non-printable characters escaped.
 *
 * @author Philip Helger
 */
@Immutable
final class SampleFormatter
{
  @PresentForCodeCoverage
  private static final SampleFormatter INSTANCE = new SampleFormatter ();

  private SampleFormatter ()
  {}

  private static void _appendChar (@NonNull final StringBuilder aSB, final char c)
  {
    if (c == '"' || c == '\\')
      aSB.append ('\\').append (c);
    else
      if (c >= 0x20 && c < 0x7f)
        aSB.append (c);
      else
      {
        final String sHex = Integer.toHexString (c);
        aSB.append ("\\u").append ("0000", sHex.length (), 4).append (sHex);
      }
  }

  @NonNull
  private static String _quote (@NonNull final CharSequence aText, final char cQuote)
  {
    final StringBuilder aSB = new StringBuilder (aText.length () + 2).append (cQuote);
    for (int i = 0; i < aText.length (); ++i)
      _appendChar (aSB, aText.charAt (i));
    return aSB.append (cQuote).toString ();
  }

  private static void _appendElementsRecursive (@NonNull final StringBuilder aSB, @NonNull final Iterable <?> aElements)
  {
    aSB.append ('[');
    boolean bFirst = true;
    for (final Object aElement : aElements)
    {
      if (bFirst)
        bFirst = false;
      else
        aSB.append (", ");
      _formatRecursive (aSB, aElement);
    }
    aSB.append (']');
  }

  private static void _formatRecursive (@NonNull final StringBuilder aSB, @Nullable final Object aValue)
  {
    if (aValue == null)
      aSB.append ("null");
    else
      if (aValue instanceof final CharSequence aText)
        aSB.append (_quote (aText, '"'));
      else
        if (aValue instanceof final Character aChar)
          aSB.append (_quote (aChar.toString (), '\''));
        else
          if (aValue instanceof final byte [] aArray)
            aSB.append (Arrays.toString (aArray));
          else
            if (aValue instanceof final char [] aArray)
              aSB.append (_quote (new String (aArray), '"'));
            else
              if (aValue instanceof final int [] aArray)
                aSB.append (Arrays.toString (aArray));
              else
                if (aValue instanceof final long [] aArray)
                  aSB.append (Arrays.toString (aArray));
                else
                  if (aValue instanceof final Object [] aArray)
                    _appendElementsRecursive (aSB, Arrays.asList (aArray));
                  else
                    if (aValue instanceof final Collection <?> aCollection)
                      _appendElementsRecursive (aSB, aCollection);
                    else
                      aSB.append (aValue);
  }

  /**
   * Format a sample value. Arrays and collections are formatted element by element.
   *
   * @param aValue
   *        The value to format. May be <code>null</code>.
   * @return The readable representation. Never <code>null</code>.
   */
  @NonNull
  static String format (@Nullable final Object aValue)
  {
    final StringBuilder aSB = new StringBuilder ();
    _formatRecursive (aSB, aValue);
    return aSB.toString ();
  }
}
