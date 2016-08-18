package com.helger.commons.function;

import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * Represents an operation that accepts a single input argument and returns no
 * result but may throw an Exception. Unlike most other functional interfaces,
 * {@code IThrowingConsumer} is expected to operate via side-effects.
 * <p>
 * This is a functional interface whose functional method is
 * {@link #accept(Object)}.
 *
 * @param <T>
 *        the type of the input to the operation
 * @param <EXTYPE>
 *        exception type
 * @since 8.3.1
 */
@FunctionalInterface
public interface IThrowingConsumer <T, EXTYPE extends Throwable>
{
  /**
   * Performs this operation on the given argument.
   *
   * @param t
   *        the input argument
   * @throws EXTYPE
   *         In case it is needed
   */
  void accept (T t) throws EXTYPE;

  /**
   * Returns a composed {@code Consumer} that performs, in sequence, this
   * operation followed by the {@code after} operation. If performing either
   * operation throws an exception, it is relayed to the caller of the composed
   * operation. If performing this operation throws an exception, the
   * {@code after} operation will not be performed.
   *
   * @param after
   *        the operation to perform after this operation
   * @return a composed {@code Consumer} that performs in sequence this
   *         operation followed by the {@code after} operation
   * @throws NullPointerException
   *         if {@code after} is null
   */
  @Nonnull
  default IThrowingConsumer <T, EXTYPE> andThen (@Nonnull final IThrowingConsumer <? super T, ? extends EXTYPE> after)
  {
    Objects.requireNonNull (after);
    return (final T t) -> {
      accept (t);
      after.accept (t);
    };
  }
}
