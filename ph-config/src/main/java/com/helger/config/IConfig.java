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
package com.helger.config;

import com.helger.annotation.Nonnegative;
import com.helger.base.state.ESuccess;
import com.helger.base.wrapper.Wrapper;
import com.helger.commons.mutable.MutableInt;
import com.helger.commons.traits.IGetterByKeyTrait;
import com.helger.config.source.res.IConfigurationSourceResource;
import com.helger.config.value.ConfiguredValue;
import com.helger.config.value.IConfigurationValueProviderWithPriorityCallback;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Read-only configuration
 *
 * @author Philip Helger
 */
public interface IConfig extends IGetterByKeyTrait <String>
{
  /**
   * Get the configured value, which is the combination of the matching configuration source and the
   * value, for the provided key.
   *
   * @param sKey
   *        The configuration key to look up.
   * @return <code>null</code> if no such configuration value is available.
   * @since 9.4.5
   */
  @Nullable
  ConfiguredValue getConfiguredValue (@Nullable String sKey);

  /**
   * Enumerate all contained configuration value provider. All items will be enumerated in the order
   * they are checked, so the ones with highest priority first.
   *
   * @param aCallback
   *        The callback to invoked. May not be <code>null</code>.
   */
  void forEachConfigurationValueProvider (@Nonnull IConfigurationValueProviderWithPriorityCallback aCallback);

  /**
   * Count all configuration sources that implement {@link IConfigurationSourceResource}
   *
   * @return The number of resource based configuration sources contained. Always &ge; 0.
   */
  @Nonnegative
  default int getResourceBasedConfigurationValueProviderCount ()
  {
    final MutableInt aCount = new MutableInt (0);
    forEachConfigurationValueProvider ( (cvp, prio) -> {
      if (cvp instanceof IConfigurationSourceResource)
        aCount.inc ();
    });
    return aCount.intValue ();
  }

  /**
   * Reload the configuration from all resource based sources (the ones implementing
   * {@link IConfigurationSourceResource}).
   *
   * @return {@link ESuccess#SUCCESS} if all could be reloaded, {@link ESuccess#FAILURE} if at least
   *         one failed.
   * @since 9.4.8
   */
  @Nonnull
  default ESuccess reloadAllResourceBasedConfigurationValues ()
  {
    final Wrapper <ESuccess> ret = new Wrapper <> (ESuccess.SUCCESS);
    forEachConfigurationValueProvider ( (cvp, prio) -> {
      if (cvp instanceof final IConfigurationSourceResource aSrcRes)
        ret.set (ret.get ().or (aSrcRes.reload ()));
    });
    return ret.get ();
  }
}
