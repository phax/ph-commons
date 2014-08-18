/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
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
 * Determines the indentation and alignment mode of XML serialization. Alignment
 * means: newlines after certain elements. Indent means: adding blanks at the
 * beginning of the line to reflect the tree structure of an XML document more
 * visibly.
 * 
 * @author Philip Helger
 */
public enum EXMLSerializeIndent implements IHasID <String>
{
  /** Neither indent nor align */
  NONE ("none", false, false),
  /** No indent but align */
  ALIGN_ONLY ("align", true, false),
  /** Both indent and align. */
  INDENT_AND_ALIGN ("indentalign", true, true);

  private final String m_sID;
  private final boolean m_bAlign;
  private final boolean m_bIndent;

  private EXMLSerializeIndent (@Nonnull @Nonempty final String sID, final boolean bAlign, final boolean bIndent)
  {
    m_sID = sID;
    m_bAlign = bAlign;
    m_bIndent = bIndent;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return <code>true</code> if newlines should be emitted
   */
  public boolean isAlign ()
  {
    return m_bAlign;
  }

  /**
   * @return <code>true</code> if the XML output should be formatted nicely
   */
  public boolean isIndent ()
  {
    return m_bIndent;
  }

  @Nullable
  public static EXMLSerializeIndent getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EXMLSerializeIndent.class, sID);
  }
}
