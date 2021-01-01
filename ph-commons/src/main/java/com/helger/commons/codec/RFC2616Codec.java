/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.commons.codec;

import java.io.IOException;
import java.io.Writer;
import java.util.BitSet;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

import com.helger.commons.string.StringHelper;
import com.helger.commons.text.util.ABNF;

/**
 * Codec for RFC 2616 HTTP header values.
 *
 * @author Philip Helger
 * @since 9.3.6
 */
public class RFC2616Codec implements ICharArrayCodec
{
  private static final char QUOTE_CHAR = '"';
  private static final char ESCAPE_CHAR = '\\';

  // Non-token chars according to RFC 2616
  private static final BitSet NON_TOKEN_RFC2616 = new BitSet (256);

  static
  {
    for (int i = ABNF.CHECK_RANGE_MIN_INCL; i <= ABNF.CHECK_RANGE_MAX_INCL; ++i)
      if (ABNF.isCtl (i) || ABNF.isSP (i) || ABNF.isHTab (i))
        NON_TOKEN_RFC2616.set (i);
    NON_TOKEN_RFC2616.set ('(');
    NON_TOKEN_RFC2616.set (')');
    NON_TOKEN_RFC2616.set ('<');
    NON_TOKEN_RFC2616.set ('>');
    NON_TOKEN_RFC2616.set ('@');
    NON_TOKEN_RFC2616.set (',');
    NON_TOKEN_RFC2616.set (';');
    NON_TOKEN_RFC2616.set (':');
    NON_TOKEN_RFC2616.set ('\\');
    NON_TOKEN_RFC2616.set ('"');
    NON_TOKEN_RFC2616.set ('/');
    NON_TOKEN_RFC2616.set ('[');
    NON_TOKEN_RFC2616.set (']');
    NON_TOKEN_RFC2616.set ('?');
    NON_TOKEN_RFC2616.set ('=');
    NON_TOKEN_RFC2616.set ('{');
    NON_TOKEN_RFC2616.set ('}');
  }

  public static boolean isToken (@Nullable final String s)
  {
    // May not be empty
    if (s == null)
      return false;

    return isToken (s.toCharArray ());
  }

  public static boolean isToken (@Nullable final char [] aChars)
  {
    // May not be empty
    if (aChars == null || aChars.length == 0)
      return false;

    // No forbidden chars may be present
    for (final char c : aChars)
      if (NON_TOKEN_RFC2616.get (c))
        return false;
    return true;
  }

  public static boolean isMaybeEncoded (@Nullable final String s)
  {
    return s != null && s.length () >= 2 && s.charAt (0) == QUOTE_CHAR && StringHelper.getLastChar (s) == QUOTE_CHAR;
  }

  public static boolean isMaybeEncoded (@Nullable final char [] s)
  {
    return s != null && s.length >= 2 && s[0] == QUOTE_CHAR && s[s.length - 1] == QUOTE_CHAR;
  }

  public RFC2616Codec ()
  {}

  @Nonnegative
  @Override
  public int getMaximumEncodedLength (@Nonnegative final int nDecodedLen)
  {
    // Worst case: each char needs quoting
    return 1 + 2 * nDecodedLen + 1;
  }

  public void encode (@Nullable final char [] aDecodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final Writer aWriter)
  {
    // Length 0 is okay, because it results in an empty string
    if (aDecodedBuffer == null)
      return;

    try
    {
      // Opening quote
      aWriter.write (QUOTE_CHAR);

      for (int i = 0; i < nLen; ++i)
      {
        final char b = aDecodedBuffer[nOfs + i];
        if (b == ESCAPE_CHAR || b == QUOTE_CHAR)
          aWriter.write (ESCAPE_CHAR);
        aWriter.write (b);
      }

      // closing quote
      aWriter.write (QUOTE_CHAR);
    }
    catch (final IOException ex)
    {
      throw new EncodeException ("Failed to encode RFC2616", ex);
    }
  }

  @Nonnegative
  @Override
  public int getMaximumDecodedLength (@Nonnegative final int nEncodedLen)
  {
    if (nEncodedLen < 2)
      return 0;
    // Without leading and trailing quote
    return nEncodedLen - 2;
  }

  public void decode (@Nullable final char [] aEncodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final Writer aWriter)
  {
    if (aEncodedBuffer == null)
      return;

    if (nLen < 2)
      throw new DecodeException ("At least the 2 quote characters must be present. Provided length is only " + nLen);

    if (aEncodedBuffer[nOfs] != QUOTE_CHAR)
      throw new DecodeException ("The provided bytes does not seem to be encoded. The first byte is not the double quote character.");
    final int nLastOfs = nOfs + nLen - 1;
    if (aEncodedBuffer[nLastOfs] != QUOTE_CHAR)
      throw new DecodeException ("The provided bytes does not seem to be encoded. The last byte is not the double quote character.");

    try
    {
      for (int i = nOfs + 1; i < nLastOfs; ++i)
      {
        final char c = aEncodedBuffer[i];
        if (c == ESCAPE_CHAR)
        {
          if (i == nLastOfs - 1)
            throw new DecodeException ("The encoded string seems to be cut. The second last character cannot be an escape character.");
          ++i;
          aWriter.write (aEncodedBuffer[i]);
        }
        else
          aWriter.write (c);
      }
    }
    catch (final IOException ex)
    {
      throw new DecodeException ("Failed to decode RFC2616", ex);
    }
  }
}
