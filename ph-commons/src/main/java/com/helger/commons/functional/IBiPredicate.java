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
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a predicate (boolean-valued function) of two arguments that is
 * serializable. This is the two-arity specialization of {@link Predicate}.
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #test(Object, Object)}.
 *
 * @param <T>
 *        the type of the first argument to the predicate
 * @param <U>
 *        the type of the second argument the predicate
 * @see IPredicate
 * @since 8.6.3
 */
@FunctionalInterface
public interface IBiPredicate <T, U> extends BiPredicate <T, U>, Serializable
{
  /**
   * Returns a composed predicate that represents a short-circuiting logical AND
   * of this predicate and another. When evaluating the composed predicate, if
   * this predicate is {@code false}, then the {@code other} predicate is not
   * evaluated.
   * <p>
   * Any exceptions thrown during evaluation of either predicate are relayed to
   * the caller; if evaluation of this predicate throws an exception, the
   * {@code other} predicate will not be evaluated.
   *
   * @param other
   *        a predicate that will be logically-ANDed with this predicate. May be
   *        <code>null</code>.
   * @return a composed predicate that represents the short-circuiting logical
   *         AND of this predicate and the {@code other} predicate
   */
  @Override
  @Nonnull
  default IBiPredicate <T, U> and (@Nullable final BiPredicate <? super T, ? super U> other)
  {
    return and (this, other);
  }

  /**
   * Returns a composed predicate that represents a short-circuiting logical OR
   * of this predicate and another. When evaluating the composed predicate, if
   * this predicate is {@code true}, then the {@code other} predicate is not
   * evaluated.
   * <p>
   * Any exceptions thrown during evaluation of either predicate are relayed to
   * the caller; if evaluation of this predicate throws an exception, the
   * {@code other} predicate will not be evaluated.
   *
   * @param other
   *        a predicate that will be logically-ORed with this predicate. May be
   *        <code>null</code>.
   * @return a composed predicate that represents the short-circuiting logical
   *         OR of this predicate and the {@code other} predicate
   */
  @Override
  @Nonnull
  default IBiPredicate <T, U> or (@Nullable final BiPredicate <? super T, ? super U> other)
  {
    return or (this, other);
  }

  /**
   * Returns a predicate that represents the logical negation of this predicate.
   *
   * @return a predicate that represents the logical negation of this predicate
   */
  @Override
  @Nonnull
  default IBiPredicate <T, U> negate ()
  {
    return (x, y) -> !test (x, y);
  }

  @Nullable
  static <T, U> IBiPredicate <T, U> and (@Nullable final BiPredicate <? super T, ? super U> aFirst,
                                         @Nullable final BiPredicate <? super T, ? super U> aSecond)
  {
    if (aFirst != null)
    {
      if (aSecond != null)
        return (x, y) -> aFirst.test (x, y) && aSecond.test (x, y);
      return aFirst::test;
    }
    if (aSecond != null)
      return aSecond::test;
    return null;
  }

  @Nullable
  static <T, U> IBiPredicate <T, U> or (@Nullable final BiPredicate <? super T, ? super U> aFirst,
                                        @Nullable final BiPredicate <? super T, ? super U> aSecond)
  {
    if (aFirst != null)
    {
      if (aSecond != null)
        return (x, y) -> aFirst.test (x, y) || aSecond.test (x, y);
      return aFirst::test;
    }
    if (aSecond != null)
      return aSecond::test;
    return null;
  }
}
