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
package com.helger.settings.exchange.configfile;

import java.io.InputStream;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsImmutableObject;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.builder.IBuilder;
import com.helger.base.enforcer.ValueEnforcer;
import com.helger.base.string.Strings;
import com.helger.base.string.ToStringGenerator;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsIterable;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resourceprovider.DefaultResourceProvider;
import com.helger.commons.io.resourceprovider.IReadableResourceProvider;
import com.helger.commons.system.SystemProperties;
import com.helger.settings.ISettings;
import com.helger.settings.exchange.ISettingsPersistence;
import com.helger.settings.exchange.properties.SettingsPersistenceProperties;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Builder class for {@link ConfigFile} objects. By default this build class reads properties files,
 * and uses a {@link DefaultResourceProvider} to resolve config file paths.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class ConfigFileBuilder implements IBuilder <ConfigFile>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ConfigFileBuilder.class);

  private ISettingsPersistence m_aSPP = new SettingsPersistenceProperties (TrimmedValueSettings::new);
  private IReadableResourceProvider m_aResProvider = new DefaultResourceProvider ();
  private final ICommonsList <String> m_aPaths = new CommonsArrayList <> ();

  public ConfigFileBuilder ()
  {}

  @Nonnull
  public ConfigFileBuilder settingsPersistence (@Nonnull final ISettingsPersistence aSPP)
  {
    ValueEnforcer.notNull (aSPP, "SPP");
    m_aSPP = aSPP;
    return this;
  }

  @Nonnull
  public ISettingsPersistence settingsPersistence ()
  {
    return m_aSPP;
  }

  @Nonnull
  public ConfigFileBuilder resourceProvider (@Nonnull final IReadableResourceProvider aResProvider)
  {
    ValueEnforcer.notNull (aResProvider, "ResProvider");
    m_aResProvider = aResProvider;
    return this;
  }

  @Nonnull
  public IReadableResourceProvider resourceProvider ()
  {
    return m_aResProvider;
  }

  @Nonnull
  public ConfigFileBuilder addPathFromSystemProperty (@Nonnull @Nonempty final String sSystemPropertyName)
  {
    ValueEnforcer.notEmpty (sSystemPropertyName, "SystemPropertyName");
    return addPath ( () -> SystemProperties.getPropertyValueOrNull (sSystemPropertyName));
  }

  @Nonnull
  public ConfigFileBuilder addPathFromEnvVar (@Nonnull @Nonempty final String sEnvVarName)
  {
    ValueEnforcer.notEmpty (sEnvVarName, "EnvVarName");
    return addPath ( () -> System.getenv ().get (sEnvVarName));
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
    if (Strings.isNotEmpty (sConfigPath))
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
  public ConfigFileBuilder path (@Nonnull @Nonempty final String sConfigPath)
  {
    ValueEnforcer.notEmpty (sConfigPath, "ConfigPath");
    m_aPaths.set (sConfigPath);
    return this;
  }

  @Nonnull
  public ConfigFileBuilder paths (@Nonnull @Nonempty final String... aConfigPaths)
  {
    ValueEnforcer.notEmptyNoNullValue (aConfigPaths, "ConfigPaths");
    m_aPaths.setAll (aConfigPaths);
    return this;
  }

  @Nonnull
  public ConfigFileBuilder paths (@Nonnull @Nonempty final Iterable <String> aConfigPaths)
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
  @ReturnsImmutableObject
  public ICommonsIterable <String> paths ()
  {
    return m_aPaths;
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
      LOGGER.warn ("Failed to resolve config file paths: " + m_aPaths);

    return new ConfigFile (aSettings != null ? aRes : null, aSettings);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("SettingsPersistencyProvider", m_aSPP)
                                       .append ("ResourceProvider", m_aResProvider)
                                       .append ("Paths", m_aPaths)
                                       .getToString ();
  }
}
