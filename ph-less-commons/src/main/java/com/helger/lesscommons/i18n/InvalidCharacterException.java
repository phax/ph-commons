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
package com.helger.lesscommons.i18n;

import com.helger.commons.string.StringHelper;

/**
 * @author Apache Abdera
 */
public class InvalidCharacterException extends RuntimeException
{
  private final int m_nInput;

  public InvalidCharacterException (final int nInput)
  {
    m_nInput = nInput;
  }

  public int getInput ()
  {
    return m_nInput;
  }

  @Override
  public String getMessage ()
  {
    return "Invalid Character 0x" + StringHelper.getHexStringLeadingZero (m_nInput, 2) + "(" + (char) m_nInput + ")";
  }
}
