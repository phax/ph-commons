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
package com.helger.base.codec.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.WillNotClose;
import com.helger.base.codec.DecodeException;
import com.helger.base.codec.EncodeException;
import com.helger.base.codec.IByteArrayCodec;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.stream.NonClosingOutputStream;
import com.helger.base.io.stream.StreamHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Encoder and decoder for flate compression
 *
 * @author Philip Helger
 */
public class FlateCodec implements IByteArrayCodec
{
  private static final Logger LOGGER = LoggerFactory.getLogger (FlateCodec.class);

  public FlateCodec ()
  {}

  public static boolean isZlibHead (@Nonnull final byte [] buf,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen)
  {
    if (nLen >= 2)
    {
      final int b0 = buf[nOfs] & 0xff;
      final int b1 = buf[nOfs + 1] & 0xff;

      if ((b0 & 0xf) == 8)
        if ((b0 >> 4) + 8 <= 15)
          if ((((b0 << 8) + b1) % 31) == 0)
            return true;
    }
    return false;
  }

  public void decode (@Nullable final byte [] aEncodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
  {
    if (aEncodedBuffer == null || nLen == 0)
      return;

    if (!isZlibHead (aEncodedBuffer, nOfs, nLen))
      LOGGER.warn ("ZLib header not found");

    try (final InflaterInputStream aDecodeIS = new InflaterInputStream (new NonBlockingByteArrayInputStream (aEncodedBuffer,
                                                                                                             nOfs,
                                                                                                             nLen)))
    {
      if (StreamHelper.copyInputStreamToOutputStream (aDecodeIS, aOS).isFailure ())
        throw new DecodeException ("Failed to flate decode!");
    }
    catch (final IOException ex)
    {
      throw new DecodeException ("Failed to flate decode", ex);
    }
  }

  public void encode (@Nullable final byte [] aDecodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
  {
    if (aDecodedBuffer == null || nLen == 0)
      return;

    try (final DeflaterOutputStream aEncodeOS = new DeflaterOutputStream (new NonClosingOutputStream (aOS)))
    {
      if (StreamHelper.copyInputStreamToOutputStream (new NonBlockingByteArrayInputStream (aDecodedBuffer, nOfs, nLen),
                                                      aEncodeOS).isFailure ())
        throw new EncodeException ("Failed to flate encode!");
    }
    catch (final IOException ex)
    {
      throw new EncodeException ("Failed to flate encode", ex);
    }
  }
}
