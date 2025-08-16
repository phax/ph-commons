/*
 * Copyright (C) 2015-2025 Philip Helger (www.helger.com)
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
package com.helger.config.json.spi;

import java.nio.charset.StandardCharsets;

import com.helger.annotation.style.IsSPIImplementation;
import com.helger.config.json.source.ConfigurationSourceJson;
import com.helger.config.source.resource.type.ConfigurationSourceResourceTypeRegistry;
import com.helger.config.source.resource.type.IConfigurationSourceResourceTypeRegistrarSPI;

import jakarta.annotation.Nonnull;

/**
 * SPI implementation that registers the JSON source type for reading configuration items.
 *
 * @author Philip Helger
 */
@IsSPIImplementation
public class JsonConfigurationSourceResourceTypeRegistrarSPI implements IConfigurationSourceResourceTypeRegistrarSPI
{
  public void registerResourceType (@Nonnull final ConfigurationSourceResourceTypeRegistry aRegistry)
  {
    aRegistry.register (ConfigurationSourceJson.FILE_EXT, x -> new ConfigurationSourceJson (x, StandardCharsets.UTF_8));
  }
}
