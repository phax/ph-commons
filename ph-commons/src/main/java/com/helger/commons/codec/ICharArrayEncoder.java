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
 * Interface for a single encoder of chars.
 *
 * @author Philip Helger
 * @since 9.3.6
 */
@FunctionalInterface
public interface ICharArrayEncoder extends IEncoder <char [], char []>
{
  /**
   * Get the maximum encoded length based on the provided decoded length. This
   * is purely for performance reasons. The name of the method would be better
   * called "getMaximumEncodedLength".
   *
   * @param nDecodedLen
   *        The decoded length. Always &ge; 0.
   * @return The maximum encoded length. Always &ge; 0.
   */
  @Nonnegative
  default int getMaximumEncodedLength (@Nonnegative final int nDecodedLen)
  {
    return nDecodedLen;
  }

  /**
   * Encode a char array.
   *
   * @param aDecodedBuffer
   *        The char array to be encoded. May be <code>null</code>.
   * @return The encoded char array or <code>null</code> if the parameter was
   *         <code>null</code>.
   * @throws EncodeException
   *         In case something goes wrong
   */
  @Nullable
  @ReturnsMutableCopy
  default char [] getEncoded (@Nullable final char [] aDecodedBuffer)
  {
    if (aDecodedBuffer == null)
      return null;
    return getEncoded (aDecodedBuffer, 0, aDecodedBuffer.length);
  }

  /**
   * Encode a char array.
   *
   * @param aDecodedBuffer
   *        The char array to be encoded. May be <code>null</code>.
   * @param nOfs
   *        Offset into the char array to start from.
   * @param nLen
   *        Number of chars starting from offset to consider.
   * @return The encoded char array or <code>null</code> if the parameter was
   *         <code>null</code>.
   * @throws EncodeException
   *         In case something goes wrong
   */
  @Nullable
  @ReturnsMutableCopy
  char [] getEncoded (@Nullable char [] aDecodedBuffer, @Nonnegative int nOfs, @Nonnegative int nLen);

  /**
   * Encode the passed string.
   *
   * @param sDecoded
   *        The string to be encoded. May be <code>null</code>.
   * @return <code>null</code> if the input string is <code>null</code>.
   * @throws EncodeException
   *         In case something goes wrong
   */
  @Nullable
  @ReturnsMutableCopy
  default char [] getEncoded (@Nullable final String sDecoded)
  {
    if (sDecoded == null)
      return null;

    return getEncoded (sDecoded.toCharArray ());
  }
}
