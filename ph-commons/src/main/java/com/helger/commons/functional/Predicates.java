/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

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

  @Nonnull
  public static ICharPredicate charIsEQ0 ()
  {
    return n -> n == 0;
  }

  @Nonnull
  public static ICharPredicate charIsNE0 ()
  {
    return n -> n != 0;
  }

  @Nonnull
  public static ICharPredicate charIsGT0 ()
  {
    return n -> n > 0;
  }

  @Nonnull
  public static DoublePredicate doubleIsLT0 ()
  {
    return n -> n < 0;
  }

  @Nonnull
  public static DoublePredicate doubleIsLE0 ()
  {
    return n -> n <= 0;
  }

  @Nonnull
  public static DoublePredicate doubleIsEQ0 ()
  {
    return n -> n == 0;
  }

  @Nonnull
  public static DoublePredicate doubleIsNE0 ()
  {
    return n -> n != 0;
  }

  @Nonnull
  public static DoublePredicate doubleIsGE0 ()
  {
    return n -> n >= 0;
  }

  @Nonnull
  public static DoublePredicate doubleIsGT0 ()
  {
    return n -> n > 0;
  }

  @Nonnull
  public static IntPredicate intIsLT0 ()
  {
    return n -> n < 0;
  }

  @Nonnull
  public static IntPredicate intIsLE0 ()
  {
    return n -> n <= 0;
  }

  @Nonnull
  public static IntPredicate intIsEQ0 ()
  {
    return n -> n == 0;
  }

  @Nonnull
  public static IntPredicate intIsNE0 ()
  {
    return n -> n != 0;
  }

  @Nonnull
  public static IntPredicate intIsGE0 ()
  {
    return n -> n >= 0;
  }

  @Nonnull
  public static IntPredicate intIsGT0 ()
  {
    return n -> n > 0;
  }

  @Nonnull
  public static LongPredicate longIsLT0 ()
  {
    return n -> n < 0;
  }

  @Nonnull
  public static LongPredicate longIsLE0 ()
  {
    return n -> n <= 0;
  }

  @Nonnull
  public static LongPredicate longIsEQ0 ()
  {
    return n -> n == 0;
  }

  @Nonnull
  public static LongPredicate longIsNE0 ()
  {
    return n -> n != 0;
  }

  @Nonnull
  public static LongPredicate longIsGE0 ()
  {
    return n -> n >= 0;
  }

  @Nonnull
  public static LongPredicate longIsGT0 ()
  {
    return n -> n > 0;
  }
}
