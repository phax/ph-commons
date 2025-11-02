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
package com.helger.config.source.resource.properties;

import java.nio.charset.Charset;
import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.rt.NonBlockingProperties;
import com.helger.base.state.ESuccess;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.config.source.IConfigurationSource;
import com.helger.config.source.resource.AbstractConfigurationSourceResource;
import com.helger.config.value.ConfiguredValue;
import com.helger.io.resource.IReadableResource;
import com.helger.io.rt.PropertiesLoader;

/**
 * Implementation of {@link IConfigurationSource} for properties file based configuration sources.
 *
 * @author Philip Helger
 */
@ThreadSafe
public class ConfigurationSourceProperties extends AbstractConfigurationSourceResource
{
  public static final String FILE_EXT = "properties";
  private static final Logger LOGGER = LoggerFactory.getLogger (ConfigurationSourceProperties.class);

  private final Charset m_aCharset;
  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  private NonBlockingProperties m_aProps;

  @Nullable
  private static NonBlockingProperties _load (@NonNull final IReadableResource aRes, @Nullable final Charset aCharset)
  {
    if (aCharset == null)
      return PropertiesLoader.loadProperties (aRes);
    return PropertiesLoader.loadProperties (aRes, aCharset);
  }

  /**
   * Constructor with default priority and default charset
   *
   * @param aRes
   *        Resource to read from. May not be <code>null</code>.
   */
  public ConfigurationSourceProperties (@NonNull final IReadableResource aRes)
  {
    this (CONFIG_SOURCE_TYPE.getDefaultPriority (), aRes, null);
  }

  /**
   * Constructor with default priority
   *
   * @param aRes
   *        Resource to read from. May not be <code>null</code>.
   * @param aCharset
   *        Character set to use. May be <code>null</code>.
   */
  public ConfigurationSourceProperties (@NonNull final IReadableResource aRes, @Nullable final Charset aCharset)
  {
    this (CONFIG_SOURCE_TYPE.getDefaultPriority (), aRes, aCharset);
  }

  /**
   * Constructor with default charset
   *
   * @param nPriority
   *        Configuration source priority.
   * @param aRes
   *        Resource to read from. May not be <code>null</code>.
   */
  public ConfigurationSourceProperties (final int nPriority, @NonNull final IReadableResource aRes)
  {
    this (nPriority, aRes, null);
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
  public ConfigurationSourceProperties (final int nPriority,
                                        @NonNull final IReadableResource aRes,
                                        @Nullable final Charset aCharset)
  {
    super (nPriority, aRes);
    m_aCharset = aCharset;
    m_aProps = _load (aRes, aCharset);

    // Consistency check
    if (m_aProps != null)
      for (final Map.Entry <String, String> aEntry : m_aProps.entrySet ())
        if (hasTrailingWhitespace (aEntry.getValue ()))
          LOGGER.warn ("The value of the configuration property '" +
                       aEntry.getKey () +
                       "' has a trailing whitespace. This may lead to unintended side effects.");
  }

  /**
   * @return The charset used to load the properties. May be <code>null</code>.
   */
  @Nullable
  public Charset getCharset ()
  {
    return m_aCharset;
  }

  public boolean isInitializedAndUsable ()
  {
    return m_aRWLock.readLockedBoolean ( () -> m_aProps != null);
  }

  @NonNull
  public ESuccess reload ()
  {
    // Main load
    final NonBlockingProperties aProps = _load (getResource (), m_aCharset);
    // Replace in write-lock
    m_aRWLock.writeLocked ( () -> m_aProps = aProps);
    return ESuccess.valueOf (aProps != null);
  }

  @Nullable
  public ConfiguredValue getConfigurationValue (@NonNull @Nonempty final String sKey)
  {
    final String sValue = m_aRWLock.readLockedGet ( () -> m_aProps == null ? null : m_aProps.get (sKey));
    return sValue == null ? null : new ConfiguredValue (this, sValue);
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsOrderedMap <String, String> getAllConfigItems ()
  {
    return m_aRWLock.readLockedGet ( () -> new CommonsLinkedHashMap <> (m_aProps));
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
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("Properties", mapToStringIgnoreSecrets (m_aProps))
                            .getToString ();
  }
}
