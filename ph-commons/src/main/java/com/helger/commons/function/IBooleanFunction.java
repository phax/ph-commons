package com.helger.commons.function;

import java.util.function.Function;

/**
 * Represents a function that accepts a boolean-valued argument and produces a
 * result. This is the {@code boolean}-consuming primitive specialization for
 * {@link Function}.
 * <p>
 * This is a functional interface whose functional method is
 * {@link #apply(boolean)}.
 *
 * @param <R>
 *        the type of the result of the function
 * @see Function
 * @since 1.8
 */
@FunctionalInterface
public interface IBooleanFunction <R>
{
  /**
   * Applies this function to the given argument.
   *
   * @param value
   *        the function argument
   * @return the function result
   */
  R apply (boolean value);
}
