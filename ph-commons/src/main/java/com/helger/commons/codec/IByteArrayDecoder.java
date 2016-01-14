/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.charset.CharsetManager;

/**
 * Interface for a single decoder.
 *
 * @author Philip Helger
 */
public interface IByteArrayDecoder extends IDecoder <byte []>
{
  /**
   * Decode a byte array.
   *
   * @param aEncodedBuffer
   *        The byte array to be decoded. May not be <code>null</code>.
   * @return The decoded byte array or <code>null</code> if the parameter was
   *         <code>null</code>.
   * @throws DecodeException
   *         in case something goes wrong
   */
  @Nullable
  @ReturnsMutableCopy
  byte [] getDecoded (@Nullable byte [] aEncodedBuffer);

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
  default public byte [] getDecoded (@Nullable final String sEncoded, @Nonnull final Charset aCharset)
  {
    if (sEncoded == null)
      return null;

    final byte [] aEncoded = CharsetManager.getAsBytes (sEncoded, aCharset);
    return getDecoded (aEncoded);
  }
}
