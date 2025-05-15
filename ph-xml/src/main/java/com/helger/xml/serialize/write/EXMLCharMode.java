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
package com.helger.xml.serialize.write;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;

/**
 * Defines the different characters modes that are relevant to determine invalid
 * characters as well characters to be masked.
 *
 * @author Philip Helger
 */
public enum EXMLCharMode implements IHasID <String>
{
  /** Element name */
  ELEMENT_NAME ("element"),
  /** Attribute name */
  ATTRIBUTE_NAME ("attrname"),
  /** Attribute value surrounded by double quotes */
  ATTRIBUTE_VALUE_DOUBLE_QUOTES ("attrvaldq"),
  /** Attribute value surrounded by single quotes */
  ATTRIBUTE_VALUE_SINGLE_QUOTES ("attrvalsq"),
  /** Text content */
  TEXT ("text"),
  /** CDATA content */
  CDATA ("cdata");

  private final String m_sID;

  EXMLCharMode (@Nonnull @Nonempty final String sID)
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
  public static EXMLCharMode getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EXMLCharMode.class, sID);
  }
}
