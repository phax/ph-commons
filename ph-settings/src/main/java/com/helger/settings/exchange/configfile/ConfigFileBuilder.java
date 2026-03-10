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
package com.helger.settings.exchange.configfile;

import java.io.InputStream;
import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsImmutableObject;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.base.system.SystemProperties;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsIterable;
import com.helger.collection.commons.ICommonsList;
import com.helger.io.resource.IReadableResource;
import com.helger.io.resourceprovider.DefaultResourceProvider;
import com.helger.io.resourceprovider.IReadableResourceProvider;
import com.helger.settings.ISettings;
import com.helger.settings.exchange.ISettingsPersistence;
import com.helger.settings.exchange.properties.SettingsPersistenceProperties;

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

  /**
   * Set the settings persistence to use.
   *
   * @param aSPP
   *        The settings persistence provider. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public ConfigFileBuilder settingsPersistence (@NonNull final ISettingsPersistence aSPP)
  {
    ValueEnforcer.notNull (aSPP, "SPP");
    m_aSPP = aSPP;
    return this;
  }

  /**
   * @return The current settings persistence provider. Never <code>null</code>.
   */
  @NonNull
  public ISettingsPersistence settingsPersistence ()
  {
    return m_aSPP;
  }

  /**
   * Set the resource provider to use for resolving config file paths.
   *
   * @param aResProvider
   *        The resource provider. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public ConfigFileBuilder resourceProvider (@NonNull final IReadableResourceProvider aResProvider)
  {
    ValueEnforcer.notNull (aResProvider, "ResProvider");
    m_aResProvider = aResProvider;
    return this;
  }

  /**
   * @return The current resource provider. Never <code>null</code>.
   */
  @NonNull
  public IReadableResourceProvider resourceProvider ()
  {
    return m_aResProvider;
  }

  /**
   * Add a configuration file path from a system property value.
   *
   * @param sSystemPropertyName
   *        The system property name to read the path from. May neither be
   *        <code>null</code> nor empty.
   * @return this for chaining
   */
  @NonNull
  public ConfigFileBuilder addPathFromSystemProperty (@NonNull @Nonempty final String sSystemPropertyName)
  {
    ValueEnforcer.notEmpty (sSystemPropertyName, "SystemPropertyName");
    return addPath ( () -> SystemProperties.getPropertyValueOrNull (sSystemPropertyName));
  }

  /**
   * Add a configuration file path from an environment variable value.
   *
   * @param sEnvVarName
   *        The environment variable name to read the path from. May neither be
   *        <code>null</code> nor empty.
   * @return this for chaining
   */
  @NonNull
  public ConfigFileBuilder addPathFromEnvVar (@NonNull @Nonempty final String sEnvVarName)
  {
    ValueEnforcer.notEmpty (sEnvVarName, "EnvVarName");
    return addPath ( () -> System.getenv ().get (sEnvVarName));
  }

  /**
   * Add a configuration file path from a supplier.
   *
   * @param aSupplier
   *        The supplier providing the path. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public ConfigFileBuilder addPath (@NonNull final Supplier <? extends String> aSupplier)
  {
    ValueEnforcer.notNull (aSupplier, "Supplier");
    return addPath (aSupplier.get ());
  }

  /**
   * Add a configuration file path.
   *
   * @param sConfigPath
   *        The path to add. May be <code>null</code> or empty in which case it
   *        is ignored.
   * @return this for chaining
   */
  @NonNull
  public ConfigFileBuilder addPath (@Nullable final String sConfigPath)
  {
    if (StringHelper.isNotEmpty (sConfigPath))
      m_aPaths.add (sConfigPath);
    return this;
  }

  /**
   * Add multiple configuration file paths.
   *
   * @param aConfigPaths
   *        The paths to add. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public ConfigFileBuilder addPaths (@NonNull final String... aConfigPaths)
  {
    ValueEnforcer.notNullNoNullValue (aConfigPaths, "ConfigPaths");
    for (final String sPath : aConfigPaths)
      addPath (sPath);
    return this;
  }

  /**
   * Add multiple configuration file paths.
   *
   * @param aConfigPaths
   *        The paths to add. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public ConfigFileBuilder addPaths (@NonNull final Iterable <String> aConfigPaths)
  {
    ValueEnforcer.notNullNoNullValue (aConfigPaths, "ConfigPaths");
    for (final String sPath : aConfigPaths)
      addPath (sPath);
    return this;
  }

  /**
   * Set a single configuration file path, replacing any previously set paths.
   *
   * @param sConfigPath
   *        The path to set. May neither be <code>null</code> nor empty.
   * @return this for chaining
   */
  @NonNull
  public ConfigFileBuilder path (@NonNull @Nonempty final String sConfigPath)
  {
    ValueEnforcer.notEmpty (sConfigPath, "ConfigPath");
    m_aPaths.set (sConfigPath);
    return this;
  }

  /**
   * Set multiple configuration file paths, replacing any previously set paths.
   *
   * @param aConfigPaths
   *        The paths to set. May neither be <code>null</code> nor empty.
   * @return this for chaining
   */
  @NonNull
  public ConfigFileBuilder paths (@NonNull @Nonempty final String... aConfigPaths)
  {
    ValueEnforcer.notEmptyNoNullValue (aConfigPaths, "ConfigPaths");
    m_aPaths.setAll (aConfigPaths);
    return this;
  }

  /**
   * Set multiple configuration file paths, replacing any previously set paths.
   *
   * @param aConfigPaths
   *        The paths to set. May neither be <code>null</code> nor empty.
   * @return this for chaining
   */
  @NonNull
  public ConfigFileBuilder paths (@NonNull @Nonempty final Iterable <String> aConfigPaths)
  {
    ValueEnforcer.notEmptyNoNullValue (aConfigPaths, "ConfigPaths");
    m_aPaths.setAll (aConfigPaths);
    return this;
  }

  /**
   * @return A mutable copy of all currently configured paths. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllPaths ()
  {
    return m_aPaths.getClone ();
  }

  /**
   * @return An immutable iterable over all currently configured paths. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsImmutableObject
  public ICommonsIterable <String> paths ()
  {
    return m_aPaths;
  }

  /** {@inheritDoc} */
  @NonNull
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
