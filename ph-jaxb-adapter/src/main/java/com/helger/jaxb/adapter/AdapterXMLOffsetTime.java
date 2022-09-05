/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.datetime.PDTWebDateHelper;
import com.helger.commons.datetime.XMLOffsetTime;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * XML Adapter between OffsetTime and String. Use it in your binding file like
 * this:<br>
 * <code>&lt;xjc:javaType name="com.helger.commons.datetime.XMLOffsetTime" xmlType="xsd:time" adapter="com.helger.jaxb.adapter.AdapterXMLOffsetTime" /&gt;</code>
 *
 * @author Philip Helger
 * @since 10.1.0
 */
public class AdapterXMLOffsetTime extends XmlAdapter <String, XMLOffsetTime>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AdapterXMLOffsetTime.class);

  @Override
  @Nullable
  public XMLOffsetTime unmarshal (@Nullable final String sValue)
  {
    if (sValue == null)
      return null;

    final XMLOffsetTime ret = PDTWebDateHelper.getXMLOffsetTimeFromXSD (sValue.trim ());
    if (ret == null)
      if (LOGGER.isWarnEnabled ())
        LOGGER.warn ("Failed to parse '" + sValue + "' to an XMLOffsetTime");
    return ret;
  }

  @Override
  @Nullable
  public String marshal (@Nullable final XMLOffsetTime aValue)
  {
    return PDTWebDateHelper.getAsStringXSD (aValue);
  }
}
