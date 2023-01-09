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
package com.helger.config.fallback;

import java.math.BigDecimal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.config.Config;
import com.helger.config.value.ConfiguredValue;
import com.helger.config.value.IConfigurationValueProvider;

/**
 * The default implementation of {@link IConfigWithFallback}.
 *
 * @author Philip Helger
 * @since 10.2.0
 */
public class ConfigWithFallback extends Config implements IConfigWithFallback
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ConfigWithFallback.class);
  /**
   * The default implementation that just logs a warning.
   */
  public static final IConfigKeyOutdatedNotifier DEFAULT_OUTDATED_NOTIFIER = (sOld, sNew) -> {
    if (LOGGER.isWarnEnabled ())
      LOGGER.warn ("Please rename the configuration property '" +
                   sOld +
                   "' to '" +
                   sNew +
                   "'. The old name is deprecated.");
  };

  private IConfigKeyOutdatedNotifier m_aOutdatedNotifier = DEFAULT_OUTDATED_NOTIFIER;

  public ConfigWithFallback (@Nonnull final IConfigurationValueProvider aValueProvider)
  {
    super (aValueProvider);
  }

  /**
   * @return The outdated key notifier to be invoked, when an old configuration
   *         key was used. Never <code>null</code>.
   */
  @Nonnull
  public final IConfigKeyOutdatedNotifier getOutdatedNotifier ()
  {
    return m_aOutdatedNotifier;
  }

  /**
   * Set the outdated key notifier to be invoked when an old configuration key
   * was used.
   *
   * @param aOutdatedNotifier
   *        The outdated key identifier to be used. May not be
   *        <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public final ConfigWithFallback setOutdatedNotifier (@Nonnull final IConfigKeyOutdatedNotifier aOutdatedNotifier)
  {
    ValueEnforcer.notNull (aOutdatedNotifier, "OutdatedNotifier");
    m_aOutdatedNotifier = aOutdatedNotifier;
    return this;
  }

  @Nullable
  public ConfiguredValue getConfiguredValueOrFallback (@Nonnull final String sPrimary,
                                                       @Nonnull final String... aOldOnes)
  {
    ConfiguredValue ret = getConfiguredValue (sPrimary);
    if (ret == null)
    {
      // Try the old names
      for (final String sOld : aOldOnes)
      {
        ret = getConfiguredValue (sOld);
        if (ret != null)
        {
          // Notify on old name usage
          m_aOutdatedNotifier.onOutdatedConfigurationKey (sOld, sPrimary);
          break;
        }
      }
    }
    return ret;
  }

  @Nullable
  public String getAsStringOrFallback (@Nonnull final String sPrimary, @Nonnull final String... aOldOnes)
  {
    String ret = getAsString (sPrimary);
    if (StringHelper.hasNoText (ret))
    {
      // Try the old names
      for (final String sOld : aOldOnes)
      {
        ret = getAsString (sOld);
        if (StringHelper.hasText (ret))
        {
          // Notify on old name usage
          m_aOutdatedNotifier.onOutdatedConfigurationKey (sOld, sPrimary);
          break;
        }
      }
    }
    return ret;
  }

  @Nullable
  public BigDecimal getAsBigDecimalOrFallback (@Nonnull final String sPrimary, @Nonnull final String... aOldOnes)
  {
    BigDecimal ret = getAsBigDecimal (sPrimary);
    if (ret == null)
    {
      // Try the old names
      for (final String sOld : aOldOnes)
      {
        ret = getAsBigDecimal (sOld);
        if (ret != null)
        {
          // Notify on old name usage
          m_aOutdatedNotifier.onOutdatedConfigurationKey (sOld, sPrimary);
          break;
        }
      }
    }
    return ret;
  }

  public int getAsIntOrFallback (@Nonnull final String sPrimary,
                                 final int nBogus,
                                 final int nDefault,
                                 @Nonnull final String... aOldOnes)
  {
    int ret = getAsInt (sPrimary, nBogus);
    if (ret == nBogus)
    {
      // Try the old names
      for (final String sOld : aOldOnes)
      {
        ret = getAsInt (sOld, nBogus);
        if (ret != nBogus)
        {
          // Notify on old name usage
          m_aOutdatedNotifier.onOutdatedConfigurationKey (sOld, sPrimary);
          break;
        }
      }
    }
    return ret == nBogus ? nDefault : ret;
  }

  public long getAsLongOrFallback (@Nonnull final String sPrimary,
                                   final long nBogus,
                                   final long nDefault,
                                   @Nonnull final String... aOldOnes)
  {
    long ret = getAsLong (sPrimary, nBogus);
    if (ret == nBogus)
    {
      // Try the old names
      for (final String sOld : aOldOnes)
      {
        ret = getAsLong (sOld, nBogus);
        if (ret != nBogus)
        {
          // Notify on old name usage
          m_aOutdatedNotifier.onOutdatedConfigurationKey (sOld, sPrimary);
          break;
        }
      }
    }
    return ret == nBogus ? nDefault : ret;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("OutdatedNotifier", m_aOutdatedNotifier)
                            .getToString ();
  }
}
