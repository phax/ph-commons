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
import com.helger.base.codec.DecodeException;
import com.helger.base.codec.EncodeException;
import com.helger.base.codec.IByteArrayCodec;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;

/**
 * Encoder and decoder for run length encoding as used by the PDF <code>RunLengthDecode</code>
 * filter (ISO 32000-1, 7.4.5), which is the same as the TIFF PackBits compression plus an end of
 * data marker:
 * <ul>
 * <li>A length byte from 0 to 127 is followed by 1 to 128 bytes that are copied literally (length +
 * 1 bytes)</li>
 * <li>A length byte from 129 to 255 is followed by a single byte that is repeated 257 - length
 * times (2 to 128 times)</li>
 * <li>A length byte of 128 marks the end of the data</li>
 * </ul>
 *
 * @author Philip Helger
 */
public class RunLengthCodec implements IByteArrayCodec
{
  protected static final int RUN_LENGTH_EOD = 0x80;

  // The maximum number of bytes in a literal or a repeated run
  private static final int MAX_RUN_LENGTH = 128;
  // The minimum number of equal bytes that are written as a repeated run
  private static final int MIN_REPEAT_LENGTH = 3;

  /**
   * Constructor.
   */
  public RunLengthCodec ()
  {}

  @Override
  @Nonnegative
  public int getMaximumEncodedLength (@Nonnegative final int nDecodedLen)
  {
    // Worst case: only literal runs, one length byte per 128 bytes, plus the end of data marker
    return nDecodedLen + (nDecodedLen + MAX_RUN_LENGTH - 1) / MAX_RUN_LENGTH + 1;
  }

  private static void _writeLiteral (final byte @NonNull [] aBuf,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen,
                                     @NonNull final OutputStream aOS) throws IOException
  {
    aOS.write (nLen - 1);
    aOS.write (aBuf, nOfs, nLen);
  }

  /**
   * Run length encode the passed buffer and write the encoded bytes to the output stream. The end
   * of data marker is always written.
   *
   * @param aDecodedBuffer
   *        The buffer to be encoded. May be <code>null</code>.
   * @param nOfs
   *        The offset in the buffer to start encoding from.
   * @param nLen
   *        The number of bytes to encode.
   * @param aOS
   *        The output stream to write the encoded data to. May not be <code>null</code>.
   */
  public void encode (final byte @Nullable [] aDecodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @NonNull @WillNotClose final OutputStream aOS)
  {
    ValueEnforcer.notNull (aOS, "OutputStream");
    if (aDecodedBuffer == null)
      return;
    ValueEnforcer.isArrayOfsLen (aDecodedBuffer, nOfs, nLen);

    try
    {
      final int nEnd = nOfs + nLen;
      // Start of the pending literal run
      int nLiteralStart = nOfs;
      int nIndex = nOfs;
      while (nIndex < nEnd)
      {
        // Determine the number of equal bytes starting at the current index
        final byte nByte = aDecodedBuffer[nIndex];
        int nRepeat = 1;
        while (nIndex + nRepeat < nEnd && nRepeat < MAX_RUN_LENGTH && aDecodedBuffer[nIndex + nRepeat] == nByte)
          nRepeat++;

        if (nRepeat >= MIN_REPEAT_LENGTH)
        {
          // Write the pending literal run first
          while (nLiteralStart < nIndex)
          {
            final int nLiteralLen = Math.min (nIndex - nLiteralStart, MAX_RUN_LENGTH);
            _writeLiteral (aDecodedBuffer, nLiteralStart, nLiteralLen, aOS);
            nLiteralStart += nLiteralLen;
          }

          aOS.write (257 - nRepeat);
          aOS.write (nByte);
          nIndex += nRepeat;
          nLiteralStart = nIndex;
        }
        else
        {
          // Add to the literal run
          nIndex += nRepeat;
          if (nIndex - nLiteralStart >= MAX_RUN_LENGTH)
          {
            _writeLiteral (aDecodedBuffer, nLiteralStart, MAX_RUN_LENGTH, aOS);
            nLiteralStart += MAX_RUN_LENGTH;
          }
        }
      }

      // Write the remaining literal run
      while (nLiteralStart < nEnd)
      {
        final int nLiteralLen = Math.min (nEnd - nLiteralStart, MAX_RUN_LENGTH);
        _writeLiteral (aDecodedBuffer, nLiteralStart, nLiteralLen, aOS);
        nLiteralStart += nLiteralLen;
      }

      aOS.write (RUN_LENGTH_EOD);
    }
    catch (final IOException ex)
    {
      throw new EncodeException ("Failed to encode RunLength", ex);
    }
  }

  /**
   * Decode the passed run length encoded buffer and write the decoded bytes to the output stream.
   *
   * @param aEncodedBuffer
   *        The run length encoded buffer to be decoded. May be <code>null</code>.
   * @param nOfs
   *        The offset in the buffer to start decoding from.
   * @param nLen
   *        The number of bytes to decode.
   * @param aOS
   *        The output stream to write the decoded data to. May not be <code>null</code>.
   */
  public void decode (final byte @Nullable [] aEncodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @NonNull @WillNotClose final OutputStream aOS)
  {
    if (aEncodedBuffer == null || nLen == 0)
      return;

    try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (aEncodedBuffer, nOfs, nLen))
    {
      int nDupAmount;
      final byte [] aReadBuffer = new byte [MAX_RUN_LENGTH];
      while ((nDupAmount = aBAIS.read ()) != -1 && nDupAmount != RUN_LENGTH_EOD)
      {
        if (nDupAmount <= 0x7f)
        {
          // no duplicates present - copy the next length + 1 bytes
          int nAmountToCopy = nDupAmount + 1;
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
