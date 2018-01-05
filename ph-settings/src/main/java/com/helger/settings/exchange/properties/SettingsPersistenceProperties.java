/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.settings.exchange.properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.WillClose;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.lang.NonBlockingProperties;
import com.helger.commons.lang.PropertiesHelper;
import com.helger.commons.state.ESuccess;
import com.helger.commons.typeconvert.TypeConverter;
import com.helger.settings.ISettings;
import com.helger.settings.exchange.ISettingsPersistence;
import com.helger.settings.factory.ISettingsFactory;

/**
 * A special {@link ISettingsPersistence} implementation that reads and writes
 * .properties files.
 *
 * @author Philip Helger
 */
public class SettingsPersistenceProperties implements ISettingsPersistence
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SettingsPersistenceProperties.class);

  private final ISettingsFactory <?> m_aSettingsFactory;

  public SettingsPersistenceProperties ()
  {
    this (ISettingsFactory.newInstance ());
  }

  public SettingsPersistenceProperties (@Nonnull final ISettingsFactory <?> aSettingsFactory)
  {
    m_aSettingsFactory = ValueEnforcer.notNull (aSettingsFactory, "SettingsFactory");
  }

  @Nonnull
  public final Charset getCharset ()
  {
    // Constant
    return StandardCharsets.ISO_8859_1;
  }

  @Nonnull
  @Nonempty
  protected String getReadSettingsName ()
  {
    return "anonymous";
  }

  @Nonnull
  public ISettings readSettings (@Nonnull @WillClose final InputStream aIS)
  {
    ValueEnforcer.notNull (aIS, "InputStream");

    // Create the settings object
    final ISettings aSettings = m_aSettingsFactory.apply (getReadSettingsName ());

    // Read the properties file from the input stream
    final NonBlockingProperties aProps = PropertiesHelper.loadProperties (aIS);

    if (aProps != null)
      for (final Map.Entry <String, String> aEntry : aProps.entrySet ())
        aSettings.putIn (aEntry.getKey (), aEntry.getValue ());
    return aSettings;
  }

  @Nonnull
  public ESuccess writeSettings (@Nonnull final ISettings aSettings, @Nonnull @WillClose final OutputStream aOS)
  {
    ValueEnforcer.notNull (aOS, "OutputStream");

    try
    {
      final NonBlockingProperties aProps = new NonBlockingProperties ();
      // Must not be sorted, as Properties sorts them as it wishes...
      for (final Map.Entry <String, Object> aEntry : aSettings.entrySet ())
      {
        final String sName = aEntry.getKey ();
        final Object aValue = aEntry.getValue ();
        if (aValue instanceof ISettings)
          throw new IllegalArgumentException ("When saving settings to a Properties object, it may not contained nested settings! Now the key '" +
                                              sName +
                                              "' is mapped to a nested ISettings object!");
        final String sValue = TypeConverter.convert (aValue, String.class);
        aProps.put (sName, sValue);
      }
      // Does not close the output stream!
      aProps.store (aOS, aSettings.getName ());
      return ESuccess.SUCCESS;
    }
    catch (final IOException ex)
    {
      s_aLogger.error ("Failed to write settings to properties file", ex);
      return ESuccess.FAILURE;
    }
    finally
    {
      StreamHelper.close (aOS);
    }
  }
}
