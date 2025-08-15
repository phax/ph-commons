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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.commons.state.ETriState;
import com.helger.xml.EXMLVersion;
import com.helger.xml.XMLHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Internal XML serializer that takes org.w3c.dom.Node objects, extracts the information to
 * serialize and passes the respective information to an {@link XMLEmitter} object.
 *
 * @author Philip Helger
 */
public class XMLSerializer extends AbstractXMLSerializer <Node>
{
  public XMLSerializer (@Nonnull final IXMLWriterSettings aSettings)
  {
    super (aSettings);
  }

  @Override
  protected void emitNode (@Nonnull final XMLEmitter aXMLWriter,
                           @Nullable final Node aParentNode,
                           @Nullable final Node aPrevSibling,
                           @Nonnull final Node aNode,
                           @Nullable final Node aNextSibling)
  {
    final short nNodeType = aNode.getNodeType ();
    if (nNodeType == Node.ELEMENT_NODE)
      _writeElement (aXMLWriter, aParentNode, aPrevSibling, (Element) aNode, aNextSibling);
    else
      if (nNodeType == Node.TEXT_NODE)
        _writeText (aXMLWriter, (Text) aNode);
      else
        if (nNodeType == Node.CDATA_SECTION_NODE)
          _writeCDATA (aXMLWriter, (CDATASection) aNode);
        else
          if (nNodeType == Node.COMMENT_NODE)
            _writeComment (aXMLWriter, (Comment) aNode);
          else
            if (nNodeType == Node.ENTITY_REFERENCE_NODE)
              _writeEntityReference (aXMLWriter, (EntityReference) aNode);
            else
              if (nNodeType == Node.DOCUMENT_NODE)
                _writeDocument (aXMLWriter, (Document) aNode);
              else
                if (nNodeType == Node.DOCUMENT_TYPE_NODE)
                  _writeDocumentType (aXMLWriter, (DocumentType) aNode);
                else
                  if (nNodeType == Node.PROCESSING_INSTRUCTION_NODE)
                    _writeProcessingInstruction (aXMLWriter, (ProcessingInstruction) aNode);
                  else
                    throw new IllegalArgumentException ("Passed node type " + nNodeType + " is not yet supported");
  }

  private void _writeNodeList (@Nonnull final XMLEmitter aXMLWriter,
                               @Nonnull final Node aParentNode,
                               @Nonnull final NodeList aChildren)
  {
    final int nLastIndex = aChildren.getLength () - 1;
    for (int nIndex = 0; nIndex <= nLastIndex; ++nIndex)
    {
      emitNode (aXMLWriter,
                aParentNode,
                nIndex == 0 ? null : aChildren.item (nIndex - 1),
                aChildren.item (nIndex),
                nIndex == nLastIndex ? null : aChildren.item (nIndex + 1));
    }
  }

  private void _writeDocument (@Nonnull final XMLEmitter aXMLWriter, @Nonnull final Document aDocument)
  {
    if (m_aSettings.getSerializeXMLDeclaration ().isEmit ())
    {
      String sXMLVersion = null;
      ETriState eDocumentStandalone = ETriState.UNDEFINED;
      try
      {
        sXMLVersion = aDocument.getXmlVersion ();
        eDocumentStandalone = ETriState.valueOf (aDocument.getXmlStandalone ());
      }
      catch (final LinkageError | DOMException ex)
      {
        // LinkageError Happens e.g. in dom4j 1.6.1:
        // AbstractMethodError: getXmlVersion and getXmlStandalone
        // DOMException in dom4j 2.0.3
      }
      final EXMLVersion eXMLVersion = EXMLVersion.getFromVersionOrDefault (sXMLVersion, m_aSettings.getXMLVersion ());
      aXMLWriter.onXMLDeclaration (eXMLVersion,
                                   m_aSettings.getCharset ().name (),
                                   m_aSettings.getSerializeXMLDeclaration ().isEmitStandalone () ? eDocumentStandalone
                                                                                                 : ETriState.UNDEFINED,
                                   m_aSettings.isNewLineAfterXMLDeclaration ());
    }

    _writeNodeList (aXMLWriter, aDocument, aDocument.getChildNodes ());
  }

