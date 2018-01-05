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

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;

/**
 * Represents a predicate (char-valued function) of one {@code char}-valued
 * argument. This is the {@code char}-consuming primitive type specialization of
 * {@link java.util.function.Predicate}.
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #test(char)}.
 *
 * @see java.util.function.Predicate
 * @since 9.0.0
 */
@FunctionalInterface
public interface ICharPredicate extends Serializable
{
  /**
   * Evaluates this predicate on the given argument.
   *
   * @param cValue
   *        the input argument
   * @return {@code true} if the input argument matches the predicate, otherwise
   *         {@code false}
   */
  boolean test (char cValue);

  /**
   * Returns a predicate that represents the logical negation of this predicate.
   *
   * @return a predicate that represents the logical negation of this predicate
   */
  @Nonnull
  default ICharPredicate negate ()
  {
    return x -> !test (x);
  }

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
   * @param aOther
   *        a predicate that will be logically-ANDed with this predicate
   * @return a composed predicate that represents the short-circuiting logical
   *         AND of this predicate and the {@code other} predicate
   * @throws NullPointerException
   *         if other is null
   */
  @Nonnull
  default ICharPredicate and (@Nonnull final ICharPredicate aOther)
  {
    ValueEnforcer.notNull (aOther, "Other");
    return x -> test (x) && aOther.test (x);
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
   * @param aOther
   *        a predicate that will be logically-ORed with this predicate
   * @return a composed predicate that represents the short-circuiting logical
   *         OR of this predicate and the {@code other} predicate
   * @throws NullPointerException
   *         if other is null
   */
  @Nonnull
  default ICharPredicate or (@Nonnull final ICharPredicate aOther)
  {
    ValueEnforcer.notNull (aOther, "Other");
    return x -> test (x) || aOther.test (x);
  }
}
