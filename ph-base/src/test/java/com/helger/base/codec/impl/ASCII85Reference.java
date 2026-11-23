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
package com.helger.base.codec.impl;

import java.nio.charset.StandardCharsets;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.Immutable;

/**
 * A straightforward ASCII85 encoder (Adobe variant, as used in PDF) for testing the
 * {@link ASCII85Codec} decoder, which has no encoder of its own. Each group of 4 bytes becomes 5
 * characters from '!' to 'u', an all-zero group becomes 'z', a final group of n bytes becomes n + 1
 * characters, and the data ends with "~&gt;".
 *
 * @author Philip Helger
 */
@Immutable
final class ASCII85Reference
{
  private ASCII85Reference ()
  {}

  @NonNull
  static byte [] encode (final byte @NonNull [] aBytes)
  {
    final StringBuilder aSB = new StringBuilder ();
    for (int nOfs = 0; nOfs < aBytes.length; nOfs += 4)
    {
      final int nGroupLen = Math.min (4, aBytes.length - nOfs);
      long nValue = 0;
      for (int i = 0; i < 4; ++i)
        nValue = (nValue << 8) | (i < nGroupLen ? aBytes[nOfs + i] & 0xff : 0);

      if (nGroupLen == 4 && nValue == 0)
        aSB.append ('z');
      else
      {
        final char [] aDigits = new char [5];
        for (int i = 4; i >= 0; --i)
        {
          aDigits[i] = (char) ('!' + nValue % 85);
          nValue /= 85;
        }
        aSB.append (aDigits, 0, nGroupLen + 1);
      }
    }
    return aSB.append ("~>").toString ().getBytes (StandardCharsets.US_ASCII);
  }
}
