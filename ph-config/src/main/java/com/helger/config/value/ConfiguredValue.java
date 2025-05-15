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
package com.helger.config.value;

import com.helger.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.config.source.IConfigurationSource;

/**
 * This class represents a resolved configuration value. It contains the
 * configuration source where it was found and the actual value.
 *
 * @author Philip Helger
 * @since 9.4.5
 */
public class ConfiguredValue
{
  private final IConfigurationSource m_aConfigSrc;
  private final String m_sValue;

  public ConfiguredValue (@Nonnull final IConfigurationSource aConfigSrc, @Nonnull final String sValue)
  {
    ValueEnforcer.notNull (aConfigSrc, "ConfigurationSource");
    ValueEnforcer.notNull (sValue, "Value");

    m_aConfigSrc = aConfigSrc;
    m_sValue = sValue;
  }

  /**
   * @return The configuration source used. Never <code>null</code>.
   */
  @Nonnull
  public IConfigurationSource getConfigurationSource ()
  {
    return m_aConfigSrc;
  }

  /**
   * @return The resolved configuration value. Never <code>null</code>.
   */
  @Nonnull
  public String getValue ()
  {
    return m_sValue;
  }

  @Nonnull
  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ConfigSrc", m_aConfigSrc).append ("Value", m_sValue).getToString ();
  }
}
