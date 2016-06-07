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
package com.helger.commons.xml.dom;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

import org.w3c.dom.Node;

import com.helger.commons.id.IHasIntID;
import com.helger.commons.lang.EnumHelper;

/**
 * Represents the different DOM node types. This is a type-safe version of the
 * node types defined in {@link org.w3c.dom.Node}.
 *
 * @author Philip Helger
 */
public enum EXMLDOMNodeType implements IHasIntID
{
  /**
   * The node is an <code>Element</code>.
   */
  ELEMENT_NODE (Node.ELEMENT_NODE),

  /**
   * The node is an <code>Attr</code>.
   */
  ATTRIBUTE_NODE (Node.ATTRIBUTE_NODE),

  /**
   * The node is a <code>Text</code> node.
   */
  TEXT_NODE (Node.TEXT_NODE),

  /**
   * The node is a <code>CDATASection</code>.
   */
  CDATA_SECTION_NODE (Node.CDATA_SECTION_NODE),

  /**
   * The node is an <code>EntityReference</code>.
   */
  ENTITY_REFERENCE_NODE (Node.ENTITY_REFERENCE_NODE),

  /**
   * The node is an <code>Entity</code>.
   */
  ENTITY_NODE (Node.ENTITY_NODE),

  /**
   * The node is a <code>ProcessingInstruction</code>.
   */
  PROCESSING_INSTRUCTION_NODE (Node.PROCESSING_INSTRUCTION_NODE),

  /**
   * The node is a <code>Comment</code>.
   */
  COMMENT_NODE (Node.COMMENT_NODE),

  /**
   * The node is a <code>Document</code>.
   */
  DOCUMENT_NODE (Node.DOCUMENT_NODE),

  /**
   * The node is a <code>DocumentType</code>.
   */
  DOCUMENT_TYPE_NODE (Node.DOCUMENT_TYPE_NODE),

  /**
   * The node is a <code>DocumentFragment</code>.
   */
  DOCUMENT_FRAGMENT_NODE (Node.DOCUMENT_FRAGMENT_NODE),

  /**
   * The node is a <code>Notation</code>.
   */
  NOTATION_NODE (Node.NOTATION_NODE);

  private final int m_nValue;

  private EXMLDOMNodeType (@Nonnegative final short nValue)
  {
    m_nValue = nValue;
  }

  @Nonnegative
  public int getID ()
  {
    return m_nValue;
  }

  @Nullable
  public static EXMLDOMNodeType getFromIDOrNull (final int nID)
  {
    return EnumHelper.getFromIDOrNull (EXMLDOMNodeType.class, nID);
  }
}
