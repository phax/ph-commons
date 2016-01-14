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

import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.base64.Base64;

/**
 * Encoder and decoder for Base64
 *
 * @author Philip Helger
 */
public class Base64Codec implements IByteArrayCodec
{
  public Base64Codec ()
  {}

  @Nullable
  @ReturnsMutableCopy
  public byte [] getEncoded (@Nullable final byte [] aDecodedBuffer)
  {
    return Base64.safeEncodeBytesToBytes (aDecodedBuffer);
  }

  @Nullable
  @ReturnsMutableCopy
  public byte [] getDecoded (@Nullable final byte [] aEncodedBuffer)
  {
    return Base64.safeDecode (aEncodedBuffer);
  }
}
