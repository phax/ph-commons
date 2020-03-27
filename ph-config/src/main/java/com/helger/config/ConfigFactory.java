/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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

import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resource.URLResource;
import com.helger.commons.io.resourceprovider.ClassPathResourceProvider;
import com.helger.commons.io.resourceprovider.FileSystemResourceProvider;
import com.helger.commons.io.resourceprovider.ReadableResourceProviderChain;
import com.helger.commons.lang.ClassLoaderHelper;
import com.helger.config.source.EConfigSourceType;
import com.helger.config.source.MultiConfigurationValueProvider;
import com.helger.config.source.envvar.ConfigurationSourceEnvVar;
import com.helger.config.source.res.ConfigurationSourceJson;
import com.helger.config.source.res.ConfigurationSourceProperties;
import com.helger.config.source.sysprop.ConfigurationSourceSystemProperty;

@Immutable
public final class ConfigFactory
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ConfigFactory.class);
  private static final IConfig DEFAULT_INSTANCE = Config.create (createDefaultValueProvider ());

  static
  {
    final int nResourceBased = DEFAULT_INSTANCE.getResourceBasedConfigurationValueProviderCount ();
    if (nResourceBased == 0)
      LOGGER.warn ("The default Config instance is based soley on system properties and environment variables. No configuration resources were found.");
  }

  @Nonnull
  public static MultiConfigurationValueProvider createDefaultValueProvider ()
  {
    final MultiConfigurationValueProvider aMCSVP = new MultiConfigurationValueProvider ();
    // Prio 400
    aMCSVP.addConfigurationSource (new ConfigurationSourceSystemProperty ());
    // Prio 300
    aMCSVP.addConfigurationSource (new ConfigurationSourceEnvVar ());

    final int nResourceDefaultPrio = EConfigSourceType.RESOURCE.getDefaultPriority ();
    // Use existing ones only
    final ReadableResourceProviderChain aResourceProvider = new ReadableResourceProviderChain (new FileSystemResourceProvider ().setCanReadRelativePaths (true),
                                                                                               new ClassPathResourceProvider ());

    // Prio 195
    IReadableResource aRes = aResourceProvider.getReadableResourceIf ("private-application.json",
                                                                      IReadableResource::exists);
    if (aRes != null)
      aMCSVP.addConfigurationSource (new ConfigurationSourceJson (aRes, StandardCharsets.UTF_8),
                                     nResourceDefaultPrio - 5);
    // Prio 190
    aRes = aResourceProvider.getReadableResourceIf ("private-application.properties", IReadableResource::exists);
    if (aRes != null)
      aMCSVP.addConfigurationSource (new ConfigurationSourceProperties (aRes, StandardCharsets.UTF_8),
                                     nResourceDefaultPrio - 10);
    // Prio 185
    aMCSVP.addConfigurationSource (MultiConfigurationValueProvider.createForClassPath (ClassLoaderHelper.getDefaultClassLoader (),
                                                                                       "application.json",
                                                                                       aURL -> new ConfigurationSourceJson (new URLResource (aURL),
                                                                                                                            StandardCharsets.UTF_8)),
                                   nResourceDefaultPrio - 15);
    // Prio 180
    aMCSVP.addConfigurationSource (MultiConfigurationValueProvider.createForClassPath (ClassLoaderHelper.getDefaultClassLoader (),
                                                                                       "application.properties",
                                                                                       aURL -> new ConfigurationSourceProperties (new URLResource (aURL),
                                                                                                                                  StandardCharsets.UTF_8)),
                                   nResourceDefaultPrio - 20);
    // Prio 1
    aMCSVP.addConfigurationSource (MultiConfigurationValueProvider.createForClassPath (ClassLoaderHelper.getDefaultClassLoader (),
                                                                                       "reference.properties",
                                                                                       aURL -> new ConfigurationSourceProperties (new URLResource (aURL),
                                                                                                                                  StandardCharsets.UTF_8)),
                                   1);
    return aMCSVP;
  }

  private ConfigFactory ()
  {}

  /**
   * Get the default {@link IConfig} instance. This call has linear effort.
   *
   * @return The default configuration to be used. Never <code>null</code>.
   */
  @Nonnull
  public static IConfig getDefaultConfig ()
  {
    return DEFAULT_INSTANCE;
  }
}
