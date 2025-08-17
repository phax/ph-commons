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
package com.helger.config.json;

import java.nio.charset.StandardCharsets;

import com.helger.base.classloader.ClassLoaderHelper;
import com.helger.config.ConfigFactory;
import com.helger.config.json.source.ConfigurationSourceJson;
import com.helger.config.source.EConfigSourceType;
import com.helger.config.source.MultiConfigurationValueProvider;
import com.helger.io.resource.URLResource;

import jakarta.annotation.Nonnull;

public final class ConfigFactoryJson
{
  public static final String PRIVATE_APPLICATION_JSON_NAME = "private-application.json";
  public static final int PRIVATE_APPLICATION_JSON_PRIORITY = EConfigSourceType.RESOURCE.getDefaultPriority () - 5;

  public static final String APPLICATION_JSON_NAME = "application.json";
  public static final int APPLICATION_JSON_PRIORITY = EConfigSourceType.RESOURCE.getDefaultPriority () - 15;

  private ConfigFactoryJson ()
  {}

  public static void addDefaultJsonConfiguration (@Nonnull final MultiConfigurationValueProvider aMVP)
  {
    final ClassLoader aCL = ClassLoaderHelper.getDefaultClassLoader ();

    // Prio 195, incl. files
    aMVP.addConfigurationSource (MultiConfigurationValueProvider.createForAllOccurrances (aCL,
                                                                                          PRIVATE_APPLICATION_JSON_NAME,
                                                                                          aURL -> new ConfigurationSourceJson (PRIVATE_APPLICATION_JSON_PRIORITY,
                                                                                                                               new URLResource (aURL),
                                                                                                                               StandardCharsets.UTF_8),
                                                                                          true),
                                 PRIVATE_APPLICATION_JSON_PRIORITY);

    // Prio 185, incl. files
    aMVP.addConfigurationSource (MultiConfigurationValueProvider.createForAllOccurrances (aCL,
                                                                                          APPLICATION_JSON_NAME,
                                                                                          aURL -> new ConfigurationSourceJson (APPLICATION_JSON_PRIORITY,
                                                                                                                               new URLResource (aURL),
                                                                                                                               StandardCharsets.UTF_8),
                                                                                          true),
                                 APPLICATION_JSON_PRIORITY);
  }

  @Nonnull
  public static MultiConfigurationValueProvider createDefaultValueProvider ()
  {
    final MultiConfigurationValueProvider ret = ConfigFactory.createDefaultValueProvider ();
    addDefaultJsonConfiguration (ret);
    return ret;
  }

}
