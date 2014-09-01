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

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import com.helger.commons.string.StringHelper;
import com.helger.commons.xml.EXMLVersion;
import com.helger.commons.xml.IXMLIterationHandler;
import com.helger.commons.xml.XMLHelper;

/**
 * org.w3c.dom.Node serializer that correctly handles HTML empty elements
 * (&lt;span&gt;&lt;/span&gt; vs. &lt;span /&gt;).
 *
 * @author Philip Helger
 */
public final class XMLSerializerPH extends AbstractXMLSerializer <Node>
{
  public XMLSerializerPH ()
  {
    this (XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }

  public XMLSerializerPH (@Nonnull final IXMLWriterSettings aSettings)
  {
    super (aSettings);
  }

  @Override
  protected void emitNode (@Nonnull final IXMLIterationHandler aXMLWriter,
                           @Nullable final Node aPrevSibling,
                           @Nonnull final Node aNode,
                           @Nullable final Node aNextSibling)
  {
    final short nNodeType = aNode.getNodeType ();
    if (nNodeType == Node.ELEMENT_NODE)
      _writeElement (aXMLWriter, aPrevSibling, (Element) aNode, aNextSibling);
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

  private void _writeNodeList (@Nonnull final IXMLIterationHandler aXMLWriter, @Nonnull final NodeList aChildren)
  {
    final int nLastIndex = aChildren.getLength () - 1;
    for (int nIndex = 0; nIndex <= nLastIndex; ++nIndex)
    {
      emitNode (aXMLWriter,
                nIndex == 0 ? null : aChildren.item (nIndex - 1),
                aChildren.item (nIndex),
                nIndex == nLastIndex ? null : aChildren.item (nIndex + 1));
    }
  }

  private void _writeDocument (@Nonnull final IXMLIterationHandler aXMLWriter, @Nonnull final Document aDocument)
  {
    if (m_aSettings.getFormat ().isXML ())
    {
      String sXMLVersion = null;
      boolean bIsDocumentStandalone = false;
      try
      {
        sXMLVersion = aDocument.getXmlVersion ();
        bIsDocumentStandalone = aDocument.getXmlStandalone ();
      }
      catch (final Throwable t)
      {
        // Happens e.g. in dom4j 1.6.1:
        // AbstractMethodError: getXmlVersion and getXmlStandalone
      }
      final EXMLVersion eXMLVersion = EXMLVersion.getFromVersionOrDefault (sXMLVersion, m_aSettings.getXMLVersion ());
      aXMLWriter.onDocumentStart (eXMLVersion,
                                  m_aSettings.getCharset (),
                                  bIsDocumentStandalone || aDocument.getDoctype () == null);
    }

    _writeNodeList (aXMLWriter, aDocument.getChildNodes ());
  }

  private void _writeDocumentType (@Nonnull final IXMLIterationHandler aXMLWriter, @Nonnull final DocumentType aDocType)
  {
    if (m_aSettings.getSerializeDocType ().isEmit ())
      aXMLWriter.onDocumentType (aDocType.getName (), aDocType.getPublicId (), aDocType.getSystemId ());
  }

  private static void _writeProcessingInstruction (@Nonnull final IXMLIterationHandler aXMLWriter,
                                                   @Nonnull final ProcessingInstruction aPI)
  {
    aXMLWriter.onProcessingInstruction (aPI.getTarget (), aPI.getData ());
  }

  private static void _writeEntityReference (@Nonnull final IXMLIterationHandler aXMLWriter,
                                             @Nonnull final EntityReference aEntRef)
  {
    aXMLWriter.onEntityReference (aEntRef.getNodeName ());
  }

  private void _writeComment (@Nonnull final IXMLIterationHandler aXMLWriter, @Nonnull final Comment aComment)
  {
    if (m_aSettings.getSerializeComments ().isEmit ())
    {
      aXMLWriter.onComment (aComment.getData ());
    }
  }

  private static void _writeText (@Nonnull final IXMLIterationHandler aXMLWriter, @Nonnull final Text aText)
  {
    // DOM text is always escaped!
    aXMLWriter.onText (aText.getData (), true);
  }

  private static void _writeCDATA (@Nonnull final IXMLIterationHandler aXMLWriter, @Nonnull final Text aText)
  {
    aXMLWriter.onCDATA (aText.getData ());
  }

  private void _writeElement (@Nonnull final IXMLIterationHandler aXMLWriter,
                              @Nullable final Node aPrevSibling,
                              @Nonnull final Element aElement,
                              @Nullable final Node aNextSibling)
  {
    // use either local name or tag name (depending on namespace prefix)
    final String sTagName = aElement.getLocalName () != null ? aElement.getLocalName () : aElement.getTagName ();

    // May be null!
    final Document aDoc = aElement.getOwnerDocument ();
    final boolean bEmitNamespaces = m_aSettings.isEmitNamespaces ();
    final NodeList aChildNodeList = aElement.getChildNodes ();
    final boolean bHasChildren = aChildNodeList.getLength () > 0;

    final boolean bIsRootElement = aDoc != null && aElement.equals (aDoc.getDocumentElement ());
    final boolean bIndentPrev = aPrevSibling == null || !XMLHelper.isTextNode (aPrevSibling) || bIsRootElement;
    final boolean bIndentNext = aNextSibling == null || !XMLHelper.isTextNode (aNextSibling);
    final boolean bHasChildElement = bHasChildren && !XMLHelper.isTextNode (aElement.getFirstChild ());

    // get all attributes (sorting is important because the order from
    // getAttributes is not guaranteed to be consistent!)
    final Map <String, String> aAttrMap = new TreeMap <String, String> ();
    final NamedNodeMap aAttrs = aElement.getAttributes ();
    for (int i = 0; i < aAttrs.getLength (); ++i)
    {
      final Attr aAttr = (Attr) aAttrs.item (i);
      aAttrMap.put (aAttr.getName (), aAttr.getValue ());
    }

    m_aNSStack.push (bEmitNamespaces ? aAttrMap : null);

    handlePutNamespaceContextPrefixInRoot (aAttrMap);

    try
    {
      // resolve Namespace prefix
      String sNSPrefix = null;
      if (bEmitNamespaces)
      {
        final String sElementNamespaceURI = StringHelper.getNotNull (aElement.getNamespaceURI ());
        final String sDefaultNamespaceURI = StringHelper.getNotNull (m_aNSStack.getDefaultNamespaceURI ());
        final boolean bIsDefaultNamespace = sElementNamespaceURI.equals (sDefaultNamespaceURI);
        if (!bIsDefaultNamespace)
          sNSPrefix = m_aNSStack.getUsedPrefixOfNamespace (sElementNamespaceURI);

        // Do we need to create a prefix?
        if (sNSPrefix == null && !bIsDefaultNamespace && (!bIsRootElement || sElementNamespaceURI.length () > 0))
        {
          // Ensure to use the correct prefix (namespace context)
          sNSPrefix = m_aNSStack.getMappedPrefix (sElementNamespaceURI);

          // Do not create a prefix for the root element
          if (sNSPrefix == null && !bIsRootElement)
            sNSPrefix = m_aNSStack.createUniquePrefix ();

          // Add and remember the attribute
          aAttrMap.put (XMLHelper.getXMLNSAttrName (sNSPrefix), sElementNamespaceURI);
          m_aNSStack.addNamespaceMapping (sNSPrefix, sElementNamespaceURI);
        }
      }

      // indent only if predecessor was an element
      if (m_aSettings.getIndent ().isIndent () && bIndentPrev && m_aIndent.length () > 0)
        aXMLWriter.onContentElementWhitespace (m_aIndent);

      aXMLWriter.onElementStart (sNSPrefix, sTagName, aAttrMap, bHasChildren);

      // write child nodes (if present)
      if (bHasChildren)
      {
        // do we have enclosing elements?
        if (m_aSettings.getIndent ().isAlign () && bHasChildElement)
          aXMLWriter.onContentElementWhitespace (m_aSettings.getNewlineString ());

        // increment indent
        final String sIndent = m_aSettings.getIndentationString ();
        m_aIndent.append (sIndent);

        // recursively process child nodes
        _writeNodeList (aXMLWriter, aChildNodeList);

        // decrement indent
        m_aIndent.delete (m_aIndent.length () - sIndent.length (), m_aIndent.length ());

        // add closing tag
        if (m_aSettings.getIndent ().isIndent () && bHasChildElement && m_aIndent.length () > 0)
          aXMLWriter.onContentElementWhitespace (m_aIndent);
      }

      aXMLWriter.onElementEnd (sNSPrefix, sTagName, bHasChildren);

      if (m_aSettings.getIndent ().isAlign () && bIndentNext)
        aXMLWriter.onContentElementWhitespace (m_aSettings.getNewlineString ());
    }
    finally
    {
      m_aNSStack.pop ();
    }
  }
}
