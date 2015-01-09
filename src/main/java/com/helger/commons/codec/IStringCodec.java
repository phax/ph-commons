/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

/**
 * Interface for a single encoder + decoder that is based on a String.
 * 
 * @author Philip Helger
 */
public interface IStringCodec extends ICodec
{
  /**
   * Gets the default charset name used for string decoding and encoding.
   * 
   * @return the default charset. Never <code>null</code>.
   */
  @Nonnull
  Charset getCharset ();

  /**
   * Encode the passed string.
   * 
   * @param sDecoded
   *        The string to be encoded. May be <code>null</code>.
   * @return <code>null</code> if the input string is <code>null</code>.
   * @throws EncoderException
   *         In case something goes wrong
   */
  @Nullable
  String encodeText (@Nullable String sDecoded);

  /**
   * Decode the passed string.
   * 
   * @param sEncoded
   *        The string to be decoded. May be <code>null</code>.
   * @return <code>null</code> if the input string is <code>null</code>.
   * @throws DecoderException
   *         in case something goes wrong
   */
  @Nullable
  String decodeText (@Nullable String sEncoded);
}
