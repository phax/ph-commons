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
package com.helger.base.string;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.equals.ValueEnforcer;

import jakarta.annotation.Nonnull;

public final class StringEncoding
{
  private StringEncoding ()
  {}

  /**
   * Encode a char array to a byte array using the provided charset. This does the same as
   * <code>new String (aCharArray).getBytes (aCharset)</code> just without the intermediate objects.
   *
   * @param aCharset
   *        Charset to be used. May not be <code>null</code>.
   * @param aCharArray
   *        The char array to be encoded. May not be <code>null</code>.
   * @return The created byte array. Never <code>null</code>.
   * @since 8.6.4
   */
  @Nonnull
  @ReturnsMutableCopy
  public static byte [] encodeCharToBytes (@Nonnull final char [] aCharArray, @Nonnull final Charset aCharset)
  {
    return encodeCharToBytes (aCharArray, 0, aCharArray.length, aCharset);
  }

  /**
   * Encode a char array to a byte array using the provided charset. This does the same as
   * <code>new String (aCharArray).getBytes (aCharset)</code> just without the intermediate objects.
   *
   * @param aCharset
   *        Charset to be used. May not be <code>null</code>.
   * @param aCharArray
   *        The char array to be encoded. May not be <code>null</code>.
   * @param nOfs
   *        Offset into char array. Must be &ge; 0.
   * @param nLen
   *        Chars to encode. Must be &ge; 0.
   * @return The created byte array. Never <code>null</code>.
   * @since 8.6.4
   */
  @Nonnull
  @ReturnsMutableCopy
  public static byte [] encodeCharToBytes (@Nonnull final char [] aCharArray,
                                           @Nonnegative final int nOfs,
                                           @Nonnegative final int nLen,
                                           @Nonnull final Charset aCharset)
  {
    ValueEnforcer.isArrayOfsLen (aCharArray, nOfs, nLen);

    final CharsetEncoder aEncoder = aCharset.newEncoder ();
    // We need to perform double, not float, arithmetic; otherwise
    // we lose low order bits when nLen is larger than 2^24.
    final int nEncodedLen = (int) (nLen * (double) aEncoder.maxBytesPerChar ());
    final byte [] aByteArray = new byte [nEncodedLen];
    if (nLen == 0)
      return aByteArray;
    aEncoder.onMalformedInput (CodingErrorAction.REPLACE).onUnmappableCharacter (CodingErrorAction.REPLACE).reset ();

    final CharBuffer aSrcBuf = CharBuffer.wrap (aCharArray, nOfs, nLen);
    final ByteBuffer aDstBuf = ByteBuffer.wrap (aByteArray);
    try
    {
      CoderResult aRes = aEncoder.encode (aSrcBuf, aDstBuf, true);
      if (!aRes.isUnderflow ())
        aRes.throwException ();
      aRes = aEncoder.flush (aDstBuf);
      if (!aRes.isUnderflow ())
        aRes.throwException ();
    }
    catch (final CharacterCodingException x)
    {
      throw new IllegalStateException (x);
    }

    final int nDstLen = aDstBuf.position ();
    if (nDstLen == aByteArray.length)
      return aByteArray;
    return Arrays.copyOf (aByteArray, nDstLen);
  }

  /**
   * Decode a byte array to a char array using the provided charset. This does the same as
   * <code>new String (aByteArray, aCharset)</code> just without the intermediate objects.
   *
   * @param aByteArray
   *        The byte array to be decoded. May not be <code>null</code>.
   * @param aCharset
   *        Charset to be used. May not be <code>null</code>.
   * @return The created char array. Never <code>null</code>.
   * @since 8.6.4
   */
  @Nonnull
  public static char [] decodeBytesToChars (@Nonnull final byte [] aByteArray, @Nonnull final Charset aCharset)
  {
    return decodeBytesToChars (aByteArray, 0, aByteArray.length, aCharset);
  }

  /**
   * Decode a byte array to a char array using the provided charset. This does the same as
   * <code>new String (aByteArray, aCharset)</code> just without the intermediate objects.
   *
   * @param aByteArray
   *        The byte array to be decoded. May not be <code>null</code>.
   * @param nOfs
   *        Offset into byte array. Must be &ge; 0.
   * @param nLen
   *        Bytes to encode. Must be &ge; 0.
   * @param aCharset
   *        Charset to be used. May not be <code>null</code>.
   * @return The created char array. Never <code>null</code>.
   * @since 8.6.4
   */
  @Nonnull
  public static char [] decodeBytesToChars (@Nonnull final byte [] aByteArray,
                                            @Nonnegative final int nOfs,
                                            @Nonnegative final int nLen,
                                            @Nonnull final Charset aCharset)
  {
    final CharsetDecoder aDecoder = aCharset.newDecoder ();
    final int nDecodedLen = (int) (nLen * (double) aDecoder.maxCharsPerByte ());
    final char [] aCharArray = new char [nDecodedLen];
    if (nLen == 0)
      return aCharArray;
    aDecoder.onMalformedInput (CodingErrorAction.REPLACE).onUnmappableCharacter (CodingErrorAction.REPLACE).reset ();

    final ByteBuffer aSrcBuf = ByteBuffer.wrap (aByteArray, nOfs, nLen);
    final CharBuffer aDstBuf = CharBuffer.wrap (aCharArray);
    try
    {
      CoderResult aRes = aDecoder.decode (aSrcBuf, aDstBuf, true);
      if (!aRes.isUnderflow ())
        aRes.throwException ();
      aRes = aDecoder.flush (aDstBuf);
      if (!aRes.isUnderflow ())
        aRes.throwException ();
    }
    catch (final CharacterCodingException x)
    {
      // Substitution is always enabled,
      // so this shouldn't happen
      throw new IllegalStateException (x);
    }

    final int nDstLen = aDstBuf.position ();
    if (nDstLen == aCharArray.length)
      return aCharArray;
    return Arrays.copyOf (aCharArray, nDstLen);
  }
}
