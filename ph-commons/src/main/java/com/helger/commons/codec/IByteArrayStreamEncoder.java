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

import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;

/**
 * Interface for a single encoder
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IByteArrayStreamEncoder extends IByteArrayEncoder
{
  @Nonnegative
  @Override
  default int getEncodedLength (@Nonnegative final int nDecodedLen)
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
  default void encode (@Nullable final byte [] aDecodedBuffer, @Nonnull @WillNotClose final OutputStream aOS)
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
  void encode (@Nullable byte [] aDecodedBuffer,
               @Nonnegative int nOfs,
               @Nonnegative int nLen,
               @Nonnull @WillNotClose OutputStream aOS);

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
  default byte [] getEncoded (@Nullable final byte [] aDecodedBuffer,
                              @Nonnegative final int nOfs,
                              @Nonnegative final int nLen)
  {
    if (aDecodedBuffer == null)
      return null;

    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream (getEncodedLength (nLen)))
    {
      encode (aDecodedBuffer, nOfs, nLen, aBAOS);
      return aBAOS.toByteArray ();
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
  @Nullable
  @ReturnsMutableCopy
  default byte [] getEncoded (@Nullable final String sDecoded, @Nonnull final Charset aCharset)
  {
    if (sDecoded == null)
      return null;

    final byte [] aDecoded = sDecoded.getBytes (aCharset);
    return getEncoded (aDecoded);
  }

  @Nullable
  default String getEncodedAsString (@Nullable final byte [] aDecodedBuf, @Nonnull final Charset aCharset)
  {
    if (aDecodedBuf == null)
      return null;

    return getEncodedAsString (aDecodedBuf, 0, aDecodedBuf.length, aCharset);
  }

  @Nullable
  default String getEncodedAsString (@Nullable final byte [] aDecodedBuf,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen,
                                     @Nonnull final Charset aCharset)
  {
    if (aDecodedBuf == null)
      return null;

    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream (getEncodedLength (nLen)))
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
  default String getEncodedAsString (@Nullable final String sDecoded, @Nonnull final Charset aCharset)
  {
    if (sDecoded == null)
      return null;

    final byte [] aDecoded = sDecoded.getBytes (aCharset);
    return getEncodedAsString (aDecoded, 0, aDecoded.length, aCharset);
  }
}
