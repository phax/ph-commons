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
package com.helger.commons.mime;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.codec.QuotedPrintableCodec;
import com.helger.commons.codec.URLCodec;

/**
 * Defines the possible MIME type parameter value quotings
 *
 * @author Philip Helger
 */
public enum EMimeQuoting
{
  /**
   * Create a quoted string according to RFC 822 (surrounding everything in
   * double-quotes and masking using backslash).<br>
   * Example: <code>foo bar</code> results in <code>"foo bar"</code>
   */
  QUOTED_STRING
  {
    @Override
    @Nonnull
    @Nonempty
    public String getQuotedString (@Nonnull @Nonempty final String sUnquotedString)
    {
      final StringBuilder aSB = new StringBuilder (sUnquotedString.length () * 2);
      aSB.append (QUOTED_STRING_SEPARATOR_CHAR);
      final char [] aChars = sUnquotedString.toCharArray ();
      for (final char c : aChars)
        if (c == QUOTED_STRING_SEPARATOR_CHAR || c == QUOTED_STRING_MASK_CHAR)
        {
          // Mask char
          aSB.append (QUOTED_STRING_MASK_CHAR).append (c);
        }
        else
          aSB.append (c);
      return aSB.append (QUOTED_STRING_SEPARATOR_CHAR).toString ();
    }

    @Override
    @Nonnull
    @Nonempty
    public String getUnquotedString (@Nonnull @Nonempty final String sQuotedString)
    {
      throw new UnsupportedOperationException ("This is handled directly inside the MimeTypeParser!");
    }
  },

  /**
   * Create a quoted printable String. Replace all non-printable characters with
   * =XY where XY is the hex encoding of the char.<br>
   * Example: <code>foo bar</code> results in <code>foo=20bar</code>
   */
  QUOTED_PRINTABLE
  {
    @Override
    @Nonnull
    @Nonempty
    public String getQuotedString (@Nonnull @Nonempty final String sUnquotedString)
    {
      // Use a special BitSet
      return new QuotedPrintableCodec (PRINTABLE_QUOTED_PRINTABLE).getEncodedAsString (sUnquotedString,
                                                                                       StandardCharsets.UTF_8);
    }

    @Override
    @Nonnull
    @Nonempty
    public String getUnquotedString (@Nonnull @Nonempty final String sQuotedString)
    {
      // Use default BitSet for decoding!
      return new QuotedPrintableCodec ().getDecodedAsString (sQuotedString, StandardCharsets.UTF_8);
    }
  },

  /**
   * Create a URL escaped String. Replace all non-printable characters with %XY
   * where XY is the hex encoding of the char. Special note: space (ASCII 20)
   * should be escaped as "%20" and not as "+".<br>
   * Example: <code>foo bar</code> results in <code>foo%20bar</code>
   */
  URL_ESCAPE
  {
    @Override
    @Nonnull
    @Nonempty
    public String getQuotedString (@Nonnull @Nonempty final String sUnquotedString)
    {
      // Use a special BitSet
      return new URLCodec (PRINTABLE_URL).getEncodedAsString (sUnquotedString, StandardCharsets.UTF_8);
    }

    @Override
    @Nonnull
    @Nonempty
    public String getUnquotedString (@Nonnull @Nonempty final String sQuotedString)
    {
      return new URLCodec (PRINTABLE_URL).getDecodedAsString (sQuotedString, StandardCharsets.UTF_8);
    }
  };

  public static final char QUOTED_STRING_SEPARATOR_CHAR = '"';
  public static final char QUOTED_STRING_MASK_CHAR = '\\';

  private static final BitSet PRINTABLE_QUOTED_PRINTABLE = QuotedPrintableCodec.getDefaultPrintableChars ();
  private static final BitSet PRINTABLE_URL = URLCodec.getDefaultPrintableChars ();

  static
  {
    // Modify BitSets
    PRINTABLE_QUOTED_PRINTABLE.set ('\t', false);
    PRINTABLE_QUOTED_PRINTABLE.set (' ', false);
    PRINTABLE_URL.set (' ', false);

    for (final char c : MimeTypeParser.getAllTSpecialChars ())
    {
      PRINTABLE_QUOTED_PRINTABLE.set (c, false);
      PRINTABLE_URL.set (c, false);
    }
  }

  @Nonnull
  @Nonempty
  public abstract String getQuotedString (@Nonnull @Nonempty String sUnquotedString);

  @Nonnull
  @Nonempty
  public abstract String getUnquotedString (@Nonnull @Nonempty String sQuotedString);

  public boolean isQuotedString ()
  {
    return this == EMimeQuoting.QUOTED_STRING;
  }
}
