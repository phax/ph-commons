/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsTreeMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.system.SystemProperties;
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
  public ConfiguredValue getConfigurationValue (@Nonnull @Nonempty final String sKey)
  {
    // Uses PrivilegedAction internally
    final String sValue = SystemProperties.getPropertyValueOrNull (sKey);
    return sValue == null ? null : new ConfiguredValue (this, sValue);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsMap <String, String> getAllConfigItems ()
  {
    // Sort by name
    return new CommonsTreeMap <> (SystemProperties.getAllProperties ());
  }
}
