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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents an operation that accepts two input arguments and returns no
 * result and is serializable. This is the two-arity specialization of
 * {@link Consumer}. Unlike most other functional interfaces, {@code BiConsumer}
 * is expected to operate via side-effects.
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #accept(Object, Object)}.
 *
 * @param <T>
 *        the type of the first argument to the operation
 * @param <U>
 *        the type of the second argument to the operation
 * @see IConsumer
 * @since 8.6.3
 */
@FunctionalInterface
public interface IBiConsumer <T, U> extends BiConsumer <T, U>, Serializable
{
  /**
   * Returns a composed {@code BiConsumer} that performs, in sequence, this
   * operation followed by the {@code after} operation. If performing either
   * operation throws an exception, it is relayed to the caller of the composed
   * operation. If performing this operation throws an exception, the
   * {@code after} operation will not be performed.
   *
   * @param after
   *        the operation to perform after this operation. May be
   *        <code>null</code>.
   * @return a composed {@code BiConsumer} that performs in sequence this
   *         operation followed by the {@code after} operation
   */
  @Override
  @Nonnull
  default IBiConsumer <T, U> andThen (@Nullable final BiConsumer <? super T, ? super U> after)
  {
    return and (this, after);
  }

  @Nullable
  static <T, U> IBiConsumer <T, U> and (@Nullable final BiConsumer <? super T, ? super U> aFirst,
                                        @Nullable final BiConsumer <? super T, ? super U> aSecond)
  {
    if (aFirst != null)
    {
      if (aSecond != null)
        return (x, y) -> {
          aFirst.accept (x, y);
          aSecond.accept (x, y);
        };
      return aFirst::accept;
    }
    if (aSecond != null)
      return aSecond::accept;
    return null;
  }
}
