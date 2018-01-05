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
package com.helger.commons.codec;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

import com.helger.commons.ValueEnforcer;

/**
 * Decoder for ASCII85 encoded values
 *
 * @author Philip Helger
 */
public class ASCII85Codec implements IByteArrayStreamDecoder
{
  private static final int BIT1 = 8;
  private static final int BIT2 = 16;
  private static final int BIT3 = 24;
  private static final int ENCODED_MAX = 117;
  private static final int ENCODED_MIN = 33;
  private static final int EIGHTY_FIVE = 85;

  public ASCII85Codec ()
  {}

  public void decode (@Nullable final byte [] aEncodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
  {
    if (aEncodedBuffer == null)
      return;
    ValueEnforcer.isTrue (nLen >= 4, () -> "Buffer too small: " + nLen);

    try
    {
      int nEncodedCount = 0;
      final byte [] aBuffer = new byte [5];

      // Determine start index
      int nIndex = nOfs;

      // Special start sequence "<~" ??
      if (aEncodedBuffer[nIndex] == '<' && aEncodedBuffer[nIndex + 1] == '~')
        nIndex += 2;

      for (; nIndex < nOfs + nLen; ++nIndex)
      {
        final byte nEncByte = aEncodedBuffer[nIndex];

        // end of data with "~>"
        if (nEncByte == '~')
          break;

        // skip all whitespaces
        if (Character.isWhitespace (nEncByte))
          continue;

        if (nEncByte == 'z' && nEncodedCount == 0)
        {
          aOS.write (0);
          aOS.write (0);
          aOS.write (0);
          aOS.write (0);
        }
        else
        {
          if (nEncByte < ENCODED_MIN || nEncByte > ENCODED_MAX)
            throw new DecodeException ("Illegal character in ASCII85Decode: " + nEncByte);

          aBuffer[nEncodedCount] = (byte) (nEncByte - ENCODED_MIN);
          ++nEncodedCount;
          if (nEncodedCount == 5)
          {
            nEncodedCount = 0;
            int r = 0;
            for (int j = 0; j < 5; ++j)
              r = r * EIGHTY_FIVE + aBuffer[j];
            aOS.write ((byte) (r >> BIT3));
            aOS.write ((byte) (r >> BIT2));
            aOS.write ((byte) (r >> BIT1));
            aOS.write ((byte) r);
          }
        }
      }

      int nRest;
      switch (nEncodedCount)
      {
        case 1:
          throw new IllegalStateException ("Unexpected end of ASCII85 encoded data!");
        case 2:
          nRest = (aBuffer[0] * EIGHTY_FIVE * EIGHTY_FIVE * EIGHTY_FIVE * EIGHTY_FIVE) +
                  (aBuffer[1] * EIGHTY_FIVE * EIGHTY_FIVE * EIGHTY_FIVE) +
                  (EIGHTY_FIVE * EIGHTY_FIVE * EIGHTY_FIVE) +
                  (EIGHTY_FIVE * EIGHTY_FIVE) +
                  EIGHTY_FIVE;
          aOS.write ((byte) (nRest >> BIT3));
          break;
        case 3:
          nRest = (aBuffer[0] * EIGHTY_FIVE * EIGHTY_FIVE * EIGHTY_FIVE * EIGHTY_FIVE) +
                  (aBuffer[1] * EIGHTY_FIVE * EIGHTY_FIVE * EIGHTY_FIVE) +
                  (aBuffer[2] * EIGHTY_FIVE * EIGHTY_FIVE) +
                  (EIGHTY_FIVE * EIGHTY_FIVE) +
                  EIGHTY_FIVE;
          aOS.write ((byte) (nRest >> BIT3));
          aOS.write ((byte) (nRest >> BIT2));
          break;
        case 4:
          nRest = (aBuffer[0] * EIGHTY_FIVE * EIGHTY_FIVE * EIGHTY_FIVE * EIGHTY_FIVE) +
                  (aBuffer[1] * EIGHTY_FIVE * EIGHTY_FIVE * EIGHTY_FIVE) +
                  (aBuffer[2] * EIGHTY_FIVE * EIGHTY_FIVE) +
                  (aBuffer[3] * EIGHTY_FIVE) +
                  EIGHTY_FIVE;
          aOS.write ((byte) (nRest >> BIT3));
          aOS.write ((byte) (nRest >> BIT2));
          aOS.write ((byte) (nRest >> BIT1));
          break;
        default:
          break;
      }
    }
    catch (final IOException ex)
    {
      throw new DecodeException ("Failed to decode ASCII85", ex);
    }
  }
}