  private void _writeDocumentType (@Nonnull final XMLEmitter aXMLWriter, @Nonnull final DocumentType aDocType)
  {
    if (m_aSettings.getSerializeDocType ().isEmit ())
      aXMLWriter.onDocumentType (aDocType.getName (), aDocType.getPublicId (), aDocType.getSystemId ());
  }

  private static void _writeProcessingInstruction (@Nonnull final XMLEmitter aXMLWriter,
                                                   @Nonnull final ProcessingInstruction aPI)
  {
    aXMLWriter.onProcessingInstruction (aPI.getTarget (), aPI.getData ());
  }

  private static void _writeEntityReference (@Nonnull final XMLEmitter aXMLWriter,
                                             @Nonnull final EntityReference aEntRef)
  {
    aXMLWriter.onEntityReference (aEntRef.getNodeName ());
  }

  private void _writeComment (@Nonnull final XMLEmitter aXMLWriter, @Nonnull final Comment aComment)
  {
    if (m_aSettings.getSerializeComments ().isEmit ())
    {
      final String sComment = aComment.getData ();
      aXMLWriter.onComment (sComment);
      if (sComment.indexOf ('\n') >= 0)
      {
        // Newline only after multi-line comments
        aXMLWriter.newLine ();
      }
    }
  }

  private static void _writeText (@Nonnull final XMLEmitter aXMLWriter, @Nonnull final Text aText)
  {
    // DOM text is always escaped!
    aXMLWriter.onText (aText.getData ());
  }

  private void _writeCDATA (@Nonnull final XMLEmitter aXMLWriter, @Nonnull final Text aText)
  {
    if (m_aSettings.isWriteCDATAAsText ())
      aXMLWriter.onText (aText.getData ());
    else
      aXMLWriter.onCDATA (aText.getData ());
  }

