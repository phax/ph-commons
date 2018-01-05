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

import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;

/**
 * Decoder for run length encoding
 *
 * @author Philip Helger
 */
public class RunLengthCodec implements IByteArrayStreamDecoder
{
  protected static final int RUN_LENGTH_EOD = 0x80;

  public RunLengthCodec ()
  {}

  public void decode (@Nullable final byte [] aEncodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
  {
    if (aEncodedBuffer == null || nLen == 0)
      return;

    try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (aEncodedBuffer, nOfs, nLen))
    {
      int nDupAmount;
      final byte [] aReadBuffer = new byte [128];
      while ((nDupAmount = aBAIS.read ()) != -1 && nDupAmount != RUN_LENGTH_EOD)
      {
        if (nDupAmount <= 0x7f)
        {
          // no duplicates present
          int nAmountToCopy = nDupAmount;
          while (nAmountToCopy > 0)
          {
            final int nCompressedRead = aBAIS.read (aReadBuffer, 0, nAmountToCopy);
            if (nCompressedRead < 0)
              throw new DecodeException ("Unexpected EOF in RunLengthCodec - " + nAmountToCopy + " elements left");

            aOS.write (aReadBuffer, 0, nCompressedRead);
            nAmountToCopy -= nCompressedRead;
          }
        }
        else
        {
          // we have something duplicated
          final int aDupByte = aBAIS.read ();
          if (aDupByte == -1)
            throw new DecodeException ("Unexpected EOF in RunLengthCodec");

          // The char is repeated for 257-nDupAmount times
          for (int i = 0; i < 257 - nDupAmount; i++)
            aOS.write (aDupByte);
        }
      }
    }
    catch (final IOException ex)
    {
      throw new DecodeException ("Failed to decode RunLength", ex);
    }
  }
}
