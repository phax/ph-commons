package com.helger.commons.function;

import java.util.function.Function;

/**
 * Represents a function that accepts an int-valued argument and produces a
 * float-valued result. This is the {@code int}-to-{@code float} primitive
 * specialization for {@link Function}.
 * <p>
 * This is a functional interface whose functional method is
 * {@link #applyAsFloat(int)}.
 *
 * @see Function
 * @since 8.4.0
 */
@FunctionalInterface
public interface IntToFloatFunction
{

  /**
   * Applies this function to the given argument.
   *
   * @param value
   *        the function argument
   * @return the function result
   */
  float applyAsFloat (int value);
}
