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
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nonnull;

/**
 * Represents a function that accepts two arguments and produces a result. This
 * is the two-arity specialization of {@link Function}.
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #apply(Object, Object)}.
 *
 * @param <T>
 *        the type of the first argument to the function
 * @param <U>
 *        the type of the second argument to the function
 * @param <R>
 *        the type of the result of the function
 * @see IFunction
 * @since 8.6.3
 */
@FunctionalInterface
public interface IBiFunction <T, U, R> extends BiFunction <T, U, R>, Serializable
{
  /**
   * Returns a composed function that first applies this function to its input,
   * and then applies the {@code after} function to the result. If evaluation of
   * either function throws an exception, it is relayed to the caller of the
   * composed function.
   *
   * @param <V>
   *        the type of output of the {@code after} function, and of the
   *        composed function
   * @param aAfter
   *        the function to apply after this function is applied
   * @return a composed function that first applies this function and then
   *         applies the {@code after} function
   * @throws NullPointerException
   *         if after is null
   */
  @Override
  @Nonnull
  default <V> IBiFunction <T, U, V> andThen (@Nonnull final Function <? super R, ? extends V> aAfter)
  {
    Objects.requireNonNull (aAfter);
    return (final T t, final U u) -> aAfter.apply (apply (t, u));
  }
}
