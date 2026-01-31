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
package com.helger.base.codec;

import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.WillNotClose;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;

/**
 * Interface for a single encoder of bytes, based on streams.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IByteArrayStreamEncoder extends IByteArrayEncoder
{
  @Nonnegative
  @Override
  default int getMaximumEncodedLength (@Nonnegative final int nDecodedLen)
  {
    return nDecodedLen;
  }

  /**
   * Encode a byte array to an {@link OutputStream}.
   *
   * @param aDecodedBuffer
   *        The byte array to be encoded. May be <code>null</code>.
   * @param aOS
   *        The output stream to write to. May not be <code>null</code> and is
   *        NOT closed afterwards!
   * @throws EncodeException
   *         In case something goes wrong
   * @since 9.0.0
   */
  default void encode (final byte @Nullable [] aDecodedBuffer, @NonNull @WillNotClose final OutputStream aOS)
  {
    if (aDecodedBuffer == null)
      encode (null, 0, 0, aOS);
    else
      encode (aDecodedBuffer, 0, aDecodedBuffer.length, aOS);
  }

  /**
   * Encode (part of) a byte array to an {@link OutputStream}.
   *
   * @param aDecodedBuffer
   *        The byte array to be encoded. May be <code>null</code>.
   * @param nOfs
   *        Offset into the byte array to start from.
   * @param nLen
   *        Number of bytes starting from offset to consider.
   * @param aOS
   *        The output stream to write to. May not be <code>null</code> and is
   *        NOT closed afterwards!
   * @throws EncodeException
   *         In case something goes wrong
   */
  void encode (byte @Nullable [] aDecodedBuffer, @Nonnegative int nOfs, @Nonnegative int nLen, @NonNull @WillNotClose OutputStream aOS);

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
  @Override
  @ReturnsMutableCopy
  default byte @Nullable [] getEncoded (final byte @Nullable [] aDecodedBuffer)
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
  @ReturnsMutableCopy
  default byte @Nullable [] getEncoded (final byte @Nullable [] aDecodedBuffer, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    if (aDecodedBuffer == null)
      return null;

    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream (getMaximumEncodedLength (nLen)))
    {
      encode (aDecodedBuffer, nOfs, nLen, aBAOS);
      return aBAOS.getBufferOrCopy ();
    }
  }

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
  @Override
  @ReturnsMutableCopy
  default byte @Nullable [] getEncoded (@Nullable final String sDecoded, @NonNull final Charset aCharset)
  {
    if (sDecoded == null)
      return null;

    final byte [] aDecoded = sDecoded.getBytes (aCharset);
    return getEncoded (aDecoded, 0, aDecoded.length);
  }

  @Nullable
  default String getEncodedAsString (final byte @Nullable [] aDecodedBuf, @NonNull final Charset aCharset)
  {
    if (aDecodedBuf == null)
      return null;

    return getEncodedAsString (aDecodedBuf, 0, aDecodedBuf.length, aCharset);
  }

  @Nullable
  default String getEncodedAsString (final byte @Nullable [] aDecodedBuf,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen,
                                     @NonNull final Charset aCharset)
  {
    if (aDecodedBuf == null)
      return null;

    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream (getMaximumEncodedLength (nLen)))
    {
      encode (aDecodedBuf, nOfs, nLen, aBAOS);
      return aBAOS.getAsString (aCharset);
    }
  }

  /**
   * Encode the passed string and return the result as a String.
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
  default String getEncodedAsString (@Nullable final String sDecoded, @NonNull final Charset aCharset)
  {
    if (sDecoded == null)
      return null;

    final byte [] aDecoded = sDecoded.getBytes (aCharset);
    return getEncodedAsString (aDecoded, 0, aDecoded.length, aCharset);
  }
}
