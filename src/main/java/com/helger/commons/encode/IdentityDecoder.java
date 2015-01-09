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
package com.helger.commons.encode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.lang.GenericReflection;

/**
 * The most simple decoder, that does not do anything
 * 
 * @author Philip Helger
 * @param <DATATYPE>
 *        The handled data type
 */
public final class IdentityDecoder <DATATYPE> implements IDecoder <DATATYPE>
{
  private static final IdentityDecoder <Object> s_aInstance = new IdentityDecoder <Object> ();

  private IdentityDecoder ()
  {}

  @Nullable
  public DATATYPE decode (@Nullable final DATATYPE aInput)
  {
    return aInput;
  }

  /**
   * Factory method for this class
   * 
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static <DATATYPE> IdentityDecoder <DATATYPE> create ()
  {
    return GenericReflection.<IdentityDecoder <Object>, IdentityDecoder <DATATYPE>> uncheckedCast (s_aInstance);
  }
}
