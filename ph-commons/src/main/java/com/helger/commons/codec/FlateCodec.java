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

import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.io.stream.StreamHelper;

/**
 * Encoder and decoder for flate compression
 *
 * @author Philip Helger
 */
public class FlateCodec implements IByteArrayCodec
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (FlateCodec.class);

  public FlateCodec ()
  {}

  public static boolean isZlibHead (@Nonnull final byte [] buf)
  {
    return isZlibHead (buf, 0, buf.length);
  }

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

  @Nullable
  @ReturnsMutableCopy
  public static byte [] getDecodedFlate (@Nullable final byte [] aEncodedBuffer)
  {
    if (aEncodedBuffer == null)
      return null;

    return getDecodedFlate (aEncodedBuffer, 0, aEncodedBuffer.length);
  }

  @Nullable
  @ReturnsMutableCopy
  public static byte [] getDecodedFlate (@Nullable final byte [] aEncodedBuffer,
                                         @Nonnegative final int nOfs,
                                         @Nonnegative final int nLen)
  {
    if (aEncodedBuffer == null)
      return null;

    if (!isZlibHead (aEncodedBuffer, nOfs, nLen))
      s_aLogger.warn ("ZLib header not found");

    final InflaterInputStream aDecodeIS = new InflaterInputStream (new NonBlockingByteArrayInputStream (aEncodedBuffer,
                                                                                                        nOfs,
                                                                                                        nLen));
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      if (StreamHelper.copyInputStreamToOutputStream (aDecodeIS, aBAOS).isFailure ())
        throw new DecodeException ("Failed to flate decode!");
      return aBAOS.toByteArray ();
    }
  }

  @Nullable
  @ReturnsMutableCopy
  public byte [] getDecoded (@Nullable final byte [] aEncodedBuffer,
                             @Nonnegative final int nOfs,
                             @Nonnegative final int nLen)
  {
    return getDecodedFlate (aEncodedBuffer, nOfs, nLen);
  }

  @Nullable
  @ReturnsMutableCopy
  public static byte [] getEncodedFlate (@Nullable final byte [] aDecodedBuffer)
  {
    if (aDecodedBuffer == null)
      return null;
    return getEncodedFlate (aDecodedBuffer, 0, aDecodedBuffer.length);
  }

  @Nullable
  @ReturnsMutableCopy
  public static byte [] getEncodedFlate (@Nullable final byte [] aDecodedBuffer,
                                         @Nonnegative final int nOfs,
                                         @Nonnegative final int nLen)
  {
    if (aDecodedBuffer == null)
      return null;

    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    final DeflaterOutputStream aEncodeOS = new DeflaterOutputStream (aBAOS);
    try
    {
      if (StreamHelper.copyInputStreamToOutputStream (new NonBlockingByteArrayInputStream (aDecodedBuffer, nOfs, nLen),
                                                      aEncodeOS)
                      .isFailure ())
        throw new EncodeException ("Failed to flate encode!");
    }
    finally
    {
      StreamHelper.close (aEncodeOS);
    }
    return aBAOS.toByteArray ();
  }

  @Nullable
  @ReturnsMutableCopy
  public byte [] getEncoded (@Nullable final byte [] aDecodedBuffer, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    return getEncodedFlate (aDecodedBuffer, nOfs, nLen);
  }
}
