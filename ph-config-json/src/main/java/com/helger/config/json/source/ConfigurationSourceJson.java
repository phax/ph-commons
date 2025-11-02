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
package com.helger.config.json.source;

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
import com.helger.base.state.ESuccess;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.config.source.IConfigurationSource;
import com.helger.config.source.resource.AbstractConfigurationSourceResource;
import com.helger.config.value.ConfiguredValue;
import com.helger.io.resource.IReadableResource;
import com.helger.json.IJson;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.serialize.JsonReader;

/**
 * Implementation of {@link IConfigurationSource} for properties file based configuration
 * sources.<br>
 * If you want to use this class, you need to add the dependency "com.helger.commons:ph-json".
 *
 * @author Philip Helger
 */
@ThreadSafe
public class ConfigurationSourceJson extends AbstractConfigurationSourceResource
{
  public static final String FILE_EXT = "json";
  public static final char LEVEL_SEPARATOR = '.';
  public static final String ARRAY_SUFFIX_COUNT = "$count";

  private static final Logger LOGGER = LoggerFactory.getLogger (ConfigurationSourceJson.class);

  private final Charset m_aCharset;
  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  private ICommonsOrderedMap <String, String> m_aProps;

  private static void _recursiveFlattenJson (@NonNull final String sNamePrefix,
                                             @NonNull final IJson aJson,
                                             @NonNull final Map <String, String> aTarget)
  {
    if (aJson.isValue ())
      aTarget.put (sNamePrefix, aJson.getAsValue ().getAsString ());
    else
      if (aJson.isObject ())
      {
        for (final Map.Entry <String, IJson> aEntry : aJson.getAsObject ())
          _recursiveFlattenJson (sNamePrefix + LEVEL_SEPARATOR + aEntry.getKey (), aEntry.getValue (), aTarget);
      }
      else
      {
        final IJsonArray aArray = aJson.getAsArray ();
        aTarget.put (sNamePrefix + LEVEL_SEPARATOR + ARRAY_SUFFIX_COUNT, Integer.toString (aArray.size ()));
        int nIndex = 0;
        for (final IJson aEntry : aArray)
        {
          _recursiveFlattenJson (sNamePrefix + LEVEL_SEPARATOR + nIndex, aEntry, aTarget);
          nIndex++;
        }
      }
  }

  @Nullable
  private static ICommonsOrderedMap <String, String> _load (@NonNull final IReadableResource aRes,
                                                            @NonNull final Charset aCharset)
  {
    final JsonReader.JsonBuilder aBuilder = JsonReader.builder ()
                                                      .source (aRes, aCharset)
                                                      .requireStringQuotes (false)
                                                      .allowSpecialCharsInStrings (true)
                                                      .alwaysUseBigNumber (true)
                                                      .trackPosition (true)
                                                      .customExceptionCallback (ex -> LOGGER.error ("Failed to parse '" +
                                                                                                    aRes.getPath () +
                                                                                                    "' to JSON: " +
                                                                                                    ex.getMessage ()));
    final IJsonObject aProps = aBuilder.hasSource () ? aBuilder.readAsObject () : null;
    if (aProps == null)
      return null;

    final ICommonsOrderedMap <String, String> ret = new CommonsLinkedHashMap <> ();
    for (final Map.Entry <String, IJson> aEntry : aProps)
      _recursiveFlattenJson (aEntry.getKey (), aEntry.getValue (), ret);
    return ret;
  }

  /**
   * Constructor with default priority and default charset
   *
   * @param aRes
   *        Resource to read from. May not be <code>null</code>.
   */
  public ConfigurationSourceJson (@NonNull final IReadableResource aRes)
  {
    this (CONFIG_SOURCE_TYPE.getDefaultPriority (), aRes, (Charset) null);
  }

  /**
   * Constructor with default priority
   *
   * @param aRes
   *        Resource to read from. May not be <code>null</code>.
   * @param aCharset
   *        Character set to use. May be <code>null</code>.
   */
  public ConfigurationSourceJson (@NonNull final IReadableResource aRes, @Nullable final Charset aCharset)
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
  public ConfigurationSourceJson (final int nPriority, @NonNull final IReadableResource aRes)
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
  public ConfigurationSourceJson (final int nPriority,
                                  @NonNull final IReadableResource aRes,
                                  @Nullable final Charset aCharset)
  {
    super (nPriority, aRes);
    m_aCharset = aCharset != null ? aCharset : JsonReader.DEFAULT_CHARSET;
    m_aProps = _load (aRes, m_aCharset);

    // Consistency check
    if (m_aProps != null)
      for (final Map.Entry <String, String> aEntry : m_aProps.entrySet ())
        if (hasTrailingWhitespace (aEntry.getValue ()))
          LOGGER.warn ("The value of the JSON configuration property '" +
                       aEntry.getKey () +
                       "' has a trailing whitespace. This may lead to unintended side effects.");
  }

  /**
   * @return The charset used to load the JSON. Never <code>null</code>.
   */
  @NonNull
  public final Charset getCharset ()
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
    final ICommonsOrderedMap <String, String> aProps = _load (getResource (), m_aCharset);
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
