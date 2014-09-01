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

import com.helger.commons.base64.Base64;
import com.helger.commons.base64.Base64Helper;

/**
 * Encoder and decoder for Base64
 *
 * @author Philip Helger
 */
public class Base64Codec extends AbstractCodec
{
  public Base64Codec ()
  {}

  @Nullable
  public byte [] encode (@Nullable final byte [] aDecodedBuffer)
  {
    return aDecodedBuffer == null ? null : Base64.encodeBytesToBytes (aDecodedBuffer);
  }

  @Nullable
  public byte [] decode (@Nullable final byte [] aEncodedBuffer)
  {
    return aEncodedBuffer == null ? null : Base64Helper.safeDecode (aEncodedBuffer);
  }
}
