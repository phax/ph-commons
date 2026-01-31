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

import java.io.IOException;
import java.io.OutputStream;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.WillNotClose;
import com.helger.base.CGlobal;
import com.helger.base.codec.DecodeException;
import com.helger.base.codec.IByteArrayStreamDecoder;
import com.helger.base.string.StringHex;

/**
 * Decoder for ASCII Hex encoding. It's similar to the {@link Base16Codec} but it ignores whitespace
 * characters and ends with the ">" character
 *
 * @author Philip Helger
 */
public class ASCIIHexCodec implements IByteArrayStreamDecoder
{
  public ASCIIHexCodec ()
  {}

  public void decode (final byte @Nullable [] aEncodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @NonNull @WillNotClose final OutputStream aOS)
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

        final byte nDecByte = (byte) StringHex.getHexValue ((char) nEncByte);
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
