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
package com.helger.base.codec.base64;

import java.io.IOException;
import java.io.OutputStream;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.WillNotClose;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.codec.DecodeException;
import com.helger.base.codec.EncodeException;
import com.helger.base.codec.IByteArrayCodec;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.stream.NonClosingOutputStream;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.numeric.MathHelper;

/**
 * Encoder and decoder for Base64
 *
 * @author Philip Helger
 */
public class Base64Codec implements IByteArrayCodec
{
  public Base64Codec ()
  {}

  @Override
  public int getMaximumEncodedLength (final int nLen)
  {
    return MathHelper.getRoundedUp (nLen * 4 / 3, 4);
  }

  @NonNull
  @OverrideOnDemand
  protected Base64OutputStream createBase64OutputStream (@NonNull @WillNotClose final OutputStream aOS)
  {
    return new Base64OutputStream (new NonClosingOutputStream (aOS));
  }

  public void encode (final byte @Nullable [] aDecodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @NonNull @WillNotClose final OutputStream aOS)
  {
    if (aDecodedBuffer == null || nLen == 0)
      return;

    try (final Base64OutputStream aB64OS = createBase64OutputStream (aOS))
    {
      aB64OS.write (aDecodedBuffer, nOfs, nLen);
    }
    catch (final IOException ex)
    {
      throw new EncodeException ("Failed to encode Base64", ex);
    }
  }

  @ReturnsMutableCopy
  @Override
  public byte @Nullable [] getEncoded (final byte @Nullable [] aDecodedBuffer,
                             @Nonnegative final int nOfs,
                             @Nonnegative final int nLen)
  {
    return Base64.safeEncodeBytesToBytes (aDecodedBuffer, nOfs, nLen);
  }

  @Override
  public int getMaximumDecodedLength (final int nLen)
  {
    return MathHelper.getRoundedUp (nLen, 4) * 3 / 4;
  }

  @NonNull
  @OverrideOnDemand
  protected Base64InputStream createBase64InputStream (@NonNull final NonBlockingByteArrayInputStream aBAIS)
  {
    return new Base64InputStream (aBAIS);
  }

  public void decode (final byte @Nullable [] aEncodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @NonNull @WillNotClose final OutputStream aOS)
  {
    if (aEncodedBuffer != null)
      try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (aEncodedBuffer,
                                                                                              nOfs,
                                                                                              nLen);
           final Base64InputStream aB64IS = createBase64InputStream (aBAIS))
      {
        if (StreamHelper.copyInputStreamToOutputStream (aB64IS, aOS).isFailure ())
          throw new DecodeException ("Failed to decode Base64 to OutputStream");
      }
      catch (final IOException ex)
      {
        throw new DecodeException ("Failed to decode Base64!", ex);
      }
  }

  @ReturnsMutableCopy
  @Override
  public byte @Nullable [] getDecoded (final byte @Nullable [] aEncodedBuffer,
                             @Nonnegative final int nOfs,
                             @Nonnegative final int nLen)
  {
    return Base64.safeDecode (aEncodedBuffer, nOfs, nLen);
  }
}
