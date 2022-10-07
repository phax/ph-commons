/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.helger.config;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.URLResource;
import com.helger.commons.io.resourceprovider.ClassPathResourceProvider;
import com.helger.commons.io.resourceprovider.FileSystemResourceProvider;
import com.helger.commons.io.resourceprovider.ReadableResourceProviderChain;
import com.helger.commons.lang.ClassLoaderHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.URLHelper;
import com.helger.config.source.EConfigSourceType;
import com.helger.config.source.MultiConfigurationValueProvider;
import com.helger.config.source.envvar.ConfigurationSourceEnvVar;
import com.helger.config.source.res.ConfigurationSourceJson;
import com.helger.config.source.res.ConfigurationSourceProperties;
import com.helger.config.source.res.EConfigSourceResourceType;
import com.helger.config.source.sysprop.ConfigurationSourceSystemProperty;

/**
 * Factory for creating {@link IConfig} objects.
 *
 * @author Philip Helger
 */
@Immutable
public final class ConfigFactory
{
  public static final String PRIVATE_APPLICATION_JSON_NAME = "private-application.json";
  public static final int PRIVATE_APPLICATION_JSON_PRIORITY = EConfigSourceType.RESOURCE.getDefaultPriority () - 5;

  public static final String PRIVATE_APPLICATION_PROPERTIES_NAME = "private-application.properties";
  public static final int PRIVATE_APPLICATION_PROPERTIES_PRIORITY = EConfigSourceType.RESOURCE.getDefaultPriority () -
                                                                    10;

  public static final String APPLICATION_JSON_NAME = "application.json";
  public static final int APPLICATION_JSON_PRIORITY = EConfigSourceType.RESOURCE.getDefaultPriority () - 15;

  public static final String APPLICATION_PROPERTIES_NAME = "application.properties";
  public static final int APPLICATION_PROPERTIES_PRIORITY = EConfigSourceType.RESOURCE.getDefaultPriority () - 20;

  public static final String REFERENCE_PROPERTIES_NAME = "reference.properties";
  public static final int REFERENCE_PROPERTIES_PRIORITY = 1;

  private static final Logger LOGGER = LoggerFactory.getLogger (ConfigFactory.class);
  private static final EConfigSourceResourceType FALLBACK_SOURCE_TYPE = EConfigSourceResourceType.PROPERTIES;

  /**
   * Use this configuration internally to resolve the properties used for the
   * default instance. The "system only" resolver considers system properties
   * and environment variables only.<br>
   * Initialization order is critical - this one must be first.
   */
  private static final IConfig SYSTEM_ONLY = Config.create (createValueProviderSystemOnly ());
  private static final IConfig DEFAULT_INSTANCE = Config.create (createDefaultValueProvider ());

  static
  {
    final int nResourceBased = DEFAULT_INSTANCE.getResourceBasedConfigurationValueProviderCount ();
    if (nResourceBased == 0)
    {
      // Small consistency check
      LOGGER.info ("The default Config instance is solely based on system properties and environment variables. No configuration resources were found.");
    }
  }

  /**
   * Create a configuration value provider, with the following configuration
   * sources:
   * <ul>
   * <li>Query system properties - names are takes "as are" - priority 400.</li>
   * <li>Environment variables. All non-alphanumeric characters are replaced
   * with underscores. So e.g. the property 'a.b' resolves to the environment
   * variable "A_B" - priority 300</li>
   * </ul>
   * Usually this is not what you are looking for but
   * {@link #createDefaultValueProvider()}
   *
   * @return A new value provider. Never <code>null</code>.
   * @since 9.4.5
   * @see #createDefaultValueProvider()
   */
  @Nonnull
  public static MultiConfigurationValueProvider createValueProviderSystemOnly ()
  {
    final MultiConfigurationValueProvider aMCSVP = new MultiConfigurationValueProvider ();
    // Prio 400
    aMCSVP.addConfigurationSource (new ConfigurationSourceSystemProperty ());
    // Prio 300
    aMCSVP.addConfigurationSource (new ConfigurationSourceEnvVar ());
    return aMCSVP;
  }

  /**
   * @return A new instance of {@link ReadableResourceProviderChain} used to
   *         resolve resource based configuration items.
   * @since 9.4.8
   */
  @Nonnull
  public static ReadableResourceProviderChain createDefaultResourceProviderChain ()
  {
    return new ReadableResourceProviderChain (new FileSystemResourceProvider ().setCanReadRelativePaths (true),
                                              new ClassPathResourceProvider ());
  }

