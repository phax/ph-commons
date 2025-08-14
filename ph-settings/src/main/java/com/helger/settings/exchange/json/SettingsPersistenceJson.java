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
package com.helger.settings.exchange.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.WillClose;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.state.ESuccess;
import com.helger.collection.helper.CollectionSort;
import com.helger.commons.typeconvert.TypeConverter;
import com.helger.json.IJson;
import com.helger.json.IJsonObject;
import com.helger.json.JsonObject;
import com.helger.json.serialize.JsonReader;
import com.helger.json.serialize.JsonWriter;
import com.helger.json.serialize.JsonWriterSettings;
import com.helger.settings.ISettings;
import com.helger.settings.exchange.ISettingsPersistence;
import com.helger.settings.factory.ISettingsFactory;

import jakarta.annotation.Nonnull;

/**
 * A special {@link ISettingsPersistence} implementation that reads and writes .json files. It
 * assumes the ISO-8859-1 charset.
 *
 * @author Philip Helger
 */
public class SettingsPersistenceJson implements ISettingsPersistence
{
  private static final Logger LOGGER = LoggerFactory.getLogger (SettingsPersistenceJson.class);

  private final ISettingsFactory <?> m_aSettingsFactory;
  private Charset m_aCharset = JsonReader.DEFAULT_CHARSET;

  public SettingsPersistenceJson ()
  {
    this (ISettingsFactory.newInstance ());
  }

  public SettingsPersistenceJson (@Nonnull final ISettingsFactory <?> aSettingsFactory)
  {
    m_aSettingsFactory = ValueEnforcer.notNull (aSettingsFactory, "SettingsFactory");
  }

  @Nonnull
  public final Charset getCharset ()
  {
    return m_aCharset;
  }

  /**
   * Set the fallback character set to be used for parsing and writing.
   *
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public final SettingsPersistenceJson setCharset (@Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (aCharset, "Charset");
    m_aCharset = aCharset;
    return this;
  }

  /**
   * @return The settings factory as specified in the constructor. Never <code>null</code>.
   */
  @Nonnull
  public final ISettingsFactory <?> getSettingsFactory ()
  {
    return m_aSettingsFactory;
  }

  @Nonnull
  @Nonempty
  protected String getReadSettingsName ()
  {
    return "anonymous";
  }

  private static void _recursiveReadSettings (@Nonnull final String sNamePrefix,
                                              @Nonnull final IJson aJson,
                                              @Nonnull final ISettings aSettings)
  {
    if (aJson.isValue ())
      aSettings.putIn (sNamePrefix, aJson.getAsValue ().getAsString ());
    else
      if (aJson.isObject ())
      {
        for (final Map.Entry <String, IJson> aEntry : aJson.getAsObject ())
          _recursiveReadSettings (sNamePrefix + "." + aEntry.getKey (), aEntry.getValue (), aSettings);
      }
      else
        throw new IllegalArgumentException ("JSON arrays are not supported in settings");
  }

  @Nonnull
  public ISettings readSettings (@Nonnull @WillClose final InputStream aIS)
  {
    ValueEnforcer.notNull (aIS, "InputStream");

    // Create the settings object
    final ISettings aSettings = m_aSettingsFactory.apply (getReadSettingsName ());

    // Read the properties file from the input stream
    final IJsonObject aProps = JsonReader.builder ()
                                         .source (aIS, m_aCharset)
                                         .requireStringQuotes (false)
                                         .alwaysUseBigNumber (true)
                                         .readAsObject ();
    if (aProps != null)
      for (final Map.Entry <String, IJson> aEntry : aProps)
        _recursiveReadSettings (aEntry.getKey (), aEntry.getValue (), aSettings);
    return aSettings;
  }

  @Nonnull
  public ESuccess writeSettings (@Nonnull final ISettings aSettings, @Nonnull @WillClose final OutputStream aOS)
  {
    ValueEnforcer.notNull (aOS, "OutputStream");

    try
    {
      final IJsonObject aProps = new JsonObject ();
      for (final Map.Entry <String, Object> aEntry : CollectionSort.getSorted (aSettings.entrySet (),
                                                                               Comparator.comparing (Map.Entry::getKey)))
      {
        final String sName = aEntry.getKey ();
        final Object aValue = aEntry.getValue ();
        final String sValue = TypeConverter.convert (aValue, String.class);
        aProps.add (sName, sValue);
      }

      final JsonWriterSettings aJWS = new JsonWriterSettings ();
      aJWS.setIndentEnabled (true);
      aJWS.setQuoteNames (false);

      // Does not close the output stream!
      new JsonWriter (aJWS).writeToWriterAndClose (aProps, StreamHelper.createWriter (aOS, m_aCharset));
      return ESuccess.SUCCESS;
    }
    catch (final IOException ex)
    {
      LOGGER.error ("Failed to write settings to JSON file", ex);
      return ESuccess.FAILURE;
    }
    finally
    {
      StreamHelper.close (aOS);
    }
  }
}
