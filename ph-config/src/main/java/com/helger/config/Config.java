package com.helger.config;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.StringHelper;
import com.helger.config.source.IConfigurationValueProvider;

/**
 * Default implementation of {@link IConfig}. It is recommended to use
 * {@link ConfigFactory} for accessing {@link IConfig} objects.
 * 
 * @author Philip Helger
 */
public class Config implements IConfig
{
  private final Function <String, Object> m_aResolver;

  public Config (@Nonnull final Function <String, Object> aResolver)
  {
    ValueEnforcer.notNull (aResolver, "Resolver");
    m_aResolver = aResolver;
  }

  @Nullable
  public Object getValue (@Nullable final String sKey)
  {
    if (StringHelper.hasNoText (sKey))
      return null;
    return m_aResolver.apply (sKey);
  }

  @Nonnull
  public static Config create (@Nonnull final IConfigurationValueProvider aCVP)
  {
    ValueEnforcer.notNull (aCVP, "CVP");
    return new Config (aCVP::getConfigurationValue);
  }
}
