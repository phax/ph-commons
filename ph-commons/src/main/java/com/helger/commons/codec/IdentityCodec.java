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
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.ReturnsMutableObject;

/**
 * The most simple codec, that does not do anything :)
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The handled data type
 */
@Immutable
public final class IdentityCodec <DATATYPE> implements ICodec <DATATYPE>
{
  public IdentityCodec ()
  {}

  @Nullable
  @ReturnsMutableObject ("design")
  public DATATYPE getDecoded (@Nullable final DATATYPE aInput)
  {
    // Return "as-is"
    return aInput;
  }

  @Nullable
  @ReturnsMutableObject ("design")
  public DATATYPE getEncoded (@Nullable final DATATYPE aInput)
  {
    // Return "as-is"
    return aInput;
  }
}
