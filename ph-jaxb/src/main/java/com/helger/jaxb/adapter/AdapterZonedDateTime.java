/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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

import java.time.ZonedDateTime;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.helger.commons.datetime.PDTWebDateHelper;

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
  @Override
  public ZonedDateTime unmarshal (@Nullable final String value)
  {
    return PDTWebDateHelper.getDateTimeFromXSD (value);
  }

  @Override
  public String marshal (@Nullable final ZonedDateTime value)
  {
    return PDTWebDateHelper.getAsStringXSD (value);
  }
}
