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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for a single encoder + decoder.
 *
 * @param <DATATYPE>
 *        data type
 * @author Philip Helger
 */
public interface ICodec <DATATYPE> extends IEncoder <DATATYPE>, IDecoder <DATATYPE>
{
  @Nonnull
  static <T> ICodec <T> identity ()
  {
    return new ICodec <T> ()
    {
      @Nullable
      public T getEncoded (@Nullable final T aInput)
      {
        return aInput;
      }

      @Nullable
      public T getDecoded (@Nullable final T aInput)
      {
        return aInput;
      }
    };
  }
}
