/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.base64.Base64;
import com.helger.commons.base64.Base64InputStream;
import com.helger.commons.base64.Base64OutputStream;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.io.stream.NonClosingOutputStream;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.math.MathHelper;

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

  @Nonnull
  @OverrideOnDemand
  protected Base64OutputStream createBase64OutputStream (@Nonnull @WillNotClose final OutputStream aOS)
  {
    return new Base64OutputStream (new NonClosingOutputStream (aOS));
  }

  public void encode (@Nullable final byte [] aDecodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
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

  @Nullable
  @ReturnsMutableCopy
  @Override
  public byte [] getEncoded (@Nullable final byte [] aDecodedBuffer, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    return Base64.safeEncodeBytesToBytes (aDecodedBuffer, nOfs, nLen);
  }

  @Override
  public int getMaximumDecodedLength (final int nLen)
  {
    return MathHelper.getRoundedUp (nLen, 4) * 3 / 4;
  }

  @Nonnull
  @OverrideOnDemand
  protected Base64InputStream createBase64InputStream (@Nonnull final NonBlockingByteArrayInputStream aBAIS)
  {
    return new Base64InputStream (aBAIS);
  }

  public void decode (@Nullable final byte [] aEncodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
  {
    if (aEncodedBuffer != null)
      try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (aEncodedBuffer, nOfs, nLen);
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

  @Nullable
  @ReturnsMutableCopy
  @Override
  public byte [] getDecoded (@Nullable final byte [] aEncodedBuffer, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    return Base64.safeDecode (aEncodedBuffer, nOfs, nLen);
  }
}
