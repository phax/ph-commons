/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.base.codec.impl;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.codec.IEncoder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHex;

/**
 * RFC 5987 Encoder. Character Set and Language Encoding for Hypertext Transfer Protocol (HTTP)
 * Header Field Parameters
 *
 * @author Philip Helger
 */
public class RFC5987Codec implements IEncoder <String, String>
{
  public static final char ESCAPE_CHAR = '%';
  // Order is important for binary search!
  private static final byte [] ALLOWED_BYTES = { '!',
                                                 '#',
                                                 '$',
                                                 '&',
                                                 '+',
                                                 '-',
                                                 '.',
                                                 '0',
                                                 '1',
                                                 '2',
                                                 '3',
                                                 '4',
                                                 '5',
                                                 '6',
                                                 '7',
                                                 '8',
                                                 '9',
                                                 'A',
                                                 'B',
                                                 'C',
                                                 'D',
                                                 'E',
                                                 'F',
                                                 'G',
                                                 'H',
                                                 'I',
                                                 'J',
                                                 'K',
                                                 'L',
                                                 'M',
                                                 'N',
                                                 'O',
                                                 'P',
                                                 'Q',
                                                 'R',
                                                 'S',
                                                 'T',
                                                 'U',
                                                 'V',
                                                 'W',
                                                 'X',
                                                 'Y',
                                                 'Z',
                                                 '^',
                                                 '_',
                                                 '`',
                                                 'a',
                                                 'b',
                                                 'c',
                                                 'd',
                                                 'e',
                                                 'f',
                                                 'g',
                                                 'h',
                                                 'i',
                                                 'j',
                                                 'k',
                                                 'l',
                                                 'm',
                                                 'n',
                                                 'o',
                                                 'p',
                                                 'q',
                                                 'r',
                                                 's',
                                                 't',
                                                 'u',
                                                 'v',
                                                 'w',
                                                 'x',
                                                 'y',
                                                 'z',
                                                 '|',
                                                 '~' };
  private final Charset m_aCharset;

  public RFC5987Codec ()
  {
    this (StandardCharsets.UTF_8);
  }

  public RFC5987Codec (@NonNull final Charset aCharset)
  {
    m_aCharset = ValueEnforcer.notNull (aCharset, "Charset");
  }

  @NonNull
  public static String getRFC5987Encoded (@NonNull final String sSrc, @NonNull final Charset aCharset)
  {
    ValueEnforcer.notNull (sSrc, "Src");

    final StringBuilder aSB = new StringBuilder (sSrc.length () * 2);
    for (final byte b : sSrc.getBytes (aCharset))
    {
      if (Arrays.binarySearch (ALLOWED_BYTES, b) >= 0)
        aSB.append ((char) b);
      else
      {
        aSB.append (ESCAPE_CHAR).append (StringHex.getHexChar ((b >> 4) & 0xf)).append (StringHex.getHexChar (b & 0xf));
      }
    }

    return aSB.toString ();
  }

  @NonNull
  public static String getRFC5987EncodedUTF8 (@NonNull final String sSrc)
  {
    return getRFC5987Encoded (sSrc, StandardCharsets.UTF_8);
  }

  @Nullable
  public String getEncoded (@Nullable final String sSrc)
  {
    if (sSrc == null)
      return null;
    return getRFC5987Encoded (sSrc, m_aCharset);
  }
}
