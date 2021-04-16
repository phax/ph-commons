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

import java.time.LocalDate;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.datetime.OffsetDate;
import com.helger.commons.datetime.PDTWebDateHelper;

/**
 * XML Adapter between OffsetDate and String. Use it in your binding file like
 * this:<br>
 * <code>&lt;xjc:javaType name="com.helger.commons.datetime.OffsetDate" xmlType="xsd:date" adapter="com.helger.jaxb.adapter.AdapterOffsetDate" /&gt;</code>
 *
 * @author Philip Helger
 * @since 10.0.0
 */
public class AdapterOffsetDate extends XmlAdapter <String, OffsetDate>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AdapterOffsetDate.class);

  @Override
  public OffsetDate unmarshal (@Nullable final String sValue)
  {
    if (sValue == null)
      return null;

    final String sTrimmed = sValue.trim ();
    OffsetDate ret = PDTWebDateHelper.getOffsetDateFromXSD (sTrimmed);
    if (ret == null)
    {
      // OffsetDate is only possible if a zone offset is present.
      // Check if this would be a valid LocalDate and use UTC as fallback
      final LocalDate aLD = PDTWebDateHelper.getLocalDateFromXSD (sTrimmed);
      if (aLD != null)
        ret = OffsetDate.of (aLD, null);
      else
        LOGGER.warn ("Failed to parse '" + sValue + "' to an OffsetDate");
    }
    return ret;
  }

  @Override
  public String marshal (@Nullable final OffsetDate aValue)
  {
    if (aValue == null)
      return null;
    if (aValue.getOffset () == null)
    {
      // Required for Java 9+
      return PDTWebDateHelper.getAsStringXSD (aValue.toLocalDate ());
    }
    return PDTWebDateHelper.getAsStringXSD (aValue);
  }
}
