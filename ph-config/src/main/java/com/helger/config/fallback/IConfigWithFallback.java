/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.config.fallback;

import java.math.BigDecimal;

import com.helger.annotation.Nonempty;
import com.helger.config.IConfig;
import com.helger.config.value.ConfiguredValue;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * This is an extended configuration interface that supports the resolution of
 * configuration values from more then one key (e.g. for migration purposes).
 *
 * @author Philip Helger
 * @since 10.2.0
 */
public interface IConfigWithFallback extends IConfig
{
  /**
   * Get the {@link ConfiguredValue} based on the primary or the alternative
   * keys.
   *
   * @param sPrimary
   *        Primary configuration key. Should not be <code>null</code>.
   * @param aOldOnes
   *        The alternative keys to be resolved in the provided order. May
   *        neither be <code>null</code> nor empty.
   * @return <code>null</code> if neither the primary nor the old configuration
   *         property keys could be resolved.
   * @see #getConfiguredValue(String)
   */
  @Nullable
  ConfiguredValue getConfiguredValueOrFallback (@Nonnull String sPrimary, @Nonnull @Nonempty String... aOldOnes);

  /**
   * Get the configuration value as a String based on the primary or the
   * alternative keys.
   *
   * @param sPrimary
   *        Primary configuration key. Should not be <code>null</code>.
   * @param aOldOnes
   *        The alternative keys to be resolved in the provided order. May
   *        neither be <code>null</code> nor empty.
   * @return <code>null</code> if neither the primary nor the old configuration
   *         property keys could be resolved.
   */
  @Nullable
  String getAsStringOrFallback (@Nonnull String sPrimary, @Nonnull @Nonempty String... aOldOnes);

  /**
   * Get the configuration value as a char array based on the primary or the
   * alternative keys.
   *
   * @param sPrimary
   *        Primary configuration key. Should not be <code>null</code>.
   * @param aOldOnes
   *        The alternative keys to be resolved in the provided order. May
   *        neither be <code>null</code> nor empty.
   * @return <code>null</code> if neither the primary nor the old configuration
   *         property keys could be resolved.
   * @since 11.1.10
   */
  @Nullable
  char [] getAsCharArrayOrFallback (@Nonnull String sPrimary, @Nonnull @Nonempty String... aOldOnes);

  /**
   * Get the configuration value as a BigDecimal based on the primary or the
   * alternative keys.
   *
   * @param sPrimary
   *        Primary configuration key. Should not be <code>null</code>.
   * @param aOldOnes
   *        The alternative keys to be resolved in the provided order. May
   *        neither be <code>null</code> nor empty.
   * @return <code>null</code> if neither the primary nor the old configuration
   *         property keys could be resolved.
   */
  @Nullable
  BigDecimal getAsBigDecimalOrFallback (@Nonnull String sPrimary, @Nonnull @Nonempty String... aOldOnes);

  /**
   * Get the configuration value as a int based on the primary or the
   * alternative keys.
   *
   * @param sPrimary
   *        Primary configuration key. Should not be <code>null</code>.
   * @param nBogus
   *        A value that is considered invalid for all (primary and old ones) to
   *        indicate that the other values need to be searched as well.
   * @param nDefault
   *        The value to be returned if none of the keys could be resolved.
   * @param aOldOnes
   *        The alternative keys to be resolved in the provided order. May
   *        neither be <code>null</code> nor empty.
   * @return <code>null</code> if neither the primary nor the old configuration
   *         property keys could be resolved.
   */
  int getAsIntOrFallback (@Nonnull String sPrimary, int nBogus, int nDefault, @Nonnull @Nonempty String... aOldOnes);

  /**
   * Get the configuration value as a long based on the primary or the
   * alternative keys.
   *
   * @param sPrimary
   *        Primary configuration key. Should not be <code>null</code>.
   * @param nBogus
   *        A value that is considered invalid for all (primary and old ones) to
   *        indicate that the other values need to be searched as well.
   * @param nDefault
   *        The value to be returned if none of the keys could be resolved.
   * @param aOldOnes
   *        The alternative keys to be resolved in the provided order. May
   *        neither be <code>null</code> nor empty.
   * @return <code>null</code> if neither the primary nor the old configuration
   *         property keys could be resolved.
   */
  long getAsLongOrFallback (@Nonnull String sPrimary,
                            long nBogus,
                            long nDefault,
                            @Nonnull @Nonempty String... aOldOnes);
}
