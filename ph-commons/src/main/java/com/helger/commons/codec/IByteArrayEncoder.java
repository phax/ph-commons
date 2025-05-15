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

import java.nio.charset.Charset;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.misc.ReturnsMutableCopy;

/**
 * Interface for a single encoder of bytes.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IByteArrayEncoder extends IEncoder <byte [], byte []>
{
  /**
   * Get the maximum encoded length based on the provided decoded length. This
   * is purely for performance reasons. The name of the method would be better
   * called "getMaximumEncodedLength".
   *
   * @param nDecodedLen
   *        The decoded length. Always &ge; 0.
   * @return The maximum encoded length. Always &ge; 0.
   * @since 9.3.6
   */
  @Nonnegative
  default int getMaximumEncodedLength (@Nonnegative final int nDecodedLen)
  {
    return nDecodedLen;
  }

  /**
   * Encode a byte array.
   *
   * @param aDecodedBuffer
   *        The byte array to be encoded. May be <code>null</code>.
   * @return The encoded byte array or <code>null</code> if the parameter was
   *         <code>null</code>.
   * @throws EncodeException
   *         In case something goes wrong
   */
  @Nullable
  @ReturnsMutableCopy
  default byte [] getEncoded (@Nullable final byte [] aDecodedBuffer)
  {
    if (aDecodedBuffer == null)
      return null;
    return getEncoded (aDecodedBuffer, 0, aDecodedBuffer.length);
  }

  /**
   * Encode a byte array.
   *
   * @param aDecodedBuffer
   *        The byte array to be encoded. May be <code>null</code>.
   * @param nOfs
   *        Offset into the byte array to start from.
   * @param nLen
   *        Number of bytes starting from offset to consider.
   * @return The encoded byte array or <code>null</code> if the parameter was
   *         <code>null</code>.
   * @throws EncodeException
   *         In case something goes wrong
   */
  @Nullable
  @ReturnsMutableCopy
  byte [] getEncoded (@Nullable final byte [] aDecodedBuffer, @Nonnegative final int nOfs, @Nonnegative final int nLen);

  /**
   * Encode the passed string.
   *
   * @param sDecoded
   *        The string to be encoded. May be <code>null</code>.
   * @param aCharset
   *        The charset to be used. May not be <code>null</code>.
   * @return <code>null</code> if the input string is <code>null</code>.
   * @throws EncodeException
   *         In case something goes wrong
   */
  @Nullable
  @ReturnsMutableCopy
  default byte [] getEncoded (@Nullable final String sDecoded, @Nonnull final Charset aCharset)
  {
    if (sDecoded == null)
      return null;

    final byte [] aDecoded = sDecoded.getBytes (aCharset);
    return getEncoded (aDecoded);
  }
}
