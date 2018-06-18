/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.settings.exchange.configfile;

import java.io.InputStream;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resourceprovider.DefaultResourceProvider;
import com.helger.commons.io.resourceprovider.IReadableResourceProvider;
import com.helger.commons.string.StringHelper;
import com.helger.commons.system.SystemProperties;
import com.helger.settings.ISettings;
import com.helger.settings.exchange.ISettingsPersistence;
import com.helger.settings.exchange.properties.SettingsPersistenceProperties;

/**
 * Builder class for {@link ConfigFile} objects. By default this build class
 * reads properties files, and uses a {@link DefaultResourceProvider} to resolve
 * config file paths.
 *
 * @author Philip Helger
 */
public class ConfigFileBuilder
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (ConfigFileBuilder.class);

  private ISettingsPersistence m_aSPP = new SettingsPersistenceProperties (TrimmedValueSettings::new);
  private final ICommonsList <String> m_aPaths = new CommonsArrayList <> ();
  private IReadableResourceProvider m_aResProvider = new DefaultResourceProvider ();

  public ConfigFileBuilder ()
  {}

  @Nonnull
  public ConfigFileBuilder setSettingsPersistence (@Nonnull final ISettingsPersistence aSPP)
  {
    ValueEnforcer.notNull (aSPP, "SPP");
    m_aSPP = aSPP;
    return this;
  }

  @Nonnull
  public ISettingsPersistence getSettingsPersistence ()
  {
    return m_aSPP;
  }

  @Nonnull
  public ConfigFileBuilder addPathFromSystemProperty (@Nonnull @Nonempty final String sSystemPropertyName)
  {
    ValueEnforcer.notEmpty (sSystemPropertyName, "SystemPropertyName");
    return addPath ( () -> SystemProperties.getPropertyValueOrNull (sSystemPropertyName));
  }

  @Nonnull
  public ConfigFileBuilder addPath (@Nonnull final Supplier <? extends String> aSupplier)
  {
    ValueEnforcer.notNull (aSupplier, "Supplier");
    return addPath (aSupplier.get ());
  }

  @Nonnull
  public ConfigFileBuilder addPath (@Nullable final String sConfigPath)
  {
    if (StringHelper.hasText (sConfigPath))
      m_aPaths.add (sConfigPath);
    return this;
  }

  @Nonnull
  public ConfigFileBuilder addPaths (@Nonnull final String... aConfigPaths)
  {
    ValueEnforcer.notNullNoNullValue (aConfigPaths, "ConfigPaths");
    for (final String sPath : aConfigPaths)
      addPath (sPath);
    return this;
  }

  @Nonnull
  public ConfigFileBuilder addPaths (@Nonnull final Iterable <String> aConfigPaths)
  {
    ValueEnforcer.notNullNoNullValue (aConfigPaths, "ConfigPaths");
    for (final String sPath : aConfigPaths)
      addPath (sPath);
    return this;
  }

  @Nonnull
  public ConfigFileBuilder setPath (@Nonnull @Nonempty final String sConfigPath)
  {
    ValueEnforcer.notEmpty (sConfigPath, "ConfigPath");
    m_aPaths.set (sConfigPath);
    return this;
  }

  @Nonnull
  public ConfigFileBuilder setPaths (@Nonnull @Nonempty final String... aConfigPaths)
  {
    ValueEnforcer.notEmptyNoNullValue (aConfigPaths, "ConfigPaths");
    m_aPaths.setAll (aConfigPaths);
    return this;
  }

  @Nonnull
  public ConfigFileBuilder setPaths (@Nonnull @Nonempty final Iterable <String> aConfigPaths)
  {
    ValueEnforcer.notEmptyNoNullValue (aConfigPaths, "ConfigPaths");
    m_aPaths.setAll (aConfigPaths);
    return this;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllPaths ()
  {
    return m_aPaths.getClone ();
  }

  @Nonnull
  public ConfigFileBuilder setResourceProvider (@Nonnull final IReadableResourceProvider aResProvider)
  {
    ValueEnforcer.notNull (aResProvider, "ResProvider");
    m_aResProvider = aResProvider;
    return this;
  }

  @Nonnull
  public IReadableResourceProvider getResourceProvider ()
  {
    return m_aResProvider;
  }

  @Nonnull
  public ConfigFile build ()
  {
    if (m_aPaths.isEmpty ())
      throw new IllegalStateException ("No config file path was provided!");

    IReadableResource aRes = null;
    ISettings aSettings = null;
    for (final String sConfigPath : m_aPaths)
    {
      // Support reading?
      if (m_aResProvider.supportsReading (sConfigPath))
      {
        // Convert to resource
        aRes = m_aResProvider.getReadableResource (sConfigPath);
        if (aRes != null)
        {
          // Open stream
          final InputStream aIS = aRes.getInputStream ();
          if (aIS != null)
          {
            // Read settings
            aSettings = m_aSPP.readSettings (aIS);
            if (aSettings != null)
              break;
          }
        }
      }
    }

    if (aSettings == null)
      if (s_aLogger.isWarnEnabled ())
        s_aLogger.warn ("Failed to resolve config file paths: " + m_aPaths);

    return new ConfigFile (aSettings != null ? aRes : null, aSettings);
  }
}
