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
package com.helger.config.source.appl;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
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
  private static final Logger LOGGER = LoggerFactory.getLogger (ConfigurationSourceFunction.class);

  private final UnaryOperator <String> m_aValueProvider;

  /**
   * Constructor with default priority
   *
   * @param aValueProvider
   *        The value provider to be used. May not be <code>null</code>.
   */
  public ConfigurationSourceFunction (@NonNull final UnaryOperator <String> aValueProvider)
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
  public ConfigurationSourceFunction (final int nPriority, @NonNull final UnaryOperator <String> aValueProvider)
  {
    super (SOURCE_TYPE, nPriority);
    ValueEnforcer.notNull (aValueProvider, "ValueProvider");
    m_aValueProvider = aValueProvider;
  }

  /**
   * @return The value provider as passed in the constructor. Never
   *         <code>null</code>.
   */
  @NonNull
  public final UnaryOperator <String> getValueProvider ()
  {
    return m_aValueProvider;
  }

  public boolean isInitializedAndUsable ()
  {
    return true;
  }

  @Nullable
  public ConfiguredValue getConfigurationValue (@NonNull @Nonempty final String sKey)
  {
    final String sValue = m_aValueProvider.apply (sKey);
    if (sValue == null)
      return null;

    // Consistency check
    if (hasTrailingWhitespace (sValue))
      LOGGER.warn ("The value of the configuration key '" +
                   sKey +
                   "' has a trailing whitespace. This may lead to unintended side effects.");

    return new ConfiguredValue (this, sValue);
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
