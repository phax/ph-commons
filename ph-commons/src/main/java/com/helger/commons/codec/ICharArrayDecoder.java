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

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;

/**
 * Interface for a single decoder for chars.
 *
 * @author Philip Helger
 * @since 9.3.6
 */
@FunctionalInterface
public interface ICharArrayDecoder extends IDecoder <char [], char []>
{
  /**
   * Get the maximum decoded length based on the provided encoded length. This
   * is purely for performance reasons.
   *
   * @param nEncodedLen
   *        The encoded length. Always &ge; 0.
   * @return The maximum decoded length. Always &ge; 0.
   */
  @Nonnegative
  default int getMaximumDecodedLength (@Nonnegative final int nEncodedLen)
  {
    return nEncodedLen;
  }

  /**
   * Decode a char array.
   *
   * @param aEncodedBuffer
   *        The char array to be decoded. May be <code>null</code>.
   * @return The decoded char array or <code>null</code> if the parameter was
   *         <code>null</code>.
   * @throws DecodeException
   *         in case something goes wrong
   */
  @Nullable
  @ReturnsMutableCopy
  default char [] getDecoded (@Nullable final char [] aEncodedBuffer)
  {
    if (aEncodedBuffer == null)
      return null;
    return getDecoded (aEncodedBuffer, 0, aEncodedBuffer.length);
  }

  /**
   * Decode a char array.
   *
   * @param aEncodedBuffer
   *        The char array to be decoded. May be <code>null</code>.
   * @param nOfs
   *        Offset into the char array to start from.
   * @param nLen
   *        Number of chars starting from offset to consider.
   * @return The decoded char array or <code>null</code> if the parameter was
   *         <code>null</code>.
   * @throws DecodeException
   *         in case something goes wrong
   */
  @Nullable
  @ReturnsMutableCopy
  char [] getDecoded (@Nullable final char [] aEncodedBuffer, @Nonnegative final int nOfs, @Nonnegative final int nLen);

  /**
   * Decode the passed string.
   *
   * @param sEncoded
   *        The string to be decoded. May be <code>null</code>.
   * @return <code>null</code> if the input string is <code>null</code>.
   * @throws DecodeException
   *         in case something goes wrong
   */
  @Nullable
  @ReturnsMutableCopy
  default char [] getDecoded (@Nullable final String sEncoded)
  {
    if (sEncoded == null)
      return null;

    return getDecoded (sEncoded.toCharArray ());
  }
}
