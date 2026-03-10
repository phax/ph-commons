/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.base.functional;

import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;

/**
 * Some globally usable predicates.
 *
 * @author Philip Helger
 * @since 9.1.3
 */
@Immutable
public final class Predicates
{
  private Predicates ()
  {}

  /**
   * @param <DATATYPE>
   *        The data type to be checked.
   * @return A predicate that always returns <code>true</code>. Never <code>null</code>.
   */
  @NonNull
  public static <DATATYPE> Predicate <DATATYPE> all ()
  {
    return x -> true;
  }

  /**
   * @param <DATATYPE>
   *        The data type to be checked.
   * @return A predicate that always returns <code>false</code>. Never <code>null</code>.
   */
  @NonNull
  public static <DATATYPE> Predicate <DATATYPE> none ()
  {
    return x -> false;
  }

  /**
   * @param <DATATYPE>
   *        The data type to be checked.
   * @return A predicate that returns <code>true</code> if the value is not <code>null</code>.
   *         Never <code>null</code>.
   */
  @NonNull
  public static <DATATYPE> Predicate <DATATYPE> notNull ()
  {
    return Objects::nonNull;
  }

  /**
   * @param <DATATYPE>
   *        The data type to be checked.
   * @return A predicate that returns <code>true</code> if the value is <code>null</code>. Never
   *         <code>null</code>.
   */
  @NonNull
  public static <DATATYPE> Predicate <DATATYPE> isNull ()
  {
    return Objects::isNull;
  }

  /**
   * @return A char predicate that checks for equality with 0. Never <code>null</code>.
   */
  @NonNull
  public static ICharPredicate charIsEQ0 ()
  {
    return x -> x == 0;
  }

  /**
   * @return A char predicate that checks for inequality with 0. Never <code>null</code>.
   */
  @NonNull
  public static ICharPredicate charIsNE0 ()
  {
    return x -> x != 0;
  }

  /**
   * @return A char predicate that checks for values &gt; 0. Never <code>null</code>.
   */
  @NonNull
  public static ICharPredicate charIsGT0 ()
  {
    return x -> x > 0;
  }

  /**
   * @return A double predicate that checks for values &lt; 0. Never <code>null</code>.
   */
  @NonNull
  public static DoublePredicate doubleIsLT0 ()
  {
    return x -> x < 0;
  }

  /**
   * @return A double predicate that checks for values &le; 0. Never <code>null</code>.
   */
  @NonNull
  public static DoublePredicate doubleIsLE0 ()
  {
    return x -> x <= 0;
  }

  /**
   * @return A double predicate that checks for equality with 0. Never <code>null</code>.
   */
  @NonNull
  public static DoublePredicate doubleIsEQ0 ()
  {
    return x -> x == 0;
  }

  /**
   * @return A double predicate that checks for inequality with 0. Never <code>null</code>.
   */
  @NonNull
  public static DoublePredicate doubleIsNE0 ()
  {
    return x -> x != 0;
  }

  /**
   * @return A double predicate that checks for values &ge; 0. Never <code>null</code>.
   */
  @NonNull
  public static DoublePredicate doubleIsGE0 ()
  {
    return x -> x >= 0;
  }

  /**
   * @return A double predicate that checks for values &gt; 0. Never <code>null</code>.
   */
  @NonNull
  public static DoublePredicate doubleIsGT0 ()
  {
    return x -> x > 0;
  }

  /**
   * @return An int predicate that checks for values &lt; 0. Never <code>null</code>.
   */
  @NonNull
  public static IntPredicate intIsLT0 ()
  {
    return x -> x < 0;
  }

  /**
   * @return An int predicate that checks for values &le; 0. Never <code>null</code>.
   */
  @NonNull
  public static IntPredicate intIsLE0 ()
  {
    return x -> x <= 0;
  }

  /**
   * @return An int predicate that checks for equality with 0. Never <code>null</code>.
   */
  @NonNull
  public static IntPredicate intIsEQ0 ()
  {
    return x -> x == 0;
  }

  /**
   * @return An int predicate that checks for inequality with 0. Never <code>null</code>.
   */
  @NonNull
  public static IntPredicate intIsNE0 ()
  {
    return x -> x != 0;
  }

  /**
   * @return An int predicate that checks for values &ge; 0. Never <code>null</code>.
   */
  @NonNull
  public static IntPredicate intIsGE0 ()
  {
    return x -> x >= 0;
  }

  /**
   * @return An int predicate that checks for values &gt; 0. Never <code>null</code>.
   */
  @NonNull
  public static IntPredicate intIsGT0 ()
  {
    return x -> x > 0;
  }

  /**
   * @return A long predicate that checks for values &lt; 0. Never <code>null</code>.
   */
  @NonNull
  public static LongPredicate longIsLT0 ()
  {
    return x -> x < 0;
  }

  /**
   * @return A long predicate that checks for values &le; 0. Never <code>null</code>.
   */
  @NonNull
  public static LongPredicate longIsLE0 ()
  {
    return x -> x <= 0;
  }

  /**
   * @return A long predicate that checks for equality with 0. Never <code>null</code>.
   */
  @NonNull
  public static LongPredicate longIsEQ0 ()
  {
    return x -> x == 0;
  }

  /**
   * @return A long predicate that checks for inequality with 0. Never <code>null</code>.
   */
  @NonNull
  public static LongPredicate longIsNE0 ()
  {
    return x -> x != 0;
  }

  /**
   * @return A long predicate that checks for values &ge; 0. Never <code>null</code>.
   */
  @NonNull
  public static LongPredicate longIsGE0 ()
  {
    return x -> x >= 0;
  }

  /**
   * @return A long predicate that checks for values &gt; 0. Never <code>null</code>.
   */
  @NonNull
  public static LongPredicate longIsGT0 ()
  {
    return x -> x > 0;
  }

  /**
   * Combine two predicates with a logical AND. If both are <code>null</code>, <code>null</code> is
   * returned.
   *
   * @param <T>
   *        The data type to be checked.
   * @param aFirst
   *        The first predicate. May be <code>null</code>.
   * @param aSecond
   *        The second predicate. May be <code>null</code>.
   * @return The combined predicate, or <code>null</code> if both are <code>null</code>.
   */
  @Nullable
  public static <T> Predicate <T> and (@Nullable final Predicate <? super T> aFirst,
                                       @Nullable final Predicate <? super T> aSecond)
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

  /**
   * Combine two predicates with a logical OR. If both are <code>null</code>, <code>null</code> is
   * returned.
   *
   * @param <T>
   *        The data type to be checked.
   * @param aFirst
   *        The first predicate. May be <code>null</code>.
   * @param aSecond
   *        The second predicate. May be <code>null</code>.
   * @return The combined predicate, or <code>null</code> if both are <code>null</code>.
   */
  @Nullable
  public static <T> Predicate <T> or (@Nullable final Predicate <? super T> aFirst,
                                      @Nullable final Predicate <? super T> aSecond)
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
}