  /**
   * Create a configuration value provider, with the following configuration
   * sources:
   * <ul>
   * <li>Query system properties - names are takes "as are" - priority 400.</li>
   * <li>Environment variables. All non-alphanumeric characters are replaced
   * with underscores. So e.g. the property 'a.b' resolves to the environment
   * variable "A_B" - priority 300</li>
   * <li>If a system property <code>config.resource</code> exists and it points
   * to an existing classpath resource, the first one matching is used -
   * priority 200 or determined by system property
   * <code>config.resource.priority</code> (since v9.4.5)</li>
   * <li>If a system property <code>config.resources</code> (note the trailing
   * "s") exists and it points to an existing classpath resource, all matching
   * ones are used - priority 200 or determined by system property
   * <code>config.resources.priority</code> (also see the "s") (since
   * v9.4.5)</li>
   * <li>If a system property <code>config.file</code> exists and it points to
   * an existing file, it is used - priority 200 or determined by system
   * property <code>config.file.priority</code> (since v9.4.5)</li>
   * <li>If a system property <code>config.url</code> exists and it points to an
   * existing URL, it is used - priority 200 or determined by system property
   * <code>config.url.priority</code> (since v9.4.5)</li>
   * <li>A file or classpath entry with the name
   * <code>private-application.json</code> - priority 195</li>
   * <li>A file or classpath entry with the name
   * <code>private-application.properties</code> - priority 190</li>
   * <li>Than all instances of <code>application.json</code> are looked up on
   * the classpath (only!). All of them share priority 185</li>
   * <li>Than all instances of <code>application.properties</code> are looked up
   * on the classpath (only!). All of them share priority 180</li>
   * <li>Than all instances of <code>reference.properties</code> are looked up
   * on the classpath (only!). All of them share priority 1</li>
   * </ul>
   * <p>
   * <b>Note:</b> the default configuration does NOT contain any custom
   * configuration files.
   * </p>
   * <p>
   * <b>Note:</b> JSON and Properties files are expected to be UTF-8 encoded.
   * </p>
   *
   * @return A new value provider. Never <code>null</code>.
   */
  @Nonnull
  public static MultiConfigurationValueProvider createDefaultValueProvider ()
  {
    final MultiConfigurationValueProvider aMCSVP = new MultiConfigurationValueProvider ();
    // Prio 400
    aMCSVP.addConfigurationSource (new ConfigurationSourceSystemProperty ());
    // Prio 300
    aMCSVP.addConfigurationSource (new ConfigurationSourceEnvVar ());

    final int nResourceDefaultPrio = EConfigSourceType.RESOURCE.getDefaultPriority ();
    final ClassLoader aCL = ClassLoaderHelper.getDefaultClassLoader ();

    // Prio 200 - external files
    {
      // Load one only
      final String sConfigResource = SYSTEM_ONLY.getAsString ("config.resource");
      if (StringHelper.hasText (sConfigResource))
      {
        final EConfigSourceResourceType eResType = EConfigSourceResourceType.getFromExtensionOrDefault (FilenameHelper.getExtension (sConfigResource),
                                                                                                        FALLBACK_SOURCE_TYPE);
        final ClassPathResource aRes = new ClassPathResource (sConfigResource);
        if (aRes.exists ())
        {
          // Take priority from system property
          final int nPriority = SYSTEM_ONLY.getAsInt ("config.resource.priority", nResourceDefaultPrio);
          aMCSVP.addConfigurationSource (eResType.createConfigurationSource (aRes), nPriority);
        }
      }
    }

    {
      // Load all
      final String sConfigResources = SYSTEM_ONLY.getAsString ("config.resources");
      if (StringHelper.hasText (sConfigResources))
      {
        // Take priority from system property
        final int nPriority = SYSTEM_ONLY.getAsInt ("config.resources.priority", nResourceDefaultPrio);
        final EConfigSourceResourceType eResType = EConfigSourceResourceType.getFromExtensionOrDefault (FilenameHelper.getExtension (sConfigResources),
                                                                                                        FALLBACK_SOURCE_TYPE);
        // Classpath only
        aMCSVP.addConfigurationSource (MultiConfigurationValueProvider.createForClassPath (aCL,
                                                                                           sConfigResources,
                                                                                           aURL -> eResType.createConfigurationSource (new URLResource (aURL))),
                                       nPriority);
      }
    }

    {
      final String sConfigFile = SYSTEM_ONLY.getAsString ("config.file");
      if (StringHelper.hasText (sConfigFile))
      {
        final EConfigSourceResourceType eResType = EConfigSourceResourceType.getFromExtensionOrDefault (FilenameHelper.getExtension (sConfigFile),
                                                                                                        FALLBACK_SOURCE_TYPE);
        final FileSystemResource aRes = new FileSystemResource (sConfigFile);
        if (aRes.exists ())
        {
          // Take priority from system property
          final int nPriority = SYSTEM_ONLY.getAsInt ("config.file.priority", nResourceDefaultPrio);
          aMCSVP.addConfigurationSource (eResType.createConfigurationSource (aRes), nPriority);
        }
      }
    }

    {
      final String sConfigURL = SYSTEM_ONLY.getAsString ("config.url");
      if (StringHelper.hasText (sConfigURL))
      {
        final EConfigSourceResourceType eResType = EConfigSourceResourceType.getFromExtensionOrDefault (FilenameHelper.getExtension (sConfigURL),
                                                                                                        FALLBACK_SOURCE_TYPE);
        final URL aURL = URLHelper.getAsURL (sConfigURL);
        if (aURL != null)
        {
          final URLResource aRes = new URLResource (aURL);
          if (aRes.exists ())
          {
            // Take priority from system property
            final int nPriority = SYSTEM_ONLY.getAsInt ("config.url.priority", nResourceDefaultPrio);
            aMCSVP.addConfigurationSource (eResType.createConfigurationSource (aRes), nPriority);
          }
        }
      }
    }

    // Prio 195, incl. files
    aMCSVP.addConfigurationSource (MultiConfigurationValueProvider.createForAllOccurrances (aCL,
                                                                                            PRIVATE_APPLICATION_JSON_NAME,
                                                                                            aURL -> new ConfigurationSourceJson (PRIVATE_APPLICATION_JSON_PRIORITY,
                                                                                                                                 new URLResource (aURL),
                                                                                                                                 StandardCharsets.UTF_8),
                                                                                            true),
                                   PRIVATE_APPLICATION_JSON_PRIORITY);

    // Prio 190, incl. files
    aMCSVP.addConfigurationSource (MultiConfigurationValueProvider.createForAllOccurrances (aCL,
                                                                                            PRIVATE_APPLICATION_PROPERTIES_NAME,
                                                                                            aURL -> new ConfigurationSourceProperties (PRIVATE_APPLICATION_PROPERTIES_PRIORITY,
                                                                                                                                       new URLResource (aURL),
                                                                                                                                       StandardCharsets.UTF_8),
                                                                                            true),
                                   PRIVATE_APPLICATION_PROPERTIES_PRIORITY);

    // Prio 185, incl. files
    aMCSVP.addConfigurationSource (MultiConfigurationValueProvider.createForAllOccurrances (aCL,
                                                                                            APPLICATION_JSON_NAME,
                                                                                            aURL -> new ConfigurationSourceJson (APPLICATION_JSON_PRIORITY,
                                                                                                                                 new URLResource (aURL),
                                                                                                                                 StandardCharsets.UTF_8),
                                                                                            true),
                                   APPLICATION_JSON_PRIORITY);

    // Prio 180, incl. files
    aMCSVP.addConfigurationSource (MultiConfigurationValueProvider.createForAllOccurrances (aCL,
                                                                                            APPLICATION_PROPERTIES_NAME,
                                                                                            aURL -> new ConfigurationSourceProperties (APPLICATION_PROPERTIES_PRIORITY,
                                                                                                                                       new URLResource (aURL),
                                                                                                                                       StandardCharsets.UTF_8),
                                                                                            true),
                                   APPLICATION_PROPERTIES_PRIORITY);

    // Prio 1
    aMCSVP.addConfigurationSource (MultiConfigurationValueProvider.createForAllOccurrances (aCL,
                                                                                            REFERENCE_PROPERTIES_NAME,
                                                                                            aURL -> new ConfigurationSourceProperties (REFERENCE_PROPERTIES_PRIORITY,
                                                                                                                                       new URLResource (aURL),
                                                                                                                                       StandardCharsets.UTF_8),
                                                                                            true),
                                   REFERENCE_PROPERTIES_PRIORITY);
    return aMCSVP;
  }

  private ConfigFactory ()
  {}

  /**
   * Get a special {@link IConfig} instance that is only intended to be used to
   * setup other config instances. Usually you are looking for
   * {@link #getDefaultConfig()}.
   *
   * @return The system configuration to be used. Never <code>null</code>.
   * @see #getDefaultConfig()
   * @since 9.4.5
   */
  @Nonnull
  public static IConfig getSystemConfig ()
  {
    return SYSTEM_ONLY;
  }

  /**
   * Get the default {@link IConfig} instance. This call has linear effort. See
   * {@link #createDefaultValueProvider()} on the resolutions that are
   * attempted.
   *
   * @return The default configuration to be used. Never <code>null</code>.
   * @see #createDefaultValueProvider()
   */
  @Nonnull
  public static IConfig getDefaultConfig ()
  {
    return DEFAULT_INSTANCE;
  }
}
