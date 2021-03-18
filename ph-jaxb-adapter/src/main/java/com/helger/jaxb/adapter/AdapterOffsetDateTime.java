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

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.datetime.PDTWebDateHelper;

/**
 * XML Adapter between OffsetDateTime and String. Use it in your binding file
 * like this:<br>
 * <code>&lt;xjc:javaType name="java.time.OffsetDateTime" xmlType="xsd:dateTime" adapter="com.helger.jaxb.adapter.AdapterOffsetDateTime" /&gt;</code>
 *
 * @author Philip Helger
 * @since 10.0.0
 */
public class AdapterOffsetDateTime extends XmlAdapter <String, OffsetDateTime>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AdapterOffsetDateTime.class);

  @Override
  public OffsetDateTime unmarshal (@Nullable final String sValue)
  {
    if (sValue == null)
      return null;

    final ZonedDateTime ret = PDTWebDateHelper.getDateTimeFromXSD (sValue.trim ());
    if (ret == null)
    {
      LOGGER.warn ("Failed to parse '" + sValue + "' to a OffsetDateTime");
      return null;
    }
    return ret.toOffsetDateTime ();
  }

  @Override
  public String marshal (@Nullable final OffsetDateTime aValue)
  {
    return PDTWebDateHelper.getAsStringXSD (aValue);
  }
}
