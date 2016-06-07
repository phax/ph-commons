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
package com.helger.commons.microdom;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

import org.w3c.dom.Node;

import com.helger.commons.id.IHasIntID;
import com.helger.commons.lang.EnumHelper;

/**
 * Denotes the type of {@link IMicroNode} objects.
 *
 * @author Philip Helger
 */
public enum EMicroNodeType implements IHasIntID
{
  CDATA (1, Node.CDATA_SECTION_NODE),
  COMMENT (2, Node.COMMENT_NODE),
  CONTAINER (3),
  DOCUMENT (4, Node.DOCUMENT_NODE),
  DOCUMENT_TYPE (5, Node.DOCUMENT_TYPE_NODE),
  ELEMENT (6, Node.ELEMENT_NODE),
  ENTITY_REFERENCE (7, Node.ENTITY_REFERENCE_NODE),
  PROCESSING_INSTRUCTION (8, Node.PROCESSING_INSTRUCTION_NODE),
  TEXT (9, Node.TEXT_NODE);

  /** Represents a non-existing DOM Node Type */
  public static final short ILLEGAL_DOM_NODE_TYPE = 0;

  private final int m_nID;
  private final short m_nDOMNodeType;

  private EMicroNodeType (final int nID)
  {
    this (nID, ILLEGAL_DOM_NODE_TYPE);
  }

  private EMicroNodeType (@Nonnegative final int nID, final short nDOMNodeType)
  {
    m_nID = nID;
    m_nDOMNodeType = nDOMNodeType;
  }

  @Nonnegative
  public int getID ()
  {
    return m_nID;
  }

  /**
   * @return <code>true</code> if this micro node type has a corresponding DOM
   *         node type. <code>false</code> if not.
   */
  public boolean hasCorrespondingDOMNodeType ()
  {
    return m_nDOMNodeType != ILLEGAL_DOM_NODE_TYPE;
  }

  /**
   * @return The corresponding DOM node type or {@link #ILLEGAL_DOM_NODE_TYPE}
   *         if this micro node type has no corresponding DOM node type.
   */
  public short getDOMNodeType ()
  {
    return m_nDOMNodeType;
  }

  public boolean isCDATA ()
  {
    return this == CDATA;
  }

  public boolean isText ()
  {
    return this == TEXT;
  }

  @Nullable
  public static EMicroNodeType getFromIDOrNull (final int nID)
  {
    return EnumHelper.getFromIDOrNull (EMicroNodeType.class, nID);
  }
}
