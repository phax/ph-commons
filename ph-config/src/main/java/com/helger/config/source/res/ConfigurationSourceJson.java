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
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.string.ToStringGenerator;
import com.helger.config.source.IConfigurationSource;
import com.helger.json.IJson;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.serialize.JsonReader;

/**
 * Implementation of {@link IConfigurationSource} for properties file based
 * configuration sources.
 *
 * @author Philip Helger
 */
@Immutable
public class ConfigurationSourceJson extends AbstractConfigurationSourceResource
{
  public static final char LEVEL_SEPARATOR = '.';
  public static final String ARRAY_SUFFIX_COUNT = "$count";

  private final ICommonsOrderedMap <String, String> m_aProps;

  private static void _recursiveFlattenJson (@Nonnull final String sNamePrefix,
                                             @Nonnull final IJson aJson,
                                             @Nonnull final Map <String, String> aTarget)
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

  /**
   * Constructor with default priority and default charset
   *
   * @param aRes
   *        Resource to read from. May not be <code>null</code>.
   */
  public ConfigurationSourceJson (@Nonnull final IReadableResource aRes)
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
  public ConfigurationSourceJson (@Nonnull final IReadableResource aRes, @Nullable final Charset aCharset)
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
  public ConfigurationSourceJson (final int nPriority, @Nonnull final IReadableResource aRes)
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
                                  @Nonnull final IReadableResource aRes,
                                  @Nullable final Charset aCharset)
  {
    super (nPriority, aRes);
    final JsonReader.Builder aBuilder = JsonReader.builder ()
                                                  .setSource (aRes,
                                                              aCharset != null ? aCharset : JsonReader.DEFAULT_CHARSET)
                                                  .setCustomizeCallback (aParser -> {
                                                    aParser.setRequireStringQuotes (false);
                                                    aParser.setAlwaysUseBigNumber (true);
                                                  });
    final IJsonObject aProps = aBuilder.hasSource () ? aBuilder.readAsObject () : null;
    if (aProps != null)
    {
      m_aProps = new CommonsLinkedHashMap <> ();
      for (final Map.Entry <String, IJson> aEntry : aProps)
        _recursiveFlattenJson (aEntry.getKey (), aEntry.getValue (), m_aProps);
    }
    else
      m_aProps = null;
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
