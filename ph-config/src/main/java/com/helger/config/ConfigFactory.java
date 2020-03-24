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

import com.helger.commons.io.resource.URLResource;
import com.helger.commons.lang.ClassLoaderHelper;
import com.helger.config.source.EConfigSourceType;
import com.helger.config.source.MultiConfigurationSourceValueProvider;
import com.helger.config.source.envvar.ConfigurationSourceEnvVar;
import com.helger.config.source.file.ConfigurationSourceJson;
import com.helger.config.source.file.ConfigurationSourceProperties;
import com.helger.config.source.sysprop.ConfigurationSourceSystemProperty;

@Immutable
public final class ConfigFactory
{
  private static final IConfig DEFAULT_INSTANCE = Config.create (createDefaultValueProvider ());

  @Nonnull
  public static MultiConfigurationSourceValueProvider createDefaultValueProvider ()
  {
    final MultiConfigurationSourceValueProvider aMCSVP = new MultiConfigurationSourceValueProvider ();
    // Prio 400
    aMCSVP.addConfigurationSource (new ConfigurationSourceSystemProperty ());
    // Prio 300
    aMCSVP.addConfigurationSource (new ConfigurationSourceEnvVar ());
    // Prio 199
    aMCSVP.addConfigurationSource (MultiConfigurationSourceValueProvider.createForClassPath (ClassLoaderHelper.getDefaultClassLoader (),
                                                                                             "application.json",
                                                                                             aURL -> new ConfigurationSourceJson (new URLResource (aURL),
                                                                                                                                  StandardCharsets.UTF_8)),
                                   EConfigSourceType.RESOURCE.getDefaultPriority () - 1);
    // Prio 198
    aMCSVP.addConfigurationSource (MultiConfigurationSourceValueProvider.createForClassPath (ClassLoaderHelper.getDefaultClassLoader (),
                                                                                             "application.properties",
                                                                                             aURL -> new ConfigurationSourceProperties (new URLResource (aURL),
                                                                                                                                        StandardCharsets.UTF_8)),
                                   EConfigSourceType.RESOURCE.getDefaultPriority () - 2);
    // Prio 1
    aMCSVP.addConfigurationSource (MultiConfigurationSourceValueProvider.createForClassPath (ClassLoaderHelper.getDefaultClassLoader (),
                                                                                             "reference.properties",
                                                                                             aURL -> new ConfigurationSourceProperties (new URLResource (aURL),
                                                                                                                                        StandardCharsets.UTF_8)),
                                   1);
    return aMCSVP;
  }

  private ConfigFactory ()
  {}

  @Nonnull
  public static IConfig getDefaultConfig ()
  {
    return DEFAULT_INSTANCE;
  }
}
