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
package com.helger.jaxb.adapter;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.datetime.rt.OffsetDate;
import com.helger.datetime.web.PDTWebDateHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

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

  private static Function <LocalDate, ZoneOffset> s_aZOSupplier = x -> ZoneOffset.UTC;

  @Nonnull
  public static Function <LocalDate, ZoneOffset> getFallbackZoneOffsetSupplier ()
  {
    return s_aZOSupplier;
  }

  @Nonnull
  public static void setFallbackZoneOffsetSupplier (@Nonnull final Function <LocalDate, ZoneOffset> aZOSupplier)
  {
    ValueEnforcer.notNull (aZOSupplier, "ZoneOffsetSupplier");
    s_aZOSupplier = aZOSupplier;
  }

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
        ret = OffsetDate.of (aLD, getFallbackZoneOffsetSupplier ().apply (aLD));
      else
      {
        LOGGER.warn ("Failed to parse '" + sValue + "' to an OffsetDate");
      }
    }
    return ret;
  }

  @Override
  public String marshal (@Nullable final OffsetDate aValue)
  {
    return PDTWebDateHelper.getAsStringXSD (aValue);
  }
}
