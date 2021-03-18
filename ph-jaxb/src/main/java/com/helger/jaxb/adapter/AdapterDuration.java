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

import java.time.Duration;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * XML Adapter between Duration and String. Use it in your binding file like
 * this:<br>
 * <code>&lt;xjc:javaType name="java.time.Duration" xmlType="xsd:duration" adapter="com.helger.jaxb.adapter.AdapterDuration" /&gt;</code>
 *
 * @author Philip Helger
 * @since 9.4.7
 */
public class AdapterDuration extends XmlAdapter <String, Duration>
{
  @Override
  public Duration unmarshal (@Nullable final String sValue)
  {
    if (sValue == null)
      return null;
    return Duration.parse (sValue.trim ());
  }

  @Override
  public String marshal (@Nullable final Duration aValue)
  {
    return aValue != null ? aValue.toString () : null;
  }
}
