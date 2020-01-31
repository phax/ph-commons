package com.helger.config.source;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;

public interface IConfigurationSource
{
  /**
   * @return The configuration source type. Never <code>null</code>.
   */
  @Nonnull
  EConfigSourceType getType ();

  /**
   * @return THe higher the priority the earlier it is checked. Values between
   *         {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE} are
   *         allowed.
   */
  int getPriority ();

  /**
   * Get the configuration value with the provided key.
   * 
   * @param sKey
   *        The key to be retrieved. May neither be <code>null</code> nor empty.
   * @return <code>null</code> if no such value is available
   */
  @Nullable
  String getConfigurationValue (@Nonnull @Nonempty String sKey);
}
