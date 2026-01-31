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
package com.helger.config.source.resource.type;

import java.nio.charset.StandardCharsets;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.style.IsSPIImplementation;
import com.helger.config.source.resource.properties.ConfigurationSourceProperties;

/**
 * The default SPI implementation that registers the properties resource type
 *
 * @author Philip Helger
 */
@IsSPIImplementation
public class DefaultConfigurationSourceResourceTypeRegistrarSPI implements IConfigurationSourceResourceTypeRegistrarSPI
{
  public void registerResourceType (@NonNull final ConfigurationSourceResourceTypeRegistry aRegistry)
  {
    aRegistry.register (ConfigurationSourceProperties.FILE_EXT,
                        x -> new ConfigurationSourceProperties (x, StandardCharsets.UTF_8));
  }
}
