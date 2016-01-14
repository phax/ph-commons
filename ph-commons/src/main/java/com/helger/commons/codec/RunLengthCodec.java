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

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;

/**
 * Decoder for run length encoding
 *
 * @author Philip Helger
 */
public class RunLengthCodec implements IByteArrayDecoder
{
  protected static final int RUN_LENGTH_EOD = 128;

  public RunLengthCodec ()
  {}

  @Nullable
  @ReturnsMutableCopy
  public byte [] getDecoded (@Nullable final byte [] aEncodedBuffer)
  {
    return getDecodedRunLength (aEncodedBuffer);
  }

  @Nullable
  @ReturnsMutableCopy
  public static byte [] getDecodedRunLength (@Nullable final byte [] aEncodedBuffer)
  {
    if (aEncodedBuffer == null)
      return null;

    int nDupAmount;
    final byte [] aReadBuffer = new byte [128];
    try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (aEncodedBuffer);
        final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      while ((nDupAmount = aBAIS.read ()) != -1 && nDupAmount != RUN_LENGTH_EOD)
      {
        if (nDupAmount <= 127)
        {
          // no duplicates present
          int nAmountToCopy = nDupAmount + 1;
          while (nAmountToCopy > 0)
          {
            final int nCompressedRead = aBAIS.read (aReadBuffer, 0, nAmountToCopy);
            aBAOS.write (aReadBuffer, 0, nCompressedRead);
            nAmountToCopy -= nCompressedRead;
          }
        }
        else
        {
          // we have something duplicated
          final int aDupByte = aBAIS.read ();
          if (aDupByte == -1)
            throw new DecodeException ("Unexpected EOF");

          // The char is repeated for 257-nDupAmount types
          for (int i = 0; i < 257 - nDupAmount; i++)
            aBAOS.write (aDupByte);
        }
      }
      return aBAOS.toByteArray ();
    }
  }
}
