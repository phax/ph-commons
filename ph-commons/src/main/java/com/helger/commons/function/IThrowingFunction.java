package com.helger.commons.function;

import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * Represents a function that accepts one argument and produces a result and may
 * throw an Exception.
 * <p>
 * This is a functional interface whose functional method is
 * {@link #apply(Object)}.
 *
 * @param <T>
 *        the type of the input to the function
 * @param <R>
 *        the type of the result of the function
 * @param <EXTYPE>
 *        exception type
 * @since 8.3.1
 */
@FunctionalInterface
public interface IThrowingFunction <T, R, EXTYPE extends Throwable>
{
  /**
   * Applies this function to the given argument.
   *
   * @param t
   *        the function argument
   * @return the function result
   * @throws EXTYPE
   *         In case it is needed
   */
  R apply (T t) throws EXTYPE;

  /**
   * Returns a composed function that first applies the {@code before} function
   * to its input, and then applies this function to the result. If evaluation
   * of either function throws an exception, it is relayed to the caller of the
   * composed function.
   *
   * @param <V>
   *        the type of input to the {@code before} function, and to the
   *        composed function
   * @param before
   *        the function to apply before this function is applied
   * @return a composed function that first applies the {@code before} function
   *         and then applies this function
   * @throws NullPointerException
   *         if before is null
   * @see #andThen(IThrowingFunction)
   */
  @Nonnull
  default <V> IThrowingFunction <V, R, EXTYPE> compose (@Nonnull final IThrowingFunction <? super V, ? extends T, ? extends EXTYPE> before)
  {
    Objects.requireNonNull (before);
    return (final V v) -> apply (before.apply (v));
  }

  /**
   * Returns a composed function that first applies this function to its input,
   * and then applies the {@code after} function to the result. If evaluation of
   * either function throws an exception, it is relayed to the caller of the
   * composed function.
   *
   * @param <V>
   *        the type of output of the {@code after} function, and of the
   *        composed function
   * @param after
   *        the function to apply after this function is applied
   * @return a composed function that first applies this function and then
   *         applies the {@code after} function
   * @throws NullPointerException
   *         if after is null
   * @see #compose(IThrowingFunction)
   */
  @Nonnull
  default <V> IThrowingFunction <T, V, EXTYPE> andThen (@Nonnull final IThrowingFunction <? super R, ? extends V, ? extends EXTYPE> after)
  {
    Objects.requireNonNull (after);
    return (final T t) -> after.apply (apply (t));
  }
}
