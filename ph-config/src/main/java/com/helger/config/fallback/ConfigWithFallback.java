/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;
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
    LOGGER.warn ("Please rename the configuration property '" +
                 sOld +
                 "' to '" +
                 sNew +
                 "'. The old name is deprecated.");
  };

  private IConfigKeyOutdatedNotifier m_aOutdatedNotifier = DEFAULT_OUTDATED_NOTIFIER;

  public ConfigWithFallback (@NonNull final IConfigurationValueProvider aValueProvider)
  {
    super (aValueProvider);
  }

  /**
   * @return The outdated key notifier to be invoked, when an old configuration key was used. Never
   *         <code>null</code>.
   */
  @NonNull
  public final IConfigKeyOutdatedNotifier getOutdatedNotifier ()
  {
    return m_aOutdatedNotifier;
  }

  /**
   * Set the outdated key notifier to be invoked when an old configuration key was used.
   *
   * @param aOutdatedNotifier
   *        The outdated key identifier to be used. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final ConfigWithFallback setOutdatedNotifier (@NonNull final IConfigKeyOutdatedNotifier aOutdatedNotifier)
  {
    ValueEnforcer.notNull (aOutdatedNotifier, "OutdatedNotifier");
    m_aOutdatedNotifier = aOutdatedNotifier;
    return this;
  }

  @Nullable
  public ConfiguredValue getConfiguredValueOrFallback (@NonNull final String sPrimary,
                                                       @NonNull final String... aOldOnes)
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
  public String getAsStringOrFallback (@NonNull final String sPrimary, @NonNull final String... aOldOnes)
  {
    String ret = getAsString (sPrimary);
    if (StringHelper.isEmpty (ret))
    {
      // Try the old names
      for (final String sOld : aOldOnes)
      {
        ret = getAsString (sOld);
        if (StringHelper.isNotEmpty (ret))
        {
          // Notify on old name usage
          m_aOutdatedNotifier.onOutdatedConfigurationKey (sOld, sPrimary);
          break;
        }
      }
    }
    return ret;
  }

  public char @Nullable [] getAsCharArrayOrFallback (@NonNull final String sPrimary, @NonNull final String... aOldOnes)
  {
    char [] ret = getAsCharArray (sPrimary);
    if (ret == null)
    {
      // Try the old names
      for (final String sOld : aOldOnes)
      {
        ret = getAsCharArray (sOld);
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
  public BigDecimal getAsBigDecimalOrFallback (@NonNull final String sPrimary, @NonNull final String... aOldOnes)
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

  public int getAsIntOrFallback (@NonNull final String sPrimary,
                                 final int nBogus,
                                 final int nDefault,
                                 @NonNull final String... aOldOnes)
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

  public long getAsLongOrFallback (@NonNull final String sPrimary,
                                   final long nBogus,
                                   final long nDefault,
                                   @NonNull final String... aOldOnes)
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
