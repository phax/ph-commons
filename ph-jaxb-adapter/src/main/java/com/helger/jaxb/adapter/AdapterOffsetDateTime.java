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

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.datetime.PDTWebDateHelper;

/**
 * XML Adapter between OffsetDateTime and String. Use it in your binding file
 * like this:<br>
 * <code>&lt;xjc:javaType name="java.time.OffsetDateTime" xmlType="xsd:dateTime" adapter="com.helger.jaxb.adapter.AdapterOffsetDateTime" /&gt;</code>
 *
 * @author Philip Helger
 * @since 9.5.5
 */
public class AdapterOffsetDateTime extends XmlAdapter <String, OffsetDateTime>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AdapterOffsetDateTime.class);

  private static Function <LocalDateTime, ZoneOffset> s_aZOSupplier = x -> ZoneOffset.UTC;

  @Nonnull
  public static Function <LocalDateTime, ZoneOffset> getFallbackZoneOffsetSupplier ()
  {
    return s_aZOSupplier;
  }

  @Nonnull
  public static void setFallbackZoneOffsetSupplier (@Nonnull final Function <LocalDateTime, ZoneOffset> aZOSupplier)
  {
    ValueEnforcer.notNull (aZOSupplier, "ZoneOffsetSupplier");
    s_aZOSupplier = aZOSupplier;
  }

  @Override
  public OffsetDateTime unmarshal (@Nullable final String sValue)
  {
    if (sValue == null)
      return null;

    final String sTrimmed = sValue.trim ();
    OffsetDateTime ret = PDTWebDateHelper.getOffsetDateTimeFromXSD (sTrimmed);
    if (ret == null)
    {
      // OffsetDateTime is only possible if a zone offset is present.
      // Check if this would be a valid LocalTime and use UTC as fallback
      final LocalDateTime aLDT = PDTWebDateHelper.getLocalDateTimeFromXSD (sTrimmed);
      if (aLDT != null)
        ret = OffsetDateTime.of (aLDT, getFallbackZoneOffsetSupplier ().apply (aLDT));
      else
        LOGGER.warn ("Failed to parse '" + sValue + "' to an OffsetDateTime");
    }
    return ret;
  }

  @Override
  public String marshal (@Nullable final OffsetDateTime aValue)
  {
    return PDTWebDateHelper.getAsStringXSD (aValue);
  }
}
