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
package com.helger.commons.format;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A simple formatter interface that may be used to format arbitrary objects to
 * a string.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        Source data type
 */
@FunctionalInterface
public interface IFormatter <DATATYPE> extends Serializable, Function <DATATYPE, String>
{
  /**
   * Convert the passed value to a formatted string according to the pattern.
   *
   * @param aValue
   *        The source value to be formatted. May be <code>null</code>.
   * @return The formatted string. Never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the formatter does not understand the object
   */
  @Nonnull
  String apply (@Nullable DATATYPE aValue);

  default IFormatter <DATATYPE> andThen (final IFormatter <? super String> after)
  {
    Objects.requireNonNull (after);
    return (final DATATYPE t) -> after.apply (apply (t));
  }
}
