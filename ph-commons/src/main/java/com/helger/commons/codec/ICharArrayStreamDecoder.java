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

import java.io.Writer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.io.stream.NonBlockingStringWriter;

/**
 * Interface for a single decoder of char, based on reader/writer.
 *
 * @author Philip Helger
 * @since 9.3.6
 */
@FunctionalInterface
public interface ICharArrayStreamDecoder extends ICharArrayDecoder
{
  /**
   * Decode a char array.
   *
   * @param aEncodedBuffer
   *        The char array to be decoded. May be <code>null</code>.
   * @param aWriter
   *        The output stream to write to. May not be <code>null</code> and is
   *        NOT closed afterwards!
   * @throws DecodeException
   *         in case something goes wrong
   * @since 9.0.0
   */
  default void decode (@Nullable final char [] aEncodedBuffer, @Nonnull @WillNotClose final Writer aWriter)
  {
    if (aEncodedBuffer == null)
      decode (null, 0, 0, aWriter);
    else
      decode (aEncodedBuffer, 0, aEncodedBuffer.length, aWriter);
  }

  /**
   * Decode (part of) a char array.
   *
   * @param aEncodedBuffer
   *        The char array to be decoded. May be <code>null</code>.
   * @param nOfs
   *        Offset into the char array to start from.
   * @param nLen
   *        Number of chars starting from offset to consider.
   * @param aWriter
   *        The writer to write to. May not be <code>null</code> and is NOT
   *        closed afterwards!
   * @throws DecodeException
   *         in case something goes wrong
   */
  void decode (@Nullable char [] aEncodedBuffer, @Nonnegative int nOfs, @Nonnegative int nLen, @Nonnull @WillNotClose Writer aWriter);

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
  default char [] getDecoded (@Nullable final char [] aEncodedBuffer, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    if (aEncodedBuffer == null)
      return null;

    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter (getMaximumDecodedLength (nLen)))
    {
      decode (aEncodedBuffer, nOfs, nLen, aSW);
      return aSW.getAsCharArray ();
    }
  }

  @Nullable
  default String getDecodedAsString (@Nullable final char [] aEncodedBuffer)
  {
    if (aEncodedBuffer == null)
      return null;

    return getDecodedAsString (aEncodedBuffer, 0, aEncodedBuffer.length);
  }

  @Nullable
  default String getDecodedAsString (@Nullable final char [] aEncodedBuffer, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    if (aEncodedBuffer == null)
      return null;

    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter (getMaximumDecodedLength (nLen)))
    {
      decode (aEncodedBuffer, nOfs, nLen, aSW);
      return aSW.getAsString ();
    }
  }

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
  default String getDecodedAsString (@Nullable final String sEncoded)
  {
    if (sEncoded == null)
      return null;

    final char [] aEncoded = sEncoded.toCharArray ();
    return getDecodedAsString (aEncoded, 0, aEncoded.length);
  }
}
