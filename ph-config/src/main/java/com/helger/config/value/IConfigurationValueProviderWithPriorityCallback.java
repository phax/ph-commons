package com.helger.config.value;

import javax.annotation.Nonnull;

/**
 * Callback interface for enumeration.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IConfigurationValueProviderWithPriorityCallback
{
  /**
   * Invoked for a single configuration value provider.
   * 
   * @param aCVP
   *        The Configuration value provider.
   * @param nPriority
   *        The priority it has.
   */
  void onConfigurationSource (@Nonnull IConfigurationValueProvider aCVP, int nPriority);
}
