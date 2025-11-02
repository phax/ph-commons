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
package com.helger.config.source.sysprop;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.system.SystemProperties;
import com.helger.collection.commons.CommonsTreeMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.config.source.AbstractConfigurationSource;
import com.helger.config.source.EConfigSourceType;
import com.helger.config.source.IConfigurationSource;
import com.helger.config.source.IIterableConfigurationSource;
import com.helger.config.value.ConfiguredValue;

/**
 * Default implementation of {@link IConfigurationSource} for system properties.
 *
 * @author Philip Helger
 */
@Immutable
public class ConfigurationSourceSystemProperty extends AbstractConfigurationSource implements
                                               IIterableConfigurationSource
{
  public static final EConfigSourceType SOURCE_TYPE = EConfigSourceType.SYSTEM_PROPERTY;
  private static final Logger LOGGER = LoggerFactory.getLogger (ConfigurationSourceSystemProperty.class);

  public ConfigurationSourceSystemProperty ()
  {
    this (SOURCE_TYPE.getDefaultPriority ());
  }

  public ConfigurationSourceSystemProperty (final int nPriority)
  {
    super (SOURCE_TYPE, nPriority);
  }

  public boolean isInitializedAndUsable ()
  {
    // No differentiation here
    return true;
  }

  @Nullable
  public ConfiguredValue getConfigurationValue (@NonNull @Nonempty final String sKey)
  {
    if (LOGGER.isTraceEnabled ())
      LOGGER.trace ("Querying configuration property '" + sKey + "' as SystemProperty");

    // Uses PrivilegedAction internally
    final String sValue = SystemProperties.getPropertyValueOrNull (sKey);
    if (sValue == null)
      return null;

    // Consistency check
    if (hasTrailingWhitespace (sValue))
      LOGGER.warn ("The value of the system property '" +
                   sKey +
                   "' has a trailing whitespace. This may lead to unintended side effects.");

    return new ConfiguredValue (this, sValue);
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsMap <String, String> getAllConfigItems ()
  {
    // Sort by name
    return new CommonsTreeMap <> (SystemProperties.getAllProperties ());
  }
}
