/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.settings.exchange.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.state.ESuccess;
import com.helger.settings.IMutableSettings;
import com.helger.settings.ISettings;
import com.helger.settings.exchange.ISettingsPersistence;
import com.helger.settings.factory.ISettingsFactory;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.serialize.MicroReader;
import com.helger.xml.microdom.serialize.MicroWriter;
import com.helger.xml.serialize.write.IXMLWriterSettings;
import com.helger.xml.serialize.write.XMLWriterSettings;

/**
 * A special {@link ISettingsPersistence} implementation that reads and writes
 * .xml files.
 *
 * @author Philip Helger
 */
public class SettingsPersistenceXML implements ISettingsPersistence
{
  public static final boolean DEFAULT_MARSHAL_TYPES = true;

  private final ISettingsFactory m_aSettingsFactory;
  private final IXMLWriterSettings m_aXWS;

  public SettingsPersistenceXML ()
  {
    this (ISettingsFactory.newInstance ());
  }

  public SettingsPersistenceXML (@Nonnull final ISettingsFactory aSettingsFactory)
  {
    this (aSettingsFactory, XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }

  public SettingsPersistenceXML (@Nonnull final ISettingsFactory aSettingsFactory,
                                 @Nonnull final IXMLWriterSettings aXWS)
  {
    m_aSettingsFactory = ValueEnforcer.notNull (aSettingsFactory, "SettingsFactory");
    m_aXWS = aXWS;
  }

  @Nonnull
  public final Charset getCharset ()
  {
    return m_aXWS.getCharsetObj ();
  }

  /**
   * @return The settings factory as specified in the constructor. Never
   *         <code>null</code>.
   */
  @Nonnull
  public ISettingsFactory getSettingsFactory ()
  {
    return m_aSettingsFactory;
  }

  /**
   * @return The XML writer settings as specified in the constructor. Never
   *         <code>null</code>.
   */
  @Nonnull
  public IXMLWriterSettings getXMLWriterSettings ()
  {
    return m_aXWS;
  }

  @Nonnull
  public IMutableSettings readSettings (@Nonnull @WillClose final InputStream aIS)
  {
    ValueEnforcer.notNull (aIS, "InputStream");

    final IMicroDocument aDoc = MicroReader.readMicroXML (aIS);
    if (aDoc == null)
      throw new IllegalArgumentException ("Passed XML document is illegal");

    // read items
    final SettingsMicroDocumentConverter aConverter = new SettingsMicroDocumentConverter (m_aSettingsFactory);
    return aConverter.convertToNative (aDoc.getDocumentElement ());
  }

  /**
   * @return The namespace URI to be used for writing XML settings. Defaults to
   *         <code>null</code>.
   */
  @Nullable
  @OverrideOnDemand
  protected String getWriteNamespaceURI ()
  {
    return null;
  }

  /**
   * @return The document root element local name. May neither be
   *         <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  @OverrideOnDemand
  protected String getWriteElementName ()
  {
    return "settings";
  }

  @Nonnull
  public ESuccess writeSettings (@Nonnull final ISettings aSettings, @Nonnull @WillClose final OutputStream aOS)
  {
    ValueEnforcer.notNull (aSettings, "Settings");
    ValueEnforcer.notNull (aOS, "OutputStream");

    try
    {
      // Inside try so that OS is closed
      ValueEnforcer.notNull (aSettings, "Settings");

      // No event manager invocation on writing
      final SettingsMicroDocumentConverter aConverter = new SettingsMicroDocumentConverter (m_aSettingsFactory);
      final IMicroDocument aDoc = new MicroDocument ();
      aDoc.appendChild (aConverter.convertToMicroElement (aSettings, getWriteNamespaceURI (), getWriteElementName ()));

      // auto-closes the stream
      return MicroWriter.writeToStream (aDoc, aOS, m_aXWS);
    }
    finally
    {
      StreamHelper.close (aOS);
    }
  }
}
