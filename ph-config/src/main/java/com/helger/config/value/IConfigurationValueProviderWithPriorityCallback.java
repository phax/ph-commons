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
  void onConfigurationSource (@Nonnull IConfigurationValueProvider aCVP, int nPriority);
}
