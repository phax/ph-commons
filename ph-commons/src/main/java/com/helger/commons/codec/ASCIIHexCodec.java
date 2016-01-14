/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import javax.annotation.Nullable;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.string.StringHelper;

/**
 * Decoder for ASCII Hex encoding
 *
 * @author Philip Helger
 */
public class ASCIIHexCodec implements IByteArrayDecoder
{
  public ASCIIHexCodec ()
  {}

  @Nullable
  @ReturnsMutableCopy
  public byte [] getDecoded (@Nullable final byte [] aEncodedBuffer)
  {
    return getDecodedASCIIHex (aEncodedBuffer);
  }

  @Nullable
  @ReturnsMutableCopy
  public static byte [] getDecodedASCIIHex (@Nullable final byte [] aEncodedBuffer)
  {
    if (aEncodedBuffer == null)
      return null;

    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      boolean bFirstByte = true;
      int nFirstByte = 0;
      for (final byte nEncByte : aEncodedBuffer)
      {
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
          aBAOS.write ((byte) (nFirstByte << 4 | nDecByte));
        bFirstByte = !bFirstByte;
      }

      // Write trailing byte
      if (!bFirstByte)
        aBAOS.write ((byte) (nFirstByte << 4));
      return aBAOS.toByteArray ();
    }
  }
}
