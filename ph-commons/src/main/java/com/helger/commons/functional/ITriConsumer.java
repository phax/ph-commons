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
package com.helger.commons.functional;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents an operation that accepts three input arguments and returns no
 * result. This is the three-arity specialization of {@link Consumer}. Unlike
 * most other functional interfaces, {@code ITriConsumer} is expected to operate
 * via side-effects.
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #accept(Object, Object,Object)}.
 *
 * @param <T>
 *        the type of the first argument to the operation
 * @param <U>
 *        the type of the second argument to the operation
 * @param <V>
 *        the type of the third argument to the operation
 * @since 8.0.0
 */
@FunctionalInterface
public interface ITriConsumer <T, U, V>
{
  /**
   * Performs this operation on the given arguments.
   *
   * @param t
   *        the first input argument
   * @param u
   *        the second input argument
   * @param v
   *        the third input argument
   */
  void accept (T t, U u, V v);

  /**
   * Returns a composed {@code ITriConsumer} that performs, in sequence, this
   * operation followed by the {@code after} operation. If performing either
   * operation throws an exception, it is relayed to the caller of the composed
   * operation. If performing this operation throws an exception, the
   * {@code after} operation will not be performed.
   *
   * @param after
   *        the operation to perform after this operation. May be
   *        <code>null</code>.
   * @return a composed {@code ITriConsumer} that performs in sequence this
   *         operation followed by the {@code after} operation
   */
  @Nonnull
  default ITriConsumer <T, U, V> andThen (@Nullable final ITriConsumer <? super T, ? super U, ? super V> after)
  {
    if (after == null)
      return this;
    return (t, u, v) -> {
      accept (t, u, v);
      after.accept (t, u, v);
    };
  }
}
