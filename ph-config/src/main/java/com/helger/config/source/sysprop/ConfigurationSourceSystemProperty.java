package com.helger.config.source.sysprop;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.system.SystemProperties;
import com.helger.config.source.AbstractConfigurationSource;
import com.helger.config.source.EConfigSourceType;
import com.helger.config.source.IConfigurationSource;

/**
 * Default implementation of {@link IConfigurationSource} for system properties.
 *
 * @author Philip Helger
 */
public class ConfigurationSourceSystemProperty extends AbstractConfigurationSource
{
  private static final EConfigSourceType TYPE = EConfigSourceType.SYSTEM_PROPERTY;

  public ConfigurationSourceSystemProperty ()
  {
    this (TYPE.getDefaultPriority ());
  }

  public ConfigurationSourceSystemProperty (final int nPriority)
  {
    super (TYPE, nPriority);
  }

  @Nullable
  public String getConfigurationValue (@Nonnull @Nonempty final String sKey)
  {
    // Uses PrivilegedAction internally
    return SystemProperties.getPropertyValueOrNull (sKey);
  }
}
