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
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a predicate (boolean-valued function) of one argument that is
 * serializable.
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #test(Object)}.
 *
 * @param <T>
 *        the type of the input to the predicate
 * @since 8.6.3
 */
@FunctionalInterface
public interface IPredicate <T> extends Predicate <T>, Serializable
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
  default IPredicate <T> and (@Nullable final Predicate <? super T> other)
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
  default IPredicate <T> or (@Nullable final Predicate <? super T> other)
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
  default IPredicate <T> negate ()
  {
    return x -> !test (x);
  }

  @Nonnull
  static <DATATYPE> IPredicate <DATATYPE> all ()
  {
    return x -> true;
  }

  @Nonnull
  static <DATATYPE> IPredicate <DATATYPE> none ()
  {
    return x -> false;
  }

  @Nonnull
  static <DATATYPE> IPredicate <DATATYPE> notNull ()
  {
    return Objects::nonNull;
  }

  @Nonnull
  static <DATATYPE> IPredicate <DATATYPE> isNull ()
  {
    return Objects::isNull;
  }

  @Nullable
  static <DATATYPE> IPredicate <DATATYPE> and (@Nullable final Predicate <? super DATATYPE> aFirst,
                                               @Nullable final Predicate <? super DATATYPE> aSecond)
  {
    if (aFirst != null)
    {
      if (aSecond != null)
        return x -> aFirst.test (x) && aSecond.test (x);
      return aFirst::test;
    }
    if (aSecond != null)
      return aSecond::test;
    return null;
  }

  @Nullable
  static <DATATYPE> IPredicate <DATATYPE> or (@Nullable final Predicate <? super DATATYPE> aFirst,
                                              @Nullable final Predicate <? super DATATYPE> aSecond)
  {
    if (aFirst != null)
    {
      if (aSecond != null)
        return x -> aFirst.test (x) || aSecond.test (x);
      return aFirst::test;
    }
    if (aSecond != null)
      return aSecond::test;
    return null;
  }

  /**
   * Returns a predicate that tests if two arguments are equal according to
   * {@link Objects#equals(Object, Object)}.
   *
   * @param <T>
   *        the type of arguments to the predicate
   * @param aCmpTo
   *        the object reference with which to compare for equality, which may
   *        be {@code null}
   * @return a predicate that tests if two arguments are equal according to
   *         {@link Objects#equals(Object, Object)}
   */
  @Nonnull
  static <T> IPredicate <T> isEqual (@Nullable final Object aCmpTo)
  {
    return aCmpTo == null ? isNull () : aCmpTo::equals;
  }
}
