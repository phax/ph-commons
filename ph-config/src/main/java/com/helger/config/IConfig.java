/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import java.time.Duration;
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.Nonempty;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.state.ESuccess;
import com.helger.base.wrapper.Wrapper;
import com.helger.config.source.MultiConfigurationValueProvider;
import com.helger.config.source.resource.IConfigurationSourceResource;
import com.helger.config.value.ConfiguredValue;
import com.helger.config.value.IConfigurationValueProvider;
import com.helger.config.value.IConfigurationValueProviderWithPriorityCallback;
import com.helger.config.value.parser.ConfigDurationParser;
import com.helger.typeconvert.trait.IGetterByKeyTrait;

/**
 * Read-only configuration
 *
 * @author Philip Helger
 */
public interface IConfig extends IGetterByKeyTrait <String>
{
  /**
   * @return The underlying configuration value provider. Never <code>null</code>.
   * @since 12.1.4 in this interface
   */
  @NonNull
  IConfigurationValueProvider getConfigurationValueProvider ();

  /**
   * Check if the provided key is available in any of the configured value providers.
   *
   * @param sKey
   *        The configuration key to look up. May be <code>null</code>.
   * @return <code>true</code> if it is present, <code>false</code> if not.
   * @since 12.2.0
   */
  boolean containsConfiguredValue (@Nullable String sKey);

  /**
   * Get the configured value, which is the combination of the matching configuration source and the
   * value, for the provided key. This method may trigger key-found/not-found callbacks.
   *
   * @param sKey
   *        The configuration key to look up. May be <code>null</code>.
   * @return <code>null</code> if no such configuration value is available.
   * @since 9.4.5
   */
  @Nullable
  ConfiguredValue getConfiguredValue (@Nullable String sKey);

  /**
   * Enumerate all contained configuration value provider. All items will be enumerated in the order
   * they are checked, so the ones with highest priority first. This iteration is recursive and also
   * automatically dives into nested {@link MultiConfigurationValueProvider} instances
   * automatically.
   *
   * @param aCallback
   *        The callback to invoked. May not be <code>null</code>.
   */
  void forEachConfigurationValueProvider (@NonNull IConfigurationValueProviderWithPriorityCallback aCallback);

  /**
   * Create a subset view of this config that automatically prepends the provided key prefix to all
   * lookups. This allows passing a scoped configuration to subsystems without them needing to know
   * their own prefix.
   * <p>
   * Example: <code>config.getSubConfig ("db").getAsString ("host")</code> resolves the key
   * <code>"db.host"</code>.
   *
   * @param sPrefix
   *        The key prefix. May not be <code>null</code> or empty. A trailing dot is optional.
   * @return A new {@link ConfigSubset} instance. Never <code>null</code>.
   * @since 12.2.0
   */
  @NonNull
  default IConfig getSubConfig (@NonNull @Nonempty final String sPrefix)
  {
    return new ConfigSubset (this, sPrefix);
  }

  /**
   * Look up the configured value for the provided key and parse it as a {@link Duration} using
   * {@link ConfigDurationParser}. Equivalent to
   * {@link #getAsConfigDuration(String, Consumer)} with a <code>null</code> error handler — i.e.
   * parse failures are reported only via a <code>null</code> return value.
   *
   * @param sKey
   *        The configuration key to look up. May be <code>null</code>.
   * @return <code>null</code> if no such value is configured, the value is blank, or it cannot be
   *         parsed; otherwise the parsed {@link Duration}.
   * @see ConfigDurationParser
   * @since 12.2.5
   */
  @Nullable
  default Duration getAsConfigDuration (@Nullable final String sKey)
  {
    return getAsConfigDuration (sKey, null);
  }

  /**
   * Look up the configured value for the provided key and parse it as a {@link Duration} using
   * {@link ConfigDurationParser}. Variable replacement (if enabled on the underlying config) is
   * applied before parsing.
   *
   * @param sKey
   *        The configuration key to look up. May be <code>null</code>.
   * @param aParseErrorHdl
   *        Invoked once with a descriptive message if the configured value is present but cannot be
   *        parsed. Not invoked when no value is configured. May be <code>null</code> in which case
   *        parse failures are silent.
   * @return <code>null</code> if no such value is configured, the value is blank, or it cannot be
   *         parsed; otherwise the parsed {@link Duration}.
   * @see ConfigDurationParser#parseDuration(String, Consumer)
   * @since 12.2.5
   */
  @Nullable
  default Duration getAsConfigDuration (@Nullable final String sKey,
                                        @Nullable final Consumer <String> aParseErrorHdl)
  {
    return ConfigDurationParser.parseDuration (getAsString (sKey), aParseErrorHdl);
  }

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
  @NonNull
  default ESuccess reloadAllResourceBasedConfigurationValues ()
  {
    final Wrapper <ESuccess> ret = Wrapper.of (ESuccess.SUCCESS);
    forEachConfigurationValueProvider ( (cvp, prio) -> {
      if (cvp instanceof final IConfigurationSourceResource aSrcRes)
        ret.set (ret.get ().and (aSrcRes.reload ()));
    });
    return ret.get ();
  }
}
