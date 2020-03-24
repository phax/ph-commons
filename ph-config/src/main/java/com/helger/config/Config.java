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

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.StringHelper;
import com.helger.config.source.IConfigurationValueProvider;

/**
 * Default implementation of {@link IConfig}. It is recommended to use
 * {@link ConfigFactory} for accessing {@link IConfig} objects.
 * 
 * @author Philip Helger
 */
public class Config implements IConfig
{
  private final Function <String, Object> m_aResolver;

  public Config (@Nonnull final Function <String, Object> aResolver)
  {
    ValueEnforcer.notNull (aResolver, "Resolver");
    m_aResolver = aResolver;
  }

  @Nullable
  public Object getValue (@Nullable final String sKey)
  {
    if (StringHelper.hasNoText (sKey))
      return null;
    return m_aResolver.apply (sKey);
  }

  @Nonnull
  public static Config create (@Nonnull final IConfigurationValueProvider aCVP)
  {
    ValueEnforcer.notNull (aCVP, "CVP");
    return new Config (aCVP::getConfigurationValue);
  }
}
