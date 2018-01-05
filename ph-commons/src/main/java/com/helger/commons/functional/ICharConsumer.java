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
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents an operation that accepts a single {@code char}-valued argument
 * and returns no result. This is the primitive type specialization of
 * {@link Consumer} for {@code char}. Unlike most other functional interfaces,
 * {@code CharConsumer} is expected to operate via side-effects.
 * <p>
 * This is a functional interface whose functional method is
 * {@link #accept(char)}.
 *
 * @see Consumer
 * @since 8.0.0
 */
@FunctionalInterface
public interface ICharConsumer extends Serializable
{
  /**
   * Performs this operation on the given argument.
   *
   * @param value
   *        the input argument
   */
  void accept (char value);

  /**
   * Returns a composed {@code CharConsumer} that performs, in sequence, this
   * operation followed by the {@code after} operation. If performing either
   * operation throws an exception, it is relayed to the caller of the composed
   * operation. If performing this operation throws an exception, the
   * {@code after} operation will not be performed.
   *
   * @param after
   *        the operation to perform after this operation
   * @return a composed {@code CharConsumer} that performs in sequence this
   *         operation followed by the {@code after} operation
   * @throws NullPointerException
   *         if {@code after} is null
   */
  @Nonnull
  default ICharConsumer andThen (@Nullable final ICharConsumer after)
  {
    return and (this, after);
  }

  @Nullable
  static ICharConsumer and (@Nullable final ICharConsumer aFirst, @Nullable final ICharConsumer aSecond)
  {
    if (aFirst != null)
    {
      if (aSecond != null)
        return x -> {
          aFirst.accept (x);
          aSecond.accept (x);
        };
      return aFirst;
    }
    return aSecond;
  }
}
