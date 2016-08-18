package com.helger.commons.function;

/**
 * Represents a supplier of results that may throw an Exception.
 * <p>
 * There is no requirement that a new or distinct result be returned each time
 * the supplier is invoked.
 * <p>
 * This is a functional interface whose functional method is {@link #get()}.
 *
 * @param <T>
 *        the type of results supplied by this supplier
 * @param <EXTYPE>
 *        exception type
 * @since 8.3.1
 */
@FunctionalInterface
public interface IThrowingSupplier <T, EXTYPE extends Throwable>
{
  /**
   * Gets a result.
   *
   * @return a result
   * @throws EXTYPE
   *         In case it is needed
   */
  T get () throws EXTYPE;
}
