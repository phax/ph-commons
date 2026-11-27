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
package com.helger.base.string;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.array.ArrayHelper;

/**
 * A set of characters to search for, that is cheap to create. Characters below 256 are looked up in
 * a bit mask, so that an input character that is not searched for (the common case) is skipped
 * without looping over all search characters. Only for characters from 256 on, the search
 * characters are scanned, and only if there are such search characters at all.
 *
 * @author Philip Helger
 * @since 12.9.0
 */
@Immutable
final class CharSearchSet
{
  /** 256 bits, one for each of the characters 0 to 255, in 4 longs of 64 bits each */
  private final long [] m_aLowMask = new long [4];
  /** The search chars, not copied - internal use only */
  private final char [] m_aChars;
  private final boolean m_bHasHighChar;

  CharSearchSet (final char @NonNull [] aChars)
  {
    m_aChars = aChars;
    boolean bHasHighChar = false;
    for (final char c : aChars)
      if (c < 256)
      {
        // The long that holds the bit of the character: 0-63 in the 1st, 64-127 in the 2nd ...
        // The shift implicitly uses only the lowest 6 bits, which is the position within the long
        m_aLowMask[c >>> 6] |= 1L << c;
      }
      else
        bHasHighChar = true;
    m_bHasHighChar = bHasHighChar;
  }

  /**
   * @param c
   *        The character to check.
   * @return <code>true</code> if the character is one of the search characters.
   */
  boolean contains (final char c)
  {
    if (c < 256)
      return (m_aLowMask[c >>> 6] & (1L << c)) != 0;
    return m_bHasHighChar && ArrayHelper.contains (m_aChars, c);
  }

  /**
   * @param c
   *        The character to search.
   * @return The index of the first occurrence of the character in the search characters, or -1 if
   *         it is not contained.
   */
  int getFirstIndex (final char c)
  {
    if (c < 256 ? (m_aLowMask[c >>> 6] & (1L << c)) == 0 : !m_bHasHighChar)
      return -1;
    return ArrayHelper.getFirstIndex (m_aChars, c);
  }
}
