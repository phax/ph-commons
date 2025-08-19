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
package com.helger.settings.exchange.xml;

import java.util.Comparator;
import java.util.Map;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.settings.ISettings;
import com.helger.settings.factory.ISettingsFactory;
import com.helger.typeconvert.impl.TypeConverter;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.IMicroQName;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.MicroQName;
import com.helger.xml.microdom.convert.IMicroTypeConverter;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class SettingsMicroDocumentConverter <T extends ISettings> implements IMicroTypeConverter <T>
{
  private static final String ELEMENT_SETTING = "setting";
  private static final IMicroQName ATTR_NAME = new MicroQName ("name");
  private static final IMicroQName ATTR_IS_NULL = new MicroQName ("is-null");

  private final ISettingsFactory <T> m_aSettingFactory;

  /**
   * Constructor that uses the default settings factory.
   *
   * @param aSettingsFactory
   *        Settings factory to be used. May not be <code>null</code>.
   */
  public SettingsMicroDocumentConverter (@Nonnull final ISettingsFactory <T> aSettingsFactory)
  {
    m_aSettingFactory = ValueEnforcer.notNull (aSettingsFactory, "SettingsFactory");
  }

  @Nonnull
  public ISettingsFactory <T> getSettingsFactory ()
  {
    return m_aSettingFactory;
  }

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final T aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final IMicroElement eRoot = new MicroElement (sNamespaceURI, sTagName);
    eRoot.setAttribute (ATTR_NAME, aObject.getName ());

    // Sort fields to have them deterministic
    for (final Map.Entry <String, Object> aEntry : aObject.getSortedByKey (Comparator.naturalOrder ()).entrySet ())
    {
      final String sFieldName = aEntry.getKey ();
      final Object aValue = aEntry.getValue ();

      final IMicroElement eSetting = eRoot.addElementNS (sNamespaceURI, ELEMENT_SETTING);
      eSetting.setAttribute (ATTR_NAME, sFieldName);
      if (aValue == null)
        eSetting.setAttribute (ATTR_IS_NULL, true);
      else
      {
        final String sValue = TypeConverter.convert (aValue, String.class);
        eSetting.addText (sValue);
      }
    }
    return eRoot;
  }

  @Nonnull
  public T convertToNative (final IMicroElement aElement)
  {
    // Create new settings object
    final String sSettingsName = aElement.getAttributeValue (ATTR_NAME);
    if (StringHelper.isEmpty (sSettingsName))
      throw new IllegalStateException ("Settings 'name' is missing or empty");
    final T aSettings = m_aSettingFactory.apply (sSettingsName);

    // settings are only on the top-level
    for (final IMicroElement eSetting : aElement.getAllChildElements ())
    {
      final String sFieldName = eSetting.getAttributeValue (ATTR_NAME);

      final String sValue;
      if (eSetting.hasAttribute (ATTR_IS_NULL))
      {
        // Special case for null
        sValue = null;
      }
      else
      {
        // Backwards compatibility
        final IMicroElement eValue = eSetting.getFirstChildElement ("value");
        if (eValue != null)
          sValue = eValue.getTextContent ();
        else
          sValue = eSetting.getTextContent ();
      }
      // Use "put" instead of "putIn" to avoid that the callbacks are invoked!
      // Use "putIn" to ensure that the custom value modifiers are applied and
      // that it is consistent with the properties implementation
      aSettings.putIn (sFieldName, sValue);
    }
    return aSettings;
  }
}
