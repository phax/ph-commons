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
 * Represents an operation that accepts a single input argument and returns no
 * result but may throw an Exception. Unlike most other functional interfaces,
 * {@code IConsumer} is expected to operate via side-effects. This is a
 * serializable extension to the {@link Consumer} interface.
 * <p>
 * This is a functional interface whose functional method is
 * {@link #accept(Object)}.
 *
 * @param <T>
 *        the type of the input to the operation
 * @since 8.6.0
 */
@FunctionalInterface
public interface IConsumer <T> extends Consumer <T>, Serializable
{
  /**
   * Returns a composed {@code Consumer} that performs, in sequence, this
   * operation followed by the {@code after} operation. If performing either
   * operation throws an exception, it is relayed to the caller of the composed
   * operation. If performing this operation throws an exception, the
   * {@code after} operation will not be performed.
   *
   * @param after
   *        the operation to perform after this operation. May be
   *        <code>null</code>.
   * @return a composed {@code Consumer} that performs in sequence this
   *         operation followed by the {@code after} operation
   */
  @Override
  @Nonnull
  default IConsumer <T> andThen (@Nonnull final Consumer <? super T> after)
  {
    return and (this, after);
  }

  @Nullable
  static <DATATYPE> IConsumer <DATATYPE> and (@Nullable final Consumer <? super DATATYPE> aFirst,
                                              @Nullable final Consumer <? super DATATYPE> aSecond)
  {
    if (aFirst != null)
    {
      if (aSecond != null)
        return x -> {
          aFirst.accept (x);
          aSecond.accept (x);
        };
      return aFirst::accept;
    }
    if (aSecond != null)
      return aSecond::accept;
    return null;
  }
}
