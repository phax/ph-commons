package com.helger.commons.function;

import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

/**
 * Represents an operation that accepts a single {@code char}-valued argument
 * and returns no result. This is the primitive type specialization of
 * {@link Consumer} for {@code char}. Unlike most other functional interfaces,
 * {@code CharConsumer} is expected to operate via side-effects.
 * <p>
 * This is a functional interface whose functional method is
 * {@link #accept(char)}.
 *
 * @see Consumer
 * @since 1.8
 */
@FunctionalInterface
public interface CharConsumer
{
  /**
   * Performs this operation on the given argument.
   *
   * @param value
   *        the input argument
   */
  void accept (char value);

  /**
   * Returns a composed {@code CharConsumer} that performs, in sequence, this
   * operation followed by the {@code after} operation. If performing either
   * operation throws an exception, it is relayed to the caller of the composed
   * operation. If performing this operation throws an exception, the
   * {@code after} operation will not be performed.
   *
   * @param after
   *        the operation to perform after this operation
   * @return a composed {@code CharConsumer} that performs in sequence this
   *         operation followed by the {@code after} operation
   * @throws NullPointerException
   *         if {@code after} is null
   */
  default CharConsumer andThen (@Nonnull final CharConsumer after)
  {
    Objects.requireNonNull (after);
    return (final char t) -> {
      accept (t);
      after.accept (t);
    };
  }
}
