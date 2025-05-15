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

import java.io.OutputStream;
import java.nio.charset.Charset;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.WillNotClose;
import com.helger.annotation.misc.ReturnsMutableCopy;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;

/**
 * Interface for a single decoder of bytes, based on streams.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IByteArrayStreamDecoder extends IByteArrayDecoder
{
  /**
   * Decode a byte array.
   *
   * @param aEncodedBuffer
   *        The byte array to be decoded. May be <code>null</code>.
   * @param aOS
   *        The output stream to write to. May not be <code>null</code> and is
   *        NOT closed afterwards!
   * @throws DecodeException
   *         in case something goes wrong
   * @since 9.0.0
   */
  default void decode (@Nullable final byte [] aEncodedBuffer, @Nonnull @WillNotClose final OutputStream aOS)
  {
    if (aEncodedBuffer == null)
      decode (null, 0, 0, aOS);
    else
      decode (aEncodedBuffer, 0, aEncodedBuffer.length, aOS);
  }

  /**
   * Decode (part of) a byte array.
   *
   * @param aEncodedBuffer
   *        The byte array to be decoded. May be <code>null</code>.
   * @param nOfs
   *        Offset into the byte array to start from.
   * @param nLen
   *        Number of bytes starting from offset to consider.
   * @param aOS
   *        The output stream to write to. May not be <code>null</code> and is
   *        NOT closed afterwards!
   * @throws DecodeException
   *         in case something goes wrong
   */
  void decode (@Nullable byte [] aEncodedBuffer, @Nonnegative int nOfs, @Nonnegative int nLen, @Nonnull @WillNotClose OutputStream aOS);

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
  default byte [] getDecoded (@Nullable final byte [] aEncodedBuffer, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    if (aEncodedBuffer == null)
      return null;

    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream (getMaximumDecodedLength (nLen)))
    {
      decode (aEncodedBuffer, nOfs, nLen, aBAOS);
      return aBAOS.getBufferOrCopy ();
    }
  }

  @Nullable
  default String getDecodedAsString (@Nullable final byte [] aEncodedBuffer, @Nonnull final Charset aCharset)
  {
    if (aEncodedBuffer == null)
      return null;

    return getDecodedAsString (aEncodedBuffer, 0, aEncodedBuffer.length, aCharset);
  }

  @Nullable
  default String getDecodedAsString (@Nullable final byte [] aEncodedBuffer,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen,
                                     @Nonnull final Charset aCharset)
  {
    if (aEncodedBuffer == null)
      return null;

    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream (getMaximumDecodedLength (nLen)))
    {
      decode (aEncodedBuffer, nOfs, nLen, aBAOS);
      return aBAOS.getAsString (aCharset);
    }
  }

  /**
   * Decode the passed string.
   *
   * @param sEncoded
   *        The string to be decoded. May be <code>null</code>.
   * @param aCharset
   *        The charset to be used for encoding AND decoding. May not be
   *        <code>null</code>.
   * @return <code>null</code> if the input string is <code>null</code>.
   * @throws DecodeException
   *         in case something goes wrong
   */
  @Nullable
  default String getDecodedAsString (@Nullable final String sEncoded, @Nonnull final Charset aCharset)
  {
    return getDecodedAsString (sEncoded, aCharset, aCharset);
  }

  /**
   * Decode the passed string.
   *
   * @param sEncoded
   *        The string to be decoded. May be <code>null</code>.
   * @param aEncodedCharset
   *        The charset to be used for the encoded string. May not be
   *        <code>null</code>.
   * @param aDecodedCharset
   *        The charset to be used for the decoded result string. May not be
   *        <code>null</code>.
   * @return <code>null</code> if the input string is <code>null</code>.
   * @throws DecodeException
   *         in case something goes wrong
   * @since 9.3.6
   */
  @Nullable
  default String getDecodedAsString (@Nullable final String sEncoded,
                                     @Nonnull final Charset aEncodedCharset,
                                     @Nonnull final Charset aDecodedCharset)
  {
    if (sEncoded == null)
      return null;

    final byte [] aEncoded = sEncoded.getBytes (aEncodedCharset);
    return getDecodedAsString (aEncoded, 0, aEncoded.length, aDecodedCharset);
  }
}
