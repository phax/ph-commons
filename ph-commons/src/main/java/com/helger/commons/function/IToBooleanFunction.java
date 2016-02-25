package com.helger.commons.function;

/**
 * Represents a function that produces a boolean-valued result. This is the
 * {@code boolean}-producing primitive specialization for
 * {@link java.util.function.Function}.
 * <p>
 * This is a functional interface whose functional method is
 * {@link #applyAsBoolean(Object)}.
 *
 * @param <T>
 *        the type of the input to the function
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface IToBooleanFunction <T>
{

  /**
   * Applies this function to the given argument.
   *
   * @param value
   *        the function argument
   * @return the function result
   */
  boolean applyAsBoolean (T value);
}
