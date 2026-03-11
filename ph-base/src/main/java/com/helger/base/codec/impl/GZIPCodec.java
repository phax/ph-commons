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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.WillNotClose;
import com.helger.base.codec.DecodeException;
import com.helger.base.codec.EncodeException;
import com.helger.base.codec.IByteArrayCodec;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.stream.NonClosingOutputStream;
import com.helger.base.io.stream.StreamHelper;

/**
 * Encoder and decoder for GZip compression
 *
 * @author Philip Helger
 */
public class GZIPCodec implements IByteArrayCodec
{
  /**
   * Constructor.
   */
  public GZIPCodec ()
  {}

  /**
   * Decode the passed GZIP compressed buffer and write the decoded bytes to the output stream.
   *
   * @param aEncodedBuffer
   *        The GZIP compressed buffer to be decoded. May be <code>null</code>.
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

    try (final GZIPInputStream aDecodeIS = new GZIPInputStream (new NonBlockingByteArrayInputStream (aEncodedBuffer,
                                                                                                     nOfs,
                                                                                                     nLen)))
    {
      if (StreamHelper.copyInputStreamToOutputStream (aDecodeIS, aOS).isFailure ())
        throw new DecodeException ("Failed to GZIP decode!");
    }
    catch (final IOException ex)
    {
      throw new DecodeException ("Failed to GZIP encode", ex);
    }
  }

  /**
   * Encode the passed buffer using GZIP compression and write it to the output stream.
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

    try (final GZIPOutputStream aEncodeOS = new GZIPOutputStream (new NonClosingOutputStream (aOS)))
    {
      if (StreamHelper.copyInputStreamToOutputStream (new NonBlockingByteArrayInputStream (aDecodedBuffer, nOfs, nLen),
                                                      aEncodeOS).isFailure ())
        throw new EncodeException ("Failed to GZIP encode!");
    }
    catch (final IOException ex)
    {
      throw new EncodeException ("Failed to GZIP encode", ex);
    }
  }
}