  private void _writeElement (@Nonnull final XMLEmitter aXMLWriter,
                              @Nullable final Node aParentNode,
                              @Nullable final Node aPrevSibling,
                              @Nonnull final Element aElement,
                              @Nullable final Node aNextSibling)
  {
    // use either local name or tag name (depending on namespace prefix)
    final String sTagName = XMLHelper.getLocalNameOrTagName (aElement);

    // May be null!
    final Document aDoc = aElement.getOwnerDocument ();
    final boolean bEmitNamespaces = m_aSettings.isEmitNamespaces ();
    final NodeList aChildNodeList = aElement.getChildNodes ();
    final boolean bHasChildren = aChildNodeList.getLength () > 0;

    final boolean bIsRootElement = aDoc != null && aElement.equals (aDoc.getDocumentElement ());
    final boolean bIndentPrev = aPrevSibling == null || !XMLHelper.isInlineNode (aPrevSibling) || bIsRootElement;
    final boolean bIndentNext = aNextSibling == null || !XMLHelper.isInlineNode (aNextSibling);
    final boolean bIsFirstChildElement = bHasChildren && !XMLHelper.isInlineNode (aElement.getFirstChild ());

    // get all attributes (order is important!)
    final ICommonsOrderedMap <QName, String> aAttrMap = new CommonsLinkedHashMap <> ();

    m_aNSStack.push ();

    try
    {
      // Eventually adds a namespace attribute in the AttrMap
      handlePutNamespaceContextPrefixInRoot (aAttrMap);

      // resolve Namespace prefix
      final String sElementNamespaceURI;
      final String sElementNSPrefix;
      if (bEmitNamespaces)
      {
        sElementNamespaceURI = StringHelper.getNotNull (aElement.getNamespaceURI ());
        // Eventually adds a namespace attribute in the AttrMap
        sElementNSPrefix = m_aNSStack.getElementNamespacePrefixToUse (sElementNamespaceURI, bIsRootElement, aAttrMap);
      }
      else
      {
        sElementNamespaceURI = null;
        sElementNSPrefix = null;
      }

      // Get all attributes
      XMLHelper.forAllAttributes (aElement, aAttr -> {
        final String sAttrNamespaceURI = StringHelper.getNotNull (aAttr.getNamespaceURI ());

        // Ignore all "xmlns" attributes as they are created manually. They are
        // only available when reading via DOM
        if (!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals (sAttrNamespaceURI))
        {
          final String sAttrName = XMLHelper.getLocalNameOrName (aAttr);
          final String sAttrValue = aAttr.getValue ();
          String sAttributeNSPrefix = null;
          if (bEmitNamespaces)
          {
            // Eventually adds a namespace attribute in the AttrMap
            sAttributeNSPrefix = m_aNSStack.getAttributeNamespacePrefixToUse (sAttrNamespaceURI,
                                                                              sAttrName,
                                                                              sAttrValue,
                                                                              aAttrMap);
          }

          if (sAttributeNSPrefix != null)
            aAttrMap.put (new QName (sAttrNamespaceURI, sAttrName, sAttributeNSPrefix), sAttrValue);
          else
            aAttrMap.put (new QName (sAttrNamespaceURI, sAttrName), sAttrValue);
        }
      });

      // Determine indent
      final Element aParentElement = aParentNode != null && aParentNode.getNodeType () == Node.ELEMENT_NODE
                                                                                                            ? (Element) aParentNode
                                                                                                            : null;
      final String sParentNamespaceURI;
      final String sParentTagName;
      if (aParentElement != null)
      {
        sParentNamespaceURI = aParentNode.getNamespaceURI ();
        sParentTagName = XMLHelper.getLocalNameOrTagName (aParentElement);
      }
      else
      {
        sParentNamespaceURI = null;
        sParentTagName = null;
      }
      final EXMLSerializeIndent eIndentOuter = m_aSettings.getIndentDeterminator ()
                                                          .getIndentOuter (sParentNamespaceURI,
                                                                           sParentTagName,
                                                                           sElementNamespaceURI,
                                                                           sTagName,
                                                                           aAttrMap,
                                                                           bHasChildren,
                                                                           m_aSettings.getIndent ());
      final EXMLSerializeBracketMode eBracketMode = m_aSettings.getBracketModeDeterminator ()
                                                               .getBracketMode (sElementNamespaceURI,
                                                                                sTagName,
                                                                                aAttrMap,
                                                                                bHasChildren);

      // Indent?
      if (eIndentOuter.isIndent () && m_aIndent.length () > 0 && bIndentPrev)
        aXMLWriter.onContentElementWhitespace (m_aIndent);

      // Open tag
      aXMLWriter.onElementStart (sElementNSPrefix, sTagName, aAttrMap, eBracketMode);

      // write child nodes (if present)
      if (bHasChildren)
      {
        final EXMLSerializeIndent eIndentInner = m_aSettings.getIndentDeterminator ()
                                                            .getIndentInner (sParentNamespaceURI,
                                                                             sParentTagName,
                                                                             sElementNamespaceURI,
                                                                             sTagName,
                                                                             aAttrMap,
                                                                             bHasChildren,
                                                                             m_aSettings.getIndent ());

        // Align?
        if (eIndentInner.isAlign () && bIsFirstChildElement)
          aXMLWriter.onContentElementWhitespace (m_aSettings.getNewLineString ());

        // increment indent
        final String sIndentPerLevel = m_aSettings.getIndentationString ();
        m_aIndent.append (sIndentPerLevel);

        // recursively process child nodes
        _writeNodeList (aXMLWriter, aElement, aChildNodeList);

        // decrement indent again
        m_aIndent.setLength (m_aIndent.length () - sIndentPerLevel.length ());

        // Indent?
        if (eIndentInner.isIndent () && m_aIndent.length () > 0 && bIsFirstChildElement)
          aXMLWriter.onContentElementWhitespace (m_aIndent);
      }

      // add closing tag
      aXMLWriter.onElementEnd (sElementNSPrefix, sTagName, eBracketMode);

      // Align?
      if (eIndentOuter.isAlign () && bIndentNext)
        aXMLWriter.onContentElementWhitespace (m_aSettings.getNewLineString ());
    }
    finally
    {
      m_aNSStack.pop ();
    }
  }
}
