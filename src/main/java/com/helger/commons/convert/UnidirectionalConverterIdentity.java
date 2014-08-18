/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
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
package com.helger.commons.convert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A dummy implementation of {@link IUnidirectionalConverterSameType} that uses
 * the input object as the output object.
 * 
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to be converted
 */
public final class UnidirectionalConverterIdentity <DATATYPE> implements IUnidirectionalConverterSameType <DATATYPE>
{
  @Nullable
  public DATATYPE convert (@Nullable final DATATYPE aInput)
  {
    return aInput;
  }

  /**
   * Get a converter instance that does not conversion at all. Just to fulfil
   * API compatibility.
   * 
   * @param <DATATYPE>
   *        The type of input and output object
   * @return A non-<code>null</code> converter object.
   */
  @Nonnull
  public static <DATATYPE> UnidirectionalConverterIdentity <DATATYPE> create ()
  {
    return new UnidirectionalConverterIdentity <DATATYPE> ();
  }
}
