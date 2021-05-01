/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.jaxb.adapter;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.datetime.PDTWebDateHelper;
import com.helger.commons.datetime.XMLOffsetDateTime;

/**
 * XML Adapter between OffsetDateTime and String. Use it in your binding file
 * like this:<br>
 * <code>&lt;xjc:javaType name="com.helger.commons.datetime.XMLOffsetDateTime" xmlType="xsd:dateTime" adapter="com.helger.jaxb.adapter.AdapterXMLOffsetDateTime" /&gt;</code>
 *
 * @author Philip Helger
 * @since 10.1.0
 */
public class AdapterXMLOffsetDateTime extends XmlAdapter <String, XMLOffsetDateTime>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AdapterXMLOffsetDateTime.class);

  @Override
  @Nullable
  public XMLOffsetDateTime unmarshal (@Nullable final String sValue)
  {
    if (sValue == null)
      return null;

    final XMLOffsetDateTime ret = PDTWebDateHelper.getXMLOffsetDateTimeFromXSD (sValue.trim ());
    if (ret == null)
      LOGGER.warn ("Failed to parse '" + sValue + "' to an XMLOffsetDateTime");
    return ret;
  }

  @Override
  @Nullable
  public String marshal (@Nullable final XMLOffsetDateTime aValue)
  {
    return PDTWebDateHelper.getAsStringXSD (aValue);
  }
}
