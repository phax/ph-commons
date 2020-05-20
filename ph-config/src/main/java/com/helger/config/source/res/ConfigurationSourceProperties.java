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
package com.helger.config.source.res;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.lang.NonBlockingProperties;
import com.helger.commons.lang.PropertiesHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.config.source.IConfigurationSource;

/**
 * Implementation of {@link IConfigurationSource} for properties file based
 * configuration sources.
 *
 * @author Philip Helger
 */
@Immutable
public class ConfigurationSourceProperties extends AbstractConfigurationSourceResource
{
  private final NonBlockingProperties m_aProps;

  /**
   * Constructor with default priority and default charset
   *
   * @param aRes
   *        Resource to read from. May not be <code>null</code>.
   */
  public ConfigurationSourceProperties (@Nonnull final IReadableResource aRes)
  {
    this (SOURCE_TYPE.getDefaultPriority (), aRes, (Charset) null);
  }

  /**
   * Constructor with default priority
   *
   * @param aRes
   *        Resource to read from. May not be <code>null</code>.
   * @param aCharset
   *        Character set to use. May be <code>null</code>.
   */
  public ConfigurationSourceProperties (@Nonnull final IReadableResource aRes, @Nullable final Charset aCharset)
  {
    this (SOURCE_TYPE.getDefaultPriority (), aRes, aCharset);
  }

  /**
   * Constructor with default charset
   *
   * @param nPriority
   *        Configuration source priority.
   * @param aRes
   *        Resource to read from. May not be <code>null</code>.
   */
  public ConfigurationSourceProperties (final int nPriority, @Nonnull final IReadableResource aRes)
  {
    this (nPriority, aRes, (Charset) null);
  }

  /**
   * Constructor
   *
   * @param nPriority
   *        Configuration source priority.
   * @param aRes
   *        Resource to read from. May not be <code>null</code>.
   * @param aCharset
   *        Character set to use. May be <code>null</code>.
   */
  public ConfigurationSourceProperties (final int nPriority, @Nonnull final IReadableResource aRes, @Nullable final Charset aCharset)
  {
    super (nPriority, aRes);
    m_aProps = aCharset == null ? PropertiesHelper.loadProperties (aRes) : PropertiesHelper.loadProperties (aRes, aCharset);
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

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedMap <String, String> getAllConfigItems ()
  {
    return new CommonsLinkedHashMap <> (m_aProps);
  }

  @Override
  public boolean equals (final Object o)
  {
    // New field, no change
    return super.equals (o);
  }

  @Override
  public int hashCode ()
  {
    // New field, no change
    return super.hashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("Properties", m_aProps).getToString ();
  }
}
