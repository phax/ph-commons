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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.style.UsedInGeneratedCode;
import com.helger.datetime.web.PDTWebDateHelper;

import jakarta.annotation.Nullable;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * XML Adapter between LocalDate and String. Use it in your binding file like this:<br>
 * <code>&lt;xjc:javaType name="java.time.LocalDate" xmlType="xsd:date" adapter="com.helger.jaxb.adapter.AdapterLocalDate" /&gt;</code>
 *
 * @author Philip Helger
 * @since 9.4.7
 */
@UsedInGeneratedCode
public class AdapterLocalDate extends XmlAdapter <String, LocalDate>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AdapterLocalDate.class);

  @Override
  public LocalDate unmarshal (@Nullable final String sValue)
  {
    if (sValue == null)
      return null;

    final LocalDate ret = PDTWebDateHelper.getLocalDateFromXSD (sValue.trim ());
    if (ret == null)
      LOGGER.warn ("Failed to parse '" + sValue + "' to a LocalDate");
    return ret;
  }

  @Override
  public String marshal (@Nullable final LocalDate aValue)
  {
    return PDTWebDateHelper.getAsStringXSD (aValue);
  }
}
