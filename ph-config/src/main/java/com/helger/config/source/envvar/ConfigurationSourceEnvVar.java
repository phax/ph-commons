/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.config.source.envvar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsTreeMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.config.source.AbstractConfigurationSource;
import com.helger.config.source.EConfigSourceType;
import com.helger.config.source.IConfigurationSource;
import com.helger.config.source.IIterableConfigurationSource;
import com.helger.config.value.ConfiguredValue;

/**
 * Default implementation of {@link IConfigurationSource} for environment
 * variables.
 *
 * @author Philip Helger
 */
@Immutable
public class ConfigurationSourceEnvVar extends AbstractConfigurationSource implements IIterableConfigurationSource
{
  public static final EConfigSourceType SOURCE_TYPE = EConfigSourceType.ENVIRONMENT_VARIABLE;

  private static final Logger LOGGER = LoggerFactory.getLogger (ConfigurationSourceEnvVar.class);

  public ConfigurationSourceEnvVar ()
  {
    this (SOURCE_TYPE.getDefaultPriority ());
  }

  public ConfigurationSourceEnvVar (final int nPriority)
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
    // Unify the naming to the environment conventions
    final String sRealName = EnvVarHelper.getUnifiedSysEnvName (sKey, EnvVarHelper.DEFAULT_REPLACEMENT_CHAR);

    if (LOGGER.isTraceEnabled ())
      LOGGER.trace ("Querying configuration property '" + sKey + "' as EnvVar '" + sRealName + "'");

    String sValue = null;
    try
    {
      sValue = System.getenv (sRealName);
    }
    catch (final SecurityException ex)
    {
      LOGGER.error ("Security violation accessing environment variable '" + sRealName + "'", ex);
    }
    if (sValue == null)
      return null;

    // Consistency check
    if (hasTrailingWhitespace (sValue))
      LOGGER.warn ("The value of the environment variable '" +
                   sRealName +
                   "' has a trailing whitespace. This may lead to unintended side effects.");

    return new ConfiguredValue (this, sValue);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsMap <String, String> getAllConfigItems ()
  {
    // Sort by name
    return new CommonsTreeMap <> (System.getenv ());
  }
}
