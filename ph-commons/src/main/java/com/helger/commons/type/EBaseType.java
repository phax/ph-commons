/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;

/**
 * Contains generic type definitions independent from the underlying language.
 *
 * @author Philip Helger
 */
public enum EBaseType implements IHasID <String>
{
 /** boolean: true or false */
  BOOLEAN ("boolean"),

 /** byte array */
  BYTE_ARRAY ("bytearray"),

 /** date */
  DATE ("date"),

 /** date and time */
  DATETIME ("datetime"),

 /** double */
  DOUBLE ("double"),

 /** integer */
  INT ("int"),

 /** multilingual text */
  MTEXT ("mtext"),

 /** string */
  TEXT ("text"),

 /** time */
  TIME ("time"),

 /** xml */
  XML ("xml");

  private final String m_sID;

  private EBaseType (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nullable
  public static EBaseType getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EBaseType.class, sID);
  }
}
