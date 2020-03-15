package com.helger.config.source.envvar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.Nonempty;
import com.helger.config.source.AbstractConfigurationSource;
import com.helger.config.source.EConfigSourceType;
import com.helger.config.source.IConfigurationSource;

/**
 * Default implementation of {@link IConfigurationSource} for environment
 * variables.
 *
 * @author Philip Helger
 */
public class ConfigurationSourceEnvVar extends AbstractConfigurationSource
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ConfigurationSourceEnvVar.class);
  private static final EConfigSourceType TYPE = EConfigSourceType.ENVIRONMENT_VARIABLE;

  public ConfigurationSourceEnvVar ()
  {
    this (TYPE.getDefaultPriority ());
  }

  public ConfigurationSourceEnvVar (final int nPriority)
  {
    super (TYPE, nPriority);
  }

  @Nullable
  public String getConfigurationValue (@Nonnull @Nonempty final String sKey)
  {
    final String sRealName = EnvVarHelper.getUnifiedSysEnvName (sKey, EnvVarHelper.DEFAULT_REPLACEMENT_CHAR);
    try
    {
      return System.getenv (sRealName);
    }
    catch (final SecurityException ex)
    {
      LOGGER.error ("Security violation accessing environment variable '" + sRealName + "'", ex);
      return null;
    }
  }
}
