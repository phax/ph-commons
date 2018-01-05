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

import java.nio.charset.Charset;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;

/**
 * Interface for a single decoder.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IByteArrayDecoder extends IDecoder <byte [], byte []>
{
  @Nonnegative
  default int getDecodedLength (@Nonnegative final int nLen)
  {
    return nLen;
  }

  /**
   * Decode a byte array.
   *
   * @param aEncodedBuffer
   *        The byte array to be decoded. May be <code>null</code>.
   * @return The decoded byte array or <code>null</code> if the parameter was
   *         <code>null</code>.
   * @throws DecodeException
   *         in case something goes wrong
   */
  @Nullable
  @ReturnsMutableCopy
  default byte [] getDecoded (@Nullable final byte [] aEncodedBuffer)
  {
    if (aEncodedBuffer == null)
      return null;
    return getDecoded (aEncodedBuffer, 0, aEncodedBuffer.length);
  }

  /**
   * Decode a byte array.
   *
   * @param aEncodedBuffer
   *        The byte array to be decoded. May be <code>null</code>.
   * @param nOfs
   *        Offset into the byte array to start from.
   * @param nLen
   *        Number of bytes starting from offset to consider.
   * @return The decoded byte array or <code>null</code> if the parameter was
   *         <code>null</code>.
   * @throws DecodeException
   *         in case something goes wrong
   */
  @Nullable
  @ReturnsMutableCopy
  byte [] getDecoded (@Nullable final byte [] aEncodedBuffer, @Nonnegative final int nOfs, @Nonnegative final int nLen);

  /**
   * Decode the passed string.
   *
   * @param sEncoded
   *        The string to be decoded. May be <code>null</code>.
   * @param aCharset
   *        The charset to be used. May not be <code>null</code>.
   * @return <code>null</code> if the input string is <code>null</code>.
   * @throws DecodeException
   *         in case something goes wrong
   */
  @Nullable
  @ReturnsMutableCopy
  default byte [] getDecoded (@Nullable final String sEncoded, @Nonnull final Charset aCharset)
  {
    if (sEncoded == null)
      return null;

    final byte [] aEncoded = sEncoded.getBytes (aCharset);
    return getDecoded (aEncoded);
  }
}
