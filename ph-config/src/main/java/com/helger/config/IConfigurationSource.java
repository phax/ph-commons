package com.helger.config;

import javax.annotation.Nonnull;

import com.helger.commons.traits.IGetterByKeyTrait;

public interface IConfigurationSource extends IGetterByKeyTrait <String>
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
}
