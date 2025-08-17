package com.helger.base.cleanup;

import com.helger.annotation.style.IsSPIInterface;

import jakarta.annotation.Nonnull;

/**
 * Cleanup interface
 *
 * @author Philip Helger
 */
@IsSPIInterface
public interface ICleanUpRegistrarSPI
{
  /**
   * Register cleanup actions.
   *
   * @param aRegistry
   *        The destination registry. Never <code>null</code>.
   */
  void registerCleanUpAction (@Nonnull ICleanUpRegistry aRegistry);
}
