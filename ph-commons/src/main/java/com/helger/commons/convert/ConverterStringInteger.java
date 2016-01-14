/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.convert;

import javax.annotation.Nullable;

import com.helger.commons.string.StringParser;

/**
 * Get a generic data converter that converts a string to an Integer. If the
 * conversion fails, the default value is returned from the converter.
 *
 * @author Philip Helger
 */
public class ConverterStringInteger implements IConverter <String, Integer>
{
  private final Integer m_aDefaultValue;

  public ConverterStringInteger ()
  {
    this ((Integer) null);
  }

  public ConverterStringInteger (@Nullable final Integer aDefaultValue)
  {
    m_aDefaultValue = aDefaultValue;
  }

  @Nullable
  public Integer getDefaultValue ()
  {
    return m_aDefaultValue;
  }

  @Nullable
  public Integer apply (@Nullable final String sInput)
  {
    return StringParser.parseIntObj (sInput, m_aDefaultValue);
  }
}
