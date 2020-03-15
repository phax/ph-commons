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
package com.helger.config.source.file;

import java.io.File;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.lang.NonBlockingProperties;
import com.helger.commons.lang.PropertiesHelper;
import com.helger.config.source.IConfigurationSource;

/**
 * Implementation of {@link IConfigurationSource} for properties file based
 * configuration sources.
 *
 * @author Philip Helger
 */
public class ConfigurationSourcePropertiesFile extends AbstractConfigurationSourceFile
{
  private final NonBlockingProperties m_aProps;

  public ConfigurationSourcePropertiesFile (@Nonnull final File aFile)
  {
    this (SOURCE_TYPE.getDefaultPriority (), aFile, (Charset) null);
  }

  public ConfigurationSourcePropertiesFile (@Nonnull final File aFile, @Nullable final Charset aCharset)
  {
    this (SOURCE_TYPE.getDefaultPriority (), aFile, aCharset);
  }

  public ConfigurationSourcePropertiesFile (final int nPriority, @Nonnull final File aFile)
  {
    this (nPriority, aFile, (Charset) null);
  }

  /**
   * @param nPriority
   *        Configuration source priority.
   * @param aFile
   *        File to read from. MAy not be <code>null</code>.
   * @param aCharset
   *        Character set to use. May be <code>null</code>.
   */
  public ConfigurationSourcePropertiesFile (final int nPriority,
                                            @Nonnull final File aFile,
                                            @Nullable final Charset aCharset)
  {
    super (nPriority, aFile);
    m_aProps = aCharset == null ? PropertiesHelper.loadProperties (aFile)
                                : PropertiesHelper.loadProperties (aFile, aCharset);
  }

  public boolean isInitializedAndUsable ()
  {
    return m_aProps != null;
  }

  @Nullable
  public String getConfigurationValue (@Nonnull @Nonempty final String sKey)
  {
    return m_aProps == null ? null : m_aProps.get (sKey);
  }
}
