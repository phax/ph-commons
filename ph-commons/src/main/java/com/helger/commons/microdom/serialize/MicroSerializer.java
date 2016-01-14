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
package com.helger.commons.microdom.serialize;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.microdom.IMicroCDATA;
import com.helger.commons.microdom.IMicroComment;
import com.helger.commons.microdom.IMicroContainer;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroDocumentType;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.IMicroEntityReference;
import com.helger.commons.microdom.IMicroNode;
import com.helger.commons.microdom.IMicroProcessingInstruction;
import com.helger.commons.microdom.IMicroQName;
import com.helger.commons.microdom.IMicroText;
import com.helger.commons.string.StringHelper;
import com.helger.commons.xml.serialize.write.AbstractXMLSerializer;
import com.helger.commons.xml.serialize.write.EXMLSerializeBracketMode;
import com.helger.commons.xml.serialize.write.EXMLSerializeIndent;
import com.helger.commons.xml.serialize.write.IXMLWriterSettings;
import com.helger.commons.xml.serialize.write.XMLEmitter;
import com.helger.commons.xml.serialize.write.XMLWriterSettings;

/**
 * Materializes micro nodes into a string representation.
 *
 * @author Philip
 */
public class MicroSerializer extends AbstractXMLSerializer <IMicroNode>
{
  public MicroSerializer ()
  {
    this (XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }

  public MicroSerializer (@Nonnull final IXMLWriterSettings aSettings)
  {
    super (aSettings);
  }

  @Override
  protected void emitNode (@Nonnull final XMLEmitter aXMLWriter,
                           @Nullable final IMicroNode aParentNode,
                           @Nullable final IMicroNode aPrevSibling,
                           @Nonnull final IMicroNode aNode,
                           @Nullable final IMicroNode aNextSibling)
  {
    ValueEnforcer.notNull (aNode, "Node");

    if (aNode.isElement ())
      _writeElement (aXMLWriter, aParentNode, aPrevSibling, (IMicroElement) aNode, aNextSibling);
    else
      if (aNode.isText ())
        _writeText (aXMLWriter, (IMicroText) aNode);
      else
        if (aNode.isCDATA ())
          _writeCDATA (aXMLWriter, (IMicroCDATA) aNode);
        else
          if (aNode.isComment ())
            _writeComment (aXMLWriter, (IMicroComment) aNode);
          else
            if (aNode.isEntityReference ())
              _writeEntityReference (aXMLWriter, (IMicroEntityReference) aNode);
            else
              if (aNode.isDocument ())
                _writeDocument (aXMLWriter, (IMicroDocument) aNode);
              else
                if (aNode.isDocumentType ())
                  _writeDocumentType (aXMLWriter, (IMicroDocumentType) aNode);
                else
                  if (aNode.isProcessingInstruction ())
                    _writeProcessingInstruction (aXMLWriter, (IMicroProcessingInstruction) aNode);
                  else
                    if (aNode.isContainer ())
                      _writeContainer (aXMLWriter, aParentNode, (IMicroContainer) aNode);
                    else
                      throw new IllegalArgumentException ("Passed node type " +
                                                          aNode.getClass ().getName () +
                                                          " is not yet supported");
  }

  /**
   * Special helper method to write a list of nodes. This implementations is
   * used to avoid calling {@link IMicroNode#getPreviousSibling()} and
   * {@link IMicroNode#getNextSibling()} since there implementation is compute
   * intensive since the objects are not directly linked. So to avoid this call,
   * we're manually retrieving the previous and next sibling by their index in
   * the list.
   *
   * @param aXMLWriter
   *        The XML writer to use. May not be <code>null</code>.
   * @param aParentNode
   *        The parent node to be used. May not be <code>null</code>.
   * @param aChildren
   *        The node list to be serialized. May not be <code>null</code>.
   */
  private void _writeNodeList (@Nonnull final XMLEmitter aXMLWriter,
                               @Nullable final IMicroNode aParentNode,
                               @Nonnull final List <IMicroNode> aChildren)
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

  private void _writeDocument (@Nonnull final XMLEmitter aXMLWriter, final IMicroDocument aDocument)
  {
    if (m_aSettings.getSerializeXMLDeclaration ().isEmit ())
      aXMLWriter.onXMLDeclaration (m_aSettings.getXMLVersion (), m_aSettings.getCharset (), aDocument.isStandalone ());

    if (aDocument.hasChildren ())
      _writeNodeList (aXMLWriter, aDocument, aDocument.getAllChildren ());
  }

  private void _writeDocumentType (@Nonnull final XMLEmitter aXMLWriter, final IMicroDocumentType aDocType)
  {
    if (m_aSettings.getSerializeDocType ().isEmit ())
      aXMLWriter.onDocumentType (aDocType.getQualifiedName (), aDocType.getPublicID (), aDocType.getSystemID ());
  }

  private static void _writeProcessingInstruction (@Nonnull final XMLEmitter aXMLWriter,
                                                   @Nonnull final IMicroProcessingInstruction aPI)
  {
    aXMLWriter.onProcessingInstruction (aPI.getTarget (), aPI.getData ());
  }

  private void _writeContainer (@Nonnull final XMLEmitter aXMLWriter,
                                @Nonnull final IMicroNode aParentNode,
                                @Nonnull final IMicroContainer aContainer)
  {
    // A container has no own properties!
    if (aContainer.hasChildren ())
      _writeNodeList (aXMLWriter, aParentNode, aContainer.getAllChildren ());
  }

  private static void _writeEntityReference (@Nonnull final XMLEmitter aXMLWriter,
                                             @Nonnull final IMicroEntityReference aEntRef)
  {
    aXMLWriter.onEntityReference (aEntRef.getName ());
  }

  private static void _writeText (@Nonnull final XMLEmitter aXMLWriter, @Nonnull final IMicroText aText)
  {
    aXMLWriter.onText (aText.getData ().toString (), aText.isEscape ());
  }

  private void _writeComment (@Nonnull final XMLEmitter aXMLWriter, @Nonnull final IMicroComment aComment)
  {
    if (m_aSettings.getSerializeComments ().isEmit ())
    {
      if (m_aSettings.getIndent ().isIndent () && m_aIndent.length () > 0)
        aXMLWriter.onContentElementWhitespace (m_aIndent);
      aXMLWriter.onComment (aComment.getData ().toString ());
      if (m_aSettings.getIndent ().isAlign ())
        aXMLWriter.onContentElementWhitespace (m_aSettings.getNewLineString ());
    }
  }

  private static void _writeCDATA (@Nonnull final XMLEmitter aXMLWriter, @Nonnull final IMicroCDATA aCDATA)
  {
    aXMLWriter.onCDATA (aCDATA.getData ().toString ());
  }

  private static boolean _isInlineNode (@Nonnull final IMicroNode aNode)
  {
    return aNode.isText () || aNode.isCDATA () || aNode.isEntityReference ();
  }

  private void _writeElement (@Nonnull final XMLEmitter aXMLWriter,
                              @Nullable final IMicroNode aParentNode,
                              @Nullable final IMicroNode aPrevSibling,
                              @Nonnull final IMicroElement aElement,
                              @Nullable final IMicroNode aNextSibling)
  {
    // use either local name or tag name (depending on namespace prefix)
    final String sTagName = aElement.getTagName ();

    final boolean bEmitNamespaces = m_aSettings.isEmitNamespaces ();
    final List <IMicroNode> aChildNodeList = aElement.getAllChildren ();
    final boolean bHasChildren = aElement.hasChildren ();

    final boolean bIsRootElement = aElement.getParent () != null && aElement.getParent ().isDocument ();
    final boolean bIndentPrev = aPrevSibling == null || !_isInlineNode (aPrevSibling) || bIsRootElement;
    final boolean bIndentNext = aNextSibling == null || !_isInlineNode (aNextSibling);
    final boolean bIsFirstChildElement = bHasChildren && !_isInlineNode (aElement.getFirstChild ());

    // get all attributes (order is important!)
    final Map <QName, String> aAttrMap = new LinkedHashMap <QName, String> ();

    m_aNSStack.push ();

    handlePutNamespaceContextPrefixInRoot (aAttrMap);

    try
    {
      // resolve Namespace prefix
      String sElementNamespaceURI = null;
      String sElementNSPrefix = null;
      if (bEmitNamespaces)
      {
        sElementNamespaceURI = StringHelper.getNotNull (aElement.getNamespaceURI ());
        sElementNSPrefix = m_aNSStack.getElementNamespacePrefixToUse (sElementNamespaceURI, bIsRootElement, aAttrMap);
      }

      // Get all attributes
      if (aElement.hasAttributes ())
      {
        // Delivers an ordered copy!
        for (final Map.Entry <IMicroQName, String> aEntry : aElement.getAllQAttributes ().entrySet ())
        {
          final IMicroQName aAttrName = aEntry.getKey ();
          final String sAttrNamespaceURI = StringHelper.getNotNull (aAttrName.getNamespaceURI ());
          final String sAttrName = aAttrName.getName ();
          final String sAttrValue = aEntry.getValue ();
          String sAttrNSPrefix = null;
          if (bEmitNamespaces)
          {
            sAttrNSPrefix = m_aNSStack.getAttributeNamespacePrefixToUse (sAttrNamespaceURI,
                                                                         sAttrName,
                                                                         sAttrValue,
                                                                         aAttrMap);
          }

          if (sAttrNSPrefix != null)
            aAttrMap.put (new QName (sAttrNamespaceURI, aAttrName.getName (), sAttrNSPrefix), sAttrValue);
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

      aXMLWriter.onElementStart (sElementNSPrefix, sTagName, aAttrMap, bHasChildren, eBracketMode);

      // write child nodes (if present)
      if (bHasChildren)
      {
        final EXMLSerializeIndent eIndentInner = m_aSettings.getIndentDeterminator ()
                                                            .getIndentOuter (sParentNamespaceURI,
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

      aXMLWriter.onElementEnd (sElementNSPrefix, sTagName, bHasChildren, eBracketMode);

      if (eIndentOuter.isAlign () && bIndentNext)
        aXMLWriter.onContentElementWhitespace (m_aSettings.getNewLineString ());
    }
    finally
    {
      m_aNSStack.pop ();
    }
  }
}
