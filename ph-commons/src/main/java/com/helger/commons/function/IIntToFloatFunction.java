/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.commons.function;

import java.util.function.Function;

/**
 * Represents a function that accepts an int-valued argument and produces a
 * float-valued result. This is the {@code int}-to-{@code float} primitive
 * specialization for {@link Function}.
 * <p>
 * This is a functional interface whose functional method is
 * {@link #applyAsFloat(int)}.
 *
 * @see Function
 * @since 8.4.1
 */
@FunctionalInterface
public interface IIntToFloatFunction
{

  /**
   * Applies this function to the given argument.
   *
   * @param value
   *        the function argument
   * @return the function result
   */
  float applyAsFloat (int value);
}
