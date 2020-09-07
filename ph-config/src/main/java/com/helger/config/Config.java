/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.config.source.IConfigurationSource;
import com.helger.config.source.MultiConfigurationValueProvider;
import com.helger.config.value.ConfiguredValue;
import com.helger.config.value.IConfigurationValueProvider;
import com.helger.config.value.IConfigurationValueProviderWithPriorityCallback;

/**
 * Default implementation of {@link IConfig}. It is recommended to use
 * {@link ConfigFactory} for accessing {@link IConfig} objects.
 *
 * @author Philip Helger
 */
public class Config implements IConfig
{
  private final IConfigurationValueProvider m_aValueProvider;
  private BiConsumer <String, ConfiguredValue> m_aKeyFoundConsumer;
  private Consumer <String> m_aKeyNotFoundConsumer;

  /**
   * Constructor
   *
   * @param aValueProvider
   *        The main configuration value provider. May not be <code>null</code>.
   */
  public Config (@Nonnull final IConfigurationValueProvider aValueProvider)
  {
    ValueEnforcer.notNull (aValueProvider, "ValueProvider");
    m_aValueProvider = aValueProvider;
  }

  /**
   * @return The configuration value provider as provided in the constructor.
   *         Never <code>null</code>.
   */
  @Nonnull
  public final IConfigurationValueProvider getConfigurationValueProvider ()
  {
    return m_aValueProvider;
  }

  /**
   * @return The callback to be invoked if a configuration value was found. May
   *         be <code>null</code>.
   */
  @Nullable
  public final BiConsumer <String, ConfiguredValue> getFoundKeyConsumer ()
  {
    return m_aKeyFoundConsumer;
  }

  /**
   * @param aKeyFoundConsumer
   *        The callback to be invoked if a configuration value was found. The
   *        parameters are key and value. May be <code>null</code>.
   * @return this for chaining
   * @since 9.4.5
   */
  @Nullable
  public final Config setFoundKeyConsumer (@Nullable final BiConsumer <String, ConfiguredValue> aKeyFoundConsumer)
  {
    m_aKeyFoundConsumer = aKeyFoundConsumer;
    return this;
  }

  /**
   * @return The callback to be invoked if a configuration value was <b>not</b>
   *         found. May be <code>null</code>.
   */
  @Nullable
  public final Consumer <String> getKeyNotFoundConsumer ()
  {
    return m_aKeyNotFoundConsumer;
  }

  /**
   * @param aKeyNotFoundConsumer
   *        The callback to be invoked if a configuration value was <b>not</b>
   *        found. The parameter is the key. May be <code>null</code>.
   * @return this for chaining
   * @since 9.4.5
   */
  @Nullable
  public final Config setKeyNotFoundConsumer (@Nullable final Consumer <String> aKeyNotFoundConsumer)
  {
    m_aKeyNotFoundConsumer = aKeyNotFoundConsumer;
    return this;
  }

  @Nullable
  public ConfiguredValue getConfiguredValue (@Nullable final String sKey)
  {
    // Resolve value
    final ConfiguredValue ret;
    if (StringHelper.hasNoText (sKey))
      ret = null;
    else
      ret = m_aValueProvider.getConfigurationValue (sKey);

    // Call consumers if configured
    if (ret != null)
    {
      if (m_aKeyFoundConsumer != null)
        m_aKeyFoundConsumer.accept (sKey, ret);
    }
    else
    {
      if (m_aKeyNotFoundConsumer != null)
        m_aKeyNotFoundConsumer.accept (sKey);
    }
    return ret;
  }

  @Nullable
  public String getValue (@Nullable final String sKey)
  {
    final ConfiguredValue aCV = getConfiguredValue (sKey);
    return aCV == null ? null : aCV.getValue ();
  }

  public static void forEachConfigurationValueProviderRecursive (@Nonnull final IConfigurationValueProvider aValueProvider,
                                                                 @Nonnull final IConfigurationValueProviderWithPriorityCallback aCallback)
  {
    if (aValueProvider instanceof MultiConfigurationValueProvider)
    {
      final MultiConfigurationValueProvider aMulti = (MultiConfigurationValueProvider) aValueProvider;
      // Descend recursively
      aMulti.forEachConfigurationValueProvider ( (cvp, prio) -> forEachConfigurationValueProviderRecursive (cvp, aCallback));
    }
    else
    {
      // By default no priority
      int nPriority = 0;
      if (aValueProvider instanceof IConfigurationSource)
      {
        final IConfigurationSource aSource = (IConfigurationSource) aValueProvider;
        nPriority = aSource.getPriority ();
      }
      aCallback.onConfigurationSource (aValueProvider, nPriority);
    }
  }

  public void forEachConfigurationValueProvider (@Nonnull final IConfigurationValueProviderWithPriorityCallback aCallback)
  {
    ValueEnforcer.notNull (aCallback, "Callback");
    forEachConfigurationValueProviderRecursive (m_aValueProvider, aCallback);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ValueProvider", m_aValueProvider)
                                       .append ("KeyFoundConsumer", m_aKeyFoundConsumer)
                                       .append ("KeyNotFoundConsumer", m_aKeyNotFoundConsumer)
                                       .getToString ();
  }

  @Nonnull
  public static Config create (@Nonnull final IConfigurationValueProvider aCVP)
  {
    return new Config (aCVP);
  }
}
