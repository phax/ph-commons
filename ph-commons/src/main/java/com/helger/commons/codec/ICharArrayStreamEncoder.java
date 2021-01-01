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
 * Interface for a single encoder of chars, based on reader/writer.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface ICharArrayStreamEncoder extends ICharArrayEncoder
{
  @Nonnegative
  @Override
  default int getMaximumEncodedLength (@Nonnegative final int nDecodedLen)
  {
    return nDecodedLen;
  }

  /**
   * Encode a char array to a {@link Writer}.
   *
   * @param aDecodedBuffer
   *        The char array to be encoded. May be <code>null</code>.
   * @param aWriter
   *        The writer to write to. May not be <code>null</code> and is NOT
   *        closed afterwards!
   * @throws EncodeException
   *         In case something goes wrong
   * @since 9.0.0
   */
  default void encode (@Nullable final char [] aDecodedBuffer, @Nonnull @WillNotClose final Writer aWriter)
  {
    if (aDecodedBuffer == null)
      encode (null, 0, 0, aWriter);
    else
      encode (aDecodedBuffer, 0, aDecodedBuffer.length, aWriter);
  }

  /**
   * Encode (part of) a char array to an {@link Writer}.
   *
   * @param aDecodedBuffer
   *        The char array to be encoded. May be <code>null</code>.
   * @param nOfs
   *        Offset into the char array to start from.
   * @param nLen
   *        Number of chars starting from offset to consider.
   * @param aWriter
   *        The writer to write to. May not be <code>null</code> and is NOT
   *        closed afterwards!
   * @throws EncodeException
   *         In case something goes wrong
   */
  void encode (@Nullable char [] aDecodedBuffer, @Nonnegative int nOfs, @Nonnegative int nLen, @Nonnull @WillNotClose Writer aWriter);

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
  @Override
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
  default char [] getEncoded (@Nullable final char [] aDecodedBuffer, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    if (aDecodedBuffer == null)
      return null;

    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter (getMaximumEncodedLength (nLen)))
    {
      encode (aDecodedBuffer, nOfs, nLen, aSW);
      return aSW.getAsCharArray ();
    }
  }

  /**
   * Encode the passed string.
   *
   * @param sDecoded
   *        The string to be encoded. May be <code>null</code>.
   * @return <code>null</code> if the input string is <code>null</code>.
   * @throws EncodeException
   *         In case something goes wrong
   */
  @Override
  @Nullable
  @ReturnsMutableCopy
  default char [] getEncoded (@Nullable final String sDecoded)
  {
    if (sDecoded == null)
      return null;

    final char [] aDecoded = sDecoded.toCharArray ();
    return getEncoded (aDecoded, 0, aDecoded.length);
  }

  @Nullable
  default String getEncodedAsString (@Nullable final char [] aDecodedBuf)
  {
    if (aDecodedBuf == null)
      return null;

    return getEncodedAsString (aDecodedBuf, 0, aDecodedBuf.length);
  }

  @Nullable
  default String getEncodedAsString (@Nullable final char [] aDecodedBuf, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    if (aDecodedBuf == null)
      return null;

    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter (getMaximumEncodedLength (nLen)))
    {
      encode (aDecodedBuf, nOfs, nLen, aSW);
      return aSW.getAsString ();
    }
  }

  /**
   * Encode the passed string and return the result as a String.
   *
   * @param sDecoded
   *        The string to be encoded. May be <code>null</code>.
   * @return <code>null</code> if the input string is <code>null</code>.
   * @throws EncodeException
   *         In case something goes wrong
   */
  @Nullable
  default String getEncodedAsString (@Nullable final String sDecoded)
  {
    if (sDecoded == null)
      return null;

    final char [] aDecoded = sDecoded.toCharArray ();
    return getEncodedAsString (aDecoded, 0, aDecoded.length);
  }
}
