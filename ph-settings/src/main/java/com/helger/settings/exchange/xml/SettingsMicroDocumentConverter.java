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
package com.helger.settings.exchange.xml;

import java.util.Comparator;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.lang.GenericReflection;
import com.helger.settings.ISettings;
import com.helger.settings.factory.ISettingsFactory;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.IMicroQName;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.MicroQName;
import com.helger.xml.microdom.convert.IMicroTypeConverter;
import com.helger.xml.microdom.convert.MicroTypeConverter;

public class SettingsMicroDocumentConverter <T extends ISettings> implements IMicroTypeConverter <T>
{
  private static final String ELEMENT_SETTING = "setting";
  private static final IMicroQName ATTR_NAME = new MicroQName ("name");
  private static final IMicroQName ATTR_CLASS = new MicroQName ("class");
  private static final String ELEMENT_VALUE = "value";

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

      final IMicroElement eSetting = eRoot.appendElement (sNamespaceURI, ELEMENT_SETTING);
      eSetting.setAttribute (ATTR_NAME, sFieldName);
      eSetting.setAttribute (ATTR_CLASS, aValue.getClass ().getName ());
      eSetting.appendChild (MicroTypeConverter.convertToMicroElement (aValue, ELEMENT_VALUE));
    }
    return eRoot;
  }

  @Nonnull
  public T convertToNative (final IMicroElement aElement)
  {
    // Create new settings object
    final String sSettingsName = aElement.getAttributeValue (ATTR_NAME);
    final T aSettings = m_aSettingFactory.apply (sSettingsName);

    // settings are only on the top-level
    aElement.forAllChildElements (eSetting -> {
      final String sFieldName = eSetting.getAttributeValue (ATTR_NAME);
      final String sClass = eSetting.getAttributeValue (ATTR_CLASS);
      final Class <?> aDestClass = GenericReflection.getClassFromNameSafe (sClass);
      if (aDestClass == null)
        throw new IllegalStateException ("Failed to determine class from '" + sClass + "'");

      final Object aValue = MicroTypeConverter.convertToNative (eSetting.getFirstChildElement (ELEMENT_VALUE),
                                                                aDestClass);
      // Use put instead of putIn to avoid that the callbacks are invoked!
      aSettings.put (sFieldName, aValue);
    });
    return aSettings;
  }
}
