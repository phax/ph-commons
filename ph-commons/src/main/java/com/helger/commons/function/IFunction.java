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

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

import javax.annotation.Nonnull;

/**
 * Represents a serializable function that accepts one argument and produces a
 * result.
 * <p>
 * This is a functional interface whose functional method is
 * {@link #apply(Object)}.
 *
 * @param <T>
 *        the type of the input to the function
 * @param <R>
 *        the type of the result of the function
 * @since 8.6.0
 */
@FunctionalInterface
public interface IFunction <T, R> extends Function <T, R>, Serializable
{
  /**
   * Returns a composed function that first applies the {@code before} function
   * to its input, and then applies this function to the result. If evaluation
   * of either function throws an exception, it is relayed to the caller of the
   * composed function.
   *
   * @param <V>
   *        the type of input to the {@code before} function, and to the
   *        composed function
   * @param before
   *        the function to apply before this function is applied
   * @return a composed function that first applies the {@code before} function
   *         and then applies this function
   * @throws NullPointerException
   *         if before is null
   * @see #andThen(Function)
   */
  @Nonnull
  default <V> IFunction <V, R> compose (@Nonnull final Function <? super V, ? extends T> before)
  {
    Objects.requireNonNull (before);
    return (final V v) -> apply (before.apply (v));
  }

  /**
   * Returns a composed function that first applies this function to its input,
   * and then applies the {@code after} function to the result. If evaluation of
   * either function throws an exception, it is relayed to the caller of the
   * composed function.
   *
   * @param <V>
   *        the type of output of the {@code after} function, and of the
   *        composed function
   * @param after
   *        the function to apply after this function is applied
   * @return a composed function that first applies this function and then
   *         applies the {@code after} function
   * @throws NullPointerException
   *         if after is null
   * @see #compose(Function)
   */
  @Nonnull
  default <V> IFunction <T, V> andThen (@Nonnull final Function <? super R, ? extends V> after)
  {
    Objects.requireNonNull (after);
    return (final T t) -> after.apply (apply (t));
  }
}
