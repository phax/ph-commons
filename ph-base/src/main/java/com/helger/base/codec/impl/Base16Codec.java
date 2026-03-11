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
import java.io.InputStream;
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
import com.helger.base.string.StringHex;

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

  /**
   * Encode data from the input stream and write the Base16 encoded result to the output stream.
   *
   * @param aDecodedIS
   *        The decoded input stream to read from. May not be <code>null</code>.
   * @param aOS
   *        The output stream to write the encoded data to. May not be <code>null</code>.
   */
  public void encode (@NonNull @WillNotClose final InputStream aDecodedIS,
                      @NonNull @WillNotClose final OutputStream aOS)
  {
    ValueEnforcer.notNull (aDecodedIS, "DecodedInputStream");
    ValueEnforcer.notNull (aOS, "OutputStream");

    try
    {
      int nByte;
      while ((nByte = aDecodedIS.read ()) != -1)
      {
        aOS.write (StringHex.getHexChar ((nByte & 0xf0) >> 4));
        aOS.write (StringHex.getHexChar (nByte & 0x0f));
      }
    }
    catch (final IOException ex)
    {
      throw new EncodeException ("Failed to encode Base16", ex);
    }
  }

  /**
   * Encode the passed decoded buffer to Base16 and write it to the output stream.
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
    if (aDecodedBuffer == null || nLen == 0)
      return;

    try (final NonBlockingByteArrayInputStream aIS = new NonBlockingByteArrayInputStream (aDecodedBuffer,
                                                                                          nOfs,
                                                                                          nLen,
                                                                                          false))
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

  /**
   * Decode data from the Base16 encoded input stream and write the decoded result to the output
   * stream.
   *
   * @param aEncodedIS
   *        The Base16 encoded input stream to read from. May not be <code>null</code>.
   * @param aOS
   *        The output stream to write the decoded data to. May not be <code>null</code>.
   */
  public void decode (@NonNull @WillNotClose final InputStream aEncodedIS,
                      @NonNull @WillNotClose final OutputStream aOS)
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
          throw new DecodeException ("Invalid Base16 encoding. Premature end of input after " +
                                     nBytesRead +
                                     " byte(s)");
        nBytesRead++;
        final char cLow = (char) (nByte & 0xff);

        // Combine
        final int nDecodedValue = StringHex.getHexByte (cHigh, cLow);
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

  /**
   * Decode the passed Base16 encoded buffer and write the decoded bytes to the output stream.
   *
   * @param aEncodedBuffer
   *        The Base16 encoded buffer to be decoded. May be <code>null</code>.
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
    if (aEncodedBuffer == null)
      return;

    try (final NonBlockingByteArrayInputStream aIS = new NonBlockingByteArrayInputStream (aEncodedBuffer,
                                                                                          nOfs,
                                                                                          nLen,
                                                                                          false))
    {
      decode (aIS, aOS);
    }
  }
}
