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

import com.helger.commons.CGlobal;
import com.helger.commons.string.StringHelper;

/**
 * Decoder for ASCII Hex encoding
 *
 * @author Philip Helger
 */
public class ASCIIHexCodec implements IByteArrayStreamDecoder
{
  public ASCIIHexCodec ()
  {}

  public void decode (@Nullable final byte [] aEncodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
  {
    if (aEncodedBuffer == null || nLen == 0)
      return;

    try
    {
      boolean bFirstByte = true;
      int nFirstByte = 0;
      for (int i = 0; i < nLen; ++i)
      {
        final byte nEncByte = aEncodedBuffer[nOfs + i];
        if (nEncByte == '>')
          break;

        // Ignore whitespaces
        if (Character.isWhitespace (nEncByte))
          continue;

        final byte nDecByte = (byte) StringHelper.getHexValue ((char) nEncByte);
        if (nDecByte == CGlobal.ILLEGAL_UINT)
          throw new DecodeException ("Failed to convert byte '" +
                                     nEncByte +
                                     "/" +
                                     ((char) nEncByte) +
                                     "' to hex value in ASCIIHexDecode");
        if (bFirstByte)
          nFirstByte = nDecByte;
        else
          aOS.write ((byte) (nFirstByte << 4 | nDecByte & 0xff));
        bFirstByte = !bFirstByte;
      }

      // Write trailing byte
      if (!bFirstByte)
        aOS.write ((byte) (nFirstByte << 4));
    }
    catch (final IOException ex)
    {
      throw new DecodeException ("Failed to decode ASCII Hex", ex);
    }
  }
}
