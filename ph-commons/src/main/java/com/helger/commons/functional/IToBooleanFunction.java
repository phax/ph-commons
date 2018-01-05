/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.functional;

import java.io.Serializable;

/**
 * Represents a function that produces a boolean-valued result. This is the
 * {@code boolean}-producing primitive specialization for
 * {@link java.util.function.Function}.
 * <p>
 * This is a functional interface whose functional method is
 * {@link #applyAsBoolean(Object)}.
 *
 * @param <T>
 *        the type of the input to the function
 * @see java.util.function.Function
 * @since 8.0.0
 */
@FunctionalInterface
public interface IToBooleanFunction <T> extends Serializable
{
  /**
   * Applies this function to the given argument.
   *
   * @param value
   *        the function argument
   * @return the function result
   */
  boolean applyAsBoolean (T value);
}
