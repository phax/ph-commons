/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.xml.serialize;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;

/**
 * Determines the output format for XML serialization. XML or HTML?
 * 
 * @author Philip Helger
 */
public enum EXMLSerializeFormat implements IHasID <String>
{
  HTML ("html"),
  XHTML ("xhtml"),
  XML ("xml");

  private final String m_sID;

  private EXMLSerializeFormat (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return <code>true</code> if the serialization format is HTML
   */
  public boolean isHTML ()
  {
    return this == HTML;
  }

  /**
   * @return <code>true</code> if the serialization format is XHTML
   */
  public boolean isXHTML ()
  {
    return this == XHTML;
  }

  /**
   * @return <code>true</code> if the serialization format is XML
   */
  public boolean isXML ()
  {
    return this == XML;
  }

  @Nullable
  public static EXMLSerializeFormat getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EXMLSerializeFormat.class, sID);
  }
}
