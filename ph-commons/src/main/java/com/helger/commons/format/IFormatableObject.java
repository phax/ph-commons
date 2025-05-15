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
package com.helger.commons.format;

import java.util.function.Function;

import com.helger.annotation.Nullable;

/**
 * Basic interface for special objects having a certain string representation.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        Data type to be formatted
 */
public interface IFormatableObject <DATATYPE>
{
  /**
   * Get the original value.
   *
   * @return The unformatted value. May be <code>null</code>.
   */
  @Nullable
  DATATYPE getValue ();

  /**
   * @return The formatter to be used for formatting this object. Never
   *         <code>null</code>.
   */
  @Nullable
  Function <? super DATATYPE, ? extends String> getFormatter ();

  /**
   * Get the value converted to a string with the specified formatter.
   *
   * @return the string representation of the value. May be <code>null</code>
   *         dependent on the semantics of the formatter.
   */
  @Nullable
  default String getAsString ()
  {
    Function <? super DATATYPE, ? extends String> aFormatter = getFormatter ();
    if (aFormatter == null)
      aFormatter = String::valueOf;
    return aFormatter.apply (getValue ());
  }

}
