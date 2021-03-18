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

/**
 * Special implementation of {@link IByteArrayCodec} that does nothing. This is
 * a separate class to be able to identify it from non-identity codecs.
 *
 * @author Philip Helger
 * @since 9.1.7
 */
public class IdentityByteArrayCodec extends IdentityCodec <byte []> implements IByteArrayCodec
{
  /** Default instance that can be used. */
  public static final IdentityByteArrayCodec INSTANCE = new IdentityByteArrayCodec ();

  public void decode (@Nullable final byte [] aEncodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
  {
    try
    {
      aOS.write (aEncodedBuffer, nOfs, nLen);
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException (ex);
    }
  }

  public void encode (@Nullable final byte [] aDecodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
  {
    try
    {
      aOS.write (aDecodedBuffer, nOfs, nLen);
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException (ex);
    }
  }
}
