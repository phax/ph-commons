/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.helger.config.source.appl;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.ToStringGenerator;
import com.helger.config.source.AbstractConfigurationSource;
import com.helger.config.source.EConfigSourceType;
import com.helger.config.source.IConfigurationSource;
import com.helger.config.value.ConfiguredValue;

/**
 * Default implementation of {@link IConfigurationSource} for application based
 * configuration sources. The main values are provided using a custom
 * {@link Function}.
 *
 * @author Philip Helger
 */
@Immutable
public class ConfigurationSourceFunction extends AbstractConfigurationSource
{
  public static final EConfigSourceType SOURCE_TYPE = EConfigSourceType.APPLICATION;

  private final Function <String, String> m_aValueProvider;

  /**
   * Constructor with default priority
   *
   * @param aValueProvider
   *        The value provider to be used. May not be <code>null</code>.
   */
  public ConfigurationSourceFunction (@Nonnull final Function <String, String> aValueProvider)
  {
    this (SOURCE_TYPE.getDefaultPriority (), aValueProvider);
  }

  /**
   * Constructor
   *
   * @param nPriority
   *        Configuration source priority.
   * @param aValueProvider
   *        The value provider to be used. May not be <code>null</code>.
   */
  public ConfigurationSourceFunction (final int nPriority, @Nonnull final Function <String, String> aValueProvider)
  {
    super (SOURCE_TYPE, nPriority);
    ValueEnforcer.notNull (aValueProvider, "ValueProvider");
    m_aValueProvider = aValueProvider;
  }

  /**
   * @return The value provider as passed in the constructor. Never
   *         <code>null</code>.
   */
  @Nonnull
  public final Function <String, String> getValueProvider ()
  {
    return m_aValueProvider;
  }

  public boolean isInitializedAndUsable ()
  {
    return true;
  }

  @Nullable
  public ConfiguredValue getConfigurationValue (@Nonnull @Nonempty final String sKey)
  {
    final String sValue = m_aValueProvider.apply (sKey);
    return sValue == null ? null : new ConfiguredValue (this, sValue);
  }

  @Override
  public boolean equals (final Object o)
  {
    // New field, no change
    return super.equals (o);
  }

  @Override
  public int hashCode ()
  {
    // New field, no change
    return super.hashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("ValueProvider", m_aValueProvider).getToString ();
  }
}
