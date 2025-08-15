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
package com.helger.config.source.res;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import com.helger.annotation.Nonempty;
import com.helger.base.string.StringHelper;
import com.helger.io.resource.IReadableResource;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Defines the type of configuration source resources.
 *
 * @author Philip Helger
 * @since 9.4.5
 */
public enum EConfigSourceResourceType
{
  JSON ("json", x -> new ConfigurationSourceJson (x, StandardCharsets.UTF_8)),
  PROPERTIES ("properties", x -> new ConfigurationSourceProperties (x, StandardCharsets.UTF_8));

  private final String m_sExt;
  private final Function <IReadableResource, AbstractConfigurationSourceResource> m_aFactory;

  EConfigSourceResourceType (@Nonnull @Nonempty final String sExt,
                             @Nonnull final Function <IReadableResource, AbstractConfigurationSourceResource> aFactory)
  {
    m_sExt = sExt;
    m_aFactory = aFactory;
  }

  /**
   * @return The extension to be used. This does NOT contain the dot.
   */
  @Nonnull
  @Nonempty
  public String getExtension ()
  {
    return m_sExt;
  }

  @Nonnull
  @Nonempty
  public AbstractConfigurationSourceResource createConfigurationSource (@Nonnull final IReadableResource aRes)
  {
    return m_aFactory.apply (aRes);
  }

  @Nullable
  public static EConfigSourceResourceType getFromExtensionOrNull (@Nullable final String sExt)
  {
    return getFromExtensionOrDefault (sExt, null);
  }

  @Nullable
  public static EConfigSourceResourceType getFromExtensionOrDefault (@Nullable final String sExt,
                                                                     @Nullable final EConfigSourceResourceType eDefault)
  {
    if (StringHelper.isNotEmpty (sExt))
      for (final EConfigSourceResourceType e : values ())
        if (e.m_sExt.equalsIgnoreCase (sExt))
          return e;
    return eDefault;
  }
}
