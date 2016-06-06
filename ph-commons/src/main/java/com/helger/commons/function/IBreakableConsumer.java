package com.helger.commons.function;

import javax.annotation.Nonnull;

import com.helger.commons.state.EContinue;

/**
 * A special Consumer-like interface that allows for stopping the iteration.
 *
 * @author Philip Helger
 * @param <T>
 *        Type to be consumed
 */
public interface IBreakableConsumer <T>
{
  /**
   * Handle the current element
   *
   * @param aElement
   *        Current element.
   * @return {@link EContinue#CONTINUE} to continue or {@link EContinue#BREAK}
   *         to stop iteration.
   */
  @Nonnull
  EContinue accept (T aElement);
}
