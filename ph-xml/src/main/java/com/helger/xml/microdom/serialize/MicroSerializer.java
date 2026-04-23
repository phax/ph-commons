/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.xml.microdom.serialize;

import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.ETriState;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.xml.microdom.IMicroAttribute;
import com.helger.xml.microdom.IMicroCDATA;
import com.helger.xml.microdom.IMicroComment;
import com.helger.xml.microdom.IMicroContainer;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroDocumentType;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.IMicroEntityReference;
import com.helger.xml.microdom.IMicroNode;
import com.helger.xml.microdom.IMicroProcessingInstruction;
import com.helger.xml.microdom.IMicroQName;
import com.helger.xml.microdom.IMicroText;
import com.helger.xml.serialize.write.AbstractXMLSerializer;
import com.helger.xml.serialize.write.EXMLSerializeBracketMode;
import com.helger.xml.serialize.write.EXMLSerializeIndent;
import com.helger.xml.serialize.write.IXMLWriterSettings;
import com.helger.xml.serialize.write.XMLEmitter;
import com.helger.xml.serialize.write.XMLWriterSettings;

/**
 * Materializes micro nodes into a string representation.
 *
 * @author Philip
 */
public class MicroSerializer extends AbstractXMLSerializer <IMicroNode>
{
  /**
   * Default constructor using {@link XMLWriterSettings#DEFAULT_XML_SETTINGS}.
   */
  public MicroSerializer ()
  {
    this (XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }

  /**
   * Constructor with custom settings.
   *
   * @param aSettings
   *        The XML writer settings to use. May not be <code>null</code>.
   */
  public MicroSerializer (@NonNull final IXMLWriterSettings aSettings)
  {
    super (aSettings);
  }

  @Override
  protected void emitNode (@NonNull final XMLEmitter aXMLWriter,
                           @Nullable final IMicroNode aParentNode,
                           @Nullable final IMicroNode aPrevSibling,
                           @NonNull final IMicroNode aNode,
                           @Nullable final IMicroNode aNextSibling)
  {
    ValueEnforcer.notNull (aNode, "Node");

    switch (aNode.getType ())
    {
      case ELEMENT:
        _writeElement (aXMLWriter, aParentNode, aPrevSibling, (IMicroElement) aNode, aNextSibling);
        break;
      case TEXT:
        _writeText (aXMLWriter, (IMicroText) aNode);
        break;
      case CDATA:
        _writeCDATA (aXMLWriter, (IMicroCDATA) aNode);
        break;
      case COMMENT:
        _writeComment (aXMLWriter, aPrevSibling, (IMicroComment) aNode, aNextSibling);
        break;
      case ENTITY_REFERENCE:
        _writeEntityReference (aXMLWriter, (IMicroEntityReference) aNode);
        break;
      case DOCUMENT:
        _writeDocument (aXMLWriter, (IMicroDocument) aNode);
        break;
      case DOCUMENT_TYPE:
        _writeDocumentType (aXMLWriter, (IMicroDocumentType) aNode);
        break;
      case PROCESSING_INSTRUCTION:
        _writeProcessingInstruction (aXMLWriter, (IMicroProcessingInstruction) aNode);
        break;
      case CONTAINER:
        _writeContainer (aXMLWriter, aParentNode, (IMicroContainer) aNode);
        break;
      default:
        throw new IllegalArgumentException ("Passed node type " +
                                            aNode.getClass ().getName () +
                                            " is not yet supported");
    }
  }

  /**
   * Special helper method to write a list of nodes. This implementations is used to avoid calling
   * {@link IMicroNode#getPreviousSibling()} and {@link IMicroNode#getNextSibling()} since there
   * implementation is compute intensive since the objects are not directly linked. So to avoid this
   * call, we're manually retrieving the previous and next sibling by their index in the list.
   *
   * @param aXMLWriter
   *        The XML writer to use. May not be <code>null</code>.
   * @param aParentNode
   *        The parent node to be used. May not be <code>null</code>.
   * @param aChildren
   *        The node list to be serialized. May not be <code>null</code>.
   */
  private void _writeNodeList (@NonNull final XMLEmitter aXMLWriter,
                               @Nullable final IMicroNode aParentNode,
                               @NonNull final List <IMicroNode> aChildren)
  {
    final int nLastIndex = aChildren.size () - 1;
    for (int nIndex = 0; nIndex <= nLastIndex; ++nIndex)
    {
      emitNode (aXMLWriter,
                aParentNode,
                nIndex == 0 ? null : aChildren.get (nIndex - 1),
                aChildren.get (nIndex),
                nIndex == nLastIndex ? null : aChildren.get (nIndex + 1));
    }
  }

  private void _writeDocument (@NonNull final XMLEmitter aXMLWriter, final IMicroDocument aDocument)
  {
    if (m_aSettings.getSerializeXMLDeclaration ().isEmit ())
    {
      aXMLWriter.onXMLDeclaration (m_aSettings.getXMLVersion (),
                                   m_aSettings.getCharset ().name (),
                                   m_aSettings.getSerializeXMLDeclaration ().isEmitStandalone () ? aDocument
                                                                                                            .getStandalone ()
                                                                                                 : ETriState.UNDEFINED,
                                   m_aSettings.isNewLineAfterXMLDeclaration ());
    }

    if (aDocument.hasChildren ())
      _writeNodeList (aXMLWriter, aDocument, aDocument.getAllChildren ());
  }

  private void _writeDocumentType (@NonNull final XMLEmitter aXMLWriter, final IMicroDocumentType aDocType)
  {
    if (m_aSettings.getSerializeDocType ().isEmit ())
      aXMLWriter.onDocumentType (aDocType.getQualifiedName (), aDocType.getPublicID (), aDocType.getSystemID ());
  }

  private static void _writeProcessingInstruction (@NonNull final XMLEmitter aXMLWriter,
                                                   @NonNull final IMicroProcessingInstruction aPI)
  {
    aXMLWriter.onProcessingInstruction (aPI.getTarget (), aPI.getData ());
  }

  private void _writeContainer (@NonNull final XMLEmitter aXMLWriter,
                                @NonNull final IMicroNode aParentNode,
                                @NonNull final IMicroContainer aContainer)
  {
    // A container has no own properties!
    if (aContainer.hasChildren ())
      _writeNodeList (aXMLWriter, aParentNode, aContainer.getAllChildren ());
  }

  private static void _writeEntityReference (@NonNull final XMLEmitter aXMLWriter,
                                             @NonNull final IMicroEntityReference aEntRef)
  {
    aXMLWriter.onEntityReference (aEntRef.getName ());
  }

  private static void _writeText (@NonNull final XMLEmitter aXMLWriter, @NonNull final IMicroText aText)
  {
    aXMLWriter.onText (aText.getData ().toString (), aText.isEscape ());
  }

  private void _writeComment (@NonNull final XMLEmitter aXMLWriter,
                              @Nullable final IMicroNode aPrevSibling,
                              @NonNull final IMicroComment aComment,
                              @Nullable final IMicroNode aNextSibling)
  {
    if (m_aSettings.getSerializeComments ().isEmit ())
    {
      final boolean bIndentPrev = aPrevSibling == null || !_isInlineNode (aPrevSibling);
      final boolean bIndentNext = aNextSibling == null || !_isInlineNode (aNextSibling);

      if (m_aSettings.getIndent ().isIndent () && m_aIndent.length () > 0 && bIndentPrev)
        aXMLWriter.onContentElementWhitespace (m_aIndent);

      final String sComment = aComment.getData ().toString ();
      aXMLWriter.onComment (sComment);

      if (m_aSettings.getIndent ().isAlign () && bIndentPrev && bIndentNext)
        aXMLWriter.onContentElementWhitespace (m_aSettings.getNewLineString ());
    }
  }

  private void _writeCDATA (@NonNull final XMLEmitter aXMLWriter, @NonNull final IMicroCDATA aCDATA)
  {
    if (m_aSettings.isWriteCDATAAsText ())
      aXMLWriter.onText (aCDATA.getData ().toString ());
    else
      aXMLWriter.onCDATA (aCDATA.getData ().toString ());
  }

  private static boolean _isInlineNode (@NonNull final IMicroNode aNode)
  {
    return aNode.isText () || aNode.isCDATA () || aNode.isEntityReference ();
  }

  private void _writeElement (@NonNull final XMLEmitter aXMLWriter,
                              @Nullable final IMicroNode aParentNode,
                              @Nullable final IMicroNode aPrevSibling,
                              @NonNull final IMicroElement aElement,
                              @Nullable final IMicroNode aNextSibling)
  {
    // use either local name or tag name (depending on namespace prefix)
    final String sTagName = aElement.getTagName ();

    final boolean bEmitNamespaces = m_aSettings.isEmitNamespaces ();
    final ICommonsList <IMicroNode> aChildNodeList = aElement.getAllChildren ();
    final boolean bHasChildren = aElement.hasChildren ();

    final boolean bIsRootElement = aElement.getParent () != null && aElement.getParent ().isDocument ();
    final boolean bIndentPrev = aPrevSibling == null || !_isInlineNode (aPrevSibling) || bIsRootElement;
    final boolean bIndentNext = aNextSibling == null || !_isInlineNode (aNextSibling);
    final boolean bIsFirstChildElement = bHasChildren && !_isInlineNode (aElement.getFirstChild ());

    // get all attributes (order is important!)
    final ICommonsOrderedMap <QName, String> aAttrMap = new CommonsLinkedHashMap <> ();

    m_aNSStack.push ();

    // Eventually adds a namespace attribute in the AttrMap
    handlePutNamespaceContextPrefixInRoot (aAttrMap);

    try
    {
      String sElementNamespaceURI = null;
      String sElementNSPrefix = null;

      if (m_aSettings.isUseExistingNamespaceDeclarations ())
      {
        // Use namespace URI as-is; prefix will be derived from existing xmlns
        // attributes stored on the element or inherited from ancestors via the
        // namespace stack
        sElementNamespaceURI = StringHelper.getNotNull (aElement.getNamespaceURI ());

        // First pass: process xmlns attributes and register them in the
        // namespace stack so that child elements can look up prefixes
        if (aElement.hasAttributes ())
          for (final IMicroAttribute aAttr : aElement.getAttributeObjs ())
          {
            final IMicroQName aAttrName = aAttr.getAttributeQName ();
            final String sAttrNamespaceURI = StringHelper.getNotNull (aAttrName.getNamespaceURI ());
            final String sAttrName = aAttrName.getName ();
            final String sAttrValue = aAttr.getAttributeValue ();

            if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals (sAttrNamespaceURI))
            {
              if (XMLConstants.XMLNS_ATTRIBUTE.equals (sAttrName))
              {
                // Default namespace declaration (xmlns="...")
                aAttrMap.put (new QName (sAttrNamespaceURI, sAttrName), sAttrValue);
                m_aNSStack.addNamespaceMapping (null, sAttrValue);
              }
              else
              {
                // Prefixed namespace declaration (xmlns:prefix="...")
                aAttrMap.put (new QName (sAttrNamespaceURI, sAttrName, XMLConstants.XMLNS_ATTRIBUTE),
                              sAttrValue);
                m_aNSStack.addNamespaceMapping (sAttrName, sAttrValue);
              }
            }
          }

        // Determine element prefix from namespace stack (covers both local
        // xmlns attrs and those inherited from ancestors)
        if (sElementNamespaceURI.length () > 0)
          sElementNSPrefix = m_aNSStack.getUsedPrefixOfNamespace (sElementNamespaceURI);

        // Second pass: process non-xmlns attributes and resolve their prefixes
        if (aElement.hasAttributes ())
          for (final IMicroAttribute aAttr : aElement.getAttributeObjs ())
          {
            final IMicroQName aAttrName = aAttr.getAttributeQName ();
            final String sAttrNamespaceURI = StringHelper.getNotNull (aAttrName.getNamespaceURI ());
            final String sAttrValue = aAttr.getAttributeValue ();

            if (!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals (sAttrNamespaceURI))
            {
              // Regular attribute - find its prefix from namespace stack
              String sAttrNSPrefix = null;
              if (sAttrNamespaceURI.length () > 0)
                sAttrNSPrefix = m_aNSStack.getUsedPrefixOfNamespace (sAttrNamespaceURI);

              if (sAttrNSPrefix != null)
                aAttrMap.put (aAttrName.getAsXMLQName (sAttrNSPrefix), sAttrValue);
              else
                aAttrMap.put (aAttrName.getAsXMLQName (), sAttrValue);
            }
          }
      }
      else
      {
        // resolve Namespace prefix
        if (bEmitNamespaces)
        {
          sElementNamespaceURI = StringHelper.getNotNull (aElement.getNamespaceURI ());
          // Eventually adds a namespace attribute in the AttrMap
          sElementNSPrefix = m_aNSStack.getElementNamespacePrefixToUse (sElementNamespaceURI,
                                                                        bIsRootElement,
                                                                        aAttrMap);
        }

        // For all attributes
        if (aElement.hasAttributes ())
          for (final IMicroAttribute aAttr : aElement.getAttributeObjs ())
          {
            final IMicroQName aAttrName = aAttr.getAttributeQName ();
            final String sAttrNamespaceURI = StringHelper.getNotNull (aAttrName.getNamespaceURI ());
            final String sAttrName = aAttrName.getName ();
            final String sAttrValue = aAttr.getAttributeValue ();
            String sAttrNSPrefix = null;
            if (bEmitNamespaces)
            {
              // Eventually adds a namespace attribute in the AttrMap
              sAttrNSPrefix = m_aNSStack.getAttributeNamespacePrefixToUse (sAttrNamespaceURI,
                                                                           sAttrName,
                                                                           sAttrValue,
                                                                           aAttrMap);
            }

            if (sAttrNSPrefix != null)
              aAttrMap.put (aAttrName.getAsXMLQName (sAttrNSPrefix), sAttrValue);
            else
              aAttrMap.put (aAttrName.getAsXMLQName (), sAttrValue);
          }
      }

      // Determine indent
      final IMicroElement aParentElement = aParentNode != null && aParentNode.isElement () ? (IMicroElement) aParentNode
                                                                                           : null;
      final String sParentNamespaceURI = aParentElement != null ? aParentElement.getNamespaceURI () : null;
      final String sParentTagName = aParentElement != null ? aParentElement.getTagName () : null;
      final EXMLSerializeIndent eIndentOuter = m_aSettings.getIndentDeterminator ()
                                                          .getIndentOuter (sParentNamespaceURI,
                                                                           sParentTagName,
                                                                           sElementNamespaceURI,
                                                                           sTagName,
                                                                           aAttrMap,
                                                                           bHasChildren,
                                                                           m_aSettings.getIndent ());
      // Has indent only if enabled, and an indent string is not empty
      // indent only if predecessor was an element
      if (eIndentOuter.isIndent () && m_aIndent.length () > 0 && bIndentPrev)
        aXMLWriter.onContentElementWhitespace (m_aIndent);

      final EXMLSerializeBracketMode eBracketMode = m_aSettings.getBracketModeDeterminator ()
                                                               .getBracketMode (sElementNamespaceURI,
                                                                                sTagName,
                                                                                aAttrMap,
                                                                                bHasChildren);

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

        // do we have enclosing elements?
        if (eIndentInner.isAlign () && bIsFirstChildElement)
          aXMLWriter.onContentElementWhitespace (m_aSettings.getNewLineString ());

        // increment indent
        final String sIndent = m_aSettings.getIndentationString ();
        m_aIndent.append (sIndent);

        // recursively process child nodes
        if (aChildNodeList != null)
          _writeNodeList (aXMLWriter, aElement, aChildNodeList);

        // decrement indent
        m_aIndent.delete (m_aIndent.length () - sIndent.length (), m_aIndent.length ());

        // add closing tag
        if (eIndentInner.isIndent () && m_aIndent.length () > 0 && bIsFirstChildElement)
          aXMLWriter.onContentElementWhitespace (m_aIndent);
      }

      aXMLWriter.onElementEnd (sElementNSPrefix, sTagName, eBracketMode);

      if (eIndentOuter.isAlign () && bIndentNext)
        aXMLWriter.onContentElementWhitespace (m_aSettings.getNewLineString ());
    }
    finally
    {
      m_aNSStack.pop ();
    }
  }
}
