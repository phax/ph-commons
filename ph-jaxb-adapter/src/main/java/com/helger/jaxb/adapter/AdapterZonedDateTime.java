/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.datetime.PDTWebDateHelper;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * XML Adapter between ZonedDateTime and String. Use it in your binding file
 * like this:<br>
 * <code>&lt;xjc:javaType name="java.time.ZonedDateTime" xmlType="xsd:dateTime" adapter="com.helger.jaxb.adapter.AdapterZonedDateTime" /&gt;</code>
 *
 * @author Philip Helger
 * @since 9.4.8
 */
public class AdapterZonedDateTime extends XmlAdapter <String, ZonedDateTime>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AdapterZonedDateTime.class);

  @Override
  public ZonedDateTime unmarshal (@Nullable final String sValue)
  {
    if (sValue == null)
      return null;

    final String sTrimmed = sValue.trim ();
    ZonedDateTime ret = PDTWebDateHelper.getZonedDateTimeFromXSD (sTrimmed);
    if (ret == null)
    {
      // ZonedDateTime is only possible if a zone offset is present.
      // Check if this would be a valid LocalTime and use UTC as fallback
      final LocalDateTime aLDT = PDTWebDateHelper.getLocalDateTimeFromXSD (sTrimmed);
      if (aLDT != null)
        ret = ZonedDateTime.of (aLDT, ZoneOffset.UTC);
      else
      {
        if (LOGGER.isWarnEnabled ())
          LOGGER.warn ("Failed to parse '" + sValue + "' to a ZonedDateTime");
      }
    }
    return ret;
  }

  @Override
  public String marshal (@Nullable final ZonedDateTime aValue)
  {
    return PDTWebDateHelper.getAsStringXSD (aValue);
  }
}
