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
package com.helger.base.codec;

import java.io.Writer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.WillNotClose;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.io.nonblocking.NonBlockingStringWriter;

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
  default void decode (final char @Nullable [] aEncodedBuffer, @NonNull @WillNotClose final Writer aWriter)
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
  void decode (char @Nullable [] aEncodedBuffer, @Nonnegative int nOfs, @Nonnegative int nLen, @NonNull @WillNotClose Writer aWriter);

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
  @ReturnsMutableCopy
  default char @Nullable [] getDecoded (final char @Nullable [] aEncodedBuffer, @Nonnegative final int nOfs, @Nonnegative final int nLen)
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
  default String getDecodedAsString (final char @Nullable [] aEncodedBuffer)
  {
    if (aEncodedBuffer == null)
      return null;

    return getDecodedAsString (aEncodedBuffer, 0, aEncodedBuffer.length);
  }

  @Nullable
  default String getDecodedAsString (final char @Nullable [] aEncodedBuffer, @Nonnegative final int nOfs, @Nonnegative final int nLen)
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
