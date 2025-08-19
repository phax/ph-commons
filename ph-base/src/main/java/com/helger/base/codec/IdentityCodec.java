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
package com.helger.base.codec;

import jakarta.annotation.Nullable;

/**
 * Special implementation of {@link ICodec} that does nothing. This is a
 * separate class to be able to identify it from non-identity codecs.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        Codec data type
 */
public class IdentityCodec <DATATYPE> implements ICodec <DATATYPE>
{
  @Nullable
  public DATATYPE getEncoded (@Nullable final DATATYPE aInput)
  {
    return aInput;
  }

  @Nullable
  public DATATYPE getDecoded (@Nullable final DATATYPE aInput)
  {
    return aInput;
  }
}
