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
package com.helger.commons.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.WillNotClose;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.string.StringHelper;

/**
 * Base16 encoder and decoder.
 *
 * @author Philip Helger
 */
public class Base16Codec implements IByteArrayCodec
{
  /**
   * Creates a Base16 codec used for decoding and encoding.
   */
  public Base16Codec ()
  {}

  @Override
  @Nonnegative
  public int getMaximumEncodedLength (@Nonnegative final int nDecodedLen)
  {
    return nDecodedLen * 2;
  }

  public void encode (@Nonnull @WillNotClose final InputStream aDecodedIS, @Nonnull @WillNotClose final OutputStream aOS)
  {
    ValueEnforcer.notNull (aDecodedIS, "DecodedInputStream");
    ValueEnforcer.notNull (aOS, "OutputStream");

    try
    {
      int nByte;
      while ((nByte = aDecodedIS.read ()) != -1)
      {
        aOS.write (StringHelper.getHexChar ((nByte & 0xf0) >> 4));
        aOS.write (StringHelper.getHexChar (nByte & 0x0f));
      }
    }
    catch (final IOException ex)
    {
      throw new EncodeException ("Failed to encode Base16", ex);
    }
  }

  public void encode (@Nullable final byte [] aDecodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
  {
    if (aDecodedBuffer == null || nLen == 0)
      return;

    try (final NonBlockingByteArrayInputStream aIS = new NonBlockingByteArrayInputStream (aDecodedBuffer, nOfs, nLen, false))
    {
      encode (aIS, aOS);
    }
  }

  @Override
  @Nonnegative
  public int getMaximumDecodedLength (@Nonnegative final int nEncodedLen)
  {
    return nEncodedLen / 2;
  }

  public void decode (@Nonnull @WillNotClose final InputStream aEncodedIS, @Nonnull @WillNotClose final OutputStream aOS)
  {
    ValueEnforcer.notNull (aEncodedIS, "EncodedInputStream");
    ValueEnforcer.notNull (aOS, "OutputStream");

    try
    {
      long nBytesRead = 0;
      int nByte;
      // Read high byte
      while ((nByte = aEncodedIS.read ()) != -1)
      {
        nBytesRead++;
        final char cHigh = (char) (nByte & 0xff);

        // Read low byte
        nByte = aEncodedIS.read ();
        if (nByte < 0)
          throw new DecodeException ("Invalid Base16 encoding. Premature end of input after " + nBytesRead + " byte(s)");
        nBytesRead++;
        final char cLow = (char) (nByte & 0xff);

        // Combine
        final int nDecodedValue = StringHelper.getHexByte (cHigh, cLow);
        if (nDecodedValue < 0)
          throw new DecodeException ("Invalid Base16 encoding for " +
                                     (int) cHigh +
                                     " and " +
                                     (int) cLow +
                                     " after " +
                                     nBytesRead +
                                     " byte(s)");

        // Write
        aOS.write (nDecodedValue);
      }
    }
    catch (final IOException ex)
    {
      throw new DecodeException ("Failed to decode Base16", ex);
    }
  }

  public void decode (@Nullable final byte [] aEncodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
  {
    if (aEncodedBuffer == null)
      return;

    try (final NonBlockingByteArrayInputStream aIS = new NonBlockingByteArrayInputStream (aEncodedBuffer, nOfs, nLen, false))
    {
      decode (aIS, aOS);
    }
  }
}
