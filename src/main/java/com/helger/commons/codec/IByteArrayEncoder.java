/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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

import javax.annotation.Nullable;

import com.helger.commons.encode.IEncoder;

/**
 * Interface for a single encoder
 * 
 * @author Philip Helger
 */
public interface IByteArrayEncoder extends IEncoder <byte []>
{
  /**
   * Encode a byte array.
   * 
   * @param aDecodedBuffer
   *        The byte array to be encoded. May be <code>null</code>.
   * @return The encoded byte array or <code>null</code> if the parameter was
   *         <code>null</code>.
   * @throws EncoderException
   *         In case something goes wrong
   */
  @Nullable
  byte [] encode (@Nullable byte [] aDecodedBuffer);
}
