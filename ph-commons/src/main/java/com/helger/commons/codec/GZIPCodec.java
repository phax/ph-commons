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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.io.stream.NonClosingOutputStream;
import com.helger.commons.io.stream.StreamHelper;

/**
 * Encoder and decoder for GZip compression
 *
 * @author Philip Helger
 */
public class GZIPCodec implements IByteArrayCodec
{
  public GZIPCodec ()
  {}

  public void decode (@Nullable final byte [] aEncodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
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

  public void encode (@Nullable final byte [] aDecodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
  {
    if (aDecodedBuffer == null || nLen == 0)
      return;

    try (final GZIPOutputStream aEncodeOS = new GZIPOutputStream (new NonClosingOutputStream (aOS)))
    {
      if (StreamHelper.copyInputStreamToOutputStream (new NonBlockingByteArrayInputStream (aDecodedBuffer, nOfs, nLen),
                                                      aEncodeOS)
                      .isFailure ())
        throw new EncodeException ("Failed to GZIP encode!");
    }
    catch (final IOException ex)
    {
      throw new EncodeException ("Failed to GZIP encode", ex);
    }
  }
}
